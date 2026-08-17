<template>
  <div class="task-flow-editor">
    <el-row :gutter="10" style="height: 100vh; margin: 0;">
      <!-- 左侧：流程画布 -->
      <el-col :span="18" style="height: 100vh; padding: 10px;">
        <el-card class="canvas-card" style="height: calc(100vh - 20px);">
          <div slot="header" class="clearfix card-header-custom">
            <div class="header-title">
              <i class="el-icon-s-grid"></i>
              <span>任务流程设计</span>
            </div>
            <div class="header-actions">
              <el-tooltip content="鼠标滚轮可缩放，右键点击空白处可新增节点" placement="bottom">
                <i class="el-icon-question help-icon"></i>
              </el-tooltip>
              <el-button 
                size="small" 
                class="back-btn" 
                @click="handleClose"
              >
                <i class="el-icon-back"></i>
                <span>返回列表</span>
              </el-button>
            </div>
          </div>
          <div id="taskDiagramDiv"></div>
        </el-card>
      </el-col>

      <!-- 右侧：步骤编辑面板 -->
      <el-col :span="6" style="height: 100vh; padding: 10px 10px 10px 0;">
        <el-card class="right-panel-card" :body-style="{ padding: '0' }" style="height: calc(100vh - 20px); overflow: hidden;">
          <div slot="header" class="clearfix">
            <span v-if="!selectedStep">步骤信息</span>
            <span v-else>编辑步骤 - {{ selectedStep.name }}</span>
            <el-button 
              v-if="selectedStep" 
              size="mini" 
              class="close-panel-btn" 
              @click="closeEditPanel"
            >
              <i class="el-icon-close"></i>
            </el-button>
          </div>
          
          <!-- 未选择步骤时的提示 -->
          <div v-if="!selectedStep" class="empty-tips">
            <i class="el-icon-info" style="font-size: 48px; color: #6b7280; margin-bottom: 16px;"></i>
            <p style="color: #9ca3af; font-size: 14px;">请点击左侧步骤卡片或"+"按钮来编辑步骤信息</p>
          </div>
          
          <!-- 步骤编辑表单 -->
          <div v-loading="loading || saving" element-loading-text="加载中..." v-if="selectedStep" class="step-edit-container" style="height: calc(100vh - 95px);">
            <el-form ref="stepForm" :model="selectedStep" :rules="stepRules" label-width="100px" class="step-form">
              <!-- 基本信息 - 始终显示 -->
              <div class="basic-info-section">
                <div class="section-title">
                  <i class="el-icon-document"></i>
                  <span>基本信息</span>
                </div>
                <div class="basic-info-content">
                  <el-form-item label="步骤名称" prop="name">
                    <el-input v-model="selectedStep.name" placeholder="请输入步骤名称" size="small" />
                  </el-form-item>
                  <el-form-item label="步骤类型">
                    <el-checkbox v-model="selectedStep.isJudgeStep" :true-label="1" :false-label="0">
                      <span style="font-weight: 500;">判断步骤</span>
                    </el-checkbox>
                    <!-- <div class="help-text">
                      <i class="el-icon-info"></i>
                      判断步骤可以设置两个分支：成功和失败
                    </div> -->
                  </el-form-item>
                  <el-form-item label="排序" prop="jobIndex">
                    <el-input-number v-model="selectedStep.jobIndex" :min="1" :max="100" size="small" style="width: 100%"></el-input-number>
                  </el-form-item>
                  <!-- <el-form-item label="起点位置" prop="fromCellCode">
                    <el-input v-model="selectedStep.fromCellCode" placeholder="请输入起点位置" size="small" />
                  </el-form-item>
                  <el-form-item label="终点位置" prop="toCellCode">
                    <el-input v-model="selectedStep.toCellCode" placeholder="请输入终点位置" size="small" />
                  </el-form-item> -->
                </div>
              </div>

              <!-- 高级配置 - 手风琴 -->
              <div class="advanced-config-section">
                <div class="section-title">
                  <i class="el-icon-setting"></i>
                  <span>高级配置</span>
                </div>

                <!-- 执行条件 -->
                <div class="form-group">
                  <div class="group-header" @click="toggleGroup('cmdPre')">
                    <i :class="groupCollapsed.cmdPre ? 'el-icon-arrow-right' : 'el-icon-arrow-down'"></i>
                    <span>执行条件</span>
                    <span class="group-badge" v-if="selectedStep.cmdPreList && selectedStep.cmdPreList.length">{{ selectedStep.cmdPreList.length }}</span>
                  </div>
                  <div class="group-content" v-show="!groupCollapsed.cmdPre">
                    <HandleInfo :handleType="0" :type="0" v-model="selectedStep.cmdPreList" />
                  </div>
                </div>

                <!-- 执行命令 -->
                <div class="form-group">
                  <div class="group-header" @click="toggleGroup('cmd')">
                    <i :class="groupCollapsed.cmd ? 'el-icon-arrow-right' : 'el-icon-arrow-down'"></i>
                    <span>执行命令</span>
                    <span class="group-badge" v-if="selectedStep.cmdList && selectedStep.cmdList.length">{{ selectedStep.cmdList.length }}</span>
                  </div>
                  <div class="group-content" v-show="!groupCollapsed.cmd">
                    <HandleInfo :handleType="1" :type="0" v-model="selectedStep.cmdList" />
                  </div>
                </div>

                <!-- 成功条件 -->
                <div class="form-group">
                  <div class="group-header" @click="toggleGroup('successPre')">
                    <i :class="groupCollapsed.successPre ? 'el-icon-arrow-right' : 'el-icon-arrow-down'"></i>
                    <span>成功条件</span>
                    <span class="group-badge" v-if="selectedStep.successPreList && selectedStep.successPreList.length">{{ selectedStep.successPreList.length }}</span>
                  </div>
                  <div class="group-content" v-show="!groupCollapsed.successPre">
                    <HandleInfo :handleType="2" :type="0" v-model="selectedStep.successPreList" />
                  </div>
                </div>

                <!-- 成功回调 -->
                <div class="form-group">
                  <div class="group-header" @click="toggleGroup('success')">
                    <i :class="groupCollapsed.success ? 'el-icon-arrow-right' : 'el-icon-arrow-down'"></i>
                    <span>成功回调</span>
                    <span class="group-badge" v-if="selectedStep.successList && selectedStep.successList.length">{{ selectedStep.successList.length }}</span>
                  </div>
                  <div class="group-content" v-show="!groupCollapsed.success">
                    <HandleInfo :handleType="3" :type="0" v-model="selectedStep.successList" />
                  </div>
                </div>

                <!-- 任务删除执行器 -->
                <div class="form-group">
                  <div class="group-header" @click="toggleGroup('delete')">
                    <i :class="groupCollapsed.delete ? 'el-icon-arrow-right' : 'el-icon-arrow-down'"></i>
                    <span>任务删除执行器</span>
                    <span class="group-badge" v-if="selectedStep.deleteList && selectedStep.deleteList.length">{{ selectedStep.deleteList.length }}</span>
                  </div>
                  <div class="group-content" v-show="!groupCollapsed.delete">
                    <HandleInfo :handleType="4" :type="0" v-model="selectedStep.deleteList" />
                  </div>
                </div>
              </div>
            </el-form>
            
            <!-- 操作按钮 - 固定在底部 -->
            <div class="form-actions">
              <el-button @click="copyStep" :disabled="saving" size="mini" class="compact-btn" v-if="selectedStep && selectedStep.id">
                <i class="el-icon-copy-document"></i> 复制
              </el-button>
              <el-button type="primary" @click="saveStep" :loading="saving" size="mini" class="compact-btn">
                <i class="el-icon-check"></i> 保存
              </el-button>
              <el-button @click="cancelEdit" :disabled="saving" size="mini" class="compact-btn">
                <i class="el-icon-close"></i> 取消
              </el-button>
              <el-button type="danger" @click="deleteStep" :disabled="saving" size="mini" class="compact-btn" v-if="selectedStep && selectedStep.id">
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import go from "@/lib/js/go.js";
import { listTaskDefine, getTaskDefine, getTaskDefineDetail, delTaskDefine, addTaskDefine, updateTaskDefine, updateTaskDefineLink, updateTaskDefineLink2, deleteTaskDefineLink, updateStepPosition } from "@/api/wcs-base/TaskDefine";
import HandleInfo from "../Handle/HandleInfo";

