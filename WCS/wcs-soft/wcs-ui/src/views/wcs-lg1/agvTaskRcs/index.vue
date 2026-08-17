<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="请求编号" prop="reqCode">
        <el-input
          v-model="queryParams.reqCode"
          placeholder="请输入请求编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请求时间" prop="reqTime">
        <el-input
          v-model="queryParams.reqTime"
          placeholder="请输入请求时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地码:X" prop="cooX">
        <el-input
          v-model="queryParams.cooX"
          placeholder="请输入地码:X"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地码:Y" prop="cooY">
        <el-input
          v-model="queryParams.cooY"
          placeholder="请输入地码:Y"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前位置编号" prop="currentPositionCode">
        <el-input
          v-model="queryParams.currentPositionCode"
          placeholder="请输入当前位置编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地图编号" prop="mapCode">
        <el-input
          v-model="queryParams.mapCode"
          placeholder="请输入地图编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地码编号" prop="mapDataCode">
        <el-input
          v-model="queryParams.mapDataCode"
          placeholder="请输入地码编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="方法名" prop="method">
        <el-input
          v-model="queryParams.method"
          placeholder="请输入方法名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="货架编号" prop="podCode">
        <el-input
          v-model="queryParams.podCode"
          placeholder="请输入货架编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地图的方位" prop="podDir">
        <el-input
          v-model="queryParams.podDir"
          placeholder="请输入地图的方位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物料编号" prop="materialLot">
        <el-input
          v-model="queryParams.materialLot"
          placeholder="请输入物料编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="AGV编号" prop="robotCode">
        <el-input
          v-model="queryParams.robotCode"
          placeholder="请输入AGV编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前任务单号" prop="taskCode">
        <el-input
          v-model="queryParams.taskCode"
          placeholder="请输入当前任务单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工作位" prop="wbCode">
        <el-input
          v-model="queryParams.wbCode"
          placeholder="请输入工作位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择" clearable>
          <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
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
          v-hasPermi="['lg1:agvTaskRcs:add']"
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
          v-hasPermi="['lg1:agvTaskRcs:edit']"
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
          v-hasPermi="['lg1:agvTaskRcs:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['lg1:agvTaskRcs:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="agvTaskRcsList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="请求编号" align="center" prop="reqCode"  min-width="120">
    </el-table-column>
    <el-table-column label="请求时间" align="center" prop="reqTime"  min-width="120">
    </el-table-column>
    <el-table-column label="地码:X" align="center" prop="cooX"  min-width="120">
    </el-table-column>
    <el-table-column label="地码:Y" align="center" prop="cooY"  min-width="120">
    </el-table-column>
    <el-table-column label="当前位置编号" align="center" prop="currentPositionCode"  min-width="120">
    </el-table-column>
    <el-table-column label="地图编号" align="center" prop="mapCode"  min-width="120">
    </el-table-column>
    <el-table-column label="地码编号" align="center" prop="mapDataCode"  min-width="120">
    </el-table-column>
    <el-table-column label="方法名" align="center" prop="method"  min-width="120">
    </el-table-column>
    <el-table-column label="货架编号" align="center" prop="podCode"  min-width="120">
    </el-table-column>
    <el-table-column label="地图方位" align="center" prop="podDir"  min-width="120">
    </el-table-column>
    <el-table-column label="物料编号" align="center" prop="materialLot"  min-width="120">
    </el-table-column>
    <el-table-column label="AGV编号" align="center" prop="robotCode"  min-width="120">
    </el-table-column>
    <el-table-column label="当前任务单号" align="center" prop="taskCode"  min-width="120">
    </el-table-column>
    <el-table-column label="工作位" align="center" prop="wbCode"  min-width="120">
    </el-table-column>
    <el-table-column label="状态" align="center" prop="state"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.state == 0" style="color: #F56C6C;">未处理</div>
          <div v-if="scope.row.state == 1" style="color: #67C23A;">已处理</div>
        </template>
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['lg1:agvTaskRcs:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['lg1:agvTaskRcs:remove']"
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

    <!-- 添加或修改接收agv回传信息对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="请求编号" prop="reqCode">
          <el-input v-model="form.reqCode" placeholder="请输入请求编号" />
        </el-form-item>
        <el-form-item label="请求时间" prop="reqTime">
          <el-input v-model="form.reqTime" placeholder="请输入请求时间" />
        </el-form-item>
        <el-form-item label="地码:X" prop="cooX">
          <el-input v-model="form.cooX" placeholder="请输入地码:X" />
        </el-form-item>
        <el-form-item label="地码:Y" prop="cooY">
          <el-input v-model="form.cooY" placeholder="请输入地码:Y" />
        </el-form-item>
        <el-form-item label="当前位置编号" prop="currentPositionCode">
          <el-input v-model="form.currentPositionCode" placeholder="请输入当前位置编号" />
        </el-form-item>
        <el-form-item label="地图编号" prop="mapCode">
          <el-input v-model="form.mapCode" placeholder="请输入地图编号" />
        </el-form-item>
        <el-form-item label="地码编号" prop="mapDataCode">
          <el-input v-model="form.mapDataCode" placeholder="请输入地码编号" />
        </el-form-item>
        <el-form-item label="方法名" prop="method">
          <el-input v-model="form.method" placeholder="请输入方法名" />
        </el-form-item>
        <el-form-item label="货架编号" prop="podCode">
          <el-input v-model="form.podCode" placeholder="请输入货架编号" />
        </el-form-item>
        <el-form-item label="地图方位" prop="podDir">
          <el-input v-model="form.podDir" placeholder="请输入地图方位" />
        </el-form-item>
        <el-form-item label="物料编号" prop="materialLot">
          <el-input v-model="form.materialLot" placeholder="请输入物料编号" />
        </el-form-item>
        <el-form-item label="AGV编号" prop="robotCode">
          <el-input v-model="form.robotCode" placeholder="请输入AGV编号" />
        </el-form-item>
        <el-form-item label="当前任务单号" prop="taskCode">
          <el-input v-model="form.taskCode" placeholder="请输入当前任务单号" />
        </el-form-item>
        <el-form-item label="工作位" prop="wbCode">
          <el-input v-model="form.wbCode" placeholder="请输入工作位" />
        </el-form-item>
        <el-form-item label="状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择" clearable>
            <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
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
import { listAgvTaskRcs, getAgvTaskRcs, delAgvTaskRcs, addAgvTaskRcs, updateAgvTaskRcs } from "@/api/lg1/agvTaskRcs";

export default {
  name: "AgvTaskRcs",
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
      // 接收agv回传信息表格数据
      agvTaskRcsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reqCode: null,
        reqTime: null,
        cooX: null,
        cooY: null,
        currentPositionCode: null,
        mapCode: null,
        mapDataCode: null,
        method: null,
        podCode: null,
        podDir: null,
        materialLot: null,
        robotCode: null,
        taskCode: null,
        wbCode: null,
        state: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      states: [
        { value: 0, label: "未处理" },
        { value: 1, label: "已处理" },
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询接收agv回传信息列表 */
    getList() {
      this.loading = true;
      listAgvTaskRcs(this.queryParams).then(response => {
          if(response.code==200){
            this.agvTaskRcsList = response.rows;
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
        reqCode: null,
        reqTime: null,
        cooX: null,
        cooY: null,
        currentPositionCode: null,
        mapCode: null,
        mapDataCode: null,
        method: null,
        podCode: null,
        podDir: null,
        materialLot: null,
        robotCode: null,
        taskCode: null,
        wbCode: null,
        state: null,
        createTime: null
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
      this.title = "添加接收agv回传信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAgvTaskRcs(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改接收agv回传信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAgvTaskRcs(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg);
              }
            });
          } else {
            addAgvTaskRcs(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除接收agv回传信息编号为"' + ids + '"的数据项？').then(function() {
        return delAgvTaskRcs(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('lg1/agvTaskRcs/export', {
        ...this.queryParams
      }, `agvTaskRcs_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
