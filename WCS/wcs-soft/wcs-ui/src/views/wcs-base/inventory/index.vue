<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="物料信息" prop="productCode">
        <el-input v-model="queryParams.productCode" placeholder="请输入物料信息" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="物料名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入物料名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="批次号" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="物料规格" prop="spec">
        <el-input v-model="queryParams.spec" placeholder="请输入物料规格" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="物料数量" prop="quantity">
        <el-input v-model="queryParams.quantity" placeholder="请输入物料数量" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input v-model="queryParams.palletCode" placeholder="请输入托盘编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="生产日期" prop="productDate">
        <el-date-picker clearable v-model="queryParams.productDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择生产日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="入库日期" prop="inDate">
        <el-date-picker clearable v-model="queryParams.inDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择入库日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="过期日期" prop="exDate">
        <el-date-picker clearable v-model="queryParams.exDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择过期日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:inventory:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:inventory:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:inventory:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:inventory:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="inventoryList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="物料信息" align="center" prop="productCode" min-width="120">
      </el-table-column>
      <el-table-column label="物料名称" align="center" prop="productName" min-width="120">
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="batchNo" min-width="120">
      </el-table-column>
      <el-table-column label="物料规格" align="center" prop="spec" min-width="120">
      </el-table-column>
      <el-table-column label="物料数量" align="center" prop="quantity" min-width="120">
      </el-table-column>
      <el-table-column label="托盘编码" align="center" prop="palletCode" min-width="120">
      </el-table-column>
      <el-table-column label="生产日期" align="center" prop="productDate" width="180">

      </el-table-column>
      <el-table-column label="入库日期" align="center" prop="inDate" width="180">

      </el-table-column>
      <el-table-column label="过期日期" align="center" prop="exDate" width="180">

      </el-table-column>
      <!-- <el-table-column label="版本号" align="center" prop="version" min-width="120">
      </el-table-column> -->
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:inventory:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:inventory:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改库存信息对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="物料信息" prop="productCode">
          <el-input v-model="form.productCode" placeholder="请输入物料信息" />
        </el-form-item>
        <el-form-item label="物料名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="form.batchNo" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="物料规格" prop="spec">
          <el-input v-model="form.spec" placeholder="请输入物料规格" />
        </el-form-item>
        <el-form-item label="物料数量" prop="quantity">
          <el-input v-model="form.quantity" placeholder="请输入物料数量" />
        </el-form-item>
        <el-form-item label="托盘编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘编码" />
        </el-form-item>
        <el-form-item label="生产日期" prop="productDate">
          <el-date-picker clearable v-model="form.productDate" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择生产日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="入库日期" prop="inDate">
          <el-date-picker clearable v-model="form.inDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择入库日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="过期日期" prop="exDate">
          <el-date-picker clearable v-model="form.exDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择过期日期">
          </el-date-picker>
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
import { listInventory, getInventory, delInventory, addInventory, updateInventory } from "@/api/wcs-base/inventory";

export default {
  name: "Inventory",
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
      // 库存信息表格数据
      inventoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productCode: null,
        productName: null,
        batchNo: null,
        spec: null,
        quantity: null,
        palletCode: null,
        productDate: null,
        inDate: null,
        exDate: null,
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
    /** 查询库存信息列表 */
    getList() {
      this.loading = true;
      listInventory(this.queryParams).then(response => {
        if (response.code == 200) {
          this.inventoryList = response.rows;
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
        productCode: null,
        productName: null,
        batchNo: null,
        spec: null,
        quantity: null,
        palletCode: null,
        productDate: null,
        inDate: null,
        exDate: null,
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
      this.title = "添加库存信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInventory(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改库存信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateInventory(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addInventory(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除库存信息编号为"' + ids + '"的数据项？').then(function () {
        return delInventory(ids);
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
      this.download('wcs-base/inventory/export', {
        ...this.queryParams
      }, `inventory_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
