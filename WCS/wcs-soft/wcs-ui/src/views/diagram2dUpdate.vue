<template>
  <div class="diagram-page">
    <div class="diagram-content">
      <el-card class="diagram-toolbar">
        <el-form
          :inline="true"
          ref="ware"
          :model="wareInfo"
          class="diagram-toolbarForm"
        >
          <el-form-item label="仓库名称">
            <el-input v-model="wareInfo.name" :disabled="true" class="diagram-wareInput"></el-input>
          </el-form-item>
          <el-form-item class="diagram-toolbarActions">
            <el-button type="primary" @click="saveWare()" :loading="isSaving" class="diagram-btn">保存模型</el-button>
            <el-button @click="backToDiagramView()" class="diagram-btn diagram-btn-secondary">退出</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-row class="diagram-layout" :gutter="12">
        <el-col :span="4">
          <el-card class="diagram-sideCard">
            <div class="diagram-sourcePanel">
              <el-card class="diagram-sourceHeader" shadow="never">
                <el-form :model="positionSource" class="diagram-sourceForm">
                  <el-form-item label="模块名称">
                    <span class="diagram-sourceValue">{{ positionSource.name || "-" }}</span>
                  </el-form-item>
                  <el-form-item label="图片路径">
                    <span class="diagram-sourceValue diagram-mono">{{ positionSource.imgUrl || "-" }}</span>
                  </el-form-item>
                  <el-form-item class="diagram-sourceActions">
                    <el-tooltip content="新增" placement="top">
                      <el-button
                        type="primary"
                        icon="el-icon-plus"
                        size="mini"
                        @click="handleAddModel"
                        plain
                        class="diagram-actionBtn"
                      ></el-button>
                    </el-tooltip>
                    <el-tooltip content="修改" placement="top">
                      <el-button
                        type="warning"
                        icon="el-icon-edit"
                        size="mini"
                        @click="handleUpdateModel"
                        plain
                        class="diagram-actionBtn"
                      ></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                      <el-button
                        type="danger"
                        icon="el-icon-delete"
                        size="mini"
                        @click="handleDeleteModel"
                        plain
                        class="diagram-actionBtn"
                      ></el-button>
                    </el-tooltip>
                  </el-form-item>
                </el-form>
              </el-card>

              <el-card class="diagram-sourcePalette" shadow="never">
                <div
                  id="myPaletteDiv"
                  class="diagram-palette"
                ></div>
              </el-card>
            </div>
          </el-card>
        </el-col>

        <el-col :span="15">
          <el-card class="diagram-canvasCard">
            <div
              id="myDiagramDiv"
              class="diagram-canvas"
            ></div>
          </el-card>
        </el-col>

        <el-col :span="5">
          <el-card class="diagram-sideCard">
            <div class="diagram-bindPanel">
              <el-card class="diagram-bindContainer" shadow="never">
                <el-tabs v-model="activeName" v-if="showBind" class="diagram-bindTabs">
                  <el-tab-pane label="设备绑定" name="first">
                    <el-form class="diagram-bindForm">
                      <el-form-item label="绑定设备">
                        <el-select
                          v-model="nowData.deviceCode"
                          placeholder="请选择绑定设备"
                          class="diagram-bindSelect"
                        >
                          <el-option
                            v-for="item in devices"
                            :key="item.code"
                            :label="item.name"
                            :value="item.code"
                          ></el-option>
                        </el-select>
                      </el-form-item>
                    </el-form>
                    <el-descriptions
                      :column="1"
                      v-for="item in devices"
                      :key="item.code"
                      v-show="nowData.deviceCode == item.code"
                      border
                      class="diagram-bindDescriptions"
                    >
                      <el-descriptions-item label="编码">{{ item.code }}</el-descriptions-item>
                      <el-descriptions-item label="IP">{{ item.ip }}</el-descriptions-item>
                      <el-descriptions-item label="端口">{{ item.port }}</el-descriptions-item>
                    </el-descriptions>
                  </el-tab-pane>
                  <el-tab-pane label="站台绑定" name="second">
                    <el-form class="diagram-bindForm">
                      <el-form-item label="绑定站台">
                        <el-select
                          v-model="nowData.positionCode"
                          placeholder="请选择绑定站台"
                          filterable
                          class="diagram-bindSelect"
                        >
                          <el-option
                            v-for="item in positions"
                            :key="item.code"
                            :label="item.code + '【' + item.subCode + '】'"
                            :value="item.code"
                          ></el-option>
                        </el-select>
                      </el-form-item>
                    </el-form>
                    <el-descriptions
                      :column="1"
                      v-for="item in positions"
                      :key="item.code"
                      v-show="nowData.positionCode == item.code"
                      border
                      class="diagram-bindDescriptions"
                    >
                      <el-descriptions-item label="编码">{{ item.code }}</el-descriptions-item>
                      <el-descriptions-item label="类型">{{ item.type }}</el-descriptions-item>
                    </el-descriptions>
                  </el-tab-pane>
                </el-tabs>
                <div v-else class="diagram-bindEmpty">
                  请点击画布上的节点进行绑定操作
                </div>
              </el-card>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog
      v-dialogDrags
      :title="source.title"
      :visible.sync="source.open"
      width="500px"
      append-to-body
      class="diagram-sourceDialog"
    >
      <el-form
        ref="positionSourceUpdate"
        :model="positionSourceUpdate"
        label-width="80px"
        class="diagram-sourceDialogForm"
      >
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="positionSourceUpdate.name"
            placeholder="请输入名称"
          />
        </el-form-item>
        <el-form-item label="图片路径" prop="imgUrl">
          <el-input
            placeholder="请选择图片"
            v-model="positionSourceUpdate.imgUrl"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSourceForm">确 定</el-button>
        <el-button @click="cancelSourceUpdate">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  updateWareInfo,
} from "@/api/wcs-base/WareInfo";
import {
  listSource,
  getSource,
  delSource,
  addSource,
  updateSource,
} from "@/api/wcs-base/source";
import { listPositionInfo } from "@/api/wcs-base/PositionInfo";
import { listDeviceInfo } from "@/api/wcs-base/DeviceInfo";
import request from "@/utils/request.js";
import Cookies from "js-cookie";
import go from "@/lib/js/go.js";
import {
  initDiagram,
  initPalette,
  loadDiagramData,
  addNodesToPalette,
} from "@/utils/gojs/gojs-helper";

