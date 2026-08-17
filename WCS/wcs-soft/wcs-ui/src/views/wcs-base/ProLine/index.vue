<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入产品"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工艺路线" prop="proRouteId">
        <el-input
          v-model="queryParams.proRouteId"
          placeholder="请输入工艺路线"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库" prop="wareId">
        <el-select  v-model="queryParams.wareCode" placeholder="">
            <el-option
              v-for="item in wareInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
      </el-form-item>
      
      <el-form-item label="状态" prop="state">
        <el-input
          v-model="queryParams.state"
          placeholder="请输入状态"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="版本号" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入版本号"
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
          v-hasPermi="['wcs-base:ProLine:add']"
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
          v-hasPermi="['wcs-base:ProLine:edit']"
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
          v-hasPermi="['wcs-base:ProLine:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-base:ProLine:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ProLineList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
    <el-table-column label="编码" align="center" prop="code"  min-width="120">
    </el-table-column>
    <el-table-column label="产品" align="center" prop="name"  min-width="120">
      <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.productCode" placeholder="">
            <el-option
              v-for="item in productInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
      </template>
    </el-table-column>
    <!-- <el-table-column label="产品" align="center" prop="productId"  min-width="120">
    </el-table-column> -->
    <el-table-column label="物流路线" align="center" prop="wareId"  min-width="120">
      <template  slot-scope="scope">
        <el-select  v-model="scope.row.proRouteId" placeholder="">
          <el-option
            v-for="item in ProRoutes"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
      </el-select>
      </template>
     
    </el-table-column>
    <el-table-column label="仓库" align="center" prop="wareId"  min-width="120">
      <template  slot-scope="scope">
        <el-select  v-model="scope.row.wareCode" placeholder="">
          <el-option
            v-for="item in wareInfos"
            :key="item.code"
            :label="item.name"
            :value="item.code">
          </el-option>
      </el-select>
      </template>
     
    </el-table-column>
    <el-table-column label="创建人" align="center" prop="createUserId"  min-width="120">
    </el-table-column>
    <el-table-column label="创建人" align="center" prop="createUserName"  min-width="120">
    </el-table-column>
    <el-table-column label="更新人" align="center" prop="uodateUserId"  min-width="120">
    </el-table-column>
    <el-table-column label="更新人" align="center" prop="updateUserName"  min-width="120">
    </el-table-column>
    <el-table-column label="状态" align="center" prop="state"  min-width="120">
    </el-table-column>
    <el-table-column label="版本号" align="center" prop="version"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:ProLine:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:ProLine:remove']"
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

    <!-- 添加或修改产线对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="产品" prop="productCode">
          <el-select  v-model="form.productCode" placeholder="">
            <el-option
              v-for="item in productInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="工艺路线" prop="proRouteId">
          <el-input v-model="form.proRouteId" placeholder="请输入工艺路线" />
        </el-form-item> -->
        <el-form-item label="物流路线" prop="wareId">
         
         <el-select  v-model="form.proRouteId" placeholder="">
           <el-option
             v-for="item in ProRoutes"
             :key="item.id"
             :label="item.name"
             :value="item.id">
           </el-option>
         </el-select>
       </el-form-item>
        <el-form-item label="仓库" prop="wareId">
         
          <el-select  v-model="form.wareCode" placeholder="">
            <el-option
              v-for="item in wareInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code">
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
import { listProductInfo, getProductInfo, delProductInfo, addProductInfo, updateProductInfo } from "@/api/wcs-base/ProductInfo";
import { listProLine, getProLine, delProLine, addProLine, updateProLine } from "@/api/wcs-base/ProLine";
import request from "@/utils/request";
export default {
  name: "ProLine",
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
      // 产线表格数据
      ProLineList: [],
      productInfos:[],
      wareInfos:[],
      ProRoutes:[],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: null,
        name: null,
        productId: null,
        proRouteId: null,
        wareId: null,
        createUserId: null,
        createUserName: null,
        uodateUserId: null,
        updateUserName: null,
        state: null,
        version: null
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
    this.getProductList();
    this.getWareInfos();
    this.getProRoutes();
  },
  methods: {
    /** 查询产线列表 */
    getList() {
      this.loading = true;
      listProLine(this.queryParams).then(response => {
          if(response.code==200){
            this.ProLineList = response.rows;
            this.total = response.total;
          }
        this.loading = false;
      });
    },

      //获取所有仓库信息列表
    getWareInfos() {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/list",
        method: "get",
        params: { isDelete: 0 },
      }).then((response) => {
        if (response.code == 200) {
          that.wareInfos = response.rows;
        
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },

    
      //获取所有仓库信息列表
      getProRoutes() {
      var that = this;
      request({
        url: "/wcs-base/ProRoute/list",
        method: "get",
        params: { isDelete: 0 },
      }).then((response) => {
        if (response.code == 200) {
          that.ProRoutes = response.rows;
        
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },

      /** 查询产线列表 */
    getProductList() {
      this.loading = true;
      listProductInfo(this.queryParams).then(response => {
          if(response.code==200){
            this.productInfos = response.rows;
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
        code: null,
        name: null,
        productId: null,
        proRouteId: null,
        wareId: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        uodateUserId: null,
        updateUserName: null,
        state: null,
        version: null
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
      this.title = "添加产线";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getProLine(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改产线";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProLine(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addProLine(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除产线编号为"' + ids + '"的数据项？').then(function() {
        return delProLine(ids);
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
      this.download('wcs-base/ProLine/export', {
        ...this.queryParams
      }, `ProLine_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