export default {
  name: "TaskFlowEditor",
  components: {
    HandleInfo
  },
  props: {
    taskTypeCode: {
      type: String,
      required: true
    },
    wareCode: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      myDiagram: null,
      taskSteps: [], // 任务步骤列表
      selectedStep: null,
      selectedStepId: null,
      saving: false,
      loading: false, // 加载步骤详情的loading状态
      stepRules: {
        name: [
          { required: true, message: "步骤名称不能为空", trigger: "blur" }
        ]
      },
      // 分组折叠状态
      groupCollapsed: {
        cmdPre: true,
        cmd: true,
        successPre: true,
        success: true,
        delete: true
      },
      // 视图状态保存
      savedViewState: {
        scale: 1,
        position: { x: 0, y: 0 }
      },
      // 位置保存防抖定时器
      positionSaveTimer: null,
      // 最大加载步骤数量
      maxStepCount: 200
    };
  },
  mounted() {
    this.initDiagram();
    // 首次加载不保存视图状态
    this.loadTaskSteps(false);
    // 添加窗口大小变化监听
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    if (this.myDiagram) {
      this.myDiagram.div = null;
    }
    if (this.positionSaveTimer) {
      clearTimeout(this.positionSaveTimer);
    }
    // 移除窗口大小变化监听
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    /**
     * 初始化go.js图表
     */
    initDiagram() {
      var that = this;
      var $ = go.GraphObject.make;
      
      const container = document.getElementById("taskDiagramDiv");
      if (!container) {
        console.error("GoJS container element not found");
        return;
      }
      
      this.myDiagram = $(
        go.Diagram,
        container,
        {
          "undoManager.isEnabled": true,
          isReadOnly: false,
          // 启用鼠标滚轮缩放
          "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom,
          allowZoom: true,
          // 设置缩放范围
          maxScale: 3,
          minScale: 0.3
        }
      );

      // 定义任务步骤节点模板
      this.myDiagram.nodeTemplateMap.add("taskStep", $(
        go.Node, "Spot",
        {
          selectable: true,
          deletable: false,
          copyable: false,
          movable: true,
          selectionAdorned: true,
          category: "taskStep",
          click: function (e, node) {
            that.myDiagram.startTransaction('selectStep');
            that.selectStep(node.data);
            that.myDiagram.commitTransaction('selectStep');
          },
          cursor: "pointer"
        },
        new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
        
        // 主面板
        $(
          go.Panel, "Spot",
          {
            name: "MAINPANEL"
          },
          // 外形
          $(
            go.Shape, "RoundedRectangle",
            {
              name: "SHAPE",
              fill: "#2d3748",
              stroke: "#5a8dee",
              strokeWidth: 2,
              cursor: "pointer",
              desiredSize: new go.Size(200, 80)
            },
            new go.Binding("fill", "fillColor"),
            new go.Binding("stroke", "borderColor")
          ),
          // 内容面板
          $(
            go.Panel, "Table",
            {
              margin: 10,
              defaultAlignment: go.Spot.Left,
              width: 180,
              alignment: go.Spot.Center
            },
            // 步骤名称
            $(
              go.TextBlock,
              {
                name: "NAMETB",
                font: "bold 12pt Microsoft YaHei, Arial",
                stroke: "#ffffff",
                row: 0,
                column: 0,
                columnSpan: 2,
                margin: new go.Margin(0, 0, 8, 0),
                maxLines: 1,
                overflow: go.TextOverflow.Ellipsis,
                width: 160
              },
              new go.Binding("text", "name", function(val) { return val || "未命名步骤"; })
            ),
            // 排序
            $(
              go.TextBlock, "排序: ",
              {
                font: "9pt Microsoft YaHei, Arial",
                stroke: "#a8b4c0",
                row: 1,
                column: 0,
                margin: new go.Margin(0, 0, 0, 0)
              }
            ),
            $(
              go.TextBlock,
              {
                font: "bold 9pt Microsoft YaHei, Arial",
                stroke: "#e8f0f8",
                row: 1,
                column: 1,
                margin: new go.Margin(0, 0, 0, 0),
                maxLines: 1,
                overflow: go.TextOverflow.Ellipsis,
                width: 100
              },
              new go.Binding("text", "jobIndex", function(val) { return val || "无"; })
            )
          ),
          
          // 入口端口（顶部）
          $(
            go.Shape, "Circle",
            {
              name: "INPORT",
              portId: "INPORT",
              fill: "#409eff",
              stroke: "#ffffff",
              strokeWidth: 2,
              width: 12,
              height: 12,
              alignment: go.Spot.Top,
              alignmentFocus: go.Spot.Center,
              margin: new go.Margin(6, 0, 0, 0),
              fromSpot: go.Spot.Top,  // 从顶部方向连出（如果需要）
              fromLinkable: false,
              toSpot: go.Spot.Top,  // 从顶部方向连入
              toLinkable: true,
              toMaxLinks: 999,
              cursor: "pointer"
            }
          ),
          
          // 出口端口（底部）
          $(
            go.Shape, "Circle",
            {
              name: "OUTPORT",
              portId: "OUTPORT",
              fill: "#409eff",
              stroke: "#ffffff",
              strokeWidth: 2,
              width: 12,
              height: 12,
              alignment: go.Spot.Bottom,
              alignmentFocus: go.Spot.Center,
              margin: new go.Margin(0, 0, 6, 0),
              fromSpot: go.Spot.Bottom,  // 从底部方向连出
              fromLinkable: true,
              toSpot: go.Spot.Bottom,  // 从底部方向连入（如果需要）
              toLinkable: false,
              fromMaxLinks: 1,
              cursor: "pointer"
            }
          )
        ),
        
        // 鼠标悬停效果
        {
          mouseEnter: function (e, node) {
            var shape = node.findObject("SHAPE");
            if (shape) {
              shape.fill = "#3f4d5f";
              shape.stroke = "#7aa3ff";
              shape.strokeWidth = 3;
            }
          },
          mouseLeave: function (e, node) {
            var shape = node.findObject("SHAPE");
            if (shape) {
              shape.fill = node.data.fillColor || "#2d3748";
              shape.stroke = node.data.borderColor || "#5a8dee";
              shape.strokeWidth = 2;
            }
          }
        }
      ));

      // 判断步骤节点模板（菱形）
      this.myDiagram.nodeTemplateMap.add("judgeStep", $(
        go.Node, "Spot",
        {
          selectable: true,
          deletable: false,
          copyable: false,
          movable: true,
          selectionAdorned: true,
          category: "judgeStep",
          click: function (e, node) {
            that.myDiagram.startTransaction('selectStep');
            that.selectStep(node.data);
            that.myDiagram.commitTransaction('selectStep');
          },
          cursor: "pointer"
        },
        new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
        
        // 主面板
        $(
          go.Panel, "Spot",
          {
            name: "MAINPANEL"
          },
          // 外形：菱形
          $(
            go.Shape, "Diamond",
            {
              name: "SHAPE",
              fill: "#3d3320",
              stroke: "#f6ad55",
              strokeWidth: 2,
              cursor: "pointer",
              desiredSize: new go.Size(180, 140)
            },
            new go.Binding("fill", "fillColor"),
            new go.Binding("stroke", "borderColor")
          ),
          // 内容面板
          $(
            go.Panel, "Table",
            {
              margin: 10,
              defaultAlignment: go.Spot.Center,
              width: 140,
              alignment: go.Spot.Center
            },
            // 步骤名称
            $(
              go.TextBlock,
              {
                name: "NAMETB",
                font: "bold 12pt Microsoft YaHei, Arial",
                stroke: "#ffeaa7",
                row: 0,
                column: 0,
                columnSpan: 2,
                margin: new go.Margin(0, 0, 8, 0),
                maxLines: 2,
                overflow: go.TextOverflow.Ellipsis,
                width: 120,
                textAlign: "center"
              },
              new go.Binding("text", "name", function(val) { return val || "未命名判断"; })
            ),
            // 排序
            $(
              go.TextBlock,
              {
                font: "bold 10pt Microsoft YaHei, Arial",
                stroke: "#f0d8a8",
                row: 1,
                column: 0,
                columnSpan: 2,
                margin: new go.Margin(0, 0, 3, 0),
                textAlign: "center"
              },
              new go.Binding("text", "jobIndex", function(val) { return "排序: " + (val || "无"); })
            )
          ),
          
          // 入口端口（顶部）
          $(
            go.Shape, "Circle",
            {
              name: "INPORT",
              portId: "INPORT",
              fill: "#409eff",
              stroke: "#ffffff",
              strokeWidth: 2,
              width: 12,
              height: 12,
              alignment: go.Spot.Top,
              alignmentFocus: go.Spot.Center,
              margin: new go.Margin(6, 0, 0, 0),
              fromSpot: go.Spot.Top,
              fromLinkable: false,
              toSpot: go.Spot.Top,  // 从顶部方向连入
              toLinkable: true,
              toMaxLinks: 999,
              cursor: "pointer"
            }
          ),
          
          // 成功出口端口（左下）
          $(
            go.Shape, "Circle",
            {
              name: "SUCCESS_PORT",
              portId: "SUCCESS_PORT",
              fill: "#67c23a",
              stroke: "#ffffff",
              strokeWidth: 2,
              width: 12,
              height: 12,
              alignment: new go.Spot(0.25, 1),
              alignmentFocus: go.Spot.Center,
              margin: new go.Margin(0, 0, 6, 0),
              fromSpot: new go.Spot(0.25, 1),
              fromLinkable: true,
              toSpot: new go.Spot(0.25, 1),
              toLinkable: false,
              fromMaxLinks: 1,
              cursor: "pointer"
            }
          ),
          
          // 失败出口端口（右下）
          $(
            go.Shape, "Circle",
            {
              name: "FAIL_PORT",
              portId: "FAIL_PORT",
              fill: "#f56c6c",
              stroke: "#ffffff",
              strokeWidth: 2,
              width: 12,
              height: 12,
              alignment: new go.Spot(0.75, 1),
              alignmentFocus: go.Spot.Center,
              margin: new go.Margin(0, 0, 6, 0),
              fromSpot: new go.Spot(0.75, 1),
              fromLinkable: true,
              toSpot: new go.Spot(0.75, 1),
              toLinkable: false,
              fromMaxLinks: 1,
              cursor: "pointer"
            }
          )
        ),
        
        // 鼠标悬停效果
        {
          mouseEnter: function (e, node) {
            var shape = node.findObject("SHAPE");
            if (shape) {
              shape.fill = "#5a5330";
              shape.stroke = "#ffcc66";
              shape.strokeWidth = 3;
            }
          },
          mouseLeave: function (e, node) {
            var shape = node.findObject("SHAPE");
            if (shape) {
              shape.fill = node.data.fillColor || "#3d3320";
              shape.stroke = node.data.borderColor || "#f6ad55";
              shape.strokeWidth = 2;
            }
          }
        }
      ));

      // 新增步骤节点模板
      this.myDiagram.nodeTemplateMap.add("addStep", $(
        go.Node, "Auto",
        {
          selectable: true,
          deletable: false,
          copyable: false,
          movable: true,
          selectionAdorned: true,
          category: "addStep",
          click: function (e, node) {
            that.handleAddStepClick();
          },
          cursor: "pointer"
        },
        new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
        
        $(
          go.Shape, "Circle",
          {
            name: "SHAPE",
            fill: "rgba(102, 126, 234, 0.15)",
            stroke: "#667eea",
            strokeWidth: 2,
            cursor: "pointer",
            desiredSize: new go.Size(80, 80)
          }
        ),
        
        $(
          go.TextBlock,
          {
            font: "bold 52pt Microsoft YaHei, Arial",
            stroke: "#8b9aff",
            alignment: go.Spot.Center
          },
          new go.Binding("text", "name", function(val) { return val || "+"; })
        ),
        
        {
          mouseEnter: function (e, node) {
            var shape = node.findObject("SHAPE");
            if (shape) {
              shape.fill = "rgba(102, 126, 234, 0.25)";
              shape.stroke = "#764ba2";
              shape.strokeWidth = 3;
            }
          },
          mouseLeave: function (e, node) {
            var shape = node.findObject("SHAPE");
            if (shape) {
              shape.fill = "rgba(102, 126, 234, 0.15)";
              shape.stroke = "#667eea";
              shape.strokeWidth = 2;
            }
          }
        }
      ));

      // 设置默认模板
      this.myDiagram.nodeTemplate = this.myDiagram.nodeTemplateMap.get("taskStep");

      // 公共的连线选中装饰模板 - 金黄色外描边，增强选中效果
      var linkSelectionAdornment = $(
        go.Adornment, "Link",
        $(
          go.Shape,
          {
            stroke: "#ffcc00",
            strokeWidth: 10,
            fill: null
          }
        )
      );

      // 普通连线模板
      this.myDiagram.linkTemplate = $(
        go.Link,
        {
          routing: go.Link.Normal,
          curve: go.Link.Bezier,
          curviness: 10,
          selectable: true,
          toPortId: "INPORT",
          relinkableFrom: true,
          relinkableTo: true,
          toShortLength: 8,
          fromShortLength: 2,
          selectionAdorned: true,
          selectionAdornmentTemplate: linkSelectionAdornment
        },
        $(
          go.Shape,
          {
            name: "SHAPE",
            stroke: "#409eff",
            strokeWidth: 5,
            fill: null
          }
        ),
        $(
          go.Shape,
          {
            name: "ARROW",
            toArrow: "Triangle",
            stroke: "#409eff",
            strokeWidth: 5,
            fill: "#409eff",
            scale: 1.5
          }
        )
      );

      // 成功分支连线模板（绿色）
      this.myDiagram.linkTemplateMap.add("successLink", $(
        go.Link,
        {
          routing: go.Link.Normal,
          curve: go.Link.Bezier,
          curviness: 10,
          selectable: true,
          relinkableFrom: true,
          relinkableTo: true,
          toShortLength: 8,
          fromShortLength: 2,
          selectionAdorned: true,
          selectionAdornmentTemplate: linkSelectionAdornment
        },
        $(
          go.Shape,
          {
            name: "SHAPE",
            stroke: "#67c23a",
            strokeWidth: 5,
            fill: null
          }
        ),
        $(
          go.Shape,
          {
            name: "ARROW",
            toArrow: "Triangle",
            stroke: "#67c23a",
            strokeWidth: 5,
            fill: "#67c23a",
            scale: 1.5
          }
        ),
        $(
          go.TextBlock, "yes",
          {
            segmentOffset: new go.Point(0, -15),
            font: "bold 13px Microsoft YaHei, Arial",
            stroke: "#6fdb7b",
            background: "rgba(26, 26, 46, 0.95)",
            segmentOrientation: go.Link.OrientUpright
          }
        )
      ));

      // 失败分支连线模板（红色）
      this.myDiagram.linkTemplateMap.add("failLink", $(
        go.Link,
        {
          routing: go.Link.Normal,
          curve: go.Link.Bezier,
          curviness: 10,
          selectable: true,
          relinkableFrom: true,
          relinkableTo: true,
          toShortLength: 8,
          fromShortLength: 2,
          selectionAdorned: true,
          selectionAdornmentTemplate: linkSelectionAdornment
        },
        $(
          go.Shape,
          {
            name: "SHAPE",
            stroke: "#f56c6c",
            strokeWidth: 5,
            fill: null
          }
        ),
        $(
          go.Shape,
          {
            name: "ARROW",
            toArrow: "Triangle",
            stroke: "#f56c6c",
            strokeWidth: 5,
            fill: "#f56c6c",
            scale: 1.5
          }
        ),
        $(
          go.TextBlock, "no",
          {
            segmentOffset: new go.Point(0, -15),
            font: "bold 13px Microsoft YaHei, Arial",
            stroke: "#ff8787",
            background: "rgba(26, 26, 46, 0.95)",
            segmentOrientation: go.Link.OrientUpright
          }
        )
      ));

      // 监听拖拽结束事件
      var originalDoDeactivate = this.myDiagram.toolManager.draggingTool.doDeactivate;
      this.myDiagram.toolManager.draggingTool.doDeactivate = function() {
        var result = originalDoDeactivate.call(this);
        setTimeout(function() {
          if (that.myDiagram && that.myDiagram.selection.count > 0) {
            var selectedNode = that.myDiagram.selection.first();
            // 支持普通步骤和判断步骤
            if (selectedNode && (selectedNode.category === "taskStep" || selectedNode.category === "judgeStep")) {
              that.debouncedUpdateStepPosition(selectedNode.data);
            }
          }
        }, 100);
        return result;
      };

      // 保存连线工具的原始方法
      const linkingTool = this.myDiagram.toolManager.linkingTool;
      const originalInsertLink = linkingTool.insertLink;
      
      // 重写 insertLink 方法以捕获端口信息
      linkingTool.insertLink = function(fromnode, fromport, tonode, toport) {
        const result = originalInsertLink.call(this, fromnode, fromport, tonode, toport);
        
        if (result && fromport && result.data) {
          that.myDiagram.model.setDataProperty(result.data, 'fromPort', fromport.portId);
          that.myDiagram.model.setDataProperty(result.data, 'toPort', toport ? toport.portId : 'INPORT');
        }
        
        return result;
      };

      // 监听连线变化事件（添加/删除/重新连接连线）
      this.myDiagram.addDiagramListener("LinkDrawn", function(e) {
        that.handleLinkChanged(e.subject);
      });
      
      this.myDiagram.addDiagramListener("LinkRelinked", function(e) {
        that.handleLinkChanged(e.subject);
      });
      
      // 监听选中内容变化（用于删除连线）
      this.myDiagram.addDiagramListener("SelectionDeleted", function(e) {
        e.subject.each(function(part) {
          if (part instanceof go.Link) {
            // console.log('连线被删除:', part);
            that.handleLinkDeleted(part);
          }
        });
      });

      // 监听右键点击事件
      this.myDiagram.addDiagramListener("BackgroundContextClicked", function(e) {
        // 获取点击位置
        var clickPoint = that.myDiagram.lastInput.documentPoint;
        // console.log('右键点击位置:', clickPoint);
        that.handleRightClickAddStep(clickPoint);
      });
    },

    /**
     * 保存当前视图状态
     */
    saveViewState() {
      if (!this.myDiagram) return;
      
      this.savedViewState = {
        scale: this.myDiagram.scale,
        position: {
          x: this.myDiagram.position.x,
          y: this.myDiagram.position.y
        }
      };
      // console.log('💾 保存视图状态:', this.savedViewState);
    },

    /**
     * 恢复视图状态
     */
    restoreViewState() {
      if (!this.myDiagram) return;
      
      // 使用 setTimeout 确保在模型完全渲染后恢复视图
      this.$nextTick(() => {
        setTimeout(() => {
          if (this.savedViewState.scale && this.savedViewState.position) {
            this.myDiagram.scale = this.savedViewState.scale;
            this.myDiagram.position = new go.Point(
              this.savedViewState.position.x,
              this.savedViewState.position.y
            );
            // console.log('📍 恢复视图状态 - 缩放:', this.savedViewState.scale.toFixed(2), '位置:', 
            //            `(${this.savedViewState.position.x.toFixed(0)}, ${this.savedViewState.position.y.toFixed(0)})`);
          }
        }, 50);
      });
    },

    /**
     * 处理窗口大小变化
     */
    handleResize() {
      if (this.myDiagram) {
        this.myDiagram.requestUpdate();
      }
    },

    /**
     * 加载任务步骤
     */
    loadTaskSteps(keepViewState = true) {
      var that = this;
      
      // 保存当前视图状态
      if (keepViewState) {
        this.saveViewState();
      }
      
      listTaskDefine({
        wareCode: this.wareCode,
        type: this.taskTypeCode,
        pageNum: 1,
        pageSize: this.maxStepCount
      }).then((response) => {
        if (response.code == 200) {
          that.taskSteps = response.rows || [];
          that.createStepNodes(keepViewState);
        } else {
          that.$modal.msgError(response.msg || '加载步骤失败');
        }
      }).catch((error) => {
        // console.error('加载步骤出错:', error);
        that.$modal.msgError('加载步骤出错');
      });
    },

    /**
     * 创建步骤节点
     */
    createStepNodes(keepViewState = true) {
      if (!this.taskSteps || this.taskSteps.length === 0) {
        this.showAddStepNode();
        return;
      }

      const nodeDataArray = [];
      const linkDataArray = [];

      // 按排序排列步骤
      const sortedSteps = [...this.taskSteps].sort((a, b) => (a.jobIndex || 0) - (b.jobIndex || 0));

      sortedSteps.forEach((step, index) => {
        // 优先使用保存的位置，如果没有则使用默认位置
        let x, y;
        if (step.positionX != null && step.positionY != null) {
          x = step.positionX;
          y = step.positionY;
        } else {
          x = 300;
          y = 100 + index * 180; // 竖向排列，每个卡片间隔180px
        }
        
        const isJudgeStep = step.isJudgeStep == 1;

        const nodeData = {
          key: step.id,
          id: step.id,
          category: isJudgeStep ? "judgeStep" : "taskStep",
          name: step.name || (isJudgeStep ? '未命名判断' : '未命名步骤'),
          jobIndex: step.jobIndex,
          fromCellCode: step.fromCellCode || '',
          toCellCode: step.toCellCode || '',
          lastId: step.lastId || null,
          isJudgeStep: step.isJudgeStep || 0,
          judgeBranchType: step.judgeBranchType || null,
          positionX: x,
          positionY: y,
          cmdPreList: step.cmdPreList || [],
          cmdList: step.cmdList || [],
          successPreList: step.successPreList || [],
          successList: step.successList || [],
          deleteList: step.deleteList || [],
          fillColor: isJudgeStep ? "#3d3320" : this.getStepColor(index),
          borderColor: isJudgeStep ? "#f6ad55" : "#5a8dee",
          loc: `${x} ${y}`
        };

        nodeDataArray.push(nodeData);
      });

      // 根据 lastId 创建连线
      sortedSteps.forEach((step) => {
        if (step.lastId) {
          // 查找上一个步骤
          const prevStep = sortedSteps.find(s => s.id === step.lastId);
          if (prevStep) {
            const prevIsJudgeStep = prevStep.isJudgeStep == 1;
            
            if (prevIsJudgeStep && step.judgeBranchType) {
              // 上一步是判断步骤，根据 judgeBranchType 确定连线样式
              const branchType = step.judgeBranchType;
              linkDataArray.push({
                from: step.lastId,
                to: step.id,
                fromPort: branchType === 'yes' ? 'SUCCESS_PORT' : 'FAIL_PORT',
                toPort: "INPORT",
                category: branchType === 'yes' ? 'successLink' : 'failLink',
                branchType: branchType
              });
            } else {
              // 普通连线
              linkDataArray.push({
                from: step.lastId,
                to: step.id,
                fromPort: "OUTPORT",
                toPort: "INPORT"
              });
            }
          }
        }
      });

      // 添加新增步骤节点
      const lastY = sortedSteps.length > 0 ? 100 + sortedSteps.length * 180 : 100;
      nodeDataArray.push({
        key: 'add-step',
        id: null,
        name: '+',
        category: "addStep",
        loc: `300 ${lastY}`,
        isAddNode: true
      });

      // 更新图表
      const diagramData = {
        class: "GraphLinksModel",
        nodeDataArray: nodeDataArray,
        linkDataArray: linkDataArray
      };

      this.myDiagram.model = go.Model.fromJson(diagramData);
      
      // 恢复视图状态
      if (keepViewState) {
        this.restoreViewState();
      }
    },

    /**
     * 显示新增步骤节点
     */
    showAddStepNode() {
      const addNodeData = {
        key: 'add-step',
        name: '+',
        description: '新增步骤',
        fillColor: "#f0f9ff",
        borderColor: "#409eff",
        loc: "300 100",
        isAddNode: true,
        category: "addStep"
      };

      const diagramData = { class: "GraphLinksModel", nodeDataArray: [addNodeData], linkDataArray: [] };
      this.myDiagram.model = go.Model.fromJson(diagramData);
    },

    /**
     * 获取步骤颜色 - 深色模式优化版
     */
    getStepColor(index) {
      const colors = [
        "#2d3748", // 蓝灰
        "#2f4858", // 深青
        "#3a4557", // 靛蓝
        "#364851", // 青灰
        "#2d4a54", // 深青蓝
        "#3d4654"  // 石墨蓝
      ];
      return colors[index % colors.length];
    },

    /**
     * 处理新增步骤点击
     */
    handleAddStepClick() {
      // console.log('点击新增步骤');
      
      const newJobIndex = (this.taskSteps.length + 1);
      
      const newStep = {
        key: 'new-step',
        id: null,
        name: '',
        jobIndex: newJobIndex,
        fromCellCode: '',
        toCellCode: '',
        cmdPreList: [],
        cmdList: [],
        successPreList: [],
        successList: [],
        deleteList: []
      };
      
      // 使用统一的方法填充表单
      this.fillStepForm(newStep);
    },

    /**
     * 处理右键点击新增步骤
     */
    handleRightClickAddStep(clickPoint) {
      // console.log('右键点击新增步骤，位置:', clickPoint);
      
      // 计算新步骤的排序号
      const newJobIndex = (this.taskSteps.length + 1);
      
      // 创建新步骤数据
      const newStep = {
        key: 'new-step-' + Date.now(), // 使用时间戳生成唯一key
        id: null,
        name: '',
        jobIndex: newJobIndex,
        fromCellCode: '',
        toCellCode: '',
        positionX: clickPoint.x,
        positionY: clickPoint.y,
        cmdPreList: [],
        cmdList: [],
        successPreList: [],
        successList: [],
        deleteList: []
      };
      
      // 打开编辑面板
      this.fillStepForm(newStep);
      
      // 提示用户
      this.$message({
        message: '请在右侧面板填写新步骤信息',
        type: 'info',
        duration: 2000
      });
    },

    /**
     * 选择步骤
     */
    selectStep(step) {
      // console.log('选择步骤:', step);
      
      // 如果步骤有ID，则从后端获取最新数据
      if (step.id) {
        this.loadStepDetail(step.id);
      } else {
        // 如果是新增节点，直接使用本地数据
        this.fillStepForm(step);
      }
    },
    
    /**
     * 从后端加载步骤详情
     */
    loadStepDetail(stepId) {
      var that = this;
      // console.log('从后端加载步骤详情, ID:', stepId);
      
      // 设置loading状态
      this.loading = true;
      this.selectedStepId = stepId;
      
      // 先设置一个临时的selectedStep以显示loading
      this.selectedStep = {
        id: stepId,
        name: '加载中...'
      };
      
      // 使用新的接口获取完整数据（包含所有处理器列表）
      getTaskDefineDetail(stepId).then((response) => {
        that.loading = false;
        if (response.code == 200) {
          // console.log('获取步骤详情成功:', response.data);
          that.fillStepForm(response.data);
        } else {
          that.$modal.msgError(response.msg || '加载步骤详情失败');
          that.closeEditPanel();
        }
      }).catch((error) => {
        that.loading = false;
        // console.error('加载步骤详情出错:', error);
        that.$modal.msgError('加载步骤详情出错');
        that.closeEditPanel();
      });
    },
    
    /**
     * 填充步骤表单数据
     */
    fillStepForm(step) {
      // console.log('填充步骤表单:', step);
      
      this.selectedStepId = step.key || step.id;
      
      // 使用 $set 确保响应式更新，直接设置 selectedStep
      this.$set(this, 'selectedStep', {
        id: step.id,
        key: step.key || step.id,
        name: step.name || '',
        jobIndex: step.jobIndex || 1,
        fromCellCode: step.fromCellCode || '',
        toCellCode: step.toCellCode || '',
        lastId: step.lastId || null,
        isJudgeStep: step.isJudgeStep || 0,
        judgeBranchType: step.judgeBranchType || null,
        positionX: step.positionX != null ? step.positionX : null,
        positionY: step.positionY != null ? step.positionY : null,
        cmdPreList: JSON.parse(JSON.stringify(step.cmdPreList || [])),
        cmdList: JSON.parse(JSON.stringify(step.cmdList || [])),
        successPreList: JSON.parse(JSON.stringify(step.successPreList || [])),
        successList: JSON.parse(JSON.stringify(step.successList || [])),
        deleteList: JSON.parse(JSON.stringify(step.deleteList || []))
      });
      
      // console.log('selectedStep 更新为:', this.selectedStep);
      
      // 强制更新视图
      this.$nextTick(() => {
        if (this.$refs.stepForm) {
          this.$refs.stepForm.clearValidate();
        }
      });
    },

    /**
     * 关闭编辑面板
     */
    closeEditPanel() {
      // console.log('关闭编辑面板');
      this.selectedStep = null;
      this.selectedStepId = null;
      this.loading = false;
      
      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.stepForm) {
          this.$refs.stepForm.clearValidate();
        }
      });
    },

    /**
     * 切换分组折叠状态
     */
    toggleGroup(groupName) {
      // 切换指定分组的折叠状态
      this.$set(this.groupCollapsed, groupName, !this.groupCollapsed[groupName]);
    },

    /**
     * 取消编辑
     */
    cancelEdit() {
      this.closeEditPanel();
    },

    /**
     * 复制步骤并新增
     */
    copyStep() {
      if (!this.selectedStep || !this.selectedStep.id) {
        return;
      }
      
      const newStep = {
        ...this.selectedStep,
        id: null,
        key: 'new-step-' + Date.now(),
        name: this.selectedStep.name + ' (副本)',
        jobIndex: this.taskSteps.length + 1,
        positionX: this.selectedStep.positionX ? this.selectedStep.positionX + 50 : null,
        positionY: this.selectedStep.positionY ? this.selectedStep.positionY + 50 : null,
        lastId: null,
        judgeBranchType: null
      };
      
      this.fillStepForm(newStep);
      
      this.$message({
        message: '已复制步骤信息，请修改后保存',
        type: 'success',
        duration: 2000
      });
    },

    /**
     * 保存步骤
     */
    saveStep() {
      this.$refs.stepForm.validate(valid => {
        if (valid) {
          this.saving = true;
          
          // 使用 selectedStep 的数据
          const stepData = {
            ...this.selectedStep,
            wareCode: this.wareCode,
            type: this.taskTypeCode
          };
          
          if (stepData.id) {
            this.updateTaskStep(stepData);
          } else {
            this.addTaskStep(stepData);
          }
        }
      });
    },

    /**
     * 添加任务步骤
     */
    addTaskStep(stepData) {
      var that = this;
      addTaskDefine(stepData).then((response) => {
        setTimeout(() => {
          that.saving = false;
          if (response.code == 200) {
            that.$message.success('添加成功');
            that.closeEditPanel();
            that.loadTaskSteps();
          } else {
            that.$message.error('添加失败：' + (response.msg || '未知错误'));
          }
        }, 300);
      }).catch((error) => {
        setTimeout(() => {
          that.saving = false;
          that.$message.error('添加失败：' + (error.message || '未知错误'));
        }, 300);
      });
    },

    /**
     * 更新任务步骤
     */
    updateTaskStep(stepData) {
      var that = this;
      updateTaskDefine(stepData).then((response) => {
        setTimeout(() => {
          that.saving = false;
          if (response.code == 200) {
            that.$message.success('更新成功');
            // 重新加载列表
            that.loadTaskSteps();
            // 重新加载当前步骤的最新数据
            if (stepData.id) {
              that.loadStepDetail(stepData.id);
            }
          } else {
            that.$message.error('更新失败：' + (response.msg || '未知错误'));
          }
        }, 300);
      }).catch((error) => {
        setTimeout(() => {
          that.saving = false;
          that.$message.error('更新失败：' + (error.message || '未知错误'));
        }, 300);
      });
    },

    /**
     * 删除步骤
     */
    deleteStep() {
      const that = this;
      this.$modal.confirm('是否确认删除该步骤？').then(function () {
        return delTaskDefine(that.selectedStep.id);
      }).then((response) => {
        if (response.code == 200) {
          that.$message.success('删除成功');
          that.closeEditPanel();
          that.loadTaskSteps();
        } else {
          that.$message.error('删除失败：' + (response.msg || '未知错误'));
        }
      }).catch(() => {});
    },

    /**
     * 防抖更新步骤位置
     */
    debouncedUpdateStepPosition(stepData) {
      if (this.positionSaveTimer) {
        clearTimeout(this.positionSaveTimer);
      }
      this.positionSaveTimer = setTimeout(() => {
        this.updateStepPosition(stepData);
      }, 500);
    },

    /**
     * 更新步骤位置
     */
    updateStepPosition(stepData) {
      // console.log('更新步骤位置:', stepData);
      
      if (!stepData || !stepData.id) {
        return;
      }
      
      // 从 loc 字符串解析坐标
      const loc = stepData.loc;
      if (!loc) {
        return;
      }
      
      const parts = loc.split(' ');
      if (parts.length !== 2) {
        return;
      }
      
      const positionX = parseFloat(parts[0]);
      const positionY = parseFloat(parts[1]);
      
      // console.log(`📍 保存节点位置: ID=${stepData.id}, X=${positionX}, Y=${positionY}`);
      
      // 调用API保存位置
      updateStepPosition({
        stepId: stepData.id,
        positionX: positionX,
        positionY: positionY
      }).then((response) => {
        if (response.code == 200) {
          // console.log('✓ 位置保存成功');
        } else {
          // console.error('位置保存失败:', response.msg);
        }
      }).catch((error) => {
        // console.error('位置保存出错:', error);
      });
    },

    /**
     * 处理连线变化
     */
    handleLinkChanged(link) {
      var that = this;
      
      if (!link || !link.data) {
        return;
      }
      
      const fromNodeKey = link.data.from;
      const toNodeKey = link.data.to;
      
      const fromNode = this.myDiagram.findNodeForKey(fromNodeKey);
      if (!fromNode) {
        return;
      }
      
      let fromPort = null;
      
      if (link.fromPort && link.fromPort.portId) {
        fromPort = link.fromPort.portId;
      } else if (link.fromPortId && link.fromPortId !== '') {
        fromPort = link.fromPortId;
      } else if (fromNode.data.isJudgeStep == 1) {
        fromNode.ports.each(function(port) {
          if (port.fromLinkable && !fromPort) {
            fromPort = port.portId;
          }
        });
      } else {
        fromPort = 'OUTPORT';
      }
      
      this.myDiagram.commit(() => {
        that.processLinkWithPort(link, fromNode, toNodeKey, fromNodeKey, fromPort);
      }, 'handleLinkChanged');
    },

    /**
     * 处理已知端口的连线
     */
    processLinkWithPort(link, fromNode, toNodeKey, fromNodeKey, fromPort) {
      const fromNodeData = fromNode.data;
      const isJudgeStep = fromNodeData.isJudgeStep == 1;
      
      // 确定分支类型
      let branchType = null;
      if (isJudgeStep) {
        if (fromPort === 'SUCCESS_PORT') {
          branchType = 'yes';
        } else if (fromPort === 'FAIL_PORT') {
          branchType = 'no';
        }
        
        if (!branchType) {
          this.$message.error('判断步骤必须从成功或失败端口连线');
          this.myDiagram.remove(link);
          this.loadTaskSteps();
          return;
        }
        
        // 检查该端口是否已经有连线
        const existingLinks = [];
        this.myDiagram.links.each(l => {
          if (!l || !l.data) return; // 跳过无效的连线
          const lFromPort = l.fromPort && l.fromPort.portId ? l.fromPort.portId : (l.fromPortId || l.data.fromPort);
          if (l !== link && l.data.from === fromNodeKey && lFromPort === fromPort) {
            existingLinks.push(l);
          }
        });
        
        if (existingLinks.length > 0) {
          this.$message.warning(`${branchType === 'yes' ? 'yes' : 'no'}分支已存在连线`);
          this.myDiagram.remove(link);
          this.loadTaskSteps();
          return;
        }
      } else {
        // 普通步骤，检查是否已有出口连线
        const existingLinks = [];
        this.myDiagram.links.each(l => {
          if (!l || !l.data) return; // 跳过无效的连线
          if (l !== link && l.data.from === fromNodeKey) {
            existingLinks.push(l);
          }
        });
        
        if (existingLinks.length > 0) {
          this.$message.warning('普通步骤只能有一条出口连线');
          this.myDiagram.remove(link);
          this.loadTaskSteps();
          return;
        }
        
        // 普通步骤确保保存端口信息
        this.myDiagram.model.setDataProperty(link.data, 'fromPort', 'OUTPORT');
      }
      
      // 更新连线样式和数据
      if (branchType === 'yes' || branchType === 'no') {
        this.myDiagram.model.setDataProperty(link.data, 'category', branchType === 'yes' ? 'successLink' : 'failLink');
        this.myDiagram.model.setDataProperty(link.data, 'fromPort', branchType === 'yes' ? 'SUCCESS_PORT' : 'FAIL_PORT');
      }
      
      // 更新目标节点的 lastId 和 judgeBranchType
      this.updateStepLink(toNodeKey, fromNodeKey, branchType);
    },

    /**
     * 更新步骤连线（新方法，支持判断步骤）
     */
    updateStepLink(toStepId, fromStepId, branchType) {
      var that = this;
      const requestData = {
        fromStepId: fromStepId,
        toStepId: toStepId,
        branchType: branchType  // 如果from是判断步骤，传入branchType；否则为null
      };
      // console.log(`🔗 准备更新连线, 请求数据:`, requestData);
      
      // 调用新的更新连线接口（支持判断步骤）
      updateTaskDefineLink2(requestData).then((response) => {
        // console.log('API响应:', response);
        if (response.code == 200) {
          // console.log('更新连线成功');
          // 重新加载步骤列表
          that.loadTaskSteps();
        } else {
          that.$message.error('更新连线失败：' + (response.msg || '未知错误'));
          that.loadTaskSteps();
        }
      }).catch((error) => {
        // console.error('更新连线失败:', error);
        that.$message.error('更新连线失败：' + (error.message || '未知错误'));
        that.loadTaskSteps();
      });
    },

    /**
     * 更新步骤的 lastId（保留用于兼容）
     */
    updateStepLastId(stepId, lastId) {
      // 直接调用新方法，branchType为null表示普通连线
      this.updateStepLink(stepId, lastId, null);
    },

    /**
     * 处理连线删除
     */
    handleLinkDeleted(link) {
      // console.log('处理连线删除:', link);
      
      if (!link || !link.data) {
        return;
      }
      
      const toNodeKey = link.data.to;
      
      if (!toNodeKey) {
        return;
      }
      
      this.$modal.confirm('是否确认删除该连线？').then(() => {
        // 调用删除连线接口
        this.deleteStepLink(toNodeKey);
      }).catch(() => {
        // 用户取消，重新加载数据恢复连线
        this.loadTaskSteps();
      });
    },

    /**
     * 删除步骤连线
     */
    deleteStepLink(stepId) {
      var that = this;
      // console.log(`🗑️ 删除连线: stepId=${stepId}`);
      
      // 调用删除连线接口
      deleteTaskDefineLink(stepId).then((response) => {
        // console.log('删除连线API响应:', response);
        if (response.code == 200) {
          // console.log('删除连线成功');
          // 重新加载步骤列表
          that.loadTaskSteps();
        } else {
          that.$message.error('删除连线失败：' + (response.msg || '未知错误'));
          that.loadTaskSteps();
        }
      }).catch((error) => {
        // console.error('删除连线失败:', error);
        that.$message.error('删除连线失败：' + (error.message || '未知错误'));
        that.loadTaskSteps();
      });
    },

    /**
     * 返回列表
     */
    handleClose() {
      this.$emit('close');
    }
  }
};
</script>

