<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务ID" prop="taskId">
        <el-input v-model="queryParams.taskId" placeholder="请输入任务ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务号" prop="taskNo">
        <el-input v-model="queryParams.taskNo" placeholder="请输入任务号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="wms任务号" prop="wmsTaskNo">
        <el-input v-model="queryParams.wmsTaskNo" placeholder="请输入wms任务号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input v-model="queryParams.wareCode" placeholder="请输入仓库编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库名称" prop="wareName">
        <el-input v-model="queryParams.wareName" placeholder="请输入仓库名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="区域编码" prop="areaCode">
        <el-input v-model="queryParams.areaCode" placeholder="请输入区域编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="区域名称" prop="areaName">
        <el-input v-model="queryParams.areaName" placeholder="请输入区域名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择任务类型" clearable>
          <el-option v-for="dict in dict.type.task_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input v-model="queryParams.palletCode" placeholder="请输入托盘编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="起点位置" prop="fromCellCode">
        <el-input v-model="queryParams.fromCellCode" placeholder="请输入起点位置" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="终点位置" prop="toCellCode">
        <el-input v-model="queryParams.toCellCode" placeholder="请输入终点位置" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="完成时间" prop="finishTime">
        <el-date-picker clearable v-model="queryParams.finishTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="任务状态" prop="state">
        <el-input v-model="queryParams.state" placeholder="请输入任务状态" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="版本号" prop="version">
        <el-input v-model="queryParams.version" placeholder="请输入版本号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="备注" prop="memo">
        <el-input v-model="queryParams.memo" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="托盘类型" prop="palletType">
        <el-input v-model="queryParams.palletType" placeholder="请输入托盘类型" clearable>
        </el-input>
      </el-form-item>
      <el-form-item label="载具高度" prop="palletHeight">
        <el-input v-model="queryParams.palletHeight" placeholder="请输入载具高度" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="载具重量" prop="palletWeight">
        <el-input v-model="queryParams.palletWeight" placeholder="请输入载具重量" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="remark1" prop="remark1">
        <el-input v-model="queryParams.remark1" placeholder="请输入remark1" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="remark2" prop="remark2">
        <el-input v-model="queryParams.remark2" placeholder="请输入remark2" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="remark3" prop="remark3">
        <el-input v-model="queryParams.remark3" placeholder="请输入remark3" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-task:historyTaskList:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-task:historyTaskList:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-task:historyTaskList:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-task:historyTaskList:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="historyTaskListList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="任务ID" align="center" prop="taskId">
      </el-table-column>
      <el-table-column label="任务号" align="center" prop="taskNo">
      </el-table-column>
      <el-table-column label="wms任务号" align="center" prop="wmsTaskNo">
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName">
      </el-table-column>
      <el-table-column label="区域编码" align="center" prop="areaCode">
      </el-table-column>
      <el-table-column label="区域名称" align="center" prop="areaName">
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.task_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="托盘编码" align="center" prop="palletCode">
      </el-table-column>
      <el-table-column label="起点位置" align="center" prop="fromCellCode">
      </el-table-column>
      <el-table-column label="终点位置" align="center" prop="toCellCode">
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="finishTime" width="180">

      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="state">
      </el-table-column>
      <el-table-column label="版本号" align="center" prop="version">
      </el-table-column>
      <el-table-column label="备注" align="center" prop="memo">
      </el-table-column>
      <el-table-column label="托盘类型" align="center" prop="palletType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.task_state" :value="scope.row.palletType" />
        </template>
      </el-table-column>
      <el-table-column label="载具高度" align="center" prop="palletHeight">
      </el-table-column>
      <el-table-column label="载具重量" align="center" prop="palletWeight">
      </el-table-column>
      <el-table-column label="remark1" align="center" prop="remark1">
      </el-table-column>
      <el-table-column label="remark2" align="center" prop="remark2">
      </el-table-column>
      <el-table-column label="remark3" align="center" prop="remark3">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:historyTaskList:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-task:historyTaskList:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改用于记录任务执行的每一步历史对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务ID" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入任务ID" />
        </el-form-item>
        <el-form-item label="任务号" prop="taskNo">
          <el-input v-model="form.taskNo" placeholder="请输入任务号" />
        </el-form-item>
        <el-form-item label="wms任务号" prop="wmsTaskNo">
          <el-input v-model="form.wmsTaskNo" placeholder="请输入wms任务号" />
        </el-form-item>
        <el-form-item label="仓库编码" prop="wareCode">
          <el-input v-model="form.wareCode" placeholder="请输入仓库编码" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="wareName">
          <el-input v-model="form.wareName" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="区域编码" prop="areaCode">
          <el-input v-model="form.areaCode" placeholder="请输入区域编码" />
        </el-form-item>
        <el-form-item label="区域名称" prop="areaName">
          <el-input v-model="form.areaName" placeholder="请输入区域名称" />
        </el-form-item>
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择任务类型">
            <el-option v-for="dict in dict.type.task_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
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
        <el-form-item label="完成时间" prop="finishTime">
          <el-date-picker clearable v-model="form.finishTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="任务状态" prop="state">
          <el-input v-model="form.state" placeholder="请输入任务状态" />
        </el-form-item>
        <el-form-item label="版本号" prop="version">
          <el-input v-model="form.version" placeholder="请输入版本号" />
        </el-form-item>
        <el-form-item label="备注" prop="memo">
          <el-input v-model="form.memo" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="托盘类型" prop="palletType">
          <el-input v-model="form.palletType" placeholder="请输入托盘类型">
          </el-input>
        </el-form-item>
        <el-form-item label="载具高度" prop="palletHeight">
          <el-input v-model="form.palletHeight" placeholder="请输入载具高度" />
        </el-form-item>
        <el-form-item label="载具重量" prop="palletWeight">
          <el-input v-model="form.palletWeight" placeholder="请输入载具重量" />
        </el-form-item>
        <el-form-item label="remark1" prop="remark1">
          <el-input v-model="form.remark1" placeholder="请输入remark1" />
        </el-form-item>
        <el-form-item label="remark2" prop="remark2">
          <el-input v-model="form.remark2" placeholder="请输入remark2" />
        </el-form-item>
        <el-form-item label="remark3" prop="remark3">
          <el-input v-model="form.remark3" placeholder="请输入remark3" />
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
import { listHistoryTaskList, getHistoryTaskList, delHistoryTaskList, addHistoryTaskList, updateHistoryTaskList } from "@/api/wcs-task/historyTaskList";

