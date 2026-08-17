<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="任务编码" prop="taskCode">
        <el-input
          v-model="queryParams.taskCode"
          placeholder="请输入任务编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="在制品ID" prop="wipId">
        <el-input
          v-model="queryParams.wipId"
          placeholder="请输入在制品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产线ID" prop="lineId">
        <el-select v-model="queryParams.lineId" placeholder="产线id" clearable>
          <el-option v-for="item in lineAll" :key="item.id" :label="item.lineName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="工艺流程ID" prop="routeId">
       <el-select v-model="queryParams.routeId" placeholder="请选择工艺流程ID">
            <el-option
              v-for="dict in ProcessNodeListByRouteId"
              :key="dict.id"
              :label="dict.routeName"
              :value="dict.id"
            ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in statuss"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
      <el-form-item label="总加工时长" prop="totalDuration">
        <el-input
          v-model="queryParams.totalDuration"
          placeholder="请输入总加工时长"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人ID" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作人ID"
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
          v-hasPermi="['wcs-xlPro:ProductionTask:add']"
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
          v-hasPermi="['wcs-xlPro:ProductionTask:edit']"
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
          v-hasPermi="['wcs-xlPro:ProductionTask:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:ProductionTask:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProductionTaskList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="任务id" min-width="100" >
        <template slot-scope="scope">
          <!-- 跳转路径改为ProductionTaskDetail，并携带taskId参数（对应生产任务id） -->
          <router-link 
            :to="`/wcs-xlPro/ProductionTaskDetail?taskId=${scope.row.id}`" 
            class="link-type"
          >
            <span style="color: #409EFF; font-weight: bold; cursor: pointer;">{{ scope.row.id }}</span>
          </router-link>
        </template>
      </el-table-column>
    <!-- <el-table-column label="任务编码" align="center" prop="taskCode"  min-width="120">
    </el-table-column> -->
    <el-table-column label="在制品" align="center" prop="wipId"  min-width="120">
    <template slot-scope="scope">
          <!-- 跳转路径改为ProductionTaskDetail，并携带taskId参数（对应生产任务id） -->
          <router-link 
            :to="`/wcs-xlPro/WipInfo?wipId=${scope.row.wipId}`" 
            class="link-type"
          >
            <span style="color: #409EFF; font-weight: bold; cursor: pointer;">{{ scope.row.wipId }}</span>
          </router-link>
        </template>
    </el-table-column>
    <el-table-column label="产线ID" align="center" prop="lineId"  min-width="120">
      <template slot-scope="scope">
        <!-- 遍历positionAll，匹配id与当前行的positionId -->
        <span v-for="item in lineAll" :key="item.id">
          <span v-if="item.id === scope.row.lineId">{{ item.lineName }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!lineAll.some(item => item.id === scope.row.lineId)">
          无匹配站台
        </span> -->
      </template>
    </el-table-column>
    <el-table-column label="工艺流程ID" align="center" prop="routeId"  min-width="120">
      <template slot-scope="scope">
        <!-- 遍历ProcessNodeListByRouteId，匹配routeId与当前行的routeId -->
        <span v-for="item in ProcessNodeListByRouteId" :key="item.id">
          <span v-if="item.id === scope.row.routeId">{{ item.routeName }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!ProcessNodeListByRouteId.some(item => item.id === scope.row.routeId)">
          无匹配工艺流程
        </span> -->
      </template>
    </el-table-column>
      <el-table-column label="状态" align="center" prop="status"  min-width="120">
        <template slot-scope="scope">
          <div v-for="dict in statuss" :key="dict.value" v-if="dict.value == scope.row.status">
            {{dict.label}}
          </div>
        </template>
      </el-table-column>
    <el-table-column label="开始时间" align="center" prop="startTime"  min-width="120">
    </el-table-column>
    <el-table-column label="结束时间" align="center" prop="endTime"  min-width="120">
    </el-table-column>
    <el-table-column label="总加工时长" align="center" prop="totalDuration"  min-width="120">
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
            v-hasPermi="['wcs-xlPro:ProductionTask:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:ProductionTask:remove']"
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

    <!-- 添加或修改生产任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="任务编码" prop="taskCode">
          <el-input v-model="form.taskCode" placeholder="请输入任务编码" />
        </el-form-item>
        <el-form-item label="在制品ID" prop="wipId">
          <el-input v-model="form.wipId" placeholder="请输入在制品ID" />
        </el-form-item>
        <el-form-item label="产线ID" prop="lineId">
          <el-select v-model="form.lineId" placeholder="产线id" clearable>
            <el-option v-for="item in lineAll" :key="item.id" :label="item.lineName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="工艺流程ID" prop="routeId">
           <el-select v-model="form.routeId" placeholder="请选择工艺流程ID">
            <el-option
              v-for="dict in ProcessNodeListByRouteId"
              :key="dict.id"
              :label="dict.routeName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statuss"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-input v-model="form.startTime" placeholder="请输入开始时间" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-input v-model="form.endTime" placeholder="请输入结束时间" />
        </el-form-item>
        <el-form-item label="总加工时长" prop="totalDuration">
          <el-input v-model="form.totalDuration" placeholder="请输入总加工时长" />
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
import { listProductionTask, getProductionTask, delProductionTask, addProductionTask, updateProductionTask } from "@/api/wcs-xlPro/ProductionTask";
import { getWipInfo } from "@/api/wcs-xlPro/WipInfo";
import { findLineAll } from "@/api/wcs-xlPro/PositionInfoExtend";
import { findProcessNodeByRouteId } from "@/api/wcs-xlPro/ProcessNode";
import request from "@/utils/request";
export default {
  name: "ProductionTask",
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
      // 生产任务表格数据
      ProductionTaskList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskCode: null,
        wipId: null,
        lineId: null,
        routeId: null,
        status: null,
        startTime: null,
        endTime: null,
        totalDuration: null,
        operatorId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      statuss: [
        { value: 0, label: '待开始' },
        { value: 1, label: '进行中' },
        { value: 2, label: '已完成' },
        { value: 3, label: '异常' },
        { value: 4, label: '已取消' },
        { value: 5, label: '未生成任务' },
      ],
      // 在制品信息
      wipInfo: {},
      // 站台扩展信息
      positionExtendInfo: {},
      lineAll: [],
      ProcessNodeListByRouteIdAll: [],
    };
  },
  created() {
    this.getList();
    this.findProcessNodeByRouteId();
    this.getLineAll();
  },
  methods: {
    getLineAll(){
      findLineAll().then(response => {
          if(response.code==200){
            this.lineAll = response.data;
          }
      });
    },
   findProcessNodeByRouteId(){
      findProcessNodeByRouteId().then(response => {
            if(response.code==200){
              this.ProcessNodeListByRouteId = response.data;
              console.log('工艺流程数据:', response.data);
              // 打印每个选项的routeId和数据类型
              response.data.forEach(item => {
                console.log('选项routeId:', item.routeId, '类型:', typeof item.routeId);
              });
            }
        });
    },
    /** 查询生产任务列表 */
    getList() {
      this.loading = true;
      listProductionTask(this.queryParams).then(response => {
          if(response.code==200){
            this.ProductionTaskList = response.rows;
            this.total = response.total;
            this.findProcessNodeByRouteId();
            this.getLineAll();
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
        taskCode: null,
        wipId: null,
        lineId: null,
        routeId: null,
        status: null,
        startTime: null,
        endTime: null,
        totalDuration: null,
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
      this.title = "添加生产任务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProductionTask(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改生产任务";
      });
    },
    /** 处理在制品ID变化 */
    handleWipIdChange() {
      if (!this.form.wipId) {
        this.form.routeId = null;
        this.form.lineId = null;
        return;
      }
      
      // 获取在制品信息
      getWipInfo(this.form.wipId).then(response => {
        if (response.code == 200 && response.data) {
          this.wipInfo = response.data;
          this.form.routeId = response.data.routeId;
          
          // 如果存在当前站台扩展ID，则获取站台扩展信息
          if (response.data.currentPositionExtendId) {
            findLineAll(response.data.currentPositionExtendId).then(posResponse => {
              if (posResponse.code == 200 && posResponse.data) {
                this.positionExtendInfo = posResponse.data;
                this.form.lineId = posResponse.data.lineId;
              } else {
                this.$modal.msgError("获取站台扩展信息失败");
                this.form.lineId = null;
              }
            }).catch(error => {
              console.error("获取站台扩展信息失败:", error);
              this.$modal.msgError("获取站台扩展信息失败");
              this.form.lineId = null;
            });
          } else {
            this.$modal.msgWarning("该在制品没有关联的站台扩展信息");
            this.form.lineId = null;
          }
        } else {
          this.$modal.msgError("获取在制品信息失败");
          this.form.routeId = null;
          this.form.lineId = null;
        }
      }).catch(error => {
        console.error("获取在制品信息失败:", error);
        this.$modal.msgError("获取在制品信息失败");
        this.form.routeId = null;
        this.form.lineId = null;
      });
    },
    
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProductionTask(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProductionTask(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除生产任务编号为"' + ids + '"的数据项？').then(function() {
        return delProductionTask(ids);
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
      this.download('wcs-xlPro/ProductionTask/export', {
        ...this.queryParams
      }, `ProductionTask_${new Date().getTime()}.xlsx`)
    },
  
  }
};
</script>
