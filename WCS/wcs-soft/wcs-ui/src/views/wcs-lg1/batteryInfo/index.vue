<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="电极编码" prop="batteryCode">
        <el-input
          v-model="queryParams.batteryCode"
          placeholder="请输入电极编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产线编码" prop="productionLineCode">
        <el-select  v-model="queryParams.productionLineCode"  placeholder="请输入电极编码" clearable>
                        <el-option
                          v-for="item in productionLineCodes"
                          :key="item"
                          :label="item"
                          :value="item"
                         />
          </el-select>
      </el-form-item>
      <el-form-item label="阴/阳" prop="mark">
        <el-select v-model="queryParams.mark" placeholder="请选择" clearable>
          <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
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
          v-hasPermi="['wcs-lg1:batteryInfo:add']"
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
          v-hasPermi="['wcs-lg1:batteryInfo:edit']"
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
          v-hasPermi="['wcs-lg1:batteryInfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-lg1:batteryInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="batteryInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="电极编码" align="center" prop="batteryCode"  min-width="120">
    </el-table-column>
    <el-table-column label="产线编码" align="center" prop="productionLineCode"  min-width="120">
    </el-table-column>
    <el-table-column label="阴/阳" align="center" prop="mark"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.mark == 1">阴</div>
          <div v-if="scope.row.mark == 2">阳</div>
        </template>
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-lg1:batteryInfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-lg1:batteryInfo:remove']"
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

    <!-- 添加或修改电极信息对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="电极编码" prop="batteryCode">
          <el-input v-model="form.batteryCode" placeholder="请输入电极编码" />
        </el-form-item>
        <el-form-item label="产线编码" prop="productionLineCode">
          <el-select  v-model="form.productionLineCode"  placeholder="请输入电极编码" clearable>
                        <el-option
                          v-for="item in productionLineCodes"
                          :key="item"
                          :label="item"
                          :value="item"
                         />
          </el-select>
        </el-form-item>
        <el-form-item label="阴/阳" prop="mark">
          <el-select v-model="form.mark" placeholder="请选择" clearable>
            <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
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
import { listBatteryInfo, getBatteryInfo, delBatteryInfo, addBatteryInfo, updateBatteryInfo } from "@/api/wcs-lg1/batteryInfo";
import request from "@/utils/request.js";

export default {
  name: "BatteryInfo",
  data() {
    return {
      productionLineCodes: [],
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
      // 电极信息表格数据
      batteryInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batteryCode: null,
        productionLineCode: null,
        mark: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      marks: [
        { value: 1, label: "阴" },
        { value: 2, label: "阳" },
      ],
    };
  },
  created() {
    this.getList();
    this.getProductionLineCodes();
  },
  methods: {

    //查询所有产线
    getProductionLineCodes() {
      request({
        url: "/wcs-lg1/cellInfoLg/productionLineCode",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          this.productionLineCodes = response.data;
          console.log(this.productionLineCodes)
        }
      });
    },

    /** 查询电极信息列表 */
    getList() {
      this.loading = true;
      listBatteryInfo(this.queryParams).then(response => {
          if(response.code==200){
            this.batteryInfoList = response.rows;
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
        batteryCode: null,
        productionLineCode: null,
        mark: null,
        createTime: null,
        updateTime: null
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
      this.title = "添加电极信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getBatteryInfo(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改电极信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateBatteryInfo(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg);
              }
            });
          } else {
            addBatteryInfo(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
              }else{
                this.$modal.msgError(response.msg);
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除电极信息编号为"' + ids + '"的数据项？').then(function() {
        return delBatteryInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-lg1/batteryInfo/export', {
        ...this.queryParams
      }, `batteryInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
