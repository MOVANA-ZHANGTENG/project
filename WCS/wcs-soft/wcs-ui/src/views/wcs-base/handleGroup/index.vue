<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分组名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入分组名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="分组描述" prop="memo">
        <el-input v-model="queryParams.memo" placeholder="请输入分组描述" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="分组类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择分组类型" clearable>
          <el-option v-for="item in handleTypes" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="是否禁用" prop="disableState">
        <el-select v-model="queryParams.disableState" placeholder="请选择禁用状态" clearable>
          <el-option v-for="item in dict.type.disable_state" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="删除标志" prop="delFlag">
        <el-select v-model="queryParams.delFlag" placeholder="请选择删除标志" clearable>
          <el-option v-for="item in dict.type.del_flag" :key="item.key" :label="item.label" :value="item.value" />
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
          v-hasPermi="['wcs-base:handleGroup:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:handleGroup:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:handleGroup:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:handleGroup:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="handleGroupList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="分组名称" align="center" prop="name" min-width="120">
      </el-table-column>
      <el-table-column label="分组类型" align="center" prop="type" min-width="100">
        <template slot-scope="scope">
          <div v-for="item in handleTypes">
            <span v-if="scope.row.type == item.value" :style="'color:' + item.color">{{ item.label }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分组描述" align="center" prop="memo" min-width="300px">
        <template slot-scope="scope">
          <div>{{ scope.row.memo }}</div>
        </template>
      </el-table-column>
      <el-table-column label="是否禁用" align="center" prop="disableState" min-width="120">
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.disableState == 0 ? '已启用' : '已禁用'" placement="top">
            <el-switch v-model="scope.row.disableState" @change="updateDisableState(scope.row)" inactive-color="#13ce66"
              active-text="off" inactive-text="on" active-color="#ff4949" active-value="1" inactive-value="0">
            </el-switch>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="delFlag" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.delFlag" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="180">
      </el-table-column>
      <!-- <el-table-column label="创建人ID" align="center" prop="createUserId">
      </el-table-column> -->
      <el-table-column label="创建人姓名" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" min-width="180">
      </el-table-column>
      <!-- <el-table-column label="更新人ID" align="center" prop="updateUserId">
      </el-table-column> -->
      <el-table-column label="更新人姓名" align="center" prop="updateUserName" min-width="120">

      </el-table-column>
      <!-- <el-table-column label="版本号" align="center" prop="version">
      </el-table-column> -->
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:handleGroup:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:handleGroup:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改分组管理对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分组名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分组名称" />
        </el-form-item>
        <el-form-item label="分组类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择分组类型">
            <el-option v-for="item in handleTypes" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分组描述" prop="memo">
          <el-input v-model="form.memo" type="textarea" placeholder="请输入分组描述" :autosize="{ minRows: 4, maxRows: 4 }"
            :style="{ width: '100%' }" />
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
import { listHandleGroup, getHandleGroup, delHandleGroup, addHandleGroup, updateHandleGroup } from "@/api/wcs-base/handleGroup";

export default {
  name: "HandleGroup",
  dicts: ["del_flag", "disable_state"],
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
      // 分组管理表格数据
      handleGroupList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        memo: null,
        disableState: null,
        delFlag: '0',
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        version: null
      },
      handleTypes: [
        { value: 0, label: 'jobInfo', color: '#409EFF' },
        { value: 1, label: 'pathInfo', color: '#67C23A' },
        { value: 2, label: 'callBox', color: '#F56C6C' },
        { value: 3, label: 'scanCode', color: '#E6A23C' },
      ],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "分组名称不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /**
     * 修改分组的禁用状态
     */
    updateDisableState(object) {
      var that = this
      // that.$modal.loading("正在切换模式，请稍等")
      updateHandleGroup(object).then(response => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.getList();
        }
      });
      // that.$modal.closeLoading()
    },
    /** 查询分组管理列表 */
    getList() {
      this.loading = true;
      listHandleGroup(this.queryParams).then(response => {
        this.handleGroupList = response.rows;
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
        name: null,
        memo: null,
        disableState: null,
        delFlag: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加分组管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getHandleGroup(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改分组管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateHandleGroup(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addHandleGroup(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
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
      this.$modal.confirm('是否确认删除分组管理编号为"' + ids + '"的数据项？').then(function () {
        return delHandleGroup(ids);
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
      this.download('wcs-base/handleGroup/export', {
        ...this.queryParams
      }, `handleGroup_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
