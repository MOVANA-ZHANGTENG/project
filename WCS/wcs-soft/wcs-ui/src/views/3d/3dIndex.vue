<template>
  <div class="container3d">
   
  </div>
</template>

<script>
 
export default {
  data() {
    return {
    }
  },
  watch: {},
  created() { 
     // 打开新标签
     let routeData = this.$router.resolve(({
      path: '/3d', // path 要跳转的路由地址
    
      }))
      //新页签打开3d
    window.open(routeData.href,'_blank') ;
    //返回原来页面
    window.history.back();
  },
  mounted() {
   
  },

  beforeDestroy() {
    
  },
  methods: {
     
  },
};
</script>

<style lang="scss">
.container3d {
  width: 100vw;
  height: 100vh;
  // background-color: #041e0b;
  //background-color: rgba(0, 0, 0, 0.8);

  background-image: url("/img/999.jpg"); /* 替换 'image-path.jpg' 为你的图片路径 */
  background-size: cover; /* 背景图片覆盖整个元素 */
  background-repeat: no-repeat; /* 背景图片不重复 */
  background-position: center; /* 背景图片居中 */
}
.three-canvas {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.workshop-text {
  box-shadow: 0 0 5px #2d69a1;
  padding: 10px;
  background: #2d69a154;
}

.workshop-text p {
  font-size: 0.3rem;
  font-weight: bold;
  padding: 10px;
  color: #fff;
}
.workshop-text.IDLE {
  background: #ecd272 !important;
}

.workshop-text.RUNNING {
  background: #86efd4 !important;
}

.workshop-text.OFFLINE {
  background: #8ab1de !important;
}
</style>
