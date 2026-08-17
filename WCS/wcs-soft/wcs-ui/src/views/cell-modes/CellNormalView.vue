<template>
  <div class="cell-normal-view">
    <el-row :gutter="0">
      <!-- 中间：货位展示区域 -->
      <el-col :span="20">
        <el-card class="diagram-card">
          <!-- 货位状态图例 -->
          <div class="legend-container">
            <div class="legend-item-wrapper">
              <span>无货</span>
              <div class="kongCell"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>有货</span>
              <div class="noKongCell"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>入库中</span>
              <div class="inCell"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>出库中</span>
              <div class="outCell"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>禁用</span>
              <div class="disableCell"></div>
            </div>
          </div>
          
          <div id="diagramDivNormal" class="diagram-container"></div>
        </el-card>
      </el-col>
      
      <!-- 右侧：货位信息面板 -->
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
                <label class="form-label">库位类型</label>
                <div class="form-value">{{ getCellTypeName(cellInfo.type) }}</div>
              </div>

              <!-- <div class="form-group">
                <label class="form-label">前置库位</label>
                <div class="form-value">{{ cellInfo.preCode }}</div>
              </div> -->

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
                <input :disabled="true" v-model="cellInfo.areaCode" class="form-input disabled" readonly />
              </div> -->

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

              <!-- <div class="form-actions">
                <button class="btn-danger" @click="deleteCellInfo(cellInfo.code)">
                  <i class="el-icon-delete"></i>
                  删除提交
                </button>
              </div> -->

              <div class="form-actions">
                <button class="btn-secondary" @click="preCellVisible = true">
                  <i class="el-icon-setting"></i>
                  设置前置库位
                </button>
              </div>

              <div v-if="cellInfo != null && cellInfo.taskState == 0 && cellInfo.invenState == 1" class="form-actions">
                <button class="btn-outbound" @click="openMoveDialog(cellInfo.code)">
                  <i class="el-icon-position"></i>
                  托盘搬运
                </button>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 设置前置库位对话框 -->
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

    <!-- 移动托盘对话框 -->
    <el-dialog title="移动托盘" width="500px" append-to-body :visible.sync="moveDialogVisible" @open="loadTargetCells">
      <el-form label-width="100px" :model="moveForm" ref="moveForm" :rules="moveRules">
        <el-form-item label="当前货位">
          <el-input v-model="moveForm.fromCell" disabled></el-input>
        </el-form-item>
        <el-form-item label="目的地货位" prop="toCell">
          <el-select 
            v-model="moveForm.toCell" 
            placeholder="请选择目的地货位" 
            filterable 
            clearable
            style="width: 100%"
            :loading="cellListLoading"
          >
            <el-option
              v-for="cell in targetCellList"
              :key="cell.code"
              :label="cell.code + ' (层:' + cell.z + ', X:' + cell.x + ', Y:' + cell.y + ')'"
              :value="cell.code"
              :disabled="cell.disableState == 1 || cell.taskState > 0 || cell.invenState == 1"
            >
              <span style="float: left">
                {{ cell.code }}
                <span style="color: #909399; font-size: 12px; margin-left: 5px">
                  [{{ getCellTypeName(cell.type) }}]
                </span>
              </span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                层{{ cell.z }} 
                <span v-if="cell.invenState == 1" style="color: #67c23a"> | 有货</span>
                <span v-else style="color: #909399"> | 无货</span>
                <span v-if="cell.taskState > 0" style="color: #e6a23c"> | 任务中</span>
                <span v-if="cell.disableState == 1" style="color: #f56c6c"> | 已禁用</span>
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="moveForm.memo" type="textarea" placeholder="选填：任务备注信息"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="moveDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmMove" :loading="moveLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCellInfo } from "@/api/wcs-base/CellInfo";
import request from "@/utils/request";

