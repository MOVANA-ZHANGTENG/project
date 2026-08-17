<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="操作时间" prop="operationTime">
        <el-input v-model="queryParams.operationTime" placeholder="请输入操作时间" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作类型" prop="operationType">
        <el-select v-model="queryParams.operationType" placeholder="请选择操作类型" clearable>
          <!-- <el-option
            v-for="dict in dict.type.${dictType}"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <!-- <el-option
            v-for="dict in dict.type.${dictType}"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          /> -->
        </el-select>
      </el-form-item>
      <el-form-item label="产品编码" prop="itemCode">
        <el-input v-model="queryParams.itemCode" placeholder="请输入产品编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="产品名称" prop="itemName">
        <el-input v-model="queryParams.itemName" placeholder="请输入产品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="变更库位" prop="mapCellCode">
        <el-input v-model="queryParams.mapCellCode" placeholder="请输入变更库位" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单编号" prop="referenceNo">
        <el-input v-model="queryParams.referenceNo" placeholder="请输入订单编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="产品批次" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="请输入产品批次" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作人姓名" prop="operateUserName">
        <el-input v-model="queryParams.operateUserName" placeholder="请输入操作人姓名" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作人ip" prop="operatorIp">
        <el-input v-model="queryParams.operatorIp" placeholder="请输入操作人ip" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作来源" prop="source">
        <el-input v-model="queryParams.source" placeholder="请输入操作来源" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-input v-model="queryParams.createTime" placeholder="请输入创建时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新时间" prop="updateTime">
        <el-input v-model="queryParams.updateTime" placeholder="请输入更新时间" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-rcs:MapCellLog:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-rcs:MapCellLog:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-rcs:MapCellLog:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-rcs:MapCellLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="MapCellLogList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="操作时间" align="center" prop="operationTime" min-width="120">
      </el-table-column>
      <el-table-column label="操作类型" align="center" prop="operationType" min-width="120">
        <template slot-scope="scope">
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" min-width="120">
        <template slot-scope="scope">
        </template>
      </el-table-column>
      <el-table-column label="产品编码" align="center" prop="itemCode" min-width="120">
      </el-table-column>
      <el-table-column label="产品名称" align="center" prop="itemName" min-width="120">
      </el-table-column>
      <el-table-column label="变更前数量" align="center" prop="beforeQuantity" min-width="120">
      </el-table-column>
      <el-table-column label="变更后数量" align="center" prop="afterQuantity" min-width="120">
      </el-table-column>
      <el-table-column label="变更数量" align="center" prop="changeQuantity" min-width="120">
      </el-table-column>
      <el-table-column label="变更库位" align="center" prop="mapCellCode" min-width="120">
      </el-table-column>
      <el-table-column label="订单编号" align="center" prop="referenceNo" min-width="120">
      </el-table-column>
      <el-table-column label="产品批次" align="center" prop="batchNo" min-width="120">
      </el-table-column>
      <el-table-column label="操作人id" align="center" prop="operateUserId" min-width="120">
      </el-table-column>
      <el-table-column label="操作人姓名" align="center" prop="operateUserName" min-width="120">
      </el-table-column>
      <el-table-column label="操作人ip" align="center" prop="operatorIp" min-width="120">
      </el-table-column>
      <el-table-column label="设备信息" align="center" prop="deviceInfo" min-width="120">
      </el-table-column>
      <el-table-column label="操作来源" align="center" prop="source" min-width="120">
      </el-table-column>
      <el-table-column label="备注信息" align="center" prop="remark" min-width="120">
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="120">
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-rcs:MapCellLog:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-rcs:MapCellLog:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改库存变更对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMapCellLog, getMapCellLog, delMapCellLog, addMapCellLog, updateMapCellLog } from "@/api/wcs-rcs/MapCellLog";
import request from "@/utils/request";
export default {
  name: "MapCellLog",
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
      // 库存变更表格数据
      MapCellLogList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        operationTime: null,
        operationType: null,
        status: null,
        itemCode: null,
        itemName: null,
        beforeQuantity: null,
        afterQuantity: null,
        changeQuantity: null,
        mapCellCode: null,
        referenceNo: null,
        batchNo: null,
        operateUserId: null,
        operateUserName: null,
        operatorIp: null,
        deviceInfo: null,
        source: null,
        createTime: null,
        updateTime: null,
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
    /** 查询库存变更列表 */
    getList() {
      this.loading = true;
      listMapCellLog(this.queryParams).then(response => {
        if (response.code == 200) {
          this.MapCellLogList = response.rows;
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
        operationTime: null,
        operationType: null,
        status: null,
        itemCode: null,
        itemName: null,
        beforeQuantity: null,
        afterQuantity: null,
        changeQuantity: null,
        mapCellCode: null,
        referenceNo: null,
        batchNo: null,
        operateUserId: null,
        operateUserName: null,
        operatorIp: null,
        deviceInfo: null,
        source: null,
        remark: null,
        createTime: null,
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
      this.open = true;
      this.title = "添加库存变更";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getMapCellLog(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改库存变更";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateMapCellLog(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addMapCellLog(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除库存变更编号为"' + ids + '"的数据项？').then(function () {
        return delMapCellLog(ids);
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
      this.download('wcs-rcs/MapCellLog/export', {
        ...this.queryParams
      }, `MapCellLog_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
