<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="密炼机编码" prop="deviceId">
        <el-input v-model="queryParams.deviceId" placeholder="请输入密炼机编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="密炼计划号" prop="planId">
        <el-input v-model="queryParams.planId" placeholder="请输入密炼计划号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="小料计划号" prop="planNo">
        <el-input v-model="queryParams.planNo" placeholder="请输入小料计划号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="小料ID" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入小料ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="小料名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入小料名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="配方号" prop="recipeId">
        <el-input v-model="queryParams.recipeId" placeholder="请输入配方号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态">
            <el-option  :style="'color:'+item.color " v-for="item in states" :value="item.value" :key="item.value" :label="item.label"
           ></el-option>
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
          v-hasPermi="['wcs-ds:DsRealPlan:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-ds:DsRealPlan:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-ds:DsRealPlan:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-ds:DsRealPlan:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>


      <el-table v-loading="loading" :data="DsRealPlanList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="密炼机编码" align="center" prop="deviceId" min-width="120">
      </el-table-column>
      <el-table-column label="密炼计划号" align="center" prop="planId" min-width="120">
        <template slot-scope="scope">
          <router-link :to="'/Inventory/DsPlanPallet/' + scope.row.id" class="link-type">
            <span>{{ scope.row.planId }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="小料计划号" align="center" prop="planNo" min-width="120">
        <template slot-scope="scope">
          <router-link :to="'/Inventory/DsPlanPallet/' + scope.row.id" class="link-type">
            <span>{{ scope.row.planNo }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="小料ID" align="center" prop="productId" min-width="120">
      </el-table-column>
      <el-table-column label="小料名称" align="center" prop="productName" min-width="120">
      </el-table-column>
      <el-table-column label="生产顺序" align="center" prop="sequence" min-width="120">
      </el-table-column>

      <el-table-column label="生产数量" align="center" prop="qty" min-width="120">
      </el-table-column>
      <el-table-column label="分配箱数" align="center" prop="allotPalletCount" min-width="120">
      </el-table-column>
      <el-table-column label="单次投料用量" align="center" prop="singleQty" min-width="120">
      </el-table-column>
      <el-table-column label="配方号" align="center" prop="recipeId" min-width="120">
      </el-table-column>
      <el-table-column label="计划状态" align="center" prop="state" min-width="120">
        <template slot-scope="scope">
          <span v-for="item in states" v-if="scope.row.state==item.value" :style="'color:'+item.color">{{item.label }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="120">
      </el-table-column>
      <el-table-column label="MES预警状态" align="center" prop="mesState1" min-width="120">
      </el-table-column>
      <el-table-column label="MES完成状态" align="center" prop="mesState2" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds:DsRealPlan:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds:DsRealPlan:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>




    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改生产计划对话框 -->
    <el-dialog v-loading="loading" :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="密炼机编码" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入密炼机编码" />
        </el-form-item>
        <el-form-item label="planId" prop="planId">
          <el-input v-model="form.planId" placeholder="请输入密炼计划号" />
        </el-form-item>
        <el-form-item label="planNo" prop="planNo">
          <el-input v-model="form.planNo" placeholder="请输入小料计划号" />
        </el-form-item>
        <el-form-item label="小料ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入小料ID" />
        </el-form-item>
        <el-form-item label="小料名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入小料名称" />
        </el-form-item>
        <!-- <el-form-item label="生产顺序" prop="sequence">
          <el-input v-model="form.sequence" placeholder="请输入生产顺序" />
        </el-form-item> -->
        <el-form-item label="生产数量" prop="qty">
          <el-input v-model="form.qty" placeholder="请输入生产数量" />
        </el-form-item>
        <el-form-item label="单次投料用量" prop="singleQty">
          <el-input v-model="form.singleQty" placeholder="请输入单次投料用量" />
        </el-form-item>
        <!-- <el-form-item label="配方号" prop="recipeId">
          <el-input v-model="form.recipeId" placeholder="请输入配方号" />
        </el-form-item> -->

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDsRealPlan, getDsRealPlan, delDsRealPlan, addDsRealPlan, updateDsRealPlan } from "@/api/wcs-ds/DsRealPlan";
import request from "@/utils/request";
import PalletRecord from "../..//wcs-task/PalletRecord/index.vue"
export default {
  name: "DsRealPlan",
  data() {
    return {

       states:[
             {value: 0, label: '未开始',color: 'primary'}
             ,    {value: 1, label: '已全部分配',color: '#409EFF'}
             ,    {value: 2, label: '已全部下架',color: '#E6A23C'}
             ,    {value: 3, label: '已全部到达出库口',color: '#E6A23C'}
             ,    {value: 4, label: '已全部上AGV',color: '#67C23A'}
             ,    {value: 5, label: '已全部投料',color: '#67C23A'}
             ,    {value: -1, label: '取消',color: '#909399'}
           ],
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
      // 生产计划表格数据
      DsRealPlanList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: null,
        planId: null,
        planNo: null,
        productId: null,
        productName: null,
        sequence: null,
        qty: null,
        singleQty: null,
        recipeId: null,
        state: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  components: {  
    PalletRecord, 
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询生产计划列表 */
    getList() {
      this.loading = true;
      listDsRealPlan(this.queryParams).then(response => {
        if (response.code == 200) {
          this.DsRealPlanList = response.rows;
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
        deviceId: null,
        planId: null,
        planNo: null,
        productId: null,
        productName: null,
        sequence: null,
        qty: null,
        singleQty: null,
        recipeId: null,
        createTime: null,
        updateTime: null,
        state: null
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
      this.title = "添加生产计划";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDsRealPlan(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改生产计划";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDsRealPlan(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addDsRealPlan(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除生产计划编号为"' + ids + '"的数据项？').then(function () {
        return delDsRealPlan(ids);
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
      this.download('wcs-ds/DsRealPlan/export', {
        ...this.queryParams
      }, `DsRealPlan_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
