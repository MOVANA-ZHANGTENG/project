<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left"
      label-width="100px">

      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="车辆类型" prop="rcsCarTypeId">
        <el-select v-model="queryParams.rcsCarTypeId" placeholder="请选择车辆类型" clearable filterable style="width: 200px;"
          @change="handleQuery">
          <el-option v-for="carType in rcsCarTypeList" :key="carType.id" :label="carType.code + ' - ' + carType.name"
            :value="carType.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="禁用状态" prop="disableState">
        <el-select v-model="queryParams.disableState" placeholder="请选择禁用状态" clearable style="width: 200px;"
          @change="handleQuery">
          <el-option label="正常" :value="0"></el-option>
          <el-option label="禁用" :value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态" prop="taskState">
        <el-select v-model="queryParams.taskState" placeholder="请选择任务状态" clearable style="width: 200px;"
          @change="handleQuery">
          <el-option label="空闲" :value="0"></el-option>
          <el-option label="执行中" :value="1"></el-option>
          <el-option label="暂停" :value="2"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="是否充电" prop="isCharge">
        <el-select v-model="queryParams.isCharge" placeholder="请选择是否充电" clearable style="width: 200px;"
          @change="handleQuery">
          <el-option label="否" :value="0"></el-option>
          <el-option label="是" :value="1"></el-option>
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
          v-hasPermi="['wcs-rcs:RcsCarInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-rcs:RcsCarInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-rcs:RcsCarInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-rcs:RcsCarInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="RcsCarInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="ID" align="center" prop="id" min-width="100" /> -->
      <!-- <el-table-column label="设备ID" align="center" prop="deviceId" min-width="120">
      </el-table-column> -->
      <!-- <el-table-column label="编码" align="center" prop="code" min-width="120">
      </el-table-column> -->
      <el-table-column label="名称" align="center" prop="name" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="类型ID" align="center" prop="rcsCarTypeId" min-width="120">
      </el-table-column> -->
      <el-table-column label="是否在线" align="center" prop="isConnected" min-width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isConnected == 1" type="success">在线</el-tag>
          <el-tag v-else-if="scope.row.isConnected == 0" type="info">离线</el-tag>
          <span v-else>{{ scope.row.isConnected }}</span>
        </template>
      </el-table-column>

      <el-table-column label="当前位置" align="center" prop="fromCellCode" min-width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.fromCellCode" type="primary">{{ scope.row.fromCellCode }}</el-tag>
          <span v-else style="color: #999;">未设置</span>
        </template>
      </el-table-column>

      <el-table-column label="电量" align="center" prop="batteryLevel" min-width="100">
        <template slot-scope="scope">
          <span
            :style="{ color: scope.row.batteryLevel < 20 ? 'red' : scope.row.batteryLevel < 50 ? 'orange' : 'green' }">
            {{ scope.row.batteryLevel }}%
          </span>
        </template>
      </el-table-column>
      <el-table-column label="是否充电" align="center" prop="isCharge" min-width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isCharge == 1" type="success">是</el-tag>
          <el-tag v-else-if="scope.row.isCharge == 0" type="info">否</el-tag>
          <span v-else>{{ scope.row.isCharge }}</span>
        </template>
      </el-table-column>

      <el-table-column label="负载状态" align="center" prop="loadState" min-width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.loadState == 1" type="warning">
            <i class="el-icon-box"></i> 负载
          </el-tag>
          <el-tag v-else-if="scope.row.loadState == 0" type="success">
            <i class="el-icon-circle-check"></i> 空载
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="taskState" min-width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.taskState == 0" type="info">空闲</el-tag>
          <el-tag v-else-if="scope.row.taskState == 1" type="warning">执行中</el-tag>
          <el-tag v-else-if="scope.row.taskState == 2" type="danger">暂停</el-tag>
          <span v-else>{{ scope.row.taskState }}</span>
        </template>
      </el-table-column>

      <el-table-column label="目标位置" align="center" prop="toCellCode" min-width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.toCellCode" type="warning">{{ scope.row.toCellCode }}</el-tag>
          <span v-else style="color: #999;">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="禁用状态" align="center" prop="disableState" min-width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.disableState == 0" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.disableState == 1" type="danger">禁用</el-tag>
          <span v-else>{{ scope.row.disableState }}</span>
        </template>
      </el-table-column>


      <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
      </el-table-column>

      <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">
      </el-table-column>

      <el-table-column fixed="right" width="200" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-location" @click="handleQuickEditPosition(scope.row)"
            v-hasPermi="['wcs-rcs:RcsCarInfo:edit']">修改位置</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-rcs:RcsCarInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-rcs:RcsCarInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 快速修改位置对话框 -->
    <el-dialog title="修改小车位置" :visible.sync="positionDialogOpen" width="500px" append-to-body>
      <el-form ref="positionForm" :model="positionForm" label-width="100px">
        <el-form-item label="小车编码">
          <el-input v-model="positionForm.code" disabled />
        </el-form-item>
        <el-form-item label="小车名称">
          <el-input v-model="positionForm.name" disabled />
        </el-form-item>
        <el-form-item label="当前位置" prop="fromCellCode">
          <el-select v-model="positionForm.fromCellCode" placeholder="请选择当前位置" clearable filterable style="width: 100%;"
            @focus="getCellList">
            <el-option v-for="cell in cellList" :key="cell.code" :label="'[' + cell.wareCode + ']' + cell.code"
              :value="cell.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="目标位置" prop="toCellCode">
          <el-select v-model="positionForm.toCellCode" placeholder="请选择目标位置" clearable filterable style="width: 100%;"
            @focus="getCellList">
            <el-option v-for="cell in cellList" :key="cell.code" :label="'[' + cell.wareCode + ']' + cell.code"
              :value="cell.code">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPositionForm">确 定</el-button>
        <el-button @click="cancelPositionForm">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改四向车/AGV对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备" prop="deviceId">
              <el-select v-model="form.deviceId" placeholder="请选择设备" clearable filterable style="width: 100%;">
                <el-option v-for="device in deviceList" :key="device.id" :label="device.code + ' - ' + device.name"
                  :value="device.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车辆类型" prop="rcsCarTypeId">
              <el-select v-model="form.rcsCarTypeId" placeholder="请选择车辆类型" clearable filterable style="width: 100%;">
                <el-option v-for="carType in rcsCarTypeList" :key="carType.id"
                  :label="carType.code + ' - ' + carType.name" :value="carType.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="禁用状态" prop="disableState">
              <el-select v-model="form.disableState" placeholder="请选择禁用状态" style="width: 100%;">
                <el-option label="正常" :value="0"></el-option>
                <el-option label="禁用" :value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务状态" prop="taskState">
              <el-select v-model="form.taskState" placeholder="请选择任务状态" style="width: 100%;">
                <el-option label="空闲" :value="0"></el-option>
                <el-option label="执行中" :value="1"></el-option>
                <el-option label="暂停" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电量" prop="batteryLevel">
              <el-input-number v-model="form.batteryLevel" :min="0" :max="100" placeholder="请输入电量"
                style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否充电" prop="isCharge">
              <el-select v-model="form.isCharge" placeholder="请选择是否充电" style="width: 100%;">
                <el-option label="否" :value="0"></el-option>
                <el-option label="是" :value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前位置" prop="fromCellCode">
              <el-select v-model="form.fromCellCode" placeholder="请选择当前位置" clearable filterable style="width: 100%;"
                @focus="getCellList">
                <el-option v-for="cell in cellList" :key="cell.code" :label="'[' + cell.wareCode + ']' + cell.code"
                  :value="cell.code">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标位置" prop="toCellCode">
              <el-select v-model="form.toCellCode" placeholder="请选择目标位置" clearable filterable style="width: 100%;"
                @focus="getCellList">
                <el-option v-for="cell in cellList" :key="cell.code" :label="'[' + cell.wareCode + ']' + cell.code"
                  :value="cell.code">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="IP地址" prop="ip">
              <el-input v-model="form.ip" placeholder="请输入IP地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="端口" prop="port">
              <el-input-number v-model="form.port" :min="1" :max="65535" placeholder="请输入端口"
                style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRcsCarInfo, getRcsCarInfo, delRcsCarInfo, addRcsCarInfo, updateRcsCarInfo } from "@/api/wcs-rcs/RcsCarInfo";
