<template>
  <div class="app-container outbound-plan-container">
    <el-form :model="queryParams" ref="queryForm" size="small" v-show="showSearch" class="search-form">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="WMS计划号" prop="wmsplanid">
            <el-input
              v-model="queryParams.wmsplanid"
              placeholder="请输入计划号"
              clearable
              prefix-icon="el-icon-document"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="计划类型" prop="planType">
            <el-select v-model="queryParams.planType" placeholder="请选择计划类型" clearable style="width: 100%;">
              <el-option label="不指定卷号" value="plantocloth" />
              <el-option label="指定卷号" value="plantobar" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 100%;">
              <el-option label="已创建" value="created" />
              <el-option label="处理中" value="processing" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="创建人" prop="createUserName">
            <el-input
              v-model="queryParams.createUserName"
              placeholder="请输入创建人姓名"
              clearable
              prefix-icon="el-icon-user"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="创建时间" prop="createTimeRange">
            <el-date-picker
              v-model="createTimeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd HH:mm:ss"
              format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%;"
              @change="handleCreateTimeRangeChange"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="更新时间" prop="updateTimeRange">
            <el-date-picker
              v-model="updateTimeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd HH:mm:ss"
              format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%;"
              @change="handleUpdateTimeRangeChange"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24" style="text-align: right;">
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
            <el-button 
              type="text" 
              icon="el-icon-arrow-up" 
              size="small" 
              @click="toggleAdvancedSearch"
              style="margin-left: 10px;"
            >
              {{ showAdvancedSearch ? '收起' : '展开' }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-show="showAdvancedSearch" :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="创建人ID" prop="createUserId">
            <el-input
              v-model="queryParams.createUserId"
              placeholder="请输入创建人ID"
              clearable
              prefix-icon="el-icon-user"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="更新人" prop="updateUserName">
            <el-input
              v-model="queryParams.updateUserName"
              placeholder="请输入更新人姓名"
              clearable
              prefix-icon="el-icon-user"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item label="更新人ID" prop="updateUserId">
            <el-input
              v-model="queryParams.updateUserId"
              placeholder="请输入更新人ID"
              clearable
              prefix-icon="el-icon-user"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlan:add']"
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
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlan:edit']"
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
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlan:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-ds-xw:DsXwOutboundPlan:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table 
      v-loading="loading" 
      :data="DsXwOutboundPlanList" 
      @selection-change="handleSelectionChange"
      @expand-change="handleExpandChange"
      border
      row-key="id"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="expand" width="50">
        <template slot-scope="scope">
          <div class="pallet-detail-container">
            <div class="pallet-header">
              <span class="pallet-title">
                <i class="el-icon-box"></i>
                框子明细（共 {{ getPalletList(scope.row.id).length }} 个）
              </span>
            </div>
            <el-table 
              :data="getPalletList(scope.row.id)" 
              border
              size="small"
              v-loading="palletLoadingMap[scope.row.id]"
              empty-text="暂无框子数据"
              v-if="!palletLoadingMap[scope.row.id]"
            >
              <el-table-column type="index" label="序号" width="60" align="center" />
              <el-table-column label="框子编码" prop="palletCode" min-width="150" show-overflow-tooltip>
                <template slot-scope="palletScope">
                  <el-tag type="info" size="small">{{ palletScope.row.palletCode }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" prop="status" width="120" align="center">
                <template slot-scope="palletScope">
                  <el-tag v-if="palletScope.row.status === 'pending'" type="info" size="small">待处理</el-tag>
                  <el-tag v-else-if="palletScope.row.status === 'downing'" type="warning" size="small">下架中</el-tag>
                  <el-tag v-else-if="palletScope.row.status === 'downed'" type="success" size="small">已下架</el-tag>
                  <el-tag v-else-if="palletScope.row.status === 'completed'" type="success" size="small">已完成</el-tag>
                  <span v-else>{{ palletScope.row.status || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="任务ID" prop="taskId" width="100" align="center">
                <template slot-scope="palletScope">
                  <span v-if="palletScope.row.taskId">{{ palletScope.row.taskId }}</span>
                  <span v-else class="empty-data">-</span>
                </template>
              </el-table-column>
              <el-table-column label="下架时间" prop="downTime" width="160" align="center" show-overflow-tooltip>
                <template slot-scope="palletScope">
                  <span v-if="palletScope.row.downTime">{{ palletScope.row.downTime }}</span>
                  <span v-else class="empty-data">-</span>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" prop="createTime" width="160" align="center" show-overflow-tooltip>
                <template slot-scope="palletScope">
                  <span v-if="palletScope.row.createTime">{{ palletScope.row.createTime }}</span>
                  <span v-else class="empty-data">-</span>
                </template>
              </el-table-column>
              <el-table-column label="备注" prop="memo" min-width="150" show-overflow-tooltip>
                <template slot-scope="palletScope">
                  <span v-if="palletScope.row.memo">{{ palletScope.row.memo }}</span>
                  <span v-else class="empty-data">-</span>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="palletLoadingMap[scope.row.id]" class="pallet-loading">
              <i class="el-icon-loading"></i>
              <span>加载中...</span>
            </div>
            <div v-else-if="getPalletList(scope.row.id).length === 0" class="pallet-empty">
              <i class="el-icon-box"></i>
              <span>暂无框子数据</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="WMS计划号" align="center" prop="wmsplanid" min-width="150" show-overflow-tooltip>
      </el-table-column>
      <el-table-column label="计划类型" align="center" prop="planType" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.planType === 'plantocloth'" type="info" size="small">不指定卷号</el-tag>
          <el-tag v-else-if="scope.row.planType === 'plantobar'" type="warning" size="small">指定卷号</el-tag>
          <span v-else>{{ scope.row.planType || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="出库明细" align="left" prop="jsonData" min-width="300">
        <template slot-scope="scope">
          <div class="json-data-display">
            <div v-if="!scope.row.jsonData" class="empty-data">-</div>
            <div v-else>
              <div v-if="scope.row.planType === 'plantocloth'" class="order-summary">
                <div v-if="getOrderSummary(scope.row.jsonData)" class="summary-content">
                  <div class="summary-item">
                    <span class="label">订单数：</span>
                    <el-tag type="info" size="mini">{{ getOrderSummary(scope.row.jsonData).orderCount }}</el-tag>
                  </div>
                  <div class="summary-item" v-if="getOrderSummary(scope.row.jsonData).totalPs > 0">
                    <span class="label">总匹数：</span>
                    <span class="value">{{ getOrderSummary(scope.row.jsonData).totalPs }}</span>
                  </div>
                  <div class="summary-item" v-if="getOrderSummary(scope.row.jsonData).totalMs > 0">
                    <span class="label">总米数：</span>
                    <span class="value">{{ getOrderSummary(scope.row.jsonData).totalMs }}</span>
                  </div>
                </div>
                <el-button 
                  type="text" 
                  size="mini" 
                  icon="el-icon-view" 
                  @click="viewJsonDetail(scope.row)"
                  class="view-detail-btn"
                >查看详情</el-button>
              </div>
              <div v-else-if="scope.row.planType === 'plantobar'" class="bar-summary">
                <div v-if="getBarSummary(scope.row.jsonData)" class="summary-content">
                  <div class="summary-item">
                    <span class="label">条码数：</span>
                    <el-tag type="warning" size="mini">{{ getBarSummary(scope.row.jsonData).barCount }}</el-tag>
                  </div>
                </div>
                <el-button 
                  type="text" 
                  size="mini" 
                  icon="el-icon-view" 
                  @click="viewJsonDetail(scope.row)"
                  class="view-detail-btn"
                >查看详情</el-button>
              </div>
              <div v-else class="json-preview">
                <el-button 
                  type="text" 
                  size="mini" 
                  icon="el-icon-view" 
                  @click="viewJsonDetail(scope.row)"
                  class="view-detail-btn"
                >查看JSON</el-button>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 'created'" type="info" size="small">已创建</el-tag>
          <el-tag v-else-if="scope.row.status === 'processing'" type="warning" size="small">处理中</el-tag>
          <el-tag v-else-if="scope.row.status === 'completed'" type="success" size="small">已完成</el-tag>
          <el-tag v-else-if="scope.row.status === 'cancelled'" type="danger" size="small">已取消</el-tag>
          <span v-else>{{ scope.row.status || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="100" show-overflow-tooltip>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" show-overflow-tooltip>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="memo" min-width="120" show-overflow-tooltip>
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds-xw:DsXwOutboundPlan:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds-xw:DsXwOutboundPlan:remove']"
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

    <!-- 添加或修改出库计划对话框 -->
    <el-dialog 
      :title="title" 
      :visible.sync="open" 
      width="1200px" 
      append-to-body
      custom-class="outbound-plan-dialog"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="WMS计划号" prop="wmsplanid">
          <el-input v-model="form.wmsplanid" placeholder="请输入WMS计划号" :disabled="form.id != null" />
        </el-form-item>
        <el-form-item label="计划类型" prop="planType">
          <el-select v-model="form.planType" placeholder="请选择计划类型" @change="handlePlanTypeChange" :disabled="form.id != null">
            <el-option label="不指定卷号（按条件筛选）" value="plantocloth"></el-option>
            <el-option label="指定卷号（直接指定条码）" value="plantobar"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 不指定卷号方式：订单列表 -->
        <div v-if="form.planType === 'plantocloth'" class="order-list-container">
          <el-divider content-position="left">订单明细</el-divider>
          <el-table :data="form.orderlist" border class="order-table" style="width: 100%">
            <el-table-column label="客户品号" prop="khbh" min-width="130">
              <template slot-scope="scope">
                <el-input v-model="scope.row.khbh" placeholder="客户品号" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="品号" prop="bh" min-width="110">
              <template slot-scope="scope">
                <el-input v-model="scope.row.bh" placeholder="品号" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="品名" prop="pm" min-width="120">
              <template slot-scope="scope">
                <el-input v-model="scope.row.pm" placeholder="品名" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="色称" prop="sc" min-width="110">
              <template slot-scope="scope">
                <el-input v-model="scope.row.sc" placeholder="色称" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="缸号" prop="gh" min-width="110">
              <template slot-scope="scope">
                <el-input v-model="scope.row.gh" placeholder="缸号" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="批次" prop="hh" min-width="110">
              <template slot-scope="scope">
                <el-input v-model="scope.row.hh" placeholder="批次" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="匹数" prop="ps" width="100" align="center">
              <template slot-scope="scope">
                <el-input-number 
                  v-model.number="scope.row.ps" 
                  :min="0" 
                  :precision="0"
                  :step="1"
                  :controls="true"
                  :controls-position="'right'"
                  size="small" 
                  style="width: 100%;"
                  placeholder="0"
                />
              </template>
            </el-table-column>
            <el-table-column label="米数" prop="ms" width="100" align="center">
              <template slot-scope="scope">
                <el-input-number 
                  v-model.number="scope.row.ms" 
                  :min="0" 
                  :precision="0"
                  :step="1"
                  :controls="true"
                  :controls-position="'right'"
                  size="small" 
                  style="width: 100%;"
                  placeholder="0"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="removeOrderItem(scope.$index)" />
              </template>
            </el-table-column>
          </el-table>
          <div class="add-item-button">
            <el-button type="primary" icon="el-icon-plus" size="small" @click="addOrderItem">添加订单</el-button>
          </div>
        </div>
        
        <!-- 指定卷号方式：条码列表 -->
        <div v-if="form.planType === 'plantobar'" class="bar-list-container">
          <el-divider content-position="left">条码列表</el-divider>
          <el-table :data="form.barlist" border class="bar-table" style="width: 100%">
            <el-table-column type="index" label="序号" width="70" align="center" />
            <el-table-column label="条码号" prop="barcode" min-width="300">
              <template slot-scope="scope">
                <el-input v-model="scope.row.barcode" placeholder="请输入条码号" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="removeBarItem(scope.$index)" />
              </template>
            </el-table-column>
          </el-table>
          <div class="add-item-button">
            <el-button type="primary" icon="el-icon-plus" size="small" @click="addBarItem">添加条码</el-button>
          </div>
        </div>
        
        <el-form-item label="备注" prop="memo">
          <el-input v-model="form.memo" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- JSON详情查看对话框 -->
    <el-dialog 
      title="出库计划详情" 
      :visible.sync="jsonDetailVisible" 
      width="1100px" 
      append-to-body
      custom-class="json-detail-dialog"
    >
      <div class="json-detail-content">
        <el-tabs v-model="jsonDetailTab" type="border-card">
          <el-tab-pane label="格式化JSON" name="formatted">
            <pre class="json-viewer">{{ formattedJson }}</pre>
          </el-tab-pane>
          <el-tab-pane label="原始数据" name="raw">
            <pre class="json-viewer">{{ rawJson }}</pre>
          </el-tab-pane>
          <el-tab-pane v-if="jsonDetailData.planType === 'plantocloth'" label="订单明细" name="orders">
            <el-table :data="jsonDetailData.orderlist" border style="width: 100%">
              <el-table-column label="客户品号" prop="khbh" min-width="120" />
              <el-table-column label="品号" prop="bh" min-width="100" />
              <el-table-column label="品名" prop="pm" min-width="120" />
              <el-table-column label="色称" prop="sc" min-width="100" />
              <el-table-column label="缸号" prop="gh" min-width="100" />
              <el-table-column label="批次" prop="hh" min-width="100" />
              <el-table-column label="匹数" prop="ps" width="80" align="right" />
              <el-table-column label="米数" prop="ms" width="80" align="right" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane v-if="jsonDetailData.planType === 'plantobar'" label="条码列表" name="barcodes">
            <el-table :data="jsonDetailData.barlist" border style="width: 100%">
              <el-table-column type="index" label="序号" width="60" align="center" />
              <el-table-column label="条码号" prop="barcode" min-width="200" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="jsonDetailVisible = false">关 闭</el-button>
        <el-button type="primary" @click="copyJson">复制JSON</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDsXwOutboundPlan, getDsXwOutboundPlan, delDsXwOutboundPlan, addDsXwOutboundPlan, updateDsXwOutboundPlan } from "@/api/wcs-ds-xw/DsXwOutboundPlan";
import { listDsXwOutboundPlanPallet } from "@/api/wcs-ds-xw/DsXwOutboundPlanPallet";
import request from "@/utils/request";
export default {
  name: "DsXwOutboundPlan",
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
      // 出库计划表格数据
      DsXwOutboundPlanList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wmsplanid: null,
        jsonData: null,
        planType: null,
        status: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        createTimeStart: null,
        createTimeEnd: null,
        updateTimeStart: null,
        updateTimeEnd: null,
        memo: null
      },
      // 表单参数
      form: {
        id: null,
        wmsplanid: null,
        planType: null,
        orderlist: [],
        barlist: [],
        memo: null
      },
      // 表单校验
      rules: {
        wmsplanid: [
          { required: true, message: "WMS计划号不能为空", trigger: "blur" }
        ],
        planType: [
          { required: true, message: "计划类型不能为空", trigger: "change" }
        ]
      },
      // JSON详情对话框
      jsonDetailVisible: false,
      jsonDetailTab: 'formatted',
      jsonDetailData: {},
      formattedJson: '',
      rawJson: '',
      // 框子明细数据存储：key为planId，value为框子列表
      palletMap: {},
      // 框子加载状态：key为planId，value为loading状态
      palletLoadingMap: {},
      // 日期范围选择
      createTimeRange: [],
      updateTimeRange: [],
      // 高级搜索展开/收起
      showAdvancedSearch: false
    };
  },
  created() {
    this.getList();
  },
  watch: {
    // 监听订单列表变化，确保ps和ms始终是数字类型
    'form.orderlist': {
      handler(newVal) {
        if (newVal && Array.isArray(newVal)) {
          newVal.forEach(item => {
            if (item.ps === null || item.ps === undefined || item.ps === '') {
              this.$set(item, 'ps', 0);
            } else {
              this.$set(item, 'ps', Number(item.ps) || 0);
            }
            if (item.ms === null || item.ms === undefined || item.ms === '') {
              this.$set(item, 'ms', 0);
            } else {
              this.$set(item, 'ms', Number(item.ms) || 0);
            }
          });
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /** 查询出库计划列表 */
    getList() {
      this.loading = true;
      listDsXwOutboundPlan(this.queryParams).then(response => {
          if(response.code==200){
            this.DsXwOutboundPlanList = response.rows;
            this.total = response.total;
          }
        this.loading = false;
      });
    },
    /** 表格展开行变化事件 */
    handleExpandChange(row, expandedRows) {
      if (expandedRows.includes(row)) {
        // 展开时加载框子明细
        this.loadPalletList(row.id);
      }
    },
    /** 加载框子明细数据 */
    loadPalletList(planId) {
      // 如果已经加载过，不再重复加载
      if (this.palletMap[planId] && this.palletMap[planId].length > 0) {
        return;
      }
      
      // 设置加载状态
      this.$set(this.palletLoadingMap, planId, true);
      
      // 查询框子明细
      const queryParams = {
        planId: planId,
        pageNum: 1,
        pageSize: 1000 // 设置较大的页面大小以获取所有框子
      };
      
      listDsXwOutboundPlanPallet(queryParams).then(response => {
        if (response.code == 200) {
          // 存储框子列表
          this.$set(this.palletMap, planId, response.rows || []);
        } else {
          this.$set(this.palletMap, planId, []);
          this.$message.error(response.msg || '加载框子明细失败');
        }
        this.$set(this.palletLoadingMap, planId, false);
      }).catch(error => {
        console.error('加载框子明细失败:', error);
        this.$set(this.palletMap, planId, []);
        this.$set(this.palletLoadingMap, planId, false);
        this.$message.error('加载框子明细失败');
      });
    },
    /** 获取框子列表 */
    getPalletList(planId) {
      return this.palletMap[planId] || [];
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
        wmsplanid: null,
        planType: null,
        orderlist: [],
        barlist: [],
        memo: null
      };
      this.resetForm("form");
    },
    // 计划类型改变
    handlePlanTypeChange(value) {
      if (value === 'plantocloth') {
        // 不指定卷号，初始化订单列表
        if (!this.form.orderlist || this.form.orderlist.length === 0) {
          this.form.orderlist = [{
            khbh: '',
            bh: '',
            pm: '',
            sc: '',
            gh: '',
            hh: '',
            ps: 0,
            ms: 0
          }];
        } else {
          // 确保已有数据的ps和ms是数字类型
          this.form.orderlist.forEach(item => {
            if (item.ps === null || item.ps === undefined) {
              item.ps = 0;
            }
            if (item.ms === null || item.ms === undefined) {
              item.ms = 0;
            }
          });
        }
        this.form.barlist = [];
      } else if (value === 'plantobar') {
        // 指定卷号，初始化条码列表
        if (!this.form.barlist || this.form.barlist.length === 0) {
          this.form.barlist = [{
            barcode: ''
          }];
        }
        this.form.orderlist = [];
      }
    },
    // 添加订单项
    addOrderItem() {
      if (!this.form.orderlist) {
        this.form.orderlist = [];
      }
      this.form.orderlist.push({
        khbh: '',
        bh: '',
        pm: '',
        sc: '',
        gh: '',
        hh: '',
        ps: 0,
        ms: 0
      });
      // 强制更新视图
      this.$nextTick(() => {
        this.$forceUpdate();
      });
    },
    // 删除订单项
    removeOrderItem(index) {
      this.form.orderlist.splice(index, 1);
    },
    // 添加条码项
    addBarItem() {
      if (!this.form.barlist) {
        this.form.barlist = [];
      }
      this.form.barlist.push({
        barcode: ''
      });
    },
    // 删除条码项
    removeBarItem(index) {
      this.form.barlist.splice(index, 1);
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      // 重置日期范围
      this.createTimeRange = [];
      this.updateTimeRange = [];
      // 重置查询参数中的日期字段
      this.queryParams.createTime = null;
      this.queryParams.updateTime = null;
      this.handleQuery();
    },
    /** 创建时间范围变化 */
    handleCreateTimeRangeChange(value) {
      if (value && value.length === 2) {
        this.queryParams.createTimeStart = value[0];
        this.queryParams.createTimeEnd = value[1];
      } else {
        this.queryParams.createTimeStart = null;
        this.queryParams.createTimeEnd = null;
      }
    },
    /** 更新时间范围变化 */
    handleUpdateTimeRangeChange(value) {
      if (value && value.length === 2) {
        this.queryParams.updateTimeStart = value[0];
        this.queryParams.updateTimeEnd = value[1];
      } else {
        this.queryParams.updateTimeStart = null;
        this.queryParams.updateTimeEnd = null;
      }
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
      this.title = "添加出库计划";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDsXwOutboundPlan(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改出库计划";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            // 修改操作
            updateDsXwOutboundPlan(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            // 新增操作 - 调用ERP接口
            this.createOutboundPlan();
          }
        }
      });
    },
    /** 创建出库计划 - 调用ERP接口 */
    createOutboundPlan() {
      if (!this.form.planType) {
        this.$modal.msgError("请选择计划类型");
        return;
      }
      
      if (this.form.planType === 'plantocloth') {
        // 不指定卷号方式
        if (!this.form.orderlist || this.form.orderlist.length === 0) {
          this.$modal.msgError("请至少添加一条订单明细");
          return;
        }
        
        // 验证订单明细
        for (let i = 0; i < this.form.orderlist.length; i++) {
          const order = this.form.orderlist[i];
          if (!order.khbh && !order.bh && !order.pm) {
            this.$modal.msgError(`第${i + 1}条订单明细至少需要填写客户品号、品号或品名之一`);
            return;
          }
          if (order.ps === 0 && order.ms === 0) {
            this.$modal.msgError(`第${i + 1}条订单明细的匹数和米数不能同时为0`);
            return;
          }
        }
        
        // 构建请求参数
        const requestData = {
          wmsplanid: this.form.wmsplanid,
          orderlist: this.form.orderlist.map(order => ({
            khbh: order.khbh || '',
            bh: order.bh || '',
            pm: order.pm || '',
            sc: order.sc || '',
            gh: order.gh || '',
            hh: order.hh || '',
            ps: order.ps || 0,
            ms: order.ms || 0
          }))
        };
        
        // 调用ERP接口
        request({
          url: '/dn/api/plantocloth',
          method: 'post',
          data: requestData
        }).then(response => {
          if (response.code == 200) {
            this.$modal.msgSuccess("出库计划创建成功");
            this.open = false;
            this.getList();
          } else {
            this.$modal.msgError(response.msg || "出库计划创建失败");
          }
        }).catch(error => {
          console.error('创建出库计划失败:', error);
          this.$modal.msgError("创建出库计划失败：" + (error.message || "未知错误"));
        });
        
      } else if (this.form.planType === 'plantobar') {
        // 指定卷号方式
        if (!this.form.barlist || this.form.barlist.length === 0) {
          this.$modal.msgError("请至少添加一条条码");
          return;
        }
        
        // 验证条码
        for (let i = 0; i < this.form.barlist.length; i++) {
          const bar = this.form.barlist[i];
          if (!bar.barcode || bar.barcode.trim() === '') {
            this.$modal.msgError(`第${i + 1}条条码不能为空`);
            return;
          }
        }
        
        // 构建请求参数
        const requestData = {
          wmsplanid: this.form.wmsplanid,
          barlist: this.form.barlist.map(bar => ({
            barcode: bar.barcode.trim()
          }))
        };
        
        // 调用ERP接口
        request({
          url: '/dn/api/plantobar',
          method: 'post',
          data: requestData
        }).then(response => {
          if (response.code == 200) {
            this.$modal.msgSuccess("出库计划创建成功");
            this.open = false;
            this.getList();
          } else {
            this.$modal.msgError(response.msg || "出库计划创建失败");
          }
        }).catch(error => {
          console.error('创建出库计划失败:', error);
          this.$modal.msgError("创建出库计划失败：" + (error.message || "未知错误"));
        });
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除出库计划编号为"' + ids + '"的数据项？').then(function() {
        return delDsXwOutboundPlan(ids);
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
      this.download('wcs-ds-xw/DsXwOutboundPlan/export', {
        ...this.queryParams
      }, `DsXwOutboundPlan_${new Date().getTime()}.xlsx`)
    },
    /** 获取订单摘要信息 */
    getOrderSummary(jsonData) {
      if (!jsonData) return null;
      try {
        const data = typeof jsonData === 'string' ? JSON.parse(jsonData) : jsonData;
        if (data.orderlist && Array.isArray(data.orderlist)) {
          const orderCount = data.orderlist.length;
          const totalPs = data.orderlist.reduce((sum, order) => sum + (order.ps || 0), 0);
          const totalMs = data.orderlist.reduce((sum, order) => sum + (order.ms || 0), 0);
          return {
            orderCount,
            totalPs,
            totalMs
          };
        }
        return null;
      } catch (e) {
        console.error('解析订单JSON失败:', e);
        return null;
      }
    },
    /** 获取条码摘要信息 */
    getBarSummary(jsonData) {
      if (!jsonData) return null;
      try {
        const data = typeof jsonData === 'string' ? JSON.parse(jsonData) : jsonData;
        if (data.barlist && Array.isArray(data.barlist)) {
          return {
            barCount: data.barlist.length
          };
        }
        return null;
      } catch (e) {
        console.error('解析条码JSON失败:', e);
        return null;
      }
    },
    /** 查看JSON详情 */
    viewJsonDetail(row) {
      this.jsonDetailData = {};
      this.formattedJson = '';
      this.rawJson = '';
      
      if (!row.jsonData) {
        this.$modal.msgWarning('该计划没有JSON数据');
        return;
      }
      
      try {
        const jsonData = typeof row.jsonData === 'string' ? JSON.parse(row.jsonData) : row.jsonData;
        this.jsonDetailData = {
          ...jsonData,
          planType: row.planType
        };
        this.formattedJson = JSON.stringify(jsonData, null, 2);
        this.rawJson = typeof row.jsonData === 'string' ? row.jsonData : JSON.stringify(row.jsonData);
        this.jsonDetailTab = 'formatted';
        this.jsonDetailVisible = true;
      } catch (e) {
        console.error('解析JSON失败:', e);
        this.rawJson = row.jsonData;
        this.formattedJson = 'JSON格式错误，无法解析';
        this.jsonDetailVisible = true;
      }
    },
    /** 复制JSON */
    copyJson() {
      const textToCopy = this.jsonDetailTab === 'formatted' ? this.formattedJson : this.rawJson;
      
      // 使用现代浏览器的 Clipboard API
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(textToCopy).then(() => {
          this.$modal.msgSuccess('JSON已复制到剪贴板');
        }).catch(err => {
          console.error('复制失败:', err);
          this.fallbackCopyText(textToCopy);
        });
      } else {
        // 降级方案
        this.fallbackCopyText(textToCopy);
      }
    },
    /** 降级复制方案 */
    fallbackCopyText(text) {
      const textArea = document.createElement('textarea');
      textArea.value = text;
      textArea.style.position = 'fixed';
      textArea.style.left = '-999999px';
      textArea.style.top = '-999999px';
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      try {
        document.execCommand('copy');
        this.$modal.msgSuccess('JSON已复制到剪贴板');
      } catch (err) {
        console.error('复制失败:', err);
        this.$modal.msgError('复制失败，请手动复制');
      }
      document.body.removeChild(textArea);
    }
  }
};
</script>

<style scoped lang="scss">
/* 出库计划页面样式 - 使用scoped避免外部样式影响 */
/* 搜索表单样式优化 */
.search-form {
  background: #ffffff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  
  ::v-deep .el-form-item {
    margin-bottom: 18px;
    
    .el-form-item__label {
      color: #606266;
      font-weight: 500;
      padding-right: 12px;
    }
    
    .el-form-item__content {
      .el-input,
      .el-select,
      .el-date-editor {
        width: 100%;
        
        .el-input__inner {
          border-radius: 4px;
          transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
          
          &:focus {
            border-color: #409EFF;
          }
        }
      }
      
      .el-input__prefix {
        left: 10px;
        
        .el-input__icon {
          color: #c0c4cc;
        }
      }
      
      .el-input--prefix .el-input__inner {
        padding-left: 35px;
      }
      
      .el-date-editor {
        .el-range-input {
          color: #606266;
        }
        
        .el-range-separator {
          color: #606266;
        }
      }
    }
  }
  
  /* 按钮组样式 */
  .el-form-item:last-child {
    margin-bottom: 0;
    
    .el-button {
      margin-left: 0;
      margin-right: 10px;
      
      &:last-child {
        margin-right: 0;
      }
    }
  }
  
  /* 响应式布局优化 */
  @media (max-width: 768px) {
    padding: 15px;
    
    ::v-deep .el-col {
      margin-bottom: 10px;
    }
  }
}

.outbound-plan-container {
  padding: 20px;
}

/* 对话框遮罩层样式 - 强制浅色主题 */
::v-deep .el-dialog__wrapper {
  .v-modal {
    background-color: rgba(0, 0, 0, 0.5) !important;
  }
}

/* 全局对话框样式覆盖 - 确保所有对话框都是浅色主题 */
::v-deep .outbound-plan-dialog {
  background-color: #ffffff !important;
  
  /* 覆盖所有可能的深色背景 */
  &,
  * {
    &::before,
    &::after {
      background-color: transparent !important;
    }
  }
}

/* 对话框样式优化 - 强制浅色主题，避免外部样式影响 */
::v-deep .outbound-plan-dialog {
  border-radius: 8px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15) !important;
  background: #ffffff !important;
  background-color: #ffffff !important;
  border: 1px solid #e4e7ed !important;
  max-width: 95vw !important;
  
  /* 强制所有子元素背景为白色 */
  * {
    background-color: inherit !important;
  }
  
  .el-dialog__header {
    padding: 20px 20px 15px !important;
    border-bottom: 1px solid #e4e7ed !important;
    background: #ffffff !important;
    background-color: #ffffff !important;
    
    .el-dialog__title {
      font-size: 18px !important;
      font-weight: 600 !important;
      color: #303133 !important;
      background: transparent !important;
    }
    
    .el-dialog__headerbtn {
      background: transparent !important;
      
      .el-dialog__close {
        color: #909399 !important;
        background: transparent !important;
        
        &:hover {
          color: #409EFF !important;
        }
      }
    }
  }
  
  .el-dialog__body {
    padding: 20px !important;
    max-height: 70vh !important;
    overflow-y: auto !important;
    background: #ffffff !important;
    background-color: #ffffff !important;
    color: #303133 !important;
  }
  
  .el-form {
    background: transparent !important;
  }
  
  .el-form-item {
    margin-bottom: 18px !important;
    background: transparent !important;
    
    &:last-child {
      margin-bottom: 0 !important;
    }
  }
  
  .el-form-item__label {
    font-weight: 500 !important;
    color: #606266 !important;
    line-height: 32px !important;
    background: transparent !important;
  }
  
  .el-input__inner,
  .el-textarea__inner {
    background: #ffffff !important;
    background-color: #ffffff !important;
    color: #606266 !important;
    border-color: #dcdfe6 !important;
  }
  
  .el-select .el-input__inner {
    background: #ffffff !important;
    background-color: #ffffff !important;
    color: #606266 !important;
  }
}

/* 订单明细和条码列表容器 */
.order-list-container,
.bar-list-container {
  margin: 15px 0;
  
  .el-divider {
    margin: 15px 0;
    
    .el-divider__text {
      font-size: 14px;
      font-weight: 600;
      color: #409EFF;
      background-color: #fff;
      padding: 0 15px;
    }
  }
}

/* 表格样式优化 - 强制浅色主题 */
.outbound-plan-dialog {
  .order-list-container,
  .bar-list-container {
    width: 100%;
    overflow-x: auto;
    
    ::v-deep .el-table {
      border: 1px solid #ebeef5 !important;
      border-radius: 4px !important;
      overflow: visible !important;
      background: #ffffff !important;
      background-color: #ffffff !important;
      min-width: 1000px;
      
      .el-table__header-wrapper {
        background: #ffffff !important;
        background-color: #ffffff !important;
        
        th {
          background: #f5f7fa !important;
          background-color: #f5f7fa !important;
          color: #606266 !important;
          font-weight: 600 !important;
          padding: 10px 0 !important;
          border-bottom: 2px solid #e4e7ed !important;
          border-right: 1px solid #ebeef5 !important;
          
          &:last-child {
            border-right: none !important;
          }
        }
      }
      
      .el-table__body-wrapper {
        background: #ffffff !important;
        background-color: #ffffff !important;
        
        td {
          padding: 8px 0 !important;
          border-bottom: 1px solid #ebeef5 !important;
          background: #ffffff !important;
          background-color: #ffffff !important;
          color: #606266 !important;
          border-right: 1px solid #ebeef5 !important;
          
          &:last-child {
            border-right: none !important;
          }
          
          .el-input,
          .el-input-number {
            width: 100% !important;
            background: transparent !important;
            
            .el-input__inner {
              border: 1px solid #dcdfe6 !important;
              border-radius: 4px !important;
              transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1) !important;
              height: 32px !important;
              line-height: 32px !important;
              background: #ffffff !important;
              background-color: #ffffff !important;
              color: #606266 !important;
              
              &:focus {
                border-color: #409EFF !important;
                outline: none !important;
                background: #ffffff !important;
                background-color: #ffffff !important;
              }
              
              &:hover {
                border-color: #c0c4cc !important;
              }
            }
          }
          
          .el-input-number {
            .el-input__inner {
              text-align: left !important;
            }
          }
        }
        
        tr {
          background: #ffffff !important;
          background-color: #ffffff !important;
          
          &:hover {
            background: #f5f7fa !important;
            background-color: #f5f7fa !important;
            
            td {
              background: #f5f7fa !important;
              background-color: #f5f7fa !important;
            }
          }
          
          td {
            background-color: inherit !important;
          }
        }
      }
      
      .el-button--mini {
        padding: 7px 10px !important;
        border-radius: 4px !important;
        
        &.el-button--danger {
          background: #f56c6c !important;
          background-color: #f56c6c !important;
          border-color: #f56c6c !important;
          color: #ffffff !important;
          
          &:hover {
            background: #f78989 !important;
            background-color: #f78989 !important;
            border-color: #f78989 !important;
            transform: scale(1.05) !important;
          }
        }
        
        &:hover {
          transform: scale(1.05) !important;
        }
      }
    }
  }
}

/* 添加按钮样式 - 强制浅色主题 */
.add-item-button {
  margin-top: 12px;
  text-align: left;
  
  ::v-deep .el-button {
    border-radius: 4px !important;
    transition: all 0.3s !important;
    padding: 9px 15px !important;
    font-weight: 500 !important;
    background-color: #409EFF !important;
    border-color: #409EFF !important;
    color: #ffffff !important;
    
    &:hover {
      transform: translateY(-2px) !important;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3) !important;
      background-color: #66b1ff !important;
      border-color: #66b1ff !important;
    }
    
    &:active {
      transform: translateY(0) !important;
    }
  }
}

/* 对话框底部按钮区域 - 强制浅色主题 */
::v-deep .outbound-plan-dialog .el-dialog__footer {
  padding: 15px 20px !important;
  border-top: 1px solid #e4e7ed !important;
  text-align: right !important;
  background: #ffffff !important;
  background-color: #ffffff !important;
  
  .el-button {
    margin-left: 10px !important;
    min-width: 80px !important;
    
    &.el-button--primary {
      background-color: #409EFF !important;
      border-color: #409EFF !important;
      color: #ffffff !important;
      
      &:hover {
        background-color: #66b1ff !important;
        border-color: #66b1ff !important;
      }
    }
    
    &:not(.el-button--primary) {
      background-color: #ffffff !important;
      border-color: #dcdfe6 !important;
      color: #606266 !important;
      
      &:hover {
        background-color: #ecf5ff !important;
        border-color: #b3d8ff !important;
        color: #409EFF !important;
      }
    }
  }
}

/* 主表格样式优化 */
::v-deep .el-table {
  border-radius: 4px;
  overflow: hidden;
  
  .el-table__header-wrapper {
    th {
      background-color: #f5f7fa;
      color: #606266;
      font-weight: 600;
      border-bottom: 2px solid #e4e7ed;
    }
  }
  
  .el-table__body-wrapper {
    .el-table__row {
      transition: background-color 0.25s;
      
      &:hover {
        background-color: #f5f7fa;
      }
      
      td {
        border-bottom: 1px solid #ebeef5;
      }
    }
  }
  
  .el-tag {
    border-radius: 4px;
    padding: 0 8px;
    font-size: 12px;
    border: none;
    font-weight: 500;
  }
}

/* 搜索表单样式 */
::v-deep .el-form--inline {
  .el-form-item {
    margin-bottom: 15px;
    margin-right: 15px;
    
    .el-form-item__label {
      font-weight: 500;
      color: #606266;
    }
  }
}

/* 操作按钮区域 */
.mb8 {
  margin-bottom: 15px;
  
  .el-button {
    border-radius: 4px;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-1px);
    }
    
    &.el-button--mini {
      padding: 7px 15px;
    }
  }
}

