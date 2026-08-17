<template>
  <div class="container">
    <div id="tooltip" style=" opacity: 0.8;" class="tooltip">
      <el-card v-if="nowDeviceData0 != null && nowDeviceData0.code != null"
        style="  background: linear-gradient(to bottom, #555, #333);">
        <div slot="header" class="clearfix">
          <span style="color:white">设备信息</span>
        </div>
        <div style="color:white">
          <span>编码：</span>
          <span>{{ nowDeviceData0.code }}</span>
        </div>
        <div style="color:white">
          <span>名称：</span>
          <span>{{ nowDeviceData0.name }}</span>
        </div>
        <div style="color:white">
          <span>IP：</span>
          <span>{{ nowDeviceData0.ip }}</span>
        </div>
        <div style="color:white">
          <span>端口：</span>
          <span>{{ nowDeviceData0.port }}</span>
        </div>
        <div style="color:white">
          <span>状态：</span>
          <dict-tag :options="dict.type.is_online" :value="nowDeviceData0.isOnline" />
        </div>
      </el-card>
      <el-card v-if="nowPositionData0 != null && nowPositionData0.code != null"
        style="  background: linear-gradient(to bottom, #555, #333);">
        <div slot="header" class="clearfix">
          <span style="color:white">站台信息</span>
        </div>
        <div style="color:white">
          <span>编码：</span>
          <span>{{ nowPositionData0.code }}</span>
        </div>
        <div style="color:white">
          <span>名称：</span>
          <span>{{ nowPositionData0.name }}</span>
        </div>
        <div style="color:white">
          <span>状态：</span>
          <span>{{ nowPositionData0.invenState }}</span>
        </div>
        <div style="color:white">
          <span>信息：</span>
          <span>{{ nowPositionData0.memo }}</span>
        </div>
      </el-card>
    </div>
    <div style="width: 100%">
      <el-card style="margin: 10px">
        <el-form :inline="true" ref="ware" :model="wareInfo" class="demo-form-inline">
          <el-form-item label="仓库名称">
            <el-select v-model="wareCode" placeholder="请选择仓库">
              <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code + ''">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item style="margin-left: 10%">
            <el-button type="primary" @click="routerToUpdate()">修改模型</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-row>
        <el-col :span="18">
          <el-card style="margin: 1%">
            <div id="myDiagramDiv" style="
                flex-grow: 1;
                height: 750px;
                background-color: rgba(255, 255, 255, 0);
                position: relative;
                -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
                cursor: auto;
              "></div>
          </el-card>
        </el-col>

        <el-col v-if="nowPositionData!=null && nowPositionData.code!=null" :span="6">
          <el-card style="margin: 3%">
            <div slot="header" class="clearfix">
              <span>站台信息</span>
            </div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="编码">{{ nowPositionData.code }}</el-descriptions-item>
              <el-descriptions-item label="名称">{{ nowPositionData.name }}</el-descriptions-item>
              <el-descriptions-item label="类型">
                <dict-tag :options="dict.type.position_type" :value="nowPositionData.type" />
              </el-descriptions-item>
              <el-descriptions-item label="是否禁用">
                <el-select @change="updatePosition({
                            id:nowPositionData.positionId
                            ,code:nowPositionData.code
                            ,disableState:nowPositionData.disableState
                            })" v-model="nowPositionData.disableState" placeholder="">
                  <el-option style="color: #909399;" key=1 label="禁用" :value="1">
                  </el-option>
                  <el-option style="color:#67C23A;" key=0 label="可用" :value="0">
                  </el-option>
                </el-select>
              </el-descriptions-item>
              <el-descriptions-item label="站台产品" v-if="nowPositionData.wareCode ==='XL_WARE3'">
                <el-select @change="update(nowPositionData)" v-model="nowPositionData.modelCode" placeholder="">
                  <el-option v-for="item in itemInfos" :key="item.modelCode" :label="item.modelName"
                    :value="item.modelCode">
                  </el-option>
                </el-select>
              </el-descriptions-item>
              <el-descriptions-item v-if="nowPositionData.wareCode ==='XL_WARE3'" label="是否报警">
                <el-select @change="update(nowPositionData)" v-model="nowPositionData.state" placeholder="">
                  <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <el-card style="margin: 3%">
            <div slot="header" class="clearfix">
              <span>状态</span>
            </div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="任务状态">{{ nowPositionData.taskState }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
          <el-card style="margin: 3%" v-if="nowPositionData.wareCode ==='XL_WARE3'">
            <div slot="header" class="clearfix">
              <span>恢复机械手</span>
              <el-button @click="submitTaskInfo({fromCellCode:nowPositionData.code,toCellCode:nowPositionData.toCellCode})"
                style="float: right; padding: 3px 0" type="text">确 定</el-button>
            </div>
          </el-card>

          <el-card style="margin: 3%">
            <div slot="header" class="clearfix">
              <span>点对点搬运</span>
              <el-button
                @click="submitTaskInfo({fromCellCode:nowPositionData.code,
                toCellCode:nowPositionData.toCellCode,
                remark2:nowPositionData.modelCode,
                remark1:nowPositionData.remark1})"
                style="float: right; padding: 3px 0" type="text">确 定</el-button>
            </div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="起点">
                <el-select v-if="nowPositionData.wareCode === 'XL_WARE'" v-model="nowPositionData.code"
                  placeholder="请选择起点" clearable>
                  <el-option v-for="item in positionOptions" :key="item.code" :label="item.name" :value="item.code" />
                </el-select>
                <el-input v-else v-model="nowPositionData.code" placeholder="起点" />
              </el-descriptions-item>
              <el-descriptions-item label="终点">
                <el-select v-if="nowPositionData.wareCode === 'XL_WARE'" v-model="nowPositionData.toCellCode"
                  placeholder="请选择终点" clearable>
                  <el-option v-for="item in positionOptions" :key="item.code" :label="item.name" :value="item.code" />
                </el-select>
                <el-input v-else v-model="nowPositionData.toCellCode" placeholder="终点" />
              </el-descriptions-item>
              <!-- 【核心修改1】调用组件内的getModelName方法，替换全局函数 -->
              <el-descriptions-item label="产品类型" v-if="nowPositionData.wareCode ==='XL_WARE3'">
                <el-input :value="getModelName(nowPositionData.modelCode)" placeholder="产品类型" readonly />
              </el-descriptions-item>
              <el-descriptions-item label="任务类型">
                <el-select v-model="nowPositionData.remark1" placeholder="任务类型">
                  <el-option v-for="item in taskTypes" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { listPositionInfo, updatePositionInfo } from "@/api/wcs-base/PositionInfo";
