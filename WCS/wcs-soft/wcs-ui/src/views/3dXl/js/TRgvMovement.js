import TWEEN from '@tweenjs/tween.js';

/**
 * RGV移动控制类
 * 实现RGV在指定空物体之间的移动功能
 */
export class RgvMovement {
  constructor(threeEngine) {
    this.threeEngine = threeEngine;
    this.rgvs = new Map(); // 存储所有RGV对象
    this.destinations = new Map(); // 存储所有目标位置
    
    // 定义每个RGV的有效目标位置
    this.rgvValidDestinations = {
      rgv1: [
        'LINE1_IN_1FF',
        'LINE1_OUT_1FF',
        'LINE2_IN_1FF',
        'LINE2_OUT_1FF',
        'LINE3_IN_1FF',
        'LINE3_OUT_1FF',
        'LINE4_1FF',
        'OUT_1FF'
      ],
      rgv2: [
        'LINE1_IN_1FB',
        'LINE1_OUT_1FB',
        'LINE2_IN_1FB',
        'LINE2_OUT_1FB',
        'LINE3_IN_1FB',
        'LINE3_OUT_1FB',
        'LINE4_1FB',
        'OUT_1FB'
      ],
      rgv3: [
        'LINE1_IN_3F',
        'LINE1_OUT_3F',
        'LINE2_IN_3F',
        'LINE2_OUT_3F',
        'LINE3_IN_3F',
        'LINE3_OUT_3F',
        'LINE4_IN_3F',
        'LINE4_OUT_3F'
      ]
    };
    
    this.init();
  }
  
  /**
   * 初始化RGV移动控制器现在重新整理测试任务方法
   */
  init() {
    this.findRgvs();
    this.findDestinations();
  }
  
  /**
   * 查找所有RGV
   */
  findRgvs() {
    if (!this.threeEngine || !this.threeEngine.scene) {
      console.error('RgvMovement: ThreeEngine或场景未初始化');
      return;
    }
    
    this.threeEngine.scene.traverse((obj) => {
      // 查找RGV对象，严格验证RGV名称格式为"rgv+数字"，例如"rgv1", "rgv2", "rgv3"
      // 排除特殊命名的RGV对象（如'rgv1_up', 'rgv2_up', 'rgv3_up'）
      if (obj.name) {
        // 使用严格的正则表达式验证RGV名称格式：必须是"rgv"开头，后面跟着一个或多个数字，且没有其他字符
        const rgvMatch = obj.name.match(/^rgv(\d+)$/i);
        if (rgvMatch) {
          // 统一RGV名称格式为小写字母加数字
          const rgvName = `rgv${rgvMatch[1]}`;
          this.rgvs.set(rgvName, obj);
        }
      }
    });
  }
  
  /**
   * 查找所有目标位置
   */
  findDestinations() {
    if (!this.threeEngine || !this.threeEngine.scene) {
      console.error('RgvMovement: ThreeEngine或场景未初始化');
      return;
    }
    
    // 收集所有RGV的有效目标位置
    const allDestinations = new Set();
    Object.values(this.rgvValidDestinations).forEach(destinations => {
      destinations.forEach(destination => allDestinations.add(destination));
    });
    
    this.threeEngine.scene.traverse((obj) => {
      if (allDestinations.has(obj.name)) {
        this.destinations.set(obj.name, obj);
      }
    });
  }
  
  /**
   * 获取RGV对象
   * @param {string} rgvName - RGV名称 (必须为"rgv"+数字形式，如: 'rgv1')
   * @returns {THREE.Object3D|null} RGV对象
   */
  getRgv(rgvName) {
    // 严格验证RGV名称格式：必须为"rgv"+数字形式
    const rgvNameRegex = /^rgv\d+$/;
    if (!rgvNameRegex.test(rgvName)) {
      console.error(`RgvMovement.getRgv: RGV名称格式错误，必须为"rgv"+数字形式。传入的名称为: ${rgvName}`);
      return null;
    }
    return this.rgvs.get(rgvName) || null;
  }
  
  /**
   * 获取目标位置坐标
   * @param {string} destinationName - 目标位置名称
   * @returns {THREE.Vector3|null} 目标位置坐标
   */
  getDestinationPosition(destinationName) {
    const destination = this.destinations.get(destinationName);
    if (!destination) {
      console.error(`RgvMovement: 未找到目标位置: ${destinationName}`);
      return null;
    }
    
    return destination.position.clone();
  }
  
