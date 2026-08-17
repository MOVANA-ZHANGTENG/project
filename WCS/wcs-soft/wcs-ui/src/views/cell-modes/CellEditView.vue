<template>
  <div class="cell-edit-view">
    <el-row :gutter="0">
      <!-- 中间：货位展示区域 -->
      <el-col :span="20">
        <el-card class="diagram-card">
          <!-- 库位类型图例 -->
          <div class="legend-container">
            <div class="legend-item-wrapper">
              <span>普通库位</span>
              <div class="legend-box" style="border-color: #4a5568;"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>入库接驳位</span>
              <div class="legend-box" style="border-color: #48bb78;"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>出库接驳位</span>
              <div class="legend-box" style="border-color: #4299e1;"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>通用接驳位</span>
              <div class="legend-box" style="border-color: #ed8936;"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>四向车通道</span>
              <div class="legend-box" style="border-color: #556270;"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>提升机位置</span>
              <div class="legend-box" style="border-color: #f56565;"></div>
            </div>
            <div class="legend-item-wrapper">
              <span>充电桩位置</span>
              <div class="legend-box" style="border-color: #9333ea;"></div>
            </div>
          </div>

          <div id="diagramDivEdit" class="diagram-container"></div>
        </el-card>
      </el-col>

      <!-- 右侧：配置面板 -->
      <el-col :span="4">
        <!-- 库位配置面板 -->
        <div class="info-panel" v-if="selectedCell">
          <div class="info-header">
            <i class="el-icon-edit-outline"></i>
            <span>库位配置</span>
          </div>
          <div class="info-content">
            <div class="tool-container">
              <div class="tool-section">
                <div class="section-title">
                  <i class="el-icon-location"></i>
                  <span>基本信息</span>
                </div>
                <div class="section-content">
                  <div class="form-group">
                    <label>库位编码</label>
                    <div class="form-value">{{ cellEditForm.code }}</div>
                  </div>
                </div>
              </div>

              <div class="tool-section">
                <div class="section-title">
                  <i class="el-icon-setting"></i>
                  <span>配置参数</span>
                </div>
                <div class="section-content">
                  <el-form :model="cellEditForm" label-position="top" size="small">
                    <el-form-item label="库位类型">
                      <el-select
                        v-model="cellEditForm.type"
                        placeholder="请选择库位类型"
                        style="width: 100%"
                      >
                        <el-option label="普通库位" :value="0"></el-option>
                        <el-option label="入库接驳位" :value="1"></el-option>
                        <el-option label="出库接驳位" :value="2"></el-option>
                        <el-option label="通用接驳位" :value="3"></el-option>
                        <el-option label="四向车通道" :value="4"></el-option>
                        <el-option label="提升机位置" :value="5"></el-option>
                        <el-option label="充电桩位置" :value="6"></el-option>
                      </el-select>
                    </el-form-item>

                    <el-form-item label="前置库位">
                      <el-input
                        v-model="cellEditForm.preCode"
                        placeholder="请输入前置库位编码"
                        clearable
                      ></el-input>
                    </el-form-item>

                    <el-form-item label="Sub X">
                      <el-input-number
                        v-model="cellEditForm.subX"
                        :min="0"
                        :max="99999"
                        controls-position="right"
                        style="width: 100%"
                      ></el-input-number>
                    </el-form-item>

                    <el-form-item label="Sub Y">
                      <el-input-number
                        v-model="cellEditForm.subY"
                        :min="0"
                        :max="99999"
                        controls-position="right"
                        style="width: 100%"
                      ></el-input-number>
                    </el-form-item>

                    <el-form-item label="Sub Z">
                      <el-input-number
                        v-model="cellEditForm.subZ"
                        :min="0"
                        :max="999"
                        controls-position="right"
                        style="width: 100%"
                      ></el-input-number>
                    </el-form-item>
                  </el-form>
                </div>
              </div>

              <div class="tool-actions">
                <el-button
                  size="small"
                  type="primary"
                  @click="saveCellConfig"
                  icon="el-icon-check"
                  :loading="savingCell"
                  style="width: 100%"
                >
                  保存
                </el-button>
                <el-button
                  size="small"
                  @click="cancelCellEdit"
                  icon="el-icon-close"
                  style="width: 100%"
                >
                  取消
                </el-button>

                <el-button
                  size="small"
                  type="success"
                  @click="openBatchPreCellDialog"
                  icon="el-icon-setting"
                  style="width: 100%"
                >
                  批量配置前置库位
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteCell"
                  icon="el-icon-delete"
                  style="width: 100%"
                >
                  删除库位
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 批量配置面板 -->
        <!-- <div class="info-panel" :class="{'panel-collapsed': selectedCell}">
          <div class="info-header">
            <i class="el-icon-s-tools"></i>
            <span>批量配置工具</span>
          </div>
          <div class="info-content">
            <div class="tool-container">
              <div class="tool-section">
                <div class="section-title">
                  <i class="el-icon-info"></i>
                  <span>使用说明</span>
                </div>
                <div class="section-content">
                  <p>批量配置前置库位</p>
                  <p>• 指定库位范围（X、Y坐标）</p>
                  <p>• 选择前置方向</p>
                  <p>• 自动计算并设置</p>
                </div>
              </div>

              <div class="tool-section">
                <div class="section-title">
                  <i class="el-icon-data-analysis"></i>
                  <span>统计信息</span>
                </div>
                <div class="section-content">
                  <p>连接线总数: <strong>{{ linkCount }}</strong></p>
                  <p>库位总数: <strong>{{ cellCount }}</strong></p>
                </div>
              </div>

              <div class="tool-actions">
                <el-button
                  size="small"
                  type="success"
                  @click="openBatchPreCellDialog"
                  icon="el-icon-setting"
                  style="width: 100%"
                >
                  批量配置前置库位
                </el-button>
                <el-button
                  size="small"
                  type="warning"
                  @click="clearAllLinks"
                  icon="el-icon-delete"
                  style="width: 100%"
                >
                  清空所有连接线
                </el-button>
              </div>
            </div>
          </div>
        </div> -->
      </el-col>
    </el-row>

    <!-- 批量设置前置库位对话框 -->
    <el-dialog title="批量设置前置库位" width="500px" append-to-body :visible.sync="preCellVisible">
      <el-form label-width="80px" :model="preCellForm">
        <el-form-item label="起始X">
          <el-input v-model="preCellForm.fromX" placeholder="请输入起始X坐标"></el-input>
        </el-form-item>
        <el-form-item label="结束X">
          <el-input v-model="preCellForm.toX" placeholder="请输入结束X坐标"></el-input>
        </el-form-item>
        <el-form-item label="起始Y">
          <el-input v-model="preCellForm.fromY" placeholder="请输入起始Y坐标"></el-input>
        </el-form-item>
        <el-form-item label="结束Y">
          <el-input v-model="preCellForm.toY" placeholder="请输入结束Y坐标"></el-input>
        </el-form-item>
        <el-form-item label="前置方向">
          <el-select v-model="preCellForm.fx" placeholder="请选择前置方向" style="width: 100%">
            <el-option label="-X (向左)" value="-X"></el-option>
            <el-option label="+X (向右)" value="+X"></el-option>
            <el-option label="-Y (向下)" value="-Y"></el-option>
            <el-option label="+Y (向上)" value="+Y"></el-option>
          </el-select>
        </el-form-item>
        <el-alert
          title="使用说明"
          type="info"
          :closable="false"
          show-icon
          style="margin-top: 10px">
          <div slot="default">
            <p>1. 输入要配置的库位范围（X和Y坐标）</p>
            <p>2. 选择前置方向，系统将自动计算前置库位</p>
            <p>3. 例如：选择+X方向，前置库位为当前库位X+1</p>
          </div>
        </el-alert>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="preCellVisible = false">取 消</el-button>
        <el-button type="primary" @click="setPreCell()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCellInfo, getCellInfo, updateCellConfig, delCellInfo } from "@/api/wcs-base/CellInfo";
