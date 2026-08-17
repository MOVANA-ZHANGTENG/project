<template>
  <div class="alert-panel">
    <div class="panel-header">
      <div class="header-title">
        <i class="el-icon-bell"></i>
        <span>系统告警</span>
      </div>
      <div class="header-count" v-if="alerts.length > 0">
        <span class="alert-count">{{ alerts.length }}</span>
      </div>
    </div>

    <div class="alert-list">
      <div 
        v-for="(alert, index) in alerts" 
        :key="index" 
        class="alert-item"
        :class="alert.level"
      >
        <div class="alert-icon">
          <i :class="getAlertIcon(alert.level)"></i>
        </div>
        <div class="alert-content">
          <div class="alert-message">{{ alert.message }}</div>
          <div class="alert-time">{{ alert.time }}</div>
        </div>
      </div>

      <div v-if="alerts.length === 0" class="empty-state">
        <i class="el-icon-success"></i>
        <span>系统运行正常</span>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request"

export default {
  name: "AlertPanel",
  data() {
    return {
      alerts: [],
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
  methods: {
    async loadData() {
      const wareCode = localStorage.getItem('wareCode')
      if (!wareCode) return

      try {
        // 获取设备和任务数据，生成告警
        const deviceResponse = await request({
          url: "/wcs-base/DeviceInfo/list",
          method: "get",
          params: { wareCode, pageNum: 1, pageSize: 100 }
        })

        const taskResponse = await request({
          url: "/wcs-task/TaskInfo/list",
          method: "get",
          params: { wareCode, state: '3', pageNum: 1, pageSize: 10 }
        })

        this.alerts = []

        // 检测离线设备
        if (deviceResponse.code === 200) {
          const offlineDevices = (deviceResponse.rows || []).filter(d => d.isOnline === 0)
          if (offlineDevices.length > 0) {
            this.alerts.push({
              level: 'warning',
              message: `有 ${offlineDevices.length} 台设备离线`,
              time: this.formatTime(new Date())
            })
          }

          // 检测故障设备
          const errorDevices = (deviceResponse.rows || []).filter(d => d.state === '2')
          if (errorDevices.length > 0) {
            errorDevices.slice(0, 2).forEach(device => {
              this.alerts.push({
                level: 'error',
                message: `设备 ${device.code} 发生故障`,
                time: this.formatTime(new Date())
              })
            })
          }
        }

        // 检测失败任务
        if (taskResponse.code === 200) {
          const failedTasks = taskResponse.rows || []
          if (failedTasks.length > 0) {
            this.alerts.push({
              level: 'error',
              message: `有 ${failedTasks.length} 个任务执行失败`,
              time: this.formatTime(new Date())
            })
          }
        }

        // 限制最多显示5条告警
        this.alerts = this.alerts.slice(0, 5)

      } catch (error) {
        console.error('加载告警数据失败:', error)
      }
    },

    getAlertIcon(level) {
      const iconMap = {
        'info': 'el-icon-info',
        'warning': 'el-icon-warning',
        'error': 'el-icon-circle-close'
      }
      return iconMap[level] || 'el-icon-info'
    },

    formatTime(date) {
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${hours}:${minutes}:${seconds}`
    }
  }
}
</script>

<style lang="scss" scoped>
.alert-panel {
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
      font-size: 2vh;
      font-weight: 600;
      color: #4facfe;
      text-shadow: 0 0 4px rgba(79, 172, 254, 0.3);

      i {
        font-size: 2.2vh;
      }
    }

    .header-count {
      .alert-count {
        display: inline-block;
        min-width: 2vh;
        padding: 0.2vh 0.5vw;
        background: rgba(245, 108, 108, 0.15);
        border: 1px solid rgba(245, 108, 108, 0.3);
        border-radius: 8px;
        font-size: 1.6vh;
        font-weight: 600;
        color: #f56c6c;
        text-align: center;
        text-shadow: 0 0 4px rgba(245, 108, 108, 0.4);
      }
    }
  }

  .alert-list {
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

    .alert-item {
      display: flex;
      align-items: flex-start;
      gap: 0.8vw;
      padding: 0.8vh 1vw;
      margin: 0.4vh 0;
      background: linear-gradient(
        135deg,
        rgba(0, 30, 60, 0.2) 0%,
        rgba(0, 20, 40, 0.1) 100%
      );
      border: 1px solid rgba(0, 150, 255, 0.08);
      border-left: 2px solid;
      border-radius: 4px;
      transition: all 0.3s ease;
      animation: slideIn 0.3s ease;
      width: 100%;

      &:hover {
        background: linear-gradient(
          135deg,
          rgba(0, 40, 80, 0.3) 0%,
          rgba(0, 30, 60, 0.2) 100%
        );
        transform: translateX(2px);
      }

      .alert-icon {
        width: 2vw;
        height: 2vw;
        min-width: 20px;
        min-height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        flex-shrink: 0;

        i {
          font-size: 1.8vh;
        }
      }

      .alert-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 0.2vh;
        min-width: 0;

        .alert-message {
          font-size: 1.6vh;
          font-weight: 500;
          color: #ffffff;
          line-height: 1.4;
          text-shadow: 0 0 2px rgba(255, 255, 255, 0.15);
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .alert-time {
          font-size: 1.4vh;
          color: #7ea8c8;
          font-family: 'Courier New', monospace;
        }
      }

      // 不同级别告警样式
      &.info {
        border-left-color: #4facfe;

        .alert-icon {
          background: rgba(79, 172, 254, 0.15);
          
          i {
            color: #4facfe;
            text-shadow: 0 0 5px rgba(79, 172, 254, 0.5);
          }
        }
      }

      &.warning {
        border-left-color: #e6a23c;

        .alert-icon {
          background: rgba(230, 162, 60, 0.15);
          animation: pulse 2s infinite;
          
          i {
            color: #e6a23c;
            text-shadow: 0 0 5px rgba(230, 162, 60, 0.5);
          }
        }
      }

      &.error {
        border-left-color: #f56c6c;

        .alert-icon {
          background: rgba(245, 108, 108, 0.15);
          animation: pulse 1.5s infinite;
          
          i {
            color: #f56c6c;
            text-shadow: 0 0 5px rgba(245, 108, 108, 0.5);
          }
        }

        .alert-message {
          color: #f56c6c;
        }
      }
    }

    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 3vh 0;
      color: #43e97b;

      i {
        font-size: 3vh;
        margin-bottom: 1vh;
        opacity: 0.7;
      }

      span {
        font-size: 1.8vh;
        opacity: 0.8;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.05);
  }
}

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

