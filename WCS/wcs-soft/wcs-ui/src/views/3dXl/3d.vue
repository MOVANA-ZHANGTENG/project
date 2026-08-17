<template>
  <div class="three3d-wrapper">
    <!-- 3D画布容器 -->
    <div class="container3d">
      <div refs="3d" class="three-canvas" id="3d" ref="threeTarget">
      </div>
    </div>

    <!-- UI层 - 在wrapper下，不受container3d的层叠上下文限制 -->
    <div v-if="jiinduDisable" class="progress-box">
      <div class="name">
        模型加载中{{ jindu }}
      </div>

      <div class="progress-bar">
        <div :style="'width:' + jindu" class="progress"></div>
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
          <button :class="isFirstPersonMode ? 'custom-btn danger' : 'custom-btn warning'"
            @click="toggleFirstPersonMode">
            {{ isFirstPersonMode ? '退出无人机模式' : '🚁 无人机飞行' }}
          </button>
          <button class="custom-btn primary" @click="testDroneControl" v-if="isFirstPersonMode">
            🔍 测试诊断
          </button>
        </div>
      </div>


      <!-- 任务测试面板 -->
      <div v-if="isTest" class="control-panel panel-rgv-move-task">
        <h3>任务测试</h3>
        <div class="rgv-move-task-controls">
          <div class="control-group">
            <label for="taskType">任务类型:</label>
            <select id="taskType" v-model="taskType" class="control-input">
              <option value="RGV_MOVE">RGV_MOVE (RGV移动任务)</option>
              <option value="DDJ_MOVE">DDJ_MOVE (堆垛机移动任务)</option>
            </select>
          </div>
          <div class="control-group">
            <label for="moveFromCell">起点单元格:</label>
            <input id="moveFromCell" type="text" v-model="moveFromCell" placeholder="格式: 层数-站台号 (如: 1-2)"
              class="control-input">
          </div>
          <div class="control-group">
            <label for="moveToCell">终点单元格:</label>
            <input id="moveToCell" type="text" v-model="moveToCell" placeholder="格式: 层数-站台号 (如: 1-5)"
              class="control-input">
          </div>
          <div class="control-group">
            <label for="palletCode">托盘号:</label>
            <input id="palletCode" type="text" v-model="palletCode" placeholder="如: XL0000001H" class="control-input">
          </div>
          <div v-if="taskType === 'DDJ_MOVE'" class="control-group">
            <label for="ddjNumber">堆垛机编号:</label>
            <select id="ddjNumber" v-model="ddjNumber" class="control-input">
              <option value="1">1号堆垛机</option>
              <option value="2">2号堆垛机</option>
              <option value="3">3号堆垛机</option>
              <option value="4">4号堆垛机</option>
            </select>
          </div>
          <button class="custom-btn primary" @click="runTaskTest" :disabled="!isTaskParamsValid">任务测试
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
          <span class="hud-value">{{ Math.round(ThreeEngine && ThreeEngine.camera ? ThreeEngine.camera.position.y : 0)
          }}m</span>
        </div>
        <div class="hud-info">
          <span class="hud-label">速度</span>
          <span class="hud-value">{{ firstPersonController ? firstPersonController.moveSpeed.toFixed(1) : 0 }}m/s</span>
        </div>
        <div class="hud-info">
          <span class="hud-label">状态</span>
          <span class="hud-value" :style="{ color: isMouseLocked ? '#00ff64' : '#ff6b6b' }">
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
import RgvMovement from './js/TRgvMovement.js';
import MoveToLocationPanel from './components/MoveToLocationPanel.vue';
export default {
  components: {
    MoveToLocationPanel
  },
  data() {
    return {
      isTest: false,
      jindu: "0%",
      jiinduDisable: true,

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

      // WebSocket配置参数
      wsConfig: {
        heartbeatInterval: 30000, // 心跳检测间隔(ms)
        reconnectInterval: 5000,  // 重连间隔(ms)
        maxReconnectAttempts: 30  // 最大重连尝试次数
      },
      wsStatus: {
        connected: false,   // 连接状态
        reconnectTimer: null,  // 重连定时器
        heartbeatTimer: null,  // 心跳定时器
        reconnectAttempts: 0   // 已重连次数
      },

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

      // 货物移动面板相关
      cargoList: [],             // 货物列表
      selectedCargo: null,       // 当前选中的货物
      locationCode: null,        // 当前输入的货位编码

      // RGV移动相关
      rgvMovement: null,         // RGV移动控制器实例
      selectedRgv: 'rgv1',       // 当前选中的RGV
      moveDuration: 5000,        // 移动持续时间（毫秒）

      // 物流线货物移动相关
      conveyorLineName: '1-2',   // 物流线名称（格式：层数-站台号）
      moveDirection: 'fromTo',   // 移动方向：fromTo（从_from到_to）或toFrom（从_to到_from）
      conveyorMoveDuration: 8000, // 进一步降低物流线移动速度，增加持续时间（原来4000ms）
      conveyorLineStatus: {},     // 物流线状态管理：键为物流线名称，值为'free'或'moving'

      // RGV_MOVE任务测试相关
      // 通用任务测试相关
      taskType: 'DDJ_MOVE',       // 任务类型 (RGV_MOVE 或 DDJ_MOVE)
      moveFromCell: '1-11',         // 测试用起点单元格
      moveToCell: '7-2-14',           // 测试用终点单元格
      palletCode: 'XL0000001H',   // 测试用托盘号
      ddjNumber: '4',              // 堆垛机编号 (仅DDJ_MOVE任务使用)
    };
  },
  watch: {},
  created() { },
  mounted() {
    this.open();
  },
  computed: {
    /**
     * 验证任务参数是否有效
     * 不同任务类型有不同的参数验证规则
     * @returns {boolean} 参数是否有效
     */
    isTaskParamsValid() {
      // 通用验证：起点、终点、托盘号不能为空
      if (!this.moveFromCell || !this.moveToCell || !this.palletCode) {
        return false;
      }

      // 不同任务类型的特殊验证
      if (this.taskType === 'DDJ_MOVE') {
        // 堆垛机任务必须选择堆垛机编号
        if (!this.ddjNumber) {
          return false;
        }
      }

      // 验证位置编码格式
      const locationRegex = /^\d+-\d+(?:-\d+)?$/; // 格式：数字-数字 或 数字-数字-数字
      if (!locationRegex.test(this.moveFromCell) || !locationRegex.test(this.moveToCell)) {
        return false;
      }

      // 所有验证通过
      return true;
    }
  },

  beforeDestroy() {
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
    /**
     * 根据货物列表批量生成3D货物对象
     * 该方法接收货物列表数据，解析并计算每个货物的货位坐标，然后在场景中生成对应的3D对象
     * @param {Array} cargoList - 货物列表数组，每个元素应包含货物编码(code)、货位编码(locationCode)等信息
     */
    generateCargoFromList(cargoList) {
      if (!cargoList || cargoList.length === 0) {
        console.warn('generateCargoFromList: 货物列表为空，跳过处理');
        return;
      }

      if (!this.ThreeEngine) {
        console.error('generateCargoFromList: ThreeEngine 未初始化，无法生成货物');
        return;
      }

      // 遍历货物列表，逐个生成货物
      cargoList.forEach((cargo, index) => {
        try {
          // 验证货物数据完整性
          if (!cargo || !cargo.code || !cargo.palletCode) {
            console.warn(`货物数据不完整，跳过处理:`, cargo);
            return;
          }

          const { code, palletCode } = cargo; // code为货位，palletCode为货位名称
          const currentPalletCode = palletCode || code; // 使用货位名称(palletCode)或货位(code)作为唯一标识

          // 计算货位坐标
          const coordinates = this.getShelfLocationCoordinates(code);
          if (!coordinates) {
            console.error(`无法计算货位坐标，跳过货物: ${currentPalletCode}`);
            return;
          }

          // 创建临时位置对象
          const targetPosition = {
            position: coordinates
          };

          // 生成货物（调用pallet方法直接实现）
          this.remove(currentPalletCode); // 清理同名旧对象

          // 获取原始货物模板
          const originalPallet = this.ThreeEngine.getObjectByName('货物');
          if (!originalPallet) {
            console.error('未找到原始货物模板，无法生成货物');
            return;
          }

          // 克隆并设置新货物属性
          const newPallet = originalPallet.clone();
          newPallet.visible = true;
          newPallet.position.set(coordinates.x, coordinates.y, coordinates.z);
          newPallet.name = currentPalletCode;

          // 应用材质
          try {
            materialManager.applyMaterialToObject(newPallet, '货物', 'main', 'default');
          } catch (materialError) {
            console.error(`应用材质失败 for ${currentPalletCode}:`, materialError);
          }

          // 添加到场景
          this.ThreeEngine.addObject(newPallet);

          // 为特殊格式的货物创建标签
          if (currentPalletCode && currentPalletCode.length > 5 && currentPalletCode.substring(0, 3) === 'OSA') {
            try {
              this.createSprite(currentPalletCode, currentPalletCode);
            } catch (spriteError) {
              console.error(`创建标签失败 for ${currentPalletCode}:`, spriteError);
            }
          }
        } catch (error) {
          console.error(`处理货物失败 [${index}]:`, cargo, error);
        }
      });
    },

    initInventory() {
      // 初始化库存
      request({
        url: '/wcs-base/CellInfo/find3dInventory',
        method: 'get',
        params: {
          wareCode: "XL_WARE"
        }
      }).then(res => {
        if (res.code === 200) {
          this.cargoList = res.data;
          // 根据货物列表生成3D货物对象
          this.generateCargoFromList(this.cargoList);
        }
      })

    },

    cancelAnimation() {
      if (this.requestId != null) {
        cancelAnimationFrame(this.requestId);
        this.requestId = null;
      }
    },
    open() {
      var that = this;
      // 初始化three.js
      that.init();
      // 初始化WebSocket连接
      that.join();
      // 移除模型点击事件（不再需要）
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

    // 运行RGV测试
    runRgvTest() {
      if (!this.rgvMovement) {
        console.error('RGV移动控制器未初始化');
        return;
      }

      this.rgvMovement.runRgvTest(this.selectedRgv, this.moveDuration);
    },

    /**
     * 获取堆垛机移动位置的坐标
     * 针对堆垛机移动的特殊需求进行坐标计算，包括物流线的特殊处理
     * 
     * @param {string} locationCode - 位置编码
     * @returns {Object|null} 坐标对象 {x, y, z} 或 null（如果无法解析）
     */
    /**
     * 获取堆垛机移动坐标
     * 针对堆垛机移动的需求进行坐标计算
     * 
     * @param {string} locationCode - 位置编码
     * @param {boolean} isPickup - 是否为取货操作，取货时返回起点坐标，放货时返回终点坐标
     * @returns {Object|null} 坐标对象 {x, y, z} 或 null（如果无法解析）
     */
    getStackerLocationCoordinates(locationCode, isPickup = false, ddjNumber = null) {
      // 精简日志，只保留关键信息
      if (isPickup && (locationCode === "1-5" || locationCode === "1-6")) {
      }

      // 物流线编码解析 - 堆垛机移动有特殊处理
      if (locationCode.includes('-')) {
        const parts = locationCode.split('-');
        if (parts.length === 2) {
          // 验证ThreeEngine是否初始化
          if (this.ThreeEngine) {

            const lineName = locationCode; // 物流线名称格式为"层数-站台号"
            // 根据取货/放货操作选择起点或终点
            // 取货时（去任务起点拿货）：物流线终点（_to）
            // 放货时（去任务终点放货）：物流线起点（_from）
            let endpointCode = null;

            // 处理堆垛机在特定物流线取放货的特例规则
            // 根据堆垛机编号和物流线名称确定货物应该放置的位置
            if (ddjNumber && ['1', '2', '3', '4'].includes(ddjNumber)) {
              // 堆垛机4在1-11物流线取放货，货物都应在1-11_to
              if (ddjNumber === '4' && lineName === '1-11') {
                endpointCode = lineName + "_to";
              }
              // 堆垛机1在1-12物流线取放货，货物都应在1-12_from
              else if (ddjNumber === '1' && lineName === '1-12') {
                endpointCode = lineName + "_from";
              }
              // 堆垛机2在1-12物流线取放货，货物都应在1-12_to
              else if (ddjNumber === '2' && lineName === '1-12') {
                endpointCode = lineName + "_to";
              }
              // 堆垛机2在1-13物流线取放货，货物都应在1-13_from
              else if (ddjNumber === '2' && lineName === '1-13') {
                endpointCode = lineName + "_from";
              }
              // 堆垛机3在1-13物流线取放货，货物都应在1-13_to
              else if (ddjNumber === '3' && lineName === '1-13') {
                endpointCode = lineName + "_to";
              }
              // 堆垛机3在1-14物流线取放货，货物都应在1-14_from
              else if (ddjNumber === '3' && lineName === '1-14') {
                endpointCode = lineName + "_from";
              }
              // 堆垛机4在1-14物流线取放货，货物都应在1-14_to
              else if (ddjNumber === '4' && lineName === '1-14') {
                endpointCode = lineName + "_to";
              }
            }

            // 如果没有符合的特例规则，则使用默认的取放货位置逻辑
            if (!endpointCode) {
              endpointCode = isPickup ? lineName + "_to" : lineName + "_from";
            }

            // 查找物流线对应端点对象
            const endpointObj = this.ThreeEngine.getObjectByName(endpointCode);
            if (endpointObj) {
              // 获取位置坐标
              const x = endpointObj.position.x;
              const y = endpointObj.position.y;
              const z = endpointObj.position.z;
              return { x, y, z };
            }
          }
        } else if (parts.length === 3) {
          // 货位编码解析，优先调用getShelfLocationCoordinates方法
          const shelfLocation = this.getShelfLocationCoordinates(locationCode);
          if (shelfLocation) {
            return shelfLocation;
          } else {
            console.error(`[堆垛机] 货位编码 ${locationCode} 解析失败`);
            return null;
          }
        }
      }
    },

    /**
     * 获取货物移动坐标
     * 针对货物移动的需求进行坐标计算，使用标准的物流线终点位置
     * 
     * @param {string} locationCode - 位置编码
     * @returns {Object|null} 坐标对象 {x, y, z} 或 null（如果无法解析）
     */
    /**
     * 获取货物移动坐标
     * 针对货物移动的需求进行坐标计算
     * 
     * @param {string} locationCode - 位置编码
     * @param {boolean} isPickup - 是否为取货操作，取货时返回起点坐标，放货时返回终点坐标
     * @returns {Object|null} 坐标对象 {x, y, z} 或 null（如果无法解析）
     */
    /**
     * 获取货物移动坐标
     * 针对货物移动的需求进行坐标计算，支持堆垛机在特定物流线的特例规则
     * 
     * @param {string} locationCode - 位置编码
     * @param {boolean} isPickup - 是否为取货操作，取货时返回起点坐标，放货时返回终点坐标
     * @param {string} [ddjNumber] - 堆垛机编号（用于处理特殊物流线的取放货位置）
     * @returns {Object|null} 坐标对象 {x, y, z} 或 null（如果无法解析）
     */
    getCargoLocationCoordinates(locationCode, isPickup = false, ddjNumber = null) {
      // 物流线编码解析 - 货物移动根据操作类型选择起点或终点
      if (locationCode.includes('-')) {
        const parts = locationCode.split('-');
        if (parts.length === 2) {
          // 验证ThreeEngine是否初始化
          if (this.ThreeEngine) {
            const lineName = locationCode; // 物流线名称格式为"层数-站台号"
            let endpointCode = '';

            // 处理堆垛机与物流线组合的特例规则
            // 根据堆垛机编号和物流线名称确定正确的端点
            if (ddjNumber && ['1', '2', '3', '4'].includes(ddjNumber)) {
              if (lineName === '1-11') {
                // 堆垛机4在1-11物流线取放货，货物都应在1-11_to
                if (ddjNumber === '4') {
                  endpointCode = lineName + "_to";
                }
              } else if (lineName === '1-12') {
                // 堆垛机1在1-12物流线取放货时货物位置为1-12_from
                // 堆垛机2在1-12取放货为1-12_to
                if (ddjNumber === '1') {
                  endpointCode = lineName + "_from";
                } else if (ddjNumber === '2') {
                  endpointCode = lineName + "_to";
                } else {
                  // 其他堆垛机使用默认规则
                  endpointCode = isPickup ? lineName + "_to" : lineName + "_from";
                }
              } else if (lineName === '1-13') {
                // 堆垛机2在1-13取放货为1-13_from
                // 堆垛机3在1-13取放货为1-13_to
                if (ddjNumber === '2') {
                  endpointCode = lineName + "_from";
                } else if (ddjNumber === '3') {
                  endpointCode = lineName + "_to";
                } else {
                  // 其他堆垛机使用默认规则
                  endpointCode = isPickup ? lineName + "_to" : lineName + "_from";
                }
              } else if (lineName === '1-14') {
                // 堆垛机3在1-14取放货为1-14_from
                // 堆垛机4在1-14取放货为1-14_to
                if (ddjNumber === '3') {
                  endpointCode = lineName + "_from";
                } else if (ddjNumber === '4') {
                  endpointCode = lineName + "_to";
                } else {
                  // 其他堆垛机使用默认规则
                  endpointCode = isPickup ? lineName + "_to" : lineName + "_from";
                }
              } else {
                // 非特殊物流线使用默认规则
                endpointCode = isPickup ? lineName + "_to" : lineName + "_from";
              }
            } else {
              // 没有堆垛机编号或堆垛机编号不在1-4范围内，使用默认规则
              endpointCode = isPickup ? lineName + "_to" : lineName + "_from";
            }

            // 查找物流线对应端点对象
            const endpointObj = this.ThreeEngine.getObjectByName(endpointCode);
            if (endpointObj) {
              // 获取位置坐标
              const x = endpointObj.position.x;
              const y = endpointObj.position.y;
              const z = endpointObj.position.z;
              return { x, y, z };
            }
          }
        } else if (parts.length === 3) {
          // 货位编码解析，优先调用getShelfLocationCoordinates方法
          const shelfLocation = this.getShelfLocationCoordinates(locationCode);
          if (shelfLocation) {
            return shelfLocation;
          } else {
            console.error(`[货物] 无法解析位置编码 ${locationCode}`);
            return null;
          }
        }
      }

      console.error(`货物 - 无法解析位置编码 ${locationCode}`);
      return null;
    },

    /**
     * 兼容旧代码的坐标获取方法
     * @deprecated 建议使用getStackerLocationCoordinates或getCargoLocationCoordinates代替
     */
    getLocationCoordinates(locationCode) {
      console.warn(`getLocationCoordinates方法已过时，建议使用getStackerLocationCoordinates或getCargoLocationCoordinates代替`);
      return this.getStackerLocationCoordinates(locationCode);
    },

    /**
     * 执行任务的通用方法
     * 根据任务类型调用相应的处理方法
     * 
     * @param {string} fromCellCode - 起点位置（格式：层数-站台号，如"1-2"）
     * @param {string} toCellCode - 终点位置（格式：层数-站台号，如"1-5"）
     * @param {string} palletCode - 托盘号
     * @param {string} type - 任务类型（RGV_MOVE 或 DDJ_MOVE）
     * @param {string} [ddjNumber] - 堆垛机编号（仅在 DDJ_MOVE 任务中需要）
     * @description 这是任务执行的通用入口，负责ThreeEngine初始化检查、任务类型路由和参数传递
     */
    executeTask(fromCellCode, toCellCode, palletCode, type, ddjNumber) {
      const that = this;

      // 检查ThreeEngine是否初始化
      if (!that.ThreeEngine) {
        console.error('错误：ThreeEngine未初始化，无法执行任务');
        that.$message.error('系统未初始化，请稍后重试');
        return;
      }

      // 参数验证
      if (!fromCellCode || !toCellCode || !palletCode) {
        console.error('错误：缺少必要的任务参数');
        that.$message.error('缺少必要的任务参数');
        return;
      }

      // 根据任务类型执行相应的任务处理
      try {
        switch (type) {
          case 'RGV_MOVE':
            that.handleRgvMoveTask(fromCellCode, toCellCode, palletCode);
            break;
          case 'DDJ_MOVE':
            if (!ddjNumber) {
              throw new Error('堆垛机任务必须指定堆垛机编号');
            }
            that.handleDdjMoveTask(fromCellCode, toCellCode, palletCode, ddjNumber);
            break;
          default:
            throw new Error(`不支持的任务类型: ${type}`);
        }
      } catch (error) {
        console.error('执行任务时发生错误:', error);
        that.$message.error(`任务执行失败：${error.message}`);
      }
    },



    /**
     * 运行任务测试 - 统一入口，根据任务类型执行相应测试
     * @description 这是任务测试的统一入口，负责参数验证、测试日志记录和调用相应的测试执行方法
     */
    runTaskTest() {
      // 验证通用参数
      if (!this.isTaskParamsValid) {
        console.error('任务测试参数验证失败，终止测试');
        return;
      }

      // 根据任务类型执行测试
      try {
        if (this.taskType === 'RGV_MOVE') {
          this.executeRgvMoveTest();
        } else if (this.taskType === 'DDJ_MOVE') {
          this.executeDdjMoveTest();
        } else {
          throw new Error(`不支持的任务类型: ${this.taskType}`);
        }
      } catch (error) {
        console.error('执行任务测试时发生错误:', error);
        this.$message.error(`测试执行失败：${error.message}`);
      }
    },

    /**
     * 执行RGV移动任务测试
     * @description 负责执行RGV移动任务的测试，包括参数信息输出和调用核心执行逻辑
     */
    executeRgvMoveTest() {
      try {
        // 执行RGV移动任务
        this.executeTask(this.moveFromCell, this.moveToCell, this.palletCode, 'RGV_MOVE');
        this.$message.success('RGV移动任务测试已开始，详细信息请查看控制台日志');
      } catch (error) {
        console.error('执行RGV_MOVE任务测试失败:', error);
        this.$message.error(`测试执行失败：${error.message}`);
      }
    },

    /**
     * 执行堆垛机移动任务测试
     * @description 负责执行堆垛机移动任务的测试，包括参数信息输出、堆垛机初始位置记录和调用核心执行逻辑
     */
    executeDdjMoveTest() {

      try {
        // 执行堆垛机移动任务
        this.executeTask(this.moveFromCell, this.moveToCell, this.palletCode, 'DDJ_MOVE', this.ddjNumber);
        this.$message.success('堆垛机移动任务测试已开始，详细信息请查看控制台日志');
      } catch (error) {
        console.error('执行DDJ_MOVE任务测试失败:', error);
        this.$message.error(`测试执行失败：${error.message}`);
      }
    },

    /**
     * 处理DDJ_MOVE任务
     * 堆垛机取货时，首先确定移动的堆垛机，然后将对应的躯干移动到与要取得货物的x坐标一致，
     * 同时对应的载货台需要移动到比货物y坐标低0.5的位置，之后将货物移动到载货台上y坐标多0.5的位置，放货时同理
     * 
     * @param {string} fromCellCode - 起点位置编码
     * @param {string} toCellCode - 终点位置编码
     * @param {string} palletCode - 托盘编号
     * @param {string} ddjNumber - 堆垛机编号
     */
    handleDdjMoveTask(fromCellCode, toCellCode, palletCode, ddjNumber) {
      var that = this;

      // 根据堆垛机编号查找对应的堆垛机对象
      const ddjObj = this.ThreeEngine.getObjectByName(`堆垛机${ddjNumber}`);
      if (!ddjObj) {
        console.error(`未找到堆垛机 ${ddjNumber} 的对象`);
        this.$message.error(`未找到堆垛机 ${ddjNumber} 的对象`);
        return;
      }

      // 查找对应的躯干对象
      const torsoObj = this.ThreeEngine.getObjectByName(`躯干${ddjNumber}`);
      if (!torsoObj) {
        console.error(`未找到堆垛机 ${ddjNumber} 的躯干对象`);
        this.$message.error(`未找到堆垛机 ${ddjNumber} 的躯干对象`);
        return;
      }

      // 查找对应的载货台对象
      const cargoPlatformObj = this.ThreeEngine.getObjectByName(`载货台${ddjNumber}`);
      if (!cargoPlatformObj) {
        console.error(`未找到堆垛机 ${ddjNumber} 的载货台对象`);
        this.$message.error(`未找到堆垛机 ${ddjNumber} 的载货台对象`);
        return;
      }

      // 特殊处理：当堆垛机从2-4或4-4物流线取货时
      // 需要先在from位置创建货物，然后让货物移动到to位置
      // 之后再执行堆垛机的移动操作
      const specialLogisticsLines = ['2-4', '4-4'];
      const isSpecialCase = specialLogisticsLines.includes(fromCellCode);

      if (isSpecialCase) {
        // 获取起点物流线的from和to端点坐标
        // 货物需要在from端点创建，然后移动到to端点
        const lineName = fromCellCode;
        const fromEndpoint = that.ThreeEngine.getObjectByName(lineName + '_from');
        const toEndpoint = that.ThreeEngine.getObjectByName(lineName + '_to');

        if (!fromEndpoint || !toEndpoint) {
          console.error(`无法获取物流线 ${lineName} 的端点对象`);
          this.$message.error(`无法获取物流线 ${lineName} 的端点对象`);
          return;
        }

        // 检查货物是否已经存在
        let existingPallet = that.ThreeEngine.getObjectByName(palletCode);
        if (existingPallet) {
          that.ThreeEngine.removeByName(palletCode);
        }

        // 在物流线的from端点位置创建货物
        const cargoCreatePosition = {
          x: fromEndpoint.position.x,
          y: fromEndpoint.position.y,
          z: fromEndpoint.position.z
        };
        const pallet = that.pallet(palletCode, { position: cargoCreatePosition });
        if (!pallet) {
          console.error(`货物 ${palletCode} 创建失败`);
          this.$message.error(`货物 ${palletCode} 创建失败`);
          return;
        }

        // 创建货物移动到to端点的动画
        const cargoMoveDuration = 3200; // 进一步降低货物移动速度，增加移动时间（原来1600ms）
        const cargoToEndpointPosition = {
          x: toEndpoint.position.x,
          y: toEndpoint.position.y,
          z: toEndpoint.position.z
        };

        const cargoMoveTween = new TWEEN.Tween(pallet.position)
          .to(cargoToEndpointPosition, cargoMoveDuration)
          .easing(TWEEN.Easing.Quadratic.InOut)
          .onComplete(() => {
            // 货物到达to端点后，重新计算起点坐标并执行堆垛机移动
            // 注意：这里的起点坐标应该是物流线的to端点坐标
            // 堆垛机移动使用堆垛机专属坐标
            // 货物移动使用货物专属坐标
            // 起点是取货操作，传入isPickup=true；终点是放货操作，传入isPickup=false
            const stackerFromPosition = this.getStackerLocationCoordinates(fromCellCode, true, ddjNumber);
            const stackerToPosition = this.getStackerLocationCoordinates(toCellCode, false, ddjNumber);
            // 货物起点坐标应该是物流线to端点的坐标
            const cargoFromPosition = {
              x: toEndpoint.position.x,
              y: toEndpoint.position.y,
              z: toEndpoint.position.z
            };
            const cargoToPosition = this.getCargoLocationCoordinates(toCellCode, false, ddjNumber);

            if (!stackerFromPosition || !stackerToPosition || !cargoFromPosition || !cargoToPosition) {
              console.error('无法获取起点或终点位置坐标');
              this.$message.error('无法获取起点或终点位置坐标');
              return;
            }

            // 堆垛机移动的动画持续时间设置
            // 根据isTest值设置堆垛机和载货台的动画持续时间
            let torsoMoveDuration1, torsoMoveDuration2, platformMoveDuration1, platformMoveDuration2;
            let torsoMoveDistance1, torsoMoveDistance2, platformMoveDistance1, platformMoveDistance2;

            if (this.isTest) {
              // 测试模式：设置固定的动画持续时间
              torsoMoveDuration1 = 500; // 躯干第一次移动持续时间（毫秒）
              torsoMoveDuration2 = 500; // 躯干第二次移动持续时间（毫秒）
              platformMoveDuration1 = 200; // 载货台第一次移动持续时间（毫秒）
              platformMoveDuration2 = 200; // 载货台第二次移动持续时间（毫秒）

              // 为了避免后续代码访问未定义的距离变量，在测试模式下也计算这些距离
              torsoMoveDistance1 = Math.abs(stackerFromPosition.x - torsoObj.position.x);
              torsoMoveDistance2 = Math.abs(stackerToPosition.x - stackerFromPosition.x);
              platformMoveDistance1 = Math.abs((cargoFromPosition.y - 0.3) - cargoPlatformObj.position.y);
              platformMoveDistance2 = Math.abs((stackerToPosition.y - 0.3) - (cargoFromPosition.y - 0.3));

            } else {
              // 生产模式：根据距离和速度计算动画持续时间
              // 定义移动速度（单位：距离/毫秒）
              const torsoSpeed = 0.001; // 降低躯干移动速度（原来0.002）
              const platformSpeed = 0.0002; // 再次降低载货台移动速度（原来0.0005）

              // 计算堆垛机x方向移动距离和持续时间
              torsoMoveDistance1 = Math.abs(stackerFromPosition.x - torsoObj.position.x);
              torsoMoveDistance2 = Math.abs(stackerToPosition.x - stackerFromPosition.x);
              torsoMoveDuration1 = Math.max(500, torsoMoveDistance1 / torsoSpeed); // 最小持续时间500ms
              torsoMoveDuration2 = Math.max(500, torsoMoveDistance2 / torsoSpeed); // 最小持续时间500ms

              // 计算载货台y方向移动距离和持续时间
              platformMoveDistance1 = Math.abs((cargoFromPosition.y - 0.3) - cargoPlatformObj.position.y);
              platformMoveDistance2 = Math.abs((stackerToPosition.y - 0.3) - (cargoFromPosition.y - 0.3));
              platformMoveDuration1 = Math.max(500, platformMoveDistance1 / platformSpeed); // 增加最小持续时间到500ms
              platformMoveDuration2 = Math.max(500, platformMoveDistance2 / platformSpeed); // 增加最小持续时间到500ms
            }

            // 1. 躯干移动到起点货物的x坐标位置，同时载货台x坐标跟随躯干移动
            const torsoTween1 = new TWEEN.Tween(torsoObj.position)
              .to({ x: stackerFromPosition.x }, torsoMoveDuration1)
              .easing(TWEEN.Easing.Quadratic.InOut)
              .onStart(() => {
                console.log(`【步骤1/8】堆垛机躯干开始移动到取货起点，移动距离: ${torsoMoveDistance1.toFixed(2)}, 持续时间: ${torsoMoveDuration1.toFixed(0)}ms`);
              })
              .onUpdate(() => {
                // 实时更新载货台的x坐标，使其跟随躯干移动
                cargoPlatformObj.position.x = torsoObj.position.x;
              })
              .onComplete(() => {
                console.log('【步骤1/8】堆垛机移动到取货起点完成');
              });

            // 2. 载货台移动到取货位置
            const platformTween1 = new TWEEN.Tween(cargoPlatformObj.position)
              .to({ y: cargoFromPosition.y - 0.3 }, platformMoveDuration1)
              .easing(TWEEN.Easing.Quadratic.InOut)
              .onStart(() => {
                console.log(`【步骤2/8】堆垛机载货台开始调整到取货位置，移动距离: ${platformMoveDistance1.toFixed(2)}, 持续时间: ${platformMoveDuration1.toFixed(0)}ms`);
              })
              .onComplete(() => {
                console.log('【步骤2/8】堆垛机载货台调整到取货位置完成');
                // 解析货叉伸出方向
                const fromForkDirection = this.parseShelfNumber(fromCellCode);
                // 载货台到位后，先执行货叉伸出动画
                console.log('【步骤3/8】开始伸出货叉');
                this.executeForkExtendAnimation(ddjNumber, () => {
                  console.log('【步骤3/8】货叉伸出完成');
                  // 货叉伸出完成后，同时执行货物移动到载货台和货叉收回的动画
                  console.log('【步骤4/8】开始同时执行货物移动和货叉收回');
                  // 同时启动货物移动和货叉收回动画
                  cargoTween1.start();
                  this.executeForkRetractAnimation(ddjNumber, () => {
                    console.log('【步骤4/8】货叉收回完成');
                  }, cargoMoveDuration); // 货叉收回动画使用与货物移动相同的持续时间
                }, 3200, fromForkDirection);
              });

            // 3. 货物移动到载货台动画
            const cargoTween1 = new TWEEN.Tween(pallet.position)
              .to(
                {
                  z: cargoPlatformObj.position.z  // 货物z坐标移动到载货台z坐标位置
                },
                cargoMoveDuration
              )
              .easing(TWEEN.Easing.Quadratic.InOut)
              .onStart(() => {
                console.log('【步骤4/8】货物开始移动到载货台');
              })
              .onComplete(() => {
                console.log('【步骤4/8】货物移动到载货台完成，开始堆垛机移动到放货终点');
                // 货物移动完成后，执行堆垛机移动到放货终点
                torsoTween2.start();
              });

            // 4. 堆垛机移动到放货终点
            const torsoTween2 = new TWEEN.Tween(torsoObj.position)
              .to({ x: stackerToPosition.x }, torsoMoveDuration2)
              .easing(TWEEN.Easing.Quadratic.InOut)
              .onStart(() => {
                console.log(`【步骤5/8】堆垛机开始移动到放货终点，移动距离: ${torsoMoveDistance2.toFixed(2)}, 持续时间: ${torsoMoveDuration2.toFixed(0)}ms`);
              })
              .onUpdate(() => {
                // 实时更新载货台和货物的x坐标，使其跟随躯干移动
                cargoPlatformObj.position.x = torsoObj.position.x;
                pallet.position.x = torsoObj.position.x;
              })
              .onComplete(() => {
                console.log('【步骤5/8】堆垛机移动到放货终点完成');
                // 堆垛机到位后，载货台移动到放货位置
                platformTween2.start();
              });

            // 5. 载货台移动到放货位置
            const platformTween2 = new TWEEN.Tween(cargoPlatformObj.position)
              .to({ y: stackerToPosition.y - 0.3 }, platformMoveDuration2)
              .easing(TWEEN.Easing.Quadratic.InOut)
              .onStart(() => {
                console.log(`【步骤6/8】载货台开始移动到放货位置，移动距离: ${platformMoveDistance2.toFixed(2)}, 持续时间: ${platformMoveDuration2.toFixed(0)}ms`);
              })
              .onUpdate(() => {
                // 实时更新货物的y坐标，使其跟随载货台移动
                // 货物在载货台上方0.3单位
                pallet.position.y = cargoPlatformObj.position.y + 0.3;
              })
              .onComplete(() => {
                console.log('【步骤6/8】载货台移动到放货位置完成');
                // 解析货叉伸出方向
                const toForkDirection = this.parseShelfNumber(toCellCode);
                // 载货台到位后，货叉伸出和货物移动同时执行
                console.log('【步骤7/8】货叉伸出和货物移动同时开始');
                this.executeForkExtendAnimation(ddjNumber, () => {
                  console.log('【步骤7/8】货叉伸出完成');
                }, 3200, toForkDirection);
                // 同时启动货物移动动画
                cargoTween2.start();
              });

            // 6. 货物移动到放货位置
            const cargoTween2 = new TWEEN.Tween(pallet.position)
              .to(
                {
                  x: cargoToPosition.x, // 货物x坐标与终点位置x坐标一致
                  y: cargoToPosition.y, // 货物y坐标与终点位置y坐标一致
                  z: cargoToPosition.z // 货物z坐标与终点位置z坐标一致
                },
                cargoMoveDuration
              )
              .easing(TWEEN.Easing.Quadratic.InOut)
              .onStart(() => {
                console.log('【步骤7/8】货物开始移动到放货位置');
              })
              .onComplete(() => {
                console.log('【步骤7/8】货物到达放货位置完成');
                // 货物到位后，执行货叉收回动画
                console.log('【步骤8/8】开始收回货叉');
                this.executeForkRetractAnimation(ddjNumber, () => {
                  console.log('【步骤8/8】货叉收回完成，所有取放货操作完成');
                });

                // 检查终点位置是否是物流线（格式：数字-数字），如果是则再添加一步动画让货物到达另一端
                const needMoveLines = ["1-12", "1-13", "1-14"];
                if (needMoveLines.includes(toCellCode)) {
                  const lineName = toCellCode; // 物流线名称
                  const currentEndpoint = cargoToPosition.x === this.ThreeEngine.getObjectByName(lineName + '_from')?.position.x &&
                    cargoToPosition.y === this.ThreeEngine.getObjectByName(lineName + '_from')?.position.y &&
                    cargoToPosition.z === this.ThreeEngine.getObjectByName(lineName + '_from')?.position.z ? '_from' : '_to';
                  const otherEndpoint = currentEndpoint === '_from' ? '_to' : '_from';
                  const endpointCode = lineName + otherEndpoint;
                  const endpointObj = that.ThreeEngine.getObjectByName(endpointCode);
                  if (endpointObj) {
                    const targetPosition = {
                      x: endpointObj.position.x,
                      y: endpointObj.position.y,
                      z: endpointObj.position.z
                    };
                    const cargoTween3 = new TWEEN.Tween(pallet.position)
                      .to(targetPosition, cargoMoveDuration)
                      .easing(TWEEN.Easing.Quadratic.InOut)
                      .onStart(() => {
                      })
                      .onComplete(() => {
                      })
                      .start();
                  } else {
                    console.error(`未找到物流线 ${lineName} 的终点对象 ${endpointCode}`);
                  }
                }
              });

            // 开始动画
            torsoTween1.chain(platformTween1);

            // 移除自动链接，使用回调函数手动控制动画流程
            // torsoTween1.chain(platformTween1);
            // platformTween1.chain(cargoTween1);
            // cargoTween1.chain(torsoTween2);
            // torsoTween2.chain(platformTween2);
            // platformTween2.chain(cargoTween2);

            // 开始动画
            torsoTween1.start();
          })
          // 开始货物移动到to端点的动画
          .start();

        // 确保动画系统在运行
        if (!this.requestId) {
          this.animate();
        }

        return; // 特殊处理逻辑完成，返回函数
      }

      // 普通处理：非2-4或4-4物流线取货
      // 获取起点和终点位置的坐标
      // 堆垛机移动使用堆垛机专属坐标
      // 货物移动使用货物专属坐标
      // 起点是取货操作，传入isPickup=true；终点是放货操作，传入isPickup=false
      const stackerFromPosition = this.getStackerLocationCoordinates(fromCellCode, true, ddjNumber);
      const stackerToPosition = this.getStackerLocationCoordinates(toCellCode, false, ddjNumber);
      const cargoFromPosition = this.getCargoLocationCoordinates(fromCellCode, true, ddjNumber);
      const cargoToPosition = this.getCargoLocationCoordinates(toCellCode, false, ddjNumber);

      if (!stackerFromPosition || !stackerToPosition || !cargoFromPosition || !cargoToPosition) {
        console.error('无法获取起点或终点位置坐标');
        this.$message.error('无法获取起点或终点位置坐标');
        return;
      }

      // 检查货物是否已经存在
      let existingPallet = that.ThreeEngine.getObjectByName(palletCode);
      if (existingPallet) {
        that.ThreeEngine.removeByName(palletCode);
      }

      // 在起点位置创建货物
      const pallet = that.pallet(palletCode, { position: cargoFromPosition });
      if (!pallet) {
        console.error(`货物 ${palletCode} 创建失败`);
        this.$message.error(`货物 ${palletCode} 创建失败`);
        return;
      }
      // 定义堆垛机取放货的动画步骤
      // 1. 躯干移动到起点货物的x坐标位置，同时载货台x坐标跟随躯干移动
      // 2. 载货台移动到起点货物y坐标位置（修复偏移问题，移除减0.5的偏移）
      // 3. 货物移动到载货台上（坐标与载货台一致）
      // 4. 躯干移动到终点位置的x坐标位置，同时载货台和货物的x坐标跟随躯干移动
      // 5. 载货台移动到终点位置y坐标位置（修复偏移问题，移除减0.5的偏移）
      // 6. 货物移动到终点位置

      // 动画持续时间设置
      // 货物移动时间保持固定不变
      const cargoMoveDuration = 3200; // 进一步降低货物移动速度，增加移动时间（原来1600ms）

      // 根据isTest值设置堆垛机和载货台的动画持续时间
      let torsoMoveDuration1, torsoMoveDuration2, platformMoveDuration1, platformMoveDuration2;
      let torsoMoveDistance1, torsoMoveDistance2, platformMoveDistance1, platformMoveDistance2;

      if (this.isTest) {
        // 测试模式：设置固定的动画持续时间
        torsoMoveDuration1 = 500; // 躯干第一次移动持续时间（毫秒）
        torsoMoveDuration2 = 500; // 躯干第二次移动持续时间（毫秒）
        platformMoveDuration1 = 200; // 载货台第一次移动持续时间（毫秒）
        platformMoveDuration2 = 200; // 载货台第二次移动持续时间（毫秒）

        // 为了避免后续代码访问未定义的距离变量，在测试模式下也计算这些距离
        torsoMoveDistance1 = Math.abs(stackerFromPosition.x - torsoObj.position.x);
        torsoMoveDistance2 = Math.abs(stackerToPosition.x - stackerFromPosition.x);
        platformMoveDistance1 = Math.abs((cargoFromPosition.y - 0.3) - cargoPlatformObj.position.y);
        platformMoveDistance2 = Math.abs((stackerToPosition.y - 0.3) - (cargoFromPosition.y - 0.3));

      } else {
        // 生产模式：根据距离和速度计算动画持续时间
        // 定义移动速度（单位：距离/毫秒）
        const torsoSpeed = 0.001; // 躯干移动速度保持不变
        const platformSpeed = 0.001; // 提高载货台移动速度（从0.0002改为0.001）

        // 计算堆垛机x方向移动距离和持续时间
        torsoMoveDistance1 = Math.abs(stackerFromPosition.x - torsoObj.position.x);
        torsoMoveDistance2 = Math.abs(stackerToPosition.x - stackerFromPosition.x);
        torsoMoveDuration1 = Math.max(500, torsoMoveDistance1 / torsoSpeed); // 最小持续时间500ms
        torsoMoveDuration2 = Math.max(500, torsoMoveDistance2 / torsoSpeed); // 最小持续时间500ms

        // 计算载货台y方向移动距离和持续时间
        platformMoveDistance1 = Math.abs((cargoFromPosition.y - 0.3) - cargoPlatformObj.position.y);
        platformMoveDistance2 = Math.abs((stackerToPosition.y - 0.3) - (cargoFromPosition.y - 0.3));
        platformMoveDuration1 = Math.max(200, platformMoveDistance1 / platformSpeed); // 降低最小持续时间到200ms
        platformMoveDuration2 = Math.max(200, platformMoveDistance2 / platformSpeed); // 降低最小持续时间到200ms
      }

      // 1. 躯干移动到起点货物的x坐标位置，同时载货台x坐标跟随躯干移动
      const torsoTween1 = new TWEEN.Tween(torsoObj.position)
        .to({ x: stackerFromPosition.x }, torsoMoveDuration1)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`【步骤1/8】堆垛机躯干开始移动到取货起点，移动距离: ${torsoMoveDistance1.toFixed(2)}, 持续时间: ${torsoMoveDuration1.toFixed(0)}ms`);
        })
        .onUpdate(() => {
          // 实时更新载货台的x坐标，使其跟随躯干移动
          cargoPlatformObj.position.x = torsoObj.position.x;
        })
        .onComplete(() => {
          console.log('【步骤1/8】堆垛机移动到取货起点完成');
        });

      // 2. 载货台移动到取货位置
      const platformTween1 = new TWEEN.Tween(cargoPlatformObj.position)
        .to({ y: cargoFromPosition.y - 0.3 }, platformMoveDuration1)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`【步骤2/8】堆垛机载货台开始调整到取货位置，移动距离: ${platformMoveDistance1.toFixed(2)}, 持续时间: ${platformMoveDuration1.toFixed(0)}ms`);
        })
        .onComplete(() => {
          console.log('【步骤2/8】堆垛机载货台调整到取货位置完成');
          // 解析货叉伸出方向
          const fromForkDirection = this.parseShelfNumber(fromCellCode);
          // 载货台到位后，先执行货叉伸出动画
          console.log('【步骤3/8】开始伸出货叉');
          this.executeForkExtendAnimation(ddjNumber, () => {
            console.log('【步骤3/8】货叉伸出完成');
            // 货叉伸出完成后，同时执行货物随货叉一起收回和货叉收回的动画
            console.log('【步骤4/8】开始同时执行货物移动和货叉收回');
            // 同时启动货物收回和货叉收回动画
            cargoTween1.start();
            startForkRetract();
          }, 3200, fromForkDirection);
        });

      // 3. 起点货物随着货叉一起收回（与货叉收回动画同时执行）
      let cargoTween1Completed = false;
      let forkRetractCompleted = false;

      const checkBothAnimationsComplete = () => {
        if (cargoTween1Completed && forkRetractCompleted) {
          console.log('【步骤4/8】货物和货叉都收回完成，开始堆垛机移动到放货终点');
          // 两者都完成后，执行堆垛机移动到放货终点
          torsoTween2.start();
        }
      };

      const cargoTween1 = new TWEEN.Tween(pallet.position)
        .to(
          {
            z: cargoPlatformObj.position.z  // 货物z坐标移动到载货台z坐标位置
          },
          cargoMoveDuration
        )
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log('【步骤4/8】起点货物开始随着货叉收回');
        })
        .onComplete(() => {
          console.log('【步骤4/8】起点货物随着货叉收回完成');
          cargoTween1Completed = true;
          checkBothAnimationsComplete();
        });

      // 货叉收回动画函数（与货物收回动画同时执行）
      const startForkRetract = () => {
        this.executeForkRetractAnimation(ddjNumber, () => {
          console.log('【步骤4/8】货叉收回完成');
          forkRetractCompleted = true;
          checkBothAnimationsComplete();
        });
      };

      // 4. 堆垛机移动到放货终点
      const torsoTween2 = new TWEEN.Tween(torsoObj.position)
        .to({ x: stackerToPosition.x }, torsoMoveDuration2)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`【步骤5/8】堆垛机开始移动到放货终点，移动距离: ${torsoMoveDistance2.toFixed(2)}, 持续时间: ${torsoMoveDuration2.toFixed(0)}ms`);
        })
        .onUpdate(() => {
          // 实时更新载货台和货物的x坐标，使其跟随躯干移动
          cargoPlatformObj.position.x = torsoObj.position.x;
          pallet.position.x = torsoObj.position.x;
        })
        .onComplete(() => {
          console.log('【步骤5/8】堆垛机移动到放货终点完成');
          // 堆垛机到位后，载货台移动到放货位置
          platformTween2.start();
        });

      // 5. 载货台移动到放货位置
      const platformTween2 = new TWEEN.Tween(cargoPlatformObj.position)
        .to({ y: stackerToPosition.y - 0.3 }, platformMoveDuration2)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`【步骤6/8】载货台开始移动到放货位置，移动距离: ${platformMoveDistance2.toFixed(2)}, 持续时间: ${platformMoveDuration2.toFixed(0)}ms`);
        })
        .onUpdate(() => {
          // 实时更新货物的y坐标，使其跟随载货台移动
          // 货物在载货台上方0.3单位
          pallet.position.y = cargoPlatformObj.position.y + 0.3;
        })
        .onComplete(() => {
          console.log('【步骤6/8】载货台移动到放货位置完成');
          // 解析货叉伸出方向
          const toForkDirection = this.parseShelfNumber(toCellCode);
          // 载货台到位后，货叉伸出和货物移动同时执行
          console.log('【步骤7/8】货叉伸出和货物移动同时开始');
          this.executeForkExtendAnimation(ddjNumber, () => {
            console.log('【步骤7/8】货叉伸出完成');
          }, 3200, toForkDirection);
          // 同时启动货物移动动画
          cargoTween2.start();
        });

      // 6. 货物移动到放货位置
      const cargoTween2 = new TWEEN.Tween(pallet.position)
        .to(
          {
            x: cargoToPosition.x, // 货物x坐标与终点位置x坐标一致
            y: cargoToPosition.y, // 货物y坐标与终点位置y坐标一致
            z: cargoToPosition.z // 货物z坐标与终点位置z坐标一致
          },
          cargoMoveDuration
        )
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log('【步骤7/8】货物开始移动到放货位置');
        })
        .onComplete(() => {
          console.log('【步骤7/8】货物到达放货位置完成');
          // 货物到位后，执行货叉收回动画
          console.log('【步骤8/8】开始收回货叉');
          this.executeForkRetractAnimation(ddjNumber, () => {
            console.log('【步骤8/8】货叉收回完成，所有取放货操作完成');
          });

          // 检查终点位置是否是物流线（格式：数字-数字），如果是则再添加一步动画让货物到达另一端
          const needMoveLines = ["1-12", "1-13", "1-14"];
          if (needMoveLines.includes(toCellCode)) {
            const lineName = toCellCode; // 物流线名称
            const currentEndpoint = cargoToPosition.x === this.ThreeEngine.getObjectByName(lineName + '_from')?.position.x &&
              cargoToPosition.y === this.ThreeEngine.getObjectByName(lineName + '_from')?.position.y &&
              cargoToPosition.z === this.ThreeEngine.getObjectByName(lineName + '_from')?.position.z ? '_from' : '_to';
            const otherEndpoint = currentEndpoint === '_from' ? '_to' : '_from';
            const endpointCode = lineName + otherEndpoint;
            const endpointObj = that.ThreeEngine.getObjectByName(endpointCode);
            if (endpointObj) {
              const targetPosition = {
                x: endpointObj.position.x,
                y: endpointObj.position.y,
                z: endpointObj.position.z
              };
              const cargoTween3 = new TWEEN.Tween(pallet.position)
                .to(targetPosition, cargoMoveDuration)
                .easing(TWEEN.Easing.Quadratic.InOut)
                .onStart(() => {
                })
                .onComplete(() => {
                })
                .start();
            } else {
              console.error(`未找到物流线 ${lineName} 的终点对象 ${endpointCode}`);
            }
          }
        });

      // 开始动画
      torsoTween1.chain(platformTween1);

      // 移除自动链接，使用回调函数手动控制动画流程
      // torsoTween1.chain(platformTween1);
      // platformTween1.chain(cargoTween1);
      // cargoTween1.chain(torsoTween2);
      // torsoTween2.chain(platformTween2);
      // platformTween2.chain(cargoTween2);

      // 开始动画
      torsoTween1.start();

      // 确保动画系统在运行
      if (!this.requestId) {
        this.animate();
      }
    },


    /**
     * 处理RGV移动任务
     * @param {string} fromCellCode - 起点位置（格式：层数-站台号，如"1-2"）
     * @param {string} toCellCode - 终点位置（格式：层数-站台号，如"1-5"）
     * @param {string} palletCode - 托盘号
     */
    handleRgvMoveTask(fromCellCode, toCellCode, palletCode) {
      var that = this;

      // 验证参数格式
      if (!/^\d+-\d+$/.test(fromCellCode) || !/^\d+-\d+$/.test(toCellCode)) {
        console.error('起点或终点格式错误，应为"层数-站台号"格式，如"1-2"'); 577
        return;
      }

      if (!palletCode) {
        console.error('托盘号不能为空');
        return;
      }

      // 1. 获取起点物流线的起点和终点位置
      const fromLineName = fromCellCode;
      let fromLineFrom = fromCellCode + "_from";
      let fromLineTo = fromCellCode + "_to";
      if (fromCellCode === "1-11" || fromCellCode === "1-25" || fromCellCode === "1-24") {
        fromLineFrom = fromCellCode + "_to"; // 对于特定物流线，交换起点和终点
        fromLineTo = fromCellCode + "_from";
      }

      const fromLineFromPos = that.ThreeEngine.getObjectByName(fromLineFrom);
      const fromLineToPos = that.ThreeEngine.getObjectByName(fromLineTo);

      if (!fromLineFromPos || !fromLineToPos) {
        console.error(`未找到起点物流线 ${fromLineName} 的起点或终点位置`);
        return;
      }

      // 2. 获取终点物流线的起点和终点位置
      const toLineName = toCellCode;
      let toLineFrom = toCellCode + "_from";
      let toLineTo = toCellCode + "_to";
      if (toCellCode === "1-22") {
        toLineFrom = toCellCode + "_to"; // 对于特定物流线，交换起点和终点
        toLineTo = toCellCode + "_from";
      }

      const toLineFromPos = that.ThreeEngine.getObjectByName(toLineFrom);
      const toLineToPos = that.ThreeEngine.getObjectByName(toLineTo);

      if (!toLineFromPos || !toLineToPos) {
        console.error(`未找到终点物流线 ${toLineName} 的起点或终点位置`);
        return;
      }

      // 3. 根据物流线自动选择使用的RGV
      // 定义各物流线对应的RGV
      const lineToRgvMap = {
        // RGV1对应的物流线
        '1-2': 'rgv1',
        '1-3': 'rgv1',
        '1-5': 'rgv1',
        '1-6': 'rgv1',
        '1-7': 'rgv1',
        '1-8': 'rgv1',
        '1-9': 'rgv1',
        '1-10': 'rgv1',
        '1-11': 'rgv1',
        '1-25': 'rgv1',
        // RGV2对应的物流线
        '1-16': 'rgv2',
        '1-17': 'rgv2',
        '1-18': 'rgv2',
        '1-19': 'rgv2',
        '1-20': 'rgv2',
        '1-21': 'rgv2',
        '1-22': 'rgv2',
        '1-24': 'rgv2',
        // RGV2对应的物流线
        '3-2': 'rgv3',
        '3-3': 'rgv3',
        '3-5': 'rgv3',
        '3-6': 'rgv3',
        '3-7': 'rgv3',
        '3-8': 'rgv3',
        '3-9': 'rgv3',
        '3-10': 'rgv3',
        '3-11': 'rgv3',
        '3-12': 'rgv3'
      };

      // 确定使用的RGV
      let rgvName = lineToRgvMap[fromCellCode] || lineToRgvMap[toCellCode] || 'rgv1';

      // 获取对应的RGV对象
      if (this.rgvMovement) {
        console.log(`rgvMovement中的rgvs:`, Array.from(this.rgvMovement.rgvs.keys()));
      }
      const rgv = this.rgvMovement ? this.rgvMovement.getRgv(rgvName) : null;
      if (!rgv) {
        console.error(`未找到${rgvName}对象`);
        return;
      }

      // 获取RGV的有效固定位置，验证是否可用
      const validDestinations = this.rgvMovement.rgvValidDestinations[rgvName];
      if (!validDestinations || !Array.isArray(validDestinations) || validDestinations.length === 0) {
        console.error(`RGV移动任务失败: ${rgvName} 没有配置有效的固定位置`);
        return;
      }

      // 4. 根据物流线名称和RGV类型确定取货固定位置
      // 物流线与RGV固定位置的映射关系（按RGV类型区分）
      const lineToRgvPosMap = {
        // RGV1的映射关系
        rgv1: {
          '1-2': 'LINE2_OUT_1FF',  // 1-2物流线取放货时使用LINE2_OUT_1FF位置
          '1-3': 'LINE3_IN_1FF',   // 1-3物流线取放货时使用LINE3_IN_1FF位置
          '1-5': 'LINE1_IN_1FF',   // 1-5物流线取放货时使用LINE1_IN_1FF位置
          '1-6': 'LINE1_OUT_1FF',  // 1-6物流线取放货时使用LINE1_OUT_1FF位置
          '1-7': 'LINE2_IN_1FF',   // 1-7物流线取放货时使用LINE1_IN_1FF位置
          '1-8': 'LINE2_OUT_1FF',  // 1-8物流线取放货时使用LINE1_OUT_1FF位置
          '1-9': 'LINE3_IN_1FF',   // 1-9物流线取放货时使用LINE1_IN_1FF位置
          '1-10': 'LINE3_OUT_1FF', // 1-10物流线取放货时使用LINE1_OUT_1FF位置
          '1-11': 'LINE4_1FF',     // 1-11物流线取放货时使用LINE1_1FF位置
          '1-25': 'OUT_1FF'        // 1-25物流线取放货时使用OUT_1FF位置
        },
        // RGV2的映射关系
        rgv2: {
          '1-16': 'LINE1_IN_1FB',   // 1-16物流线取放货时使用LINE1_IN_1FB位置
          '1-17': 'LINE1_OUT_1FB',  // 1-17物流线取放货时使用LINE1_OUT_1FB位置
          '1-18': 'LINE2_IN_1FB',   // 1-18物流线取放货时使用LINE2_IN_1FB位置
          '1-19': 'LINE2_OUT_1FB',  // 1-19物流线取放货时使用LINE2_OUT_1FB位置
          '1-20': 'LINE3_IN_1FB',   // 1-20物流线取放货时使用LINE3_IN_1FB位置
          '1-21': 'LINE3_OUT_1FB',  // 1-21物流线取放货时使用LINE3_OUT_1FB位置
          '1-22': 'LINE4_1FB',      // 1-22物流线取放货时使用LINE4_1FB位置
          '1-24': 'OUT_1FB'         // 1-24物流线取放货时使用OUT_1FB位置
        },
        // RGV3的映射关系
        rgv3: {
          '3-2': 'LINE2_OUT_3F',   // 3-2物流线取放货时使用LINE1_IN_1FF位置
          '3-3': 'LINE3_IN_3F',  // 3-3物流线取放货时使用LINE1_OUT_1FF位置
          '3-5': 'LINE1_IN_3F',   // 3-5物流线取放货时使用LINE2_IN_1FF位置
          '3-6': 'LINE1_OUT_3F',  // 3-6物流线取放货时使用LINE2_OUT_1FF位置
          '3-7': 'LINE2_IN_3F',   // 3-7物流线取放货时使用LINE3_IN_1FF位置
          '3-8': 'LINE2_OUT_3F',  // 3-8物流线取放货时使用LINE3_OUT_1FF位置
          '3-9': 'LINE3_IN_3F',   // 3-9物流线取放货时使用LINE4_IN_1FF位置
          '3-10': 'LINE3_OUT_3F', // 3-10物流线取放货时使用LINE4_OUT_1FF位置
          '3-11': 'LINE4_IN_3F',     // 3-11物流线取放货时使用LINE4_IN_3F位置
          '3-12': 'LINE4_OUT_3F'        // 3-12物流线取放货时使用OUT_1FF位置
        }
      };

      // 获取当前RGV的映射表
      const currentRgvMap = lineToRgvPosMap[rgvName] || {};

      // 从起点物流线名称获取对应的固定RGV位置
      let fromRgvPosName;
      // 检查是否是当前RGV映射表中的物流线
      if (currentRgvMap[fromCellCode]) {
        fromRgvPosName = currentRgvMap[fromCellCode];
      } else {
        // 其他物流线使用最近位置算法
        fromRgvPosName = this.rgvMovement.findNearestPosition(rgvName, fromLineToPos.position);
      }

      let rgvFirstPos;
      if (fromRgvPosName) {
        rgvFirstPos = this.rgvMovement.getDestinationPosition(fromRgvPosName);
      } else {
        // 如果找不到对应位置，直接移动到物流线接触位置
        rgvFirstPos = fromLineToPos.position;
      }

      // 验证RGV起始位置是否有效
      if (!rgvFirstPos) {
        console.error(`RGV移动任务失败: 无法获取${rgvName}的起始位置`);
        return;
      }

      // 5. 根据终点物流线名称确定RGV的放货固定位置
      let toRgvPosName;
      // 检查是否是当前RGV映射表中的物流线
      if (currentRgvMap[toCellCode]) {
        toRgvPosName = currentRgvMap[toCellCode];
      } else {
        // 其他物流线使用最近位置算法
        toRgvPosName = this.rgvMovement.findNearestPosition(rgvName, toLineFromPos.position);
      }

      let rgvLastPos;
      if (toRgvPosName) {
        rgvLastPos = this.rgvMovement.getDestinationPosition(toRgvPosName);
      } else {
        // 如果找不到对应位置，直接移动到物流线接触位置
        rgvLastPos = toLineFromPos.position;
      }

      // 验证RGV终点位置是否有效
      if (!rgvLastPos) {
        console.error(`RGV移动任务失败: 无法获取${rgvName}的终点位置`);
        return;
      }

      // 4. 在起点物流线的起点创建货物
      const pallet = that.pallet(palletCode, fromLineFromPos);
      if (!pallet) {
        console.error(`货物 ${palletCode} 创建失败`);
        return;
      }

      // 动画持续时间设置
      const moveDuration = 2000; // 降低货物移动到物流线终点的速度，增加移动时间（原来1000ms）
      const rgvMoveDuration = 5000; // 提高RGV移动速度，减少移动时间（从6000ms改为3000ms）
      const loadUnloadDuration = 500; // 货物加载/卸载到RGV的时间（加快速度）

      // 5. 货物从起点移动到物流线终点
      let tween1;
      // 对于物流线1-25或1-24，货物移动需要先经过中间点1-25_m或1-24_m
      if (fromCellCode === "1-25" || fromCellCode === "1-24") {
        const middlePointName = fromCellCode + "_m";
        const middlePoint = that.ThreeEngine.getObjectByName(middlePointName);

        if (middlePoint) {
          console.log(`货物移动需要经过中间点 ${middlePointName}`);
          // 创建两个连续的动画：从起点到中间点，再从中间点到终点
          tween1 = new TWEEN.Tween(pallet.position)
            .to(
              {
                x: middlePoint.position.x,
                y: middlePoint.position.y,
                z: middlePoint.position.z
              },
              moveDuration / 2 // 前半段动画时长
            )
            .easing(TWEEN.Easing.Quadratic.InOut)
            .onStart(() => {
              console.log(`货物开始移动，先前往中间点 ${middlePointName}`);
            })
            .chain(
              new TWEEN.Tween(pallet.position)
                .to(
                  {
                    x: fromLineToPos.position.x,
                    y: fromLineToPos.position.y,
                    z: fromLineToPos.position.z
                  },
                  moveDuration / 2 // 后半段动画时长
                )
                .easing(TWEEN.Easing.Quadratic.InOut)
                .onStart(() => {
                  console.log(`货物到达中间点 ${middlePointName}，继续前往终点`);
                })
            );
        } else {
          console.warn(`未找到中间点对象 ${middlePointName}，使用直接路径`);
          // 如果找不到中间点，使用默认路径
          tween1 = new TWEEN.Tween(pallet.position)
            .to(
              {
                x: fromLineToPos.position.x,
                y: fromLineToPos.position.y,
                z: fromLineToPos.position.z
              },
              moveDuration
            )
            .easing(TWEEN.Easing.Quadratic.InOut)
            .onStart(() => {
            });
        }
      } else {
        // 默认货物移动路径：直接从起点到终点
        tween1 = new TWEEN.Tween(pallet.position)
          .to(
            {
              x: fromLineToPos.position.x,
              y: fromLineToPos.position.y,
              z: fromLineToPos.position.z
            },
            moveDuration
          )
          .easing(TWEEN.Easing.Quadratic.InOut)
          .onStart(() => {
          });
      }

      // 6. 同时移动RGV到与起点物流线最近的有效位置
      // RGV只能移动到固定的有效位置，如LINE1_IN_1FB等
      const tween2 = new TWEEN.Tween(rgv.position)
        .to(
          {
            x: rgvFirstPos.x, // RGV的x轴与最近有效位置x轴一致
            y: rgvFirstPos.y, // RGV的y轴与最近有效位置y轴一致
            z: rgvFirstPos.z // RGV的z轴与最近有效位置z轴一致
          },
          rgvMoveDuration
        )
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`${rgvName}开始移动到距离起点物流线最近的有效位置(x: ${rgvFirstPos.x.toFixed(2)}, y: ${rgvFirstPos.y.toFixed(2)}, z: ${rgvFirstPos.z.toFixed(2)})`);
        });

      // 10. 货物从物流线终点移动到RGV上
      // 根据不同RGV选择对应的固定位置（rgv1_up、rgv2_up、rgv3_up）
      let rgvUpPos;
      if (rgvName === 'rgv1') {
        rgvUpPos = this.ThreeEngine.getObjectByName('rgv1_up');
      } else if (rgvName === 'rgv2') {
        rgvUpPos = this.ThreeEngine.getObjectByName('rgv2_up');
      } else if (rgvName === 'rgv3') {
        rgvUpPos = this.ThreeEngine.getObjectByName('rgv3_up');
      } else {
        console.error(`未知的RGV名称: ${rgvName}, 无法获取对应的固定位置`);
        return;
      }

      // 验证RGV固定位置是否存在
      if (!rgvUpPos) {
        console.error(`无法找到${rgvName}对应的固定位置对象`);
        return;
      }

      const tween3 = new TWEEN.Tween(pallet.position)
        .to(
          {
            // x: rgvUpPos.position.x,
            // y: rgvUpPos.position.y,
            // z: rgvUpPos.position.z
            x: rgvFirstPos.x,
            y: rgvFirstPos.y + 0.5,
            z: rgvFirstPos.z
          },
          loadUnloadDuration
        )
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
        });

      // 8. RGV移动到与终点物流线接触位置的x和y值一致
      const tween4 = new TWEEN.Tween(rgv.position)
        .to(
          {
            x: rgvLastPos.x, // RGV的x轴与终点物流线接触位置x轴一致
            y: rgvLastPos.y, // RGV的y轴与终点物流线接触位置y轴一致
            z: rgvLastPos.z // RGV的z轴与终点物流线接触位置z轴一致
          },
          rgvMoveDuration
        )
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`${rgvName}开始移动到与终点物流线接触位置(x: ${toLineFromPos.position.x.toFixed(2)}, y: ${toLineFromPos.position.y.toFixed(2)})`);
        });

      // 9. 货物从RGV1移动到终点物流线的接触位置
      const tween5 = new TWEEN.Tween(pallet.position)
        .to(
          {
            x: toLineFromPos.position.x,
            y: toLineFromPos.position.y, // 货物y轴与终点物流线接触位置y轴一致
            z: toLineFromPos.position.z
          },
          loadUnloadDuration
        )
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
        });

      // 10. 货物自动从终点物流线起点移动到终点
      // 对于特定物流线（1-25或1-24），货物移动需要先经过中间点
      let tween6;

      if (toCellCode === '1-25' || toCellCode === '1-24') {
        const middlePointCode = toCellCode + '_m'; // 中间点代码
        const middlePointPos = that.ThreeEngine.getObjectByName(middlePointCode);
        console.log(`货物从终点物流线起点移动到终点，物流线: ${toCellCode}，将经过中间点: ${middlePointCode}`);

        // 创建第一个动画：从终点物流线起点到中间点
        const tween6Part1 = new TWEEN.Tween(pallet.position)
          .to(
            {
              x: middlePointPos.position.x,
              y: middlePointPos.position.y,
              z: middlePointPos.position.z
            },
            moveDuration / 2 // 分配一半的移动时间
          )
          .easing(TWEEN.Easing.Quadratic.InOut)
          .onStart(() => {
            console.log(`货物开始移动到中间点: ${middlePointCode}`);
          });

        // 创建第二个动画：从中间点到终点物流线终点
        const tween6Part2 = new TWEEN.Tween(pallet.position)
          .to(
            {
              x: toLineToPos.position.x,
              y: toLineToPos.position.y,
              z: toLineToPos.position.z
            },
            moveDuration / 2 // 分配另一半的移动时间
          )
          .easing(TWEEN.Easing.Quadratic.InOut)
          .onStart(() => {
            console.log(`货物从中点移动到终点: ${toLineTo}`);
          })
          .onComplete(() => {
            // 检查货物是否被移动到需要删除的位置（用户指定的特定位置）
            const deletePositions = ['1-3_to', '1-24_to', '1-25_to', '3-3_to'];
            if (deletePositions.includes(toLineTo)) {
              // 只在用户指定的特定位置删除货物模型
              if (pallet && pallet.parent) {
                pallet.parent.remove(pallet);
              }
            } else {
            }
          });

        // 将两个动画连接起来
        tween6Part1.chain(tween6Part2);
        tween6 = tween6Part1; // tween6指向第一个动画
      } else {
        // 默认路径：直接从终点物流线起点移动到终点
        tween6 = new TWEEN.Tween(pallet.position)
          .to(
            {
              x: toLineToPos.position.x,
              y: toLineToPos.position.y,
              z: toLineToPos.position.z
            },
            moveDuration
          )
          .easing(TWEEN.Easing.Quadratic.InOut)
          .onStart(() => {
          })
          .onComplete(() => {
            // 检查货物是否被移动到需要删除的位置（用户指定的特定位置）
            const deletePositions = ['1-3_to', '1-24_to', '1-25_to', '3-3_to'];
            if (deletePositions.includes(toLineTo)) {
              // 只在用户指定的特定位置删除货物模型
              if (pallet && pallet.parent) {
                pallet.parent.remove(pallet);
              }
            } else {
            }
          });
      }


      // 货物和RGV都到达物流线接触位置后，开始货物上RGV
      // 使用标志来跟踪两个动画是否都完成
      let tween1Complete = false;
      let tween2Complete = false;

      tween1.onComplete(() => {
        tween1Complete = true;
        if (tween2Complete) {
          tween3.start();
        }
      });

      tween2.onComplete(() => {
        tween2Complete = true;
        if (tween1Complete) {
          tween3.start();
        }
      });

      // 货物上RGV后，RGV移动到终点物流线接触位置
      tween3.onComplete(() => {
        // 使用TRgvMovement类的moveRgvTo方法移动RGV，并传递货物对象
        this.rgvMovement.moveRgvTo(rgvName, toRgvPosName, {
          duration: rgvMoveDuration,
          cargo: pallet, // 传递货物对象，实现同步移动
          onComplete: () => {
            // RGV移动完成后，货物下RGV
            tween5.start();
          }
        });
      });

      // 货物下RGV后，继续移动到物流线终点
      tween5.chain(tween6);

      // 同时开始货物和RGV的移动
      tween1.start();
      tween2.start();
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
      console.log(`总面数（三角形）: ${statistics.totalTriangles.toLocaleString()}`);
      console.log(`总顶点数: ${statistics.totalVertices.toLocaleString()}`);
      console.table(statistics.details);
      return statistics;
    },

    // 在模型加载完成后调用
    addStorageMachineLabels() {

      // this.createSprite("堆垛机躯干", "堆垛机躯干", 16);

      // 为所有载货台添加信息显示框
      // this.addPlatformLabels();

    },

    // 为所有载货台添加信息显示框
    addPlatformLabels() {
      // 定义载货台的名称模式
      const platformNames = ['载货台1', '载货台2', '载货台3', '载货台4'];

      platformNames.forEach((platformName, index) => {
        const platform = this.ThreeEngine.getObjectByName(platformName);
        if (platform) {
          this.createSpritePallet(platformName, `载货台${index + 1}`);
        } else {
          console.warn(`未找到名为 ${platformName} 的载货台对象`);
        }
      });
    },

    // 为四层地板应用自定义材质（使用材质管理器）
    applyCustomFloorMaterials() {
      // 定义四层地板的名称
      const floorNames = ['floor-1', 'floor-2', 'floor-3', 'floor-4'];
      // 对应的材质状态
      const floorStates = ['floor1', 'floor2', 'floor3', 'floor4'];

      let appliedCount = 0;

      // 为每层地板应用材质
      floorNames.forEach((floorName, index) => {
        const floor = this.ThreeEngine.getObjectByName(floorName);
        if (floor) {
          // 使用材质管理器应用材质
          materialManager.applyMaterialToObject(floor, 'floor', 'main', floorStates[index]);
          appliedCount++;
        } else {
          console.warn(`未找到名为 ${floorName} 的地板对象`);
        }
      });

      // 如果没有找到指定名称的地板，尝试查找其他可能的地板名称
      if (appliedCount === 0) {
        const floorObjects = [];
        this.ThreeEngine.scene.traverse((obj) => {
          if (obj.name && obj.name.toLowerCase().includes('floor')) {
            floorObjects.push(obj.name);
          }
        });

        floorObjects.forEach((floorName) => {
          const floor = this.ThreeEngine.getObjectByName(floorName);
          if (floor) {
            // 根据名称确定使用哪个状态
            const floorIndex = floorNames.indexOf(floorName);
            const stateIndex = floorIndex >= 0 ? floorIndex : 0;

            // 使用材质管理器应用材质
            materialManager.applyMaterialToObject(floor, 'floor', 'main', floorStates[stateIndex]);
            appliedCount++;
          }
        });
      }

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
          appliedCount++;
        } else {
          console.warn(`未找到名为 ${bodyName} 的堆垛机躯干对象`);
        }
      });

    },

    /**
     * 根据货位编码获取货位3D坐标
     * @param {string} locationCode - 货位编码，格式为：货架号-列数-层数 (例如：1-1-1、7-39-13)
     * @returns {object|null} - 货位3D坐标对象 {x, y, z}，如果定位失败返回null
     */
    getShelfLocationCoordinates(locationCode) {
      // 参数验证
      if (!locationCode) {
        console.error('货位编码不能为空');
        return null;
      }

      // 解析货位编码
      const parts = locationCode.split('-');
      if (parts.length !== 3) {
        console.error('货位编码格式不正确，应为：货架号-列数-层数');
        return null;
      }

      const shelfNum = parseInt(parts[0]);
      const column = parseInt(parts[1]);
      const layer = parseInt(parts[2]);

      // 验证解析结果
      if (isNaN(shelfNum) || isNaN(column) || isNaN(layer)) {
        console.error('货位编码中的数字格式不正确');
        return null;
      }

      // 获取定位空物体
      let startLocation, endLocation;
      if (shelfNum >= 1 && shelfNum <= 6) {
        // 1-6号货架使用 "货架号-1-1" 和 "货架号-39-14" 作为定位空物体
        startLocation = `${shelfNum}-1-1`;
        endLocation = `${shelfNum}-39-14`;
      } else if (shelfNum >= 7 && shelfNum <= 10) {
        // 7-10号货架使用 "货架号-1-1" 和 "货架号-39-13" 作为定位空物体
        startLocation = `${shelfNum}-1-1`;
        endLocation = `${shelfNum}-39-13`;
      } else {
        console.error('无效的货架号');
        return null;
      }

      // 获取定位空物体的坐标
      const startPos = this.ThreeEngine.getObjectByName(startLocation);
      const endPos = this.ThreeEngine.getObjectByName(endLocation);

      if (!startPos || !endPos) {
        console.error(`未找到定位空物体: ${startLocation} ${endLocation}`);
        return null;
      }

      // 计算列数和层数的插值比例
      let columnRatio, layerRatio;
      if (shelfNum >= 1 && shelfNum <= 6) {
        // 1-6号货架：共39列，14层
        columnRatio = (column - 1) / (39 - 1);
        layerRatio = (layer - 1) / (14 - 1);
      } else {
        // 7-10号货架：共39列，13层
        columnRatio = (column - 1) / (39 - 1);
        layerRatio = (layer - 1) / (13 - 1);
      }

      // 计算目标货位的坐标
      const x = startPos.position.x + (endPos.position.x - startPos.position.x) * columnRatio;
      const y = startPos.position.y + (endPos.position.y - startPos.position.y) * layerRatio;
      const z = startPos.position.z + (endPos.position.z - startPos.position.z) * columnRatio;

      return { x, y, z };
    },

    // 为货架应用自定义材质（使用材质管理器）
    applyShelfMaterials() {
      let appliedCount = 0;

      // 遍历场景中所有对象，查找所有名为'货架'的对象
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('货架')) {
          // 使用材质管理器应用材质
          materialManager.applyMaterialToObject(obj, 'shelf', 'main', 'default');
          appliedCount++;
        }
      });

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
      });

      if (walls.length === 0) {
        console.warn('未找到墙体组件');
      } else {
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
      });

      if (platforms.length === 0) {
        console.warn('未找到载货台组件');
      } else {
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
      });

      if (upperForks.length === 0) {
        console.warn('未找到上货叉组件');
      } else {
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
      });

      if (lowerForks.length === 0) {
        console.warn('未找到下货叉组件');
      } else {
      }
    },

    // 从货位代码或物流线代码中解析货叉伸出方向的辅助方法
    // 返回 'positive' 表示正向伸出，'negative' 表示反向伸出
    parseShelfNumber(locationCode) {
      if (!locationCode) {
        console.error('解析货叉方向时位置代码为空');
        return 'positive'; // 默认返回正向伸出
      }

      // 区分物流线代码和货位代码
      // 物流线代码格式: x-x (两个部分)
      // 货位代码格式: x-x-x (三个部分)
      const isLogisticsLine = locationCode.match(/^\d+-\d+$/) !== null;
      const isCellCode = locationCode.match(/^\d+-\d+-\d+$/) !== null;

      // 物流线代码规则
      const positiveLogisticsLines = ['1-5', '1-7', '1-9', '1-11', '2-4', '3-5', '3-7', '3-9', '3-11', '4-4', '1-16', '1-18', '1-20', '1-22'];

      // 货位代码规则：根据货架号确定方向
      const positiveShelfNumbers = [1, 3, 5, 7, 8];
      const negativeShelfNumbers = [2, 4, 6, 9, 10];

      // 优先处理物流线代码
      if (isLogisticsLine) {
        if (positiveLogisticsLines.includes(locationCode)) {
          console.log(`物流线代码 ${locationCode} 对应正向伸出`);
          return 'positive';
        } else {
          console.log(`物流线代码 ${locationCode} 对应反向伸出`);
          return 'negative';
        }
      }

      // 处理货位代码
      if (isCellCode) {
        const parts = locationCode.split('-');
        if (parts.length >= 1) {
          const shelfNum = parseInt(parts[0], 10);
          if (!isNaN(shelfNum) && shelfNum >= 1 && shelfNum <= 10) {
            if (positiveShelfNumbers.includes(shelfNum)) {
              console.log(`货位代码 ${locationCode} 解析出货架号 ${shelfNum}，对应正向伸出`);
              return 'positive';
            } else if (negativeShelfNumbers.includes(shelfNum)) {
              console.log(`货位代码 ${locationCode} 解析出货架号 ${shelfNum}，对应反向伸出`);
              return 'negative';
            }
          }
        }
        console.warn(`无法从货位代码 ${locationCode} 中解析有效货架号，返回默认正向伸出`);
        return 'positive';
      }

      console.warn(`位置代码 ${locationCode} 格式不识别，返回默认正向伸出`);
      return 'positive';
    },

    // 货叉伸出动画执行方法
    // 参数说明：
    // - ddjNumber: 堆垛机编号
    // - callback: 动画完成后的回调函数
    // - duration: 动画持续时间，默认 3200ms
    // - forkDirection: 货叉伸出方向，'positive' 表示正向，'negative' 表示反向，默认 'positive'
    executeForkExtendAnimation(ddjNumber, callback, duration = 3200, forkDirection = 'positive') {
      // 货叉对象名称（携带数字编号）
      const upperForkName = `上货叉${ddjNumber}`;
      const lowerForkName = `下货叉${ddjNumber}`;

      // 获取货叉对象
      const upperFork = this.ThreeEngine.getObjectByName(upperForkName);
      const lowerFork = this.ThreeEngine.getObjectByName(lowerForkName);

      console.log(`尝试查找货叉对象: 上货叉${ddjNumber}=${!!upperFork}, 下货叉${ddjNumber}=${!!lowerFork}`);

      if (!upperFork || !lowerFork) {
        console.error(`无法找到堆垛机${ddjNumber}的货叉对象: 上货叉=${upperForkName}, 下货叉=${lowerForkName}`);
        // 确保回调函数被调用，避免动画流程中断
        if (callback && typeof callback === 'function') {
          callback();
        }
        return;
      }

      console.log(`开始执行堆垛机${ddjNumber}的货叉伸出动画，持续时间: ${duration}ms，货叉方向: ${forkDirection}`);

      // 保存货叉原始位置，用于后续回退
      if (!upperFork.originalZ) {
        upperFork.originalZ = upperFork.position.z;
      }
      if (!lowerFork.originalZ) {
        lowerFork.originalZ = lowerFork.position.z;
      }

      // 根据货叉方向设置目标位置
      let upperForkTargetZ, lowerForkTargetZ;
      if (forkDirection === 'positive') {
        upperForkTargetZ = 1;
        lowerForkTargetZ = 0.5;
        console.log(`货叉正向伸出，上货叉目标位置: 1, 下货叉目标位置: 0.5`);
      } else {
        upperForkTargetZ = -1;
        lowerForkTargetZ = -0.5;
        console.log(`货叉反向伸出，上货叉目标位置: -1, 下货叉目标位置: -0.5`);
      }

      console.log(`上货叉目标位置: ${upperForkTargetZ}, 下货叉目标位置: ${lowerForkTargetZ}`);

      let completedTweens = 0;
      const totalTweens = 2;

      // 上货叉伸出动画
      const upperForkTween = new TWEEN.Tween(upperFork.position)
        .to({ z: upperForkTargetZ }, duration)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`上货叉${ddjNumber}开始伸出`);
        })
        .onComplete(() => {
          console.log(`上货叉${ddjNumber}伸出完成`);
          completedTweens++;
          if (completedTweens === totalTweens && callback) {
            console.log('所有货叉伸出完成，调用回调函数');
            callback();
          }
        });

      // 下货叉伸出动画
      const lowerForkTween = new TWEEN.Tween(lowerFork.position)
        .to({ z: lowerForkTargetZ }, duration)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`下货叉${ddjNumber}开始伸出`);
        })
        .onComplete(() => {
          console.log(`下货叉${ddjNumber}伸出完成`);
          completedTweens++;
          if (completedTweens === totalTweens && callback) {
            console.log('所有货叉伸出完成，调用回调函数');
            callback();
          }
        });

      // 同时启动上下货叉伸出动画
      upperForkTween.start();
      lowerForkTween.start();
    },

    // 公共函数：根据isTest模式计算动画持续时间
    // 参数说明：
    // - normalDuration: 正常模式下的持续时间（毫秒）
    // - testDuration: 测试模式下的持续时间（毫秒）
    // 返回值：实际应用的动画持续时间
    getAnimationDuration(normalDuration, testDuration = 1000) {
      return this.isTest ? testDuration : normalDuration;
    },

    // 公共函数：创建货物移动动画
    // 参数说明：
    // - ddjNumber: 堆垛机编号
    // - duration: 动画持续时间
    // - callback: 动画完成后回调
    // - cargo: 货物对象
    createCargoTween(ddjNumber, duration, callback, cargo = null) {
      // 如果没有提供货物对象，则自动查找
      const targetCargo = cargo || this.ThreeEngine.getObjectByName(`货物${ddjNumber}`);

      if (!targetCargo) {
        console.error(`无法找到货物对象 货物${ddjNumber}`);
        if (callback && typeof callback === 'function') {
          callback();
        }
        return null;
      }

      console.log(`创建货物移动动画，持续时间: ${duration}ms`);

      // 货物移动动画 - 这里根据实际情况调整目标位置
      const cargoTween = new TWEEN.Tween(targetCargo.position)
        .to({ z: 0.3 }, duration) // 货物移动到载货台
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`货物${ddjNumber}开始移动`);
        })
        .onComplete(() => {
          console.log(`货物${ddjNumber}移动完成`);
          if (callback && typeof callback === 'function') {
            callback();
          }
        });

      return cargoTween;
    },

    // 货叉收回动画执行方法
    executeForkRetractAnimation(ddjNumber, callback, duration = 3200) {
      // 货叉对象名称（携带数字编号）
      const upperForkName = `上货叉${ddjNumber}`;
      const lowerForkName = `下货叉${ddjNumber}`;

      // 获取货叉对象
      const upperFork = this.ThreeEngine.getObjectByName(upperForkName);
      const lowerFork = this.ThreeEngine.getObjectByName(lowerForkName);

      console.log(`尝试查找货叉对象: 上货叉${ddjNumber}=${!!upperFork}, 下货叉${ddjNumber}=${!!lowerFork}`);

      if (!upperFork || !lowerFork) {
        console.error(`无法找到堆垛机${ddjNumber}的货叉对象: 上货叉=${upperForkName}, 下货叉=${lowerForkName}`);
        // 回调函数存在时调用
        if (callback && typeof callback === 'function') {
          callback();
        }
        return;
      }

      console.log(`开始执行堆垛机${ddjNumber}的货叉收回动画，持续时间: ${duration}ms`);

      // 根据用户需求设置货叉收回目标位置
      // 货叉收回时，上下货叉都移动到0
      const retractTargetZ = 0;

      // 跟踪动画完成状态
      let completedTweens = 0;
      const totalTweens = 2;

      const checkComplete = () => {
        completedTweens++;
        if (completedTweens === totalTweens) {
          console.log(`堆垛机${ddjNumber}所有货叉收回完成`);
          // 回调函数存在时调用
          if (callback && typeof callback === 'function') {
            callback();
          }
        }
      };

      // 上货叉收回动画 - 移动到坐标0
      const upperForkTween = new TWEEN.Tween(upperFork.position)
        .to({ z: retractTargetZ }, duration)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`上货叉${ddjNumber}开始收回`);
        })
        .onComplete(() => {
          console.log(`上货叉${ddjNumber}收回完成`);
          checkComplete();
        });

      // 下货叉收回动画 - 移动到坐标0
      const lowerForkTween = new TWEEN.Tween(lowerFork.position)
        .to({ z: retractTargetZ }, duration)
        .easing(TWEEN.Easing.Quadratic.InOut)
        .onStart(() => {
          console.log(`下货叉${ddjNumber}开始收回`);
        })
        .onComplete(() => {
          console.log(`下货叉${ddjNumber}收回完成`);
          checkComplete();
        });

      // 同时启动上下货叉收回动画
      upperForkTween.start();
      lowerForkTween.start();
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
      });

      // 使用材质管理器为RGV载货台应用材质
      rgvZht.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'platform', 'default');
      });

      // 使用材质管理器为RGV链条应用材质
      rgvZhtLt.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'chain', 'default');
      });

      // 使用材质管理器为RGV装饰条应用材质
      rgvZst.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'decoration', 'default');
      });

      // 使用材质管理器为RGV轨道应用材质
      rgvGd.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'rgv', 'track', 'default');
      });

      // 输出统计信息
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
      });

      // 使用材质管理器为输送带应用材质
      conveyorBelts.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'belt', 'default');
      });

      // 使用材质管理器为滚筒应用材质
      conveyorRollers.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'roller', 'default');
      });

      // 使用材质管理器为链条槽应用材质
      chainGrooves.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'chainGroove', 'default');
      });

      // 使用材质管理器为导向条应用材质
      guideStrips.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'conveyor', 'guideStrip', 'default');
      });

      // 输出统计信息
    },

    // 为托盘应用乳白色材质（使用材质管理器）
    applyPalletMaterials() {
      const pallets = []; // 存储所有托盘组件

      // 收集所有托盘相关组件
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && (obj.name.includes('pallet') || obj.name.includes('托盘') || obj.name.includes('货物'))) {
          pallets.push(obj);
        }
      });

      // 使用材质管理器为托盘应用乳白色材质
      pallets.forEach((obj) => {
        materialManager.applyMaterialToObject(obj, 'pallet', 'main', 'default');
      });

      // 输出统计信息
      if (pallets.length === 0) {
        console.warn('未找到托盘组件');
      } else {
      }
    },

    // 设置摄像头到指定楼层的观测点
    setCameraToFloor(floorNumber) {
      if (!this.ThreeEngine || !this.ThreeEngine.camera) {
        console.warn('摄像头未初始化');
        return;
      }

      const renderer = this.ThreeEngine.renderer;
      if (!renderer) {
        console.warn('渲染器未初始化');
        return;
      }

      const dom = renderer.domElement.parentElement;
      if (!dom) {
        console.warn('DOM元素未找到');
        return;
      }

      const width = dom.offsetWidth;
      const height = dom.offsetHeight;

      // 根据不同观测点设置不同的视图
      switch (floorNumber) {
        case 1:
          // 观测点一：正交视图
          this.switchToOrthographicCamera(width, height);
          break;
        case 2:
          // 观测点二：正视图（从Z轴正方向看）
          this.setPerspectiveView({ x: 0, y: 50, z: 200 }, { x: 0, y: 50, z: 0 });
          break;
        case 3:
          // 观测点三：侧视图（从X轴正方向看）
          this.setPerspectiveView({ x: 200, y: 50, z: 0 }, { x: 0, y: 50, z: 0 });
          break;
        case 4:
          // 观测点四：保持原有的透视视图
          this.setPerspectiveView({ x: -700, y: 150, z: -150 }, { x: 0, y: 150, z: 0 });
          break;
        default:
          console.warn(`无效的观测点: ${floorNumber}`);
          return;
      }
    },

    // 切换到正交相机
    switchToOrthographicCamera(width, height) {
      const aspect = width / height;
      const zoom = 500;

      // 创建正交相机
      const orthoCamera = new THREE.OrthographicCamera(
        -zoom * aspect, zoom * aspect, zoom, -zoom, 0.1, 10000
      );

      // 设置正交相机位置和看向点
      orthoCamera.position.set(-800, 200, -800);
      orthoCamera.lookAt(new THREE.Vector3(0, 0, 0));

      // 更新投影矩阵
      orthoCamera.updateProjectionMatrix();

      // 替换相机
      this.ThreeEngine.camera = orthoCamera;
      this.ThreeEngine.controls.object = orthoCamera;

    },

    // 设置透视相机视图
    setPerspectiveView(position, lookAt) {
      // 如果当前是正交相机，切换回透视相机
      if (this.ThreeEngine.camera instanceof THREE.OrthographicCamera) {
        const renderer = this.ThreeEngine.renderer;
        const dom = renderer.domElement.parentElement;
        const width = dom.offsetWidth;
        const height = dom.offsetHeight;

        // 创建透视相机
        const perspCamera = new THREE.PerspectiveCamera(
          35,      // 视场角FOV
          width / height,
          0.1,     // 近裁剪面
          1000     // 远裁剪面
        );

        this.ThreeEngine.camera = perspCamera;
        this.ThreeEngine.controls.object = perspCamera;

      }

      // 立即设置相机位置，不使用TWEEN动画以确保能看到效果
      this.ThreeEngine.camera.position.set(position.x, position.y, position.z);
      this.ThreeEngine.camera.lookAt(new THREE.Vector3(lookAt.x, lookAt.y, lookAt.z));

      // 更新投影矩阵
      this.ThreeEngine.camera.updateProjectionMatrix();

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
        }
      );

      // 不在初始化时改变相机位置，保持原始位置 (-800, 100, 500)
      // 进入无人机模式时才设置位置

      // 初始化炮弹系统
      this.projectileSystem = new ProjectileSystem(
        this.ThreeEngine.scene,
        this.ThreeEngine.camera
      );

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
      loader.load("/讯联新工厂.glb", function (glb) {

        //blander中灯光强度与three.js不同，这个需要做一个计算
        glb.scene.traverse((object) => {
          if (object.isLight) {
            // 禁用模型中的所有光源，使用我们自定义的灯光设置
            object.visible = false;
          }
          // 隐藏货物模型
          if (object.name === "货物" || object.name === "货物1" || object.name === "货物2" || object.name === "货物3" || object.name === "货物4") {
            object.visible = false;
          }
          if (object.userData.aaaaa == "bbbbb") {
          }
        });
        that.ThreeEngine.addObject(glb.scene);

        // 检查躯干1-4的初始位置
        const stackerBodyNames = ['躯干1', '躯干2', '躯干3', '躯干4'];
        stackerBodyNames.forEach((bodyName) => {
          const body = that.ThreeEngine.getObjectByName(bodyName);
          if (body) {
            // 如果需要，也可以检查父对象的位置
            if (body.parent) {
            }
          } else {
            console.warn(`未找到 ${bodyName}`);
          }
        });

        that.addStorageMachineLabels();
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

        // 初始化RGV移动控制器
        that.rgvMovement = new RgvMovement(that.ThreeEngine);

        // 统计模型信息
        that.getModelStatistics();

        // 初始化库存货物
        that.initInventory();

      }
        , function (xhr) {
          const percent = xhr.loaded / xhr.total;
          that.jindu = Math.round(100 * percent) + "%";
          //   if (percent >= 1) {
          setTimeout(function () {
            that.jiinduDisable = false;
          }, 300);

        }

      );

      // 创建一个时钟对象Clock
      const clock = new THREE.Clock();

      function render() {
        TWEEN.update();
        that.requestId = requestAnimationFrame(render);

        const delta = that.clock.getDelta();

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
        if (that.lift1texture != undefined) {
          that.lift1texture.offset.x -= 0.04;
        }
        if (that.texture != undefined) {
          that.texture.offset.x -= 0.01; // 改为原来的50%
        }
      }
      render();
    },

    initObjClickHandler() {
      // 重写ThreeEngine的点击事件处理函数
      // this.ThreeEngine.onObjectClick = (intersect) => {
      //   // 检查点击的对象是否为"堆垛机躯干"
      //   if (intersect.object.name === "躯干1") {
      //     this.handleStackerClick(intersect.object);
      //   }
      //    if (intersect.object.name === "载货台1") {
      //     this.handleStackerClick(intersect.object);
      //   }
      // };
    },

    // 处理堆垛机点击事件
    handleStackerClick(stackerObject) {
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

    createSpritePallet(objName, text) {
      // 获取载货台对象以读取实时位置信息
      const obj = this.ThreeEngine.getObjectByName(objName);
      if (!obj) {
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
    createSprite(objName, text, y) {
      if (!y) {
        y = 8;
      }
      // 使用
      const spriteText = createSpriteTextLabel({
        global: {
          headerHeight: 50,
          totalWidth: 450,
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
      if (!obj) {
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

      // 连接成功回调
      this.ws.onopen = function (event) {
        console.info('✅ WebSocket连接成功');
        self.wsStatus.connected = true;
        self.wsStatus.reconnectAttempts = 0; // 重置重连次数

        // 启动心跳检测
        self.startHeartbeat();
      };

      // 消息接收回调
      this.ws.onmessage = function (event) {
        console.info('📩 接收WebSocket消息:', event.data);

        // 处理心跳消息（ping）
        if (event.data === 'ping') {
          return;
        }

        try {
          var data = JSON.parse(event.data);
          console.info('📩 解析后的WebSocket消息:', data);

          // 处理任务数据
          const { type, palletCode, fromCellCode, toCellCode, ddjNumber } = data;
          if (type && palletCode && fromCellCode && toCellCode) {
            // 调用任务执行方法
            self.executeTask(fromCellCode, toCellCode, palletCode, type, ddjNumber);
          } else {
            console.warn('⚠️ 任务数据不完整，跳过处理: 缺少必要参数');
          }
        } catch (error) {
          console.error('❌ 解析WebSocket消息失败:', error);
          console.error('📥 原始消息内容:', event.data);
        }
      };

      // 连接关闭回调
      this.ws.onclose = function (event) {
        console.warn('❌ WebSocket连接关闭，正在尝试重连...');
        self.text_content = self.text_content + "已经关闭连接!" + "\n";
        self.wsStatus.connected = false;

        // 停止心跳检测
        self.stopHeartbeat();

        // 启动重连机制
        self.startReconnect();
      };

      // 连接错误回调
      this.ws.onerror = function (error) {
        console.error('❌ WebSocket连接错误:', error);
        self.wsStatus.connected = false;
      };
    },

    // 启动心跳检测
    startHeartbeat() {
      const self = this;

      // 清除之前的定时器
      this.stopHeartbeat();

      // 定时发送心跳消息
      this.wsStatus.heartbeatTimer = setInterval(function () {
        if (self.ws && self.ws.readyState === WebSocket.OPEN) {
          try {
            self.ws.send('ping'); // 发送心跳消息
          } catch (error) {
            console.error('❌ 心跳发送失败:', error);
          }
        }
      }, this.wsConfig.heartbeatInterval);
    },

    // 停止心跳检测
    stopHeartbeat() {
      if (this.wsStatus.heartbeatTimer) {
        clearInterval(this.wsStatus.heartbeatTimer);
        this.wsStatus.heartbeatTimer = null;
      }
    },

    // 启动重连
    startReconnect() {
      const self = this;

      // 清除之前的重连定时器
      if (self.wsStatus.reconnectTimer) {
        clearTimeout(self.wsStatus.reconnectTimer);
      }

      // 限制最大重连次数
      if (self.wsStatus.reconnectAttempts >= self.wsConfig.maxReconnectAttempts) {
        console.error('❌ 已达到最大重连次数，停止重连');
        return;
      }

      // 设置重连定时器
      self.wsStatus.reconnectTimer = setTimeout(function () {
        self.wsStatus.reconnectAttempts++;
        self.join(); // 重新连接
      }, self.wsConfig.reconnectInterval);
    },

    // 停止重连
    stopReconnect() {
      if (this.wsStatus.reconnectTimer) {
        clearTimeout(this.wsStatus.reconnectTimer);
        this.wsStatus.reconnectTimer = null;
      }
    },

    exit() {
      this.stopHeartbeat();
      this.stopReconnect();
      if (this.ws) {
        this.ws.close();
        this.ws = null;
        this.wsStatus.connected = false;
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

    agvTweenStop() {
      const AgvCar = this.ThreeEngine.getObjectByName("agv");
      for (var i = 0; i < 20; i++) {
        var aaaaa = AgvCar["tween" + i];
        if (aaaaa != undefined && aaaaa != null) {
          aaaaa.stop();
        }
        AgvCar["tween" + i] = null;
      }
    },

    agvLookAt(AgvCar, tween, i) {
      var that = this;
      var agvRouter = that.agvRouter;
      var from = agvRouter[i - 1];
      var to = agvRouter[i];
      var distance = that.distance3D(from, to);
      var time = distance * 150;
      tween.to(to, time);
      if (i < agvRouter.length - 1) {
        tween.onComplete(function () {
          var agvRouter = that.agvRouter;
          AgvCar.lookAt(agvRouter[i + 1].x, agvRouter[i + 1].y, agvRouter[i + 1].z);
        });
      };
      that.agvTween(AgvCar, tween, i);
    },

    distance3D(point1, point2) {
      return Math.sqrt(
        Math.pow(point2.x - point1.x, 2) +
        Math.pow(point2.y - point1.y, 2) +
        Math.pow(point2.z - point1.z, 2)
      );
    },

    agvTween(AgvCar, tween, i) {
      if (i == 1) {
        AgvCar.tween = tween;
      } else if (i > 1) {
        AgvCar["tween" + (i - 1)].chain(tween);
      }
    },





    aaa() {
      this.task();
    },



    /**
     * 在物流线的起点生成货物
     * @param {string} lineName - 物流线名称，格式为"层数-站台号"，如"1-2"
     * @param {string|Object} directionOrSourcePosition - 移动方向("fromTo"或"toFrom")或货物源位置(THREE.Vector3)
     * @param {string} [palletCode] - 托盘名称，可选
     * @returns {THREE.Mesh|null} - 生成的货物对象或null
     */
    generateCargoOnLineFrom(lineName, directionOrSourcePosition, palletCode) {
      var that = this;

      // 检查ThreeEngine是否初始化
      if (!that.ThreeEngine) {
        console.error('ThreeEngine 未初始化，无法生成货物');
        return null;
      }

      // 验证物流线名称格式
      if (!/^\d+-\d+$/.test(lineName)) {
        console.error("物流线名称格式错误，应为'层数-站台号'，如'1-2'");
        return null;
      }

      let fromCellCode;

      // 判断参数是方向还是源位置
      if (typeof directionOrSourcePosition === "string" &&
        (directionOrSourcePosition === "fromTo" || directionOrSourcePosition === "toFrom")) {
        // 参数是方向
        fromCellCode = directionOrSourcePosition === "fromTo" ? lineName + "_from" : lineName + "_to";
      } else if (directionOrSourcePosition && directionOrSourcePosition.isVector3) {
        // 参数是源位置，计算最近的端点
        const nearestEndpoint = that.getNearestLineEndpoint(lineName, directionOrSourcePosition);
        if (!nearestEndpoint) {
          console.error('无法确定最近的物流线端点');
          return null;
        }
        fromCellCode = nearestEndpoint.code;
        console.log(`使用最近端点策略，从端点 ${fromCellCode} 生成货物（源位置距离：${directionOrSourcePosition.distanceTo(nearestEndpoint.position)}）`);
      } else {
        console.error("移动方向或源位置参数错误，应为'fromTo'、'toFrom'或THREE.Vector3对象");
        return null;
      }

      // 检查起点是否存在
      const fromCell = that.ThreeEngine.getObjectByName(fromCellCode);
      if (!fromCell) {
        console.error(`未找到起点点位：${fromCellCode}`);
        return null;
      }

      // 检查起点位置是否已有货物
      // 获取场景中的所有货物对象（名称包含"pallet"或"货物"的对象）
      const existingPallet = that.checkPalletAtPosition(fromCell.position);
      if (existingPallet) {
        return existingPallet; // 返回已存在的货物对象
      }

      // 处理托盘代码
      if (!palletCode) {
        // 生成唯一的托盘代码
        const timestamp = Date.now();
        palletCode = `${lineName}_${direction}_pallet_${timestamp}`;
      }

      // 移除可能存在的同名托盘
      try {
        that.remove(palletCode);
      } catch (error) {
        console.error("移除同名托盘失败：", error);
      }

      // 在起点生成货物
      var pallet = null;
      try {
        pallet = that.pallet(palletCode, fromCell);
        if (pallet) {
        } else {
          console.error(`货物生成失败：${palletCode}`);
        }
      } catch (error) {
        console.error(`生成货物时发生错误：${error}`);
      }

      return pallet;
    },

    /**
     * 计算距离指定位置最近的物流线端点
     * @param {string} lineName - 物流线名称
     * @param {THREE.Vector3} targetPosition - 目标位置
     * @returns {Object|null} - 返回最近端点的信息，包括code和position，或者null
     */
    getNearestLineEndpoint(lineName, targetPosition) {
      var that = this;

      // 检查ThreeEngine是否初始化
      if (!that.ThreeEngine) {
        console.error('ThreeEngine 未初始化，无法计算最近端点');
        return null;
      }

      // 验证物流线名称格式
      if (!/^\d+-\d+$/.test(lineName)) {
        console.error("物流线名称格式错误，应为'层数-站台号'，如'1-2'");
        return null;
      }

      // 获取物流线的两个端点
      const endpoints = [
        {
          code: lineName + "_from",
          position: null
        },
        {
          code: lineName + "_to",
          position: null
        }
      ];

      // 获取端点的位置信息
      for (let i = 0; i < endpoints.length; i++) {
        const endpoint = endpoints[i];
        const endpointObj = that.ThreeEngine.getObjectByName(endpoint.code);
        if (!endpointObj) {
          console.error(`未找到端点对象：${endpoint.code}`);
          return null;
        }
        endpoint.position = endpointObj.position.clone();
      }

      // 计算每个端点到目标位置的距离
      let nearestEndpoint = null;
      let shortestDistance = Infinity;

      endpoints.forEach(endpoint => {
        const distance = endpoint.position.distanceTo(targetPosition);
        if (distance < shortestDistance) {
          shortestDistance = distance;
          nearestEndpoint = endpoint;
        }
      });

      return nearestEndpoint;
    },

    /**
     * 检查指定位置是否已有货物
     * @param {THREE.Vector3} position - 要检查的位置
     * @returns {THREE.Object3D|null} - 如果有货物返回货物对象，否则返回null
     */
    checkPalletAtPosition(position) {
      var that = this;

      // 遍历场景中的所有对象，检查是否有货物在指定位置附近
      const threshold = 0.1; // 位置误差阈值
      let existingPallet = null;

      that.ThreeEngine.scene.traverse(function (object) {
        // 检查对象是否是货物（名称包含"pallet"、"货物"或特定前缀）
        if (object.name && (object.name.includes('pallet') || object.name.includes('货物') ||
          object.name.match(/^\d+-\d+_.+_pallet_/) || object.name.startsWith('OSA'))) {

          // 检查位置是否接近
          const distance = object.position.distanceTo(position);
          if (distance < threshold) {
            existingPallet = object;
          }
        }
      });

      return existingPallet;
    },

    /**
     * 货物在物流线两端之间移动
     * @param {string} lineName - 物流线名称，格式为"层数-站台号"，如"1-2"
     * @param {string|Object} directionOrSourcePosition - 移动方向("fromTo"或"toFrom")或货物源位置(THREE.Vector3)
     * @param {string} [palletCode] - 托盘名称，可选
     * @param {number} [duration=2000] - 移动持续时间(毫秒)
     */
    moveCargoOnLine(lineName, directionOrSourcePosition, palletCode, duration = 2000) {
      var that = this;

      // 检查ThreeEngine是否初始化
      if (!that.ThreeEngine) {
        console.error('ThreeEngine 未初始化，无法进行货物移动');
        return;
      }

      // 验证物流线名称格式
      if (!/^\d+-\d+$/.test(lineName)) {
        console.error("物流线名称格式错误，应为'层数-站台号'，如'1-2'");
        return;
      }

      let fromCellCode, toCellCode, direction;

      // 判断参数是方向还是源位置
      if (typeof directionOrSourcePosition === "string" &&
        (directionOrSourcePosition === "fromTo" || directionOrSourcePosition === "toFrom")) {
        // 参数是方向
        direction = directionOrSourcePosition;
        fromCellCode = direction === "fromTo" ? lineName + "_from" : lineName + "_to";
        toCellCode = direction === "fromTo" ? lineName + "_to" : lineName + "_from";
      } else if (directionOrSourcePosition && directionOrSourcePosition.isVector3) {
        // 参数是源位置，计算最近的端点
        const nearestEndpoint = that.getNearestLineEndpoint(lineName, directionOrSourcePosition);
        if (!nearestEndpoint) {
          console.error('无法确定最近的物流线端点');
          return;
        }
        fromCellCode = nearestEndpoint.code;
        toCellCode = nearestEndpoint.code === lineName + "_from" ? lineName + "_to" : lineName + "_from";
        direction = nearestEndpoint.code === lineName + "_from" ? "fromTo" : "toFrom";
      } else {
        console.error("移动方向或源位置参数错误，应为'fromTo'、'toFrom'或THREE.Vector3对象");
        return;
      }

      // 检查起点和终点是否存在
      const fromCell = that.ThreeEngine.getObjectByName(fromCellCode);
      const toCell = that.ThreeEngine.getObjectByName(toCellCode);

      if (!fromCell) {
        console.error(`未找到起点点位：${fromCellCode}`);
        return;
      }

      if (!toCell) {
        console.error(`未找到终点点位：${toCellCode}`);
        return;
      }

      // 先检查终点位置是否已有货物
      const existingPalletAtEnd = that.checkPalletAtPosition(toCell.position);
      if (existingPalletAtEnd) {
        console.error(`终点位置 ${toCellCode} 已有货物：${existingPalletAtEnd.name}，无法再移动货物`);
        return;
      }

      // 在起点生成货物
      const generatedPallet = that.generateCargoOnLineFrom(lineName, direction, palletCode);
      if (!generatedPallet) {
        console.error("货物生成失败，无法进行移动操作");
        return;
      }

      // 设置物流线状态为正在移动
      that.conveyorLineStatus[lineName] = 'moving';

      // 创建移动动画
      try {
        var tween = new TWEEN.Tween(generatedPallet.position)
          .to(
            {
              x: toCell.position.x,
              y: toCell.position.y,
              z: toCell.position.z
            },
            duration
          )
          .easing(TWEEN.Easing.Quadratic.InOut)
          .onStart(() => {
          })
          .onComplete(() => {
            // 移动完成后，将物流线状态恢复为空闲
            that.conveyorLineStatus[lineName] = 'free';
          })
          .start();
      } catch (error) {
        console.error("创建货物移动动画失败：", error);
        // 发生错误时，将物流线状态恢复为空闲
        that.conveyorLineStatus[lineName] = 'free';
      }
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

      if (!that.ThreeEngine) {
        console.error('ThreeEngine 未初始化，无法生成货物');
        return null;
      }

      if (!obj) {
        console.error('生成货物时未提供目标位置对象');
        return null;
      }

      that.remove(palletCode);

      // 获取原始货物模板对象
      var originalPallet = that.ThreeEngine.getObjectByName("货物");
      if (!originalPallet) {
        console.error('未找到原始货物模板对象("pallet")，无法生成货物');
        return null;
      }

      var newPallet = originalPallet.clone();
      newPallet.visible = true;
      newPallet.position.set(obj.position.x, obj.position.y, obj.position.z);
      newPallet.name = palletCode;

      // 应用乳白色材质
      try {
        materialManager.applyMaterialToObject(newPallet, '货物', 'main', 'default');
      } catch (e) {
        console.error('应用材质时出错:', e);
      }

      that.ThreeEngine.addObject(newPallet);
      if (
        palletCode != null &&
        palletCode.length > 5 &&
        palletCode.substring(0, 3) == "OSA"
      ) {
        try {
          this.createSprite(palletCode, palletCode);
        } catch (e) {
          console.error('创建货物标签时出错:', e);
        }
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
        }

        // 启用无人机模式
        this.firstPersonController.enable();

        // 启用炮弹系统
        if (this.projectileSystem) {
          this.projectileSystem.enable();
        }

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
      // 精简调试信息，只保留关键测试结果
      // 手动设置测试值
      if (this.firstPersonController) {
        this.firstPersonController.moveState.forward = true;
        setTimeout(() => {
          this.firstPersonController.moveState.forward = false;
        }, 2000);
      }
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
    background: radial-gradient(ellipse at center,
        rgba(0, 0, 0, 0.1) 0%,
        rgba(0, 0, 0, 0.3) 70%,
        rgba(0, 0, 0, 0.5) 100%);
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
  background: radial-gradient(ellipse at center,
      rgba(20, 30, 48, 0.7) 0%,
      rgba(20, 30, 48, 0.5) 40%,
      rgba(20, 30, 48, 0.2) 70%,
      transparent 100%);
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
  top: 120px; // 向下移动，避开Header组件（Header高度为10vh）
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

// 货物移动面板样式
.panel-move-cargo {
  top: 410px; // 精确放置在调试控制面板正下方（调试控制面板底部约为408px）
  right: 20px; // 右侧靠边显示，与调试控制面板对齐
  width: 300px;
  height: 200px;
  padding: 0;
  background: radial-gradient(ellipse at center,
      rgba(20, 30, 48, 0.9) 0%,
      rgba(20, 30, 48, 0.7) 40%,
      rgba(20, 30, 48, 0.5) 70%,
      transparent 100%);
  z-index: 100; // 确保不被其他面板遮挡
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
  background: radial-gradient(ellipse at center,
      rgba(20, 30, 48, 0.9) 0%,
      rgba(20, 30, 48, 0.7) 40%,
      rgba(20, 30, 48, 0.4) 70%,
      transparent 100%);
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

.progress-box {
  border-radius: 5vh;
  height: 25vh;
  // 更自然的背景
  background: radial-gradient(ellipse at center,
      rgba(20, 30, 48, 0.8) 0%,
      rgba(20, 30, 48, 0.6) 40%,
      rgba(20, 30, 48, 0.3) 70%,
      rgba(20, 30, 48, 0.1) 100%);
  width: 30vw;
  margin-left: 30vw;
  margin-top: 30vh;
  position: absolute;
  z-index: 999;
  // 更柔和的边框效果
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow:
    0 0 10px rgba(64, 158, 255, 0.1),
    inset 0 0 15px rgba(0, 0, 0, 0.3);
  // 更轻的毛玻璃效果
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);

  .name {
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
    width: 50%;
    /* 进度条的初始宽度，可以根据需要更改 */
    height: 20px;
    background-color: #4caf50;
    border-radius: 4px;
    transition: width 1s ease-in-out;
    /* 进度条变化的动画效果 */
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
  z-index: 20; // 高于Header、Left和Bottom组件的z-index(10)，确保不被遮挡

  // 子元素恢复pointer-events
  >* {
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 200px;
  // 更轻的毛玻璃效果
  backdrop-filter: blur(3px);
  border: 1px solid rgba(255, 255, 255, 0.1);
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

  0%,
  100% {
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

  0%,
  100% {
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