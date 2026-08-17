<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="任务编号" prop="taskNo">
        <el-input v-model="queryParams.taskNo" placeholder="请输入任务号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="WMS任务号" prop="wmsTaskNo">
        <el-input v-model="queryParams.wmsTaskNo" placeholder="请输入WMS任务号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode"
          @change="getTaskTypesAndPositionInfos(queryParams.wareCode); queryParams.type = null; queryParams.fromCellCode = null; queryParams.toCellCode = null;"
          placeholder="仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="库区编码" prop="areaCode">
        <el-input v-model="queryParams.areaCode" placeholder="请输入库区编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="库区名称" prop="areaName">
        <el-input v-model="queryParams.areaName" placeholder="请输入库区名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="任务类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
          <el-option v-for="item in taskTypes" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input v-model="queryParams.palletCode" placeholder="请输入托盘号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="createTimeRange"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间">
        <el-date-picker
          v-model="finishTimeRange"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="起始位置" prop="fromCellCode">
        <el-input v-model="queryParams.fromCellCode" placeholder="请选择起始位置" clearable
          @keyup.enter.native="handleQuery" />
        <!-- <el-select v-model="form.fromCellCode" placeholder="请选择起始位置" clearable>
            <el-option v-for="item in positionInfos" :key="item.code" :label="'['+item.code+'] - '+item.name"
              :value="item.code + ''"></el-option>
          </el-select> -->
      </el-form-item>
      <el-form-item label="目标位置" prop="toCellCode">
        <el-input v-model="queryParams.toCellCode" placeholder="请选择目标位置" clearable @keyup.enter.native="handleQuery" />
        <!-- <el-select v-model="form.fromCellCode" placeholder="请选择起始位置" clearable>
            <el-option v-for="item in positionInfos" :key="item.code" :label="'['+item.code+'] - '+item.name"
              :value="item.code + ''"></el-option>
          </el-select> -->
      </el-form-item>
      <el-form-item label="任务状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable>
          <el-option v-for="dict in taskStates" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="车辆" prop="rcsCarId">
        <el-select v-model="queryParams.rcsCarId" placeholder="请选择车辆" clearable filterable>
          <el-option v-for="item in rcsCarInfos" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-task:TaskInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-task:TaskInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-task:TaskInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-task:TaskInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="TaskInfoList" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" fixed />
      <el-table-column label="任务编号" align="center" prop="id" width="100" fixed>
        <template slot-scope="scope">
          <router-link :to="'/taskMonitor/job/' + scope.row.id" class="link-type">
            <span style="color: #409EFF; font-weight: bold;">{{ scope.row.id }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="state" width="100">
        <template slot-scope="scope">
          <div v-for="dict in taskStates" v-if="dict.value == scope.row.state">
            <el-tag v-if="dict.value == 0" size="mini" type="info">待执行</el-tag>
            <el-tag v-else-if="dict.value == 1" size="mini" type="success">执行中</el-tag>
            <el-tag v-else-if="dict.value == 2" size="mini" type="primary">已完成</el-tag>
            <el-tag v-else size="mini" type="danger">任务异常</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="仓库信息" align="center" prop="wareCode" min-width="140">
        <template slot-scope="scope">
          <div style="line-height: 1.5;">
            <div style="font-weight: bold;">{{ scope.row.wareName }}</div>
            <div style="color: #909399; font-size: 12px;">{{ scope.row.wareCode }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="taskTypeName" min-width="160">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ scope.row.type }}</el-tag>
          <div style="margin-top: 4px;">{{ scope.row.taskTypeName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="运输信息" align="center" min-width="220">
        <template slot-scope="scope">
          <div style="line-height: 1.8;">
            <div style="margin-bottom: 6px;">
              <i class="el-icon-box" style="color: #409EFF;"></i>
              <span style="font-weight: bold; margin-left: 4px;">{{ scope.row.palletCode || '-' }}</span>
            </div>
            <div style="display: flex; align-items: center; justify-content: center;">
              <el-tag size="mini" type="success" style="min-width: 60px;">{{ scope.row.fromCellCode || '-' }}</el-tag>
              <i class="el-icon-right" style="color: #409EFF; margin: 0 8px; font-weight: bold;"></i>
              <el-tag size="mini" type="warning" style="min-width: 60px;">{{ scope.row.toCellCode || '-' }}</el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="WMS任务号" align="center" prop="wmsTaskNo" width="130" show-overflow-tooltip>
      </el-table-column>
      <el-table-column label="车辆信息" align="center" prop="rcsCarName" width="140">
        <template slot-scope="scope">
          <div v-if="scope.row.rcsCarId" style="line-height: 1.5;">
            <div style="font-weight: bold;">{{ scope.row.rcsCarName || '-' }}</div>
            <div style="color: #909399; font-size: 12px;">ID: {{ scope.row.rcsCarId }}</div>
          </div>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <div style="line-height: 1.5;">
            <div><i class="el-icon-date"></i> {{ scope.row.createTime ? scope.row.createTime.split(' ')[0] : '' }}</div>
            <div style="color: #909399; font-size: 12px;"><i class="el-icon-time"></i> {{ scope.row.createTime ?
              scope.row.createTime.split(' ')[1] : '' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="left" prop="memo" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-tooltip v-if="scope.row.memo && scope.row.memo.length > 0" :content="scope.row.memo" placement="top">
            <span>{{ scope.row.memo && scope.row.memo.length > 50 ? scope.row.memo.substring(0, 50) + '...' :
              scope.row.memo }}</span>
          </el-tooltip>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" width="140" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:TaskInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-task:TaskInfo:remove']" style="color: #F56C6C;">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <!-- <el-form-item label="任务编号" prop="taskNo">
          <el-input v-model="form.taskNo" placeholder="请输入任务号" />
        </el-form-item> -->
        <!-- <el-form-item label="WMS任务号" prop="wmsTaskNo">
          <el-input v-model="form.wmsTaskNo" placeholder="请输入WMS任务号" />
        </el-form-item> -->
        <el-form-item label="仓库名称" prop="wareCode">
          <el-select v-model="form.wareCode"
            @change="getTaskTypesAndPositionInfos(form.wareCode); form.type = null; form.fromCellCode = null; form.toCellCode = null;"
            placeholder="请选择仓库名称" clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="库区编码" prop="areaCode">
          <el-input v-model="form.areaCode" placeholder="请输入库区编码" />
        </el-form-item>
        <el-form-item label="库区名称" prop="areaName">
          <el-input v-model="form.areaName" placeholder="请输入库区名称" />
        </el-form-item> -->
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option v-for="item in taskTypes" :value="item.code" :key="item.code" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="托盘编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘号" />
        </el-form-item>
        <el-form-item label="起始位置" prop="fromCellCode">
          <el-input v-model="form.fromCellCode" placeholder="起始位置" />
          <!-- <el-select v-model="form.fromCellCode" placeholder="请选择起始位置">
            <el-option v-for="item in positionInfos" :key="item.code" :label="'['+item.code+'] - '+item.name"
              :value="item.code + ''"></el-option>
          </el-select> -->
        </el-form-item>
        <el-form-item label="目标位置" prop="toCellCode">
          <el-input v-model="form.toCellCode" placeholder="目标位置" />
          <!-- <el-select v-model="form.toCellCode" placeholder="请选择目标位置">
            <el-option v-for="item in positionInfos" :key="item.code" :label="'['+item.code+'] - '+item.name" :value="item.code + ''"></el-option>
          </el-select> -->
        </el-form-item>
        <el-form-item label="任务状态" prop="state" v-if="form.id != null">
          <el-select v-model="form.state" placeholder="请选择状态">
            <el-option v-for="dict in taskStates" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车辆" prop="rcsCarId">
          <el-select v-model="form.rcsCarId" placeholder="请选择车辆" clearable filterable>
            <el-option v-for="item in rcsCarInfos" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
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
import { listWareInfo, getWareInfo, delWareInfo, addWareInfo, updateWareInfo } from "@/api/wcs-base/WareInfo";
import { listTaskType, getTaskType } from "@/api/wcs-base/taskType";
import { listPositionInfo } from "@/api/wcs-base/PositionInfo";
import { listRcsCarInfo } from "@/api/wcs-rcs/RcsCarInfo";

export default {
  name: "TaskInfo",
  data() {
    return {
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
      // 任务表格数据
      TaskInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskNo: null,
        wmsTaskNo: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        state: null,
        version: null,
        rcsCarId: null,
      },
      positionInfos: [],
      // 表单参数
      form: {
        wareCode: localStorage.getItem("wareCode"),
      },
      taskStates: [
        {
          label: "待执行",
          value: 0,
        },
        {
          label: "执行中",
          value: 1,
        },
        {
          label: "已完成",
          value: 2,
        },
        {
          label: "任务异常",
          value: 3,
        },
      ],
      taskTypes: [],
      wareInfos: [],
      rcsCarInfos: [],
      displayPositionInfos:[],
      displayTaskTypes:[],
      // 创建时间范围
      createTimeRange: [],
      // 完成时间范围
      finishTimeRange: [],
      // 表单校验
      rules: {
        wareCode: [
          { required: true, message: "请选择仓库", trigger: "blur" },
        ],
        type: [
          { required: true, message: "请选择类型", trigger: "blur" },
        ],

        palletCode: [{ required: false, message: "请输入托盘编码", trigger: "blur" }],
        fromCellCode: [{ required: false, message: "请输入起始货位", trigger: "blur" }],
        toCellCode: [{ required: false, message: "请输入目标货位", trigger: "blur" }],
      },
      timer: null,
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
    this.getRcsCarInfos();
    this.getDisPlayMessage()
    this.timer = setInterval(() => {
      this.getList();
    }, 5000);
    var wareCode = localStorage.getItem("wareCode");
    if (wareCode != undefined && wareCode != null) {
      this.getTaskTypesAndPositionInfos(wareCode);
    }
  },
  beforeDestroy() {
    if (this.timer) {
      //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
  },
  methods: {
    getDisPlayMessage() {
      listPositionInfo({ isDelete: 0 }).then((response) => {
        this.displayPositionInfos = response.rows
      })
      listTaskType({ delFlag: 0 }).then((response) => {
        if (response.code == 200) {
          this.displayTaskTypes = response.rows
        }
      })
    },
    //根据仓库编码获取任务类型和站点信息
    getTaskTypesAndPositionInfos(wareCode) {
      this.getAllTaskTypes(wareCode)
      this.getPositionInfosByWareCode(wareCode)
    },
    //根据仓库编码获取站点信息
    getPositionInfosByWareCode(wareCode) {
      listPositionInfo({ wareCode: wareCode, isDelete: 0 }).then((response) => {
        this.positionInfos = response.rows
      })
    },

    //获取所有的任务类型
    getAllTaskTypes(wareCode) {
      listTaskType({wareCode: wareCode,delFlag:0,pageSize:999}).then((response) => {
        if (response.code == 200) {
          this.taskTypes = response.rows
        }
      })
    },
    getWareInfos() {
      this.loading = true;
      listWareInfo({ isDelete: 0 }).then((response) => {
        this.wareInfos = response.rows;
      });
    },
    getRcsCarInfos() {
      listRcsCarInfo({}).then((response) => {
        if (response.code == 200) {
          this.rcsCarInfos = response.rows || [];
        }
      });
    },
    /** 查询任务列表 */
    getList() {
      this.loading = true;
      // 处理创建时间范围
      const params = { ...this.queryParams };
      if (!params.params) {
        params.params = {};
      }
      if (this.createTimeRange && this.createTimeRange.length === 2) {
        params.params.createTimeStart = this.createTimeRange[0];
        params.params.createTimeEnd = this.createTimeRange[1];
      } else {
        delete params.params.createTimeStart;
        delete params.params.createTimeEnd;
      }
      // 处理完成时间范围
      if (this.finishTimeRange && this.finishTimeRange.length === 2) {
        params.params.finishTimeStart = this.finishTimeRange[0];
        params.params.finishTimeEnd = this.finishTimeRange[1];
      } else {
        delete params.params.finishTimeStart;
        delete params.params.finishTimeEnd;
      }
      listTaskInfo(params).then((response) => {
        this.TaskInfoList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
        taskNo: null,
        wmsTaskNo: null,
        wareCode: localStorage.getItem("wareCode"),
        wareName: null,
        areaCode: null,
        areaName: null,
        type: null,
        palletCode: null,
        fromCellCode: null,
        toCellCode: null,
        createTime: null,
        state: null,
        version: null,
        rcsCarId: null,
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
      this.createTimeRange = [];
      this.finishTimeRange = [];
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
      this.reset();
      this.open = true;
      this.title = "添加任务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getTaskInfo(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改任务";
        this.getTaskTypesAndPositionInfos(this.form.wareCode)
      });
    },
    /** 提交按钮 */
    submitForm() {
      var form = this.form;
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (form.fromCellCode != null && form.toCellCode != null && form.fromCellCode == form.toCellCode) {
            this.$modal.msgError("起始位置不可与终点位置一致")
            return;
          }
          for (var i = 0; i < this.wareInfos.length; i++) {
            if (this.form.wareCode == this.wareInfos[i].code) {
              this.form.wareName = this.wareInfos[i].name
            }
          }
          if (this.form.id != null) {
            updateTaskInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addTaskInfo(this.form).then((response) => {
              if (response.code == 200) {
                localStorage.setItem("wareCode", this.form.wareCode);
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
        .confirm('是否确认删除任务编号为"' + ids + '"的数据项？')
        .then(function () {
          return delTaskInfo(ids);
        })
        .then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("删除成功");
          } else {
            this.$modal.msgError(response.msg || "删除失败")
          }
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-task/TaskInfo/export",
        {
          ...this.queryParams,
        },
        `TaskInfo_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>

<style scoped>
/* 链接样式优化 */
.link-type {
  text-decoration: none;
}

.link-type:hover {
  text-decoration: underline;
}

/* 表格内的标签间距 */
::v-deep .el-table .cell {
  padding: 8px 0;
}
</style>
