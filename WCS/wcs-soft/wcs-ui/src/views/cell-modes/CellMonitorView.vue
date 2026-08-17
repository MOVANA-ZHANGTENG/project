<template>
  <div class="cell-monitor-view">
    <el-row :gutter="0">
      <!-- 中间：货位展示区域 -->
      <el-col :span="20">
        <el-card class="diagram-card">
          <!-- 监控模式路径状态图例 - 紧凑单行显示 -->
          <div class="monitor-legend-compact">
            <div class="legend-item">
              <svg width="35" height="18" style="margin-right: 6px;">
                <line x1="0" y1="9" x2="35" y2="9" stroke="#cbd5e0" stroke-width="4" stroke-dasharray="4,4"/>
                <polygon points="30,9 35,6 35,12" fill="#cbd5e0"/>
              </svg>
              <span>未占用</span>
            </div>
            <div class="legend-item">
              <svg width="35" height="18" style="margin-right: 6px;">
                <line x1="0" y1="9" x2="35" y2="9" stroke="#f6ad55" stroke-width="6"/>
                <polygon points="30,9 35,6 35,12" fill="#f6ad55"/>
              </svg>
              <span>已占用</span>
            </div>
            <div class="legend-item">
              <svg width="35" height="18" style="margin-right: 6px;">
                <line x1="0" y1="9" x2="35" y2="9" stroke="#48bb78" stroke-width="8" stroke-dasharray="8,4">
                  <animate attributeName="stroke-dashoffset" from="0" to="-12" dur="0.5s" repeatCount="indefinite"/>
                </line>
                <polygon points="30,9 35,6 35,12" fill="#48bb78"/>
              </svg>
              <span>执行中</span>
            </div>
            <div class="legend-item">
              <svg width="35" height="18" style="margin-right: 6px;">
                <line x1="0" y1="9" x2="35" y2="9" stroke="#4a5568" stroke-width="5"/>
                <polygon points="30,9 35,6 35,12" fill="#4a5568"/>
              </svg>
              <span>已完成</span>
            </div>
          </div>

          <div id="diagramDivMonitor" class="diagram-container"></div>
        </el-card>
      </el-col>

      <!-- 右侧：小车状态面板 -->
      <el-col :span="4">
        <div class="info-panel">
          <div class="info-header">
            <i class="el-icon-truck"></i>
            <span>小车状态</span>
          </div>
          <div class="info-content">
            <div class="monitor-container">
              <!-- WebSocket连接状态 -->
              <div class="status-section">
                <div class="status-item">
                  <span class="status-label">WebSocket:</span>
                  <span class="status-value" :class="{ 'connected': wsConnected, 'disconnected': !wsConnected }">
                    {{ wsConnected ? '● 已连接' : '● 未连接' }}
                  </span>
                </div>
                <div class="status-item">
                  <span class="status-label">推送模式:</span>
                  <span class="status-value push-mode">
                    <i class="el-icon-s-data"></i> 按需推送
                  </span>
                </div>
              </div>

              <!-- 小车列表（带颜色标识） -->
              <div class="car-list-section">
                <div class="section-title">
                  <i class="el-icon-position"></i>
                  <span>小车列表 ({{ Object.keys(carPositions).length }})</span>
                </div>

                <!-- 测试充电动画：临时测试数据 -->
                <div v-if="false" class="car-item test-car">
                  <div class="car-header">
                    <div class="car-name-row">
                      <span class="car-color-indicator" style="background-color: #FF6B6B;"></span>
                      <span class="car-name">测试小车 (充电中)</span>
                    </div>
                    <div class="car-status-row">
                      <span class="status-text online">● 在线</span>
                      <span class="status-divider">|</span>
                      <span class="status-text task-0">空闲</span>
                    </div>
                  </div>
                  <div class="car-info">
                    <div class="info-row battery-row">
                      <i class="el-icon-s-operation battery-icon-charging"></i>
                      <span class="battery-text battery-charging">
                        85%
                        <i class="el-icon-lightning charging-icon"></i>
                      </span>
                    </div>
                  </div>
                </div>

                <div v-if="Object.keys(carPositions).length > 0" class="color-legend">
                  <div class="legend-tip">
                    <i class="el-icon-info"></i>
                    <span>路径边框颜色对应小车</span>
                  </div>
                </div>

                <div v-if="Object.keys(carPositions).length === 0" class="empty-tip">
                  暂无小车数据
                </div>

                <div v-else class="car-list">
                  <div v-for="(car, carCode) in carPositions" :key="carCode" class="car-item">
                    <div class="car-header">
                      <!-- 小车名称行 -->
                      <div class="car-name-row">
                        <span
                          class="car-color-indicator"
                          :style="{ backgroundColor: getCarColorForDisplay(car) }"
                          :title="'此小车的路径边框颜色'"
                        ></span>
                        <span class="car-name">{{ car.carName || carCode }}</span>
                      </div>

                      <!-- 在线状态行 -->
                      <div class="car-status-row">
                        <span class="status-text" :class="{ 'online': car.isConnected === 1, 'offline': car.isConnected === 0 }">
                          {{ car.isConnected === 1 ? '● 在线' : '● 离线' }}
                        </span>
                        <span class="status-divider">|</span>
                        <span class="status-text" :class="'task-' + car.taskState">
                          {{ getTaskStateLabel(car.taskState) }}
                        </span>
                      </div>
                    </div>
                    <div class="car-info">
                      <div class="info-row position-row">
                        <i class="el-icon-location-outline"></i>
                        <span class="position-text">
                          <span v-if="car.fromCellCode === car.toCellCode">{{ car.fromCellCode || '-' }}</span>
                          <span v-else>{{ car.fromCellCode || '-' }} → {{ car.toCellCode || '-' }}</span>
                        </span>
                        <el-button
                          type="text"
                          icon="el-icon-edit"
                          size="mini"
                          class="edit-position-btn"
                          @click="openEditCarPositionDialog(car)"
                          title="修改起点位置"
                        ></el-button>
                      </div>
                      <!-- <div class="info-row">
                        <i class="el-icon-odometer"></i>
                        <span>进度: {{ (car.positionRatio * 100).toFixed(0) }}%</span>
                      </div> -->
                      <div class="info-row">
                        <i class="el-icon-lightning"></i>
                        <span>速度: {{ car.speed || 0 }} m/s</span>
                      </div>
                      <div class="info-row battery-row">
                        <i class="el-icon-s-operation" :class="getBatteryIconClass(car)"></i>
                        <span class="battery-text" :class="getBatteryClass(car)">
                          {{ car.batteryLevel || 0 }}%
                          <i v-if="isCarCharging(car)" class="el-icon-lightning charging-icon"></i>
                        </span>
                      </div>
                      <div class="info-row">
                        <i class="el-icon-box"></i>
                        <span :style="{color: car.loadState === 1 ? '#E6A23C' : '#67C23A'}">
                          负载: {{ car.loadState === 1 ? '有货' : car.loadState === 0 ? '空载' : '-' }}
                        </span>
                      </div>
                      <!-- <div class="info-row">
                        <i class="el-icon-connection"></i>
                        <span>连接状态: {{ car.isConnected === 1 ? '已连接' : '未连接' }}</span>
                      </div> -->
                    </div>
                  </div>
                </div>
              </div>

              <!-- 统计信息 -->
              <div class="statistics-section">
                <div class="section-title">
                  <i class="el-icon-data-analysis"></i>
                  <span>统计信息</span>
                </div>
                <div class="stat-items">
                  <div class="stat-item">
                    <span class="stat-label">路径总数:</span>
                    <span class="stat-value">{{ pathCount }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">执行中:</span>
                    <span class="stat-value" style="color: #48bb78;">{{ executingPathCount }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">总小车数:</span>
                    <span class="stat-value" style="color: #4facfe;">{{ Object.keys(carPositions).length }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">在线小车:</span>
                    <span class="stat-value" style="color: #48bb78;">{{ onlineCarCount }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">离线小车:</span>
                    <span class="stat-value" style="color: #f56565;">{{ offlineCarCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 修改小车位置对话框 -->
    <el-dialog
      title="修改小车位置（静止状态）"
      :visible.sync="editPositionDialogVisible"
      width="500px"
      :close-on-click-modal="false"
      :modal-append-to-body="true"
      :append-to-body="true"
      custom-class="car-position-dialog"
    >
      <el-alert
        title="说明：修改小车静止位置，系统会同时更新起点和终点为相同位置"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 15px;">
      </el-alert>

      <el-form :model="editPositionForm" :rules="editPositionRules" ref="editPositionForm" label-width="100px">
        <el-form-item label="小车编码">
          <el-input v-model="editPositionForm.carCode" disabled></el-input>
        </el-form-item>
        <el-form-item label="小车名称">
          <el-input v-model="editPositionForm.carName" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前位置" prop="currentFromCellCode">
          <el-input v-model="editPositionForm.currentFromCellCode" disabled>
            <template slot="prepend">
              <i class="el-icon-location-outline"></i>
            </template>
          </el-input>
          <div class="form-tip" v-if="editPositionForm.currentFromCellCode !== editPositionForm.toCellCode">
            <i class="el-icon-warning" style="color: #E6A23C;"></i>
            <span style="color: #E6A23C;">小车正在移动中（{{ editPositionForm.currentFromCellCode }} → {{ editPositionForm.toCellCode }}）</span>
          </div>
        </el-form-item>
        <el-form-item label="新位置" prop="newFromCellCode">
          <el-autocomplete
            v-model="editPositionForm.newFromCellCode"
            :fetch-suggestions="queryCellCodeSuggestions"
            placeholder="请输入新的库位编码"
            style="width: 100%;"
            clearable
          >
            <template slot="prepend">
              <i class="el-icon-map-location" style="color: #409EFF;"></i>
            </template>
            <template slot-scope="{ item }">
              <div class="cell-suggestion-item">
                <span class="cell-code">{{ item.value }}</span>
                <span class="cell-type" v-if="item.typeLabel">{{ item.typeLabel }}</span>
              </div>
            </template>
          </el-autocomplete>
          <div class="form-tip">
            <i class="el-icon-info"></i>
            <span>输入库位编码，支持模糊搜索当前楼层的库位</span>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editPositionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditCarPosition" :loading="editPositionLoading">
          <i class="el-icon-check"></i> 确定修改
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCellInfo } from "@/api/wcs-base/CellInfo";
import { listRcsCarPath } from "@/api/wcs-rcs/RcsCarPath";
import { listRcsCarInfo } from "@/api/wcs-rcs/RcsCarInfo";
import request from "@/utils/request";

export default {
  name: "CellMonitorView",
  props: {
    wareCode: {
      type: String,
      required: true
    },
    floor: {
      type: Number,
      default: null
    },
    gridSize: {
      type: Object,
      default: () => ({ width: 50, height: 50 })
    }
  },
  data() {
    return {
      myDiagram: null,
      cellInfos: [],
      floors: [],
      lineModelData: {
        class: "GraphLinksModel",
        nodeDataArray: [],
        linkDataArray: []
      },
      // WebSocket相关
      carWebSocket: null,
      carPositions: {},
      heartbeatTimer: null,
      wsConnected: false,

      // 路径动画相关
      animationTimer: null,
      previousLinkDataArray: [],

      // 统计数据
      pathCount: 0,
      executingPathCount: 0,

      // 小车颜色映射（用于路径边框区分）
      carColorMap: {},
      carColors: [
        '#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A',
        '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E2',
        '#F8B500', '#52BE80', '#EC7063', '#5DADE2',
        '#AF7AC5', '#48C9B0', '#F39C12', '#3498DB'
      ],

      // 楼层加载标志
      isLoadingFloor: false,

      // 修改小车位置对话框
      editPositionDialogVisible: false,
      editPositionLoading: false,
      editPositionForm: {
        carId: null,
        carCode: '',
        carName: '',
        currentFromCellCode: '',
        newFromCellCode: '',
        toCellCode: ''
      },
      editPositionRules: {
        newFromCellCode: [
          { required: true, message: '请输入新的起点库位编码', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    // 计算在线小车数量
    onlineCarCount() {
      return Object.values(this.carPositions).filter(car => car.isConnected === 1).length;
    },
    // 计算离线小车数量
    offlineCarCount() {
      return Object.values(this.carPositions).filter(car => car.isConnected === 0).length;
    }
  },
  watch: {
    wareCode: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.getFloors(newVal);
        }
      }
    },
    floor(newVal, oldVal) {
      if (newVal && newVal !== oldVal) {
        if (this.myDiagram) {
          this.getCellInfos(newVal);
        }
        // WebSocket会自动处理楼层切换后的推送
        this.changeFloorSubscription(newVal);
      }
    }
  },
  mounted() {
    this.initDiagram();

    if (this.wareCode && this.floor) {
      this.getFloors(this.wareCode).then(() => {
        this.getCellInfos(this.floor);
      });
    }

    // 延迟启动WebSocket（统一推送小车位置和路径状态）
    setTimeout(() => {
      if (this.wareCode && this.floor) {
        this.connectCarPositionWebSocket();
      }
    }, 1500);
  },
  beforeDestroy() {
    this.cleanup();
  },
  methods: {
    initDiagram() {
      if (this.myDiagram) {
        return;
      }

      var that = this;
      var $ = go.GraphObject.make;
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.height);

      this.myDiagram = $(
        go.Diagram,
        "diagramDivMonitor",
        {
          initialAutoScale: go.Diagram.Uniform,
          "draggingTool.isGridSnapEnabled": true,
          "draggingTool.gridSnapCellSpot": go.Spot.Center,
          "resizingTool.isGridSnapEnabled": true,
          // 鼠标滚轮缩放配置
          "commandHandler.zoomFactor": 1.1, // 缩放因子，每次滚轮滚动的缩放比例
          "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom, // 鼠标滚轮行为：缩放
          "undoManager.isEnabled": true,
          isReadOnly: true,
        }
      );

      // 节点模板辅助函数
      function nodeStyle() {
        return [
          new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
          { locationSpot: go.Spot.TopLeft },
        ];
      }

      function textStyle() {
        return {
          font: "bold 8pt Lato, Helvetica, Arial, sans-serif",
          stroke: "#303133",
        };
      }

      // 库位节点模板（带端口，用于连接线）
      this.myDiagram.nodeTemplateMap.add(
        "cell_can_link",
        $(
          go.Node,
          nodeStyle(),
          {
            movable: false,
            copyable: false,
            deletable: false
          },
          $(
            go.Panel,
            "Spot",
            $(
              go.Shape,
              "Circle",  // 改为圆形
              {
                desiredSize: CellSize,
                strokeWidth: 3,
              },
              new go.Binding("fill", "fillColor"),
              new go.Binding("stroke", "borderColor"),
              new go.Binding("desiredSize", "cellSize", go.Size.parse)  // 根据cellSize动态调整大小
            ),
            $(
              go.TextBlock,
              "",
              textStyle(),
              new go.Binding("text", "text"),
              new go.Binding("stroke", "textColor"),
              new go.Binding("font", "cellSize", function(size) {
                // 根据节点大小调整字体
                if (size) {
                  var sizeObj = go.Size.parse(size);
                  if (sizeObj.width < 30) {
                    return "bold 6pt Lato, Helvetica, Arial, sans-serif";
                  }
                }
                return "bold 8pt Lato, Helvetica, Arial, sans-serif";
              })
            ),
          ),
          {
            click: function (e, node) {
              console.log("点击库位:", node.data.key);
            },
            cursor: "pointer",
          }
        ),
      );

      // 添加小车节点模板
      this.myDiagram.nodeTemplateMap.add(
        "car",
        $(
          go.Node,
          "Spot",
          {
            locationSpot: go.Spot.TopLeft,  // 与库位节点保持一致，使用左上角作为参考点
            selectable: true,
            movable: false,
            layerName: "Foreground"
          },
          // 添加location绑定，将loc字符串转换为Point对象
          new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
          $(
            go.Panel,
            "Auto",
            $(
              go.Shape,
              "RoundedRectangle",
              {
                fill: "#4CAF50",
                stroke: "#2E7D32",
                strokeWidth: 2.5,
                desiredSize: CellSize,  // 使用与库位相同的尺寸
                parameter1: 8  // 圆角半径
              },
              new go.Binding("fill", "taskState", function(state) {
                switch(state) {
                  case 0: return "#9E9E9E";  // 空闲 - 灰色
                  case 1: return "#FFC107";  // 任务中 - 橙色
                  case 2: return "#4CAF50";  // 执行中 - 绿色
                  default: return "#9E9E9E";
                }
              }),
              new go.Binding("stroke", "taskState", function(state) {
                switch(state) {
                  case 0: return "#757575";
                  case 1: return "#F57C00";
                  case 2: return "#2E7D32";
                  default: return "#757575";
                }
              })
            ),
            $(
              go.Panel,
              "Vertical",
              {
                alignment: go.Spot.Center
              },
              $(
                go.TextBlock,
                {
                  text: "🚗",
                  font: "bold 20px sans-serif",
                  margin: new go.Margin(2, 0, 2, 0)
                }
              ),
              $(
                go.TextBlock,
                {
                  font: "bold 10px sans-serif",
                  stroke: "#FFFFFF",
                  margin: 0
                },
                new go.Binding("text", "code")
              ),
              $(
                go.TextBlock,
                {
                  font: "9px sans-serif",
                  stroke: "#FFFFFF",
                  margin: new go.Margin(1, 0, 0, 0)
                },
                new go.Binding("text", "batteryLevel", function(level) {
                  return "🔋 " + (level || 0) + "%";
                })
              )
            )
          ),
          // 方向指示箭头（位于节点外部）
          $(
            go.Shape,
            "Triangle",
            {
              alignment: go.Spot.Right,
              alignmentFocus: go.Spot.Center,
              width: 12,
              height: 12,
              fill: "#FF5722",
              stroke: "#FFFFFF",
              strokeWidth: 1,
              angle: 0
            },
            new go.Binding("visible", "speed", function(speed) {
              return Boolean(speed && speed > 0);  // 确保返回布尔值
            }),
            new go.Binding("angle", "direction", function(dir) {
              // 根据方向调整箭头角度
              // 1=右, 2=左, 3=上, 4=下
              switch(dir) {
                case 1: return 0;    // 右
                case 2: return 180;  // 左
                case 3: return 270;  // 上
                case 4: return 90;   // 下
                default: return 0;
              }
            }),
            new go.Binding("alignment", "direction", function(dir) {
              // 根据方向调整箭头位置
              switch(dir) {
                case 1: return go.Spot.Right;
                case 2: return go.Spot.Left;
                case 3: return go.Spot.Top;
                case 4: return go.Spot.Bottom;
                default: return go.Spot.Right;
              }
            })
          )
        )
      );

      // 连接线模板（管道效果：边框包围主线）
      this.myDiagram.linkTemplate = $(
        go.Link,
        {
          routing: go.Link.AvoidsNodes,  // 避开节点的路由
          curve: go.Link.JumpGap,  // 跳过交叉
          corner: 10,  // 拐角半径
          selectionAdorned: true,
          selectable: true,
          layerName: "Background",  // 背景层，不遮挡节点
        },
        // 第1层：外层边框（小车专属颜色，最粗，静态实线，最底层）
        $(
          go.Shape,
          {
            isPanelMain: true,  // 关键：获取连接线几何形状
            stroke: "transparent",  // 默认透明
            strokeWidth: 15,  // 最粗的边框
            name: "OUTER_BORDER",
            opacity: 0.3  // 完全透明
            // 注意：外层边框不设置 strokeDashOffset 和 strokeDashArray
          },
          new go.Binding("stroke", "borderColor"),
          new go.Binding("visible", "borderColor", (c) => c && c !== "transparent")
        ),

        // 第2层：主路径线（带状态颜色和动画效果，最上层）
        $(
          go.Shape,
          {
            isPanelMain: true,  // 关键：获取连接线几何形状
            stroke: "#4facfe",
            strokeWidth: 6,  // 主线宽度
            name: "MAIN_PIPE",
            strokeDashOffset: 0,  // 初始偏移量，动画会动态修改
            opacity: 0.8
          },
          new go.Binding("stroke", "color"),  // 使用原始状态颜色
         // new go.Binding("strokeWidth", "thickness", (t) => t || 3),
          new go.Binding("strokeDashArray", "dashArray")
        ),
        // 第4层：箭头（方向指示，顶层）
        $(
          go.Shape,
          {
            toArrow: "Triangle",
            scale: 0.8,  // 稍大的箭头
            fill: "#4facfe",
            stroke: null  // 无边框
          },
          new go.Binding("fill", "color")
        )
      );

      this.load();
    },

    load() {
      var data = {
        class: "GraphLinksModel",
        linkFromPortIdProperty: "fromPort",
        linkToPortIdProperty: "toPort",
        nodeDataArray: [],
      };
      this.myDiagram.model = go.Model.fromJson(data);
    },

    getCellInfos(floor) {
      var that = this;

      console.log("🔄 监控模式 - 切换到楼层:", floor, "- 清空所有数据");

      // 设置标志位
      this.isLoadingFloor = true;

      // 清空所有数据
      this.cellInfos = [];
      this.previousLinkDataArray = [];

      if (!floor) {
        this.isLoadingFloor = false;
        return;
      }

      if (this.floors.length === 0) {
        this.getFloors(this.wareCode).then(() => {
          this.getCellInfos(floor);
        });
        this.isLoadingFloor = false;
        return;
      }

      that.lineModelData = {
        class: "GraphLinksModel",
        nodeDataArray: [],
        linkDataArray: []
      };

      // 清空 diagram 模型
      if (that.myDiagram) {
        var oldNodeCount = that.myDiagram.model.nodeDataArray ? that.myDiagram.model.nodeDataArray.length : 0;
        var oldLinkCount = that.myDiagram.model.linkDataArray ? that.myDiagram.model.linkDataArray.length : 0;

        var newModel = new go.GraphLinksModel();
        newModel.linkFromPortIdProperty = "fromPort";
        newModel.linkToPortIdProperty = "toPort";
        that.myDiagram.model = newModel;

        console.log("✓ 监控模式 Diagram 模型已完全替换 - 清除了", oldNodeCount, "个节点和", oldLinkCount, "条连接线");
      }

      listCellInfo({
        wareCode: that.wareCode,
        z: floor,
        isDelete: 0,
        pageSize: 999,
      }).then((response) => {
        this.loadData({});
        if (response.code == 200) {
          this.cellInfos = response.rows;

          var floorInfo = that.getFloorInfo(floor);

          if (!floorInfo) {
            console.error("找不到楼层信息:", floor);
            return;
          }

          var xy = floorInfo.xy;
          var totalX = floorInfo.totalX;
          var totalY = floorInfo.totalY;

          // 监控模式：添加间隔（坐标*2）
          this.cellInfos.forEach((cell) => {
            var dispalyX = 0;
            var dispalyY = 0;
            switch (xy) {
              case 1:
                dispalyX = cell.x * 2;
                dispalyY = cell.y * 2;
                break;
              case 2:
                dispalyX = (totalX - cell.x + 1) * 2;
                dispalyY = cell.y * 2;
                break;
              case 3:
                dispalyX = cell.x * 2;
                dispalyY = (totalY - cell.y + 1) * 2;
                break;
              case 4:
                dispalyX = (totalX - cell.x + 1) * 2;
                dispalyY = (totalY - cell.y + 1) * 2;
                break;
            }

            cell.dispalyX = dispalyX;
            cell.dispalyY = dispalyY;
            this.cellColor(cell);
            cell.category = "cell_can_link";
            this.addNode(cell);
          });

          // 添加坐标轴
          switch (xy) {
            case 1:
              addXAxis(0, false, 2);
              addYAxis(0, false, 2);
              break;
            case 2:
              addXAxis(totalY + 1, true, 2);
              addYAxis(0, false, 2);
              break;
            case 3:
              addXAxis(0, false, 2);
              addYAxis(totalX + 1, true, 2);
              break;
            case 4:
              addXAxis(totalY + 1, true, 2);
              addYAxis(totalX + 1, true, 2);
              break;
          }

          function addXAxis(yPos, fz, multiplier = 1) {
            for (let x = 1; x <= totalX; x++) {
              var code = `X${x}`;
              if (fz) {
                code = `X${totalX - x + 1}`;
              }
              var xCell = {
                dispalyX: x * multiplier,
                dispalyY: yPos * multiplier,
                code: code,
                fillColor: "transparent",
                textColor: "#a0aec0",
                borderColor: "transparent"
              };
              that.addNode(xCell);
            }
          }

          function addYAxis(xPos, fz, multiplier = 1) {
            for (let y = 1; y <= totalY; y++) {
              var code = `Y${y}`;
              if (fz) {
                code = `Y${totalY - y + 1}`;
              }
              var yCell = {
                dispalyX: xPos * multiplier,
                dispalyY: y * multiplier,
                code: code,
                fillColor: "transparent",
                textColor: "#a0aec0",
                borderColor: "transparent"
              };
              that.addNode(yCell);
            }
          }

          // 加载监控路径连接线
          this.addMonitorConnectionLines();
        }
      });
    },

    // 加载监控路径（从rcs_car_path表）
    addMonitorConnectionLines() {
      var that = this;

      const cellIdToCodeMap = {};
      this.cellInfos.forEach((cell) => {
        if (cell.id) {
          cellIdToCodeMap[cell.id] = cell.code;
        }
      });

      listRcsCarPath({
        pageSize: 9999,
      }).then((response) => {
        if (response.code == 200) {
          const carPaths = response.rows;

          // 不再需要复合键映射，因为路径状态直接在path对象中

          // 构建路径状态映射，支持多车同路径（与增量更新逻辑一致）
          const pathStateMap = new Map();
          this.executingPathCount = 0;

          carPaths.forEach((path) => {
            const fromCellCode = cellIdToCodeMap[path.fromCellId];
            const toCellCode = cellIdToCodeMap[path.toCellId];

            if (fromCellCode && toCellCode) {
              // 路径状态直接使用path.state
              const pathState = path.state;

              const pathKey = `${fromCellCode}=>${toCellCode}`;
              const existingState = pathStateMap.get(pathKey);

              if (!existingState) {
                // 第一次遇到这条路径
                pathStateMap.set(pathKey, {
                  fromCellCode: fromCellCode,
                  toCellCode: toCellCode,
                  pathState: pathState,
                  carCount: 1,
                  rcsCarIds: [path.rcsCarId]
                });

                if (pathState === 2) {
                  that.executingPathCount++;
                }
              } else {
                // 路径已存在，合并小车信息
                existingState.carCount++;
                existingState.rcsCarIds.push(path.rcsCarId);

                // 状态优先级比较：执行中(2) > 已占用(1) > 未占用(0) > 已完成(3)
                const shouldUpdate =
                  (pathState === 2) ||
                  (pathState === 1 && existingState.pathState !== 2) ||
                  (pathState === 0 && existingState.pathState === 3);

                if (shouldUpdate) {
                  if (existingState.pathState !== 2 && pathState === 2) {
                    that.executingPathCount++;
                  } else if (existingState.pathState === 2 && pathState !== 2) {
                    that.executingPathCount--;
                  }
                  existingState.pathState = pathState;
                }
              }
            }
          });

          // 根据合并后的路径状态创建连接线
          pathStateMap.forEach((stateInfo) => {
            let lineColor = "#4facfe";
            let lineThickness = 5;
            let dashArray = null;

            switch(stateInfo.pathState) {
              case 0:
                lineColor = "#cbd5e0";
                lineThickness = 1;  // 未占用：细线
                dashArray = [4, 4];
                break;
              case 1:
                lineColor = "#f6ad55";
                lineThickness = 2;  // 已占用：正常
                dashArray = null;
                break;
              case 2:
                lineColor = "#48bb78";
                lineThickness = 3;  // 执行中：稍粗
                dashArray = [8, 4];
                break;
              case 3:
                lineColor = "#4a5568";
                lineThickness = 2;  // 已完成：正常
                dashArray = null;
                break;
            }

            // 计算边框颜色（用于区分不同小车）
            let borderColor = 'transparent';
            if (stateInfo.carCount === 1) {
              // 单车路径：使用小车专属颜色作为边框
              borderColor = that.getCarColor(stateInfo.rcsCarIds[0]);
            } else if (stateInfo.carCount > 1) {
              // 多车路径：使用第一辆车的颜色（半透明）
              borderColor = that.getCarColor(stateInfo.rcsCarIds[0]) + '80'; // 添加50%透明度
            }

            const linkData = {
              from: stateInfo.fromCellCode,
              to: stateInfo.toCellCode,
              color: lineColor,  // 原始状态颜色
              thickness: lineThickness,
              borderColor: borderColor,  // ✨ 小车专属边框颜色
              fromPort: "",
              toPort: "",
              pathState: stateInfo.pathState,
              isAnimated: stateInfo.pathState === 2,
              carCount: stateInfo.carCount,  // 记录小车数量
              carIds: stateInfo.rcsCarIds  // 记录小车ID列表
            };

            if (dashArray) {
              linkData.dashArray = dashArray;
            }

            that.lineModelData.linkDataArray.push(linkData);
          });

          // 输出统计信息
          this.pathCount = pathStateMap.size;
          const multiCarPaths = Array.from(pathStateMap.values()).filter(p => p.carCount > 1);
          console.log(`📊 初始加载路径: 共${this.pathCount}条, 执行中${this.executingPathCount}条`);
          if (multiCarPaths.length > 0) {
            console.log(`   📍 多车路径: ${multiCarPaths.length}条`, multiCarPaths.map(p => `${p.fromCellCode}→${p.toCellCode}[${p.carCount}辆]`));
          }

          that.loadData(that.lineModelData);

          that.$nextTick(() => {
            if (that.myDiagram && that.myDiagram.model) {
              that.previousLinkDataArray = JSON.parse(
                JSON.stringify(that.myDiagram.model.linkDataArray || [])
              );
              that.startLinkAnimations();
              // WebSocket订阅后会自动推送路径状态更新
            }

            // 数据加载完成，重置标志位
            that.isLoadingFloor = false;
            console.log("✓ 监控模式 - 楼层切换完成");
          });
        } else {
          console.error("查询车辆路径失败:", response.msg);
          that.loadData(that.lineModelData);
          // 即使失败也要重置标志位
          that.isLoadingFloor = false;
        }
      }).catch((error) => {
        console.error("查询车辆路径出错:", error);
        that.loadData(that.lineModelData);
        // 即使出错也要重置标志位
        that.isLoadingFloor = false;
      });
    },

    // 获取楼层列表
    getFloors(wareCode) {
      return new Promise((resolve, reject) => {
        request({
          url: "/wcs-base/FloorInfo/list",
          method: "get",
          params: { wareCode: wareCode },
        }).then((response) => {
          if (response.code == 200) {
            this.floors = response.rows;
            resolve(this.floors);
          } else {
            reject(response.msg);
          }
        });
      });
    },

    getFloorInfo(floor) {
      for (let index = 0; index < this.floors.length; index++) {
        const floorInfo = this.floors[index];
        if (floorInfo.z == floor) {
          return floorInfo;
        }
      }
      return null;
    },

    cellColor(cellInfo) {
      // 库位类型边框颜色
      const cellTypeBorderColors = {
        0: "#4a5568",  // 普通库位 - 灰色
        1: "#48bb78",  // 入库接驳位 - 绿色
        2: "#4299e1",  // 出库接驳位 - 蓝色
        3: "#ed8936",  // 通用接驳位 - 橙色
        4: "#556270",  // 四向车通道 - 深灰色（非常低调，接近普通库位）
        5: "#f56565"   // 提升机位置 - 红色
      };

      const colorScheme = {
        empty: {
          normal: { fill: "#2d3748", text: "#a0aec0" },
          task: { fill: "#d69e2e", text: "#ffffff" }
        },
        occupied: {
          normal: { fill: "#38a169", text: "#ffffff" },
          task: { fill: "#e53e3e", text: "#ffffff" }
        },
        disabled: { fill: "#ffffff", text: "#2d3748" }
      };

      let colors = {};
      if (cellInfo.disableState == 1) {
        colors = colorScheme.disabled;
      } else if (cellInfo.invenState == 0) {
        colors = cellInfo.taskState != 0 ? colorScheme.empty.task : colorScheme.empty.normal;
      } else {
        colors = cellInfo.taskState != 0 ? colorScheme.occupied.task : colorScheme.occupied.normal;
      }

      cellInfo.fillColor = colors.fill;
      cellInfo.textColor = colors.text;
      // 根据库位类型设置不同的边框颜色
      const cellType = cellInfo.type !== undefined && cellInfo.type !== null ? cellInfo.type : 0;
      cellInfo.borderColor = cellTypeBorderColors[cellType] || cellTypeBorderColors[0];
    },

    addNode(cellInfo) {
      var that = this;

      // 根据库位类型决定尺寸：四向车通道为一半大小
      var cellWidth = that.gridSize.width;
      var cellHeight = that.gridSize.height;
      var cellType = cellInfo.type !== undefined && cellInfo.type !== null ? cellInfo.type : 0;

      if (cellType === 4) {
        // 四向车通道使用一半尺寸
        cellWidth = cellWidth / 2;
        cellHeight = cellHeight / 2;
      }

      var x = that.gridSize.width * cellInfo.dispalyX;
      var y = that.gridSize.height * cellInfo.dispalyY;

      // 如果是小尺寸节点，调整位置使其居中
      if (cellType === 4) {
        x += (that.gridSize.width - cellWidth) / 2;
        y += (that.gridSize.height - cellHeight) / 2;
      }

      var loc = "" + x + " " + y;
      var size = "" + that.gridSize.width + " " + that.gridSize.height;
      var cellSize = "" + cellWidth + " " + cellHeight;
      var nodeCategory = cellInfo.category || "cell_can_link";

      var node = {
        category: nodeCategory,
        text: cellInfo.code,
        key: cellInfo.code,
        loc: loc,
        size: size,
        cellSize: cellSize,  // 添加cellSize用于动态调整节点大小
        fillColor: cellInfo.fillColor,
        textColor: cellInfo.textColor,
        borderColor: cellInfo.borderColor,
      };

      that.lineModelData.nodeDataArray.push(node);
    },

    loadData(data) {
      var that = this;
      if (data == {}) {
        var modelData = go.Model.fromJson(data);
        that.myDiagram.model = modelData;
        return;
      }

      if (data.nodeDataArray != undefined) {
        that.myDiagram.initialAutoScale = go.Diagram.Uniform;
        that.myDiagram.zoomToFit();
        that.loadDataInChunks(data.nodeDataArray, 20);
      }

      if (data.linkDataArray != undefined) {
        that.myDiagram.model.addLinkDataCollection(data.linkDataArray);
      }
    },

    loadDataInChunks(data, chunkSize) {
      var that = this;
      let index = 0;

      function loadNextChunk() {
        if (that.lineModelData.nodeDataArray.length == 0) {
          return;
        }
        const chunk = data.slice(index, index + chunkSize);
        that.myDiagram.model.addNodeDataCollection(chunk);
        index += chunkSize;
        if (index < data.length) {
          requestAnimationFrame(loadNextChunk);
        } else {
          // 所有数据加载完成后，居中显示
          setTimeout(() => {
            that.centerAndFitDiagram();
          }, 100); // 延迟100ms确保所有节点都已渲染
        }
      }
      loadNextChunk();
    },

    // 居中并适应图表显示
    centerAndFitDiagram() {
      if (!this.myDiagram) {
        return;
      }

      try {
        // 计算所有节点的边界
        const bounds = this.myDiagram.documentBounds;

        if (bounds.width > 0 && bounds.height > 0) {
          // 设置合适的缩放比例，确保所有内容可见
          this.myDiagram.initialAutoScale = go.Diagram.Uniform;
          this.myDiagram.zoomToFit();

          // 确保图表居中显示
          this.myDiagram.centerRect(bounds);

          console.log("✓ 监控模式图表已居中显示，边界:", bounds);
        }
      } catch (error) {
        console.warn("监控模式居中显示失败:", error);
        // 降级处理：使用简单的zoomToFit
        this.myDiagram.zoomToFit();
      }
    },

    // ========== 流动动画 ==========

    startLinkAnimations() {
      var that = this;
      if (!this.myDiagram) return;

      // 如果动画已经在运行，不重复启动
      if (this.animationTimer) {
        console.log("⚠️ 动画已在运行中，跳过重复启动");
        return;
      }

      // 先检查是否有需要动画的连接线
      let animatedCount = 0;
      that.myDiagram.links.each((link) => {
        if (link.data.isAnimated) {
          animatedCount++;
        }
      });

      console.log(`🎬 启动路径动画，共有 ${animatedCount} 条执行中的路径`);

      if (animatedCount === 0) {
        console.log("⚠️ 没有需要动画的路径");
        return;
      }

      // 使用requestAnimationFrame实现更流畅的动画
      let lastTime = Date.now();
      const targetInterval = 50; // 目标间隔50ms，稍微快一点更流畅

      const loop = () => {
        const currentTime = Date.now();
        const elapsed = currentTime - lastTime;

        // 控制帧率，避免过于频繁的更新
        if (elapsed >= targetInterval) {
          if (!that.myDiagram) return;

          const oldskips = that.myDiagram.skipsUndoManager;
          that.myDiagram.skipsUndoManager = true;

          // 批量更新所有动画连接线
          that.myDiagram.links.each((link) => {
            if (link.data.isAnimated) {
              // 更新主路径线的动画效果
              const mainPipe = link.findObject("MAIN_PIPE");
              if (mainPipe) {
                var off = mainPipe.strokeDashOffset - 2;
                mainPipe.strokeDashOffset = (off <= 0) ? 12 : off;
              }

              // 同时更新中层的动画效果（保持同步）
              const midLayer = link.findObject("MID_LAYER");
              if (midLayer) {
                var off = midLayer.strokeDashOffset - 2;
                midLayer.strokeDashOffset = (off <= 0) ? 12 : off;
              }

              // 注意：外层边框不进行动画，保持静态实线
            }
          });

          that.myDiagram.skipsUndoManager = oldskips;
          lastTime = currentTime;
        }

        // 使用requestAnimationFrame继续循环
        that.animationTimer = requestAnimationFrame(loop);
      };

      // 启动动画循环
      that.animationTimer = requestAnimationFrame(loop);
      console.log("✅ 动画循环已启动");
    },

    stopLinkAnimations() {
      if (this.animationTimer) {
        cancelAnimationFrame(this.animationTimer);
        this.animationTimer = null;
      }
    },

    // ========== 路径状态处理（WebSocket推送） ==========

    /**
     * 处理路径状态 WebSocket 推送（Redis标志位优化，按需推送）
     * @param {Array} pathsData - 路径状态数据数组
     */
    handlePathStatusUpdate(pathsData) {
      if (!this.myDiagram || !this.myDiagram.model) {
        return;
      }

      // 静默处理，使用rebuildConnectionLines更新路径
      this.rebuildConnectionLines(pathsData);

      console.log(`📡 收到路径状态推送: ${pathsData ? pathsData.length : 0} 条路径`);
    },

    /**
     * 重新构建连接线（删除所有旧连接线，添加新连接线）
     * @param {Array} pathsData - 路径状态数据（包含cellId和nextId，需要转换为cellCode）
     */
    rebuildConnectionLines(pathsData) {
      if (!this.myDiagram || !this.myDiagram.model) {
        return;
      }

      const that = this;

      // 1. 建立 cellId 到 cellCode 的映射
      const cellIdToCodeMap = {};
      const currentFloorCellCodes = new Set();

      this.cellInfos.forEach(cell => {
        if (cell.id && cell.code) {
          cellIdToCodeMap[cell.id] = cell.code;
          currentFloorCellCodes.add(cell.code);
        }
      });

      // 2. 不停止动画，保持流畅
      // this.stopLinkAnimations();  // 注释掉，避免频繁停止/启动

      // 3. 使用事务批量操作，提升性能
      this.myDiagram.startTransaction("updatePaths");

      // 4. 不再需要复合键映射，路径状态直接在path对象中

      // 5. 构建新的路径状态映射 (from-to => pathState)
      // 同一路径可能有多个小车，需要合并状态（取最高优先级）
      const newPathStateMap = new Map();
      const pathCarMapping = new Map(); // 记录每条路径上的小车信息
      this.executingPathCount = 0;

      pathsData.forEach(path => {
        // 将 fromCellId 和 toCellId 转换为 cellCode
        const fromCellCode = cellIdToCodeMap[path.fromCellId];
        const toCellCode = cellIdToCodeMap[path.toCellId];

        // 只有当两个库位都存在且在当前楼层时才处理
        if (fromCellCode && toCellCode &&
            currentFloorCellCodes.has(fromCellCode) &&
            currentFloorCellCodes.has(toCellCode)) {

          // 路径状态直接使用path.state
          const pathState = path.state;

          const pathKey = `${fromCellCode}=>${toCellCode}`;

          // 如果该路径已存在，比较并保留优先级更高的状态
          // 优先级：执行中(2) > 已占用(1) > 未占用(0) > 已完成(3)
          const existingState = newPathStateMap.get(pathKey);

          if (!existingState) {
            // 第一次遇到这条路径
            newPathStateMap.set(pathKey, {
              pathState: pathState,
              fromCellCode: fromCellCode,
              toCellCode: toCellCode,
              carCount: 1,
              rcsCarIds: [path.rcsCarId]
            });

            if (pathState === 2) {
              that.executingPathCount++;
            }
          } else {
            // 路径已存在，合并小车信息
            existingState.carCount++;
            existingState.rcsCarIds.push(path.rcsCarId);

            // 状态优先级比较
            const shouldUpdate =
              (pathState === 2) || // 新状态是执行中，直接更新
              (pathState === 1 && existingState.pathState !== 2) || // 新状态是已占用，且现有状态不是执行中
              (pathState === 0 && existingState.pathState === 3); // 新状态是未占用，现有状态是已完成

            if (shouldUpdate) {
              // 如果原来不是执行中，现在是执行中，增加计数
              if (existingState.pathState !== 2 && pathState === 2) {
                that.executingPathCount++;
              }
              // 如果原来是执行中，现在不是执行中，减少计数
              else if (existingState.pathState === 2 && pathState !== 2) {
                that.executingPathCount--;
              }

              existingState.pathState = pathState;
            }
          }
        }
      });

      // 6. 增量更新：只更新变化的连接线
      const linksToRemove = [];
      const existingPathKeys = new Set();

      // 遍历现有连接线，更新状态或标记删除
      this.myDiagram.links.each((link) => {
        const pathKey = `${link.data.from}=>${link.data.to}`;
        existingPathKeys.add(pathKey);

        const newState = newPathStateMap.get(pathKey);

        if (!newState) {
          // 路径不存在了，标记删除
          linksToRemove.push(link.data);
        } else if (link.data.pathState !== newState.pathState) {
          // 状态变化，更新样式
          let lineColor, lineThickness, dashArray, isAnimated;

          switch(newState.pathState) {
            case 0:
              lineColor = "#cbd5e0";
              lineThickness = 1;  // 未占用：细线
              dashArray = [4, 4];
              isAnimated = false;
              break;
            case 1:
              lineColor = "#f6ad55";
              lineThickness = 2;  // 已占用：正常
              dashArray = null;
              isAnimated = false;
              break;
            case 2:
              lineColor = "#48bb78";
              lineThickness = 3;  // 执行中：稍粗
              dashArray = [8, 4];
              isAnimated = true;
              break;
            case 3:
              lineColor = "#4a5568";
              lineThickness = 2;  // 已完成：正常
              dashArray = null;
              isAnimated = false;
              break;
            default:
              lineColor = "#4facfe";
              lineThickness = 2;  // 默认：正常
              dashArray = null;
              isAnimated = false;
          }

          // 计算边框颜色（用于区分不同小车）
          let borderColor = 'transparent';
          if (newState.carCount === 1) {
            borderColor = this.getCarColor(newState.rcsCarIds[0]);
          } else if (newState.carCount > 1) {
            borderColor = this.getCarColor(newState.rcsCarIds[0]) + '80';
          }

          // 更新连接线属性
          this.myDiagram.model.setDataProperty(link.data, "color", lineColor);  // 原始状态颜色
          this.myDiagram.model.setDataProperty(link.data, "thickness", lineThickness);
          this.myDiagram.model.setDataProperty(link.data, "borderColor", borderColor);  // ✨ 小车专属边框颜色
          this.myDiagram.model.setDataProperty(link.data, "dashArray", dashArray);
          this.myDiagram.model.setDataProperty(link.data, "pathState", newState.pathState);
          this.myDiagram.model.setDataProperty(link.data, "isAnimated", isAnimated);
          this.myDiagram.model.setDataProperty(link.data, "carCount", newState.carCount);
          this.myDiagram.model.setDataProperty(link.data, "carIds", newState.rcsCarIds);
        }
      });

      // 7. 删除不存在的连接线
      if (linksToRemove.length > 0) {
        console.log(`➖ 删除 ${linksToRemove.length} 条路径:`, linksToRemove.map(l => `${l.from}→${l.to}`).join(', '));
        this.myDiagram.model.removeLinkDataCollection(linksToRemove);
      }

      // 8. 添加新出现的连接线
      const linksToAdd = [];
      newPathStateMap.forEach((stateInfo, pathKey) => {
        if (!existingPathKeys.has(pathKey)) {
          // 直接从 stateInfo 中获取库位编码，避免字符串分割问题
          const fromCellCode = stateInfo.fromCellCode;
          const toCellCode = stateInfo.toCellCode;

          let lineColor, lineThickness, dashArray, isAnimated;

          switch(stateInfo.pathState) {
            case 0:
              lineColor = "#cbd5e0";
              lineThickness = 1;  // 未占用：细线
              dashArray = [4, 4];
              isAnimated = false;
              break;
            case 1:
              lineColor = "#f6ad55";
              lineThickness = 2;  // 已占用：正常
              dashArray = null;
              isAnimated = false;
              break;
            case 2:
              lineColor = "#48bb78";
              lineThickness = 3;  // 执行中：稍粗
              dashArray = [8, 4];
              isAnimated = true;
              break;
            case 3:
              lineColor = "#4a5568";
              lineThickness = 2;  // 已完成：正常
              dashArray = null;
              isAnimated = false;
              break;
            default:
              lineColor = "#4facfe";
              lineThickness = 2;  // 默认：正常
              dashArray = null;
              isAnimated = false;
          }

          // 计算边框颜色（用于区分不同小车）
          let borderColor = 'transparent';
          if (stateInfo.carCount === 1) {
            // 单车路径：使用小车专属颜色作为边框
            borderColor = this.getCarColor(stateInfo.rcsCarIds[0]);
          } else if (stateInfo.carCount > 1) {
            // 多车路径：使用第一辆车的颜色（半透明）
            borderColor = this.getCarColor(stateInfo.rcsCarIds[0]) + '80';
          }

          const linkData = {
            from: fromCellCode,
            to: toCellCode,
            color: lineColor,  // 原始状态颜色
            thickness: lineThickness,
            borderColor: borderColor,  // ✨ 小车专属边框颜色
            pathState: stateInfo.pathState,
            isAnimated: isAnimated,
            dashArray: dashArray,
            fromPort: "",
            toPort: "",
            carCount: stateInfo.carCount,
            carIds: stateInfo.rcsCarIds
          };

          linksToAdd.push(linkData);
        }
      });

      if (linksToAdd.length > 0) {
        console.log(`➕ 添加 ${linksToAdd.length} 条新路径`);
        linksToAdd.forEach(l => {
          const stateInfo = newPathStateMap.get(`${l.from}=>${l.to}`);
          const carInfo = stateInfo ? `[${stateInfo.carCount}辆小车]` : '';
          console.log(`   ${l.from}→${l.to} 状态:${l.pathState} ${carInfo}`);
        });
        this.myDiagram.model.addLinkDataCollection(linksToAdd);
      }

      // 9. 提交事务
      this.myDiagram.commitTransaction("updatePaths");

      // 10. 更新统计数据
      this.pathCount = newPathStateMap.size;

      // 输出更新摘要和多车路径统计
      if (linksToRemove.length > 0 || linksToAdd.length > 0) {
        const multiCarPaths = Array.from(newPathStateMap.values()).filter(p => p.carCount > 1);
        console.log(`🔄 路径更新完成: 删除${linksToRemove.length}条, 添加${linksToAdd.length}条, 当前共${this.pathCount}条`);
        if (multiCarPaths.length > 0) {
          console.log(`   📍 多车路径: ${multiCarPaths.length}条 (${multiCarPaths.map(p => `${p.fromCellCode}→${p.toCellCode}[${p.carCount}辆]`).join(', ')})`);
        }
      }

      // 11. 确保动画运行（如果有执行中的路径且动画未启动）
      if (that.executingPathCount > 0 && !that.animationTimer) {
        that.$nextTick(() => {
          that.startLinkAnimations();
        });
      }
    },

    // ========== WebSocket连接 ==========

    connectCarPositionWebSocket() {
      const that = this;

      if (this.carWebSocket && this.carWebSocket.readyState === WebSocket.OPEN) {
        return;
      }

      if (!this.wareCode || !this.floor) {
        return;
      }

      try {
        let wsUrl;
        const userId = localStorage.getItem('userId') || 'anonymous';
        const userName = localStorage.getItem('userName') || '匿名用户';

        if (process.env.NODE_ENV === 'development') {
          const protocol = 'ws:';
          const backendHost = 'localhost:8007';
          wsUrl = `${protocol}//${backendHost}/wcs/websocket/carPosition?userId=${userId}&userName=${encodeURIComponent(userName)}`;
        } else {
          const protocol = 'ws:';
          const backendHost = "192.168.3.210:8007";
          wsUrl = `${protocol}//${backendHost}/wcs/websocket/carPosition?userId=${userId}&userName=${encodeURIComponent(userName)}`;
        }

        this.carWebSocket = new WebSocket(wsUrl);

        this.carWebSocket.onopen = () => {
          console.log("✓ WebSocket连接成功");
          that.wsConnected = true;

          that.subscribeWarehouse(that.wareCode, that.floor);
          that.$message.success("小车位置实时监控已启动");
          that.startHeartbeat();

          // 加载初始小车数据
          that.loadInitialCarData();
        };

        this.carWebSocket.onmessage = (event) => {
          try {
            const message = JSON.parse(event.data);

            switch (message.type) {
              case 'carPosition':
                that.handleCarPositionUpdate(message);
                break;
              case 'pathStatus':
                // 处理路径状态推送（Redis标志位优化，按需推送）
                that.handlePathStatusUpdate(message.data);
                break;
              case 'heartbeat':
                // 静默处理心跳
                break;
              case 'error':
                console.error("服务器错误:", message.message);
                that.$message.error(message.message);
                break;
              default:
                console.warn("未知消息类型:", message.type);
            }
          } catch (error) {
            console.error("解析WebSocket消息失败:", error);
          }
        };

        this.carWebSocket.onerror = (error) => {
          console.error("WebSocket连接错误:", error);
          that.wsConnected = false;
         // that.$message.warning("小车位置实时监控连接失败");
        };

        this.carWebSocket.onclose = (event) => {
          that.wsConnected = false;
          that.stopHeartbeat();

          if (event.code !== 1000) {
            setTimeout(() => {
              that.connectCarPositionWebSocket();
            }, 5000);
          }
        };
      } catch (error) {
        console.error("创建WebSocket连接失败:", error);
      //  this.$message.error("创建WebSocket连接失败");
      }
    },

    subscribeWarehouse(wareCode, floor) {
      if (!this.carWebSocket || this.carWebSocket.readyState !== WebSocket.OPEN) {
        return;
      }

      const message = {
        type: 'subscribe',
        wareCode: wareCode,
        floor: floor,
        timestamp: Date.now()
      };

      this.carWebSocket.send(JSON.stringify(message));
    },

    changeFloorSubscription(newFloor) {
      if (!this.carWebSocket || this.carWebSocket.readyState !== WebSocket.OPEN) {
        return;
      }

      const message = {
        type: 'changeFloor',
        floor: newFloor,
        timestamp: Date.now()
      };

      this.carWebSocket.send(JSON.stringify(message));

      // 切换楼层后，清空当前小车数据并重新加载
      this.clearAllCarNodes();
      this.carPositions = {};

      // 延迟加载新楼层的小车数据（等待库位加载完成）
      setTimeout(() => {
        this.loadInitialCarData();
      }, 1000);
    },

    startHeartbeat() {
      const that = this;

      this.heartbeatTimer = setInterval(() => {
        if (that.carWebSocket && that.carWebSocket.readyState === WebSocket.OPEN) {
          const message = {
            type: 'heartbeat',
            timestamp: Date.now()
          };
          that.carWebSocket.send(JSON.stringify(message));
        }
      }, 30000);
    },

    stopHeartbeat() {
      if (this.heartbeatTimer) {
        clearInterval(this.heartbeatTimer);
        this.heartbeatTimer = null;
      }
    },

    disconnectCarPositionWebSocket() {
      this.stopHeartbeat();

      if (this.carWebSocket) {
        this.carWebSocket.close();
        this.carWebSocket = null;
        this.wsConnected = false;
      }
    },

    // ========== 小车节点统一管理 ==========

    /**
     * 统一的小车节点更新/创建方法
     * @param {Object} carData - 小车数据
     * @param {boolean} useAnimation - 是否使用动画（WebSocket更新时使用动画，初始加载不使用）
     * @param {boolean} verbose - 是否输出详细日志
     */
    updateOrCreateCarNode(carData, useAnimation = false, verbose = true) {
      if (!this.myDiagram) return false;

      // 兼容不同的字段名：
      // carCode(WebSocket) / code(数据库) / id(备用)
      // 注意：code是编码字符串，应该优先使用；id是数字，仅作为备用
      const carCode = carData.carCode || carData.code || carData.id;

      if (!carCode) {
        console.error(`  ❌ 小车数据缺少标识符:`, carData);
        return false;
      }

      // 字段映射：moveDirection(数据库) → direction(前端)
      const direction = carData.direction || carData.moveDirection || 1;
      const carName = carData.carName || carData.name || carCode;
      const taskState = carData.taskState ? Number(carData.taskState) : 0;

      // 计算小车位置
      let carLocation = null;
      let locationPoint = null;

      if (verbose) {
        console.log(`  🚗 小车: ${carCode} (${carName})`);
        console.log(`  📋 位置数据: fromCellCode=${carData.fromCellCode}, toCellCode=${carData.toCellCode}, positionRatio=${carData.positionRatio}`);
      }

      if (carData.fromCellCode && carData.toCellCode && carData.positionRatio !== null && carData.positionRatio !== undefined) {
        // 有完整的路径信息，按比例计算位置
        const coord = this.calculatePositionByRatio(carData.fromCellCode, carData.toCellCode, carData.positionRatio);
        if (coord) {
          locationPoint = new go.Point(coord.x, coord.y);
          carLocation = `${coord.x} ${coord.y}`;
          if (verbose) {
            console.log(`  📍 按比例计算位置: ${carData.fromCellCode} → ${carData.toCellCode} (${(carData.positionRatio * 100).toFixed(0)}%)`);
          }
        } else {
          if (verbose) {
            console.warn(`  ⚠️ 路径信息完整但计算位置失败`);
          }
        }
      } else if (carData.fromCellCode) {
        // 只有起点，显示在起点位置
        const fromNode = this.myDiagram.findNodeForKey(carData.fromCellCode);
        if (fromNode && fromNode.location) {
          locationPoint = fromNode.location.copy();
          carLocation = `${fromNode.location.x} ${fromNode.location.y}`;
          if (verbose) {
            console.log(`  📍 显示在起点: ${carData.fromCellCode} (坐标: ${carLocation})`);
          }
        } else {
          if (verbose) {
            console.warn(`  ⚠️ 找不到库位节点: ${carData.fromCellCode}`);
          }
        }
      } else {
        if (verbose) {
          console.warn(`  ⚠️ 缺少位置信息 (fromCellCode为空)`);
        }
      }

      if (!carLocation || !locationPoint) {
        if (verbose) {
          console.warn(`  ✗ 无法确定小车 ${carCode} 的位置，跳过显示`);
        }
        return false;
      }

      const carKey = `car_${carCode}`;
      const carNode = this.myDiagram.findNodeForKey(carKey);

      if (carNode) {
        // 更新现有小车节点
        if (verbose) {
          console.log(`  ✓ 更新小车节点: ${carCode}`);
        }

        this.myDiagram.startTransaction("updateCarPosition");

        if (useAnimation) {
          // 使用动画平滑移动小车
          const currentLocation = carNode.location.copy();
          const animation = new go.Animation();
          animation.add(carNode, "location", currentLocation, locationPoint);
          animation.duration = 1500;
          animation.easing = go.Animation.EaseInOutQuad;
          animation.start();
        } else {
          // 直接更新位置
          this.myDiagram.model.setDataProperty(carNode.data, "loc", carLocation);
        }

        // 更新其他属性（使用映射后的字段）
        this.myDiagram.model.setDataProperty(carNode.data, "batteryLevel", carData.batteryLevel || 100);
        this.myDiagram.model.setDataProperty(carNode.data, "direction", direction);
        this.myDiagram.model.setDataProperty(carNode.data, "speed", carData.speed || 0);
        this.myDiagram.model.setDataProperty(carNode.data, "taskState", taskState);
        this.myDiagram.model.setDataProperty(carNode.data, "isConnected", carData.isConnected || 0);

        this.myDiagram.commitTransaction("updateCarPosition");
      } else {
        // 创建新小车节点（使用映射后的字段）
        if (verbose) {
          console.log(`  ✓ 创建新小车节点: ${carCode}`);
          console.log(`     位置: ${carLocation}`);
          console.log(`     坐标: x=${locationPoint.x}, y=${locationPoint.y}`);
        }

        const nodeData = {
          key: carKey,
          category: "car",
          code: carCode,
          name: carName,
          loc: carLocation,
          batteryLevel: carData.batteryLevel || 100,
          direction: direction,
          speed: carData.speed || 0,
          taskState: taskState,
          isConnected: carData.isConnected || 0
        };

        this.myDiagram.model.addNodeData(nodeData);

        // 验证节点是否创建成功并在正确位置
        if (verbose) {
          this.$nextTick(() => {
            const createdNode = this.myDiagram.findNodeForKey(carKey);
            if (createdNode) {
              console.log(`     ✓ 节点已创建，实际位置: (${createdNode.location.x}, ${createdNode.location.y})`);
            } else {
              console.warn(`     ✗ 节点创建失败`);
            }
          });
        }
      }

      // 更新carPositions状态（使用映射后的字段）
      this.$set(this.carPositions, carCode, {
        carId: carData.id || carData.rcsCarId,  // ✨ 保存小车ID用于颜色映射
        carCode: carCode,
        carName: carName,
        fromCellCode: carData.fromCellCode || '',
        toCellCode: carData.toCellCode || '',
        positionRatio: carData.positionRatio || 0,
        batteryLevel: carData.batteryLevel || 100,
        speed: carData.speed || 0,
        direction: direction,
        taskState: taskState,
        isConnected: carData.isConnected || 0,
        isCharge: carData.isCharge || '0',  // ✨ 充电状态
        loadState: carData.loadState  // ✨ 负载状态（0-空载/1-负载）
      });

      if (verbose) {
        console.log(`  ✓ 小车显示成功`);
        console.log(`  ✓ carPositions已更新，当前共 ${Object.keys(this.carPositions).length} 辆在线小车`);
      }

      return true;
    },

    // ========== 小车位置更新（WebSocket） ==========

    handleCarPositionUpdate(carData) {
      if (!this.myDiagram) return;

      // 兼容字段名
      const carCode = carData.carCode || carData.code || carData.id;
      const carName = carData.carName || carData.name || carCode;
      const timestamp = new Date().toLocaleTimeString();

      console.log(`\n═══ WebSocket小车位置更新 [${timestamp}] ═══`);
      console.log(`🚗 小车: ${carCode} (${carName})`);
      const fromCell = carData.fromCellCode || '?';
      const toCell = carData.toCellCode || '?';
      console.log(`📍 路径: ${fromCell} → ${toCell}`);
      console.log(`📊 进度: ${carData.positionRatio !== null ? (carData.positionRatio * 100).toFixed(1) + '%' : '未知'}`);
      const speed = carData.speed || 0;
      console.log(`⚡ 速度: ${speed} m/s`);
      const batteryLevel = carData.batteryLevel || 0;
      console.log(`🔋 电量: ${batteryLevel}%`);
      console.log(`🔌 充电: ${carData.isCharge} (类型: ${typeof carData.isCharge})`);
      console.log(`📦 负载: ${carData.loadState === 1 ? '有货(1)' : carData.loadState === 0 ? '空载(0)' : '未知(' + carData.loadState + ')'}`);
      console.log(`📌 状态: ${this.getTaskStateLabel(carData.taskState)}`);
      console.log(`🔗 连接: ${carData.isConnected === 1 ? '已连接' : '未连接'}`);

      // 使用统一方法更新/创建节点（使用动画）
      const success = this.updateOrCreateCarNode(carData, true, true);

      if (!success) {
        console.warn(`❌ 无法显示小车 ${carCode}`);
      }

      console.log(`═══════════════════════════════\n`);
    },

    calculatePositionByRatio(fromCellCode, toCellCode, ratio) {
      if (!this.myDiagram) {
        console.warn("   ❌ Diagram未初始化");
        return null;
      }

      // 1. 从GoJS图表中找到起点和终点节点
      const fromNode = this.myDiagram.findNodeForKey(fromCellCode);
      const toNode = this.myDiagram.findNodeForKey(toCellCode);

      if (!fromNode) {
        console.warn(`   ❌ 找不到起点节点: ${fromCellCode}`);
        return null;
      }

      if (!toNode) {
        console.warn(`   ❌ 找不到终点节点: ${toCellCode}`);
        return null;
      }

      // 2. 获取节点的实际位置（GoJS的location）
      const fromLoc = fromNode.location;
      const toLoc = toNode.location;

      if (!fromLoc || !toLoc) {
        console.warn(`   ❌ 节点位置无效: fromLoc=${fromLoc}, toLoc=${toLoc}`);
        return null;
      }

      // 3. 线性插值计算小车位置
      const carX = fromLoc.x + (toLoc.x - fromLoc.x) * ratio;
      const carY = fromLoc.y + (toLoc.y - fromLoc.y) * ratio;

      console.log(`   💡 坐标计算: ${fromCellCode}(${fromLoc.x.toFixed(0)}, ${fromLoc.y.toFixed(0)}) → ${toCellCode}(${toLoc.x.toFixed(0)}, ${toLoc.y.toFixed(0)}) × ${(ratio * 100).toFixed(0)}% = (${carX.toFixed(0)}, ${carY.toFixed(0)})`);

      return { x: carX, y: carY };
    },

    // ========== 初始小车数据加载 ==========

    clearAllCarNodes() {
      if (!this.myDiagram || !this.myDiagram.model) {
        return;
      }

      // 找到所有小车节点并删除
      const carNodesToRemove = [];
      this.myDiagram.nodes.each((node) => {
        if (node.data.category === "car") {
          carNodesToRemove.push(node.data);
        }
      });

      if (carNodesToRemove.length > 0) {
        this.myDiagram.model.removeNodeDataCollection(carNodesToRemove);
        console.log(`✓ 清除了 ${carNodesToRemove.length} 个小车节点`);
      }
    },

    loadInitialCarData() {
      const that = this;

      if (!this.wareCode) {
        console.warn("仓库编码为空，无法加载小车数据");
        return;
      }

      console.log(`\n═══ 加载初始小车数据 ═══`);
      console.log(`📦 仓库: ${this.wareCode}, 楼层: ${this.floor}`);

      listRcsCarInfo({
        wareCode: this.wareCode,
        z: this.floor,
        pageSize: 999
      }).then((response) => {
        if (response.code == 200) {
          const allCars = response.rows;
          console.log(`✓ 获取到 ${allCars.length} 辆小车`);

          // 输出第一辆小车的数据结构供调试
          if (allCars.length > 0) {
            console.log(`  📋 小车数据示例:`, JSON.stringify(allCars[0], null, 2));
          }

          // 遍历小车，判断是否应该显示在当前楼层
          let successCount = 0;
          let failCount = 0;

          allCars.forEach((car, index) => {
            console.log(`\n--- 处理第 ${index + 1}/${allCars.length} 辆小车 ---`);
            const success = that.updateOrCreateCarNode(car, false, true);  // 初始加载不使用动画
            if (success) {
              successCount++;
            } else {
              failCount++;
            }
          });

          console.log(`\n✓ 初始加载完成: 成功 ${successCount} 辆，失败 ${failCount} 辆`);
          console.log(`✓ 右侧面板显示: ${Object.keys(that.carPositions).length} 辆在线小车`);
          console.log(`═══════════════════════════════\n`);
        } else {
          console.error("获取小车信息失败:", response.msg);
        }
      }).catch((error) => {
        console.error("加载小车数据出错:", error);
      });
    },


    // ========== 修改小车位置功能 ==========

    /**
     * 打开编辑小车位置对话框
     * @param {Object} car - 小车对象
     */
    openEditCarPositionDialog(car) {
      console.log("打开编辑对话框，小车数据:", car);

      this.editPositionForm = {
        carId: car.carId,
        carCode: car.carCode,
        carName: car.carName,
        currentFromCellCode: car.fromCellCode || '',
        newFromCellCode: '',
        toCellCode: car.toCellCode || ''
      };

      this.editPositionDialogVisible = true;

      // 重置表单验证
      this.$nextTick(() => {
        if (this.$refs.editPositionForm) {
          this.$refs.editPositionForm.clearValidate();
        }
      });
    },

    /**
     * 库位编码自动补全查询
     * @param {String} queryString - 查询字符串
     * @param {Function} cb - 回调函数
     */
    queryCellCodeSuggestions(queryString, cb) {
      const cellTypeLabels = {
        0: '普通库位',
        1: '入库接驳位',
        2: '出库接驳位',
        3: '通用接驳位',
        4: '四向车通道',
        5: '提升机位置'
      };

      // 从当前楼层的库位列表中筛选
      let suggestions = this.cellInfos.map(cell => ({
        value: cell.code,
        typeLabel: cellTypeLabels[cell.type] || '普通库位',
        type: cell.type,
        x: cell.x,
        y: cell.y,
        z: cell.z
      }));

      // 如果有查询字符串，进行模糊匹配
      if (queryString) {
        suggestions = suggestions.filter(item =>
          item.value.toLowerCase().includes(queryString.toLowerCase())
        );
      }

      // 限制返回数量，提高性能
      suggestions = suggestions.slice(0, 50);

      cb(suggestions);
    },

    /**
     * 提交修改小车位置
     */
    submitEditCarPosition() {
      this.$refs.editPositionForm.validate((valid) => {
        if (!valid) {
          return false;
        }

        // 验证新起点是否与当前起点相同
        if (this.editPositionForm.newFromCellCode === this.editPositionForm.currentFromCellCode) {
          this.$message.warning('新起点与当前起点相同，无需修改');
          return;
        }

        // 验证新起点是否存在于当前楼层
        const cellExists = this.cellInfos.some(cell =>
          cell.code === this.editPositionForm.newFromCellCode
        );

        if (!cellExists) {
          this.$message.error('输入的库位编码在当前楼层不存在');
          return;
        }

        // 确认提示
        this.$confirm(
          `确定将小车 ${this.editPositionForm.carName}(${this.editPositionForm.carCode}) 的位置从 ${this.editPositionForm.currentFromCellCode} 修改为 ${this.editPositionForm.newFromCellCode} 吗？\n\n系统会同时更新起点和终点为相同位置，表示小车静止在该库位。`,
          '确认修改小车位置',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          this.editPositionLoading = true;

          // 调用后端API更新小车位置
          request({
            url: "/wcs-rcs/RcsCarInfo/updateFromCellCode",
            method: "post",
            data: {
              carId: this.editPositionForm.carId,
              carCode: this.editPositionForm.carCode,
              fromCellCode: this.editPositionForm.newFromCellCode
            }
          }).then((response) => {
            this.editPositionLoading = false;

            if (response.code == 200) {
              this.$message.success('小车位置修改成功');
              this.editPositionDialogVisible = false;

              // 更新本地carPositions数据
              if (this.carPositions[this.editPositionForm.carCode]) {
                this.$set(
                  this.carPositions[this.editPositionForm.carCode],
                  'fromCellCode',
                  this.editPositionForm.newFromCellCode
                );
              }

              // 重新加载小车数据以确保同步
              setTimeout(() => {
                this.loadInitialCarData();
              }, 500);
            } else {
              this.$message.error(response.msg || '修改失败');
            }
          }).catch((error) => {
            this.editPositionLoading = false;
            console.error('修改小车位置失败:', error);
            this.$message.error('修改失败，请稍后重试');
          });
        }).catch(() => {
          // 用户取消
        });
      });
    },

    // ========== 工具方法 ==========

    /**
     * 获取小车的专属颜色（用于路径边框）
     * @param {Number} carId - 小车ID
     * @return {String} 颜色值
     */
    getCarColor(carId) {
      if (!carId) return 'transparent';

      // 如果已经分配过颜色，直接返回
      if (this.carColorMap[carId]) {
        return this.carColorMap[carId];
      }

      // 为新小车分配颜色（循环使用颜色数组）
      const colorIndex = Object.keys(this.carColorMap).length % this.carColors.length;
      const color = this.carColors[colorIndex];
      this.carColorMap[carId] = color;

      console.log(`🎨 为小车 ${carId} 分配颜色: ${color}`);
      return color;
    },

    /**
     * 获取小车显示用的颜色（用于UI指示器）
     * @param {Object} car - 小车对象（来自carPositions）
     * @return {String} 颜色值
     */
    getCarColorForDisplay(car) {
      // 尝试从不同的字段获取小车ID
      const carId = car.carId || car.rcsCarId || car.id;
      return this.getCarColor(carId);
    },

    getTaskStateLabel(state) {
      const labels = {
        0: '空闲',
        1: '任务中',
        2: '执行中'
      };
      return labels[state] || '未知';
    },

    /**
     * 判断小车是否正在充电（兼容多种数据格式）
     * @param {Object} car - 小车对象
     * @return {Boolean} 是否充电中
     */
    isCarCharging(car) {
      if (!car || car.isCharge === undefined || car.isCharge === null) {
        return false;
      }
      // 兼容字符串 '1' 和数字 1
      return car.isCharge === '1' || car.isCharge === 1 || car.isCharge === true;
    },

    /**
     * 获取电池样式类（参考智能手机设计）
     * @param {Object} car - 小车对象
     * @return {String} CSS类名
     */
    getBatteryClass(car) {
      const level = car.batteryLevel || 0;
      const isCharging = this.isCarCharging(car);

      if (isCharging) {
        return 'battery-charging'; // 充电中 - 青色
      } else if (level <= 20) {
        return 'battery-low'; // 低电量 - 红色
      } else if (level <= 50) {
        return 'battery-medium'; // 中等电量 - 橙色
      } else {
        return 'battery-high'; // 充足电量 - 绿色
      }
    },

    /**
     * 获取电池图标样式类
     * @param {Object} car - 小车对象
     * @return {String} CSS类名
     */
    getBatteryIconClass(car) {
      const level = car.batteryLevel || 0;
      const isCharging = this.isCarCharging(car);

      if (isCharging) {
        return 'battery-icon-charging';
      } else if (level <= 20) {
        return 'battery-icon-low';
      } else {
        return 'battery-icon-normal';
      }
    },

    cleanup() {
      this.stopLinkAnimations();
      this.stopHeartbeat();
      this.disconnectCarPositionWebSocket();

      if (this.myDiagram) {
        this.myDiagram.div = null;
        this.myDiagram = null;
      }
    }
  }
};
</script>

<style scoped>
@import '../CellSxcView-styles.css';

.cell-monitor-view {
  width: 100%;
  height: 80vh;
  margin-top: 1vh;
}

.diagram-card {
  height: 80vh;
  margin-right: 1%;
  display: flex;
  flex-direction: column;
}

.diagram-card >>> .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 12px;
}

.diagram-container {
  flex: 1;
  min-height: 0;
  background-color: transparent;
  position: relative;
}

/* 监控模式路径图例 - 紧凑单行样式 */
.monitor-legend-compact {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  padding: 8px 16px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  flex-shrink: 0;
}

.monitor-legend-compact .legend-item {
  display: flex;
  align-items: center;
  color: #e2e8f0;
  font-size: 12px;
  white-space: nowrap;
}

.monitor-legend-compact .legend-item span {
  margin-left: 2px;
}

/* 监控面板样式 */
.monitor-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.status-section {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
}

.status-item:last-child {
  margin-bottom: 0;
}

.status-label {
  color: #a0aec0;
  font-weight: 400;
}

.status-value {
  font-weight: 500;
  transition: color 0.3s ease;
}

.status-value.connected {
  color: #48bb78;
}

.status-value.disconnected {
  color: #f56565;
}

.status-value.push-mode {
  color: #4ecdc4;
}

/* 小车列表 */
.car-list-section {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.car-list-section .section-title {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #ffffff;
  font-weight: 600;
  font-size: 13px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.car-list-section .section-title i {
  color: #4facfe;
  margin-right: 6px;
  font-size: 14px;
}

.empty-tip {
  color: #a0aec0;
  font-size: 12px;
  text-align: center;
  padding: 20px 0;
}

.color-legend {
  margin-bottom: 10px;
  padding: 8px 10px;
  background: rgba(79, 172, 254, 0.1);
  border-left: 3px solid rgba(79, 172, 254, 0.6);
  border-radius: 4px;
}

.legend-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #a0aec0;
}

.legend-tip i {
  color: #4facfe;
  font-size: 12px;
}

.car-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.car-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
  transition: all 0.2s ease;
}

.car-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(79, 172, 254, 0.4);
}

.car-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 10px;
}

/* 小车名称行 */
.car-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

/* 状态行 */
.car-status-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.status-text {
  font-weight: 500;
  transition: color 0.2s ease;
}

.status-text.online {
  color: #48bb78;
}

.status-text.offline {
  color: #f56565;
}

.status-text.task-0 {
  color: #a0aec0;
}

.status-text.task-1 {
  color: #f6ad55;
}

.status-text.task-2 {
  color: #4ecdc4;
}

.status-divider {
  color: rgba(255, 255, 255, 0.3);
  font-weight: 300;
}

.car-color-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  cursor: help;
}

