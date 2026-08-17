<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="站台扩展ID" prop="positionExtendId">
         <el-select v-model="queryParams.positionExtendId" placeholder="请选择站台扩展ID">
            <el-option
              v-for="dict in positionExtendIdAll"
              :key="dict.id"
              :label="dict.code"
              :value="dict.id"
            ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="偏移量编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入偏移量编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="偏移量名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入偏移量名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="通信地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入通信地址"
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
          v-hasPermi="['wcs-xlPro:PositionPlcOffset:add']"
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
          v-hasPermi="['wcs-xlPro:PositionPlcOffset:edit']"
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
          v-hasPermi="['wcs-xlPro:PositionPlcOffset:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:PositionPlcOffset:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="PositionPlcOffsetList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="站台扩展ID" align="center" prop="positionExtendId"  min-width="120">
    <template slot-scope="scope">
        <el-select @change="update(scope.row)" v-model="scope.row.positionExtendId" placeholder="请选择所需站台扩展ID">
          <el-option
            v-for="item in positionExtendIdAll"
            :key="item.id"
            :label="item.code"
            :value="item.id"
          ></el-option>
        </el-select>
      </template>
    </el-table-column>
    <el-table-column label="偏移量编码" align="center" prop="code"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.code"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="偏移量名称" align="center" prop="name"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.name"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="通信地址" align="center" prop="address"  min-width="120">
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.address"></el-input>
      </template>
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:PositionPlcOffset:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:PositionPlcOffset:remove']"
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

    <!-- 添加或修改站台通信偏移量对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="站台扩展ID" prop="positionExtendId">
           <el-select v-model="form.positionExtendId" placeholder="请选择站台扩展ID">
            <el-option
              v-for="dict in positionExtendIdAll"
              :key="dict.id"
              :label="dict.code"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="偏移量编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入偏移量编码" />
        </el-form-item>
        <el-form-item label="偏移量名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入偏移量名称" />
        </el-form-item>
        <el-form-item label="通信地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入通信地址" />
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
import { listPositionPlcOffset, getPositionPlcOffset, delPositionPlcOffset, addPositionPlcOffset, updatePositionPlcOffset,getPositionExtendIdAll } from "@/api/wcs-xlPro/PositionPlcOffset";
import request from "@/utils/request";
import { get } from "jquery";
export default {
  name: "PositionPlcOffset",
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
      // 站台通信偏移量表格数据
      PositionPlcOffsetList: [],
      positionExtendIdAll: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        positionExtendId: null,
        code: null,
        name: null,
        address: null,
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
    this.getPositionExtendIdAll();
  },
  methods: {
    update(row) {
      updatePositionPlcOffset(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    getPositionExtendIdAll(){
      getPositionExtendIdAll().then(response => {
          if(response.code==200){
            this.positionExtendIdAll = response.data;
          }
        
      });
    },
    /** 查询站台通信偏移量列表 */
    getList() {
      this.loading = true;
      listPositionPlcOffset(this.queryParams).then(response => {
          if(response.code==200){
            this.PositionPlcOffsetList = response.rows;
            this.total = response.total;
            this.getPositionExtendIdAll();
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
        positionExtendId: null,
        code: null,
        name: null,
        address: null,
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
      this.title = "添加站台通信偏移量";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionPlcOffset(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改站台通信偏移量";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePositionPlcOffset(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addPositionPlcOffset(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除站台通信偏移量编号为"' + ids + '"的数据项？').then(function() {
        return delPositionPlcOffset(ids);
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
      this.download('wcs-xlPro/PositionPlcOffset/export', {
        ...this.queryParams
      }, `PositionPlcOffset_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
