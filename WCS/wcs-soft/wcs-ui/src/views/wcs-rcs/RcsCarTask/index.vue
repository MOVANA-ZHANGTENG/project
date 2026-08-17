<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left"
      label-width="100px">
      <el-form-item label="任务ID" prop="id">
        <el-input v-model="queryParams.id" placeholder="请输入任务ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务编号" prop="taskCode">
        <el-input v-model="queryParams.taskCode" placeholder="请输入任务编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务类型" prop="taskType">
        <el-select v-model="queryParams.taskType" placeholder="请选择任务类型" clearable>
          <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-input v-model="queryParams.createTime" placeholder="请输入创建时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建用户" prop="createUserName">
        <el-input v-model="queryParams.createUserName" placeholder="请输入创建用户" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="小车ID" prop="allotCarId">
        <el-input v-model="queryParams.allotCarId" placeholder="请输入小车ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务起点" prop="fromCellCode">
        <el-input v-model="queryParams.fromCellCode" placeholder="请输入任务起点" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务终点" prop="toCellCode">
        <el-input v-model="queryParams.toCellCode" placeholder="请输入任务终点" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="搬运托盘" prop="palletCode">
        <el-input v-model="queryParams.palletCode" placeholder="请输入搬运托盘" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择任务状态" clearable>
          <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-input v-model="queryParams.startTime" placeholder="请输入开始时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-input v-model="queryParams.endTime" placeholder="请输入结束时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-rcs:RcsCarTask:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-rcs:RcsCarTask:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-rcs:RcsCarTask:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-rcs:RcsCarTask:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="RcsCarTaskList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务ID" align="center" prop="id" min-width="100" />
      <el-table-column label="任务编号" align="center" prop="taskCode" min-width="120">
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="taskType" min-width="120">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.taskType" /> -->
        </template>
      </el-table-column>
      <el-table-column label="任务优先级" align="center" prop="priority" min-width="120">
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="120">
      </el-table-column>
      <el-table-column label="创建用户" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column label="小车ID" align="center" prop="allotCarId" min-width="120">
      </el-table-column>
      <el-table-column label="任务起点" align="center" prop="fromCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="任务终点" align="center" prop="toCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="搬运托盘" align="center" prop="palletCode" min-width="120">
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="status" min-width="120">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.status" /> -->
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" min-width="120">
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" min-width="120">
      </el-table-column>
      <el-table-column label="持续时间" align="center" prop="duration" min-width="120">
      </el-table-column>
      <el-table-column label="错误代码" align="center" prop="errorCode" min-width="120">
      </el-table-column>
      <el-table-column label="错误详情" align="center" prop="errorMessage" min-width="120">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.errorMessage" /> -->
        </template>
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-rcs:RcsCarTask:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-rcs:RcsCarTask:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改小车任务主体对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务类型" prop="taskType">
          <el-select v-model="form.taskType" placeholder="请选择任务类型">
            <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="任务优先级" prop="priority">
          <el-input v-model="form.priority" placeholder="请输入任务优先级" />
        </el-form-item>
        <el-form-item label="小车ID" prop="allotCarId">
          <el-input v-model="form.allotCarId" placeholder="请输入小车ID" />
        </el-form-item>
        <el-form-item label="任务起点" prop="fromCellCode">
          <el-input v-model="form.fromCellCode" placeholder="请输入任务起点" />
        </el-form-item>
        <el-form-item label="任务终点" prop="toCellCode">
          <el-input v-model="form.toCellCode" placeholder="请输入任务终点" />
        </el-form-item>
        <el-form-item label="搬运托盘" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入搬运托盘" />
        </el-form-item>
        <el-form-item label="任务状态" prop="status">
          <el-radio-group v-model="form.status">
            <!-- <el-radio v-for="dict in dict.type.${ dictType }" :key="dict.value"
              :label="dict.value">{{ dict.label }}</el-radio> -->
          </el-radio-group>
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
import { listRcsCarTask, getRcsCarTask, delRcsCarTask, addRcsCarTask, updateRcsCarTask } from "@/api/wcs-rcs/RcsCarTask";
import request from "@/utils/request";
export default {
  name: "RcsCarTask",
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
      // 小车任务主体表格数据
      RcsCarTaskList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: null,
        taskCode: null,
        taskType: null,
        createTime: null,
        createUserName: null,
        allotCarId: null,
        fromCellCode: null,
        toCellCode: null,
        palletCode: null,
        status: null,
        startTime: null,
        endTime: null,
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
    /** 查询小车任务主体列表 */
    getList() {
      this.loading = true;
      listRcsCarTask(this.queryParams).then(response => {
        if (response.code == 200) {
          this.RcsCarTaskList = response.rows;
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
        taskType: null,
        priority: null,
        createTime: null,
        createUserName: null,
        allotCarId: null,
        fromCellCode: null,
        toCellCode: null,
        palletCode: null,
        status: null,
        startTime: null,
        endTime: null,
        duration: null,
        errorCode: null,
        errorMessage: null
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
      this.title = "添加小车任务主体";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getRcsCarTask(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改小车任务主体";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRcsCarTask(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addRcsCarTask(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "新增失败");
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除小车任务主体编号为"' + ids + '"的数据项？').then(function () {
        return delRcsCarTask(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg || "删除失败");
        }

      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-rcs/RcsCarTask/export', {
        ...this.queryParams
      }, `RcsCarTask_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