export default {
  name: "Index",
  data() {
    return {
      backgroundImg: null,
      wareCode: null,
      wareInfo: {},
      positionSource: {},
      positionSourceUpdate: {},
      source: {
        open: false,
        title: null,
      },
      positionSources: [],
      devices: [],
      positions: [],
      activeName: "first",
      showBind: false,
      nowData: {},
      myDiagram: null,
      modelData: {},
      gridSize: {
        width: 50,
        height: 50,
      },
      myPalette: null,
      isSaving: false,
      isModified: false,
    };
  },
  watch: {
    nowData: {
      handler: function (newValue) {
        if (newValue != null) {
          this.showBind = true;
        }
        if (this.myDiagram) {
          this.myDiagram.layoutDiagram(true);
        }
      },
      deep: true,
    },
  },
  mounted() {
    var that = this;
    this.wareCode = this.$route.query.code;
    that.backgroundImg = this.$route.query.backgroundImg;

    if (that.myDiagram) {
      that.myDiagram.div = null;
    }
    that.initMyDiagram(that.backgroundImg);

    setTimeout(() => {
      that.getData();
    }, 1000);
  },
  created() {},
  methods: {
    getData() {
      this.getWareInfoByCode(this.wareCode);
      this.getAllDevices();
      this.getPositionsByWareCode();
      this.getPositionSourcesList();
    },
    backToDiagramView() {
      if (this.isModified) {
        this.$modal
          .confirm("当前模型已修改，是否保存后退出？")
          .then(() => {
            return this.saveWare();
          })
          .then(() => {
            this.closePage();
          })
          .catch(() => {
            this.closePage();
          });
      } else {
        this.closePage();
      }
    },
    closePage() {
      const obj = { path: "/diagram2dView" };
      this.$tab.closeOpenPage(obj).then(() => {
        Cookies.set("wareCode", this.wareCode, { expires: 1 });
      });
    },
    getAllDevices() {
      listDeviceInfo().then((response) => {
        if (response.code == 200) {
          this.devices = response.rows;
        }
      });
    },
    getPositionsByWareCode() {
      var query = {
        pageNum: 1,
        pageSize: 999,
      };
      query.wareCode = this.wareCode;
      listPositionInfo(query).then((response) => {
        if (response.code == 200) {
          this.positions = response.rows;
        }
      });
    },
    getWareInfoByCode(code) {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/getByCode",
        method: "get",
        params: { code: code },
      }).then((response) => {
        if (response.code == 200) {
          that.wareInfo = response.data;
          that.loadData(that.wareInfo.monitorData);
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },
    getPositionSourcesList() {
      var that = this;
      listSource().then((response) => {
        if (response.code == 200) {
          this.positionSources = response.rows;
          addNodesToPalette(that.myPalette, response.rows);
        }
      });
    },
    handleAddModel() {
      this.positionSourceUpdate = {};
      this.source.open = true;
      this.source.title = "新增模块";
    },
    handleUpdateModel() {
      this.positionSourceUpdate = {};
      const id = this.positionSource.id;
      if (id == null || id == undefined) {
        this.$modal.msgError("未选择模块");
        return;
      }
      getSource(id).then((response) => {
        if (response.code == 200) {
          this.positionSourceUpdate = response.data;
        }
        this.source.open = true;
        this.source.title = "修改模块";
      });
    },
    handleDeleteModel() {
      const id = this.positionSource.id;
      this.$modal
        .confirm('是否确认删除站台资源编号为"' + id + '"的数据项？')
        .then(function () {
          return delSource(id);
        })
        .then(() => {
          this.getPositionSourcesList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    cancelSourceUpdate() {
      this.source.open = false;
      this.positionSourceUpdate = {};
    },
    submitSourceForm() {
      this.$refs["positionSourceUpdate"].validate((valid) => {
        if (valid) {
          if (this.positionSourceUpdate.id != null) {
            updateSource(this.positionSourceUpdate).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.source.open = false;
                this.getPositionSourcesList();
                this.positionSource = this.positionSourceUpdate;
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addSource(this.positionSourceUpdate).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.source.open = false;
                this.getPositionSourcesList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          }
        }
      });
    },
    saveWare() {
      if (!this.myDiagram) {
        this.$modal.msgError("画布未初始化");
        return;
      }
      this.isSaving = true;
      this.saveData();
      this.wareInfo.monitorData = this.myDiagram.model.toJson();
      updateWareInfo(this.wareInfo)
        .then((response) => {
          if (response.code == 200) {
            this.$modal.msgSuccess("修改成功");
            this.isModified = false;
          } else {
            this.$modal.msgError(response.msg);
          }
        })
        .finally(() => {
          this.isSaving = false;
        });
    },
    saveData() {
      this.myDiagram.layoutDiagram(true);
    },
    initMyDiagram(backgroundImg) {
      var that = this;

      const clickHandler = function (e, node) {
        that.nowData = node.data;
      };

      this.myDiagram = initDiagram({
        containerId: "myDiagramDiv",
        gridSize: { width: 50, height: 50 },
        isReadOnly: false,
        showGrid: true,
        clickHandler,
        backgroundImg: typeof backgroundImg === 'string' && backgroundImg !== '' ? backgroundImg : null,
      });

      this.myDiagram.addChangedListener(function (event) {
        that.isModified = true;
      });

      this.myDiagram.commandHandler.deleteSelection = function () {
        const diagram = this.diagram;
        const count = diagram.selection.count;
        if (count === 0) return;
        
        that.$modal.confirm('是否确认删除选中的' + count + '个节点？').then(function () {
          diagram.startTransaction("delete selection");
          diagram.selection.each(function (part) {
            if (part instanceof go.Node) {
              diagram.model.removeNodeData(part.data);
            } else if (part instanceof go.Link) {
              diagram.model.removeLinkData(part.data);
            }
          });
          diagram.commitTransaction("delete selection");
        }).catch(function () {});
      };

      const paletteClickHandler = function (e, node) {
        getSource(node.part.data.id).then((response) => {
          if (response.code == 200) {
            that.positionSource = response.data;
          }
        });
      };

      this.myPalette = initPalette("myPaletteDiv", paletteClickHandler);
    },
    loadData(data) {
      loadDiagramData(this.myDiagram, data);
    },
  },
};
</script>

<style lang="scss" scoped>
.diagram-page {
  --diagram-toolbar-gap: 10px;
  --diagram-canvas-height: calc(100vh - 130px);
  --diagram-border: rgba(15, 23, 42, 0.12);
  --diagram-shadow: 0 10px 30px rgba(2, 6, 23, 0.12);
  --diagram-panel-bg: rgba(255, 255, 255, 0.9);
  --diagram-primary: #3b82f6;
  --diagram-primary-hover: #2563eb;
  --diagram-secondary: #6b7280;
  --diagram-success: #10b981;
  --diagram-warning: #f59e0b;
  --diagram-danger: #ef4444;

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

.diagram-wareInput {
  min-width: 220px;
  border-radius: 8px;
}

.diagram-toolbarActions {
  margin-left: auto;
  display: flex;
  gap: 8px;
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

.diagram-btn-secondary {
  background-color: rgba(15, 23, 42, 0.06);
  border-color: rgba(15, 23, 42, 0.12);
  color: rgba(15, 23, 42, 0.75);
}

.diagram-btn-secondary:hover {
  background-color: rgba(15, 23, 42, 0.1);
  border-color: rgba(15, 23, 42, 0.18);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.15);
}

.diagram-layout {
  align-items: stretch;
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

.diagram-sourcePanel {
  height: calc(var(--diagram-canvas-height) + 20px);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.diagram-sourceHeader {
  border-radius: 10px;
  border: 1px solid var(--diagram-border);
  overflow: hidden;
  flex-shrink: 0;
}

.diagram-sourceForm {
  padding: 10px;
}

.diagram-sourceValue {
  color: rgba(15, 23, 42, 0.84);
  font-size: 13px;
}

.diagram-mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono",
    "Courier New", monospace;
}

.diagram-sourceActions {
  display: flex;
  gap: 8px;
  justify-content: flex-start;
}

.diagram-actionBtn {
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.2s ease;
}

.diagram-actionBtn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.diagram-sourcePalette {
  flex: 1;
  border-radius: 10px;
  border: 1px solid var(--diagram-border);
  overflow: hidden;
}

.diagram-palette {
  height: 100%;
  min-height: 480px;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 8px;
  padding: 8px;
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
}

.diagram-bindPanel {
  height: calc(var(--diagram-canvas-height) + 20px);
}

.diagram-bindContainer {
  height: 100%;
  border: 1px solid var(--diagram-border);
  border-radius: 10px;
  overflow: hidden;
}

.diagram-bindTabs {
  height: 100%;
}

.diagram-bindForm {
  padding: 12px;
  text-align: center;
}

.diagram-bindSelect {
  min-width: 100%;
  border-radius: 8px;
}

.diagram-bindDescriptions {
  margin-top: 10px;
  border-radius: 8px;
}

.diagram-bindEmpty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: rgba(15, 23, 42, 0.5);
  font-size: 13px;
  background: rgba(255, 255, 255, 0.4);
}

.diagram-sourceDialog {
  border-radius: 16px;
}

.diagram-sourceDialogForm {
  padding: 20px 0;
}

.el-form-item {
  margin-bottom: 16px;
}

@media (max-width: 1366px) {
  .diagram-page {
    --diagram-canvas-height: calc(100vh - 150px);
  }
  .diagram-wareInput {
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
  .diagram-layout .el-col {
    margin-bottom: 10px;
  }
  .diagram-palette {
    min-height: 200px;
  }
  .diagram-sourcePanel {
    height: auto;
    min-height: 200px;
  }
  .diagram-bindPanel {
    height: auto;
    min-height: 200px;
    max-height: 300px;
  }
}

@media (max-width: 480px) {
  .diagram-page {
    --diagram-canvas-height: calc(50vh);
    padding: 8px;
  }
  .diagram-toolbarForm {
    flex-direction: column;
    align-items: stretch;
  }
  .diagram-wareInput {
    min-width: 100%;
  }
  .diagram-toolbarActions {
    justify-content: center;
  }
  .diagram-sourceHeader {
    padding: 8px;
  }
  .diagram-sourceForm {
    padding: 8px;
  }
  .diagram-sourceActions {
    justify-content: center;
  }
}
</style>