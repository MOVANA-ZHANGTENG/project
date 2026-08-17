<template>
  <div class="warehouse-selector">
    <el-card class="box-card compact-card">
      <div class="warehouse-content">
        
        <div class="warehouse-grid">
          <div 
            v-for="item in WareInfoList" 
            :key="item.code"
            class="warehouse-card"
            :class="{ 
              'is-selected': internalValue === item.code,
              'is-enabled': item.disableState === 0,
              'is-disabled': item.disableState === 1
            }"
            @click="selectWarehouse(item)"
          >
            <div class="warehouse-icon">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="warehouse-info">
              <div class="warehouse-name">{{ item.name }}</div>
              <div class="warehouse-code">{{ item.code }}</div>
            </div>
            <div v-if="internalValue === item.code" class="selected-indicator">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <input @input="handleInput" style="width: 100px;display: none;" v-model="internalValue" />
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "WareInfo",
  props: {
    value: { // 父组件传递的值
      type: [String],
      required: false
    },
    
  },
  computed: {
    // 计算属性作为中间代理
    internalValue: {
      get() {
        return this.value;
      },
      set(newVal) {
        // 通知父组件更新
        this.$emit('input', newVal);
      }
    }
  },
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
      // 仓库表格数据
      WareInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 999,
        code: null,
        name: null,
        modelData: null,
        monitorData: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
        isTest: null,
        disableState: null,
        inCellTactics: null,
        backgroundImg: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "编码不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "名称不能为空", trigger: "blur" }
        ],
        version: [
          { required: true, message: "版本号不能为空", trigger: "blur" }
        ],
        isDelete: [
          { required: true, message: "删除标志 0-正常 1-删除不能为空", trigger: "blur" }
        ],
        isTest: [
          { required: true, message: "测试模式 0-关闭  1-打开不能为空", trigger: "blur" }
        ],
        disableState: [
          { required: true, message: "是否禁用 0-启用  1-禁用不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getWareInfos();
  },
  methods: {
     // 处理输入框变化
     handleInput(e) {
      this.internalValue = e.target.value;
    },
    
    // 处理选择器变化（Element UI 的 change 事件）
    handleChange(value) {
      this.internalValue = value;
    },
    
    // 选择仓库
    selectWarehouse(warehouse) {
      this.internalValue = warehouse.code;
    },
    getWareInfos() {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/list",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          that.WareInfoList = response.rows;
        } else {
          that.$modal.msgError(response.msg);
        }
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
        name: null,
        modelData: null,
        monitorData: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
        isTest: null,
        disableState: null,
        inCellTactics: null,
        backgroundImg: null
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
      this.title = "添加仓库";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getWareInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改仓库";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateWareInfo(this.form).then(response => {
              if(response.code === 200){
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }else{
                this.open = false;
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addWareInfo(this.form).then(response => {
              if(response.code === 200){
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              }else{
                this.open = false;
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
      this.$modal.confirm('是否确认删除仓库编号为"' + ids + '"的数据项？').then(function() {
        return delWareInfo(ids);
      }).then((response) => {
        if(response.code === 200){
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }else{
          this.$modal.msgError(response.msg || "删除失败");
        }
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wms-base/WareInfo/export', {
        ...this.queryParams
      }, `WareInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style lang="scss" scoped>
.warehouse-selector {
  margin-bottom: 20px;
  
  .compact-card {
    background: rgba(255, 255, 255, 0.03);
    backdrop-filter: blur(3px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
    }
  }

  .warehouse-content {
    padding: 2px 10px;
    position: relative;
    z-index: 2;
    
    .warehouse-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;
      
      .label {
        font-size: 14px;
        font-weight: 600;
        color: #ffffff;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      }
    }
    
    .warehouse-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
      gap: 12px;
      
      .warehouse-card {
        background: rgba(255, 255, 255, 0.03);
        backdrop-filter: blur(3px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 10px;
        padding: 12px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;
        min-height: 60px;
        display: flex;
        align-items: center;
        gap: 12px;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          opacity: 0;
          transition: opacity 0.3s ease;
          pointer-events: none;
        }
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
          border-color: rgba(255, 255, 255, 0.15);
          
          &::before {
            opacity: 1;
          }
        }
        
        &.is-selected {
          border-color: #667eea;
          box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2), 0 4px 16px rgba(0, 0, 0, 0.25);
          background: rgba(102, 126, 234, 0.08);
          
          &::before {
            opacity: 1;
          }
        }
        
        &.is-disabled {
          opacity: 0.6;
          cursor: not-allowed;
          
          &:hover {
            transform: none;
            box-shadow: none;
          }
        }
        
        .warehouse-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 36px;
          height: 36px;
          background: rgba(102, 126, 234, 0.2);
          border-radius: 8px;
          flex-shrink: 0;
          
          i {
            font-size: 18px;
            color: #667eea;
            text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
          }
        }
        
        .warehouse-info {
          flex: 1;
          min-width: 0;
          
          .warehouse-name {
            font-size: 14px;
            font-weight: 600;
            color: #ffffff;
            margin-bottom: 2px;
            text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
            line-height: 1.2;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
          
          .warehouse-code {
            font-size: 11px;
            color: #8a9ba8;
            font-family: 'Courier New', monospace;
            margin-bottom: 4px;
            line-height: 1.2;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
        
        .selected-indicator {
          position: absolute;
          top: 6px;
          right: 6px;
          width: 16px;
          height: 16px;
          background: #67c23a;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 8px rgba(103, 194, 58, 0.4);
          flex-shrink: 0;
          
          i {
            font-size: 10px;
            color: #ffffff;
            font-weight: bold;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .warehouse-selector {
    .warehouse-grid {
      grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
      gap: 8px;
      
      .warehouse-card {
        padding: 8px;
        min-height: 50px;
        
        .warehouse-icon {
          width: 30px;
          height: 30px;
          
          i {
            font-size: 14px;
          }
        }
        
        .warehouse-info {
          .warehouse-name {
            font-size: 12px;
          }
          
          .warehouse-code {
            font-size: 10px;
          }
        }
      }
    }
  }
}
</style>
