<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="类型编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入类型编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入类型名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="托盘重量/kg" prop="weight">
        <el-input v-model="queryParams.weight" placeholder="请输入托盘重量/kg" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="托盘长度/cm" prop="length">
        <el-input v-model="queryParams.length" placeholder="请输入托盘长度/cm" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="托盘宽度/cm" prop="width">
        <el-input v-model="queryParams.width" placeholder="请输入托盘宽度/cm" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="托盘高度/cm" prop="height">
        <el-input v-model="queryParams.height" placeholder="请输入托盘高度/cm" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:palletType:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:palletType:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:palletType:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:palletType:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="palletTypeList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="类型编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="类型名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="托盘重量/kg" align="center" prop="weight" min-width="100">
      </el-table-column>
      <el-table-column label="托盘承重/kg" align="center" prop="maxWeight" min-width="100">
      </el-table-column>
      <el-table-column label="托盘长度/cm" align="center" prop="length" min-width="100">
      </el-table-column>
      <el-table-column label="托盘宽度/cm" align="center" prop="width" min-width="100">
      </el-table-column>
      <el-table-column label="托盘高度/cm" align="center" prop="height" min-width="100">
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="120" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:palletType:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:palletType:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改托盘类型对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入类型编码" />
        </el-form-item>
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="托盘重量/kg" prop="weight">
          <el-input v-model="form.weight" placeholder="请输入托盘重量/kg" />
        </el-form-item>
        <el-form-item label="托盘承重/kg" prop="weight">
          <el-input v-model="form.maxWeight" placeholder="请输入托盘承重/kg" />
        </el-form-item>
        <el-form-item label="托盘长度/cm" prop="length">
          <el-input v-model="form.length" placeholder="请输入托盘长度/cm" />
        </el-form-item>
        <el-form-item label="托盘宽度/cm" prop="width">
          <el-input v-model="form.width" placeholder="请输入托盘宽度/cm" />
        </el-form-item>
        <el-form-item label="托盘高度/cm" prop="height">
          <el-input v-model="form.height" placeholder="请输入托盘高度/cm" />
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
import { listPalletType, getPalletType, delPalletType, addPalletType, updatePalletType } from "@/api/wcs-base/palletType";

export default {
  name: "PalletType",
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
      // 托盘类型表格数据
      palletTypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: null,
        name: null,
        weight: null,
        length: null,
        width: null,
        height: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "类型编码不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "类型名称不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询托盘类型列表 */
    getList() {
      this.loading = true;
      listPalletType(this.queryParams).then(response => {
        this.palletTypeList = response.rows;
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
        code: null,
        rfidCode:null,
        name: null,
        realWeight: null,
        weight: null,
        length: null,
        width: null,
        height: null
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
      this.title = "添加托盘类型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPalletType(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改托盘类型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePalletType(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addPalletType(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除托盘类型编号为"' + ids + '"的数据项？').then(function () {
        return delPalletType(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }else{
          this.$modal.msgError(response.msg || "删除失败");
        }
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/palletType/export', {
        ...this.queryParams
      }, `palletType_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
