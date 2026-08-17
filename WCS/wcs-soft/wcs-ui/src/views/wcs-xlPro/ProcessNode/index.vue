<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
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
      <el-form-item label="节点编码" prop="nodeCode">
        <el-input
          v-model="queryParams.nodeCode"
          placeholder="请输入节点编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="节点名称" prop="nodeName">
        <el-input
          v-model="queryParams.nodeName"
          placeholder="请输入节点名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="顺序号" prop="sequence">
        <el-input
          v-model="queryParams.sequence"
          placeholder="请输入顺序号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="节点描述" prop="description">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入节点描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预计加工时长" prop="estimatedTime">
        <el-input
          v-model="queryParams.estimatedTime"
          placeholder="请输入预计加工时长"
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
          v-hasPermi="['wcs-xlPro:ProcessNode:add']"
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
          v-hasPermi="['wcs-xlPro:ProcessNode:edit']"
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
          v-hasPermi="['wcs-xlPro:ProcessNode:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:ProcessNode:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProcessNodeList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" min-width="100" />
    <el-table-column label="工艺流程ID" align="center" prop="routeId"  min-width="120">
       <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.routeId" placeholder="请选择工艺流程ID">
              <el-option
                v-for="item in ProcessNodeListByRouteId"
                :key="item.id"
                :label="item.routeName"
                :value="item.id">
              </el-option>
            </el-select>
        </template>
    </el-table-column>
    <el-table-column label="节点编码" align="center" prop="nodeCode"  min-width="120">
       <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.nodeCode"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="节点名称" align="center" prop="nodeName"  min-width="120">
       <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.nodeName"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="顺序号" align="center" prop="sequence"  min-width="120">
       <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.sequence"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="节点描述" align="center" prop="description"  min-width="120">
    </el-table-column>
    <el-table-column label="预计加工时长" align="center" prop="estimatedTime"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:ProcessNode:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:ProcessNode:remove']"
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

    <!-- 添加或修改工艺节点对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
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
        <el-form-item label="节点编码" prop="nodeCode">
          <el-input v-model="form.nodeCode" placeholder="请输入节点编码" />
        </el-form-item>
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model="form.nodeName" placeholder="请输入节点名称" />
        </el-form-item>
        <el-form-item label="顺序号" prop="sequence">
          <el-input v-model="form.sequence" placeholder="请输入顺序号" />
        </el-form-item>
        <el-form-item label="节点描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入节点描述" />
        </el-form-item>
        <el-form-item label="预计加工时长" prop="estimatedTime">
          <el-input v-model="form.estimatedTime" placeholder="请输入预计加工时长" />
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
import { listProcessNode, getProcessNode, delProcessNode, addProcessNode, updateProcessNode,findProcessNodeByRouteId } from "@/api/wcs-xlPro/ProcessNode";
import request from "@/utils/request";
export default {
  name: "ProcessNode",
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
      // 工艺节点表格数据
      ProcessNodeList: [],
      ProcessNodeListByRouteId: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        routeId: null,
        nodeCode: null,
        nodeName: null,
        sequence: null,
        description: null,
        estimatedTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        routeId: [
          { required: true, message: "工艺流程ID不能为空", trigger: "change" }
        ],
        nodeCode: [
          { required: true, message: "节点编码不能为空", trigger: "blur" }
        ],
        nodeName: [
          { required: true, message: "节点名称不能为空", trigger: "blur" }
        ],
        sequence: [
          { required: true, message: "顺序号不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.findProcessNodeByRouteId();
  },
  
  watch: {
    'form.routeId': {
      handler(newVal, oldVal) {
        console.log('form.routeId变化:', oldVal, '->', newVal);
        console.log('数据类型:', typeof newVal);
      },
      immediate: true
    }
  },
  
  methods: {
    update(row) {
      updateProcessNode(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
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
    /** 查询工艺节点列表 */
    getList() {
      this.loading = true;
      listProcessNode(this.queryParams).then(response => {
          if(response.code==200){
            this.ProcessNodeList = response.rows;
            this.total = response.total;
            this.findProcessNodeByRouteId();
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
        routeId: null,
        nodeCode: null,
        nodeName: null,
        sequence: null,
        description: null,
        estimatedTime: null,
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
      this.title = "添加工艺节点";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProcessNode(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改工艺节点";
      });
    },
    /** 提交按钮 */
    submitForm() {
      console.log('提交前表单数据:', this.form);
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProcessNode(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProcessNode(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除工艺节点编号为"' + ids + '"的数据项？').then(function() {
        return delProcessNode(ids);
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
      this.download('wcs-xlPro/ProcessNode/export', {
        ...this.queryParams
      }, `ProcessNode_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
