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
      <el-form-item label="优先级" prop="pathIndex">
        <el-input v-model="queryParams.pathIndex" placeholder="请输入优先级" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择任务类型" clearable>
          <el-option v-for="dict in dict.type.task_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input v-model="queryParams.palletCode" placeholder="请输入托盘编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input v-model="queryParams.wareCode" placeholder="请输入仓库编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库名称" prop="wareName">
        <el-input v-model="queryParams.wareName" placeholder="请输入仓库名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="起点位置" prop="fromCellCode">
        <el-input v-model="queryParams.fromCellCode" placeholder="请输入起点位置" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="终点位置" prop="toCellCode">
        <el-input v-model="queryParams.toCellCode" placeholder="请输入终点位置" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="当前状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择当前状态" clearable>
          <el-option v-for="item in dict.type.task_state" :key="item.key" :label="item.label" :value="item.value" />
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
          v-hasPermi="['wcs-base:pathInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:pathInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:pathInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:pathInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pathInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id"  min-width="60"/>
      <el-table-column label="任务ID" align="center" prop="taskId" min-width="100">
      </el-table-column>
      <el-table-column label="任务编号" align="center" prop="taskNo" min-width="100">
      </el-table-column>
      <el-table-column label="步骤ID" align="center" prop="jobId" min-width="100">
      </el-table-column>
      <el-table-column label="优先级" align="center" prop="pathIndex" min-width="100">
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.task_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="100">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="100">
      </el-table-column>
      <el-table-column label="托盘编码" align="center" prop="palletCode" min-width="100">
      </el-table-column>
      <el-table-column label="起点位置" align="center" prop="fromCellCode" min-width="100">
      </el-table-column>
      <el-table-column label="终点位置" align="center" prop="toCellCode" min-width="100">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:pathInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:pathInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改执行路径对话框 -->
    <el-dialog  v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
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
        <el-form-item label="优先级" prop="pathIndex">
          <el-input v-model="form.pathIndex" placeholder="请输入优先级" />
        </el-form-item>
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择任务类型">
            <el-option v-for="dict in dict.type.task_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="托盘编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘编码" />
        </el-form-item>
        <el-form-item label="起点位置" prop="fromCellCode">
          <el-input v-model="form.fromCellCode" placeholder="请输入起点位置" />
        </el-form-item>
        <el-form-item label="终点位置" prop="toCellCode">
          <el-input v-model="form.toCellCode" placeholder="请输入终点位置" />
        </el-form-item>
        <el-form-item label="当前状态" prop="state">
          <el-select v-model="form.type" placeholder="请选择当前状态" clearable>
            <el-option v-for="item in dict.type.task_state" :key="item.value" :value="item.value"
              :label="item.label"></el-option>
          </el-select>
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
import { listPathInfo, getPathInfo, delPathInfo, addPathInfo, updatePathInfo } from "@/api/wcs-base/pathInfo";

export default {
  name: "PathInfo",
  dicts: ["task_state","task_type"],
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
      // 执行路径表格数据
      pathInfoList: [],
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
        pathIndex: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        state: null,
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
    /** 查询执行路径列表 */
    getList() {
      this.loading = true;
      listPathInfo(this.queryParams).then(response => {
        this.pathInfoList = response.rows;
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
        pathIndex: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        createTime: null,
        state: null,
        cmdTime: null,
        finishTime: null
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
      this.title = "添加执行路径";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPathInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改执行路径";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePathInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addPathInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除执行路径编号为"' + ids + '"的数据项？').then(function () {
        return delPathInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/pathInfo/export', {
        ...this.queryParams
      }, `pathInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
