 
<template>
  <div class="app-container">
    <div id="app">
    <div class="container"> 
     
      
      <div class="dashboard">
      
        <div class="search-container">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" class="search-form">
            <div class="search-content">
              <el-form-item label="托盘码" prop="palletCode" class="search-form-item">
                <el-input 
                  v-model="queryParams.palletCode" 
                  placeholder="请输入托盘码" 
                  clearable 
                  @keyup.enter.native="handleQuery"
                  class="search-input"/>
              </el-form-item>
              <div class="button-group">
                <el-button
                  type="primary"
                  icon="el-icon-search"
                  size="medium"
                  @click="handleQuery"
                  class="search-btn"
                  >搜索</el-button
                >
                <el-button icon="el-icon-refresh" size="medium" @click="resetQuery"
                  class="reset-btn"
                  >重置</el-button
                >
              </div>
            </div>
          </el-form>
        </div>
        <div style="width: 800px;" class="log-container animated delay-2">
        
          
          <div v-loading="loading" class="log-list">
            <template v-if="PalletRecordList.length > 0">
              <div 
                v-for="(log, index) in PalletRecordList" 
                :key="log.id" 
                class="log-item animated"
                :class="'delay-' + (index % 3 + 1)">
                <div class="log-header">
                  <div class="log-time">{{ log.createTime }}</div>
                  <el-tag :type="log.statusType">{{ log.status }}</el-tag>
                </div>
                <div class="log-content">
                  <strong>托盘 #{{ log.palletCode }}</strong> - {{ log.content }}
                </div>
              </div>
            </template>
            <div v-else class="no-data">
              <i class="el-icon-document-remove"></i>
              <div>未找到匹配的操作记录</div>
            </div>

           
            <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
          </div>
           
          
        
        </div>
      </div>
      
       
    </div>
  </div>
  </div>
</template>