.car-name {
  color: #ffffff;
  font-weight: 500;
  font-size: 14px;
}

/* 电池显示样式（智能手机风格） */
.battery-row {
  position: relative;
}

.battery-text {
  font-weight: 600;
  font-size: 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 电池电量颜色 */
.battery-high {
  color: #48bb78; /* 绿色 - 充足 */
}

.battery-medium {
  color: #f6ad55; /* 橙色 - 中等 */
}

.battery-low {
  color: #f56565; /* 红色 - 低电量 */
  animation: battery-blink 1.5s ease-in-out infinite;
}

.battery-charging {
  color: #4ecdc4; /* 青色 - 充电中 */
}

/* 电池图标颜色 */
.battery-icon-normal {
  color: #4facfe;
}

.battery-icon-low {
  color: #f56565;
  animation: battery-icon-blink 1.5s ease-in-out infinite;
}

.battery-icon-charging {
  color: #4ecdc4;
  animation: battery-icon-pulse 1.5s ease-in-out infinite;
}

/* 充电闪电图标 */
.charging-icon {
  color: #ffd700 !important; /* 金色 - 使用 !important 覆盖默认样式 */
  font-size: 12px;
  animation: charging-blink 1s ease-in-out infinite;
  margin-left: 4px;
  display: inline-block;
  font-weight: bold;
}

/* 低电量闪烁动画 */
@keyframes battery-blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes battery-icon-blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

/* 充电脉冲动画 */
@keyframes battery-icon-pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 充电闪电闪烁动画 - 更明显的效果 */
@keyframes charging-blink {
  0%, 100% {
    opacity: 1;
    transform: scale(1) rotate(0deg);
    color: #ffd700;
  }
  25% {
    opacity: 0.6;
    transform: scale(1.2) rotate(-5deg);
    color: #ffed4e;
  }
  50% {
    opacity: 1;
    transform: scale(1.3) rotate(5deg);
    color: #ffaa00;
  }
  75% {
    opacity: 0.6;
    transform: scale(1.2) rotate(-5deg);
    color: #ffed4e;
  }
}

.car-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.info-row {
  display: flex;
  align-items: center;
  color: #a0aec0;
  font-size: 11px;
}

.info-row i {
  color: #4facfe;
  margin-right: 6px;
  font-size: 11px;
  width: 12px;
}

/* 统计信息 */
.statistics-section {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
}

.statistics-section .section-title {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #ffffff;
  font-weight: 600;
  font-size: 13px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.statistics-section .section-title i {
  color: #4facfe;
  margin-right: 6px;
  font-size: 14px;
}

.stat-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #e2e8f0;
  font-size: 11px;
  padding: 2px 0;
}

.stat-label {
  color: #a0aec0;
}

.stat-value {
  color: #ffffff;
  font-weight: 500;
  font-size: 14px;
}

/* 位置行样式 */
.position-row {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 4px;
}

.position-row .position-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.edit-position-btn {
  padding: 2px 4px;
  margin-left: 4px;
  color: #4facfe;
  font-size: 12px;
  opacity: 0.6;
  transition: all 0.2s ease;
}

.edit-position-btn:hover {
  opacity: 1;
  color: #4ecdc4;
  transform: scale(1.1);
}

.car-item:hover .edit-position-btn {
  opacity: 0.8;
}

/* 对话框样式增强 */
.form-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 11px;
  color: #a0aec0;
}

.form-tip i {
  color: #4facfe;
  font-size: 12px;
}

.cell-suggestion-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2px 0;
}

.cell-suggestion-item .cell-code {
  font-weight: 500;
  color: #303133;
}

.cell-suggestion-item .cell-type {
  font-size: 11px;
  color: #909399;
  padding: 2px 6px;
  background: #f5f7fa;
  border-radius: 3px;
}

/* 对话框确认按钮图标 */
.dialog-footer .el-button i {
  margin-right: 4px;
}

/* 对话框层级设置 - 确保对话框在最上层 */
.car-position-dialog {
  z-index: 3000 !important;
}

/* Element UI 对话框遮罩层级 */
::v-deep .el-dialog__wrapper {
  z-index: 3000 !important;
}

/* 确保对话框内容可点击 */
::v-deep .el-dialog {
  z-index: 3001 !important;
  position: relative;
}

/* 确保自动完成下拉框在对话框之上 */
::v-deep .el-autocomplete-suggestion {
  z-index: 3002 !important;
}

/* 滚动条样式 */
.car-list-section::-webkit-scrollbar {
  width: 4px;
}

.car-list-section::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
}

.car-list-section::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.car-list-section::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}
</style>
