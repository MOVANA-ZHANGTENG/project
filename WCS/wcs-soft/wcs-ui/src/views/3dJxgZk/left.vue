<template>
  <div class="left-container">
    <KeyMetrics />
    <CellReport />
    <DeviceMonitor />
    <AlertPanel />
  </div>
</template>

<script>
import KeyMetrics from './components/KeyMetrics.vue'
import CellReport from './components/CellReport.vue'
import DeviceMonitor from './components/DeviceMonitor.vue'
import AlertPanel from './components/AlertPanel.vue'

export default {
  name: "Index",
  components: {
    KeyMetrics,
    CellReport,
    DeviceMonitor,
    AlertPanel
  },
  data() {
    return {}
  },
  methods: {}
}
</script>

<style lang="scss" scoped>
.left-container {
  position: relative;
  margin-left: 0;
  margin-top: 3vh;
  width: 23vw;
  height: 85vh;
  padding: 2vh 1.2vw;
  pointer-events: none; // 容器不捕获事件
  
  // 更加平滑的渐变背景 - 从左到右透明，无明显边界
  background: linear-gradient(
    to right,
    rgba(0, 0, 0, 0.70) 0%,
    rgba(5, 10, 20, 0.55) 30%,
    rgba(0, 0, 0, 0.20) 70%,
    rgba(0, 0, 0, 0.05) 90%,
    transparent 100%
  );
  
  // 轻微毛玻璃效果
  backdrop-filter: blur(1px);
  -webkit-backdrop-filter: blur(1px);
  
  // 圆角
  border-radius: 0 20px 20px 0;
  
  // 内部组件布局
  display: flex;
  flex-direction: column;
  gap: 2vh;
  
  // 优化内部子组件的统一样式 - 无边框版本
  ::v-deep > div {
    background: transparent; // 完全透明背景
    border: none; // 去除边框
    border-radius: 8px;
    padding: 1vh 0; // 移除左右padding，充分利用宽度
    transition: all 0.3s ease;
    pointer-events: auto; // 子组件可交互
    width: 100%; // 确保组件宽度100%
    
    &:hover {
      background: rgba(0, 15, 30, 0.15); // 悬浮时微妙背景
      transform: translateX(2px);
    }
    
    // 为不同组件设置合理高度
    &:nth-child(1) { // TaskNum
      flex: 0 0 auto;
      min-height: 10vh;
    }
    
    &:nth-child(2) { // CellReport
      flex: 0 0 auto;
      min-height: 16vh;
    }
    
    &:nth-child(3) { // DeviceMonitor
      flex: 1; // 占据剩余空间
      min-height: 22vh;
      max-height: 30vh;
    }
    
    &:nth-child(4) { // PalletReport
      flex: 0 0 auto;
      min-height: 16vh;
    }
  }
  
  // 优化内部滚动
  overflow-y: hidden;
  overflow-x: hidden;
}
</style>