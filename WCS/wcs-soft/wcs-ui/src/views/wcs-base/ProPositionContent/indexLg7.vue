<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">

      <el-form-item label="类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="请选择">
            <el-option
              v-for="item in types"
              :key="item.value"
              :label="item.name"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <!-- <el-form-item label="站台物料类型" prop="type">
          <el-select v-model="queryParams.itemTypeCode" placeholder="请选择">
            <el-option
              v-for="item in itemTypes"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item> -->

        <el-form-item label="站台物料" prop="type">
          <el-select v-model="queryParams.itemCode" placeholder="请选择">
            <el-option
              v-for="item in itemInfos"
              :key="item.itemCode"
              :label="item.itemName"
              :value="item.itemCode">
            </el-option>
          </el-select>
        </el-form-item>
        
     
      <el-form-item label="产线" prop="proLineId">
        <el-select @change="update(scope.row)" v-model="queryParams.proLineCode" placeholder="">
            <el-option
              v-for="item in lineInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
      </el-form-item>
       
      
      <el-form-item label="IP" prop="wifiModeIp">
        <el-input
          v-model="queryParams.wifiModeIp"
          placeholder="请输入wifi模块IP"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
    
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input
          v-model="queryParams.palletCode"
          placeholder="请输入托盘编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['wcs-base:ProPositionContent:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleTaskInfo"
          v-hasPermi="['wcs-base:ProPositionContent:taskInfo']"
        >搬运</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['wcs-base:ProPositionContent:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['wcs-base:ProPositionContent:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-base:ProPositionContent:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProPositionContentList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="ID" align="center" prop="id" min-width="100" /> -->
      <el-table-column label="类型" align="center" prop="type"  min-width="120">
      <template slot-scope="scope">
           <span v-for="type in types" :style="'color:'+type.color" v-if="scope.row.type==type.value">{{ type.name }}</span>
           
            <!-- <el-input @blur="update(scope.row)" v-model="scope.row.type"></el-input> -->
        
      </template>
    </el-table-column>
    <el-table-column label="编码" align="center" prop="code"  min-width="120">
      <template slot-scope="scope">
             <span>{{ scope.row.code }}</span>
             <!-- <el-input @blur="updateProPositionContent(
              updatePosition({
          id:scope.row.positionId
          ,code:scope.row.code
          })
             )" v-model="scope.row.code"></el-input> -->
         
       </template>
    </el-table-column>
    <!-- <el-table-column label="前置编码" align="center" prop="code2"  min-width="120">
      <template slot-scope="scope">
             
            <el-input @blur="update(scope.row)" v-model="scope.row.code2"></el-input>
        
      </template>
    </el-table-column>
    <el-table-column label="物料类型" align="center" prop="disableState"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.itemTypeCode" placeholder="">
            <el-option
              v-for="item in itemTypes"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
      </template>
     
    </el-table-column> 
     -->
    <el-table-column label="物料" align="center" prop="disableState"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.itemCode" placeholder="">
            <el-option
            :disabled="item.itemTypeCode != scope.row.itemTypeCode"
              v-for="item in itemInfos"
              :key="item.itemCode"
              :label="item.itemName "
              :value="item.itemCode">
            </el-option>
          </el-select>
      </template>
     
    </el-table-column> 

    <el-table-column label="托盘状态" align="center" prop="disableState"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.palletState" placeholder="">
            <el-option
           
              v-for="item in palletStates"
              :key="item.value"
              :label="item.name "
              :value="item.value">
            </el-option>
          </el-select>
      </template>
     
    </el-table-column> 
    <!-- <el-table-column label="产线" align="center" prop="disableState"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.proLineCode" placeholder="">
            <el-option
              v-for="item in lineInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
      </template>
    </el-table-column>  -->
    <!-- <el-table-column label="仓库编码" align="center" prop="wareCode"  min-width="120">
    </el-table-column> 
    <el-table-column label="仓库编码" align="center" prop="wareCode"  min-width="120">
    </el-table-column>  -->
    <el-table-column label="是否有料" align="center" prop="invenState"  min-width="120">
     
      <template slot-scope="scope">
        <el-select @change="updatePosition({
          id:scope.row.positionId
          ,code:scope.row.code
          ,invenState:scope.row.invenState
          })" v-model="scope.row.invenState" placeholder="">
            <el-option
           
              v-for="item in invenStates"
              :key="item.value"
              :label="item.text "
              :value="item.value">
            </el-option>
          </el-select>
           <span v-for="type in invenStates" :style="'color:'+type.color" v-if="scope.row.invenState==type.value">{{ type.text }}</span>

      </template>
    </el-table-column> 
    <el-table-column label="任务状态" align="center" prop="taskState"  min-width="120">
    </el-table-column> 
    <el-table-column label="禁用状态" align="center" prop="disableState"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="updatePosition({
          id:scope.row.positionId
          ,code:scope.row.code
          ,disableState:scope.row.disableState
          })" v-model="scope.row.disableState" placeholder="">
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
      </template>
     
    </el-table-column> 
 
    <!-- <el-table-column label="产品ID" align="center" prop="productId"  min-width="120">
    </el-table-column>
    <el-table-column label="物料ID" align="center" prop="itemId"  min-width="120">
    </el-table-column>
    <el-table-column label="位置Id" align="center" prop="PositionId"  min-width="120">
    </el-table-column>
    <el-table-column label="产线ID" align="center" prop="proLineId"  min-width="120">
    </el-table-column>
    <el-table-column label="工序ID" align="center" prop="proProcessId"  min-width="120">
    </el-table-column> -->
    <!-- <el-table-column label="站台传感器" align="left" prop="deviceCode"  min-width="260">
      <template slot-scope="scope">
        <div v-if="scope.row.type==1">
          <div>扫码点位：{{scope.row.deviceScanCodeAddress}}</div>
          <div>有料点位：{{scope.row.devceGuangdianAddress}}</div>
          <div>状态：
            <span  style="color:#67C23A" v-if="scope.row.wifiModeIsConnect==1">连接</span>
        <span  style="color:red" v-if="scope.row.wifiModeIsConnect==0">断开</span>
          </div>
        </div>

        <div v-if="scope.row.type!=1">
          <div>WIFI模块IP：{{scope.row.wifiModeIp}}</div>
          <div>WIFI模块偏移量：{{scope.row.wifiModeOffset}}</div>
          <div>状态：
            <span  style="color:#67C23A" v-if="scope.row.wifiModeIsConnect==1">连接</span>
        <span  style="color:red" v-if="scope.row.wifiModeIsConnect==0">断开</span>
          </div>
        </div>
       
        
      </template>
    </el-table-column> -->
    <!-- <el-table-column label="wifi模块偏移量" align="center" prop="wifiModeOffset"  min-width="150">
      <template slot-scope="scope">
             <el-input @blur="update(scope.row)" v-model="scope.row.wifiModeOffset"></el-input>
        
      </template>
    </el-table-column> 
    <el-table-column label="wifi模块IP" align="center" prop="wifiModeIp"  min-width="150">
      <template slot-scope="scope">
             <el-input @blur="update(scope.row)" v-model="scope.row.wifiModeIp"></el-input>
        
      </template>
    </el-table-column>
    <el-table-column label="wifi模块偏移量" align="center" prop="wifiModeOffset"  min-width="150">
      <template slot-scope="scope">
             <el-input @blur="update(scope.row)" v-model="scope.row.wifiModeOffset"></el-input>
        
      </template>
    </el-table-column> -->
     <!--<el-table-column label="连接状态" align="center" prop="wifiModeIsConnect"  min-width="120">
      <template slot-scope="scope">
        <span  style="color:chartreuse" v-if="scope.row.wifiModeIsConnect==1">连接</span>
        <span  style="color:red" v-if="scope.row.wifiModeIsConnect==0">断开</span>
        
      </template>
    </el-table-column>
    <el-table-column label="wifi模块PORT" align="center" prop="wifiModePort"  min-width="120">
    </el-table-column> -->
    
    <el-table-column label="托盘编码" align="center" prop="palletCode"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:ProPositionContent:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:ProPositionContent:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="搬运" :visible.sync="taskInfoOpen" width="500px" append-to-body>
      <el-form ref="taskInfoForm" :model="taskInfo" :rules="taskInfoRules" label-width="80px">
        <el-form-item label="起始位置" prop="productId">
          <el-input v-model="taskInfo.fromCellCode" placeholder="起始位置" />
        </el-form-item>
        <el-form-item label="目标位置" prop="productId">
          <el-select v-model="taskInfo.toCellCode" placeholder="请选择">
            <el-option
            :disabled=" item.disableState>0|| item.taskState>0"
              v-for="item in allPositions"
              :key="item.code"
              :label="item.code"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTaskInfo()">确 定</el-button>
        <el-button @click="taskInfoOpen=false;">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改站台扩展对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="productId">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option
              v-for="item in types"
              :key="item.value"
              :label="item.name"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="产品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入产品ID" />
        </el-form-item>
        <el-form-item label="物料ID" prop="itemId">
          <el-input v-model="form.itemId" placeholder="请输入物料ID" />
        </el-form-item>
        <!-- <el-form-item label="位置Id" prop="PositionId">
          <el-input v-model="form.PositionId" placeholder="请输入位置Id" />
        </el-form-item> -->
        <el-form-item label="产线ID" prop="proLineId">
          <el-input v-model="form.proLineId" placeholder="请输入产线ID" />
        </el-form-item>
        <el-form-item label="工序ID" prop="proProcessId">
          <el-input v-model="form.proProcessId" placeholder="请输入工序ID" />
        </el-form-item>
        <el-form-item v-if="form.type==1" label="设备编码" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item v-if="form.type==1"  label="扫码点位" prop="deviceScanCodeAddress">
          <el-input v-model="form.deviceScanCodeAddress" placeholder="请输入扫码点位" />
        </el-form-item>
        <el-form-item v-if="form.type==1"  label="是否有点位" prop="devceGuangdianAddress">
          <el-input v-model="form.devceGuangdianAddress" placeholder="请输入是否有点位" />
        </el-form-item>
        <el-form-item v-if="form.type!=1"  label="wifi模块IP" prop="wifiModeIp">
          <el-input v-model="form.wifiModeIp" placeholder="请输入wifi模块IP" />
        </el-form-item>
        <el-form-item v-if="form.type!=1" label="wifi模块PORT" prop="wifiModePort">
          <el-input v-model="form.wifiModePort" placeholder="请输入wifi模块PORT" />
        </el-form-item>
        <!-- <el-form-item label="托盘编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘编码" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import { listItemType, getItemType, delItemType, addItemType, updateItemType } from "@/api/wcs-base/ItemType";
