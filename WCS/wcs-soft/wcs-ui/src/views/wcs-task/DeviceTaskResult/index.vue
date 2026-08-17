<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备任务编码" prop="taskCode">
        <el-input
          v-model="queryParams.taskCode"
          placeholder="请输入设备任务编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in states"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="当前位置" prop="node">
        <el-input
          v-model="queryParams.node"
          placeholder="请输入当前位置"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input
          v-model="queryParams.type"
          placeholder="请输入类型"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="备注数据" prop="data">
        <el-input
          v-model="queryParams.data"
          placeholder="请输入备注数据"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名" prop="fromDevice">
        <el-input
          v-model="queryParams.fromDevice"
          placeholder="请输入设备名"
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
          v-hasPermi="['wcs-task:DeviceTaskResult:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['wcs-task:DeviceTaskResult:edit']"
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
          v-hasPermi="['wcs-task:DeviceTaskResult:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-task:DeviceTaskResult:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="DeviceTaskResultList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
    <el-table-column label="设备任务编码" align="center" prop="taskCode"  min-width="120">
    </el-table-column>
    <el-table-column label="状态" align="center" prop="state"  min-width="120">
      <template slot-scope="scope">
          <span  :style="'color:'+item.color"  v-for="item in states " v-if="scope.row.state == item.value">{{ item.label }}</span>
        </template>
    </el-table-column>
    <el-table-column label="当前位置" align="center" prop="node"  min-width="120">
    </el-table-column>
      <el-table-column label="类型" align="center" prop="type"  min-width="120">

      </el-table-column>
      <el-table-column label="时间" align="center" prop="createTime"  min-width="120">

      </el-table-column>
    <el-table-column label="备注数据" align="center" prop="data"  min-width="120">
    </el-table-column>
    <el-table-column label="设备名" align="center" prop="fromDevice"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:DeviceTaskResult:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-task:DeviceTaskResult:remove']"
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

    <!-- 添加或修改设备任务回传对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备任务编码" prop="taskCode">
          <el-input v-model="form.taskCode" placeholder="请输入设备任务编码" />
        </el-form-item>

        <el-form-item label="当前位置" prop="node">
          <el-input v-model="form.node" placeholder="请输入当前位置" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-input v-model="form.type" placeholder="类型" />
        </el-form-item>
        <el-form-item label="备注数据" prop="data">
          <el-input v-model="form.data" placeholder="请输入备注数据" />
        </el-form-item>
        <el-form-item label="设备名" prop="fromDevice">
          <el-input v-model="form.fromDevice" placeholder="请输入设备名" />
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
import { listDeviceTaskResult, getDeviceTaskResult, delDeviceTaskResult, addDeviceTaskResult, updateDeviceTaskResult } from "@/api/wcs-task/DeviceTaskResult";

export default {
  name: "DeviceTaskResult",
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
      // 设备任务回传表格数据
      DeviceTaskResultList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskCode: null,
        state: null,
        node: null,
        type: null,
        data: null,
        fromDevice: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      states:[
        {value:0,label:"初始化",color:"#909399"}
        ,{value:1,label:"已经处理",color:"#409EFF"}
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询设备任务回传列表 */
    getList() {
      this.loading = true;
      listDeviceTaskResult(this.queryParams).then(response => {
          if(response.code==200){
            this.DeviceTaskResultList = response.rows;
            this.total = response.total;
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
        taskCode: null,
        state: null,
        node: null,
        type: "end",
        createTime: null,
        data: null,
         fromDevice: null
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
      this.title = "添加设备任务回传";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDeviceTaskResult(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改设备任务回传";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDeviceTaskResult(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addDeviceTaskResult(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除设备任务回传编号为"' + ids + '"的数据项？').then(function() {
        return delDeviceTaskResult(ids);
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
      this.download('wcs-task/DeviceTaskResult/export', {
        ...this.queryParams
      }, `DeviceTaskResult_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