import { listCellLink, addCellLink, deleteByFromCellIdAndToCellIdAndWareCode } from "@/api/wcs-base/CellLink";
import request from "@/utils/request";

export default {
  name: "CellEditView",
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
      previousLinkDataArray: [],
      selectedLink: null,
      selectedCell: null,
      cellEditForm: {
        id: null,
        code: '',
        type: 0,
        preCode: '',
        subX: 0,
        subY: 0,
        subZ: 0
      },
      savingCell: false,
      preCellVisible: false,
      preCellForm: {
        fromX: '',
        toX: '',
        fromY: '',
        toY: '',
        fx: ''
      },
      isLoadingFloor: false, // 标志位：是否正在切换楼层
    };
  },
  computed: {
    linkCount() {
      return this.lineModelData.linkDataArray ? this.lineModelData.linkDataArray.length : 0;
    },
    cellCount() {
      return this.cellInfos.length;
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
    floor(newVal) {
      if (newVal && this.myDiagram) {
        this.getCellInfos(newVal);
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
  },
  beforeDestroy() {
    if (this.myDiagram) {
      this.myDiagram.div = null;
      this.myDiagram = null;
    }
  },
  methods: {
    initDiagram() {
      // 防止重复初始化
      if (this.myDiagram) {
        console.warn("Diagram已存在，跳过初始化");
        return;
      }

      var that = this;
      var $ = go.GraphObject.make;
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.height);

      this.myDiagram = $(
        go.Diagram,
        "diagramDivEdit",
        {
          initialAutoScale: go.Diagram.Uniform,
          "draggingTool.isGridSnapEnabled": true,
          "draggingTool.gridSnapCellSpot": go.Spot.TopLeft,
          "resizingTool.isGridSnapEnabled": true,
          // 鼠标滚轮缩放配置
          "commandHandler.zoomFactor": 1.1, // 缩放因子，每次滚轮滚动的缩放比例
          "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom, // 鼠标滚轮行为：缩放
          LinkDrawn: showLinkLabel,
          LinkRelinked: showLinkLabel,
          "undoManager.isEnabled": true,
          isReadOnly: false, // 编辑模式可编辑
        }
      );

      var myDiagram = this.myDiagram;

      // 添加变化监听器
      this.myDiagram.addChangedListener(function (event) {
        // 如果正在切换楼层，跳过监听器处理
        if (that.isLoadingFloor) {
          return;
        }

        var modelData = myDiagram.model.toJson();

        // 检测link的增加和减少
        var currentLinkDataArray = myDiagram.model.linkDataArray || [];

        if (currentLinkDataArray.length !== that.previousLinkDataArray.length) {
          if (currentLinkDataArray.length > that.previousLinkDataArray.length) {
            // 连接线增加
            var addedLinks = currentLinkDataArray.filter(function(currentLink) {
              return !that.previousLinkDataArray.some(function(prevLink) {
                return prevLink.from === currentLink.from && prevLink.to === currentLink.to;
              });
            });

            addedLinks.forEach(function(link) {
              that.handleLinkAddedInternal(link);
            });

          } else if (currentLinkDataArray.length < that.previousLinkDataArray.length) {
            // 连接线减少
            var removedLinks = that.previousLinkDataArray.filter(function(prevLink) {
              return !currentLinkDataArray.some(function(currentLink) {
                return currentLink.from === prevLink.from && currentLink.to === prevLink.to;
              });
            });

            removedLinks.forEach(function(link) {
              that.handleLinkRemovedInternal(link);
            });
          }

          that.previousLinkDataArray = JSON.parse(JSON.stringify(currentLinkDataArray));
        }
      });

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

      function makePort(name, align, spot, output, input) {
        var horizontal = align.equals(go.Spot.Top) || align.equals(go.Spot.Bottom);
        return $(go.Shape, {
          fill: "transparent",
          strokeWidth: 0,
          width: horizontal ? NaN : 8,
          height: !horizontal ? NaN : 8,
          alignment: align,
          stretch: horizontal ? go.GraphObject.Horizontal : go.GraphObject.Vertical,
          portId: name,
          fromSpot: spot,
          fromLinkable: output,
          toSpot: spot,
          toLinkable: input,
          cursor: "pointer",
          mouseEnter: function (e, port) {
            if (!e.diagram.isReadOnly) port.fill = "rgba(255,0,255,0.5)";
          },
          mouseLeave: function (e, port) {
            port.fill = "transparent";
          },
        });
      }

      function showLinkLabel(e) {
        var label = e.subject.findObject("LABEL");
        if (label !== null)
          label.visible = e.subject.fromNode.data.category === "Conditional";
      }

      // 库位节点模板（支持连接）
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
              "Rectangle",
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
            makePort("T", go.Spot.Top, go.Spot.Top, true, true),
            makePort("L", go.Spot.Left, go.Spot.Left, true, true),
            makePort("R", go.Spot.Right, go.Spot.Right, true, true),
            makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, true)
          ),
          {
            click: function (e, node) {
              console.log("点击库位:", node.data.key);
              that.handleCellClick(node.data.key);
            },
            cursor: "pointer",
          }
        ),
      );

      // 连接线模板
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
            stroke: "#4facfe",
            name: "LINKSHAPE"
          },
          new go.Binding("stroke", "color"),
          new go.Binding("strokeWidth", "thickness", (t) => t || 3),
          new go.Binding("strokeDashArray", "dashArray")
        ),
        $(
          go.Shape,
          { toArrow: "Standard", stroke: "#4facfe", fill: "#4facfe" },
          new go.Binding("stroke", "color"),
          new go.Binding("fill", "color")
        ),
        {
          selectionChanged: function(link) {
            if (link.isSelected) {
              that.selectedLink = link.data;
            } else {
              that.selectedLink = null;
            }
          }
        }
      );

      // 临时链接也是正交的
      this.myDiagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
      this.myDiagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;

      // 启用链接工具
      this.myDiagram.toolManager.linkingTool.isEnabled = true;
      this.myDiagram.toolManager.relinkingTool.isEnabled = true;

      this.load();
      console.log("✓ 编辑模式初始化完成");
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

      console.log("🔄 切换到楼层:", floor, "- 清空所有数据");

      // 设置标志位，暂停监听器
      this.isLoadingFloor = true;

      // 清空所有数据，避免楼层间数据干扰
      this.cellInfos = [];
      this.previousLinkDataArray = [];
      this.selectedCell = null;
      this.cellEditForm = {
        id: null,
        code: '',
        type: 0,
        preCode: '',
        subX: 0,
        subY: 0,
        subZ: 0
      };

      if (!floor) {
        console.warn("楼层参数为空，无法加载货位");
        this.isLoadingFloor = false;
        return;
      }

      if (this.floors.length === 0) {
        console.warn("楼层列表为空，等待加载...");
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

      // 清空 diagram 模型（重要！否则旧楼层的节点和连接线会残留）
      // 使用替换模型的方式，比 clear() 更彻底
      if (that.myDiagram) {
        var oldNodeCount = that.myDiagram.model.nodeDataArray ? that.myDiagram.model.nodeDataArray.length : 0;
        var oldLinkCount = that.myDiagram.model.linkDataArray ? that.myDiagram.model.linkDataArray.length : 0;

        var newModel = new go.GraphLinksModel();
        newModel.linkFromPortIdProperty = "fromPort";
        newModel.linkToPortIdProperty = "toPort";
        that.myDiagram.model = newModel;

        console.log("✓ Diagram 模型已完全替换 - 清除了", oldNodeCount, "个节点和", oldLinkCount, "条连接线");
      }

      listCellInfo({
        wareCode: that.wareCode,
        z: floor,
        isDelete: 0,
        pageSize: 999,
      }).then((response) => {
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

          // 编辑模式：添加间隔（坐标*2）
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

          // 添加坐标轴（也需要*2）
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

          // 加载连接线
          this.addConnectionLines();
        }
      });
    },

    // 加载连接线
    addConnectionLines() {
      var that = this;

      const cellIdToCodeMap = {};
      this.cellInfos.forEach((cell) => {
        if (cell.id) {
          cellIdToCodeMap[cell.id] = cell.code;
        }
      });

      listCellLink({
        wareCode: that.wareCode,
        pageSize: 9999,
      }).then((response) => {
        if (response.code == 200) {
          const cellLinks = response.rows;
          console.log("📊 查询到连接线总数:", cellLinks.length);

          cellLinks.forEach((link) => {
            const fromCellCode = cellIdToCodeMap[link.fromCellId];
            const toCellCode = cellIdToCodeMap[link.toCellId];

            // 只添加属于当前楼层的连接线（通过cellIdToCodeMap自动过滤）
            if (fromCellCode && toCellCode) {
              const lineColor = link.isBlocked === 1 ? "#e53e3e" : "#4facfe";

              that.lineModelData.linkDataArray.push({
                from: fromCellCode,
                to: toCellCode,
                color: lineColor,
                fromPort: "",
                toPort: ""
              });
            }
          });

          that.loadData(that.lineModelData);

          that.$nextTick(() => {
            if (that.myDiagram && that.myDiagram.model) {
              that.previousLinkDataArray = JSON.parse(
                JSON.stringify(that.myDiagram.model.linkDataArray || [])
              );
              console.log("✓ 当前楼层连接线:", that.previousLinkDataArray.length, "条");
            }

            // 数据加载完成，重置标志位，恢复监听器
            that.isLoadingFloor = false;
            console.log("✓ 楼层切换完成，监听器已恢复");
          });

          console.log("✓ 连接线加载完成:", that.lineModelData.linkDataArray.length, "条");
        } else {
          console.error("查询库位连接关系失败:", response.msg);
          that.loadData(that.lineModelData);
          // 即使失败也要重置标志位
          that.isLoadingFloor = false;
        }
      }).catch((error) => {
        console.error("查询库位连接关系出错:", error);
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
            console.log("✓ 楼层列表加载成功:", this.floors.length, "个楼层");
            resolve(this.floors);
          } else {
            console.error("获取楼层列表失败:", response.msg);
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
        5: "#f56565",  // 提升机位置 - 红色
        6: "#9333ea"   // 充电桩位置 - 紫色
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

      // 调试：打印前5个库位的类型信息
      if (cellInfo.code && !cellInfo.code.startsWith('X') && !cellInfo.code.startsWith('Y')) {
        const cellIndex = this.cellInfos.indexOf(cellInfo);
        if (cellIndex < 5) {
          console.log(`📍 库位 ${cellInfo.code}: type=${cellInfo.type} (原始值), 使用的type=${cellType}, borderColor=${cellInfo.borderColor}`);
        }
      }
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

      // 如果有节点数据，加载节点
      if (data.nodeDataArray != undefined && data.nodeDataArray.length > 0) {
        that.myDiagram.initialAutoScale = go.Diagram.Uniform;
        that.myDiagram.zoomToFit();
        that.loadDataInChunks(data.nodeDataArray, 20);
      }

      // 如果有连接线数据，加载连接线
      if (data.linkDataArray != undefined && data.linkDataArray.length > 0) {
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

          console.log("✓ 编辑模式图表已居中显示，边界:", bounds);
        }
      } catch (error) {
        console.warn("编辑模式居中显示失败:", error);
        // 降级处理：使用简单的zoomToFit
        this.myDiagram.zoomToFit();
      }
    },

    // 处理连接线添加
    handleLinkAddedInternal(linkData) {
      var that = this;

      const fromCell = this.cellInfos.find(cell => cell.code === linkData.from);
      const toCell = this.cellInfos.find(cell => cell.code === linkData.to);

      if (!fromCell || !toCell) {
        console.error("无法找到对应的库位信息");
        return;
      }

      const cellLinkData = {
        fromCellId: fromCell.id,
        toCellId: toCell.id,
        distance: 1.0,
        isBlocked: 0,
        wareCode: that.wareCode,
        createTime: new Date().toISOString()
      };

      addCellLink(cellLinkData).then((response) => {
        if (response.code == 200) {
          console.log(`✓ 连接线已保存: ${linkData.from} → ${linkData.to}`);
        } else {
          that.$modal.msgError("添加连接线失败：" + response.msg);
        }
      }).catch((error) => {
        console.error("添加连接线出错：", error);
        that.$modal.msgError("添加连接线失败");
      });
    },

    // 处理连接线删除
    handleLinkRemovedInternal(linkData) {
      var that = this;

      const fromCell = this.cellInfos.find(cell => cell.code === linkData.from);
      const toCell = this.cellInfos.find(cell => cell.code === linkData.to);

      if (!fromCell || !toCell) {
        console.error("无法找到对应的库位信息");
        return;
      }

      deleteByFromCellIdAndToCellIdAndWareCode({
        fromCellId: fromCell.id,
        toCellId: toCell.id,
        wareCode: that.wareCode
      }).then((response) => {
        if (response.code == 200) {
          console.log(`✓ 连接线已删除: ${linkData.from} → ${linkData.to}`);
        } else {
          that.$modal.msgError("删除连接线失败：" + response.msg);
        }
      }).catch((error) => {
        console.error("删除连接线出错：", error);
        that.$modal.msgError("删除连接线失败");
      });
    },

    // 清空所有连接线
    clearAllLinks() {
      this.$confirm('确定要清空所有连接线吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.myDiagram && this.myDiagram.model) {
          const links = [...this.myDiagram.model.linkDataArray];
          links.forEach(link => {
            this.handleLinkRemovedInternal(link);
          });
          this.myDiagram.model.linkDataArray = [];
          this.previousLinkDataArray = [];
          this.$message.success('已清空所有连接线');
        }
      }).catch(() => {});
    },

    // 处理库位点击
    handleCellClick(cellCode) {
      var that = this;
      const cell = this.cellInfos.find(c => c.code === cellCode);

      if (!cell || !cell.id) {
        console.warn("未找到库位信息或库位ID为空");
        return;
      }

      // 加载完整的库位信息
      getCellInfo(cell.id).then((response) => {
        if (response.code == 200) {
          that.selectedCell = response.data;
          that.cellEditForm = {
            id: response.data.id,
            code: response.data.code,
            type: response.data.type !== undefined ? response.data.type : 0,
            preCode: response.data.preCode || '',
            subX: response.data.subX || 0,
            subY: response.data.subY || 0,
            subZ: response.data.subZ || 0
          };
          console.log("✓ 库位信息加载成功:", that.selectedCell.code, "类型:", that.cellEditForm.type);
        } else {
          that.$modal.msgError(response.msg);
        }
      }).catch((error) => {
        console.error("加载库位信息失败:", error);
        that.$modal.msgError("加载库位信息失败");
      });
    },

    // 保存库位配置
    saveCellConfig() {
      var that = this;

      if (!this.selectedCell || !this.cellEditForm.id) {
        this.$modal.msgError("请先选择库位");
        return;
      }

      this.savingCell = true;

      // 使用专门的编辑配置接口
      const updateData = {
        id: this.cellEditForm.id,
        code: this.cellEditForm.code,
        type: this.cellEditForm.type,
        preCode: this.cellEditForm.preCode,
        subX: this.cellEditForm.subX,
        subY: this.cellEditForm.subY,
        subZ: this.cellEditForm.subZ,
        wareCode: this.wareCode
      };

      console.log("📤 发送库位配置更新请求:", updateData);

      updateCellConfig(updateData).then((response) => {
        that.savingCell = false;
        if (response.code == 200) {
          that.$modal.msgSuccess("库位配置保存成功");
          console.log("✓ 库位配置更新成功");
          // 更新本地数据
          const cellIndex = that.cellInfos.findIndex(c => c.id === that.cellEditForm.id);
          if (cellIndex !== -1) {
            that.cellInfos[cellIndex].type = that.cellEditForm.type;
            that.cellInfos[cellIndex].preCode = that.cellEditForm.preCode;
            that.cellInfos[cellIndex].subX = that.cellEditForm.subX;
            that.cellInfos[cellIndex].subY = that.cellEditForm.subY;
            that.cellInfos[cellIndex].subZ = that.cellEditForm.subZ;
            // 更新颜色
            that.cellColor(that.cellInfos[cellIndex]);
            // 更新节点显示
            that.updateNodeDisplay(that.cellInfos[cellIndex]);
            console.log("✓ 本地数据已更新，库位类型:", that.cellInfos[cellIndex].type);
          }
        } else {
          that.$modal.msgError("保存失败：" + response.msg);
          console.error("❌ 保存失败:", response.msg);
        }
      }).catch((error) => {
        that.savingCell = false;
        console.error("❌ 保存库位配置失败:", error);
        that.$modal.msgError("保存失败，请检查网络连接");
      });
    },

    // 更新节点显示
    updateNodeDisplay(cellInfo) {
      if (!this.myDiagram || !cellInfo) return;

      const node = this.myDiagram.findNodeForKey(cellInfo.code);
      if (node) {
        // 更新颜色
        this.myDiagram.model.setDataProperty(node.data, "borderColor", cellInfo.borderColor);
        this.myDiagram.model.setDataProperty(node.data, "fillColor", cellInfo.fillColor);
        this.myDiagram.model.setDataProperty(node.data, "textColor", cellInfo.textColor);

        // 更新大小（根据type）
        var cellType = cellInfo.type !== undefined && cellInfo.type !== null ? cellInfo.type : 0;
        var cellWidth = this.gridSize.width;
        var cellHeight = this.gridSize.height;

        if (cellType === 4) {
          cellWidth = cellWidth / 2;
          cellHeight = cellHeight / 2;
        }

        var cellSize = "" + cellWidth + " " + cellHeight;
        this.myDiagram.model.setDataProperty(node.data, "cellSize", cellSize);

        // 如果需要调整位置使其居中
        var x = this.gridSize.width * cellInfo.dispalyX;
        var y = this.gridSize.height * cellInfo.dispalyY;

        if (cellType === 4) {
          x += (this.gridSize.width - cellWidth) / 2;
          y += (this.gridSize.height - cellHeight) / 2;
        }

        var loc = "" + x + " " + y;
        this.myDiagram.model.setDataProperty(node.data, "loc", loc);
      }
    },

    // 取消编辑
    cancelCellEdit() {
      this.selectedCell = null;
      this.cellEditForm = {
        id: null,
        code: '',
        type: 0,
        preCode: '',
        subX: 0,
        subY: 0,
        subZ: 0
      };
    },

    // 批量设置前置库位
    setPreCell() {
      var that = this;

      if (!this.preCellForm.fromX || !this.preCellForm.toX ||
          !this.preCellForm.fromY || !this.preCellForm.toY ||
          !this.preCellForm.fx) {
        this.$modal.msgWarning("请填写完整的配置信息");
        return;
      }

      request({
        url: "/wcs-base/CellInfo/setPreCell",
        method: "get",
        params: {
          wareCode: that.wareCode,
          fromX: that.preCellForm.fromX,
          toX: that.preCellForm.toX,
          fromY: that.preCellForm.fromY,
          toY: that.preCellForm.toY,
          fx: that.preCellForm.fx,
          z: that.floor
        },
      }).then((response) => {
        that.preCellVisible = false;
        if (response.code == 200) {
          that.$modal.msgSuccess("批量设置前置库位成功");
          // 刷新当前楼层数据
          that.getCellInfos(that.floor);
        } else {
          that.$modal.msgError(response.msg);
        }
      }).catch((error) => {
        console.error("设置前置库位失败:", error);
        that.$modal.msgError("设置失败");
      });
    },

    // 打开批量配置对话框
    openBatchPreCellDialog() {
      this.preCellForm = {
        fromX: '',
        toX: '',
        fromY: '',
        toY: '',
        fx: ''
      };
      this.preCellVisible = true;
    },
    
    // 删除库位
    deleteCell() {
      var that = this;
      
      if (!this.selectedCell || !this.cellEditForm.id) {
        this.$modal.msgError("请先选择库位");
        return;
      }
      
      this.$confirm('确定要删除库位 "' + this.cellEditForm.code + '" 吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delCellInfo(that.cellEditForm.id).then((response) => {
          if (response.code == 200) {
            that.$modal.msgSuccess("库位删除成功");
            
            // 从图表中移除节点
            if (that.myDiagram) {
              const node = that.myDiagram.findNodeForKey(that.cellEditForm.code);
              if (node) {
                that.myDiagram.model.removeNodeData(node.data);
              }
            }
            
            // 从本地数据中移除库位
            const cellIndex = that.cellInfos.findIndex(c => c.id === that.cellEditForm.id);
            if (cellIndex !== -1) {
              that.cellInfos.splice(cellIndex, 1);
            }
            
            // 清除选择和编辑表单
            that.selectedCell = null;
            that.cellEditForm = {
              id: null,
              code: '',
              type: 0,
              preCode: '',
              subX: 0,
              subY: 0,
              subZ: 0
            };
          } else {
            that.$modal.msgError("删除失败：" + response.msg);
          }
        }).catch((error) => {
          console.error("删除库位失败:", error);
          that.$modal.msgError("删除失败，请检查网络连接");
        });
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
@import '../CellSxcView-styles.css';

