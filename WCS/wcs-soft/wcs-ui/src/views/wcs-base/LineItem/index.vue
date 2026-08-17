<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="产线" prop="lineCode">
        <el-input
          v-model="queryParams.lineCode"
          placeholder="请输入产线"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物料" prop="itemCode">
        <el-input
          v-model="queryParams.itemCode"
          placeholder="请输入物料"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最低数量" prop="quantity">
        <el-input
          v-model="queryParams.quantity"
          placeholder="请输入最低数量"
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
          v-hasPermi="['wcs-base:LineItem:add']"
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
          v-hasPermi="['wcs-base:LineItem:edit']"
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
          v-hasPermi="['wcs-base:LineItem:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-base:LineItem:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="LineItemList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="物料" align="center" prop="itemName"  min-width="120">
      <!-- <template slot-scope="scope">
        <el-select :disabled="1==1" v-model="scope.row.itemCode" placeholder="">
            <el-option
          
              v-for="item in itemInfos"
              :key="item.itemCode"
              :label="item.itemName+'('+item.itemTypeName+'）'"
              :value="item.itemCode">
            </el-option>
          </el-select>
      </template> -->
     
    </el-table-column> 
    <el-table-column label="产线" align="center" prop="lineName"  min-width="120">
      <template slot-scope="scope">
         <span style="color: #67C23A;" v-if="scope.row.lineCode=='000'">共用产线</span>
         <span v-else>{{ scope.row.lineName }}</span>
      </template>
    </el-table-column> 
    <el-table-column label="最低数量" align="center" prop="quantity"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:LineItem:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:LineItem:remove']"
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

    <!-- 添加或修改产线物料对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="产线" prop="lineCode">
          <el-select :disabled="form.id!=null" v-model="form.lineCode" placeholder="">
            <el-option
              v-for="item in lineInfos"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
            <!-- <el-option
                           style="color: #67C23A;"
                            key="000"
                            label="共用产线"
                            value="000">
                          </el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="物料" prop="itemCode">
          <el-select    :disabled="form.id!=null" v-model="form.itemCode" placeholder="">
            <el-option
          
              v-for="item in itemInfos"
              :key="item.itemCode"
              :label="item.itemName+'('+item.itemTypeName+'）'"
              :value="item.itemCode">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="最低数量" prop="quantity">
          <el-input v-model="form.quantity" placeholder="请输入最低数量" />
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
import { listLineItem, getLineItem, delLineItem, addLineItem, updateLineItem } from "@/api/wcs-base/LineItem";
import { listItemType, getItemType, delItemType, addItemType, updateItemType } from "@/api/wcs-base/ItemType";
import { listProLine, getProLine, delProLine, addProLine, updateProLine } from "@/api/wcs-base/ProLine";
import { listItemInfo, getItemInfo, delItemInfo, addItemInfo, updateItemInfo } from "@/api/wcs-base/ItemInfo";
export default {
  name: "LineItem",
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
      // 产线物料表格数据
      LineItemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        lineCode: null,
        itemCode: null,
        quantity: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      itemInfos:[],
      lineInfos:[],
    };
  },
  created() {
    this.getList();
    this.getItemInfoList();
    this.getLineInfoList();
  },
  methods: {
    getItemInfoList() {
      this.loading = true;
      listItemInfo({ pageNum: 1,
        pageSize: 999,}).then(response => {
          if(response.code==200){
            this.itemInfos = response.rows; 
          } 
      });
    },

    getItemTypeList() {
      this.loading = true;
      listItemType({ pageNum: 1,
        pageSize: 999,}).then(response => {
          if(response.code==200){
            this.itemTypes = response.rows; 
          } 
      });
    },
    getLineInfoList() {
      this.loading = true;
      listProLine({ pageNum: 1,
        pageSize: 999,}).then(response => {
          if(response.code==200){
            this.lineInfos = response.rows; 
          } 
      });
    },
    /** 查询产线物料列表 */
    getList() {
      this.loading = true;
      listLineItem(this.queryParams).then(response => {
          if(response.code==200){
            this.LineItemList = response.rows;
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
        lineCode: null,
        itemCode: null,
        quantity: null
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
      this.title = "添加产线物料";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getLineItem(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改产线物料";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLineItem(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addLineItem(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除产线物料编号为"' + ids + '"的数据项？').then(function() {
        return delLineItem(ids);
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
      this.download('wcs-base/LineItem/export', {
        ...this.queryParams
      }, `LineItem_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
