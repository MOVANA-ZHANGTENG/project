<template>
  <div class="container">
    <div style="width: 100%">
      <el-card style="margin: 10px">
        <el-form :inline="true" ref="ware" :model="wareInfo" class="demo-form-inline">
          <el-form-item label="编码">
            <el-input v-model="wareInfo.code" placeholder="编码"></el-input>
          </el-form-item>
          <el-form-item style="margin-left:10%" label="名称">
            <el-input v-model="wareInfo.name" placeholder="名称"></el-input>
          </el-form-item>
          <!-- <el-form-item label="模型数据">
            <el-input v-model="wareInfo.modelData" placeholder="模型数据"></el-input>
          </el-form-item> -->

          <el-form-item style="margin-left:10%">
            <el-button type="primary" @click="saveWare()">saveWare</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      <el-row>
        <el-col :span="4">
          <el-card style="margin: 10px">
            <div id="myPaletteDiv" style="
                height: 750px;
                width: 100%;
                margin-right: 2px;
                background-color: rgba(255, 255, 255, 0);
                position: relative;
                -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
                cursor: auto;
              ">
              <canvas tabindex="0" width="150" height="1125" style="
                  position: absolute;
                  top: 0px;
                  left: 0px;
                  z-index: 2;
                  user-select: none;
                  width: 100px;
                  height: 750px;
                  cursor: auto;
                "> </canvas>
              <div style="
                  position: absolute;
                  overflow: auto;
                  width: 100px;
                  height: 750px;
                  z-index: 1;
                ">
                <div style="position: absolute; width: 1px; height: 1px"></div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="14">
          <el-card style="margin: 10px">
            <div id="this.myDiagramDiv" style="
                flex-grow: 1;
                height: 750px;
                background-color: rgba(255, 255, 255, 0);
                position: relative;
                -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
                cursor: auto;
              ">
              <canvas tabindex="0" width="1206" height="1125" style="
                  position: absolute;
                  top: 0px;
                  left: 0px;
                  z-index: 2;
                  user-select: none;
                  width: 804px;
                  height: 750px;
                  cursor: auto;
                ">This text is displayed if your browser does not support the
                Canvas HTML element.</canvas>
              <div style="
                  position: absolute;
                  overflow: auto;
                  width: 804px;
                  height: 750px;
                  z-index: 1;
                ">
                <div style="position: absolute; width: 1px; height: 1px"></div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card style="margin: 10px">
            <!-- <el-button @click="saveData()" type="primary">主要按钮</el-button> -->
            <el-form ref="form" v-if="nowData.key != undefined && nowData.key != null" :model="nowData" label-width="80px">
              <el-form-item label="站台名称">
                <el-input v-model="nowData.name"></el-input>
              </el-form-item>
              <el-form-item label="站点编码">
                <el-input v-model="nowData.code"></el-input>
              </el-form-item>
              <el-form-item label="站点类型">
                <el-select v-model="nowData.category" placeholder="站点类型">
                  <el-option v-for="item in palletModel" :key="item.category" :label="item.name"
                    :value="item.category"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="图形位置">
                <el-input v-model="nowData.loc"></el-input>
              </el-form-item>


            </el-form>
            <el-form ref="form" v-else-if="nowData.from != undefined && nowData.from != null" :model="nowData"
              label-width="80px">
              <el-form-item label="流程编码">
                <el-input v-model="nowData.code"></el-input>
              </el-form-item>
              <el-form-item label="流程名称">
                <el-input v-model="nowData.name"></el-input>
              </el-form-item>
              <el-form-item label="起始位置">
                <el-input v-model="nowData.from"></el-input>
              </el-form-item>
              <el-form-item label="目标位置">
                <el-input v-model="nowData.to"></el-input>
              <el-form-item label="前置条件">
                <el-input @focus="stepSetting.open = true" v-model="nowData.cmdPre"></el-input>
              </el-form-item>
              <el-form-item label="执行函数">
                <el-input @focus="stepSetting.open = true" v-model="nowData.cmd"></el-input>
              </el-form-item>
              <el-form-item label="完成条件">
                <el-input @focus="stepSetting.open = true" v-model="nowData.successPre"></el-input>
              </el-form-item>
              <el-form-item label="成功回调">
                <el-input @focus="stepSetting.open = true" v-model="nowData.success"></el-input>
              </el-form-item>
              </el-form-item>



            </el-form>
            <!-- {{ nowData }} -->

            <!-- <br>
            {{ modelData }} -->

            <div style="text-align: center; margin-top: 10%;">
              <el-button @click="saveWare()" type="primary">saveWare</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- <button id="SaveButton" onclick="save()">Save</button>
    <button onclick="load()">Load</button>
    <textarea id="mySavedModel" style="width: 100%; height: 300px">
{ "class": "go.GraphLinksModel",
	"linkFromPortIdProperty": "fromPort",
	"linkToPortIdProperty": "toPort",
	"nodeDataArray": [
 
	],
	"linkDataArray": [
 
	]}</textarea
    >
    <button onclick="printDiagram()">Print Diagram Using SVG</button> -->
  </div>