<style lang="scss" scoped>
.task-flow-editor {
  background: #1a1a2e;
  min-height: 100vh;
  height: 100vh;
  padding: 0;
  overflow: hidden;
  
  .empty-tips {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: calc(100vh - 140px);
    padding: 20px;
    text-align: center;
  }
  
  .step-edit-container {
    height: calc(100vh - 80px);
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  
  .step-form {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    overflow-x: hidden;
    
    // 美化滚动条 - 纯色版
    &::-webkit-scrollbar {
      width: 8px;
    }
    
    &::-webkit-scrollbar-track {
      background: rgba(20, 25, 40, 0.5);
      border-radius: 4px;
      margin-bottom: 8px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: rgba(102, 126, 234, 0.5);
      border-radius: 4px;
      
      &:hover {
        background: rgba(102, 126, 234, 0.7);
      }
    }
  }

  // Element UI Card 深色覆盖 - 纯色版
  ::v-deep .el-card {
    background: rgba(30, 35, 50, 0.95);
    backdrop-filter: blur(5px);
    border: 1px solid rgba(102, 126, 234, 0.25);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
    border-radius: 12px;
    
    .el-card__header {
      background: rgba(25, 30, 45, 0.9);
      border-bottom: 1px solid rgba(102, 126, 234, 0.3);
      color: #ffffff;
      padding: 15px 20px;
      border-radius: 12px 12px 0 0;
      flex-shrink: 0;
    }
    
    .el-card__body {
      background: rgba(20, 25, 40, 0.85);
      color: #ffffff;
      padding: 20px;
    }
  }
  
  // 画布卡片特殊样式
  .canvas-card {
    display: flex !important;
    flex-direction: column !important;
    
    ::v-deep .el-card__body {
      flex: 1 !important;
      padding: 15px !important;
      overflow: hidden !important;
      display: flex !important;
      flex-direction: column !important;
      background: rgba(20, 25, 40, 0.85) !important;
    }
  }
  
  // 卡片头部自定义样式
  .card-header-custom {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .header-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 16px;
      font-weight: 700;
      color: #ffffff;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
      
      i {
        font-size: 18px;
        color: #667eea;
      }
    }
    
    .header-actions {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }
  
  // 帮助图标样式
  .help-icon {
    color: #a8b4c0;
    font-size: 18px;
    cursor: help;
    transition: all 0.3s ease;
    padding: 6px;
    border-radius: 50%;
    
    &:hover {
      color: #667eea;
      background: rgba(102, 126, 234, 0.15);
      transform: scale(1.1);
    }
  }
  
  // Go.js 画布容器
  #taskDiagramDiv {
    flex: 1 !important;
    width: 100% !important;
    min-height: 500px !important;
    background-color: #1a1d23 !important;
    border-radius: 8px;
    position: relative;
  }
  
  // 右侧面板卡片特殊样式
  .right-panel-card {
    display: flex;
    flex-direction: column;
    
    ::v-deep .el-card__body {
      flex: 1;
      padding: 0 !important;
      overflow: hidden;
    }
  }

  // 基本信息区域 - 纯色版
  .basic-info-section {
    flex: 0 0 auto;
    background: rgba(35, 40, 60, 0.9);
    border-radius: 8px;
    margin: 12px;
    padding: 16px;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
    border: 1px solid rgba(102, 126, 234, 0.3);
    
    .section-title {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      color: #ffffff;
      font-size: 15px;
      font-weight: 600;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
      
      i {
        font-size: 18px;
        margin-right: 8px;
        color: #667eea;
      }
    }
    
    .basic-info-content {
      background: rgba(20, 25, 40, 0.85);
      border-radius: 6px;
      padding: 16px;
      border: 1px solid rgba(102, 126, 234, 0.2);
      
      .el-form-item {
        margin-bottom: 16px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
      
      ::v-deep .el-form-item__label {
        color: #b8c5d1;
        font-weight: 600;
        font-size: 13px;
      }
      
      // 输入框边框设计 - 深色纯色版
      ::v-deep .el-input__inner {
        border: 1px solid rgba(102, 126, 234, 0.25);
        border-radius: 6px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        background-color: rgba(20, 25, 40, 0.9);
        color: #ffffff;
        font-weight: 500;
        font-size: 13px;
        
        &::placeholder {
          color: #6b7280;
          font-weight: 400;
        }
        
        &:hover {
          border-color: rgba(102, 126, 234, 0.4);
          background-color: rgba(25, 30, 45, 0.95);
        }
        
        &:focus {
          border-color: #667eea;
          background-color: rgba(25, 30, 45, 0.95);
          box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.15);
          
          &::placeholder {
            color: #9ca3af;
          }
        }
      }
      
      // 输入数字框边框设计 - 深色纯色版
      ::v-deep .el-input-number {
        width: 100%;
        
        .el-input__inner {
          border: 1px solid rgba(102, 126, 234, 0.25);
          border-radius: 6px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          background-color: rgba(20, 25, 40, 0.9);
          color: #ffffff;
          font-weight: 600;
          font-size: 14px;
          text-align: center;
          
          &:hover {
            border-color: rgba(102, 126, 234, 0.4);
            background-color: rgba(25, 30, 45, 0.95);
          }
          
          &:focus {
            border-color: #667eea;
            background-color: rgba(25, 30, 45, 0.95);
            box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.15);
          }
        }
        
        .el-input-number__decrease,
        .el-input-number__increase {
          border: none;
          background: transparent;
          color: #667eea;
          font-weight: 600;
          transition: all 0.2s;
          
          &:hover {
            color: #764ba2;
            background: rgba(102, 126, 234, 0.15);
          }
          
          &.is-disabled {
            color: #6b7280;
          }
        }
      }
      
      // 复选框设计 - 深色模式
      ::v-deep .el-checkbox {
        .el-checkbox__inner {
          border: 1px solid rgba(255, 255, 255, 0.2);
          border-radius: 4px;
          transition: all 0.3s;
          width: 16px;
          height: 16px;
          background: rgba(255, 255, 255, 0.05);
          
          &:hover {
            border-color: #667eea;
            background: rgba(255, 255, 255, 0.08);
          }
        }
        
        .el-checkbox__label {
          color: #ffffff;
          font-weight: 500;
          font-size: 13px;
          padding-left: 8px;
        }
        
        &.is-checked {
          .el-checkbox__inner {
            background-color: #667eea;
            border-color: #667eea;
          }
          
          .el-checkbox__label {
            color: #667eea;
            font-weight: 600;
          }
        }
        
        &:hover {
          .el-checkbox__label {
            color: #667eea;
          }
        }
      }
      
      .help-text {
        font-size: 12px;
        color: #9ca3af;
        margin-top: 6px;
        display: flex;
        align-items: center;
        line-height: 1.4;
        padding: 8px 12px;
        background: rgba(102, 126, 234, 0.1);
        border-left: 3px solid #667eea;
        border-radius: 4px;
        
        i {
          margin-right: 6px;
          font-size: 14px;
          color: #667eea;
        }
      }
    }
  }

  // 高级配置区域 - 手风琴 - 深色模式
  .advanced-config-section {
    flex: 0 0 auto;
    padding: 0 12px 20px 12px;
    
    .section-title {
      display: flex;
      align-items: center;
      margin-bottom: 12px;
      color: #ffffff;
      font-size: 14px;
      font-weight: 600;
      padding: 8px 0;
      border-bottom: 2px solid #667eea;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
      
      i {
        font-size: 16px;
        margin-right: 8px;
        color: #667eea;
      }
    }
  }

  .form-group {
    margin-bottom: 10px;
    border: 1px solid rgba(102, 126, 234, 0.2);
    border-radius: 6px;
    overflow: hidden;
    background: rgba(25, 30, 45, 0.9);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.4);
      border-color: rgba(102, 126, 234, 0.4);
    }
  }

  .group-header {
    display: flex;
    align-items: center;
    padding: 12px 14px;
    background: rgba(30, 35, 50, 0.9);
    cursor: pointer;
    transition: all 0.3s ease;
    user-select: none;
    position: relative;
    
    &:hover {
      background: rgba(102, 126, 234, 0.2);
    }
    
    i:first-child {
      margin-right: 10px;
      color: #667eea;
      font-size: 14px;
      transition: transform 0.3s ease;
    }
    
    span:first-of-type {
      flex: 1;
      font-weight: 600;
      color: #ffffff;
      font-size: 13px;
    }
    
    .group-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 20px;
      height: 20px;
      padding: 0 6px;
      background: #667eea;
      color: #ffffff;
      border-radius: 10px;
      font-size: 11px;
      font-weight: 600;
      margin-left: auto;
      box-shadow: 0 2px 4px rgba(102, 126, 234, 0.3);
    }
  }

  .group-content {
    padding: 14px;
    background: rgba(20, 25, 40, 0.8);
    border-top: 1px solid rgba(102, 126, 234, 0.2);
    
    // 移除固定高度和内部滚动，让内容自然展开
    // 由外层容器 .step-edit-container 统一处理滚动
    
    ::v-deep .el-form-item__label {
      color: #b8c5d1;
      font-weight: 500;
    }
    
    ::v-deep .el-input__inner {
      background-color: rgba(15, 20, 35, 0.9);
      color: #ffffff;
      border-color: rgba(102, 126, 234, 0.25);
    }
    
    ::v-deep .el-input-number__input {
      color: #ffffff;
    }
    
    .el-form-item {
      margin-bottom: 14px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  // 操作按钮 - 固定在底部 - 纯色版
  .form-actions {
    flex: 0 0 auto;
    padding: 8px 10px;
    background: rgba(25, 30, 45, 0.98);
    border-top: 2px solid rgba(102, 126, 234, 0.3);
    text-align: center;
    box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.4);
    z-index: 10;
    display: flex !important;
    flex-direction: row !important;
    flex-wrap: nowrap !important;
    justify-content: center;
    align-items: center;
    gap: 5px;
    
    ::v-deep .el-button {
      margin: 0 !important;
      font-weight: 500;
      border-radius: 4px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      flex-shrink: 0;
      display: inline-flex !important;
      align-items: center;
      justify-content: center;
      
      &.compact-btn {
        min-width: 55px;
        max-width: 80px;
        height: 28px;
        line-height: 1;
        padding: 0 8px !important;
        font-size: 12px;
        white-space: nowrap;
        
        i {
          margin-right: 2px;
          font-size: 12px;
        }
      }
      
      &.el-button--primary {
        background: #667eea;
        border-color: #667eea;
        color: #ffffff;
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
        
        &:hover {
          background: #764ba2;
          border-color: #764ba2;
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.5);
          transform: translateY(-1px);
        }
      }
      
      &.el-button--default {
        background: rgba(108, 117, 125, 0.8);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: #ffffff;
        backdrop-filter: blur(10px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
        
        &:hover {
          background: rgba(90, 98, 104, 0.9);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
          transform: translateY(-1px);
        }
      }
      
      &.el-button--danger {
        background: #f56c6c;
        border-color: #f56c6c;
        color: #ffffff;
        box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
        
        &:hover {
          background: #f78989;
          border-color: #f78989;
          box-shadow: 0 4px 12px rgba(245, 108, 108, 0.5);
          transform: translateY(-1px);
        }
      }
    }
  }
}