/* 响应式优化 */
@media (max-width: 1400px) {
  ::v-deep .outbound-plan-dialog {
    width: 95% !important;
    max-width: 1200px;
  }
  
  ::v-deep .json-detail-dialog {
    width: 95% !important;
    max-width: 1100px;
  }
}

@media (max-width: 1200px) {
  ::v-deep .outbound-plan-dialog {
    width: 98% !important;
  }
  
  ::v-deep .json-detail-dialog {
    width: 98% !important;
  }
  
  .order-list-container,
  .bar-list-container {
    ::v-deep .el-table {
      font-size: 12px;
      
      .el-table__cell {
        padding: 8px 5px;
      }
    }
  }
}

/* 滚动条样式优化 */
::v-deep .el-dialog__body {
  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: #c1c1c1;
    border-radius: 3px;
    
    &:hover {
      background-color: #a8a8a8;
    }
  }
  
  &::-webkit-scrollbar-track {
    background-color: #f1f1f1;
    border-radius: 3px;
  }
}

/* 输入框聚焦效果 - 强制浅色主题 */
::v-deep .outbound-plan-dialog {
  .el-input,
  .el-input-number,
  .el-select,
  .el-textarea {
    background: transparent !important;
    
    .el-input__inner,
    .el-textarea__inner {
      background: #ffffff !important;
      background-color: #ffffff !important;
      color: #606266 !important;
      border-color: #dcdfe6 !important;
      
      &:focus {
        border-color: #409EFF !important;
        background: #ffffff !important;
        background-color: #ffffff !important;
      }
      
      &:hover {
        border-color: #c0c4cc !important;
      }
    }
  }
  
  .el-select-dropdown {
    background: #ffffff !important;
    background-color: #ffffff !important;
    border-color: #e4e7ed !important;
    
    .el-select-dropdown__item {
      color: #606266 !important;
      background: #ffffff !important;
      background-color: #ffffff !important;
      
      &:hover {
        background: #f5f7fa !important;
        background-color: #f5f7fa !important;
        color: #409EFF !important;
      }
      
      &.selected {
        background: #ecf5ff !important;
        background-color: #ecf5ff !important;
        color: #409EFF !important;
      }
    }
  }
}