</template>

<script>
import { listWareInfo, getWareInfo, delWareInfo, addWareInfo, updateWareInfo } from "@/api/wcs-base/WareInfo";
import go from "@/lib/js/go.js"
export default {
  name: "Index",
  data() {
    return {
      wareInfo: {},
      nowData: {},
      myDiagram: null,
      modelData: {},
      palletModel: [
        { category: "Start", name: "入库口", code: '' },
        { category: "End", name: "出库口", code: '' },
        { category: "dockingPoint", name: "接驳位置", code: '' },
        { category: "ExPort", name: "异常排除口", code: '' },
        { category: "line", name: "巷道", code: '' },
        { category: "check", name: "校验点", code: '' },
      ],
      //网格尺寸
      gridSize: {
        width: 20, length: 20
      },
    };
  },
  watch: {
    // nowData: {
    //         deep: true,
    //         handler(newVal, oldVal) { 
    //           debugger
    //            var data = this.myDiagram.model.toJson();
    //            console.info("模型数据");
    //            console.info(data);
    //            console.info("当前选中");
    //            console.info(newVal);


    //         }
    //     }, 
  },
  mounted() {
    this.init();
  },
  methods: {
    /** 提交按钮 */
    saveWare() {
      this.saveData()
      this.$refs["ware"].validate(valid => {
        if (valid) {
          if (this.wareInfo.id != null) {
            updateWareInfo(this.wareInfo).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }

            });
          } else {
            addWareInfo(this.wareInfo).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }


            });
          }
        }
      });
    },
    saveData() {
      if (this.nowData.key != undefined) {
        var data = this.myDiagram.model.toJson();
        data = JSON.parse(data);
        this.modelData = data;
        for (var i = 0; i < this.modelData.nodeDataArray.length; i++) {
          if ((this.modelData.nodeDataArray[i].key == this.nowData.key)) {
            this.modelData.nodeDataArray[i].code = this.nowData.code;
            this.modelData.nodeDataArray[i].text = this.nowData.text;
          }
        }
        this.loadData(this.modelData);
      } else if (this.nowData.from != undefined) {
        var data = this.myDiagram.model.toJson();
        data = JSON.parse(data);
        this.modelData = data;
        for (var i = 0; i < this.modelData.linkDataArray.length; i++) {
          if ((this.modelData.linkDataArray[i].from == this.nowData.from && this.modelData.linkDataArray[i].to == this.nowData.to)) {
            this.modelData.linkDataArray[i].code = this.nowData.code;
            this.modelData.linkDataArray[i].text = this.nowData.text;
          }
        }
        this.loadData(this.modelData);
      }

    },
    incrementCounter(e, obj) {
      console.info(obj);
      var node = obj.part;
      var data = node.data;
      this.nowData = data;
    },
    init() {
      var that = this;
      var $ = go.GraphObject.make; // 定义模板时的简洁性
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.length);
      this.myDiagram = $(
        go.Diagram,
        "this.myDiagramDiv", //必须命名或引用DIV HTML元素
        {
          //背景网格
          grid: $(
            go.Panel,
            "Grid",
            { gridCellSize: CellSize },
            $(go.Shape, "LineH", { stroke: "lightgray" }),
            $(go.Shape, "LineV", { stroke: "lightgray" })
          ),
          //网格捕捉
          "draggingTool.isGridSnapEnabled": true,
          //拖动捕捉
          "draggingTool.gridSnapCellSpot": go.Spot.Center,
          //缩放捕捉
          "resizingTool.isGridSnapEnabled": true,
          LinkDrawn: showLinkLabel, // 下面定义了此DiagramEvent侦听器
          LinkRelinked: showLinkLabel,
          "undoManager.isEnabled": true, //启用撤消和重做
        }
      );

      var myDiagram = this.myDiagram;
      this.myDiagram.addChangedListener(function (event) {
        // 当模型数据发生变化时，这个函数会被调用
        // 使用event.change可以获取具体的变化信息
        var change = event.change;

        // 如果需要获取整个模型的数据，可以使用model.toJson()
        var modelData = myDiagram.model.toJson();
        that.modelData = modelData;
        that.wareInfo.modelData = modelData;
        console.log("模型数据变化:", change);
        console.log("最新模型数据:", modelData);
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
            //Node.location位于每个节点的中心
            locationSpot: go.Spot.Center,
          },
        ];
      }
      //定义一个用于创建通常透明的“端口”的函数。
      //“name”用作GraphObject.portId，
      //“对齐”用于确定端口相对于节点主体的位置，
      //“点”用于控制链接与端口的连接方式以及端口是否连接
      //沿着节点的侧面延伸，
      //布尔值“output”和“input”参数控制用户是否可以从端口绘制链接或绘制到端口的链接。
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
      function textStyle() {
        return {

          font: "bold 8pt Lato, Helvetica, Arial, sans-serif",
          stroke: "#303133",
        };
      }

      myDiagram.nodeTemplate = $(
        go.Node,
        "Auto",
        {
          click: function (e, node) {
            debugger;
          },
          resizable: true,
          resizeObjectName: "SHAPE",
          // 因为gridSnapCellSpot是中心，所以偏移节点的位置
          locationSpot: new go.Spot(
            0,
            0,
            CellSize.width / 2,
            CellSize.height / 2
          ),
          // 提供有关将任何东西掉落到“物品”上的视觉警告
          mouseDragEnter: function (e, node) {
            debugger;
            e.handled = true;
            node.findObject("SHAPE").fill = "red";
            e.diagram.currentCursor = "not-allowed";
            highlightGroup(node.containingGroup, false);
          },
          mouseDragLeave: function (e, node) {
            debugger;
            node.updateTargetBindings();
          },
          mouseDrop: function (e, node) {
            debugger;
            //禁止将任何内容放到“项目”上
            node.diagram.currentTool.doCancel();
          },
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
            desiredSize: CellSize, // initially 1x1 cell
          },
          new go.Binding("fill", "color"),
          new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(
            go.Size.stringify
          )
        ),
        // 定义了文本  文本位置  文本字体  显示json中哪个字段
        $(
          go.TextBlock,
          { alignment: go.Spot.Center, font: "bold 16px sans-serif" },
          new go.Binding("text", "key")
        )
      );

      // 定义常规节点的节点模板
      this.myDiagram.nodeTemplateMap.add(
        "", //默认类别

        $(
          go.Node,
          "Table",
          nodeStyle(),
          // 主对象是一个面板，它围绕着一个矩形的TextBlock
          $(
            go.Panel,
            "Auto",

            $(
              go.Shape,
              "Rectangle",
              { fill: "#FFFFFF", stroke: "#00A9C9", strokeWidth: 3.5 },
              new go.Binding("figure", "figure")
            ),
            $(
              go.TextBlock,
              textStyle(),
              {
                margin: 8,
                maxSize: new go.Size(160, NaN),
                wrap: go.TextBlock.WrapFit,
                editable: true,
              },
              new go.Binding("text").makeTwoWay()
            )
          ),
          // 四个命名端口，每侧一个：
          makePort("T", go.Spot.Top, go.Spot.TopSide, false, true),
          makePort("L", go.Spot.Left, go.Spot.LeftSide, true, true),
          makePort("R", go.Spot.Right, go.Spot.RightSide, true, true),
          makePort("B", go.Spot.Bottom, go.Spot.BottomSide, true, false)
        )
      );
      this.myDiagram.nodeTemplateMap.add(
        "check",
        $(
          go.Node,
          "Table",
          nodeStyle(),
          //主对象是一个面板，它围绕着一个矩形的TextBlock
          $(
            go.Panel,
            "Auto",
            $(
              go.Shape,
              "Diamond",
              { desiredSize: new go.Size(100, 50), fill: "#FFFFFF", stroke: "#00A9C9", strokeWidth: 3.5 },
              new go.Binding("figure", "figure")
            ),
            // 当按钮被点击时的处理函数
            {
              click: function (e, obj) {
                var node = obj.part;
                var data = node.data;
                that.nowData = data;
                console.info(data);
              },
            },
            $(go.TextBlock, textStyle(), new go.Binding("text", "name"))
          ),
          // 四个命名端口，每侧一个：
          makePort("T", go.Spot.Top, go.Spot.Top, false, true),
          makePort("L", go.Spot.Left, go.Spot.Left, true, true),
          makePort("R", go.Spot.Right, go.Spot.Right, true, true),
          makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, false)
        )
      );
      this.myDiagram.nodeTemplateMap.add(
        "Start",
        $(
          go.Node,
          "Table",
          nodeStyle(),
          //主对象是一个面板，它围绕着一个矩形的TextBlock
          $(
            go.Panel,
            "Auto",
            $(
              go.Shape,
              "Circle",
              { desiredSize: new go.Size(50, 50), fill: "#FFFFFF", stroke: "#67C23A", strokeWidth: 3.5 },
              new go.Binding("figure", "figure")
            ),
            // 当按钮被点击时的处理函数
            {
              click: function (e, obj) {
                var node = obj.part;
                var data = node.data;
                that.nowData = data;
                console.info(data);
              },
            },
            $(go.TextBlock, textStyle(), new go.Binding("text", "name"))
          ),
          // 四个命名端口，每侧一个：
          makePort("T", go.Spot.Top, go.Spot.Top, false, true),
          makePort("L", go.Spot.Left, go.Spot.Left, true, true),
          makePort("R", go.Spot.Right, go.Spot.Right, true, true),
          makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, false)
        )
      );

      this.myDiagram.nodeTemplateMap.add(
        "End",
        $(
          go.Node,
          "Table",
          nodeStyle(),
          //主对象是一个面板，它围绕着一个矩形的TextBlock
          $(
            go.Panel,
            "Auto",
            $(
              go.Shape,
              "Circle",
              { desiredSize: new go.Size(50, 50), fill: "#FFFFFF", stroke: "#E6A23C", strokeWidth: 3.5 },
              new go.Binding("figure", "figure")
            ),
            // 当按钮被点击时的处理函数
            {
              click: function (e, obj) {
                var node = obj.part;
                var data = node.data;
                that.nowData = data;
                console.info(data);
              },
            },
            $(go.TextBlock, textStyle(), new go.Binding("text", "name"))
          ),
          // 四个命名端口，每侧一个：
          makePort("T", go.Spot.Top, go.Spot.Top, false, true),
          makePort("L", go.Spot.Left, go.Spot.Left, true, true),
          makePort("R", go.Spot.Right, go.Spot.Right, true, true),
          makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, false)
        )
      );
      this.myDiagram.nodeTemplateMap.add(
        "dockingPoint",
        $(
          go.Node,
          "Table",
          nodeStyle(),
          $(
            go.Panel,
            "Spot",

            $(go.Shape, "Circle", {
              desiredSize: new go.Size(58, 58),
              fill: "#FFFFFF",
              stroke: "#09d3ac",
              strokeWidth: 3,
            }),
            // 当按钮被点击时的处理函数
            {
              click: function (e, obj) {
                var node = obj.part;
                var data = node.data;
                that.nowData = data;
                console.info(data);
              },
            },
            $(go.TextBlock, textStyle(), new go.Binding("text", "name"))
          ),
          //三个命名端口，除顶部外，每侧各一个，所有端口仅输出：
          makePort("T", go.Spot.Top, go.Spot.Top, true, true),
          makePort("L", go.Spot.Left, go.Spot.Left, true, true),
          makePort("R", go.Spot.Right, go.Spot.Right, true, true),
          makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, true)
        )
      );
      this.myDiagram.nodeTemplateMap.add(
        "line",
        $(
          go.Node,
          "Table",
          nodeStyle(),
          $(
            go.Panel,
            "Spot",

            $(go.Shape, "Rectangle", {
              desiredSize: new go.Size(50, 50),
              fill: "#FFFFFF",
              stroke: "#409EFF",
              strokeWidth: 3,
            }),
            // 当按钮被点击时的处理函数
            {
              click: function (e, obj) {
                var node = obj.part;
                var data = node.data;
                that.nowData = data;
                console.info(data);
              },
            },
            $(go.TextBlock, "巷道", textStyle(), new go.Binding("text", "name"))
          ),
          //三个命名端口，除顶部外，每侧各一个，所有端口仅输出：
          makePort("T", go.Spot.Top, go.Spot.Top, true, true),
          makePort("L", go.Spot.Left, go.Spot.Left, true, true),
          makePort("R", go.Spot.Right, go.Spot.Right, true, true),
          makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, true)
        )
      );
      this.myDiagram.nodeTemplateMap.add(
        "ExPort",
        $(
          go.Node,
          "Table",
          nodeStyle(),
          $(
            go.Panel,
            "Spot",
            $(go.Shape, "Circle", {
              desiredSize: new go.Size(50, 50),
              fill: "#FFFFFF",
              stroke: "#DC3C00",
              strokeWidth: 3,
            }),
            // 当按钮被点击时的处理函数
            {
              click: function (e, obj) {
                var node = obj.part;
                var data = node.data;
                that.nowData = data;
                console.info(data);
              },
            },
            $(go.TextBlock, "End", textStyle(), new go.Binding("text", "name"))
          ),
          // 三个命名端口，除底部外，每侧各一个，仅限输入：
          makePort("T", go.Spot.Top, go.Spot.Top, false, true),
          makePort("L", go.Spot.Left, go.Spot.Left, false, true),
          makePort("R", go.Spot.Right, go.Spot.Right, false, true)
        )
      );
      // taken from ../extensions/Figures.js:
      go.Shape.defineFigureGenerator("File", function (shape, w, h) {
        var geo = new go.Geometry();
        var fig = new go.PathFigure(0, 0, true); // starting point
        geo.add(fig);
        fig.add(new go.PathSegment(go.PathSegment.Line, 0.75 * w, 0));
        fig.add(new go.PathSegment(go.PathSegment.Line, w, 0.25 * h));
        fig.add(new go.PathSegment(go.PathSegment.Line, w, h));
        fig.add(new go.PathSegment(go.PathSegment.Line, 0, h).close());
        var fig2 = new go.PathFigure(0.75 * w, 0, false);
        geo.add(fig2);
        // The Fold
        fig2.add(new go.PathSegment(go.PathSegment.Line, 0.75 * w, 0.25 * h));
        fig2.add(new go.PathSegment(go.PathSegment.Line, w, 0.25 * h));
        geo.spot1 = new go.Spot(0, 0.25);
        geo.spot2 = go.Spot.BottomRight;
        return geo;
      });
      this.myDiagram.nodeTemplateMap.add(
        "Comment",
        $(
          go.Node,
          "Auto",
          nodeStyle(),
          $(go.Shape, "File", {
            fill: "#FFFFFF",
            stroke: "#DEE0A3",
            strokeWidth: 3,
          }),
          $(
            go.TextBlock,
            textStyle(),
            {
              margin: 8,
              maxSize: new go.Size(100, NaN),
              wrap: go.TextBlock.WrapFit,
              textAlign: "center",
              editable: true,
            },
            new go.Binding("text").makeTwoWay()
          )
          // 没有端口，因为不允许链接与注释连接
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
            debugger
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
      // initialize the Palette that is on the left side of the page
      var myPalette = $(
        go.Palette,
        "myPaletteDiv", // must name or refer to the DIV HTML element
        {
          // Instead of the default animation, use a custom fade-down
          "animationManager.initialAnimationStyle": go.AnimationManager.None,
          InitialAnimationStarting: animateFadeDown, // Instead, animate with this function
          nodeTemplateMap: this.myDiagram.nodeTemplateMap, // share the templates used by this.myDiagram
          model: new go.GraphLinksModel(this.palletModel),
        }
      );
      // This is a re-implementation of the default animation, except it fades in from downwards, instead of upwards.

      function animateFadeDown(e) {
        var diagram = e.diagram;
        var animation = new go.Animation();
        animation.isViewportUnconstrained = true; // So Diagram positioning rules let the animation start off-screen
        animation.easing = go.Animation.EaseOutExpo;
        animation.duration = 900;
        // Fade "down", in other words, fade in from above
        animation.add(
          diagram,
          "position",
          diagram.position.copy().offset(0, 200),
          diagram.position
        );
        animation.add(diagram, "opacity", 0, 1);
        animation.start();
      }
    }, // end init
    //以JSON格式显示图表的模型，用户可以编辑该模型
    save() {
      document.getElementById("mySavedModel").value =
        this.myDiagram.model.toJson();

      this.myDiagram.isModified = false;
    },
    load() {
      // var data={ class: "GraphLinksModel", "linkFromPortIdProperty": "fromPort", "linkToPortIdProperty": "toPort", "nodeDataArray": [ { "category": "dockingPoint", "text": "测试节点", "key": -1  }, { "category": "End", "text": "出库口", "key": -2  } ], "linkDataArray": [ { "from": -1, "fromText":'123',"to": -2  } ] };


      // this.myDiagram.model =go.Model.fromJson(data);
      // this.myDiagram.model = go.Model.fromJson(
      //   document.getElementById("mySavedModel").value
      // );
    },
    loadData(data) {
      this.myDiagram.model = go.Model.fromJson(data);
    },
    //通过打开一个新窗口来打印图表，该窗口中包含每页的图表内容的SVG图像
    printDiagram() {
      var svgWindow = window.open();
      if (!svgWindow) return; //无法打开新窗口
      var printSize = new go.Size(700, 960);
      var bnds = this.myDiagram.documentBounds;
      var x = bnds.x;
      var y = bnds.y;
      while (y < bnds.bottom) {
        while (x < bnds.right) {
          var svg = this.myDiagram.makeSVG({
            scale: 1.0,
            position: new go.Point(x, y),
            size: printSize,
          });
          svgWindow.document.body.appendChild(svg);
          x += printSize.width;
        }
        x = bnds.x;
        y += printSize.height;
      }
      setTimeout(function () {
        svgWindow.print();
      }, 1);
    },
  },
};
</script>

<style lang="scss" scoped>
.container {
  background-color: #f2f6fc;
  height: 100vh;

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
    color: #dcdfe6;
    background-color: #dcdfe6;

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
    color: #f56c6c;
    background-color: #f56c6c;

    .el-icon-close {
      display: none;
    }
  }

  .outCell {
    color: #e6a23c;
    background-color: #e6a23c;

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

body>.el-container {
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
