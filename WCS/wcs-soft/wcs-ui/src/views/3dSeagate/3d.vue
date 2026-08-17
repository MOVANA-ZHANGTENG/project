<template>
  <div class="three3d-wrapper">
    <!-- 3D画布容器 -->
    <div class="container3d">
      <div refs="3d" class="three-canvas" id="3d" ref="threeTarget" @mousedown="handleCanvasClick">
      </div>
    </div>

    <!-- UI层 - 在wrapper下，不受container3d的层叠上下文限制 -->
    <div v-if="jiinduDisable" class="progress-box">
      <div class="name">
        模型加载中{{ jindu }}
      </div>

      <div class="progress-bar">
        <div :style="'width:'+jindu" class="progress"></div>
      </div>
    </div>

    <!-- 调试和控制面板 -->
    <div v-if="isTest" class="debug-ui-layer">
      <!-- 调试视角控制面板 -->
      <div class="control-panel panel-debug">
        <h3>调试控制面板</h3>
        <div class="debug-controls">
          <button class="custom-btn primary" @click="setCameraToFloor(1)">观测点1</button>
          <button class="custom-btn primary" @click="setCameraToFloor(2)">观测点2</button>
          <button class="custom-btn primary" @click="setCameraToFloor(3)">观测点3</button>
          <button class="custom-btn primary" @click="setCameraToFloor(4)">观测点4</button>
          <button class="custom-btn secondary" @click="resetCamera">重置视角</button>
          <button class="custom-btn success" @click="getModelStatistics">查看模型统计</button>
          <button class="custom-btn info" @click="startStackingAnimation">🤖 机械臂码垛动画</button>
          <button
            :class="isFirstPersonMode ? 'custom-btn danger' : 'custom-btn warning'"
            @click="toggleFirstPersonMode"
          >
            {{ isFirstPersonMode ? '退出无人机模式' : '🚁 无人机飞行' }}
          </button>
          <button class="custom-btn primary" @click="testDroneControl" v-if="isFirstPersonMode">
            🔍 测试诊断
          </button>
        </div>
      </div>

      <!-- 堆垛机信息面板 -->
      <div v-if="stackerInfoVisible" class="stacker-info-panel panel-top">
        <h3>堆垛机信息</h3>
        <div class="info-content">
          <p>名称: {{ stackerInfo.name }}</p>
          <p>状态: {{ stackerInfo.status }}</p>
          <p>位置: {{ stackerInfo.position }}</p>
          <button class="custom-btn primary" @click="closeStackerInfo">关闭</button>
        </div>
      </div>
    </div>

    <!-- 无人机飞行UI -->
    <div v-if="isFirstPersonMode" class="drone-crosshair">
      <!-- 鼠标未锁定提示 -->
      <div v-if="!isMouseLocked" class="lock-hint">
        <div class="lock-hint-icon">🖱️</div>
        <div class="lock-hint-text">点击画面锁定鼠标</div>
        <div class="lock-hint-subtext">准星将固定在中心</div>
      </div>

      <!-- 准星（仅在锁定后显示） -->
      <div v-if="isMouseLocked">
        <div class="crosshair-center"></div>
        <div class="crosshair-line horizontal"></div>
        <div class="crosshair-line vertical"></div>
      </div>

      <div class="drone-hud">
        <div class="hud-info">
          <span class="hud-label">高度</span>
          <span class="hud-value">{{ Math.round(ThreeEngine && ThreeEngine.camera ? ThreeEngine.camera.position.y : 0) }}m</span>
        </div>
        <div class="hud-info">
          <span class="hud-label">速度</span>
          <span class="hud-value">{{ firstPersonController ? firstPersonController.moveSpeed.toFixed(1) : 0 }}m/s</span>
        </div>
        <div class="hud-info">
          <span class="hud-label">状态</span>
          <span class="hud-value" :style="{color: isMouseLocked ? '#00ff64' : '#ff6b6b'}">
            {{ isMouseLocked ? '🔒锁定' : '🔓未锁' }}
          </span>
        </div>
      </div>

      <!-- 控制说明面板 -->
      <div class="control-hints">
        <div class="control-section">
          <div class="control-title">🖱️ 鼠标控制</div>
          <div class="control-item">移动鼠标 → 旋转视角</div>
          <div class="control-item">左键 → 发射炮弹</div>
          <div class="control-item">ESC → 解锁鼠标</div>
        </div>
        <div class="control-section">
          <div class="control-title">⌨️ 键盘控制</div>
          <div class="control-item">↑/W → 前进</div>
          <div class="control-item">↓/S → 后退</div>
          <div class="control-item">←/A → 左移</div>
          <div class="control-item">→/D → 右移</div>
          <div class="control-item">Space → 上升</div>
          <div class="control-item">Shift → 下降</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 引入three.js
