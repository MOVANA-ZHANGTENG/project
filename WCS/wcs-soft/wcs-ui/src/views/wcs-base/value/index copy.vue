<template>
  <div class="app-container">
    <el-card v-loading="deviceLoading">
      <el-row >
        <el-col :span="4" v-for="device in devices">
    
          <el-card  :class="device.code==deviceCode? 'deviceSelected': ''"  @click.native="getList(device.code)" style="width:90% ;margin-right:10%">
            {{ device.code }}
          </el-card> 
        </el-col>
      </el-row>
     
    </el-card>
    
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="属性编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入属性编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="属性名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入属性名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备编码" prop="deviceCode">
        <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="通讯方式" prop="comType">
        <el-select v-model="queryParams.comType" placeholder="请选择通讯方式" clearable>
          <el-option v-for="dict in dict.type.com_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="s7类型" prop="s7Type">
        <el-select v-model="queryParams.s7Type" placeholder="请选择s7类型" clearable>
          <el-option v-for="dict in dict.type.s7_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="modbus类型" prop="modbusType">
        <el-select v-model="queryParams.modbusType" placeholder="请选择modbus类型" clearable>
          <el-option v-for="dict in dict.type.modbus_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="通讯地址" prop="ip">
        <el-input v-model="queryParams.ip" placeholder="请输入通讯地址" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="通讯端口" prop="port">
        <el-input v-model="queryParams.port" placeholder="请输入通讯端口" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="属性地址" prop="address">
        <el-input v-model="queryParams.address" placeholder="请输入属性地址" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="plc类型" prop="plcType">
        <el-select v-model="queryParams.plcType" placeholder="请选择属性类型" clearable>
          <el-option v-for="dict in dict.type.plc_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="java类型" prop="javaType">
        <el-select v-model="queryParams.javaType" placeholder="请选择modbus类型" clearable>
          <el-option v-for="dict in dict.type.java_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建人" prop="createUserName">
        <el-input v-model="queryParams.createUserName" placeholder="请输入创建人" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新人" prop="updateUserName">
        <el-input v-model="queryParams.updateUserName" placeholder="请输入更新人" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="删除标志" prop="isDelete">
        <el-select v-model="queryParams.isDelete" placeholder="请选择删除标志" clearable>
          <el-option v-for="item in dict.type.del_flag" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:value:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:value:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:value:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:value:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="valueList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="属性编码" align="center" prop="code" width="100">
      </el-table-column>
      <el-table-column label="属性名称" align="center" prop="name" width="100">
      </el-table-column>
      <el-table-column label="设备编码" align="center" prop="deviceCode" width="100">
      </el-table-column>
      <el-table-column label="设备名称" align="center" prop="deviceName" width="100">
      </el-table-column>
      <el-table-column label="通讯方式" align="center" prop="comType"  width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.com_type" :value="scope.row.comType" />
        </template>
      </el-table-column>
      <el-table-column label="S7类型" align="center" prop="s7Type"  width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.s7_type" :value="scope.row.s7Type" />
        </template>
      </el-table-column>
      <el-table-column label="modbus类型" align="center" prop="modbusType"  width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.modbus_type" :value="scope.row.modbusType" />
        </template>
      </el-table-column>
      <el-table-column label="通讯地址" align="center" prop="ip" width="100">
      </el-table-column>
      <el-table-column label="通讯端口" align="center" prop="port" width="100">
      </el-table-column>
      <el-table-column label="属性地址" align="center" prop="address" width="120">
      </el-table-column>
      <el-table-column label="plc类型" align="center" prop="plcType" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.plc_type" :value="scope.row.plcType" />
        </template>
      </el-table-column>
      <el-table-column label="java类型" align="center" prop="javaType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.java_type" :value="scope.row.javaType" />
        </template>
      </el-table-column>
      <el-table-column label="属性长度" align="center" prop="length" width="100">
      </el-table-column>
      <el-table-column label="读取属性" align="center" prop="readValue" width="100">
      </el-table-column>
      <el-table-column label="读取时间" align="center" prop="readTime" width="180">
      </el-table-column>
      <el-table-column label="写入属性" align="center" prop="writeValue" width="100">
      </el-table-column>
      <el-table-column label="写入时间" align="center" prop="writeTime" width="180">
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserName" width="100">
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updateUserName" width="100">
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="isDelete" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.isDelete" />
        </template>
      </el-table-column>
      <el-table-column fixed="right" width="120"  label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:value:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:value:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改属性对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="属性编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入属性编码" />
        </el-form-item>
        <el-form-item label="属性名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入属性名称" />
        </el-form-item>
        <el-form-item label="设备编码" prop="deviceId">
          <template slot-scope="scope">
            <el-select  v-model="form.deviceId" placeholder="请选择设备" clearable>
              <el-option v-for="item in devices" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="modbus类型" prop="modbusType">
          <template slot-scope="scope">
            <el-select  v-model="form.modbusType" placeholder="请选择属性类型" clearable>
              <el-option v-for="dict in dict.type.modbus_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="属性地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入属性地址" />
        </el-form-item>
        <el-form-item label="plc类型" prop="plcType">
          <template slot-scope="scope">
            <el-select  v-model="form.plcType" placeholder="请选择属性类型" clearable>
              <el-option v-for="dict in dict.type.plc_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="java类型" prop="javaType">
          <template slot-scope="scope">
            <el-select  v-model="form.javaType" placeholder="请选择属性类型" clearable>
              <el-option v-for="dict in dict.type.java_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="属性长度" prop="length">
          <el-input v-model="form.length" placeholder="请输入属性长度" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listValue, getValue, delValue, addValue, updateValue } from "@/api/wcs-base/value";