import { listProLine, getProLine, delProLine, addProLine, updateProLine } from "@/api/wcs-base/ProLine";
import { listItemInfo, getItemInfo, delItemInfo, addItemInfo, updateItemInfo } from "@/api/wcs-base/ItemInfo";
import { listPositionInfo, getPositionInfo, delPositionInfo, addPositionInfo, updatePositionInfo } from "@/api/wcs-base/PositionInfo";
import { listProPositionContent, getProPositionContent, delProPositionContent, addProPositionContent, updateProPositionContent } from "@/api/wcs-base/ProPositionContent";
import request from "@/utils/request.js";
export default {
  name: "ProPositionContent",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 站台扩展表格数据
      ProPositionContentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productId: null,
        itemId: null,
        PositionId: null,
        proLineId: null,
        proProcessId: null,
        deviceCode: null,
        deviceScanCodeAddress: null,
        devceGuangdianAddress: null,
        wifiModeIp: null,
        wifiModePort: null,
        palletCode: null
      },
      itemInfos:[],
      lineInfos:[],
      itemTypes:[],
      allPositions:[],
      taskInfoOpen: false,
      taskInfo:{},
      taskInfoRules:{
        fromCellCode: [{ required: true, message: "起始位置不能为空", trigger: "blur" }],
        toCellCode: [{ required: true, message: "目标位置不能为空", trigger: "blur" }],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      invenStates:[
        {value:1,text:"有托盘",color:"#409EFF"}
        ,  {value:0,text:"无托盘",color:"#909399"}
      
      ],
      //0-仓库 1-缓存架 2-CT 3-RP
      types:[
        {value:0,name:"仓库",color:"#67C23A"}
        ,  {value:1,name:"缓存架",color:"#409EFF"}
        ,  {value:2,name:"CT",color:"#E6A23C"}
        ,  {value:3,name:"RP",color:"#E6A23C"}
      ],
      palletStates:[
            {value:0,name:"空托盘",color:"#67C23A"}
        ,  {value:1,name:"满托盘",color:"#409EFF"} 
      ]
    };
  },
  created() {
    this.getList();
    this.getItemInfoList();
    this.getLineInfoList();
    this.getItemTypeList();
  },
  methods: {
    /** 查询站台扩展列表 */
    getList() {
      this.loading = true;
      listProPositionContent(this.queryParams).then(response => {
          if(response.code==200){
            this.ProPositionContentList = response.rows;
            this.total = response.total;
          }
        this.loading = false;
      });
    },
    getItemInfoList() {
      this.loading = true;
      listItemInfo({ pageNum: 1,
        pageSize: 999,}).then(response => {
          if(response.code==200){
            this.itemInfos = response.rows; 
          } 
      });
    },

    getItemTypeList() {
      this.loading = true;
      listItemType({ pageNum: 1,
        pageSize: 999,}).then(response => {
          if(response.code==200){
            this.itemTypes = response.rows; 
          } 
      });
    },
    getLineInfoList() {
      this.loading = true;
      listProLine({ pageNum: 1,
        pageSize: 999,}).then(response => {
          if(response.code==200){
            this.lineInfos = response.rows; 
          } 
      });
    },

    update(row) {
      updateProPositionContent(row).then((response) => {
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

    /** 查询站台扩展列表 */
    getAllList() {
      this.loading = true;
      this.allPositions=[];
      listProPositionContent({pageNum: 1,
        pageSize: 999,}).then(response => {
          if(response.code==200){
            this.allPositions = response.rows; 
          }
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        productId: null,
        itemId: null,
        PositionId: null,
        proLineId: null,
        proProcessId: null,
        deviceCode: null,
        deviceScanCodeAddress: null,
        devceGuangdianAddress: null,
        wifiModeIp: null,
        wifiModePort: null,
        palletCode: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加站台扩展";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProPositionContent(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改站台扩展";
      });
    }, 
    submitTaskInfo(){
      this.$refs["taskInfoForm"].validate(valid => {
        if (valid) {
          request({
            url: "/wcs-task/lg7/TaskInfo",
            method: "post",
            data: this.taskInfo,
          }).then((response) => {
            if (response.code == 200) {
              this.taskInfoOpen=false;
              this.getList(this.deviceCode);
              this.$modal.msgSuccess("保存成功");
            } else {
              this.$modal.msgError(response.msg || "保存失败");
            }
          });
        }
      });
    },

     /** 修改按钮操作 */
     handleTaskInfo(row) {
      var that =this;
      this.reset();
      const id = row.id || this.ids
      getProPositionContent(id).then(response => {
          if(response.code==200){
            var position =  response.data;
            if(position.taskState>0.9){
              this.$modal.msgError( "该位置存在搬运任务");
            }
            if(position.invenState<0.9){
              this.$modal.msgError( "该位置无托盘");
            }
            if(position.disableState>0.9){
              this.$modal.msgError( "该位置禁用");
            }
          }
          that.taskInfo={};
          that.taskInfo.type=8;
          that.taskInfo.fromCellCode=position.code;
          that.taskInfo.wareCode="LG-NA";
          that.taskInfo.wareName="LG-NA";
 
          that.getAllList();
          that.taskInfoOpen=true;
     
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProPositionContent(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProPositionContent(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
              }else{
                this.$modal.msgError(response.msg||"新增失败");
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除站台扩展编号为"' + ids + '"的数据项？').then(function() {
        return delProPositionContent(ids);
      }).then((response) => {
          if(response.code==200){
            this.getList();
            this.$modal.msgSuccess("删除成功");
          }else{
            this.$modal.msgError(response.msg||"删除失败");
          }

      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/ProPositionContent/export', {
        ...this.queryParams
      }, `ProPositionContent_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