import * as THREE from "three";
import * as echarts from "echarts";
import request from "@/utils/request";
import { listCellInfo } from "@/api/wcs-base/CellInfo";
import { listRcsCarInfo } from "@/api/wcs-rcs/RcsCarInfo";
import { listRcsCarPath } from "@/api/wcs-rcs/RcsCarPath";
import { getElevatorStatus } from "@/api/wcs-jxg/JxgZk2";
import { ThreeEngine } from "./js/TEngine.js";
import { allBaseObject } from "./js/TBaseObject";
import { allLights } from "./js/TallLights";
import { allHelper } from "./js/TGridHelper.js";
import { AmbientLight, PointLight, SpotLight } from "three";
import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader.js";
import TWEEN from "@tweenjs/tween.js";
import { createSpriteTextLabel } from './js/Sprite.js';
import { DRACOLoader } from "three/examples/jsm/loaders/DRACOLoader.js";
import {
  CSS3DRenderer,
  CSS3DSprite,
} from "three/examples/jsm/renderers/CSS3DRenderer.js";
import materialManager from './js/TMaterialManager.js';
import FirstPersonController from './js/TFirstPersonController.js';
import ProjectileSystem from './js/TProjectileSystem.js';
export default {
  data() {
    return {
      isTest:true,
      jindu:"0%",
      jiinduDisable:true,

      ThreeEngine: null,
      cellCode: "1-1-1",
      fromCellCode: "15",
      toCellCode: "16",

      url:
        "ws://localhost:8007/" +
        process.env.VUE_APP_ROUTER +
        "/websocket/message",
      message: "",
      text_content: "",
      ws: null,

      texture: { offset: { x: 0 } },
      lift1texture: { offset: { x: 0 } },

      agv_fx: 1,

      agvtime: 0,
      agvRouter: [],

      labelRender: null,

      agv_sudu: 500,
      agvTask: {
        agv_fx: 1,
        isStart: 2,
      },

      requestId: null,

      // 堆垛机信息面板相关数据
      stackerInfoVisible: false,
      stackerInfo: {
        name: '',
        status: '',
        position: ''
      },

      // 第一人称控制器
      firstPersonController: null,
      isFirstPersonMode: false,  // 是否第一人称模式
      isMouseLocked: false,       // 鼠标锁定状态（Vue响应式）
      clock: null,                // 时钟（用于计算delta时间）

      // 炮弹系统
      projectileSystem: null,     // 炮弹系统实例

      // 库位定位系统
      storageLocations: {},        // 存储所有库位的位置信息 { "z-x-y": {x, y, z, layer} }
      locationGrids: {},           // 存储每层的网格信息 { layer: { startPoint, endPoint, xStep, yStep } }

      // 定时更新系统
      dataUpdateTimer: null,        // 数据更新定时器
      isUpdatingPallets: false,     // 是否正在更新托盘数据（防止重复请求）
      isUpdatingVehicles: false,    // 是否正在更新四向车数据（防止重复请求）
      lastPalletData: null,         // 上次的托盘数据（用于对比，只更新变化的部分）
      lastVehicleData: null,        // 上次的四向车数据（用于对比，只更新变化的部分）

      // 路径管道系统
      pathUpdateTimer: null,         // 路径更新定时器（每2秒）
      isUpdatingPaths: false,       // 是否正在更新路径数据（防止重复请求）
      carPathGroups: {},             // 存储每个rcsCarId的路径组对象 { rcsCarId: THREE.Group }

      // 摄像机旋转系统
      autoRotateEnabled: false,       // 是否启用自动旋转
      rotationSpeed: 3,           // 旋转速度（度/秒）
      cameraRotationAngle: 0,       // 当前旋转角度（弧度）
      sceneCenter: { x: -10, y: 0, z: 0 }, // 场景中心点（参考原本的lookAt点）
      cameraRadius: 0,              // 摄像机水平距离场景中心的距离（将在加载时计算）
      cameraHeight: 12,             // 摄像机高度（参考原本的y=12）

      // 四向车位置配置
      vehicleHeightOffset: -0.05,    // 四向车高度偏移量（负数表示降低，正数表示升高）

      // 亚克力板配置
      acrylicPlateHeightOffset: -0.18, // 亚克力板相对于库位空物体的高度偏移量（负数表示降低，正数表示升高）

      // 提升机同步配置
      elevatorConfig: {
        // 提升机位置映射：同一提升机的不同层位置
        positions: ['1-8-3', '2-8-3', '3-8-3'],  // 提升机的3层位置
        layerMap: {
          '1-8-3': 1,  // 第1层
          '2-8-3': 2,  // 第2层
          '3-8-3': 3   // 第3层
        },
        elevatorName: '提升机',  // 提升机对象名称
        heightOffset: -0.13,       // 提升机在四向车下方的偏移量（负数表示在下方，0表示与库位同高，可调整）
      },
      // 提升机状态管理
      elevatorState: {
        currentLayer: null,        // 当前所在层（1-3），null表示未初始化
        targetLayer: null,         // 目标层（移动时）
        isMoving: false,           // 是否正在移动
        targetCellCode: null,      // 目标库位编码（如 '1-8-3'）
        currentPosition: null,     // 当前位置 {x, y, z}
        targetPosition: null,      // 目标位置 {x, y, z}
      },
      // 提升机状态更新定时器
      elevatorStatusTimer: null,   // 提升机状态更新定时器
      isUpdatingElevatorStatus: false, // 是否正在更新提升机状态
    };
  },
  watch: {},
  created() { },
  mounted() {
    this.open();
  },

  beforeDestroy() {
    // 清理定时更新
    this.stopDataUpdate();
    this.stopPathUpdate();
    this.stopElevatorStatusUpdate();
    this.exit();
    this.cancelAnimation();

    // 清理炮弹系统
    if (this.projectileSystem) {
      this.projectileSystem.dispose();
    }

    // 清理第一人称控制器
    if (this.firstPersonController) {
      this.firstPersonController.dispose();
    }
  },
  methods: {
    cancelAnimation() {
      if (this.requestId!=null) {
        cancelAnimationFrame(this.requestId);
        this.requestId = null;
      }
    },
    open() {
      var that = this;
      // 初始化three.js
      that.init();
      //添加模型点击事件
      this.initObjClickHandler();
      // this.$confirm("该界面需要加载3维模型数据， 是否加载?", "提示", {
      //   confirmButtonText: "是",
      //   cancelButtonText: "否",
      //   type: "warning",
      // })
      //   .then(() => {
      //     that.init();
      //   })
      //   .catch(() => {});
    },

    // 关闭堆垛机信息面板
    closeStackerInfo() {
      this.stackerInfoVisible = false;
    },

    // 统计模型面数和顶点数
    getModelStatistics() {
      if (!this.ThreeEngine || !this.ThreeEngine.scene) {
        console.warn('场景未初始化');
        return null;
      }

      let totalTriangles = 0;  // 总三角形数（面数）
      let totalVertices = 0;   // 总顶点数
      let meshCount = 0;       // 网格对象数量
      let objectCount = 0;     // 总对象数量

      const objectDetails = []; // 详细信息

      this.ThreeEngine.scene.traverse((obj) => {
        objectCount++;

        if (obj.isMesh && obj.geometry) {
          meshCount++;

          const geometry = obj.geometry;

          // 计算三角形数量
          let triangles = 0;
          if (geometry.index !== null) {
            // 使用索引缓冲区
            triangles = geometry.index.count / 3;
          } else {
            // 无索引缓冲区
            triangles = geometry.attributes.position.count / 3;
          }

          // 计算顶点数量
          const vertices = geometry.attributes.position.count;

          totalTriangles += triangles;
          totalVertices += vertices;

          // 记录详细信息（只记录面数超过100的对象）
          if (triangles > 100) {
            objectDetails.push({
              name: obj.name || '未命名对象',
              triangles: Math.round(triangles),
              vertices: vertices,
              hasIndex: geometry.index !== null
            });
          }
        }
      });

      // 按面数排序
      objectDetails.sort((a, b) => b.triangles - a.triangles);

      const statistics = {
        totalTriangles: Math.round(totalTriangles),
        totalVertices: totalVertices,
        meshCount: meshCount,
        objectCount: objectCount,
        details: objectDetails.slice(0, 200) // 只显示前20个最复杂的对象
      };

      // 输出统计信息
      console.log('============ 模型统计信息 ============');
      console.log(`总面数（三角形）: ${statistics.totalTriangles.toLocaleString()}`);
      console.log(`总顶点数: ${statistics.totalVertices.toLocaleString()}`);
      console.log(`网格对象数量: ${statistics.meshCount}`);
      console.log(`总对象数量: ${statistics.objectCount}`);
      console.log('\n前20个最复杂的对象:');
      console.table(statistics.details);
      console.log('====================================');

      return statistics;
    },

    // 在模型加载完成后调用
    addStorageMachineLabels() {

        this.createSprite("堆垛机躯干", "堆垛机躯干",16);

        // 为所有载货台添加信息显示框
        this.addPlatformLabels();

    },

    // 为所有载货台添加信息显示框
    addPlatformLabels() {
      // 定义载货台的名称模式
      const platformNames = ['载货台1', '载货台2', '载货台3', '载货台4'];

      platformNames.forEach((platformName, index) => {
        const platform = this.ThreeEngine.getObjectByName(platformName);
        if (platform) {
          this.createSpritePallet(platformName, `载货台${index + 1}`);
          console.log(`已为 ${platformName} 添加信息显示框`);
        } else {
          console.warn(`未找到名为 ${platformName} 的载货台对象`);
        }
      });
    },

    // 为地板应用自定义材质（使用材质管理器）
    applyCustomFloorMaterials() {
      // 定义四层地板的名称（支持英文和中文命名）
      const floorNames = ['floor-1', 'floor-2', 'floor-3', 'floor-4'];
      // 对应的材质状态
      const floorStates = ['floor1', 'floor2', 'floor3', 'floor4'];

      let appliedCount = 0;

      // 为每层地板应用材质（优先查找英文命名）
      floorNames.forEach((floorName, index) => {
        const floor = this.ThreeEngine.getObjectByName(floorName);
        if (floor) {
          // 使用材质管理器应用材质
          materialManager.applyMaterialToObject(floor, 'floor', 'main', floorStates[index]);
          console.log(`已为 ${floorName} 应用地板材质（状态: ${floorStates[index]}）`);
          appliedCount++;
        }
      });

      // 查找名称包含"地板"的对象（中文命名）
        const floorObjects = [];
        this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name) {
          // 查找名称完全匹配"地板"的对象
          if (obj.name === '地板') {
            floorObjects.push({ obj, state: 'default' }); // 使用默认状态
          }
          // 查找包含"地板"的其他对象（如"地板1"、"地板2"等）
          else if (obj.name.includes('地板') && obj.name !== '地板') {
            // 尝试从名称中提取数字
            const match = obj.name.match(/\d+/);
            if (match) {
              const floorNum = parseInt(match[0]);
              const stateIndex = (floorNum - 1) % 4; // 循环使用4种状态
              floorObjects.push({ obj, state: floorStates[stateIndex] });
            } else {
              // 没有数字，使用默认状态
              floorObjects.push({ obj, state: 'default' });
            }
          }
          // 也查找包含"floor"的对象（英文命名，但不在上面的列表中）
          else if (obj.name.toLowerCase().includes('floor') && !floorNames.includes(obj.name)) {
            // 尝试从名称中提取数字
            const match = obj.name.match(/\d+/);
            if (match) {
              const floorNum = parseInt(match[0]);
              const stateIndex = (floorNum - 1) % 4;
              floorObjects.push({ obj, state: floorStates[stateIndex] });
            } else {
              floorObjects.push({ obj, state: 'default' });
            }
          }
        }
      });

      // 为找到的地板对象应用材质
      if (floorObjects.length > 0) {
        console.log(`找到 ${floorObjects.length} 个地板对象:`, floorObjects.map(item => `${item.obj.name} (状态: ${item.state})`));

        floorObjects.forEach((item) => {
          materialManager.applyMaterialToObject(item.obj, 'floor', 'main', item.state);
          console.log(`已为 ${item.obj.name} 应用地板材质（状态: ${item.state}）`);
            appliedCount++;
        });
      }

      console.log(`共为 ${appliedCount} 个地板应用了材质`);
    },

    // 为堆垛机躯干应用自定义材质（使用材质管理器）
    applyStackerBodyMaterials() {
      // 定义堆垛机躯干的名称模式
      const stackerBodyNames = ['躯干1', '躯干2', '躯干3', '躯干4'];

      let appliedCount = 0;
      // 为每个堆垛机躯干应用材质
      stackerBodyNames.forEach((bodyName) => {
        const stackerBody = this.ThreeEngine.getObjectByName(bodyName);
        if (stackerBody) {
          // 使用材质管理器应用材质
          materialManager.applyMaterialToObject(stackerBody, 'stacker', 'body', 'default');
          console.log(`已为 ${bodyName} 应用堆垛机躯干材质（来自材质管理器）`);
          appliedCount++;
        } else {
          console.warn(`未找到名为 ${bodyName} 的堆垛机躯干对象`);
        }
      });

      console.log(`共为 ${appliedCount} 个堆垛机躯干应用了材质`);
    },

    // 为货架应用自定义材质（使用材质管理器）
    applyShelfMaterials() {
      // 定义货架的名称模式
      const shelfNames = ['货架1', '货架2', '货架3', '货架4','货架5', '货架6', '货架7', '货架8', '货架9', '货架10'];

      let appliedCount = 0;
      // 为每个货架应用材质
      shelfNames.forEach((shelfName) => {
        const shelf = this.ThreeEngine.getObjectByName(shelfName);
        if (shelf) {
          // 使用材质管理器应用材质
          materialManager.applyMaterialToObject(shelf, 'shelf', 'main', 'default');
          console.log(`已为 ${shelfName} 应用货架材质（来自材质管理器）`);
          appliedCount++;
        } else {
          console.warn(`未找到名为 ${shelfName} 的货架对象`);
        }
      });

      console.log(`共为 ${appliedCount} 个货架应用了材质`);
    },

    // 为墙体应用科技感材质（使用材质管理器）
    applyWallMaterials() {
      // 查找所有墙体组件（命名为墙体+index）
      const walls = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('墙体')) {
          walls.push(obj);
        }
      });

      walls.forEach((wall) => {
        // 使用材质管理器应用材质
        materialManager.applyMaterialToObject(wall, 'wall', 'main', 'default');
        console.log(`已为 ${wall.name} 应用墙体材质（来自材质管理器）`);
      });

      if (walls.length === 0) {
        console.warn('未找到墙体组件');
      } else {
        console.log(`共为 ${walls.length} 个墙体组件应用了科技感材质`);
      }
    },

    // 为载货台应用自定义材质（使用材质管理器）
    applyPlatformMaterials() {
      // 查找所有载货台组件（带索引）
      const platforms = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('载货台')) {
          platforms.push(obj);
        }
      });

      platforms.forEach((platform) => {
        // 使用材质管理器应用材质
        materialManager.applyMaterialToObject(platform, 'stacker', 'platform', 'default');
        console.log(`已为 ${platform.name} 应用载货台材质（来自材质管理器）`);
      });

      if (platforms.length === 0) {
        console.warn('未找到载货台组件');
      } else {
        console.log(`共为 ${platforms.length} 个载货台应用了材质`);
      }
    },

    // 为上货叉应用自定义材质（使用材质管理器）
    applyUpperForkMaterials() {
      // 查找所有上货叉组件（带索引）
      const upperForks = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('上货叉')) {
          upperForks.push(obj);
        }
      });

      upperForks.forEach((fork) => {
        // 使用材质管理器应用材质
        materialManager.applyMaterialToObject(fork, 'stacker', 'upperFork', 'default');
        console.log(`已为 ${fork.name} 应用上货叉材质（来自材质管理器）`);
      });

      if (upperForks.length === 0) {
        console.warn('未找到上货叉组件');
      } else {
        console.log(`共为 ${upperForks.length} 个上货叉应用了材质`);
      }
    },

    // 为下货叉应用自定义材质（使用材质管理器）
    applyLowerForkMaterials() {
      // 查找所有下货叉组件（带索引）
      const lowerForks = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('下货叉')) {
          lowerForks.push(obj);
        }
      });

      lowerForks.forEach((fork) => {
        // 使用材质管理器应用材质
        materialManager.applyMaterialToObject(fork, 'stacker', 'lowerFork', 'default');
        console.log(`已为 ${fork.name} 应用下货叉材质（来自材质管理器）`);
      });

      if (lowerForks.length === 0) {
        console.warn('未找到下货叉组件');
      } else {
        console.log(`共为 ${lowerForks.length} 个下货叉应用了材质`);
      }
    },

    // 为RGV应用材质（使用材质管理器）
    applyRgvMaterials() {
      const rgvBodya = []; // 存储所有rgv车体组件
      const rgvZht = []; // 存储所有rgv载货台组件
      const rgvZhtLt = []; // 存储所有rgv链条组件
      const rgvZst = []; // 存储所有rgv装饰条组件
      const rgvGd = []; // 存储所有rgv轨道组件

      // 收集所有RGV组件
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('rgv车体-')) {
          rgvBodya.push(obj);
        }
        if (obj.name && obj.name.includes('rgv载货台-')) {
          rgvZht.push(obj);
        }
        if (obj.name && obj.name.includes('rgv载货台链条-')) {
          rgvZhtLt.push(obj);
        }
        if (obj.name && obj.name.includes('rgv轨道-')) {
          rgvGd.push(obj);
        }
        if (obj.name && obj.name.includes('rgv装饰条-')) {
          rgvZst.push(obj);
        }
      });

      // 使用材质管理器为RGV车体应用材质
      rgvBodya.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'body', 'default');
        console.log(`已为 ${obj.name} 应用RGV车体材质（来自材质管理器）`);
      });

      // 使用材质管理器为RGV载货台应用材质
      rgvZht.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'platform', 'default');
        console.log(`已为 ${obj.name} 应用RGV载货台材质（来自材质管理器）`);
      });

      // 使用材质管理器为RGV链条应用材质
      rgvZhtLt.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'chain', 'default');
        console.log(`已为 ${obj.name} 应用RGV链条材质（来自材质管理器）`);
      });

      // 使用材质管理器为RGV装饰条应用材质
      rgvZst.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'decoration', 'default');
        console.log(`已为 ${obj.name} 应用RGV装饰条材质（来自材质管理器）`);
      });

      // 使用材质管理器为RGV轨道应用材质
      rgvGd.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'track', 'default');
        console.log(`已为 ${obj.name} 应用RGV轨道材质（来自材质管理器）`);
      });

      // 输出统计信息
      console.log(`RGV材质应用完成: 车体${rgvBodya.length}个, 载货台${rgvZht.length}个, 链条${rgvZhtLt.length}个, 装饰条${rgvZst.length}个, 轨道${rgvGd.length}个`);
    },

    // 为输送线框架应用材质（使用材质管理器）
    applyConveyorMaterials() {
      const conveyorFrames = []; // 存储所有输送线框架组件
      const conveyorBelts = []; // 存储所有输送带组件
      const conveyorRollers = []; // 存储所有滚筒组件
      const chainGrooves = []; // 存储所有链条槽组件
      const guideStrips = []; // 存储所有导向条组件

      // 收集所有输送线相关组件
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name) {
          // 输送线框架
          if (obj.name.includes('输送线框架') || obj.name.includes('conveyor-frame')) {
            conveyorFrames.push(obj);
          }
          // 输送带
          if (obj.name.includes('输送带') || obj.name.includes('conveyor-belt')) {
            conveyorBelts.push(obj);
          }
          // 滚筒
          if (obj.name.includes('滚筒') || obj.name.includes('roller')) {
            conveyorRollers.push(obj);
          }
          // 链条槽
          if (obj.name.includes('链条槽') ||
              obj.name.includes('chain-groove') ||
              obj.name.includes('chain-slot') ||
              obj.name.includes('槽道')) {
            chainGrooves.push(obj);
          }
          // 导向条
          if (obj.name.includes('导向条') ||
              obj.name.includes('guide-strip') ||
              obj.name.includes('guide-bar') ||
              obj.name.includes('导轨') ||
              obj.name.includes('引导条')) {
            guideStrips.push(obj);
          }
        }
      });

      // 使用材质管理器为输送线框架应用材质
      conveyorFrames.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'frame', 'default');
        console.log(`已为 ${obj.name} 应用输送线框架材质（来自材质管理器）`);
      });

      // 使用材质管理器为输送带应用材质
      conveyorBelts.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'belt', 'default');
        console.log(`已为 ${obj.name} 应用输送带材质（来自材质管理器）`);
      });

      // 使用材质管理器为滚筒应用材质
      conveyorRollers.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'roller', 'default');
        console.log(`已为 ${obj.name} 应用滚筒材质（来自材质管理器）`);
      });

      // 使用材质管理器为链条槽应用材质
      chainGrooves.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'chainGroove', 'default');
        console.log(`已为 ${obj.name} 应用链条槽材质（来自材质管理器）`);
      });

      // 使用材质管理器为导向条应用材质
      guideStrips.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'guideStrip', 'default');
        console.log(`已为 ${obj.name} 应用导向条材质（来自材质管理器）`);
      });

      // 输出统计信息
      console.log(`输送线材质应用完成: 框架${conveyorFrames.length}个, 输送带${conveyorBelts.length}个, 滚筒${conveyorRollers.length}个, 链条槽${chainGrooves.length}个, 导向条${guideStrips.length}个`);
    },

    // 为托盘应用浅蓝色材质（使用材质管理器）
    // 托盘命名格式：pallet前缀 + 数字，如 pallet4840
    applyPalletMaterials() {
      const pallets = []; // 存储所有托盘组件

      // 收集所有托盘相关组件
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name) {
          const name = obj.name.toLowerCase();
          // 优先匹配 pallet 前缀格式（如 pallet4840）
          if (name.startsWith('pallet')) {
          pallets.push(obj);
          }
          // 也匹配包含"托盘"或"货物"的中文命名
          else if (obj.name.includes('托盘') || obj.name.includes('货物')) {
            pallets.push(obj);
          }
        }
      });

      // 使用材质管理器为托盘应用浅蓝色材质
      pallets.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'pallet', 'main', 'default');
        console.log(`已为 ${obj.name} 应用托盘浅蓝色材质（来自材质管理器）`);
      });

      // 输出统计信息
      if (pallets.length === 0) {
        console.warn('未找到托盘组件');
      } else {
        console.log(`共为 ${pallets.length} 个托盘应用了浅蓝色材质`);
        console.log('托盘列表:', pallets.map(obj => obj.name));
      }
    },

    // 为立柱应用材质（使用材质管理器）
    applyColumnMaterials() {
      const columns = []; // 存储所有立柱组件

      // 收集所有立柱组件（名称包含"立柱"）
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('立柱')) {
          columns.push(obj);
        }
      });

      // 使用材质管理器为立柱应用深灰色金属材质
      columns.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'structure', 'column', 'default');
        console.log(`已为 ${obj.name} 应用立柱材质（来自材质管理器）`);
      });

      // 输出统计信息
      if (columns.length === 0) {
        console.warn('未找到立柱组件');
      } else {
        console.log(`共为 ${columns.length} 个立柱应用了材质`);
      }
    },

    // 为横梁-轨道应用材质（使用材质管理器）
    // 注意："横梁-轨道"是一个完整的对象名称
    applyBeamRailMaterials() {
      const beamRails = []; // 存储所有横梁-轨道组件

      // 收集所有横梁-轨道组件（名称完全匹配"横梁-轨道"）
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('横梁-轨道')) {
          beamRails.push(obj);
        }
      });

      // 使用材质管理器为横梁-轨道应用深灰色金属材质
      // 使用横梁材质，因为横梁-轨道是横向支撑结构
      beamRails.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'structure', 'beam', 'default');
        console.log(`已为 ${obj.name} 应用横梁-轨道材质（来自材质管理器）`);
      });

      // 输出统计信息
      if (beamRails.length === 0) {
        console.warn('未找到横梁-轨道组件');
      } else {
        console.log(`共为 ${beamRails.length} 个横梁-轨道应用了材质`);
      }
    },

    // 为四向车应用材质（使用材质管理器）
    // 四向车命名格式：四向车[组件名][车号]，如 四向车车体4840
    applyFourWayVehicleMaterials() {
      const vehicles = {
        body: [],           // 车体
        topCover: [],       // 上盖板
        wheel: [],          // 轮子
        metalFrame: [],     // 金属边框
        alarmLight: [],     // 报警灯
        runningIndicator: [], // 运行指示灯
        lifting: []         // 顶升
      };

      // 收集所有四向车组件
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.startsWith('四向车')) {
          const name = obj.name;

          // 根据组件名称分类
          if (name.includes('车体')) {
            vehicles.body.push(obj);
          } else if (name.includes('上盖板')) {
            vehicles.topCover.push(obj);
          } else if (name.includes('轮子')) {
            vehicles.wheel.push(obj);
          } else if (name.includes('金属边框')) {
            vehicles.metalFrame.push(obj);
          } else if (name.includes('报警灯')) {
            vehicles.alarmLight.push(obj);
          } else if (name.includes('运行指示灯')) {
            vehicles.runningIndicator.push(obj);
          } else if (name.includes('顶升')) {
            vehicles.lifting.push(obj);
          }
        }
      });

      // 为车体应用材质
      vehicles.body.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'fourWayVehicle', 'body', 'default');
        console.log(`已为 ${obj.name} 应用四向车车体材质`);
      });

      // 为上盖板应用材质
      vehicles.topCover.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'fourWayVehicle', 'topCover', 'default');
        console.log(`已为 ${obj.name} 应用四向车上盖板材质`);
      });

      // 为轮子应用材质
      vehicles.wheel.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'fourWayVehicle', 'wheel', 'default');
        console.log(`已为 ${obj.name} 应用四向车轮子材质`);
      });

      // 为金属边框应用材质
      vehicles.metalFrame.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'fourWayVehicle', 'metalFrame', 'default');
        console.log(`已为 ${obj.name} 应用四向车金属边框材质`);
      });

      // 为报警灯应用材质
      vehicles.alarmLight.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'fourWayVehicle', 'alarmLight', 'default');
        console.log(`已为 ${obj.name} 应用四向车报警灯材质`);
      });

      // 为运行指示灯应用材质
      vehicles.runningIndicator.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'fourWayVehicle', 'runningIndicator', 'default');
        console.log(`已为 ${obj.name} 应用四向车运行指示灯材质`);
      });

      // 为顶升应用材质
      vehicles.lifting.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'fourWayVehicle', 'lifting', 'default');
        console.log(`已为 ${obj.name} 应用四向车顶升材质`);
      });

      // 输出统计信息
      const totalCount = Object.values(vehicles).reduce((sum, arr) => sum + arr.length, 0);
      if (totalCount === 0) {
        console.warn('未找到四向车组件');
      } else {
        console.log(`四向车材质应用完成:`);
        console.log(`  车体: ${vehicles.body.length}个`);
        console.log(`  上盖板: ${vehicles.topCover.length}个`);
        console.log(`  轮子: ${vehicles.wheel.length}个`);
        console.log(`  金属边框: ${vehicles.metalFrame.length}个`);
        console.log(`  报警灯: ${vehicles.alarmLight.length}个`);
        console.log(`  运行指示灯: ${vehicles.runningIndicator.length}个`);
        console.log(`  顶升: ${vehicles.lifting.length}个`);
        console.log(`  总计: ${totalCount}个组件`);
      }
    },

    // 库位定位系统 - 根据定位点计算所有库位位置
    // 命名规则：z-x-y，z是层，x是1-8，y是1-5
    // 每层定义两个定位点：z-1-1 和 z-8-5
    calculateStorageLocations() {
      if (!this.ThreeEngine || !this.ThreeEngine.scene) {
        console.warn('场景未初始化，无法计算库位位置');
        return;
      }

      // 定义层数和每层的x、y范围
      const layers = [1, 2, 3, 4]; // 假设有4层，可根据实际情况调整
      const xRange = { min: 1, max: 8 }; // x范围：1-8
      const yRange = { min: 1, max: 5 }; // y范围：1-5

      this.storageLocations = {};
      this.locationGrids = {};

      layers.forEach((layer) => {
        // 查找每层的两个定位点
        const startPointName = `${layer}-1-1`;  // z-1-1
        const endPointName = `${layer}-8-5`;    // z-8-5

        const startPoint = this.ThreeEngine.getObjectByName(startPointName);
        const endPoint = this.ThreeEngine.getObjectByName(endPointName);

        if (!startPoint || !endPoint) {
          console.warn(`第${layer}层缺少定位点: ${startPointName} 或 ${endPointName}`);
          return;
        }

        // 获取定位点的位置
        const startPos = startPoint.position;
        const endPos = endPoint.position;

        // 计算x和y方向的步长
        // z-1-1 是左下角 (x=1, y=1)
        // z-8-5 是右上角 (x=8, y=5)
        // x方向：从x=1到x=8，共7步
        // y方向：从y=1到y=5，共4步

        const xSteps = xRange.max - xRange.min; // 7步（从1到8）
        const ySteps = yRange.max - yRange.min; // 4步（从1到5）

        // 查找辅助定位点来确定x和y方向的步长
        // 现在每层都有三个定位点：z-1-1（起点）、z-1-5（y方向终点），z-8-5（终点）
        const y1_5Point = this.ThreeEngine.getObjectByName(`${layer}-1-5`); // y方向的终点（左边界）
        const x8_1Point = this.ThreeEngine.getObjectByName(`${layer}-8-1`); // x方向的终点（下边界，可选）
        let xStepVector, yStepVector;

        if (y1_5Point) {
          // 有z-1-5定位点，可以准确计算y方向
          // y方向：从z-1-1到z-1-5（y从1到5，x保持1）
          yStepVector = {
            x: (y1_5Point.position.x - startPos.x) / ySteps,
            y: (y1_5Point.position.y - startPos.y) / ySteps,
            z: (y1_5Point.position.z - startPos.z) / ySteps
          };

          if (x8_1Point) {
            // 最佳情况：z-1-5和z-8-1都存在，可以准确计算两个方向
            // x方向：从z-1-1到z-8-1（x从1到8，y保持1）
            xStepVector = {
              x: (x8_1Point.position.x - startPos.x) / xSteps,
              y: (x8_1Point.position.y - startPos.y) / xSteps,
              z: (x8_1Point.position.z - startPos.z) / xSteps
            };

            console.log(`第${layer}层使用完整定位点（z-1-1, z-1-5, z-8-1）计算库位位置`);
          } else {
            // 有z-1-5但没有z-8-1，使用z-1-5和z-8-5计算x方向
            // x方向：从z-1-5到z-8-5（x从1到8，y保持5）
            xStepVector = {
              x: (endPos.x - y1_5Point.position.x) / xSteps,
              y: (endPos.y - y1_5Point.position.y) / xSteps,
              z: (endPos.z - y1_5Point.position.z) / xSteps
            };

            console.log(`第${layer}层使用定位点（z-1-1, z-1-5, z-8-5）计算库位位置`);
          }
        } else if (x8_1Point) {
          // 只有z-8-1，没有z-1-5，使用z-8-1和z-8-5计算y方向
          // x方向：从z-1-1到z-8-1
          xStepVector = {
            x: (x8_1Point.position.x - startPos.x) / xSteps,
            y: (x8_1Point.position.y - startPos.y) / xSteps,
            z: (x8_1Point.position.z - startPos.z) / xSteps
          };

          // y方向：从z-8-1到z-8-5（y从1到5，x保持8）
          yStepVector = {
            x: (endPos.x - x8_1Point.position.x) / ySteps,
            y: (endPos.y - x8_1Point.position.y) / ySteps,
            z: (endPos.z - x8_1Point.position.z) / ySteps
          };

          console.log(`第${layer}层使用定位点（z-1-1, z-8-1, z-8-5）计算库位位置`);
        } else {
          // 只有z-1-1和z-8-5两个点，需要假设网格是规则的矩形
          // 从z-1-1到z-8-5的向量 = x方向向量 * 7 + y方向向量 * 4
          // 由于无法唯一确定，我们需要合理的假设
          const totalVector = {
            x: endPos.x - startPos.x,
            y: endPos.y - startPos.y,
            z: endPos.z - startPos.z
          };

          // 假设网格是规则的矩形，且x方向和y方向的变化是独立的
          // 最合理的假设：x方向主要在水平面（XZ平面），y方向主要在垂直方向（Y轴）
          // 或者：x方向和y方向在同一个平面上，且相互垂直

          // 方案1：假设x方向主要在X轴和Z轴，y方向主要在Y轴
          // 计算总向量在各个轴上的分量
          const totalDistance = Math.sqrt(
            totalVector.x * totalVector.x +
            totalVector.y * totalVector.y +
            totalVector.z * totalVector.z
          );

          // 假设x方向主要在XZ平面，y方向主要在Y轴
          // x方向：假设X和Z的变化与xSteps成正比
          const xzDistance = Math.sqrt(totalVector.x * totalVector.x + totalVector.z * totalVector.z);
          const xzRatio = xSteps / (xSteps + ySteps); // x方向占总步数的比例

          xStepVector = {
            x: (totalVector.x / xSteps) * xzRatio,
            y: 0, // 假设x方向在Y轴上不变
            z: (totalVector.z / xSteps) * xzRatio
          };

          yStepVector = {
            x: (totalVector.x / ySteps) * (1 - xzRatio),
            y: totalVector.y / ySteps, // y方向主要在Y轴上
            z: (totalVector.z / ySteps) * (1 - xzRatio)
          };

          // 方案2（备选）：如果方案1不准确，可以尝试均匀分配
          // 但这样会导致网格不准确

          console.warn(`第${layer}层只有两个定位点，使用估算方法。建议在模型中添加z-1-5或z-8-1定位点以获得准确结果`);
          console.log(`估算的x方向步长:`, xStepVector);
          console.log(`估算的y方向步长:`, yStepVector);
        }

        // 保存网格信息
        this.locationGrids[layer] = {
          startPoint: { x: startPos.x, y: startPos.y, z: startPos.z },
          endPoint: { x: endPos.x, y: endPos.y, z: endPos.z },
          xStepVector: xStepVector,
          yStepVector: yStepVector
        };

        // 计算所有库位的位置
        for (let x = xRange.min; x <= xRange.max; x++) {
          for (let y = yRange.min; y <= yRange.max; y++) {
            const locationName = `${layer}-${x}-${y}`;

            // 计算位置：起点 + x方向偏移 + y方向偏移
            // x方向的偏移量（x方向在3D空间中的x、y、z分量）
            const xStepCount = x - xRange.min; // x方向的步数
            const xDirOffset = {
              x: xStepCount * xStepVector.x,
              y: xStepCount * xStepVector.y,
              z: xStepCount * xStepVector.z
            };

            // y方向的偏移量（y方向在3D空间中的x、y、z分量）
            const yStepCount = y - yRange.min; // y方向的步数
            const yDirOffset = {
              x: yStepCount * yStepVector.x,
              y: yStepCount * yStepVector.y,
              z: yStepCount * yStepVector.z
            };

            // 最终位置 = 起点 + x方向偏移 + y方向偏移
            const position = {
              x: startPos.x + xDirOffset.x + yDirOffset.x,
              y: startPos.y + xDirOffset.y + yDirOffset.y,
              z: startPos.z + xDirOffset.z + yDirOffset.z,
              layer: layer,
              xIndex: x,
              yIndex: y
            };

            this.storageLocations[locationName] = position;

            // 如果场景中已存在该库位对象，更新其位置
            const existingLocation = this.ThreeEngine.getObjectByName(locationName);
            if (existingLocation) {
              existingLocation.position.set(position.x, position.y, position.z);
              console.log(`已更新库位 ${locationName} 的位置:`, position);
            } else {
              // 如果不存在，可以创建一个空对象作为库位标记
              // 这里先不创建，只记录位置信息
              console.log(`计算库位 ${locationName} 的位置:`, position);
            }
          }
        }

        console.log(`第${layer}层库位计算完成，共${(xRange.max - xRange.min + 1) * (yRange.max - yRange.min + 1)}个库位`);
      });

      console.log('库位定位系统初始化完成，共计算', Object.keys(this.storageLocations).length, '个库位位置');
    },

    // 创建每层的亚克力板
    createLayerAcrylicPlates() {
      if (!this.ThreeEngine || !this.ThreeEngine.scene) {
        console.warn('场景未初始化，无法创建亚克力板');
        return;
      }

      if (!this.locationGrids || Object.keys(this.locationGrids).length === 0) {
        console.warn('库位网格信息未计算，无法创建亚克力板');
        return;
      }

      // 定义每层的颜色（不同颜色区分不同层）
      const layerColors = {
        1: new THREE.Color(0.1, 0.3, 0.6),   // 第1层：深蓝色
        2: new THREE.Color(0.1, 0.5, 0.3),   // 第2层：深绿色
        3: new THREE.Color(0.5, 0.3, 0.1),   // 第3层：深橙色
        4: new THREE.Color(0.5, 0.1, 0.5)    // 第4层：深紫色
      };

      // 遍历每一层
      Object.keys(this.locationGrids).forEach(layerStr => {
        const layer = parseInt(layerStr);
        const grid = this.locationGrids[layer];

        if (!grid || !grid.startPoint || !grid.endPoint) {
          console.warn(`第${layer}层网格信息不完整，跳过创建亚克力板`);
          return;
        }

        // 计算亚克力板的尺寸和位置
        const startPoint = grid.startPoint;
        const endPoint = grid.endPoint;
        const xStepVector = grid.xStepVector;
        const yStepVector = grid.yStepVector;

        // 计算库位步长
        const xStepLength = Math.sqrt(
          xStepVector.x * xStepVector.x +
          xStepVector.y * xStepVector.y +
          xStepVector.z * xStepVector.z
        );
        const yStepLength = Math.sqrt(
          yStepVector.x * yStepVector.x +
          yStepVector.y * yStepVector.y +
          yStepVector.z * yStepVector.z
        );

        // 计算X方向的长度（从库位1的中心到库位8的中心，共7步）
        // 但要覆盖到边缘，需要加上左右各半个库位边长，总共加1个库位边长
        const xTotalLength = xStepLength * 7 + xStepLength; // 7步 + 1个库位边长（左右各0.5）

        // 计算Y方向的长度（从库位1的中心到库位5的中心，共4步）
        // 但要覆盖到边缘，需要加上前后各半个库位边长，总共加1个库位边长
        const yTotalLength = yStepLength * 4 + yStepLength; // 4步 + 1个库位边长（前后各0.5）

        // 计算矩形的中心点
        // 起点是库位1-1的中心，终点是库位8-5的中心
        // 由于要覆盖到边缘，中心点仍然是起点和终点的中点（因为左右/前后各扩展0.5步，中心不变）
        // 但更准确的方式是：起点 + X方向3.5步 + Y方向2步（因为是从1到8，中间是3.5步；从1到5，中间是2步）
        const centerX = startPoint.x + (xStepVector.x * 3.5) + (yStepVector.x * 2);
        // Y坐标使用起点的Y坐标 + 高度偏移量（负数表示降低，正数表示升高）
        const centerY = startPoint.y + this.acrylicPlateHeightOffset;
        const centerZ = startPoint.z + (xStepVector.z * 3.5) + (yStepVector.z * 2);

        // 计算XZ平面上的宽度和长度（用于创建矩形板）
        // X方向在XZ平面上的投影
        const xDirXZ = Math.sqrt(xStepVector.x * xStepVector.x + xStepVector.z * xStepVector.z);
        const xWidth = xDirXZ * 8; // 7步 + 1个库位边长 = 8个库位宽度

        // Y方向在XZ平面上的投影
        const yDirXZ = Math.sqrt(yStepVector.x * yStepVector.x + yStepVector.z * yStepVector.z);
        const yLength = yDirXZ * 5; // 4步 + 1个库位边长 = 5个库位宽度

        // 使用计算出的尺寸（已经包含了边缘库位的半个边长）
        const finalWidth = xWidth;
        const finalLength = yLength;

        // 创建亚克力板几何体（薄板，高度很小）
        const plateHeight = 0.05; // 亚克力板厚度
        const plateGeometry = new THREE.BoxGeometry(finalWidth, plateHeight, finalLength);

        // 创建亚克力板材质（半透明，带颜色）
        const plateColor = layerColors[layer] || new THREE.Color(0.3, 0.3, 0.3); // 默认灰色
        const plateMaterial = new THREE.MeshPhongMaterial({
          color: plateColor,
          transparent: true,
          opacity: 0.3, // 30%不透明度，亚克力板效果
          side: THREE.DoubleSide, // 双面渲染
          shininess: 100, // 高光泽度，模拟亚克力板的光滑表面
          specular: new THREE.Color(0.8, 0.8, 0.8), // 高光反射
          emissive: new THREE.Color(0, 0, 0), // 不自发光
        });

        // 创建亚克力板网格
        const plateMesh = new THREE.Mesh(plateGeometry, plateMaterial);
        plateMesh.name = `acrylicPlate_${layer}`;
        plateMesh.position.set(centerX, centerY, centerZ);

        // 计算旋转角度：X方向向量在XZ平面上的角度
        if (xStepVector.x !== 0 || xStepVector.z !== 0) {
          const angle = Math.atan2(xStepVector.z, xStepVector.x);
          plateMesh.rotation.y = angle;
        }

        // 将亚克力板添加到场景
        this.ThreeEngine.addObject(plateMesh);

        console.log(`已创建第${layer}层亚克力板: 位置(${centerX.toFixed(2)}, ${centerY.toFixed(2)}, ${centerZ.toFixed(2)}), 尺寸(${finalWidth.toFixed(2)} x ${finalLength.toFixed(2)}), 颜色(${plateColor.r.toFixed(2)}, ${plateColor.g.toFixed(2)}, ${plateColor.b.toFixed(2)})`);
      });

      console.log('所有层的亚克力板创建完成');
    },

    // 获取库位位置
    getStorageLocation(layer, x, y) {
      const locationName = `${layer}-${x}-${y}`;
      return this.storageLocations[locationName] || null;
    },

    // 查询库位并放置托盘
    // wareCode: 仓库编码，如 "sxc"
    // invenState: 1=有托盘，0=无托盘
    // isIncremental: 是否为增量更新（只更新变化的部分）
    async loadStoragePallets(wareCode = 'sxc', isIncremental = false) {
      if (!this.ThreeEngine || !this.ThreeEngine.scene) {
        console.warn('场景未初始化，无法加载托盘');
        return;
      }

      // 防止重复请求
      if (this.isUpdatingPallets) {
        if (!isIncremental) {
          console.log('托盘数据正在更新中，跳过本次请求');
        }
        return;
      }

      this.isUpdatingPallets = true;

      try {
        // 获取基础pallet对象用于克隆
        const basePallet = this.ThreeEngine.getObjectByName("pallet");
        if (!basePallet) {
          console.error('未找到基础pallet对象，无法创建托盘');
          return;
        }

        // 分页查询所有库位
        const pageSize = 100; // 每页查询数量
        let pageNum = 1;
        let total = 0;
        let allCellList = []; // 存储所有库位数据
        const maxPages = 1000; // 最大页数限制，防止无限循环
        let currentPage = 0;

        console.log(`开始分页查询库位，wareCode: ${wareCode}`);

        // 循环查询所有页的数据
        do {
          currentPage++;
          if (currentPage > maxPages) {
            console.warn(`查询页数超过最大限制${maxPages}页，停止查询`);
            break;
          }

          const response = await listCellInfo({
            wareCode: wareCode,
            pageNum: pageNum,
            pageSize: pageSize
          });

          if (response.code !== 200) {
            console.error(`查询库位失败（第${pageNum}页）:`, response.msg);
            const errorMsg = response.msg || `查询库位失败（第${pageNum}页）`;
            this.$modal?.msgError(errorMsg);
            break;
          }

          // 累加数据
          const currentPageRows = response.rows || [];
          allCellList = allCellList.concat(currentPageRows);
          total = response.total || 0;

          console.log(`已查询第${pageNum}页，本页${currentPageRows.length}条，累计${allCellList.length}/${total}条`);

          // 判断是否还有下一页
          // 如果当前页数据量小于每页大小，说明已经是最后一页
          // 或者累计数据量已经达到总数
          if (currentPageRows.length < pageSize || allCellList.length >= total) {
            if (!isIncremental) {
              console.log(`已查询完所有数据，共${allCellList.length}条`);
            }
            break; // 已查询完所有数据
          }

          pageNum++; // 查询下一页
        } while (true);

        if (!isIncremental) {
          console.log(`库位查询完成，共查询到 ${allCellList.length} 个库位（总计${total}个），共查询${currentPage}页`);
        }

        let cellList = allCellList;

        // 如果是增量更新，对比数据变化，只更新变化的部分
        if (isIncremental && this.lastPalletData) {
          const lastDataMap = new Map();
          this.lastPalletData.data.forEach(item => {
            lastDataMap.set(item.code, item.invenState);
          });

          const currentDataMap = new Map();
          allCellList.forEach(cell => {
            currentDataMap.set(cell.code, cell.invenState || 0);
          });

          // 找出变化的库位
          const changedCells = allCellList.filter(cell => {
            const lastState = lastDataMap.get(cell.code);
            const currentState = cell.invenState || 0;
            return lastState !== currentState;
          });

          // 找出被移除的库位（上次有，这次没有）
          const removedCells = [];
          lastDataMap.forEach((state, code) => {
            if (!currentDataMap.has(code)) {
              removedCells.push({ code, invenState: state });
            }
          });

          // 只处理变化的库位
          cellList = [...changedCells, ...removedCells];

          if (changedCells.length === 0 && removedCells.length === 0) {
            console.log('托盘数据无变化，跳过更新');
            this.isUpdatingPallets = false;
            return;
          }

          console.log(`检测到 ${changedCells.length} 个变化的库位，${removedCells.length} 个移除的库位`);
        }

        let palletCount = 0; // 放置的托盘数量
        let removedCount = 0; // 移除的托盘数量

        // 遍历库位列表（增量更新时只处理变化的部分）
        cellList.forEach((cell) => {
          // 库位编码格式应该是 z-x-y
          const cellCode = cell.code;

          // 解析库位编码，提取层、x、y
          const match = cellCode.match(/^(\d+)-(\d+)-(\d+)$/);
          if (!match) {
            console.warn(`库位编码格式不正确: ${cellCode}，应为 z-x-y 格式`);
            return;
          }

          const layer = parseInt(match[1]);
          const x = parseInt(match[2]);
          const y = parseInt(match[3]);

          // 获取库位位置
          const location = this.getStorageLocation(layer, x, y);
          if (!location) {
            console.warn(`未找到库位 ${cellCode} 的位置信息，请先计算库位位置`);
            return;
          }

          // invenState: 1=有托盘，0=无托盘
          const invenState = cell.invenState || 0;
          const palletCode = `pallet${cellCode}`; // 托盘名称：pallet + 库位编码

          // 查找库位位置的空物体（如果存在）
          let locationObj = this.ThreeEngine.getObjectByName(cellCode);

          if (invenState === 1) {
            // 有托盘，需要放置托盘
            // 先移除已存在的托盘（如果有）
            this.remove(palletCode);

            // 如果库位位置对象不存在，创建一个空对象作为库位位置标记
            if (!locationObj) {
              locationObj = new THREE.Object3D();
              locationObj.name = cellCode;
              locationObj.position.set(location.x, location.y, location.z);
              this.ThreeEngine.addObject(locationObj);
            }

            // 克隆pallet并放置到库位位置（使用pallet方法，它会自动应用材质）
            this.pallet(palletCode, locationObj);
            palletCount++;

            // 增量更新时减少日志输出
            if (!isIncremental) {
              console.log(`已放置托盘 ${palletCode} 到库位 ${cellCode}，位置:`, location);
            }
          } else {
            // 无托盘，移除托盘（如果存在）
            const existingPallet = this.ThreeEngine.getObjectByName(palletCode);
            if (existingPallet) {
              this.remove(palletCode);
              removedCount++;
              // 增量更新时减少日志输出
              if (!isIncremental) {
                console.log(`已移除库位 ${cellCode} 的托盘`);
              }
            }
          }
        });

        // 保存当前数据用于下次对比
        this.lastPalletData = {
          timestamp: Date.now(),
          data: allCellList.map(cell => ({
            code: cell.code,
            invenState: cell.invenState || 0
          }))
        };

        if (!isIncremental) {
          console.log(`托盘加载完成: 放置 ${palletCount} 个托盘，移除 ${removedCount} 个托盘`);
          this.$modal.msgSuccess(`托盘加载完成: 放置 ${palletCount} 个托盘，移除 ${removedCount} 个托盘`);
        } else if (palletCount > 0 || removedCount > 0) {
          console.log(`增量更新: 放置 ${palletCount} 个托盘，移除 ${removedCount} 个托盘`);
        }
      } catch (error) {
        console.error('加载托盘失败:', error);
        if (!isIncremental) {
          this.$modal?.msgError('加载托盘失败: ' + (error.message || '未知错误'));
        }
      } finally {
        this.isUpdatingPallets = false;
      }
    },

    // 查询四向车信息并定位
    // 四向车命名格式：四向车4080，其中4080是rcsCarInfo的code
    // isIncremental: 是否为增量更新（只更新变化的部分）
    async loadFourWayVehiclePositions(wareCode = 'sxc', isIncremental = false) {
      if (!this.ThreeEngine || !this.ThreeEngine.scene) {
        console.warn('场景未初始化，无法加载四向车位置');
        return;
      }

      // 防止重复请求
      if (this.isUpdatingVehicles) {
        if (!isIncremental) {
          console.log('四向车数据正在更新中，跳过本次请求');
        }
        return;
      }

      this.isUpdatingVehicles = true;

      try {
        // 分页查询所有四向车信息
        const pageSize = 100;
        let pageNum = 1;
        let total = 0;
        let allCarList = [];
        const maxPages = 1000;
        let currentPage = 0;

        console.log(`开始分页查询四向车信息，wareCode: ${wareCode}`);

        // 循环查询所有页的数据
        do {
          currentPage++;
          if (currentPage > maxPages) {
            console.warn(`查询页数超过最大限制${maxPages}页，停止查询`);
            break;
          }

          const response = await listRcsCarInfo({
            wareCode: wareCode,
            pageNum: pageNum,
            pageSize: pageSize
          });

          if (response.code !== 200) {
            console.error(`查询四向车信息失败（第${pageNum}页）:`, response.msg);
            break;
          }

          const currentPageRows = response.rows || [];
          allCarList = allCarList.concat(currentPageRows);
          total = response.total || 0;

          console.log(`已查询第${pageNum}页，本页${currentPageRows.length}条，累计${allCarList.length}/${total}条`);

          if (currentPageRows.length < pageSize || allCarList.length >= total) {
            if (!isIncremental) {
              console.log(`已查询完所有数据，共${allCarList.length}条`);
            }
            break;
          }

          pageNum++;
        } while (true);

        if (!isIncremental) {
          console.log(`四向车信息查询完成，共查询到 ${allCarList.length} 个四向车（总计${total}个），共查询${currentPage}页`);
        }

        // 如果是增量更新，对比数据变化，只更新变化的部分
        if (isIncremental && this.lastVehicleData) {
          const lastDataMap = new Map();
          this.lastVehicleData.data.forEach(item => {
            lastDataMap.set(item.code, item);
          });

          // 找出变化的四向车（位置、状态等发生变化）
          const changedCars = allCarList.filter(car => {
            const lastCar = lastDataMap.get(car.code);
            if (!lastCar) {
              return true; // 新出现的四向车
            }

            // 检查关键属性是否变化
            return (
              lastCar.fromCellCode !== car.fromCellCode ||
              lastCar.toCellCode !== car.toCellCode ||
              Math.abs((lastCar.positionRatio || 0) - (car.positionRatio || 0)) > 0.01 || // 位置变化超过1%
              lastCar.taskState !== (car.taskState !== undefined ? Number(car.taskState) : 0) ||
              lastCar.isConnected !== (car.isConnected !== undefined ? Number(car.isConnected) : 0) ||
              lastCar.batteryLevel !== (car.batteryLevel || 0) ||
              lastCar.speed !== (car.speed || 0) ||
              lastCar.loadState !== (car.loadState !== undefined ? Number(car.loadState) : 0) || // 载货状态变化需要更新托盘显示
              lastCar.isCharge !== car.isCharge
            );
          });

          // 找出消失的四向车（上次有，这次没有）
          const removedCars = [];
          lastDataMap.forEach((lastCar, code) => {
            if (!allCarList.find(car => car.code === code)) {
              removedCars.push(lastCar);
            }
          });

          // 只处理变化的四向车
          allCarList = [...changedCars];

          if (changedCars.length === 0 && removedCars.length === 0) {
            console.log('四向车数据无变化，跳过更新');
            this.isUpdatingVehicles = false;
            return;
          }

          console.log(`检测到 ${changedCars.length} 个变化的四向车，${removedCars.length} 个移除的四向车`);
        }

        // 定位四向车
        let positionedCount = 0;
        let failedCount = 0;
        let updatedCount = 0;

        allCarList.forEach((car) => {
          const carCode = car.code;
          if (!carCode) {
            console.warn('四向车数据缺少code字段:', car);
            failedCount++;
            return;
          }

          // 四向车对象名称格式：四向车4080
          const vehicleName = `四向车${carCode}`;

          // 查找四向车对象（可能是组对象，包含多个子对象）
          let vehicleGroup = this.ThreeEngine.getObjectByName(vehicleName);

          // 如果直接找不到，尝试查找所有以"四向车"开头且包含车号的对象
          // 四向车可能是由多个组件组成的组，需要找到根组对象
          if (!vehicleGroup) {
            let foundObject = null;
            this.ThreeEngine.scene.traverse((obj) => {
              if (obj.name && obj.name.includes(`四向车`) && obj.name.includes(carCode)) {
                foundObject = obj;
                // 向上查找父组对象，找到最顶层的组
                let parent = obj.parent;
                while (parent && parent !== this.ThreeEngine.scene) {
                  if (parent.name && parent.name.includes(`四向车`) && parent.name.includes(carCode)) {
                    foundObject = parent;
                  }
                  parent = parent.parent;
                }
              }
            });
            vehicleGroup = foundObject;
          }

          if (!vehicleGroup) {
            console.warn(`未找到四向车对象: ${vehicleName}`);
            failedCount++;
            return;
          }

          // ⚠️ 检查四向车是否正在跟随提升机移动
          if (vehicleGroup.userData && vehicleGroup.userData.isFollowingElevator) {
            // 如果正在跟随提升机移动，只更新X和Z坐标，Y坐标由跟随动画控制
            const position = this.calculateVehiclePosition(
              car.fromCellCode,
              car.toCellCode,
              car.positionRatio
            );

            if (position) {
              // 只更新X和Z坐标，保持Y坐标由跟随动画控制
              vehicleGroup.position.x = position.x;
              vehicleGroup.position.z = position.z;
              // Y坐标不更新，由跟随动画控制

              if (!isIncremental) {
                console.log(`四向车 ${carCode} 正在跟随提升机移动，只更新X和Z坐标`);
              }
              positionedCount++;
              updatedCount++;
              return; // 跳过后续的位置更新逻辑（forEach中使用return而不是continue）
            }
          }

          // 计算四向车位置
          const position = this.calculateVehiclePosition(
            car.fromCellCode,
            car.toCellCode,
            car.positionRatio
          );

          if (!position) {
            console.warn(`无法计算四向车 ${carCode} 的位置，fromCellCode: ${car.fromCellCode}, toCellCode: ${car.toCellCode}`);
            failedCount++;
            return;
          }

          // 更新四向车位置（如果有位置变化，使用动画）
          const currentPos = {
            x: vehicleGroup.position.x,
            y: vehicleGroup.position.y,
            z: vehicleGroup.position.z
          };
          const targetPos = {
            x: position.x,
            y: position.y,
            z: position.z
          };

          // 计算位置距离，判断是否需要动画
          const dx = targetPos.x - currentPos.x;
          const dy = targetPos.y - currentPos.y;
          const dz = targetPos.z - currentPos.z;
          const distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
          const needAnimation = distance > 0.01; // 如果距离超过0.01，使用动画

          if (needAnimation && isIncremental) {
            // 位置有变化，使用TWEEN动画平滑移动（1秒）
            // 停止之前的动画（如果有）
            if (vehicleGroup.tweenAnimation) {
              vehicleGroup.tweenAnimation.stop();
            }

            // 创建新的动画对象
            const tweenObj = { x: currentPos.x, y: currentPos.y, z: currentPos.z };

            // 创建新的动画
            // 动画设置为2秒
            vehicleGroup.tweenAnimation = new TWEEN.Tween(tweenObj)
              .to({ x: targetPos.x, y: targetPos.y, z: targetPos.z }, 2000) // 2秒动画
              .easing(TWEEN.Easing.Quadratic.InOut) // 缓动函数，平滑开始和结束
              .onUpdate(() => {
                vehicleGroup.position.set(tweenObj.x, tweenObj.y, tweenObj.z);
                // 标签是vehicleGroup的子对象，会自动跟随移动，但需要确保标签位置正确
                // 标签位置在createOrUpdateVehicleLabel中已经设置，这里不需要额外更新
              })
              .onComplete(() => {
                // 确保最终位置准确
                vehicleGroup.position.set(targetPos.x, targetPos.y, targetPos.z);
                vehicleGroup.tweenAnimation = null;
              })
              .start();
          } else {
            // 位置没有变化或首次加载，直接设置位置
            vehicleGroup.position.set(position.x, position.y, position.z);
          }

          // 如果四向车有子对象，也更新它们的位置（相对于父对象）
          vehicleGroup.traverse((child) => {
            if (child !== vehicleGroup && child.isObject3D) {
              // 保持子对象的相对位置不变，只更新组的位置
            }
          });

          // 创建或更新四向车信息标签
          this.createOrUpdateVehicleLabel(vehicleGroup, car, position);

          // 根据载货状态显示/隐藏托盘
          // 托盘命名格式：pallet4080（pallet + 四向车code）
          const palletName = `pallet${carCode}`;
          let palletObject = this.ThreeEngine.getObjectByName(palletName);

          // 如果直接找不到，尝试遍历场景查找
          if (!palletObject) {
            this.ThreeEngine.scene.traverse((obj) => {
              if (obj.name === palletName) {
                palletObject = obj;
              }
            });
          }

          if (palletObject) {
            // loadState: 0-空载, 1-负载
            const loadState = car.loadState !== undefined ? Number(car.loadState) : 0;
            const shouldShowPallet = loadState === 1; // 只有负载状态才显示托盘

            palletObject.visible = shouldShowPallet;

            // 如果托盘是四向车的子对象，也需要设置可见性
            if (palletObject.parent === vehicleGroup || vehicleGroup.children.includes(palletObject)) {
              palletObject.visible = shouldShowPallet;
            }

            if (!isIncremental) {
              console.log(`四向车 ${carCode} 托盘 ${palletName} ${shouldShowPallet ? '显示' : '隐藏'} (载货状态: ${loadState})`);
            }
          } else {
            // 托盘对象不存在，可能是模型中没有定义，不影响功能
            if (!isIncremental) {
              console.warn(`未找到四向车 ${carCode} 对应的托盘对象: ${palletName}`);
            }
          }

          positionedCount++;
          updatedCount++;

          // 增量更新时减少日志输出
          if (!isIncremental) {
            console.log(`已定位四向车 ${vehicleName} 到位置:`, position, `(进度: ${car.positionRatio ? (car.positionRatio * 100).toFixed(1) + '%' : '静止'})`);
          }
        });

        // 保存当前数据用于下次对比
        this.lastVehicleData = {
          timestamp: Date.now(),
          data: allCarList.map(car => ({
            code: car.code,
            fromCellCode: car.fromCellCode,
            toCellCode: car.toCellCode,
            positionRatio: car.positionRatio,
            loadState: car.loadState !== undefined ? Number(car.loadState) : 0, // 保存载货状态
            taskState: car.taskState !== undefined ? Number(car.taskState) : 0,
            isConnected: car.isConnected !== undefined ? Number(car.isConnected) : 0,
            batteryLevel: car.batteryLevel || 0,
            speed: car.speed || 0,
            loadState: car.loadState !== undefined ? Number(car.loadState) : -1,
            isCharge: car.isCharge
          }))
        };

        if (!isIncremental) {
          console.log(`四向车定位完成: 成功 ${positionedCount} 个，失败 ${failedCount} 个`);
          if (positionedCount > 0) {
            this.$modal.msgSuccess(`四向车定位完成: 成功 ${positionedCount} 个`);
          }
        } else if (updatedCount > 0) {
          console.log(`增量更新: ${updatedCount} 个四向车位置已更新`);
        }
      } catch (error) {
        console.error('加载四向车位置失败:', error);
        if (!isIncremental) {
          this.$modal?.msgError('加载四向车位置失败: ' + (error.message || '未知错误'));
        }
      } finally {
        this.isUpdatingVehicles = false;
      }
    },

    // 启动定时数据更新（每1秒更新一次）
    startDataUpdate() {
      // 如果定时器已存在，先清除
      if (this.dataUpdateTimer) {
        clearInterval(this.dataUpdateTimer);
      }

      console.log('启动定时数据更新，每1秒更新一次库位和四向车信息');

      // 每1秒更新一次
      this.dataUpdateTimer = setInterval(() => {
        this.updateDataIncremental();
      }, 1000);
    },

    // 停止定时数据更新
    stopDataUpdate() {
      if (this.dataUpdateTimer) {
        clearInterval(this.dataUpdateTimer);
        this.dataUpdateTimer = null;
        console.log('已停止定时数据更新');
      }
    },

    // 增量更新数据（只更新变化的部分，提升性能）
    async updateDataIncremental() {
      // 并行请求库位和四向车数据（使用增量更新模式）
      Promise.all([
        this.loadStoragePallets('sxc', true),
        this.loadFourWayVehiclePositions('sxc', true)
      ]).catch(error => {
        console.error('定时更新数据失败:', error);
      });
    },

    // 启动路径更新（每2秒更新一次）
    startPathUpdate() {
      // 如果定时器已存在，先清除
      if (this.pathUpdateTimer) {
        clearInterval(this.pathUpdateTimer);
      }

      console.log('启动路径更新，每2秒更新一次路径信息');

      // 立即执行一次
      this.loadCarPaths('sxc');

      // 每2秒更新一次
      this.pathUpdateTimer = setInterval(() => {
        this.loadCarPaths('sxc');
      }, 2000);
    },

    // 停止路径更新
    stopPathUpdate() {
      if (this.pathUpdateTimer) {
        clearInterval(this.pathUpdateTimer);
        this.pathUpdateTimer = null;
        console.log('已停止路径更新');
      }
    },

    // 加载并渲染车路径
    async loadCarPaths(wareCode = 'sxc') {
      if (!this.ThreeEngine || !this.ThreeEngine.scene) {
        console.warn('场景未初始化，无法加载路径');
        return;
      }

      // 防止重复请求
      if (this.isUpdatingPaths) {
        return;
      }

      this.isUpdatingPaths = true;

      try {
        // 查询路径数据（带分页参数）
        const response = await listRcsCarPath({
          wareCode: wareCode,
          pageNum: 1,
          pageSize: 999
        });

        if (response.code !== 200) {
          console.error('查询路径失败:', response.msg);
          this.isUpdatingPaths = false;
          return;
        }

        const pathList = response.rows || [];
        console.log(`查询到 ${pathList.length} 条路径数据`);

        // 按 rcsCarId 分组路径
        const pathGroups = {};
        pathList.forEach(path => {
          const carId = path.rcsCarId;
          if (!carId) {
            console.warn('路径数据缺少rcsCarId字段:', path);
            return;
          }

          if (!pathGroups[carId]) {
            pathGroups[carId] = [];
          }
          pathGroups[carId].push(path);
        });

        // 为每个 rcsCarId 渲染路径
        Object.keys(pathGroups).forEach(carId => {
          this.renderCarPath(carId, pathGroups[carId]);
        });

        // 移除已不存在的路径组
        Object.keys(this.carPathGroups).forEach(carId => {
          if (!pathGroups[carId]) {
            const group = this.carPathGroups[carId];
            if (group) {
              this.ThreeEngine.remove(group);
              delete this.carPathGroups[carId];
              console.log(`已移除路径组: ${carId}`);
            }
          }
        });

        // 需求一：检测路径终点，提前移动提升机
        this.syncElevatorWithPaths(pathGroups);

      } catch (error) {
        console.error('加载路径失败:', error);
      } finally {
        this.isUpdatingPaths = false;
      }
    },

    // 渲染单个车的路径管道
    renderCarPath(rcsCarId, pathList) {
      if (!pathList || pathList.length === 0) {
        return;
      }

      // 移除旧的路径组（如果存在）
      if (this.carPathGroups[rcsCarId]) {
        this.ThreeEngine.remove(this.carPathGroups[rcsCarId]);
      }

      // 创建新的路径组
      const pathGroup = new THREE.Group();
      pathGroup.name = `carPath_${rcsCarId}`;

      // 构建路径点序列
      const pathPoints = [];
      const processedPaths = new Set(); // 用于避免重复处理

      // 找到起始路径（fromCellCode 不在任何 path 的 toCellCode 中）
      let currentPath = pathList.find(path => {
        const fromCode = path.fromCellCode;
        return !pathList.some(p => p.toCellCode === fromCode);
      });

      // 如果找不到起始路径，使用第一条路径
      if (!currentPath) {
        currentPath = pathList[0];
      }

      // 按顺序构建路径点（去重，避免重复添加相同的点）
      let lastCellCode = null; // 记录上一个添加的库位编码，避免重复

      while (currentPath && !processedPaths.has(currentPath.id || currentPath.fromCellCode + currentPath.toCellCode)) {
        processedPaths.add(currentPath.id || currentPath.fromCellCode + currentPath.toCellCode);

        // 检查路径数据是否有效
        if (!currentPath.fromCellCode || !currentPath.toCellCode) {
          console.warn(`路径数据缺少 fromCellCode 或 toCellCode:`, currentPath);
          // 查找下一条路径
          currentPath = pathList.find(p =>
            p.fromCellCode === currentPath.toCellCode &&
            !processedPaths.has(p.id || p.fromCellCode + p.toCellCode)
          );
          continue;
        }

        // 获取起始位置（只有当起始点与上一个点不同时才添加）
        if (currentPath.fromCellCode !== lastCellCode) {
          const fromLocation = this.getStorageLocationByCode(currentPath.fromCellCode);
          if (fromLocation) {
            // 管道高度设置为库位高度上方0.01个单位（更贴近地面）
            pathPoints.push([fromLocation.x, fromLocation.y + 0.01, fromLocation.z]);
            lastCellCode = currentPath.fromCellCode;
          } else {
            console.warn(`未找到起始库位位置: ${currentPath.fromCellCode}`);
          }
        }

        // 获取结束位置
        const toLocation = this.getStorageLocationByCode(currentPath.toCellCode);
        if (toLocation) {
          // 管道高度设置为库位高度上方0.01个单位（更贴近地面）
          pathPoints.push([toLocation.x, toLocation.y + 0.01, toLocation.z]);
          lastCellCode = currentPath.toCellCode;
        } else {
          console.warn(`未找到结束库位位置: ${currentPath.toCellCode}`);
        }

        // 查找下一条路径（当前路径的 toCellCode 是下一条路径的 fromCellCode）
        currentPath = pathList.find(p =>
          p.fromCellCode === currentPath.toCellCode &&
          !processedPaths.has(p.id || p.fromCellCode + p.toCellCode)
        );
      }

      // 如果有路径点，创建管道
      if (pathPoints.length >= 2) {
        try {
          // 去重路径点（避免连续相同的点）
          const uniquePathPoints = [];
          for (let i = 0; i < pathPoints.length; i++) {
            const point = pathPoints[i];
            const prevPoint = uniquePathPoints[uniquePathPoints.length - 1];

            // 如果当前点与上一个点不同，才添加
            if (!prevPoint ||
                Math.abs(point[0] - prevPoint[0]) > 0.01 ||
                Math.abs(point[1] - prevPoint[1]) > 0.01 ||
                Math.abs(point[2] - prevPoint[2]) > 0.01) {
              uniquePathPoints.push(point);
            }
          }

          if (uniquePathPoints.length < 2) {
            console.warn(`路径组 ${rcsCarId} 去重后路径点不足，无法创建管道`);
            return;
          }

          const curve = this.createPath(uniquePathPoints);

          // 创建管道几何体
          // 参数说明：curve(路径曲线), segments(分段数，越大越平滑), radius(管道半径), radialSegments(径向分段数), closed(是否闭合)
          // 根据实际场景调整：
          // - segments: 根据路径长度动态计算，确保每段有足够的细分
          const pathLength = curve.getLength();
          const segments = Math.max(50, Math.min(200, Math.floor(pathLength / 2))); // 每2个单位一个分段，最少50，最多200
          // - radius: 0.05（管道半径，根据实际场景调整，模型整体较小，所以管道要更细）
          // - radialSegments: 6（圆形截面的分段数，减少到6以提升性能）
          const tubeGeometry = new THREE.TubeGeometry(curve, segments, 0.05, 6, false);

          // 创建流动纹理（使用Canvas生成，增强流动效果，添加光点粒子）
          const canvas = document.createElement('canvas');
          canvas.width = 1024; // 增加宽度，使纹理更精细，便于添加更多细节
          canvas.height = 256;
          const ctx = canvas.getContext('2d');

          // 绘制背景：科技感绿色系渐变
          const bgGradient = ctx.createLinearGradient(0, 0, canvas.width, 0);
          bgGradient.addColorStop(0, '#0A4A2A'); // 深绿色
          bgGradient.addColorStop(0.3, '#1DB954'); // 亮绿色
          bgGradient.addColorStop(0.5, '#00FF88'); // 荧光绿
          bgGradient.addColorStop(0.7, '#1DB954'); // 亮绿色
          bgGradient.addColorStop(1, '#0A4A2A'); // 深绿色
          ctx.fillStyle = bgGradient;
          ctx.fillRect(0, 0, canvas.width, canvas.height);

          // 添加流动光带（更明显的流动效果）
          const flowGradient = ctx.createLinearGradient(0, 0, canvas.width / 3, 0);
          flowGradient.addColorStop(0, 'rgba(0, 255, 136, 0)'); // 透明
          flowGradient.addColorStop(0.3, 'rgba(0, 255, 136, 0.6)'); // 荧光绿，中等亮度
          flowGradient.addColorStop(0.5, 'rgba(255, 255, 255, 1)'); // 白色高亮中心
          flowGradient.addColorStop(0.7, 'rgba(0, 255, 136, 0.6)'); // 荧光绿
          flowGradient.addColorStop(1, 'rgba(0, 255, 136, 0)'); // 透明

          // 绘制多条流动光带，增强流动感
          for (let i = 0; i < 4; i++) {
            const x = (canvas.width / 4) * i;
            ctx.fillStyle = flowGradient;
            ctx.fillRect(x, 0, canvas.width / 3, canvas.height);
          }

          // 添加流动条纹（更密集，增强流动感）
          ctx.strokeStyle = '#00FF88'; // 荧光绿条纹
          ctx.lineWidth = 2;
          for (let i = 0; i < canvas.width; i += 20) {
            ctx.beginPath();
            ctx.moveTo(i, 0);
            ctx.lineTo(i, canvas.height);
            ctx.stroke();
          }

          // 添加光点粒子（增强流动视觉效果，让流动更明显）
          // 大光点（主要流动粒子）
          ctx.fillStyle = '#FFFFFF';
          for (let i = 0; i < canvas.width; i += 40) {
            const y = canvas.height / 2 + (Math.sin(i * 0.1) * 20); // 让光点有轻微上下波动
            ctx.beginPath();
            ctx.arc(i, y, 4, 0, Math.PI * 2);
            ctx.fill();
            // 添加光点外圈发光
            const glowGradient = ctx.createRadialGradient(i, y, 0, i, y, 8);
            glowGradient.addColorStop(0, 'rgba(0, 255, 136, 0.8)');
            glowGradient.addColorStop(0.5, 'rgba(0, 255, 136, 0.4)');
            glowGradient.addColorStop(1, 'rgba(0, 255, 136, 0)');
            ctx.fillStyle = glowGradient;
            ctx.beginPath();
            ctx.arc(i, y, 8, 0, Math.PI * 2);
            ctx.fill();
            ctx.fillStyle = '#FFFFFF';
          }

          // 中等光点（次要流动粒子）
          ctx.fillStyle = '#00FF88';
          for (let i = 20; i < canvas.width; i += 30) {
            const y = canvas.height / 2 + (Math.cos(i * 0.15) * 15);
            ctx.beginPath();
            ctx.arc(i, y, 2.5, 0, Math.PI * 2);
            ctx.fill();
          }

          // 小光点（背景粒子，增强流动感）
          ctx.fillStyle = '#1DB954';
          for (let i = 10; i < canvas.width; i += 15) {
            const y = canvas.height / 2 + (Math.sin(i * 0.2) * 10);
            ctx.beginPath();
            ctx.arc(i, y, 1.5, 0, Math.PI * 2);
            ctx.fill();
          }

          // 添加流动箭头/方向指示（让流动方向更明显）
          ctx.strokeStyle = '#FFFFFF';
          ctx.fillStyle = '#FFFFFF';
          ctx.lineWidth = 2;
          for (let i = 0; i < canvas.width; i += 50) {
            const y = canvas.height / 2;
            // 绘制箭头
            ctx.beginPath();
            ctx.moveTo(i, y);
            ctx.lineTo(i + 8, y - 3);
            ctx.lineTo(i + 8, y - 1);
            ctx.lineTo(i + 12, y - 1);
            ctx.lineTo(i + 12, y + 1);
            ctx.lineTo(i + 8, y + 1);
            ctx.lineTo(i + 8, y + 3);
            ctx.closePath();
            ctx.fill();
          }

          const texture = new THREE.CanvasTexture(canvas);
          texture.wrapS = THREE.RepeatWrapping;
          texture.wrapT = THREE.RepeatWrapping;
          texture.repeat.set(15, 1); // 沿管道方向重复15次，使流动更明显
          texture.needsUpdate = true;

          // 存储纹理引用，用于动画更新
          texture.userData = {
            offset: 0,
            speed: 0.02 // 增加流动速度，使效果更明显
          };

          // 创建管道材质（科技感绿色系，带自发光和流动纹理）
          const tubeMaterial = new THREE.MeshPhongMaterial({
            map: texture, // 使用流动纹理
            color: new THREE.Color(0x1DB954), // 亮绿色
            emissive: new THREE.Color(0x0A4A2A), // 深绿色自发光
            transparent: true,
            opacity: 0.95,
            side: THREE.DoubleSide,
            shininess: 120,
            specular: new THREE.Color(0x00FF88) // 荧光绿高光
          });

          const tubeMesh = new THREE.Mesh(tubeGeometry, tubeMaterial);
          tubeMesh.name = `pathTube_${rcsCarId}`;
          // 存储纹理引用到mesh，方便后续更新
          tubeMesh.userData.flowTexture = texture;
          pathGroup.add(tubeMesh);

          // 不再添加edgeMesh，避免看起来像两条管道
          // 发光效果通过材质的emissive属性实现

          // 将路径组添加到场景
          this.ThreeEngine.addObject(pathGroup);
          this.carPathGroups[rcsCarId] = pathGroup;

          console.log(`已渲染路径组 ${rcsCarId}，包含 ${uniquePathPoints.length} 个路径点（原始${pathPoints.length}个），路径长度: ${pathLength.toFixed(2)}，分段数: ${segments}`);
        } catch (error) {
          console.error(`渲染路径组 ${rcsCarId} 失败:`, error, error.stack);
        }
      } else {
        console.warn(`路径组 ${rcsCarId} 路径点不足，无法创建管道`);
      }
    },

    // 根据库位编码和比例计算四向车位置
    // 参考CellMonitorView.vue中的calculatePositionByRatio方法
    calculateVehiclePosition(fromCellCode, toCellCode, positionRatio) {
      if (!fromCellCode) {
        return null;
      }

      // 检查起点是否是提升机位置
      const isFromElevatorPosition = this.elevatorConfig.positions.includes(fromCellCode);
      const isToElevatorPosition = toCellCode && this.elevatorConfig.positions.includes(toCellCode);

      // 获取起点库位位置
      const fromLocation = this.getStorageLocationByCode(fromCellCode);
      if (!fromLocation) {
        console.warn(`未找到起点库位位置: ${fromCellCode}`);
        return null;
      }

      // 如果没有终点或比例，四向车静止在起点
      if (!toCellCode || positionRatio === null || positionRatio === undefined) {
        // 四向车Y坐标 = 库位Y坐标 + 四向车高度偏移量
        // 注意：如果四向车在提升机上，它的Y坐标应该跟随提升机，但这里只计算静止位置
        // 提升机移动时的跟随逻辑在syncVehiclesWithElevator中处理
        let yPosition = fromLocation.y + this.vehicleHeightOffset;

        return {
          x: fromLocation.x,
          y: yPosition,
          z: fromLocation.z
        };
      }

      // 获取终点库位位置
      const toLocation = this.getStorageLocationByCode(toCellCode);
      if (!toLocation) {
        console.warn(`未找到终点库位位置: ${toCellCode}`);
        // 如果找不到终点，返回起点位置
        let yPosition = fromLocation.y + this.vehicleHeightOffset;

        return {
          x: fromLocation.x,
          y: yPosition,
          z: fromLocation.z
        };
      }

      // 线性插值计算四向车位置
      // positionRatio: 0表示在起点，1表示在终点
      const ratio = Math.max(0, Math.min(1, positionRatio)); // 限制在0-1之间

      const vehicleX = fromLocation.x + (toLocation.x - fromLocation.x) * ratio;
      const vehicleZ = fromLocation.z + (toLocation.z - fromLocation.z) * ratio;

      // Y坐标计算：四向车始终在库位Y坐标 + vehicleHeightOffset的位置
      // 如果起点或终点是提升机位置，库位Y坐标会跟随提升机所在的层
      // 提升机移动时的跟随逻辑在syncVehiclesWithElevator中处理
      const vehicleY = fromLocation.y + (toLocation.y - fromLocation.y) * ratio + this.vehicleHeightOffset;

      return {
        x: vehicleX,
        y: vehicleY,
        z: vehicleZ
      };
    },

    // ========== 提升机同步相关方法 ==========

    // 获取提升机的当前Y坐标
    getElevatorYPosition() {
      const elevatorObject = this.ThreeEngine.getObjectByName(this.elevatorConfig.elevatorName);
      if (elevatorObject) {
        return elevatorObject.position.y;
      }
      // 如果提升机对象不存在，根据当前层数计算Y坐标
      if (this.elevatorState.currentLayer != null) {
        const cellCode = this.elevatorConfig.positions[this.elevatorState.currentLayer - 1];
        const location = this.getStorageLocationByCode(cellCode);
        if (location) {
          return location.y + this.elevatorConfig.heightOffset;
        }
      }
      return null;
    },

    // 检测路径终点是否是提升机位置
    checkPathDestination(pathList) {
      if (!pathList || pathList.length === 0) {
        return null;
      }

      // 获取路径的最后一条（最终目的地）
      const lastPath = pathList[pathList.length - 1];
      const destinationCellCode = lastPath.toCellCode;

      if (!destinationCellCode) {
        return null;
      }

      // 检查是否是提升机位置（只有一个提升机）
      if (this.elevatorConfig.positions.includes(destinationCellCode)) {
        return {
          cellCode: destinationCellCode,
          layer: this.elevatorConfig.layerMap[destinationCellCode]
        };
      }

      return null;
    },

    // 计算提升机位置（基于库位空物体）
    calculateElevatorPosition(targetCellCode) {
      // 获取库位空物体的位置
      const cellLocation = this.getStorageLocationByCode(targetCellCode);
      if (!cellLocation) {
        console.warn(`未找到库位位置: ${targetCellCode}`);
        return null;
      }

      // 计算提升机位置（在四向车下方）
      const elevatorPosition = {
        x: cellLocation.x,  // X坐标与库位相同
        y: cellLocation.y + this.elevatorConfig.heightOffset,  // Y坐标 = 库位Y + 偏移量（负数）
        z: cellLocation.z   // Z坐标与库位相同
      };

      return elevatorPosition;
    },

    // 移动提升机到目标位置
    moveElevatorToPosition(targetPosition, targetCellCode) {
      // 获取提升机对象
      const elevatorObject = this.ThreeEngine.getObjectByName(this.elevatorConfig.elevatorName);
      if (!elevatorObject) {
        console.warn(`未找到提升机对象: ${this.elevatorConfig.elevatorName}`);
        return;
      }

      // 如果正在移动，停止当前动画
      if (this.elevatorState.isMoving && elevatorObject.tweenAnimation) {
        elevatorObject.tweenAnimation.stop();
      }

      this.elevatorState.isMoving = true;
      this.elevatorState.targetPosition = targetPosition;
      this.elevatorState.targetCellCode = targetCellCode;

      // 获取当前位置
      const currentPos = {
        x: elevatorObject.position.x,
        y: elevatorObject.position.y,
        z: elevatorObject.position.z
      };

      // 创建移动动画（与四向车动画时间一致：2秒）
      const tweenObj = { ...currentPos };
      elevatorObject.tweenAnimation = new TWEEN.Tween(tweenObj)
        .to(targetPosition, 2000) // 2秒动画
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onUpdate(() => {
          elevatorObject.position.set(tweenObj.x, tweenObj.y, tweenObj.z);
        })
        .onComplete(() => {
          elevatorObject.position.set(targetPosition.x, targetPosition.y, targetPosition.z);
          this.elevatorState.currentPosition = { ...targetPosition };
          this.elevatorState.currentLayer = this.elevatorConfig.layerMap[targetCellCode];
          this.elevatorState.isMoving = false;
          this.elevatorState.targetLayer = null;
          elevatorObject.tweenAnimation = null;
          console.log(`提升机已移动到 ${targetCellCode} (第${this.elevatorState.currentLayer}层)`);
        })
        .start();
    },

    // 需求一：同步提升机与路径终点
    syncElevatorWithPaths(pathGroups) {
      // 遍历所有路径组，找出终点是提升机位置的
      let targetDestination = null; // 记录目标位置（如果有多个四向车，使用最后一个）

      Object.keys(pathGroups).forEach(rcsCarId => {
        const pathList = pathGroups[rcsCarId];
        const destination = this.checkPathDestination(pathList);

        if (destination) {
          // 记录目标（如果有多个，使用最后一个）
          targetDestination = {
            cellCode: destination.cellCode,
            layer: destination.layer,
            rcsCarId: rcsCarId
          };
        }
      });

      // 如果找到目标位置，移动提升机
      if (targetDestination) {
        const state = this.elevatorState;

        // 如果目标层与当前层不同，或者还未初始化
        if (state.currentLayer !== targetDestination.layer || state.currentLayer === null) {
          // 计算提升机目标位置
          const targetPosition = this.calculateElevatorPosition(targetDestination.cellCode);

          if (targetPosition) {
            this.moveElevatorToPosition(targetPosition, targetDestination.cellCode);
          }
        } else {
          // 已经在目标层，无需移动
          console.log(`提升机已在第 ${targetDestination.layer} 层，无需移动`);
        }
      }
    },

    // 检测四向车是否在提升机位置
    findVehiclesAtElevator(cellCode) {
      const vehiclesAtElevator = [];

      // 遍历所有四向车，检查是否在提升机位置
      // 需要从 lastVehicleData 或当前四向车数据中获取
      if (this.lastVehicleData && this.lastVehicleData.data) {
        this.lastVehicleData.data.forEach(car => {
          // 检查四向车是否在提升机位置
          // 情况1：静止在提升机位置（fromCellCode 是提升机位置，toCellCode 为空或相同）
          if (car.fromCellCode === cellCode && (!car.toCellCode || car.toCellCode === cellCode)) {
            const vehicleGroup = this.ThreeEngine.getObjectByName(`四向车${car.code}`);
            if (vehicleGroup) {
              vehiclesAtElevator.push({
                code: car.code,
                vehicleGroup: vehicleGroup
              });
            }
          }
          // 情况2：正在移动到提升机位置（toCellCode 是提升机位置，positionRatio 接近 1）
          else if (car.toCellCode === cellCode && car.positionRatio > 0.9) {
            const vehicleGroup = this.ThreeEngine.getObjectByName(`四向车${car.code}`);
            if (vehicleGroup) {
              vehiclesAtElevator.push({
                code: car.code,
                vehicleGroup: vehicleGroup
              });
            }
          }
        });
      }

      return vehiclesAtElevator;
    },

    // 需求二：四向车跟随提升机移动
    syncVehiclesWithElevator(elevatorStatus) {
      const state = this.elevatorState;

      // 如果提升机位置发生变化
      if (state.currentLayer !== elevatorStatus.currentLayer) {
        const oldLayer = state.currentLayer;
        const newLayer = elevatorStatus.currentLayer;
        const newCellCode = elevatorStatus.cellCode;

        // 获取新位置的库位坐标
        const newLocation = this.getStorageLocationByCode(newCellCode);
        if (!newLocation) {
          console.warn(`未找到库位位置: ${newCellCode}`);
          return;
        }

        // 计算提升机新位置
        const newElevatorPosition = {
          x: newLocation.x,
          y: newLocation.y + this.elevatorConfig.heightOffset,
          z: newLocation.z
        };

        // 移动提升机
        this.moveElevatorToPosition(newElevatorPosition, newCellCode);

        // 查找在提升机位置的四向车（检查旧位置）
        const oldCellCode = oldLayer ? this.elevatorConfig.positions[oldLayer - 1] : null;
        if (oldCellCode) {
          const vehiclesAtElevator = this.findVehiclesAtElevator(oldCellCode);

          // 让四向车跟随提升机移动
          vehiclesAtElevator.forEach(vehicle => {
            if (vehicle.vehicleGroup) {
              const currentPos = vehicle.vehicleGroup.position;
              const targetY = newLocation.y + this.vehicleHeightOffset; // 四向车位置

              // 停止之前的动画（如果有）
              if (vehicle.vehicleGroup.tweenAnimation) {
                vehicle.vehicleGroup.tweenAnimation.stop();
              }

              // ⚠️ 标记四向车正在跟随提升机移动（重要！）
              if (!vehicle.vehicleGroup.userData) {
                vehicle.vehicleGroup.userData = {};
              }
              vehicle.vehicleGroup.userData.isFollowingElevator = true;
              vehicle.vehicleGroup.userData.followingElevatorTargetY = targetY;

              // 创建跟随动画（与提升机动画同步）
              const tweenObj = { y: currentPos.y };
              vehicle.vehicleGroup.tweenAnimation = new TWEEN.Tween(tweenObj)
                .to({ y: targetY }, 2000) // 与提升机动画时间一致
                .easing(TWEEN.Easing.Quadratic.InOut)
                .onUpdate(() => {
                  vehicle.vehicleGroup.position.y = tweenObj.y;
                  // 标签是vehicleGroup的子对象，会自动跟随移动
                })
                .onComplete(() => {
                  vehicle.vehicleGroup.position.y = targetY;
                  vehicle.vehicleGroup.tweenAnimation = null;
                  // ⚠️ 清除跟随标记（重要！）
                  vehicle.vehicleGroup.userData.isFollowingElevator = false;
                  vehicle.vehicleGroup.userData.followingElevatorTargetY = null;
                  console.log(`四向车 ${vehicle.code} 已跟随提升机移动到第 ${newLayer} 层`);
                })
                .start();
            }
          });
        }

        // 更新状态
        state.currentLayer = newLayer;
      }
    },

    
 

   

    // 更新提升机状态并同步四向车
    

    // 根据库位编码获取位置（库位编码格式：z-x-y）
    getStorageLocationByCode(cellCode) {
      // 检查 cellCode 是否有效
      if (!cellCode || typeof cellCode !== 'string') {
        console.warn(`无效的库位编码: ${cellCode}`);
        return null;
      }

      // 首先尝试从计算的库位位置中获取
      const match = cellCode.match(/^(\d+)-(\d+)-(\d+)$/);
      if (match) {
        const layer = parseInt(match[1]);
        const x = parseInt(match[2]);
        const y = parseInt(match[3]);

        const calculatedLocation = this.getStorageLocation(layer, x, y);
        if (calculatedLocation) {
          return calculatedLocation;
        }
      }

      // 如果计算的位置不存在，尝试从场景中查找该库位编码的空物体
      // 某些特殊位置（如1-8-6）直接在模型中存在，不需要计算
      const locationObj = this.ThreeEngine.getObjectByName(cellCode);
      if (locationObj && locationObj.position) {
        console.log(`从场景中获取特殊库位位置: ${cellCode}`, locationObj.position);
        return {
          x: locationObj.position.x,
          y: locationObj.position.y,
          z: locationObj.position.z
        };
      }

      console.warn(`未找到库位位置: ${cellCode}（既不在计算位置中，也不在场景中）`);
      return null;
    },

    // 创建或更新四向车信息标签
    createOrUpdateVehicleLabel(vehicleGroup, carData, position) {
      const carCode = carData.code;
      const labelName = `vehicleLabel_${carCode}`;

      // 查找是否已存在标签
      let labelGroup = vehicleGroup.getObjectByName(labelName);

      // 获取任务状态标签
      const taskState = carData.taskState !== undefined ? Number(carData.taskState) : 0;
      const taskStateLabel = this.getTaskStateLabel(taskState);

      // 获取在线状态（用于设置状态指示器颜色，不显示文字）
      const isConnected = carData.isConnected !== undefined ? Number(carData.isConnected) : 0;
      const onlineColor = isConnected === 1 ? '#48bb78' : '#f56565'; // 在线=绿色，离线=红色

      // 获取位置信息
      const fromCellCode = carData.fromCellCode || '-';
      const toCellCode = carData.toCellCode || '-';
      const positionText = fromCellCode === toCellCode ? fromCellCode : `${fromCellCode} → ${toCellCode}`;

      // 获取速度
      const speed = carData.speed || 0;

      // 获取电池电量
      const batteryLevel = carData.batteryLevel || 0;
      const isCharging = this.isVehicleCharging(carData);
      const batteryText = `${batteryLevel}%${isCharging ? ' ⚡' : ''}`;
      const batteryColor = this.getBatteryColor(batteryLevel, isCharging);

      // 获取负载状态
      const loadState = carData.loadState !== undefined ? Number(carData.loadState) : -1;
      const loadText = loadState === 1 ? '有货' : loadState === 0 ? '空载' : '-';
      const loadColor = loadState === 1 ? '#E6A23C' : '#67C23A';

      // 获取任务状态颜色
      const taskStateColor = this.getTaskStateColor(taskState);

      // 创建标签配置（优化布局，合理分配2行信息，减少宽度）
      // 在线状态通过右上角状态指示器颜色表示，不显示文字
      // 提高文字对比度，确保在透明背景上清晰可见
      const labelConfig = {
        global: {
          deviceName: `四向车${carCode}`,
          totalWidth: 480,  // 减少宽度，信息更紧凑
          headerHeight: 70,  // 标题高度
          headerFontSize: 30, // 标题字体
          headerTextColor: '#FFFFFF', // 标题使用纯白色，对比度最高
          backgroundColor: 'rgba(0, 0, 0, 0.6)',  // 降低不透明度，提高透明度，减少遮挡
          status: isConnected === 1 ? (taskState === 2 ? 'active' : taskState === 1 ? 'warning' : 'idle') : 'error', // 离线时显示error状态（红色）
          statusColor: onlineColor, // 自定义状态指示器颜色（在线=绿色，离线=红色）
          scaleRatio: 0.025, // 整体缩放比例
          arrowColor: 0x00D4FF
        },
        rows: [
          {
            rowHeight: 50,  // 第一行：状态和速度电量信息
            columns: [
              {
                content: taskStateLabel,
                widthRatio: 2.5,  // 任务状态
                textStyle: {
                  color: this.getHighContrastColor(taskStateColor), // 使用高对比度颜色
                  fontSize: 20,
                  fontWeight: 'bold',
                  align: 'left'
                }
              },
              {
                content: '|',
                widthRatio: 0.5,  // 分隔符
                textStyle: {
                  color: 'rgba(255, 255, 255, 0.6)', // 提高分隔符对比度
                  fontSize: 18,
                  align: 'center'
                }
              },
              {
                content: `速度: ${speed} m/s`,
                widthRatio: 2.5,  // 速度
                textStyle: {
                  color: '#FFFFFF', // 纯白色，提高对比度
                  fontSize: 20,
                  fontWeight: 'bold', // 加粗提高可读性
                  align: 'left'
                }
              },
              {
                content: '|',
                widthRatio: 0.5,  // 分隔符
                textStyle: {
                  color: 'rgba(255, 255, 255, 0.6)', // 提高分隔符对比度
                  fontSize: 18,
                  align: 'center'
                }
              },
              {
                content: `电量: ${batteryText}`,
                widthRatio: 4.5,  // 电量（占用更多空间）
                textStyle: {
                  color: this.getHighContrastColor(batteryColor), // 使用高对比度颜色
                  fontSize: 20,
                  fontWeight: 'bold',
                  align: 'left'
                }
              }
            ]
          },
          {
            rowHeight: 50,  // 第二行：位置和负载信息
            backgroundColor: 'rgba(79, 172, 254, 0.1)',  // 稍微提高行背景透明度，增强对比
            columns: [
              {
                content: `📍 位置: ${positionText}`,
                widthRatio: 6,  // 位置信息（占用更多空间，因为可能较长）
                textStyle: {
                  color: '#FFFFFF', // 纯白色，提高对比度
                  fontSize: 21,
                  fontWeight: 'bold', // 加粗提高可读性
                  align: 'left'
                }
              },
              {
                content: '|',
                widthRatio: 0.5,  // 分隔符
                textStyle: {
                  color: 'rgba(255, 255, 255, 0.6)', // 提高分隔符对比度
                  fontSize: 18,
                  align: 'center'
                }
              },
              {
                content: `负载: ${loadText}`,
                widthRatio: 3.5,  // 负载
                textStyle: {
                  color: this.getHighContrastColor(loadColor), // 使用高对比度颜色
                  fontSize: 20,
                  fontWeight: 'bold', // 加粗提高可读性
                  align: 'left'
                }
              }
            ]
          }
        ]
      };

      if (labelGroup) {
        // 删除旧标签，重新创建以确保内容更新
        vehicleGroup.remove(labelGroup);
        // 清理旧标签的资源
        if (labelGroup.children) {
          labelGroup.children.forEach(child => {
            if (child.material) {
              if (child.material.map) {
                child.material.map.dispose();
              }
              child.material.dispose();
            }
            if (child.geometry) {
              child.geometry.dispose();
            }
          });
        }
      }

      // 创建新标签
      labelGroup = createSpriteTextLabel(labelConfig);
      labelGroup.name = labelName;
      labelGroup.position.set(0, 5, 0); // 在四向车上方5个单位

      // 设置标签材质透明度，进一步减少遮挡
      labelGroup.traverse((child) => {
        if (child.material) {
          child.material.opacity = 0.75;  // 设置材质透明度
          child.material.transparent = true;
        }
      });

      // 将标签添加到四向车组中，使其跟随四向车移动
      vehicleGroup.add(labelGroup);
    },

    // 获取任务状态标签
    getTaskStateLabel(state) {
      const labels = {
        0: '空闲',
        1: '任务中',
        2: '执行中'
      };
      return labels[state] || '未知';
    },

    // 获取任务状态颜色
    getTaskStateColor(state) {
      const colors = {
        0: '#a0aec0',  // 空闲 - 灰色
        1: '#f6ad55',  // 任务中 - 橙色
        2: '#4ecdc4'   // 执行中 - 青色
      };
      return colors[state] || '#a0aec0';
    },

    // 判断四向车是否正在充电
    isVehicleCharging(carData) {
      if (!carData || carData.isCharge === undefined || carData.isCharge === null) {
        return false;
      }
      return carData.isCharge === '1' || carData.isCharge === 1 || carData.isCharge === true;
    },

    // 获取电池颜色
    getBatteryColor(level, isCharging) {
      if (isCharging) {
        return '#4ecdc4'; // 充电中 - 青色
      } else if (level <= 20) {
        return '#f56565'; // 低电量 - 红色
      } else if (level <= 50) {
        return '#f6ad55'; // 中等电量 - 橙色
      } else {
        return '#48bb78'; // 充足电量 - 绿色
      }
    },

    // 获取高对比度颜色（在透明背景上更清晰）
    getHighContrastColor(originalColor) {
      // 将颜色转换为更亮的版本，提高对比度
      const colorMap = {
        // 任务状态颜色
        '#a0aec0': '#E8F4F8', // 空闲 - 从灰色改为亮灰白色
        '#f6ad55': '#FFD700', // 任务中 - 从橙色改为亮金色
        '#4ecdc4': '#00FFFF', // 执行中 - 从青色改为亮青色

        // 电池颜色
        '#4ecdc4': '#00FFFF', // 充电中 - 亮青色
        '#f56565': '#FF4444', // 低电量 - 亮红色
        '#f6ad55': '#FFB84D', // 中等电量 - 亮橙色
        '#48bb78': '#66FF99', // 充足电量 - 亮绿色

        // 负载颜色
        '#E6A23C': '#FFB84D', // 有货 - 亮橙色
        '#67C23A': '#7FFF7F', // 空载 - 亮绿色
      };

      // 如果颜色在映射表中，返回高对比度版本
      if (colorMap[originalColor]) {
        return colorMap[originalColor];
      }

      // 否则，尝试将颜色变亮
      // 简单的亮度提升：如果是深色，返回更亮的版本
      if (originalColor.startsWith('#')) {
        // 提取RGB值
        const r = parseInt(originalColor.slice(1, 3), 16);
        const g = parseInt(originalColor.slice(3, 5), 16);
        const b = parseInt(originalColor.slice(5, 7), 16);

        // 计算亮度
        const brightness = (r * 299 + g * 587 + b * 114) / 1000;

        // 如果颜色较暗，提高亮度
        if (brightness < 150) {
          const factor = 1.5; // 亮度提升因子
          const newR = Math.min(255, Math.round(r * factor));
          const newG = Math.min(255, Math.round(g * factor));
          const newB = Math.min(255, Math.round(b * factor));
          return `#${newR.toString(16).padStart(2, '0')}${newG.toString(16).padStart(2, '0')}${newB.toString(16).padStart(2, '0')}`;
        }
      }

      // 如果无法处理，返回原色或白色
      return originalColor || '#FFFFFF';
    },

    // 设置摄像头到指定楼层的观测点
    setCameraToFloor(floorNumber) {
      if (!this.ThreeEngine || !this.ThreeEngine.camera) {
        console.warn('摄像头未初始化');
        return;
      }

      // 根据楼层设置不同的摄像头位置
      const positions = {
        1: { x: -700, y: -70, z: -150 },
        2: { x: -700, y: 20, z: -150 },
        3: { x: -700, y: 100, z: -150 },
        4: { x: -700, y: 150, z: -150 }
      };

      const lookAtPositions = {
        1: { x: 0, y: -50, z: 0 },
        2: { x: 0, y: -50, z: 0 },
        3: { x: 0, y: 100, z: 0 },
        4: { x: 0, y: 150, z: 0 }
      };

      const pos = positions[floorNumber];
      const lookAt = lookAtPositions[floorNumber];

      if (pos && lookAt) {
        // 平滑过渡到新视角
        const positionTween = new TWEEN.Tween(this.ThreeEngine.camera.position)
          .to(pos, 1000)
          .easing(TWEEN.Easing.Quadratic.InOut);

        // 在位置动画完成后设置看向目标点
        positionTween.onComplete(() => {
          this.ThreeEngine.camera.lookAt(lookAt.x, lookAt.y, lookAt.z);
        });

        positionTween.start();
      //

        console.log(`摄像头已设置到楼层${floorNumber}的观测点`);
      } else {
        console.warn(`无效的楼层号: ${floorNumber}`);
      }
    },

    // 重置摄像头视角
    resetCamera() {
      var that = this;
      if (!that.ThreeEngine || !that.ThreeEngine.camera) {
        console.warn('摄像头未初始化');
        return;
      }

      // 重置到初始视角
      const positionTween = new TWEEN.Tween(this.ThreeEngine.camera.position)
        .to({ x: -700, y: 0, z: 0 }, 1000)
        .easing(TWEEN.Easing.Quadratic.InOut);

      // 在位置动画完成后设置看向目标点
      positionTween.onComplete(() => {
        this.ThreeEngine.camera.lookAt(0, 0, 0);
      });

      positionTween.start();

      console.log('摄像头视角已重置');
    },

    init() {
      var that = this;
      var dom = document.getElementById("3d");
      this.ThreeEngine = new ThreeEngine(dom);

      // 初始化时钟
      this.clock = new THREE.Clock();

      // 初始化第一人称控制器（无人机）
      // 使用容器DOM而不是canvas，确保鼠标事件正确触发
      this.firstPersonController = new FirstPersonController(
        this.ThreeEngine.camera,
        this.ThreeEngine.scene,
        dom,  // 使用整个容器div
        (locked) => {
          // 锁定状态变化回调，同步更新Vue的响应式状态
          this.isMouseLocked = locked;
          console.log('🔄 Vue状态已同步, isMouseLocked =', locked);
        }
      );

      // 不在初始化时改变相机位置，保持原始位置 (-800, 100, 500)
      // 进入无人机模式时才设置位置

      console.log('🚁 无人机控制器已初始化, DOM元素:', dom);

      // 初始化炮弹系统
      this.projectileSystem = new ProjectileSystem(
        this.ThreeEngine.scene,
        this.ThreeEngine.camera
      );

      console.log('🎯 炮弹系统已初始化');




      // 添加基础模型
      this.ThreeEngine.addObjects(...allBaseObject);
      // 添加灯光
      this.ThreeEngine.addObjects(...allHelper);
      this.ThreeEngine.addObjects(...allLights);

      const loader = new GLTFLoader();
      //
      const dracoLoader = new DRACOLoader();
      dracoLoader.setDecoderPath('/examples/jsm/libs/draco/');
      loader.setDRACOLoader(dracoLoader);
      let mixer = null; //声明一个播放器变量
      loader.load("/glb/seagate.glb", function (glb) {

        //blander中灯光强度与three.js不同，这个需要做一个计算
        glb.scene.traverse((object) => {
            if (object.isLight) {
              // 禁用模型中的所有光源，使用我们自定义的灯光设置
              object.visible = false;
              console.log('已禁用模型中的光源:', object);
            }
            if(object.userData.aaaaa=="bbbbb"){
              console.log('ccccccccccccc');
            }
        });
        that.ThreeEngine.addObject(glb.scene);
        that.addStorageMachineLabels();

        // 使用原本的摄像机参数设置旋转中心
        // 原本摄像机位置：(-15, 12, 12)，lookAt：(-10, 0, 0)
        // 场景中心使用原本的lookAt点
        that.sceneCenter = { x: 0, y: 0, z: 0 };

        // 根据原本的摄像机位置计算水平距离
        // 从 (-10, 0, 0) 到 (-15, 10, 20) 的水平距离
        const originalCameraPos = { x: -8, y: 10, z: 10 };
        const dx = originalCameraPos.x - that.sceneCenter.x; // -5
        const dz = originalCameraPos.z - that.sceneCenter.z; // 20
        that.cameraRadius = Math.sqrt(dx * dx + dz * dz); // sqrt(25 + 400) = sqrt(425) ≈ 20.6
        that.cameraHeight = originalCameraPos.y; // 10

        // 计算初始旋转角度（从原本的摄像机位置开始）
        // atan2(z, x) 计算角度，注意x是负数
        that.cameraRotationAngle = Math.atan2(dz, dx); // atan2(20, -5) ≈ 1.82弧度

        console.log('场景中心:', that.sceneCenter, '摄像机水平距离:', that.cameraRadius, '初始角度:', that.cameraRotationAngle);
        // 添加自定义地板材质
        that.applyCustomFloorMaterials();
        // 添加堆垛机躯干材质
        that.applyStackerBodyMaterials();
        // 添加货架材质
        that.applyShelfMaterials();
        // 添加墙体材质
        that.applyWallMaterials();
        // 添加载货台材质
        that.applyPlatformMaterials();
        // 添加上货叉材质
        that.applyUpperForkMaterials();
        // 添加下货叉材质
        that.applyLowerForkMaterials();
        // 添加RGV材质
        that.applyRgvMaterials();
        // 添加输送线框架材质
        that.applyConveyorMaterials();
        // 添加托盘材质
        that.applyPalletMaterials();
        // 添加立柱材质
        that.applyColumnMaterials();
        // 添加横梁-轨道材质（注意："横梁-轨道"是一个完整的对象名称）
        that.applyBeamRailMaterials();
        // 添加四向车材质
        that.applyFourWayVehicleMaterials();
        // 计算库位位置
        that.calculateStorageLocations();
        // 创建每层的亚克力板
        that.createLayerAcrylicPlates();
        // 初始加载库位和四向车数据
       // that.loadStoragePallets('sxc');
        that.loadFourWayVehiclePositions('sxc');

        // 启动定时更新（每1秒更新一次）
     //   that.startDataUpdate();

        // 启动路径更新（每2秒更新一次）
      //  that.startPathUpdate();

        // 启动提升机状态更新（每2秒更新一次）
      

        // 统计模型信息
        that.getModelStatistics();

      }
      , function (xhr) {
            const percent = xhr.loaded / xhr.total;
            that.jindu=Math.round(100*percent) +"%";
          //   console.log('加载进度' + percent);
             if(percent>=1){
              setTimeout(function() {
                that.jiinduDisable=false;
              }, 300);

             }
            }

    );

      // 创建一个时钟对象Clock
      const clock = new THREE.Clock();

      function render() {
          TWEEN.update();

          const delta = that.clock.getDelta();

          // 更新路径管道的流动效果
          if (that.ThreeEngine && that.ThreeEngine.scene) {
            that.ThreeEngine.scene.traverse((obj) => {
              if (obj.name && obj.name.startsWith('carPath_')) {
                obj.traverse((child) => {
                  if (child.userData && child.userData.flowTexture) {
                    const texture = child.userData.flowTexture;
                    // 反向流动（负值），使流动方向正确
                    texture.userData.offset -= texture.userData.speed;
                    // 确保offset在合理范围内，避免数值过大
                    if (texture.userData.offset < -1) {
                      texture.userData.offset += 1;
                    }
                    texture.offset.x = texture.userData.offset;
                  }
                });
              }
            });
          }

          // 摄像机缓慢旋转（围绕场景中心）
          if (that.autoRotateEnabled && that.ThreeEngine && that.ThreeEngine.camera) {
            // 禁用 OrbitControls 的自动更新，避免干扰旋转逻辑
            if (that.ThreeEngine.controls) {
              that.ThreeEngine.controls.enabled = false;
            }

            // 更新旋转角度
            that.cameraRotationAngle += (that.rotationSpeed * delta * Math.PI) / 180; // 转换为弧度

            // 确保 cameraRadius 有效（防止未初始化）
            if (!that.cameraRadius || that.cameraRadius <= 0) {
              console.warn('cameraRadius 未正确初始化，使用默认值');
              that.cameraRadius = 20.6; // 根据用户修改的参数计算：sqrt(5² + 20²) ≈ 20.6
            }

            // 计算摄像机新位置（围绕场景中心旋转）
            const x = that.sceneCenter.x + that.cameraRadius * Math.sin(that.cameraRotationAngle);
            const z = that.sceneCenter.z + that.cameraRadius * Math.cos(that.cameraRotationAngle);
            const y = that.cameraHeight;

            // 更新摄像机位置
            that.ThreeEngine.camera.position.set(x, y, z);

            // 让摄像机始终看向场景中心
            that.ThreeEngine.camera.lookAt(that.sceneCenter.x, that.sceneCenter.y, that.sceneCenter.z);

            // 更新摄像机的投影矩阵
            that.ThreeEngine.camera.updateProjectionMatrix();
          } else {
            // 如果自动旋转被禁用，重新启用 OrbitControls
            if (that.ThreeEngine && that.ThreeEngine.controls) {
              that.ThreeEngine.controls.enabled = true;
            }
          }

          that.requestId = requestAnimationFrame(render);

          // 更新第一人称控制器
          if (that.firstPersonController && that.isFirstPersonMode) {
            that.firstPersonController.update(delta);
          }

          // 更新炮弹系统
          if (that.projectileSystem) {
            that.projectileSystem.update(delta);
          }

          if (mixer !== null) {
              mixer.update(clock.getDelta());
          }
          if(that.lift1texture!=undefined){
              that.lift1texture.offset.x -= 0.04;
          }
          if(that.texture!=undefined){
              that.texture.offset.x -= 0.01; // 改为原来的50%
          }
      }
      render();
    },

    // 机械臂码垛动画方法
    startStackingAnimation() {
      // 尝试不同的组件名称格式
      const baseNames = ["机械臂底座.003", "机械臂底座003", "机械臂底座"];
      const arm1Names = ["机械臂一臂.003", "机械臂一臂003", "机械臂一臂"];
      const arm2Names = ["机械臂二臂.003", "机械臂二臂003", "机械臂二臂"];
      const arm3Names = ["机械臂三臂.003", "机械臂三臂003", "机械臂三臂"];
      
      let base = null;
      let arm1 = null;
      let arm2 = null;
      let arm3 = null;
      
      // 尝试所有可能的名称
      for (const name of baseNames) {
        base = this.ThreeEngine.getObjectByName(name);
        if (base) {
          console.log('找到机械臂底座:', name);
          break;
        }
      }
      
      for (const name of arm1Names) {
        arm1 = this.ThreeEngine.getObjectByName(name);
        if (arm1) {
          console.log('找到机械臂一臂:', name);
          break;
        }
      }
      
      for (const name of arm2Names) {
        arm2 = this.ThreeEngine.getObjectByName(name);
        if (arm2) {
          console.log('找到机械臂二臂:', name);
          break;
        }
      }
      
      for (const name of arm3Names) {
        arm3 = this.ThreeEngine.getObjectByName(name);
        if (arm3) {
          console.log('找到机械臂三臂:', name);
          break;
        }
      }
      
      // 检查组件是否存在
      if (!base || !arm1 || !arm2 || !arm3) {
        console.warn('机械臂组件未找到，请检查模型中的组件名称');
        console.log('已找到的组件:');
        console.log('机械臂底座:', base);
        console.log('机械臂一臂:', arm1);
        console.log('机械臂二臂:', arm2);
        console.log('机械臂三臂:', arm3);
        
        // 打印场景中所有对象，以便找到正确的组件名称
        console.log('场景中的所有对象:');
        this.ThreeEngine.scene.traverse((obj) => {
          if (obj.name && (obj.name.includes('机械臂') || obj.name.includes('底座') || obj.name.includes('臂'))) {
            console.log('对象名称:', obj.name);
          }
        });
        
        return;
      }
      
      console.log('开始机械臂码垛动画');
      
      // 打印初始旋转值和组件信息
      console.log('初始旋转值:');
      console.log('底座旋转:', base.rotation);
      console.log('一臂旋转:', arm1.rotation);
      console.log('二臂旋转:', arm2.rotation);
      console.log('三臂旋转:', arm3.rotation);
      
      console.log('组件层级结构:');
      console.log('底座父级:', base.parent ? base.parent.name : '无');
      console.log('一臂父级:', arm1.parent ? arm1.parent.name : '无');
      console.log('二臂父级:', arm2.parent ? arm2.parent.name : '无');
      console.log('三臂父级:', arm3.parent ? arm3.parent.name : '无');
      
      // 重置机械臂到初始位置
      base.rotation.set(0, 0, 0);
      arm1.rotation.set(0, 0, 0);
      arm2.rotation.set(0, 0, 0);
      arm3.rotation.set(0, 0, 0);
      
      // 尝试不同的旋转轴和角度，使用更大的角度以便观察效果
      // 动画序列：抓取 -> 移动 -> 放置
      // 1. 底座旋转到目标位置
      console.log('开始底座旋转');
      new TWEEN.Tween(base.rotation)
        .to({ y: Math.PI }, 2000) // 旋转180度，更明显
        .easing(TWEEN.Easing.Sinusoidal.InOut)
        .onUpdate(() => {
          console.log('底座旋转中:', base.rotation.y);
        })
        .start()
        .onComplete(() => {
          console.log('底座旋转完成');
          
          // 2. 一臂抬起 - 尝试不同的轴
          console.log('开始一臂抬起');
          new TWEEN.Tween(arm1.rotation)
            .to({ z: Math.PI/2 }, 1500) // 尝试z轴旋转
            .easing(TWEEN.Easing.Sinusoidal.InOut)
            .onUpdate(() => {
              console.log('一臂旋转中:', arm1.rotation.z);
            })
            .start()
            .onComplete(() => {
              console.log('一臂抬起完成');
              
              // 3. 二臂伸展 - 尝试不同的轴
              console.log('开始二臂伸展');
              new TWEEN.Tween(arm2.rotation)
                .to({ z: Math.PI/2 }, 1200) // 尝试z轴旋转
                .easing(TWEEN.Easing.Sinusoidal.InOut)
                .onUpdate(() => {
                  console.log('二臂旋转中:', arm2.rotation.z);
                })
                .start()
                .onComplete(() => {
                  console.log('二臂伸展完成');
                  
                  // 4. 三臂微调 - 尝试不同的轴
                  console.log('开始三臂微调');
                  new TWEEN.Tween(arm3.rotation)
                    .to({ z: Math.PI/2 }, 900) // 尝试z轴旋转
                    .easing(TWEEN.Easing.Sinusoidal.InOut)
                    .onUpdate(() => {
                      console.log('三臂旋转中:', arm3.rotation.z);
                    })
                    .start()
                    .onComplete(() => {
                      console.log('三臂微调完成，准备抓取');
                      
                      // 5. 抓取动作（暂停一下模拟抓取）
                      setTimeout(() => {
                        console.log('开始移动货物');
                        
                        // 6. 三臂收回
                        new TWEEN.Tween(arm3.rotation)
                          .to({ z: 0 }, 900)
                          .easing(TWEEN.Easing.Sinusoidal.InOut)
                          .start()
                          .onComplete(() => {
                            console.log('三臂收回完成');
                            
                            // 7. 二臂收回
                            new TWEEN.Tween(arm2.rotation)
                              .to({ z: 0 }, 1200)
                              .easing(TWEEN.Easing.Sinusoidal.InOut)
                              .start()
                              .onComplete(() => {
                                console.log('二臂收回完成');
                                
                                // 8. 一臂放下
                                new TWEEN.Tween(arm1.rotation)
                                  .to({ z: 0 }, 1500)
                                  .easing(TWEEN.Easing.Sinusoidal.InOut)
                                  .start()
                                  .onComplete(() => {
                                    console.log('一臂放下完成');
                                    
                                    // 9. 底座旋转回初始位置
                                    new TWEEN.Tween(base.rotation)
                                      .to({ y: 0 }, 2000)
                                      .easing(TWEEN.Easing.Sinusoidal.InOut)
                                      .start()
                                      .onComplete(() => {
                                        console.log('机械臂码垛动画完成');
                                      });
                                  });
                              });
                          });
                      }, 500);
                    });
                });
            });
        });
    },

    initObjClickHandler() {
       // 重写ThreeEngine的点击事件处理函数
      this.ThreeEngine.onObjectClick = (intersect) => {
        // 检查点击的对象是否为"堆垛机躯干"
        if (intersect.object.name === "躯干1") {
          this.handleStackerClick(intersect.object);
        }
         if (intersect.object.name === "载货台1") {
          this.handleStackerClick(intersect.object);
        }
      };
    },

    // 处理堆垛机点击事件
    handleStackerClick(stackerObject) {
      console.log('点击了堆垛机躯干:', stackerObject.name);

      // 显示堆垛机信息面板
      this.stackerInfoVisible = true;
      this.stackerInfo = {
        name: stackerObject.name,
        status: '运行中',
        position: `X: ${stackerObject.position.x.toFixed(2)}, Y: ${stackerObject.position.y.toFixed(2)}, Z: ${stackerObject.position.z.toFixed(2)}`
      };

      // 可以在这里添加其他处理逻辑，例如：
      // 1. 高亮显示堆垛机
      // 2. 显示详细信息面板
      // 3. 触发相关操作等
    },

    createSpritePallet(objName, text){
      // 获取载货台对象以读取实时位置信息
      const obj = this.ThreeEngine.getObjectByName(objName);
      if(!obj){
        console.warn(`未找到名为 ${objName} 的载货台对象`);
        return;
      }

      // 模拟载货台状态数据（实际使用时应从后端获取）
      const platformData = {
        name: text,
        status: 'active', // active: 工作中, idle: 空闲, warning: 警告, error: 故障
        hasLoad: Math.random() > 0.5, // 是否有货物
        loadCode: 'PLT-' + Math.floor(Math.random() * 10000).toString().padStart(4, '0'), // 货物编码
        position: {
          x: obj.position.x.toFixed(1),
          y: obj.position.y.toFixed(1),
          z: obj.position.z.toFixed(1)
        },
        task: {
          current: Math.floor(Math.random() * 10), // 当前任务数
          total: 15 // 总任务数
        },
        weight: (Math.random() * 500 + 100).toFixed(1) // 当前载重(kg)
      };

      const spriteText = createSpriteTextLabel({
        global: {
          headerHeight: 20, // 缩小为1/2
          totalWidth: 120, // 缩小为1/2
          scaleRatio: 0.0075, // 缩小为1/2
          deviceName: platformData.name,
          status: platformData.status,
          backgroundColor: 'rgba(9, 28, 64, 0.92)', // 深蓝透明背景
          headerBackground: 'rgba(16, 78, 126, 0.85)', // 更深的渐变蓝
          headerTextColor: '#FFF', // 浅青文字
          textStyle: {
              fontFamily: "Microsoft YaHei",
              fontSize: 8, // 缩小为1/2
              color: "#E6F7FF", // 全局浅青文字
              textShadow: '0 0 1px rgba(173,216,230,0.8)' // 文字发光效果缩小
          },
          borderColor: '#4A90E2', // 科技蓝边框
          arrowColor: 0x00D4FF // 新增：箭头颜色（科技青色）
        },
        rows: [
          // 第一行：设备状态
          {
            rowHeight: 16, // 缩小为1/2
            backgroundColor: 'rgba(26, 67, 117, 0.5)',
            columns: [
              {
                content: "设备状态",
                widthRatio: 5,
                textStyle: {
                  color: "#4A90E2",
                  fontWeight: "bold",
                  align: "left",
                  fontSize: 7 // 缩小为1/2
                }
              },
              {
                content: platformData.status === 'active' ? '运行中' :
                         platformData.status === 'idle' ? '空闲' :
                         platformData.status === 'warning' ? '警告' : '故障',
                widthRatio: 5,
                textStyle: {
                  color: platformData.status === 'active' ? '#00FF64' :
                         platformData.status === 'idle' ? '#FFD700' :
                         platformData.status === 'warning' ? '#FFA500' : '#FF4757',
                  align: "right",
                  fontSize: 7, // 缩小为1/2
                  fontWeight: "bold"
                }
              }
            ]
          },
          // 第二行：货物状态
          {
            rowHeight: 16, // 缩小为1/2
            backgroundColor: 'rgba(20, 50, 90, 0.4)',
            columns: [
              {
                content: "货物状态",
                widthRatio: 5,
                textStyle: {
                  color: "#4A90E2",
                  fontWeight: "bold",
                  align: "left",
                  fontSize: 7 // 缩小为1/2
                }
              },
              {
                content: platformData.hasLoad ? '有货' : '空载',
                widthRatio: 5,
                textStyle: {
                  color: platformData.hasLoad ? '#7FFFD4' : '#999',
                  align: "right",
                  fontSize: 7 // 缩小为1/2
                }
              }
            ]
          },
          // 第三行：货物编码（如果有货）
          ...(platformData.hasLoad ? [{
            rowHeight: 16, // 缩小为1/2
            backgroundColor: 'rgba(26, 67, 117, 0.5)',
            columns: [
              {
                content: "货物编码",
                widthRatio: 5,
                textStyle: {
                  color: "#4A90E2",
                  fontWeight: "bold",
                  align: "left",
                  fontSize: 7 // 缩小为1/2
                }
              },
              {
                content: platformData.loadCode,
                widthRatio: 5,
                textStyle: {
                  color: "#00F2FE",
                  align: "right",
                  fontSize: 7 // 缩小为1/2
                }
              }
            ]
          }] : []),
          // 第四行：当前载重
          {
            rowHeight: 16, // 缩小为1/2
            backgroundColor: 'rgba(20, 50, 90, 0.4)',
            columns: [
              {
                content: "当前载重",
                widthRatio: 5,
                textStyle: {
                  color: "#4A90E2",
                  fontWeight: "bold",
                  align: "left",
                  fontSize: 7 // 缩小为1/2
                }
              },
              {
                content: platformData.hasLoad ? `${platformData.weight}kg` : '0kg',
                widthRatio: 5,
                textStyle: {
                  color: "#FFD700",
                  align: "right",
                  fontSize: 7 // 缩小为1/2
                }
              }
            ]
          },
          // 第五行：任务进度
          {
            rowHeight: 16, // 缩小为1/2
            backgroundColor: 'rgba(26, 67, 117, 0.5)',
            columns: [
              {
                content: "任务进度",
                widthRatio: 5,
                textStyle: {
                  color: "#4A90E2",
                  fontWeight: "bold",
                  align: "left",
                  fontSize: 7 // 缩小为1/2
                }
              },
              {
                content: `${platformData.task.current}/${platformData.task.total}`,
                widthRatio: 5,
                textStyle: {
                  color: "#7FFFD4",
                  align: "right",
                  fontSize: 7 // 缩小为1/2
                }
              }
            ]
          },
          // 第六行：位置坐标
          {
            rowHeight: 16, // 缩小为1/2
            backgroundColor: 'rgba(20, 50, 90, 0.4)',
            columns: [
              {
                content: "位置坐标",
                widthRatio: 5,
                textStyle: {
                  color: "#4A90E2",
                  fontWeight: "bold",
                  align: "left",
                  fontSize: 7 // 缩小为1/2
                }
              },
              {
                content: `(${platformData.position.x},${platformData.position.y},${platformData.position.z})`,
                widthRatio: 5,
                textStyle: {
                  color: "#999",
                  align: "right",
                  fontSize: 6 // 缩小为1/2
                }
              }
            ]
          }
        ]
      });

      spriteText.position.y = 4; //标签底部箭头和空对象标注点重合
      obj.add(spriteText); //tag会标注在空对象obj对应的位置
    },

    //给设备加上方标记
    createSprite(objName, text,y) {
      if(!y){
        y=8;
      }
      // 使用
      const spriteText = createSpriteTextLabel({
    global: {
      headerHeight:50,
      totalWidth:450,
        deviceName: "AGV小车01",
        status: "active",
        backgroundColor: 'rgba(9, 28, 64, 0.9)', // 深蓝透明背景
        headerBackground: 'rgba(16, 78, 126, 0.8)', // 更深的渐变蓝
        headerTextColor: '#FFF', // 浅青文字
        textStyle: {
            fontFamily: "Microsoft YaHei",
            fontSize: 16,
            color: "#E6F7FF", // 全局浅青文字
            textShadow: '0 0 2px rgba(173,216,230,0.8)' // 文字发光效果
        },
        borderColor: '#4A90E2' // 新增科技蓝边框
    },
    rows: [
        {
            rowHeight: 50,
            backgroundColor: 'rgba(26, 67, 117, 0.6)', // 半透明科技蓝
            columns: [
                {
                    content: "运行状态",
                    widthRatio: 5,
                    textStyle: {
                        color: "#4A90E2",
                        fontWeight: "bold",
                        align: "center",
                        fontSize: 25
                    }
                },
                {
                    content: "在线",
                    widthRatio: 5,
                    textStyle: {
                        color: "#00F2FE", // 霓虹青
                        align: "center",
                        fontSize: 25
                    }
                }
            ]
        },
        {
            rowHeight: 50,
            columns: [
                {
                    content: "任务队列",
                    widthRatio: 5,
                    textStyle: {
                        color: "#4A90E2",
                        fontWeight: "bold",
                        align: "center",
                        fontSize: 25
                    }
                },
                {
                    content: "3/5",
                    widthRatio: 5,
                    textStyle: {
                        color: "#7FFFD4", // 宝石绿
                        align: "center",
                        fontSize: 25
                    }
                }
            ]
        }
    ]
});
      spriteText.position.y = y; //标签底部箭头和空对象标注点重合
      const obj = this.ThreeEngine.getObjectByName(objName);
      if(!obj){
        return;
      }
      obj.add(spriteText); //tag会标注在空对象obj对应的位置
    },

    removeaa(objName) {
      this.ThreeEngine.removeByName(objName);
    },

    // 连接
    join() {
      var that = this;
      const wsuri = this.url;
      this.ws = new WebSocket(wsuri);
      const self = this;
      this.ws.onopen = function (event) {
        console.info("连接");
      };

      this.ws.onmessage = function (event) {
        // self.text_content = event.data + "\n";
        var data = JSON.parse(event.data);
        console.info("接收", data);
        if (data.type == "agv") {
          if (data.fromNode == "00" && data.toNode == "C01") {
            that.agvTaskRun(0);
          }
          if (data.fromNode == "C01" && data.toNode == "R01") {
            that.agvTaskRun(1);
          }
          if (data.fromNode == "R01" && data.toNode == "00") {
            that.agvTaskRun(2);
          }
        }
        if (data.type == "agv_shangliao") {
          that.shangxialiao(4);
        }
        if (data.type == "agv_xialiao") {
          that.shangxialiao(5);
        }
        if (data.type == "scTask") {
          that.scTaskRun(data.fromNode, data.toNode);
        }

        if (data.type == "ssx") {
          if (data.ssxType == "leave") {
            that.ssx(data.fromNode, data.toNode, data.palletCode);
          }

          if (data.ssxType == "hasPallet") {
            that.hasPallet(data.fromNode, data.palletCode);
          }
          if (data.ssxType == "noHasPallet") {
            that.noHasPallet(data.fromNode, data.palletCode);
          }
        }
      };
      this.ws.onclose = function (event) {
        self.text_content = self.text_content + "已经关闭连接!" + "\n";
      };
    },
    exit() {
      if (this.ws) {
        this.ws.close();
        this.ws = null;
      }
    },
    send() {
      if (this.ws) {
        this.ws.send(this.message);
      } else {
        alert("未连接到服务器");
      }
    },

    hasPallet(cellCode, palletCode) {
      var that = this;
      if (palletCode == null || palletCode == "" || palletCode == "123") {
        palletCode = cellCode + "palletCode";
      }
      that.pallet(palletCode, that.ThreeEngine.getObjectByName(cellCode));
    },
    noHasPallet(cellCode, palletCode) {
      var that = this;
      if (palletCode == null || palletCode == "" || palletCode == "123") {
        palletCode = cellCode + "palletCode";
      }
      that.ThreeEngine.removeByName(palletCode);
    },
    aaaaa() {
      var that = this;
      var spotLight = new SpotLight(0xffffff);
      const sjwz = this.ThreeEngine.getObjectByName("视觉位置");
      const sjfx = this.ThreeEngine.getObjectByName("视觉方向");
      spotLight.position.set(sjwz.position.x, sjwz.position.y, sjwz.position.z);
      spotLight.intensity = 100;
      spotLight.distance = 25;
      spotLight.angle = 0.3;
      spotLight.target = sjfx; //光源自动追踪方块
      this.ThreeEngine.addObjects(spotLight);
      // 设置一个1000毫秒后执行的定时器
      setTimeout(function () {
        that.ThreeEngine.remove(spotLight);
      }, 50);
    },

    agv_router(type) {
      // var that=this;
      // that.agv_fx=type;
    },

    createPath(pointsArr) {
      // 将参数数组转换成点数组的形式
      pointsArr = pointsArr.map((point) => new THREE.Vector3(...point));
      // 自定义三维路径 curvePath
      const path = new THREE.CurvePath();
      for (let i = 0; i < pointsArr.length - 1; i++) {
        // 每两个点之间形成一条三维直线
        const lineCurve = new THREE.LineCurve3(pointsArr[i], pointsArr[i + 1]);
        // curvePath有一个curves属性，里面存放组成该三维路径的各个子路径
        path.curves.push(lineCurve);
      }
      return path;
    },

    // type 0 ：00-起点   1 C01--R01  2:R01-00 3:R01-C01  4 上料  5 下料
    agvTaskRun(type) {
      var that = this;
      that.agvTask.isStart = 2;
      that.createAgvRouter(type);
      that.agvTask.isStart = 0;
      that.moveAgvCar();
    },

    shangxialiao(type) {
      var that = this;
      that.agvTweenStop();
      if (type == 4) {
        var from = "29";
        var to = "29-001";
        var agv_wz_code = "agv_router10";
        var agv_wz_fx_code = "agv_router11";

        var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
        pallet_agv.visible = false;
      }

      if (type == 5) {
        var from = "03-001";
        var to = "03";
        var agv_wz_code = "agv_router1";
        var agv_wz_fx_code = "agv_router0";
        var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
        pallet_agv.visible = false;
      }
      var that = this;
      that.agvTask.isStart = 2;
      const fromCell = that.ThreeEngine.getObjectByName(from);
      var pallet = that.pallet("123", fromCell);
      const toCell = that.ThreeEngine.getObjectByName(to);
      const agv = that.ThreeEngine.getObjectByName("agv");
      const agv_wz = that.ThreeEngine.getObjectByName(agv_wz_code);
      const agv_fx = that.ThreeEngine.getObjectByName(agv_wz_fx_code);
      agv.position.x = agv_wz.position.x;
      agv.position.y = agv_wz.position.y;
      agv.position.z = agv_wz.position.z;
      agv.lookAt(agv_fx.position.x, agv_fx.position.y, agv_fx.position.z);
      if (agv.tweenA != undefined && agv.tweenA != null) {
        agv.tweenA.stop();
      }
      agv.tweenA = new TWEEN.Tween(pallet.position);
      agv.tweenA.to(
        { x: toCell.position.x, y: toCell.position.y, z: toCell.position.z },
        2000
      );
      agv.tweenA.start();
      agv.tweenA.onComplete(function () {
        if (type == 4) {
          var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
          pallet_agv.visible = true;
        }

        if (type == 5) {
          var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
          pallet_agv.visible = false;
        }
      });
    },

    // type 0 ：00-起点   1 C01--R01  2:R01-00 3:R01-C01
    createAgvRouter(type) {
      var that = this;
      var yy = 20;
      const pointsArr = [];
      var fx;
      var from;
      var to;
      if (type == 0) {
        fx = 1;
        from = 1;
        to = 10;
        that.agvTask.agv_fx = fx;
      }

      if (type == 1) {
        fx = -1;
        from = 10;
        to = 1;
        that.agvTask.agv_fx = fx;
      }

      if (type == 2) {
        fx = 1;
        from = 1;
        to = 5;
        that.agvTask.agv_fx = fx;
      }
      if (type == 3) {
        fx = 1;
        from = 1;
        to = 10;
        that.agvTask.agv_fx = fx;
      }

      that.agvRouter = [];
      that.ThreeEngine.removeByName("agvRouter11");
      // this.ThreeEngine.getObjectByName("agvRouter");
      if (fx == -1) {
        for (let index = from; index >= to; index--) {
          var agv_router = this.ThreeEngine.getObjectByName(
            "agv_router" + index
          );
          pointsArr.push([
            agv_router.position.x,
            agv_router.position.y,
            agv_router.position.z,
          ]);
          that.agvRouter.push({
            x: agv_router.position.x,
            y: agv_router.position.y,
            z: agv_router.position.z,
          });
        }
      }

      if (fx == 1) {
        for (let index = from; index <= to; index++) {
          var agv_router = this.ThreeEngine.getObjectByName(
            "agv_router" + index
          );
          pointsArr.push([
            agv_router.position.x,
            agv_router.position.y,
            agv_router.position.z,
          ]);
          that.agvRouter.push({
            x: agv_router.position.x,
            y: agv_router.position.y,
            z: agv_router.position.z,
          });
        }
      }

      console.info(that.agvRouter);

      const curve = this.createPath(pointsArr);

      // 2. 创建管道体
      const tubeGeometry = new THREE.TubeGeometry(curve, 1000, 1, 2, false);
      // 纹理贴图：一定要使用透明背景的图片，否则贴图会全部叠在一起，看不出来效果
      const texLoader = new THREE.TextureLoader();
      // 图片可以用这张：http://pic.yupoo.com/mazhenghjj/e546038d/9610773f.jpg

      this.texture = texLoader.load("/img/hhh.png");
      const texture = this.texture;
      // 允许横纵设置矩阵（人话就是可以平铺）
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      texture.repeat.x = 20;
      texture.repeat.y = 1;
      // texture.offset.z =1;

      // 3. 创建管道材质
      const tubeMaterial = new THREE.MeshPhongMaterial({
        map: texture, // 颜色贴图
        transparent: true,
        color: 0x47d8fa,
        side: THREE.DoubleSide,
      });

      const mesh = new THREE.Mesh(tubeGeometry, tubeMaterial);
      mesh.position.y = 0;
      mesh.position.x = 0;
      mesh.name = "agvRouter11";
      // 4. 把几何体（管道）和 材质 生成的网格物体添加到场景中
      this.ThreeEngine.addObject(mesh);
    },

    //  AGV移动动画
    moveAgvCar() {
  var that = this;
  const AgvCar = this.ThreeEngine.getObjectByName("agv");
  var agvRouter = this.agvRouter;
  AgvCar.tweens = [];
  that.agvTweenStop();
  if (agvRouter != null && agvRouter.length > 0) {
    var position = agvRouter[0];
    for (var i = 0; i < agvRouter.length; i++) {
      var ele = agvRouter[i];
      if (i === 0) {
        AgvCar.position.set(position.x, position.y, position.z);
        AgvCar.lookAt(agvRouter[1].x, agvRouter[1].y, agvRouter[1].z);

        // 初始化摄像头位置，让摄像头在 AGV 后方一定距离跟随（第一人称模式下禁用）
        if (!that.isFirstPersonMode) {
          const followDistance = 10; // 可根据实际情况调整
          that.ThreeEngine.camera.position.set(
            AgvCar.position.x - followDistance,
            AgvCar.position.y,
            AgvCar.position.z
          );
          that.ThreeEngine.camera.lookAt(AgvCar.position);
        }
      } else {
        AgvCar["tween" + i] = new TWEEN.Tween(AgvCar.position);
        that.agvLookAt(AgvCar, AgvCar["tween" + i], i);
        // 在每个 tween 动画完成后更新摄像头位置（第一人称模式下禁用）
        AgvCar["tween" + i].onUpdate(function () {
          // 如果在第一人称模式下，不跟随AGV
          if (that.isFirstPersonMode) return;

          const followDistance = 10; // 可根据实际情况调整
          that.ThreeEngine.camera.position.set(
            AgvCar.position.x - followDistance,
            AgvCar.position.y,
            AgvCar.position.z
          );
          that.ThreeEngine.camera.lookAt(AgvCar.position);
        });
      }
    };
    AgvCar.tween.start();
  }
},

    agvTweenStop(){
      const AgvCar = this.ThreeEngine.getObjectByName("agv");
      for(var i =0;i<20;i++){
        var aaaaa =   AgvCar["tween"+i];
          if(aaaaa!=undefined && aaaaa!=null){
            aaaaa.stop();
          }
          AgvCar["tween"+i]=null;
      }
    },

    agvLookAt(AgvCar,tween,i){
      var that = this;
      var agvRouter=that.agvRouter;
      var from  = agvRouter[i-1];
      var to  = agvRouter[i];
      var distance = that.distance3D(from,to);
      var time = distance*150;
      tween.to(to , time);
      if(i<agvRouter.length-1){
        tween.onComplete(function () {
          var agvRouter=that.agvRouter;
          AgvCar.lookAt(agvRouter[i+1].x,agvRouter[i+1].y,agvRouter[i+1].z);
        });
      };
      that.agvTween(AgvCar,tween,i);
    },

    distance3D(point1, point2) {
        return Math.sqrt(
            Math.pow(point2.x - point1.x, 2) +
            Math.pow(point2.y - point1.y, 2) +
            Math.pow(point2.z - point1.z, 2)
        );
    },

    agvTween(AgvCar,tween ,i){
      if(i==1){
        AgvCar.tween=tween;
      }else if(i>1){
        AgvCar["tween"+(i-1)].chain(tween);
      }
    },


    createLiftRouter(fromCode, toCode) {
      var that = this;
      var yy = 20;
      var from = this.ThreeEngine.getObjectByName(fromCode);
      var to = this.ThreeEngine.getObjectByName(toCode);
      const pointsArr = [];
      pointsArr.push([from.position.x, from.position.y, from.position.z]);
      pointsArr.push([to.position.x, to.position.y, to.position.z]);
      const curve = this.createPath(pointsArr);

      // 2. 创建管道体
      const tubeGeometry = new THREE.TubeGeometry(curve, 10, 2, 10, false);
      // 纹理贴图：一定要使用透明背景的图片，否则贴图会全部叠在一起，看不出来效果
      const texLoader = new THREE.TextureLoader();
      // 图片可以用这张：http://pic.yupoo.com/mazhenghjj/e546038d/9610773f.jpg

      this.lift1texture = texLoader.load("/img/hhh.png");
      const texture = this.lift1texture;
      // 允许横纵设置矩阵（人话就是可以平铺）
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      texture.repeat.y = 30;
      texture.repeat.x = 3;
      texture.offset.z = 11;

      // 3. 创建管道材质
      const tubeMaterial = new THREE.MeshPhongMaterial({
        map: texture, // 颜色贴图
        transparent: true,
        color: 0x47d8fa,
        side: THREE.DoubleSide,
      });

      that.ThreeEngine.removeByName("liftRouter" + fromCode);
      that.ThreeEngine.removeByName("liftRouter" + toCode);
      const mesh = new THREE.Mesh(tubeGeometry, tubeMaterial);
      mesh.position.y = 0;
      mesh.position.x = 0;
      mesh.name = "liftRouter" + fromCode;
      // 4. 把几何体（管道）和 材质 生成的网格物体添加到场景中
      this.ThreeEngine.addObject(mesh);
    },

    //createLiftRouter() {
    //   var that = this;
    //   var yy = 20;
    //   const pointsArr = [];
    //   for (let index = 1; index <= 2; index++) {
    //     var lift_router1 = this.ThreeEngine.getObjectByName(
    //       "lift_router1-" + index
    //     );
    //     pointsArr.push([
    //       lift_router1.position.x,
    //       lift_router1.position.y,
    //       lift_router1.position.z,
    //     ]);
    //   }
    //   const curve = this.createPath(pointsArr);

    //   // 2. 创建管道体
    //   const tubeGeometry = new THREE.TubeGeometry(curve, 10, 2, 10, false);
    //   // 纹理贴图：一定要使用透明背景的图片，否则贴图会全部叠在一起，看不出来效果
    //   const texLoader = new THREE.TextureLoader();
    //   // 图片可以用这张：http://pic.yupoo.com/mazhenghjj/e546038d/9610773f.jpg

    //   this.lift1texture = texLoader.load("/img/hhh.png");
    //   const texture = this.lift1texture;
    //   // 允许横纵设置矩阵（人话就是可以平铺）
    //   texture.wrapS = THREE.RepeatWrapping;
    //   texture.wrapT = THREE.RepeatWrapping;
    //   texture.repeat.y = 30;
    //   texture.repeat.x = 3;
    //   texture.offset.z = 11;

    //   // 3. 创建管道材质
    //   const tubeMaterial = new THREE.MeshPhongMaterial({
    //     map: texture, // 颜色贴图
    //     transparent: true,
    //     color: 0x47d8fa,
    //     side: THREE.DoubleSide,
    //   });

    //   const mesh = new THREE.Mesh(tubeGeometry, tubeMaterial);
    //   mesh.position.y = 0;
    //   mesh.position.x = 0;
    //   // 4. 把几何体（管道）和 材质 生成的网格物体添加到场景中
    //   this.ThreeEngine.addObject(mesh);
    // },

    // 根据几个关键点，创建路径

    Route(points) {
      let curvePoints = new Array();
      for (let i = 0; i < points.length; i++) {
        let point = points[i];
        curvePoints.push(new THREE.Vector3(point.x, point.y, point.z));
      }
      let curve = new THREE.CatmullRomCurve3(
        curvePoints,
        false /*是否闭合*/,
        "catmullrom",
        0.000000001
      );
      return curve;
    },

    aaa() {
      this.task();
    },

    scTaskRun(from, to) {
      var that = this;

      this.scTask(1, 123, from, function name() {
        that.scTask(-1, 123, to);
      });
    },

    scTask(type, palletCode, cellCode, aaa) {
      var that = this;

      var x = 0;
      var y = 0;

      if (type == 1) {
        var scPallet = that.ThreeEngine.getObjectByName("pallet");
        scPallet.visible = false;
      } else if (type == -1) {
        var scPallet = that.ThreeEngine.getObjectByName("pallet");
        scPallet.visible = true;
      }

      const meshCell = this.ThreeEngine.getObjectByName(cellCode);

      x = meshCell.position.x;

      y = meshCell.position.y;
      const mesh = this.ThreeEngine.getObjectByName("堆垛机躯干");
      y = y - mesh.position.y;
      if (mesh.tweenA != undefined && mesh.tweenA != null) {
        mesh.tweenA.stop();
      }

      mesh.tweenA = new TWEEN.Tween(mesh.position);

      var timeA = 100 * (mesh.position.x - x);
      timeA = Math.abs(timeA);
      mesh.tweenA.to({ x: x }, timeA);
      const zht = this.ThreeEngine.getObjectByName("载货台");

      if (zht.tweenB != undefined && zht.tweenB != null) {
        zht.tweenB.stop();
      }
      zht.tweenB = new TWEEN.Tween(zht.position);
      var timeB = 200 * (zht.position.y - y);
      timeB = Math.abs(timeB);
      zht.tweenB.to({ y: y - 0.4 * type }, timeB);

      zht.tweenC = new TWEEN.Tween(zht.position);

      zht.tweenC.to({ y: y + 0.4 * type }, 500);

      const hc = this.ThreeEngine.getObjectByName("上货叉");
      const hc2 = this.ThreeEngine.getObjectByName("下货叉");
      const zhtwz = this.ThreeEngine.getObjectByName("载货台位置");
      if (type == 1) {
        zhtwz.children = [];
      }
      //为了解决货叉位置莫名其妙出问题，直接把货叉额原始位置存在堆垛机里
      if (mesh.hcX == undefined || mesh.hcX == null) {
        mesh.hcX = hc.position.x;
      }else{
        hc.position.x=mesh.hcX;
      }
      if (mesh.hc2X == undefined || mesh.hc2X == null) {
        mesh.hc2X = hc2.position.x;
      }else{
        hc2.position.x=mesh.hc2X;
      }

      if (hc.tweenS != undefined && hc.tweenS != null) {
        hc.tweenS.stop();
      }
      var fx;
      if (cellCode == "22" || cellCode == "01") {
        fx = -1;
      } else {
        fx = 1;
      }
      debugger;
      //下货叉伸
      hc2.tweenS = new TWEEN.Tween(hc2.position);
      hc2.tweenS.to({ z: mesh.hc2X - 1.3 * fx }, 1000);
      //下货叉回
      hc2.tweenH = new TWEEN.Tween(hc2.position);
      hc2.tweenH.to({ z: mesh.hc2X }, 1000);
      //上货叉伸
      hc.tweenS = new TWEEN.Tween(hc.position);
      hc.tweenS.to({ z: mesh.hcX - 3.8 * fx }, 2000);
      //上货叉回
      hc.tweenH = new TWEEN.Tween(hc.position);
      hc.tweenH.to({ z: mesh.hcX }, 2000);
      if (type == 1) {
        hc.tweenS.onComplete(function () {
          const cell = that.ThreeEngine.getObjectByName(cellCode);

          that.remove(palletCode);
          var scPallet = that.ThreeEngine.getObjectByName("pallet");
          scPallet.visible = true;
        });
      } else if (type == -1) {
        zht.tweenC.onComplete(function () {
          that.remove(palletCode);
          const cell = that.ThreeEngine.getObjectByName(cellCode);

          var scPallet = that.ThreeEngine.getObjectByName("pallet");
          scPallet.visible = false;
          that.pallet(palletCode, cell);
        });
      }
      //动画执行先后顺序

      //躯干行走--》载货台到位
      mesh.tweenA.chain(zht.tweenB);
      //载货台到位 --》下货叉伸
      zht.tweenB.chain(hc2.tweenS);
      //下货叉伸--》上货叉伸
      hc2.tweenS.chain(hc.tweenS);
      //上货叉伸--》载货台动
      hc.tweenS.chain(zht.tweenC);
      //载货台动--》下货叉收
      zht.tweenC.chain(hc2.tweenH);
      //下货叉收--》上货叉收
      hc2.tweenH.chain(hc.tweenH);

      hc.tweenH.onComplete(aaa);
      mesh.tweenA.start();
      //tween动画开始执行
    },

    //输送线动画
    ssx(fromCellCode, toCellCode, palletCode) {
      var that = this;
      if (palletCode == null || palletCode == "" || palletCode == "123") {
        palletCode = toCellCode + "palletCode";
      }
      that.ThreeEngine.removeByName(fromCellCode + "palletCode");
      if (fromCellCode == "05" && toCellCode == "06") {
        this.ssx2(fromCellCode, "05-001", toCellCode, palletCode);
        return;
      }
      if (fromCellCode == "15" && toCellCode == "16") {
        this.ssx2(fromCellCode, "15-001", toCellCode, palletCode);
        return;
      }
      if (fromCellCode == "21" && toCellCode == "22") {
        this.ssx2(fromCellCode, "21-001", toCellCode, palletCode);
        return;
      }

      const fromCell = that.ThreeEngine.getObjectByName(fromCellCode);
      var pallet = that.pallet(palletCode, fromCell);
      const toCell = that.ThreeEngine.getObjectByName(toCellCode);
      var tweenA = new TWEEN.Tween(pallet.position);
      tweenA.to(
        { x: toCell.position.x, y: toCell.position.y, z: toCell.position.z },
        2000
      );

      tweenA.start();
    },

    //专门给提升机用
    ssx2(fromCellCode, zzCellCode, toCellCode, palletCode) {
      var that = this;
      const fromCell = that.ThreeEngine.getObjectByName(fromCellCode);
      var pallet = that.pallet(palletCode, fromCell);
      const toCell = that.ThreeEngine.getObjectByName(toCellCode);

      const zzwCell = that.ThreeEngine.getObjectByName(zzCellCode);

      var liftFrom = fromCellCode + "-a";
      var liftTo = zzCellCode + "-a";
      that.createLiftRouter(liftFrom, liftTo);

      var tweenA = new TWEEN.Tween(pallet.position);
      tweenA.to(
        {
          x: zzwCell.position.x,
          y: zzwCell.position.y,
          z: zzwCell.position.z,
        },
        3000
      );
      tweenA.onComplete(function () {
        that.ThreeEngine.removeByName("liftRouter" + liftFrom);
        that.ThreeEngine.removeByName("liftRouter" + liftTo);
      });

      var tweenB = new TWEEN.Tween(pallet.position);
      tweenB.to(
        { x: toCell.position.x, y: toCell.position.y, z: toCell.position.z },
        2000
      );
      tweenA.chain(tweenB);
      tweenA.start();
    },

    remove(name) {
      var that = this;
      const obj = that.ThreeEngine.getObjectByName(name);
      if (obj != null) {
        const parentObj = obj.parent;
        if (parentObj != null) {
          if (parentObj.name != "") {
            parentObj.children = [];
          }
        }
      }
      that.ThreeEngine.removeByName(name);
    },

    pallet(palletCode, obj) {
      var that = this;

      that.remove(palletCode);

      var newPallet = that.ThreeEngine.getObjectByName("pallet").clone();
      newPallet.visible = true;
      newPallet.position.set(obj.position.x, obj.position.y, obj.position.z); // 将Y坐标增加1，表示在父物体上方1单位长度的位置
      newPallet.name = palletCode;

      // 应用浅蓝色材质
      materialManager.applyMaterialToObject(newPallet, 'pallet', 'main', 'default');

      that.ThreeEngine.addObject(newPallet);
      if (
        palletCode != null &&
        palletCode.length > 5 &&
        palletCode.substring(0, 3) == "OSA"
      ) {
        this.createSprite(palletCode, palletCode);
      }

      return newPallet;
    },

    getChildByName(obj, name) {
      obj.children.forEach((child) => {
        if (child.name == name) {
          return child;
        }
      });
    },

    // 切换无人机飞行模式
    toggleFirstPersonMode() {
      this.isFirstPersonMode = !this.isFirstPersonMode;

      if (this.isFirstPersonMode) {
        // 重置鼠标锁定状态
        this.isMouseLocked = false;

        // 禁用轨道控制器（重要！防止冲突）
        if (this.ThreeEngine.controls) {
          this.ThreeEngine.controls.enabled = false;
          // 阻止阻尼效果继续运行
          this.ThreeEngine.controls.enableDamping = false;
          console.log('✅ 轨道控制器已完全禁用');
        }

        // 启用无人机模式
        this.firstPersonController.enable();

        // 启用炮弹系统
        if (this.projectileSystem) {
          this.projectileSystem.enable();
        }

        // 输出详细状态
        console.log('📊 无人机模式状态:');
        console.log('  - isFirstPersonMode:', this.isFirstPersonMode);
        console.log('  - controller.enabled:', this.firstPersonController.enabled);
        console.log('  - domElement:', this.firstPersonController.domElement);
        console.log('  - projectileSystem.enabled:', this.projectileSystem?.enabled);

        this.$modal.msgSuccess('🚁 无人机飞行模式已启用（FPS风格）\n\n操作说明：\n━━━━━━━━━━━━\n🖱️ 第一步：点击画面锁定鼠标\n🎯 第二步：移动鼠标旋转视角\n🔫 第三步：鼠标左键发射炮弹\n\n⌨️ 方向键控制移动：\n  · ↑/W：前进\n  · ↓/S：后退\n  · ←/A：向左平移\n  · →/D：向右平移\n  · Space：上升\n  · Shift：下降\n\n🔓 按ESC解锁鼠标\n💥 炮弹有重力和爆炸效果');
      } else {
        // 禁用无人机模式
        this.firstPersonController.disable();

        // 重置鼠标锁定状态
        this.isMouseLocked = false;

        // 禁用炮弹系统
        if (this.projectileSystem) {
          this.projectileSystem.disable();
        }

        // 重新启用轨道控制器
        if (this.ThreeEngine.controls) {
          this.ThreeEngine.controls.enabled = true;
          this.ThreeEngine.controls.enableDamping = true;
          console.log('✅ 轨道控制器已重新启用');
        }
        this.$modal.msgSuccess('已退出无人机飞行模式');
      }
    },

    // 处理画布点击（用于锁定鼠标和发射炮弹）
    handleCanvasClick(event) {
      // 第一人称模式下
      if (this.isFirstPersonMode && this.firstPersonController) {
        // 如果鼠标未锁定，先锁定鼠标
        if (!this.isMouseLocked) {
          this.firstPersonController.lock();
          console.log('🖱️ 点击画面，请求鼠标锁定...');
        }
        // 如果鼠标已锁定且是左键点击，发射炮弹
        else if (event.button === 0) {
          this.fireProjectile();
        }
      }
    },

    // 发射炮弹
    fireProjectile() {
      if (this.projectileSystem && this.projectileSystem.enabled) {
        this.projectileSystem.fire();
      }
    },

    // 测试无人机控制（调试用）
    testDroneControl() {
      console.log('========== 无人机控制测试 ==========');
      console.log('1. 控制器存在:', !!this.firstPersonController);
      console.log('2. 控制器启用:', this.firstPersonController?.enabled);
      console.log('3. 第一人称模式:', this.isFirstPersonMode);
      console.log('4. 鼠标锁定状态:', this.firstPersonController?.isLocked);
      console.log('5. DOM元素:', this.firstPersonController?.domElement);
      console.log('6. 相机位置:', this.ThreeEngine?.camera?.position);
      console.log('7. 相机旋转:', this.ThreeEngine?.camera?.rotation);
      console.log('8. 移动状态:', this.firstPersonController?.moveState);

      // 手动设置测试值
      if (this.firstPersonController) {
        console.log('\n手动设置前进状态...');
        this.firstPersonController.moveState.forward = true;
        setTimeout(() => {
          this.firstPersonController.moveState.forward = false;
          console.log('✅ 已停止前进');
        }, 2000);
        console.log('✅ 已设置前进2秒，请观察相机是否移动');
      }
      console.log('====================================');
    },
  },
};
</script>

