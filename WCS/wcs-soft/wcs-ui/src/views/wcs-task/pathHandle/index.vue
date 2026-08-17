<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务ID" prop="taskId">
        <el-input v-model="queryParams.taskId" placeholder="请输入任务ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务编号" prop="taskNo">
        <el-input v-model="queryParams.taskNo" placeholder="请输入任务编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="步骤ID" prop="jobId">
        <el-input v-model="queryParams.jobId" placeholder="请输入步骤ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="路劲ID" prop="pathId">
        <el-input v-model="queryParams.pathId" placeholder="请输入路劲ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="方法ID" prop="handleId">
        <el-input v-model="queryParams.handleId" placeholder="请输入方法ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择任务类型" clearable>
          <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="类名" prop="className">
        <el-input v-model="queryParams.className" placeholder="请输入类名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="方法名" prop="methodName">
        <el-input v-model="queryParams.methodName" placeholder="请输入方法名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="执行顺序" prop="cmdIndex">
        <el-input v-model="queryParams.cmdIndex" placeholder="请输入执行顺序" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建人ID" prop="createUserId">
        <el-input v-model="queryParams.createUserId" placeholder="请输入创建人ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建人姓名" prop="createUserName">
        <el-input v-model="queryParams.createUserName" placeholder="请输入创建人姓名" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新人ID" prop="updateUserId">
        <el-input v-model="queryParams.updateUserId" placeholder="请输入更新人ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新人姓名" prop="updateUserName">
        <el-input v-model="queryParams.updateUserName" placeholder="请输入更新人姓名" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="删除标志" prop="isDelete">
        <el-input v-model="queryParams.isDelete" placeholder="请输入删除标志" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="版本号" prop="version">
        <el-input v-model="queryParams.version" placeholder="请输入版本号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-task:pathHandle:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-task:pathHandle:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-task:pathHandle:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-task:pathHandle:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pathHandleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="任务ID" align="center" prop="taskId" width="100">
      </el-table-column>
      <el-table-column label="任务编号" align="center" prop="taskNo" width="100">
      </el-table-column>
      <el-table-column label="步骤ID" align="center" prop="jobId" width="100">
      </el-table-column>
      <el-table-column label="路劲ID" align="center" prop="pathId" width="100">
      </el-table-column>
      <el-table-column label="方法ID" align="center" prop="handleId" width="100">
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="type">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.type" /> -->
        </template>
      </el-table-column>
      <el-table-column label="类名" align="center" prop="className" width="100">
      </el-table-column>
      <el-table-column label="方法名" align="center" prop="methodName" width="100">
      </el-table-column>
      <el-table-column label="编码" align="center" prop="code" width="100">
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" width="100">
      </el-table-column>
      <el-table-column label="执行顺序" align="center" prop="cmdIndex" width="100">
      </el-table-column>
      <el-table-column label="创建人ID" align="center" prop="createUserId" width="100">
      </el-table-column>
      <el-table-column label="创建人姓名" align="center" prop="createUserName" width="100">
      </el-table-column>
      <el-table-column label="更新人ID" align="center" prop="updateUserId" width="100">
      </el-table-column>
      <el-table-column label="更新人姓名" align="center" prop="updateUserName" width="100">
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="isDelete" width="100">
      </el-table-column>
      <el-table-column label="版本号" align="center" prop="version" width="100">
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:pathHandle:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-task:pathHandle:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改路径方法对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务ID" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入任务ID" />
        </el-form-item>
        <el-form-item label="任务编号" prop="taskNo">
          <el-input v-model="form.taskNo" placeholder="请输入任务编号" />
        </el-form-item>
        <el-form-item label="步骤ID" prop="jobId">
          <el-input v-model="form.jobId" placeholder="请输入步骤ID" />
        </el-form-item>
        <el-form-item label="路劲ID" prop="pathId">
          <el-input v-model="form.pathId" placeholder="请输入路劲ID" />
        </el-form-item>
        <el-form-item label="方法ID" prop="handleId">
          <el-input v-model="form.handleId" placeholder="请输入方法ID" />
        </el-form-item>
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择任务类型">
            <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="类名" prop="className">
          <el-input v-model="form.className" placeholder="请输入类名" />
        </el-form-item>
        <el-form-item label="方法名" prop="methodName">
          <el-input v-model="form.methodName" placeholder="请输入方法名" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="执行顺序" prop="cmdIndex">
          <el-input v-model="form.cmdIndex" placeholder="请输入执行顺序" />
        </el-form-item>
        <el-form-item label="创建人ID" prop="createUserId">
          <el-input v-model="form.createUserId" placeholder="请输入创建人ID" />
        </el-form-item>
        <el-form-item label="创建人姓名" prop="createUserName">
          <el-input v-model="form.createUserName" placeholder="请输入创建人姓名" />
        </el-form-item>
        <el-form-item label="更新人ID" prop="updateUserId">
          <el-input v-model="form.updateUserId" placeholder="请输入更新人ID" />
        </el-form-item>
        <el-form-item label="更新人姓名" prop="updateUserName">
          <el-input v-model="form.updateUserName" placeholder="请输入更新人姓名" />
        </el-form-item>
        <el-form-item label="删除标志" prop="isDelete">
          <el-input v-model="form.isDelete" placeholder="请输入删除标志" />
        </el-form-item>
        <el-form-item label="版本号" prop="version">
          <el-input v-model="form.version" placeholder="请输入版本号" />
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
import { listPathHandle, getPathHandle, delPathHandle, addPathHandle, updatePathHandle } from "@/api/wcs-task/pathHandle";

export default {
  name: "PathHandle",
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
      // 路径方法表格数据
      pathHandleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskId: null,
        taskNo: null,
        jobId: null,
        pathId: null,
        handleId: null,
        type: null,
        className: null,
        methodName: null,
        code: null,
        name: null,
        cmdIndex: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询路径方法列表 */
    getList() {
      this.loading = true;
      listPathHandle(this.queryParams).then(response => {
        this.pathHandleList = response.rows;
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
        taskId: null,
        taskNo: null,
        jobId: null,
        pathId: null,
        handleId: null,
        type: null,
        className: null,
        methodName: null,
        code: null,
        name: null,
        cmdIndex: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null
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
      this.reset();
      this.open = true;
      this.title = "添加路径方法";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPathHandle(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改路径方法";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePathHandle(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addPathHandle(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除路径方法编号为"' + ids + '"的数据项？').then(function () {
        return delPathHandle(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-task/pathHandle/export', {
        ...this.queryParams
      }, `pathHandle_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