import request from "@/utils/request";
export default {
  name: "RcsCarInfo",
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
      // 四向车/AGV表格数据
      RcsCarInfoList: [],
      // 设备列表
      deviceList: [],
      // 车辆类型列表
      rcsCarTypeList: [],
      // 库位列表
      cellList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示位置修改对话框
      positionDialogOpen: false,
      // 位置修改表单
      positionForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: null,
        code: null,
        name: null,
        rcsCarTypeId: null,
        disableState: null,
        taskState: null,
        batteryLevel: null,
        isCharge: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        version: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "编码不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "名称不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getRcsCarTypeList();
  },
  methods: {
    /** 查询四向车/AGV列表 */
    getList() {
      this.loading = true;
      listRcsCarInfo(this.queryParams).then(response => {
        if (response.code == 200) {
          this.RcsCarInfoList = response.rows;
          this.total = response.total;
        }
        this.loading = false;
      });
    },
    /** 获取设备列表 */
    getDeviceList() {
      var that = this;
      request({
        url: "/wcs-base/DeviceInfo/list",
        method: "get",
        params: { pageNum: 1, pageSize: 9999 }
      }).then((response) => {
        if (response.code == 200) {
          that.deviceList = response.rows || [];
        } else {
          that.$modal.msgError(response.msg || "获取设备列表失败");
        }
      });
    },
    /** 获取车辆类型列表 */
    getRcsCarTypeList() {
      var that = this;
      request({
        url: "/wcs-rcs/RcsCarType/list",
        method: "get",
        params: { pageNum: 1, pageSize: 9999 }
      }).then((response) => {
        if (response.code == 200) {
          that.rcsCarTypeList = response.rows || [];
        } else {
          that.$modal.msgError(response.msg || "获取车辆类型列表失败");
        }
      });
    },
    /** 获取库位列表 */
    getCellList() {
      // 如果已经加载过，则不重复加载
      if (this.cellList.length > 0) {
        return;
      }
      var that = this;
      request({
        url: "/wcs-base/CellInfo/list",
        method: "get",
        params: { wareCode:"P1",pageNum: 1, pageSize: 9999 }
      }).then((response) => {
        if (response.code == 200) {
          that.cellList = response.rows || [];
        } else {
          that.$modal.msgError(response.msg || "获取库位列表失败");
        }
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
        deviceId: null,
        code: null,
        name: null,
        rcsCarTypeId: null,
        disableState: null,
        taskState: null,
        batteryLevel: null,
        isCharge: null,
        fromCellCode: null,
        toCellCode: null,
        ip: null,
        port: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        updateTime: null,
        version: null
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
      this.getDeviceList();
      this.getRcsCarTypeList();
      this.open = true;
      this.title = "添加四向车/AGV";
    },
    /** 快速修改位置按钮操作 */
    handleQuickEditPosition(row) {
      this.positionForm = {
        id: row.id,
        code: row.code,
        name: row.name,
        fromCellCode: row.fromCellCode,
        toCellCode: row.toCellCode
      };
      this.getCellList();
      this.positionDialogOpen = true;
    },
    /** 提交位置修改 */
    submitPositionForm() {
      const updateData = {
        id: this.positionForm.id,
        fromCellCode: this.positionForm.fromCellCode,
        toCellCode: this.positionForm.toCellCode
      };

      updateRcsCarInfo(updateData).then(response => {
        if (response.code == 200) {
          this.$modal.msgSuccess("位置修改成功");
          this.positionDialogOpen = false;
          this.getList();
        } else {
          this.$modal.msgError(response.msg || "位置修改失败");
        }
      }).catch(error => {
        this.$modal.msgError("位置修改失败：" + (error.message || "未知错误"));
      });
    },
    /** 取消位置修改 */
    cancelPositionForm() {
      this.positionDialogOpen = false;
      this.positionForm = {};
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getDeviceList();
      this.getRcsCarTypeList();
      const id = row.id || this.ids
      getRcsCarInfo(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改四向车/AGV";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRcsCarInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addRcsCarInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除四向车/AGV编号为"' + ids + '"的数据项？').then(function () {
        return delRcsCarInfo(ids);
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
      this.download('wcs-rcs/RcsCarInfo/export', {
        ...this.queryParams
      }, `RcsCarInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
