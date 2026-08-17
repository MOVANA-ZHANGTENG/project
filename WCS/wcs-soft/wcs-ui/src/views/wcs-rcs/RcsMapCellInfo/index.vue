<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left"
      label-width="100px">
      <el-form-item label="上位库位编码" prop="cellCode">
        <el-input v-model="queryParams.cellCode" placeholder="请输入上位库位编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="库位编码" prop="mapCellCode">
        <el-input v-model="queryParams.mapCellCode" placeholder="请输入库位编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="库位名称" prop="mapCellName">
        <el-input v-model="queryParams.mapCellName" placeholder="请输入库位名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input v-model="queryParams.wareCode" placeholder="请输入仓库编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库名称" prop="wareName">
        <el-input v-model="queryParams.wareName" placeholder="请输入仓库名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="区域编码" prop="areaCode">
        <el-input v-model="queryParams.areaCode" placeholder="请输入区域编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="区域名称" prop="areaName">
        <el-input v-model="queryParams.areaName" placeholder="请输入区域名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="库位类型" prop="mapCellType">
        <el-select v-model="queryParams.mapCellType" placeholder="请选择库位类型" clearable>
          <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="库位状态" prop="mapCellStatus">
        <el-select v-model="queryParams.mapCellStatus" placeholder="请选择库位状态" clearable>
          <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="可移动方向" prop="allowedDirections">
        <el-input v-model="queryParams.allowedDirections" placeholder="请输入可移动方向" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="坐标Z值" prop="mapCellZ">
        <el-input v-model="queryParams.mapCellZ" placeholder="请输入坐标Z值" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="存储策略" prop="storagePolicy">
        <el-input v-model="queryParams.storagePolicy" placeholder="请输入存储策略" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="可存放产品" prop="allowedSkuTypes">
        <el-input v-model="queryParams.allowedSkuTypes" placeholder="请输入可存放产品" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="使用优先级" prop="priority">
        <el-input v-model="queryParams.priority" placeholder="请输入使用优先级" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="是否可堆叠" prop="stackable">
        <el-input v-model="queryParams.stackable" placeholder="请输入是否可堆叠" clearable @keyup.enter.native="handleQuery" />
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
      <el-form-item label="设备ID" prop="deviceId">
        <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="小车ID" prop="carId">
        <el-input v-model="queryParams.carId" placeholder="请输入小车ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-rcs:RcsMapCellInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-rcs:RcsMapCellInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-rcs:RcsMapCellInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-rcs:RcsMapCellInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="RcsMapCellInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="上位库位编码" align="center" prop="cellCode" min-width="120">
      </el-table-column>
      <el-table-column label="库位编码" align="center" prop="mapCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="库位名称" align="center" prop="mapCellName" min-width="120">
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="120">
      </el-table-column>
      <el-table-column label="区域编码" align="center" prop="areaCode" min-width="120">
      </el-table-column>
      <el-table-column label="区域名称" align="center" prop="areaName" min-width="120">
      </el-table-column>
      <el-table-column label="库位类型" align="center" prop="mapCellType" min-width="120">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.mapCellType" /> -->
        </template>
      </el-table-column>
      <el-table-column label="库位状态" align="center" prop="mapCellStatus" min-width="120">
        <template slot-scope="scope">
          <!-- <dict-tag :options="dict.type.${ column.dictType }" :value="scope.row.mapCellStatus" /> -->
        </template>
      </el-table-column>
      <el-table-column label="坐标X值" align="center" prop="mapCellX" min-width="120">
      </el-table-column>
      <el-table-column label="坐标Y值" align="center" prop="mapCellY" min-width="120">
      </el-table-column>
      <el-table-column label="可移动方向" align="center" prop="allowedDirections" min-width="120">
      </el-table-column>
      <el-table-column label="坐标Z值" align="center" prop="mapCellZ" min-width="120">
      </el-table-column>
      <el-table-column label="最大存储容量" align="center" prop="maxCapacity" min-width="120">
      </el-table-column>
      <el-table-column label="当前存储容量" align="center" prop="currentCapacity" min-width="120">
      </el-table-column>
      <el-table-column label="存储策略" align="center" prop="storagePolicy" min-width="120">
      </el-table-column>
      <el-table-column label="可存放产品" align="center" prop="allowedSkuTypes" min-width="120">
      </el-table-column>
      <el-table-column label="使用优先级" align="center" prop="priority" min-width="120">
      </el-table-column>
      <el-table-column label="是否可堆叠" align="center" prop="stackable" min-width="120">
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="120">
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" min-width="120">
      </el-table-column>
      <el-table-column label="创建用户" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column label="文本备注" align="center" prop="remark" min-width="120">
      </el-table-column>
      <el-table-column label="设备ID" align="center" prop="deviceId" min-width="120">
      </el-table-column>
      <el-table-column label="小车ID" align="center" prop="carId" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-rcs:RcsMapCellInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-rcs:RcsMapCellInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改RCS库位信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上位库位编码" prop="cellCode">
          <el-input v-model="form.cellCode" placeholder="请输入上位库位编码" />
        </el-form-item>
        <el-form-item label="库位编码" prop="mapCellCode">
          <el-input v-model="form.mapCellCode" placeholder="请输入库位编码" />
        </el-form-item>
        <el-form-item label="库位名称" prop="mapCellName">
          <el-input v-model="form.mapCellName" placeholder="请输入库位名称" />
        </el-form-item>
        <el-form-item label="仓库编码" prop="wareCode">
          <el-input v-model="form.wareCode" placeholder="请输入仓库编码" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="wareName">
          <el-input v-model="form.wareName" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="区域编码" prop="areaCode">
          <el-input v-model="form.areaCode" placeholder="请输入区域编码" />
        </el-form-item>
        <el-form-item label="区域名称" prop="areaName">
          <el-input v-model="form.areaName" placeholder="请输入区域名称" />
        </el-form-item>
        <el-form-item label="库位类型" prop="mapCellType">
          <el-select v-model="form.mapCellType" placeholder="请选择库位类型">
            <!-- <el-option v-for="dict in dict.type.${ dictType }" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="库位状态" prop="mapCellStatus">
          <el-radio-group v-model="form.mapCellStatus">
            <!-- <el-radio v-for="dict in dict.type.${ dictType }" :key="dict.value"
              :label="dict.value">{{ dict.label }}</el-radio> -->
          </el-radio-group>
        </el-form-item>
        <el-form-item label="坐标X值" prop="mapCellX">
          <el-input v-model="form.mapCellX" placeholder="请输入坐标X值" />
        </el-form-item>
        <el-form-item label="坐标Y值" prop="mapCellY">
          <el-input v-model="form.mapCellY" placeholder="请输入坐标Y值" />
        </el-form-item>
        <el-form-item label="可移动方向" prop="allowedDirections">
          <el-input v-model="form.allowedDirections" placeholder="请输入可移动方向" />
        </el-form-item>
        <el-form-item label="坐标Z值" prop="mapCellZ">
          <el-input v-model="form.mapCellZ" placeholder="请输入坐标Z值" />
        </el-form-item>
        <el-form-item label="最大存储容量" prop="maxCapacity">
          <el-input v-model="form.maxCapacity" placeholder="请输入最大存储容量" />
        </el-form-item>
        <el-form-item label="当前存储容量" prop="currentCapacity">
          <el-input v-model="form.currentCapacity" placeholder="请输入当前存储容量" />
        </el-form-item>
        <el-form-item label="存储策略" prop="storagePolicy">
          <el-input v-model="form.storagePolicy" placeholder="请输入存储策略" />
        </el-form-item>
        <el-form-item label="可存放产品" prop="allowedSkuTypes">
          <el-input v-model="form.allowedSkuTypes" placeholder="请输入可存放产品" />
        </el-form-item>
        <el-form-item label="使用优先级" prop="priority">
          <el-input v-model="form.priority" placeholder="请输入使用优先级" />
        </el-form-item>
        <el-form-item label="是否可堆叠" prop="stackable">
          <el-input v-model="form.stackable" placeholder="请输入是否可堆叠" />
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
import { listRcsMapCellInfo, getRcsMapCellInfo, delRcsMapCellInfo, addRcsMapCellInfo, updateRcsMapCellInfo } from "@/api/wcs-rcs/RcsMapCellInfo";
import request from "@/utils/request";
export default {
  name: "RcsMapCellInfo",
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
      // RCS库位信息表格数据
      RcsMapCellInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        cellCode: null,
        mapCellCode: null,
        mapCellName: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        mapCellType: null,
        mapCellStatus: null,
        allowedDirections: null,
        mapCellZ: null,
        storagePolicy: null,
        allowedSkuTypes: null,
        priority: null,
        stackable: null,
        createTime: null,
        updateTime: null,
        createUserName: null,
        deviceId: null,
        carId: null
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
    /** 查询RCS库位信息列表 */
    getList() {
      this.loading = true;
      listRcsMapCellInfo(this.queryParams).then(response => {
        if (response.code == 200) {
          this.RcsMapCellInfoList = response.rows;
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
        cellCode: null,
        mapCellCode: null,
        mapCellName: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        mapCellType: null,
        mapCellStatus: null,
        mapCellX: null,
        mapCellY: null,
        allowedDirections: null,
        mapCellZ: null,
        maxCapacity: null,
        currentCapacity: null,
        storagePolicy: null,
        allowedSkuTypes: null,
        priority: null,
        stackable: null,
        createTime: null,
        updateTime: null,
        createUserName: null,
        remark: null,
        deviceId: null,
        carId: null
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
      this.title = "添加RCS库位信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getRcsMapCellInfo(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改RCS库位信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRcsMapCellInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addRcsMapCellInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除RCS库位信息编号为"' + ids + '"的数据项？').then(function () {
        return delRcsMapCellInfo(ids);
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
      this.download('wcs-rcs/RcsMapCellInfo/export', {
        ...this.queryParams
      }, `RcsMapCellInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