<style lang="scss">
// 最外层wrapper - 不创建层叠上下文
.three3d-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

.container3d {
  width: 100%;
  height: 100%;
  position: absolute; // 改为absolute
  top: 0;
  left: 0;
  z-index: 0; // 底层

  background-image: url("/img/999.jpg");
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;

  // 添加轻微的遮罩层，使3D区域与报表区域更好地融合
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(
      ellipse at center,
      rgba(0, 0, 0, 0.1) 0%,
      rgba(0, 0, 0, 0.3) 70%,
      rgba(0, 0, 0, 0.5) 100%
    );
    pointer-events: none;
  }
}

// 控制面板样式
.control-panel {
  position: absolute;
  z-index: 100; // 提高z-index确保不被外层UI遮挡
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  // 更自然的背景，减少边缘对比度
  background: radial-gradient(
    ellipse at center,
    rgba(20, 30, 48, 0.7) 0%,
    rgba(20, 30, 48, 0.5) 40%,
    rgba(20, 30, 48, 0.2) 70%,
    transparent 100%
  );
  border-radius: 8px;
  // 更柔和的边框效果
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow:
    0 0 5px rgba(64, 158, 255, 0.1),
    inset 0 0 10px rgba(0, 0, 0, 0.3);
  // 更轻的毛玻璃效果
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
}

