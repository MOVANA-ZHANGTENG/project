<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="120px">
      <el-form-item label="在制品编码" prop="wipCode">
        <el-input
          v-model="queryParams.wipCode"
          placeholder="请输入在制品编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品型号ID" prop="modelId">
        <el-select v-model="queryParams.modelId" placeholder="请选择产品型号ID">
            <el-option
              v-for="dict in modelAll"
              :key="dict.modelId"
              :label="dict.modelName"
              :value="dict.modelId"
            ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input
          v-model="queryParams.palletCode"
          placeholder="请输入托盘编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
      <el-form-item label="当前工艺节点ID" prop="currentNodeId">
        <el-select v-model="queryParams.currentNodeId" placeholder="请选择当前工艺节点ID">
            <el-option
              v-for="dict in ProcessNodeListByRouteIdAll"
              :key="dict.id"
              :label="dict.nodeName"
              :value="dict.id"
            ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="当前站台扩展ID" prop="currentPositionExtendId">
       <el-select v-model="queryParams.currentPositionExtendId" placeholder="请选择站台扩展ID">
            <el-option
              v-for="dict in positionExtendIdAll"
              :key="dict.positionInfoExtendsId"
              :label="dict.code"
              :value="dict.positionInfoExtendsId"
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
      <el-form-item label="质检状态" prop="qualityStatus">
        <el-select v-model="queryParams.qualityStatus" placeholder="请选择质检状态" clearable>
          <el-option
            v-for="dict in qualityStatus"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="创建人ID" prop="createUserId">
        <el-input
          v-model="queryParams.createUserId"
          placeholder="请输入创建人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建人姓名" prop="createUserName">
        <el-input
          v-model="queryParams.createUserName"
          placeholder="请输入创建人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="删除标志" prop="isDelete">
        <el-input
          v-model="queryParams.isDelete"
          placeholder="请输入删除标志"
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
          v-hasPermi="['wcs-xlPro:WipInfo:add']"
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
          v-hasPermi="['wcs-xlPro:WipInfo:edit']"
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
          v-hasPermi="['wcs-xlPro:WipInfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:WipInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="WipInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="在制品编码" align="center" prop="wipCode"  min-width="120">
    </el-table-column>
    <el-table-column label="产品型号ID" align="center" prop="modelId"  min-width="120">
      <template slot-scope="scope">
        <!-- 遍历positionAll，匹配id与当前行的positionId -->
        <span v-for="item in modelAll" :key="item.id">
          <span v-if="item.id === scope.row.modelId">{{ item.modelName }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!modelAll.some(item => item.id === scope.row.modelId)">
          无匹配站台
        </span> -->
      </template>
    </el-table-column>
    <el-table-column label="托盘编码" align="center" prop="palletCode"  min-width="120">
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
    <el-table-column label="当前工艺节点ID" align="center" prop="currentNodeId"  min-width="120">
       <template slot-scope="scope">
        <!-- 遍历ProcessNodeListByRouteId，匹配nodeId与当前行的nodeId -->
        <span v-for="item in ProcessNodeListByRouteIdAll" :key="item.id">
          <span v-if="item.id === scope.row.currentNodeId">{{ item.nodeName }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!ProcessNodeListByRouteIdAll.some(item => item.id === scope.row.currentNodeId)">
          无匹配工艺流程
        </span> -->
      </template>
    </el-table-column>
    <el-table-column label="当前站台扩展ID" align="center" prop="currentPositionExtendId"  min-width="120">
       <template slot-scope="scope">
        <!-- 遍历positionExtendIdAll，匹配id与当前行的positionExtendId -->
        <span v-for="item in positionExtendIdAll" :key="item.id">
          <span v-if="item.id === scope.row.currentPositionExtendId">{{ item.name }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <!-- <span v-if="!positionExtendIdAll.some(item => item.id === scope.row.currentPositionExtendId)">
          无匹配站台
        </span> -->
      </template>
    </el-table-column>
      <el-table-column label="状态" align="center" prop="status"  min-width="120">
        <template slot-scope="scope">
          <div v-for="dict in statuss" :key="dict.value">
            <span v-if="scope.row.status === dict.value">{{ dict.label }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="质检状态" align="center" prop="qualityStatus"  min-width="120">
        <template slot-scope="scope">
          <div v-for="dict in qualityStatus" :key="dict.value">
            <span v-if="scope.row.qualityStatus === dict.value">{{ dict.label }}</span>
          </div>
        </template>
      </el-table-column>
    <el-table-column label="创建人ID" align="center" prop="createUserId"  min-width="120">
    </el-table-column>
    <el-table-column label="创建人姓名" align="center" prop="createUserName"  min-width="120">
    </el-table-column>
    <el-table-column label="删除标志" align="center" prop="isDelete"  min-width="120">
      <template slot-scope="scope">
        <div v-for="dict in isDelete" :key="dict.value">
          <span v-if="scope.row.isDelete === dict.value">{{ dict.label }}</span>
        </div>
      </template>
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:WipInfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:WipInfo:remove']"
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

    <!-- 添加或修改在制品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="在制品编码" prop="wipCode">
          <el-input v-model="form.wipCode" placeholder="请输入在制品编码" />
        </el-form-item>
        <el-form-item label="产品型号ID" prop="modelId">
          <el-select v-model="form.modelId" placeholder="请选择产品型号ID">
            <el-option
              v-for="dict in modelAll"
              :key="dict.id"
              :label="dict.modelName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="托盘编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘编码" />
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
        <el-form-item label="当前工艺节点ID" prop="currentNodeId">
          <el-select v-model="form.currentNodeId" placeholder="请选择当前工艺节点ID">
            <el-option
              v-for="dict in ProcessNodeListByRouteIdAll"
              :key="dict.id"
              :label="dict.nodeName"
              :value="parseInt(dict.id)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="当前站台扩展ID" prop="currentPositionExtendId">
           <el-select v-model="form.currentPositionExtendId" placeholder="请选择站台扩展ID">
            <el-option
              v-for="dict in positionExtendIdAll"
              :key="dict.id"
              :label="dict.name"
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
        <el-form-item label="质检状态" prop="qualityStatus">
          <el-radio-group v-model="form.qualityStatus">
            <el-radio
              v-for="dict in qualityStatus"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="创建人ID" prop="createUserId">
          <el-input v-model="form.createUserId" placeholder="请输入创建人ID" />
        </el-form-item>
        <el-form-item label="创建人姓名" prop="createUserName">
          <el-input v-model="form.createUserName" placeholder="请输入创建人姓名" />
        </el-form-item>
        <el-form-item label="删除标志" prop="isDelete">
        <el-select v-model="form.isDelete" placeholder="请选择删除标志">
            <el-option
              v-for="dict in isDelete"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
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
import { listWipInfo, getWipInfo, delWipInfo, addWipInfo, updateWipInfo } from "@/api/wcs-xlPro/WipInfo";
import { findModelAll } from "@/api/wcs-xlPro/ProcessRoute";
import { findProcessNodeByRouteId, listProcessNodeByRouteId } from "@/api/wcs-xlPro/ProcessNode";
import { findPositionAll } from "@/api/wcs-xlPro/PositionInfoExtend";
import { getProcessNodeListByRouteId } from "@/api/wcs-xlPro/ProcessNodePosition";


import request from "@/utils/request";
import { status } from "nprogress";
export default {
  name: "WipInfo",
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
      // 在制品表格数据
      WipInfoList: [],
      modelAll: [],
      ProcessNodeListByRouteId: [],
      ProcessNodeListByRouteIdAll: [],
      positionExtendIdAll: [],
      statuss: [
        { value: 0, label: '待加工' },
        { value: 1, label: '加工中' },
        { value: 2, label: '已完成' },
        { value: 3, label: '异常' },
        { value: 4, label: '暂停' },
        { value: 5, label: '未生成任务' },
        { value: 5, label: '待生成任务' },
      ],
      qualityStatus: [
        { value: 0, label: '未质检' },
        { value: 1, label: '合格' },
        { value: 2, label: '不合格' }
      ],
      isDelete: [
        { value: 0, label: '未删除' },
        { value: 1, label: '已删除' }
      ],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wipCode: null,
        modelId: null,
        palletCode: null,
        routeId: null,
        currentNodeId: null,
        currentPositionExtendId: null,
        status: null,
        qualityStatus: null,
        createUserId: null,
        createUserName: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 工艺节点列表
      currentNodeOptions: [],
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    // 1. 获取URL中携带的wipId（在制品id）
    const wipId = this.$route.query.wipId;
    if (wipId) {
      this.queryParams.id = wipId; // 赋值给查询参数
    }
    this.getList();
    this.getModelAll();
    this.findProcessNodeByRouteId();
    this.getPositionExtendIdAll();
    this.getProcessNodeListByRouteId();
  },
  watch: {
    // 监听routeId变化，动态加载工艺节点
    'form.routeId': {
      handler(newVal) {
        this.getNodeListByRouteId(newVal);
        // 清空当前工艺节点ID，因为所属工艺流程已改变
        console.log('this.form.currentNodeId', this.form.currentNodeId);
        this.form.currentNodeId = null;
      },
      immediate: true
    }
  },
  methods: {
    getProcessNodeListByRouteId() {
      getProcessNodeListByRouteId().then(response => {
          if(response.code==200){
            this.ProcessNodeListByRouteIdAll = response.data;
            console.log('工艺节点数据A:', this.ProcessNodeListByRouteIdAll);
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
    // 根据routeId获取工艺节点列表
    getNodeListByRouteId(routeId) {
      if (routeId) {
        console.log('routeId:', routeId);

        listProcessNodeByRouteId(routeId).then(response => {
          if (response.code == 200) {
            this.currentNodeOptions = response.data || [];
            console.log('工艺节点数据:', this.currentNodeOptions);
          }
        });
      } else {
        this.currentNodeOptions = [];
      }
    },
    //查询所有的产品型号
    getModelAll(){
      findModelAll().then(response => {
          if(response.code==200){
            this.modelAll = response.data;
          }
      });
    },
     getPositionExtendIdAll(){
      findPositionAll().then(response => {
          if(response.code==200){
            this.positionExtendIdAll = response.data;
           
          }

      });
    },
    /** 查询在制品列表 */
    getList() {
      this.loading = true;
      listWipInfo(this.queryParams).then(response => {
          if(response.code==200){
            this.WipInfoList = response.rows;
            this.total = response.total;
            this.getModelAll();
            this.findProcessNodeByRouteId();
            this.getPositionExtendIdAll();
            this.getProcessNodeListByRouteId();
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
        wipCode: null,
        modelId: null,
        palletCode: null,
        routeId: null,
        currentNodeId: null,
        currentPositionExtendId: null,
        status: null,
        qualityStatus: null,
        createTime: null,
        updateTime: null,
        createUserId: null,
        createUserName: null,
        isDelete: null
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
      this.title = "添加在制品";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getWipInfo(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改在制品";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateWipInfo(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addWipInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除在制品编号为"' + ids + '"的数据项？').then(function() {
        return delWipInfo(ids);
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
      this.download('wcs-xlPro/WipInfo/export', {
        ...this.queryParams
      }, `WipInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