<script>
import { listPalletRecord, getPalletRecord, delPalletRecord, addPalletRecord, updatePalletRecord } from "@/api/wcs-task/PalletRecord";
export default {
  name: "PalletRecord",
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
      // 托盘记录表格数据
      PalletRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        palletCode: null,
        content: null,
        type: null,
        wareCode: null,
        wareName: null
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
    /** 查询托盘记录列表 */
    getList() {
      this.loading = true;
      listPalletRecord(this.queryParams).then(response => {
        this.PalletRecordList = response.rows;
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
        palletCode: null,
        createTime: null,
        content: null,
        type: null,
        wareCode: null,
        wareName: null
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
      this.title = "添加托盘记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPalletRecord(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改托盘记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePalletRecord(this.form).then(response => {
                if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                }else{
                  this.open = false;
                  this.$modal.msgError(response.msg||"修改失败");
                }
            });
          } else {
            addPalletRecord(this.form).then(response => {
              if(response.code==200){
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              }else{
                this.open = false;
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
      this.$modal.confirm('是否确认删除托盘记录编号为"' + ids + '"的数据项？').then(function() {
        return delPalletRecord(ids);
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
      this.download('wms-inventory/PalletRecord/export', {
        ...this.queryParams
      }, `PalletRecord_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style lang="scss" scoped>
* {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
      font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
    }
    
    body {
      background: linear-gradient(135deg, #f5f9ff 0%, #e6f0ff 100%);
      color: #2c3e50;
      min-height: 100vh;
      padding: 20px;
      overflow-x: hidden;
    }
    
    .container {
      max-width: 1400px;
      margin: 0 auto;
      padding: 20px;
    }
    
    .header {
      text-align: center;
      margin-bottom: 30px;
      padding: 20px;
      position: relative;
    }
    
    .header h1 {
      font-size: 2.8rem;
      font-weight: 700;
      margin-bottom: 10px;
      letter-spacing: 1px;
      background: linear-gradient(90deg, #1a6dff, #0d8af7);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    
    .header .subtitle {
      font-size: 1.1rem;
      color: #5a7dab;
      max-width: 700px;
      margin: 0 auto;
      line-height: 1.6;
    }
    
    .dashboard {
      display: grid;
      grid-template-columns: 1fr 3fr;
      gap: 20px;
      margin-bottom: 30px;
    }
    
    .stats-card {
      background: #ffffff;
      border-radius: 12px;
      padding: 25px;
      box-shadow: 0 8px 30px rgba(0, 100, 255, 0.1);
      transition: all 0.3s ease;
      border: 1px solid #e1eaf9;
      position: relative;
      overflow: hidden;
    }
    
    .stats-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 5px;
      height: 100%;
      background: linear-gradient(to bottom, #1a6dff, #4da6ff);
    }
    
    .stats-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 12px 40px rgba(26, 109, 255, 0.15);
    }
    
    .stats-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #e8f1ff;
    }
    
    .stats-header i {
      font-size: 1.8rem;
      margin-right: 15px;
      color: #1a6dff;
    }
    
    .stats-header h2 {
      font-size: 1.4rem;
      font-weight: 600;
      color: #1a6dff;
    }
    
    .stat-item {
      margin-bottom: 18px;
      padding-left: 10px;
      border-left: 3px solid transparent;
      transition: all 0.3s;
    }
    
    .stat-item:hover {
      border-left: 3px solid #1a6dff;
    }
    
    .stat-label {
      font-size: 0.95rem;
      color: #6c7a92;
      margin-bottom: 5px;
    }
    
    .stat-value {
      font-size: 1.5rem;
      font-weight: 700;
      color: #1a3b75;
      letter-spacing: 1px;
    }
    
    .log-container {
      background: #ffffff;
      border-radius: 12px;
      padding: 25px;
      box-shadow: 0 8px 30px rgba(0, 100, 255, 0.1);
      border: 1px solid #e1eaf9;
    }
    
    .filters {
      display: flex;
      flex-wrap: wrap;
      gap: 15px;
      margin-bottom: 25px;
      padding-bottom: 20px;
      border-bottom: 1px solid #e8f1ff;
    }
    
    .filter-item {
      flex: 1;
      min-width: 200px;
    }
    
    .log-list {
      min-height: 500px;
    }
    
    .log-item {
      background: #f8fbff;
      border: 1px solid #e1eaf9;
      border-radius: 10px;
      padding: 20px;
      margin-bottom: 15px;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
    }
    
    .log-item:hover {
      background: #ffffff;
      transform: translateY(-3px);
      box-shadow: 0 5px 15px rgba(26, 109, 255, 0.1);
      border-color: #c2d8ff;
    }
    
    .log-item::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      height: 100%;
      width: 4px;
      background: linear-gradient(to bottom, #1a6dff, #4da6ff);
    }
    
    .log-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
    }
    
    .log-time {
      font-size: 1rem;
      font-weight: 600;
      color: #1a6dff;
    }
    
    .log-tag {
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 600;
    }
    
    .log-content {
      font-size: 1.05rem;
      line-height: 1.6;
      color: #2c3e50;
    }
    
    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 30px;
      padding-top: 25px;
      border-top: 1px solid #e8f1ff;
    }
    
    .no-data {
      text-align: center;
      padding: 50px 0;
      color: #8ca6db;
      font-size: 1.2rem;
      background: #f8fbff;
      border-radius: 10px;
      margin-top: 20px;
    }
    
    .no-data i {
      font-size: 3rem;
      margin-bottom: 20px;
      display: block;
      color: #c2d8ff;
    }
    
    .footer {
      text-align: center;
      margin-top: 40px;
      padding-top: 20px;
      color: #8ca6db;
      font-size: 0.9rem;
      border-top: 1px solid #e1eaf9;
    }
    
    .tech-bar {
      height: 4px;
      background: linear-gradient(90deg, #1a6dff, #4da6ff, #1a6dff);
      border-radius: 2px;
      margin: 15px 0;
      position: relative;
      overflow: hidden;
    }
    
    .tech-bar::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      height: 100%;
      width: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent);
      animation: techBar 2s infinite linear;
    }
    
    @keyframes techBar {
      0% { transform: translateX(-100%); }
      100% { transform: translateX(100%); }
    }
    
    @media (max-width: 900px) {
      .dashboard {
        grid-template-columns: 1fr;
      }
      
      .header h1 {
        font-size: 2.2rem;
      }
      
      .filters {
        flex-direction: column;
      }
    }
    
    /* 自定义Element UI样式 - 浅色主题 */
    .el-input__inner {
      background: #f8fbff !important;
      border: 1px solid #d7e5ff !important;
      color: #2c3e50 !important;
      border-radius: 8px !important;
      transition: all 0.3s;
    }
    
    .el-input__inner:focus {
      border-color: #1a6dff !important;
      box-shadow: 0 0 0 2px rgba(26, 109, 255, 0.2) !important;
    }
    
    .el-select .el-input__inner {
      background: #f8fbff !important;
    }
    
    .el-pagination.is-background .btn-next,
    .el-pagination.is-background .btn-prev,
    .el-pagination.is-background .el-pager li {
      background: #f8fbff !important;
      border: 1px solid #d7e5ff !important;
      color: #5a7dab !important;
      transition: all 0.3s;
    }
    
    .el-pagination.is-background .el-pager li:not(.disabled).active {
      background: linear-gradient(90deg, #1a6dff, #4da6ff) !important;
      color: #ffffff !important;
      font-weight: bold;
      border-color: #1a6dff !important;
    }
    
    .el-pagination.is-background .btn-next:not(.disabled):hover,
    .el-pagination.is-background .btn-prev:not(.disabled):hover,
    .el-pagination.is-background .el-pager li:not(.disabled):hover {
      color: #1a6dff !important;
      border-color: #1a6dff !important;
    }
    
    .el-tag {
      background: rgba(26, 109, 255, 0.1) !important;
      color: #1a6dff !important;
      border: 1px solid rgba(26, 109, 255, 0.3) !important;
      font-weight: 600;
    }
    
    .el-tag.el-tag--success {
      background: rgba(0, 200, 83, 0.1) !important;
      color: #00c853 !important;
      border: 1px solid rgba(0, 200, 83, 0.3) !important;
    }
    
    .el-tag.el-tag--danger {
      background: rgba(255, 87, 87, 0.1) !important;
      color: #ff5757 !important;
      border: 1px solid rgba(255, 87, 87, 0.3) !important;
    }
    
    .el-tag.el-tag--warning {
      background: rgba(255, 193, 7, 0.1) !important;
      color: #ffc107 !important;
      border: 1px solid rgba(255, 193, 7, 0.3) !important;
    }
    
    .el-tag.el-tag--info {
      background: rgba(100, 181, 246, 0.1) !important;
      color: #64b5f6 !important;
      border: 1px solid rgba(100, 181, 246, 0.3) !important;
    }
    
    /* 动画效果 */
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }
    
    .animated {
      animation: fadeIn 0.6s ease-out forwards;
    }
    
    .delay-1 { animation-delay: 0.1s; }
    .delay-2 { animation-delay: 0.2s; }
    .delay-3 { animation-delay: 0.3s; }
    
    .system-status {
      display: flex;
      justify-content: space-around;
      margin: 20px 0;
      padding: 15px;
      background: #ffffff;
      border-radius: 10px;
      box-shadow: 0 4px 15px rgba(0, 100, 255, 0.08);
    }
    
    .status-item {
      text-align: center;
      padding: 10px;
    }
    
    .status-icon {
      width: 50px;
      height: 50px;
      margin: 0 auto 10px;
      background: #f0f7ff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #1a6dff;
      font-size: 1.5rem;
    }
    
    .status-text {
      font-size: 0.9rem;
      color: #5a7dab;
    }
    
    .status-value {
      font-weight: bold;
      color: #1a3b75;
      font-size: 1.1rem;
      margin-top: 5px;
    }
    
    /* 按钮样式优化 - 新布局 */
    .search-btn {
      padding: 14px 28px !important;
      font-size: 15px !important;
      font-weight: 700 !important;
      border-radius: 12px !important;
      background: linear-gradient(135deg, #1a6dff, #4da6ff) !important;
      border: none !important;
      box-shadow: 0 6px 16px rgba(26, 109, 255, 0.3) !important;
      transition: all 0.3s ease !important;
      height: 44px !important;
      min-width: 100px !important;
    }
    
    .search-btn:hover {
      transform: translateY(-3px) !important;
      box-shadow: 0 8px 20px rgba(26, 109, 255, 0.4) !important;
      background: linear-gradient(135deg, #0d5fdb, #3a8bff) !important;
    }
    
    .search-btn:active {
      transform: translateY(-1px) !important;
      box-shadow: 0 4px 12px rgba(26, 109, 255, 0.3) !important;
    }
    
    .reset-btn {
      padding: 14px 28px !important;
      font-size: 15px !important;
      font-weight: 700 !important;
      border-radius: 12px !important;
      background: #ffffff !important;
      color: #5a7dab !important;
      border: 2px solid #d7e5ff !important;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
      transition: all 0.3s ease !important;
      height: 44px !important;
      min-width: 100px !important;
    }
    
    .reset-btn:hover {
      transform: translateY(-3px) !important;
      background: #f8fbff !important;
      color: #1a6dff !important;
      border-color: #1a6dff !important;
      box-shadow: 0 6px 16px rgba(26, 109, 255, 0.15) !important;
    }
    
    .reset-btn:active {
      transform: translateY(-1px) !important;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
    }
    
    /* 搜索容器整体布局 */
    .search-container {
      background: #ffffff;
      border-radius: 16px;
      box-shadow: 0 8px 32px rgba(0, 100, 255, 0.12);
      border: 1px solid #e1eaf9;
      margin-bottom: 25px;
      overflow: hidden;
      position: relative;
    }
    
    .search-container::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #1a6dff, #4da6ff, #1a6dff);
    }
    
    .search-form {
      padding: 0 !important;
      margin: 0 !important;
      background: transparent !important;
      border: none !important;
      box-shadow: none !important;
    }
    
    .search-content {
      display: flex;
      align-items: center;
      padding: 25px 30px;
      gap: 20px;
      flex-wrap: wrap;
    }
    
    .button-group {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-shrink: 0;
    }
    
    .search-form-item {
      margin-bottom: 0 !important;
      margin-right: 0 !important;
      display: flex;
      align-items: center;
      flex: 1;
      min-width: 300px;
    }
    
    .search-form .el-form-item__label {
      font-size: 15px !important;
      font-weight: 700 !important;
      color: #1a3b75 !important;
      line-height: 44px !important;
      padding-right: 12px !important;
      min-width: 70px !important;
      flex-shrink: 0;
      white-space: nowrap;
    }
    
    .search-form .el-form-item__content {
      flex: 1;
      min-width: 0;
    }
    
    .search-input {
      width: 100% !important;
      max-width: 280px !important;
      min-width: 180px !important;
    }
    
    .search-input .el-input__inner {
      height: 44px !important;
      line-height: 44px !important;
      padding: 0 18px !important;
      font-size: 15px !important;
      background: #f8fbff !important;
      border: 2px solid #d7e5ff !important;
      border-radius: 12px !important;
      color: #2c3e50 !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04) !important;
    }
    
    .search-input .el-input__inner:focus {
      border-color: #1a6dff !important;
      background: #ffffff !important;
      box-shadow: 0 0 0 3px rgba(26, 109, 255, 0.1) !important;
      transform: translateY(-1px) !important;
    }
    
    .search-input .el-input__inner:hover {
      border-color: #4da6ff !important;
      background: #ffffff !important;
    }
    
    .search-input .el-input__inner::placeholder {
      color: #8ca6db !important;
      font-size: 13px !important;
    }
    
    .search-input .el-input__suffix {
      right: 12px !important;
    }
    
    .search-input .el-input__suffix .el-input__clear {
      color: #8ca6db !important;
      font-size: 16px !important;
      transition: all 0.3s ease !important;
    }
    
    .search-input .el-input__suffix .el-input__clear:hover {
      color: #1a6dff !important;
      transform: scale(1.1) !important;
    }
    
    .search-input .el-input__prefix {
      left: 12px !important;
    }
    
    .search-input .el-input__prefix .el-input__icon {
      color: #8ca6db !important;
      font-size: 16px !important;
    }
    
    /* 搜索表单响应式设计 */
    @media (max-width: 900px) {
      .search-content {
        gap: 15px;
      }
      
      .search-form-item {
        min-width: 250px;
      }
      
      .search-input {
        max-width: 250px !important;
      }
    }
    
    @media (max-width: 768px) {
      .search-container {
        margin-bottom: 20px;
      }
      
      .search-content {
        flex-direction: column;
        align-items: stretch;
        padding: 20px;
        gap: 15px;
      }
      
      .search-form-item {
        width: 100%;
        min-width: auto;
      }
      
      .button-group {
        justify-content: center;
        gap: 15px;
      }
      
      .search-input {
        max-width: 100% !important;
        min-width: auto !important;
      }
      
      .search-btn,
      .reset-btn {
        flex: 1;
        min-width: 120px !important;
      }
    }
    
    @media (max-width: 600px) {
      .search-content {
        padding: 15px;
        gap: 12px;
      }
      
      .search-form .el-form-item__label {
        min-width: 60px !important;
        font-size: 14px !important;
        padding-right: 8px !important;
      }
      
      .search-input {
        max-width: 100% !important;
        min-width: 150px !important;
      }
    }
    
    @media (max-width: 480px) {
      .search-content {
        padding: 15px;
      }
      
      .search-form .el-form-item__label {
        font-size: 14px !important;
        min-width: 70px !important;
      }
      
      .search-input .el-input__inner {
        height: 40px !important;
        line-height: 40px !important;
        font-size: 14px !important;
      }
      
      .search-btn,
      .reset-btn {
        height: 40px !important;
        padding: 10px 20px !important;
        font-size: 14px !important;
      }
    }
</style>
