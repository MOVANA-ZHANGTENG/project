<template>
  <div class="app-container task-detail-container">
    <!-- 任务基本信息卡片 - 深色科技风格 -->
    <el-card class="task-info-card tech-dark-card" shadow="hover">
      <div slot="header" class="card-header tech-header">
        <div class="header-left">
          <i class="el-icon-monitor"></i>
          <span class="card-title">任务基本信息</span>
          <span class="tech-subtitle">TASK INFORMATION PANEL</span>
        </div>
        <div class="header-actions">
          <el-button size="small" type="warning" icon="el-icon-close" @click="handleClose()" class="action-btn tech-btn">
            关闭
          </el-button>
          <el-button size="small" type="primary" :icon="showTaskInfo ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"
            @click="showTaskInfo = !showTaskInfo" class="action-btn tech-btn">
            {{ showTaskInfo ? '隐藏详情' : '显示详情' }}
          </el-button>
        </div>
      </div>

      <transition name="slide-fade">
        <div v-show="showTaskInfo" class="task-info-content">
          <div v-loading="loading" class="task-info-modern">

            <!-- 核心信息区 -->
            <div class="info-section core-section">
              <div class="info-cards-grid">
                <div class="info-mini-card">
                  <div class="card-icon-wrapper blue">
                    <i class="el-icon-tickets"></i>
                  </div>
                  <div class="card-text">
                    <span class="card-label">WCS:</span>
                    <span class="card-value">{{ taskInfo.id || "无" }}</span>
                  </div>
                </div>

                <div class="info-mini-card">
                  <div class="card-icon-wrapper green">
                    <i class="el-icon-document"></i>
                  </div>
                  <div class="card-text">
                    <span class="card-label">WMS:</span>
                    <span class="card-value">{{ taskInfo.wmsTaskNo || "无" }}</span>
                  </div>
                </div>

                <div class="info-mini-card">
                  <div class="card-icon-wrapper orange">
                    <i class="el-icon-office-building"></i>
                  </div>
                  <div class="card-text">
                    <span class="card-label">仓库:</span>
                    <el-select v-model="selectedWareCode" 
                      @change="handleWareCodeChange" 
                      placeholder="请选择仓库" 
                      clearable
                      size="mini"
                      class="ware-select">
                      <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
                    </el-select>
                    <span v-if="!selectedWareCode" class="card-value">{{ taskInfo.wareCode || "无" }}</span>
                  </div>
                </div>

                <div class="info-mini-card">
                  <div class="card-icon-wrapper cyan">
                    <i class="el-icon-s-operation"></i>
                  </div>
                  <div class="card-text">
                    <span class="card-label">类型:</span>
                    <el-select v-model="selectedTaskType" 
                      placeholder="请选择类型" 
                      clearable
                      size="mini"
                      :disabled="!selectedWareCode"
                      class="task-type-select">
                      <el-option v-for="item in taskTypes" :key="item.code" :label="item.name" :value="item.code" />
                    </el-select>
                    <span v-if="!selectedTaskType" class="card-value">{{ getTaskTypeName(taskInfo.type) }}</span>
                  </div>
                </div>

                <div class="info-mini-card">
                  <div class="card-icon-wrapper purple">
                    <i class="el-icon-s-flag"></i>
                  </div>
                  <div class="card-text">
                    <span class="card-label">状态:</span>
                    <span class="card-value">
                  <dict-tag :options="dict.type.task_state" :value="taskInfo.state" />
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 路径信息区 -->
            <div class="info-section path-section">
              <div class="task-path-visual">
                <div class="path-point start-point">
                  <div class="point-icon">
                    <i class="el-icon-location-outline"></i>
                  </div>
                  <div class="point-info">
                    <span class="point-label">FROM:</span>
                    <span class="point-value">{{ taskInfo.fromCellCode || "无" }}</span>
                  </div>
                </div>

                <div class="path-arrow-line">
                  <div class="arrow-line-inner"></div>
                </div>

                <div class="path-point end-point">
                  <div class="point-icon">
                    <i class="el-icon-location"></i>
                  </div>
                  <div class="point-info">
                    <span class="point-label">TO:</span>
                    <span class="point-value">{{ taskInfo.toCellCode || "无" }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 分隔符 -->
            <div class="info-divider" v-if="taskInfo.palletCode || taskInfo.palletWeight || taskInfo.pallectHeight"></div>

            <!-- 托盘信息区 -->
            <div class="info-section pallet-section" v-if="taskInfo.palletCode || taskInfo.palletWeight || taskInfo.pallectHeight">
              <div class="pallet-info-list">
                <div class="pallet-info-item" v-if="taskInfo.palletCode">
                  <span class="pallet-label">
                    <i class="el-icon-box"></i>
                    托盘:
                  </span>
                  <span class="pallet-value">{{ taskInfo.palletCode }}</span>
                </div>
                <div class="pallet-info-item" v-if="taskInfo.palletWeight">
                  <span class="pallet-label">
                    <i class="el-icon-scale-to-original"></i>
                    重量:
                  </span>
                  <span class="pallet-value">{{ taskInfo.palletWeight }}</span>
                </div>
                <div class="pallet-info-item" v-if="taskInfo.pallectHeight">
                  <span class="pallet-label">
                    <i class="el-icon-sort"></i>
                    高度:
                  </span>
                  <span class="pallet-value">{{ taskInfo.pallectHeight }}</span>
                </div>
              </div>
            </div>

          </div>
        </div>
      </transition>
    </el-card>

    <!-- 主要内容区域 - 左右分栏布局 -->
    <el-row :gutter="24" class="main-content">
      <!-- 左侧：任务履历 -->
      <el-col :span="8" :md="24" :sm="24" class="left-panel">
        <el-card class="timeline-card terminal-card" shadow="hover">
          <div slot="header" class="card-header terminal-header">
            <div class="header-left">
              <i class="el-icon-monitor"></i>
              <span class="card-title">任务日志</span>
              <span class="terminal-subtitle">TASK LOG TERMINAL</span>
              <span class="terminal-count">[{{ sortedRecords.length }} 条记录]</span>
            </div>
            <div class="terminal-controls">
              <span class="terminal-dot terminal-dot-yellow"></span>
              <span class="terminal-dot terminal-dot-green"></span>
              <span class="terminal-dot terminal-dot-red"></span>
            </div>
          </div>
          <div class="terminal-container" v-loading="loading" ref="terminalContainer">
            <div class="terminal-output">
              <div v-for="(record, index) in sortedRecords" :key="index" class="terminal-line">
                <span class="terminal-line-number">{{ String(index + 1).padStart(3, '0') }}</span>
                <span class="terminal-prompt">$</span>
                <span class="terminal-timestamp">[{{ record.createTime }}]</span>
                <span class="terminal-text" v-if="record.content && record.content.length <= 150">{{ record.content }}</span>
                <span class="terminal-text" v-else>
                  {{ record.content ? record.content.substring(0, 150) + '...' : '' }}
                  <span class="terminal-more" @click="showFullContent(record.content, '任务履历')">[查看更多]</span>
                </span>
              </div>
              <div v-if="sortedRecords.length === 0" class="terminal-line">
                <span class="terminal-prompt">$</span>
                <span class="terminal-text terminal-info">等待任务日志...</span>
              </div>
              <div class="terminal-cursor"></div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：任务执行步骤 -->
      <el-col :span="16" :md="24" :sm="24" class="right-panel">
        <el-card class="job-list-card tech-border-card" shadow="hover">
          <div slot="header" class="card-header tech-header">
            <div class="header-left">
              <i class="el-icon-s-operation"></i>
              <span class="card-title">任务执行步骤</span>
              <span class="tech-subtitle">TASK EXECUTION STEPS</span>
              <span v-if="taskInfo && taskInfo.id" class="task-id-badge">ID: {{ taskInfo.id }}</span>
            </div>
          </div>

          <div class="job-timeline-container">
            <el-timeline v-loading="loading">
              <el-timeline-item v-for="(job, index) in displayJobList" :key="job.id" type="primary" size="large"
                :timestamp="job.cmdTime" placement="top" class="job-timeline-item">
                <el-card class="job-card" shadow="hover" :class="['job-state-' + job.state, job.children && job.children.length > 1 ? 'branch-node' : '']">
                  <!-- 顶部状态条 -->
                  <div class="job-status-bar" :class="getJobStateClass(job.state)"></div>

                  <div slot="header" class="job-header">
                    <div class="job-title">
                      <div class="job-badge" :class="job.children && job.children.length > 1 ? 'branch-badge' : ''">
                        <i :class="job.children && job.children.length > 1 ? 'el-icon-share' : 'el-icon-s-order'"></i>
                        <span class="job-id">#{{ job.id }}</span>
                        <span v-if="job.children && job.children.length > 1" class="branch-indicator">判断</span>
                      </div>
                      <div class="job-info-inline">
                        <span class="job-name">{{ job.name || '任务执行' }}</span>
                        <span v-for="item in jobStates" v-if="job.state == item.value" :key="item.value"
                          class="job-state-tag" :style="{ background: item.color }">
                          <i class="el-icon-success" v-if="job.state == 4"></i>
                          <i class="el-icon-loading" v-if="job.state == 2"></i>
                          {{ item.label }}
                        </span>
                      </div>
                    </div>
                    <div class="job-actions">
                      <!-- 已完成job显示展开/折叠按钮 -->
                      <el-button v-if="job.state == 4" size="mini" type="info" 
                        :icon="isJobExpanded(job.id) ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"
                        @click="toggleJobExpand(job.id)" class="action-btn-small">
                        {{ isJobExpanded(job.id) ? '收起' : '展开' }}
                      </el-button>
                      <el-button size="mini" type="success" icon="el-icon-view" @click="getJobHandles(job.id)"
                        class="action-btn-small">
                        任务进度
                      </el-button>
                      <el-button v-if="job.state == 2||job.state == 3" @click="cmd(job.id)" size="mini" type="primary"
                        icon="el-icon-refresh" class="action-btn-small">
                        强制重发
                      </el-button>
                      <el-button v-if="job.state == 2" @click="successPre(job.id)" size="mini" type="success"
                        icon="el-icon-check" class="action-btn-small">
                        强制完成
                      </el-button>
                    </div>
                  </div>

                  <!-- 详细内容区 - 已完成的job默认折叠 -->
                  <transition name="job-detail-fade">
                    <div v-show="job.state !== 4 || isJobExpanded(job.id)" class="job-detail-content">
                      <!-- 路径信息区 - 亮点设计 -->
                      <div class="job-path-section" v-if="job.fromCellCode || job.toCellCode" :class="{ 'path-completed': job.state === 4, 'path-executing': job.state === 2 }">
                        <div class="path-location from-location">
                          <div class="location-icon">
                            <i class="el-icon-location-outline"></i>
                          </div>
                          <div class="location-info">
                            <span class="location-label">FROM:</span>
                            <span class="location-code">{{ job.fromCellCode || "无" }}</span>
                          </div>
                        </div>
                        <div class="path-arrow">
                          <div class="arrow-line" :class="{ 'arrow-animated': job.state !== 4 }"></div>
                          <i class="el-icon-d-arrow-right arrow-icon" :class="{ 'icon-completed': job.state === 4 }"></i>
                        </div>
                        <div class="path-location to-location">
                          <div class="location-icon">
                            <i class="el-icon-location"></i>
                          </div>
                          <div class="location-info">
                            <span class="location-label">TO:</span>
                            <span class="location-code">{{ job.toCellCode || "无" }}</span>
                          </div>
                        </div>
                      </div>

                      <!-- 信息卡片组 - 简洁版 -->
                      <div class="job-info-cards">
                        <!-- 托盘信息 - 简化设计 -->
                        <div class="info-card-simple pallet-simple" v-if="job.palletCode">
                          <span class="card-label"><i class="el-icon-box"></i> 托盘:</span>
                          <span class="card-value">{{ job.palletCode }}</span>
                          <span class="card-extra" v-if="job.palletWeight">{{ job.palletWeight }}</span>
                        </div>

                        <!-- 设备信息 -->
                        <div class="info-card device-card" v-if="job.taskNo">
                          <div class="card-icon">
                            <i class="el-icon-cpu"></i>
                          </div>
                          <div class="card-content">
                            <div class="card-title">设备信息</div>
                            <div class="card-details">
                              <div class="card-detail-item">
                                <span class="item-label">任务号:</span>
                                <span class="item-value">{{ job.taskNo }}</span>
                              </div>
                            </div>
                          </div>
                        </div>

                        <!-- 时间信息 - 简化设计 -->
                        <div class="info-card-simple time-simple">
                          <div class="time-item" v-if="job.cmdTime">
                            <span class="time-label"><i class="el-icon-video-play"></i> 执行:</span>
                            <span class="time-value">{{ job.cmdTime }}</span>
                          </div>
                          <div class="time-item" v-if="job.finishTime">
                            <span class="time-label"><i class="el-icon-circle-check"></i> 完成:</span>
                            <span class="time-value">{{ job.finishTime }}</span>
                          </div>
                        </div>
                      </div>

                      <!-- 备注信息区 -->
                      <div class="job-memo-section" v-if="job.memo">
                        <div class="memo-header">
                          <i class="el-icon-warning-outline"></i>
                          <span>备注信息</span>
                        </div>
                        <div class="memo-content">
                          <span v-if="job.memo && job.memo.length <= 100">{{ job.memo }}</span>
                          <span v-else>
                            {{ job.memo ? job.memo.substring(0, 100) + '...' : '' }}
                            <el-button type="text" size="mini" class="view-more-btn"
                              @click="showFullContent(job.memo, '任务信息')">
                              查看完整
                            </el-button>
                          </span>
                        </div>
                      </div>
                    </div>
                  </transition>
                </el-card>
                <!-- pathTimeLine 也要根据折叠状态显示 -->
                <transition name="job-detail-fade">
                  <pathTimeLine v-show="job.state !== 4 || isJobExpanded(job.id)" :jobId="job.id" class="path-timeline"></pathTimeLine>
                </transition>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 任务进度抽屉 -->
    <el-drawer title="任务进度详情" :visible.sync="drawer" direction="rtl" size="30%" class="job-progress-drawer">
      <div class="drawer-content">
        <!-- 执行条件 -->
        <div class="progress-section">
          <el-descriptions title="执行条件" :column="1" border class="progress-descriptions">
            <el-descriptions-item v-for="item in jobHandles" v-if="item.type == 1" :key="item.id" class="progress-item">
              <template slot="label">
                <i v-if="item.state == 0" class="el-icon-loading status-icon loading"></i>
                <i v-if="item.state == 1" class="el-icon-success status-icon success"></i>
                <i v-if="item.state == 2" class="el-icon-check status-icon completed"></i>
              </template>
              <span class="progress-name">{{ item.name }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 执行函数 -->
        <div class="progress-section">
          <el-descriptions title="执行函数" :column="1" border class="progress-descriptions">
            <el-descriptions-item v-for="item in jobHandles" v-if="item.type == 2" :key="item.id" class="progress-item">
              <template slot="label">
                <i v-if="item.state == 0" class="el-icon-loading status-icon loading"></i>
                <i v-if="item.state == 1" class="el-icon-success status-icon success"></i>
                <i v-if="item.state == 2" class="el-icon-check status-icon completed"></i>
              </template>
              <span class="progress-name">{{ item.name }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 成功条件 -->
        <div class="progress-section">
          <el-descriptions title="成功条件" :column="1" border class="progress-descriptions">
            <el-descriptions-item v-for="item in jobHandles" v-if="item.type == 3" :key="item.id" class="progress-item">
              <template slot="label">
                <i v-if="item.state == 0" class="el-icon-loading status-icon loading"></i>
                <i v-if="item.state == 1" class="el-icon-success status-icon success"></i>
                <i v-if="item.state == 2" class="el-icon-check status-icon completed"></i>
              </template>
              <span class="progress-name">{{ item.name }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 成功回调 -->
        <div class="progress-section">
          <el-descriptions title="成功回调" :column="1" border class="progress-descriptions">
            <el-descriptions-item v-for="item in jobHandles" v-if="item.type == 4" :key="item.id" class="progress-item">
              <template slot="label">
                <i v-if="item.state == 0" class="el-icon-loading status-icon loading"></i>
                <i v-if="item.state == 1" class="el-icon-success status-icon success"></i>
                <i v-if="item.state == 2" class="el-icon-check status-icon completed"></i>
              </template>
              <span class="progress-name">{{ item.name }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-drawer>

    <!-- 完整内容模态框 -->
    <el-dialog :title="contentDialog.title" :visible.sync="contentDialog.visible" width="60%"
      :before-close="handleCloseDialog" class="content-dialog">
      <div class="full-content">
        <div class="content-text">{{ contentDialog.content }}</div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="contentDialog.visible = false">关闭</el-button>
        <el-button type="primary" @click="copyContent">复制内容</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {
  listTaskInfo,
  getTaskInfo,
  delTaskInfo,
  addTaskInfo,
  updateTaskInfo,
} from "@/api/wcs-task/TaskInfo";
import { listJob, getJob, delJob, addJob, updateJob } from "@/api/wcs-task/job";
import request from "@/utils/request";
import pathTimeLine from "./pathTimeLine";
import { listJobHandle } from "@/api/wcs-task/jobHandle";
import { listTaskType } from "@/api/wcs-base/taskType";
import { listWareInfo } from "@/api/wcs-base/WareInfo";

export default {
  name: "Job",
  dicts: ["task_type", "task_state"],
  components: {
    pathTimeLine,
  },
  data() {
    return {
      jobHandles: [],
      drawer: false,
      // 已完成job的展开状态 { jobId: boolean }
      expandedJobs: {},
      // 内容模态框
      contentDialog: {
        visible: false,
        title: '',
        content: ''
      },
      jobStates: [
        { value: 0, label: "初始化", color: "#909399" },
        { value: 1, label: "满足执行条件", color: "#409EFF" },
        { value: 2, label: "执行中", color: "#E6A23C" },  // 橙色 - 执行中
        { value: 3, label: "满足成功条件", color: "#67C23A" },
        { value: 4, label: "成功", color: "#67C23A" },  // 绿色 - 已完成
      ],
      pathStates: [
        { value: 0, label: "初始化", color: "#909399" },
        { value: 1, label: "任务执行中", color: "#409EFF" },
        { value: 2, label: "任务完成", color: "#67C23A" },
      ],
      timer: null,
      showTaskInfo: true,
      taskNo: null,
      taskInfo: {}, // 初始化为空对象而非null
      taskTypes: [], // 任务类型列表
      wareInfos: [], // 仓库列表
      selectedWareCode: null, // 选中的仓库编码
      selectedTaskType: null, // 选中的任务类型
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 执行步骤表格数据
      taskList: [],
      jobList: [],
      jobTreeList: [], // 树形结构的job列表
      pathList: [],
      records: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskId: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        state: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  computed: {
    // 扁平化的job列表用于显示
    displayJobList() {
      if (!this.jobTreeList || this.jobTreeList.length === 0) {
        return [];
      }
      return this.flattenJobTree(this.jobTreeList);
    },
    // 排序后的日志记录（按billRecordId正序 - 从小到大）
    sortedRecords() {
      if (!this.records || this.records.length === 0) {
        return [];
      }
      // 按billRecordId升序排序
      return [...this.records].sort((a, b) => {
        const idA = a && a.billRecordId ? a.billRecordId : 0;
        const idB = b && b.billRecordId ? b.billRecordId : 0;
        return idA - idB;
      });
    }
  },
  created() {
    var taskId = this.$route.params.taskNo;
    this.getWareInfos(); // 获取仓库列表
    this.getTaskInfo(taskId);
    this.recordList(taskId);
    this.listNowJobList(taskId);
    // 初始化时如果有任务信息，设置选中的仓库和任务类型
    this.$nextTick(() => {
      if (this.taskInfo && this.taskInfo.wareCode) {
        this.selectedWareCode = this.taskInfo.wareCode;
        this.getTaskTypesByWareCode(this.taskInfo.wareCode);
        if (this.taskInfo.type) {
          this.selectedTaskType = this.taskInfo.type;
        }
      }
    });
    this.timer = setInterval(() => {
      this.queryParams.taskId = taskId;
      this.recordList(taskId);
      this.listNowJobList(taskId);
    }, 5000);
  },
  mounted() {
    // 初始加载后滚动到底部
    this.$nextTick(() => {
      this.scrollToBottom();
    });
  },
  beforeDestroy() {
    if (this.timer) {
      //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
  },
  methods: {
    // 获取仓库列表
    getWareInfos() {
      listWareInfo({ isDelete: 0, pageSize: 999 }).then((response) => {
        if (response.code == 200) {
          this.wareInfos = response.rows || [];
        }
      });
    },

    // 根据仓库编码获取任务类型
    getTaskTypesByWareCode(wareCode) {
      if (!wareCode) {
        this.taskTypes = [];
        return;
      }
      listTaskType({ wareCode: wareCode, delFlag: 0, pageSize: 999 }).then((response) => {
        if (response.code == 200) {
          this.taskTypes = response.rows || [];
        }
      });
    },

    // 仓库选择变化处理
    handleWareCodeChange(wareCode) {
      // 清空任务类型选择
      this.selectedTaskType = null;
      // 根据仓库获取任务类型列表
      this.getTaskTypesByWareCode(wareCode);
    },

    // 获取所有任务类型（保留兼容性）
    getAllTaskTypes() {
      listTaskType({ delFlag: 0 }).then((response) => {
        if (response.code == 200) {
          this.taskTypes = response.rows;
        }
      });
    },

    // 根据任务类型code获取任务类型名称
    getTaskTypeName(typeCode) {
      const taskType = this.taskTypes.find(item => item.code == typeCode);
      return taskType ? taskType.name : '未知类型';
    },

    // 获取任务状态对应的样式类
    getJobStateClass(state) {
      const stateMap = {
        0: 'state-init',
        1: 'state-condition-met',
        2: 'state-executing',
        3: 'state-success-condition',
        4: 'state-success'
      }
      return stateMap[state] || 'state-init'
    },

    // 切换job的展开/折叠状态
    toggleJobExpand(jobId) {
      this.$set(this.expandedJobs, jobId, !this.expandedJobs[jobId]);
    },

    // 判断job是否展开
    isJobExpanded(jobId) {
      return this.expandedJobs[jobId] === true;
    },

    // 获取路径状态文本
    getJobPathStatus(state) {
      const statusMap = {
        0: '待执行',
        1: '准备中',
        2: '执行中',
        3: '即将完成',
        4: '已完成'
      }
      return statusMap[state] || '未知状态'
    },

    // 显示完整内容
    showFullContent(content, title) {
      this.contentDialog = {
        visible: true,
        title: title,
        content: content || ''
      }
    },

    // 关闭模态框
    handleCloseDialog() {
      this.contentDialog.visible = false
    },

    // 复制内容
    copyContent() {
      if (this.contentDialog.content) {
        // 创建临时文本区域
        const textArea = document.createElement('textarea')
        textArea.value = this.contentDialog.content
        document.body.appendChild(textArea)
        textArea.select()

        try {
          document.execCommand('copy')
          this.$message.success('内容已复制到剪贴板')
        } catch (err) {
          this.$message.error('复制失败，请手动复制')
        }

        document.body.removeChild(textArea)
      }
    },

    getJobHandles(jobId) {
      var query = { jobId: jobId, pageSize: 999, }
      listJobHandle(query).then((response) => {
        if (response.code == 200) {
          this.jobHandles = response.rows
          this.drawer = true
        }
      })
    },
    handleClose() {
      const obj = { path: "/taskMonitor/TaskInfo" };
      this.$tab.closeOpenPage(obj);
    },


    cmd(jobId) {
      request({
        url: "/wcs-task/job/cmd",
        method: "get",
        params: { id: jobId },
      }).then((response) => {
        this.loading = false;
        if (response.code == 200) {
          this.$message.msgSuccess("成功");
        } else {
          this.$message.error(response.message);
        }
      });
    },

    successPre(jobId) {
      request({
        url: "/wcs-task/job/successPre",
        method: "get",
        params: { id: jobId },
      }).then((response) => {
        this.loading = false;
        if (response.code == 200) {
          this.$message.msgSuccess("成功");
        } else {
          this.$message.error(response.message);
        }
      });
    },

    /** 查询执行步骤列表 */
    getTaskInfo(taskId) {
      request({
        url: "/wcs-task/TaskInfo/" + taskId,
        method: "get",
        params: {},
      }).then((response) => {
        this.loading = false;
        if (response.code == 200) {
          this.taskInfo = response.data || {};
          // 设置选中的仓库和任务类型
          if (this.taskInfo.wareCode) {
            this.selectedWareCode = this.taskInfo.wareCode;
            this.getTaskTypesByWareCode(this.taskInfo.wareCode);
            if (this.taskInfo.type) {
              this.selectedTaskType = this.taskInfo.type;
            }
          }
          this.recordList(taskId);
          this.listNowJobList(taskId);
        } else {
          this.$modal.msgError(response.msg || "获取任务失败");
          this.taskInfo = {};
        }
      }).catch(() => {
        this.loading = false;
        this.taskInfo = {};
      });
    },

    recordList(taskId) {
      request({
        url: "/bill_record/findByBillNo",
        method: "get",
        params: { billNo: taskId },
      }).then((response) => {
        if (response.code == 200) {
          this.records = response.data || [];
          this.loading = false;
          // 滚动到最新日志
          this.$nextTick(() => {
            this.scrollToBottom();
          });
        } else {
          this.records = [];
          this.loading = false;
        }
      }).catch(() => {
        this.records = [];
        this.loading = false;
      });
    },

    // 滚动终端到底部
    scrollToBottom() {
      const terminalContainer = this.$refs.terminalContainer;
      if (terminalContainer) {
        // 平滑滚动到底部
        terminalContainer.scrollTo({
          top: terminalContainer.scrollHeight,
          behavior: 'smooth'
        });
      }
    },
    listNowJobList(taskId) {
      request({
        url: "/wcs-task/job/findByTaskId",
        method: "get",
        params: { taskId: taskId },
      }).then((response) => {
        if (response.code == 200) {
          this.jobList = response.data || [];
          // 构建树形结构
          this.buildJobTree();
          this.loading = false;
        } else {
          this.jobList = [];
          this.jobTreeList = [];
          this.loading = false;
        }
      }).catch(() => {
        this.jobList = [];
        this.jobTreeList = [];
        this.loading = false;
      });
    },

    // 构建Job树形结构
    buildJobTree() {
      if (!this.jobList || this.jobList.length === 0) {
        this.jobTreeList = [];
        return;
      }

      // 创建id到job的映射
      const jobMap = {};
      this.jobList.forEach(job => {
        if (job && job.id) {
          jobMap[job.id] = { ...job, children: [] };
        }
      });

      // 构建树形结构
      const rootJobs = [];
      this.jobList.forEach(job => {
        if (!job || !job.id) {
          return;
        }
        const jobNode = jobMap[job.id];
        if (job.lastJobId && jobMap[job.lastJobId]) {
          // 有父节点，添加到父节点的children中
          jobMap[job.lastJobId].children.push(jobNode);
        } else {
          // 没有父节点，是根节点
          rootJobs.push(jobNode);
        }
      });

      // 过滤未完成判断节点的子节点
      this.jobTreeList = this.filterJobTree(rootJobs);
    },

    // 过滤树形结构：判断节点未完成时隐藏其子节点
    filterJobTree(jobNodes) {
      if (!jobNodes || jobNodes.length === 0) {
        return [];
      }
      return jobNodes.filter(job => job && job.id).map(job => {
        const filteredJob = { ...job };
        
        // 判断是否是分叉节点（有多个子节点）
        const isBranchNode = job.children && job.children.length > 1;
        
        if (isBranchNode) {
          // 如果是分叉节点（判断节点），检查是否已完成（state === 4）
          if (job.state === 4) {
            // 已完成，递归过滤子节点
            filteredJob.children = this.filterJobTree(job.children);
          } else {
            // 未完成，隐藏所有子节点
            filteredJob.children = [];
          }
        } else if (job.children && job.children.length > 0) {
          // 普通节点，继续递归过滤
          filteredJob.children = this.filterJobTree(job.children);
        }
        
        return filteredJob;
      });
    },

    // 递归获取所有要显示的job（扁平化树形结构）
    flattenJobTree(jobNodes, level = 0) {
      if (!jobNodes || jobNodes.length === 0) {
        return [];
      }
      let result = [];
      jobNodes.forEach(job => {
        if (job && job.id) {
          result.push({ ...job, level });
          if (job.children && job.children.length > 0) {
            result = result.concat(this.flattenJobTree(job.children, level + 1));
          }
        }
      });
      return result;
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        taskId: null,
        taskNo: null,
        jobIndex: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        createTime: null,
        state: null,
        cmdTime: null,
        finishTime: null,
      };
      this.resetForm("form");
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getJob(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改执行步骤";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateJob(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addJob(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除执行步骤编号为"' + ids + '"的数据项？')
        .then(function () {
          return delJob(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-task/job/export",
        {
          ...this.queryParams,
        },
        `job_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
<style lang="scss" scoped>
// 主容器样式 - 科技暗色主题
.task-detail-container {
  padding: 16px;
  background: linear-gradient(135deg, #1e1e1e 0%, #2d2d30 100%);
  min-height: 100vh;
}

// 卡片头部样式 - 紧凑版
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  margin-bottom: 0;

  .header-left {
    display: flex;
    align-items: center;

    i {
      margin-right: 6px;
      font-size: 14px;
      color: #409EFF;
    }

    .card-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      line-height: 1.2;
    }

    .task-id {
      font-size: 11px;
      color: #909399;
      margin-left: 8px;
      font-weight: 400;
    }
  }

  .header-actions {
    display: flex;
    gap: 8px;

    .action-btn {
      border-radius: 4px;
      font-weight: 500;
      font-size: 12px;
      padding: 6px 12px;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
      }
    }
  }
}

// 深色科技卡片样式
.tech-dark-card {
  margin-bottom: 12px;
  border-radius: 10px;
  border: 1px solid #3e3e42;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  background: rgba(45, 45, 48, 0.95);
  backdrop-filter: blur(10px);

  ::v-deep .el-card__header {
    background: #2d2d30;
    border-bottom: 1px solid #3e3e42;
    padding: 10px 14px;
  }

  ::v-deep .el-card__body {
    background: rgba(37, 37, 38, 0.8);
    padding: 10px 14px;
  }

  // 科技风格头部
  .tech-header {
    .header-left {
      i {
        color: #4EC9B0;
        font-size: 14px;
      }

      .card-title {
        color: #CCCCCC;
        font-family: 'Consolas', 'Monaco', 'Microsoft YaHei', sans-serif;
        font-weight: 600;
        font-size: 14px;
      }

      .tech-subtitle {
        color: #6A9955;
        font-size: 9px;
        font-weight: 500;
        margin-left: 10px;
        font-family: 'Consolas', 'Monaco', monospace;
        letter-spacing: 0.5px;
      }
    }

    .tech-btn {
      border: 1px solid #3e3e42;
      background: rgba(64, 158, 255, 0.1);
      color: #409EFF;
      padding: 6px 14px !important;
      font-size: 12px !important;

      &:hover {
        background: rgba(64, 158, 255, 0.2);
        border-color: #409EFF;
        box-shadow: 0 0 10px rgba(64, 158, 255, 0.3);
      }

      &.el-button--warning {
        background: rgba(230, 162, 60, 0.1);
        color: #E6A23C;
        border-color: rgba(230, 162, 60, 0.3);

        &:hover {
          background: rgba(230, 162, 60, 0.2);
          border-color: #E6A23C;
          box-shadow: 0 0 10px rgba(230, 162, 60, 0.3);
        }
      }
    }
  }
}

// 任务信息卡片样式 - 全新现代化设计
.task-info-card {
  margin-bottom: 12px;
  border-radius: 10px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;

  .task-info-content {
    padding: 0;
  }

  // 现代化布局（两行版：第一行核心信息，第二行路径信息）
  .task-info-modern {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 0;

    // 分隔符样式（横向分隔线）
    .info-divider {
      width: 100%;
      height: 1px;
      background: linear-gradient(90deg, transparent 0%, #3e3e42 20%, #3e3e42 80%, transparent 100%);
      flex-shrink: 0;
      margin: 4px 0;
    }

    // 信息区块通用样式
    .info-section {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 0 0 auto;

      .section-title {
        display: none; // 隐藏标题，更紧凑
      }
    }

    // 核心信息区 - 横向紧凑布局（第一行）
    .core-section {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 1;
      flex-wrap: wrap;
      width: 100%;

      .info-cards-grid {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;

        .info-mini-card {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 6px 10px;
          background: rgba(30, 30, 30, 0.6);
          border: 1px solid #3e3e42;
          border-radius: 6px;
          transition: all 0.3s ease;
          backdrop-filter: blur(5px);
          flex: 0 0 auto;

          &:hover {
            box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
            border-color: #409EFF;
            background: rgba(64, 158, 255, 0.1);
          }

          .card-icon-wrapper {
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 6px;
            flex-shrink: 0;

            i {
              font-size: 14px;
              color: white;
            }

            &.blue {
              background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
            }

            &.green {
              background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
            }

            &.orange {
              background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
            }

            &.cyan {
              background: linear-gradient(135deg, #00BCD4 0%, #4DD0E1 100%);
            }

            &.purple {
              background: linear-gradient(135deg, #909399 0%, #b3b6bb 100%);
            }
          }

          .card-text {
            display: flex;
            align-items: center;
            gap: 6px;

            .card-label {
              font-size: 10px;
              color: #858585;
              font-weight: 500;
              font-family: 'Consolas', 'Monaco', monospace;
            }

          .card-value {
            font-size: 12px;
            font-weight: 700;
            color: #CCCCCC;
            font-family: 'Courier New', 'Consolas', monospace;
          }

          // 下拉框样式
          .ware-select,
          .task-type-select {
            min-width: 120px;
            margin-left: 6px;

            ::v-deep .el-input__inner {
              background: rgba(30, 30, 30, 0.8);
              border: 1px solid #3e3e42;
              color: #CCCCCC;
              font-size: 12px;
              height: 24px;
              line-height: 24px;
              padding: 0 8px;

              &:hover {
                border-color: #409EFF;
              }

              &:focus {
                border-color: #409EFF;
              }
            }

            ::v-deep .el-input__suffix {
              .el-input__suffix-inner {
                .el-select__caret {
                  color: #858585;
                }
              }
            }
          }

          .task-type-select {
            ::v-deep .el-input.is-disabled .el-input__inner {
              background: rgba(30, 30, 30, 0.4);
              border-color: #3e3e42;
              color: #606266;
              cursor: not-allowed;
            }
          }
        }
      }
    }
  }

    // 路径信息区 - 独立行展示（第二行）
    .path-section {
      display: flex;
      align-items: center;
      gap: 0;
      padding: 10px 16px;
      background: rgba(30, 30, 30, 0.6);
      border-radius: 8px;
      border: 1px solid #3e3e42;
      flex: 0 0 auto;
      width: 100%;
      justify-content: center;

      .task-path-visual {
        display: flex;
        align-items: center;
        gap: 0;
        padding: 0;
        background: transparent;
        border-radius: 0;
        position: relative;
        border: none;
        justify-content: center;
        width: auto;

        .path-point {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 0;
          background: transparent;
          border-radius: 0;
          box-shadow: none;
          flex: 0 0 auto;
          min-width: auto;
          border: none;

          .point-icon {
            width: 20px;
            height: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 0;
            flex-shrink: 0;

            i {
              font-size: 16px;
            }
          }

          &.start-point .point-icon {
            background: transparent;
            color: #67C23A;
          }

          &.end-point .point-icon {
            background: transparent;
            color: #409EFF;
          }

          .point-info {
            display: flex;
            align-items: center;
            gap: 6px;

            .point-label {
              font-size: 10px;
              color: #858585;
              font-weight: 500;
              font-family: 'Consolas', 'Monaco', monospace;
            }

            .point-value {
              font-size: 14px;
              font-weight: 700;
              color: #4EC9B0;
              font-family: 'Courier New', monospace;
            }
          }
        }

        .path-arrow-line {
          display: flex;
          align-items: center;
          margin: 0 16px;

          .arrow-line-inner {
            width: 60px;
            height: 2px;
            background: linear-gradient(90deg, #409EFF 0%, #409EFF 50%, transparent 50%, transparent 100%);
            background-size: 10px 2px;
            animation: flow 1.5s linear infinite;
            position: relative;

            &::after {
              content: '';
              position: absolute;
              right: -5px;
              top: 50%;
              transform: translateY(-50%);
              width: 0;
              height: 0;
              border-left: 8px solid #409EFF;
              border-top: 5px solid transparent;
              border-bottom: 5px solid transparent;
            }
          }

          .arrow-text {
            display: none;
          }
        }
      }
    }

    // 托盘信息区 - 独立行展示（第三行，如果有）
    .pallet-section {
      display: flex;
      align-items: center;
      gap: 10px;
      flex: 0 0 auto;
      width: 100%;
      justify-content: flex-start;

      .pallet-info-list {
        display: flex;
        align-items: center;
        gap: 10px;
        flex-wrap: wrap;

        .pallet-info-item {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 6px 10px;
          background: rgba(30, 30, 30, 0.6);
          border: 1px solid rgba(230, 162, 60, 0.3);
          border-radius: 6px;
          flex: 0 0 auto;

          .pallet-label {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 10px;
            color: #858585;
            font-weight: 500;
            font-family: 'Consolas', 'Monaco', monospace;

            i {
              font-size: 12px;
              color: #E6A23C;
            }
          }

          .pallet-value {
            font-size: 12px;
            font-weight: 700;
            color: #CCCCCC;
            font-family: 'Courier New', monospace;
          }
        }
      }
    }
  }

  // 旧版表单样式（保留兼容）
  .task-info-form {
    .form-item {
      margin-bottom: 6px;

      ::v-deep .el-form-item__label {
        font-weight: 600;
        color: #606266;
        font-size: 12px;
        line-height: 1.2;
        padding-bottom: 2px;
        width: 80px !important;
        text-align: right;
        margin-right: 8px;
      }

      .form-value {
        color: #303133;
        font-size: 12px;
        font-weight: 500;
        padding: 4px 8px;
        background: #f8f9fa;
        border-radius: 4px;
        border: 1px solid #e4e7ed;
        min-height: 24px;
        display: flex;
        align-items: center;
        line-height: 1.2;
        word-wrap: break-word;
        word-break: break-all;
      }
    }
  }
}

// 主要内容区域 - 左右分栏布局
.main-content {
  margin-top: 0;
  display: flex;
  min-height: 50vh;

  .left-panel {
    display: flex;
    flex-direction: column;
    width: 100%;

    .timeline-card {
      height: auto;
      display: flex;
      flex-direction: column;

      ::v-deep .el-card__body {
        flex: 1;
        display: flex;
        flex-direction: column;
        padding: 12px;
      }
    }
  }

  .right-panel {
    display: flex;
    flex-direction: column;

    .job-list-card {
      height: auto;
      display: flex;
      flex-direction: column;

      ::v-deep .el-card__body {
        flex: 1;
        display: flex;
        flex-direction: column;
      }
    }
  }
}

// 终端卡片样式
.terminal-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  height: fit-content;
  background: #1e1e1e;
  overflow: hidden;

  ::v-deep .el-card__header {
    background: #2d2d30;
    border-bottom: 1px solid #3e3e42;
    padding: 10px 16px;
  }

  ::v-deep .el-card__body {
    background: #1e1e1e;
    padding: 0;
  }

  .terminal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 8px;

      i {
        color: #4EC9B0;
        font-size: 16px;
      }

      .card-title {
        color: #CCCCCC;
        font-size: 14px;
        font-weight: 600;
        font-family: 'Consolas', 'Monaco', monospace;
      }

      .terminal-subtitle {
        color: #6A9955;
        font-size: 10px;
        font-weight: 500;
        margin-left: 8px;
        font-family: 'Consolas', 'Monaco', monospace;
        letter-spacing: 1px;
      }

      .terminal-count {
        color: #569CD6;
        font-size: 11px;
        font-weight: 600;
        margin-left: 12px;
        font-family: 'Consolas', 'Monaco', monospace;
        background: rgba(86, 156, 214, 0.15);
        padding: 2px 8px;
        border-radius: 10px;
        border: 1px solid rgba(86, 156, 214, 0.3);
      }
    }

    .terminal-controls {
      display: flex;
      gap: 8px;
      align-items: center;

      .terminal-dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        display: inline-block;
        box-shadow: inset 0 -2px 4px rgba(0, 0, 0, 0.3);

        &.terminal-dot-red {
          background: #ff5f56;
        }

        &.terminal-dot-yellow {
          background: #ffbd2e;
        }

        &.terminal-dot-green {
          background: #27c93f;
        }
      }
    }
  }

  .terminal-container {
    background: #1e1e1e;
    padding: 16px;
    min-height: 400px;
    max-height: 600px;
    overflow-y: auto;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.6;
    scroll-behavior: smooth;
    position: relative;

    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-track {
      background: #252526;
      border-radius: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: #424242;
      border-radius: 4px;
      transition: background 0.3s ease;

      &:hover {
        background: #4e4e4e;
      }
    }
  }

  .terminal-output {
    .terminal-line {
      margin-bottom: 8px;
      display: flex;
      flex-wrap: wrap;
      align-items: flex-start;
      animation: terminalFadeIn 0.3s ease-in;

      &:hover {
        background: rgba(255, 255, 255, 0.03);
        border-radius: 4px;
        padding: 2px 4px;
        margin: 2px -4px;
      }

      .terminal-line-number {
        color: #858585;
        font-size: 11px;
        margin-right: 8px;
        user-select: none;
        flex-shrink: 0;
        font-family: 'Consolas', 'Monaco', monospace;
        opacity: 0.6;
      }

      .terminal-prompt {
        color: #4EC9B0;
        font-weight: 700;
        margin-right: 8px;
        user-select: none;
        flex-shrink: 0;
      }

      .terminal-id {
        color: #569CD6;
        font-size: 11px;
        margin-right: 8px;
        flex-shrink: 0;
        font-weight: 600;
        font-family: 'Consolas', 'Monaco', monospace;
      }

      .terminal-timestamp {
        color: #858585;
        font-size: 11px;
        margin-right: 8px;
        flex-shrink: 0;
      }

      .terminal-text {
        color: #D4D4D4;
        word-wrap: break-word;
        word-break: break-all;
        flex: 1;
        min-width: 0;

        &.terminal-info {
          color: #6A9955;
          font-style: italic;
        }

        &.terminal-error {
          color: #F48771;
        }

        &.terminal-warning {
          color: #DCDCAA;
        }

        &.terminal-success {
          color: #4EC9B0;
        }
      }

      .terminal-more {
        color: #569CD6;
        cursor: pointer;
        text-decoration: underline;
        margin-left: 4px;
        font-size: 11px;

        &:hover {
          color: #9CDCFE;
          text-decoration: none;
        }
      }
    }

    .terminal-cursor {
      display: inline-block;
      width: 8px;
      height: 16px;
      background: #4EC9B0;
      animation: terminalBlink 1s step-end infinite;
      margin-left: 4px;
      vertical-align: middle;
    }
  }
}

// 科技边框卡片样式
.tech-border-card {
  border-radius: 12px;
  border: 1px solid #3e3e42;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  height: fit-content;
  background: rgba(45, 45, 48, 0.95);
  backdrop-filter: blur(10px);

  ::v-deep .el-card__header {
    background: #2d2d30;
    border-bottom: 1px solid #3e3e42;
    padding: 12px 16px;
  }

  ::v-deep .el-card__body {
    background: rgba(37, 37, 38, 0.8);
    padding: 16px;
  }

  // 科技风格头部
  .tech-header {
    .header-left {
      i {
        color: #4EC9B0;
        font-size: 16px;
      }

      .card-title {
        color: #CCCCCC;
        font-family: 'Consolas', 'Monaco', 'Microsoft YaHei', sans-serif;
        font-weight: 600;
      }

      .tech-subtitle {
        color: #6A9955;
        font-size: 10px;
        font-weight: 500;
        margin-left: 12px;
        font-family: 'Consolas', 'Monaco', monospace;
        letter-spacing: 1px;
      }

      .task-id-badge {
        color: #569CD6;
        font-size: 11px;
        font-weight: 600;
        margin-left: 12px;
        font-family: 'Courier New', monospace;
        background: rgba(86, 156, 214, 0.15);
        padding: 2px 8px;
        border-radius: 10px;
        border: 1px solid rgba(86, 156, 214, 0.3);
      }
    }
  }
}

// 时间轴卡片样式 - 紧凑版
.timeline-card,
.job-list-card {
  border-radius: 8px;
  border: none;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  height: fit-content;

  .timeline-container {
    flex: 1;
    overflow: visible;
    padding: 0;
    height: auto;
    max-height: none;
  }

  .job-timeline-container {
    flex: 1;
    overflow-y: visible;
    overflow-x: hidden;
    padding: 0 4px;

    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 2px;
    }

    &::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 2px;

      &:hover {
        background: #a8a8a8;
      }
    }
  }
}

// 时间轴项目样式 - 紧凑版
.timeline-item,
.job-timeline-item {
  margin-bottom: 6px;

  ::v-deep .el-timeline-item__timestamp {
    color: #909399;
    font-size: 9px;
    font-weight: 500;
    line-height: 1.2;
    margin-bottom: 3px;
  }

  .timeline-content {
    .record-content {
      background: #f8f9fa;
      padding: 5px 8px;
      border-radius: 4px;
      border-left: 2px solid #409EFF;
      color: #303133;
      font-size: 11px;
      line-height: 1.3;
      word-wrap: break-word;
      word-break: break-all;
    }
  }
}

// 任务卡片样式 - 清晰简洁风格
.job-card {
  margin-bottom: 16px;
  border-radius: 10px;
  border: 2px solid #3e3e42;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  background: rgba(30, 30, 30, 0.95);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);

  &:hover {
    box-shadow: 0 6px 20px rgba(64, 158, 255, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.1);
    transform: translateY(-2px);
    border-color: #409EFF;
    background: rgba(30, 30, 30, 1);
  }

  // 已完成状态的卡片（暗淡显示）
  &.job-state-4 {
    opacity: 0.65;
    background: rgba(30, 30, 30, 0.6);
    border-color: rgba(62, 62, 66, 0.4);

    &:hover {
      opacity: 0.8;
      background: rgba(30, 30, 30, 0.75);
      transform: translateY(-1px);
    }

    .job-status-bar.state-success {
      background: #4EC9B0;
      opacity: 0.4;
    }

    .job-name {
      opacity: 0.7;
    }

    .job-state-tag {
      opacity: 0.8;
    }
  }

  // 执行中状态的卡片（高亮显示）
  &.job-state-2 {
    border-color: rgba(103, 194, 58, 0.4);
    box-shadow: 0 4px 12px rgba(103, 194, 58, 0.25), inset 0 1px 0 rgba(255, 255, 255, 0.05);
    
    .job-status-bar.state-executing {
      box-shadow: 0 0 10px rgba(103, 194, 58, 0.6);
    }
  }

  // 顶部状态条
  .job-status-bar {
    height: 3px;
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 10;
    overflow: hidden;

    &.state-init {
      background: linear-gradient(90deg, #909399 0%, #b3b6bb 100%);
    }

    &.state-condition-met {
      background: linear-gradient(90deg, #409EFF 0%, #66b1ff 100%);

      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.4) 50%, transparent 100%);
        animation: shimmer 2s ease-in-out infinite;
      }
    }

    &.state-executing {
      background: linear-gradient(90deg, #67C23A 0%, #85CE61 100%);
      animation: ledBreathe 2s ease-in-out infinite;

      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.6) 50%, transparent 100%);
        animation: shimmer 1.5s ease-in-out infinite;
      }
    }

    &.state-success-condition {
      background: linear-gradient(90deg, #85CE61 0%, #95d475 100%);
    }

    &.state-success {
      background: linear-gradient(90deg, #4EC9B0 0%, #67C23A 100%);
      opacity: 0.6;
    }
  }

  ::v-deep .el-card__header {
    padding: 12px 16px;
    border-bottom: 1px solid rgba(64, 158, 255, 0.2);
    background: rgba(45, 45, 48, 0.8);
  }

  ::v-deep .el-card__body {
    padding: 16px;
    background: transparent;
  }

  // 判断节点特殊样式
  &.branch-node {
    border: 2px solid #E6A23C;
    box-shadow: 0 2px 12px rgba(230, 162, 60, 0.2);

    .job-status-bar {
      height: 5px;
    }
  }

  .job-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0;
    margin-bottom: 0;

    .job-title {
      display: flex;
      align-items: center;
      gap: 8px;

      .job-badge {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 0;
        background: transparent;
        color: #409EFF;

        i {
          font-size: 14px;
        }

        .job-id {
          font-size: 13px;
          font-weight: 700;
          letter-spacing: 0.5px;
          font-family: 'Courier New', monospace;
        }

        .branch-indicator {
          font-size: 10px;
          background: rgba(230, 162, 60, 0.2);
          color: #E6A23C;
          padding: 2px 6px;
          border-radius: 4px;
          font-weight: 600;
          border: 1px solid rgba(230, 162, 60, 0.4);
        }

        // 判断节点徽章样式
        &.branch-badge {
          color: #E6A23C;

          i {
            animation: rotate-icon 3s linear infinite;
          }
        }
      }

      .job-info {
        display: flex;
        flex-direction: column;
        gap: 4px;

      .job-name {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
        line-height: 1.2;
        }

        .job-state-tag {
          display: inline-block;
          padding: 3px 10px;
          border-radius: 10px;
          font-size: 10px;
          font-weight: 600;
          color: white;
          line-height: 1;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
        }
      }

      // 新的一行式布局
      .job-info-inline {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;

        .job-name {
          font-size: 14px;
          font-weight: 600;
          color: #CCCCCC;
          line-height: 1.2;
        }

        .job-state-tag {
          display: inline-flex;
          align-items: center;
          gap: 4px;
          padding: 4px 10px;
          border-radius: 4px;
          font-size: 11px;
          font-weight: 600;
          color: white;
          line-height: 1;
          white-space: nowrap;
          font-family: 'Consolas', 'Monaco', monospace;

          i {
            font-size: 11px;

            &.el-icon-loading {
              animation: rotating 2s linear infinite;
            }
          }
        }
      }
    }

    .job-actions {
      display: flex;
      gap: 8px;

      .action-btn-small {
        border-radius: 4px;
        font-weight: 500;
        font-size: 11px;
        padding: 6px 12px;
        transition: all 0.3s ease;
        border: 1px solid rgba(64, 158, 255, 0.3);
        background: rgba(64, 158, 255, 0.1);

        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
          background: rgba(64, 158, 255, 0.2);
          border-color: #409EFF;
        }

        &:active {
          transform: translateY(0);
        }

        // 展开/折叠按钮特殊样式
        &.el-button--info {
          background: rgba(144, 147, 153, 0.15) !important;
          border-color: rgba(144, 147, 153, 0.3) !important;
          color: #909399 !important;

          &:hover {
            background: rgba(144, 147, 153, 0.25) !important;
            border-color: #909399 !important;
            box-shadow: 0 2px 8px rgba(144, 147, 153, 0.3) !important;
          }
        }
      }
    }
  }

  // 路径信息区 - 简化代码框风格
  .job-path-section {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 0 0 12px 0;
    padding: 12px 14px;
    background: rgba(45, 45, 48, 0.5);
    border-radius: 8px;
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(64, 158, 255, 0.15);
    border-left: 3px solid #409EFF;
    transition: all 0.3s ease;

    // 执行中状态
    &.path-executing {
      border-left-color: #67C23A;
      background: rgba(103, 194, 58, 0.05);
      animation: pathPulse 2s ease-in-out infinite;
    }

    // 已完成状态
    &.path-completed {
      border-left-color: #4EC9B0;
      background: rgba(78, 201, 176, 0.05);
      opacity: 0.7;

      .arrow-line {
        background: #4EC9B0 !important;
        animation: none !important;
      }

      .arrow-icon {
        color: #4EC9B0 !important;
        animation: none !important;
      }

      .location-code {
        color: #858585 !important;
      }
    }

    .path-location {
      flex: 0 0 auto;
      display: flex;
      align-items: center;
      gap: 8px;

      .location-icon {
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        font-size: 14px;
        opacity: 0.7;

        i {
          font-size: 14px;
        }
      }

      &.from-location .location-icon {
        color: #67C23A;
      }

      &.to-location .location-icon {
        color: #409EFF;
      }

      .location-info {
        display: flex;
        flex-direction: column;
        gap: 1px;

        .location-label {
          font-size: 9px;
          color: #858585;
          font-weight: 500;
          text-transform: uppercase;
          font-family: 'Consolas', 'Monaco', monospace;
        }

        .location-code {
          font-size: 12px;
          font-weight: 700;
          color: #4EC9B0;
          letter-spacing: 0.3px;
          font-family: 'Courier New', monospace;
        }
      }
    }

    .path-arrow {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      margin: 0 16px;

      .arrow-line {
        width: 100%;
        height: 2px;
        background: #409EFF;
        border-radius: 1px;
        transition: all 0.3s ease;

        // 只有带动画类的才有动画效果
        &.arrow-animated {
          background: linear-gradient(90deg, #409EFF 0%, #409EFF 50%, transparent 50%, transparent 100%);
          background-size: 12px 2px;
          animation: flow 1.5s linear infinite;
        }
      }

      .arrow-icon {
        font-size: 16px;
        color: #409EFF;
        margin-left: 8px;
        transition: all 0.3s ease;

        // 已完成图标样式
        &.icon-completed {
          color: #4EC9B0;
        }
      }

      .path-status {
        display: none;
      }
    }
  }

  // 信息卡片组（简洁版）
  .job-info-cards {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin: 12px 0 0 0;
    padding-top: 12px;
    border-top: 1px solid rgba(64, 158, 255, 0.15);

    // 简洁版卡片样式（深色）
    .info-card-simple {
      padding: 8px 12px;
      background: transparent;
      border: none;
      border-radius: 6px;
      transition: all 0.3s ease;
      flex: 0 0 auto;

      &:hover {
        background: rgba(64, 158, 255, 0.08);
      }

      // 托盘信息简洁样式
      &.pallet-simple {
        display: flex;
        align-items: center;
        gap: 8px;

        .card-label {
          font-size: 11px;
          color: #858585;
          font-weight: 500;
          display: flex;
          align-items: center;
          gap: 4px;

          i {
            font-size: 13px;
            color: #4EC9B0;
          }
        }

        .card-value {
          font-size: 12px;
          color: #4EC9B0;
          font-weight: 700;
          font-family: 'Courier New', monospace;
        }

        .card-extra {
          display: none;
        }
      }

      // 时间信息简洁样式
      &.time-simple {
        display: flex;
        align-items: center;
        gap: 16px;

        .time-item {
          display: flex;
          align-items: center;
          gap: 6px;

          .time-label {
            font-size: 11px;
            color: #858585;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 4px;

            i {
              font-size: 12px;

              &.el-icon-video-play {
                color: #67C23A;
              }

              &.el-icon-circle-check {
                color: #409EFF;
              }
            }
          }

          .time-value {
            font-size: 11px;
            color: #CCCCCC;
            font-weight: 600;
            font-family: 'Courier New', monospace;
          }
        }
      }
    }

    .info-card {
      display: flex;
      align-items: flex-start;
      gap: 10px;
      padding: 10px 12px;
      border-radius: 8px;
      transition: all 0.3s ease;
      cursor: pointer;
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        opacity: 0;
        transition: opacity 0.3s ease;
        pointer-events: none;
      }

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);

        &::before {
          opacity: 1;
        }
      }

      &.pallet-card {
        background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
        border: 1px solid #90CAF9;

        .card-icon {
          background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
          color: white;
        }
      }

      &.device-card {
        background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
        border: 1px solid #A5D6A7;

        .card-icon {
          background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
          color: white;
        }
      }

      &.time-card {
        background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 100%);
        border: 1px solid #FFCC80;

        .card-icon {
          background: linear-gradient(135deg, #FF9800 0%, #F57C00 100%);
          color: white;
        }
      }

      .card-icon {
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8px;
        flex-shrink: 0;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);

        i {
          font-size: 18px;
        }
      }

      .card-content {
        flex: 1;

        .card-title {
          font-size: 11px;
          font-weight: 700;
          color: #303133;
          margin-bottom: 6px;
          text-transform: uppercase;
          letter-spacing: 0.3px;
        }

        .card-details {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .card-detail-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 11px;

            .item-label {
              color: #606266;
              font-weight: 500;
            }

            .item-value {
              color: #303133;
              font-weight: 600;
              flex: 1;
              word-break: break-all;
            }
          }
        }
      }
    }
  }

  // 备注信息区（简洁版）
  .job-memo-section {
    margin-top: 12px;
    padding: 10px 12px;
    background: rgba(230, 162, 60, 0.1);
    border-radius: 6px;
    border-left: 3px solid #E6A23C;

    .memo-header {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 6px;
      font-size: 11px;
      font-weight: 600;
      color: #E6A23C;

      i {
        font-size: 13px;
      }
    }

    .memo-content {
      font-size: 11px;
      line-height: 1.5;
      color: #CCCCCC;
      font-weight: 400;
    }
  }
}  // 结束 .job-card

// 路径时间轴样式 - 紧凑版
.path-timeline {
  margin-top: 6px;
  padding-left: 12px;
  border-left: 2px solid #e4e7ed;
}

// 抽屉样式 - 科技暗色主题
.job-progress-drawer {
  ::v-deep .el-drawer__wrapper {
    background-color: rgba(0, 0, 0, 0.7) !important;
  }

  ::v-deep .el-drawer {
    background: rgba(30, 30, 30, 0.98) !important;
    border-left: 2px solid #3e3e42 !important;
    box-shadow: -4px 0 32px rgba(0, 0, 0, 0.5) !important;
  }

  ::v-deep .el-drawer__header {
    padding: 16px 20px !important;
    margin-bottom: 0 !important;
    border-bottom: 1px solid rgba(64, 158, 255, 0.2) !important;
    background: rgba(45, 45, 48, 0.8) !important;

    .el-drawer__title {
      font-size: 16px !important;
      font-weight: 600 !important;
      color: #CCCCCC !important;
      font-family: 'Consolas', 'Monaco', 'Microsoft YaHei', sans-serif !important;
      display: flex !important;
      align-items: center !important;
      gap: 8px;

      &::before {
        content: '⚙️';
        font-size: 18px;
      }
    }

    .el-drawer__close-btn {
      color: #858585 !important;
      font-size: 20px !important;

      &:hover {
        color: #409EFF !important;
      }
    }
  }

  ::v-deep .el-drawer__body {
    background: rgba(37, 37, 38, 0.8) !important;
  }

  .drawer-content {
    padding: 20px;
    background: transparent;

    .progress-section {
      margin-bottom: 20px;

      .progress-descriptions {
        background: rgba(30, 30, 30, 0.6) !important;
        border-radius: 8px !important;
        padding: 12px !important;
        border: 1px solid #3e3e42 !important;

        ::v-deep .el-descriptions__title {
          font-size: 14px !important;
          font-weight: 600 !important;
          color: #4EC9B0 !important;
          margin-bottom: 12px !important;
          font-family: 'Consolas', 'Monaco', monospace !important;
          padding-bottom: 8px !important;
          border-bottom: 1px solid rgba(64, 158, 255, 0.15) !important;
        }

        ::v-deep .el-descriptions__body {
          background: transparent !important;

          .el-descriptions__table {
            border: none !important;

            .el-descriptions-item__cell {
              border: none !important;
              padding: 8px 12px !important;
              background: transparent !important;
            }
          }
        }

        .progress-item {
          ::v-deep .el-descriptions-item__label {
            font-weight: 600 !important;
            color: #858585 !important;
            display: flex !important;
            align-items: center !important;
            font-size: 12px !important;
            background: rgba(45, 45, 48, 0.6) !important;
            border-right: 1px solid #3e3e42 !important;

            .status-icon {
              margin-right: 6px;
              font-size: 14px;

              &.loading {
                color: #409EFF !important;
                animation: spin 1s linear infinite;
              }

              &.success {
                color: #67C23A !important;
              }

              &.completed {
                color: #4EC9B0 !important;
                font-weight: bold;
              }
            }
          }

          ::v-deep .el-descriptions-item__content {
            background: transparent !important;
            color: #CCCCCC !important;
          }

          .progress-name {
            color: #CCCCCC !important;
            font-size: 13px !important;
            font-weight: 500 !important;
          }
        }
      }
    }
  }
}

// 动画效果
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// Job详细内容折叠动画
.job-detail-fade-enter-active,
.job-detail-fade-leave-active {
  transition: all 0.3s ease;
  max-height: 1000px;
  overflow: hidden;
}

.job-detail-fade-enter,
.job-detail-fade-leave-to {
  opacity: 0;
  max-height: 0;
  transform: translateY(-10px);
}

.job-detail-content {
  overflow: hidden;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

// 旋转动画 - 用于加载图标
@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 闪光动画 - 用于状态条
@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

// 流动动画 - 用于路径箭头
@keyframes flow {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 20px 0;
  }
}

// 脉冲动画 - 用于箭头图标
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

// 旋转图标动画 - 用于判断节点
@keyframes rotate-icon {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// LED 呼吸灯动画 - 用于状态条
@keyframes ledBreathe {
  0%, 100% {
    opacity: 1;
    box-shadow: 0 0 5px rgba(103, 194, 58, 0.5);
  }
  50% {
    opacity: 0.7;
    box-shadow: 0 0 15px rgba(103, 194, 58, 0.8);
  }
}

// 路径脉冲动画 - 用于执行中的路径区域
@keyframes pathPulse {
  0%, 100% {
    box-shadow: 0 0 0 rgba(103, 194, 58, 0);
  }
  50% {
    box-shadow: 0 0 10px rgba(103, 194, 58, 0.3);
  }
}

// 终端闪烁动画 - 用于光标
@keyframes terminalBlink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

// 终端淡入动画 - 用于日志行
@keyframes terminalFadeIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

// 左右分栏视觉分隔
.right-panel {
  .job-list-card {
    border-left: 3px solid #67C23A;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: -3px;
      top: 50%;
      transform: translateY(-50%);
      width: 0;
      height: 0;
      border-right: 8px solid #67C23A;
      border-top: 8px solid transparent;
      border-bottom: 8px solid transparent;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .task-info-form {
    .form-item {
      ::v-deep .el-form-item__label {
        width: 70px !important;
        font-size: 11px;
      }

      .form-value {
        font-size: 11px;
        padding: 3px 6px;
        min-height: 22px;
      }
    }
  }
}

@media (max-width: 768px) {
  .task-detail-container {
    padding: 10px;
  }

  .main-content {
    flex-direction: column;

    .left-panel,
    .right-panel {
      margin-bottom: 20px;

      .terminal-card,
      .timeline-card,
      .job-list-card {
        border: none;

        &::after,
        &::before {
          display: none;
        }
      }
    }
  }

  // 终端响应式优化
  .terminal-card {
    .terminal-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;

      .terminal-controls {
        align-self: flex-end;
      }
    }

    .terminal-container {
      font-size: 11px;
      min-height: 300px;
      max-height: 400px;
      padding: 12px;
    }

    .terminal-line {
      .terminal-line-number {
        font-size: 9px;
        margin-right: 4px;
      }

      .terminal-prompt {
        font-size: 11px;
      }

      .terminal-timestamp {
        font-size: 9px;
      }

      .terminal-text {
        font-size: 11px;
      }
    }
  }

  // 任务信息卡片响应式
  .task-info-card {
    .task-info-modern {
      padding: 12px;
      gap: 10px;

      .core-section .info-cards-grid {
        grid-template-columns: 1fr;
        gap: 8px;

        .info-mini-card {
          padding: 10px;

          .card-icon-wrapper {
            width: 32px;
            height: 32px;

            i {
              font-size: 16px;
            }
          }
        }
      }

      .path-section .task-path-visual {
        flex-direction: column;
        padding: 12px;
        gap: 12px;

        .path-point {
          width: 100%;
          min-width: auto;
        }

        .path-arrow-line {
          margin: 0;
          width: 100%;
          transform: rotate(90deg);

          .arrow-line-inner {
            width: 50px;
          }

          .arrow-text {
            display: none;
          }
        }
      }

      .pallet-section .pallet-info-list {
        flex-direction: column;

        .pallet-info-item {
          min-width: auto;
        }
      }
    }
  }

  .task-info-form {
    .form-item {
      ::v-deep .el-form-item__label {
        width: 60px !important;
        font-size: 10px;
        text-align: left;
      }

      .form-value {
        font-size: 10px;
        padding: 2px 4px;
        min-height: 20px;
      }
    }
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;

    .header-actions {
      width: 100%;
      justify-content: flex-end;
    }
  }

  .job-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start !important;

    .job-title {
      flex-direction: column;
      width: 100%;

      .job-info-inline {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
      }
    }

    .job-actions {
      width: 100%;
      justify-content: flex-end;
      flex-wrap: wrap;
    }
  }

  .job-card {
    .job-path-section {
    flex-direction: column;
      padding: 16px;

      .path-location {
        width: 100%;
      }

      .path-arrow {
        margin: 16px 0;
        transform: rotate(90deg);

        .arrow-line {
          width: 60px;
        }

        .path-status {
          display: none;
        }
      }
    }

    .job-info-cards {
      grid-template-columns: 1fr;

      .info-card-simple {
        &.pallet-simple {
          flex-direction: column;
          align-items: flex-start;

          .card-value {
            width: 100%;
          }
        }

        &.time-simple {
          .time-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 4px;

            .time-label {
              min-width: auto;
            }
          }
        }
      }
    }
  }
}

// 滚动条美化
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;

  &:hover {
    background: #a8a8a8;
  }
}

// 加载状态优化 - 深色主题
.el-loading-mask {
  background-color: rgba(30, 30, 30, 0.8) !important;
  backdrop-filter: blur(4px);

  .el-loading-spinner {
    .el-icon-loading {
      color: #4EC9B0 !important;
      font-size: 32px;
    }

    .el-loading-text {
      color: #CCCCCC !important;
    }
  }
}

// 全局遮罩层优化（针对模态框和抽屉）
::v-deep .v-modal {
  background-color: rgba(0, 0, 0, 0.7) !important;
}

// 查看完整按钮样式（终端风格）
.view-more-btn {
  color: #569CD6 !important;
  font-size: 11px !important;
  padding: 0 4px !important;
  margin-left: 4px;
  vertical-align: baseline;
  font-family: 'Consolas', 'Monaco', monospace !important;

  &:hover {
    color: #9CDCFE !important;
    text-decoration: underline;
  }
}

// 内容模态框样式 - 科技暗色主题
.content-dialog {
  ::v-deep .el-dialog {
    background: rgba(30, 30, 30, 0.98) !important;
    border: 2px solid #3e3e42 !important;
    border-radius: 12px !important;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
    backdrop-filter: blur(10px);
  }

  ::v-deep .el-dialog__wrapper {
    background-color: rgba(0, 0, 0, 0.7) !important;
  }

  ::v-deep .el-dialog__header {
    padding: 16px 20px !important;
    border-bottom: 1px solid rgba(64, 158, 255, 0.2) !important;
    background: rgba(45, 45, 48, 0.8) !important;

    .el-dialog__title {
      font-size: 16px !important;
      font-weight: 600 !important;
      color: #CCCCCC !important;
      font-family: 'Consolas', 'Monaco', 'Microsoft YaHei', sans-serif !important;
      display: flex !important;
      align-items: center !important;
      gap: 8px;

      &::before {
        content: '📋';
        font-size: 18px;
      }
    }

    .el-dialog__headerbtn {
      top: 16px !important;
      right: 20px !important;

      .el-dialog__close {
        color: #858585 !important;
        font-size: 18px !important;

        &:hover {
          color: #409EFF !important;
        }
      }
    }
  }

  ::v-deep .el-dialog__body {
    padding: 20px !important;
    max-height: 60vh;
    overflow-y: auto;
    background: rgba(37, 37, 38, 0.8) !important;

    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-track {
      background: #252526 !important;
      border-radius: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: #424242 !important;
      border-radius: 4px;

      &:hover {
        background: #4e4e4e !important;
      }
    }
  }

  .full-content {
    .content-text {
      background: rgba(30, 30, 30, 0.8) !important;
      padding: 16px !important;
      border-radius: 8px !important;
      border: 1px solid #3e3e42 !important;
      font-size: 13px !important;
      line-height: 1.8 !important;
      color: #CCCCCC !important;
      white-space: pre-wrap;
      word-wrap: break-word;
      word-break: break-all;
      font-family: 'Consolas', 'Monaco', 'Courier New', monospace !important;
      box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.3) !important;
    }
  }

  ::v-deep .el-dialog__footer {
    padding: 16px 20px !important;
    border-top: 1px solid rgba(64, 158, 255, 0.2) !important;
    background: rgba(45, 45, 48, 0.8) !important;
  }

  .dialog-footer {
    text-align: right;
    padding: 16px 20px !important;
    border-top: 1px solid rgba(64, 158, 255, 0.2) !important;
    background: rgba(45, 45, 48, 0.8) !important;

    .el-button {
      margin-left: 12px !important;
      border-radius: 6px !important;
      font-weight: 500 !important;
      padding: 10px 20px !important;
      transition: all 0.3s ease;

      &:not(.el-button--primary) {
        background: rgba(62, 62, 66, 0.6) !important;
        border: 1px solid #3e3e42 !important;
        color: #CCCCCC !important;

        &:hover {
          background: rgba(62, 62, 66, 0.8) !important;
          border-color: #858585 !important;
          color: #FFFFFF !important;
        }
      }

      &.el-button--primary {
        background: rgba(64, 158, 255, 0.2) !important;
        border: 1px solid rgba(64, 158, 255, 0.5) !important;
        color: #409EFF !important;

        &:hover {
          background: rgba(64, 158, 255, 0.3) !important;
          border-color: #409EFF !important;
          box-shadow: 0 0 12px rgba(64, 158, 255, 0.4) !important;
        }
      }
    }
  }
}
</style>
