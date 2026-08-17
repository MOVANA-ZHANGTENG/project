<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="120px">
      <el-form-item label="工艺流程编码" prop="routeCode">
        <el-input
          v-model="queryParams.routeCode"
          placeholder="请输入工艺流程编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工艺流程名称" prop="routeName">
        <el-input
          v-model="queryParams.routeName"
          placeholder="请输入工艺流程名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="适用产品型号ID" prop="modelId">
        <el-select v-model="queryParams.modelId" placeholder="请选择产品型号ID">
            <el-option
              v-for="dict in modelAll"
              :key="dict.id"
              :label="dict.modelName"
              :value="dict.id"
            ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="版本号" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入版本号"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- <el-form-item label="创建人ID" prop="createUserId">
        <el-input
          v-model="queryParams.createUserId"
          placeholder="请输入创建人ID"
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
          v-hasPermi="['wcs-xlPro:ProcessRoute:add']"
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
          v-hasPermi="['wcs-xlPro:ProcessRoute:edit']"
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
          v-hasPermi="['wcs-xlPro:ProcessRoute:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:ProcessRoute:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProcessRouteList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" min-width="100" />
    <el-table-column label="工艺流程编码" align="center" prop="routeCode"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.routeCode"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="工艺流程名称" align="center" prop="routeName"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.routeName"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="适用产品型号ID" align="center" prop="modelId"  min-width="120">
     <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.modelId" placeholder="请选择适用产品型号ID">
              <el-option
                v-for="item in modelAll"
                :key="item.id"
                :label="item.modelName"
                :value="item.id">
              </el-option>
            </el-select>
        </template>
    </el-table-column>
    <el-table-column label="版本号" align="center" prop="version"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.version"></el-input>
      </template>
    </el-table-column>
      <el-table-column label="状态" align="center" prop="status"  min-width="120">
        <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.status" placeholder="请选择状态">
              <el-option
                v-for="item in statuss"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
        </template>
      </el-table-column>
    <el-table-column label="描述" align="center" prop="description"  min-width="120">
    </el-table-column>
    <el-table-column label="创建人ID" align="center" prop="createUserId"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:ProcessRoute:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:ProcessRoute:remove']"
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

    <!-- 添加或修改工艺流程对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="工艺流程编码" prop="routeCode">
          <el-input v-model="form.routeCode" placeholder="请输入工艺流程编码" />
        </el-form-item>
        <el-form-item label="工艺流程名称" prop="routeName">
          <el-input v-model="form.routeName" placeholder="请输入工艺流程名称" />
        </el-form-item>
        <el-form-item label="适用产品型号ID" prop="modelId">
          <el-select v-model="form.modelId" placeholder="请选择产品型号ID">
            <el-option
              v-for="dict in modelAll"
              :key="dict.id"
              :label="dict.modelName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="版本号" prop="version">
          <el-input v-model="form.version" placeholder="请输入版本号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
              <el-option
                v-for="item in statuss"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入描述" />
        </el-form-item>
        <!-- <el-form-item label="创建人ID" prop="createUserId">
          <el-input v-model="form.createUserId" placeholder="请输入创建人ID" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProcessRoute, getProcessRoute, delProcessRoute, addProcessRoute, updateProcessRoute,findModelAll } from "@/api/wcs-xlPro/ProcessRoute";
import request from "@/utils/request";
export default {
  name: "ProcessRoute",
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
      // 工艺流程表格数据
      ProcessRouteList: [],
      modelAll: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        routeCode: null,
        routeName: null,
        modelId: null,
        version: null,
        status: null,
        description: null,
        createUserId: null
      },
      // 表单参数
      form: {},
      statuss: [
        { value: 0, label: "启用" },
        { value: 1, label: "禁用" },
      ],
      // 表单校验
      rules: {
        routeCode: [
          { required: true, message: "工艺流程编码不能为空", trigger: "blur" }
        ],
        routeName: [
          { required: true, message: "工艺流程名称不能为空", trigger: "blur" }
        ],
        modelId: [
          { required: true, message: "适用型号ID不能为空", trigger: "blur" }
        ],
        version: [
          { required: true, message: "版本号不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getModelAll();
  },
  methods: {
    update(row) {
      updateProcessRoute(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    //查询所有的产品型号
    getModelAll(){
      findModelAll().then(response => {
          if(response.code==200){
            this.modelAll = response.data;
          }
      });
    },
    /** 查询工艺流程列表 */
    getList() {
      this.loading = true;
      listProcessRoute(this.queryParams).then(response => {
          if(response.code==200){
            this.ProcessRouteList = response.rows;
            this.total = response.total;
            this.getModelAll();
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
        routeCode: null,
        routeName: null,
        modelId: null,
        version: null,
        status: null,
        description: null,
        createTime: null,
        updateTime: null,
        createUserId: null
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
      this.title = "添加工艺流程";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProcessRoute(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改工艺流程";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProcessRoute(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProcessRoute(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除工艺流程编号为"' + ids + '"的数据项？').then(function() {
        return delProcessRoute(ids);
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
      this.download('wcs-xlPro/ProcessRoute/export', {
        ...this.queryParams
      }, `ProcessRoute_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
