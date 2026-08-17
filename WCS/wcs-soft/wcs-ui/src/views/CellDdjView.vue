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


          <div class="floor-header">
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
              <span style="margin-right: 8px;">异常</span>
              <div class="errorCell" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>

            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">禁用</span>
              <div class="disableCell" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>

            <div style="display: flex; align-items: center; margin-right: 20px;">
              <span style="margin-right: 8px;">批量选中</span>
              <div class="batchSelectLegend" style="width: 20px; height: 16px; border-style: solid;"></div>
            </div>

            <el-button
              :type="palletBatchMode ? 'warning' : 'primary'"
              size="small"
              @click="togglePalletBatchMode"
            >
              {{ palletBatchMode ? '退出批量设置' : '设置适用托盘类型' }}
            </el-button>
          </div>
          <div v-if="palletBatchMode" class="batch-pallet-bar">
            <span class="batch-pallet-tip">按住左键划过库位多选，再次划过或点击已选库位可取消</span>
            <span class="batch-pallet-count">已选 {{ selectedBatchCellCodes.length }} 个库位</span>
            <label class="batch-pallet-label">适用托盘类型</label>
            <select v-model="batchPalletType" class="form-select batch-pallet-select">
              <option v-for="item in palletTypes" :key="item.code" :value="item.code">
                {{ item.name }}
              </option>
            </select>
            <el-button type="primary" size="small" :disabled="selectedBatchCellCodes.length === 0" @click="submitBatchPalletType">
              批量提交
            </el-button>
            <el-button size="small" @click="clearBatchSelection">清空选择</el-button>
          </div>
          <div
            id="diagramDiv"
            class="diagram-container"
            :class="{ 'diagram-batch-mode': palletBatchMode }"
          ></div>
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
                <label class="form-label">是否异常</label>
                <select v-model="cellInfo.isError" class="form-select">
                  <option v-for="item in isErrors" :key="item.value" :value="item.value">
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
                <label class="form-label">托盘编码</label>
                <input v-model="cellInfo.palletCode" class="form-input " />
              </div>

              <div class="form-group">
                <label class="form-label">适用托盘类型</label>
                <select v-model="cellInfo.palletType" class="form-select">
                  <option v-for="item in palletTypes" :key="item.code" :value="item.code">
                    {{ item.name }}
                  </option>
                </select>
              </div>

              <div class="form-group" v-if="cellInfo.palletCode">
                <label class="form-label">托盘状态</label>
                <select v-model="cellInfo.isEmpty" class="form-select">
                  <option :key="'1'" :value="'1'">
                    空箱
                  </option>
                  <option :key="'0'" :value="'0'">
                    有货
                  </option>
                </select>

              </div>

              <div class="form-actions">
                <button class="btn-primary" @click="updateCellInfoState(cellInfo)">
                  <i class="el-icon-check"></i>
                  修改提交
                </button>
                <button class="btn-delete" @click="deleteCell(cellInfo.code)">
                  <i class="el-icon-delete"></i>
                  删除库位
                </button>
              </div>

              <div v-if="cellInfo != null && cellInfo.taskState == 0 && cellInfo.invenState == 1" class="form-actions">
                <button class="btn-outbound" @click="saveOutTask(cellInfo.code, cellInfo.wareCode)">
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
import { listLineInfo } from "@/api/wcs-base/LineInfo";
import { listPalletType } from "@/api/wcs-base/palletType";
import request from "@/utils/request";
import go from "@/lib/js/go.js"
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
      isErrors: [
        { value: 1, label: "异常库位" },
        { value: 0, label: "正常库位" },
      ],
      taskStates: [
        { value: 1, label: "任务中" },
        { value: 0, label: "无任务" },
      ],
      invenStates: [
        { value: 1, label: "有货" },
        { value: 0, label: "无货" },
      ],
      // 托盘类型数据
      palletTypes: [],

      /** 批量设置适用托盘类型 */
      palletBatchMode: false,
      batchSelectDragging: false,
      batchDragLastCellKey: null,
      _batchDragMoved: false,
      selectedBatchCellCodes: [],
      batchPalletType: null,
      cellCodeSet: {},
      batchSelectBorderColor: "#409EFF",
      _batchSelectHandlers: null,
      _batchSelectDiv: null,

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
      this.exitPalletBatchMode();
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
    // 获取托盘类型数据
    that.getPalletTypes();
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
    this.unbindBatchSelectTools();
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

    // 获取托盘类型数据
    getPalletTypes() {
      listPalletType().then((response) => {
        if (response.code == 200) {
          this.palletTypes = response.rows;
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    saveOutTask(cellCode, wareCode) {
      addTaskInfo({ type: 'OUT', fromCellCode: cellCode, wareCode: wareCode }).then((response) => {
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
          url: "/wcs-base/CellInfo/findByLineCodeWithPallet",
          method: "get",
          params: {
            wareCode: that.wareCode,
            lineCode: that.lineCode
          },
        }).then((response) => {
          if (response.code == 200) {

            this.cellInfos = response.data.list;
            this.rebuildCellCodeSet();
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
          url: "/wcs-base/CellInfo/findByLineCodeWithPallet",
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
          if (this.isBatchCellSelected(cell.code)) {
            model.setDataProperty(node, "borderColor", this.batchSelectBorderColor);
          }
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
        isError: {
          fill: "#ce1abf",
          text: "#2d3748",
          border: "#ce1abf"
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
      // 异常状态
      else if (cellInfo.isError == 1) {
        colors = colorScheme.isError;
      }
      // 2. 任务状态
      else if (cellInfo.taskState != 0) {
        if (cellInfo.invenState == 0) {
          // 无货+任务中 = 入库中
          colors = colorScheme.empty.task;
        } else {
          // 有货+任务中 = 出库中
          colors = colorScheme.occupied.task;
        }
      }
      // 3. 库存状态
      else if (cellInfo.invenState == 0) {
        // 无货（无托盘或库存状态为0）
        colors = colorScheme.empty.normal;
      }
      // 4. 有托盘的情况
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
              if (that.palletBatchMode) {
                if (that._batchDragMoved) {
                  return;
                }
                that.toggleBatchCellSelection(node.data.key);
                return;
              }
              that.findCellInfo(node.data.key);
            },
            cursor: "pointer", //改变鼠标样式变成小手
          }
        )
      );


      this.load(); // load an initial diagram from some JSON text
      this.bindBatchSelectTools();
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
        url: "/wcs-base/CellInfo/getByCodeWithPallet",
        method: "get",
        params: { code: cellCode, wareCode: that.wareCode },
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

      // this.cellInfos.forEach((cell) => {
      //   if (cell.code == cellCode) {
      //     this.cellInfo = cell;
      //   }
      // });

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
      // 准备请求数据，包含托盘信息
      const requestData = {
        ...cellInfo,
        palletCode: cellInfo.palletCode || null,
        isEmpty: cellInfo.isEmpty || null,
        palletType: cellInfo.palletType || null
      };

      request({
        url: "/wcs-base/CellInfo/updateCellInfoStateWithPallet",
        method: "post",
        data: requestData,
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          // 刷新当前库位信息
          if (cellInfo.code) {
            this.findCellInfo(cellInfo.code);
          }
          // 刷新库位视图
          if (this.lineCode) {
            this.updateCellView(this.lineCode);
          }
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },

    deleteCell(cellCode) {
      if (!this.cellInfo || !this.cellInfo.id) {
        this.$modal.msgError("获取库位信息失败，请重新选择库位");
        return;
      }

      this.$modal.confirm("确定要删除该库位吗？删除后将无法恢复。", "删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        request({
          url: `/wcs-base/CellInfo/${this.cellInfo.id}`,
          method: "delete"
        }).then((response) => {
          if (response.code == 200) {
            this.$modal.msgSuccess("删除成功");
            // 清除当前选中的库位信息
            this.cellInfo = {};
            this.isCell = false;
            // 刷新库位视图
            if (this.lineCode) {
              this.getCellInfos(this.lineCode);
            }
          } else {
            this.$modal.msgError(response.msg);
          }
        });
      }).catch(() => {
        // 用户取消删除
      });
    },

    rebuildCellCodeSet() {
      const set = {};
      this.cellInfos.forEach((cell) => {
        if (cell && cell.code) {
          set[cell.code] = true;
        }
      });
      this.cellCodeSet = set;
    },

    isBatchCellSelected(code) {
      return this.selectedBatchCellCodes.indexOf(code) >= 0;
    },

    togglePalletBatchMode() {
      if (this.palletBatchMode) {
        this.exitPalletBatchMode();
      } else {
        this.palletBatchMode = true;
        if (this.palletTypes.length > 0) {
          this.batchPalletType = this.palletTypes[0].code;
        }
        this.$message.info("按住左键划过库位进行多选，再次划过已选库位可取消");
      }
    },

    exitPalletBatchMode() {
      this.clearBatchSelection();
      this.palletBatchMode = false;
      this.batchSelectDragging = false;
      this.batchDragLastCellKey = null;
      this._batchDragMoved = false;
    },

    clearBatchSelection() {
      const codes = this.selectedBatchCellCodes.slice();
      this.selectedBatchCellCodes = [];
      codes.forEach((code) => {
        this.restoreCellNodeBorder(code);
      });
    },

    toggleBatchCellSelection(code) {
      if (!this.cellCodeSet[code]) {
        return;
      }
      const idx = this.selectedBatchCellCodes.indexOf(code);
      if (idx >= 0) {
        this.selectedBatchCellCodes.splice(idx, 1);
        this.restoreCellNodeBorder(code);
      } else {
        this.selectedBatchCellCodes.push(code);
        this.highlightCellNodeBorder(code);
      }
    },

    setNodeBorderByKey(key, borderColor) {
      if (!this.myDiagram || !this.myDiagram.model) {
        return;
      }
      const node = this.myDiagram.model.findNodeDataForKey(key);
      if (node) {
        this.myDiagram.model.setDataProperty(node, "borderColor", borderColor);
      }
    },

    highlightCellNodeBorder(code) {
      this.setNodeBorderByKey(code, this.batchSelectBorderColor);
    },

    restoreCellNodeBorder(code) {
      const cell = this.cellInfos.find((c) => c.code === code);
      if (!cell) {
        return;
      }
      this.cellColor(cell);
      this.setNodeBorderByKey(code, cell.borderColor);
    },

    bindBatchSelectTools() {
      const that = this;
      const diagram = this.myDiagram;
      if (!diagram || !diagram.div) {
        return;
      }
      this.unbindBatchSelectTools();
      const div = diagram.div;
      this._batchSelectDiv = div;

      const onDown = (e) => {
        if (!that.palletBatchMode || e.button !== 0) {
          return;
        }
        that.batchSelectDragging = true;
        that.batchDragLastCellKey = null;
        that._batchDragMoved = false;
      };
      const onMove = (e) => {
        if (!that.palletBatchMode || !that.batchSelectDragging) {
          return;
        }
        that._batchDragMoved = true;
        that.handleBatchSelectPointer(e);
      };
      const onUp = () => {
        that.batchSelectDragging = false;
        that.batchDragLastCellKey = null;
        if (that._batchDragMoved) {
          setTimeout(() => {
            that._batchDragMoved = false;
          }, 50);
        }
      };

      div.addEventListener("mousedown", onDown);
      div.addEventListener("mousemove", onMove);
      window.addEventListener("mouseup", onUp);
      this._batchSelectHandlers = { div, onDown, onMove, onUp };
    },

    unbindBatchSelectTools() {
      if (!this._batchSelectHandlers) {
        return;
      }
      const { div, onDown, onMove, onUp } = this._batchSelectHandlers;
      if (div) {
        div.removeEventListener("mousedown", onDown);
        div.removeEventListener("mousemove", onMove);
      }
      window.removeEventListener("mouseup", onUp);
      this._batchSelectHandlers = null;
      this._batchSelectDiv = null;
    },

    handleBatchSelectPointer(e) {
      const diagram = this.myDiagram;
      if (!diagram || !diagram.div) {
        return;
      }
      const rect = diagram.div.getBoundingClientRect();
      const viewPt = new go.Point(e.clientX - rect.left, e.clientY - rect.top);
      const docPt = diagram.transformViewToDoc(viewPt);
      const part = diagram.findPartAt(docPt, false);
      if (!part || !(part instanceof go.Node)) {
        return;
      }
      const key = part.data.key;
      if (!this.cellCodeSet[key]) {
        return;
      }
      if (this.batchDragLastCellKey === key) {
        return;
      }
      this.batchDragLastCellKey = key;
      this.toggleBatchCellSelection(key);
    },

    submitBatchPalletType() {
      if (!this.palletBatchMode) {
        return;
      }
      if (this.selectedBatchCellCodes.length === 0) {
        this.$modal.msgError("请先选择库位");
        return;
      }
      if (!this.batchPalletType) {
        this.$modal.msgError("请选择适用托盘类型");
        return;
      }
      if (!this.wareCode) {
        this.$modal.msgError("仓库编码不能为空");
        return;
      }
      request({
        url: "/wcs-base/CellInfo/batchUpdatePalletType",
        method: "post",
        data: {
          wareCode: this.wareCode,
          cellCodes: this.selectedBatchCellCodes,
          palletType: this.batchPalletType,
        },
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess(response.msg || "批量修改成功");
          this.clearBatchSelection();
          if (this.lineCode) {
            this.updateCellView(this.lineCode);
          }
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

  .errorCell {
    background: #ce1abf;
    border: 2px solid #ce1abf;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(221, 21, 228, 0.2);

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
.containerDdj>.el-row>.el-col:nth-child(2) .el-card {
  .el-card__body>div:first-child {
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 12px;

    >div {
      color: #ffffff;
      font-weight: 500;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
    }
  }
}

// 货位状态图例样式优化
.containerDdj .el-card .el-card__body>div:first-child {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);

  >div {
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
  .kongCell,
  .noKongCell,
  .inCell,
  .outCell,
  .disableCell {
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

.batch-pallet-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  margin-bottom: 8px;
  background: rgba(64, 158, 255, 0.08);
  border: 1px solid rgba(64, 158, 255, 0.35);
  border-radius: 6px;
}

.batch-pallet-tip {
  color: #606266;
  font-size: 12px;
}

.batch-pallet-count {
  color: #409eff;
  font-weight: 600;
  font-size: 13px;
}

.batch-pallet-label {
  color: #303133;
  font-size: 13px;
}

.batch-pallet-select {
  min-width: 140px;
  height: 32px;
}

.batchSelectLegend {
  border-color: #409eff !important;
  border-width: 3px !important;
}

.diagram-batch-mode {
  cursor: crosshair;
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

.btn-delete {
  background: #dc2626;
  border: none;
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  box-shadow: 0 4px 15px rgba(220, 38, 38, 0.4);
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
    box-shadow: 0 8px 25px rgba(220, 38, 38, 0.6);
  }

  &:active {
    transform: translateY(0);
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
