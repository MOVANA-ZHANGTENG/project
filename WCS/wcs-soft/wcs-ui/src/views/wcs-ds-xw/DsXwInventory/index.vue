<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="流转卡号" prop="lzkh">
        <el-input
          v-model="queryParams.lzkh"
          placeholder="请输入流转卡号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="检验编号" prop="jybh">
        <el-input
          v-model="queryParams.jybh"
          placeholder="请输入检验编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="缸号" prop="gh">
        <el-input
          v-model="queryParams.gh"
          placeholder="请输入缸号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="计划单号" prop="jhdh">
        <el-input
          v-model="queryParams.jhdh"
          placeholder="请输入计划单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input
          v-model="queryParams.palletCode"
          placeholder="请输入托盘编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户名称" prop="khmch">
        <el-input
          v-model="queryParams.khmch"
          placeholder="请输入客户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="品号" prop="bh">
        <el-input
          v-model="queryParams.bh"
          placeholder="请输入品号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="品名" prop="pm">
        <el-input
          v-model="queryParams.pm"
          placeholder="请输入品名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="条形码" prop="txm">
        <el-input
          v-model="queryParams.txm"
          placeholder="请输入条形码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="text" icon="el-icon-arrow-down" size="mini" @click="toggleAdvancedSearch" v-if="!showAdvancedSearch">展开</el-button>
        <el-button type="text" icon="el-icon-arrow-up" size="mini" @click="toggleAdvancedSearch" v-else>收起</el-button>
      </el-form-item>
    </el-form>

    <!-- 高级搜索 -->
    <el-form :model="queryParams" ref="advancedQueryForm" size="small" :inline="true" v-show="showSearch && showAdvancedSearch" label-position="left" label-width="100px">
      <el-form-item label="验布车间" prop="cj">
        <el-input
          v-model="queryParams.cj"
          placeholder="请输入验布车间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户品号" prop="khbh">
        <el-input
          v-model="queryParams.khbh"
          placeholder="请输入客户品号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户品名" prop="khpm">
        <el-input
          v-model="queryParams.khpm"
          placeholder="请输入客户品名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户色称" prop="khsc">
        <el-input
          v-model="queryParams.khsc"
          placeholder="请输入客户色称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="色称" prop="sc">
        <el-input
          v-model="queryParams.sc"
          placeholder="请输入色称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="批次" prop="hh">
        <el-input
          v-model="queryParams.hh"
          placeholder="请输入批次"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成分" prop="sz">
        <el-input
          v-model="queryParams.sz"
          placeholder="请输入成分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="匹号" prop="ph">
        <el-input
          v-model="queryParams.ph"
          placeholder="请输入匹号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="验布类型" prop="yblx">
        <el-input
          v-model="queryParams.yblx"
          placeholder="请输入验布类型"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="验布机台" prop="sbbh">
        <el-input
          v-model="queryParams.sbbh"
          placeholder="请输入验布机台"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['wcs-ds-xw:DsXwInventory:add']"
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
          v-hasPermi="['wcs-ds-xw:DsXwInventory:edit']"
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
          v-hasPermi="['wcs-ds-xw:DsXwInventory:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-ds-xw:DsXwInventory:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table 
      v-loading="loading" 
      :data="DsXwInventoryList" 
      @selection-change="handleSelectionChange" 
      @expand-change="handleExpandChange"
      border
      row-key="id"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="expand" width="50">
        <template slot-scope="scope">
          <div class="bind-history-container">
            <div class="bind-history-header">
              <span class="bind-history-title">
                <i class="el-icon-link"></i>
                绑定/回绑记录（共 {{ getBindHistoryList(scope.row.txm).length }} 条）
              </span>
            </div>
            <el-table 
              :data="getBindHistoryList(scope.row.txm)" 
              border
              size="small"
              v-loading="bindHistoryLoadingMap[scope.row.txm]"
              empty-text="暂无绑定/回绑记录"
              v-if="!bindHistoryLoadingMap[scope.row.txm]"
            >
              <el-table-column type="index" label="序号" width="60" align="center" />
              <el-table-column label="条码号" align="center" prop="barcode" min-width="120" show-overflow-tooltip />
              <el-table-column label="操作类型" align="center" prop="operationType" min-width="100">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.operationType === 'unbind'" type="warning" size="mini">解绑</el-tag>
                  <el-tag v-else-if="scope.row.operationType === 'rebind'" type="success" size="mini">回绑</el-tag>
                  <span v-else>{{ scope.row.operationType || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="框子编码" align="center" prop="palletCode" min-width="120" show-overflow-tooltip>
                <template slot-scope="scope">
                  <span>{{ scope.row.palletCode || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="回绑框子编码" align="center" prop="rebindPalletCode" min-width="140" show-overflow-tooltip>
                <template slot-scope="scope">
                  <span>{{ scope.row.rebindPalletCode || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作时间" align="center" prop="createTime" min-width="160" />
              <el-table-column label="备注" align="center" prop="memo" min-width="150" show-overflow-tooltip>
                <template slot-scope="scope">
                  <span>{{ scope.row.memo || '-' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="流转卡号" align="center" prop="lzkh" min-width="120" show-overflow-tooltip />
      <el-table-column label="检验编号" align="center" prop="jybh" min-width="120" show-overflow-tooltip />
      <el-table-column label="缸号" align="center" prop="gh" min-width="100" show-overflow-tooltip />
      <el-table-column label="计划单号" align="center" prop="jhdh" min-width="120" show-overflow-tooltip />
      <el-table-column label="托盘编码" align="center" prop="palletCode" min-width="120" show-overflow-tooltip />
      <el-table-column label="库位编码" align="center" prop="cellCode" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.cellCode || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="客户名称" align="center" prop="khmch" min-width="120" show-overflow-tooltip />
      <el-table-column label="客户品号" align="center" prop="khbh" min-width="120" show-overflow-tooltip />
      <el-table-column label="客户品名" align="center" prop="khpm" min-width="120" show-overflow-tooltip />
      <el-table-column label="品号" align="center" prop="bh" min-width="120" show-overflow-tooltip />
      <el-table-column label="品名" align="center" prop="pm" min-width="120" show-overflow-tooltip />
      <el-table-column label="色称" align="center" prop="sc" min-width="100" show-overflow-tooltip />
      <el-table-column label="批次" align="center" prop="hh" min-width="100" show-overflow-tooltip />
      <el-table-column label="匹号" align="center" prop="ph" min-width="100" show-overflow-tooltip />
      <el-table-column label="总长" align="center" prop="zc" min-width="100" />
      <el-table-column label="纯长" align="center" prop="chc" min-width="100" />
      <el-table-column label="毛重" align="center" prop="zl" min-width="100" />
      <el-table-column label="净重" align="center" prop="jz" min-width="100" />
      <el-table-column label="检验等级" align="center" prop="jydj" min-width="100" show-overflow-tooltip />
      <el-table-column label="扣分数" align="center" prop="kfs" min-width="100" />
      <el-table-column label="疵点数" align="center" prop="cds" min-width="100" />
      <el-table-column label="条形码" align="center" prop="txm" min-width="120" show-overflow-tooltip />
      <el-table-column label="验布类型" align="center" prop="yblx" min-width="100" show-overflow-tooltip />
      <el-table-column label="验布机台" align="center" prop="sbbh" min-width="100" show-overflow-tooltip />
      <el-table-column label="疵点信息" align="center" prop="cdxx" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.cdxx || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="100" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="160" />
      <el-table-column label="备注" align="center" prop="memo" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.memo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds-xw:DsXwInventory:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds-xw:DsXwInventory:remove']"
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

    <!-- 添加或修改验布库存对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-tabs v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="流转卡号" prop="lzkh">
                  <el-input v-model="form.lzkh" placeholder="请输入流转卡号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="检验编号" prop="jybh">
                  <el-input v-model="form.jybh" placeholder="请输入检验编号" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="缸号" prop="gh">
                  <el-input v-model="form.gh" placeholder="请输入缸号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="验布车间" prop="cj">
                  <el-input v-model="form.cj" placeholder="请输入验布车间" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="计划单号" prop="jhdh">
                  <el-input v-model="form.jhdh" placeholder="请输入计划单号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="托盘编码" prop="palletCode">
                  <el-input v-model="form.palletCode" placeholder="请输入托盘编码" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="条形码" prop="txm">
                  <el-input v-model="form.txm" placeholder="请输入条形码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="验布类型" prop="yblx">
                  <el-input v-model="form.yblx" placeholder="请输入验布类型" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="验布机台" prop="sbbh">
                  <el-input v-model="form.sbbh" placeholder="请输入验布机台" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="检验等级" prop="jydj">
                  <el-input v-model="form.jydj" placeholder="请输入检验等级" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 客户信息 -->
          <el-tab-pane label="客户信息" name="customer">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="客户名称" prop="khmch">
                  <el-input v-model="form.khmch" placeholder="请输入客户名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="客户品号" prop="khbh">
                  <el-input v-model="form.khbh" placeholder="请输入客户品号" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="客户品名" prop="khpm">
                  <el-input v-model="form.khpm" placeholder="请输入客户品名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="客户色称" prop="khsc">
                  <el-input v-model="form.khsc" placeholder="请输入客户色称" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 产品信息 -->
          <el-tab-pane label="产品信息" name="product">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="品号" prop="bh">
                  <el-input v-model="form.bh" placeholder="请输入品号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="品名" prop="pm">
                  <el-input v-model="form.pm" placeholder="请输入品名" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="色称" prop="sc">
                  <el-input v-model="form.sc" placeholder="请输入色称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="批次" prop="hh">
                  <el-input v-model="form.hh" placeholder="请输入批次" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="成分" prop="sz">
                  <el-input v-model="form.sz" placeholder="请输入成分" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="匹号" prop="ph">
                  <el-input v-model="form.ph" placeholder="请输入匹号" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="成品门幅" prop="cpmf">
                  <el-input v-model="form.cpmf" placeholder="请输入成品门幅" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="成品克重" prop="kz">
                  <el-input v-model="form.kz" placeholder="请输入成品克重" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 尺寸重量信息 -->
          <el-tab-pane label="尺寸重量" name="size">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="总长" prop="zc">
                  <el-input v-model="form.zc" placeholder="请输入总长" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="纯长" prop="chc">
                  <el-input v-model="form.chc" placeholder="请输入纯长" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="码长" prop="mc">
                  <el-input v-model="form.mc" placeholder="请输入码长" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="扣米数" prop="kms">
                  <el-input v-model="form.kms" placeholder="请输入扣米数" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="毛重" prop="zl">
                  <el-input v-model="form.zl" placeholder="请输入毛重" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="净重" prop="jz">
                  <el-input v-model="form.jz" placeholder="请输入净重" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="毛磅" prop="mb">
                  <el-input v-model="form.mb" placeholder="请输入毛磅" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="净磅" prop="jb">
                  <el-input v-model="form.jb" placeholder="请输入净磅" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 检验信息 -->
          <el-tab-pane label="检验信息" name="inspection">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="扣分数" prop="kfs">
                  <el-input v-model="form.kfs" placeholder="请输入扣分数" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="疵点数" prop="cds">
                  <el-input v-model="form.cds" placeholder="请输入疵点数" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="疵点信息" prop="cdxx">
                  <el-input v-model="form.cdxx" type="textarea" :rows="4" placeholder="请输入疵点信息" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 修改信息 -->
          <el-tab-pane label="修改信息" name="modify">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="色称修改" prop="scxg">
                  <el-input v-model="form.scxg" placeholder="请输入色称修改" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="品号修改" prop="blxg">
                  <el-input v-model="form.blxg" placeholder="请输入品号修改" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="品名修改" prop="pmxg">
                  <el-input v-model="form.pmxg" placeholder="请输入品名修改" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 其他信息 -->
          <el-tab-pane label="其他信息" name="other">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="备注" prop="memo">
                  <el-input v-model="form.memo" type="textarea" :rows="4" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDsXwInventory, getDsXwInventory, delDsXwInventory, addDsXwInventory, updateDsXwInventory } from "@/api/wcs-ds-xw/DsXwInventory";
import { listByBarcode } from "@/api/wcs-ds-xw/DsXwBarcodeUnbindHistory";
import request from "@/utils/request";
export default {
  name: "DsXwInventory",
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
      // 显示高级搜索
      showAdvancedSearch: false,
      // 总条数
      total: 0,
      // 验布库存表格数据
      DsXwInventoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 当前激活的标签页
      activeTab: "basic",
      // 绑定/回绑记录数据映射 key: 条码号, value: 记录列表
      bindHistoryMap: {},
      // 绑定/回绑记录加载状态映射 key: 条码号, value: 是否加载中
      bindHistoryLoadingMap: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        lzkh: null,
        jybh: null,
        gh: null,
        cj: null,
        jhdh: null,
        palletCode: null,
        khmch: null,
        khbh: null,
        khpm: null,
        khsc: null,
        bh: null,
        pm: null,
        sc: null,
        hh: null,
        sz: null,
        ph: null,
        zc: null,
        chc: null,
        zl: null,
        jz: null,
        mb: null,
        jb: null,
        mc: null,
        jydj: null,
        cpmf: null,
        kz: null,
        kfs: null,
        cds: null,
        kms: null,
        txm: null,
        yblx: null,
        cdxx: null,
        sbbh: null,
        scxg: null,
        blxg: null,
        pmxg: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        memo: null
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
    /** 查询验布库存列表 */
    getList() {
      this.loading = true;
      listDsXwInventory(this.queryParams).then(response => {
          if(response.code==200){
            this.DsXwInventoryList = response.rows;
            this.total = response.total;
          }
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
      this.activeTab = "basic";
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        lzkh: null,
        jybh: null,
        gh: null,
        cj: null,
        jhdh: null,
        palletCode: null,
        khmch: null,
        khbh: null,
        khpm: null,
        khsc: null,
        bh: null,
        pm: null,
        sc: null,
        hh: null,
        sz: null,
        ph: null,
        zc: null,
        chc: null,
        zl: null,
        jz: null,
        mb: null,
        jb: null,
        mc: null,
        jydj: null,
        cpmf: null,
        kz: null,
        kfs: null,
        cds: null,
        kms: null,
        txm: null,
        yblx: null,
        cdxx: null,
        sbbh: null,
        scxg: null,
        blxg: null,
        pmxg: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
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
    /** 切换高级搜索 */
    toggleAdvancedSearch() {
      this.showAdvancedSearch = !this.showAdvancedSearch;
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
      this.title = "添加验布库存";
      this.activeTab = "basic";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDsXwInventory(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改验布库存";
        this.activeTab = "basic";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDsXwInventory(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addDsXwInventory(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除验布库存编号为"' + ids + '"的数据项？').then(function() {
        return delDsXwInventory(ids);
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
      this.download('wcs-ds-xw/DsXwInventory/export', {
        ...this.queryParams
      }, `DsXwInventory_${new Date().getTime()}.xlsx`)
    },
    /** 表格展开行变化事件 */
    handleExpandChange(row, expandedRows) {
      if (expandedRows.includes(row)) {
        // 展开时加载绑定/回绑记录
        if (row.txm) {
          this.loadBindHistory(row.txm);
        }
      }
    },
    /** 加载绑定/回绑记录数据 */
    loadBindHistory(barcode) {
      // 如果已经加载过，不再重复加载
      if (this.bindHistoryMap[barcode] !== undefined) {
        return;
      }
      
      // 设置加载状态
      this.$set(this.bindHistoryLoadingMap, barcode, true);
      
      // 查询绑定/回绑记录
      listByBarcode(barcode).then(response => {
        if (response.code == 200) {
          // 存储记录列表
          this.$set(this.bindHistoryMap, barcode, response.data || []);
        } else {
          this.$set(this.bindHistoryMap, barcode, []);
          this.$message.error(response.msg || '加载绑定/回绑记录失败');
        }
        this.$set(this.bindHistoryLoadingMap, barcode, false);
      }).catch(error => {
        console.error('加载绑定/回绑记录失败:', error);
        this.$set(this.bindHistoryMap, barcode, []);
        this.$set(this.bindHistoryLoadingMap, barcode, false);
        this.$message.error('加载绑定/回绑记录失败');
      });
    },
    /** 获取绑定/回绑记录列表 */
    getBindHistoryList(barcode) {
      return this.bindHistoryMap[barcode] || [];
    }
  }
};
</script>

<style scoped>
.bind-history-container {
  padding: 10px;
  background-color: #f5f7fa;
}

.bind-history-header {
  margin-bottom: 10px;
}

.bind-history-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.bind-history-title i {
  margin-right: 5px;
  color: #409eff;
}
</style>
