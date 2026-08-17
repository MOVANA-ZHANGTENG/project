<template>
  <div class="dashboard-container">
    <!-- 仓库选择器 -->
    <div class="warehouse-selector">
      <SelectModel v-model="selectedWarehouse" />
 
    </div>

    <!-- 关键指标卡片 -->

    <el-row class="metrics-cards" :gutter="20">
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon tasks">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="metric-content">
            <div class="metric-title">总任务数</div>
            <div class="metric-value">
              <count-to :start-val="0" :end-val="taskStats.total" :duration="2000"></count-to>
            </div>
            <div class="metric-trend">
              <span class="trend-up">+{{ taskStats.today }}</span>
              <span class="trend-text">今日新增</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon devices">
            <i class="el-icon-cpu"></i>
          </div>
          <div class="metric-content">
            <div class="metric-title">设备总数</div>
            <div class="metric-value">
              <count-to :start-val="0" :end-val="deviceStats.total" :duration="2000"></count-to>
            </div>
            <div class="metric-trend">
              <span class="trend-up">{{ deviceStats.online }}</span>
              <span class="trend-text">在线设备</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon cells">
            <i class="el-icon-location"></i>
          </div>
          <div class="metric-content">
            <div class="metric-title">库位总数</div>
            <div class="metric-value">
              <count-to :start-val="0" :end-val="cellStats.total" :duration="2000"></count-to>
            </div>
            <div class="metric-trend">
              <span class="trend-up">{{ cellStats.occupied }}</span>
              <span class="trend-text">已占用</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon efficiency">
            <i class="el-icon-data-line"></i>
          </div>
          <div class="metric-content">
            <div class="metric-title">库位利用率</div>
            <div class="metric-value">
              <count-to :start-val="0" :end-val="utilizationRate" :duration="2000"></count-to>%
            </div>
            <div class="metric-trend">
              <span class="trend-up">{{ cellStats.available }}</span>
              <span class="trend-text">可用库位</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>


    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 设备状态分布 -->
        <!-- <el-col :xs="24" :sm="24" :lg="8">
          <el-card class="chart-card">
            <div slot="header" class="clearfix">
              <span>设备状态分布</span>
            </div>
            <div ref="deviceStatusChart" class="chart-container"></div>
          </el-card>
        </el-col> -->

        <!-- 7日内任务类型统计 -->
        <el-col :xs="24" :sm="24" :lg="16">
          <el-card class="chart-card">
            <div slot="header" class="clearfix">
              <span>7日内任务类型统计</span>
            </div>
            <div ref="taskTypeTrendChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 库位利用率 -->
        <el-col :xs="24" :sm="24" :lg="8">
          <el-card class="chart-card">
            <div slot="header" class="clearfix">
              <span>库位利用率</span>
            </div>
            <div ref="utilizationChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 实时数据表格 -->
    <div class="realtime-data">
      <el-row :gutter="20">
        <!-- 当前任务 -->
        <el-col :xs="24" :sm="24" :lg="12">
          <el-card class="data-card">
            <div slot="header" class="clearfix">
              <span>当前任务</span>
              <el-button style="float: right; padding: 3px 0" type="text" @click="refreshRecentTasks">刷新</el-button>
            </div>
            <div class="task-list">
              <div class="task-header">
                <div class="header-cell task-no">任务号</div>
                <div class="header-cell task-type">类型</div>
                <div class="header-cell task-positions">起始位置</div>
                <div class="header-cell task-positions">目标位置</div>
                <div class="header-cell task-pallet">托盘号</div>
                <div class="header-cell task-status">状态</div>
              </div>
              <div class="task-body" ref="taskBody">
                <div v-for="(task, index) in recentTasks" :key="task.id" class="task-row"
                  :class="{ 'task-row--even': index % 2 === 0 }">
                  <div class="task-cell task-no">
                    <span class="task-no-text">{{ task.taskNo || 'N/A' }}</span>
                  </div>
                  <div class="task-cell task-type">
                    <span class="task-type-text">{{ task.taskTypeName }}</span>
                  </div>
                  <div class="task-cell task-positions">
                    <span class="position-text">{{ task.fromCellCode || 'N/A' }}</span>
                  </div>
                  <div class="task-cell task-positions">
                    <span class="position-text">{{ task.toCellCode || 'N/A' }}</span>
                  </div>
                  <div class="task-cell task-pallet">
                    <span class="pallet-text">{{ task.palletCode || 'N/A' }}</span>
                  </div>
                  <div class="task-cell task-status">
                    <div class="status-badge" :class="getTaskStateClass(task.state)">
                      <div class="status-dot"></div>
                      <span class="status-text">{{ getTaskStateText(task.state) }}</span>
                    </div>
                  </div>
                </div>
                <div v-if="recentTasks.length === 0" class="empty-tasks">
                  <div class="empty-icon">📋</div>
                  <div class="empty-text">暂无当前任务</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 设备监控 -->
        <el-col :xs="24" :sm="24" :lg="12">
          <el-card class="data-card">
            <div slot="header" class="clearfix">
              <span>设备监控</span>
              <el-button style="float: right; padding: 3px 0" type="text" @click="refreshDeviceStatus">刷新</el-button>
            </div>
            <div class="device-grid">
              <div v-for="device in deviceStatus" :key="device.id" class="device-card" :class="{
                'online': device.isOnline === 1,
                'offline': device.isOnline === 0,
                'running': device.state === '1',
                'idle': device.state === '0',
                'error': device.state === '2'
              }">
                <div class="device-header">
                  <div class="device-code">{{ device.code }}</div>
                  <div class="device-status">
                    <el-tag :type="device.isOnline === 1 ? 'success' : 'danger'" size="mini" class="status-tag">
                      {{ device.isOnline === 1 ? '在线' : '离线' }}
                    </el-tag>
                  </div>
                </div>
                <div class="device-body">
                  <div class="device-name">{{ device.name }}</div>
                  <!-- <div class="device-type">{{ device.type }}</div> -->
                </div>
                <div class="device-footer">
                  <div class="device-state">
                    <i :class="getDeviceStateIcon(device.state)" class="state-icon"></i>
                    <span class="state-text">{{ getDeviceStateText(device.state) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import * as echarts from 'echarts'
import { listWareInfo, getWareInfo } from '@/api/wcs-base/WareInfo'
import { listTaskInfo, getSevenDaysStatistics, getBasicStatistics } from '@/api/wcs-task/TaskInfo'
import { listDeviceInfo } from '@/api/wcs-base/DeviceInfo'
import { listCellInfo } from '@/api/wcs-base/CellInfo'

import SelectModel from "./wcs-base/WareInfo/SelectModel.vue";
export default {
  name: 'Dashboard',
  components: {
    CountTo,
    SelectModel
  },
  data() {
    return {
      // 仓库相关
      selectedWarehouse: null,
      warehouseList: [],
      selectedWarehouseInfo: null,

      // 统计数据
      taskStats: {
        total: 0,
        today: 0,
        running: 0,
        completed: 0,
        failed: 0
      },
      deviceStats: {
        total: 0,
        online: 0,
        offline: 0,
        running: 0,
        idle: 0,
        error: 0
      },
      cellStats: {
        total: 0,
        occupied: 0,
        available: 0
      },

      // 图表实例
      deviceStatusChart: null,
      taskTypeTrendChart: null,
      utilizationChart: null,

      // 表格数据
      recentTasks: [],
      deviceStatus: [],

      // 定时器
      refreshTimer: null
    }
  },
  computed: {
    utilizationRate() {
      if (this.cellStats.total === 0) return 0
      return Math.round((this.cellStats.occupied / this.cellStats.total) * 100)
    }
  },
  watch: {
    // 监听仓库选择变化
    selectedWarehouse(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.handleWarehouseChange(newVal)
      }
    }
  },
  created() {
    this.loadWarehouseList()
    this.startRefreshTimer()
  },

  beforeDestroy() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer)
    }
    // 销毁图表实例
    if (this.deviceStatusChart) {
      this.deviceStatusChart.dispose()
    }
    if (this.taskTypeTrendChart) {
      this.taskTypeTrendChart.dispose()
    }
    if (this.utilizationChart) {
      this.utilizationChart.dispose()
    }
  },
  methods: {
    // 加载仓库列表
    async loadWarehouseList() {
      try {
        const response = await listWareInfo({
          pageNum: 1,
          pageSize: 999
        })
        this.warehouseList = response.rows || []

        // 如果有默认仓库，自动选择
        const defaultWarehouse = localStorage.getItem('wareCode')
        if (defaultWarehouse && this.warehouseList.find(w => w.code === defaultWarehouse)) {
          this.selectedWarehouse = defaultWarehouse
          this.handleWarehouseChange(defaultWarehouse)
        }
      } catch (error) {
        this.$modal.msgError('加载仓库列表失败')
      }
    },

    // 仓库切换处理
    async handleWarehouseChange(wareCode) {
      if (!wareCode) { 
        this.clearData()
        return
      }

      try {
        localStorage.setItem('wareCode', wareCode)
    

        // 加载该仓库的数据
        await this.loadWarehouseData(wareCode)

        // 初始化图表
        this.$nextTick(() => {
          this.initCharts()
        })
      } catch (error) {
        this.$modal.msgError('切换仓库失败')
      }
    },

   

    

    // 加载仓库数据
    async loadWarehouseData(wareCode) {
      try {
        // 并行加载各种数据
        const [deviceResponse, cellResponse] = await Promise.all([
          listDeviceInfo({ wareCode, pageNum: 1, pageSize: 10000 }),
          listCellInfo({ wareCode, pageNum: 1, pageSize: 10000 })
        ])

        // 处理设备数据
        this.processDeviceData(deviceResponse.rows || [])

        // 处理库位数据
        this.processCellData(cellResponse.rows || [])

        // 加载任务基础统计（替代原来的processTaskData）
        this.loadBasicStatistics(wareCode)

        // 加载当前任务
        this.loadRecentTasks(wareCode)

        // 加载设备状态
        this.loadDeviceStatus(wareCode)

        // 加载7天任务统计数据
        this.loadSevenDaysStatistics(wareCode)

      } catch (error) {
        console.error('加载仓库数据失败:', error)
      }
    },

    // 加载任务基础统计
    async loadBasicStatistics(wareCode) {
      try {
        const response = await getBasicStatistics(wareCode)
        if (response.code === 200 && response.data) {
          this.taskStats.total = response.data.total || 0
          this.taskStats.today = response.data.today || 0
          this.taskStats.running = response.data.running || 0
          this.taskStats.completed = response.data.completed || 0
          this.taskStats.failed = response.data.failed || 0
        } else {
          this.$modal.msgError(response.msg || '加载任务统计失败')
        }
      } catch (error) {
        console.error('加载任务基础统计失败:', error)
      }
    },

    // 处理设备数据
    processDeviceData(devices) {
      this.deviceStats.total = devices.length
      this.deviceStats.online = devices.filter(device => device.isOnline === 1).length
      this.deviceStats.offline = devices.filter(device => device.isOnline === 0).length
      this.deviceStats.running = devices.filter(device => device.state === '1').length
      this.deviceStats.idle = devices.filter(device => device.state === '0').length
      this.deviceStats.error = devices.filter(device => device.state === '2').length
    },

    // 处理库位数据
    processCellData(cells) {
      this.cellStats.total = cells.length
      // 使用 invenState 判断库位是否被占用：1=有货/被占用，0或null=空闲
      this.cellStats.occupied = cells.filter(cell => cell.invenState === 1 || cell.invenState === '1').length
      this.cellStats.available = this.cellStats.total - this.cellStats.occupied
    },

    // 加载当前任务
    async loadRecentTasks(wareCode) {
      try {
        const response = await listTaskInfo({
          wareCode,
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'create_time',
          isAsc: 'desc'
        })
        this.recentTasks = response.rows || []
      } catch (error) {
        console.error('加载当前任务失败:', error)
      }
    },

    // 加载设备状态
    async loadDeviceStatus(wareCode) {
      try {
        const response = await listDeviceInfo({
          wareCode,
          pageNum: 1,
          pageSize: 20,
          orderByColumn: 'create_time',
          isAsc: 'desc'
        })
        this.deviceStatus = response.rows || []
      } catch (error) {
        console.error('加载设备状态失败:', error)
      }
    },

    // 加载7天任务统计数据
    async loadSevenDaysStatistics(wareCode) {
      try {
        const response = await getSevenDaysStatistics(wareCode)
        if (response.code === 200 && response.data) {
          // 数据加载成功，更新图表
          this.$nextTick(() => {
            this.updateTaskTypeTrendChart(response.data)
          })
        } else {
          this.$modal.msgError(response.msg || '加载统计数据失败')
        }
      } catch (error) {
        console.error('加载7天任务统计失败:', error)
      }
    },

    // 初始化图表
    initCharts() {
      this.initDeviceStatusChart()
      this.initTaskTypeTrendChart()
      this.initUtilizationChart()
    },

    // 7日内任务类型统计曲线图
    initTaskTypeTrendChart() {
      if (!this.$refs.taskTypeTrendChart) return

      // 如果图表已存在，先销毁
      if (this.taskTypeTrendChart) {
        this.taskTypeTrendChart.dispose()
      }

      this.taskTypeTrendChart = echarts.init(this.$refs.taskTypeTrendChart)

      // 初始显示加载提示
      const option = {
        backgroundColor: 'transparent',
        title: {
          text: '加载中...',
          left: 'center',
          top: 'middle',
          textStyle: {
            color: '#ffffff',
            fontSize: 16
          }
        }
      }
      this.taskTypeTrendChart.setOption(option)
    },

    // 更新任务类型趋势图表数据
    updateTaskTypeTrendChart(data) {
      if (!this.taskTypeTrendChart || !data) return

      const dates = data.dates || []
      const taskTypes = data.taskTypes || []

      // 配色方案
      const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4']

      // 构建系列数据
      const series = taskTypes.map((type, index) => ({
        name: type.name,
        type: 'line',
        smooth: true,
        data: type.data,
        lineStyle: {
          width: 2
        },
        itemStyle: {
          color: colors[index % colors.length]
        }
      }))

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: 'rgba(255, 255, 255, 0.2)',
          textStyle: {
            color: '#ffffff'
          }
        },
        legend: {
          data: taskTypes.map(t => t.name),
          top: 'bottom',
          textStyle: {
            color: '#ffffff'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLine: {
            lineStyle: {
              color: '#ffffff'
            }
          },
          axisTick: {
            lineStyle: {
              color: '#ffffff'
            }
          },
          axisLabel: {
            color: '#ffffff'
          }
        },
        yAxis: {
          type: 'value',
          name: '任务数量',
          nameTextStyle: {
            color: '#ffffff'
          },
          axisLine: {
            lineStyle: {
              color: '#ffffff'
            }
          },
          axisTick: {
            lineStyle: {
              color: '#ffffff'
            }
          },
          axisLabel: {
            color: '#ffffff'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.2)'
            }
          }
        },
        series: series
      }
      
      this.taskTypeTrendChart.setOption(option, true)
    },

    // 设备状态分布饼图
    initDeviceStatusChart() {
      if (!this.$refs.deviceStatusChart) return

      this.deviceStatusChart = echarts.init(this.$refs.deviceStatusChart)
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['在线', '离线']
        },
        series: [
          {
            name: '设备状态',
            type: 'pie',
            radius: '50%',
            data: [
              { value: this.deviceStats.online, name: '在线' },
              { value: this.deviceStats.offline, name: '离线' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      this.deviceStatusChart.setOption(option)
    },

    // 库位利用率图表
    initUtilizationChart() {
      if (!this.$refs.utilizationChart) return

      this.utilizationChart = echarts.init(this.$refs.utilizationChart)
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: 'rgba(255, 255, 255, 0.2)',
          textStyle: {
            color: '#ffffff'
          },
          formatter: function (params) {
            return `库位利用率: ${params.value}%<br/>已占用: ${Math.round(params.value * 0.01 * 100)}个<br/>总库位: 100个`
          }
        },
        series: [
          {
            name: '库位利用率',
            type: 'gauge',
            radius: '80%',
            center: ['50%', '55%'],
            startAngle: 200,
            endAngle: -20,
            min: 0,
            max: 100,
            splitNumber: 10,
            itemStyle: {
              color: '#5470c6',
              shadowColor: 'rgba(0,0,0,0.45)',
              shadowBlur: 10,
              shadowOffsetX: 2,
              shadowOffsetY: 2
            },
            progress: {
              show: true,
              width: 18,
              itemStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 1,
                  y2: 0,
                  colorStops: [
                    { offset: 0, color: '#67c23a' },
                    { offset: 0.5, color: '#e6a23c' },
                    { offset: 1, color: '#f56c6c' }
                  ]
                }
              }
            },
            pointer: {
              itemStyle: {
                color: '#5470c6',
                shadowColor: 'rgba(0,0,0,0.2)',
                shadowBlur: 5
              }
            },
            axisLine: {
              lineStyle: {
                width: 18,
                color: [
                  [0.3, '#67c23a'],
                  [0.7, '#e6a23c'],
                  [1, '#f56c6c']
                ]
              }
            },
            axisTick: {
              distance: -30,
              splitNumber: 5,
              lineStyle: {
                width: 2,
                color: 'rgba(255, 255, 255, 0.6)'
              }
            },
            splitLine: {
              distance: -30,
              length: 30,
              lineStyle: {
                width: 4,
                color: 'rgba(255, 255, 255, 0.6)'
              }
            },
            axisLabel: {
              color: '#ffffff',
              fontSize: 12,
              distance: -60,
              rotate: 'tangential',
              formatter: function (value) {
                if (value === 20) return '低';
                if (value === 50) return '中';
                if (value === 80) return '高';
                return '';
              }
            },
            detail: {
              valueAnimation: true,
              fontSize: 20,
              offsetCenter: [0, '70%'],
              formatter: function (value) {
                return Math.round(value) + '%';
              },
              color: '#ffffff'
            },
            data: [
              {
                value: this.utilizationRate,
                name: '库位利用率'
              }
            ]
          }
        ]
      }
      this.utilizationChart.setOption(option)
    },


    // 获取任务状态类型
    getTaskStateType(state) {
      const stateMap = {
        '0': 'info',
        '1': 'warning',
        '2': 'success',
        '3': 'danger'
      }
      return stateMap[state] || 'info'
    },

    // 获取任务状态文本
    getTaskStateText(state) {
      const stateMap = {
        '0': '待执行',
        '1': '执行中',
        '2': '已完成',
        '3': '失败'
      }
      return stateMap[state] || '未知'
    },

    // 获取任务状态样式类
    getTaskStateClass(state) {
      const classMap = {
        '0': 'status-pending',
        '1': 'status-running',
        '2': 'status-completed',
        '3': 'status-error'
      }
      return classMap[state] || 'status-unknown'
    },

    // 刷新当前任务
    refreshRecentTasks() {
      if (this.selectedWarehouse) {
        this.loadRecentTasks(this.selectedWarehouse)
      }
    },

    // 刷新设备状态
    refreshDeviceStatus() {
      if (this.selectedWarehouse) {
        this.loadDeviceStatus(this.selectedWarehouse)
      }
    },

    // 获取设备状态类型
    getDeviceStateType(state) {
      const stateMap = {
        '0': 'info',    // 空闲
        '1': 'success', // 运行
        '2': 'danger'   // 故障
      }
      return stateMap[state] || 'info'
    },

    // 获取设备状态文本
    getDeviceStateText(state) {
      const stateMap = {
        '0': '空闲',
        '1': '运行',
        '2': '故障'
      }
      return stateMap[state] || '未知'
    },

    // 获取设备状态图标
    getDeviceStateIcon(state) {
      const iconMap = {
        '0': 'el-icon-time',      // 空闲
        '1': 'el-icon-video-play', // 运行
        '2': 'el-icon-warning'     // 故障
      }
      return iconMap[state] || 'el-icon-question'
    },

    // 清空数据
    clearData() {
      this.taskStats = { total: 0, today: 0, running: 0, completed: 0, failed: 0 }
      this.deviceStats = { total: 0, online: 0, offline: 0, running: 0, idle: 0, error: 0 }
      this.cellStats = { total: 0, occupied: 0, available: 0 }
      this.recentTasks = []
      this.deviceStatus = []
    },

    // 开始定时刷新
    startRefreshTimer() {
      this.refreshTimer = setInterval(() => {
        if (this.selectedWarehouse) {
          this.loadWarehouseData(this.selectedWarehouse)
        }
      }, 30000) // 30秒刷新一次
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  background: #0f0f1e;
  min-height: calc(100vh - 84px);
  position: relative;

  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: transparent;
    pointer-events: none;
    z-index: 0;
  }

  >* {
    position: relative;
    z-index: 1;
  }
}