// 返回按钮样式 - 醒目纯色版
.back-btn {
  background: #667eea !important;
  border: none !important;
  color: #ffffff !important;
  padding: 10px 22px !important;
  border-radius: 8px !important;
  font-weight: 700 !important;
  font-size: 14px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.5);
  letter-spacing: 0.5px;
  
  i {
    margin-right: 6px;
    font-size: 15px;
    font-weight: bold;
  }
  
  span {
    font-weight: 700;
  }
  
  &:hover {
    background: #7a8ef5 !important;
    color: #ffffff !important;
    box-shadow: 0 6px 24px rgba(102, 126, 234, 0.6);
    transform: translateY(-2px) scale(1.05);
  }
  
  &:active {
    transform: translateY(0) scale(0.98);
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  }
}

// 关闭面板按钮样式
.close-panel-btn {
  float: right !important;
  background: rgba(245, 108, 108, 0.15) !important;
  border: 1px solid rgba(245, 108, 108, 0.4) !important;
  color: #ff8787 !important;
  padding: 6px 10px !important;
  border-radius: 6px !important;
  font-weight: 600 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  min-width: 32px;
  
  i {
    font-size: 14px;
    font-weight: bold;
  }
  
  &:hover {
    background: rgba(245, 108, 108, 0.25) !important;
    border-color: #ff8787 !important;
    color: #ffffff !important;
    box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
    transform: rotate(90deg) scale(1.1);
  }
  
  &:active {
    transform: rotate(90deg) scale(0.95);
  }
}

// 原有文本按钮样式
::v-deep .el-button--text {
  color: #667eea;
  transition: all 0.3s ease;
  
  &:hover {
    color: #ffffff;
    background: rgba(102, 126, 234, 0.1);
  }
}
</style>

