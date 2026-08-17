<template>
  <div class="containerDdj">
    <el-row>
      <el-col :span="4">
        <el-card style="
            width: 95%;
            height: 90vh;

            margin-left: 5%;
            margin-top: 1vh;
            padding-top: 2vh;
          ">



          <!-- 巷道 -->
          <!-- <div style="width: 100%; margin-top: 50px">
            <el-radio
              v-for="item in lineInfos"
              style="
                display: block;
                width: 90%;
                margin-left: 5%;
                margin-top: 5px;
              "
              v-model="lineCode"
              :label="item.code"
              border
              >{{ item.code }}</el-radio
            >
          </div> -->


            <div class="floor-header"  >
              <i class="el-icon-office-building"></i>
              <span>巷道选择</span>
            </div>

            <el-row :gutter="16" class="floor-container">
              <el-col v-for="item in lineInfos" :key="item.code" :span="24" @click.native="lineCode = item.code">
                <div class="floor-card" :class="{ active: lineCode === item.code }">
                  <div class="card-left">
                    <i class="el-icon-guide"></i>
                    <span class="floor-title">{{ item.code }}</span>
                  </div>
                  <div class="card-right">
                    <el-tag v-if="lineCode === item.code" type="success" effect="dark" size="small">
                      当前选中
                    </el-tag>
                  </div>
                </div>
              </el-col>
            </el-row>

          <!-- <el-button v-if="show" @click="updateLineModel">更新货位模型</el-button> -->
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
              <span style="margin-right: 8px;">空箱</span>
              <div class="emptyBoxCell" style="width: 20px; height: 16px; border-style: solid;"></div>
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

            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">过期</span>
              <div class="expiredCell" style="width: 20px; height: 16px; border-style: solid;"></div>
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
          <div v-show="cellInfo.code != null && isCell" class="info-content">
            <div v-loading="cellLoading" class="form-container">
              <div class="form-group">
                <label class="form-label">货位编码</label>
                <div class="form-value">{{ cellInfo.code }}</div>
              </div>

              <!-- <div class="form-group">
                <label class="form-label">货位层数</label>
                <div class="form-value">{{ cellInfo.z }}</div>
              </div> -->

              <!-- <div class="form-group">
                <label class="form-label">巷道编码</label>
                <div class="form-value">{{ cellInfo.lineCode }}</div>
              </div> -->

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

              <!-- <div class="form-group">
                <label class="form-label">货区编码</label>
                <input
                  :disabled="true"
                  v-model="cellInfo.areaCode"
                  class="form-input disabled"
                  readonly
                />
              </div> -->

              <div class="form-group" v-if="cellInfo.palletCode">
                <label class="form-label">托盘编码</label>
                <div class="form-value">{{ cellInfo.palletCode }}</div>
              </div>

              <!-- <div class="form-group" v-if="cellInfo.palletCode">
                <label class="form-label">托盘状态</label>
                <div class="form-value">
                  <el-tag v-if="cellInfo.isEmpty === '1' || cellInfo.isEmpty === 1" type="warning" size="small">空箱</el-tag>
                  <el-tag v-else-if="cellInfo.isEmpty === '0' || cellInfo.isEmpty === 0" type="success" size="small">有货</el-tag>
                  <span v-else style="color: #a0aec0;">未知状态</span>
                </div>
              </div> -->

              <!-- 料袋信息区域 -->
              <div v-if="cellInfo.bagMasterId" class="bag-info-section">
                <div class="section-divider">
                  <i class="el-icon-tickets"></i>
                  <span>料袋信息</span>
                </div>

                <div class="form-group">
                  <label class="form-label">料号</label>
                  <div class="form-value highlight">{{ cellInfo.recipeId }}</div>
                </div>

                <div class="form-group">
                  <label class="form-label">配方名称</label>
                  <div class="form-value">{{ cellInfo.recipeName }}</div>
                </div>

                <!-- <div class="form-group" v-if="cellInfo.recipeBagName">
                  <label class="form-label">料袋条码</label>
                  <div class="form-value">{{ cellInfo.recipeBagName }}</div>
                </div> -->

                <!-- <div class="form-group" v-if="cellInfo.bagAllWeight">
                  <label class="form-label">料袋重量</label>
                  <div class="form-value">{{ cellInfo.bagAllWeight }} kg</div>
                </div>
                 -->
                <!-- <div class="form-group" v-if="cellInfo.orderNo">
                  <label class="form-label">计划编号</label>
                  <div class="form-value">{{ cellInfo.orderNo }}</div>
                </div> -->
              </div>

              <div class="form-actions">
                <button class="btn-primary" @click="updateCellInfoState(cellInfo)">
                  <i class="el-icon-check"></i>
                  修改提交
                </button>
              </div>

              <div v-if="cellInfo != null && cellInfo.taskState == 0 && cellInfo.invenState == 1" class="form-actions">
                <button class="btn-outbound" @click="saveOutTask(cellInfo.code,cellInfo.wareCode)">
                  <i class="el-icon-download"></i>
                  出库
                </button>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {listLineInfo} from "@/api/wcs-base/LineInfo";