.el-card__body {
  padding: 8px 20px 8px 20px;
}

.warehouse-selector {
  margin-bottom: 20px;

  .compact-card {
    background: rgba(255, 255, 255, 0.03);
    backdrop-filter: blur(3px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
    }
  }


  .warehouse-content {
    padding: 2px 10px;
    position: relative;
    z-index: 2;

    

   
  }
}

.metrics-cards {
  margin-bottom: 20px;

  .metric-card {
    background: rgba(255, 255, 255, 0.03);
    backdrop-filter: blur(3px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    height: 120px;
    display: flex;
    align-items: center;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      // background: rgba(255, 255, 255, 0.1);
      pointer-events: none;
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
      border-color: rgba(255, 255, 255, 0.15);
    }

    .metric-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      position: relative;
      z-index: 2;

      i {
        font-size: 24px;
        color: white;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
      }

      &.tasks {
        background: #667eea;
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
      }

      &.devices {
        background: #f5576c;
        box-shadow: 0 4px 15px rgba(240, 147, 251, 0.4);
      }

      &.cells {
        background: #4facfe;
        box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
      }

      &.efficiency {
        background: #43e97b;
        box-shadow: 0 4px 15px rgba(67, 233, 123, 0.4);
      }
    }

    .metric-content {
      flex: 1;
      position: relative;
      z-index: 2;

      .metric-title {
        font-size: 14px;
        color: #b8c5d1;
        margin-bottom: 8px;
        font-weight: 500;
      }

      .metric-value {
        font-size: 28px;
        font-weight: bold;
        color: #ffffff;
        margin-bottom: 5px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
      }

      .metric-trend {
        font-size: 12px;

        .trend-up {
          color: #67c23a;
          font-weight: bold;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
        }

        .trend-text {
          color: #b8c5d1;
          margin-left: 5px;
        }
      }
    }
  }
}

