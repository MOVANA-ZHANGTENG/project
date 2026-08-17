<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" placeholder="请选择仓库" clearable style="width: 200px">
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="配方代码" prop="recipeId">
        <el-input v-model="queryParams.recipeId" placeholder="请输入配方代码" clearable style="width: 200px"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="配方名称" prop="recipeName">
        <el-input v-model="queryParams.recipeName" placeholder="请输入配方名称" clearable style="width: 200px"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="是否过期" prop="isExpired">
        <el-select v-model="queryParams.isExpired" placeholder="是否过期" clearable style="width: 150px">
          <el-option label="不过期" :value="0" />
          <el-option label="过期" :value="1" />
        </el-select>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-ds:bagStatistics:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 统计表格 -->
    <el-table v-loading="loading" :data="statisticsList" border>
      <el-table-column label="仓库编码" align="center" prop="wareCode" width="150" />
      <el-table-column label="仓库名称" align="center" prop="wareName" width="200" />
      <el-table-column label="配方代码" align="center" prop="recipeId" width="200" />
      <el-table-column label="配方名称" align="center" prop="recipeName" min-width="250" show-overflow-tooltip />
      <el-table-column label="配方版本" align="center" prop="recipeRel" width="150" />
      <el-table-column label="袋子数量" align="center" prop="bagCount" width="150">
        <template slot-scope="scope">
          <span style="color: #409EFF; font-weight: bold; font-size: 16px;">
            {{ scope.row.bagCount }}
          </span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
import request from "@/utils/request";
import { listWareInfo } from "@/api/wcs-base/WareInfo";

export default {
  name: "BagStatistics",
  data() {
    return {
      // 遮罩层
      loading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 统计列表
      statisticsList: [],
      // 仓库列表
      wareInfos: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wareCode: null,
        recipeId: null,
        recipeName: null,
        isExpired: null
      }
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
  },
  methods: {
    /** 查询统计列表 */
    getList() {
      this.loading = true;
      var that = this;
      request({
        url: "/wcs-ds/bagStatistics/list",
        method: "get",
        params: this.queryParams
      }).then((response) => {
        if (response.code == 200) {
          that.statisticsList = response.rows;
          that.total = response.total;
        } else {
          that.$modal.msgError(response.msg);
        }
        this.loading = false;
      });
    },

    /** 获取仓库列表 */
    getWareInfos() {
      var that = this;
      var query = { isDelete: 0 };
      listWareInfo(query).then((response) => {
        if (response.code == 200) {
          that.wareInfos = response.rows;
        }
      });
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

    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-ds/bagStatistics/export', {
        ...this.queryParams
      }, `料号袋子统计_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

<style scoped>
/* 可以添加自定义样式 */
</style>

