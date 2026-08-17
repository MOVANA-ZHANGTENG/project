<template>
  <div>
    <el-card v-if="pathList.length != 0">
      <div slot="header" class="clearfix">
        <span>执行任务</span>
        <el-button v-if="!showCard" style="float: right; margin: 5px" size="mini" type="text"
          @click="showCard = !showCard">显示</el-button>
        <el-button v-if="showCard" style="float: right; margin: 5px" size="mini" type="text"
          @click="showCard = !showCard">隐藏</el-button>
        <el-button style="float: right; margin: 5px" size="mini" type="text" @click="refreshComponent()">刷新</el-button>
      </div>
      <el-timeline v-if="showCard">
        <el-timeline-item type="primary" size="large" v-loading="loading" :timestamp="path.cmdTime"
          v-for="(path, index) in pathList" placement="top">
          <el-card shadow="hover">
            <div slot="header" class="clearfix">
              <span>第 {{ index + 1 }} 步 PATH_INFO : {{ path.name }}</span>

              <el-button style="float: right; margin: 5px" size="mini" type="success"
                @click="getPathHandles(path.id)">任务进度</el-button>
            </div>
            <el-form label-position="left" label-width="80px" size="mini" inline class="demo-table-expand width-path">
              <el-form-item label="托盘编码">
                <div>{{ path.palletCode || "无" }}</div>
              </el-form-item>
              <el-form-item label="任务状态">
                <span v-for="item in pathStates" v-if="path.state == item.value" :style="'color:' + item.color">{{
                  item.label }}</span>
              </el-form-item>
              <el-form-item label="起点位置">
                <div>{{ path.fromCellCode || "无" }}</div>
              </el-form-item>
              <el-form-item label="信息">
                <div>{{ path.memo || "无" }}</div>
              </el-form-item>
              <el-form-item label="终点位置">
                <div>{{ path.toCellCode || "无" }}</div>
              </el-form-item>
              <el-form-item label="执行时间">
                <div>{{ path.cmdTime || "无" }}</div>
              </el-form-item>
            </el-form>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-drawer title="任务进度（PATH_INFO）" :visible.sync="drawer" direction="rtl" size="25%">
      <div style="margin: 3%;">
        <el-descriptions class="margin-top" title="执行条件" :column="1" border>
          <!-- <template slot="extra">
            <el-button type="primary" size="small">操作</el-button>
          </template> -->

          <el-descriptions-item v-for="item in pathHandles" v-if="item.type == 1">
            <template slot="label">
              <i v-if="item.state == 0" class="el-icon-loading" style="color: #409EFF;"></i>
              <i v-if="item.state == 1" class="el-icon-success" style="color: #67C23A;"></i>
            </template>
            {{ item.name }}
          </el-descriptions-item>

        </el-descriptions>
      </div>

      <div style="margin: 3%;">
        <el-descriptions class="margin-top" title="执行函数" :column="1" border>
          <!-- <template slot="extra">
            <el-button type="primary" size="small">操作</el-button>
          </template> -->
          <el-descriptions-item v-for="item in pathHandles" v-if="item.type == 2">
            <template slot="label">
              <i v-if="item.state == 0" class="el-icon-loading" style="color: #409EFF;"></i>
              <i v-if="item.state == 1" class="el-icon-success" style="color: #67C23A;"></i>
            </template>
            {{ item.name }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div style="margin: 3%;">
        <el-descriptions class="margin-top" title="成功条件" :column="1" border>
          <!-- <template slot="extra">
            <el-button type="primary" size="small">操作</el-button>
          </template> -->
          <el-descriptions-item v-for="item in pathHandles" v-if="item.type == 3">
            <template slot="label">
              <i v-if="item.state == 0" class="el-icon-loading" style="color: #409EFF;"></i>
              <i v-if="item.state == 1" class="el-icon-success" style="color: #67C23A;"></i>
            </template>
            {{ item.name }}
          </el-descriptions-item>
        </el-descriptions>
      </div>


      <div style="margin: 3%;">
        <el-descriptions class="margin-top" title="成功回调" :column="1" border>
          <!-- <template slot="extra">
            <el-button type="primary" size="small">操作</el-button>
          </template> -->
          <el-descriptions-item v-for="item in pathHandles" v-if="item.type == 4">
            <template slot="label">
              <i v-if="item.state == 0" class="el-icon-loading" style="color: #409EFF;"></i>
              <i v-if="item.state == 1" class="el-icon-success" style="color: #67C23A;"></i>
            </template>
            {{ item.name }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>

</template>

<script>
import request from "@/utils/request";
import { listPathHandle, getPathHandle, delPathHandle, addPathHandle, updatePathHandle } from "@/api/wcs-task/pathHandle";

export default {
  name: "index",
  props: ["jobId"],
  dicts: ["task_state", "task_type"],
  data() {
    return {
      drawer:false,
      timer: null,
      showCard: true,
      loading: false,
      pathList: [],
      queryParams: {},
      pathHandles:[],

      jobStates: [
        { value: 0, label: "初始化", color: "#909399" },
        { value: 1, label: "任务执行中", color: "#409EFF" },
        { value: 2, label: "任务完成", color: "#67C23A" },
      ],
      pathStates: [
        { value: 0, label: "初始化", color: "#909399" },
        { value: 1, label: "任务执行中", color: "#409EFF" },
        { value: 2, label: "任务完成", color: "#67C23A" },
      ],
    };
  },
  created() {
    this.onStartTimer();
  },
  mounted() { },
  methods: {
    getPathHandles(pathId) {
      var query = { pathId: pathId }
      listPathHandle(query).then((response) => {
        if (response.code == 200) {
          this.pathHandles = response.rows
          this.drawer = true
        }
      })
    },
    onStartTimer() {
      this.refreshComponent();
      const timer = setInterval(() => {
        this.refreshComponent();
      }, 5000);
      this.$once("hook:beforeDestroy", () => {
        clearInterval(timer);
      });
    },
    refreshComponent() {
      if (this.jobId != null) {
        this.queryParams.jobId = this.jobId;
        this.listNowPathList(this.queryParams);
      }
      this.$forceUpdate();
    },
    listNowPathList(query) {
      request({
        url: "/wcs-base/pathInfo/findPathListByJobId",
        method: "get",
        params: query,
      }).then((response) => {
        if (response.code == 200) {
          this.pathList = response.data;
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.text {
  font-size: 14px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 80px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-left: 5%;
  margin-right: 0;
  margin-bottom: 0;
  // width: 15%;
}

.width-path .el-form-item {
  margin-left: 0%;
  width: 50%;
}
</style>