.panel-top {
  top: 20px;
  left: 20px;
}

.panel-middle {
  top: 80px;
  left: 20px;
}

.panel-bottom {
  top: 140px;
  left: 20px;
}

.panel-debug {
  top: 20px;
  right: 20px;
  width: 200px;
}

.panel-debug h3 {
  color: #fff;
  margin: 0 0 10px 0;
  font-size: 16px;
  text-align: center;
  // 更柔和的发光效果
  text-shadow: 0 0 2px rgba(255, 255, 255, 0.3);
}

.debug-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

// 堆垛机信息面板样式
.stacker-info-panel {
  position: absolute;
  z-index: 110; // 提高z-index确保不被外层UI遮挡
  top: 20px;
  left: 250px;
  width: 300px;
  padding: 15px;
  background: radial-gradient(
    ellipse at center,
    rgba(20, 30, 48, 0.9) 0%,
    rgba(20, 30, 48, 0.7) 40%,
    rgba(20, 30, 48, 0.4) 70%,
    transparent 100%
  );
  border-radius: 8px;
  border: 1px solid rgba(64, 158, 255, 0.3);
  box-shadow:
    0 0 10px rgba(64, 158, 255, 0.2),
    inset 0 0 15px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  color: #fff;
}

.stacker-info-panel h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  text-align: center;
  text-shadow: 0 0 3px rgba(64, 158, 255, 0.5);
}