/* 数字输入框样式 - 强制浅色主题 */
::v-deep .outbound-plan-dialog {
  .el-input-number {
    background: transparent !important;
    width: 100% !important;
    
    .el-input {
      width: 100% !important;
    }
    
    .el-input__inner {
      text-align: left !important;
      background: #ffffff !important;
      background-color: #ffffff !important;
      color: #606266 !important;
      padding-right: 30px !important;
      
      &::placeholder {
        color: #c0c4cc !important;
      }
    }
    
    .el-input-number__decrease,
    .el-input-number__increase {
      width: 28px !important;
      height: 14px !important;
      line-height: 14px !important;
      background: #f5f7fa !important;
      background-color: #f5f7fa !important;
      color: #606266 !important;
      border-color: #dcdfe6 !important;
      font-size: 12px !important;
      
      &:hover {
        color: #409EFF !important;
        background-color: #ecf5ff !important;
      }
      
      &:active {
        background-color: #d9ecff !important;
      }
    }
    
    .el-input-number__decrease {
      border-top-left-radius: 4px !important;
      border-bottom-left-radius: 0 !important;
      border-bottom: none !important;
    }
    
    .el-input-number__increase {
      border-top-right-radius: 0 !important;
      border-bottom-right-radius: 4px !important;
      border-top: none !important;
    }
    
    /* 确保值为0时也能正常显示 */
    &.is-controls-right {
      .el-input__inner {
        padding-left: 10px !important;
        padding-right: 50px !important;
      }
    }
  }
}