.charts-section {
  margin-bottom: 20px;

  .chart-card {
    height: 350px;
    background: rgba(255, 255, 255, 0.03);
    backdrop-filter: blur(3px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);

    .el-card__header {
      background: rgba(255, 255, 255, 0.05);
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      color: #ffffff;
      font-weight: 700;
      font-size: 16px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
    }

    .chart-container {
      height: 280px;
    }
  }
}

.realtime-data {
  .data-card {
    height: 400px;
    background: rgba(255, 255, 255, 0.03);
    backdrop-filter: blur(3px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);

    .el-card__header {
      background: rgba(255, 255, 255, 0.05);
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      color: #ffffff;
      font-weight: 600;
    }

    // 原生任务列表样式
    .task-list {
      background: transparent;
      border-radius: 8px;
      overflow: hidden;
      height: 300px;
      display: flex;
      flex-direction: column;

      .task-header {
        display: grid;
        grid-template-columns: 100px 80px 120px 120px 100px 80px;
        gap: 8px;
        padding: 12px 16px;
        background: rgba(255, 255, 255, 0.03);
        backdrop-filter: blur(3px);
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        position: sticky;
        top: 0;
        z-index: 10;

        .header-cell {
          color: #ffffff;
          font-weight: 600;
          font-size: 12px;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
          display: flex;
          align-items: center;

          &.task-no {
            justify-content: center;
          }

          &.task-type {
            justify-content: center;
          }

          &.task-positions {
            justify-content: center;
          }

          &.task-pallet {
            justify-content: center;
          }

          &.task-status {
            justify-content: center;
          }
        }
      }

      .task-body {
        flex: 1;
        overflow-y: auto;
        max-height: 240px;

        // 自定义滚动条
        &::-webkit-scrollbar {
          width: 6px;
        }

        &::-webkit-scrollbar-track {
          background: rgba(255, 255, 255, 0.1);
          border-radius: 3px;
        }

        &::-webkit-scrollbar-thumb {
          background: rgba(255, 255, 255, 0.3);
          border-radius: 3px;

          &:hover {
            background: rgba(255, 255, 255, 0.5);
          }
        }

        .task-row {
          display: grid;
          grid-template-columns: 100px 80px 120px 120px 100px 80px;
          gap: 8px;
          padding: 12px 16px;
          border-bottom: 1px solid rgba(255, 255, 255, 0.05);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 3px;
            background: rgba(102, 126, 234, 0.6);
            opacity: 0;
            transition: opacity 0.3s ease;
          }

          &:hover {
            background: rgba(255, 255, 255, 0.03);
            backdrop-filter: blur(2px);
            transform: translateX(2px);

            &::before {
              opacity: 1;
            }
          }

          &.task-row--even {
            background: rgba(255, 255, 255, 0.02);
          }

          .task-cell {
            display: flex;
            align-items: center;
            color: #e0e6ed;
            font-size: 11px;

            &.task-no {
              justify-content: center;

              .task-no-text {
                font-family: 'Courier New', monospace;
                font-weight: 600;
                color: #667eea;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
                font-size: 11px;
              }
            }

            &.task-type {
              justify-content: center;

              .task-type-text {
                font-weight: 500;
                color: #b8c5d1;
                font-size: 10px;
              }
            }

            &.task-positions {
              justify-content: center;

              .position-text {
                font-family: 'Courier New', monospace;
                font-weight: 500;
                color: #8a9ba8;
                font-size: 10px;
                text-align: center;
                word-break: break-all;
              }
            }

            &.task-pallet {
              justify-content: center;

              .pallet-text {
                font-family: 'Courier New', monospace;
                font-weight: 500;
                color: #67c23a;
                font-size: 10px;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
              }
            }

            &.task-status {
              justify-content: center;
            }
          }
        }

        .empty-tasks {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 200px;
          color: #8a9ba8;

          .empty-icon {
            font-size: 48px;
            margin-bottom: 16px;
            opacity: 0.6;
          }

          .empty-text {
            font-size: 14px;
            font-weight: 500;
          }
        }
      }
    }

    // 状态徽章样式
    .status-badge {
      display: inline-flex;
      align-items: center;
      padding: 4px 8px;
      border-radius: 8px;
      font-size: 11px;
      font-weight: 500;
      backdrop-filter: blur(2px);
      border: 1px solid;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        // background: rgba(255, 255, 255, 0.2);
        transition: left 0.6s ease;
      }

      &:hover::before {
        left: 100%;
      }

      .status-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        margin-right: 6px;
        animation: pulse 2s infinite;
      }

      .status-text {
        font-weight: 500;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      }

      // 不同状态的样式
      &.status-pending {
        background: rgba(144, 147, 153, 0.2);
        border-color: rgba(144, 147, 153, 0.3);
        color: #909399;

        .status-dot {
          background: #909399;
        }
      }

      &.status-running {
        background: rgba(230, 162, 60, 0.2);
        border-color: rgba(230, 162, 60, 0.3);
        color: #e6a23c;

        .status-dot {
          background: #e6a23c;
          animation: pulse 1.5s infinite;
        }
      }

      &.status-completed {
        background: rgba(103, 194, 58, 0.2);
        border-color: rgba(103, 194, 58, 0.3);
        color: #67c23a;

        .status-dot {
          background: #67c23a;
        }
      }

      &.status-error {
        background: rgba(245, 108, 108, 0.2);
        border-color: rgba(245, 108, 108, 0.3);
        color: #f56c6c;

        .status-dot {
          background: #f56c6c;
          animation: pulse 1s infinite;
        }
      }

      &.status-unknown {
        background: rgba(144, 147, 153, 0.1);
        border-color: rgba(144, 147, 153, 0.2);
        color: #8a9ba8;

        .status-dot {
          background: #8a9ba8;
        }
      }
    }

    // 脉冲动画
    @keyframes pulse {

      0%,
      100% {
        opacity: 1;
        transform: scale(1);
      }

      50% {
        opacity: 0.7;
        transform: scale(1.1);
      }
    }
  }
}

