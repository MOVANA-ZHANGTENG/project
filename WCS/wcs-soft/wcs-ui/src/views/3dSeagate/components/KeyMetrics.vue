<template>
  <div class="key-metrics">
    <div class="metric-item" v-for="(metric, index) in metrics" :key="index" :class="metric.class">
      <div class="metric-icon">
        <i :class="metric.icon"></i>
      </div>
      <div class="metric-content">
        <div class="metric-label">{{ metric.label }}</div>
        <div class="metric-value">
          <count-to :start-val="0" :end-val="metric.value" :duration="1500"></count-to>
          <span class="metric-unit">{{ metric.unit }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import request from "@/utils/request"

export default {
  name: "KeyMetrics",
  components: {
    CountTo
  },
  data() {
    return {
      metrics: [
        {
          label: '任务总数',
          value: 0,
          unit: '',
          desc: '历史累计任务',
          icon: 'el-icon-s-order',
          class: 'metric-tasks'
        },
        {
          label: '在线设备',
          value: 0,
          unit: '台',
          desc: '当前在线设备',
          icon: 'el-icon-cpu',
          class: 'metric-devices'
        },
        {
          label: '库位利用率',
          value: 0,
          unit: '%',
          desc: '实时库位占用',
          icon: 'el-icon-data-line',
          class: 'metric-utilization'
        }
      ],
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
        // 获取任务统计
        const taskResponse = await request({
          url: "/wcs-task/TaskInfo/statistics/basic",
          method: "get",
          params: { wareCode }
        })
        
        if (taskResponse.code === 200) {
          this.metrics[0].value = taskResponse.data.total || 0
        }

        // 获取设备统计
        const deviceResponse = await request({
          url: "/wcs-base/DeviceInfo/list",
          method: "get",
          params: { wareCode, pageNum: 1, pageSize: 10000 }
        })
        
        if (deviceResponse.code === 200) {
          const devices = deviceResponse.rows || []
          this.metrics[1].value = devices.filter(d => d.isOnline === 1).length
        }

        // 获取库位统计
        const cellResponse = await request({
          url: "/wcs-base/CellInfo/list",
          method: "get",
          params: { wareCode, pageNum: 1, pageSize: 10000 }
        })
        
        if (cellResponse.code === 200) {
          const cells = cellResponse.rows || []
          const total = cells.length
          const occupied = cells.filter(c => c.invenState === 1 || c.invenState === '1').length
          this.metrics[2].value = total > 0 ? Math.round((occupied / total) * 100) : 0
        }

      } catch (error) {
        console.error('加载关键指标失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.key-metrics {
  display: flex;
  flex-direction: row;
  gap: 0.6vw;
  padding: 0;
  width: 100%;

  .metric-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 0.6vh;
    padding: 1.2vh 0.5vw;
    background: linear-gradient(
      135deg,
      rgba(0, 30, 60, 0.25) 0%,
      rgba(0, 20, 40, 0.15) 100%
    );
    border: 1px solid rgba(0, 150, 255, 0.1);
    border-top: 3px solid rgba(79, 172, 254, 0.4);
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 100, 200, 0.1);
    transition: all 0.3s ease;
    min-width: 0;

    &:hover {
      background: linear-gradient(
        135deg,
        rgba(0, 40, 80, 0.35) 0%,
        rgba(0, 30, 60, 0.25) 100%
      );
      border-color: rgba(0, 180, 255, 0.2);
      transform: translateY(-2px);
      box-shadow: 0 4px 15px rgba(0, 150, 255, 0.15);
    }

    .metric-icon {
      width: 3vw;
      height: 3vw;
      min-width: 32px;
      min-height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: rgba(79, 172, 254, 0.15);
      border: 2px solid rgba(79, 172, 254, 0.3);
      flex-shrink: 0;
      
      i {
        font-size: 2vh;
        color: #4facfe;
        text-shadow: 0 0 8px rgba(79, 172, 254, 0.5);
      }
    }

    .metric-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.3vh;
      text-align: center;
      width: 100%;

      .metric-label {
        font-size: 1.5vh;
        font-weight: 600;
        color: #7ea8c8;
        text-shadow: 0 0 3px rgba(100, 150, 200, 0.3);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        width: 100%;
        line-height: 1.3;
      }

      .metric-value {
        font-size: 3vh;
        font-weight: 700;
        color: #ffffff;
        text-shadow: 
          0 0 10px rgba(79, 172, 254, 0.5),
          0 2px 4px rgba(0, 0, 0, 0.5);
        line-height: 1;
        white-space: nowrap;
        
        .metric-unit {
          font-size: 1.8vh;
          margin-left: 0.2vw;
          opacity: 0.9;
        }
      }
    }

    // 不同指标的特殊样式
    &.metric-tasks {
      border-top-color: #667eea;
      
      .metric-icon {
        background: rgba(102, 126, 234, 0.15);
        border-color: rgba(102, 126, 234, 0.35);
        
        i {
          color: #667eea;
          text-shadow: 0 0 8px rgba(102, 126, 234, 0.5);
        }
      }

      .metric-value {
        text-shadow: 
          0 0 10px rgba(102, 126, 234, 0.5),
          0 2px 4px rgba(0, 0, 0, 0.5);
      }
    }

    &.metric-devices {
      border-top-color: #f5576c;
      
      .metric-icon {
        background: rgba(245, 87, 108, 0.15);
        border-color: rgba(245, 87, 108, 0.35);
        
        i {
          color: #f5576c;
          text-shadow: 0 0 8px rgba(245, 87, 108, 0.5);
        }
      }

      .metric-value {
        text-shadow: 
          0 0 10px rgba(245, 87, 108, 0.5),
          0 2px 4px rgba(0, 0, 0, 0.5);
      }
    }

    &.metric-utilization {
      border-top-color: #43e97b;
      
      .metric-icon {
        background: rgba(67, 233, 123, 0.15);
        border-color: rgba(67, 233, 123, 0.35);
        
        i {
          color: #43e97b;
          text-shadow: 0 0 8px rgba(67, 233, 123, 0.5);
        }
      }

      .metric-value {
        text-shadow: 
          0 0 10px rgba(67, 233, 123, 0.5),
          0 2px 4px rgba(0, 0, 0, 0.5);
      }
    }
  }
}
</style>

