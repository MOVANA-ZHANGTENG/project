<template>
  <div class="right-container">
    <!-- <Warnning></Warnning> -->
  </div>
</template>

<script> 
import Warnning from "./Warnning";
export default {
  name: "Index",

  data() {
    return {};
  },
  components: { 
    Warnning,
  },
  methods: {},
};
</script>

<style lang="scss" scoped>
.right-container {
  width: 20vw;
  height: 95vh;
  padding: 10px;
  
  // 黑色背景，不透明渐变效果
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.9) 0%,
    rgba(0, 0, 0, 0.8) 50%,
    rgba(0, 0, 0, 0.7) 100%
  );
  
  // 去除边框
  border: none;
  box-shadow: none;
  
  // 保持毛玻璃效果
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
  
  // 保持圆角
  border-radius: 8px;
  
  // 添加内部组件间距
  display: flex;
  flex-direction: column;
  gap: 1vh;
}
</style>