/* 分隔线样式 - 强制浅色主题 */
::v-deep .outbound-plan-dialog {
  .el-divider {
    margin: 20px 0 !important;
    background: #e4e7ed !important;
    background-color: #e4e7ed !important;
    
    &.el-divider--horizontal {
      margin: 20px 0 !important;
    }
    
    .el-divider__text {
      background: #ffffff !important;
      background-color: #ffffff !important;
      color: #409EFF !important;
      font-weight: 600 !important;
    }
  }
}

/* 空状态提示 */
.empty-tip {
  text-align: center;
  color: #909399;
  padding: 20px;
  font-size: 14px;
}

/* 框子明细展开行样式 */
.pallet-detail-container {
  padding: 20px;
  background: #fafafa;
  margin: 10px 0;
  border-radius: 4px;
  
  .pallet-header {
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid #e4e7ed;
    
    .pallet-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      display: flex;
      align-items: center;
      
      i {
        margin-right: 8px;
        color: #409EFF;
        font-size: 16px;
      }
    }
  }
  
  .pallet-loading,
  .pallet-empty {
    text-align: center;
    padding: 40px 20px;
    color: #909399;
    font-size: 14px;
    
    i {
      font-size: 24px;
      margin-right: 8px;
      color: #c0c4cc;
    }
  }
  
  .pallet-loading {
    i {
      animation: rotating 2s linear infinite;
    }
  }
  
  @keyframes rotating {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
  
  /* 嵌套表格样式 */
  ::v-deep .el-table {
    background: #ffffff !important;
    
    .el-table__header-wrapper {
      background: #f5f7fa !important;
      
      th {
        background: #f5f7fa !important;
        background-color: #f5f7fa !important;
        color: #606266 !important;
        font-weight: 600 !important;
      }
    }
    
    .el-table__body-wrapper {
      background: #ffffff !important;
      
      td {
        background: #ffffff !important;
        background-color: #ffffff !important;
        color: #606266 !important;
      }
      
      tr:hover > td {
        background: #f5f7fa !important;
        background-color: #f5f7fa !important;
      }
    }
    
    .empty-data {
      color: #c0c4cc;
      font-style: italic;
    }
  }
}

