<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left"
      label-width="100px">
      <el-form-item label="提升机编号" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入提升机编号" clearable @input="debounceQuery"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="提升机名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入提升机名称" clearable @input="debounceQuery"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备型号" prop="model">
        <el-input v-model="queryParams.model" placeholder="请输入设备型号" clearable @input="debounceQuery"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备位置" prop="location">
        <el-input v-model="queryParams.location" placeholder="请输入设备位置" clearable @input="debounceQuery"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input v-model="queryParams.wareCode" placeholder="请输入仓库编码" clearable @input="debounceQuery"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库名称" prop="wareName">
        <el-input v-model="queryParams.wareName" placeholder="请输入仓库名称" clearable @input="debounceQuery"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="当前状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择当前状态" clearable @change="handleQuery">
          <el-option label="空闲" value="idle" />
          <el-option label="运行中" value="running" />
          <el-option label="故障" value="fault" />
          <el-option label="维护中" value="maintenance" />
          <el-option label="充电中" value="charging" />
        </el-select>
      </el-form-item>
      <el-form-item label="运行方向" prop="moveDirection">
        <el-select v-model="queryParams.moveDirection" placeholder="请选择运行方向" clearable @change="handleQuery">
          <el-option label="上行" value="up" />
          <el-option label="下行" value="down" />
          <el-option label="静止" value="stationary" />
        </el-select>
      </el-form-item>
      <el-form-item label="负载状态" prop="loadStatus">
        <el-select v-model="queryParams.loadStatus" placeholder="请选择负载状态" clearable @change="handleQuery">
          <el-option label="有负载" value="true" />
          <el-option label="无负载" value="false" />
        </el-select>
      </el-form-item>
      <el-form-item label="当前任务ID" prop="currentTaskId">
        <el-input v-model="queryParams.currentTaskId" placeholder="请输入当前任务ID" clearable @input="debounceQuery"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-rcs:RcsTsj:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-rcs:RcsTsj:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-rcs:RcsTsj:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-rcs:RcsTsj:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="RcsTsjList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="提升机主键" align="center" prop="id" min-width="100" /> -->
      <el-table-column label="提升机编号" align="center" prop="code" min-width="120" />
      <el-table-column label="提升机名称" align="center" prop="name" min-width="120" />
      <el-table-column label="设备型号" align="center" prop="model" min-width="120" />
      <el-table-column label="设备位置" align="center" prop="cellCode" min-width="120" />
      <el-table-column label="接驳位置" align="center" prop="dockCellCode" min-width="120" />
      <el-table-column label="仓库" align="center" prop="wareCode" min-width="200">
        <template slot-scope="scope">
          <div class="ware-info">
            <el-tag size="mini" type="primary" effect="plain" class="ware-code">
              {{ scope.row.wareCode }}
            </el-tag>
            <span class="ware-name">{{ scope.row.wareName || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="当前状态" align="center" prop="status" min-width="120">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.status === 'running' ? 'success' : scope.row.status === 'fault' ? 'danger' : scope.row.status === 'maintenance' ? 'warning' : 'info'">
            {{ scope.row.status === 'idle' ? '空闲' : scope.row.status === 'running' ? '运行中' : scope.row.status ===
              'fault' ? '故障' : scope.row.status === 'maintenance' ? '维护中' : scope.row.status === 'charging' ? '充电中' :
            scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前层数" align="center" prop="currentFloor" min-width="100" />
      <el-table-column label="目标层数" align="center" prop="targetFloor" min-width="100" />
      <el-table-column label="运行方向" align="center" prop="moveDirection" min-width="100">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.moveDirection === 'up' ? 'success' : scope.row.moveDirection === 'down' ? 'danger' : 'info'">
            {{ scope.row.moveDirection === 'up' ? '上行' : scope.row.moveDirection === 'down' ? '下行' :
              scope.row.moveDirection === 'stationary' ? '静止' : scope.row.moveDirection }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="负载状态" align="center" prop="loadStatus" min-width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.loadStatus ? 'success' : 'info'">
            {{ scope.row.loadStatus ? '有负载' : '无负载' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前任务ID" align="center" prop="currentTaskId" min-width="120" />
      <el-table-column label="累计运行时长" align="center" prop="totalOperationTime" min-width="120">
        <template slot-scope="scope">
          {{ scope.row.totalOperationTime }} 分钟
        </template>
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-rcs:RcsTsj:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-rcs:RcsTsj:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改提升机对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="提升机编号" prop="code">
              <el-input v-model="form.code" placeholder="请输入提升机编号" />
            </el-form-item>
            <el-form-item label="提升机名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入提升机名称" />
            </el-form-item>
            <el-form-item label="设备型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入设备型号" />
            </el-form-item>
            <el-form-item label="设备位置" prop="cellCode">
              <el-input v-model="form.cellCode" placeholder="请输入设备位置" />
            </el-form-item>
            <el-form-item label="接驳位置" prop="dockCellCode">
              <el-input v-model="form.dockCellCode" placeholder="请输入接驳位置" />
            </el-form-item>
            <el-form-item label="仓库编码" prop="wareCode">
              <el-input v-model="form.wareCode" placeholder="请输入仓库编码" />
            </el-form-item>
            <el-form-item label="仓库名称" prop="wareName">
              <el-input v-model="form.wareName" placeholder="请输入仓库名称" />
            </el-form-item>
            <el-form-item label="当前状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="'idle'">空闲</el-radio>
                <el-radio :label="'running'">运行中</el-radio>
                <el-radio :label="'fault'">故障</el-radio>
                <el-radio :label="'maintenance'">维护中</el-radio>
                <el-radio :label="'charging'">充电中</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="当前层数" prop="currentFloor">
              <el-input v-model="form.currentFloor" placeholder="请输入当前层数" type="number" />
            </el-form-item>
            <el-form-item label="目标层数" prop="targetFloor">
              <el-input v-model="form.targetFloor" placeholder="请输入目标层数" type="number" />
            </el-form-item>
            <el-form-item label="运行方向" prop="moveDirection">
              <el-select v-model="form.moveDirection" placeholder="请选择运行方向">
                <el-option label="上行" value="up" />
                <el-option label="下行" value="down" />
                <el-option label="静止" value="stationary" />
              </el-select>
            </el-form-item>
            <el-form-item label="负载状态" prop="loadStatus">
              <el-radio-group v-model="form.loadStatus">
                <el-radio :label="true">有负载</el-radio>
                <el-radio :label="false">无负载</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实时速度" prop="realTimeSpeed">
              <el-input v-model="form.realTimeSpeed" placeholder="请输入实时速度" type="number" />
            </el-form-item>
            <el-form-item label="累计运行时长" prop="totalOperationTime">
              <el-input v-model="form.totalOperationTime" placeholder="请输入累计运行时长（分钟）" type="number" />
            </el-form-item>
            <el-form-item label="当前任务ID" prop="currentTaskId">
              <el-input v-model="form.currentTaskId" placeholder="请输入当前任务ID" />
            </el-form-item>
            <el-form-item label="待执行任务列表" prop="taskQueue">
              <el-input v-model="form.taskQueue" type="textarea" placeholder="请输入待执行任务列表，用逗号分隔" :rows="3" />
            </el-form-item>
            <el-form-item label="关联小车ID" prop="carId">
              <el-input v-model="form.carId" placeholder="请输入关联小车ID" />
            </el-form-item>
            <el-form-item label="额定载重" prop="ratedLoadCapacity">
              <el-input v-model="form.ratedLoadCapacity" placeholder="请输入额定载重（kg）" type="number" />
            </el-form-item>
            <el-form-item label="内部尺寸" prop="interiorDimensions">
              <el-input v-model="form.interiorDimensions" placeholder="请输入长×宽×高，例如：1200×800×1000" />
            </el-form-item>
            <el-form-item label="电机功率" prop="motorPower">
              <el-input v-model="form.motorPower" placeholder="请输入电机功率（kW）" type="number" />
            </el-form-item>
            <el-form-item label="通信协议" prop="communicationProtocol">
              <el-input v-model="form.communicationProtocol" placeholder="请输入通信协议" />
            </el-form-item>
            <el-form-item label="定位精度" prop="positioningAccuracy">
              <el-input v-model="form.positioningAccuracy" placeholder="请输入定位精度（mm）" type="number" />
            </el-form-item>
            <el-form-item label="PLC设备编码" prop="deviceCode">
              <el-input v-model="form.deviceCode" placeholder="请输入PLC设备关联编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider />
        <el-form-item label="报警信息">
          <el-input v-model="form.errorMessage" type="textarea" placeholder="请输入错误信息" :rows="3" />
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
import { listRcsTsj, getRcsTsj, delRcsTsj, addRcsTsj, updateRcsTsj } from "@/api/wcs-rcs/RcsTsj";
export default {
  name: "RcsTsj",
  data() {
    return {
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
      // 提升机表格数据
      RcsTsjList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: null,
        name: null,
        model: null,
        location: null,
        wareCode: null,
        wareName: null,
        status: null,
        currentFloor: null,
        targetFloor: null,
        moveDirection: null,
        loadStatus: null,
        realTimeSpeed: null,
        totalOperationTime: null,
        currentTaskId: null,
        taskQueue: null,
        taskType: null,
        taskStartTime: null,
        taskEndTime: null,
        carId: null,
        ratedLoadCapacity: null,
        interiorDimensions: null,
        motorPower: null,
        chargingInterfaceType: null,
        chargingPower: null,
        communicationProtocol: null,
        positioningAccuracy: null,
        errorCode: null,
        alarmType: null,
        errorMessage: null,
        authorizedFloorRange: null,
        deviceCode: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: '提升机编号不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '提升机名称不能为空', trigger: 'blur' }
        ]
      },
      // 防抖计时器
      debounceTimer: null
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询提升机列表 */
    getList() {
      this.loading = true;
      listRcsTsj(this.queryParams)
        .then(response => {
          if (response.code === 200) {
            this.RcsTsjList = response.rows;
            this.total = response.total;
          } else {
            this.$modal.msgError(response.msg || "获取数据失败");
          }
        })
        .catch(error => {
          this.$modal.msgError("获取数据失败，请稍后重试");
          console.error("获取提升机列表失败:", error);
        })
        .finally(() => {
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
        code: null,
        name: null,
        model: null,
        location: null,
        wareCode: null,
        wareName: null,
        createTime: null,
        status: null,
        currentFloor: null,
        targetFloor: null,
        moveDirection: null,
        loadStatus: null,
        realTimeSpeed: null,
        totalOperationTime: null,
        currentTaskId: null,
        taskQueue: null,
        taskType: null,
        taskStartTime: null,
        taskEndTime: null,
        carId: null,
        ratedLoadCapacity: null,
        interiorDimensions: null,
        motorPower: null,
        chargingInterfaceType: null,
        chargingPower: null,
        communicationProtocol: null,
        positioningAccuracy: null,
        errorCode: null,
        alarmType: null,
        errorMessage: null,
        authorizedFloorRange: null,
        deviceCode: null
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
    // 防抖搜索
    debounceQuery() {
      if (this.debounceTimer) {
        clearTimeout(this.debounceTimer);
      }
      this.debounceTimer = setTimeout(() => {
        this.handleQuery();
      }, 300);
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加提升机";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getRcsTsj(id)
        .then(response => {
          if (response.code === 200) {
            this.form = response.data;
            this.open = true;
            this.title = "修改提升机";
          } else {
            this.$modal.msgError(response.msg || "获取数据失败");
          }
        })
        .catch(error => {
          this.$modal.msgError("获取数据失败，请稍后重试");
          console.error("获取提升机详情失败:", error);
        });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const request = this.form.id != null ? updateRcsTsj(this.form) : addRcsTsj(this.form);
          request
            .then(response => {
              if (response.code === 200) {
                this.$modal.msgSuccess(this.form.id != null ? "修改成功" : "新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || (this.form.id != null ? "修改失败" : "新增失败"));
              }
            })
            .catch(error => {
              this.$modal.msgError(this.form.id != null ? "修改失败，请稍后重试" : "新增失败，请稍后重试");
              console.error(this.form.id != null ? "修改提升机失败:" : "新增提升机失败:", error);
            });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除提升机编号为"' + ids + '"的数据项？')
        .then(() => {
          return delRcsTsj(ids);
        })
        .then((response) => {
          if (response.code === 200) {
            this.getList();
            this.$modal.msgSuccess("删除成功");
          } else {
            this.$modal.msgError(response.msg || "删除失败");
          }
        })
        .catch(error => {
          if (error !== 'cancel') {
            this.$modal.msgError("删除失败，请稍后重试");
            console.error("删除提升机失败:", error);
          }
        });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-rcs/RcsTsj/export', {
        ...this.queryParams
      }, `RcsTsj_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style scoped>

.ware-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  .ware-code {
    font-size: 12px;
    font-weight: 500;
    border-radius: 4px;
    padding: 2px 8px;
    
    &:deep(.el-tag__content) {
      color: #409eff;
    }
  }
  
  .ware-name {
    font-size: 13px;
    color: #606266;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
