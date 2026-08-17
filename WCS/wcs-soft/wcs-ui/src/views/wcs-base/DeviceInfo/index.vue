<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
        <el-form-item label="设备编码" prop="code">
          <el-input v-model="queryParams.code" placeholder="请输入设备编码" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="请选择设备类型" clearable>
            <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="通讯方式" prop="comType">
          <el-select v-model="queryParams.comType" placeholder="请选择通讯方式" clearable>
            <el-option v-for="dict in dict.type.com_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="通讯地址" prop="ip">
          <el-input v-model="queryParams.ip" placeholder="请输入通讯地址" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="通讯端口" prop="port">
          <el-input v-model="queryParams.port" placeholder="请输入通讯端口" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="是否在线" prop="isOnline">
          <el-select v-model="queryParams.comType" placeholder="请选择是否在线" clearable>
            <el-option v-for="dict in dict.type.is_online" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="运行状态" prop="state">
          <el-select v-model="queryParams.comType" placeholder="请选择运行状态" clearable>
            <el-option v-for="dict in dict.type.device_state" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-date-picker clearable v-model="queryParams.createTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="创建人" prop="createUserName">
          <el-input v-model="queryParams.createUserName" placeholder="请输入创建人" clearable
            @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="更新时间" prop="updateTime">
          <el-date-picker clearable v-model="queryParams.updateTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择更新时间">
          </el-date-picker>
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
    </el-card>

    <el-card>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
            v-hasPermi="['wcs-base:DeviceInfo:add']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" :disabled="single" @click="handleAdd2"
            v-hasPermi="['wcs-base:DeviceInfo:add']">复制新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
            v-hasPermi="['wcs-base:DeviceInfo:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
            v-hasPermi="['wcs-base:DeviceInfo:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
            v-hasPermi="['wcs-base:DeviceInfo:export']">导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="DeviceInfoList" @selection-change="handleSelectionChange" border>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" />
        <el-table-column label="设备编码" align="center" prop="code" width="100">
        </el-table-column>
        <el-table-column label="设备名称" align="center" prop="name" width="100">
        </el-table-column>
        <el-table-column label="设备类型" align="center" prop="type">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.device_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column label="通讯方式" align="center" prop="comType" width="150">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.com_type" :value="scope.row.comType" />
          </template>
        </el-table-column>
        <el-table-column label="S7类型" align="center" prop="s7Type">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.s7_type" :value="scope.row.s7Type" />
          </template>
        </el-table-column>
        <el-table-column label="通讯地址" align="center" prop="ip" width="120">
        </el-table-column>
        <el-table-column label="通讯端口" align="center" prop="port" width="80">
        </el-table-column>
        <el-table-column label="是否在线" align="center" prop="isOnline" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.is_online" :value="scope.row.isOnline" />
          </template>
        </el-table-column>
        <el-table-column label="运行状态" align="center" prop="state" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.device_state" :value="scope.row.state" />
          </template>
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
        <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['wcs-base:DeviceInfo:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['wcs-base:DeviceInfo:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList" />
    </el-card>

    <!-- 添加或修改设备对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型">
            <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通讯方式" prop="comType">
          <el-select v-model="form.comType" placeholder="请选择通讯方式">
            <el-option v-for="dict in dict.type.com_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="S7类型" prop="s7Type" v-if="form.comType == 0">
          <el-select v-model="form.s7Type" placeholder="请选择S7类型">
            <el-option v-for="dict in dict.type.s7_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通讯地址" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入通讯地址" />
        </el-form-item>
        <el-form-item label="通讯端口" prop="port">
          <el-input v-model="form.port" placeholder="请输入通讯端口" />
        </el-form-item>
        <el-form-item label="地址偏移量" prop="offset" v-if="form.isAdd">
          <el-input v-model="form.offset" placeholder="请输入地址偏移量" />
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
import {
  listDeviceInfo,
  getDeviceInfo,
  delDeviceInfo,
  addDeviceInfo,
  updateDeviceInfo,
} from "@/api/wcs-base/DeviceInfo";

export default {
  name: "DeviceInfo",
  dicts: [
    "com_type",
    "device_type",
    "s7_type",
    "del_flag",
    "is_online",
    "device_state",
  ],
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
      // 设备表格数据
      DeviceInfoList: [],
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
        type: null,
        comType: null,
        ip: null,
        port: null,
        isOnline: null,
        state: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: '0',
        s7Type: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "设备编码不能为空", trigger: "blur" },
        ],
        name: [
          { required: true, message: "设备名称不能为空", trigger: "blur" },
        ],
        type: [
          { required: true, message: "设备类型不能为空", trigger: "change" },
        ],
        comType: [
          { required: true, message: "通讯方式不能为空", trigger: "change" },
        ],
        isOnline: [
          { required: true, message: "是否在线不能为空", trigger: "blur" },
        ],
        state: [
          { required: true, message: "运行状态不能为空", trigger: "blur" },
        ],
        version: [
          { required: true, message: "版本号不能为空", trigger: "blur" },
        ],
        isDelete: [
          { required: true, message: "删除标志不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询设备列表 */
    getList() {
      this.loading = true;

      listDeviceInfo(this.queryParams).then((response) => {

        this.DeviceInfoList = response.rows;
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
        type: null,
        comType: null,
        ip: null,
        port: null,
        isOnline: null,
        state: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
        valueStyle: null,
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
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加设备";
    },
      /** 新增按钮操作 */
    handleAdd2(row) {
      this.reset();
      const id = row.id || this.ids;
      getDeviceInfo(id).then((response) => {
        this.form = response.data;
        this.form.isAdd=true;
        this.open = true;
        this.title = "添加设备";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getDeviceInfo(id).then((response) => {
        this.form = response.data;
        this.form.isAdd=false;
        this.open = true;
        this.title = "修改设备";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.loading = true;
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null && this.form.isAdd) {
            addDeviceInfo(this.form).then((response) => {
              this.loading = false;
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg||"新增失败");
              }
            });
          }
          else if (this.form.id != null && !this.form.isAdd) {
            updateDeviceInfo(this.form).then((response) => {
              this.loading = false;
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg||"修改失败");
              }
            });
          }
          else {
            addDeviceInfo(this.form).then((response) => {
              this.loading = false;
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
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
      this.$modal
        .confirm('是否确认删除设备编号为"' + ids + '"的数据项？')
        .then(function () {
          return delDeviceInfo(ids);
        })
        .then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("删除成功");
          }else{
            this.$modal.msgError(response.msg||"删除失败");
          }
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-base/DeviceInfo/export",
        {
          ...this.queryParams,
        },
        `DeviceInfo_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
