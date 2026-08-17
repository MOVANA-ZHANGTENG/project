<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="小车编码" prop="carCode">
        <el-input v-model="queryParams.carCode" placeholder="请输入小车编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="小车名称" prop="carName">
        <el-input v-model="queryParams.carName" placeholder="请输入小车名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="小车型号" prop="model">
        <el-input v-model="queryParams.model" placeholder="请输入小车型号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="当前z值" prop="currentZ">
        <el-input v-model="queryParams.currentZ" placeholder="请输入当前z值" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="是否充电" prop="isCharge">
        <el-input v-model="queryParams.isCharge" placeholder="请输入是否充电" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="当前状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择当前状态" clearable>
          <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="错误代码" prop="errorCode">
        <el-input v-model="queryParams.errorCode" placeholder="请输入错误代码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新时间" prop="lastUpdated">
        <el-input v-model="queryParams.lastUpdated" placeholder="请输入更新时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="目标库位" prop="nextMapCellCode">
        <el-input v-model="queryParams.nextMapCellCode" placeholder="请输入目标库位" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="避碰策略" prop="avoidanceStrategy">
        <el-input v-model="queryParams.avoidanceStrategy" placeholder="请输入避碰策略" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="当前任务id" prop="currentTaskId">
        <el-input v-model="queryParams.currentTaskId" placeholder="请输入当前任务id" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="装载托盘编码" prop="loadPalletCode">
        <el-input v-model="queryParams.loadPalletCode" placeholder="请输入装载托盘编码" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务开始时间" prop="taskStartTime">
        <el-input v-model="queryParams.taskStartTime" placeholder="请输入任务开始时间" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="实际结束时间" prop="actualEndTime">
        <el-input v-model="queryParams.actualEndTime" placeholder="请输入实际任务结束时间" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-input v-model="queryParams.createTime" placeholder="请输入创建时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新时间" prop="updateTime">
        <el-input v-model="queryParams.updateTime" placeholder="请输入更新时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建用户" prop="createUserName">
        <el-input v-model="queryParams.createUserName" placeholder="请输入创建用户" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="是否激活" prop="isActive">
        <el-input v-model="queryParams.isActive" placeholder="请输入是否激活" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-rcs:RcsCar:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-rcs:RcsCar:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-rcs:RcsCar:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-rcs:RcsCar:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="RcsCarList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="小车编码" align="center" prop="carCode" min-width="120">
      </el-table-column>
      <el-table-column label="小车名称" align="center" prop="carName" min-width="120">
      </el-table-column>
      <el-table-column label="小车型号" align="center" prop="model" min-width="120">
      </el-table-column>
      <el-table-column label="最大行驶速度(m/s)" align="center" prop="maxSpeed" min-width="140">
      </el-table-column>
      <el-table-column label="小车载重能力(kg)" align="center" prop="loadCapacity" min-width="140">
      </el-table-column>
      <el-table-column label="电池类型" align="center" prop="batteryType" min-width="120">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.batteryType" /> -->
        </template>
      </el-table-column>
      <el-table-column label="小车尺寸" align="center" prop="dimensions" min-width="120">
      </el-table-column>
      <el-table-column label="当前x值" align="center" prop="currentX" min-width="120">
      </el-table-column>
      <el-table-column label="当前y值" align="center" prop="currentY" min-width="120">
      </el-table-column>
      <el-table-column label="当前z值" align="center" prop="currentZ" min-width="120">
      </el-table-column>
      <el-table-column label="当前朝向" align="center" prop="currentDirection" min-width="120">
      </el-table-column>
      <el-table-column label="当前速度(m/s)" align="center" prop="currentSpeed" min-width="120">
      </el-table-column>
      <el-table-column label="小车电量" align="center" prop="batteryLevel" min-width="120">
      </el-table-column>
      <el-table-column label="空闲时自动充电电量" align="center" prop="idleChargeThreshold" min-width="150">
      </el-table-column>
      <el-table-column label="最小充电电量" align="center" prop="minChargeLevel" min-width="120">
      </el-table-column>
      <el-table-column label="充电位置" align="center" prop="chargeMapCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="是否充电" align="center" prop="isCharge" min-width="120">
      </el-table-column>
      <el-table-column label="临时停车位置" align="center" prop="tempParkMapCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="临时停车开始时间" align="center" prop="parkingStartTime" min-width="180">
      </el-table-column>
      <el-table-column label="临时停车结束时间" align="center" prop="parkingEndTime" min-width="180">
      </el-table-column>
      <el-table-column label="当前状态" align="center" prop="status" min-width="120">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.status" /> -->
        </template>
      </el-table-column>
      <el-table-column label="错误代码" align="center" prop="errorCode" min-width="120">
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="lastUpdated" min-width="120">
      </el-table-column>
      <el-table-column label="目标库位" align="center" prop="nextMapCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="路径优先级" align="center" prop="priorityLevel" min-width="120">
      </el-table-column>
      <el-table-column label="避碰策略" align="center" prop="avoidanceStrategy" min-width="120">
      </el-table-column>
      <el-table-column label="当前任务id" align="center" prop="currentTaskId" min-width="120">
      </el-table-column>
      <el-table-column label="装载托盘编码" align="center" prop="loadPalletCode" min-width="120">
      </el-table-column>
      <el-table-column label="任务开始时间" align="center" prop="taskStartTime" min-width="180">
      </el-table-column>
      <el-table-column label="预计任务结束时间" align="center" prop="estimatedEndTime" min-width="180">
      </el-table-column>
      <el-table-column label="实际任务结束时间" align="center" prop="actualEndTime" min-width="180">
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="180">
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" min-width="180">
      </el-table-column>
      <el-table-column label="创建用户" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column label="更新用户" align="center" prop="updateUserName" min-width="120">
      </el-table-column>
      <el-table-column label="是否激活" align="center" prop="isActive" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-rcs:RcsCar:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-rcs:RcsCar:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改小车信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="小车编码" prop="carCode">
          <el-input v-model="form.carCode" placeholder="请输入小车编码" />
        </el-form-item>
        <el-form-item label="小车名称" prop="carName">
          <el-input v-model="form.carName" placeholder="请输入小车名称" />
        </el-form-item>
        <el-form-item label="小车型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入小车型号" />
        </el-form-item>
        <el-form-item label="最大行驶速度(m/s)" prop="maxSpeed">
          <el-input v-model="form.maxSpeed" placeholder="请输入最大行驶速度(m/s)" />
        </el-form-item>
        <el-form-item label="小车载重能力(kg)" prop="loadCapacity">
          <el-input v-model="form.loadCapacity" placeholder="请输入小车载重能力(kg)" />
        </el-form-item>
        <el-form-item label="电池类型" prop="batteryType">
          <el-select v-model="form.batteryType" placeholder="请选择电池类型">
            <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="小车尺寸" prop="dimensions">
          <el-input v-model="form.dimensions" placeholder="请输入小车尺寸" />
        </el-form-item>
        <el-form-item label="当前x值" prop="currentX">
          <el-input v-model="form.currentX" placeholder="请输入当前x值" />
        </el-form-item>
        <el-form-item label="当前y值" prop="currentY">
          <el-input v-model="form.currentY" placeholder="请输入当前y值" />
        </el-form-item>
        <el-form-item label="当前z值" prop="currentZ">
          <el-input v-model="form.currentZ" placeholder="请输入当前z值" />
        </el-form-item>
        <el-form-item label="空闲时自动充电电量" prop="idleChargeThreshold">
          <el-input v-model="form.idleChargeThreshold" placeholder="请输入空闲时自动充电电量" />
        </el-form-item>
        <el-form-item label="最小充电电量" prop="minChargeLevel">
          <el-input v-model="form.minChargeLevel" placeholder="请输入最小充电电量" />
        </el-form-item>
        <el-form-item label="充电位置" prop="chargeMapCellCode">
          <el-input v-model="form.chargeMapCellCode" placeholder="请输入充电位置" />
        </el-form-item>
        <el-form-item label="路径优先级" prop="priorityLevel">
          <el-input v-model="form.priorityLevel" placeholder="请输入路径优先级" />
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
import { listRcsCar, getRcsCar, delRcsCar, addRcsCar, updateRcsCar } from "@/api/wcs-rcs/RcsCar";
import request from "@/utils/request";
export default {
  name: "RcsCar",
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
      // 小车信息表格数据
      RcsCarList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        carCode: null,
        carName: null,
        model: null,
        currentZ: null,
        idleChargeThreshold: null,
        minChargeLevel: null,
        chargeMapCellCode: null,
        isCharge: null,
        status: null,
        errorCode: null,
        lastUpdated: null,
        nextMapCellCode: null,
        priorityLevel: null,
        avoidanceStrategy: null,
        currentTaskId: null,
        loadPalletCode: null,
        taskStartTime: null,
        actualEndTime: null,
        createTime: null,
        updateTime: null,
        createUserName: null,
        updateUserName: null,
        isActive: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询小车信息列表 */
    getList() {
      this.loading = true;
      listRcsCar(this.queryParams).then(response => {
        if (response.code == 200) {
          this.RcsCarList = response.rows;
          this.total = response.total;
        }
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
        carCode: null,
        carName: null,
        model: null,
        maxSpeed: null,
        loadCapacity: null,
        batteryType: null,
        dimensions: null,
        currentX: null,
        currentY: null,
        currentZ: null,
        currentDirection: null,
        currentSpeed: null,
        batteryLevel: null,
        idleChargeThreshold: null,
        minChargeLevel: null,
        chargeMapCellCode: null,
        isCharge: null,
        tempParkMapCellCode: null,
        parkingStartTime: null,
        parkingEndTime: null,
        status: null,
        errorCode: null,
        lastUpdated: null,
        nextMapCellCode: null,
        priorityLevel: null,
        avoidanceStrategy: null,
        currentTaskId: null,
        loadPalletCode: null,
        taskStartTime: null,
        estimatedEndTime: null,
        actualEndTime: null,
        createTime: null,
        updateTime: null,
        createUserName: null,
        updateUserName: null,
        isActive: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加小车信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getRcsCar(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改小车信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRcsCar(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addRcsCar(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除小车信息编号为"' + ids + '"的数据项？').then(function () {
        return delRcsCar(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg || "删除失败");
        }

      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-rcs/RcsCar/export', {
        ...this.queryParams
      }, `RcsCar_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
