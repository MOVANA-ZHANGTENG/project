<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" placeholder="请选择仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="库区编码" prop="areaCode">
        <el-input v-model="queryParams.areaCode" placeholder="请输入库区编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="类型" clearable>
          <el-option v-for="item in taskTypes" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="排序" prop="jobIndex">
        <el-input v-model="queryParams.jobIndex" placeholder="请输入排序" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:TaskDefine:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:TaskDefine:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:TaskDefine:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:TaskDefine:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-set-up" size="mini" @click="handleOpenFlowEditor">流程设计</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-switch-button" size="mini" @click="handleClose">关闭</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="TaskDefineList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="库区编码" align="center" prop="areaCode" min-width="120">
      </el-table-column> -->
      <el-table-column prop="type" align="center" label="类型" min-width="150">
        <template slot-scope="scope">
          <span v-for="item in taskTypes" v-if="scope.row.type == item.code">{{ item.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="排序" align="center" prop="jobIndex" min-width="100">
      </el-table-column>

      <el-table-column label="执行条件" align="left" prop="cmdPreList" min-width="180">
        <template slot-scope="scope">
          <el-tag size="mini" v-for="item in scope.row.cmdPreList">{{
            item.name
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="执行命令" align="left" prop="cmdList" min-width="180">
        <template slot-scope="scope">
          <el-tag size="mini" v-for="item in scope.row.cmdList">{{
            item.name
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="成功条件" align="left" prop="successPreList" min-width="180">
        <template slot-scope="scope">
          <el-tag size="mini" v-for="item in scope.row.successPreList">{{
            item.name
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="成功回调" align="left" prop="successList" min-width="180">
        <template slot-scope="scope">
          <el-tag size="mini" v-for="item in scope.row.successList">{{
            item.name
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="起点位置" align="center" prop="fromCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="终点位置" align="center" prop="toCellCode" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:TaskDefine:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:TaskDefine:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务定义对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="仓库" prop="wareCode">
          <el-select v-model="form.wareCode" placeholder="仓库" clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="库区编码" prop="areaCode">
          <el-input v-model="form.areaCode" placeholder="请输入库区编码" />
        </el-form-item> -->
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="form.type" placeholder="类型" clearable>
            <el-option v-for="dict in taskTypes" :key="dict.code + ''" :label="dict.name" :value="dict.code + ''" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="排序" prop="jobIndex">
          <el-input-number v-model="form.jobIndex"   :min="1" :max="20" label="排序"></el-input-number>

          <!-- <el-input v-model="form.jobIndex" placeholder="请输入排序" /> -->
        </el-form-item>
        <el-form-item label="起点位置" prop="fromCellCode">
          <el-input v-model="form.fromCellCode" placeholder="请输入起点位置" />
        </el-form-item>
        <el-form-item label="终点位置" prop="toCellCode">
          <el-input v-model="form.toCellCode" placeholder="请输入终点位置" />
        </el-form-item>
        <el-form-item label="执行条件" prop="cmdPreList">
          <HandleInfo :handleType="0" :type="0" v-model="form.cmdPreList" />
        </el-form-item>
        <el-form-item label="执行命令" prop="cmdList">
          <HandleInfo :handleType="1" :type="0" v-model="form.cmdList" />
        </el-form-item>
        <el-form-item label="成功条件" prop="successPreList">
          <HandleInfo :handleType="2" :type="0" v-model="form.successPreList" />
        </el-form-item>
        <el-form-item label="成功回调" prop="successList">
          <HandleInfo :handleType="3" :type="0" v-model="form.successList" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="步骤设置" :visible.sync="stepSetting.open" width="70%" center>
      <div>
        <div style="margin: 3%; font-size: medium">
          <span v-if="stepSetting.cmdIndex == 1">前置条件：</span>
          <span v-if="stepSetting.cmdIndex == 2">执行函数：</span>
          <span v-if="stepSetting.cmdIndex == 3">成功条件：</span>
          <span v-if="stepSetting.cmdIndex == 4">成功回调：</span>
          <el-button style="float: right" type="primary" @click="
            addMethod.open = true;
          getAllHandlers();
          " plain>添加方法
          </el-button>
        </div>
        <el-card style="margin: 3%">
          <el-table :data="checkedMethods" border stripe style="width: 100%" height="250"
            :default-sort="{ prop: 'id', order: 'asc' }">
            <el-table-column prop="id" label="ID"> </el-table-column>
            <el-table-column prop="code" label="方法编码"> </el-table-column>
            <el-table-column prop="name" label="方法名称"> </el-table-column>
            <el-table-column prop="className" label="类名"> </el-table-column>
            <el-table-column prop="methodName" label="方法名">
            </el-table-column>
            <el-table-column prop="jobIndex" sortable label="执行顺序">
              <template slot-scope="scope">
                <el-input-number size="mini" v-model="scope.row.jobIndex" :min="1"></el-input-number>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="stepSetting.open = false">取 消</el-button>
        <el-button type="primary" @click="setMethodList()">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="添加方法" :visible.sync="addMethod.open" center>
      <div>
        <div style="margin: 3%; font-size: medium">添加方法</div>
        <el-card style="margin: 3%">
          <el-checkbox-group id="checkBoxGroup" v-model="addMethod.methodList">
            <el-checkbox v-for="item in methodList" :label="item.code" :key="item.code">{{
              item.name + ":" + item.className + "." + item.methodName
            }}</el-checkbox>
          </el-checkbox-group>
        </el-card>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addMethod.open = false">取 消</el-button>
        <el-button type="primary" @click="checkMethods()">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 流程设计器对话框 -->
    <el-dialog 
      title="任务流程设计" 
      :visible.sync="flowEditorVisible" 
      width="98%" 
      top="1vh"
      :close-on-click-modal="false"
      :append-to-body="true"
      fullscreen
    >
      <TaskFlowEditor 
        v-if="flowEditorVisible"
        :taskTypeCode="taskType.code"
        :wareCode="taskType.wareCode"
        @close="flowEditorVisible = false"
      />
    </el-dialog>

  </div>
</template>

<script>
import {
  listTaskDefine,
  getTaskDefine,
  delTaskDefine,
  addTaskDefine,
  updateTaskDefine,
} from "@/api/wcs-base/TaskDefine";
import { listWareInfo } from "@/api/wcs-base/WareInfo";
import HandleInfo from "../Handle/HandleInfo";
import { listTaskType, getTaskType } from "@/api/wcs-base/taskType";
import TaskFlowEditor from "./TaskFlowEditor";

export default {
  name: "TaskDefine",
  dicts: ["task_state"],
  data() {
    return {
      taskTypes: [],
      wareCode: localStorage.getItem("wareCode"),
      nowData: {},
      stepSetting: {
        open: false,
        cmdIndex: null,
        cmdPreList: [],
        cmdList: [],
        successPreList: [],
        successList: [],
      },
      addMethod: {
        open: false,
        methodList: [],
      },
      methodList: [],
      checkedMethods: [],

      wareInfos: [],

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
      // 任务定义表格数据
      TaskDefineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wareCode: localStorage.getItem("wareCode"),
        areaCode: null,
        type: null,
        name: null,
        jobIndex: null,
      },
      // 表单参数
      form: {
        wareCode: localStorage.getItem("wareCode"),
      },
      taskType: {},
      // 表单校验
      rules: {},
      // 流程编辑器
      flowEditorVisible: false,
    };
  },
  components: {
    HandleInfo,
    TaskFlowEditor
  },
  created() {
    var typeId = this.$route.params.id;
    this.getTaskTypeDetail(typeId)
    this.getWareInfos();
    this.getAllTaskTypes()
  },
  methods: {
    handleClose() {
      const obj = { path: "/task/taskType" };
      this.$tab.closeOpenPage(obj);
    },
    //打开流程设计器
    handleOpenFlowEditor() {
      if (!this.taskType || !this.taskType.code) {
        this.$modal.msgError('请先选择任务类型');
        return;
      }
      this.flowEditorVisible = true;
    },
    //获取所有的任务类型
    getAllTaskTypes() {
      listTaskType({delFlag:0,pageSize:999}).then((response) => {
        if (response.code == 200) {
          this.taskTypes = response.rows
        }
      })
    },
    //获取任务类型
    getTaskTypeDetail(id) {
      getTaskType(id).then((response) => {
        if (response.code == 200) {
          this.taskType = response.data
          this.getList(this.taskType)
        }
      })
    },
    //获取所有仓库
    getWareInfos() {
      listWareInfo({isDelete:0}).then((response) => {
        if (response.code == 200) {
          this.wareInfos = response.rows;
        }
      });
    },
    /** 查询任务定义列表 */
    getList(taskType) {
      if (taskType != {} && taskType != null) {
        this.queryParams.wareCode = taskType.wareCode
        this.queryParams.type = taskType.code
        localStorage.setItem("wareCode", taskType.wareCode)
        localStorage.setItem("taskType", taskType.code)
      }
      this.loading = true;
      listTaskDefine(this.queryParams).then((response) => {
        this.TaskDefineList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
      // console.log(this.TaskDefineList);
      // console.log(this.form);
    },
    // 表单重置
    reset() {
      this.nowData = {};
      this.form = {
        id: null,
        wareCode: localStorage.getItem("wareCode"),
        areaCode: null,
        type: localStorage.getItem("taskType"),
        name: null,
        jobIndex: null,
        cmdPreList: [],
        cmdList: [],
        successPreList: [],
        successList: [],
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.getWareInfos();
      this.getAllTaskTypes()
      this.reset();
      this.open = true;
      this.title = "添加任务定义";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getWareInfos();
      this.getAllTaskTypes()
      this.reset();
      const id = row.id || this.ids;
      var list = this.TaskDefineList;
      for (var i = 0; i < list.length; i++) {
        if (id == list[i].id) {
          var aaa = list[i];
          this.form = JSON.parse(JSON.stringify(aaa));
        }
      }
      this.open = true;
      this.title = "修改任务定义";
      // getTaskDefine(id).then((response) => {
      //   this.form = response.data;
      //   for (var i = 0; i < list.length; i++) {
      //     if (id == list[i].id) {
      //       this.form.cmdPreList = list[i].cmdPreList
      //       this.form.cmdList = list[i].cmdList
      //       this.form.successPreList = list[i].successPreList
      //       this.form.successList = list[i].successList
      //     }
      //   }
      //   this.open = true;
      //   this.title = "修改任务定义";
      // });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateTaskDefine(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addTaskDefine(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "新增失败");
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
        .confirm('是否确认删除任务定义编号为"' + ids + '"的数据项？')
        .then(function () {
          return delTaskDefine(ids);
        })
        .then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("删除成功");
          } else {
            this.$modal.msgError("删除失败")
          }
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-base/TaskDefine/export",
        {
          ...this.queryParams,
        },
        `TaskDefine_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
