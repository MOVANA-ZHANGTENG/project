<template>
  <div class="cell-report">
    <div class="report-header">
      <div class="header-title">
        <i class="el-icon-location"></i>
        <span>库位概况</span>
      </div>
      <div class="header-refresh" @click="loadData">
        <i class="el-icon-refresh"></i>
      </div>
    </div>
    
    <div class="report-content">
      <!-- 环形进度图 -->
      <div class="utilization-chart">
        <div class="chart-wrapper">
          <svg viewBox="0 0 120 120" class="progress-ring">
            <defs>
              <linearGradient id="progressGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color:#43e97b;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#4facfe;stop-opacity:1" />
              </linearGradient>
            </defs>
            <circle
              class="progress-ring-bg"
              cx="60"
              cy="60"
              r="50"
            />
            <circle
              class="progress-ring-fill"
              cx="60"
              cy="60"
              r="50"
              :style="progressStyle"
            />
          </svg>
          <div class="chart-center">
            <div class="center-value">{{ utilizationRate }}</div>
            <div class="center-unit">%</div>
          </div>
        </div>
      </div>

      <!-- 库位统计信息 -->
      <div class="cell-stats">
        <div class="stat-item">
          <div class="stat-label">
            <i class="el-icon-finished"></i>
            <span>总库位</span>
          </div>
          <div class="stat-value">{{ cellStats.total }}</div>
        </div>
        <div class="stat-item occupied">
          <div class="stat-label">
            <i class="el-icon-box"></i>
            <span>已占用</span>
          </div>
          <div class="stat-value">{{ cellStats.occupied }}</div>
        </div>
        <div class="stat-item available">
          <div class="stat-label">
            <i class="el-icon-circle-check"></i>
            <span>可用</span>
          </div>
          <div class="stat-value">{{ cellStats.available }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request"

export default {
  name: "CellReport",
  data() {
    return {
      cellStats: {
        total: 0,
        occupied: 0,
        available: 0
      },
      timer: null
    }
  },
  computed: {
    utilizationRate() {
      if (this.cellStats.total === 0) return 0
      return Math.round((this.cellStats.occupied / this.cellStats.total) * 100)
    },
    progressStyle() {
      const circumference = 2 * Math.PI * 50
      const progress = this.utilizationRate / 100
      const offset = circumference * (1 - progress)
      return {
        strokeDasharray: `${circumference} ${circumference}`,
        strokeDashoffset: offset
      }
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
  methods: {
    async loadData() {
      const wareCode = localStorage.getItem('wareCode')
      if (!wareCode) return

      try {
        const response = await request({
          url: "/wcs-base/CellInfo/list",
          method: "get",
          params: { wareCode, pageNum: 1, pageSize: 10000 }
        })
        
        if (response.code === 200) {
          const cells = response.rows || []
          this.cellStats.total = cells.length
          this.cellStats.occupied = cells.filter(c => c.invenState === 1 || c.invenState === '1').length
          this.cellStats.available = this.cellStats.total - this.cellStats.occupied
        }
      } catch (error) {
        console.error('加载库位数据失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.cell-report {
  display: flex;
  flex-direction: column;
  max-height: 16vh;
  overflow: hidden;
  width: 100%;
  
  .report-header {
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
      font-size: 2vh;
      font-weight: 600;
      color: #4facfe;
      text-shadow: 0 0 4px rgba(79, 172, 254, 0.3);

      i {
        font-size: 2.2vh;
      }
    }

    .header-refresh {
      cursor: pointer;
      padding: 0.2vh 0.4vw;
      border-radius: 3px;
      transition: all 0.3s ease;
      
      i {
        font-size: 2vh;
        color: #7ea8c8;
      }

      &:hover {
        background: rgba(79, 172, 254, 0.1);
        
        i {
          color: #4facfe;
          animation: rotate 0.6s ease;
        }
      }
    }
  }

  .report-content {
    display: flex;
    gap: 1.2vw;
    padding: 0 0.5vw;
    align-items: center;
    min-height: 0;
    width: 100%;

    // 环形进度图
    .utilization-chart {
      flex: 0 0 8vh;
      display: flex;
      align-items: center;
      justify-content: center;

      .chart-wrapper {
        position: relative;
        width: 8vh;
        height: 8vh;

        .progress-ring {
          transform: rotate(-90deg);

          .progress-ring-bg {
            fill: none;
            stroke: rgba(0, 30, 60, 0.5);
            stroke-width: 8;
          }

          .progress-ring-fill {
            fill: none;
            stroke: url(#progressGradient);
            stroke-width: 8;
            stroke-linecap: round;
            transition: stroke-dashoffset 0.8s ease;
            filter: drop-shadow(0 0 6px rgba(79, 172, 254, 0.5));
          }
        }

        .chart-center {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          text-align: center;

          .center-value {
            font-size: 3vh;
            font-weight: 700;
            color: #ffffff;
            text-shadow: 
              0 0 8px rgba(79, 172, 254, 0.4),
              0 2px 4px rgba(0, 0, 0, 0.4);
            line-height: 1;
          }

          .center-unit {
            font-size: 1.6vh;
            color: #7ea8c8;
            margin-top: 0.2vh;
          }
        }
      }
    }

    // 库位统计
    .cell-stats {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 0.5vh;
      justify-content: center;
      min-width: 0;

      .stat-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5vh 0.8vw;
        background: rgba(0, 30, 60, 0.2);
        border: 1px solid rgba(0, 150, 255, 0.1);
        border-radius: 4px;
        transition: all 0.3s ease;
        width: 100%;

        &:hover {
          background: rgba(0, 40, 80, 0.3);
          border-color: rgba(0, 180, 255, 0.15);
          transform: translateX(2px);
        }

        .stat-label {
          display: flex;
          align-items: center;
          gap: 0.3vw;
          font-size: 1.6vh;
          color: #7ea8c8;

          i {
            font-size: 1.7vh;
            color: #4facfe;
          }
        }

        .stat-value {
          font-size: 2.1vh;
          font-weight: 600;
          color: #ffffff;
          text-shadow: 0 0 4px rgba(255, 255, 255, 0.25);
        }

        &.occupied {
          .stat-label i {
            color: #e6a23c;
          }
          .stat-value {
            color: #e6a23c;
            text-shadow: 0 0 5px rgba(230, 162, 60, 0.4);
          }
        }

        &.available {
          .stat-label i {
            color: #43e97b;
          }
          .stat-value {
            color: #43e97b;
            text-shadow: 0 0 5px rgba(67, 233, 123, 0.4);
          }
        }
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>