.info-content p {
  margin: 10px 0;
  font-size: 14px;
}

// 自定义输入框样式
.custom-input {
  width: 100px;
  padding: 8px 12px;
  // 更自然的背景
  background: rgba(30, 30, 30, 0.6);
  border: 1px solid rgba(100, 150, 255, 0.3);
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;

  &:focus {
    border-color: #4A90E2;
    box-shadow: 0 0 5px rgba(74, 144, 226, 0.3);
  }

  &::placeholder {
    color: #aaa;
  }
}

// 自定义按钮样式
.custom-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  outline: none;

  // 更柔和的背景渐变
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.8), rgba(53, 122, 189, 0.6));

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }

  &:active {
    transform: translateY(0);
  }

  &:focus {
    outline: 1px solid rgba(255, 255, 255, 0.2);
  }

  // 更柔和的按钮波纹效果
  &::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5px;
    height: 5px;
    background: rgba(255, 255, 255, 0.3);
    opacity: 0;
    border-radius: 100%;
    transform: scale(1, 1) translate(-50%);
    transform-origin: 50% 50%;
  }

  &:focus:not(:active)::after {
    animation: ripple 1s ease-out;
  }
}

// 按钮颜色变体
.custom-btn.primary {
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.8), rgba(53, 122, 189, 0.6));
  color: white;
}