export default {
  name: "CellNormalView",
  props: {
    wareCode: {
      type: String,
      required: true
    },
    floor: {
      type: Number,
      default: null // 允许初始为null
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
      floors: [],  // 自己维护楼层列表
      lineModelData: {
        class: "GraphLinksModel",
        nodeDataArray: []
      },
      minNode: {
        category: "cell", 
        x: 999009999, 
        y: 999009999, 
        borderColor: "transparent",
        textColor: "transparent",
        fillColor: "transparent"
      },
      maxNode: {
        category: "cell", 
        x: -100000000, 
        y: -100000000, 
        borderColor: "transparent",
        textColor: "transparent",
        fillColor: "transparent"
      },
      refreshTimer: null,
      
      // 货位信息面板相关
      cellInfo: {},
      cellLoading: false,
      
      // 状态选项
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
      
      // 前置库位设置
      preCellVisible: false,
      preCellForm: {},
      
      // 移动托盘对话框
      moveDialogVisible: false,
      moveLoading: false,
      moveForm: {
        fromCell: '',
        toCell: '',
        memo: ''
      },
      moveRules: {
        toCell: [
          { required: true, message: '请选择目的地货位', trigger: 'change' }
        ]
      },
      targetCellList: [],  // 目标库位列表
      cellListLoading: false,  // 库位列表加载状态
      
      // 楼层加载标志
      isLoadingFloor: false,
    };
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
    floor(newVal) {
      if (newVal && this.myDiagram) {
        this.getCellInfos(newVal);
      }
    }
  },
  mounted() {
    this.initDiagram();
    
    // 如果已经有wareCode和floor，立即加载
    if (this.wareCode && this.floor) {
      this.getFloors(this.wareCode).then(() => {
        this.getCellInfos(this.floor);
      });
    }
    
    // 启动定时刷新
    this.startAutoRefresh();
  },
  beforeDestroy() {
    this.stopAutoRefresh();
    if (this.myDiagram) {
      this.myDiagram.div = null;
      this.myDiagram = null;
    }
  },
  methods: {
    // 获取库位类型名称
    getCellTypeName(type) {
      const cellType = type !== undefined && type !== null ? type : 0;
      const typeNames = {
        0: '普通库位',
        1: '入库接驳位',
        2: '出库接驳位',
        3: '通用接驳位',
        4: '四向车通道',
        5: '提升机位置',
        6: '充电桩位置'
      };
      return typeNames[cellType] || '未知类型';
    },
    
    initDiagram(mode) {
      // 防止重复初始化
      if (this.myDiagram) {
        console.warn("Diagram已存在，跳过初始化");
        return;
      }
      
      var that = this;
      var $ = go.GraphObject.make; // 定义模板时的简洁性
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.height);
      
      this.myDiagram = $(
        go.Diagram,
        "diagramDivNormal", //正常模式专用的DIV ID
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
          isReadOnly: true, // 根据模式设置是否只读
        }
      );

      var myDiagram = this.myDiagram;
      
       

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
          // 当按钮被点击时的处理函数
          {
            click: function (e, node) {
              // 查找货位信息并显示在右侧面板
              that.findCellInfo(node.data.key);
            },
            cursor: "pointer", //改变鼠标样式变成小手
          }
        ),
        
      );
 
      
       
 
 
      this.load(); // load an initial diagram from some JSON text
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
      
      console.log("🔄 库位模式 - 切换到楼层:", floor, "- 清空所有数据");
      
      // 设置标志位
      this.isLoadingFloor = true;
      
      // 清空所有数据
      this.cellInfos = [];
      this.cellInfo = {}; // 清空右侧面板
      
      // 重置边界节点
      this.minNode = {
        category: "cell", 
        x: 999009999, 
        y: 999009999, 
        borderColor: "transparent",
        textColor: "transparent",
        fillColor: "transparent"
      };
      this.maxNode = {
        category: "cell", 
        x: -100000000, 
        y: -100000000, 
        borderColor: "transparent",
        textColor: "transparent",
        fillColor: "transparent"
      };
      
      if (!floor) {
        console.warn("楼层参数为空，无法加载货位");
        this.isLoadingFloor = false;
        return;
      }

      // 检查是否有楼层信息
      if (this.floors.length === 0) {
        console.warn("楼层列表为空，等待加载...");
        // 重新获取楼层信息
        this.getFloors(this.wareCode).then(() => {
          this.getCellInfos(floor);
        });
        this.isLoadingFloor = false;
        return;
      }

      that.lineModelData = {
        class: "GraphLinksModel",
        nodeDataArray: []
      };
      
      // 清空 diagram 模型
      if (that.myDiagram) {
        var oldNodeCount = that.myDiagram.model.nodeDataArray ? that.myDiagram.model.nodeDataArray.length : 0;
        
        var newModel = new go.GraphLinksModel();
        that.myDiagram.model = newModel;
        
        console.log("✓ 库位模式 Diagram 模型已完全替换 - 清除了", oldNodeCount, "个节点");
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
          console.log("✓ 货位数据加载成功:", this.cellInfos.length, "个货位");

          var floorInfo = that.getFloorInfo(floor);
          
          if (!floorInfo) {
            console.error("找不到楼层信息:", floor);
            return;
          }
          
          var xy = floorInfo.xy;
          var totalX = floorInfo.totalX;
          var totalY = floorInfo.totalY;

          this.cellInfos.forEach((cell) => {
            var dispalyX = 0;
            var dispalyY = 0;
            switch (xy) {
              case 1:
                dispalyX = cell.x;
                dispalyY = cell.y;
                break;
              case 2:
                dispalyX = totalX - cell.x + 1;
                dispalyY = cell.y;
                break;
              case 3:
                dispalyX = cell.x;
                dispalyY = totalY - cell.y + 1;
                break;
              case 4:
                dispalyX = totalX - cell.x + 1;
                dispalyY = totalY - cell.y + 1;
                break;
            }

            cell.dispalyX = dispalyX;
            cell.dispalyY = dispalyY;
            this.cellColor(cell);
            this.addNode(cell);
          });

          // 添加坐标轴
          switch (xy) {
            case 1:
              addXAxis(0);
              addYAxis(0);
              break;
            case 2:
              addXAxis(totalY + 1, true);
              addYAxis(0);
              break;
            case 3:
              addXAxis(0);
              addYAxis(totalX + 1, true);
              break;
            case 4:
              addXAxis(totalY + 1, true);
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
          
          // 数据加载完成，重置标志位
          that.$nextTick(() => {
            that.isLoadingFloor = false;
            console.log("✓ 库位模式 - 楼层切换完成");
          });
        }
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
            console.log("✓ 楼层列表加载成功:", this.floors.length, "个楼层");
            resolve(this.floors);
          } else {
            console.error("获取楼层列表失败:", response.msg);
            reject(response.msg);
          }
        }).catch((error) => {
          console.error("获取楼层列表出错:", error);
          reject(error);
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
          normal: {
            fill: "#2d3748",
            text: "#a0aec0"
          },
          task: {
            fill: "#d69e2e",
            text: "#ffffff"
          }
        },
        occupied: {
          normal: {
            fill: "#38a169",
            text: "#ffffff"
          },
          task: {
            fill: "#e53e3e",
            text: "#ffffff"
          }
        },
        disabled: {
          fill: "#ffffff",
          text: "#2d3748"
        }
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
      var nodeCategory = cellInfo.category || "cell";

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

      // 更新最大最小节点用于自动缩放
      if (that.maxNode.y < y) {
        that.maxNode.y = y;
        that.maxNode.loc = "" + that.maxNode.x + " " + that.maxNode.y;
        that.maxNode.size = size;
      }
      if (that.maxNode.x < x) {
        that.maxNode.x = x;
        that.maxNode.loc = "" + that.maxNode.x + " " + that.maxNode.y;
        that.maxNode.size = size;
      }
      if (that.minNode.y > y) {
        that.minNode.y = y;
        that.minNode.loc = "" + that.minNode.x + " " + that.minNode.y;
        that.minNode.size = size;
      }
      if (that.minNode.x > x) {
        that.minNode.x = x;
        that.minNode.loc = "" + that.minNode.x + " " + that.minNode.y;
        that.minNode.size = size;
      }
    },

    loadData(data) {
      var that = this;
      if (data == {}) {
        var modelData = go.Model.fromJson(data);
        that.myDiagram.model = modelData;
        return;
      }
      
      if (data.nodeDataArray != undefined) {
        that.myDiagram.model.addNodeDataCollection([that.minNode, that.maxNode]);
        that.myDiagram.initialAutoScale = go.Diagram.Uniform;
        that.loadDataInChunks(data.nodeDataArray, 20);
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
          
          console.log("✓ 图表已居中显示，边界:", bounds);
        }
      } catch (error) {
        console.warn("居中显示失败:", error);
        // 降级处理：使用简单的zoomToFit
        this.myDiagram.zoomToFit();
      }
    },

    // 更新单个货位状态（用于定时刷新）
    updateCellNode(cell) {
      if (!this.myDiagram || !this.myDiagram.model) {
        return;
      }

      var nodes = this.myDiagram.model.nodeDataArray;
      var model = this.myDiagram.model;
      
      nodes.forEach((n) => {
        if (n.key == cell.code) {
          this.cellColor(cell);
          model.setDataProperty(n, "borderColor", cell.borderColor);
          model.setDataProperty(n, "textColor", cell.textColor);
          model.setDataProperty(n, "fillColor", cell.fillColor);
        }
      });
    },

    // 启动自动刷新
    startAutoRefresh() {
      this.refreshTimer = setInterval(() => {
        if (this.floor) {
          this.refreshCellStatus();
        }
      }, 3000);
    },

    // 停止自动刷新
    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer);
        this.refreshTimer = null;
      }
    },

    // 刷新货位状态
    refreshCellStatus() {
      var that = this;
      listCellInfo({ 
        z: this.floor, 
        pageSize: 999, 
        wareCode: this.wareCode 
      }).then((response) => {
        if (response.code == 200) {
          var cellInfos = response.rows;
          cellInfos.forEach((cell) => {
            if (cell.z == this.floor) {
              this.updateCellNode(cell);
            }
          });
        }
      });
    },
    
    // ========== 货位信息面板相关方法 ==========
    
    // 查找货位信息
    findCellInfo(cellCode) {
      this.cellLoading = true;
      request({
        url: "/wcs-base/CellInfo/getByCode",
        method: "get",
        params: { code: cellCode, wareCode: this.wareCode },
      }).then((response) => {
        this.cellLoading = false;
        if (response.code == 200) {
          this.cellInfo = response.data;
        } else {
          this.$modal.msgError(response.msg);
        }
      }).catch(() => {
        this.cellLoading = false;
      });
    },
    
    // 删除货位
    deleteCellInfo(cellCode) {
      this.$confirm('确定要删除该货位吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: "/wcs-base/CellInfo/deleteByCode",
          method: "get",
          params: { code: cellCode, wareCode: this.wareCode },
        }).then((response) => {
          if (response.code == 200) {
            this.$modal.msgSuccess("删除成功");
            this.cellInfo = {};
            this.getCellInfos(this.floor);
          } else {
            this.$modal.msgError(response.msg);
          }
        });
      }).catch(() => {});
    },
    
    // 更新货位状态
    updateCellInfoState(cellInfo) {
      request({
        url: "/wcs-base/palletInfo/updateCellInfoState",
        method: "post",
        data: cellInfo,
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.findCellInfo(cellInfo.code);
          // 刷新图表中的货位颜色
          this.updateCellNode(cellInfo);
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    
    // 设置前置库位
    setPreCell() {
      request({
        url: "/wcs-base/CellInfo/setPreCell",
        method: "get",
        params: {
          wareCode: this.wareCode,
          fromX: this.preCellForm.fromX,
          toX: this.preCellForm.toX,
          fromY: this.preCellForm.fromY,
          toY: this.preCellForm.toY,
          fx: this.preCellForm.fx,
          z: this.floor
        },
      }).then((response) => {
        this.preCellVisible = false;
        if (response.code == 200) {
          this.$modal.msgSuccess("设置前置库位成功");
          this.getCellInfos(this.floor);
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    
    // 打开移动对话框
    openMoveDialog(cellCode) {
      this.moveForm.fromCell = cellCode;
      this.moveForm.toCell = '';
      this.moveForm.memo = '';
      this.moveDialogVisible = true;
    },
    
    // 加载目标库位列表
    loadTargetCells() {
      var that = this;
      that.cellListLoading = true;
      that.targetCellList = [];
      
      listCellInfo({
        wareCode: that.wareCode,
        isDelete: 0,
        pageSize: 9999,
      }).then((response) => {
        that.cellListLoading = false;
        if (response.code == 200) {
          // 过滤条件：
          // 1. 不是当前货位
          // 2. 排除 type==4（四向车通道）和 type==5（提升机位置）
          that.targetCellList = response.rows
            .filter(cell => {
              const cellType = cell.type !== undefined && cell.type !== null ? cell.type : 0;
              return cell.code !== that.moveForm.fromCell && cellType !== 4 && cellType !== 5;
            })
            .sort((a, b) => {
              // 先按楼层排序
              if (a.z !== b.z) {
                return a.z - b.z;
              }
              // 同楼层按编码排序
              return a.code.localeCompare(b.code);
            });
          console.log("✓ 目标库位列表加载成功:", that.targetCellList.length, "个可用库位");
        } else {
          that.$modal.msgError(response.msg);
        }
      }).catch(() => {
        that.cellListLoading = false;
      });
    },
    
    // 确认移动
    confirmMove() {
      var that = this;
      this.$refs.moveForm.validate((valid) => {
        if (valid) {
          that.moveLoading = true;
          request({
            url: "/wcs-task/TaskInfo",
            method: "post",
            data: {
              wareCode: that.wareCode,
              fromCellCode: that.moveForm.fromCell,
              toCellCode: that.moveForm.toCell,
              memo: that.moveForm.memo,
              type: "move1", // 托盘搬运任务
            },
          }).then((response) => {
            that.moveLoading = false;
            if (response.code == 200) {
              that.$modal.msgSuccess("托盘搬运任务创建成功");
              that.moveDialogVisible = false;
              // 刷新货位状态
              that.getCellInfos(that.floor);
              // 刷新右侧货位信息
              that.findCellInfo(that.moveForm.fromCell);
            } else {
              that.$modal.msgError(response.msg);
            }
          }).catch(() => {
            that.moveLoading = false;
          });
        }
      });
    }
  }
};
</script>

<style scoped>
@import '../CellSxcView-styles.css';

.cell-normal-view {
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
</style>