import { addTaskInfo } from "@/api/wcs-task/TaskInfo";
import request from "@/utils/request";
export default {
  components: {

  },
  name: "container",
  props: {
    wareCode: {
      type: String,
      default: null,
    },

  },

  data() {
    return {
      isCell: true,
      aaaaa: 0,
      //网格尺寸
      gridSize: {
        width: 100,
        height: 80,
      },
      wareInfos: [],
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
      }
    };
  },
  watch: {
    wareCode(newValue, oldValue) {
      // console.log("newValue:", newValue);
      this.getLineInfos(newValue);
    },
    //巷道跟换将画布清空
    lineCode(newValue, oldValue) {
      this.loadData({});
      this.getCellInfos(newValue);
    },
    cellCode(newValue, oldValue) {
      if (newValue != null) {
        this.findCellInfo(newValue);
      }
    },
  },
  created() {
    var that = this;
    if (this.wareCode != null) {
      that.getLineInfos(this.wareCode);
    }
    if (this.lineCode != null) {
      that.getCellInfos(this.lineCode);
    }
    this.timer = setInterval(() => {
      if (this.lineInfos.length == 0) {
        return;
      }
      if (this.lineCode == null || this.lineCode == "") {
        return;
      }
      this.updateCellView(this.lineCode);
    }, 3000);
  },
  mounted() {
    this.init();
  },
  beforeDestroy() {
    if (this.timer) {
      //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
  },

  methods: {

    //获取所有的巷道
    getLineInfos(wareCode) {
      debugger;
      this.lineInfos = [];
      listLineInfo({ wareCode: wareCode }).then((response) => {
        if (response.code == 200) {
          this.lineInfos = response.rows;
          if (this.lineInfos.length > 0) {
            this.lineCode = this.lineInfos[0].code;
          }

          //  this.getCellInfos(this.lineCode);
        }
      });
    },
    saveOutTask(cellCode,wareCode) {
      addTaskInfo({ type: 'ddj_out', fromCellCode: cellCode,toCellCode: '19',wareCode: wareCode}).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },

    getMaxZ(lineCode) {
      for (let index = 0; index < this.lineInfos.length; index++) {
        const lineInfo = this.lineInfos[index];
        if (lineInfo.code == lineCode) {
          return lineInfo.maxZ;
        }
      }
    },
    //获取所有的货位
    getCellInfos(lineCode) {
      var that = this;
      this.cellInfos = [];
      if (lineCode != null) {
        if (that.myDiagram) {
          // 清理现有的Diagram
          that.myDiagram.div = null;

        }
        that.init();
        that.minNode = {
          category: "cell", x: 999009999, y: 999009999, borderColor: "transparent",
          textColor: "transparent",
          fillColor: "transparent"
        };
        that.maxNode = {
          category: "cell", x: -100000000, y: -100000000, borderColor: "transparent",
          textColor: "transparent",
          fillColor: "transparent"
        };
        that.lineModelData = {
          class: "GraphLinksModel",
          nodeDataArray: [
          ]
        };
        request({
          url: "/wcs-ds/bagMaster/findByLineCodeWithPalletAndExpiry",
          method: "get",
          params: {
            wareCode: that.wareCode,
            lineCode: that.lineCode
          },
        }).then((response) => {
          if (response.code == 200) {

            this.cellInfos = response.data.list;
            this.nodes = [];
            var maxZ = response.data.lineInfo.maxZ;
            var startDirection = response.data.lineInfo.startDirection;
            var maxDiapalyX = response.data.lineInfo.maxY;
            var maxDiapalyY = 0;
            var minDiapalyY = 0;


            this.cellInfos.forEach((cell) => {

              // console.info(cell);

              var priority = cell.priority;

              // if (startDirection === 'RightToLeft') {
              //   dispalyX = 200 - cell.y + 1; // 反向排列
              // }

              // var dispalyX = cell.y;
              var dispalyY;
              var dispalyX
              if (startDirection == 'right') {
                dispalyX = maxDiapalyX - cell.y + 1; // 反向排列
              } else {
                dispalyX = cell.y;
              }

              var BY = 1 + priority + maxZ * (priority - 1);

              // 使用 if-else 确保 dispalyY 总是被赋值
              if (cell.ab === "A") {
                dispalyY = -(maxZ * (priority - 1) + cell.z + priority);
              } else if (cell.ab === "B") {
                dispalyY = (maxZ * (priority - 1) + cell.z + priority);
                dispalyY = dispalyY - BY;
                dispalyY = maxZ - 1 - dispalyY;
                dispalyY = dispalyY + BY;

              } else {
                // 处理 cell.ab 不是 "A" 或 "B" 的情况
                // console.warn(`Unknown value for cell.ab: ${cell.ab}`);
                dispalyY = 0;
              }
              cell.dispalyX = dispalyX;
              cell.dispalyY = dispalyY;

              if (minDiapalyY > dispalyY) {
                minDiapalyY = dispalyY;
              }
              if (minDiapalyY < 0) {
                maxDiapalyY = maxDiapalyY - minDiapalyY;
              }

              this.cellColor(cell);
              this.addNode(cell);

              //纵坐标
              if (dispalyX == 1) {
                cell.dispalyX = 0;
                cell.dispalyY = dispalyY;
                cell.code = cell.z;
                cell.fillColor = "transparent";
                cell.textColor = "#a0aec0";
                cell.borderColor = "transparent";
                this.addNode(cell);
              }
            });

            //横坐标
            for (let index = 1; index <= maxDiapalyX; index++) {
              var cell = {};
              cell.dispalyX = index;
              cell.dispalyY = minDiapalyY - 1;
              cell.code = index;
              cell.fillColor = "transparent";
              cell.textColor = "#a0aec0";
              cell.borderColor = "transparent";
              this.addNode(cell);
            }
            //中间巷道
            for (let index = 1; index <= maxDiapalyX; index++) {
              var cell = {};
              cell.dispalyX = index;
              cell.dispalyY = 0;
              cell.code = "";
              cell.fillColor = "rgba(102, 126, 234, 0.3)";
              cell.textColor = "#667eea";
              cell.borderColor = "rgba(102, 126, 234, 0.5)";
              this.addNode(cell);
            }
            this.loadData(this.lineModelData);


          }
        });
      }
    },



    updateCellView(lineCode) {
      var that = this;
      if (lineCode != null && that.wareCode != null) {
        request({
          url: "/wcs-ds/bagMaster/findByLineCodeWithPalletAndExpiry",
          method: "get",
          params: {
            wareCode: that.wareCode,
            lineCode: lineCode
          },
        }).then((response) => {
          if (response.code == 200) {
            var cellInfos = response.data.list;
            cellInfos.forEach((cell) => {
              if (cell.lineCode != lineCode) {
                return;
              }
              this.updateCellNode(cell);
            });
          }
        });
      }
    },

    updateCellNode(cell) {
      if (this.myDiagram == null) {
        return;
      }
      var model = this.myDiagram.model;
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
            border: "#b7791f"
          }
        },
        emptyBox: {       // 空箱（新增）
          fill: "#60a5fa",  // 浅蓝色（更易区分）
          text: "#ffffff",
          border: "#3b82f6"
        },
        occupied: {       // 有货
          normal: {
            fill: "#38a169",
            text: "#ffffff",
            border: "#2f855a"
          },
          task: {
            fill: "#e53e3e",
            text: "#ffffff",
            border: "#c53030"
          }
        },
        expired: {       // 过期（新增）
          fill: "#9333ea",  // 紫色
          text: "#ffffff",
          border: "#7e22ce"
        },
        disabled: {
          fill: "#ffffff",
          text: "#2d3748",
          border: "#e2e8f0"
        }  // 禁用
      };

      let colors = {};

      // 1. 禁用状态（最高优先级）
      if (cellInfo.disableState == 1) {
        colors = colorScheme.disabled;
      }
      // 2. 过期状态（第二优先级，如果有料袋且过期）
      else if (cellInfo.isExpired === 1 || cellInfo.isExpired === "1") {
        // 过期状态 - 显示紫色
        colors = colorScheme.expired;
      }
      // 3. 任务状态
      else if (cellInfo.taskState != 0) {
        if (cellInfo.invenState == 0) {
          // 无货+任务中 = 入库中
          colors = colorScheme.empty.task;
        } else {
          // 有货+任务中 = 出库中
          colors = colorScheme.occupied.task;
        }
      }
      // 4. 库存状态
      else if (cellInfo.invenState == 0 || !cellInfo.palletCode) {
        // 无货（无托盘或库存状态为0）
        colors = colorScheme.empty.normal;
      }
      // 5. 有托盘的情况
      else if (cellInfo.isEmpty === "1" || cellInfo.isEmpty === 1) {
        // 空箱（新增状态）- isEmpty = "1" 表示空箱
        colors = colorScheme.emptyBox;
      } else {
        // 有货 - isEmpty = "0" 或其他值表示有货
        colors = colorScheme.occupied.normal;
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


      var node = {
        category: "cell",
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

    init() {
      var that = this;
      var $ = go.GraphObject.make; // 定义模板时的简洁性
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.height);
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
          // 鼠标滚轮缩放配置
          "commandHandler.zoomFactor": 1.1, // 缩放因子，每次滚轮滚动的缩放比例
          "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom, // 鼠标滚轮行为：缩放
          "undoManager.isEnabled": true, //启用撤消和重做
          isReadOnly: true, //只读
        }
      );

      var myDiagram = this.myDiagram;
      this.myDiagram.addChangedListener(function (event) {
        // 当模型数据发生变化时，这个函数会被调用
        // 使用event.change可以获取具体的变化信息
        //  var change = event.change;

        // 如果需要获取整个模型的数据，可以使用model.toJson()
        // var modelData = myDiagram.model.toJson();
        // that.modelData = modelData;
        // console.log("模型数据变化:", change);
        // console.log("最新模型数据:", modelData);
      });

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
          font: "bold 16pt Lato, Helvetica, Arial, sans-serif",
          stroke: "#303133",
        };
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

      this.myDiagram.nodeTemplateMap.add(
        "cell",
        $(
          go.Node,
          // { resizable: true, resizeObjectName: "SHAPE" },
          nodeStyle(),
          $(
            go.Panel,
            "Spot",
            $(
              go.Shape,
              "Rectangle",
              {
                desiredSize: CellSize,
                strokeWidth: 10,
              },
              new go.Binding("fill", "fillColor"),
              new go.Binding("stroke", "borderColor")
            ),
            $(
              go.TextBlock,
              "",
              textStyle(),
              new go.Binding("text", "text"),
              new go.Binding("stroke", "textColor")
            )
          ),
          // 当按钮被点击时的处理函数
          {
            click: function (e, node) {
              // that.cellCode = node.text
              that.findCellInfo(node.data.key);
            },
            cursor: "pointer", //改变鼠标样式变成小手
          }
        )
      );


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
        url: "/wcs-ds/bagMaster/getCellDetailInfo",
        method: "get",
        params: { cellCode: cellCode, wareCode: that.wareCode },
      }).then((response) => {
        if (response.code == 200) {
          if (response.data == null) {
            this.cellInfo = null;
            this.isCell = false;
          } else {
            this.cellInfo = response.data;
            this.isCell = true;
          }
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
        url: "/wcs-base/CellInfo/updateCellInfoState",
        method: "post",
        data: cellInfo,
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");

        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.containerDdj {
  // background: linear-gradient(135deg, #0c0c0c 0%, #1a1a2e 50%, #16213e 100%);
  // min-height: calc(100vh - 84px);
  // position: relative;
  // padding: 20px;

  // &::before {
  //   content: '';
  //   position: fixed;
  //   top: 0;
  //   left: 0;
  //   right: 0;
  //   bottom: 0;
  //   background:
  //     radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
  //     radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
  //     radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%);
  //   pointer-events: none;
  //   z-index: 0;
  // }

  > * {
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

  .emptyBoxCell {
    background: #60a5fa;
    border: 2px solid #3b82f6;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(96, 165, 250, 0.3);

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

  .expiredCell {
    background: #9333ea;
    border: 2px solid #7e22ce;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(147, 51, 234, 0.3);

    .el-icon-close {
      display: none;
    }
  }

  // 添加通用样式
  [class$="Cell"] {
    width: 32px;
    height: 24px;
    margin: 2px;
    backdrop-filter: blur(10px);

    &:hover {
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }
  }
}

// 卡片样式
.el-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  color: #ffffff;

  .el-card__header {
    background: rgba(255, 255, 255, 0.05);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    color: #ffffff;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  }

  .el-card__body {
    color: #ffffff;
  }
}

// 巷道选择面板
.floor-panel {
  backdrop-filter: blur(15px);
  border-radius: 12px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
  }
}

.floor-header {
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
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
  max-height: 75vh;
  overflow-y: auto;
  position: relative;
  z-index: 2;

  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 3px;

    &:hover {
      background: rgba(255, 255, 255, 0.5);
    }
  }
}

.floor-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
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

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.1);
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
  }
}

