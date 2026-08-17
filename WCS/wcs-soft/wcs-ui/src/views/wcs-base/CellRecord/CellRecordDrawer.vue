<template>
  <el-drawer
    :title="drawerTitle"
    :visible.sync="visible"
    direction="rtl"
    size="60%"
    :before-close="handleClose"
    :append-to-body="true"
  >
    <div class="drawer-content">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="80px" class="search-form">
        <el-form-item label="日志内容" prop="content">
          <el-input
            v-model="queryParams.content"
            placeholder="请输入日志内容"
            clearable
            style="width: 200px;"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计信息 -->
      <div class="info-bar">
        <el-tag type="info">库位编码: {{ cellCode }}</el-tag>
        <el-tag type="success" style="margin-left: 10px;">仓库编码: {{ wareCode }}</el-tag>
        <el-tag type="warning" style="margin-left: 10px;">共 {{ total }} 条记录</el-tag>
      </div>

      <!-- 时间线展示日志 -->
      <div class="timeline-container">
        <el-timeline v-loading="loading">
          <el-timeline-item
            v-for="item in recordList"
            :key="item.id"
            :timestamp="item.createTime"
            placement="top"
            :color="getTimelineColor(item)"
          >
            <el-card shadow="hover" class="record-card">
              <div class="record-header">
                <span class="record-id">#{{ item.id }}</span>
                <el-tag size="mini" type="info">{{ item.cellCode }}</el-tag>
              </div>
              <div class="record-content">
                {{ item.content }}
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>

        <!-- 空状态 -->
        <el-empty v-if="!loading && recordList.length === 0" description="暂无日志记录"></el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </div>
  </el-drawer>
</template>

<script>
import { listCellRecord } from "@/api/wcs-base/CellRecord";

export default {
  name: "CellRecordDrawer",
  props: {
    // 是否显示抽屉
    show: {
      type: Boolean,
      default: false
    },
    // 库位编码
    cellCode: {
      type: String,
      default: ""
    },
    // 仓库编码
    wareCode: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 库位日志记录列表
      recordList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        cellCode: null,
        wareCode: null,
        content: null
      }
    };
  },
  computed: {
    visible: {
      get() {
        return this.show;
      },
      set(val) {
        this.$emit("update:show", val);
      }
    },
    drawerTitle() {
      const cellCode = this.cellCode || '';
      return `库位日志记录 - ${cellCode}`;
    }
  },
  watch: {
    show(val) {
      if (val) {
        this.initQuery();
        this.getList();
      }
    }
  },
  methods: {
    // 初始化查询参数
    initQuery() {
      this.queryParams.cellCode = this.cellCode;
      this.queryParams.wareCode = this.wareCode;
      this.queryParams.pageNum = 1;
    },
    // 查询库位日志记录列表
    getList() {
      if (!this.cellCode || !this.wareCode) {
        this.$modal.msgWarning("库位编码和仓库编码不能为空");
        return;
      }
      this.loading = true;
      listCellRecord(this.queryParams).then(response => {
        if (response.code == 200) {
          this.recordList = response.rows;
          this.total = response.total;
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 重置按钮操作
    resetQuery() {
      this.queryParams.content = null;
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 关闭抽屉
    handleClose() {
      this.$emit("update:show", false);
      this.resetQuery();
    },
    // 获取时间线颜色
    getTimelineColor(item) {
      // 可以根据日志内容关键字返回不同颜色
      const content = item.content || "";
      if (content.includes("错误") || content.includes("失败")) {
        return "#F56C6C";
      } else if (content.includes("警告")) {
        return "#E6A23C";
      } else if (content.includes("成功")) {
        return "#67C23A";
      }
      return "#409EFF";
    }
  }
};
</script>

<style scoped>
.drawer-content {
  padding: 0 20px 20px 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.search-form {
  padding: 10px 0;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 15px;
  padding: 15px;
}

.info-bar {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.timeline-container {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
  margin-bottom: 15px;
}

.record-card {
  margin-bottom: 10px;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.record-id {
  font-weight: bold;
  color: #409eff;
  font-size: 14px;
}

.record-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-all;
}

.pagination-container {
  padding: 10px 0;
  border-top: 1px solid #ebeef5;
  background-color: #fff;
}

/* 滚动条美化 */
.timeline-container::-webkit-scrollbar {
  width: 6px;
}

.timeline-container::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.timeline-container::-webkit-scrollbar-thumb:hover {
  background-color: #c0c4cc;
}

.timeline-container::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}

/* 时间线样式优化 */
::v-deep .el-timeline-item__timestamp {
  color: #909399;
  font-size: 13px;
}

::v-deep .el-timeline-item__wrapper {
  padding-left: 20px;
}

::v-deep .el-card__body {
  padding: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .drawer-content {
    padding: 0 10px 10px 10px;
  }
  
  .search-form {
    padding: 10px;
  }
}
</style>

