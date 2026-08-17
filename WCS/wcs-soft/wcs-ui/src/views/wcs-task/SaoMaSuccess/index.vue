<template>
  <div class="app-container">
    

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择" clearable>
          <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="班次" prop="classTime">
        <el-select v-model="queryParams.classTime" placeholder="请选择" clearable>
          <el-option v-for="item in classTimes" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="班次的总数量" prop="classNumber">
        <el-input
          v-model="queryParams.classNumber"
          placeholder="请输入班次的总数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="班次失败率" prop="classSuccess">
        <el-input
          v-model="queryParams.classSuccess"
          placeholder="请输入班次失败率"
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
          v-hasPermi="['wcs-task:SaoMaSuccess:add']"
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
          v-hasPermi="['wcs-task:SaoMaSuccess:edit']"
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
          v-hasPermi="['wcs-task:SaoMaSuccess:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-task:SaoMaSuccess:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="SaoMaSuccessList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
      <el-table-column label="类型" align="center" prop="type"  min-width="180">
        <template slot-scope="scope">
          <div v-if="scope.row.type == 1">PLC扫码成功和wms数据比对成功</div>
          <div v-if="scope.row.type == 2">PLC扫码成功和wms数据比对失败</div>
          <div v-if="scope.row.type == 3">PLC扫码失败</div>
        </template>
      </el-table-column>
      <el-table-column label="班次" align="center" prop="classTime"  min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.classTime == 1">白班(8:00-20:00)</div>
          <div v-if="scope.row.classTime == 2">夜班(20:00-8:00)</div>
        </template>
      </el-table-column>
      <!-- 空白隔断列1 -->
      <el-table-column width="20" />
      <!-- 扫码失败率统计 分组列 -->
      <el-table-column label="扫码失败率统计" align="center" min-width="240">
        <el-table-column label="扫码数量" align="center" prop="classNumber"  min-width="120" />
        <el-table-column label="班次扫码失败率" align="center" prop="classSuccess"  min-width="120" />
      </el-table-column>
      <!-- 空白隔断列2 -->
      <el-table-column width="20" />
      <!-- 任务失败率统计 分组列 -->
      <el-table-column label="任务失败率统计" align="center" min-width="240">
        <el-table-column label="失败任务数量" align="center" prop="taskNumber"  min-width="120" />
        <el-table-column label="任务失败率" align="center" prop="taskSuccess"  min-width="120" />
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime"  min-width="120" />
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:SaoMaSuccess:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-task:SaoMaSuccess:remove']"
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

    <!-- 统计日期选择区 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleQuery1"
          value-format="yyyy-MM-dd"
        />
      </el-col>
      <el-col :span="4">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery1">查询</el-button>
      </el-col>
    </el-row>

    <!-- 底部统计展示区 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="4">
        <div>统计日期（可选择）</div>
      </el-col>
      <el-col :span="4">
        <div>扫码总次数：{{ totalScanCount }}</div>
      </el-col>
      <el-col :span="4">
        <div>任务总次数：{{ totalTaskCount }}</div>
      </el-col>
    </el-row>

    <!-- 添加或修改扫描失败率对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择" clearable>
            <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班次" prop="classTime">
          <el-select v-model="form.classTime" placeholder="请选择" clearable>
            <el-option v-for="item in classTimes" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班次的总数量" prop="classNumber">
          <el-input v-model="form.classNumber" placeholder="请输入班次的总数量" />
        </el-form-item>
        <el-form-item label="班次失败率" prop="classSuccess">
          <el-input v-model="form.classSuccess" placeholder="请输入班次失败率" />
        </el-form-item>
        <el-form-item label="失败任务数量" prop="taskNumber">
          <el-input v-model="form.taskNumber" placeholder="请输入失败任务数量" />
        </el-form-item>
        <el-form-item label="任务失败率" prop="taskSuccess">
          <el-input v-model="form.taskSuccess" placeholder="请输入任务失败率" />
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
import { listSaoMaSuccess, getSaoMaSuccess, delSaoMaSuccess, addSaoMaSuccess, updateSaoMaSuccess,taskAllNumber } from "@/api/wcs-task/SaoMaSuccess";
import request from "@/utils/request";
export default {
  name: "SaoMaSuccess",
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
      // 扫描失败率表格数据
      SaoMaSuccessList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: null,
        classTime: null,
        classNumber: null,
        classSuccess: null,
        startDate: null, // 新增：开始日期
        endDate: null    // 新增：结束日期
      },
      // 表单参数
      form: {
        id: null,
        type: null,
        classTime: null,
        classNumber: null,
        classSuccess: null,
        taskNumber: null, // 新增：失败任务数量
        taskSuccess: null // 新增：任务失败率
      },
      // 表单校验
      rules: {
      },
      types:[
        { value: 1, label: "PLC扫码成功和wms数据比对成功" },
        { value: 2, label: "PLC扫码成功和wms数据比对失败" },
        { value: 3, label: "PLC扫码失败" }
      ],
      classTimes:[
        { value: 1, label: "白班(8:00-20:00)" },
        { value: 2, label: "夜班(20:00-8:00)" },
      ],
      // 新增：日期范围
      dateRange: [],
      // 新增：统计总次数
      totalScanCount: 0,
      totalTaskCount: 0
    };
  },
  created() {
    this.getList();
  },
  methods: {
    formatSuccessRate(row, column, cellValue) {
      if (cellValue === null || cellValue === undefined || cellValue === '') {
        return '';
      }
      return cellValue + '%';
    },
    /** 查询扫描失败率列表 */
    getList() {
      this.loading = true;
     
      listSaoMaSuccess(this.queryParams).then(response => {
        if(response.code==200){
          this.SaoMaSuccessList = response.rows;
          this.total = response.total;
        }
        this.loading = false;
      });
    },
    // 日期查询专用方法 handleQuery1
    handleQuery1() {
      // 处理日期参数
      this.queryParams.startDate = this.dateRange[0] || '';
      this.queryParams.endDate = this.dateRange[1] || '';
      console.log("startDate:"+this.queryParams.startDate);
      console.log("endDate:"+this.queryParams.endDate);
      taskAllNumber(this.queryParams).then(response => {
        if(response.code==200){
          // 假设后端返回统计总次数
          console.log("返回："+response.data.taskAll)
          this.totalScanCount = response.data.saoMaAll || 0;
          this.totalTaskCount = response.data.taskAll || 0;
        }
      
      });
      
    },
    // 原搜索按钮方法
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
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
        type: null,
        classTime: null,
        classNumber: null,
        classSuccess: null,
        taskNumber: null,
        taskSuccess: null
      };
      this.resetForm("form");
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.dateRange = []; // 重置日期选择
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
      this.title = "添加扫描失败率";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getSaoMaSuccess(id).then(response => {
        if(response.code==200){
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改扫描失败率";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSaoMaSuccess(this.form).then(response => {
              if(response.code==200){
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }else{
                this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addSaoMaSuccess(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除扫描失败率编号为"' + ids + '"的数据项？').then(function() {
        return delSaoMaSuccess(ids);
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
      this.download('wcs-task/SaoMaSuccess/export', {
        ...this.queryParams
      }, `SaoMaSuccess_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>