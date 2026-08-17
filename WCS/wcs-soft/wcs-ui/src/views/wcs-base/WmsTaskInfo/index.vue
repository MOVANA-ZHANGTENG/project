<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务编号" prop="taskNo">
        <el-input v-model="queryParams.taskNo" placeholder="请输入任务编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input v-model="queryParams.wareCode" placeholder="请输入仓库编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库名称" prop="wareName">
        <el-input v-model="queryParams.wareName" placeholder="请输入仓库名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="库区编码" prop="areaCode">
        <el-input v-model="queryParams.areaCode" placeholder="请输入库区编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="库区名称" prop="areaName">
        <el-input v-model="queryParams.areaName" placeholder="请输入库区名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="任务类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择任务类型" clearable>
          <el-option v-for="dict in taskTypes" :key="dict.code" :label="dict.name" :value="parseInt(dict.code)" />
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
      <el-form-item label="任务状态" prop="state">
        <el-input v-model="queryParams.state" placeholder="请输入任务状态" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-input v-model="queryParams.priority" placeholder="请输入优先级" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:WmsTaskInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:WmsTaskInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:WmsTaskInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:WmsTaskInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="WmsTaskInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="任务编号" align="center" prop="taskNo" min-width="120">
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="150">
      </el-table-column>
      <!-- <el-table-column label="库区编码" align="center" prop="areaCode" min-width="120">
      </el-table-column>
      <el-table-column label="库区名称" align="center" prop="areaName" min-width="150">
      </el-table-column> -->
      <el-table-column label="任务类型" align="center" prop="type" min-width="150">
        <template slot-scope="scope">
          <span v-for="item in taskTypes" v-if="scope.row.type == item.code">{{ item.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="托盘编码" align="center" prop="palletCode" min-width="120">
      </el-table-column>
      <el-table-column label="起点位置" align="center" prop="fromCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="终点位置" align="center" prop="toCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="state" min-width="100">
      </el-table-column>
      <!-- <el-table-column label="版本号" align="center" prop="version" width="100">
      </el-table-column> -->
      <el-table-column label="优先级" align="center" prop="priority" min-width="100">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:WmsTaskInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:WmsTaskInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改wms任务对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <!-- <el-form-item label="任务号" prop="taskNo">
          <el-input v-model="form.taskNo" placeholder="请输入任务号" />
        </el-form-item> -->
        <!-- <el-form-item label="WMS任务号" prop="wmsTaskNo">
          <el-input v-model="form.wmsTaskNo" placeholder="请输入WMS任务号" />
        </el-form-item> -->
        <el-form-item label="仓库名称" prop="wareCode">
          <el-select v-model="form.wareCode" placeholder="请选择仓库名称" clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="库区编码" prop="areaCode">
          <el-input v-model="form.areaCode" placeholder="请输入库区编码" />
        </el-form-item>
        <el-form-item label="库区名称" prop="areaName">
          <el-input v-model="form.areaName" placeholder="请输入库区名称" />
        </el-form-item> -->
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option v-for="item in taskTypes" :key="item.code" :label="item.name"
              :value="parseInt(item.code)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="托盘号" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘号" />
        </el-form-item>
        <el-form-item label="起始位置" prop="fromCellCode">
          <el-input v-model="form.fromCellCode" placeholder="请输入起始位置" />
        </el-form-item>
        <el-form-item label="目标位置" prop="toCellCode">
          <el-input v-model="form.toCellCode" placeholder="请输入目标位置" />
        </el-form-item>
        <el-form-item label="状态" prop="state" v-if="form.id != null">
          <el-select v-model="form.state" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.task_state" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
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
import { listWmsTaskInfo, getWmsTaskInfo, delWmsTaskInfo, addWmsTaskInfo, updateWmsTaskInfo } from "@/api/wcs-base/WmsTaskInfo";
import { listWareInfo, getWareInfo, delWareInfo, addWareInfo, updateWareInfo } from "@/api/wcs-base/WareInfo";
import { listTaskType, getTaskType } from "@/api/wcs-base/taskType";

export default {
  name: "WmsTaskInfo",
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
      // wms任务表格数据
      WmsTaskInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      wareInfos: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskNo: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        state: null,
        version: null,
        priority: null
      },
      taskTypes: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
    this.getAllTaskTypes()
  },
  methods: {

    //获取所有的任务类型
    getAllTaskTypes() {
      listTaskType().then((response) => {
        if (response.code == 200) {
          this.taskTypes = response.rows
        }
      })
    },
    getWareInfos() {
      this.loading = true;
      listWareInfo({ isDelete: 0 }).then((response) => {
        this.wareInfos = response.rows;
      });
    },
    /** 查询wms任务列表 */
    getList() {
      this.loading = true;
      listWmsTaskInfo(this.queryParams).then(response => {
        this.WmsTaskInfoList = response.rows;
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
        taskNo: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        createTime: null,
        state: null,
        version: null,
        priority: null
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
      this.getWareInfos();
      this.getAllTaskTypes()
      this.reset();
      this.open = true;
      this.title = "添加wms任务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getWareInfos();
      this.getAllTaskTypes()
      this.reset();
      const id = row.id || this.ids
      getWmsTaskInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改wms任务";
      });
    },
    /** 提交按钮 */
    submitForm() {
      var form = this.form;
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (form.fromCellCode != null && form.toCellCode != null && form.fromCellCode == form.toCellCode) {
            this.$modal.msgError("起始位置不可与终点位置一致")
            return;
          }
          for (var i = 0; i < this.wareInfos.length; i++) {
            if (this.form.wareCode == this.wareInfos[i].code) {
              this.form.wareName = this.wareInfos[i].name
            }
          }
          if (this.form.id != null) {
            updateWmsTaskInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addWmsTaskInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除wms任务编号为"' + ids + '"的数据项？').then(function () {
        return delWmsTaskInfo(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg||"删除失败")
        }
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/WmsTaskInfo/export', {
        ...this.queryParams
      }, `WmsTaskInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
