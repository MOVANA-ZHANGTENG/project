<template>
  <div class="container">
    <div id="tooltip"  style=" opacity: 0.8;" class="tooltip">

      <!-- <div class="tooltip-text">{{ nowData }}</div> -->
        <el-card v-if="nowDeviceData0!=null && nowDeviceData0.code!=null" style="  background: linear-gradient(to bottom, #555, #333);">
          <div slot="header" class="clearfix">
            <span style="color:white">设备信息</span>
          </div>
          <div  style="color:white">
            <span>编码：</span>
            <span>{{
                        nowDeviceData0.code
                      }}</span>
          </div>
          <div  style="color:white">
            <span>名称：</span>
            <span>{{
                        nowDeviceData0.name
                      }}</span>
          </div>
          <div  style="color:white">
            <span>IP：</span>
            <span>{{
                        nowDeviceData0.ip
                      }}</span>
          </div>
          <div  style="color:white">
            <span>端口：</span>
            <span>{{
                        nowDeviceData0.port
                      }}</span>
          </div>

          <div  style="color:white">
            <span>状态：</span>
            <dict-tag
                          :options="dict.type.is_online"
                          :value="nowDeviceData0.isOnline"
                        />
          </div>
        </el-card>
        <el-card  v-if="nowPositionData0!=null && nowPositionData0.code!=null"  style="  background: linear-gradient(to bottom, #555, #333);">
          <div slot="header" class="clearfix">
            <span style="color:white">站台信息</span>
          </div>
          <div  style="color:white">
            <span>编码：</span>
            <span>{{
                        nowPositionData0.code
                      }}</span>
          </div>

          <div  style="color:white">
            <span>名称</span>
            <span>{{
                        nowPositionData0.name
                      }}</span>
          </div>
          <div  style="color:white">
            <span>状态</span>
            <span>{{
                        nowPositionData0.state
                      }}</span>
          </div>



              </el-card>
    </div>
    <div style="width: 100%">
      <el-card style="margin: 10px">
        <el-form
          :inline="true"
          ref="ware"
          :model="wareInfo"
          class="demo-form-inline"
        >
          <el-form-item label="仓库名称">
            <el-select v-model="wareCode" placeholder="请选择仓库">
              <el-option
                v-for="item in wareInfos"
                :key="item.code"
                :label="item.name"
                :value="item.code + ''"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item style="margin-left: 10%">
            <el-button type="primary" @click="routerToUpdate()"
              >修改模型</el-button
            >
          </el-form-item>
        </el-form>
      </el-card>

      <el-row>
        <el-col :span="18">
          <el-card style="margin: 1%">
            <div
              id="myDiagramDiv"
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

        <el-col  v-if=" nowPositionData!=null &&  nowPositionData.code!=null && nowPositionData.type!=3"  :span="6">
          <el-card style="margin: 3%">
            <div slot="header" class="clearfix">
              <span>站台信息</span>
               
            </div>
            <el-descriptions :column="1" border>
                      <el-descriptions-item label="编码">{{
                        nowPositionData.code
                      }}</el-descriptions-item>
                      <el-descriptions-item label="名称">{{
                        nowPositionData.name
                      }}</el-descriptions-item>

                      <el-descriptions-item v-if="nowPositionData.type==1" label="电视机">
                        <el-input  @change="update(nowPositionData)"  v-model="nowPositionData.codeGroup" placeholder="电视机" />
                        
                      </el-descriptions-item>
                      <el-descriptions-item label="备用站台编码">
                        <el-input  @change="update(nowPositionData)"  v-model="nowPositionData.backCode" placeholder="backCode" />
                        
                      </el-descriptions-item>
                       <el-descriptions-item label="产线">
                        <el-select @change="update(nowPositionData)" v-model="nowPositionData.proLineCode" placeholder="">
                          <el-option
                            v-for="item in lineInfos"
                            :key="item.code"
                            :label="item.name"
                            :value="item.code">
                          </el-option>
                          <!-- <el-option
                           style="color: #67C23A;"
                            key="000"
                            label="共用产线"
                            value="000">
                          </el-option> -->
                        </el-select>
                        <el-button type="text" @click="lineItemUpdateVisible = true;
                        
                        lineItemUpdate.lineCode =nowPositionData.proLineCode;
                        lineItemUpdate.itemTypeCode =nowPositionData.itemTypeCode;
                        ">一键换型</el-button>
                      
                      </el-descriptions-item>

                      <el-descriptions-item label="是否禁用">
                        <el-select @change="updatePosition({
                            id:nowPositionData.positionId
                            ,code:nowPositionData.code
                            ,disableState:nowPositionData.disableState
                            })" v-model="nowPositionData.disableState" placeholder="">
                              <el-option
                                style="color: #909399;"
                                key=1
                                label="禁用"
                                :value="1">
                              </el-option>

                              <el-option
                              style="color:#67C23A;"
                                key=0
                                label="可用"
                                :value="0">
                              </el-option>
                            </el-select>
                        
                      </el-descriptions-item>

                     

                     

                      <el-descriptions-item label="光电传感器">
                        
                          <!-- {{ nowPositionData.wifiModeIsConnect }} -->
                          <span  style="color:#67C23A" v-if="nowPositionData.wifiModeIsConnect==1">{{nowPositionData.wifiModeIp}}连接</span>
                            <span  style="color:red" v-if="nowPositionData.wifiModeIsConnect==0">{{nowPositionData.wifiModeIp}}断开</span>
                        
                        
                      </el-descriptions-item>

                      
                     
                      <el-descriptions-item label="物料类型">
                        <el-select @change="update(nowPositionData)" v-model="nowPositionData.itemTypeCode" placeholder="">
                          <el-option
                            v-for="item in itemTypes"
                            :key="item.code"
                            :label="item.name"
                            :value="item.code">
                          </el-option>
                        </el-select>
                        
                      </el-descriptions-item>
                      <el-descriptions-item label="站台物料">
                        <el-select @change="update(nowPositionData)" v-model="nowPositionData.itemCode" placeholder="">
                          <el-option
                          :disabled="item.itemTypeCode !=nowPositionData.itemTypeCode"
                            v-for="item in itemInfos"
                            :key="item.itemCode"
                            :label="item.itemName"
                            :value="item.itemCode">
                          </el-option>
                        </el-select>
                        
                      </el-descriptions-item>

                      <el-descriptions-item label="托盘号" >
                        {{
                        nowPositionData.palletCode
                      }}
                      </el-descriptions-item>

                      <el-descriptions-item label="物料检测">
                        <!-- {{ nowPositionData }} -->
                        <!-- {{ nowPositionData.wifiModeIsConnect }} -->
                        <span  style="color:#67C23A" v-if="nowPositionData.isOk==1">成功</span>
                        <span  style="color:red" v-if="nowPositionData.isOk==0">{{ nowPositionData.memo }}</span>
                      
                      
                    </el-descriptions-item>

               
                    </el-descriptions>
          </el-card>

          <el-card style="margin: 3%">
            <div slot="header" class="clearfix">
              <span>测试</span>
              <!-- <el-button  @click="submitTaskInfo({fromCellCode:nowPositionData.code,toCellCode:nowPositionData.toCellCode})" style="float: right; padding: 3px 0" type="text">确 定</el-button> -->
            </div>
        
            <el-descriptions :column="1" border>
              <el-descriptions-item label="光电连接（测试使用）">
                        <el-select @change="update(nowPositionData)" v-model="nowPositionData.wifiModeIsConnect" placeholder="">
                              <el-option
                                style="color: #909399;"
                                key=0
                                label="断开"
                                :value="0">
                              </el-option>

                              <el-option
                              style="color:#67C23A;"
                                key=1
                                label="连接"
                                :value="1">
                              </el-option>
                            </el-select>
                        
                      </el-descriptions-item>
                      <el-descriptions-item label="是否有托盘（测试使用）">
                        <el-select @change="updatePosition({
                            id:nowPositionData.positionId
                            ,code:nowPositionData.code
                            ,invenState:nowPositionData.invenState
                            })" v-model="nowPositionData.invenState" placeholder="">
                              <el-option
                                style="color: #67C23A;"
                                key=1
                                label="有"
                                :value="1">
                              </el-option>

                              <el-option
                              style="color:#909399;"
                                key=0
                                label="无"
                                :value="0">
                              </el-option>
                            </el-select>
                        
                      </el-descriptions-item>
                      
                
              
              </el-descriptions>
                    
          </el-card>
           
          <el-card style="margin: 3%">
            <div slot="header" class="clearfix">
              <span>点对点搬运</span>
              <el-button  @click="submitTaskInfo({fromCellCode:nowPositionData.code,toCellCode:nowPositionData.toCellCode})" style="float: right; padding: 3px 0" type="text">确 定</el-button>
            </div>
        
            <el-descriptions :column="1" border>
                <el-descriptions-item label="起点站台">
                  <el-input v-model="nowPositionData.code" placeholder="目标站台" />
                </el-descriptions-item>
                <el-descriptions-item label="目标站台">
                  <el-input v-model="nowPositionData.toCellCode" placeholder="目标站台" />
                </el-descriptions-item>
              
              </el-descriptions>
                    
          </el-card>
        </el-col>
      <el-col v-else :span="6">
        <el-card style="margin: 3%">
            <div slot="header" class="clearfix">
              <span>产线罐体</span>
               
            </div>
            <el-descriptions :column="1" border>
                      <el-descriptions-item label="原料名称">{{
                        nowPositionData.materialName
                      }}</el-descriptions-item>
                      <el-descriptions-item label="原料id">{{
                        nowPositionData.materialId
                      }}</el-descriptions-item>
                      <el-descriptions-item label="余量">{{
                        nowPositionData.last
                      }}</el-descriptions-item>
                    

                      <el-descriptions-item label="是否禁用">
                        <el-select @change="updatePosition({
                            id:nowPositionData.positionId
                            ,code:nowPositionData.code
                            ,disableState:nowPositionData.disableState
                            })" v-model="nowPositionData.disableState" placeholder="">
                              <el-option
                                style="color: #909399;"
                                key=1
                                label="禁用"
                                :value="1">
                              </el-option>

                              <el-option
                              style="color:#67C23A;"
                                key=0
                                label="可用"
                                :value="0">
                              </el-option>
                            </el-select>
                        
                      </el-descriptions-item>


               
                    </el-descriptions>
          </el-card>
      </el-col>
      </el-row>

     

      <el-dialog title="一键换型" :visible.sync="lineItemUpdateVisible">
        <el-form :model="lineItemUpdate">
          <el-form-item label="产线" :label-width="formLabelWidth">
            <el-select :disabled=true @change="update(nowPositionData)" v-model="lineItemUpdate.lineCode" placeholder="">
                          <el-option
                            v-for="item in lineInfos"
                            :key="item.code"
                            :label="item.name"
                            :value="item.code">
                          </el-option>
                        </el-select>
          </el-form-item>
          <el-form-item label="物料种类" :label-width="formLabelWidth">
            <el-select  :disabled=true  @change="update(nowPositionData)" v-model="lineItemUpdate.itemTypeCode" placeholder="">
                          <el-option
                            v-for="item in itemTypes"
                            :key="item.code"
                            :label="item.name"
                            :value="item.code">
                          </el-option>
                        </el-select>
          </el-form-item>
          <el-form-item label="换型物料" :label-width="formLabelWidth">
            <el-select v-model="lineItemUpdate.itemCode" placeholder="">
                          <el-option
                          v-if="item.itemTypeCode ==lineItemUpdate.itemTypeCode"
                            v-for="item in itemInfos"
                            :key="item.itemCode"
                            :label="item.itemName"
                            :value="item.itemCode">
                          </el-option>
                        </el-select>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="lineItemUpdateVisible = false">取 消</el-button>
          <el-button type="primary" @click="lineItemUpdateConfirm">确 定</el-button>
        </div>
      </el-dialog>
    </div>


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
import { listDeviceInfo } from "@/api/wcs-base/DeviceInfo";
import Cookies from "js-cookie";
import { listItemType, getItemType, delItemType, addItemType, updateItemType } from "@/api/wcs-base/ItemType";
import { listProLine, getProLine, delProLine, addProLine, updateProLine } from "@/api/wcs-base/ProLine";
import { listItemInfo, getItemInfo, delItemInfo, addItemInfo, updateItemInfo } from "@/api/wcs-base/ItemInfo";
import { listPositionInfo, getPositionInfo, delPositionInfo, addPositionInfo, updatePositionInfo } from "@/api/wcs-base/PositionInfo";
import { listProPositionContent, getProPositionContent, delProPositionContent, addProPositionContent, updateProPositionContent } from "@/api/wcs-base/ProPositionContent";
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
  ],

  data() {
    return {
      formLabelWidth:"150px",
      aaaaa:0,
      wareInfos: [],
      wareCode: null,
      wareInfo: {},
      itemInfos:[],
      itemTypes:[],
      lineInfos:[],
      lineItemUpdate:{},
      lineItemUpdateVisible:false,
      //设备列表
      devices: [],
      //站台列表
      positions: [],
      //设备绑定还是站台绑定
      activeName: "first",
      //显示绑定卡片
      showBind: false,
      //当前设备信息
      nowDeviceData: {},
      nowDeviceData0: {},
      //当前站台信息
      nowPositionData: {},
      nowPositionData0: {},
      //定时器
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
      //网格尺寸
      gridSize: {
        width: 50,
        height: 50,
      },
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
   // this.init();
  },
  created() {
    this.aaaaa++;
    console.info(this.aaaaa);
    this.getWareInfos();
    this.getItemInfoList();
    this.getLineInfoList();
    this.getItemTypeList();


    if (this.timer) {
      //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
    this.timer = setInterval(() => {
      if (this.wareCode == null) {
        return;
      }
     // this.getAllDevices();
      
      this.getPositionContentsByWareCode();
    }, 3000);
  },
  beforeDestroy() {
    if (this.timer) {
      //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
  },

  methods: {
    
    //跳转修改页面
    routerToUpdate() {
      debugger
      if (this.wareInfo.id != null) {
        this.$router.push({
          path: "/diagram2dUpdate",
          query:{code:this.wareInfo.code,backgroundImg:this.wareInfo.backgroundImg}  
        });
      } else {
        this.$modal.msgError("未选择仓库");
      }
    },

    lineItemUpdateConfirm(){
      var that=this;
      if(that.lineItemUpdate.lineCode==null ){
        this.$modal.msgError(  "无产线信息");
        return;
      }
      if(that.lineItemUpdate.itemTypeCode==null  ){
        this.$modal.msgError(  "无物料类型信息");
        return;
      }
      if(that.lineItemUpdate.itemCode==null  ){
        this.$modal.msgError(  "无物料信息");
        return;
      } 
      request({
        url: "/wcs-base/ProPositionContent/updateItemByLine",
        method: "get",
        params: that.lineItemUpdate,
      }).then((response) => {
        if (response.code == 200) {
          that.lineItemUpdateVisible=false,
          that.nowPositionData.itemCode = that.lineItemUpdate.itemCode;
       //   that.getPositionMsg(that.nowPositionData.code,1);
          this.$modal.msgSuccess("保存成功");
        } else {
          this.$modal.msgError(response.msg || "保存失败");
        }
      });
    },
    submitTaskInfo(taskInfo){
      var that=this;
      if(taskInfo.fromCellCode==null ){
        this.$modal.msgError(  "无站台信息");
        return;
      }
      // if(taskInfo.toCellCode==null ){
      //   this.$modal.msgError(  "无目标站台信息");
      //   return;
      // }
  
      taskInfo.wareCode=that.wareCode;
      taskInfo.wareName=that.wareCode;
      request({
        url: "/wcs-task/na/TaskInfo",
        method: "post",
        data: taskInfo,
      }).then((response) => {
        if (response.code == 200) {
           
          this.$modal.msgSuccess("保存成功");
        } else {
          this.$modal.msgError(response.msg || "保存失败");
        }
      });
    },
    update(row) {
      updateProPositionContent(row).then((response) => {
        console.log("itemCode:"+row.itemCode)
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },

    updatePosition(row) {
      updatePositionInfo(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    getItemInfoList() {
      this.loading = true;
      listItemInfo({ pageNum: 1,
        pageSize: 999}).then(response => {
          if(response.code==200){
            this.itemInfos = response.rows; 
          } 
      });
    },

    getItemTypeList() {
      this.loading = true;
      listItemType({ pageNum: 1,
        pageSize: 999}).then(response => {
          if(response.code==200){
            this.itemTypes = response.rows; 
          } 
      });
    },
    getLineInfoList() {
      this.loading = true;
      listProLine({ pageNum: 1,
        pageSize: 999}).then(response => {
          if(response.code==200){
            this.lineInfos = response.rows; 
          } 
      });
    },
    //获取该仓库所有设备信息
    // getAllDevices() {
    //   var query = {};
    //   query.wareCode = this.wareCode;
    //   listDeviceInfo(query).then((response) => {
    //     if (response.code == 200) {
    //       this.devices = response.rows;
    //       this.showWarningInfo();
    //     }
    //   });
    // },
    //获取该仓库所有站台信息
    getPositionsByWareCode() {
      var query = {};
      query.wareCode = this.wareCode;
      listPositionInfo(query).then((response) => {
        if (response.code == 200) {
          this.positions = response.rows;
        }
      });
    },

      //获取该仓库所有站台信息
    getPositionContentsByWareCode() {
      var that = this;
      request({
        url: "/wcs-base/ProPositionContent/list",
        method: "get",
        params: { pageNum: 1,
          pageSize: 999},
      }).then((response) => {
        if (response.code == 200) {
          this.positions = response.rows;
          this.showWarningInfo();
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },

    getPositionInfoByCode(code){
      for(var i=0;i<this.positions.length;i++){
          var pos = this.positions[i];
          if(pos.code == code){
            return pos;
          }
       
      }

    },
    //有故障显示故障图片
    showWarningInfo() {
      var that=this; 
      if(this.myDiagram==null){
          return;
      }
      var nodes = this.myDiagram.model.nodeDataArray;
      var model = this.myDiagram.model;
      nodes.forEach((node) => {
        var pos=that.getPositionInfoByCode(node.positionCode);
        if(pos==null){
          model.setDataProperty(node, "source", node.source);
          return;
        }
        var imanName = "/img/position";
        if(pos.invenState==1){
          imanName+="_inven1";
        }else{
          imanName+="_inven0";
        }

        
        if(pos.disableState==1){
          imanName+="_dis1";
        };

        if(pos.taskState>0.9){
          imanName+="_task1";
        };
        imanName+=".png";
        console.log(imanName);
        model.setDataProperty(node, "source", imanName); 
          
        });  
    },
    getDeviceInfoByCode(code){
      var devices=this.devices;
      for (let index = 0; index < devices.length; index++) {
        const device = devices[index];
        if(device.code == code){
            return device;
        }
        
      }

    },
    //获取设备详细信息
    getDeviceMsg(deviceCode,type) {
      if (deviceCode == null) {
        //鼠标悬停
        if(type==0){
          this.nowDeviceData0 = {};
        }
        //点击
        else if(type==1){
          this.nowDeviceData = {};
        }

        return;
      }
      var device = this.getDeviceInfoByCode(deviceCode);
      console.debug(device);
      if (device.code == deviceCode) {
          //鼠标悬停
          if(type==0){
            this.nowDeviceData0 = device;
          }
          //点击
          else if(type==1){
            this.nowDeviceData = device;
          }

        }
    },
    //获取站台详细信息
    getPositionMsg(positionCode,type) {
      var that=this;
      if (positionCode == null) {
         //鼠标悬停
         if(type==0){
          that.nowPositionData0 = {};
        }
        //点击
        else if(type==1){
          that.nowPositionData = {};
        }

        return;
      }
      for (let index = 0; index <  that.positions.length; index++) {
        const position = that.positions[index];
        if (position.code == positionCode) {
           //鼠标悬停
          if(type==0){
          that.nowPositionData0 = position;
          return;
          }
          //点击
          else if(type==1){
            that.nowPositionData = position;
            console.info(that.nowPositionData);
            that.$forceUpdate();
            return;
          }
        }
      }
      
    },

     //获取所有仓库信息列表
     getPositionContent() {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/list",
        method: "get",
        params: { isDelete: 0 },
      }).then((response) => {
        if (response.code == 200) {
          that.wareInfos = response.rows;
          that.getWareInfoMsg(that.wareInfos[0].id);
          that.wareCode = that.wareInfos[0].code;
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },
    getWareInfoByCode(wareCode){
      for (let index = 0; index < this.wareInfos.length; index++) {
        const element = this.wareInfos[index];
        if(element.code == wareCode){
            return element;
        } 
      }

    }, 

    createGo(wareCode){
      var that=this;
      var wareInfo = that.getWareInfoByCode(wareCode);
      that.wareInfo=wareInfo;
      if(that.myDiagram!=null && that.myDiagram["wareCode"]==wareInfo.code){
        return;

      }
      if (that.myDiagram) {
        // 清理现有的Diagram
        that.myDiagram.div = null;
        
      } 
      console.log("wareInfo.code",wareInfo.code);
      console.log("wareInfo.backgroundImg",wareInfo.backgroundImg);
      that.init(wareInfo.backgroundImg);
      setTimeout(
            function(){ 
              that.myDiagram["wareCode"]=wareInfo.wareCode;
              that.myDiagram.initialAutoScale = go.Diagram.Uniform;
              that.myDiagram.zoomToFit();
              that.getWareInfoMsg(wareInfo.id);
            },1000
          ); 
    },
    //获取仓库的详细模型数据
    getWareInfoMsg(id) {
      var that = this;
      getWareInfo(id).then((response) => {
        if (response.code == 200) {
          this.wareInfo = response.data;
          var modelData;
          if (this.wareInfo.monitorData == null) {
            modelData = {};
          } else {
           
            modelData = JSON.parse(this.wareInfo.monitorData);
          }
          this.loadData(modelData);
        } else {
          this.wareInfo = {};
          this.$modal.msgError(response.msg);
        }
      });
    },
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
          that.getWareInfoMsg(that.wareInfos[0].id);
          that.wareCode = that.wareInfos[0].code;
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },
    init(backgroundImg) {
      var that = this;
      var $ = go.GraphObject.make; // 定义模板时的简洁性
      var CellSize = new go.Size(this.gridSize.width, this.gridSize.height); 
      this.myDiagram = $(
        go.Diagram,
        "myDiagramDiv", //必须命名或引用DIV HTML元素
        { 
          //缩放捕捉
          "resizingTool.isGridSnapEnabled": true,
         
          "undoManager.isEnabled": true, //启用撤消和重做
          isReadOnly: true, //只读
        }
      );
      if(backgroundImg!=null){
          this.myDiagram.add(
          $(go.Part,  // this Part is not bound to any model data
            { layerName: "Background", position: new go.Point(0, 0),
              selectable: false, pickable: false },
            $(go.Picture, backgroundImg)
          )); 
      }
     
      var myDiagram = this.myDiagram;
      this.myDiagram.addChangedListener(function (event) {
        // 当模型数据发生变化时，这个函数会被调用
        // 使用event.change可以获取具体的变化信息
        var change = event.change;

        // 如果需要获取整个模型的数据，可以使用model.toJson()
        // var modelData = myDiagram.model.toJson();
        // that.modelData = modelData;
        // that.wareInfo.modelData = modelData;
        // console.log("模型数据变化:", change);
        // console.log("最新模型数据:", modelData);
      });
      // 节点模板的辅助定义
      function nodeStyle() {
        return [
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
          font: "bold 8pt Lato, Helvetica, Arial, sans-serif",
          stroke: "#303133",
        };
      }

      // 定义常规节点的节点模板
      myDiagram.nodeTemplate = $(
        go.Node,
        {
          resizable: true,
          resizeObjectName: "SHAPE",
          // 因为gridSnapCellSpot是中心，所以偏移节点的位置
          locationSpot: new go.Spot(0, 0, CellSize.width, CellSize.height),
          // 提供有关将任何东西掉落到“物品”上的视觉警告
      
        },
        // 始终保存/加载节点左上角的点，而不是位置
        new go.Binding("position", "pos", go.Point.parse).makeTwoWay(
          go.Point.stringify
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
        /**click,//鼠标单击事件
          doubleClick,//双击事件
          mouseEnter,// 鼠标移入事件
          mouseLeave,//鼠标移出事件
          mouseHover,//鼠标悬停事件
          selectionChanged,//节点的选中和取消选中事件
          contextMenu,//鼠标右键点击事件  */
       {
        click: function (e, node) {
           that.nowData = node.data;
          //  that.getDeviceMsg(node.data.deviceCode,1);
            that.getPositionMsg(node.data.positionCode,1);
           // that.showBind = true;

          },
         // cursor: "pointer", //改变鼠标样式变成小手
        },
        // {
        //   mouseEnter: function (e, node) {
        //     that.nowData = node.data;
            
        //     that.getDeviceMsg(node.data.deviceCode,0);
        //     that.getPositionMsg(node.data.positionCode,0);
        //     that.showBind = true;
        //     var tooltip = document.getElementById('tooltip');
        //     tooltip.style.display = 'block';
        //     const modelPosition = node.location;
        //     // 将模型坐标转换为屏幕坐标
        //     const screenPosition = that.myDiagram.transformDocToView(modelPosition);
        //     console.info(screenPosition);
        //     tooltip.style.transform = 'translate('+(screenPosition.S+50)+'px, '+screenPosition.P+'px)';
        //   },
        //   cursor: "pointer", //改变鼠标样式变成小手
        // },
        // {
        //   mouseLeave: function (e, node) {
        //     tooltip.style.display = 'none';
        //   },
        //   cursor: "pointer", //改变鼠标样式变成小手
        // }
      );

      this.myDiagram.nodeTemplateMap.add(
        "AGV",
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
                strokeWidth: 1,
              },
              new go.Binding("fill", "fillColor"),
              new go.Binding("stroke", "borderColor")
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
            $(go.TextBlock, "", textStyle(), new go.Binding("text", "text"))
          )
        )
      );

     
     
      
    },

    // end init
    //以JSON格式显示图表的模型，用户可以编辑该模型
    save() {
      document.getElementById("mySavedModel").value =
        this.myDiagram.model.toJson();

      this.myDiagram.isModified = false;
    },

    addNode() {
      var size = "8 8";
      var borderColor = "#FFFFFF";

      // 添加节点到图表

      this.myDiagram.model.addNodeData({
        category: "cell",
        text: "X",
        key: -1,
        loc: "40  40",
        size: size,
        fillColor: "red",
        borderColor: borderColor,
        group: -1,
      });
    },

    addCell() {
      var size = "8 8";
      var borderColor = "#FFFFFF";

      // 添加节点到图表
      this.myDiagram.model.addNodeData({
        category: "cell",
        text: "X",
        key: -1,
        loc: "40  40",
        size: size,
        fillColor: "red",
        borderColor: borderColor,
        group: -1,
      });
    },
 

    loadData(data) {
      this.myDiagram.model = go.Model.fromJson(data);
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
  /* ... 其他样式 ... */
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
