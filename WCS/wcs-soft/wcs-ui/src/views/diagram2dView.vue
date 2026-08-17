<template>
  <div class="diagram-page">
    <div id="tooltip" class="diagram-tooltip">
      <el-card v-if="nowDeviceData0 != null && nowDeviceData0.code != null" class="diagram-tooltipCard">
        <div slot="header" class="clearfix">
          <span class="diagram-tooltipTitle">设备信息</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>编码：</span>
          <span>{{ nowDeviceData0.code }}</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>名称：</span>
          <span>{{ nowDeviceData0.name }}</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>IP：</span>
          <span>{{ nowDeviceData0.ip }}</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>端口：</span>
          <span>{{ nowDeviceData0.port }}</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>状态：</span>
          <dict-tag :options="dict.type.is_online" :value="nowDeviceData0.isOnline" />
        </div>
      </el-card>
      <el-card v-if="nowPositionData0 != null && nowPositionData0.code != null" class="diagram-tooltipCard">
        <div slot="header" class="clearfix">
          <span class="diagram-tooltipTitle">站台信息</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>编码：</span>
          <span>{{ nowPositionData0.code }}</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>名称</span>
          <span>{{ nowPositionData0.name }}</span>
        </div>
        <div class="diagram-tooltipRow">
          <span>状态</span>
          <span>{{ nowPositionData0.state }}</span>
        </div>
      </el-card>
    </div>

    <div class="diagram-content">
      <el-card class="diagram-toolbar">
        <el-form :inline="true" ref="ware" :model="wareInfo" class="diagram-toolbarForm">
          <el-form-item label="仓库名称">
            <el-select v-model="wareCode" placeholder="请选择仓库" class="diagram-wareSelect">
              <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code + ''">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="diagram-toolbarActions">
            <el-button type="primary" @click="routerToUpdate()" class="diagram-btn">修改模型</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-row class="diagram-layout" :gutter="12">
        <el-col :span="18">
          <el-card class="diagram-canvasCard">
            <div id="myDiagramDiv" class="diagram-canvas"></div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="diagram-sideCard">
            <div class="diagram-sideInner">
              <el-card class="diagram-sidePanel">
                <div class="diagram-sideStack">
                  <DevicePanel :device="nowDeviceData" />
                  <PositionPanel :position="nowPositionData" />
                  <LogPanel :position-code="nowPositionData && nowPositionData.code" :ware-code="wareCode"
                    :is-dev="isDev" />
                </div>
              </el-card>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import {
  listWareInfo,
  getWareInfo,
} from "@/api/wcs-base/WareInfo";
import { listPositionInfo } from "@/api/wcs-base/PositionInfo";
import { listDeviceInfo } from "@/api/wcs-base/DeviceInfo";
import request from "@/utils/request.js";
import go from "@/lib/js/go.js";
import {
  initDiagram,
  loadDiagramData,
  zoomToFit,
  setInitialAutoScale,
} from "@/utils/gojs/gojs-helper";
import DevicePanel from "@/components/diagram/DevicePanel";
import PositionPanel from "@/components/diagram/PositionPanel";
import LogPanel from "@/components/diagram/LogPanel";

