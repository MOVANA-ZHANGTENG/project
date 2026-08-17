<template>
  <div class="container">
    <!-- 3D场景容器（最底层） -->
    <Three3d class="three-container"></Three3d>
    
    <!-- 顶部控制栏 -->
    <Header style="position: absolute; top: 0px; left: 0; right: 0; z-index: 10;"></Header>
    
    <!-- 左侧信息面板 -->
    <Left v-if="disable==1" style="position: absolute; top: 10vh; left: 0px; z-index: 10;"></Left>
   
    <!-- 底部任务栏 -->
    <Bottom v-if="disable==1" 
      style="position: absolute; bottom: 0vh; right: 2vw; z-index: 10;"
    ></Bottom>
  </div>
</template>

<script>
import Header from "./header";
import Left from "./left"; 
import Bottom from "./Bottom";
import Three3d from "./3d";
export default {
  data() {
    return {
      disable:1,
      timer:null,
    };
  },
  watch: {},
  components: {
    Header,
    Left, 
    Bottom,
    Three3d,
  },
  created() {// 设置定时器每秒更新counter
    // this.timer = setInterval(() => {
    //   this.disable  =  this.disable*-1;
    // }, 5000);
  },
  beforeDestroy() {
    // 清除定时器以避免内存泄漏
    clearInterval(this.timer);
  },
  mounted() {},

  beforeDestroy() {},
  methods: {},
};
</script>

<style lang="scss">
.container {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: #000000;
  
  // 全局科技感滤镜 - 微妙版本
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: 
      radial-gradient(circle at 20% 30%, rgba(0, 100, 200, 0.02) 0%, transparent 30%),
      radial-gradient(circle at 80% 70%, rgba(0, 150, 255, 0.02) 0%, transparent 30%);
    pointer-events: none;
    z-index: 1;
  }
}

.three-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}
</style>
