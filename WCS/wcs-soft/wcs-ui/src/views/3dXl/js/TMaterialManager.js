/**
 * 材质管理器 - 统一管理3D场景中各种组件的材质配置
 * 支持按组件类型和状态获取不同的材质
 */

import * as THREE from "three";

/**
 * 材质配置类
 * 管理所有3D组件的材质，按组件类型和状态分类
 */
class MaterialManager {
  constructor() {
    // 材质配置映射表
    this.materials = {
      // 堆垛机组件材质
      stacker: {
        // 躯干材质 - 突出显示的金属框架
        body: {
          default: {
            color: new THREE.Color(0.45, 0.45, 0.5), // 更亮的银灰色金属色
            emissive: new THREE.Color(0.02, 0.02, 0.025), // 增强自发光
            specular: new THREE.Color(0.3, 0.3, 0.3), // 增加高光反射
            shininess: 40, // 增加光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.5, 0.55, 0.5), // 工作状态更亮
            emissive: new THREE.Color(0.05, 0.08, 0.05), // 绿色调自发光
            specular: new THREE.Color(0.4, 0.4, 0.4),
            shininess: 50,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.55, 0.35, 0.35), // 错误状态红色调
            emissive: new THREE.Color(0.08, 0.02, 0.02), // 红色自发光
            specular: new THREE.Color(0.4, 0.3, 0.3),
            shininess: 45,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 载货台材质
        platform: {
          // 默认状态 - 蓝色金属
          default: {
            color: new THREE.Color(0.2, 0.4, 0.7), // 柔和的蓝色金属色
            emissive: new THREE.Color(0.05, 0.1, 0.2), // 柔和的自发光效果
            specular: new THREE.Color(0.5, 0.5, 0.5), // 中等高光反射
            shininess: 60, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 绿色
          working: {
            color: new THREE.Color(0.2, 0.7, 0.3),
            emissive: new THREE.Color(0.05, 0.2, 0.1),
            specular: new THREE.Color(0.5, 0.5, 0.5),
            shininess: 60,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色
          error: {
            color: new THREE.Color(0.8, 0.2, 0.2),
            emissive: new THREE.Color(0.3, 0.05, 0.05),
            specular: new THREE.Color(0.5, 0.5, 0.5),
            shininess: 60,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 空闲状态 - 灰色
          idle: {
            color: new THREE.Color(0.4, 0.4, 0.4),
            emissive: new THREE.Color(0.05, 0.05, 0.05),
            specular: new THREE.Color(0.3, 0.3, 0.3),
            shininess: 40,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 上货叉材质
        upperFork: {
          // 默认状态 - 亮蓝色
          default: {
            color: new THREE.Color(0.2, 0.5, 0.8), // 柔和的蓝色金属色
            emissive: new THREE.Color(0.08, 0.15, 0.3), // 柔和的自发光效果
            specular: new THREE.Color(0.6, 0.6, 0.6), // 中等高光反射
            shininess: 70, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 绿色
          working: {
            color: new THREE.Color(0.2, 0.8, 0.4),
            emissive: new THREE.Color(0.1, 0.3, 0.15),
            specular: new THREE.Color(0.6, 0.6, 0.6),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色
          error: {
            color: new THREE.Color(0.9, 0.2, 0.2),
            emissive: new THREE.Color(0.35, 0.08, 0.08),
            specular: new THREE.Color(0.6, 0.6, 0.6),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 下货叉材质
        lowerFork: {
          // 默认状态 - 更亮的蓝色
          default: {
            color: new THREE.Color(0.15, 0.6, 0.9), // 柔和的蓝色金属色
            emissive: new THREE.Color(0.1, 0.2, 0.4), // 柔和的自发光效果
            specular: new THREE.Color(0.7, 0.7, 0.7), // 中等高光反射
            shininess: 80, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 绿色
          working: {
            color: new THREE.Color(0.15, 0.9, 0.5),
            emissive: new THREE.Color(0.12, 0.35, 0.2),
            specular: new THREE.Color(0.7, 0.7, 0.7),
            shininess: 80,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色
          error: {
            color: new THREE.Color(0.95, 0.15, 0.15),
            emissive: new THREE.Color(0.4, 0.1, 0.1),
            specular: new THREE.Color(0.7, 0.7, 0.7),
            shininess: 80,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },
      
      // RGV组件材质
      rgv: {
        // 车体材质 - 突出显示的金属车体
        body: {
          default: {
            color: new THREE.Color(0.5, 0.5, 0.55), // 更亮的银灰色金属色
            emissive: new THREE.Color(0.03, 0.03, 0.04), // 增强自发光
            specular: new THREE.Color(0.4, 0.4, 0.4), // 增强高光反射
            shininess: 60, // 高光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.55, 0.6, 0.55), // 工作状态更亮
            emissive: new THREE.Color(0.08, 0.12, 0.08), // 绿色调自发光
            specular: new THREE.Color(0.5, 0.5, 0.5),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.6, 0.3, 0.3), // 错误状态红色调
            emissive: new THREE.Color(0.2, 0.05, 0.05), // 红色自发光
            specular: new THREE.Color(0.5, 0.4, 0.4),
            shininess: 65,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 载货台材质 - 橘黄色
        platform: {
          default: {
            color: new THREE.Color(1.0, 0.50, 0.10), // 鲜艳的橘黄色
            emissive: new THREE.Color(0.35, 0.18, 0.02), // 橘黄色自发光
            specular: new THREE.Color(0.8, 0.6, 0.3), // 金属光泽，带橘黄色调
            shininess: 80, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(1.0, 0.60, 0.15), // 更亮的橘黄色
            emissive: new THREE.Color(0.45, 0.25, 0.03), // 更强的橘黄色自发光
            specular: new THREE.Color(0.9, 0.7, 0.4), // 更强的金属光泽
            shininess: 90,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(1.0, 0.30, 0.10), // 带红色调的橘黄色
            emissive: new THREE.Color(0.45, 0.08, 0.02), // 带红色调的自发光
            specular: new THREE.Color(0.9, 0.4, 0.2), // 带红色调的金属光泽
            shininess: 75,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },
      
      // 地板组件材质
      floor: {
        // 地板主体材质
        main: {
          // 第1层 - 极低明度深蓝色，不透明
          floor1: {
            color: new THREE.Color(0.02, 0.025, 0.05), // 极低明度深蓝色
            emissive: new THREE.Color(0.005, 0.008, 0.020), // 偏蓝的自发光
            specular: new THREE.Color(0.06, 0.08, 0.18), // 偏蓝的高光
            shininess: 30, // 低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第2层 - 明度稍高的深蓝色，不透明
          floor2: {
            color: new THREE.Color(0.03, 0.035, 0.07), // 明度稍高的深蓝色
            emissive: new THREE.Color(0.008, 0.012, 0.030), // 偏蓝的自发光
            specular: new THREE.Color(0.09, 0.12, 0.25), // 偏蓝的高光
            shininess: 35, // 低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第3层 - 明度更高的深蓝色，透明
          floor3: {
            color: new THREE.Color(0.04, 0.06, 0.14),
            emissive: new THREE.Color(0.005, 0.01, 0.03),
            specular: new THREE.Color(0.08, 0.12, 0.20),
            shininess: 15,
            transparent: true, // 透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第4层 - 最亮的蓝黑色，透明
          floor4: {
            color: new THREE.Color(0.05, 0.07, 0.16),
            emissive: new THREE.Color(0.005, 0.01, 0.03),
            specular: new THREE.Color(0.08, 0.12, 0.20),
            shininess: 15,
            transparent: true, // 透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          }
        }
      },
      
      // 货物组件材质
      货物: {
        // 货物主体材质
        main: {
          // 默认状态 - 蓝色塑料货物
          default: {
            color: new THREE.Color(0.2, 0.5, 0.8), // 蓝色货物
            emissive: new THREE.Color(0.05, 0.12, 0.2), // 蓝色自发光
            specular: new THREE.Color(0.4, 0.4, 0.4), // 中等高光反射
            shininess: 35, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 确保两面都可见
          },
          // 高亮状态 - 更亮的蓝色
          highlight: {
            color: new THREE.Color(0.3, 0.6, 0.9), // 更亮的蓝色
            emissive: new THREE.Color(0.1, 0.2, 0.3), // 更强的自发光
            specular: new THREE.Color(0.5, 0.5, 0.5), // 更高的高光反射
            shininess: 45, // 更高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          },
          // 选中状态 - 带绿色调的蓝色
          selected: {
            color: new THREE.Color(0.2, 0.7, 0.8), // 带绿色调的蓝色
            emissive: new THREE.Color(0.05, 0.2, 0.3), // 带绿色调的自发光
            specular: new THREE.Color(0.4, 0.6, 0.7), // 带绿色调的高光反射
            shininess: 45, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          },
          // 错误状态 - 带红色调的蓝色
          error: {
            color: new THREE.Color(0.8, 0.3, 0.3), // 红色货物
            emissive: new THREE.Color(0.2, 0.05, 0.05), // 红色自发光
            specular: new THREE.Color(0.7, 0.4, 0.4), // 带红色调的高光反射
            shininess: 30, // 中低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          }
        }
      },
      
      // 托盘组件材质
      pallet: {
        // 托盘主体材质
        main: {
          // 默认状态 - 现代灰白色塑料质感
          default: {
            color: new THREE.Color(0.82, 0.82, 0.82), // 更现代的灰白色
            emissive: new THREE.Color(0.08, 0.08, 0.08), // 适度自发光，增强可见度
            specular: new THREE.Color(0.4, 0.4, 0.4), // 提高高光反射，增强塑料质感
            shininess: 35, // 中等光泽度，更接近真实塑料
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 确保两面都可见
          },
          // 高亮状态 - 更亮的白色，用于选中或特殊状态
          highlight: {
            color: new THREE.Color(0.92, 0.92, 0.92), // 更亮的白色
            emissive: new THREE.Color(0.15, 0.15, 0.15), // 更强的自发光
            specular: new THREE.Color(0.5, 0.5, 0.5), // 更高的高光反射
            shininess: 45, // 更高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          },
          // 选中状态 - 带蓝色调的亮白色，更醒目
          selected: {
            color: new THREE.Color(0.85, 0.90, 0.95), // 带蓝色调的亮白色
            emissive: new THREE.Color(0.15, 0.20, 0.25), // 蓝色调自发光
            specular: new THREE.Color(0.5, 0.6, 0.7), // 带蓝色调的高光反射
            shininess: 45, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          },
          // 错误状态 - 带红色调的灰白色
          error: {
            color: new THREE.Color(0.95, 0.85, 0.85), // 带红色调的灰白色
            emissive: new THREE.Color(0.25, 0.15, 0.15), // 红色调自发光
            specular: new THREE.Color(0.7, 0.4, 0.4), // 带红色调的高光反射
            shininess: 30, // 中低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          },
          // 空闲状态 - 稍暗的灰色
          idle: {
            color: new THREE.Color(0.75, 0.75, 0.75), // 稍暗的灰色
            emissive: new THREE.Color(0.05, 0.05, 0.05), // 轻微自发光
            specular: new THREE.Color(0.3, 0.3, 0.3), // 低高光反射
            shininess: 25, // 低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          }
        }
      },
      
      // 货架组件材质
      shelf: {
        // 货架主体材质 - 深灰色哑光
        main: {
          // 默认状态 - 深灰色哑光
          default: {
            color: new THREE.Color(0.4, 0.4, 0.4), // 深灰色，避免过亮
            emissive: new THREE.Color(0.02, 0.02, 0.02), // 几乎没有自发光
            specular: new THREE.Color(0.1, 0.1, 0.1), // 低高光反射
            shininess: 10, // 低光泽度，哑光效果
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 确保两面都可见
          },
          // 高亮状态 - 稍亮的灰色
          highlight: {
            color: new THREE.Color(0.5, 0.5, 0.5), // 稍亮的灰色
            emissive: new THREE.Color(0.05, 0.05, 0.05), // 轻微自发光
            specular: new THREE.Color(0.2, 0.2, 0.2), // 稍高的高光反射
            shininess: 15, // 低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          }
        }
      },
    };
    
    // 材质缓存，避免重复创建相同配置的材质
    this.materialCache = new Map();
    
    // 初始化材质缓存
    this.initMaterialCache();
  }
  
  /**
   * 初始化材质缓存
   */
  initMaterialCache() {
    // 清空缓存
    this.materialCache.clear();
    
    // 预创建常用材质
    const commonMaterials = [
      ['stacker', 'body', 'default'],
      ['stacker', 'body', 'working'],
      ['stacker', 'body', 'error'],
      ['stacker', 'platform', 'default'],
      ['stacker', 'platform', 'working'],
      ['stacker', 'platform', 'error'],
      ['stacker', 'upperFork', 'default'],
      ['stacker', 'upperFork', 'working'],
      ['stacker', 'lowerFork', 'default'],
      ['stacker', 'lowerFork', 'working'],
      ['货物', 'main', 'default'],
      ['货物', 'main', 'highlight'],
      ['pallet', 'main', 'default'],
      ['pallet', 'main', 'highlight'],
      ['floor', 'main', 'floor1'],
      ['floor', 'main', 'floor2'],
      ['shelf', 'main', 'default'],
      ['shelf', 'main', 'highlight']
    ];
    
    // 预创建材质并缓存
    commonMaterials.forEach(([componentType, partType, state]) => {
      this.createMaterial(componentType, partType, state);
    });
    
    }
  
  /**
   * 获取材质配置
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   * @returns {Object} 材质配置对象
   */
  getMaterialConfig(componentType, partType, state = 'default') {
    const componentConfig = this.materials[componentType];
    if (!componentConfig) {
      console.warn(`MaterialManager: 未找到组件类型 ${componentType}`);
      return this.materials.pallet.main.default;
    }
    
    const partConfig = componentConfig[partType];
    if (!partConfig) {
      console.warn(`MaterialManager: 未找到部件类型 ${partType} 对于组件 ${componentType}`);
      return this.materials.pallet.main.default;
    }
    
    const stateConfig = partConfig[state];
    if (!stateConfig) {
      console.warn(`MaterialManager: 未找到状态 ${state} 对于组件 ${componentType} 的部件 ${partType}`);
      return partConfig.default || this.materials.pallet.main.default;
    }
    
    return stateConfig;
  }
  
  /**
   * 创建材质缓存键
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   * @returns {string} 缓存键
   */
  getMaterialCacheKey(componentType, partType, state = 'default') {
    return `${componentType}-${partType}-${state}`;
  }
  
  /**
   * 创建Three.js材质对象
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   * @returns {THREE.MeshPhongMaterial} Three.js材质对象
   */
  createMaterial(componentType, partType, state = 'default') {
    // 生成缓存键
    const cacheKey = this.getMaterialCacheKey(componentType, partType, state);
    
    // 检查缓存中是否已存在
    if (this.materialCache.has(cacheKey)) {
      return this.materialCache.get(cacheKey);
    }
    
    // 获取材质配置
    const config = this.getMaterialConfig(componentType, partType, state);
    
    // 创建材质
    const material = new THREE.MeshPhongMaterial({
      color: config.color.clone(),
      emissive: config.emissive.clone(),
      specular: config.specular.clone(),
      shininess: config.shininess,
      transparent: config.transparent,
      opacity: config.opacity,
      wireframe: config.wireframe,
      side: config.side || THREE.FrontSide,
      flatShading: config.flatShading || false,
      depthWrite: config.depthWrite !== undefined ? config.depthWrite : true,
    });
    
    // 缓存材质
    this.materialCache.set(cacheKey, material);
    
    return material;
  }
  
  /**
   * 应用材质到3D对象
   * @param {THREE.Object3D} object - 3D对象
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   */
  applyMaterialToObject(object, componentType, partType, state = 'default') {
    if (!object) {
      console.warn('MaterialManager: 对象不存在，无法应用材质');
      return;
    }
    
    // 获取材质（从缓存或创建）
    const material = this.createMaterial(componentType, partType, state);
    
    // 如果对象是网格对象，直接替换材质
    if (object.isMesh) {
      // 对于单个网格，直接使用材质引用
      object.material = material;
      object.material.needsUpdate = true;
      } 
    // 如果对象包含子对象，遍历子对象并替换材质
    else {
      object.traverse((child) => {
        if (child.isMesh) {
          // 对于子对象，克隆材质以避免共享
          const childMaterial = material.clone();
          
          // 确保克隆后的材质保留所有关键属性
          childMaterial.side = material.side;
          childMaterial.transparent = material.transparent;
          childMaterial.opacity = material.opacity;
          childMaterial.depthWrite = material.depthWrite;
          childMaterial.needsUpdate = true;
          
          // 额外确保双面渲染（针对消失问题的加强处理）
          if (material.side === THREE.DoubleSide) {
            childMaterial.side = THREE.DoubleSide;
          }
          
          child.material = childMaterial;
          child.material.needsUpdate = true;
          
          }
      });
    }
  }
  
  /**
   * 获取材质配置（公开方法）
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   * @returns {Object} 材质配置对象
   */
  getMaterial(componentType, partType, state = 'default') {
    return this.createMaterial(componentType, partType, state);
  }
  
  /**
   * 清除材质缓存
   */
  clearCache() {
    this.materialCache.clear();
    }
  
  /**
   * 重新加载材质缓存
   */
  reloadCache() {
    this.clearCache();
    this.initMaterialCache();
    }
}

// 导出材质管理器实例
const materialManager = new MaterialManager();
export default materialManager;
export { MaterialManager };