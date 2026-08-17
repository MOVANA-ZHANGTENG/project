<template>
  <div class="bottom-container">
    <div class="content">
      <div class="marquee-container">
        <div class="marquee">
          <!-- 任务卡片 -->
          <div 
            v-for="(data, index) in datas" 
            :key="index" 
            class="task-card"
            :class="getTaskClass(data.taskTypeName)"
          >
            <!-- 卡片头部：任务类型 + 图标 -->
            <div class="card-header">
              <div class="task-type">
                <i :class="getTaskIcon(data.taskTypeName)" class="task-icon"></i>
                <span class="task-type-name">{{ data.taskTypeName }}</span>
              </div>
              <div class="status-badge">执行中</div>
            </div>

            <!-- 分隔线 -->
            <div class="divider"></div>

            <!-- 卡片内容 -->
            <div class="card-body">
              <!-- 托盘信息 -->
              <div class="info-item">
                <div class="info-label">
                  <i class="el-icon-box"></i>
                  <span>托盘号</span>
                </div>
                <div class="info-value">{{ data.palletCode }}</div>
              </div>

              <!-- 路线信息 -->
              <div class="route-info">
                <div class="info-item">
                  <div class="info-label">
                    <i class="el-icon-position"></i>
                    <span>起点</span>
                  </div>
                  <div class="info-value">{{ data.fromCellCode || data.from }}</div>
                </div>

                <!-- 箭头指示 -->
                <div class="route-arrow">
                  <i class="el-icon-right"></i>
                </div>

                <div class="info-item">
                  <div class="info-label">
                    <i class="el-icon-location"></i>
                    <span>终点</span>
                  </div>
                  <div class="info-value">{{ data.toCellCode || data.to }}</div>
                </div>
              </div>
            </div>

            <!-- 卡片底部：进度条 -->
            <div class="card-footer">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: getRandomProgress() + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script> 
