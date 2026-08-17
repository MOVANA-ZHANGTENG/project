<template>
  <div class="pallet-report">
    <div class="panel-header">
      <div class="header-title">
        <i class="el-icon-box"></i>
        <span>托盘信息</span>
      </div>
    </div>

    <div class="pallet-content">
      <!-- 托盘列表 -->
      <div class="pallet-list">
        <div 
          v-for="(pallet, index) in pallets" 
          :key="index"
          class="pallet-item"
          @click="highlightPallet(pallet.cellCode, pallet.palletCode)"
        >
          <div class="pallet-info">
            <div class="info-row">
              <span class="info-label">托盘号:</span>
              <span class="info-value">{{ pallet.palletCode }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">库位:</span>
              <span class="info-value">{{ pallet.cellCode }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">状态:</span>
              <span class="info-value status-badge" :class="getPalletStatusClass(pallet.status)">
                {{ getPalletStatusText(pallet.status) }}
              </span>
            </div>
          </div>
        </div>

        <div v-if="pallets.length === 0" class="empty-state">
          <i class="el-icon-information"></i>
          <span>暂无托盘信息</span>
        </div>
      </div>

      <!-- 托盘统计信息 -->
      <div class="pallet-stats">
        <div class="stat-item">
          <div class="stat-value">{{ totalPallets }}</div>
          <div class="stat-label">总托盘数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ occupiedPallets }}</div>
          <div class="stat-label">已占用</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ availablePallets }}</div>
          <div class="stat-label">可用</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request"

export default {
  name: "PalletReport",
  data() {
    return {
      pallets: [],
      timer: null
    }
  },
  created() {
    this.loadData()
    this.timer = setInterval(() => {
      this.loadData()
    }, 10000) // 10秒刷新
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  computed: {
    // 计算托盘统计信息
    totalPallets() {
      return this.pallets.length
    },
    occupiedPallets() {
      return this.pallets.filter(p => p.status === 1).length
    },
    availablePallets() {
      return this.pallets.filter(p => p.status === 0).length
    }
  },
  methods: {
    async loadData() {
      const wareCode = localStorage.getItem('wareCode')
      if (!wareCode) return

      try {
        // 获取托盘信息数据
        const response = await request({
          url: "/wcs-base/CellInfo/list",
          method: "get",
          params: { wareCode, pageNum: 1, pageSize: 100 }
        })

        if (response.code === 200) {
          // 处理返回的库位数据，提取有托盘的库位信息
          const cells = response.rows || []
          this.pallets = cells
            .filter(cell => cell.invenState === 1 && cell.palletCode) // 过滤有库存且有托盘号的库位
            .map(cell => ({
              cellCode: cell.cellCode,
              palletCode: cell.palletCode,
              status: 1, // 1: 已占用, 0: 可用
              // 可以根据需要添加更多信息
            }))
        }
      } catch (error) {
        console.error('加载托盘信息失败:', error)
      }
    },

    // 获取托盘状态样式类
    getPalletStatusClass(status) {
      return status === 1 ? 'status-occupied' : 'status-available'
    },

    // 获取托盘状态文本
    getPalletStatusText(status) {
      return status === 1 ? '已占用' : '可用'
    },

    // 高亮显示托盘
    highlightPallet(cellCode, palletCode) {
      // 发送事件给父组件或3D场景，用于高亮显示对应的托盘
      this.$emit('highlight-pallet', { cellCode, palletCode })
      }
  }
}
</script>

<style lang="scss" scoped>
.pallet-report {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.6vh 0.5vw;
    margin-bottom: 0.8vh;
    border-bottom: 1px solid rgba(0, 150, 255, 0.1);

    .header-title {
      display: flex;
      align-items: center;
      gap: 0.4vw;
      font-size: 1.5vh;
      font-weight: 600;
      color: #4facfe;
      text-shadow: 0 0 4px rgba(79, 172, 254, 0.3);

      i {
        font-size: 1.7vh;
      }
    }
  }

  .pallet-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    // 托盘列表
    .pallet-list {
      flex: 1;
      overflow-y: auto;
      padding: 0 0.5vw;
      width: 100%;

      // 自定义滚动条
      &::-webkit-scrollbar {
        width: 3px;
      }

      &::-webkit-scrollbar-track {
        background: rgba(0, 0, 0, 0.05);
        border-radius: 2px;
      }

      &::-webkit-scrollbar-thumb {
        background: rgba(0, 150, 255, 0.25);
        border-radius: 2px;

        &:hover {
          background: rgba(0, 150, 255, 0.4);
        }
      }

      // 托盘项
      .pallet-item {
        display: flex;
        align-items: center;
        gap: 0.8vw;
        padding: 0.8vh 1vw;
        margin: 0.4vh 0;
        background: linear-gradient(
          135deg,
          rgba(0, 30, 60, 0.2) 0%,
          rgba(0, 20, 40, 0.1) 100%
        );
        border: 1px solid rgba(0, 150, 255, 0.08);
        border-left: 2px solid #4facfe;
        border-radius: 4px;
        transition: all 0.3s ease;
        animation: slideIn 0.3s ease;
        width: 100%;
        cursor: pointer;

        &:hover {
          background: linear-gradient(
            135deg,
            rgba(0, 40, 80, 0.3) 0%,
            rgba(0, 30, 60, 0.2) 100%
          );
          border-color: rgba(79, 172, 254, 0.3);
          transform: translateX(2px);
          box-shadow: 0 2px 8px rgba(0, 100, 200, 0.15);
        }

        .pallet-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 0.3vh;
          min-width: 0;

          .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .info-label {
              font-size: 1.1vh;
              color: #7ea8c8;
              text-shadow: 0 0 2px rgba(100, 150, 200, 0.15);
            }

            .info-value {
              font-size: 1.2vh;
              font-weight: 500;
              color: #ffffff;
              text-shadow: 0 0 2px rgba(255, 255, 255, 0.15);
              font-family: 'Courier New', monospace;
            }

            .status-badge {
              padding: 0.2vh 0.5vw;
              border-radius: 8px;
              font-size: 1vh;
              font-weight: 600;
              text-shadow: none;

              &.status-occupied {
                background: rgba(245, 108, 108, 0.15);
                border: 1px solid rgba(245, 108, 108, 0.3);
                color: #f56c6c;
              }

              &.status-available {
                background: rgba(67, 233, 123, 0.15);
                border: 1px solid rgba(67, 233, 123, 0.3);
                color: #43e97b;
              }
            }
          }
        }
      }

      // 空状态
      .empty-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 3vh 0;
        color: #7ea8c8;

        i {
          font-size: 3vh;
          margin-bottom: 1vh;
          opacity: 0.7;
        }

        span {
          font-size: 1.4vh;
          opacity: 0.8;
        }
      }
    }

    // 托盘统计信息
    .pallet-stats {
      display: flex;
      justify-content: space-around;
      align-items: center;
      padding: 1vh 0.5vw;
      margin-top: 0.8vh;
      border-top: 1px solid rgba(0, 150, 255, 0.1);

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.3vh;

        .stat-value {
          font-size: 2vh;
          font-weight: 700;
          color: #4facfe;
          text-shadow: 0 0 8px rgba(79, 172, 254, 0.5);
        }

        .stat-label {
          font-size: 1.1vh;
          color: #7ea8c8;
          text-shadow: 0 0 2px rgba(100, 150, 200, 0.15);
        }
      }
    }
  }
}

// 动画效果
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>