.el-card {
  color: #ffffff;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  padding: 8px 0;
  max-height: 300px;
  overflow-y: auto;

  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;

    &:hover {
      background: #a8a8a8;
    }
  }

  .device-card {
    background: rgba(255, 255, 255, 0.02);
    backdrop-filter: blur(2px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    padding: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    position: relative;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      // background: rgba(255, 255, 255, 0.01);
      pointer-events: none;
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
      border-color: rgba(255, 255, 255, 0.15);
    }

    // 在线状态样式
    &.online {
      border-left: 4px solid #67c23a;
    }

    // 离线状态样式
    &.offline {
      border-left: 4px solid #f56c6c;
    }

    // 运行状态样式
    &.running {
      .device-state {
        color: #4ade80;
        font-weight: 600;
        text-shadow: 0 2px 4px rgba(74, 222, 128, 0.4);
        filter: drop-shadow(0 0 2px rgba(74, 222, 128, 0.3));
      }
    }

    // 空闲状态样式
    &.idle {
      .device-state {
        color: #a1a1aa;
        text-shadow: 0 2px 4px rgba(161, 161, 170, 0.3);
        filter: drop-shadow(0 0 1px rgba(255, 255, 255, 0.1));
      }
    }

    // 故障状态样式
    &.error {
      .device-state {
        color: #f87171;
        font-weight: 600;
        text-shadow: 0 2px 4px rgba(248, 113, 113, 0.4);
        filter: drop-shadow(0 0 2px rgba(248, 113, 113, 0.3));
      }
    }

    .device-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      position: relative;
      z-index: 2;

      .device-code {
        font-size: 14px;
        font-weight: 600;
        color: #ffffff;
        letter-spacing: 0.2px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
        filter: drop-shadow(0 0 2px rgba(102, 126, 234, 0.3));
      }

      .device-status {
        .status-tag {
          font-size: 10px;
          padding: 2px 8px;
          border-radius: 4px;
          font-weight: 500;
          text-transform: uppercase;
          letter-spacing: 0.2px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }
      }
    }

    .device-body {
      margin-bottom: 8px;
      position: relative;
      z-index: 2;

      .device-name {
        font-size: 12px;
        font-weight: 500;
        color: #e8f4fd;
        line-height: 1.3;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.4);
        filter: drop-shadow(0 0 1px rgba(255, 255, 255, 0.1));
      }

      .device-type {
        font-size: 10px;
        color: #cbd5e1;
        margin-top: 2px;
        font-style: italic;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
        filter: drop-shadow(0 0 1px rgba(255, 255, 255, 0.1));
      }
    }

    .device-footer {
      position: relative;
      z-index: 2;

      .device-state {
        display: flex;
        align-items: center;
        font-size: 11px;
        font-weight: 500;

        .state-icon {
          margin-right: 4px;
          font-size: 12px;
          width: 14px;
          height: 14px;
          display: flex;
          align-items: center;
          justify-content: center;
          filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
        }

        .state-text {
          font-weight: 500;
          letter-spacing: 0.1px;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
          filter: drop-shadow(0 0 1px rgba(255, 255, 255, 0.1));
        }
      }
    }
  }
}


// 响应式设计
@media (max-width: 768px) {
  .device-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 8px;

    .device-card {
      padding: 8px;

      .device-header .device-code {
        font-size: 11px;
      }

      .device-body .device-name {
        font-size: 10px;
      }

      .device-footer .device-state {
        font-size: 9px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }

  .metrics-cards {
    .metric-card {
      height: auto;
      flex-direction: column;
      text-align: center;

      .metric-icon {
        margin-right: 0;
        margin-bottom: 10px;
      }
    }
  }
}
</style>
