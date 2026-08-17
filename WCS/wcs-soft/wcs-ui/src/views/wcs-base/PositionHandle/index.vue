<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="路径ID" prop="positionConditionId">
        <el-input
          v-model="queryParams.positionConditionId"
          placeholder="请输入路径ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="执行器ID" prop="handleId">
        <el-input
          v-model="queryParams.handleId"
          placeholder="请输入执行器ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型 " prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型 0-cmd_pre  1-cmd 2-success_re 3-success" clearable>
        
        </el-select>
      </el-form-item>
      <el-form-item label="类名" prop="className">
        <el-input
          v-model="queryParams.className"
          placeholder="请输入类名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="方法名" prop="methodName">
        <el-input
          v-model="queryParams.methodName"
          placeholder="请输入方法名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排序" prop="index">
        <el-input
          v-model="queryParams.index"
          placeholder="请输入排序"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建人" prop="createUserId">
        <el-input
          v-model="queryParams.createUserId"
          placeholder="请输入创建人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建人" prop="createUserName">
        <el-input
          v-model="queryParams.createUserName"
          placeholder="请输入创建人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="更新人" prop="updateUserId">
        <el-input
          v-model="queryParams.updateUserId"
          placeholder="请输入更新人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="更新人" prop="updateUserName">
        <el-input
          v-model="queryParams.updateUserName"
          placeholder="请输入更新人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="删除标志" prop="isDelete">
        <el-input
          v-model="queryParams.isDelete"
          placeholder="请输入删除标志"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="版本号" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入版本号"
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
          v-hasPermi="['wcs-base:PositionHandle:add']"
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
          v-hasPermi="['wcs-base:PositionHandle:edit']"
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
          v-hasPermi="['wcs-base:PositionHandle:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-base:PositionHandle:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="PositionHandleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="路径ID" align="center" prop="positionConditionId">
       
      </el-table-column>
      <el-table-column label="执行器ID" align="center" prop="handleId">
        
      </el-table-column>
      <el-table-column label="类型 " align="center" prop="type">
        
      </el-table-column>
      <el-table-column label="类名" align="center" prop="className">
        
      </el-table-column>
      <el-table-column label="方法名" align="center" prop="methodName">
        
      </el-table-column>
      <el-table-column label="编码" align="center" prop="code">
        
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name">
        
      </el-table-column>
      <el-table-column label="排序" align="center" prop="index">
       
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserId">
       
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserName">
        
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updateUserId">
       
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updateUserName">
        
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="isDelete">
       
      </el-table-column>
      <el-table-column label="版本号" align="center" prop="version">
        
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:PositionHandle:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:PositionHandle:remove']"
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

    <!-- 添加或修改路径执行器对话框 -->
    <el-dialog  v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="路径ID" prop="positionConditionId">
          <el-input v-model="form.positionConditionId" placeholder="请输入路径ID" />
        </el-form-item>
        <el-form-item label="执行器ID" prop="handleId">
          <el-input v-model="form.handleId" placeholder="请输入执行器ID" />
        </el-form-item>
        <el-form-item label="类型 " prop="type">
          <el-select v-model="form.type" placeholder="请选择类型 0-cmd_pre  1-cmd 2-success_re 3-success">
            
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
        <el-form-item label="排序" prop="index">
          <el-input v-model="form.index" placeholder="请输入排序" />
        </el-form-item>
        <el-form-item label="创建人" prop="createUserId">
          <el-input v-model="form.createUserId" placeholder="请输入创建人" />
        </el-form-item>
        <el-form-item label="创建人" prop="createUserName">
          <el-input v-model="form.createUserName" placeholder="请输入创建人" />
        </el-form-item>
        <el-form-item label="更新人" prop="updateUserId">
          <el-input v-model="form.updateUserId" placeholder="请输入更新人" />
        </el-form-item>
        <el-form-item label="更新人" prop="updateUserName">
          <el-input v-model="form.updateUserName" placeholder="请输入更新人" />
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
import { listPositionHandle, getPositionHandle, delPositionHandle, addPositionHandle, updatePositionHandle } from "@/api/wcs-base/PositionHandle";

export default {
  name: "PositionHandle",
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
      // 路径执行器表格数据
      PositionHandleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        positionConditionId: null,
        handleId: null,
        type: null,
        className: null,
        methodName: null,
        code: null,
        name: null,
        index: null,
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
    /** 查询路径执行器列表 */
    getList() {
      this.loading = true;
      listPositionHandle(this.queryParams).then(response => {
        this.PositionHandleList = response.rows;
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
        positionConditionId: null,
        handleId: null,
        type: null,
        className: null,
        methodName: null,
        code: null,
        name: null,
        index: null,
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加路径执行器";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionHandle(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改路径执行器";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePositionHandle(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPositionHandle(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除路径执行器编号为"' + ids + '"的数据项？').then(function() {
        return delPositionHandle(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/PositionHandle/export', {
        ...this.queryParams
      }, `PositionHandle_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
