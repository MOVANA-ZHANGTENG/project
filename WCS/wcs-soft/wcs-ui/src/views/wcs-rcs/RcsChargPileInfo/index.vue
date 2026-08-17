<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="位置ID" prop="cellId">
        <el-input
          v-model="queryParams.cellId"
          placeholder="请输入位置ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input
          v-model="queryParams.wareCode"
          placeholder="请输入仓库编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用的车子类型" prop="rcsCarTypeId">
        <el-input
          v-model="queryParams.rcsCarTypeId"
          placeholder="请输入使用的车子类型"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前充电的车子" prop="carId">
        <el-input
          v-model="queryParams.carId"
          placeholder="请输入当前充电的车子"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="禁用状态" prop="disableState">
        <el-input
          v-model="queryParams.disableState"
          placeholder="请输入禁用状态"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['wcs-rcs:RcsChargPileInfo:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['wcs-rcs:RcsChargPileInfo:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['wcs-rcs:RcsChargPileInfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-rcs:RcsChargPileInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="RcsChargPileInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
    <el-table-column label="编码" align="center" prop="code"  min-width="120">
    </el-table-column>
    <el-table-column label="位置ID" align="center" prop="cellId"  min-width="120">
    </el-table-column>
    <el-table-column label="仓库编码" align="center" prop="wareCode"  min-width="120">
    </el-table-column>
    <el-table-column label="使用的车子类型" align="center" prop="rcsCarTypeId"  min-width="120">
    </el-table-column>
    <el-table-column label="当前充电的车子" align="center" prop="carId"  min-width="120">
    </el-table-column>
    <el-table-column label="禁用状态" align="center" prop="disableState"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-rcs:RcsChargPileInfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-rcs:RcsChargPileInfo:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改充电桩对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="位置ID" prop="cellId">
          <el-input v-model="form.cellId" placeholder="请输入位置ID" />
        </el-form-item>
        <el-form-item label="仓库编码" prop="wareCode">
          <el-input v-model="form.wareCode" placeholder="请输入仓库编码" />
        </el-form-item>
        <el-form-item label="使用的车子类型" prop="rcsCarTypeId">
          <el-input v-model="form.rcsCarTypeId" placeholder="请输入使用的车子类型" />
        </el-form-item>
        <el-form-item label="当前充电的车子" prop="carId">
          <el-input v-model="form.carId" placeholder="请输入当前充电的车子" />
        </el-form-item>
        <el-form-item label="禁用状态" prop="disableState">
          <el-input v-model="form.disableState" placeholder="请输入禁用状态" />
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
import { listRcsChargPileInfo, getRcsChargPileInfo, delRcsChargPileInfo, addRcsChargPileInfo, updateRcsChargPileInfo } from "@/api/wcs-rcs/RcsChargPileInfo";
import request from "@/utils/request";
export default {
  name: "RcsChargPileInfo",
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
      // 充电桩表格数据
      RcsChargPileInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: null,
        cellId: null,
        wareCode: null,
        rcsCarTypeId: null,
        carId: null,
        disableState: null
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
    /** 查询充电桩列表 */
    getList() {
      this.loading = true;
      listRcsChargPileInfo(this.queryParams).then(response => {
          if(response.code==200){
            this.RcsChargPileInfoList = response.rows;
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
        code: null,
        cellId: null,
        wareCode: null,
        rcsCarTypeId: null,
        carId: null,
        disableState: null
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加充电桩";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getRcsChargPileInfo(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改充电桩";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRcsChargPileInfo(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addRcsChargPileInfo(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
              }else{
                this.$modal.msgError(response.msg||"新增失败");
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除充电桩编号为"' + ids + '"的数据项？').then(function() {
        return delRcsChargPileInfo(ids);
      }).then((response) => {
          if(response.code==200){
            this.getList();
            this.$modal.msgSuccess("删除成功");
          }else{
            this.$modal.msgError(response.msg||"删除失败");
          }

      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-rcs/RcsChargPileInfo/export', {
        ...this.queryParams
      }, `RcsChargPileInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