export default {
  name: "Index",
  components: {
    DevicePanel,
    PositionPanel,
    LogPanel,
  },
  dicts: [
    "com_type",
    "device_type",
    "s7_type",
    "del_flag",
    "is_online",
    "device_state",
    "position_state",
  ],
  data() {
    return {
      wareInfos: [],
      wareCode: null,
      wareInfo: {},
      devices: [],
      positions: [],
      showBind: false,
      nowDeviceData: {},
      nowDeviceData0: {},
      nowPositionData: {},
      nowPositionData0: {},
      timer: null,
      nowData: {},
      myDiagram: null,
      modelData: {},
      gridSize: {
        width: 50,
        height: 50,
      },
      isZooming: false,
      zoomTimer: null,
      devicesDebounceTimer: null,
      positionsDebounceTimer: null,
      lastFetchTime: {
        devices: 0,
        positions: 0,
      },
    };
  },
  computed: {
    isDev() {
      return process.env.NODE_ENV !== "production";
    },
  },
  watch: {
    wareCode(newValue, oldValue) {
      if (newValue != null) {
        this.wareInfos.forEach((element) => {
          if (newValue === element.code) {
            this.createGo(element);
            this.startPolling();
          }
        });
      }
    },
  },
  mounted() {
    this.getWareInfos();
  },
  created() { 

  },
  beforeDestroy() {
    this.stopPolling();
    if (this.zoomTimer) {
      clearTimeout(this.zoomTimer);
    }
    if (this.devicesDebounceTimer) {
      clearTimeout(this.devicesDebounceTimer);
    }
    if (this.positionsDebounceTimer) {
      clearTimeout(this.positionsDebounceTimer);
    }
  },
  methods: {
    routerToUpdate() {
      if (this.wareInfo.code != null) {
        this.$router.push({
          path: "/diagram2dUpdate",
          query: { code: this.wareInfo.code, backgroundImg: this.wareInfo.backgroundImg },
        });
      } else {
        this.$modal.msgError("未选择仓库");
      }
    },
    startPolling() {
      this.stopPolling();
      this.timer = setInterval(() => {
        if (this.wareCode == null) {
          return;
        }
        this.getAllDevices();
        this.getPositionsByWareCode();
      }, 3000);
    },
    stopPolling() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    getAllDevices() {
      if (!this.wareCode) return;
      const now = Date.now();
      if (now - this.lastFetchTime.devices < 1000) return;

      if (this.devicesDebounceTimer) {
        clearTimeout(this.devicesDebounceTimer);
      }

      this.devicesDebounceTimer = setTimeout(() => {
        var query = {};
        query.wareCode = this.wareCode;
        listDeviceInfo(query).then((response) => {
          if (response.code == 200) {
            this.devices = response.rows;
            this.lastFetchTime.devices = Date.now();
            this.showWarningInfo();
          }
        });
      }, 300);
    },
    getPositionsByWareCode() {
      if (!this.wareCode) return;
      const now = Date.now();
      if (now - this.lastFetchTime.positions < 1000) return;

      if (this.positionsDebounceTimer) {
        clearTimeout(this.positionsDebounceTimer);
      }

      this.positionsDebounceTimer = setTimeout(() => {
        var query = {};
        query.wareCode = this.wareCode;
        listPositionInfo(query).then((response) => {
          if (response.code == 200) {
            this.positions = response.rows;
            this.lastFetchTime.positions = Date.now();
          }
        });
      }, 300);
    },
    getPositionInfoByCode(wareCode, positionCode) {
      var position = null;
      this.positions.forEach((element) => {
        if (element.wareCode === wareCode && element.code === positionCode) {
          position = element;
        }
      });
      return position;
    },
    /**
     * 根据positionInfo对象确定站台状态
     * 
     * 状态字段说明：
     * - inven_state: 1=有库存(出站), 0=无库存(入站)
     * - task_state: 0=无任务, >0=有任务
     * - is_delete: 0=正常, 1=禁用
     * - state: 0=空闲, 1=预留, 2=占用, -1=异常
     * 
     * 四种状态组合（库存 × 任务）：
     * 1. 有货有任务 (hasStockWithTask): 绿色 #10B981 - 正在执行出站任务
     * 2. 有货无任务 (hasStockWithoutTask): 蓝色 #3B82F6 - 等待出站任务
     * 3. 无货无任务 (noStockWithoutTask): 灰色 #9CA3AF - 等待入站任务
     * 4. 无货有任务 (noStockWithTask): 橙色 #F59E0B - 正在执行入站任务
     * 
     * 特殊状态：
     * - 禁用状态 (maintenance): 深灰色 #6B7280 - 站台不可用
     * - 异常状态 (abnormal): 红色 #EF4444 - 站台故障
     * 
     * @param {Object} positionInfo - 站台信息对象
     * @returns {Object} - 包含status（状态标识）和description（状态描述）的对象
     */
    determinePositionStatus(positionInfo) {
      if (!positionInfo) {
        return { status: "offline", description: "位置信息未找到" };
      }

      // 1. 禁用状态 - 最高优先级，深灰色
      if (positionInfo.disableState === 1) {
        return { status: "maintenance", description: "已禁用" };
      }

      // 2. 异常状态 - 红色警示
      if (positionInfo.state == -1) {
        return { status: "abnormal", description: "异常" };
      }

      // 获取库存状态和任务状态
      var hasStock = positionInfo.invenState === 1;
      var hasTask = positionInfo.taskState != null && positionInfo.taskState > 0;

      // 3. 有货有任务 - 绿色，正在执行出站任务
      if (hasStock && hasTask) {
        return { status: "hasStockWithTask", description: "出站中" };
      }

      // 4. 有货无任务 - 蓝色，等待出站任务
      if (hasStock && !hasTask) {
        return { status: "hasStockWithoutTask", description: "有货" };
      }

      // 5. 无货有任务 - 橙色，正在执行入站任务
      if (!hasStock && hasTask) {
        return { status: "noStockWithTask", description: "入站中" };
      }

      // 6. 无货无任务 - 灰色，等待入站任务
      if (!hasStock && !hasTask) {
        return { status: "noStockWithoutTask", description: "无货" };
      }

      // 7. 根据state字段确定状态（兜底逻辑）
      switch (positionInfo.state) {
        case 0:
          return { status: "noStockWithoutTask", description: "空闲" };
        case 1:
          return { status: "warning", description: "预留" };
        case 2:
          return { status: "hasStockWithTask", description: "占用" };
        default:
          return { status: "noStockWithoutTask", description: "未知状态" };
      }
    },
    showWarningInfo() {
      var that = this;
      if (this.myDiagram == null) {
        return;
      }
      var nodes = this.myDiagram.model.nodeDataArray;
      var model = this.myDiagram.model;

      model.startTransaction("update node status");

      nodes.forEach((node) => {
        var position = that.getPositionInfoByCode(that.wareCode, node.positionCode);

        // 使用新的状态判断函数确定站台状态
        var statusResult = that.determinePositionStatus(position);
        var status = statusResult.status;

        // 保存原始图片源，用于状态恢复时还原
        if (!node.originalSource && node.source) {
          model.setDataProperty(node, "originalSource", node.source);
        }

        // 根据状态设置图片显示
        switch (status) {
          case "abnormal":
            // 异常状态，显示警告图标
            model.setDataProperty(node, "source", "/img/source/warning.png");
            break;
          case "offline":
            // 离线状态，显示离线图标
            model.setDataProperty(node, "source", "/img/source/lixian.png");
            break;
          case "maintenance":
            // 禁用状态，显示离线图标
            model.setDataProperty(node, "source", "/img/source/lixian.png");
            break;
          case "warning":
            // 警告状态，显示警告图标
            model.setDataProperty(node, "source", "/img/source/warning.png");
            break;
          case "hasStockWithTask":
            // 有货有任务（出站执行中），显示原始图片
            if (node.originalSource) {
              model.setDataProperty(node, "source", node.originalSource);
            }
            break;
          case "hasStockWithoutTask":
            // 有货无任务（出站待分配），显示原始图片
            if (node.originalSource) {
              model.setDataProperty(node, "source", node.originalSource);
            }
            break;
          case "noStockWithTask":
            // 无货有任务（入站执行中），显示原始图片
            if (node.originalSource) {
              model.setDataProperty(node, "source", node.originalSource);
            }
            break;
          case "noStockWithoutTask":
            // 无货无任务（入站待分配），显示原始图片
            if (node.originalSource) {
              model.setDataProperty(node, "source", node.originalSource);
            }
            break;
          default:
            // 其他状态，显示原始图片
            if (node.originalSource) {
              model.setDataProperty(node, "source", node.originalSource);
            }
        }

        model.setDataProperty(node, "status", status);
        model.setDataProperty(node, "statusDescription", statusResult.description);
      });

      model.commitTransaction("update node status");
    },
    getDeviceInfoByCode(code) {
      var devices = this.devices;
      for (let index = 0; index < devices.length; index++) {
        const device = devices[index];
        if (device.code === code) {
          return device;
        }
      }
      return null;
    },
    getDeviceMsg(deviceCode, type) {
      if (deviceCode == null) {
        if (type == 0) {
          this.nowDeviceData0 = {};
        } else if (type == 1) {
          this.nowDeviceData = {};
        }
        return;
      }
      var device = this.getDeviceInfoByCode(deviceCode);
      if (device && device.code == deviceCode) {
        if (type == 0) {
          this.nowDeviceData0 = device;
        } else if (type == 1) {
          this.nowDeviceData = device;
        }
      }
    },
    getPositionMsg(positionCode, type) {
      if (positionCode == null) {
        if (type == 0) {
          this.nowPositionData0 = {};
        } else if (type == 1) {
          this.nowPositionData = {};
        }
        return;
      }
      this.positions.forEach((position) => {
        if (position.code == positionCode) {
          if (type == 0) {
            this.nowPositionData0 = position;
          } else if (type == 1) {
            this.nowPositionData = position;
          }
        }
      });
    },
    updateTooltipPosition(node) {
      var tooltip = document.getElementById("tooltip");
      if (!tooltip || !node) {
        return;
      }

      const modelPosition = node.location;
      const screenPosition = this.myDiagram.transformDocToView(modelPosition);

      const tooltipWidth = tooltip.offsetWidth || 200;
      const tooltipHeight = tooltip.offsetHeight || 150;
      const canvasRect = document.getElementById("myDiagramDiv").getBoundingClientRect();

      let left = canvasRect.left + screenPosition.x + 20;
      let top = canvasRect.top + screenPosition.y - 10;

      if (left + tooltipWidth > window.innerWidth) {
        left = canvasRect.left + screenPosition.x - tooltipWidth - 20;
      }
      if (top + tooltipHeight > window.innerHeight) {
        top = window.innerHeight - tooltipHeight - 20;
      }
      if (top < 20) {
        top = 20;
      }

      tooltip.style.left = left + "px";
      tooltip.style.top = top + "px";
      tooltip.style.display = "block";
    },
    hideTooltip() {
      var tooltip = document.getElementById("tooltip");
      if (tooltip) {
        tooltip.style.display = "none";
      }
    },
    onZoomStart() {
      this.isZooming = true;
      const canvasDiv = document.getElementById("myDiagramDiv");
      if (canvasDiv) {
        canvasDiv.classList.add("is-zooming");
      }
    },
    onZoomEnd() {
      if (this.zoomTimer) {
        clearTimeout(this.zoomTimer);
      }
      this.zoomTimer = setTimeout(() => {
        this.isZooming = false;
        const canvasDiv = document.getElementById("myDiagramDiv");
        if (canvasDiv) {
          canvasDiv.classList.remove("is-zooming");
        }
      }, 150);
    },
    createGo(wareInfo) {
      var that = this;
      if (that.myDiagram != null && that.myDiagram["wareCode"] == wareInfo.code) {
        return;
      }
      if (that.myDiagram) {
        that.myDiagram.div = null;
      }
      that.init(wareInfo.backgroundImg);
      setTimeout(() => {
        that.myDiagram["wareCode"] = wareInfo.code;
        setInitialAutoScale(that.myDiagram, go.Diagram.Uniform);
        zoomToFit(that.myDiagram);
        that.getWareInfoMsg(wareInfo.id);
      }, 1000);
    },
    getWareInfoMsg(id) {
      var that = this;
      getWareInfo(id).then((response) => {
        if (response.code == 200) {
          this.wareInfo = response.data;
          var modelData;
          if (this.wareInfo.monitorData == null) {
            modelData = {};
          } else {
            modelData = JSON.parse(this.wareInfo.monitorData);
          }
          loadDiagramData(this.myDiagram, modelData);
          this.startPolling();
        } else {
          this.wareInfo = {};
          this.$modal.msgError(response.msg);
        }
      });
    },
    getWareInfos() {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/list",
        method: "get",
        params: { isDelete: 0 },
      }).then((response) => {
        if (response.code == 200) {
          that.wareInfos = response.rows;
          that.getWareInfoMsg(that.wareInfos[0].id);
          that.wareCode = that.wareInfos[0].code;
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },
    init(backgroundImg) {
      var that = this;

      if (this.myDiagram != null) {
        this.myDiagram.div = null;
      }

      const clickHandler = function (e, node) {
        that.nowData = node.data;
        that.getDeviceMsg(node.data.deviceCode, 1);
        that.getPositionMsg(node.data.positionCode, 1);
        that.showBind = true;
        that.hideTooltip();
      };

      const mouseEnterHandler = function (e, node) {
        that.nowData = node.data;
        that.getDeviceMsg(node.data.deviceCode, 0);
        that.getPositionMsg(node.data.positionCode, 0);
        that.showBind = true;
        that.updateTooltipPosition(node);
      };

      const mouseLeaveHandler = function (e, node) {
        that.hideTooltip();
      };

      const changedSelectionHandler = function (e) {
        that.hideTooltip();
      };

      this.myDiagram = initDiagram({
        containerId: "myDiagramDiv",
        gridSize: this.gridSize,
        isReadOnly: true,
        showGrid: true,
        clickHandler,
        mouseEnterHandler,
        mouseLeaveHandler,
        changedSelectionHandler,
        backgroundImg,
      });

      this.myDiagram.addDiagramListener("ViewportBoundsChanged", function () {
        that.onZoomStart();
        that.onZoomEnd();
      });

      var myDiagram = this.myDiagram;
      this.myDiagram.addChangedListener(function (event) {
        var modelData = myDiagram.model.toJson();
        that.modelData = modelData;
        that.wareInfo.modelData = modelData;
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.diagram-page {
  --diagram-toolbar-gap: 10px;
  --diagram-canvas-height: calc(100vh - 130px);
  --diagram-surface: #0b1020;
  --diagram-panel: rgba(255, 255, 255, 0.9);
  --diagram-border: rgba(15, 23, 42, 0.12);
  --diagram-shadow: 0 10px 30px rgba(2, 6, 23, 0.12);

  min-height: 100vh;
  padding: 10px;
  background:
    radial-gradient(1200px 600px at 10% 0%, rgba(99, 102, 241, 0.18), rgba(255, 255, 255, 0) 60%),
    radial-gradient(900px 500px at 90% 10%, rgba(16, 185, 129, 0.14), rgba(255, 255, 255, 0) 55%),
    linear-gradient(180deg, #f6f8ff 0%, #eef2ff 35%, #f8fafc 100%);
}

.diagram-content {
  width: 100%;
}

.diagram-toolbar {
  margin-bottom: var(--diagram-toolbar-gap);
  border: 1px solid var(--diagram-border);
  box-shadow: var(--diagram-shadow);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.diagram-toolbar:hover {
  box-shadow: 0 12px 36px rgba(2, 6, 23, 0.16);
}

.diagram-toolbarForm {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 10px 16px;
}

.diagram-wareSelect {
  min-width: 220px;
  border-radius: 8px;
}

.diagram-toolbarActions {
  margin-left: auto;
}

.diagram-btn {
  border-radius: 8px;
  padding: 8px 20px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.diagram-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.diagram-layout {
  align-items: stretch;
}

.diagram-canvasCard {
  border: 1px solid var(--diagram-border);
  box-shadow: var(--diagram-shadow);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.diagram-canvasCard:hover {
  box-shadow: 0 12px 36px rgba(2, 6, 23, 0.16);
}

.diagram-canvas {
  position: relative;
  height: var(--diagram-canvas-height);
  min-height: 560px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: inset 0 0 0 1px rgba(15, 23, 42, 0.08);
  -webkit-tap-highlight-color: rgba(255, 255, 255, 0);
  transition: box-shadow 0.2s ease;
}

.diagram-canvas.is-zooming {
  transition: none;
  box-shadow: none !important;
}

.diagram-sideCard {
  border: 1px solid var(--diagram-border);
  box-shadow: var(--diagram-shadow);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.diagram-sideCard:hover {
  box-shadow: 0 12px 36px rgba(2, 6, 23, 0.16);
}

.diagram-sideInner {
  height: var(--diagram-canvas-height);
  min-height: 560px;
  overflow: hidden;
  display: flex;
}

.diagram-sidePanel {
  width: 100%;
  height: 100%;
  overflow: auto;
  border: 0;
}

.diagram-sideStack {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.diagram-tooltip {
  display: none;
  position: fixed;
  padding: 8px;
  border-radius: 12px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 3000;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
  background: rgba(15, 23, 42, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(12px);
  transition: opacity 0.2s ease, transform 0.2s ease;
  pointer-events: none;
}

.diagram-tooltip::after {
  content: "";
  position: absolute;
  top: 12px;
  left: -6px;
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-right: 6px solid rgba(15, 23, 42, 0.92);
  border-bottom: 6px solid transparent;
}

.diagram-tooltipCard {
  background: linear-gradient(180deg, rgba(30, 41, 59, 0.92), rgba(15, 23, 42, 0.92));
  border: 1px solid rgba(255, 255, 255, 0.14);
  color: #fff;
  border-radius: 8px;
  margin-bottom: 6px;
}

.diagram-tooltipCard:last-child {
  margin-bottom: 0;
}

.diagram-tooltipTitle {
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.diagram-tooltipRow {
  color: rgba(255, 255, 255, 0.92);
  line-height: 22px;
  font-size: 12px;
}

@media (max-width: 1366px) {
  .diagram-page {
    --diagram-canvas-height: calc(100vh - 150px);
  }

  .diagram-wareSelect {
    min-width: 180px;
  }
}

@media (max-width: 1200px) {
  .diagram-toolbarActions {
    margin-left: 0;
    margin-top: 8px;
  }
}

@media (max-width: 768px) {
  .diagram-page {
    --diagram-canvas-height: calc(70vh);
    padding: 8px;
  }

  .diagram-layout .el-col {
    margin-bottom: 10px;
  }

  .diagram-sideInner {
    height: auto;
    min-height: 300px;
    max-height: 300px;
  }

  .diagram-tooltip {
    max-width: 90vw;
    white-space: normal;
  }
}

@media (max-width: 480px) {
  .diagram-page {
    --diagram-canvas-height: calc(50vh);
  }

  .diagram-toolbarForm {
    flex-direction: column;
    align-items: stretch;
  }

  .diagram-wareSelect {
    min-width: 100%;
  }
}
</style>