<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="工序编码" prop="proProcessCode">
        <el-input
          v-model="queryParams.proProcessCode"
          placeholder="请输入工序编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工序名称" prop="proProcessName">
        <el-input
          v-model="queryParams.proProcessName"
          placeholder="请输入工序名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="NEXT工序ID" prop="nextProPorcessId">
        <el-input
          v-model="queryParams.nextProPorcessId"
          placeholder="请输入NEXT工序ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="NEXT工序编码" prop="nextProProcessCode">
        <el-input
          v-model="queryParams.nextProProcessCode"
          placeholder="请输入NEXT工序编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="NEXT工序名称" prop="nextProProcessName">
        <el-input
          v-model="queryParams.nextProProcessName"
          placeholder="请输入NEXT工序名称"
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
          v-hasPermi="['wcs-base:ProRouteProcess:add']"
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
          v-hasPermi="['wcs-base:ProRouteProcess:edit']"
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
          v-hasPermi="['wcs-base:ProRouteProcess:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-base:ProRouteProcess:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProRouteProcessList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="工艺流程ID" align="center" prop="proRouteId" min-width="100" />
      <el-table-column label="工序ID" align="center" prop="proProcessId" min-width="100" />
    <el-table-column label="工序编码" align="center" prop="proProcessCode"  min-width="120">
    </el-table-column>
    <el-table-column label="工序名称" align="center" prop="proProcessName"  min-width="120">
    </el-table-column>
    <el-table-column label="NEXT工序ID" align="center" prop="nextProPorcessId"  min-width="120">
    </el-table-column>
    <el-table-column label="NEXT工序编码" align="center" prop="nextProProcessCode"  min-width="120">
    </el-table-column>
    <el-table-column label="NEXT工序名称" align="center" prop="nextProProcessName"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:ProRouteProcess:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:ProRouteProcess:remove']"
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

    <!-- 添加或修改工艺流程工序关联对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="工序编码" prop="proProcessCode">
          <el-input v-model="form.proProcessCode" placeholder="请输入工序编码" />
        </el-form-item>
        <el-form-item label="工序名称" prop="proProcessName">
          <el-input v-model="form.proProcessName" placeholder="请输入工序名称" />
        </el-form-item>
        <el-form-item label="NEXT工序ID" prop="nextProPorcessId">
          <el-input v-model="form.nextProPorcessId" placeholder="请输入NEXT工序ID" />
        </el-form-item>
        <el-form-item label="NEXT工序编码" prop="nextProProcessCode">
          <el-input v-model="form.nextProProcessCode" placeholder="请输入NEXT工序编码" />
        </el-form-item>
        <el-form-item label="NEXT工序名称" prop="nextProProcessName">
          <el-input v-model="form.nextProProcessName" placeholder="请输入NEXT工序名称" />
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
import { listProRouteProcess, getProRouteProcess, delProRouteProcess, addProRouteProcess, updateProRouteProcess } from "@/api/wcs-base/ProRouteProcess";

export default {
  name: "ProRouteProcess",
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
      // 工艺流程工序关联表格数据
      ProRouteProcessList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        proProcessCode: null,
        proProcessName: null,
        nextProPorcessId: null,
        nextProProcessCode: null,
        nextProProcessName: null
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
    /** 查询工艺流程工序关联列表 */
    getList() {
      this.loading = true;
      listProRouteProcess(this.queryParams).then(response => {
          if(response.code==200){
            this.ProRouteProcessList = response.rows;
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
        proRouteId: null,
        proProcessId: null,
        proProcessCode: null,
        proProcessName: null,
        nextProPorcessId: null,
        nextProProcessCode: null,
        nextProProcessName: null
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
      this.title = "添加工艺流程工序关联";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProRouteProcess(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改工艺流程工序关联";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProRouteProcess(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProRouteProcess(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除工艺流程工序关联编号为"' + ids + '"的数据项？').then(function() {
        return delProRouteProcess(ids);
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
      this.download('wcs-base/ProRouteProcess/export', {
        ...this.queryParams
      }, `ProRouteProcess_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