.custom-btn.secondary {
  background: linear-gradient(135deg, rgba(80, 200, 120, 0.8), rgba(58, 151, 90, 0.6));
  color: white;
}

.custom-btn.success {
  background: linear-gradient(135deg, rgba(0, 201, 167, 0.8), rgba(0, 155, 119, 0.6));
  color: white;
}

.custom-btn.warning {
  background: linear-gradient(135deg, rgba(255, 165, 0, 0.8), rgba(204, 132, 0, 0.6));
  color: white;
}

.custom-btn.danger {
  background: linear-gradient(135deg, rgba(255, 71, 87, 0.8), rgba(201, 58, 64, 0.6));
  color: white;
}

// 波纹动画
@keyframes ripple {
  0% {
    transform: scale(0, 0);
    opacity: 0.3;
  }
  100% {
    transform: scale(50, 50);
    opacity: 0;
  }
}

.progress-box{
  border-radius:5vh;
  height: 25vh;
  // 更自然的背景
  background: radial-gradient(
    ellipse at center,
    rgba(20, 30, 48, 0.8) 0%,
    rgba(20, 30, 48, 0.6) 40%,
    rgba(20, 30, 48, 0.3) 70%,
    rgba(20, 30, 48, 0.1) 100%
  );
  width: 30vw;
  margin-left: 30vw;
  margin-top: 30vh;
  position: absolute;
  z-index:999;
  // 更柔和的边框效果
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow:
    0 0 10px rgba(64, 158, 255, 0.1),
    inset 0 0 15px rgba(0, 0, 0, 0.3);
  // 更轻的毛玻璃效果
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  .name{
    color: #fff;
    width: 100%;
    text-align: center;
    margin-top: 5vh;
    margin-bottom: 5vh;
    // 更柔和的发光效果
    text-shadow: 0 0 2px rgba(255, 255, 255, 0.3);
  }
  .progress-bar {
    width: 80%;
    margin-left: 10%;
    background-color: #e0e0e0;
    border-radius: 4px;
    overflow: hidden;
  }
  .progress {
    width: 50%; /* 进度条的初始宽度，可以根据需要更改 */
    height: 20px;
    background-color: #4caf50;
    border-radius: 4px;
    transition: width 1s ease-in-out; /* 进度条变化的动画效果 */
  }
}

