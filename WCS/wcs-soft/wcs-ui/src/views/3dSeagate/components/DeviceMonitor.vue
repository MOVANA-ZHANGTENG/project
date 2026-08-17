<template>
  <div class="device-monitor">
    <div class="monitor-header">
      <div class="header-title">
        <i class="el-icon-cpu"></i>
        <span>设备监控</span>
      </div>
      <div class="header-stats">
        <span class="stat-online">{{ onlineCount }}/{{ totalCount }}</span>
      </div>
    </div>

    <div class="device-list">
      <div 
        v-for="device in devices" 
        :key="device.id" 
        class="device-item"
        :class="{
          'online': device.isOnline === 1,
          'offline': device.isOnline === 0,
          'running': device.state === '1',
          'error': device.state === '2'
        }"
      >
        <div class="device-main">
          <div class="device-status-indicator">
            <div class="status-dot"></div>
          </div>
          <div class="device-info">
            <div class="device-code">{{ device.code }}</div>
            <div class="device-name">{{ device.name }}</div>
          </div>
        </div>
        <div class="device-state">
          <i :class="getDeviceStateIcon(device.state)" class="state-icon"></i>
          <span class="state-text">{{ getDeviceStateText(device.state) }}</span>
        </div>
      </div>

      <div v-if="devices.length === 0" class="empty-state">
        <i class="el-icon-warning-outline"></i>
        <span>暂无设备数据</span>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request"

export default {
  name: "DeviceMonitor",
  data() {
    return {
      devices: [],
      timer: null
    }
  },
  computed: {
    totalCount() {
      return this.devices.length
    },
    onlineCount() {
      return this.devices.filter(d => d.isOnline === 1).length
    }
  },
  created() {
    this.loadData()
    this.timer = setInterval(() => {
      this.loadData()
    }, 5000) // 5秒刷新
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
          url: "/wcs-base/DeviceInfo/list",
          method: "get",
          params: { 
            wareCode, 
            pageNum: 1, 
            pageSize: 20,
            orderByColumn: 'is_online',
            isAsc: 'desc'
          }
        })
        
        if (response.code === 200) {
          this.devices = response.rows || []
        }
      } catch (error) {
        console.error('加载设备数据失败:', error)
      }
    },

    getDeviceStateIcon(state) {
      const iconMap = {
        '0': 'el-icon-time',
        '1': 'el-icon-video-play',
        '2': 'el-icon-warning'
      }
      return iconMap[state] || 'el-icon-question'
    },

    getDeviceStateText(state) {
      const textMap = {
        '0': '空闲',
        '1': '运行',
        '2': '故障'
      }
      return textMap[state] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.device-monitor {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;

  .monitor-header {
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

    .header-stats {
      .stat-online {
        font-size: 1.7vh;
        font-weight: 600;
        color: #43e97b;
        text-shadow: 0 0 4px rgba(67, 233, 123, 0.3);
      }
    }
  }

  .device-list {
    flex: 1;
    overflow-y: auto;
    padding: 0 0.3vw;
    width: 100%;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 0.6vh 0.5vw;
    align-content: start;
    min-height: 18vh;

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

    .device-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 0.6vh 0.4vw;
      background: linear-gradient(
        135deg,
        rgba(0, 30, 60, 0.2) 0%,
        rgba(0, 20, 40, 0.1) 100%
      );
      border: 1px solid rgba(0, 150, 255, 0.08);
      border-radius: 6px;
      transition: all 0.3s ease;
      cursor: pointer;
      height: 7.5vh;
      max-height: 7.5vh;

      &:hover {
        background: linear-gradient(
          135deg,
          rgba(0, 40, 80, 0.3) 0%,
          rgba(0, 30, 60, 0.2) 100%
        );
        border-color: rgba(0, 180, 255, 0.2);
        transform: translateY(-2px);
        box-shadow: 0 2px 12px rgba(0, 150, 255, 0.12);
      }

      .device-main {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.3vh;
        width: 100%;
        flex: 1;

        .device-status-indicator {
          width: 1vw;
          height: 1vw;
          min-width: 10px;
          min-height: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;

          .status-dot {
            width: 0.6vw;
            height: 0.6vw;
            min-width: 6px;
            min-height: 6px;
            border-radius: 50%;
            background: #909399;
            box-shadow: 0 0 6px rgba(144, 147, 153, 0.5);
            animation: pulse 2s infinite;
          }
        }

        .device-info {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 0.2vh;
          width: 100%;
          text-align: center;

          .device-code {
            font-size: 1.5vh;
            font-weight: 600;
            color: #ffffff;
            text-shadow: 0 0 4px rgba(255, 255, 255, 0.25);
            letter-spacing: 0.2px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            width: 100%;
            line-height: 1.2;
          }

          .device-name {
            font-size: 1.3vh;
            color: #7ea8c8;
            text-shadow: 0 0 2px rgba(100, 150, 200, 0.15);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            width: 100%;
            line-height: 1.2;
          }
        }
      }

      .device-state {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.2vw;
        padding: 0.25vh 0.5vw;
        background: rgba(0, 30, 60, 0.25);
        border-radius: 3px;
        margin-top: 0.2vh;
        width: fit-content;

        .state-icon {
          font-size: 1.3vh;
        }

        .state-text {
          font-size: 1.3vh;
          font-weight: 500;
          white-space: nowrap;
        }
      }

      // 在线状态
      &.online {
        .device-status-indicator .status-dot {
          background: #43e97b;
          box-shadow: 0 0 10px rgba(67, 233, 123, 0.6);
        }
      }

      // 离线状态
      &.offline {
        opacity: 0.7;

        .device-status-indicator .status-dot {
          background: #f56c6c;
          box-shadow: 0 0 8px rgba(245, 108, 108, 0.5);
          animation: none;
        }

        .device-info {
          .device-code,
          .device-name {
            opacity: 0.6;
          }
        }
      }

      // 运行状态
      &.running .device-state {
        background: rgba(67, 233, 123, 0.15);
        
        .state-icon,
        .state-text {
          color: #43e97b;
          text-shadow: 0 0 5px rgba(67, 233, 123, 0.4);
        }
      }

      // 故障状态
      &.error {
        .device-status-indicator .status-dot {
          background: #f56c6c;
          box-shadow: 0 0 10px rgba(245, 108, 108, 0.6);
          animation: pulse 1s infinite;
        }

        .device-state {
          background: rgba(245, 108, 108, 0.2);

          .state-icon,
          .state-text {
            color: #f56c6c;
            text-shadow: 0 0 5px rgba(245, 108, 108, 0.4);
          }
        }
      }
    }

    .empty-state {
      grid-column: 1 / -1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 3vh 0;
      color: #7ea8c8;

      i {
        font-size: 3vh;
        margin-bottom: 1vh;
        opacity: 0.5;
      }

      span {
        font-size: 1.8vh;
        opacity: 0.7;
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
    transform: scale(1.1);
  }
}
</style>