import { listDeviceInfo, getDeviceInfo, delDeviceInfo, addDeviceInfo, updateDeviceInfo } from "@/api/wcs-base/DeviceInfo";
import request from "@/utils/request.js"
export default {
  name: "Value",
  dicts:['com_type','device_type','java_type','plc_type','modbus_type','s7_type','del_flag'],
  data() {
    return {
      // 遮罩层
      loading: false,
      deviceLoading:false,
      deviceCode:null,
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
      // 属性表格数据
      valueList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: null,
        name: null,
        deviceCode: null,
        deviceName: null,
        comType: null,
        ip: null,
        port: null,
        address: null,
        plcType: null,
        javaType: null,
        readValue: null,
        readTime: null,
        writeValue: null,
        writeTime: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        isDelete: null,
        s7Type:null,
        modbusType:null,
        valueStyle:null,
        javaType:null,
      },
      devices:[],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deviceCode: [
          { required: true, message: "设备编码不能为空", trigger: "blur" }
        ],
        deviceName: [
          { required: true, message: "设备名称不能为空", trigger: "blur" }
        ],
        address: [
          { required: true, message: "属性地址不能为空", trigger: "blur" }
        ],
        plcType: [
          { required: true, message: "属性类型不能为空", trigger: "change" }
        ],
        length: [
          { required: true, message: "属性长度不能为空", trigger: "blur" }
        ],
        version: [
          { required: true, message: "版本号不能为空", trigger: "blur" }
        ],
        isDelete: [
          { required: true, message: "删除标志不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
   // this.getList();
    this.getDevices();
  },
  methods: {
    
    /**
     * 获取设备信息
     */
    getDevices(){
      var that = this;
      that.deviceLoading=true;
      request({
        url:'/wcs-base/DeviceInfo/getDevices',
        method:'get'
      }
      ).then((response)=>{
        that.deviceLoading=false;
        if(response.code==200){
       
          that.devices = []
          var data = response.data
          for(var i=0;i<data.length;i++){
            that.devices.push(data[i])
          }
        }
      })
    },
    /** 查询属性列表 */
    getList(deviceCode) {
      this.loading = true;
      this.queryParams.deviceCode=deviceCode;
      this.deviceCode=deviceCode;
      listValue(this.queryParams).then(response => {
  
        this.valueList = response.rows;
        this.total = response.total;
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
        code: null,
        name: null,
        deviceId: null,
        deviceCode: null,
        deviceName: null,
        comType: null,
        ip: null,
        port: null,
        address: null,
        plcType: null,
        javaType:null,
        modbusType:null,
        length: null,
        readValue: null,
        readTime: null,
        writeValue: null,
        writeTime: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
        s7Type:null,
        valueStyle:null,
        javaType:null,
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.getDevices()
      this.reset();
      this.open = true;
      this.title = "添加属性";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getDevices()
      this.reset();
      const id = row.id || this.ids
      getValue(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改属性";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateValue(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addValue(this.form).then(response => {
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
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除属性编号为"' + ids + '"的数据项？').then(function () {
        return delValue(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/value/export', {
        ...this.queryParams
      }, `value_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
 .deviceSelected{
  background-color: #409EFF;
  color: #FFFFFF;
 }
</style>
