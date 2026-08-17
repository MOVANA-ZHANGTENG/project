<template>
    <el-card v-if="pathList.length != 0">
        <div slot="header" class="clearfix">
            <span>执行任务</span>
            <el-button v-if="!showCard" style="float: right;margin:5px;" size="mini" type="text"
                @click="showCard = !showCard">显示</el-button>
            <el-button v-if="showCard" style="float: right;margin:5px;" size="mini" type="text"
                @click="showCard = !showCard">隐藏</el-button>
            <el-button style="float: right;margin:5px;" size="mini" type="text"
                @click="refreshComponent()">刷新</el-button>
        </div>
        <el-timeline v-if="showCard">
            <el-timeline-item type="primary" size="large" v-loading="loading" :timestamp="path.cmdTime"
                v-for="(path, index) in pathList" placement="top">

                <el-card shadow="hover">
                    <div slot="header" class="clearfix">
                        <span>第 {{ index + 1 }} 步: {{ path.name }}</span>
                        <!-- <div v-if="path.state == 2">
                            <el-button style="float: right;margin:5px;" size="mini" type="primary">强制重发</el-button>
                            <el-button style="float: right;margin:5px;" size="mini" type="success">强制完成</el-button>
                        </div> -->
                    </div>
                    <el-form label-position="left" label-width="80px" size="mini" inline
                        class="demo-table-expand width-path">
                        <el-form-item label="托盘编码">
                            <div>{{ path.palletCode || "无" }}</div>
                        </el-form-item>
                        <el-form-item label="任务状态">
                            <span v-for="item in pathStates" v-if="path.state == item.value"
                                :style="'color:' + item.color">{{
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
</template>

<script>
import request from '@/utils/request'

export default {
    name: "index",
    props: ['jobId'],
    dicts: ['task_state', 'task_type'],
    data() {
        return {
            timer: null,
            showCard: true,
            loading: false,
            pathList: [],
            queryParams: {},
            pathStates: [
                { value: 0, label: "初始化", color: "#909399" },
                { value: 1, label: "任务执行中", color: "#409EFF" },
                { value: 2, label: "任务完成", color: "#67C23A" },
            ],
        };
    },
    created() {
        this.refreshComponent();
        // this.onStartTimer()
    },
    mounted() {
    },
    methods: {
        onStartTimer() {
            const timer = setInterval(() => {
                this.refreshComponent();
            }, 1000)
            this.$once('hook:beforeDestroy', () => {
                clearInterval(timer);
            })
        },
        refreshComponent() {
            if (this.jobId != null) {
                this.queryParams.jobId = this.jobId;
                this.listNowPathList(this.queryParams)
            }
            this.$forceUpdate()
        },
        listNowPathList(query) {
            request({
                url: '/wcs-base/pathInfo/findPathHisListByJobId',
                method: 'get',
                params: query
            }).then(response => {
                if (response.code == 200) {
                    this.pathList = response.data
                }
            })
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
    clear: both
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