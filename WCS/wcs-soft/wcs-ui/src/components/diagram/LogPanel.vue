<template>
  <div class="diagram-logSlot">
    <div class="diagram-logHeader">
      <div class="diagram-logTitle">站台日志</div>
      <div class="diagram-logActions">
        <el-button size="mini" icon="el-icon-refresh" @click="handleRefresh" :disabled="!positionCode"
          class="diagram-refreshBtn">刷新</el-button>
      </div>
    </div>

    <div v-if="!positionCode" class="diagram-logEmpty">请选择站台后查看日志</div>

    <div v-else>
      <div class="diagram-logStream" v-loading="loading">
        <div v-for="row in records" :key="row.id || (row.createTime + '-' + row.content)" class="diagram-logItem"
          :class="{ 'is-error': row.type === 1 }">
          <div class="diagram-logDot" />
          <div class="diagram-logCard">
            <div class="diagram-logMeta">
              <span class="diagram-logTime">{{ row.createTime }}</span>
              <span class="diagram-logLevel" :class="{ 'is-error': row.type === 1 }">{{ getTypeLabel(row.type) }}</span>
            </div>
            <div class="diagram-logContent">
              <span>{{ truncateContent(row.content, 200) }}</span>
              <span v-if="isContentOverflow(row.content, 200)" class="diagram-logMore"
                @click="openDetail(row)">查看全文</span>
            </div>
          </div>
        </div>
      </div>

      <div class="diagram-logPager" v-if="total > 0">
        <el-pagination background layout="total, sizes, prev, pager, next" :total="total" :current-page="query.pageNum"
          :page-size="query.pageSize" :page-sizes="[5, 10, 20, 50]" @current-change="handleCurrentChange"
          @size-change="handleSizeChange" />
      </div>

      <div v-if="total === 0 && !loading" class="diagram-logEmpty">暂无日志</div>
    </div>

    <el-dialog title="日志详情" :visible.sync="dialogVisible" width="640px" append-to-body class="diagram-logDialog">
      <div class="diagram-logDialogContent">
        <div class="diagram-logDialogMeta">
          <div class="diagram-logDialogLine">
            <span class="diagram-logDialogLabel">时间</span>
            <span class="diagram-logDialogValue diagram-mono">{{ dialogRow.createTime || "-" }}</span>
          </div>
          <div class="diagram-logDialogLine">
            <span class="diagram-logDialogLabel">级别</span>
            <span class="diagram-logDialogValue">
              <el-tag size="mini" :type="dialogRow.type === 1 ? 'danger' : 'info'" effect="plain">
                {{ getTypeLabel(dialogRow.type) }}
              </el-tag>
            </span>
          </div>
        </div>
        <div class="diagram-logDialogText">{{ dialogRow.content || "-" }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "LogPanel",
  props: {
    positionCode: {
      type: String,
      default: null,
    },
    wareCode: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      loading: false,
      total: 0,
      records: [],
      dialogVisible: false,
      dialogRow: {},
      query: {
        pageNum: 1,
        pageSize: 10,
        wareCode: null,
        positionCode: null,
      },
    };
  },
  watch: {
    positionCode(newCode, oldCode) {
      if (newCode && newCode !== oldCode) {
        this.query.pageNum = 1;
        this.fetchRecords();
      }
      if (!newCode) {
        this.total = 0;
        this.records = [];
      }
    },
  },
  methods: {
    truncateContent(content, maxLen) {
      const s = (content ?? "").toString();
      if (!maxLen || maxLen <= 0) return s;
      return s.length > maxLen ? `${s.slice(0, maxLen)}...` : s;
    },
    isContentOverflow(content, maxLen) {
      const s = (content ?? "").toString();
      if (!maxLen || maxLen <= 0) return false;
      return s.length > maxLen;
    },
    getTypeLabel(type) {
      if (type === 0) return "INFO";
      if (type === 1) return "ERROR";
      return "-";
    },
    fetchRecords() {
      const { listPositionRecord } = require("@/api/wcs-base/PositionRecord");
      const positionCode = this.positionCode;

      if (!positionCode) {
        this.total = 0;
        this.records = [];
        return;
      }

      this.loading = true;
      this.query.positionCode = positionCode;
      this.query.wareCode = this.wareCode;

      listPositionRecord(this.query)
        .then((res) => {
          if (res && res.code == 200) {
            this.records = res.rows || [];
            this.total = res.total || 0;
          } else {
            this.records = [];
            this.total = 0;
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleCurrentChange(page) {
      this.query.pageNum = page;
      this.fetchRecords();
    },
    handleSizeChange(size) {
      this.query.pageSize = size;
      this.query.pageNum = 1;
      this.fetchRecords();
    },
    handleRefresh() {
      this.query.pageNum = 1;
      this.fetchRecords();
    },
    openDetail(row) {
      this.dialogRow = row || {};
      this.dialogVisible = true;
    },
  },
};
</script>

<style lang="scss" scoped>
.diagram-logSlot {
  flex: 1;
  min-height: 180px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.55);
  padding: 10px;
  overflow: auto;
}

.diagram-logHeader {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.diagram-logActions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.diagram-refreshBtn {
  border-radius: 8px;
  transition: all 0.2s ease;
}

.diagram-refreshBtn:hover:not(:disabled) {
  transform: rotate(90deg);
}

.diagram-logTitle {
  font-weight: 700;
  color: rgba(15, 23, 42, 0.78);
}

.diagram-logEmpty {
  color: rgba(15, 23, 42, 0.5);
  font-size: 13px;
}

.diagram-logStream {
  position: relative;
  border-radius: 12px;
  padding: 8px 6px 2px 6px;
  background: rgba(255, 255, 255, 0.35);
  border: 1px solid rgba(15, 23, 42, 0.1);
}

.diagram-logItem {
  position: relative;
  display: grid;
  grid-template-columns: 18px 1fr;
  column-gap: 10px;
  padding: 8px 6px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.diagram-logItem::before {
  content: "";
  position: absolute;
  left: 14px;
  top: -6px;
  bottom: -6px;
  width: 2px;
  background: rgba(15, 23, 42, 0.08);
}

.diagram-logItem:first-child::before {
  top: 18px;
}

.diagram-logItem:last-child::before {
  bottom: 18px;
}

.diagram-logDot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
  margin-top: 6px;
  background: rgba(59, 130, 246, 0.95);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.12);
  z-index: 1;
  transition: all 0.2s ease;
}

.diagram-logItem:hover .diagram-logDot {
  transform: scale(1.3);
}

.diagram-logItem.is-error .diagram-logDot {
  background: rgba(239, 68, 68, 0.95);
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.12);
}

.diagram-logCard {
  border-radius: 12px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(15, 23, 42, 0.1);
  box-shadow: 0 10px 22px rgba(2, 6, 23, 0.06);
  transition: all 0.2s ease;
}

.diagram-logItem:hover .diagram-logCard {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 12px 28px rgba(2, 6, 23, 0.1);
}

.diagram-logItem.is-error .diagram-logCard {
  border-color: rgba(239, 68, 68, 0.22);
  background: linear-gradient(180deg, rgba(254, 242, 242, 0.95), rgba(255, 255, 255, 0.85));
}

.diagram-logMeta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 6px;
}