export default {
  name: "HistoryTaskList",
  dicts: ['task_state', 'task_type'],
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
      // 用于记录任务执行的每一步历史表格数据
      historyTaskListList: [],
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
        wmsTaskNo: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        finishTime: null,
        state: null,
        version: null,
        memo: null,
        palletType: null,
        palletHeight: null,
        palletWeight: null,
        remark1: null,
        remark2: null,
        remark3: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        taskNo: [
          { required: true, message: "任务号不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "任务类型不能为空", trigger: "change" }
        ],
        palletCode: [
          { required: true, message: "托盘编码不能为空", trigger: "blur" }
        ],
        state: [
          { required: true, message: "任务状态不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用于记录任务执行的每一步历史列表 */
    getList() {
      this.loading = true;
      listHistoryTaskList(this.queryParams).then(response => {
        if (response.code == 200) {
          this.historyTaskListList = response.rows;
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
        taskId: null,
        taskNo: null,
        wmsTaskNo: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        createTime: null,
        finishTime: null,
        state: null,
        version: null,
        memo: null,
        palletType: null,
        palletHeight: null,
        palletWeight: null,
        remark1: null,
        remark2: null,
        remark3: null
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
      this.title = "添加用于记录任务执行的每一步历史";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getHistoryTaskList(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改用于记录任务执行的每一步历史";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateHistoryTaskList(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addHistoryTaskList(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除用于记录任务执行的每一步历史编号为"' + ids + '"的数据项？').then(function () {
        return delHistoryTaskList(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-task/historyTaskList/export', {
        ...this.queryParams
      }, `historyTaskList_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
