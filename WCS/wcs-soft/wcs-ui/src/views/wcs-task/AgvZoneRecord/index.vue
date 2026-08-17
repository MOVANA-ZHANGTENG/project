<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input
          v-model="queryParams.wareCode"
          placeholder="请输入仓库编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库名" prop="wareName">
        <el-input
          v-model="queryParams.wareName"
          placeholder="请输入仓库名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交管区编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入交管区编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="AGV厂家" prop="agvType">
        <el-input
          v-model="queryParams.agvType"
          placeholder="AGV厂家 "
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="事件类型" prop="eventType">
        <el-select v-model="queryParams.eventType" placeholder="请选择事件类型 " clearable>
          <el-option
            v-for="item in eventTypes"
            :key="item.value"
            :label="item.label"
            :value="Number(item.value)"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态 " prop="state">
        <el-input
          v-model="queryParams.state"
          placeholder="请输入状态 "
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="备注" prop="memo">
        <el-input
          v-model="queryParams.memo"
          placeholder="请输入备注"
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
          v-hasPermi="['wcs-task:AgvZoneRecord:add']"
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
          v-hasPermi="['wcs-task:AgvZoneRecord:edit']"
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
          v-hasPermi="['wcs-task:AgvZoneRecord:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-task:AgvZoneRecord:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="AgvZoneRecordList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
    <!-- <el-table-column label="仓库编码" align="center" prop="wareCode"  min-width="120">
    </el-table-column>
    <el-table-column label="仓库名" align="center" prop="wareName"  min-width="120">
    </el-table-column> -->
    <el-table-column label="交管区编码" align="center" prop="code"  min-width="120">
    </el-table-column>
      <el-table-column label="AGV厂家" align="center" prop="agvType"  min-width="120">
        
      </el-table-column>
      <el-table-column label="事件类型" align="center" prop="eventType"  min-width="120">
        
      </el-table-column>
      <el-table-column label="时间" align="center" prop="createTime"  min-width="120">
      </el-table-column>
    <el-table-column label="状态" align="center" prop="state"  min-width="120">
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
            v-hasPermi="['wcs-task:AgvZoneRecord:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-task:AgvZoneRecord:remove']"
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

    <!-- 添加或修改交管日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="仓库编码" prop="wareCode">
          <el-input v-model="form.wareCode" placeholder="请输入仓库编码" />
        </el-form-item>
        <el-form-item label="仓库名" prop="wareName">
          <el-input v-model="form.wareName" placeholder="请输入仓库名" />
        </el-form-item>
        <el-form-item label="交管区编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入交管区编码" />
        </el-form-item>
        <el-form-item label="AGV厂家" prop="agvType">
          <el-input v-model="form.agvType" placeholder="AGV厂家" />
        </el-form-item>
        <el-form-item label="事件类型 " prop="eventType">
          <el-input v-model="form.eventType" placeholder="事件类型" />
        </el-form-item>
        <el-form-item label="状态" prop="state">
          <el-input v-model="form.state" placeholder="请输入状态" />
        </el-form-item>
        <el-form-item label="备注" prop="memo">
          <el-input v-model="form.memo" placeholder="请输入备注" />
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
import { listAgvZoneRecord, getAgvZoneRecord, delAgvZoneRecord, addAgvZoneRecord, updateAgvZoneRecord } from "@/api/wcs-task/AgvZoneRecord";

export default {
  name: "AgvZoneRecord",
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
      // 交管日志表格数据
      AgvZoneRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wareCode: null,
        wareName: null,
        code: null,
        agvType: null,
        eventType: null,
        state: null,
        memo: null
      },

      eventTypes:[
        {value:1,label:"申请进入"}
        , {value:2,label:"离开"}
      ],
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
    /** 查询交管日志列表 */
    getList() {
      this.loading = true;
      listAgvZoneRecord(this.queryParams).then(response => {
          if(response.code==200){
            this.AgvZoneRecordList = response.rows;
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
        wareCode: null,
        wareName: null,
        code: null,
        agvType: null,
        eventType: null,
        state: null,
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
      this.title = "添加交管日志";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAgvZoneRecord(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改交管日志";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAgvZoneRecord(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addAgvZoneRecord(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除交管日志编号为"' + ids + '"的数据项？').then(function() {
        return delAgvZoneRecord(ids);
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
      this.download('wcs-task/AgvZoneRecord/export', {
        ...this.queryParams
      }, `AgvZoneRecord_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
