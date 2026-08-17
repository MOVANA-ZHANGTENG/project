<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
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
      <el-form-item label="所需夹具类型ID" prop="fixtureTypeId">
        <el-select v-model="queryParams.fixtureTypeId" placeholder="请选择所需夹具类型ID">
          <el-option
            v-for="dict in fixtureTypeList"
            :key="dict.id"
            :label="dict.typeName"
            :value="dict.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-input
          v-model="queryParams.priority"
          placeholder="请输入优先级"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否启用" prop="isEnabled">
        <el-select v-model="queryParams.isEnabled" placeholder="请选择是否启用">
          <el-option
            v-for="dict in isEnabledList"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          ></el-option>
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
          v-hasPermi="['wcs-xlPro:ProcessNodePosition:add']"
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
          v-hasPermi="['wcs-xlPro:ProcessNodePosition:edit']"
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
          v-hasPermi="['wcs-xlPro:ProcessNodePosition:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:ProcessNodePosition:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProcessNodePositionList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="工艺节点ID" align="center" prop="nodeId"  min-width="120">
        <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.nodeId" placeholder="请选择工艺流程ID">
              <el-option
                v-for="item in ProcessNodeListByRouteId"
                :key="item.id"
                :label="item.nodeName"
                :value="item.id">
              </el-option>
            </el-select>
        </template>
    </el-table-column>
    <el-table-column label="站台ID" align="center" prop="positionId"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.positionId" placeholder="请选择站台ID">
          <el-option
            v-for="item in positionList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </template>
    </el-table-column>
    <el-table-column label="所需夹具类型ID" align="center" prop="fixtureTypeId"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.fixtureTypeId" placeholder="请选择所需夹具类型ID">
          <el-option
            v-for="item in fixtureTypeList"
            :key="item.id"
            :label="item.typeName"
            :value="item.id"
          ></el-option>
        </el-select>
      </template>
    </el-table-column>
    <el-table-column label="优先级" align="center" prop="priority"  min-width="120">
       <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.priority"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="是否启用" align="center" prop="isEnabled"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.isEnabled" placeholder="请选择是否启用">
          <el-option
            v-for="dict in isEnabledList"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          ></el-option>
        </el-select>
      </template>
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:ProcessNodePosition:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:ProcessNodePosition:remove']"
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

    <!-- 添加或修改工艺节点站台关联对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
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
        <el-form-item label="所需夹具类型ID" prop="fixtureTypeId">
          <el-select v-model="form.fixtureTypeId" placeholder="请选择所需夹具类型ID">
            <el-option
              v-for="dict in fixtureTypeList"
              :key="dict.id"
              :label="dict.typeName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input v-model="form.priority" placeholder="请输入优先级" />
        </el-form-item>
        <el-form-item label="是否启用" prop="isEnabled">
          <el-select v-model="form.isEnabled" placeholder="请选择是否启用">
            <el-option
              v-for="dict in isEnabledList"
              :key="dict.id"
              :label="dict.name"
              :value="dict.id"
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
import { listProcessNodePosition, getProcessNodePosition, delProcessNodePosition, addProcessNodePosition, updateProcessNodePosition,getProcessNodeListByRouteId,getPositionList,getFixtureTypeList } from "@/api/wcs-xlPro/ProcessNodePosition";
import request from "@/utils/request";
export default {
  name: "ProcessNodePosition",
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
      // 工艺节点站台关联表格数据
      ProcessNodePositionList: [],
      ProcessNodeListByRouteId: [],
      positionList: [],
      fixtureTypeList: [],
      isEnabledList: [
        { id: 1, name: '启用' },
        { id: 0, name: '禁用' }
      ],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nodeId: null,
        positionId: null,
        fixtureTypeId: null,
        priority: null,
        isEnabled: null,
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
    this.getProcessNodeListByRouteId();
    this.getPositionList();
    this.getFixtureTypeList();
  },
  methods: {
    update(row) {
      updateProcessNodePosition(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
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
  
    getPositionList() {
      getPositionList().then(response => {
          if(response.code==200){
            this.positionList = response.data;
          }
      });
    },
    getProcessNodeListByRouteId() {
      getProcessNodeListByRouteId().then(response => {
          if(response.code==200){
            this.ProcessNodeListByRouteId = response.data;
          }
      });
    },
    /** 查询工艺节点站台关联列表 */
    getList() {
      this.loading = true;
      listProcessNodePosition(this.queryParams).then(response => {
          if(response.code==200){
            this.ProcessNodePositionList = response.rows;
            this.total = response.total;
            this.getProcessNodeListByRouteId();
            this.getPositionList();
            this.getFixtureTypeList();
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
        nodeId: null,
        positionId: null,
        fixtureTypeId: null,
        priority: null,
        isEnabled: null,
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
      this.title = "添加工艺节点站台关联";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProcessNodePosition(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改工艺节点站台关联";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProcessNodePosition(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProcessNodePosition(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除工艺节点站台关联编号为"' + ids + '"的数据项？').then(function() {
        return delProcessNodePosition(ids);
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
      this.download('wcs-xlPro/ProcessNodePosition/export', {
        ...this.queryParams
      }, `ProcessNodePosition_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
