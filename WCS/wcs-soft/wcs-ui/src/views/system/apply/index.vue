<template>
  <div class="app-container">
    <el-form :model="queryParams"
             ref="queryForm"
             size="small"
             :inline="true"
             v-show="showSearch"
             label-width="68px">
      <el-form-item label="ID"
                    prop="boxTargetApplyId">
        <el-input v-model="queryParams.boxTargetApplyId"
                  placeholder="请输入ID"
                  clearable
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="托盘号"
                    prop="boxCode">
        <el-input v-model="queryParams.boxCode"
                  placeholder="请输入托盘号"
                  clearable
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="申请时位置"
                    prop="pointCode">
        <el-input v-model="queryParams.pointCode"
                  placeholder="请输入申请时位置"
                  clearable
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态"
                    prop="state">
        <el-input v-model="queryParams.state"
                  placeholder="请输入状态"
                  clearable
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型"
                    prop="type">
        <el-input v-model="queryParams.type"
                  placeholder="请输入类型"
                  clearable
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary"
                   icon="el-icon-search"
                   size="mini"
                   @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh"
                   size="mini"
                   @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10"
            class="mb8">
      <el-col :span="1.5">
        <el-button type="primary"
                   plain
                   icon="el-icon-plus"
                   size="mini"
                   @click="handleAdd"
                   v-hasPermi="['system:apply:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success"
                   plain
                   icon="el-icon-edit"
                   size="mini"
                   :disabled="single"
                   @click="handleUpdate"
                   v-hasPermi="['system:apply:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger"
                   plain
                   icon="el-icon-delete"
                   size="mini"
                   :disabled="multiple"
                   @click="handleDelete"
                   v-hasPermi="['system:apply:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning"
                   plain
                   icon="el-icon-download"
                   size="mini"
                   @click="handleExport"
                   v-hasPermi="['system:apply:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch"
                     @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading"
              :data="applyList"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection"
                       width="55"
                       align="center" />
      <el-table-column label="ID"
                       align="center"
                       prop="boxTargetApplyId" />
      <el-table-column label="托盘号"
                       align="center"
                       prop="boxCode" />
      <el-table-column label="位置"
                       align="center"
                       prop="pointCode" />
      <el-table-column label="状态 "
                       align="center"
                       prop="state">
        <template slot-scope="scope">
          <div v-if="scope.row.state==0"
               style="color:#909399">初始化</div>
          <div v-if="scope.row.state==1"
               style="color:#67C23A">处理中</div>
          <div v-if="scope.row.state==2"
               style="color:#409EFF">已处理</div>
        </template>
      </el-table-column>
      <el-table-column label="类型"
                       align="center"
                       prop="type">
        <template slot-scope="scope">
          <div v-if="scope.row.type==1">入库申请</div>
          <div v-if="scope.row.type==2">出库口目的地申请</div>
        </template>
      </el-table-column>
      <el-table-column label="操作"
                       align="center"
                       class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini"
                     type="text"
                     icon="el-icon-edit"
                     @click="handleUpdate(scope.row)"
                     v-hasPermi="['system:apply:edit']">修改</el-button>
          <el-button size="mini"
                     type="text"
                     icon="el-icon-delete"
                     @click="handleDelete(scope.row)"
                     v-hasPermi="['system:apply:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0"
                :total="total"
                :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize"
                @pagination="getList" />

    <!-- 添加或修改目的地申请对话框 -->
    <el-dialog :title="title"
               :visible.sync="open"
               width="500px"
               append-to-body>
      <el-form ref="form"
               :model="form"
               :rules="rules"
               label-width="80px">
        <el-form-item label="ID"
                      prop="boxTargetApplyId">
          <el-input v-model="form.boxTargetApplyId"
                    placeholder="请输入ID" />
        </el-form-item>
        <el-form-item label="托盘号"
                      prop="boxCode">
          <el-input v-model="form.boxCode"
                    placeholder="请输入托盘号" />
        </el-form-item>
        <el-form-item label="位置"
                      prop="pointCode">
          <el-input v-model="form.pointCode"
                    placeholder="请输入申请时位置" />
        </el-form-item>
        <el-form-item label="类型"
                      prop="type">
          <el-radio v-model="form.type"
                    label="1">入库申请</el-radio>
          <el-radio v-model="form.type"
                    label="2">出库口目的地申请</el-radio>
        </el-form-item>
      </el-form>
      <div slot="footer"
           class="dialog-footer">
        <el-button type="primary"
                   @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listApply, getApply, delApply, addApply, updateApply } from "@/api/system/apply";

export default {
  name: "Apply",
  data () {
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
      // 目的地申请表格数据
      applyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        boxTargetApplyId: null,
        boxCode: null,
        pointCode: null,
        state: null,
        type: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created () {
    this.getList();
  },
  methods: {
    /** 查询目的地申请列表 */
    getList () {
      this.loading = true;
      listApply(this.queryParams).then(response => {
        this.applyList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel () {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset () {
      this.form = {
        boxTargetApplyId: null,
        boxCode: null,
        pointCode: null,
        createTime: null,
        state: 0,
        type: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery () {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery () {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange (selection) {
      this.ids = selection.map(item => item.boxTargetApplyId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加目的地申请";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const boxTargetApplyId = row.boxTargetApplyId || this.ids
      getApply(boxTargetApplyId).then(response => {
        this.open = true;
        this.title = "修改目的地申请";
        this.form = response.data;
      });
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.boxTargetApplyId != null) {
            updateApply(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addApply(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete (row) {
      const boxTargetApplyIds = row.boxTargetApplyId || this.ids;
      this.$modal.confirm('是否确认删除目的地申请编号为"' + boxTargetApplyIds + '"的数据项？').then(function () {
        return delApply(boxTargetApplyIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('system/apply/export', {
        ...this.queryParams
      }, `apply_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
