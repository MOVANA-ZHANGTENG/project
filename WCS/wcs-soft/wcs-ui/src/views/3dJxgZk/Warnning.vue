<template>
  <div class="task-container">
    <el-alert class="warn" v-for="warn in warns" :title="warn.name" :type="warn.type" :description="warn.memo"
      show-icon>
    </el-alert>
    <!-- <div
      style="width: 80%; height: 12vh; margin-top: 2vh; border: 1px dashed #000"
    ></div> -->
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "TaskNow",
  data() {
    return {
      warns: [
        { name: "扫码器", memo: "扫码器断连", type: "error" },
        { name: "堆垛机", memo: "堆垛机取货无货", type: "error" },
      ],
      config: {
        number: [1000],
        content: "{nt} ",
      },
    };
  },
  created() { },
  mounted() {
    ///this.config.number = [1000];
  },
  methods: {},
};
</script>

<style lang="scss" scoped>
.task-container {
  padding: 1vh 1vw;
  width: 100%;
  height: 15vh;
  
  // 移除背景色
  background: transparent;
  
  // 保持阴影效果
  box-shadow: 
    0 0 3px rgba(64, 158, 255, 0.1),
    inset 0 0 5px rgba(0, 0, 0, 0.2);
  
  // 保持毛玻璃效果
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
  
  // 保持圆角
  border-radius: 6px;

  .warn {
    margin-bottom: 1vh;
    padding: 0.5vh 1vw;
    
    // 移除背景色
    background: transparent !important;
    border-radius: 4px;
    border: 1px solid rgba(255, 73, 73, 0.2) !important;
    
    // 更柔和的发光效果
    box-shadow: 0 0 3px rgba(255, 73, 73, 0.1);
  }

  .el-alert--error.is-light {
    background-color: rgba(0, 0, 0, 0);
    color: #ff4949;
    border-color: #ff4949;
    border-style: solid;
    border-radius: 4px;
  }

  .el-alert__closebtn {
    color: #c0c4cc;
    display: none;
  }

  .el-icon-close {
    display: none;
  }
}
</style>