.cell-edit-view {
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

/* 连接线图例样式 */
.link-legend-container {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 12px;
}

.link-legend-container .legend-title {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #ffffff;
  font-weight: 600;
  font-size: 14px;
}

.link-legend-container .legend-title i {
  color: #4facfe;
  margin-right: 6px;
  font-size: 16px;
}

.link-legend-container .legend-items {
  display: flex;
  justify-content: space-around;
  gap: 12px;
}

.link-legend-container .legend-item {
  display: flex;
  align-items: center;
  color: #e2e8f0;
  font-size: 13px;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
}

/* 工具面板样式 */
.tool-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tool-section {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #ffffff;
  font-weight: 600;
  font-size: 13px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title i {
  color: #4facfe;
  margin-right: 6px;
  font-size: 14px;
}

.section-content {
  color: #e2e8f0;
  font-size: 12px;
  line-height: 1.8;
}

.section-content p {
  margin: 4px 0;
}

.section-content strong {
  color: #4facfe;
  font-size: 16px;
}

.color-indicator {
  width: 30px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.tool-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tool-actions .el-button {
  width: 100%;
}

/* 库位配置面板样式 */
.info-panel {
  margin-bottom: 16px;
}

.info-panel.panel-collapsed {
  margin-top: 16px;
}

/* 库位类型图例样式（补充legend-box样式） */
.legend-box {
  width: 30px;
  height: 20px;
  background: #2d3748;
  border: 3px solid;
  border-radius: 6px;
  flex-shrink: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  color: #a0aec0;
  font-size: 12px;
  font-weight: 500;
}

.form-value {
  color: #ffffff;
  font-size: 13px;
  padding: 6px 8px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* Element UI 表单样式覆盖 */
.tool-section >>> .el-form-item {
  margin-bottom: 16px;
}

.tool-section >>> .el-form-item__label {
  color: #a0aec0;
  font-size: 12px;
  font-weight: 500;
  padding: 0;
  line-height: 1.5;
}

.tool-section >>> .el-input__inner {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: #ffffff;
  font-size: 13px;
}

.tool-section >>> .el-input__inner:focus {
  border-color: #4facfe;
  background: rgba(255, 255, 255, 0.08);
}

.tool-section >>> .el-input-number {
  width: 100%;
}

.tool-section >>> .el-input-number__increase,
.tool-section >>> .el-input-number__decrease {
  background: rgba(255, 255, 255, 0.05);
  border-left: 1px solid rgba(255, 255, 255, 0.15);
  color: #a0aec0;
}

.tool-section >>> .el-input-number__increase:hover,
.tool-section >>> .el-input-number__decrease:hover {
  color: #4facfe;
}

.tool-section >>> .el-select {
  width: 100%;
}

.tool-section >>> .el-select .el-input__inner {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: #ffffff;
}

.tool-section >>> .el-select .el-input__inner:focus {
  border-color: #4facfe;
}

.tool-section >>> .el-select-dropdown {
  background: #2d3748;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.tool-section >>> .el-select-dropdown__item {
  color: #e2e8f0;
}

.tool-section >>> .el-select-dropdown__item:hover {
  background: rgba(79, 172, 254, 0.2);
}

.tool-section >>> .el-select-dropdown__item.selected {
  color: #4facfe;
  font-weight: 600;
}

.tool-actions .el-button + .el-button {
  margin-top: 8px;
  margin-left: 0;
}
</style>

