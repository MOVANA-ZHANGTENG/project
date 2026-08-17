<template>
  <div class="handle-info-isolated-container" style="width: 100%">
    <el-card @click.native="isUpdate = true" class="dark-card handle-card">
      <div class="tags-container">
        <el-empty v-if="!value || value.length === 0" 
          description="暂无执行器，点击添加" 
          :image-size="50"
          class="mini-empty"
        ></el-empty>
        <draggable v-else v-model="value" draggable=".mode2" class="draggable-wrapper">
          <el-tag
            class="mode2 dark-tag"
            size="mini"
            :key="tag.code"
            :disable-transitions="false"
            v-for="tag in value"
          >
            {{ tag.name }}
          </el-tag>
        </draggable>
      </div>
    </el-card>

    <el-dialog 
      title="执行器选择" 
      :visible.sync="isUpdate" 
      append-to-body 
      class="dark-dialog responsive-dialog" 
      width="90%" 
      :fullscreen="isMobile"
      @close="handleDialogClose"
      @opened="handleDialogOpened"
    >
      <el-card v-if="isUpdate" class="dark-card dialog-card" style="width: 100%; position: relative">
        <el-row :gutter="16">
          <!-- 左侧：已选执行器 -->
          <el-col :span="10">
            <el-card class="dark-card inner-card selector-card" style="text-align: left; padding: 8px">
              <div class="card-title selected-title" style="text-align: center; padding-bottom: 12px">
                <span class="custom-icon success-icon">✓</span>
                <span>已选执行器</span>
                <el-badge :value="value.length" :max="99" class="count-badge-new" v-if="value && value.length" />
              </div>
              
              <!-- 已选区域操作栏 -->
              <div class="toolbar-section" v-if="value && value.length">
                <el-button 
                  size="mini" 
                  type="text" 
                  @click="clearAllSelected" 
                  class="toolbar-btn danger-btn"
                >
                  <span class="custom-icon">🗑️</span>
                  <span>清空</span>
                </el-button>
              </div>
              
              <!-- 已选列表 -->
              <div 
                class="selector-list"
                role="list"
                aria-label="已选执行器列表"
                aria-live="polite"
              >
                <el-empty v-if="!value || value.length === 0" 
                  description="暂无已选执行器" 
                  :image-size="100"
                  class="empty-state"
                ></el-empty>
                <draggable v-else v-model="value" draggable=".mode" :animation="150" class="draggable-list">
                  <div class="mode dark-mode-item" v-for="(item, index) in value" :key="item.code" @dblclick="removeItem(item)">
                    <el-tooltip :content="item.className + '.' + item.methodName" placement="left">
                      <el-checkbox
                        v-model="item.isSelect"
                        :label="item.name"
                        border
                        class="dark-checkbox"
                      >
                        <template slot="default">
                          <div class="checkbox-content">
                            <span class="custom-icon drag-handle">☰</span>
                            <span class="checkbox-label">{{ item.name }}</span>
                            <el-tag size="mini" type="info" class="index-tag">{{ index + 1 }}</el-tag>
                          </div>
                        </template>
                      </el-checkbox>
                    </el-tooltip>
                  </div>
                </draggable>
              </div>
            </el-card>
          </el-col>

          <!-- 中间：操作按钮 -->
          <el-col :span="4">
            <div class="action-buttons-wrapper">
              <el-tooltip content="添加选中的执行器（Enter）" placement="top">
                <el-button
                  @click="addSelected()"
                  type="text"
                  class="dark-icon-btn transfer-btn add-btn"
                  :disabled="!hasAvailableSelected"
                >
                  <span class="custom-icon-large">⬅</span>
                  <div class="btn-label">添加</div>
                </el-button>
              </el-tooltip>
              
              <el-tooltip content="移除选中的执行器（Delete）" placement="bottom">
                <el-button
                  @click="removeSelected()"
                  type="text"
                  class="dark-icon-btn transfer-btn remove-btn"
                  :disabled="!hasSelectedItems"
                >
                  <span class="custom-icon-large">➡</span>
                  <div class="btn-label">移除</div>
                </el-button>
              </el-tooltip>
            </div>
          </el-col>

          <!-- 右侧：待选执行器 -->
          <el-col :span="10">
            <el-card class="dark-card inner-card selector-card" style="text-align: left; padding: 8px">
              <div class="card-title available-title" style="text-align: center; padding-bottom: 12px">
                <span class="custom-icon primary-icon">➕</span>
                <span>待选执行器</span>
                <el-badge :value="filteredHandleList.length" :max="99" class="count-badge-new" v-if="filteredHandleList.length" />
              </div>
              
              
              <!-- 待选列表 -->
              <div 
                class="selector-list"
                role="list"
                aria-label="待选执行器列表"
                :aria-busy="loading"
                v-loading="loading"
                element-loading-text="加载中..."
                element-loading-background="rgba(20, 25, 40, 0.8)"
              >
                <el-empty v-if="!loading && filteredHandleList.length === 0" 
                  description="暂无可选执行器" 
                  :image-size="100"
                  class="empty-state"
                ></el-empty>
                
                <!-- 分组视图（默认且唯一视图） -->
                <div v-else class="grouped-view">
                  <div 
                    v-for="group in groupedHandleList" 
                    :key="group.id"
                    class="group-container"
                  >
                    <!-- 分组头部 -->
                    <div class="group-header" @click="toggleGroup(group.id)">
                      <div class="group-header-left">
                        <span class="custom-icon toggle-icon">{{ isGroupCollapsed(group.id) ? '▶' : '▼' }}</span>
                        <span class="custom-icon group-icon">📁</span>
                        <span class="group-name">{{ group.name }}</span>
                        <el-badge 
                          :value="group.items.length" 
                          :max="999"
                          class="group-count-badge"
                        />
                        <el-tag 
                          v-if="group.selectedCount > 0"
                          size="mini" 
                          type="success"
                          class="group-selected-tag"
                        >
                          <span class="custom-icon-mini">✓</span>
                          已选 {{ group.selectedCount }}
                        </el-tag>
                      </div>
                    </div>
                    
                    <!-- 分组内容 -->
                    <transition name="expand">
                      <div v-show="!isGroupCollapsed(group.id)" class="group-content">
                      <div
                        v-for="(Handle, index) in group.items"
                        :key="Handle.code"
                        @dblclick="addItem(Handle)"
                        class="group-item"
                      >
                        <el-tooltip :content="Handle.className + '.' + Handle.methodName" placement="right">
                          <el-checkbox
                            v-model="Handle.isSelect"
                            :label="Handle.name"
                            border
                            class="dark-checkbox grouped-checkbox"
                          ></el-checkbox>
                        </el-tooltip>
                      </div>
                      </div>
                    </transition>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 底部提示 -->
      <div slot="footer" class="dialog-footer-tip">
        <div class="tip-content">
          <span class="custom-icon">💡</span>
          <span>提示：双击执行器快速添加/移除，拖拽左侧已选项可调整顺序</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listHandle,
  listHandleAll,
  getHandle,
  delHandle,
  addHandle,
  updateHandle,
} from "@/api/wcs-base/Handle";
import draggable from "vuedraggable";
export default {
  name: "Handle",
  data() {
    return {
      isUpdate: false,
      collapsedGroups: {}, // 折叠状态管理
      isMobile: false, // 是否移动端
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
      // 执行器表格数据
      HandleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 999,
        className: null,
        methodName: null,
        code: null,
        name: null,
        createUserId: null,
        createUserName: null,
        udpateTime: null,
        udpateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  props: {
    value: {
      type: Array,
      default: function () {
        return [];
      },
    },
    handleType: {
      type: Number,
    },
    type: {
      type: Number,
    },
  },
  computed: {
    // 过滤后的待选列表（排除已选）
    filteredHandleList() {
      return this.HandleList.filter(handle => !this.has(handle.code));
    },
    
    // 按分组归类的待选列表
    groupedHandleList() {
      const groups = {};
      
      this.filteredHandleList.forEach(handle => {
        const groupId = handle.groupId || 'ungrouped';
        const groupName = handle.groupName || '未分组';
        
        if (!groups[groupId]) {
          groups[groupId] = {
            id: groupId,
            name: groupName,
            items: [],
            selectedCount: 0
          };
        }
        
        groups[groupId].items.push(handle);
        
        // 统计选中数量
        if (handle.isSelect) {
          groups[groupId].selectedCount++;
        }
      });
      
      // 转换为数组并排序
      return Object.values(groups).sort((a, b) => {
        // 优先按分组名称排序
        return a.name.localeCompare(b.name, 'zh-CN');
      });
    },
    
    // 是否有待选项被选中
    hasAvailableSelected() {
      return this.filteredHandleList.some(item => item.isSelect);
    },
    
    // 是否有已选项被选中
    hasSelectedItems() {
      return this.value && this.value.some(item => item.isSelect);
    }
  },
  watch: {
    // 使用handler函数进行深度监听
    value: {
      handler: function (newValue, oldValue) {
        this.$emit("input", newValue);
      },
      deep: true, // 开启深度监听
    },
  },
  components: {
    draggable,
  },
  created() {
    this.getList();
  },
  mounted() {
    // 添加键盘快捷键支持
    document.addEventListener('keydown', this.handleKeyDown);
    
    // 检测设备类型
    this.checkDevice();
    
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    // 移除键盘监听
    document.removeEventListener('keydown', this.handleKeyDown);
    
    // 移除resize监听
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    /**
     * 键盘快捷键处理
     */
    handleKeyDown(e) {
      if (!this.isUpdate) return;
      
      // Enter - 添加选中项
      if (e.key === 'Enter' && this.hasAvailableSelected) {
        e.preventDefault();
        this.addSelected();
      }
      // Delete - 移除选中项
      else if (e.key === 'Delete' && this.hasSelectedItems) {
        e.preventDefault();
        this.removeSelected();
      }
    },
    
    /**
     * 对话框打开处理
     */
    handleDialogOpened() {
      // 初始化分组折叠状态（默认全部折叠）
      this.$nextTick(() => {
        this.initCollapsedGroups();
      });
    },
    
    /**
     * 对话框关闭处理
     */
    handleDialogClose() {
      // 清空所有选中状态
      this.HandleList.forEach(item => item.isSelect = false);
      if (this.value) {
        this.value.forEach(item => item.isSelect = false);
      }
      // 重置折叠状态，下次打开时重新初始化
      this.collapsedGroups = {};
    },
    
    /**
     * 检测设备类型
     */
    checkDevice() {
      this.isMobile = window.innerWidth < 768;
    },
    
    /**
     * 窗口大小变化处理
     */
    handleResize() {
      this.checkDevice();
    },
    
    /**
     * 搜索处理
     */
    handleSearch() {
      // 搜索时清空待选区域的选中状态
      this.HandleList.forEach(item => item.isSelect = false);
    },
    
    /**
     * 添加选中的执行器（从右到左）
     */
    addSelected() {
      const data = this.value || [];
      let addCount = 0;
      
      this.filteredHandleList.forEach((element) => {
        if (element.isSelect) {
          this.push(data, element);
          element.isSelect = false; // 添加后取消选中
          addCount++;
        }
      });
      
      if (addCount > 0) {
        this.$message.success(`成功添加 ${addCount} 个执行器`);
      }
    },
    
    /**
     * 移除选中的执行器（从左到右）
     */
    removeSelected() {
      const removeCount = this.value.filter(item => item.isSelect).length;
      
      this.value = this.value.filter(function (element) {
        return element.isSelect == null || element.isSelect == false;
      });
      
      // 清空剩余项的选中状态
      this.value.forEach((element) => {
        element.isSelect = false;
      });
      
      if (removeCount > 0) {
        this.$message.success(`成功移除 ${removeCount} 个执行器`);
      }
    },
    
    /**
     * 添加单个执行器（双击快捷操作）
     */
    addItem(item) {
      const data = this.value || [];
      this.push(data, item);
      item.isSelect = false;
      this.$message.success(`已添加：${item.name}`);
    },
    
    /**
     * 移除单个执行器（双击快捷操作）
     */
    removeItem(item) {
      const index = this.value.findIndex(v => v.code === item.code);
      if (index > -1) {
        this.value.splice(index, 1);
        this.$message.success(`已移除：${item.name}`);
      }
    },
    
    /**
     * 添加全部执行器
     */
    addAll() {
      const data = this.value || [];
      let addCount = 0;
      
      this.filteredHandleList.forEach((element) => {
        this.push(data, element);
        element.isSelect = false;
        addCount++;
      });
      
      if (addCount > 0) {
        this.$message.success(`成功添加全部 ${addCount} 个执行器`);
      }
    },
    
    /**
     * 移除全部执行器
     */
    removeAll() {
      const removeCount = this.value.length;
      
      this.$confirm(`确定要移除全部 ${removeCount} 个执行器吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.value.splice(0, this.value.length);
        this.$message.success(`已移除全部 ${removeCount} 个执行器`);
      }).catch(() => {});
    },
    
    /**
     * 全选已选区域
     */
    selectAllSelected() {
      if (this.value && this.value.length) {
        this.value.forEach(item => item.isSelect = true);
        this.$message.success('已全选');
      }
    },
    
    /**
     * 清空已选区域
     */
    clearAllSelected() {
      const count = this.value.length;
      if (count === 0) return;
      
      this.$confirm(`确定要清空全部 ${count} 个已选执行器吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.value.splice(0, this.value.length);
        this.$message.success('已清空');
      }).catch(() => {});
    },
    
    /**
     * 全选待选区域
     */
    selectAllAvailable() {
      this.filteredHandleList.forEach(item => item.isSelect = true);
      this.$message.success('已全选');
    },
    
    /**
     * 取消待选区域选中
     */
    unselectAllAvailable() {
      this.filteredHandleList.forEach(item => item.isSelect = false);
      this.$message.success('已取消选中');
    },
    
    /**
     * 初始化分组折叠状态（默认全部折叠）
     */
    initCollapsedGroups() {
      this.groupedHandleList.forEach(group => {
        if (this.collapsedGroups[group.id] === undefined) {
          this.$set(this.collapsedGroups, group.id, true); // 默认折叠
        }
      });
    },
    
    /**
     * 切换分组折叠状态
     */
    toggleGroup(groupId) {
      this.$set(this.collapsedGroups, groupId, !this.collapsedGroups[groupId]);
    },
    
    /**
     * 判断分组是否折叠
     */
    isGroupCollapsed(groupId) {
      return this.collapsedGroups[groupId] === true;
    },
    
    /**
     * 全选/取消全选分组
     */
    toggleGroupSelection(group) {
      const allSelected = group.selectedCount === group.items.length;
      
      group.items.forEach(item => {
        item.isSelect = !allSelected;
      });
      
      if (allSelected) {
        this.$message.success(`已取消选中分组：${group.name}`);
      } else {
        this.$message.success(`已全选分组：${group.name}（${group.items.length}项）`);
      }
    },
    
    /**
     * 快速添加整个分组
     */
    addGroupItems(group) {
      const data = this.value || [];
      let addCount = 0;
      
      group.items.forEach((element) => {
        this.push(data, element);
        element.isSelect = false;
        addCount++;
      });
      
      if (addCount > 0) {
        this.$message.success(`已添加分组【${group.name}】的 ${addCount} 个执行器`);
      }
    },
    
    /**
     * 切换视图模式
     */
    toggleViewMode() {
      this.groupViewMode = this.groupViewMode === 'grouped' ? 'flat' : 'grouped';
      this.$message.success(this.groupViewMode === 'grouped' ? '已切换到分组视图' : '已切换到列表视图');
    },
    
    /**
     * 展开所有分组
     */
    expandAllGroups() {
      this.groupedHandleList.forEach(group => {
        this.$set(this.collapsedGroups, group.id, false);
      });
      this.$message.success('已展开所有分组');
    },
    
    /**
     * 折叠所有分组
     */
    collapseAllGroups() {
      this.groupedHandleList.forEach(group => {
        this.$set(this.collapsedGroups, group.id, true);
      });
      this.$message.success('已折叠所有分组');
    },
    
    /**
     * 检查是否已存在
     */
    has(code) {
      if (!this.value) return false;
      for (let index = 0; index < this.value.length; index++) {
        const element = this.value[index];
        if (element.code == code) {
          return true;
        }
      }
      return false;
    },
    
    /**
     * 添加到数组（去重）
     */
    push(data, value) {
      for (let index = 0; index < data.length; index++) {
        const element = data[index];
        if (element.code == value.code) {
          return;
        }
      }
      // 深拷贝以避免引用问题
      const newItem = JSON.parse(JSON.stringify(value));
      newItem.isSelect = false;
      data.push(newItem);
    },
    /** 查询执行器列表 */
    getList() {
      this.loading = true;
      // if (this.handleType == "jobInfo") {
      //   this.queryParams.type = 0;
      // } else if (this.handleType == "pathInfo") {
      //   this.queryParams.type = 1;
      // } else {
      //   this.HandleList = [];
      //   this.loading = false;
      //   this.$modal.msgError("组件传入的handleType有误");
      //   return;
      // }
      this.queryParams.type = this.type;
      this.queryParams.handleType = this.handleType;
      listHandleAll(this.queryParams).then((response) => {
        this.HandleList = response.rows;
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
        className: null,
        methodName: null,
        code: null,
        name: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        udpateTime: null,
        udpateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null,
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
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加执行器";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getHandle(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改执行器";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateHandle(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addHandle(this.form).then((response) => {
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
      this.$modal
        .confirm('是否确认删除执行器编号为"' + ids + '"的数据项？')
        .then(function () {
          return delHandle(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-base/Handle/export",
        {
          ...this.queryParams,
        },
        `Handle_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>

<style lang="scss">
// ==========================================
// 🔥 全局样式 - 对话框相关（append-to-body）
// ==========================================

// 完整变量定义（全局样式）
$color-primary: #667eea;
$color-primary-light: #818cf8;
$color-primary-lighter: #a5b4fc;
$color-success: #10b981;
$color-success-light: #34d399;
$color-danger: #ef4444;
$color-warning: #f59e0b;
$color-warning-light: #fbbf24;

$gradient-primary: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
$gradient-success: linear-gradient(135deg, #10b981 0%, #06b6d4 100%);
$gradient-danger: linear-gradient(135deg, #fa709a 0%, #fee140 100%);

$text-primary: #ffffff;
$text-secondary: #e5e7eb;
$text-tertiary: #d1d5db;

$spacing-sm: 8px;
$spacing-md: 12px;
$spacing-lg: 16px;
$spacing-xl: 20px;

$radius-sm: 6px;
$radius-md: 8px;
$radius-lg: 12px;

$font-size-xs: 11px;
$font-size-sm: 12px;
$font-size-base: 13px;
$font-size-md: 14px;
$font-size-lg: 15px;
$font-size-xl: 16px;

$font-weight-normal: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;

$transition-base: 0.3s;
$transition-easing: cubic-bezier(0.4, 0, 0.2, 1);

// 🎨 对话框样式（强制应用到 body 下的对话框）
.dark-dialog.responsive-dialog {
  // 确保文字可见
  div:not([class*="custom-icon"]),
  span:not([class*="custom-icon"]),
  p,
  label {
    color: #ffffff !important;
  }
  
  // ==========================================
  // 🎨 自定义图标系统（全局）
  // ==========================================
  
  .custom-icon,
  .custom-icon-large,
  .custom-icon-mini {
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    line-height: 1 !important;
    font-style: normal !important;
    user-select: none !important;
  }
  
  .custom-icon {
    font-size: 18px !important;
  }
  
  .custom-icon-large {
    font-size: 34px !important;
  }
  
  .custom-icon-mini {
    font-size: 12px !important;
    margin-right: 4px !important;
  }
  
  .success-icon {
    color: #ffffff !important;
    filter: drop-shadow(0 2px 6px rgba(16, 185, 129, 0.9)) !important;
  }
  
  .primary-icon {
    color: #ffffff !important;
    filter: drop-shadow(0 2px 6px rgba(99, 102, 241, 0.9)) !important;
  }
  
  .group-icon {
    filter: drop-shadow(0 2px 6px rgba(245, 158, 11, 0.8)) !important;
    animation: pulse-glow 3s ease-in-out infinite !important;
  }
  
  @keyframes pulse-glow {
    0%, 100% {
      filter: drop-shadow(0 2px 6px rgba(245, 158, 11, 0.6));
    }
    50% {
      filter: drop-shadow(0 2px 10px rgba(245, 158, 11, 1));
    }
  }
  
  .drag-handle {
    color: #9ca3af !important;
    cursor: move !important;
    
    &:hover {
      color: #a5b4fc !important;
    }
  }
  
  // 折叠展开图标
  .toggle-icon {
    color: #a5b4fc !important;
    font-size: 16px !important;
    font-weight: bold !important;
    filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.7)) !important;
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    margin-right: 4px !important;
  }
  
  // 🎨 序号标签 - 统一字体
  .index-tag.el-tag {
    background: rgba(102, 126, 234, 0.3) !important;
    border: 1px solid rgba(102, 126, 234, 0.5) !important;
    color: $text-primary !important;
    font-size: $font-size-xs !important;
    font-weight: $font-weight-bold !important;
    box-shadow: 0 2px 4px rgba(102, 126, 234, 0.3) !important;
    min-width: 22px !important;
    height: 18px !important;
    line-height: 18px !important;
    padding: 0 6px !important;
    
    span {
      color: $text-primary !important;
    }
  }
  
  // 🎨 分组选中标签 - 统一字体
  .group-selected-tag.el-tag {
    background: rgba(16, 185, 129, 0.25) !important;
    border: 1px solid rgba(16, 185, 129, 0.5) !important;
    color: $text-primary !important;
    font-size: $font-size-xs !important;
    font-weight: $font-weight-semibold !important;
    box-shadow: 0 2px 4px rgba(16, 185, 129, 0.3) !important;
    
    span {
      color: $text-primary !important;
    }
  }
  
  // 对话框本体
  .el-dialog {
    background: rgba(20, 20, 36, 0.98) !important;
    backdrop-filter: blur(10px) !important;
    border: 2px solid rgba(99, 102, 241, 0.5) !important;
    box-shadow: 0 20px 80px rgba(0, 0, 0, 0.8),
                0 0 0 1px rgba(99, 102, 241, 0.3) !important;
    border-radius: 20px !important;
  }
  
  // 对话框标题
  .el-dialog__header {
    background: linear-gradient(135deg, rgba(35, 40, 55, 0.98) 0%, rgba(45, 50, 70, 0.98) 100%) !important;
    border-bottom: 2px solid rgba(99, 102, 241, 0.4) !important;
    padding: 24px 30px !important;
    border-radius: 20px 20px 0 0 !important;
  }
  
  .el-dialog__title {
    color: $text-primary !important;
    font-size: $font-size-xl !important;
    font-weight: $font-weight-bold !important;
    text-shadow: 0 2px 6px rgba(0, 0, 0, 0.8) !important;
    letter-spacing: 0.3px !important;
  }
  
  // 对话框主体
  .el-dialog__body {
    background: linear-gradient(135deg, rgba(15, 20, 35, 0.95) 0%, rgba(20, 25, 40, 0.95) 100%) !important;
    padding: 20px 30px !important;
  }
  
  // 🎨 卡片标题 - 统一字体系统
  .card-title {
    padding: 10px 14px !important;
    border-radius: 8px !important;
    margin-bottom: 12px !important;
    font-size: $font-size-md !important;
    font-weight: $font-weight-semibold !important;
    position: relative !important;
    overflow: hidden !important;
    
    .custom-icon {
      font-size: 16px !important;
    }
    
    span:not(.custom-icon) {
      color: $text-primary !important;
      font-weight: $font-weight-semibold !important;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.6) !important;
    }
    
    // 光泽动画
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
      transition: left 0.6s;
    }
    
    &:hover::before {
      left: 100%;
    }
  }
  
  // 🎨 已选执行器标题 - 紧凑版
  .selected-title {
    background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%) !important;
    border: 1px solid rgba(16, 185, 129, 0.6) !important;
    box-shadow: 0 3px 12px rgba(16, 185, 129, 0.35),
                inset 0 1px 0 rgba(255, 255, 255, 0.15) !important;
    padding: 8px 12px !important;
    
    i {
      color: #ffffff !important;
      filter: drop-shadow(0 2px 6px rgba(16, 185, 129, 0.9)) !important;
      font-size: 16px !important;
    }
    
    span {
      font-size: 14px !important;
    }
  }
  
  // 🎨 待选执行器标题 - 紧凑版
  .available-title {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
    border: 1px solid rgba(99, 102, 241, 0.6) !important;
    box-shadow: 0 3px 12px rgba(99, 102, 241, 0.35),
                inset 0 1px 0 rgba(255, 255, 255, 0.15) !important;
    padding: 8px 12px !important;
    
    i {
      color: #ffffff !important;
      filter: drop-shadow(0 2px 6px rgba(99, 102, 241, 0.9)) !important;
      font-size: 16px !important;
    }
    
    span {
      font-size: 14px !important;
    }
  }
  
  // 内部卡片
  .inner-card.el-card {
    background: linear-gradient(135deg, rgba(25, 30, 45, 0.9) 0%, rgba(30, 35, 50, 0.9) 100%) !important;
    border: 2px solid rgba(99, 102, 241, 0.3) !important;
    border-radius: 14px !important;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4) !important;
  }
  
  // 🎨 复选框 - 简洁优雅设计
  .dark-checkbox.el-checkbox.is-bordered {
    background: rgba(20, 25, 40, 0.6) !important;
    border: 1px solid rgba(102, 126, 234, 0.3) !important;
    border-radius: $radius-md !important;
    padding: 10px 14px !important;
    margin-bottom: 8px !important;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2) !important;
    transition: all $transition-base $transition-easing !important;
    
    &:hover {
      background: rgba(102, 126, 234, 0.15) !important;
      border-color: rgba(102, 126, 234, 0.5) !important;
      box-shadow: 0 3px 12px rgba(102, 126, 234, 0.25) !important;
      transform: translateX(3px) !important;
    }
    
    .el-checkbox__label {
      color: $text-primary !important;
      font-size: $font-size-base !important;
      font-weight: $font-weight-medium !important;
      line-height: 1.5 !important;
    }
    
    &.is-checked {
      background: rgba(102, 126, 234, 0.2) !important;
      border-color: $color-primary !important;
      box-shadow: 0 3px 12px rgba(102, 126, 234, 0.35),
                  0 0 0 1px rgba(102, 126, 234, 0.2) !important;
      
      .el-checkbox__label {
        font-weight: $font-weight-semibold !important;
      }
      
      .el-checkbox__inner {
        background: $color-primary !important;
        border-color: $color-primary !important;
      }
    }
  }
  
  // 分组容器
  .group-container {
    margin-bottom: 12px !important;
    border: 1px solid rgba(99, 102, 241, 0.35) !important;
    border-radius: 12px !important;
    background: linear-gradient(135deg, rgba(25, 30, 45, 0.85) 0%, rgba(30, 35, 50, 0.85) 100%) !important;
    overflow: hidden !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    
    &:hover {
      border-color: rgba(99, 102, 241, 0.6) !important;
      box-shadow: 0 8px 28px rgba(99, 102, 241, 0.3),
                  0 0 0 1px rgba(99, 102, 241, 0.25) !important;
      transform: translateY(-2px) !important;
    }
  }
  
  // 🎨 分组头部 - 玻璃态设计
  .group-header {
    background: linear-gradient(135deg, rgba(35, 40, 60, 0.95) 0%, rgba(45, 50, 70, 0.95) 100%) !important;
    padding: 14px 18px !important;
    border-bottom: 1px solid rgba(99, 102, 241, 0.3) !important;
    position: relative !important;
    
    // 扫光效果
    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.08), transparent);
      opacity: 0;
      transition: opacity 0.4s;
      pointer-events: none;
    }
    
    &:hover {
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.3) 0%, rgba(118, 75, 162, 0.3) 100%) !important;
      
      &::before {
        opacity: 1;
        animation: shine 1.5s ease-in-out;
      }
    }
    
    @keyframes shine {
      0% {
        transform: translateX(-100%);
      }
      100% {
        transform: translateX(100%);
      }
    }
    
    .group-name {
      color: $text-primary !important;
      font-weight: $font-weight-semibold !important;
      font-size: $font-size-md !important;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.6) !important;
      letter-spacing: 0.2px !important;
    }
    
    .group-icon {
      color: #fbbf24 !important;
      font-size: 22px !important;
      filter: drop-shadow(0 2px 6px rgba(245, 158, 11, 0.8)) !important;
      animation: pulse-glow 3s ease-in-out infinite !important;
    }
    
    @keyframes pulse-glow {
      0%, 100% {
        filter: drop-shadow(0 2px 6px rgba(245, 158, 11, 0.6));
      }
      50% {
        filter: drop-shadow(0 2px 10px rgba(245, 158, 11, 1));
      }
    }
    
    .group-toggle-icon {
      color: #a5b4fc !important;
      font-size: 18px !important;
      filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.7)) !important;
      transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    }
    
    // 🎨 数量徽章 - 统一字体
    .group-count-badge {
      .el-badge__content {
        background: rgba(102, 126, 234, 0.8) !important;
        border: 1px solid rgba(165, 180, 252, 0.5) !important;
        box-shadow: 0 2px 6px rgba(102, 126, 234, 0.4) !important;
        color: $text-primary !important;
        font-size: $font-size-xs !important;
        font-weight: $font-weight-bold !important;
        font-variant-numeric: tabular-nums !important;
      }
    }
  }
  
  // 搜索框样式
  .search-input {
    .el-input__inner {
      background: linear-gradient(135deg, rgba(15, 18, 30, 0.95) 0%, rgba(20, 25, 40, 0.95) 100%) !important;
      border: 2px solid rgba(99, 102, 241, 0.4) !important;
      color: #ffffff !important;
      border-radius: 10px !important;
      height: 38px !important;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.25),
                  inset 0 1px 2px rgba(0, 0, 0, 0.15) !important;
      
      &:hover {
        border-color: rgba(99, 102, 241, 0.6) !important;
        box-shadow: 0 4px 16px rgba(99, 102, 241, 0.2) !important;
      }
      
      &:focus {
        border-color: #6366f1 !important;
        box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.25),
                    0 4px 16px rgba(99, 102, 241, 0.4) !important;
      }
      
      &::placeholder {
        color: rgba(255, 255, 255, 0.5) !important;
      }
    }
    
    .el-input__prefix i {
      color: #a5b4fc !important;
      font-size: 16px !important;
      filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.6));
    }
  }
  
  // 🎨 按钮容器 - 垂直居中布局
  .action-buttons-wrapper {
    display: flex !important;
    flex-direction: column !important;
    align-items: center !important;
    justify-content: center !important;
    height: 100% !important;
    min-height: 450px !important;
    gap: 24px !important;
    
    // 确保 Tooltip 容器也居中
    .el-tooltip {
      display: flex !important;
      justify-content: center !important;
    }
  }
  
  // 🎨 传输按钮 - 固定尺寸确保对齐
  .transfer-btn {
    background: linear-gradient(135deg, rgba(30, 35, 50, 0.95) 0%, rgba(40, 45, 60, 0.95) 100%) !important;
    border: 2px solid rgba(99, 102, 241, 0.5) !important;
    border-radius: $radius-lg !important;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35),
                inset 0 1px 0 rgba(255, 255, 255, 0.1) !important;
    width: 100px !important;
    height: 85px !important;
    min-width: 100px !important;
    max-width: 100px !important;
    min-height: 85px !important;
    max-height: 85px !important;
    display: flex !important;
    flex-direction: column !important;
    align-items: center !important;
    justify-content: center !important;
    padding: 0 !important;
    margin: 0 !important;
    
    .custom-icon-large {
      color: #a5b4fc !important;
      font-size: 32px !important;
      filter: drop-shadow(0 2px 6px rgba(99, 102, 241, 0.6)) !important;
      margin-bottom: 8px !important;
    }
    
    .btn-label {
      color: $text-secondary !important;
      font-size: $font-size-md !important;
      font-weight: $font-weight-semibold !important;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6) !important;
    }
    
    &.add-btn:hover {
      background: $gradient-success !important;
      border-color: $color-success !important;
      box-shadow: 0 8px 32px rgba(16, 185, 129, 0.5),
                  inset 0 1px 0 rgba(255, 255, 255, 0.2) !important;
      transform: scale(1.12) !important;
      
      .custom-icon-large {
        color: #ffffff !important;
        filter: drop-shadow(0 4px 10px rgba(16, 185, 129, 0.9)) !important;
        transform: translateY(-3px);
      }
      
      .btn-label {
        color: #ffffff !important;
      }
    }
    
    &.remove-btn:hover {
      background: $gradient-danger !important;
      border-color: $color-danger !important;
      box-shadow: 0 8px 32px rgba(239, 68, 68, 0.5),
                  inset 0 1px 0 rgba(255, 255, 255, 0.2) !important;
      transform: scale(1.12) !important;
      
      .custom-icon-large {
        color: #ffffff !important;
        filter: drop-shadow(0 4px 10px rgba(239, 68, 68, 0.9)) !important;
        transform: translateY(-3px);
      }
      
      .btn-label {
        color: #ffffff !important;
      }
    }
  }
  
  // 视图切换按钮组
  .view-mode-group {
    border: 2px solid rgba(99, 102, 241, 0.5) !important;
    border-radius: 8px !important;
    overflow: hidden !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2) !important;
    
    .el-button {
      color: #e0e7ef !important;
      font-weight: 600 !important;
      
      span {
        color: #e0e7ef !important;
      }
      
      &.el-button--primary {
        background: linear-gradient(135deg, rgba(99, 102, 241, 0.7) 0%, rgba(118, 75, 162, 0.7) 100%) !important;
        
        span {
          color: #ffffff !important;
        }
      }
    }
  }
  
  // 🎨 工具栏按钮 - 统一字体
  .toolbar-btn {
    color: $text-secondary !important;
    font-size: $font-size-sm !important;
    font-weight: $font-weight-medium !important;
    
    span {
      color: $text-secondary !important;
    }
    
    &:hover {
      color: $text-primary !important;
      background: rgba(102, 126, 234, 0.2) !important;
      
      span {
        color: $text-primary !important;
      }
    }
    
    &.danger-btn {
      color: #fbbf24 !important;
      
      span {
        color: #fbbf24 !important;
      }
      
      &:hover {
        color: $text-primary !important;
        background: rgba(245, 158, 11, 0.2) !important;
        
        span {
          color: $text-primary !important;
        }
      }
    }
  }
}
</style>

<style lang="scss" scoped>
// ==========================================
// 变量定义（Scoped样式需要单独定义）
// ==========================================
$color-primary: #667eea;
$color-primary-light: #818cf8;
$color-primary-lighter: #a5b4fc;
$color-success: #10b981;
$color-danger: #ef4444;
$color-warning: #f59e0b;
$color-warning-light: #fbbf24;

$gradient-primary: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
$gradient-success: linear-gradient(135deg, #10b981 0%, #06b6d4 100%);
$gradient-danger: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
$gradient-card: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);

$bg-dark-primary: rgba(20, 25, 40, 0.85);
$bg-dark-secondary: rgba(30, 35, 50, 0.95);
$bg-dark-tertiary: rgba(35, 40, 60, 0.9);
$bg-dark-input: rgba(15, 18, 30, 0.9);

$text-primary: #ffffff;
$text-secondary: #e5e7eb;
$text-tertiary: #d1d5db;
$text-disabled: #9ca3af;

$border-base: rgba(102, 126, 234, 0.25);
$border-light: rgba(102, 126, 234, 0.3);
$border-lighter: rgba(102, 126, 234, 0.4);

$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 12px;
$spacing-lg: 16px;
$spacing-xl: 20px;

$radius-sm: 6px;
$radius-md: 8px;
$radius-lg: 12px;

$shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.2);
$shadow-md: 0 4px 16px rgba(0, 0, 0, 0.3);

$font-size-xs: 11px;
$font-size-sm: 12px;
$font-size-base: 13px;
$font-size-md: 14px;
$font-size-lg: 15px;

$font-weight-normal: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;

$transition-base: 0.3s;
$transition-easing: cubic-bezier(0.4, 0, 0.2, 1);

// ==========================================
// 🎨 主容器样式
// ==========================================
.handle-info-isolated-container {
  // ==========================================
  // 🎨 自定义图标系统
  // ==========================================
  
  // 通用图标样式
  .custom-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    line-height: 1;
    font-style: normal;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  // 大号图标（传输按钮）
  .custom-icon-large {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    line-height: 1;
    font-style: normal;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    filter: drop-shadow(0 2px 6px rgba(99, 102, 241, 0.6));
  }
  
  // 迷你图标（标签内）
  .custom-icon-mini {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    line-height: 1;
    font-style: normal;
    margin-right: 4px;
  }
  
  // 成功图标（已选）
  .success-icon {
    color: #ffffff !important;
    font-size: 20px;
    font-weight: bold;
    filter: drop-shadow(0 2px 6px rgba(16, 185, 129, 0.9));
  }
  
  // 主色图标（待选）
  .primary-icon {
    color: #ffffff !important;
    font-size: 20px;
    filter: drop-shadow(0 2px 6px rgba(99, 102, 241, 0.9));
  }
  
  // 分组图标
  .group-icon {
    font-size: 22px !important;
    filter: drop-shadow(0 2px 6px rgba(245, 158, 11, 0.8)) !important;
    animation: pulse-glow 3s ease-in-out infinite !important;
  }
  
  @keyframes pulse-glow {
    0%, 100% {
      filter: drop-shadow(0 2px 6px rgba(245, 158, 11, 0.6));
    }
    50% {
      filter: drop-shadow(0 2px 10px rgba(245, 158, 11, 1));
    }
  }
  
  // 拖拽手柄图标
  .drag-handle {
    color: #9ca3af !important;
    font-size: 16px;
    cursor: move;
    transition: color 0.3s ease;
    
    &:hover {
      color: #a5b4fc !important;
    }
  }
  
  // 折叠展开图标
  .toggle-icon {
    color: #a5b4fc !important;
    font-size: 14px !important;
    font-weight: bold !important;
    filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.7)) !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    margin-right: 6px !important;
  }
  
  // 🎨 主卡片（在 TaskFlowEditor 中显示的那个）
  .handle-card.el-card {
    background: linear-gradient(135deg, rgba(30, 35, 50, 0.95) 0%, rgba(40, 45, 65, 0.95) 100%) !important;
    border: 2px solid rgba(99, 102, 241, 0.4) !important;
    border-radius: 14px !important;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.35),
                inset 0 1px 0 rgba(255, 255, 255, 0.05) !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    
    &:hover {
      box-shadow: 0 8px 36px rgba(99, 102, 241, 0.4),
                  0 0 0 1px rgba(99, 102, 241, 0.3),
                  inset 0 1px 0 rgba(255, 255, 255, 0.1) !important;
      border-color: rgba(99, 102, 241, 0.7) !important;
      transform: translateY(-4px);
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%) !important;
    }
  }
  
  // 标签样式
  .dark-tag.el-tag {
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.3) 0%, rgba(118, 75, 162, 0.3) 100%) !important;
    border: 1px solid rgba(99, 102, 241, 0.6) !important;
    color: #ffffff !important;
    padding: 8px 14px !important;
    font-size: 13px !important;
    font-weight: 600 !important;
    box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3) !important;
    border-radius: 8px !important;
    
    &:hover {
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.45) 0%, rgba(118, 75, 162, 0.45) 100%) !important;
      border-color: #818cf8 !important;
      box-shadow: 0 4px 16px rgba(99, 102, 241, 0.5) !important;
      transform: translateY(-2px);
    }
  }
}

// ==========================================
// P1优化：设计令牌 (Design Tokens)
// ==========================================

// ==========================================
// 🎨 精简配色方案
// ==========================================

// 主色
$color-primary: #667eea;
$color-success: #10b981;
$color-danger: #ef4444;
$color-warning: #f59e0b;

// 渐变（精简版）
$gradient-primary: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
$gradient-success: linear-gradient(135deg, #10b981 0%, #06b6d4 100%);

// 背景色
$bg-dark-primary: rgba(20, 25, 40, 0.85);
$bg-dark-secondary: rgba(30, 35, 50, 0.95);
$bg-dark-tertiary: rgba(35, 40, 60, 0.9);
$bg-dark-dialog: rgba(20, 20, 36, 0.98);
$bg-dark-card: rgba(25, 30, 45, 0.9);
$bg-dark-input: rgba(15, 18, 30, 0.9);

// ==========================================
// 🔤 字体系统
// ==========================================

// 字体大小
$font-size-xs: 11px;      // 徽章、辅助信息
$font-size-sm: 12px;      // 小按钮、标签
$font-size-base: 13px;    // 正文、复选框
$font-size-md: 14px;      // 标题、重要文字
$font-size-lg: 15px;      // 卡片标题
$font-size-xl: 16px;      // 对话框标题

// 字重
$font-weight-normal: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;

// 文字颜色
$text-primary: #ffffff;     // 主要文字
$text-secondary: #e5e7eb;   // 次要文字
$text-tertiary: #d1d5db;    // 辅助文字
$text-disabled: #9ca3af;    // 禁用文字
$text-placeholder: rgba(255, 255, 255, 0.5); // 占位符

// 边框色
$border-base: rgba(102, 126, 234, 0.25);
$border-light: rgba(102, 126, 234, 0.3);
$border-lighter: rgba(102, 126, 234, 0.4);
$border-dark: rgba(102, 126, 234, 0.2);

// 间距系统 (8px grid)
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 12px;
$spacing-lg: 16px;
$spacing-xl: 20px;
$spacing-xxl: 24px;

// 圆角
$radius-sm: 6px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-xl: 16px;

// 阴影
$shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.2);
$shadow-md: 0 4px 16px rgba(0, 0, 0, 0.3);
$shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.4);
$shadow-xl: 0 16px 64px rgba(0, 0, 0, 0.7);

// 过渡动画
$transition-fast: 0.15s;
$transition-base: 0.3s;
$transition-slow: 0.5s;
$transition-easing: cubic-bezier(0.4, 0, 0.2, 1);

// ==========================================
// ✅ 正确的样式隔离 - 保留Element UI基础，只覆盖颜色
// ==========================================
.handle-info-isolated-container {
  // 只设置必要的属性，不使用 all: unset
  width: 100%;
  
  // 只强制文字颜色为白色，Element UI 的布局样式正常工作
  .el-checkbox__label,
  .el-button span,
  .el-input__inner,
  .el-dialog__title,
  .group-name,
  .checkbox-label,
  .btn-label {
    color: #ffffff !important;
  }
  
  // ==========================================
  // 🎨 Element UI 组件文字颜色增强（保留原生布局）
  // ==========================================
  
  // 只覆盖文字颜色，Element UI 的布局、间距等保持原样
  ::v-deep .el-dialog__title,
  ::v-deep .el-button span,
  ::v-deep .el-checkbox__label,
  ::v-deep .el-input__inner,
  ::v-deep .el-tag,
  ::v-deep .el-badge__content,
  ::v-deep .el-loading-text {
    color: #ffffff !important;
  }
  
  // Empty 空状态文字
  ::v-deep .el-empty__description p {
    color: #d0dce8 !important;
  }
  
  // 空状态样式 - 紧凑版（优化文字可见度）
  .mini-empty {
    ::v-deep .el-empty__image {
      width: 50px !important;
      height: 50px !important;
    }
    
    ::v-deep .el-empty__description {
      margin-top: 8px !important;
      font-size: 13px !important;
      color: #d0dce8 !important; // 更亮的文字颜色
      font-weight: 500 !important;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8) !important;
      
      p {
        color: #d0dce8 !important;
      }
    }
  }
  
  // Card 卡片样式
  // 🎨 主卡片 - 现代渐变设计
  .handle-card.el-card {
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    min-height: 60px;
    background: linear-gradient(135deg, rgba(30, 35, 50, 0.95) 0%, rgba(40, 45, 65, 0.95) 100%) !important;
    border: 2px solid rgba(99, 102, 241, 0.4) !important;
    border-radius: 12px !important;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3), 
                inset 0 1px 0 rgba(255, 255, 255, 0.05) !important;
    
    ::v-deep .el-card__body {
      background: transparent !important;
      min-height: 60px;
      display: flex !important;
      align-items: center !important;
      justify-content: flex-start !important;
      padding: 12px 18px !important;
    }
    
    &:hover {
      box-shadow: 0 8px 32px rgba(99, 102, 241, 0.35),
                  0 0 0 1px rgba(99, 102, 241, 0.3),
                  inset 0 1px 0 rgba(255, 255, 255, 0.1) !important;
      border-color: rgba(99, 102, 241, 0.6) !important;
      transform: translateY(-3px);
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%) !important;
    }
  }
  
  // 标签容器垂直居中
  .tags-container {
    width: 100%;
    display: flex !important;
    align-items: center !important;
    justify-content: flex-start;
    flex-wrap: wrap;
    gap: 6px;
    min-height: 32px;
  }
  
  .draggable-wrapper {
    display: flex !important;
    align-items: center !important;
    flex-wrap: wrap;
    gap: 6px;
    width: 100%;
    min-height: 32px;
    
    // 空状态提示
    &:empty::before {
      content: '点击添加执行器';
      color: #6b7280;
      font-size: 13px;
      font-style: italic;
      width: 100%;
      text-align: center;
      display: block;
      line-height: 32px;
    }
  }
  
  // Tag 深色样式 - 纯色版 + 垂直居中
  ::v-deep .dark-tag {
    background: rgba(102, 126, 234, 0.25);
    border: 1px solid rgba(102, 126, 234, 0.5);
    color: #e8f0ff;
    margin: 0;
    padding: 6px 12px;
    font-size: 13px;
    font-weight: 500;
    box-shadow: 0 2px 6px rgba(102, 126, 234, 0.2);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 28px;
    line-height: 1;
    
    &:hover {
      background: rgba(102, 126, 234, 0.35);
      border-color: #8b9aff;
      color: #ffffff;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
      transform: translateY(-1px);
    }
  }
  
  // 🎨 卡片标题 - 现代化设计
  .card-title {
    color: $text-primary !important;
    font-weight: 700;
    font-size: 17px;
    text-shadow: 0 2px 12px rgba(0, 0, 0, 0.9);
    letter-spacing: 0.5px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 16px 20px;
    background: $gradient-card; // 使用渐变背景
    border-radius: $radius-lg;
    margin-bottom: 18px;
    border: 1px solid rgba(102, 126, 234, 0.4);
    position: relative;
    overflow: hidden;
    
    // 光泽效果
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
      transition: left 0.5s;
    }
    
    &:hover::before {
      left: 100%;
    }
    
    i {
      font-size: 22px;
      color: $color-primary-lighter;
      filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.6));
    }
    
    span {
      color: $text-primary !important;
      font-weight: 700;
      text-shadow: 0 2px 12px rgba(0, 0, 0, 0.9);
      position: relative;
      z-index: 1;
    }
    
    .count-badge {
      color: #ffffff !important;
      font-size: 13px;
      font-weight: 700;
      background: rgba(102, 126, 234, 0.4);
      padding: 3px 10px;
      border-radius: 14px;
      margin-left: 6px;
      border: 1px solid rgba(102, 126, 234, 0.5);
    }
    
    // 新样式的徽章
    .count-badge-new {
      margin-left: 8px;
      
      ::v-deep .el-badge__content {
        background: rgba(102, 126, 234, 0.9);
        border: 1px solid rgba(139, 154, 255, 0.6);
        font-weight: 700;
        box-shadow: 0 2px 6px rgba(102, 126, 234, 0.4);
      }
    }
  }
  
  // 🎨 搜索区域 - 紧凑设计
  .search-section {
    padding: 6px 0 10px 0;
    
    .search-input.el-input {
      ::v-deep .el-input__inner {
        background: linear-gradient(135deg, rgba(15, 18, 30, 0.95) 0%, rgba(20, 25, 40, 0.95) 100%) !important;
        border: 2px solid rgba(99, 102, 241, 0.4) !important;
        color: #ffffff !important;
        border-radius: 10px !important;
        height: 38px !important;
        line-height: 38px !important;
        padding-left: 40px !important;
        font-size: 14px !important;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2),
                    inset 0 1px 2px rgba(0, 0, 0, 0.1) !important;
        
        &:hover {
          border-color: rgba(99, 102, 241, 0.6) !important;
          background: linear-gradient(135deg, rgba(20, 25, 40, 0.95) 0%, rgba(25, 30, 45, 0.95) 100%) !important;
        }
        
        &:focus {
          border-color: #6366f1 !important;
          box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.25),
                      0 4px 12px rgba(99, 102, 241, 0.3),
                      inset 0 1px 2px rgba(0, 0, 0, 0.1) !important;
          background: linear-gradient(135deg, rgba(25, 30, 45, 0.98) 0%, rgba(30, 35, 50, 0.98) 100%) !important;
        }
        
        &::placeholder {
          color: rgba(255, 255, 255, 0.5) !important;
          font-weight: 400;
        }
      }
      
      ::v-deep .el-input__prefix {
        color: #a5b4fc !important;
        
        i {
          color: #a5b4fc !important;
          font-size: 16px !important;
          filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.5));
        }
      }
      
      ::v-deep .el-input__clear {
        color: #a5b4fc !important;
        
        &:hover {
          color: #ffffff !important;
        }
      }
    }
  }
  
  // 🎨 工具栏 - 紧凑设计
  .toolbar-section {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 6px;
    padding: 0 0 8px 0;
    border-bottom: 1px solid rgba(99, 102, 241, 0.2);
    margin-bottom: 8px;
    
    .view-mode-group {
      border: 2px solid rgba(102, 126, 234, 0.4) !important; // P2优化：增强边框突出视图切换
      border-radius: 6px !important;
      overflow: hidden;
      
      ::v-deep .el-button {
        padding: 7px 12px;
        font-size: 12px;
        background: rgba(30, 35, 50, 0.8);
        border-color: rgba(102, 126, 234, 0.3);
        color: #d0dcff !important; // 更亮的文字
        font-weight: 600 !important;
        transition: all 0.3s ease;
        
        span {
          color: #d0dcff !important;
        }
        
        &:hover {
          background: rgba(102, 126, 234, 0.2);
          border-color: rgba(102, 126, 234, 0.5);
          color: #ffffff !important;
          
          span {
            color: #ffffff !important;
          }
        }
        
        &.el-button--primary {
          background: rgba(102, 126, 234, 0.6) !important;
          border-color: #667eea !important;
          color: #ffffff !important;
          
          span {
            color: #ffffff !important;
          }
          
          &:hover {
            background: rgba(102, 126, 234, 0.8) !important;
          }
        }
      }
    }
    
    .toolbar-btn {
      color: #a8b8ff !important; // 更亮的按钮文字
      font-size: 12px;
      padding: 4px 8px;
      transition: all 0.3s ease;
      font-weight: 500; // 加粗提高可读性
      
      &:hover {
        color: $text-primary !important;
        background: rgba(102, 126, 234, 0.2);
      }
      
      &.danger-btn {
        color: #ff8787 !important;
        
        &:hover {
          color: #ffffff !important;
          background: rgba(245, 108, 108, 0.2);
        }
      }
    }
  }
  
  // 列表容器样式 - 增加可视高度
  .selector-list {
    max-height: 420px; // 从320px增加到420px
    overflow-y: auto;
    padding: 4px;
    
    // 美化滚动条
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: rgba(255, 255, 255, 0.04);
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: rgba(102, 126, 234, 0.5);
      border-radius: 3px;
      
      &:hover {
        background: rgba(102, 126, 234, 0.7);
      }
    }
  }
  
  // 拖拽列表样式
  .draggable-list {
    min-height: 50px;
  }
  
  // P2优化：展开/折叠动画
  .expand-enter-active,
  .expand-leave-active {
    transition: all $transition-base $transition-easing;
    max-height: 1000px;
    overflow: hidden;
  }
  
  .expand-enter,
  .expand-leave-to {
    max-height: 0;
    opacity: 0;
  }
  
  // 🎨 分组视图 - 现代卡片设计
  .grouped-view {
    .group-container {
      margin-bottom: 12px;
      border: 1px solid rgba(99, 102, 241, 0.3);
      border-radius: $radius-lg;
      background: linear-gradient(135deg, rgba(25, 30, 45, 0.8) 0%, rgba(30, 35, 50, 0.8) 100%);
      overflow: hidden;
      transition: all $transition-base $transition-easing;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      
      &:hover {
        border-color: rgba(99, 102, 241, 0.6);
        box-shadow: 0 8px 24px rgba(99, 102, 241, 0.25),
                    0 0 0 1px rgba(99, 102, 241, 0.2);
        transform: translateY(-2px);
      }
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    .group-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 14px 16px;
      background: linear-gradient(135deg, rgba(35, 40, 60, 0.95) 0%, rgba(45, 50, 70, 0.95) 100%);
      cursor: pointer;
      transition: all $transition-base $transition-easing;
      user-select: none;
      position: relative;
      border-bottom: 1px solid rgba(99, 102, 241, 0.2);
      
      // 悬停光效
      &::before {
        content: '';
        position: absolute;
        inset: 0;
        background: linear-gradient(90deg, transparent, rgba(99, 102, 241, 0.1), transparent);
        opacity: 0;
        transition: opacity $transition-base;
      }
      
      &:hover {
        background: linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
        
        &::before {
          opacity: 1;
        }
      }
      
      .group-header-left {
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
        
        
        .group-name {
          color: $text-primary;
          font-weight: 700;
          font-size: 15px;
          text-shadow: 0 2px 6px rgba(0, 0, 0, 0.9);
          letter-spacing: 0.3px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 180px;
          flex-shrink: 0;
        }
        
        .group-count-badge {
          ::v-deep .el-badge__content {
            background: rgba(102, 126, 234, 0.8);
            border: 1px solid rgba(139, 154, 255, 0.6);
            font-size: 11px;
            height: 18px;
            line-height: 18px;
            padding: 0 6px;
            font-variant-numeric: tabular-nums; // P2优化：等宽数字
          }
        }
        
        .group-selected-tag {
          background: rgba(103, 194, 58, 0.25) !important;
          border-color: rgba(103, 194, 58, 0.5) !important;
          color: #81d88a !important;
          font-size: 11px;
          padding: 0 8px;
          height: 20px;
          line-height: 20px;
        }
      }
      
      .group-header-right {
        display: flex;
        align-items: center;
        gap: 4px;
        
        .group-action-btn {
          color: #8b9aff !important;
          font-size: 14px;
          padding: 4px 8px !important;
          transition: all 0.3s ease;
          
          &:hover {
            color: #ffffff !important;
            background: rgba(102, 126, 234, 0.25) !important;
          }
          
          &.add-group-btn {
            color: #81d88a !important;
            
            &:hover {
              color: #ffffff !important;
              background: rgba(103, 194, 58, 0.25) !important;
            }
          }
        }
      }
    }
    
    .group-content {
      padding: 12px 14px !important;
      background: rgba(15, 18, 30, 0.5) !important;
      
      .group-item {
        margin-bottom: 8px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
      
      .grouped-checkbox {
        // 分组内的复选框不需要额外缩进，已经有padding了
        margin-left: 0;
      }
    }
  }
  
  // 复选框内容布局
  .checkbox-content {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
    
    .drag-handle {
      color: #6b7280;
      font-size: 14px;
      cursor: move;
      transition: color 0.3s ease;
      
      &:hover {
        color: #8b9aff;
      }
    }
    
    .checkbox-label {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      max-width: 150px; // P1优化：限制最大宽度，避免超长文本
    }
    
    .index-tag {
      background: rgba(102, 126, 234, 0.3) !important;
      border-color: rgba(102, 126, 234, 0.5) !important;
      color: #a8b8ff !important;
      font-weight: 600;
      min-width: 24px;
      text-align: center;
    }
  }
  
  // 🎨 空状态 - 统一字体
  .empty-state {
    padding: 30px 20px;
    
    ::v-deep .el-empty__image {
      width: 80px;
      height: 80px;
    }
    
    ::v-deep .el-empty__description {
      color: $text-tertiary !important;
      font-size: $font-size-base !important;
      margin-top: 12px;
      font-weight: $font-weight-normal !important;
      
      p {
        color: $text-tertiary !important;
      }
    }
  }
  
  // 🎨 对话框底部提示 - 统一字体
  .dialog-footer-tip {
    padding: 0;
    text-align: center;
    background: rgba(35, 40, 55, 0.8);
    border-top: 1px solid rgba(102, 126, 234, 0.2);
    margin-top: 0;
    
    .tip-content {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      padding: 10px 16px;
      
      .custom-icon {
        font-size: 14px;
        color: #a8b8ff !important;
      }
      
      span:not(.custom-icon) {
        color: $text-secondary !important;
        font-size: $font-size-sm !important;
        font-weight: $font-weight-normal !important;
      }
    }
  }
  
  // 已选执行器标题样式
  // 🎨 已选执行器标题 - 渐变绿色风格（强制应用）
  .card-title.selected-title {
    background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%) !important;
    border: 2px solid rgba(16, 185, 129, 0.5) !important;
    box-shadow: 0 4px 20px rgba(16, 185, 129, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.1) !important;
    position: relative;
    overflow: hidden;
    
    // 玻璃态效果
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.1) 0%, transparent 100%);
      pointer-events: none;
    }
    
    i {
      color: $text-primary !important;
      filter: drop-shadow(0 2px 6px rgba(16, 185, 129, 0.8));
      font-size: 24px !important;
      position: relative;
      z-index: 2;
    }
    
    span {
      position: relative;
      z-index: 2;
    }
    
    .count-badge,
    .count-badge-new {
      position: relative;
      z-index: 2;
      
      ::v-deep .el-badge__content {
        color: #ffffff !important;
        background: $color-success !important;
        border: 2px solid rgba(255, 255, 255, 0.6) !important;
        box-shadow: 0 2px 10px rgba(16, 185, 129, 0.6) !important;
        font-size: 12px !important;
        font-weight: 800 !important;
      }
    }
  }
  
  // 🎨 待选执行器标题 - 渐变紫蓝风格（强制应用）
  .card-title.available-title {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
    border: 2px solid rgba(99, 102, 241, 0.6) !important;
    box-shadow: 0 4px 20px rgba(99, 102, 241, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.1) !important;
    position: relative;
    overflow: hidden;
    
    // 玻璃态效果
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.1) 0%, transparent 100%);
      pointer-events: none;
    }
    
    i {
      color: $text-primary !important;
      filter: drop-shadow(0 2px 6px rgba(99, 102, 241, 0.8));
      font-size: 24px !important;
      position: relative;
      z-index: 2;
    }
    
    span {
      position: relative;
      z-index: 2;
    }
    
    .count-badge,
    .count-badge-new {
      position: relative;
      z-index: 2;
      
      ::v-deep .el-badge__content {
        color: #ffffff !important;
        background: $color-primary !important;
        border: 2px solid rgba(255, 255, 255, 0.6) !important;
        box-shadow: 0 2px 10px rgba(99, 102, 241, 0.6) !important;
        font-size: 12px !important;
        font-weight: 800 !important;
      }
    }
  }
  
  // 🎨 复选框 - 现代卡片式设计（强制应用）
  .dark-checkbox.el-checkbox.is-bordered {
    background: linear-gradient(135deg, rgba(15, 18, 30, 0.95) 0%, rgba(25, 30, 45, 0.95) 100%) !important;
    border: 1px solid rgba(99, 102, 241, 0.3) !important;
    border-radius: $radius-md;
    padding: 12px 16px;
    margin-bottom: 10px;
    transition: all $transition-base $transition-easing;
    position: relative;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    
    // 微妙的光效
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -2px;
      width: 4px;
      height: 100%;
      background: $gradient-primary;
      opacity: 0;
      transition: opacity $transition-base;
    }
    
    &:hover {
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%) !important;
      border-color: rgba(99, 102, 241, 0.6) !important;
      box-shadow: 0 4px 20px rgba(99, 102, 241, 0.3), 0 0 0 1px rgba(99, 102, 241, 0.2);
      transform: translateX(4px);
      
      &::before {
        opacity: 1;
      }
    }
    
    .el-checkbox__label {
      color: #ffffff !important;
      font-size: 14px !important;
      font-weight: 600 !important;
      padding-left: 12px !important;
      transition: all $transition-base ease;
      text-shadow: 0 2px 6px rgba(0, 0, 0, 0.9);
      line-height: 1.6;
      letter-spacing: 0.2px;
    }
    
    .el-checkbox__inner {
      background: rgba(255, 255, 255, 0.08);
      border: 2px solid rgba(255, 255, 255, 0.35);
      border-radius: 5px;
      width: 18px;
      height: 18px;
      transition: all 0.3s ease;
      
      &:hover {
        border-color: #a8b8ff;
        background: rgba(168, 184, 255, 0.2);
        box-shadow: 0 0 8px rgba(139, 154, 255, 0.3);
      }
      
      &::after {
        border-color: #ffffff;
        border-width: 2px;
        left: 5px;
        top: 2px;
      }
    }
    
    &.is-checked {
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.25) 0%, rgba(118, 75, 162, 0.25) 100%) !important;
      border-color: $color-primary !important;
      border-width: 2px !important;
      box-shadow: 0 4px 20px rgba(99, 102, 241, 0.4), 
                  0 0 0 1px rgba(99, 102, 241, 0.3),
                  inset 0 1px 0 rgba(255, 255, 255, 0.1);
      
      &::before {
        opacity: 1;
      }
      
      .el-checkbox__label {
        color: #ffffff !important;
        font-weight: 700 !important;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.9);
      }
      
      .el-checkbox__inner {
        background: $gradient-primary !important;
        border-color: $color-primary-light !important;
        box-shadow: 0 0 16px rgba(99, 102, 241, 0.7),
                    inset 0 1px 2px rgba(255, 255, 255, 0.3) !important;
      }
    }
  }
  
  .dark-mode-item {
    margin-bottom: 6px;
  }
  
  // 通用文字样式优化 - 增强可读性
  ::v-deep {
    // Row 间距优化
    .el-row {
      margin-bottom: 0 !important;
      align-items: stretch;
    }
    
    // Col 内边距
    .el-col {
      padding: 0 10px;
      
      &:first-child, &:last-child {
        padding: 0 5px;
      }
    }
    
    // 通用文字颜色 - 提高亮度
    div, span, p {
      color: #e8f0ff !important;
    }
    
    // 输入框样式
    .el-input__inner {
      background: rgba(255, 255, 255, 0.06);
      border-color: rgba(255, 255, 255, 0.15);
      color: #e8f0ff;
      
      &:hover {
        border-color: rgba(102, 126, 234, 0.4);
      }
      
      &:focus {
        border-color: #667eea;
        background: rgba(255, 255, 255, 0.08);
      }
      
      &::placeholder {
        color: #6b7280;
      }
    }
    
    // 强制复选框标签文字高亮
    .el-checkbox {
      .el-checkbox__label {
        color: #f0f4f8 !important;
        font-size: 14px !important;
        font-weight: 600 !important;
        text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5) !important;
      }
      
      &.is-checked .el-checkbox__label {
        color: #ffffff !important;
        font-weight: 700 !important;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.6) !important;
      }
    }
  }
  
  // 对话框卡片优化 - 纯色深色背景
  .dialog-card {
    background: rgba(20, 25, 40, 0.95) !important;
    backdrop-filter: blur(5px) !important;
    border: 1px solid rgba(102, 126, 234, 0.2) !important;
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.5) !important;
    border-radius: 12px;
    
    ::v-deep .el-card__header {
      background: rgba(15, 18, 30, 0.9) !important;
    }
    
    ::v-deep .el-card__body {
      padding: 20px;
      background: rgba(15, 18, 30, 0.8) !important;
      border-radius: 8px;
    }
  }
  
  // 复选框组优化
  .dark-mode-item {
    animation: fadeInUp 0.3s ease-out;
    
    &:hover {
      .el-checkbox__label {
        color: #ffffff !important;
      }
    }
  }
  
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  // 图标按钮样式 - 纯色版
  ::v-deep .dark-icon-btn {
    color: #8b9aff;
    font-size: 26px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    padding: 8px;
    border-radius: 8px;
    
    &:hover {
      color: #ffffff;
      background: rgba(102, 126, 234, 0.25);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
      transform: scale(1.15);
    }
    
    &:active {
      transform: scale(1.05);
    }
  }
  
  // 🎨 传输按钮 - 固定尺寸确保对齐
  .transfer-btn {
    display: flex !important;
    flex-direction: column !important;
    align-items: center !important;
    justify-content: center !important;
    width: 100px !important;
    height: 85px !important;
    min-width: 100px !important;
    max-width: 100px !important;
    min-height: 85px !important;
    max-height: 85px !important;
    padding: 0 !important;
    margin: 0 !important;
    
    .custom-icon-large {
      margin-bottom: 8px;
    }
  }
  
  // 小型传输按钮（全部添加/移除）
  .transfer-btn-small {
    display: flex !important;
    flex-direction: row !important;
    align-items: center !important;
    justify-content: center !important;
    padding: 8px 10px !important;
    background: rgba(30, 35, 50, 0.8) !important;
    border: 1px solid rgba(102, 126, 234, 0.25) !important;
    border-radius: 8px !important;
    min-width: 60px !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    position: relative;
    
    i {
      font-size: 16px;
      transition: all 0.3s ease;
      
      &.arrow-double {
        position: absolute;
        left: 5px;
        opacity: 0.5;
      }
    }
    
    &.add-btn-small {
      &:not(.is-disabled):hover {
        background: rgba(103, 194, 58, 0.2) !important;
        border-color: #67c23a !important;
        transform: scale(1.05) !important;
        
        i {
          color: #81d88a;
        }
      }
    }
    
    &.remove-btn-small {
      &:not(.is-disabled):hover {
        background: rgba(245, 108, 108, 0.2) !important;
        border-color: #f56c6c !important;
        transform: scale(1.05) !important;
        
        i {
          color: #ff8787;
        }
      }
    }
    
    &.is-disabled {
      opacity: 0.3;
      cursor: not-allowed;
      
      i {
        color: #6b7280 !important;
      }
    }
  }
  
  // 选择器卡片样式 - 增强深色背景
  .selector-card {
    min-height: 450px;
    max-height: 550px;
    display: flex;
    flex-direction: column;
    background: rgba(30, 35, 50, 0.8) !important;
    border: 1px solid rgba(102, 126, 234, 0.3) !important;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4) !important;
    
    ::v-deep .el-card__header {
      background: rgba(20, 25, 40, 0.7) !important;
      border-bottom: 1px solid rgba(102, 126, 234, 0.3) !important;
    }
    
    ::v-deep .el-card__body {
      flex: 1;
      overflow-y: auto;
      padding: 14px;
      background: rgba(20, 25, 40, 0.7) !important;
      border-radius: 6px;
    }
  }
  
  .inner-card {
    overflow: visible;
    background: rgba(30, 35, 50, 0.8) !important;
    
    // 美化滚动条 - 应用到卡片body
    ::v-deep .el-card__body {
      max-height: 380px;
      overflow-y: auto;
      background: rgba(20, 25, 40, 0.7) !important;
      
      &::-webkit-scrollbar {
        width: 8px;
      }
      
      &::-webkit-scrollbar-track {
        background: rgba(255, 255, 255, 0.04);
        border-radius: 4px;
        margin: 4px 0;
      }
      
      &::-webkit-scrollbar-thumb {
        background: rgba(102, 126, 234, 0.5);
        border-radius: 4px;
        transition: all 0.3s ease;
        
        &:hover {
          background: rgba(102, 126, 234, 0.7);
        }
      }
    }
  }
}

// ==========================================
// Element UI 组件样式隔离和覆盖
// ==========================================

// 强制覆盖 Element UI 全局样式
.handle-info-isolated-container {
  // 确保所有 Element UI 组件不受外部影响
  ::v-deep {
    // 重置 Element UI 基础样式
    .el-card,
    .el-dialog,
    .el-button,
    .el-input,
    .el-checkbox,
    .el-tag,
    .el-badge,
    .el-empty,
    .el-tooltip,
    .el-button-group {
      font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif !important;
      box-sizing: border-box !important;
    }
    
    // 确保所有文本颜色不被覆盖
    * {
      font-family: inherit !important;
    }
  }
}

// ==========================================
// P0优化：响应式设计
// ==========================================

// 响应式对话框
::v-deep .responsive-dialog {
  .el-dialog {
    max-width: 920px !important;
    margin: auto !important;
  }
  
  // 平板适配 (768px - 1024px)
  @media (max-width: 1024px) {
    .el-dialog {
      max-width: 95% !important;
    }
  }
  
  // 移动端适配 (< 768px)
  @media (max-width: 768px) {
    .el-dialog {
      width: 100% !important;
      margin: 0 !important;
      max-width: none !important;
    }
    
    .el-dialog__body {
      padding: 10px !important;
    }
    
    // 移动端改为上下布局
    .el-row {
      display: flex;
      flex-direction: column;
    }
    
    .el-col {
      width: 100% !important;
      max-width: 100% !important;
    }
    
    // 移动端按钮保持垂直排列
    .action-buttons-wrapper {
      min-height: 300px !important;
      gap: 16px !important;
    }
    
    // 列表高度调整
    .selector-list {
      max-height: 40vh !important;
    }
  }
}

// Dialog 深色样式 - 纯色版（全局样式，因为对话框 append-to-body）
// ⚡ 强力隔离对话框
::v-deep .dark-dialog {
  // 最强样式隔离：重置对话框内所有样式并强制白色文字
  *,
  *::before,
  *::after {
    box-sizing: border-box !important;
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif !important;
    color: #ffffff !important; // 强制所有元素白色文字
  }
  
  // 强制所有文本元素白色
  div, span, p, label, button, a, i, li, td, th, h1, h2, h3, h4, h5, h6,
  .el-dialog__title,
  .el-button span,
  .el-checkbox__label,
  .el-input__inner {
    color: #ffffff !important;
  }
  
  .el-dialog {
    background: rgba(20, 20, 36, 0.98) !important;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(102, 126, 234, 0.3);
    box-shadow: 0 16px 64px rgba(0, 0, 0, 0.7);
    border-radius: 16px;
    
    .el-dialog__header {
      background: rgba(35, 40, 55, 0.95) !important;
      border-bottom: 2px solid rgba(102, 126, 234, 0.3);
      padding: 22px 28px;
      
      .el-dialog__title {
        color: #ffffff !important;
        font-weight: 700;
        font-size: 18px;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
        letter-spacing: 1px;
      }
      
      .el-dialog__close {
        color: #d0dce8;
        font-size: 22px;
        transition: all 0.3s ease;
        
        &:hover {
          color: #ffffff;
          background: rgba(102, 126, 234, 0.2);
          transform: rotate(90deg);
          box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
        }
      }
    }
    
    .el-dialog__body {
      background: rgba(20, 25, 40, 0.7) !important;
      color: #e8f0ff;
      padding: 25px 28px;
    }
  }
  
  // Tooltip 深色样式 - 纯色版
  .el-tooltip__popper {
    background: rgba(26, 26, 46, 0.98);
    backdrop-filter: blur(5px);
    border: 1px solid rgba(139, 154, 255, 0.4);
    color: #e8f0ff;
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.6);
    padding: 10px 14px;
    font-size: 13px;
    font-weight: 500;
    border-radius: 8px;
    
    div {
      color: #e8f0ff !important;
      line-height: 1.6;
    }
    
    .popper__arrow {
      border-top-color: rgba(139, 154, 255, 0.4);
      
      &::after {
        border-top-color: rgba(26, 26, 46, 0.98);
      }
    }
  }
  
  
  // 空状态提示
  .empty-hint {
    text-align: center;
    padding: 40px 20px;
    color: #6b7280;
    font-size: 14px;
    font-style: italic;
    
    i {
      font-size: 48px;
      color: #4b5563;
      margin-bottom: 12px;
      display: block;
    }
  }
  
  // 进一步增强所有文字可读性
  ::v-deep {
    // 确保所有卡片内的文字都清晰
    .el-card__body {
      * {
        color: #e8f0ff;
      }
    }
    
    // 复选框边框文字特殊处理
    .el-checkbox.is-bordered {
      .el-checkbox__label {
        color: #f0f4f8 !important;
      }
      
      &.is-checked .el-checkbox__label {
        color: #ffffff !important;
      }
      
      &:hover .el-checkbox__label {
        color: #ffffff !important;
      }
    }
  }
}

// 全局 Tooltip 文字增强
::v-deep .el-tooltip__popper {
  &.is-dark {
    background: rgba(20, 20, 36, 0.95);
    border: 1px solid rgba(139, 154, 255, 0.4);
    
    * {
      color: #ffffff !important;
    }
  }
}

// 强制对话框遮罩层深色（全局样式）
::v-deep .v-modal {
  background: rgba(0, 0, 0, 0.7) !important;
  backdrop-filter: blur(4px);
}

// ==========================================
// 最终防御层 - 确保样式不被覆盖
// ==========================================

// 对话框相关的全局样式强制覆盖
body {
  // 确保对话框相关样式不被全局样式影响
  .dark-dialog {
    .el-dialog__wrapper {
      * {
        box-sizing: border-box !important;
      }
    }
  }
  
  // Popper 相关样式（Tooltip、Dropdown 等）
  .el-popper {
    &.is-dark {
      background: rgba(20, 20, 36, 0.98) !important;
      border: 1px solid rgba(102, 126, 234, 0.3) !important;
      color: #e8f0ff !important;
      
      .popper__arrow {
        border-top-color: rgba(102, 126, 234, 0.3) !important;
        
        &::after {
          border-top-color: rgba(20, 20, 36, 0.98) !important;
        }
      }
    }
  }
  
  // Tooltip 样式隔离（强制白色）
  .el-tooltip__popper {
    &.is-dark {
      *,
      *::before,
      *::after {
        box-sizing: border-box !important;
        color: #ffffff !important;
        font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif !important;
      }
      
      div, span, p {
        color: #ffffff !important;
      }
    }
  }
  
  // Select、Dropdown 等下拉组件强制白色
  .el-select-dropdown,
  .el-dropdown-menu {
    * {
      color: #ffffff !important;
    }
  }
}

// ==========================================
// 全局强制样式 - 确保对话框相关组件不受外部影响
// ==========================================

// 强制所有挂载到 body 的弹出组件使用白色文字
body {
  .dark-dialog,
  .el-popper[x-placement^="top"],
  .el-popper[x-placement^="bottom"],
  .el-popper[x-placement^="left"],
  .el-popper[x-placement^="right"] {
    *,
    *::before,
    *::after {
      color: #ffffff !important;
      font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif !important;
    }
  }
}

// 强制所有嵌套卡片深色背景 - 最高优先级
::v-deep .dark-dialog {
  .el-card {
    background: rgba(30, 35, 50, 0.9) !important;
    
    &.inner-card,
    &.selector-card {
      background: rgba(25, 30, 45, 0.85) !important;
    }
    
    .el-card__header {
      background: rgba(15, 18, 30, 0.8) !important;
    }
    
    .el-card__body {
      background: rgba(15, 18, 30, 0.75) !important;
    }
  }
  
  // 确保复选框文字始终可见
  .el-checkbox__label {
    color: #f0f4f8 !important;
  }
  
  .el-checkbox.is-checked .el-checkbox__label {
    color: #ffffff !important;
  }
  
  // 确保复选框背景深色
  .el-checkbox.is-bordered {
    background: rgba(15, 18, 30, 0.8) !important;
    
    &.is-checked {
      background: rgba(102, 126, 234, 0.35) !important;
    }
  }
}

// 全局强制 - 确保对话框内所有卡片都是深色
.dark-dialog {
  ::v-deep .el-dialog__body {
    * {
      &.el-card {
        background: rgba(25, 30, 45, 0.85) !important;
      }
    }
  }
}

// ==========================================
// 🔥 终极防御层 - 最高优先级强制白色文字
// ==========================================

// 针对 HandleInfo 组件的超级强制规则
.handle-info-isolated-container,
.dark-dialog {
  // 最高优先级：强制所有可能的文本元素使用白色
  div[class*="handle"],
  div[class*="dark"],
  div[class*="group"],
  div[class*="checkbox"],
  div[class*="card"],
  div[class*="tag"],
  div[class*="selector"],
  div[class*="toolbar"],
  span[class*="label"],
  span[class*="name"],
  span[class*="text"],
  label,
  .el-checkbox__label,
  .el-button span,
  .el-tag span,
  .group-name,
  .checkbox-label,
  .btn-label {
    color: #ffffff !important;
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8) !important;
  }
  
  // 确保输入框内文字可见
  input,
  .el-input__inner,
  .el-textarea__inner {
    color: #ffffff !important;
    
    &::placeholder {
      color: rgba(255, 255, 255, 0.5) !important;
    }
  }
  
  // 确保按钮文字可见
  button,
  .el-button {
    color: #ffffff !important;
    
    span {
      color: #ffffff !important;
    }
  }
  
  // 确保所有标题可见
  .card-title,
  .section-title,
  .group-header {
    * {
      color: #ffffff !important;
    }
  }
}

// 🔥 针对暗色复选框特殊处理
::v-deep .dark-checkbox {
  .el-checkbox__label {
    color: #ffffff !important;
    font-weight: 600 !important;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.9) !important;
  }
  
  &.is-checked {
    .el-checkbox__label {
      color: #ffffff !important;
    }
  }
}

// 🔥 确保所有徽章文字可见
::v-deep .el-badge__content {
  color: #ffffff !important;
  font-weight: 700 !important;
}

// 🔥 确保所有标签文字可见
::v-deep .el-tag {
  color: #ffffff !important;
  
  span {
    color: #ffffff !important;
  }
}

// 🔥🔥🔥 超级强制规则 - 最终防线
// 针对所有可能被外部样式影响的选择器
.handle-info-isolated-container *,
.dark-dialog * {
  // 超高优先级强制规则
  &:not(i):not(.el-icon-search):not([class*="el-icon"]) {
    color: #ffffff !important;
  }
}

// 特别处理：确保对话框标题可见
::v-deep .dark-dialog {
  .el-dialog__title,
  .el-dialog__header * {
    color: #ffffff !important;
    font-weight: 700 !important;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.8) !important;
  }
}

// 特别处理：确保卡片标题可见
.handle-info-isolated-container,
.dark-dialog {
  [class*="title"] {
    color: #ffffff !important;
    
    * {
      color: #ffffff !important;
    }
  }
  
  // 确保所有描述文字可见
  [class*="description"],
  [class*="label"],
  [class*="text"] {
    color: #f0f4f8 !important;
  }
}
</style>