/* 表格操作按钮样式 */
::v-deep .el-table {
  .el-button--text {
    padding: 0;
    margin: 0 5px;
    color: #409EFF;
    
    &:hover {
      color: #66b1ff;
    }
    
    &.el-button--danger {
      color: #f56c6c;
      
      &:hover {
        color: #f78989;
      }
    }
  }
}

/* JSON数据显示样式 */
.json-data-display {
  padding: 5px 0;
  
  .empty-data {
    color: #909399;
    font-style: italic;
  }
  
  .order-summary,
  .bar-summary,
  .json-preview {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .summary-content {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    flex: 1;
  }
  
  .summary-item {
    display: flex;
    align-items: center;
    gap: 4px;
    
    .label {
      color: #909399;
      font-size: 12px;
      white-space: nowrap;
    }
    
    .value {
      color: #606266;
      font-weight: 500;
      font-size: 13px;
    }
  }
  
  .view-detail-btn {
    padding: 0;
    font-size: 12px;
    color: #409EFF;
    
    &:hover {
      color: #66b1ff;
    }
  }
}

/* JSON详情对话框样式 */
::v-deep .json-detail-dialog {
  max-width: 95vw !important;
  
  .json-detail-content {
    min-height: 400px;
    max-height: 70vh;
    
    .el-tabs {
      .el-tab-pane {
        padding: 15px;
      }
    }
    
    .json-viewer {
      background-color: #f5f7fa;
      border: 1px solid #e4e7ed;
      border-radius: 4px;
      padding: 15px;
      margin: 0;
      font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
      font-size: 13px;
      line-height: 1.6;
      color: #303133;
      overflow-x: auto;
      white-space: pre-wrap;
      word-wrap: break-word;
      max-height: 500px;
      overflow-y: auto;
    }
    
    .el-table {
      font-size: 13px;
      
      .el-table__header-wrapper {
        th {
          background-color: #f5f7fa;
          font-weight: 600;
        }
      }
    }
  }
}

/* 表格列优化 */
::v-deep .el-table {
  .el-table__cell {
    padding: 12px 0;
  }
  
  .el-tag {
    margin: 0 2px;
  }
}
</style>