  /**
   * 检查RGV是否可以移动到指定位置
   * @param {string} rgvName - RGV名称
   * @param {string} destinationName - 目标位置名称
   * @returns {boolean} 是否可以移动
   */
  canMoveTo(rgvName, destinationName) {
    const validDestinations = this.rgvValidDestinations[rgvName];
    if (!validDestinations) {
      console.error(`RgvMovement: 未知的RGV名称: ${rgvName}`);
      return false;
    }
    
    if (!validDestinations.includes(destinationName)) {
      console.error(`RgvMovement: RGV ${rgvName} 不能移动到位置 ${destinationName}`);
      console.error(`RgvMovement: 有效位置为: ${validDestinations.join(', ')}`);
      return false;
    }
    
    if (!this.getRgv(rgvName)) {
      console.error(`RgvMovement: 未找到RGV ${rgvName}`);
      return false;
    }
    
    if (!this.getDestinationPosition(destinationName)) {
      console.error(`RgvMovement: 未找到目标位置 ${destinationName}`);
      return false;
    }
    
    return true;
  }
  
  /**
   * 移动RGV到指定位置
   * RGV可以在三维空间内移动，支持沿x、y、z轴移动
   * @param {string} rgvName - RGV名称 (必须为"rgv"+数字形式)
   * @param {string} destinationName - 目标位置名称
   * @param {Object} options - 移动选项
   * @param {number} options.duration - 移动持续时间（毫秒）
   * @param {Function} options.onComplete - 完成回调
   * @param {Object} options.cargo - 货物对象（如果有货物在RGV上）
   * @returns {boolean} 是否成功开始移动
   */
  moveRgvTo(rgvName, destinationName, options = {}) {
    // 严格验证RGV名称格式：必须为"rgv"+数字形式
    const rgvNameRegex = /^rgv\d+$/;
    if (!rgvNameRegex.test(rgvName)) {
      console.error(`RgvMovement.moveRgvTo: RGV名称格式错误，必须为"rgv"+数字形式。传入的名称为: ${rgvName}`);
      return false;
    }
    // 检查是否可以移动
    if (!this.canMoveTo(rgvName, destinationName)) {
      return false;
    }
    
    // 获取RGV和目标位置
    const rgv = this.getRgv(rgvName);
    const destinationPos = this.getDestinationPosition(destinationName);
    
    // 设置默认选项
    const defaultOptions = {
      duration: 10000, // 默认移动时间10秒，降低RGV移动速度
      onComplete: null,
      cargo: null // 货物对象，默认为null
    };
    
    const finalOptions = { ...defaultOptions, ...options };
    
   // 检查目标位置是否与当前位置相同
    if (rgv.position.x === destinationPos.x && 
        rgv.position.y === destinationPos.y && 
        rgv.position.z === destinationPos.z) {
     if (finalOptions.onComplete) {
        finalOptions.onComplete(rgvName, destinationName);
      }
      return true;
    }
    
    // 记录货物初始位置相对于RGV的偏移量
    let cargoOffset = null;
    if (finalOptions.cargo) {
      const cargo = finalOptions.cargo;
      cargoOffset = {
        x: cargo.position.x - rgv.position.x,
        y: cargo.position.y - rgv.position.y,
        z: cargo.position.z - rgv.position.z
      };
    }
    
    // 创建移动动画 - 支持三维空间内的移动
    const tween = new TWEEN.Tween(rgv.position)
      .to({ 
        x: destinationPos.x, // 目标x坐标
        y: destinationPos.y, // 目标y坐标
        z: destinationPos.z  // 目标z坐标
      }, finalOptions.duration)
      .easing(TWEEN.Easing.Quadratic.InOut) // 缓动函数
      .onStart(() => {
      })
      .onUpdate(() => {
        // 实时更新RGV的位置
        rgv.position.set(rgv.position.x, rgv.position.y, rgv.position.z);
        
        // 如果有货物在RGV上，同步更新货物位置
        if (finalOptions.cargo && cargoOffset) {
          const cargo = finalOptions.cargo;
          cargo.position.set(
            rgv.position.x + cargoOffset.x,
            rgv.position.y + cargoOffset.y,
            rgv.position.z + cargoOffset.z
          );
        }
      })
      .onComplete(() => {
       // 确保RGV最终位置准确
        rgv.position.set(destinationPos.x, destinationPos.y, destinationPos.z);
        
        // 如果有货物在RGV上，确保货物最终位置准确
        if (finalOptions.cargo && cargoOffset) {
          const cargo = finalOptions.cargo;
          cargo.position.set(
            destinationPos.x + cargoOffset.x,
            destinationPos.y + cargoOffset.y,
            destinationPos.z + cargoOffset.z
          );
        }
        
        if (finalOptions.onComplete) {
          finalOptions.onComplete(rgvName, destinationName);
        }
      });
    
    tween.start();
    return true;
  }
  
