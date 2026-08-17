<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="起点位置" prop="fromCode">
        <el-input v-model="queryParams.fromCode" placeholder="请输入起点位置" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="终点位置" prop="toCode">
        <el-input v-model="queryParams.toCode" placeholder="请输入终点位置" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="通过时间" prop="taskTime">
        <el-input v-model="queryParams.taskTime" placeholder="请输入通过时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="堵塞时间" prop="blockingTime">
        <el-input v-model="queryParams.blockingTime" placeholder="请输入堵塞时间" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:PositionCondition:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:PositionCondition:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:PositionCondition:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:PositionCondition:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="PositionConditionList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="起点位置" align="center" prop="fromCode" min-width="120">

      </el-table-column>
      <el-table-column label="终点位置" align="center" prop="toCode" min-width="120">

      </el-table-column>
      <el-table-column label="通过时间" align="center" prop="taskTime" min-width="120">

      </el-table-column>
      <el-table-column label="堵塞时间" align="center" prop="blockingTime" min-width="120">

      </el-table-column>
      <el-table-column fixed="right" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:PositionCondition:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:PositionCondition:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改路径对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="起点位置" prop="fromCode">
          <el-input v-model="form.fromCode" placeholder="请输入起点位置" />
        </el-form-item>
        <el-form-item label="终点位置" prop="toCode">
          <el-input v-model="form.toCode" placeholder="请输入终点位置" />
        </el-form-item>
        <el-form-item label="通过时间" prop="taskTime">
          <el-input v-model="form.taskTime" placeholder="请输入通过时间" />
        </el-form-item>
        <el-form-item label="堵塞时间" prop="blockingTime">
          <el-input v-model="form.blockingTime" placeholder="请输入堵塞时间" />
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
import { listPositionCondition, getPositionCondition, delPositionCondition, addPositionCondition, updatePositionCondition } from "@/api/wcs-base/PositionCondition";

export default {
  name: "PositionCondition",
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
      // 路径表格数据
      PositionConditionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fromCode: null,
        toCode: null,
        taskTime: null,
        blockingTime: null
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
    /** 查询路径列表 */
    getList() {
      this.loading = true;
      listPositionCondition(this.queryParams).then(response => {
        this.PositionConditionList = response.rows;
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
        fromCode: null,
        toCode: null,
        taskTime: null,
        blockingTime: null
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
      this.title = "添加路径";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionCondition(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改路径";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePositionCondition(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addPositionCondition(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "新增失败")
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除路径编号为"' + ids + '"的数据项？').then(function () {
        return delPositionCondition(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg || "删除失败")
        }
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/PositionCondition/export', {
        ...this.queryParams
      }, `PositionCondition_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