.diagram-logTime {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono",
    "Courier New", monospace;
  font-size: 12px;
  color: rgba(15, 23, 42, 0.62);
}

.diagram-logLevel {
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.6px;
  padding: 2px 8px;
  border-radius: 999px;
  color: rgba(30, 64, 175, 0.9);
  background: rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.18);
}

.diagram-logLevel.is-error {
  color: rgba(185, 28, 28, 0.9);
  background: rgba(239, 68, 68, 0.12);
  border-color: rgba(239, 68, 68, 0.2);
}

.diagram-logContent {
  color: rgba(15, 23, 42, 0.82);
  line-height: 18px;
  font-size: 13px;
  word-break: break-word;
}

.diagram-logMore {
  margin-left: 8px;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.4px;
  color: rgba(59, 130, 246, 0.95);
  cursor: pointer;
  user-select: none;
  padding: 2px 6px;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.18);
  transition: all 0.2s ease;
}

.diagram-logMore:hover {
  background: rgba(59, 130, 246, 0.14);
}

.diagram-logPager {
  padding-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.diagram-logDialog {
  border-radius: 16px;
}

.diagram-logDialogContent {
  padding: 16px;
}

.diagram-logDialogMeta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 12px;
}

.diagram-logDialogLine {
  border: 1px solid rgba(15, 23, 42, 0.1);
  border-radius: 12px;
  padding: 10px 12px;
  background: rgba(248, 250, 252, 0.8);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.diagram-logDialogLabel {
  font-size: 12px;
  font-weight: 800;
  color: rgba(15, 23, 42, 0.55);
  letter-spacing: 0.4px;
}

.diagram-logDialogValue {
  color: rgba(15, 23, 42, 0.8);
}

.diagram-logDialogText {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 20px;
  font-size: 13px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(15, 23, 42, 0.1);
}

.diagram-mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono",
    "Courier New", monospace;
}
</style>