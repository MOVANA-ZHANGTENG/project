<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="150px">
      <el-form-item label="设备编码原料名" prop="deviceCodeNName">
        <el-input
          v-model="queryParams.deviceCodeNName"
          placeholder="请输入设备编码原料名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="位置编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入位置编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="plc读取的原料名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入plc读取的原料名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="plc读取的原料id" prop="materialId">
        <el-input
          v-model="queryParams.materialId"
          placeholder="请输入plc读取的原料id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="plc读取的原料的余料" prop="last">
        <el-input
          v-model="queryParams.last"
          placeholder="请输入plc读取的原料的余料"
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
          v-hasPermi="['wcs-base:PlcReadStation:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini"  :disabled="single" @click="handleAdd2"
          v-hasPermi="['wcs-base:PositionInfo:add']">复制新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['wcs-base:PlcReadStation:edit']"
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
          v-hasPermi="['wcs-base:PlcReadStation:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-base:PlcReadStation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="PlcReadStationList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="设备编码名称" align="center" prop="deviceCodeName"  min-width="120">
    </el-table-column>
    <el-table-column label="设备编码ID" align="center" prop="deviceCodeId"  min-width="120">
    </el-table-column>
    <el-table-column label="设备编码余量" align="center" prop="deviceCodeLast"  min-width="120">
    </el-table-column>
    <el-table-column label="位置编码" align="center" prop="code"  min-width="120">
    </el-table-column>
    <el-table-column label="plc读取的原料名" align="center" prop="name"  min-width="120">
    </el-table-column>
    <el-table-column label="plc读取的原料id" align="center" prop="materialId"  min-width="120">
    </el-table-column>
    <el-table-column label="plc读取的原料的余料" align="center" prop="last"  min-width="120">
    </el-table-column>
    <el-table-column label="禁用状态" align="center" prop="disableState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.disable_state" :value="scope.row.disableState" />
        </template>
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:PlcReadStation:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:PlcReadStation:remove']"
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

    <!-- 添加或修改plc读取站台信号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="设备编码原料名" prop="deviceCodeName">
          <el-input v-model="form.deviceCodeName" placeholder="请输入设备编码原料名" />
        </el-form-item>
        <el-form-item label="设备编码Id" prop="deviceCodeId">
          <el-input v-model="form.deviceCodeId" placeholder="请输入设备编码Id" />
        </el-form-item>
        <el-form-item label="设备编码余量" prop="deviceCodeLast">
          <el-input v-model="form.deviceCodeLast" placeholder="请输入设备编码余量" />
        </el-form-item>
        <el-form-item label="位置编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入位置编码" />
        </el-form-item>
        <el-form-item label="plc读取的原料名" prop="name">
          <el-input v-model="form.name" placeholder="请输入plc读取的原料名" />
        </el-form-item>
        <el-form-item label="plc读取的原料id" prop="materialId">
          <el-input v-model="form.materialId" placeholder="请输入plc读取的原料id" />
        </el-form-item>
        <el-form-item label="plc读取的原料的余料" prop="last">
          <el-input v-model="form.last" placeholder="请输入plc读取的原料的余料" />
        </el-form-item>
        <el-form-item label="禁用状态" prop="disableState">
          <el-input v-model="form.disableState" placeholder="禁用状态">
          </el-input>
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
import { listPlcReadStation, getPlcReadStation, delPlcReadStation, addPlcReadStation, updatePlcReadStation } from "@/api/wcs-base/PlcReadStation";
import request from "@/utils/request";
export default {
  name: "PlcReadStation",
  dicts: [ "disable_state"],
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
      // plc读取站台信号表格数据
      PlcReadStationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceCode: null,
        code: null,
        name: null,
        materialId: null,
        last: null
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
    /** 查询plc读取站台信号列表 */
    getList() {
      this.loading = true;
      listPlcReadStation(this.queryParams).then(response => {
          if(response.code==200){
            this.PlcReadStationList = response.rows;
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
        deviceCode: null,
        code: null,
        name: null,
        materialId: null,
        last: null
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
      this.title = "添加plc读取站台信号";
    },
    handleAdd2(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionInfo(id).then(response => {
        this.form = response.data;
        this.form.name="";
        this.form.id=null;
        this.open = true;
        this.title = "添加站台";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPlcReadStation(id).then(response => {
        this.form = response.data;
        
        this.open = true;
        this.title = "修改plc读取站台信号";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePlcReadStation(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addPlcReadStation(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("新增成功");
                  //this.open = false;
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
      this.$modal.confirm('是否确认删除plc读取站台信号编号为"' + ids + '"的数据项？').then(function() {
        return delPlcReadStation(ids);
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
      this.download('wcs-base/PlcReadStation/export', {
        ...this.queryParams
      }, `PlcReadStation_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