import { listDeviceInfo } from "@/api/wcs-base/DeviceInfo";
import { listProductModel } from "@/api/wcs-xlPro/ProductModel";
import { getAgvPosition } from "@/api/wcs-xlPro/AgvTaskMsg";
import { updatePositionInfoExtendModel, getModelId } from "@/api/wcs-xlPro/PositionInfoExtend";
import go from "@/lib/js/go.js"
import request from "@/utils/request.js";
export default {
  name: "Index",
  dicts: [
    "com_type",
    "device_type",
    "s7_type",
    "del_flag",
    "is_online",
    "device_state",
    "position_state",
    "position_type",
  ],
  data() {
    return {
      aaaaa: 0,
      wareInfos: [],
      wareCode: null,
      wareInfo: {},
      devices: [],
      positions: [],
      activeName: "first",
      showBind: false,
      nowDeviceData: {},
      nowDeviceData0: {},
      nowPositionData: {},
      nowPositionData0: {},
      positionRecords: [],
      itemInfos: [], // 产品型号列表
      positionOptions: {},
      timer: null,
      cellStates: [
        { text: "空闲", color: "#909399" },
        { text: "禁用", color: "red" },
        { text: "有货", color: "#409EFF" },
      ],
      nowData: {},
      myDiagram: null,
      modelData: {},
      palletModel: [
        { category: "cell", name: "入库口", code: "" },
        { category: "End", name: "出库口", code: "" },
        { category: "dockingPoint", name: "接驳位置", code: "" },
        { category: "ExPort", name: "异常排除口", code: "" },
        { category: "line", name: "巷道", code: "" },
        { category: "check", name: "校验点", code: "" },
        { category: "Comment", name: "备注", code: "" },
      ],
      taskTypes: [
        { value: "JXB_MOVE", label: "机械臂上料任务" },
        { value: "JXB_MOVE2", label: "机械臂下料任务" },
        { value: "JXB_GZ_MOVE", label: "放盖子任务" },
        { value: "CQF06", label: "agv手动搬运任务" },
      ],
      states: [
        { value: 0, label: "无报警" },
        { value: 1, label: "报警中" },
      ],
      gridSize: { width: 50, height: 50 },
      loading: false, // 【修改2】定义loading初始值，修复未定义报错
    };
  },
  watch: {
    wareCode(newValue, oldValue) {
      if (newValue != null) {
        this.wareInfos.forEach((element) => {
          if (newValue == element.code) {
            this.createGo(newValue);
          }
        });
      }
    },
  },
  mounted() {
    this.init(null);
  },
  created() {
    this.aaaaa++;
    this.getWareInfos();
    this.getItemInfoList(); // 加载产品型号
    this.getPositionCodeList(); // 加载站台选项
    // 初始化定时器，移除重复的型号/站台请求，减少后端压力
    if (this.timer) clearInterval(this.timer);
    this.timer = setInterval(() => {
      if (this.wareCode == null) return;
      this.getAllDevices();
      this.getPositionsByWareCode();
      this.getPositionRecordByCode();
      this.getItemInfoList();
      this.getPositionCodeList();
    }, 3000);
  },
  beforeDestroy() {
    if (this.timer) clearInterval(this.timer);
  },
  methods: {
    // 【核心修改3】新增：组件内专用，根据modelCode匹配modelName，带全量容错
    getModelName(modelCode) {
      // 空值容错：编码为空/型号列表为空，返回空字符串
      if (!modelCode || !Array.isArray(this.itemInfos) || this.itemInfos.length === 0) {
        return "";
      }
      // 匹配对应的型号名称
      const matchItem = this.itemInfos.find(item => item.modelCode === modelCode);
      // 有匹配返回名称，无匹配返回空
      return matchItem ? matchItem.modelName : "";
    },
    // 手动任务
    submitTaskInfo(taskInfo) {
      var that = this;
      if (!taskInfo.fromCellCode) {
        this.$modal.msgError("无站台信息");
        return;
      }
      if (!taskInfo.toCellCode) {
        this.$modal.msgError("无目标站台信息");
        return;
      }
      taskInfo.wareCode = that.wareInfo.code;
      taskInfo.wareName = that.wareInfo.name;
      request({
        url: "/wcs-xlPro/PositionInfoExtend/jgxTask",
        method: "post",
        data: taskInfo,
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("保存成功");
          this.nowPositionData.toCellCode = null;
          this.nowPositionData.remark1 = null;
        } else {
          this.$modal.msgError(response.msg || "保存失败");
        }
      });
    },
    // 跳转修改页面
    routerToUpdate() {
      if (this.wareInfo.id != null) {
        this.$router.push({
          path: "/diagram2dUpdate",
          query: { code: this.wareInfo.code, backgroundImg: this.wareInfo.backgroundImg }
        });
      } else {
        this.$modal.msgError("未选择仓库");
      }
    },
    update(row) {
      updatePositionInfoExtendModel(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    // 获取所有的产品型号
    getItemInfoList() {
      this.loading = true;
      listProductModel().then(response => {
        this.loading = false;
        if (response && response.code === 200) {
          this.itemInfos = response.rows || []; // 兜底空数组
        }
      }).catch(error => {
        this.loading = false; // 异常也关闭loading
        console.error('获取产品型号列表失败：', error);
      });
    },
    // 获取站台编码列表
    getPositionCodeList() {
      this.loading = true;
      getAgvPosition().then(response => {
        this.loading = false;
        if (response && response.code === 200) {
          this.positionOptions = response.data || {}; // 兜底空对象
        }
      }).catch(error => {
        this.loading = false; // 【修改4】补全loading关闭
        console.error('获取站台列表失败：', error);
      });
    },
    // 获取该仓库所有设备信息
    getAllDevices() {
      var query = { wareCode: this.wareCode, pageNum: 1, pageSize: 999 };
      listDeviceInfo(query).then((response) => {
        if (response && response.code == 200) {
          this.devices = response.rows || [];
          this.showWarningInfo();
        }
      });
    },
    // 获取该仓库所有站台信息
    getPositionsByWareCode() {
      var query = { wareCode: this.wareCode, pageNum: 1, pageSize: 999 };
      listPositionInfo(query).then((response) => {
        if (response && response.code == 200) {
          this.positions = response.rows || [];
          this.updatePositionSource();
        }
      });
    },
    // 获取该仓库所有站台记录
    getPositionRecordByCode() {
      var that = this;
      var position = JSON.parse(JSON.stringify(that.nowPositionData));
      if (!position || !position.code) return; // 简化空值判断
      var query = { wareCode: that.wareCode, positionId: position.id, pageNum: 1, pageSize: 20 };
      request({
        url: "/wcs-base/PositionRecord/list",
        method: "get",
        params: query,
      }).then((response) => {
        if (response && response.code == 200) {
          that.positionRecords = response.rows || [];
        }
      }).catch(error => {
        console.error('获取站台记录失败：', error);
      });
    },
    // 根据编码获取站台信息
    getPositionInfoByCode(code) {
      return this.positions.find(pos => pos.code === code) || null; // 简化find写法
    },
    // 更新站台状态
    updatePosition(row) {
      updatePositionInfo(row).then((response) => {
        if (response && response.code == 200) {
          this.$modal.msgSuccess("修改成功");
        } else {
          this.$modal.msgError(response.msg || "修改失败");
        }
      });
    },
    // 更新站台图片资源
    updatePositionSource() {
      var that = this;
      if (!this.myDiagram) return;
      var model = this.myDiagram.model;
      this.myDiagram.model.nodeDataArray.forEach((node) => {
        if (!node.positionCode) return;
        var pos = that.getPositionInfoByCode(node.positionCode);
        if (!pos) return;
        var imanName = "/img/source/ssx" + (pos.invenState == 1 ? "_has_pallet" : "_no_pallet") + ".png";
        model.setDataProperty(node, "source", imanName);
      });
    },
    // 显示设备故障图片
    showWarningInfo() {
      var that = this;
      if (!this.myDiagram) return;
      var model = this.myDiagram.model;
      this.myDiagram.model.nodeDataArray.forEach((node) => {
        var device = that.getDeviceInfoByCode(node.deviceCode);
        if (!device || device.state !== 0 && device.state !== 2) return;
        model.setDataProperty(node, "source", device.state == 0 ? "/img/source/lixian.png" : "/img/source/warning.png");
      });
    },
    // 【修改5】修复致命bug：赋值=改为全等===，否则会导致设备信息赋值错误
    getDeviceInfoByCode(code) {
      if (!code) return null;
      return this.devices.find(device => device.code === code) || null; // 简化find写法
    },
    // 获取设备详细信息
    getDeviceMsg(deviceCode, type) {
      if (!deviceCode) {
        type === 0 ? (this.nowDeviceData0 = {}) : (this.nowDeviceData = {});
        return;
      }
      var device = this.getDeviceInfoByCode(deviceCode);
      if (device) {
        type === 0 ? (this.nowDeviceData0 = device) : (this.nowDeviceData = device);
      }
    },
    // 获取站台详细信息
    getPositionMsg(positionCode, type) {
      var that = this;
      if (!positionCode) {
        type === 0 ? (this.nowPositionData0 = {}) : (this.nowPositionData = {});
        return;
      }
      var position = this.positions.find(pos => pos.code === positionCode);
      if (!position) return;
      // 鼠标悬停/点击赋值站台信息
      if (type === 0) {
        that.nowPositionData0 = position;
      } else if (type === 1) {
        that.nowPositionData = position;
        // XL_WARE3仓库获取型号编码
        if (position.wareCode === "XL_WARE3") {
          getModelId(position.code).then((response) => {
            if (response && response.code == 200 && response.data?.modelCode) {
              // 【核心修改6】用$set解决响应式丢失，赋值后页面实时更新
              this.$set(this.nowPositionData, 'modelCode', response.data.modelCode);
            }
          });
        }
        that.positionRecords = [];
      }
    },
    // 根据编码获取仓库信息
    getWareInfoByCode(wareCode) {
      return this.wareInfos.find(element => element.code === wareCode) || null;
    },
    // 创建GO图
    createGo(wareCode) {
      var that = this;
      var wareInfo = that.getWareInfoByCode(wareCode);
      if (!wareInfo) return;
      this.wareInfo = wareInfo;
      var modelData = wareInfo.monitorData ? JSON.parse(wareInfo.monitorData) : {};
      this.loadData(modelData);
      this.updateBackgroundImg(this.wareInfo.backgroundImg);
    },
    // 更新背景图
    updateBackgroundImg(newImageUrl) {
      if (!this.myDiagram || !newImageUrl) return;
      var that = this;
      var $ = go.GraphObject.make;
      that.myDiagram.add(
        $(go.Part,
          {
            name: "backgroundPart",
            layerName: "Background", position: new go.Point(0, 0),
            selectable: false, pickable: false
          },
          $(go.Picture, newImageUrl)
        ));
    },
    // 获取所有仓库信息
    getWareInfos() {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/list",
        method: "get",
        params: { isDelete: 0 },
      }).then((response) => {
        if (response && response.code == 200) {
          that.wareInfos = response.rows || [];
          if (that.wareInfos.length > 0) {
            that.wareCode = that.wareInfos[0].code;
          }
        } else {
          that.$modal.msgError(response.msg || "获取仓库列表失败");
        }
      }).catch(error => {
        console.error('获取仓库列表失败：', error);
      });
    },
    // 初始化GO图
    init() {
      var that = this;
      var $ = go.GraphObject.make;
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.height);
      this.myDiagram = $(
        go.Diagram,
        "myDiagramDiv",
        {
          "resizingTool.isGridSnapEnabled": true,
          LinkDrawn: showLinkLabel,
          LinkRelinked: showLinkLabel,
          "undoManager.isEnabled": true,
          isReadOnly: true,
        }
      );
      var myDiagram = this.myDiagram;
      this.myDiagram.addChangedListener(function (event) {
        var change = event.change;
      });
      // 节点样式
      function nodeStyle() {
        return [
          new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
          { locationSpot: go.Spot.TopLeft },
        ];
      }
      // 文字样式
      function textStyle() {
        return { font: "bold 8pt Lato, Helvetica, Arial, sans-serif", stroke: "#303133" };
      }
      // 常规节点模板
      myDiagram.nodeTemplate = $(
        go.Node,
        {
          locationSpot: new go.Spot(0, 0, CellSize.width, CellSize.height),
        },
        new go.Binding("position", "pos", go.Point.parse).makeTwoWay(go.Point.stringify),
        $(
          go.Picture,
          {
            margin: 0,
            width: that.gridSize.width,
            height: that.gridSize.height,
            background: "white",
          },
          new go.Binding("source")
        ),
        {
          click: function (e, node) {
            that.nowData = node.data;
            that.getDeviceMsg(node.data.deviceCode, 1);
            that.getPositionMsg(node.data.positionCode, 1);
            that.showBind = true;
          },
          cursor: "pointer",
        },
        {
          mouseEnter: function (e, node) {
            that.nowData = node.data;
            that.getDeviceMsg(node.data.deviceCode, 0);
            that.getPositionMsg(node.data.positionCode, 0);
            that.showBind = true;
            var tooltip = document.getElementById('tooltip');
            tooltip.style.display = 'block';
            const modelPosition = node.location;
            const screenPosition = that.myDiagram.transformDocToView(modelPosition);
            tooltip.style.transform = 'translate(' + (screenPosition.x + 50) + 'px, ' + screenPosition.y + 'px)'; // 修复坐标属性S/P→x/y
          },
          cursor: "pointer",
        },
        {
          mouseLeave: function (e, node) {
            document.getElementById('tooltip').style.display = 'none';
          },
          cursor: "pointer",
        }
      );
      // 单元格节点模板
      this.myDiagram.nodeTemplateMap.add(
        "cell",
        $(
          go.Node,
          nodeStyle(),
          $(
            go.Panel,
            "Spot",
            $(
              go.Shape,
              "Rectangle",
              { desiredSize: CellSize, strokeWidth: 1 },
              new go.Binding("fill", "fillColor"),
              new go.Binding("stroke", "borderColor")
            ),
            {
              click: function (e, obj) {
                var node = obj.part;
                that.nowData = node.data;
              },
            },
            $(go.TextBlock, "", textStyle(), new go.Binding("text", "text"))
          )
        )
      );
      // 链接模板
      this.myDiagram.linkTemplate = $(
        go.Link,
        {
          routing: go.Link.AvoidsNodes,
          curve: go.Link.JumpOver,
          corner: 5,
          toShortLength: 4,
          relinkableFrom: true,
          relinkableTo: true,
          reshapable: true,
          resegmentable: true,
          mouseEnter: function (e, link) {
            link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)";
          },
          mouseLeave: function (e, link) {
            link.findObject("HIGHLIGHT").stroke = "transparent";
          },
          selectionAdorned: false,
          click: function (e, obj) {
            var node = obj.part;
            that.nowData = node.data;
          },
        },
        new go.Binding("points").makeTwoWay(),
        $(
          go.Shape,
          { isPanelMain: true, strokeWidth: 8, stroke: "transparent", name: "HIGHLIGHT" }
        ),
        $(
          go.Shape,
          { isPanelMain: true, stroke: "gray", strokeWidth: 2 },
          new go.Binding("stroke", "isSelected", function (sel) {
            return sel ? "dodgerblue" : "gray";
          }).ofObject()
        ),
        $(go.Shape, { toArrow: "standard", strokeWidth: 0, fill: "gray" }),
        $(
          go.Panel,
          "Auto",
          { visible: false, name: "LABEL", segmentIndex: 2, segmentFraction: 0.5 },
          new go.Binding("visible", "visible").makeTwoWay(),
          $(go.Shape, "RoundedRectangle", { fill: "#F8F8F8", strokeWidth: 0 }),
          $(
            go.TextBlock,
            "Yes",
            { textAlign: "center", font: "10pt helvetica, arial, sans-serif", stroke: "#333333", editable: true },
            new go.Binding("text").makeTwoWay()
          )
        )
      );
      // 链接标签显示逻辑
      function showLinkLabel(e) {
        var label = e.subject.findObject("LABEL");
        if (label !== null) label.visible = e.subject.fromNode.data.category === "Conditional";
      }
      // 临时链接路由
      this.myDiagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
      this.myDiagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;
      this.load();
    },
    // 保存模型
    save() {
      document.getElementById("mySavedModel").value = this.myDiagram.model.toJson();
      this.myDiagram.isModified = false;
    },
    // 添加节点
    addNode() {
      this.myDiagram.model.addNodeData({
        category: "cell",
        text: "X",
        key: -1,
        loc: "40  40",
        fillColor: "red",
        borderColor: "#FFFFFF",
        group: -1,
      });
    },
    // 添加单元格
    addCell() {
      this.addNode(); // 复用addNode逻辑
    },
    // 加载模型
    load() {
      var data = {
        class: "GraphLinksModel",
        linkFromPortIdProperty: "fromPort",
        linkToPortIdProperty: "toPort",
        nodeDataArray: [],
        linkDataArray: [],
      };
      this.myDiagram.model = go.Model.fromJson(data);
    },
    // 加载模型数据
    loadData(data) {
      var that = this;
      if (!this.myDiagram) return;
      this.myDiagram.model = go.Model.fromJson(data);
      setTimeout(function () {
        that.myDiagram.initialAutoScale = go.Diagram.Uniform;
        that.myDiagram.zoomToFit();
      }, 1000);
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

.el-form-item {
  margin-bottom: 10px;
}

.cellState {
  .left {
    float: left;
    width: 20px;
    height: 20px;
  }

  .right {
    margin-left: 5px;
    float: left;
    width: 70px;
    height: 20px;
    line-height: 20px;
  }
}

.tooltip {
  display: none;
  position: absolute;
  padding: 5px 10px;
  background-color: #333;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1000;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3);
  background: linear-gradient(to bottom, #555, #333);
  transition: opacity 0.3s ease-in-out;
}

.tooltip::after {
  content: '';
  position: absolute;
  top: -5px;
  left: 10px;
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-bottom: 5px solid #333;
}
</style>