<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="120px">
      <el-form-item label="夹具类型编码" prop="typeCode">
        <el-input
          v-model="queryParams.typeCode"
          placeholder="请输入夹具类型编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="夹具类型名称" prop="typeName">
        <el-input
          v-model="queryParams.typeName"
          placeholder="请输入夹具类型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入描述"
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
      <el-form-item label="创建人ID" prop="createUserId">
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
      </el-form-item>
      <!-- <el-form-item label="删除标志" prop="isDelete">
        <el-radio-group v-model="form.isDelete">
            <el-radio
              v-for="dict in isDeletes"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
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
          v-hasPermi="['wcs-xlPro:FixtureType:add']"
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
          v-hasPermi="['wcs-xlPro:FixtureType:edit']"
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
          v-hasPermi="['wcs-xlPro:FixtureType:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:FixtureType:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="FixtureTypeList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" min-width="100" />
    <el-table-column label="夹具类型编码" align="center" prop="typeCode"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.typeCode"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="夹具类型名称" align="center" prop="typeName"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.typeName"></el-input>
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
    <el-table-column label="创建人姓名" align="center" prop="createUserName"  min-width="120">
    </el-table-column>
    <el-table-column label="删除标志" align="center" prop="isDelete"  min-width="120">
      <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.isDelete" placeholder="请选择删除标志">
              <el-option
                v-for="item in isDeletes"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
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
            v-hasPermi="['wcs-xlPro:FixtureType:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:FixtureType:remove']"
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

    <!-- 添加或修改夹具类型对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="夹具类型编码" prop="typeCode">
          <el-input v-model="form.typeCode" placeholder="请输入夹具类型编码" />
        </el-form-item>
        <el-form-item label="夹具类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入夹具类型名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
         <el-select v-model="form.status" placeholder="请选择状态" clearable>
            <el-option v-for="item in statuss" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="创建人ID" prop="createUserId">
          <el-input v-model="form.createUserId" placeholder="请输入创建人ID" />
        </el-form-item>
        <el-form-item label="创建人姓名" prop="createUserName">
          <el-input v-model="form.createUserName" placeholder="请输入创建人姓名" />
        </el-form-item> -->
        <el-form-item label="删除标志" prop="isDelete">
          <el-select v-model="form.isDelete" placeholder="请选择删除标志" clearable>
           <el-option
              v-for="dict in isDeletes"
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
import { listFixtureType, getFixtureType, delFixtureType, addFixtureType, updateFixtureType } from "@/api/wcs-xlPro/FixtureType";
import request from "@/utils/request";
export default {
  name: "FixtureType",
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
      // 夹具类型表格数据
      FixtureTypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        typeCode: null,
        typeName: null,
        description: null,
        status: null,
        createUserId: null,
        createUserName: null,
        isDelete: null
      },
      statuss: [
        { value: 0, label: "启用" },
        { value: 1, label: "禁用" },
      ],
      isDeletes: [
        { value: 0, label: "正常" },
        { value: 1, label: "删除" },
      ],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        typeCode: [
          { required: true, message: "夹具类型编码不能为空", trigger: "blur" }
        ],
        typeName: [
          { required: true, message: "夹具类型名称不能为空", trigger: "blur" }
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
        isDelete: [
          { required: true, message: "删除标志不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    update(row) {
      updateFixtureType(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    /** 查询夹具类型列表 */
    getList() {
      this.loading = true;
      listFixtureType(this.queryParams).then(response => {
          if(response.code==200){
            this.FixtureTypeList = response.rows;
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
        typeCode: null,
        typeName: null,
        description: null,
        status: null,
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
      this.title = "添加夹具类型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getFixtureType(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改夹具类型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFixtureType(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addFixtureType(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除夹具类型编号为"' + ids + '"的数据项？').then(function() {
        return delFixtureType(ids);
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
      this.download('wcs-xlPro/FixtureType/export', {
        ...this.queryParams
      }, `FixtureType_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
