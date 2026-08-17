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
  padding: 3vh 2vw 0 3vw;
  width: 20vw;
  height: 17vh;

  .warn {
    margin-bottom: 2vh;
  }

  .el-alert--error.is-light {
    background-color: rgba(0, 0, 0, 0);
    color: #ff4949;
    border-color: #ff4949;
    border-style: solid;
    border-radius: 10px;
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