  /**
   * 运行RGV测试移动
   * 测试RGV在其固定的有效位置之间移动，验证RGV移动功能的正确性
   * 
   * @param {string} rgvName - RGV名称 (必须为"rgv"+数字形式，如: 'rgv1')
   * @param {Object} options - 测试配置选项
   * @param {number} options.duration - 每次移动的持续时间（毫秒，默认5000）
   * @param {number} options.moveCount - 移动次数（默认无限循环）
   * @param {boolean} options.cycle - 是否循环移动（默认true）
   * @param {number} options.interval - 移动间隔时间（毫秒，默认1000）
   * @param {Function} options.onTestComplete - 测试完成回调
   * @param {Function} options.onEachMoveComplete - 每次移动完成回调
   */
  runRgvTest(rgvName, options = {}) {
    // 严格验证RGV名称格式：必须为"rgv"+数字形式
    const rgvNameRegex = /^rgv\d+$/;
    if (!rgvNameRegex.test(rgvName)) {
      console.error(`RgvMovement.runRgvTest: RGV名称格式错误，必须为"rgv"+数字形式。传入的名称为: ${rgvName}`);
      return null;
    }
    // 设置默认参数
    const { 
      duration = 5000, 
      moveCount = Infinity, 
      cycle = true, 
      interval = 1000,
      onTestComplete = null,
      onEachMoveComplete = null
    } = options;
    
    // 获取RGV的有效固定位置
    const validDestinations = this.rgvValidDestinations[rgvName];
    if (!validDestinations) {
      console.error(`RgvMovement: 未知的RGV名称: ${rgvName}，或该RGV没有配置有效位置`);
      return;
    }
    
   let currentIndex = 0;
    let moveCounter = 0;
    let isTestRunning = true;
    
    // 停止测试的方法
    const stopTest = () => {
      isTestRunning = false;
     if (onTestComplete) {
        onTestComplete(rgvName, moveCounter);
      }
    };
    
    // 移动到下一个位置的方法
    const moveToNextDestination = () => {
      // 如果测试已停止，不再继续移动
      if (!isTestRunning) {
        return;
      }
      
      // 检查是否达到移动次数限制
      if (moveCounter >= moveCount) {
        stopTest();
        return;
      }
      
      // 检查是否超出位置列表范围
      if (currentIndex >= validDestinations.length) {
        if (!cycle) {
          // 如果不循环，停止测试
          stopTest();
          return;
        } else {
          // 如果循环，重置索引
          currentIndex = 0;
        }
      }
      
      const destinationName = validDestinations[currentIndex];
      moveCounter++;
      
     this.moveRgvTo(rgvName, destinationName, {
        duration: duration,
        onComplete: () => {
          // 调用每次移动完成的回调
          if (onEachMoveComplete) {
            onEachMoveComplete(rgvName, destinationName, moveCounter);
          }
          
          // 如果测试仍在运行，准备下一次移动
          if (isTestRunning) {
            setTimeout(moveToNextDestination, interval);
            currentIndex++;
          }
        }
      });
    };
    
    // 开始移动到第一个位置
    moveToNextDestination();
    
    // 返回控制对象，允许外部停止测试
    return {
      stop: stopTest,
      isRunning: () => isTestRunning,
      moveCount: () => moveCounter
    };
  }
  
  /**
   * 获取RGV的有效目标位置
   * @param {string} rgvName - RGV名称
   * @returns {Array<string>} 有效目标位置列表
   */
  getValidDestinations(rgvName) {
    return this.rgvValidDestinations[rgvName] || [];
  }
  
  /**
   * 计算两个位置之间的距离（仅考虑y轴，因为RGV只能沿y轴移动）
   * @param {THREE.Vector3} pos1 - 第一个位置
   * @param {THREE.Vector3} pos2 - 第二个位置
   * @returns {number} 两个位置之间的y轴距离
   */
  calculateDistance(pos1, pos2) {
    return Math.abs(pos1.y - pos2.y);
  }

  /**
   * 找到距离目标位置最近的RGV有效位置
   * @param {string} rgvName - RGV名称
   * @param {THREE.Vector3} targetPos - 目标位置
   * @returns {string|null} 最近的有效位置名称
   */
  findNearestPosition(rgvName, targetPos) {
    // 验证参数
    if (!rgvName || !targetPos) {
      console.error('RgvMovement.findNearestPosition: 缺少必要参数');
      return null;
    }

    // 获取RGV的有效位置
    const validDestinations = this.rgvValidDestinations[rgvName];
    if (!validDestinations || validDestinations.length === 0) {
      console.error(`RgvMovement.findNearestPosition: RGV ${rgvName} 没有配置有效位置`);
      return null;
    }

    let nearestDistance = Infinity;
    let nearestPositionName = null;

    // 遍历所有有效位置，计算到目标位置的距离
    for (const destinationName of validDestinations) {
      const destPos = this.getDestinationPosition(destinationName);
      if (destPos) {
        const distance = this.calculateDistance(destPos, targetPos);
        
        // 更新最近位置
        if (distance < nearestDistance) {
          nearestDistance = distance;
          nearestPositionName = destinationName;
        }
      }
    }

   // 已精简详细日志
    return nearestPositionName;
  }

  /**
   * 获取所有RGV名称
   * @returns {Array<string>} RGV名称列表
   */
  getRgvNames() {
    return Array.from(this.rgvs.keys());
  }
}

export default RgvMovement;