.floor-card:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.2);

  &::before {
    opacity: 1;
  }
}

.floor-card.active {
  background: rgba(102, 126, 234, 0.1);
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);

  &::before {
    opacity: 1;
    background: rgba(102, 126, 234, 0.2);
  }
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

// 货位信息面板
.el-form {
  .el-form-item__label {
    color: #ffffff !important;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  }

  .el-input__inner {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #ffffff;
    backdrop-filter: blur(10px);

    &::placeholder {
      color: rgba(255, 255, 255, 0.6);
    }

    &:focus {
      border-color: #667eea;
      box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
    }
  }

  .el-select {
    .el-input__inner {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: #ffffff;
      backdrop-filter: blur(10px);
    }
  }

  .el-button {
    background: #667eea;
    border: none;
    color: #ffffff;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

// 货位状态图例
.containerDdj > .el-row > .el-col:nth-child(2) .el-card {
  .el-card__body > div:first-child {
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 12px;

    > div {
      color: #ffffff;
      font-weight: 500;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
    }
  }
}

// 货位状态图例样式优化
.containerDdj .el-card .el-card__body > div:first-child {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);

  > div {
    color: #ffffff;
    font-weight: 500;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
    font-size: 14px;
    margin-bottom: 4px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  // 状态指示器样式
  .kongCell, .noKongCell, .inCell, .outCell, .disableCell, .expiredCell {
    width: 20px;
    height: 16px;
    margin-right: 8px;
    border-radius: 4px;
    display: inline-block;
    vertical-align: middle;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }
}

// 主卡片样式
.main-card {
  height: 90vh;
  margin: 1vh;
}

// 图例容器
.legend-container {
  padding: 1vh 0;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: transparent;
}

// 画布容器
.diagram-container {
  flex-grow: 1;
  height: calc(100vh - 200px);
  background-color: transparent;
  position: relative;
  -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
  cursor: auto;
}

// 右侧信息面板样式
.info-panel {
  margin-top: 1vh;
  height: 90vh;
  // background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.1);
    pointer-events: none;
  }
}

.info-header {
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;

  i {
    color: #667eea;
    margin-right: 8px;
    font-size: 18px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }

  span {
    color: #ffffff;
    font-size: 16px;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  }
}

.info-content {
  padding: 20px;
  position: relative;
  z-index: 2;
  height: calc(100% - 60px);
  overflow-y: auto;

  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 3px;

    &:hover {
      background: rgba(255, 255, 255, 0.5);
    }
  }
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
  backdrop-filter: blur(10px);
  min-height: 20px;
  display: flex;
  align-items: center;

  &.highlight {
    color: #60a5fa;
    font-weight: 600;
    background: rgba(96, 165, 250, 0.1);
    border-color: rgba(96, 165, 250, 0.2);
  }
}

.form-input {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: #ffffff;
  font-size: 14px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;

  &::placeholder {
    color: rgba(255, 255, 255, 0.6);
  }

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
    background: rgba(255, 255, 255, 0.15);
  }

  &.disabled {
    background: rgba(255, 255, 255, 0.05);
    border-color: rgba(255, 255, 255, 0.1);
    color: #a0aec0;
    cursor: not-allowed;
  }
}

.form-select {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: #ffffff;
  font-size: 14px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
    background: rgba(255, 255, 255, 0.15);
  }

  option {
    background: #2d3748;
    color: #ffffff;
    padding: 8px;
  }
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

  i {
    font-size: 16px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
  }

  &:active {
    transform: translateY(0);
  }
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

  i {
    font-size: 16px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(229, 62, 62, 0.6);
  }

  &:active {
    transform: translateY(0);
  }
}

// 料袋信息区域样式
.bag-info-section {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 2px solid rgba(96, 165, 250, 0.2);
}

.section-divider {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  i {
    color: #60a5fa;
    margin-right: 8px;
    font-size: 16px;
  }

  span {
    color: #ffffff;
    font-size: 14px;
    font-weight: 600;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .containerDdj {
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
</style>
