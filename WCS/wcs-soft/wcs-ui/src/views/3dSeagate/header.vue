<template>
  <div class="header-container">
    <div class="header-background"></div>
    <div class="header-content">
      <!-- 左侧系统信息 -->
      <div class="system-info">
        <div class="system-title">
          <div class="title-main">自动化立体仓库控制系统</div>
          <div class="title-sub">Automated Storage & Retrieval System</div>
        </div>
      </div>
      
      <!-- 中间数据展示 -->
      <div class="data-display">
        <div class="data-item">
          <div class="data-label">运行状态</div>
          <div class="data-value status-active">正常运行</div>
        </div>
        <!-- <div class="data-item">
          <div class="data-label">设备数量</div>
          <div class="data-value">24</div>
        </div>
        <div class="data-item">
          <div class="data-label">任务队列</div>
          <div class="data-value">8</div>
        </div> -->
      </div>
      
      <!-- 右侧时间和控制 -->
      <div class="time-control">
        <div class="current-time">{{ currentTime }}</div>
        <div class="system-status">
          <div class="status-indicator online"></div>
          <div class="status-text">系统在线</div>
        </div>
      </div>
    </div>
    
    <!-- 科技装饰元素 -->
    <div class="tech-elements">
      <div class="scan-line"></div>
      <div class="particles">
        <div class="particle" v-for="i in 8" :key="i"></div>
      </div>
      <div class="circuit-pattern"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index",

  data() {
    return {
      timer: null,
      currentTime: null,
    };
  },
  created() {
    this.timer = setInterval(() => {
      this.currentTime = new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      }).replace(/\//g, '-');
    }, 1000);
  },

  beforeDestroy() {
    // 清除定时器以避免内存泄漏
    clearInterval(this.timer);
  },
  methods: {},
};
</script>

<style lang="scss" scoped>
.header-container {
  width: 100%;
  height: 10vh;
  position: relative;
  overflow: hidden;
  pointer-events: none; // 容器不捕获事件
  
  // 优化的科技背景 - 从上到下渐变透明，无明显边界
  .header-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 120%;
    background: linear-gradient(
      to bottom,
      rgba(0, 0, 0, 0.85) 0%, 
      rgba(5, 10, 20, 0.65) 40%, 
      rgba(0, 0, 0, 0.15) 80%,
      transparent 100%
    );
    
    // 轻微毛玻璃效果
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
    
    // 移除底部边界，改为微妙的纹理
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: 
        radial-gradient(circle at 10% 20%, rgba(0, 150, 255, 0.08) 0%, transparent 25%),
        radial-gradient(circle at 90% 80%, rgba(0, 200, 255, 0.06) 0%, transparent 25%);
    }
  }
  
  // 主内容区域
  .header-content {
    position: relative;
    z-index: 2;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    padding: 0 3vw;
    pointer-events: auto; // 内容可交互
    
    // 左侧系统信息
    .system-info {
      .system-title {
        .title-main {
          color: #d5e8f5;
          font-size: 2vh;
          font-weight: 600;
          letter-spacing: 2px;
          text-shadow: 0 0 5px rgba(0, 150, 255, 0.3);
          margin-bottom: 5px;
        }
        
        .title-sub {
          color: #8cb4d4;
          font-size: 1.3vh;
          letter-spacing: 1px;
          text-shadow: 0 0 3px rgba(0, 150, 255, 0.2);
        }
      }
    }
    
    // 中间数据展示
    .data-display {
      display: flex;
      gap: 40px;
      
      .data-item {
        text-align: center;
        
        .data-label {
          color: #7ea8c8;
          font-size: 1.2vh;
          margin-bottom: 3px;
          text-shadow: 0 0 2px rgba(100, 180, 255, 0.15);
        }
        
        .data-value {
          color: #e8f4f8;
          font-size: 1.8vh;
          font-weight: 600;
          text-shadow: 0 0 3px rgba(0, 150, 255, 0.2);
          
          &.status-active {
            color: #5ddb8f;
            text-shadow: 0 0 4px rgba(0, 200, 100, 0.3);
          }
        }
      }
    }
    
    // 右侧时间和控制
    .time-control {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 8px;
      
      .current-time {
        color: #e8f4f8;
        font-size: 1.6vh;
        font-family: 'Courier New', monospace;
        text-shadow: 0 0 3px rgba(0, 150, 255, 0.25);
        background: rgba(0, 30, 60, 0.25);
        padding: 0.5vh 1.5vw;
        border-radius: 6px;
        border: 1px solid rgba(0, 150, 255, 0.15);
        letter-spacing: 1px;
      }
      
      .system-status {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .status-indicator {
          width: 10px;
          height: 10px;
          border-radius: 50%;
          
          &.online {
            background: #5ddb8f;
            box-shadow: 0 0 8px rgba(0, 200, 100, 0.4);
            animation: pulse 2s infinite;
          }
        }
        
        .status-text {
          color: #7ea8c8;
          font-size: 1.2vh;
          text-shadow: 0 0 2px rgba(0, 150, 255, 0.15);
        }
      }
    }
  }
  
  // 科技装饰元素 - 简化版
  .tech-elements {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 1;
    
    // 微妙的电路板纹理
    .circuit-pattern {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: 
        linear-gradient(90deg, rgba(0, 100, 200, 0.02) 1px, transparent 1px),
        linear-gradient(rgba(0, 100, 200, 0.02) 1px, transparent 1px);
      background-size: 60px 60px;
      opacity: 0.5;
    }
  }
}

// 动画效果
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

// 响应式调整
@media (max-width: 1200px) {
  .header-container {
    .header-content {
      .system-info {
        .system-title {
          .title-main {
            font-size: 1.8vh;
          }
          .title-sub {
            font-size: 1.1vh;
          }
        }
      }
      
      .data-display {
        gap: 20px;
        
        .data-item {
          .data-label {
            font-size: 1vh;
          }
          .data-value {
            font-size: 1.5vh;
          }
        }
      }
      
      .time-control {
        .current-time {
          font-size: 1.4vh;
          padding: 0.4vh 1.2vw;
        }
        .system-status {
          .status-text {
            font-size: 1vh;
          }
        }
      }
    }
  }
}
</style>