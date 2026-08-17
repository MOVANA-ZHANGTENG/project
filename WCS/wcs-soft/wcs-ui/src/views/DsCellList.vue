<template>
  <div class="container">
    <el-row>
      <el-col :span="3">
        <el-card
          style="
            width: 95%;
            height: 87vh;
            background-color: #ffffff;
            margin-left: 5%;
            margin-top: 3vh;
            padding-top: 2vh;
          "
        >
          <!-- <el-form :inline="true" ref="ware" :model="wareInfo" class="demo-form-inline">
              <el-form-item label="仓库名称"> -->
          <el-select
            v-model="wareCode"
            placeholder="请选择仓库"
            @change="getLineInfos(wareCode)"
          >
            <el-option
              v-for="item in wareInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            >
            </el-option>
          </el-select>
          <!-- </el-form-item>
            </el-form> -->

          <!-- 层数选择 -->
          <div style="width: 100%; margin-top: 50px">
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
          </div>
          <!-- <el-button v-if="show" @click="updateLineModel">更新货位模型</el-button> -->
        </el-card>
      </el-col>
      <el-col :span="15">
        <!-- 货位状态示例 -->
        <el-card
          style="
            margin: 1%;
            height: 7vh;
            background-color: #ffffff;
            margin-top: 3vh;
            padding: 0.3vh 0px 10px 10px;
          "
        >
          <div style="float: left; margin-left: 25px; margin-top: 5px">
            无货
          </div>
          <div
            class="kongCell"
            style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "
          ></div>

          <div style="float: left; margin-left: 25px; margin-top: 5px">
            有货
          </div>
          <div
            class="noKongCell"
            style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "
          ></div>

          <div style="float: left; margin-left: 25px; margin-top: 5px">
            入库中
          </div>
          <div
            class="inCell"
            style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "
          ></div>

          <div style="float: left; margin-left: 25px; margin-top: 5px">
            出库中
          </div>
          <div
            class="outCell"
            style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "
          ></div>
          <div style="float: left; margin-left: 25px; margin-top: 5px">
            禁用
          </div>
          <div
            class="disableCell"
            style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "
          ></div>
          <!-- <div style="
                float: left;

                width: 3%;
                height: 2.3vh;
                margin-top: 2px;
                margin-left: 2px;
                margin-top: 7px;
              ">
              <i style="color: red" class="el-icon-close"></i>
            </div> -->
        </el-card>

        <!-- 货位展示 -->
        <el-card style="margin: 1%">
          <div
            id="diagramDiv"
            style="
              flex-grow: 1;
              height: 75vh;
              background-color: rgba(255, 255, 255, 0);
              position: relative;
              -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
              cursor: auto;
            "
          ></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="margin-top: 3vh; height:    87vh;">
          <div slot="header" class="clearfix">
            <span>货位信息</span>
            <!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
          </div>
          <el-form
            v-loading="cellLoading"
            style="margin: 1%"
            v-if="cellInfo.code != null"
            ref="form"
            :model="cellInfo"
            label-width="80px"
          >
            <el-form-item label="货位编码">
              <span>{{ cellInfo.code }}</span>
            </el-form-item>
            <!-- <el-form-item label="托盘编码">
              <span>{{ cellInfo.palletCode }}</span>
            </el-form-item> -->
            <el-form-item label="货位层数">
              <span>{{ cellInfo.z }}</span>
            </el-form-item>
            <el-form-item label="巷道编码">
              <span>{{ cellInfo.lineCode }}</span>
            </el-form-item> 
            <el-form-item label="库存状态">
              <el-select
                v-model="cellInfo.invenState"
                placeholder="请选择库存状态"
              >
                <el-option
                  v-for="item in invenStates"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="任务状态">
              <el-select
                v-model="cellInfo.taskState"
                placeholder="请选择禁用状态"
              >
                <el-option
                  v-for="item in taskStates"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="是否禁用">
              <el-select
                v-model="cellInfo.disableState"
                placeholder="请选择禁用状态"
              >
                <el-option
                  v-for="item in disableStates"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="货区编码">
              <el-input :disabled="true" v-model="cellInfo.areaCode"></el-input>
              <!-- <el-select v-model="cellInfo.areaCode" placeholder="请选择库区">
                <el-option v-for="area in areaInfos" :label="area.name" :value="area.code"></el-option>
              </el-select> -->
            </el-form-item>
            <el-form-item label="料箱编码">
              {{ cellInfo.palletCode }}
            </el-form-item>
            <el-form-item label="bagId">
              {{ cellInfo.bagId }}
              
            </el-form-item>
            <el-form-item label="csId">
              {{ cellInfo.csId }}
              
            </el-form-item>
            <el-form-item label="orderNo">
              {{ cellInfo.orderNo }}
              
            </el-form-item> 
            <el-form-item label="recipeId">
              {{ cellInfo.recipeId }}
              
            </el-form-item>
            <el-form-item label="recipeName">
              {{ cellInfo.recipeName }}
              
            </el-form-item> 
            <el-form-item>
              <el-button type="primary" @click="updateCellInfoState(cellInfo)"
                >修改提交</el-button
              >
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  listWareInfo,
  getWareInfo,
  delWareInfo,
  addWareInfo,
  updateWareInfo,
  updateWareModel,
} from "@/api/wcs-base/WareInfo";
import { listPositionInfo } from "@/api/wcs-base/PositionInfo";
import { listDeviceInfo } from "@/api/wcs-base/DeviceInfo";
import {
  listLineInfo,
  getLineInfo,
  updateLineInfo,
} from "@/api/wcs-base/LineInfo";
import { listCellInfo } from "@/api/wcs-base/CellInfo"; 
import request from "@/utils/request"; 
import go from "@/lib/js/go.js"
export default {
  name: "container",
  components: {},
  data() {
    return {
      aaaaa:0,
      //网格尺寸
      gridSize: {
        width: 150,
        height: 80,
      },
      wareCode: null,
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

      minNode:{ category: "cell",x:999009999,y:999009999,        borderColor : "#ffffff",
        textColor:"#000000",
        fillColor:"#ffffff"},
      maxNode:{ category: "cell",x:-100000000,y:-100000000,  borderColor : "#ffffff",
        textColor:"#000000",
        fillColor:"#ffffff"},
      lineModelData:{
        class: "GraphLinksModel",

        nodeDataArray:[

        ]
      }
    };
  },
  watch: {
    //巷道跟换将画布清空
    lineCode(newValue, oldValue) {
      this.loadData({});
      this.lineModelData={
        class: "GraphLinksModel",

        nodeDataArray:[

        ]
      };
      
      this.minNode={ category: "cell",x:999009999,y:999009999,        borderColor : "#ffffff",
        textColor:"#000000",
        fillColor:"#ffffff"};
        this.maxNode={ category: "cell",x:-100000000,y:-100000000,  borderColor : "#ffffff",
        textColor:"#000000",
        fillColor:"#ffffff"};
      this.getCellInfos(newValue);
    },
    cellCode(newValue, oldValue) {
      if (newValue != null) {
        this.findCellInfo(newValue);
      }
    },
  },
  created() {
    this.aaaaa=this.aaaaa+1;
    console.info("aaaaaaaaaaaaaaaaaaaaaaaaa"+this.aaaaa);
    this.getWareInfos();
    if (this.wareCode != null) {
      this.getLineInfos(this.wareCode);
    }
    // this.lineInfo = JSON.stringify(localStorage.getItem("lineInfo"));
    if (this.lineCode != null) {
      that.getCellInfos(this.lineCode);
    }

    this.timer = setInterval(() => { 
      if (this.lineInfos.length == 0) {
        return;
      }
      if (this.lineCode == null || this.lineCode == "") {
        return;
      }else{
        this.updateCellView(this.lineCode);
      } 
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
    //获取所有仓库信息列表
    getWareInfos() {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/list",
        method: "get",
        params: { isDelete: 0 },
      }).then((response) => {
        if (response.code == 200) {
          that.wareInfos = response.rows;
          that.wareCode = that.wareInfos[0].code;
          this.getLineInfos(that.wareCode);
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },
    getMaxZ(lineCode){
      for (let index = 0; index < this.lineInfos.length; index++) {
        const lineInfo = this.lineInfos[index];
        if(lineInfo.code==lineCode){
          return lineInfo.maxZ;
        } 
      } 
    },
    //获取所有的巷道
    getLineInfos(wareCode) {
      this.lineInfos = [];
      if (wareCode != null) {
        listLineInfo({ wareCode: wareCode, isDelete: 0 }).then((response) => {
          if (response.code == 200) {
            localStorage.setItem("wareCode", wareCode);
            this.lineInfos = response.rows;
            this.lineCode = this.lineInfos[0].code;
            this.getCellInfos(this.lineCode);
            // this.lineInfos.forEach((element) => {
            //   if (element.code == this.lineCode) {
            //     this.getCellInfos(element);
            //   }
            // });
          }
        });
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
          that.init( );
        } 

        that.lineModelData =  {
          class: "GraphLinksModel", 
          nodeDataArray: [ 
          ]
        };
        
        request({
          url: "wcs-ds/bagMaster/cellList",
          method: "get",
          params: {
          lineCode: lineCode 
        },
        }).then((response) => {
          if (response.code == 200) {
            this.cellInfos = response.data;
            this.nodes = [];

            localStorage.setItem("lineCode", lineCode); 
            var maxZ =this.getMaxZ(lineCode);
            var maxDiapalyX = 0;
            var maxDiapalyY = 0;
            var minDiapalyY = 0;

            this.cellInfos.forEach((cell) => {
           
              var priority = cell.priority;
              var dispalyX = cell.y;
              var dispalyY;

              var BY = 1+ priority + maxZ*(priority - 1);

              // 使用 if-else 确保 dispalyY 总是被赋值
              if (cell.ab === "A") {
                dispalyY = -(maxZ * (priority - 1) + cell.z + priority);
              } else if (cell.ab === "B") {
                dispalyY = (maxZ * (priority - 1) + cell.z + priority);
                 dispalyY=dispalyY-BY;
                 dispalyY = maxZ - 1 - dispalyY;
                 dispalyY = dispalyY+BY;
       
              } else {
                // 处理 cell.ab 不是 "A" 或 "B" 的情况
                console.warn(`Unknown value for cell.ab: ${cell.ab}`);
                dispalyY = 0;
              }

              cell.dispalyX = dispalyX;
              cell.dispalyY = dispalyY;

              if (maxDiapalyX < dispalyX) {
                maxDiapalyX = dispalyX;
              }
              if (maxDiapalyY < dispalyY) {
                maxDiapalyY = dispalyY;
              }
              if (minDiapalyY > dispalyY) {
                minDiapalyY = dispalyY;
              }
            });

            if (minDiapalyY < 0) {
              maxDiapalyY = maxDiapalyY - minDiapalyY;
            }

            this.cellInfos.forEach((cell) => {
              this.cellColor(cell);
              this.addNode(cell);

              // 纵坐标
              if (cell.dispalyX === 1) {
                cell.dispalyX = 0;
                cell.fillColor = "#FFFFFF";
                cell.textColor = "#000000";
                cell.borderColor = "#FFFFFF";
                this.addNode(cell);
              }
            });

            // 横坐标
            for (let index = 1; index <= maxDiapalyX; index++) {
              var cell = {};
              cell.dispalyX = index;
              cell.dispalyY = minDiapalyY - 1;
              cell.code = index;
              cell.fillColor = "#FFFFFF";
              cell.textColor = "#000000";
              cell.borderColor = "#FFFFFF";
              this.addNode(cell);
            }
            // 中间巷道
            for (let index = 1; index <= maxDiapalyX; index++) {
              var cell = {};
              cell.dispalyX = index;
              cell.dispalyY = 0;
              cell.code = "";
              cell.fillColor = "#409EFF";
              cell.textColor = "#000000";
              cell.borderColor = "#409EFF";
              this.addNode(cell);
            }  
            this.loadData(this.lineModelData);
          }
        });
      }
    },

    updateCellView(lineCode) { 
      if (lineCode != null) {
        request({
          url: "wcs-ds/bagMaster/cellList",
          method: "get",
          params: {
          lineCode: lineCode 
        },
        }).then((response) => {
          if (response.code == 200) {
                 if (lineCode==this.lineCode && response.code == 200) {
                  var cellInfos = response.data;
                  cellInfos.forEach((cell) => {
                    if(cell.lineCode!=lineCode){
                        return;
                    }
                    this.updateCellNode(cell);
                  });
                }
          } else {
            this.$modal.msgError(response.msg);
          }
        });
        // listCellInfo({ lineCode: lineCode, pageSize: 999 }).then(
        //   (response) => { 
        //     if (lineCode==this.lineCode && response.code == 200) {
        //       var cellInfos = response.rows;
        //        cellInfos.forEach((cell) => {
        //         if(cell.lineCode!=lineCode){
        //             return;
        //         }
        //         this.updateCellNode(cell);
        //       });
        //     }
        //   }
        // );
      }
    },

    updateCellNode(cell) {
      if(this.myDiagram==null){
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
      var borderColor = "#ffffff";
      var textColor="#000000";
      var fillColor;
      /*
          1. 空货  
          2. 有货
          3. 入库任务
          4. 出库任务
          5. 货位禁用
        */
      if (cellInfo.invenState1 == 0) {
        fillColor = "#c0c4cc";
        textColor = "#000000";
        if (cellInfo.taskState != 0) {
          fillColor = "#E6A23C";
          textColor = "#FFFFFF";
        }
      } 
      else if(cellInfo.invenState1 == 1) {
        fillColor = "#409EFF";
        textColor = "#FFFFFF";
        if (cellInfo.taskState != 0) {
          fillColor = "#F56C6C";
          textColor = "#FFFFFF";
        }
      }
      else if(cellInfo.invenState1 == 2) {
        fillColor = "#67C23A";
        textColor = "#FFFFFF";
        if (cellInfo.taskState != 0) {
          fillColor = "#F56C6C";
          textColor = "#FFFFFF";
        }
      }

      //货位禁用
      if (cellInfo.disableState == 1) {
        fillColor = "#303346";
        textColor = "#FFFFFF";
        
      }

      cellInfo.fillColor = fillColor;
      cellInfo.textColor = textColor;
      cellInfo.borderColor = borderColor;
    },

    addNode(cellInfo) {
      var that=this;
      
        var x = that.gridSize.width * cellInfo.dispalyX;
        var y = that.gridSize.height * cellInfo.dispalyY;
        
        var loc = "" + x + " " + y;
        var size = "" + that.gridSize.width + " " + that.gridSize.height;

      
        var node = {
          category: "cell",
          text: cellInfo.code  ,
          key: cellInfo.code,
          loc: loc,
          size: size,
          fillColor: cellInfo.fillColor,
          textColor: cellInfo.textColor,
          borderColor: cellInfo.borderColor,
        };
        that.lineModelData.nodeDataArray.push(node);
       // that.nodes.push(node);


       if(that.maxNode.y<y  ){
          that.maxNode.y=y; 
          that.maxNode.loc= "" + that.maxNode.x + " " + that.maxNode.y;
          that.maxNode.size= size; 
        //  that.maxNode.text= cellInfo.code +"mmmm"+loc;
        }

        if(  that.maxNode.x<x){ 
          that.maxNode.x=x;
           
          that.maxNode.loc= "" + that.maxNode.x + " " + that.maxNode.y;
          that.maxNode.size= size; 
          //that.maxNode.text= cellInfo.code +"mmmm"+loc;
        }
        
        if(that.minNode.y>y  ){
          that.minNode.y=y; 
          that.minNode.loc= "" + that.minNode.x + " " + that.minNode.y;
          that.minNode.size= size; 
         // that.minNode.text= cellInfo.code +"mmmm"+loc;
        }

        if(  that.minNode.x>x){ 
          that.minNode.x=x;
          that.minNode.loc= "" + that.minNode.x + " " + that.minNode.y;
          that.minNode.size= size; 
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
          LinkDrawn: showLinkLabel, // 下面定义了此DiagramEvent侦听器
          LinkRelinked: showLinkLabel,
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
          click: function (e, node) {},
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

      //替换linkTemplateMap中的默认链接模板
      this.myDiagram.linkTemplate = $(
        go.Link, // the whole link panel
        {
          routing: go.Link.AvoidsNodes,
          curve: go.Link.JumpOver,
          corner: 5,
          toShortLength: 4,
          relinkableFrom: true,
          relinkableTo: true,
          reshapable: true,
          resegmentable: true,
          //鼠标悬停巧妙地突出显示链接：
          mouseEnter: function (e, link) {
            link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)";
          },
          mouseLeave: function (e, link) {
            link.findObject("HIGHLIGHT").stroke = "transparent";
          },
          selectionAdorned: false,
          // 当链接被点击时的处理函数
          click: function (e, obj) {
            //linkClick
            var node = obj.part;
            var data = node.data;
            that.nowData = data;
          },
        },
        new go.Binding("points").makeTwoWay(),
        $(
          go.Shape, //高光形状，通常透明
          {
            isPanelMain: true,
            strokeWidth: 8,
            stroke: "transparent",
            name: "HIGHLIGHT",
          }
        ),
        $(
          go.Shape, //链接路径形状
          { isPanelMain: true, stroke: "gray", strokeWidth: 2 },
          new go.Binding("stroke", "isSelected", function (sel) {
            return sel ? "dodgerblue" : "gray";
          }).ofObject()
        ),
        $(
          go.Shape, //箭头
          { toArrow: "standard", strokeWidth: 0, fill: "gray" }
        ),
        $(
          go.Panel,
          "Auto", //链接标签，通常不可见
          {
            visible: false,
            name: "LABEL",
            segmentIndex: 2,
            segmentFraction: 0.5,
          },
          new go.Binding("visible", "visible").makeTwoWay(),
          $(
            go.Shape,
            "RoundedRectangle", //标签形状
            { fill: "#F8F8F8", strokeWidth: 0 }
          ),
          $(
            go.TextBlock,
            "Yes", //标签
            {
              textAlign: "center",
              font: "10pt helvetica, arial, sans-serif",
              stroke: "#333333",
              editable: true,
            },
            new go.Binding("text").makeTwoWay()
          )
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
      var that=this;
      if(data=={}){
        var modelData =  go.Model.fromJson(data);
        that.myDiagram.model =modelData;
        return;
      }
      //分步加载
      if(data.nodeDataArray!=undefined){
        that.myDiagram.model.addNodeDataCollection([that.minNode,that.maxNode]);
        that.myDiagram.initialAutoScale = go.Diagram.Uniform;
        that.myDiagram.zoomToFit(); 
        that.loadDataInChunks(data.nodeDataArray, 20); // 每次加载 100 个节点
      } 
    },

      loadDataInChunks(data, chunkSize) {
        debugger
        var that=this;
        let index = 0;
        function loadNextChunk() {
          if(that.lineModelData.nodeDataArray.length==0){
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
      this.cellInfos.forEach((cell) => {
        if (cell.code == cellCode) {
          this.cellInfo = cell;
        }
      });
      // listCellInfo({ code: cellCode }).then((response) => {
      //   if (response.code == 200) {
      //     if (response.rows.length == 0) {
      //       this.cellInfo == {}
      //     } else {
      //       this.cellInfo = response.rows[0]
      //     }
      //   }
      // })
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
          // this.getCellInfos(this.z);
          // this.findByXYZ(this.cellFind, x, this.cellFind.y, this.cellFind.z);
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.container {
  background-color: #f2f6fc;


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
    color: #c0c4cc;
    background-color: #c0c4cc;

    .el-icon-close {
      display: none;
    }
  }

  .noKongCell {
    color: #409eff;
    background-color: #409eff;

    .el-icon-close {
      display: none;
    }
  }

  .inCell {
    color: #e6a23c;
    background-color: #e6a23c;

    .el-icon-close {
      display: none;
    }
  }

  .outCell {
    color: #f56c6c;
    background-color: #f56c6c;

    .el-icon-close {
      display: none;
    }
  }

  .noCell {
    .el-icon-close {
      display: none;
    }
  }

  .disableCell {
    color: #303346;
    background-color: #303346;

    .el-icon-close {
      display: contents;
    }
  }
}

.el-header,
.el-footer {
  background-color: #b3c0d1;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-aside {
  background-color: #d3dce6;
  color: #333;
  text-align: center;
  line-height: 200px;
}

.el-main {
  background-color: #e9eef3;
  color: #333;
  text-align: center;
  line-height: 160px;
}

body > .el-container {
  margin-bottom: 40px;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}
</style>
