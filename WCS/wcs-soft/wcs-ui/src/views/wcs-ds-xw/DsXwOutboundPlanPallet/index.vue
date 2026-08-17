<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="计划ID，关联ds_xw_outbound_plan.id" prop="planId">
        <el-input
          v-model="queryParams.planId"
          placeholder="请输入计划ID，关联ds_xw_outbound_plan.id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="框子编码" prop="palletCode">
        <el-input
          v-model="queryParams.palletCode"
          placeholder="请输入框子编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态：pending/downing/downed/completed" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态：pending/downing/downed/completed" clearable>
          <el-option label="待处理" value="pending" />
          <el-option label="下架中" value="downing" />
          <el-option label="已下架" value="downed" />
          <el-option label="已完成" value="completed" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务ID，关联task_info.id，可为空" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
          placeholder="请输入任务ID，关联task_info.id，可为空"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下架时间，格式：yyyy-MM-dd HH:mm:ss" prop="downTime">
        <el-input
          v-model="queryParams.downTime"
          placeholder="请输入下架时间，格式：yyyy-MM-dd HH:mm:ss"
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
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlanPallet:add']"
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
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlanPallet:edit']"
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
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlanPallet:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlanPallet:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="DsXwOutboundPlanPalletList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" min-width="100" />
    <el-table-column label="计划ID，关联ds_xw_outbound_plan.id" align="center" prop="planId"  min-width="120">
    </el-table-column>
    <el-table-column label="框子编码" align="center" prop="palletCode"  min-width="120">
    </el-table-column>
      <el-table-column label="状态：pending/downing/downed/completed" align="center" prop="status"  min-width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 'pending'" type="info">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === 'downing'" type="warning">下架中</el-tag>
          <el-tag v-else-if="scope.row.status === 'downed'" type="success">已下架</el-tag>
          <el-tag v-else-if="scope.row.status === 'completed'" type="success">已完成</el-tag>
          <span v-else>{{ scope.row.status || '-' }}</span>
        </template>
      </el-table-column>
    <el-table-column label="任务ID，关联task_info.id，可为空" align="center" prop="taskId"  min-width="120">
    </el-table-column>
    <el-table-column label="下架时间，格式：yyyy-MM-dd HH:mm:ss" align="center" prop="downTime"  min-width="120">
    </el-table-column>
      <el-table-column label="备注" align="center" prop="memo"  min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds-xw:DsXwOutboundPlanPallet:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds-xw:DsXwOutboundPlanPallet:remove']"
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

    <!-- 添加或修改出库计划框子关联对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="计划ID，关联ds_xw_outbound_plan.id" prop="planId">
          <el-input v-model="form.planId" placeholder="请输入计划ID，关联ds_xw_outbound_plan.id" />
        </el-form-item>
        <el-form-item label="框子编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入框子编码" />
        </el-form-item>
        <el-form-item label="状态：pending/downing/downed/completed" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="pending">待处理</el-radio>
            <el-radio label="downing">下架中</el-radio>
            <el-radio label="downed">已下架</el-radio>
            <el-radio label="completed">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="任务ID，关联task_info.id，可为空" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入任务ID，关联task_info.id，可为空" />
        </el-form-item>
        <el-form-item label="下架时间，格式：yyyy-MM-dd HH:mm:ss" prop="downTime">
          <el-input v-model="form.downTime" placeholder="请输入下架时间，格式：yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注" prop="memo">
          <el-input v-model="form.memo" type="textarea" placeholder="请输入内容" />
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
import { listDsXwOutboundPlanPallet, getDsXwOutboundPlanPallet, delDsXwOutboundPlanPallet, addDsXwOutboundPlanPallet, updateDsXwOutboundPlanPallet } from "@/api/wcs-ds-xw/DsXwOutboundPlanPallet";
import request from "@/utils/request";
export default {
  name: "DsXwOutboundPlanPallet",
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
      // 出库计划框子关联表格数据
      DsXwOutboundPlanPalletList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        planId: null,
        palletCode: null,
        status: null,
        taskId: null,
        downTime: null,
        memo: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        planId: [
          { required: true, message: "计划ID，关联ds_xw_outbound_plan.id不能为空", trigger: "blur" }
        ],
        palletCode: [
          { required: true, message: "框子编码不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询出库计划框子关联列表 */
    getList() {
      this.loading = true;
      listDsXwOutboundPlanPallet(this.queryParams).then(response => {
          if(response.code==200){
            this.DsXwOutboundPlanPalletList = response.rows;
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
        planId: null,
        palletCode: null,
        status: null,
        taskId: null,
        downTime: null,
        createTime: null,
        memo: null
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
      this.title = "添加出库计划框子关联";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDsXwOutboundPlanPallet(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改出库计划框子关联";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDsXwOutboundPlanPallet(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addDsXwOutboundPlanPallet(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除出库计划框子关联编号为"' + ids + '"的数据项？').then(function() {
        return delDsXwOutboundPlanPallet(ids);
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
      this.download('wcs-ds-xw/DsXwOutboundPlanPallet/export', {
        ...this.queryParams
      }, `DsXwOutboundPlanPallet_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