.three-canvas {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

// 调试UI层 - 在wrapper内，不受container3d层叠上下文限制
.debug-ui-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; // 不阻挡3D交互
  z-index: 50; // 高于container3d

  // 子元素恢复pointer-events
  > * {
    pointer-events: auto;
  }
}

.workshop-text {
  box-shadow: 0 0 3px #2d69a1;
  padding: 10px;
  background: rgba(45, 105, 161, 0.3);
}

.workshop-text p {
  font-size: 0.3rem;
  font-weight: bold;
  padding: 10px;
  color: #fff;
}
.workshop-text.IDLE {
    background: rgba(236, 210, 114, 0.3) !important;
}

.workshop-text.RUNNING {
    background: rgba(134, 239, 212, 0.3) !important;
}

.workshop-text.OFFLINE {
    background: rgba(138, 177, 222, 0.3) !important;
}

.equipment-label {
    background: rgba(255, 255, 255, 0.7);
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    min-width: 200px;
    // 更轻的毛玻璃效果
    backdrop-filter: blur(3px);
    border: 1px solid rgba(255,255,255,0.1);
}

.label-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.label-header h3 {
    margin: 0;
    color: #2c3e50;
    font-size: 16px;
}

.close-btn {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    color: #999;
    transition: color 0.2s;
}

