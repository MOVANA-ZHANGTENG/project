<template>
  <div class="containerSxc">
    <el-row>
      <el-col :span="4">
        <el-card style="
            width: 95%;
            height: 90vh;
            margin-left: 5%;
            margin-top: 1vh;
            padding-top: 2vh;
          ">
          <!-- 模式切换按钮 -->
          <div class="mode-toggle-sidebar">
            <div class="mode-header">
              <i class="el-icon-setting"></i>
              <span>显示模式</span>
            </div>
            <el-button size="small" :type="displayMode === 'normal' ? 'primary' : 'default'"
              @click="switchDisplayMode('normal')" class="mode-btn">
              <i class="el-icon-document"></i>
              正常显示
            </el-button>
            <el-button size="small" :type="displayMode === 'link' ? 'primary' : 'default'"
              @click="switchDisplayMode('link')" class="mode-btn">
              <i class="el-icon-edit"></i>
              编辑模式
            </el-button>
            <el-button size="small" :type="displayMode === 'monitor' ? 'primary' : 'default'"
              @click="switchDisplayMode('monitor')" class="mode-btn">
              <i class="el-icon-view"></i>
              监控模式
            </el-button>
          </div>

          <div class="floor-header">
            <i class="el-icon-office-building"></i>
            <span>楼层选择</span>
          </div>

          <el-row :gutter="16" class="floor-container">
            <el-col v-for="item in floors" :key="item.z" :span="24" @click.native="floor = item.z">
              <div class="floor-card" :class="{ active: floor === item.z }">
                <div class="card-left">
                  <i class="el-icon-guide"></i>
                  <span class="floor-title">第 {{ item.z }} 层</span>
                </div>
                <div class="card-right">
                  <el-tag v-if="floor === item.z" type="success" effect="dark" size="small">
                    当前选中
                  </el-tag>
                </div>
              </div>
            </el-col>
          </el-row>

        </el-card>
      </el-col>
      <el-col :span="16">


        <!-- 货位展示 -->
        <el-card class="main-card">
          <div class="legend-container">
            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">无货</span>
              <div class="kongCell" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>

            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">有货</span>
              <div class="noKongCell" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>

            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">入库中</span>
              <div class="inCell" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>

            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">出库中</span>
              <div class="outCell" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>

            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">禁用</span>
              <div class="disableCell" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>
          </div>

          <!-- 监控模式状态图例 -->
          <div v-if="displayMode === 'monitor'" class="monitor-legend-container">
            <div class="legend-title">
              <i class="el-icon-info"></i>
              <span>路径状态说明</span>
            </div>
            <div class="legend-items">
              <div class="legend-item">
                <svg width="40" height="20" style="margin-right: 8px;">
                  <line x1="0" y1="10" x2="40" y2="10" stroke="#cbd5e0" stroke-width="2" stroke-dasharray="4,4" />
                  <polygon points="35,10 40,7 40,13" fill="#cbd5e0" />
                </svg>
                <span>未占用</span>
              </div>
              <div class="legend-item">
                <svg width="40" height="20" style="margin-right: 8px;">
                  <line x1="0" y1="10" x2="40" y2="10" stroke="#f6ad55" stroke-width="4" />
                  <polygon points="35,10 40,7 40,13" fill="#f6ad55" />
                </svg>
                <span>已占用未下发</span>
              </div>
              <div class="legend-item">
                <svg width="40" height="20" style="margin-right: 8px;">
                  <line x1="0" y1="10" x2="40" y2="10" stroke="#48bb78" stroke-width="5" stroke-dasharray="8,4">
                    <animate attributeName="stroke-dashoffset" from="0" to="-12" dur="0.5s" repeatCount="indefinite" />
                  </line>
                  <polygon points="35,10 40,7 40,13" fill="#48bb78" />
                </svg>
                <span>已下发执行中 <i class="el-icon-video-play" style="color: #48bb78;"></i></span>
              </div>
              <div class="legend-item">
                <svg width="40" height="20" style="margin-right: 8px;">
                  <line x1="0" y1="10" x2="40" y2="10" stroke="#4299e1" stroke-width="3" />
                  <polygon points="35,10 40,7 40,13" fill="#4299e1" />
                </svg>
                <span>已完成</span>
              </div>
            </div>
          </div>

          <div id="diagramDiv" class="diagram-container"></div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <div class="info-panel">
          <div class="info-header">
            <i class="el-icon-info"></i>
            <span>货位信息</span>
          </div>
          <div v-show="cellInfo.code != null" class="info-content">
            <div v-loading="cellLoading" class="form-container">
              <div class="form-group">
                <label class="form-label">货位编码</label>
                <div class="form-value">{{ cellInfo.code }}</div>
              </div>

              <div class="form-group">
                <label class="form-label">前置库位</label>
                <div class="form-value">{{ cellInfo.preCode }}</div>
              </div>

              <div class="form-group">
                <label class="form-label">货位层数</label>
                <div class="form-value">{{ cellInfo.z }}</div>
              </div>

              <div class="form-group">
                <label class="form-label">巷道编码</label>
                <div class="form-value">{{ cellInfo.lineCode }}</div>
              </div>

              <div class="form-group">
                <label class="form-label">库存状态</label>
                <select v-model="cellInfo.invenState" class="form-select">
                  <option v-for="item in invenStates" :key="item.value" :value="item.value">
                    {{ item.label }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label class="form-label">任务状态</label>
                <select v-model="cellInfo.taskState" class="form-select">
                  <option v-for="item in taskStates" :key="item.value" :value="item.value">
                    {{ item.label }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label class="form-label">是否禁用</label>
                <select v-model="cellInfo.disableState" class="form-select">
                  <option v-for="item in disableStates" :key="item.value" :value="item.value">
                    {{ item.label }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label class="form-label">货区编码</label>
                <input :disabled="true" v-model="cellInfo.areaCode" class="form-input disabled" readonly />
              </div>

              <div class="form-group">
                <label class="form-label">容器编码</label>
                <input v-model="cellInfo.palletCode" class="form-input" />
              </div>

              <div class="form-actions">
                <button class="btn-primary" @click="updateCellInfoState(cellInfo)">
                  <i class="el-icon-check"></i>
                  修改提交
                </button>
              </div>

              <div class="form-actions">
                <button class="btn-danger" @click="deleteCellInfo(cellInfo.code)">
                  <i class="el-icon-delete"></i>
                  删除提交
                </button>
              </div>

              <div class="form-actions">
                <button class="btn-secondary" @click="preCellVisible = true">
                  <i class="el-icon-setting"></i>
                  设置前置库位
                </button>
              </div>

              <div v-if="cellInfo != null && cellInfo.taskState == 0 && cellInfo.invenState == 1" class="form-actions">
                <button class="btn-outbound" @click="saveOutTask(cellInfo.code)">
                  <i class="el-icon-download"></i>
                  出库
                </button>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog title="设置前置库位" width="500px" append-to-body :visible.sync="preCellVisible">
      <el-form label-width="80px" :model="preCellForm">
        <el-form-item label="起始X">
          <el-input v-model="preCellForm.fromX"></el-input>
        </el-form-item>
        <el-form-item label="结束X">
          <el-input v-model="preCellForm.toX"></el-input>
        </el-form-item>
        <el-form-item label="起始Y">
          <el-input v-model="preCellForm.fromY"></el-input>
        </el-form-item>
        <el-form-item label="结束Y">
          <el-input v-model="preCellForm.toY"></el-input>
        </el-form-item>
        <el-form-item label="前置方向">
          <el-select v-model="preCellForm.fx" placeholder="前置方向">
            <el-option label="-X" value="-X"></el-option>
            <el-option label="+X" value="+X"></el-option>
            <el-option label="-Y" value="-Y"></el-option>
            <el-option label="+Y" value="+Y"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="preCellVisible = false">取 消</el-button>
        <el-button type="primary" @click="setPreCell()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCellInfo } from "@/api/wcs-base/CellInfo";
import { listCellLink, addCellLink, delCellLink, deleteByFromCellIdAndToCellIdAndWareCode } from "@/api/wcs-base/CellLink";
import { listRcsCarPath } from "@/api/wcs-rcs/RcsCarPath";
import Cookies from "js-cookie";
import request from "@/utils/request";
import { mount } from "sortablejs";
import go from "@/lib/js/go.js"
export default {
  name: "container",
  components: {},
  props: {
    wareCode: {
      type: String,
      default: null,
    },

  },
  data() {
    return {
      preCellVisible: false,
      preCellForm: {},
      //网格尺寸
      gridSize: {
        width: 50,
        height: 50,
      },
      floors: [],
      floor: null,
      lineInfos: [],
      lineCode: null,
      lineInfo: null,
      cellInfos: [],
      myDiagram: null,
      modelData: null,
      nowData: null,
      timer: null,

      //用于存放节点集合
      nodes: [],

      cellInfo: {},
      cellList: [],
      cellLoading: false,

      MetalModeList: [],
      goodsStatus: [],
      inventoryList: [],
      cellFind: {
        x: null,
        y: null,
        z: null,
      },
      totalX: 30,
      totalY: 20,
      id: "",
      loading: false,
      invenLoading: false,
      z: 0,
      areaInfos: {},
      disableStates: [
        { value: 1, label: "禁用" },
        { value: 0, label: "不禁用" },
      ],
      taskStates: [
        { value: 1, label: "任务中" },
        { value: 0, label: "无任务" },
      ],
      invenStates: [
        { value: 1, label: "有货" },
        { value: 0, label: "无货" },
      ],

      minNode: {
        category: "cell", x: 999009999, y: 999009999, borderColor: "transparent",
        textColor: "transparent",
        fillColor: "transparent"
      },
      maxNode: {
        category: "cell", x: -100000000, y: -100000000, borderColor: "transparent",
        textColor: "transparent",
        fillColor: "transparent"
      },
      lineModelData: {
        class: "GraphLinksModel",

        nodeDataArray: [

        ]
      },
      displayMode: 'normal', // 添加显示模式变量：'normal'(正常显示) 或 'link'(连接线显示) 或 'monitor'(监控模式)
      animationTimer: null, // 存储动画定时器ID，用于清理

      // 小车位置相关
      carWebSocket: null,  // 小车位置WebSocket连接
      carPositions: {},    // 存储小车位置数据 {carCode: {x, y, z, ...}}
      heartbeatTimer: null, // 心跳定时器
      pathRefreshTimer: null, // 路径状态刷新定时器

      // 存储上一次的连接线数据，用于检测变化
      previousLinkDataArray: [],

    };
  },
  watch: {
    wareCode(newValue, oldValue) {
      // console.log("newValue:", newValue);
      this.getFloors(newValue);
    },

    floor(newValue, oldValue) {
      this.loadData({});
      this.getCellInfos(newValue);

      // 监控模式下通知WebSocket切换楼层
      if (this.displayMode === 'monitor' && newValue !== oldValue) {
        this.changeFloorSubscription(newValue);
      }
    },
    cellCode(newValue, oldValue) {
      if (newValue != null) {
        this.findCellInfo(newValue);
      }
    },
  },
  created() {
    var that = this;
    debugger;
    // console.log("this.wareCode:", this.wareCode);
    if (this.wareCode != null) {
      that.getFloors(this.wareCode);
    }
    if (this.floor != null) {
      that.getCellInfos(this.floor);
    }
    this.timer = setInterval(() => {
      if (this.floors.length == 0) {
        return;
      }
      if (this.floor == null || this.floor == "") {
        return;
      }
      this.updateCellView(this.floor);
    }, 3000);
  },
  mounted() {
    this.init(this.displayMode); // 使用当前显示模式初始化
  },
  beforeDestroy() {
    // 停止所有动画
    this.stopLinkAnimations();
    // 停止心跳和WebSocket
    this.stopHeartbeat();
    this.stopPathRefresh();
    this.disconnectCarPositionWebSocket();

    if (this.timer) {
      //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
  },

  methods: {
    getFloors(wareCode) {
      var that = this;
      request({
        url: "/wcs-base/FloorInfo/list",
        method: "get",
        params: { wareCode: wareCode },
      }).then((response) => {
        if (response.code == 200) {
          that.floors = response.rows;
          if (that.floors.length > 0) {
            that.floor = that.floors[0].z;
          }
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },

    setPreCell() {
      var that = this;
      request({
        url: "/wcs-base/CellInfo/setPreCell",
        method: "get",
        params: {
          wareCode: that.wareCode
          , fromX: that.preCellForm.fromX
          , toX: that.preCellForm.toX
          , fromY: that.preCellForm.fromY
          , toY: that.preCellForm.toY
          , fx: that.preCellForm.fx
          , z: that.floor
        },
      }).then((response) => {
        that.preCellVisible = false;
        if (response.code == 200) {
          that.$modal.msgSuccess("设置前置库位成功");
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },


    saveOutTask(cellCode) {
      addTaskInfo({ type: "OUT", fromCellCode: cellCode, wareCode: this.wareCode }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },




    getFloorInfo(floor) {
      for (let index = 0; index < this.floors.length; index++) {
        const floorInfo = this.floors[index];
        if (floorInfo.z == floor) {
          return floorInfo;
        }
      }
    },
    //获取所有的货位
    getCellInfosNoLink(floor) {
      var that = this;
      this.cellInfos = [];
      if (floor != null) {
        if (that.myDiagram) {
          // 清理现有的Diagram
          that.myDiagram.div = null;
          that.init(that.displayMode); // 使用当前显示模式重新初始化
        }

        that.lineModelData = {
          class: "GraphLinksModel",
          nodeDataArray: [
          ]
        };
        listCellInfo({
          wareCode: that.wareCode,
          z: floor,
          isDelete: 0,
          pageSize: 999,
        }).then((response) => {
          this.loadData({});
          if (response.code == 200) {

            this.cellInfos = response.rows;
            this.nodes = [];


            var floorInfo = that.getFloorInfo(floor);
            var xy = floorInfo.xy;
            var totalX = floorInfo.totalX;
            var totalY = floorInfo.totalY;

            this.cellInfos.forEach((cell) => {
              var dispalyX = 0;
              var dispalyY = 0;
              switch (xy) {
                case 1: // 左上角
                  dispalyX = cell.x;
                  dispalyY = cell.y;
                  // X轴在顶部（y=0），Y轴在左侧（x=0）
                  break;
                case 2: // 右上角
                  dispalyX = totalX - cell.x + 1;
                  dispalyY = cell.y;
                  // X轴在底部（y=totalY+1），Y轴在左侧（x=0）
                  break;
                case 3: // 左下角
                  dispalyX = cell.x;
                  dispalyY = totalY - cell.y + 1;
                  // X轴在顶部（y=0），Y轴在右侧（x=totalX+1）
                  break;
                case 4: // 右下角
                  dispalyX = totalX - cell.x + 1;
                  dispalyY = totalY - cell.y + 1;
                  // X轴在底部（y=totalY+1），Y轴在右侧（x=totalX+1）
                  break;
              }

              cell.dispalyX = dispalyX;
              cell.dispalyY = dispalyY;
              this.cellColor(cell);
              this.addNode(cell);
            });

            // 根据坐标系调整坐标轴位置
            switch (xy) {
              case 1: // 左上坐标系
                // X轴显示在顶部（y=0）
                addXAxis(0);
                // Y轴显示在左侧（x=0）
                addYAxis(0);
                break;
              case 2: // 右上坐标系
                // X轴显示在底部（y=totalY+1）
                addXAxis(totalY + 1, true);
                // Y轴显示在左侧（x=0）
                addYAxis(0);
                break;
              case 3: // 左下坐标系
                // X轴显示在顶部（y=0）
                addXAxis(0);
                // Y轴显示在右侧（x=totalX+1）
                addYAxis(totalX + 1, true);
                break;
              case 4: // 右下坐标系
                // X轴显示在底部（y=totalY+1）
                addXAxis(totalY + 1, true);
                // Y轴显示在右侧（x=totalX+1）
                addYAxis(totalX + 1, true);
                break;
            }

            function addXAxis(yPos, fz) {
              for (let x = 1; x <= totalX; x++) {
                var code = `X${x}`;
                if (fz) {
                  code = `X${totalX - x + 1}`;
                }
                var xCell = {
                  dispalyX: x,
                  dispalyY: yPos,
                  code: code,
                  fillColor: "transparent",
                  textColor: "#a0aec0",
                  borderColor: "transparent"
                };
                that.addNode(xCell);
              }

            }

            function addYAxis(xPos, fz) {
              for (let y = 1; y <= totalY; y++) {
                var code = `Y${y}`;
                if (fz) {
                  code = `Y${totalY - y + 1}`;
                }
                var yCell = {
                  dispalyX: xPos,
                  dispalyY: y,
                  code: code,
                  fillColor: "transparent",
                  textColor: "#a0aec0",
                  borderColor: "transparent"
                };
                that.addNode(yCell);
              }
            }

            this.loadData(this.lineModelData);


          }
        });
      }
    },

    // 更新后的 getCellInfos 方法，支持库位间隔显示和连接线
    getCellInfos(floor) {
      var that = this;
      this.cellInfos = [];
      if (floor != null) {
        if (that.myDiagram) {
          // 清理现有的Diagram
          that.myDiagram.div = null;
          that.init(that.displayMode); // 使用当前显示模式重新初始化
        }

        that.lineModelData = {
          class: "GraphLinksModel",
          nodeDataArray: [
          ],
          linkDataArray: [] // 添加连接线数组
        };
        listCellInfo({
          wareCode: that.wareCode,
          z: floor,
          isDelete: 0,
          pageSize: 999,
        }).then((response) => {
          this.loadData({});
          if (response.code == 200) {

            this.cellInfos = response.rows;
            this.nodes = [];


            var floorInfo = that.getFloorInfo(floor);
            var xy = floorInfo.xy;
            var totalX = floorInfo.totalX;
            var totalY = floorInfo.totalY;

            // 根据显示模式决定是否添加间隔
            if (that.displayMode === 'link' || that.displayMode === 'monitor') {
              // 连接线显示模式和监控模式：在每个库位之间添加间隔
              this.cellInfos.forEach((cell) => {
                // 为每个库位添加间隔（在坐标上乘以2）
                var dispalyX = 0;
                var dispalyY = 0;
                switch (xy) {
                  case 1: // 左上角
                    dispalyX = cell.x * 2;
                    dispalyY = cell.y * 2;
                    break;
                  case 2: // 右上角
                    dispalyX = (totalX - cell.x + 1) * 2;
                    dispalyY = cell.y * 2;
                    break;
                  case 3: // 左下角
                    dispalyX = cell.x * 2;
                    dispalyY = (totalY - cell.y + 1) * 2;
                    break;
                  case 4: // 右下角
                    dispalyX = (totalX - cell.x + 1) * 2;
                    dispalyY = (totalY - cell.y + 1) * 2;
                    break;
                }

                cell.dispalyX = dispalyX;
                cell.dispalyY = dispalyY;
                this.cellColor(cell);
                // 在连接线显示模式和监控模式下使用cell_can_link模板
                cell.category = "cell_can_link";
                this.addNode(cell);
              });

              // 在连接线显示模式下，坐标轴标签也需要间隔显示
              switch (xy) {
                case 1: // 左上坐标系
                  // X轴显示在顶部（y=0）
                  addXAxis(0, false, 2);
                  // Y轴显示在左侧（x=0）
                  addYAxis(0, false, 2);
                  break;
                case 2: // 右上坐标系
                  // X轴显示在底部（y=totalY+1）
                  addXAxis(totalY + 1, true, 2);
                  // Y轴显示在左侧（x=0）
                  addYAxis(0, false, 2);
                  break;
                case 3: // 左下坐标系
                  // X轴显示在顶部（y=0）
                  addXAxis(0, false, 2);
                  // Y轴显示在右侧（x=totalX+1）
                  addYAxis(totalX + 1, true, 2);
                  break;
                case 4: // 右下坐标系
                  // X轴显示在底部（y=totalY+1）
                  addXAxis(totalY + 1, true, 2);
                  // Y轴显示在右侧（x=totalX+1）
                  addYAxis(totalX + 1, true, 2);
                  break;
              }
            } else {
              // 正常显示模式：保持原有逻辑
              this.cellInfos.forEach((cell) => {
                var dispalyX = 0;
                var dispalyY = 0;
                switch (xy) {
                  case 1: // 左上角
                    dispalyX = cell.x;
                    dispalyY = cell.y;
                    break;
                  case 2: // 右上角
                    dispalyX = totalX - cell.x + 1;
                    dispalyY = cell.y;
                    break;
                  case 3: // 左下角
                    dispalyX = cell.x;
                    dispalyY = totalY - cell.y + 1;
                    break;
                  case 4: // 右下角
                    dispalyX = totalX - cell.x + 1;
                    dispalyY = totalY - cell.y + 1;
                    break;
                }

                cell.dispalyX = dispalyX;
                cell.dispalyY = dispalyY;
                this.cellColor(cell);
                this.addNode(cell);
              });

              // 正常显示模式下的坐标轴标签
              switch (xy) {
                case 1: // 左上坐标系
                  // X轴显示在顶部（y=0）
                  addXAxis(0);
                  // Y轴显示在左侧（x=0）
                  addYAxis(0);
                  break;
                case 2: // 右上坐标系
                  // X轴显示在底部（y=totalY+1）
                  addXAxis(totalY + 1, true);
                  // Y轴显示在左侧（x=0）
                  addYAxis(0);
                  break;
                case 3: // 左下坐标系
                  // X轴显示在顶部（y=0）
                  addXAxis(0);
                  // Y轴显示在右侧（x=totalX+1）
                  addYAxis(totalX + 1, true);
                  break;
                case 4: // 右下坐标系
                  // X轴显示在底部（y=totalY+1）
                  addXAxis(totalY + 1, true);
                  // Y轴显示在右侧（x=totalX+1）
                  addYAxis(totalX + 1, true);
                  break;
              }
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

            // 根据显示模式决定是否添加连接线
            if (that.displayMode === 'link') {
              // 在连接线显示模式下，调用addConnectionLines()，它会在完成后调用loadData
              that.addConnectionLines();
            } else if (that.displayMode === 'monitor') {
              // 在监控模式下，调用addMonitorConnectionLines()，从rcs_car_path表获取连接线
              that.addMonitorConnectionLines();
            } else {
              // 正常显示模式下，直接加载数据
              that.loadData(that.lineModelData);

              // 初始化 previousLinkDataArray（正常模式下通常为空）
              that.$nextTick(() => {
                if (that.myDiagram && that.myDiagram.model) {
                  that.previousLinkDataArray = JSON.parse(
                    JSON.stringify(that.myDiagram.model.linkDataArray || [])
                  );
                }
              });
            }
          }
        });
      }
    },

    // 添加连接线的方法，从cell_link表查询连接关系
    addConnectionLines() {
      var that = this;

      // 建立cellId到cellCode的映射
      const cellIdToCodeMap = {};
      this.cellInfos.forEach((cell) => {
        if (cell.id) {
          cellIdToCodeMap[cell.id] = cell.code;
        }
      });

      // 查询cell_link表，获取当前仓库的连接关系
      listCellLink({
        wareCode: that.wareCode,
        pageSize: 9999,
      }).then((response) => {
        if (response.code == 200) {
          const cellLinks = response.rows;

          // 根据查询结果添加连接线
          cellLinks.forEach((link) => {
            // 通过cellId找到对应的cellCode
            const fromCellCode = cellIdToCodeMap[link.fromCellId];
            const toCellCode = cellIdToCodeMap[link.toCellId];

            // 只有当两个库位都存在时才添加连接线
            if (fromCellCode && toCellCode) {
              // 根据是否阻塞设置不同的颜色
              const lineColor = link.isBlocked === 1 ? "#e53e3e" : "#667eea";

              that.lineModelData.linkDataArray.push({
                from: fromCellCode,
                to: toCellCode,
                color: lineColor,
                fromPort: "",  // 添加端口信息
                toPort: ""     // 添加端口信息
              });
            }
          });

          // 添加连接线后加载完整的模型数据（包括节点和连接线）
          that.loadData(that.lineModelData);

          // 初始化 previousLinkDataArray，用于后续检测变化
          that.$nextTick(() => {
            if (that.myDiagram && that.myDiagram.model) {
              that.previousLinkDataArray = JSON.parse(
                JSON.stringify(that.myDiagram.model.linkDataArray || [])
              );
            }
          });
        } else {
          console.error("查询库位连接关系失败:", response.msg);
          // 即使查询失败也加载节点数据
          that.loadData(that.lineModelData);

          // 初始化空的 previousLinkDataArray
          that.$nextTick(() => {
            if (that.myDiagram && that.myDiagram.model) {
              that.previousLinkDataArray = JSON.parse(
                JSON.stringify(that.myDiagram.model.linkDataArray || [])
              );
            }
          });
        }
      }).catch((error) => {
        console.error("查询库位连接关系出错:", error);
        // 即使出错也加载节点数据
        that.loadData(that.lineModelData);

        // 初始化空的 previousLinkDataArray
        that.$nextTick(() => {
          if (that.myDiagram && that.myDiagram.model) {
            that.previousLinkDataArray = JSON.parse(
              JSON.stringify(that.myDiagram.model.linkDataArray || [])
            );
          }
        });
      });
    },

    // 添加监控模式的连接线，从rcs_car_path表查询
    addMonitorConnectionLines() {
      var that = this;

      // 建立cellId到cellCode的映射
      const cellIdToCodeMap = {};
      this.cellInfos.forEach((cell) => {
        if (cell.id) {
          cellIdToCodeMap[cell.id] = cell.code;
        }
      });

      // 查询rcs_car_path表，获取车辆路径数据
      listRcsCarPath({
        pageSize: 9999,
      }).then((response) => {
        if (response.code == 200) {
          const carPaths = response.rows;

          // 建立 cellId 到 path 记录的映射，方便查找节点状态
          const cellIdToPathMap = {};
          carPaths.forEach((path) => {
            cellIdToPathMap[path.cellId] = path;
          });

          // 根据查询结果添加连接线
          carPaths.forEach((path) => {
            // 通过cellId和nextId找到对应的cellCode
            const fromCellCode = cellIdToCodeMap[path.cellId];
            const toCellCode = cellIdToCodeMap[path.nextId];

            // 只有当两个库位都存在时才添加连接线
            if (fromCellCode && toCellCode && path.nextId) {
              // 路径的状态 = 目标节点（nextId）的状态
              // 查找以 nextId 作为 cellId 的记录，获取其 state
              const nextNodePath = cellIdToPathMap[path.nextId];
              const pathState = nextNodePath ? nextNodePath.state : 0; // 默认未占用

              // 监控模式下，根据目标节点状态设置不同的颜色和样式
              // state: 0-未占用, 1-已经占用还未下发给车子, 2-已经下发给车子, 3-车子已经走过
              let lineColor = "#667eea"; // 默认蓝色
              let lineThickness = 3; // 默认粗细
              let dashArray = null; // 默认实线

              switch (pathState) {
                case 0:
                  // 未占用 - 浅灰色、细线、虚线
                  lineColor = "#cbd5e0";
                  lineThickness = 2;
                  dashArray = [4, 4]; // 虚线
                  break;
                case 1:
                  // 已经占用还未下发给车子 - 橙色、中等粗细、实线
                  lineColor = "#f6ad55";
                  lineThickness = 4;
                  dashArray = null;
                  break;
                case 2:
                  // 已经下发给车子 - 绿色、粗线、流动虚线（正在执行中）
                  lineColor = "#48bb78";
                  lineThickness = 5;
                  dashArray = [8, 4]; // 虚线用于流动效果
                  break;
                case 3:
                  // 车子已经走过 - 蓝色、中等粗细、实线
                  lineColor = "#4299e1";
                  lineThickness = 3;
                  dashArray = null;
                  break;
                default:
                  lineColor = "#667eea";
                  lineThickness = 3;
                  dashArray = null;
              }

              const linkData = {
                from: fromCellCode,
                to: toCellCode,
                color: lineColor,
                thickness: lineThickness,
                fromPort: "",  // 添加端口信息
                toPort: "",    // 添加端口信息
                isAnimated: pathState === 2  // 标记是否需要流动动画
              };

              // 如果是虚线，添加 dashArray 属性
              if (dashArray) {
                linkData.dashArray = dashArray;
              }

              that.lineModelData.linkDataArray.push(linkData);
            }
          });

          // 添加连接线后加载完整的模型数据（包括节点和连接线）
          // 注意：动画会在 loadData 方法中自动启动
          that.loadData(that.lineModelData);

          // 初始化 previousLinkDataArray
          that.$nextTick(() => {
            if (that.myDiagram && that.myDiagram.model) {
              that.previousLinkDataArray = JSON.parse(
                JSON.stringify(that.myDiagram.model.linkDataArray || [])
              );
            }
          });
        } else {
          console.error("查询车辆路径失败:", response.msg);
          // 即使查询失败也加载节点数据
          that.loadData(that.lineModelData);

          // 初始化空的 previousLinkDataArray
          that.$nextTick(() => {
            if (that.myDiagram && that.myDiagram.model) {
              that.previousLinkDataArray = JSON.parse(
                JSON.stringify(that.myDiagram.model.linkDataArray || [])
              );
            }
          });
        }
      }).catch((error) => {
        console.error("查询车辆路径出错:", error);
        // 即使出错也加载节点数据
        that.loadData(that.lineModelData);

        // 初始化空的 previousLinkDataArray
        that.$nextTick(() => {
          if (that.myDiagram && that.myDiagram.model) {
            that.previousLinkDataArray = JSON.parse(
              JSON.stringify(that.myDiagram.model.linkDataArray || [])
            );
          }
        });
      });
    },

    // 为执行中的连接线添加流动动画（参考博客优化方案：使用skipsUndoManager）
    startLinkAnimations() {
      var that = this;
      if (!this.myDiagram) return;

      console.log("开始启动流动动画（skipsUndoManager方案）...");

      // 使用递归setTimeout实现循环动画
      const loop = () => {
        // 停止之前的动画定时器（如果存在）
        if (that.animationTimer) {
          clearTimeout(that.animationTimer);
        }

        that.animationTimer = setTimeout(() => {
          if (!that.myDiagram) return;

          // 保存原有的 skipsUndoManager 状态
          const oldskips = that.myDiagram.skipsUndoManager;
          // 设置为 true，允许在只读模式下修改属性，且不记录到撤销历史
          that.myDiagram.skipsUndoManager = true;

          // 遍历所有连接线
          that.myDiagram.links.each((link) => {
            // 只为标记为需要动画的连接线添加动画
            if (link.data.isAnimated) {
              // 通过名称查找 Shape 对象
              const dashedLinkShape = link.findObject("LINKSHAPE");

              if (dashedLinkShape) {
                // 计算新的偏移量
                const currentOffset = dashedLinkShape.strokeDashOffset || 0;
                const newOffset = currentOffset - 1;

                // 设置（移动）虚线偏移动画，当偏移到-12时重置为0
                dashedLinkShape.strokeDashOffset = (newOffset <= -12) ? 0 : newOffset;
              }
            }
          });

          // 恢复原有的 skipsUndoManager 状态
          that.myDiagram.skipsUndoManager = oldskips;

          // 递归调用，实现循环动画
          loop();
        }, 50); // 每50ms更新一次，约20fps
      };

      // 启动动画循环
      loop();

      console.log("流动动画已启动");
    },

    // 停止所有动画（模式切换时调用）
    stopLinkAnimations() {
      if (this.animationTimer) {
        console.log("停止流动动画");
        clearTimeout(this.animationTimer);
        this.animationTimer = null;
      }
    },

    // ============ 小车位置相关方法 ============

    /**
     * 根据插值计算GoJS显示坐标
     * @param {String} fromCellCode - 起始库位编码
     * @param {String} toCellCode - 目标库位编码
     * @param {Number} ratio - 位置插值系数(0-1)
     * @returns {{x: Number, y: Number}} GoJS坐标
     */
    calculatePositionByRatio(fromCellCode, toCellCode, ratio) {
      // 1. 查找两个库位的信息
      const fromCell = this.cellInfos.find(c => c.code === fromCellCode);
      const toCell = this.cellInfos.find(c => c.code === toCellCode);

      if (!fromCell || !toCell) {
        console.warn("库位不存在:", fromCellCode, toCellCode);
        return null;
      }

      // 2. 获取库位的GoJS坐标
      const scale = this.displayMode === 'normal' ? 1 : 2;
      const gridSize = this.gridSize.width;

      const fromX = fromCell.x * gridSize * scale;
      const fromY = fromCell.y * gridSize * scale;
      const toX = toCell.x * gridSize * scale;
      const toY = toCell.y * gridSize * scale;

      // 3. 线性插值计算小车位置
      const carX = fromX + (toX - fromX) * ratio;
      const carY = fromY + (toY - fromY) * ratio;

      return { x: carX, y: carY };
    },

    /**
     * 查找库位对象
     * @param {String} cellCode - 库位编码
     * @returns {Object|null} 库位信息
     */
    findCellByCode(cellCode) {
      return this.cellInfos.find(c => c.code === cellCode);
    },

    // ============ WebSocket相关方法 ============

    /**
     * 建立小车位置WebSocket连接
     */
    connectCarPositionWebSocket() {
      const that = this;

      // 检查是否已有连接
      if (this.carWebSocket && this.carWebSocket.readyState === WebSocket.OPEN) {
        console.log("WebSocket已连接，跳过重复连接");
        return;
      }

      // 检查必要参数
      if (!this.wareCode || !this.floor) {
        console.warn("仓库编码或楼层为空，无法建立WebSocket连接");
        this.$message.warning("请先选择仓库和楼层");
        return;
      }

      try {
        // 构建WebSocket地址
        let wsUrl;
        const userId = localStorage.getItem('userId') || 'anonymous';
        const userName = localStorage.getItem('userName') || '匿名用户';

        if (process.env.NODE_ENV === 'development') {
          // 开发环境：直接连接到后端服务器
          const protocol = 'ws:';
          const backendHost = 'localhost:8007'; // 后端服务器地址
          wsUrl = `${protocol}//${backendHost}/wcs/websocket/carPosition?userId=${userId}&userName=${encodeURIComponent(userName)}`;
        } else {
          // 生产环境：使用当前域名
          const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
          const host = window.location.host;
          wsUrl = `${protocol}//${host}/wcs/websocket/carPosition?userId=${userId}&userName=${encodeURIComponent(userName)}`;
        }

        console.log("正在连接小车位置WebSocket...");
        console.log("环境:", process.env.NODE_ENV);
        console.log("WebSocket URL:", wsUrl);
        console.log("仓库编码:", this.wareCode, "楼层:", this.floor);

        this.carWebSocket = new WebSocket(wsUrl);

        // 连接成功
        this.carWebSocket.onopen = () => {
          console.log("✓ 小车位置WebSocket连接成功");

          // 订阅当前仓库和楼层
          that.subscribeWarehouse(that.wareCode, that.floor);

          that.$message.success("小车位置实时监控已启动");

          // 启动心跳（每30秒）
          that.startHeartbeat();
        };

        // 接收消息
        this.carWebSocket.onmessage = (event) => {
          try {
            const message = JSON.parse(event.data);

            switch (message.type) {
              case 'carPosition':
                // 小车位置更新
                that.handleCarPositionUpdate(message);
                break;

              case 'heartbeat':
                // 心跳响应
                console.log("收到服务器心跳响应");
                break;

              case 'error':
                // 错误消息
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

        // 连接错误
        this.carWebSocket.onerror = (error) => {
          console.error("============ WebSocket连接错误 ============");
          console.error("错误详情:", error);
          console.error("WebSocket URL:", wsUrl);
          console.error("WebSocket状态:", that.carWebSocket ? that.carWebSocket.readyState : 'null');
          console.error("可能原因:");
          console.error("1. 后端WebSocket服务未启动");
          console.error("2. WebSocket路径配置错误");
          console.error("3. 网络连接问题");
          console.error("4. 跨域配置问题");
          console.error("=========================================");

          that.$message.warning("小车位置实时监控连接失败，请检查后端服务是否启动");
        };

        // 连接关闭
        this.carWebSocket.onclose = (event) => {
          console.log("============ WebSocket连接关闭 ============");
          console.log("关闭代码:", event.code);
          console.log("关闭原因:", event.reason);
          console.log("是否正常关闭:", event.wasClean);
          console.log("=========================================");

          // 停止心跳
          that.stopHeartbeat();

          // 只在监控模式下自动重连
          if (that.displayMode === 'monitor' && event.code !== 1000) {
            console.log("5秒后尝试重新连接...");
            setTimeout(() => {
              if (that.displayMode === 'monitor') {
                that.connectCarPositionWebSocket();
              }
            }, 5000);
          }
        };
      } catch (error) {
        console.error("创建WebSocket连接失败:", error);
        this.$message.error("创建WebSocket连接失败: " + error.message);
      }
    },

    /**
     * 订阅仓库和楼层
     */
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
      console.log("订阅仓库:", wareCode, "楼层:", floor);
    },

    /**
     * 切换楼层时更新订阅
     */
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
      console.log("切换楼层:", newFloor);
    },

    /**
     * 启动心跳
     */
    startHeartbeat() {
      const that = this;

      // 每30秒发送一次心跳
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

    /**
     * 停止心跳
     */
    stopHeartbeat() {
      if (this.heartbeatTimer) {
        clearInterval(this.heartbeatTimer);
        this.heartbeatTimer = null;
      }
    },

    /**
     * 断开WebSocket连接
     */
    disconnectCarPositionWebSocket() {
      this.stopHeartbeat();

      if (this.carWebSocket) {
        this.carWebSocket.close();
        this.carWebSocket = null;
        console.log("小车位置WebSocket已断开");
      }
    },

    /**
     * 启动路径状态自动刷新（监控模式）
     */
    startPathRefresh() {
      const that = this;

      // 清除之前的定时器
      this.stopPathRefresh();

      console.log("✓ 启动路径状态自动刷新（每1秒）");

      // 立即执行一次
      this.refreshPathStatus();

      // 每1秒刷新一次
      this.pathRefreshTimer = setInterval(() => {
        that.refreshPathStatus();
      }, 1000);
    },

    /**
     * 停止路径状态刷新
     */
    stopPathRefresh() {
      if (this.pathRefreshTimer) {
        clearInterval(this.pathRefreshTimer);
        this.pathRefreshTimer = null;
        console.log("停止路径状态刷新");
      }
    },

    /**
     * 刷新路径状态（从rcs_car_path表获取）
     */
    refreshPathStatus() {
      const that = this;

      request({
        url: "/wcs-rcs/RcsCarInfo/paths/status",
        method: "get"
      }).then(response => {
        if (response.code == 200) {
          // 后端返回所有路径，前端自己过滤当前楼层
          that.updatePathsOnDiagram(response.data);
        }
      }).catch(error => {
        console.error("刷新路径状态失败:", error);
      });
    },

    /**
     * 根据路径状态更新图上的连接线
     */
    updatePathsOnDiagram(pathsData) {
      if (!this.myDiagram) return;

      const that = this;
      const linkDataArray = this.myDiagram.model.linkDataArray;

      // 1. 构建当前楼层的库位code集合（用于过滤）
      const currentFloorCellCodes = new Set();
      this.cellInfos.forEach(cell => {
        currentFloorCellCodes.add(cell.code);
      });

      // 2. 创建路径状态映射表 {fromCode_toCode: state}，只保留当前楼层的路径
      const pathStateMap = {};
      pathsData.forEach(path => {
        // 只处理起点和终点都在当前楼层的路径
        if (currentFloorCellCodes.has(path.fromCellCode) &&
          currentFloorCellCodes.has(path.toCellCode)) {
          const key = `${path.fromCellCode}_${path.toCellCode}`;
          pathStateMap[key] = path.state;
        }
      });

      // 3. 遍历所有连接线，更新状态
      let hasChanges = false;
      linkDataArray.forEach(linkData => {
        const key = `${linkData.from}_${linkData.to}`;
        const newState = pathStateMap[key];

        if (newState !== undefined && linkData.pathState !== newState) {
          // 状态变化了，更新连接线样式
          hasChanges = true;

          // 更新数据模型
          that.myDiagram.model.setDataProperty(linkData, "pathState", newState);

          // 根据状态设置样式
          let color, dashArray, thickness, isAnimated;

          switch (newState) {
            case 0: // 未占用
              color = "#9E9E9E";
              dashArray = [5, 5];
              thickness = 1;
              isAnimated = false;
              break;
            case 1: // 已占用
              color = "#FF9800";
              dashArray = null;
              thickness = 2;
              isAnimated = false;
              break;
            case 2: // 执行中
              color = "#4CAF50";
              dashArray = [6, 6];
              thickness = 3;
              isAnimated = true;
              break;
            case 3: // 已完成
              color = "#2196F3";
              dashArray = null;
              thickness = 2;
              isAnimated = false;
              break;
            default:
              return;
          }

          that.myDiagram.model.setDataProperty(linkData, "color", color);
          that.myDiagram.model.setDataProperty(linkData, "dashArray", dashArray);
          that.myDiagram.model.setDataProperty(linkData, "thickness", thickness);
          that.myDiagram.model.setDataProperty(linkData, "isAnimated", isAnimated);
        }
      });

      // 如果有状态变化，重新启动动画
      if (hasChanges) {
        // console.log("路径状态已更新");

        // 重启流动动画
        this.stopLinkAnimations();
        this.startLinkAnimations();
      }
    },

    /**
     * 处理小车位置更新（插值方案）
     * @param {Object} carData - 小车数据
     */
    handleCarPositionUpdate(carData) {
      if (!this.myDiagram) return;

      const carCode = carData.carCode;
      const mmZ = carData.z;

      // 只显示当前楼层的小车
      // 注意：这里需要根据actual z坐标判断楼层
      // 简化处理：可以添加楼层判断逻辑

      // 方案1：使用插值方案计算位置（推荐）
      let goCoord = null;
      if (carData.fromCellCode && carData.toCellCode && carData.positionRatio !== null) {
        goCoord = this.calculatePositionByRatio(
          carData.fromCellCode,
          carData.toCellCode,
          carData.positionRatio
        );

        console.log(`小车 ${carCode} 位置: ${carData.fromCellCode}→${carData.toCellCode} (${(carData.positionRatio * 100).toFixed(1)}%)`);
      }

      if (!goCoord) {
        console.warn(`无法计算小车 ${carCode} 的位置`);
        return;
      }

      // 查找小车节点
      const carKey = `car_${carCode}`;
      const carNode = this.myDiagram.findNodeForKey(carKey);

      if (carNode) {
        // 更新现有节点
        this.myDiagram.model.setDataProperty(
          carNode.data,
          "loc",
          `${goCoord.x} ${goCoord.y}`
        );
        this.myDiagram.model.setDataProperty(carNode.data, "batteryLevel", carData.batteryLevel);
        this.myDiagram.model.setDataProperty(carNode.data, "direction", carData.direction);
        this.myDiagram.model.setDataProperty(carNode.data, "speed", carData.speed);
        this.myDiagram.model.setDataProperty(carNode.data, "taskState", carData.taskState);
      } else {
        // 添加新节点
        this.myDiagram.model.addNodeData({
          key: carKey,
          category: "car",
          code: carCode,
          name: carData.carName,
          loc: `${goCoord.x} ${goCoord.y}`,
          batteryLevel: carData.batteryLevel,
          direction: carData.direction,
          speed: carData.speed,
          taskState: carData.taskState
        });

        console.log(`添加新小车节点: ${carCode}`);
      }

      // 保存到本地状态
      this.carPositions[carCode] = carData;
    },

    /**
     * 移除小车节点
     * @param {String} carCode - 小车编码
     */
    removeCarNode(carCode) {
      if (!this.myDiagram) return;

      const carKey = `car_${carCode}`;
      const carNode = this.myDiagram.findNodeForKey(carKey);

      if (carNode) {
        this.myDiagram.model.removeNodeData(carNode.data);
        delete this.carPositions[carCode];
        console.log(`移除小车节点: ${carCode}`);
      }
    },

    /**
     * 初始加载所有小车位置（HTTP方式）
     */
    loadCarPositions() {
      const that = this;

      request({
        url: "/wcs-rcs/RcsCarInfo/positions",
        method: "get",
        params: {
          // wareCode: that.wareCode,
          // z: that.floor
        },
      }).then((response) => {
        if (response.code == 200) {
          console.log("加载小车位置:", response.data.length, "辆");

          response.data.forEach(car => {
            that.handleCarPositionUpdate({
              type: 'carPosition',
              carCode: car.code,
              carName: car.name,
              x: car.currentX,
              y: car.currentY,
              z: car.currentZ,
              fromCellCode: car.fromCellCode,
              toCellCode: car.toCellCode,
              positionRatio: car.positionRatio,
              direction: car.moveDirection,
              speed: car.speed,
              batteryLevel: car.batteryLevel,
              taskState: car.taskState
            });
          });
        }
      }).catch(error => {
        console.error("加载小车位置失败:", error);
        that.$message.error("加载小车位置失败");
      });
    },

    // ============ 原有方法继续 ============

    // 添加切换显示模式的方法
    switchDisplayMode(mode) {
      // 先停止所有动画、定时器和WebSocket
      this.stopLinkAnimations();
      this.stopPathRefresh();
      this.disconnectCarPositionWebSocket();

      this.displayMode = mode;

      // 清理现有的Diagram
      if (this.myDiagram) {
        this.myDiagram.div = null;
      }

      // 根据新模式重新初始化图表
      this.init(mode);

      // 重新加载当前楼层的数据
      if (this.floor != null) {
        this.getCellInfos(this.floor);
      }

      // 监控模式下启动WebSocket和路径刷新
      if (mode === 'monitor') {
        setTimeout(() => {
          this.connectCarPositionWebSocket();
          this.startPathRefresh();
        }, 500);
      }
    },

    updateCellView(z) {
      var that = this;
      if (z != null) {
        listCellInfo({ z: z, pageSize: 999, wareCode: that.wareCode }).then(
          (response) => {
            if (response.code == 200) {
              var cellInfos = response.rows;
              cellInfos.forEach((cell) => {
                if (cell.z != this.floor) {
                  return;
                }
                this.updateCellNode(cell);
              });
            }
          }
        );
      }
    },

    updateCellNode(cell) {
      if (this.myDiagram == undefined || this.myDiagram == null) {
        return;
      }

      var model = this.myDiagram.model;
      if (model == undefined || model == null) {
        return;
      }
      var nodes = this.myDiagram.model.nodeDataArray;
      var node = null;
      nodes.forEach((n) => {
        if (n.key == cell.code) {
          node = n;
          this.cellColor(cell);
          // console.log(node)
          model.setDataProperty(node, "borderColor", cell.borderColor);
          model.setDataProperty(node, "textColor", cell.textColor);
          model.setDataProperty(node, "fillColor", cell.fillColor);
        }
      });
    },

    cellColor(cellInfo) {
      const colorScheme = {
        empty: {          // 无货
          normal: {
            fill: "#2d3748",
            text: "#a0aec0",
            border: "#4a5568"
          },
          task: {
            fill: "#d69e2e",
            text: "#ffffff",
            border: "#4a5568"
          }
        },
        occupied: {       // 有货
          normal: {
            fill: "#38a169",
            text: "#ffffff",
            border: "#4a5568"
          },
          task: {
            fill: "#e53e3e",
            text: "#ffffff",
            border: "#4a5568"
          }
        },
        disabled: {
          fill: "#ffffff",
          text: "#2d3748",
          border: "#4a5568"
        }  // 禁用
      };

      let colors = {};

      if (cellInfo.disableState == 1) {
        colors = colorScheme.disabled;
      } else if (cellInfo.invenState == 0) {
        colors = cellInfo.taskState != 0
          ? colorScheme.empty.task
          : colorScheme.empty.normal;
      } else {
        colors = cellInfo.taskState != 0
          ? colorScheme.occupied.task
          : colorScheme.occupied.normal;
      }

      cellInfo.fillColor = colors.fill;
      cellInfo.textColor = colors.text;
      cellInfo.borderColor = colors.border;
    },

    addNode(cellInfo) {
      var that = this;

      var x = that.gridSize.width * cellInfo.dispalyX;
      var y = that.gridSize.height * cellInfo.dispalyY;

      var loc = "" + x + " " + y;
      var size = "" + that.gridSize.width + " " + that.gridSize.height;


      // 根据category确定节点类型，默认为cell
      var nodeCategory = cellInfo.category || "cell";

      var node = {
        category: nodeCategory,
        text: cellInfo.code,
        key: cellInfo.code,
        loc: loc,
        size: size,
        fillColor: cellInfo.fillColor,
        textColor: cellInfo.textColor,
        borderColor: cellInfo.borderColor,
      };
      that.lineModelData.nodeDataArray.push(node);
      // that.nodes.push(node);


      if (that.maxNode.y < y) {
        that.maxNode.y = y;
        that.maxNode.loc = "" + that.maxNode.x + " " + that.maxNode.y;
        that.maxNode.size = size;
        //  that.maxNode.text= cellInfo.code +"mmmm"+loc;
      }

      if (that.maxNode.x < x) {
        that.maxNode.x = x;

        that.maxNode.loc = "" + that.maxNode.x + " " + that.maxNode.y;
        that.maxNode.size = size;
        //that.maxNode.text= cellInfo.code +"mmmm"+loc;
      }

      if (that.minNode.y > y) {
        that.minNode.y = y;
        that.minNode.loc = "" + that.minNode.x + " " + that.minNode.y;
        that.minNode.size = size;
        // that.minNode.text= cellInfo.code +"mmmm"+loc;
      }

      if (that.minNode.x > x) {
        that.minNode.x = x;
        that.minNode.loc = "" + that.minNode.x + " " + that.minNode.y;
        that.minNode.size = size;
        // that.minNode.text= cellInfo.code +"mmmm"+loc;
      }

    },

    init(mode) {
      var that = this;
      var $ = go.GraphObject.make; // 定义模板时的简洁性
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.height);

      // 根据模式设置不同的配置
      var isReadOnly = mode !== 'link'; // 只有编辑模式可以编辑
      var enableLinking = mode === 'link'; // 只有编辑模式可以创建连接线

      this.myDiagram = $(
        go.Diagram,
        "diagramDiv", //必须命名或引用DIV HTML元素
        {
          // 设置Diagram的自动缩放属性
          initialAutoScale: go.Diagram.Uniform, // 初始化统一缩放，使图表自动填满容器
          //背景网格
          // grid: $(
          //   go.Panel,
          //   "Grid",
          //   { gridCellSize: CellSize },
          //   $(go.Shape, "LineH", { stroke: "lightgray" }),
          //   $(go.Shape, "LineV", { stroke: "lightgray" })
          // ),
          //网格捕捉
          "draggingTool.isGridSnapEnabled": true,
          //拖动捕捉
          "draggingTool.gridSnapCellSpot": go.Spot.TopLeft,
          //缩放捕捉
          "resizingTool.isGridSnapEnabled": true,
          LinkDrawn: showLinkLabel, // 下面定义了此DiagramEvent侦听器
          LinkRelinked: showLinkLabel,

          "undoManager.isEnabled": true, //启用撤消和重做
          isReadOnly: isReadOnly, // 根据模式设置是否只读
        }
      );

      var myDiagram = this.myDiagram;

      // 只在编辑模式下添加变化监听器
      if (mode === 'link') {
        this.myDiagram.addChangedListener(function (event) {
          // 当模型数据发生变化时，这个函数会被调用
          var modelData = myDiagram.model.toJson();
          that.modelData = modelData;

          /**
           * 检测link的增加以及减少，用来删除或者增加cell_link表的数据
           */
          // 获取当前的连接线数据
          var currentLinkDataArray = myDiagram.model.linkDataArray || [];

          // 检测连接线数组长度变化
          if (currentLinkDataArray.length !== that.previousLinkDataArray.length) {

            if (currentLinkDataArray.length > that.previousLinkDataArray.length) {
              // 连接线增加：找出新增的连接线
              var addedLinks = currentLinkDataArray.filter(function (currentLink) {
                return !that.previousLinkDataArray.some(function (prevLink) {
                  return prevLink.from === currentLink.from && prevLink.to === currentLink.to;
                });
              });

              // 处理每个新增的连接线
              addedLinks.forEach(function (link) {
                that.handleLinkAdded(link);
              });

            } else if (currentLinkDataArray.length < that.previousLinkDataArray.length) {
              // 连接线减少：找出删除的连接线
              var removedLinks = that.previousLinkDataArray.filter(function (prevLink) {
                return !currentLinkDataArray.some(function (currentLink) {
                  return currentLink.from === prevLink.from && currentLink.to === prevLink.to;
                });
              });

              // 处理每个删除的连接线
              removedLinks.forEach(function (link) {
                that.handleLinkRemovedByData(link);
              });
            }

            // 更新上一次的连接线数据（深拷贝）
            that.previousLinkDataArray = JSON.parse(JSON.stringify(currentLinkDataArray));
          }
        });
      }

      // 节点模板的辅助定义
      function nodeStyle() {
        return [
          //Node.location来自节点数据的“loc”属性，
          //由Point.parse静态方法转换。
          //如果Node.location发生更改，则更新节点数据的“loc”属性，
          //使用Point.stringify静态方法转换回来。
          new go.Binding("location", "loc", go.Point.parse).makeTwoWay(
            go.Point.stringify
          ),
          {
            //Node.location位于每个节点的左上角
            locationSpot: go.Spot.TopLeft, //go.Spot.BottomLeft（BottomRigh、TopLeft、TopRight、Left、Right、Top、Bottom、Center）
          },
        ];
      }

      function textStyle() {
        return {
          font: "bold 8pt Lato, Helvetica, Arial, sans-serif",  // 从16pt改为8pt，适应50x50格子
          stroke: "#303133",
        };
      }

      function makePort(name, align, spot, output, input) {
        var horizontal =
          align.equals(go.Spot.Top) || align.equals(go.Spot.Bottom);
        //端口基本上只是沿着节点的侧面延伸的透明矩形，
        //当鼠标经过时会变为彩色
        return $(go.Shape, {
          fill: "transparent", // 在mouseEnter事件处理程序中更改为颜色
          strokeWidth: 0, // no stroke
          width: horizontal ? NaN : 8, // if not stretching horizontally, just 8 wide
          height: !horizontal ? NaN : 8, // if not stretching vertically, just 8 tall
          alignment: align, // align the port on the main Shape
          stretch: horizontal
            ? go.GraphObject.Horizontal
            : go.GraphObject.Vertical,
          portId: name, // declare this object to be a "port"
          fromSpot: spot, // declare where links may connect at this port
          fromLinkable: output, // declare whether the user may draw links from here
          toSpot: spot, // declare where links may connect at this port
          toLinkable: input, // declare whether the user may draw links to here
          cursor: "pointer", // show a different cursor to indicate potential link point
          mouseEnter: function (e, port) {
            // the PORT argument will be this Shape
            if (!e.diagram.isReadOnly) port.fill = "rgba(255,0,255,0.5)";
          },
          mouseLeave: function (e, port) {
            port.fill = "transparent";
          },
        });
      }

      // 定义常规节点的节点模板
      myDiagram.nodeTemplate = $(
        go.Node,
        {
          click: function (e, node) { },
          resizable: true,
          resizeObjectName: "SHAPE",
          // 因为gridSnapCellSpot是中心，所以偏移节点的位置
          locationSpot: new go.Spot(0, 0, CellSize.width, CellSize.height),
          // 提供有关将任何东西掉落到“物品”上的视觉警告
          // mouseDragEnter: function (e, node) {
          //   e.handled = true;
          //   node.findObject("SHAPE").fill = "red";
          //   e.diagram.currentCursor = "not-allowed";
          //   highlightGroup(node.containingGroup, false);
          // },
          // mouseDragLeave: function (e, node) {
          //   node.updateTargetBindings();
          // },
          // mouseDrop: function (e, node) {
          //   //禁止将任何内容放到“项目”上
          //   node.diagram.currentTool.doCancel();
          // },
        },
        // 始终保存/加载节点左上角的点，而不是位置
        new go.Binding("position", "pos", go.Point.parse).makeTwoWay(
          go.Point.stringify
        ),
        //这是人们看到的最主要的东西
        $(
          go.Shape,
          "Rectangle",
          {
            name: "SHAPE",
            fill: "white",
            minSize: CellSize,
            desiredSize: CellSize,
          },
          new go.Binding("fill", "color"),
          new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(
            go.Size.stringify
          )
        ),
        $(
          go.Picture,
          //图片通常应该有明确的宽度和高度
          //此图片为红色背景，仅在没有url可见
          //或者当部分图像透明时
          {
            margin: 0,
            width: that.gridSize.width,
            height: that.gridSize.height,
            background: "white",
          },
          //将picture的source绑定为node.data中的source属性
          new go.Binding("source")
        ),
        {
          click: function (e, node) {
            // console.log(node.part.data);
            that.nowData = node.data;
            that.showBind = true;
          },
          cursor: "pointer", //改变鼠标样式变成小手
        }
      );

      // 库位节点模板
      this.myDiagram.nodeTemplateMap.add(
        "cell",
        $(
          go.Node,
          // { resizable: true, resizeObjectName: "SHAPE" },
          nodeStyle(),
          {
            movable: false,  // 禁止拖动库位节点
            copyable: false,  // 禁止复制
            deletable: false  // 禁止删除
          },
          $(
            go.Panel,
            "Spot",
            $(
              go.Shape,
              "Rectangle",
              {
                desiredSize: CellSize,
                strokeWidth: 3,  // 从10改为3，适应50x50格子
              },
              new go.Binding("fill", "fillColor"),
              new go.Binding("stroke", "borderColor"),

            ),
            $(
              go.TextBlock,
              "",
              textStyle(),
              new go.Binding("text", "text"),
              new go.Binding("stroke", "textColor")
            ),

          ),
          // 当按钮被点击时的处理函数
          {
            click: function (e, node) {
              // that.cellCode = node.text
              that.findCellInfo(node.data.key);
            },
            cursor: "pointer", //改变鼠标样式变成小手
          }
        ),

      );

      // 库位节点模板
      this.myDiagram.nodeTemplateMap.add(
        "cell_can_link",
        $(
          go.Node,
          // { resizable: true, resizeObjectName: "SHAPE" },
          nodeStyle(),
          {
            movable: false,  // 禁止拖动库位节点
            copyable: false,  // 禁止复制
            deletable: false  // 禁止删除
          },
          $(
            go.Panel,
            "Spot",
            $(
              go.Shape,
              "Rectangle",
              {
                desiredSize: CellSize,
                strokeWidth: 3,  // 从10改为3，适应50x50格子
              },
              new go.Binding("fill", "fillColor"),
              new go.Binding("stroke", "borderColor"),

            ),
            $(
              go.TextBlock,
              "",
              textStyle(),
              new go.Binding("text", "text"),
              new go.Binding("stroke", "textColor")
            ),
            makePort("T", go.Spot.Top, go.Spot.Top, true, true),
            makePort("L", go.Spot.Left, go.Spot.Left, true, true),
            makePort("R", go.Spot.Right, go.Spot.Right, true, true),
            makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, true)
          ),
          // 当按钮被点击时的处理函数
          {
            click: function (e, node) {
              // that.cellCode = node.text
              that.findCellInfo(node.data.key);
            },
            cursor: "pointer", //改变鼠标样式变成小手
          }
        ),

      );

      // 添加小车节点模板
      this.myDiagram.nodeTemplateMap.add(
        "car",  // 小车类别
        $(
          go.Node,
          "Spot",
          {
            locationSpot: go.Spot.Center,  // 以中心点定位
            selectable: true,
            movable: false,  // 不允许手动拖动
            layerName: "Foreground"  // 显示在最上层
          },
          // 主体面板
          $(
            go.Panel,
            "Auto",
            // 小车外框
            $(
              go.Shape,
              "RoundedRectangle",
              {
                fill: "#4CAF50",
                stroke: "#2E7D32",
                strokeWidth: 1.5,  // 从2改为1.5
                width: 20,         // 从30改为20，适应50x50格子
                height: 20         // 从30改为20
              },
              // 根据任务状态动态改变颜色
              new go.Binding("fill", "taskState", function (state) {
                switch (state) {
                  case 0: return "#9E9E9E";  // 空闲-灰色
                  case 1: return "#FFC107";  // 任务中-黄色
                  case 2: return "#4CAF50";  // 执行中-绿色
                  default: return "#9E9E9E";
                }
              })
            ),
            // 小车图标
            $(
              go.TextBlock,
              {
                text: "🚗",
                font: "12px sans-serif",  // 从18px改为12px
                margin: 1
              }
            )
          ),
          // 小车编号标签（底部）
          $(
            go.TextBlock,
            {
              alignment: go.Spot.Bottom,
              alignmentFocus: go.Spot.Top,
              font: "bold 7px sans-serif",  // 从10px改为7px
              stroke: "#333",
              margin: new go.Margin(1, 0, 0, 0)
            },
            new go.Binding("text", "code")
          ),
          // 电量显示（顶部）
          $(
            go.TextBlock,
            {
              alignment: go.Spot.Top,
              alignmentFocus: go.Spot.Bottom,
              font: "6px sans-serif",  // 从9px改为6px
              stroke: "#666",
              margin: new go.Margin(0, 0, 1, 0)
            },
            new go.Binding("text", "batteryLevel", function (level) {
              return "🔋" + (level || 0) + "%";
            })
          ),
          // 移动方向指示器（右侧三角形）
          $(
            go.Shape,
            "Triangle",
            {
              alignment: go.Spot.Right,
              alignmentFocus: go.Spot.Left,
              width: 5,   // 从8改为5
              height: 5,  // 从8改为5
              fill: "#FF5722",
              stroke: null,
              angle: 0
            },
            new go.Binding("visible", "speed", function (speed) {
              return speed && speed > 0;
            }),
            new go.Binding("angle", "direction", function (dir) {
              // 根据方向旋转三角形
              switch (dir) {
                case 1: return 0;    // 向右
                case 2: return 180;  // 向左
                case 3: return 270;  // 向上
                case 4: return 90;   // 向下
                default: return 0;
              }
            })
          )
        )
      );

      // 定义连接线模板
      this.myDiagram.linkTemplate = $(
        go.Link,
        {
          routing: go.Link.Orthogonal,
          corner: 5,
          curve: go.Link.JumpGap,
          selectionAdorned: true,
          selectable: true,
          relinkableFrom: true,
          relinkableTo: true,
        },
        $(
          go.Shape,
          {
            strokeWidth: 3,
            stroke: "#667eea",       // 科技蓝主色
            name: "LINKSHAPE"        // 为 Shape 命名，方便后续访问
          },
          new go.Binding("stroke", "color"),
          new go.Binding("strokeWidth", "thickness", (t) => t || 3),
          new go.Binding("strokeDashArray", "dashArray") // 支持虚线
        ),
        $(
          go.Shape,
          { toArrow: "Standard", stroke: "#667eea", fill: "#667eea" },
          new go.Binding("stroke", "color"), // 箭头颜色跟随连接线
          new go.Binding("fill", "color")    // 箭头填充颜色跟随连接线
        )
      );

      //如果来自“条件”节点，则使链接标签可见。
      //此侦听器由“LinkDrawn”和“LinkRelinked”DiagramEvents调用。
      function showLinkLabel(e) {

        var label = e.subject.findObject("LABEL");
        if (label !== null)
          label.visible = e.subject.fromNode.data.category === "Conditional";
      }
      //LinkingTool和RelinkingTool使用的临时链接也是正交的：
      this.myDiagram.toolManager.linkingTool.temporaryLink.routing =
        go.Link.Orthogonal;
      this.myDiagram.toolManager.relinkingTool.temporaryLink.routing =
        go.Link.Orthogonal;

      // 根据模式启用或禁用链接工具
      this.myDiagram.toolManager.linkingTool.isEnabled = enableLinking;
      this.myDiagram.toolManager.relinkingTool.isEnabled = enableLinking;

      this.load(); // load an initial diagram from some JSON text
    },




    load() {


      var data = {
        class: "GraphLinksModel",
        linkFromPortIdProperty: "fromPort",
        linkToPortIdProperty: "toPort",

        nodeDataArray: [],
        // linkDataArray: [{ from: -1, fromText: "123", to: -2 }],
      };

      this.myDiagram.model = go.Model.fromJson(data);

    },

    loadData(data) {
      var that = this;
      if (data == {}) {
        var modelData = go.Model.fromJson(data);
        that.myDiagram.model = modelData;
        return;
      }
      //分步加载
      if (data.nodeDataArray != undefined) {
        that.myDiagram.model.addNodeDataCollection([that.minNode, that.maxNode]);
        that.myDiagram.initialAutoScale = go.Diagram.Uniform;
        that.myDiagram.zoomToFit();
        that.loadDataInChunks(data.nodeDataArray, 20); // 每次加载 100 个节点
      }

      // 如果有链接数据，也添加到模型中
      if (data.linkDataArray != undefined) {
        that.myDiagram.model.addLinkDataCollection(data.linkDataArray);

        // 在监控模式下，连接线添加完成后启动动画
        if (that.displayMode === 'monitor') {
          // 使用 setTimeout 确保连接线已经渲染
          setTimeout(() => {
            that.startLinkAnimations();
          }, 100);
        }
      }
    },

    loadDataInChunks(data, chunkSize) {
      debugger
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
          requestAnimationFrame(loadNextChunk); // 使用 requestAnimationFrame 避免阻塞 UI
        }
      }
      loadNextChunk();
    },




    /**
     * 显示货位信息
     * 1. 显示指定货位的信息
     */

    findCellInfo(cellCode) {
      var that = this;

      request({
        url: "/wcs-base/CellInfo/getByCode",
        method: "get",
        params: { code: cellCode, wareCode: that.wareCode },
      }).then((response) => {
        if (response.code == 200) {
          this.cellInfo = response.data;

        } else {
          this.$modal.msgError(response.msg);
        }
      });

    },

    deleteCellInfo(cellCode) {
      var that = this;

      request({
        url: "/wcs-base/CellInfo/deleteByCode",
        method: "get",
        params: { code: cellCode, wareCode: that.wareCode },
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("删除成功");
          this.getCellInfos(that.floor);
        } else {
          this.$modal.msgError(response.msg);
        }
      });

    },

    //寻找库存
    findInventorys(cellCode) {
      this.invenLoading = true;
      listInventory({ cellCode: cellCode }).then((response) => {
        this.invenLoading = false;
        if (response.code == 200) {
          this.inventoryList = response.rows;
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },

    findModels(cellCode) {
      this.invenLoading = true;
      listMetalMode({ cellCode: cellCode }).then((response) => {
        this.invenLoading = false;
        if (response.code == 200) {
          this.MetalModeList = response.rows;
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },

    updateCellInfoState(cellInfo) {
      // if (cellInfo.palletCode != null && cellInfo.invenState == 0) {
      //   this.$modal.msgError("该货位有托盘，无法将库存状态设置未无货");
      //   return;
      // }
      request({
        url: "/wcs-base/palletInfo/updateCellInfoState",
        method: "post",
        data: cellInfo,
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.findCellInfo(cellInfo.code);
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },

    /**
     * 处理连接线添加事件
     * @param {Object} linkData - 连接线数据，包含 from 和 to 属性
     */
    handleLinkAdded(linkData) {
      var that = this;

      // 根据 cellCode 查找对应的 cellId
      const fromCell = this.cellInfos.find(cell => cell.code === linkData.from);
      const toCell = this.cellInfos.find(cell => cell.code === linkData.to);

      if (!fromCell || !toCell) {
        console.error("无法找到对应的库位信息");
        return;
      }

      // 构建 cell_link 数据
      const cellLinkData = {
        fromCellId: fromCell.id,
        toCellId: toCell.id,
        distance: 1.0,  // 默认距离
        isBlocked: 0,  // 默认不阻塞
        wareCode: that.wareCode,
        createTime: new Date().toISOString()
      };

      // 调用 API 添加到数据库
      addCellLink(cellLinkData).then((response) => {
        if (response.code == 200) {
          // that.$modal.msgSuccess(`连接线已添加：${linkData.from} -> ${linkData.to}`);
          // console.log("连接线已保存到数据库");
        } else {
          that.$modal.msgError("添加连接线失败：" + response.msg);
        }
      }).catch((error) => {
        console.error("添加连接线出错：", error);
        that.$modal.msgError("添加连接线失败");
      });
    },

    /**
     * 处理连接线删除事件（根据连接线数据）
     * @param {Object} linkData - 被删除的连接线数据，包含 from 和 to 属性
     */
    handleLinkRemovedByData(linkData) {
      var that = this;

      // 根据 cellCode 查找对应的 cellId
      const fromCell = this.cellInfos.find(cell => cell.code === linkData.from);
      const toCell = this.cellInfos.find(cell => cell.code === linkData.to);

      if (!fromCell || !toCell) {
        console.error("无法找到对应的库位信息");
        return;
      }

      // 直接调用后端的删除接口
      deleteByFromCellIdAndToCellIdAndWareCode({
        fromCellId: fromCell.id,
        toCellId: toCell.id,
        wareCode: that.wareCode
      }).then((response) => {
        if (response.code == 200) {
          // console.log(`连接线已删除：${linkData.from} -> ${linkData.to}`);
          // that.$modal.msgSuccess(`连接线已删除：${linkData.from} -> ${linkData.to}`);
        } else {
          that.$modal.msgError("删除连接线失败：" + response.msg);
        }
      }).catch((error) => {
        console.error("删除连接线出错：", error);
        that.$modal.msgError("删除连接线失败");
      });
    },
  },
};
</script>

<style scoped>
/* CSS动画：连接线流动效果 */
@keyframes link-flow {
  from {
    stroke-dashoffset: 0;
  }

  to {
    stroke-dashoffset: -12px;
  }
}

/* 为需要动画的连接线应用CSS动画 */
::v-deep .link-flow-animation {
  animation: link-flow 1s linear infinite;
}

.containerSxc {
  >* {
    position: relative;
    z-index: 1;
  }

  .deviceState {
    color: #f2f6fc;
    width: 80%;
    margin-left: 10%;
    height: 30px;
    margin-top: 20px;
    background-color: #67c23a;
    text-align: center;
    line-height: 30px;
  }

  .kongCell {
    background: #2d3748;
    border: 2px solid #4a5568;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .el-icon-close {
      display: none;
    }
  }

  .noKongCell {
    background: #38a169;
    border: 2px solid #2f855a;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(56, 161, 105, 0.2);

    .el-icon-close {
      display: none;
    }
  }

  .inCell {
    background: #d69e2e;
    border: 2px solid #b7791f;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(214, 158, 46, 0.2);

    .el-icon-close {
      display: none;
    }
  }

  .outCell {
    background: #e53e3e;
    border: 2px solid #c53030;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(229, 62, 62, 0.2);

    .el-icon-close {
      display: none;
    }
  }

  .disableCell {
    background: #ffffff;
    border: 2px solid #e2e8f0;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .el-icon-close {
      display: contents;
      color: #2d3748;
    }
  }

  /* 添加通用样式 */
  [class$="Cell"] {
    width: 32px;
    height: 24px;
    margin: 2px;
    backdrop-filter: blur(1px);
  }

  [class$="Cell"]:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
}

/* 卡片样式 */
.el-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  color: #ffffff;
}

.el-card .el-card__header {
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.el-card .el-card__body {
  color: #ffffff;
}

/* 楼层选择面板 */
.floor-panel {
  backdrop-filter: blur(1px);
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.floor-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.floor-header {
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
  position: relative;
  z-index: 2;
}

.floor-header i {
  color: #667eea;
  margin-right: 8px;
  font-size: 18px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.floor-header span {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.floor-container {
  max-height: 60vh;
  overflow-y: auto;
  position: relative;
  z-index: 2;
}

/* 自定义滚动条 */
.floor-container::-webkit-scrollbar {
  width: 6px;
}

.floor-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 3px;
}

.floor-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.floor-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

.floor-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 12px 16px;
  margin-bottom: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.floor-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.03);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.floor-card:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.2);
}

.floor-card:hover::before {
  opacity: 1;
}

.floor-card.active {
  background: rgba(102, 126, 234, 0.1);
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.floor-card.active::before {
  opacity: 1;
  background: rgba(102, 126, 234, 0.2);
}

.card-left {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 2;
}

.card-left i {
  font-size: 20px;
  color: #667eea;
  margin-right: 12px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.floor-title {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

/* 货位信息面板 */
.info-panel {
  margin-top: 1vh;
  height: 90vh;
  /* background: rgba(255, 255, 255, 0.03); */
  backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.info-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.03);
  pointer-events: none;
}

.info-header {
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
}

.info-header i {
  color: #667eea;
  margin-right: 8px;
  font-size: 18px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.info-header span {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.info-content {
  padding: 20px;
  position: relative;
  z-index: 2;
  height: calc(100% - 60px);
  overflow-y: auto;
}

/* 自定义滚动条 */
.info-content::-webkit-scrollbar {
  width: 6px;
}

.info-content::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 3px;
}

.info-content::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.info-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  margin-bottom: 4px;
}

.form-value {
  color: #e2e8f0;
  font-size: 14px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  backdrop-filter: blur(1px);
  min-height: 20px;
  display: flex;
  align-items: center;
}

.form-input {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: #ffffff;
  font-size: 14px;
  backdrop-filter: blur(1px);
  transition: all 0.3s ease;
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.15);
}

.form-input.disabled {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: #a0aec0;
  cursor: not-allowed;
}

.form-select {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: #ffffff;
  font-size: 14px;
  backdrop-filter: blur(1px);
  transition: all 0.3s ease;
  cursor: pointer;
}

.form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.15);
}

.form-select option {
  background: #2d3748;
  color: #ffffff;
  padding: 8px;
}

.form-actions {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-primary {
  background: #667eea;
  border: none;
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-primary i {
  font-size: 16px;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-danger {
  background: #e53e3e;
  border: none;
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  box-shadow: 0 4px 15px rgba(229, 62, 62, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-danger i {
  font-size: 16px;
}

.btn-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(229, 62, 62, 0.6);
}

.btn-danger:active {
  transform: translateY(0);
}

.btn-secondary {
  background: #718096;
  border: none;
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  box-shadow: 0 4px 15px rgba(113, 128, 150, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-secondary i {
  font-size: 16px;
}

.btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(113, 128, 150, 0.6);
}

.btn-secondary:active {
  transform: translateY(0);
}

.btn-outbound {
  background: #e53e3e;
  border: none;
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  box-shadow: 0 4px 15px rgba(229, 62, 62, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-outbound i {
  font-size: 16px;
}

.btn-outbound:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(229, 62, 62, 0.6);
}

.btn-outbound:active {
  transform: translateY(0);
}

/* 货位状态图例样式优化 */
.containerSxc .el-card .el-card__body>div:first-child {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.containerSxc .el-card .el-card__body>div:first-child>div {
  color: #ffffff;
  font-weight: 500;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  font-size: 14px;
  margin-bottom: 4px;
}

.containerSxc .el-card .el-card__body>div:first-child>div:last-child {
  margin-bottom: 0;
}

/* 状态指示器样式 */
.containerSxc .el-card .el-card__body>div:first-child .kongCell,
.containerSxc .el-card .el-card__body>div:first-child .noKongCell,
.containerSxc .el-card .el-card__body>div:first-child .inCell,
.containerSxc .el-card .el-card__body>div:first-child .outCell,
.containerSxc .el-card .el-card__body>div:first-child .disableCell {
  width: 20px;
  height: 16px;
  margin-right: 8px;
  border-radius: 4px;
  display: inline-block;
  vertical-align: middle;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 主卡片样式 */
.main-card {
  height: 90vh;
  margin: 1vh;
}

/* 图例容器 */
.legend-container {
  padding: 1vh 0;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: transparent;
}

/* 侧边栏模式切换按钮样式 */
.mode-toggle-sidebar {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.mode-toggle-sidebar .mode-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
}

.mode-toggle-sidebar .mode-header i {
  color: #667eea;
  margin-right: 8px;
  font-size: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.mode-toggle-sidebar .mode-header span {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.mode-toggle-sidebar .mode-btn {
  width: 100%;
  margin-bottom: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
}

.mode-toggle-sidebar .mode-btn i {
  margin-right: 6px;
  font-size: 14px;
}

.mode-toggle-sidebar .mode-btn:hover {
  transform: translateX(3px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.mode-toggle-sidebar .mode-btn.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.mode-toggle-sidebar .mode-btn:last-child {
  margin-bottom: 0;
}

/* 监控模式状态图例样式 */
.monitor-legend-container {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 12px;
}

.monitor-legend-container .legend-title {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #ffffff;
  font-weight: 600;
  font-size: 14px;
}

.monitor-legend-container .legend-title i {
  color: #667eea;
  margin-right: 6px;
  font-size: 16px;
}

.monitor-legend-container .legend-items {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 12px;
}

.monitor-legend-container .legend-item {
  display: flex;
  align-items: center;
  color: #e2e8f0;
  font-size: 13px;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  transition: all 0.3s ease;
}

.monitor-legend-container .legend-item:hover {
  background: rgba(255, 255, 255, 0.03);
  transform: translateY(-1px);
}

.monitor-legend-container .legend-item span {
  white-space: nowrap;
}

/* 画布容器 */
.diagram-container {
  flex-grow: 1;
  height: calc(100vh - 200px);
  background-color: transparent;
  position: relative;
  -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
  cursor: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .containerSxc {
    padding: 10px;
  }

  .floor-card {
    height: 50px;
    padding: 8px 12px;
  }

  .card-left i {
    font-size: 16px;
    margin-right: 8px;
  }

  .floor-title {
    font-size: 12px;
  }

  .info-panel {
    height: auto;
    min-height: 400px;
  }

  .form-container {
    gap: 12px;
  }

  .form-group {
    gap: 4px;
  }
}
</style>```
