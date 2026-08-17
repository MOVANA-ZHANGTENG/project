<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="生产任务ID" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
          placeholder="请输入生产任务ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工艺节点ID" prop="nodeId">
       <el-select v-model="queryParams.nodeId" placeholder="请选择工艺流程ID">
            <el-option
              v-for="dict in ProcessNodeListByRouteId"
              :key="dict.id"
              :label="dict.nodeName"
              :value="dict.id"
            ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="节点顺序" prop="sequence">
        <el-input
          v-model="queryParams.sequence"
          placeholder="请输入节点顺序"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="站台ID" prop="positionId">
        <el-select v-model="queryParams.positionId" placeholder="请选择站台ID">
          <el-option
            v-for="dict in positionList"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="使用夹具类型ID" prop="fixtureTypeId">
         <el-select v-model="queryParams.fixtureTypeId" placeholder="请选择所需夹具类型ID">
          <el-option
            v-for="dict in fixtureTypeList"
            :key="dict.id"
            :label="dict.typeName"
            :value="dict.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-input
          v-model="queryParams.startTime"
          placeholder="请输入开始时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-input
          v-model="queryParams.endTime"
          placeholder="请输入结束时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in statusList"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="实际加工时长" prop="actualDuration">
        <el-input
          v-model="queryParams.actualDuration"
          placeholder="请输入实际加工时长"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- <el-form-item label="操作人ID" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
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
          v-hasPermi="['wcs-xlPro:ProductionTaskDetail:add']"
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
          v-hasPermi="['wcs-xlPro:ProductionTaskDetail:edit']"
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
          v-hasPermi="['wcs-xlPro:ProductionTaskDetail:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:ProductionTaskDetail:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProductionTaskDetailList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="生产任务ID" align="center" prop="taskId"  min-width="120">
    </el-table-column>
    <el-table-column label="工艺节点ID" align="center" prop="nodeId"  min-width="120">
      <template slot-scope="scope">
        <!-- 遍历ProcessNodeListByRouteId，匹配nodeId与当前行的nodeId -->
        <span v-for="item in ProcessNodeListByRouteId" :key="item.id">
          <span v-if="item.id === scope.row.nodeId">{{ item.nodeName }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!ProcessNodeListByRouteId.some(item => item.id === scope.row.nodeId)">
          无匹配工艺节点
        </span> -->
      </template>
    </el-table-column>
    <el-table-column label="节点顺序" align="center" prop="sequence"  min-width="120">
    </el-table-column>
    <el-table-column label="站台ID" align="center" prop="positionId"  min-width="120">
      <template slot-scope="scope">
        <!-- 遍历positionList，匹配positionId与当前行的positionId -->
        <span v-for="item in positionList" :key="item.id">
          <span v-if="item.id === scope.row.positionId">{{ item.name }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!positionList.some(item => item.id === scope.row.positionId)">
          无匹配站台
        </span> -->
      </template>
    </el-table-column>
    <el-table-column label="使用夹具类型ID" align="center" prop="fixtureTypeId"  min-width="120">
      <template slot-scope="scope">
        <!-- 遍历fixtureTypeList，匹配fixtureTypeId与当前行的fixtureTypeId -->
        <span v-for="item in fixtureTypeList" :key="item.id">
          <span v-if="item.id === scope.row.fixtureTypeId">{{ item.typeName }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!fixtureTypeList.some(item => item.id === scope.row.fixtureTypeId)">
          无匹配夹具类型
        </span> -->
      </template>
    </el-table-column>
    <el-table-column label="开始时间" align="center" prop="startTime"  min-width="120">
    </el-table-column>
    <el-table-column label="结束时间" align="center" prop="endTime"  min-width="120">
    </el-table-column>
      <el-table-column label="状态" align="center" prop="status"  min-width="120">
        <template slot-scope="scope">
          <div v-for="dict in statusList" :key="dict.value">
            <span v-if="scope.row.status === dict.value">{{ dict.label }}</span>
          </div>
        </template>
      </el-table-column>
    <el-table-column label="实际加工时长" align="center" prop="actualDuration"  min-width="120">
    </el-table-column>
    <el-table-column label="操作人ID" align="center" prop="operatorId"  min-width="120">
    </el-table-column>
    <el-table-column label="备注" align="center" prop="remark"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:ProductionTaskDetail:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:ProductionTaskDetail:remove']"
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

    <!-- 添加或修改生产任务明细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="生产任务ID" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入生产任务ID" />
        </el-form-item>
        <el-form-item label="工艺节点ID" prop="nodeId">
          <el-select v-model="form.nodeId" placeholder="请选择工艺流程ID">
            <el-option
              v-for="dict in ProcessNodeListByRouteId"
              :key="dict.id"
              :label="dict.nodeName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="节点顺序" prop="sequence">
          <el-input v-model="form.sequence" placeholder="请输入节点顺序" />
        </el-form-item>
        <el-form-item label="站台ID" prop="positionId">
          <el-select v-model="form.positionId" placeholder="请选择站台ID">
            <el-option
              v-for="dict in positionList"
              :key="dict.id"
              :label="dict.name"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="使用夹具类型ID" prop="fixtureTypeId">
          <el-select v-model="form.fixtureTypeId" placeholder="请选择所需夹具类型ID">
            <el-option
              v-for="dict in fixtureTypeList"
              :key="dict.id"
              :label="dict.typeName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-input v-model="form.startTime" placeholder="请输入开始时间" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-input v-model="form.endTime" placeholder="请输入结束时间" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusList"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="实际加工时长" prop="actualDuration">
          <el-input v-model="form.actualDuration" placeholder="请输入实际加工时长" />
        </el-form-item>
        <el-form-item label="操作人ID" prop="operatorId">
          <el-input v-model="form.operatorId" placeholder="请输入操作人ID" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
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
import { listProductionTaskDetail, getProductionTaskDetail, delProductionTaskDetail, addProductionTaskDetail, updateProductionTaskDetail } from "@/api/wcs-xlPro/ProductionTaskDetail";
import { getProcessNodeListByRouteId,getPositionList,getFixtureTypeList } from "@/api/wcs-xlPro/ProcessNodePosition";
import request from "@/utils/request";
export default {
  name: "ProductionTaskDetail",
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
      showSearch: false,
      // 总条数
      total: 0,
      // 生产任务明细表格数据
      ProductionTaskDetailList: [],
      fixtureTypeList: [],
      ProcessNodePositionList: [],
      ProcessNodeListByRouteId: [],
      positionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskId: null,
        nodeId: null,
        sequence: null,
        positionId: null,
        fixtureTypeId: null,
        startTime: null,
        endTime: null,
        status: null,
        actualDuration: null,
        operatorId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      statusList: [
        { value: 0, label: '待加工' },
        { value: 1, label: '加工中' },
        { value: 2, label: '已完成' },
        { value: 3, label: '异常' },
        { value: 4, label: '跳过' },
        { value: 5, label: '未生成任务' },
      
      ],
    };
  },
  created() {
    // 1. 获取URL中携带的taskId（生产任务id）
    const taskId = this.$route.query.taskId;
    if (taskId) {
      this.queryParams.taskId = taskId; // 赋值给查询参数
    }
    this.getList();  
    this.getFixtureTypeList();
    this.getPositionList();
    this.getProcessNodeListByRouteId();
  },
  methods: {
    getProcessNodeListByRouteId() {
      getProcessNodeListByRouteId().then(response => {
          if(response.code==200){
            this.ProcessNodeListByRouteId = response.data;
          }
      });
    },
    getPositionList() {
      getPositionList().then(response => {
          if(response.code==200){
            this.positionList = response.data;
          }
      });
    },
    getFixtureTypeList() {
      getFixtureTypeList().then(response => {
          if(response.code==200){
            this.fixtureTypeList = response.data;
          }
      });
    },
    /** 查询生产任务明细列表 */
    getList() {
      this.loading = true;
      listProductionTaskDetail(this.queryParams).then(response => {
          if(response.code==200){
            this.ProductionTaskDetailList = response.rows;
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
        taskId: null,
        nodeId: null,
        sequence: null,
        positionId: null,
        fixtureTypeId: null,
        startTime: null,
        endTime: null,
        status: null,
        actualDuration: null,
        operatorId: null,
        remark: null,
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
      this.title = "添加生产任务明细";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProductionTaskDetail(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改生产任务明细";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProductionTaskDetail(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProductionTaskDetail(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除生产任务明细编号为"' + ids + '"的数据项？').then(function() {
        return delProductionTaskDetail(ids);
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
      this.download('wcs-xlPro/ProductionTaskDetail/export', {
        ...this.queryParams
      }, `ProductionTaskDetail_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