.close-btn:hover {
    color: #ff4757;
}

.label-content p {
    margin: 8px 0;
    color: #666;
    font-size: 14px;
    white-space: pre-wrap;
}

.status-indicator {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    margin-top: 8px;
}

.status-indicator.active {
    background: #2ecc71;
    box-shadow: 0 0 5px rgba(46, 204, 113, 0.3);
}

// 无人机飞行准星样式
.drone-crosshair {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 120; // 提高z-index确保不被外层UI遮挡
}

// 鼠标锁定提示
.lock-hint {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: rgba(0, 20, 40, 0.9);
  padding: 30px 50px;
  border: 2px solid rgba(0, 255, 100, 0.8);
  border-radius: 15px;
  box-shadow:
    0 0 30px rgba(0, 255, 100, 0.5),
    inset 0 0 20px rgba(0, 255, 100, 0.2);
  backdrop-filter: blur(10px);
  animation: pulse 2s ease-in-out infinite;
}

.lock-hint-icon {
  font-size: 48px;
  margin-bottom: 15px;
  animation: bounce 1s ease-in-out infinite;
}

.lock-hint-text {
  font-size: 24px;
  color: #00ff64;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 0 0 10px rgba(0, 255, 100, 0.8);
}

.lock-hint-subtext {
  font-size: 14px;
  color: rgba(0, 255, 100, 0.7);
  letter-spacing: 1px;
}

@keyframes pulse {
  0%, 100% {
    border-color: rgba(0, 255, 100, 0.8);
    box-shadow:
      0 0 30px rgba(0, 255, 100, 0.5),
      inset 0 0 20px rgba(0, 255, 100, 0.2);
  }
  50% {
    border-color: rgba(0, 255, 100, 1.0);
    box-shadow:
      0 0 50px rgba(0, 255, 100, 0.8),
      inset 0 0 30px rgba(0, 255, 100, 0.3);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.crosshair-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 8px;
  height: 8px;
  border: 2px solid rgba(0, 255, 100, 0.8);
  border-radius: 50%;
  background: rgba(0, 255, 100, 0.2);
  box-shadow:
    0 0 10px rgba(0, 255, 100, 0.6),
    inset 0 0 5px rgba(0, 255, 100, 0.4);
}

.crosshair-line {
  position: absolute;
  background: rgba(0, 255, 100, 0.6);
  box-shadow: 0 0 5px rgba(0, 255, 100, 0.4);
}

.crosshair-line.horizontal {
  top: 50%;
  left: 50%;
  width: 40px;
  height: 2px;
  transform: translate(-50%, -50%);
}

.crosshair-line.vertical {
  top: 50%;
  left: 50%;
  width: 2px;
  height: 40px;
  transform: translate(-50%, -50%);
}

// 无人机HUD信息面板
.drone-hud {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 30px;
  padding: 10px 20px;
  background: rgba(0, 20, 40, 0.7);
  border: 1px solid rgba(0, 255, 100, 0.5);
  border-radius: 8px;
  backdrop-filter: blur(5px);
  box-shadow: 0 0 15px rgba(0, 255, 100, 0.3);
  z-index: 120; // 提高z-index确保不被外层UI遮挡
}

.hud-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.hud-label {
  font-size: 12px;
  color: rgba(0, 255, 100, 0.8);
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: bold;
}

.hud-value {
  font-size: 20px;
  color: #00ff64;
  font-weight: bold;
  font-family: 'Consolas', 'Monaco', monospace;
  text-shadow:
    0 0 5px rgba(0, 255, 100, 0.8),
    0 0 10px rgba(0, 255, 100, 0.4);
}

// 控制说明面板
.control-hints {
  position: absolute;
  bottom: 30px;
  left: 30px;
  display: flex;
  gap: 30px;
  padding: 15px 25px;
  background: rgba(0, 20, 40, 0.85);
  border: 1px solid rgba(0, 255, 100, 0.4);
  border-radius: 10px;
  backdrop-filter: blur(8px);
  box-shadow: 0 0 20px rgba(0, 255, 100, 0.2);
}

.control-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.control-title {
  font-size: 16px;
  color: #00ff64;
  font-weight: bold;
  margin-bottom: 5px;
  letter-spacing: 1px;
  text-shadow: 0 0 5px rgba(0, 255, 100, 0.6);
  border-bottom: 1px solid rgba(0, 255, 100, 0.3);
  padding-bottom: 5px;
}

.control-item {
  font-size: 13px;
  color: rgba(0, 255, 100, 0.9);
  font-family: 'Consolas', 'Microsoft YaHei', monospace;
  line-height: 1.6;
  padding-left: 8px;
  border-left: 2px solid rgba(0, 255, 100, 0.3);
  transition: all 0.2s;
}

.control-item:hover {
  color: #00ff64;
  border-left-color: #00ff64;
  padding-left: 12px;
  text-shadow: 0 0 3px rgba(0, 255, 100, 0.5);
}
</style>