import request from "@/utils/request";
export default {
  name: "Index",

  data() {
    return {
      timer:null,
      datas:[
        {
          taskTypeName:"盘点",
          palletCode:"P123456",
          from:"001001001",
          to:"001002001" 
        }
        ,
        {
          taskTypeName:"入库",
          palletCode:"P123456",
          from:"001",
          to:"001002001" 
        },
        {
          taskTypeName:"出库",
          palletCode:"P123456",
          from:"001001001",
          to:"001002009" 
        },
        {
          taskTypeName:"移库",
          palletCode:"P123456",
          from:"001001015",
          to:"001002012" 
        },
        {
          taskTypeName:"入库",
          palletCode:"P123456",
          fromCellCode:"001",
          toCellCode:"001002005" 
        }
       
      ]
    };
  },
  components: { 
  },
  created() {
    this.timer=setInterval(() => {
      this.getTaskInfos(); 
    }, 1000); 
  },

 
  beforeDestroy() { 
    clearInterval(this.timer); 
  },
  methods: {
    getTaskInfos() {
      var that = this;
      request({
        url: "/wcs-task/TaskInfo/list",
        method: "get",
        params: {  },
      }).then((response) => {
        if (response.code == 200) {
          that.datas = response.rows; 
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },
    
    // 获取任务类型对应的图标
    getTaskIcon(taskType) {
      const iconMap = {
        '入库': 'el-icon-download',
        '出库': 'el-icon-upload2',
        '移库': 'el-icon-sort',
        '盘点': 'el-icon-document-checked'
      };
      return iconMap[taskType] || 'el-icon-tickets';
    },
    
    // 获取任务类型对应的样式类
    getTaskClass(taskType) {
      const classMap = {
        '入库': 'task-in',
        '出库': 'task-out',
        '移库': 'task-move',
        '盘点': 'task-check'
      };
      return classMap[taskType] || '';
    },
    
    // 生成随机进度（实际应该从后端获取）
    getRandomProgress() {
      return Math.floor(Math.random() * 40) + 50; // 50-90%
    }
  },
};
</script>

<style lang="scss" scoped>
.bottom-container {
  width: 75vw;
  position: relative;
  pointer-events: none; // 容器不捕获事件

  .content {
    position: relative;
    padding: 1.5vh 1vw;
    width: 79vw;
    height: 28vh;
    border-radius: 20px 20px 0 0;
    overflow: hidden;
    pointer-events: none; // 内容容器也不捕获
    
    // 更加平滑的渐变背景 - 从下到上透明，无明显边界
    background: linear-gradient(
      to top,
      rgba(0, 0, 0, 0.75) 0%,
      rgba(5, 10, 20, 0.60) 30%,
      rgba(0, 0, 0, 0.25) 70%,
      rgba(0, 0, 0, 0.05) 90%,
      transparent 100%
    );
    
    // 轻微毛玻璃效果
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
  }

  .marquee-container {
    width: 100%;
    height: 100%;
    overflow: hidden;
    white-space: nowrap;
    box-sizing: border-box;
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
  }
 
  .marquee {
    display: inline-flex;
    gap: 1.2vw;
    padding: 0 1vw;
  }

  // 任务卡片
  .task-card {
    position: relative;
    display: inline-flex;
    flex-direction: column;
    width: 220px;
    min-height: 20vh;
    padding: 1.2vh 1vw;
    border-radius: 12px;
    background: linear-gradient(
      135deg,
      rgba(0, 15, 30, 0.40) 0%,
      rgba(0, 20, 40, 0.30) 100%
    );
    border: 1px solid rgba(0, 150, 255, 0.12);
    box-shadow: 
      0 2px 12px rgba(0, 100, 200, 0.1),
      inset 0 0 20px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;
    overflow: hidden;
    pointer-events: auto; // 卡片可交互
    
    // 顶部装饰线
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 2px;
      background: linear-gradient(
        90deg,
        transparent 0%,
        rgba(0, 150, 255, 0.5) 50%,
        transparent 100%
      );
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover {
      background: linear-gradient(
        135deg,
        rgba(0, 20, 40, 0.50) 0%,
        rgba(0, 30, 60, 0.40) 100%
      );
      border-color: rgba(0, 180, 255, 0.25);
      box-shadow: 
        0 4px 20px rgba(0, 150, 255, 0.15),
        inset 0 0 20px rgba(0, 100, 200, 0.15);
      transform: translateY(-3px);
      
      &::before {
        opacity: 1;
      }
    }

    // 卡片头部
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1vh;
      
      .task-type {
        display: flex;
        align-items: center;
        gap: 0.5vw;
        
        .task-icon {
          font-size: 2vh;
          color: #4facfe;
          text-shadow: 0 0 8px rgba(79, 172, 254, 0.5);
        }
        
        .task-type-name {
          font-size: 1.6vh;
          font-weight: 600;
          color: #e8f4f8;
          text-shadow: 0 0 4px rgba(255, 255, 255, 0.3);
          letter-spacing: 1px;
        }
      }
      
      .status-badge {
        padding: 0.3vh 0.8vw;
        background: rgba(67, 233, 123, 0.15);
        border: 1px solid rgba(67, 233, 123, 0.3);
        border-radius: 12px;
        font-size: 1vh;
        color: #43e97b;
        text-shadow: 0 0 5px rgba(67, 233, 123, 0.5);
        white-space: nowrap;
      }
    }

    // 分隔线
    .divider {
      width: 100%;
      height: 1px;
      background: linear-gradient(
        90deg,
        transparent 0%,
        rgba(0, 150, 255, 0.2) 50%,
        transparent 100%
      );
      margin: 0.5vh 0 1vh 0;
    }

    // 卡片内容
    .card-body {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 1vh;
      
      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5vh 0;
        
        .info-label {
          display: flex;
          align-items: center;
          gap: 0.3vw;
          font-size: 1.1vh;
          color: #7ea8c8;
          text-shadow: 0 0 2px rgba(100, 150, 200, 0.2);
          
          i {
            font-size: 1.3vh;
            color: #4facfe;
            opacity: 0.7;
          }
        }
        
        .info-value {
          font-size: 1.3vh;
          font-weight: 500;
          color: #e8f4f8;
          text-shadow: 0 0 3px rgba(255, 255, 255, 0.25);
          font-family: 'Courier New', monospace;
          letter-spacing: 0.5px;
        }
      }
      
      // 路线信息
      .route-info {
        display: flex;
        align-items: center;
        gap: 0.5vw;
        padding: 0.8vh 0.5vw;
        background: rgba(0, 30, 60, 0.3);
        border-radius: 8px;
        border: 1px solid rgba(0, 100, 200, 0.15);
        
        .info-item {
          flex: 1;
          flex-direction: column;
          align-items: flex-start;
          gap: 0.3vh;
          padding: 0;
        }
        
        .route-arrow {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 2vw;
          height: 2vw;
          background: rgba(0, 150, 255, 0.15);
          border-radius: 50%;
          
          i {
            font-size: 1.5vh;
            color: #4facfe;
            font-weight: bold;
          }
        }
      }
    }

    // 卡片底部
    .card-footer {
      margin-top: 1vh;
      
      .progress-bar {
        width: 100%;
        height: 4px;
        background: rgba(0, 30, 60, 0.5);
        border-radius: 2px;
        overflow: hidden;
        
        .progress-fill {
          height: 100%;
          background: linear-gradient(
            90deg,
            #4facfe 0%,
            #00f2fe 100%
          );
          box-shadow: 0 0 8px rgba(79, 172, 254, 0.5);
          border-radius: 2px;
          transition: width 0.5s ease;
          animation: progress-glow 2s ease-in-out infinite;
        }
      }
    }

    // 不同任务类型的特殊样式
    &.task-in {
      .card-header .task-icon {
        color: #43e97b;
        text-shadow: 0 0 8px rgba(67, 233, 123, 0.5);
      }
      .card-footer .progress-fill {
        background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
        box-shadow: 0 0 8px rgba(67, 233, 123, 0.5);
      }
    }

    &.task-out {
      .card-header .task-icon {
        color: #fa709a;
        text-shadow: 0 0 8px rgba(250, 112, 154, 0.5);
      }
      .card-footer .progress-fill {
        background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
        box-shadow: 0 0 8px rgba(250, 112, 154, 0.5);
      }
    }

    &.task-move {
      .card-header .task-icon {
        color: #a8edea;
        text-shadow: 0 0 8px rgba(168, 237, 234, 0.5);
      }
      .card-footer .progress-fill {
        background: linear-gradient(90deg, #a8edea 0%, #fed6e3 100%);
        box-shadow: 0 0 8px rgba(168, 237, 234, 0.5);
      }
    }

    &.task-check {
      .card-header .task-icon {
        color: #ffd89b;
        text-shadow: 0 0 8px rgba(255, 216, 155, 0.5);
      }
      .card-footer .progress-fill {
        background: linear-gradient(90deg, #ffd89b 0%, #19547b 100%);
        box-shadow: 0 0 8px rgba(255, 216, 155, 0.5);
      }
    }
  }

  // 进度条发光动画
  @keyframes progress-glow {
    0%, 100% {
      opacity: 1;
    }
    50% {
      opacity: 0.8;
    }
  }
}
</style>