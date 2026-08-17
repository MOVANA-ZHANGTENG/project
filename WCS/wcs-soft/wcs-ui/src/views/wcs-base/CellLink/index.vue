<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="起始库位ID" prop="fromCellId">
        <el-input
          v-model="queryParams.fromCellId"
          placeholder="请输入起始库位ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标库位ID" prop="toCellId">
        <el-input
          v-model="queryParams.toCellId"
          placeholder="请输入目标库位ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="节点间距离，默认1.0" prop="distance">
        <el-input
          v-model="queryParams.distance"
          placeholder="请输入节点间距离，默认1.0"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否阻塞，0-正常通行，1-阻塞不可通行" prop="isBlocked">
        <el-input
          v-model="queryParams.isBlocked"
          placeholder="请输入是否阻塞，0-正常通行，1-阻塞不可通行"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库信息，可存储仓库编号、区域标识等" prop="wareInfo">
        <el-input
          v-model="queryParams.wareInfo"
          placeholder="请输入仓库信息，可存储仓库编号、区域标识等"
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
          v-hasPermi="['wcs-base:CellLink:add']"
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
          v-hasPermi="['wcs-base:CellLink:edit']"
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
          v-hasPermi="['wcs-base:CellLink:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-base:CellLink:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="CellLinkList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID，自增长" align="center" prop="id" min-width="100" />
    <el-table-column label="起始库位ID" align="center" prop="fromCellId"  min-width="120">
    </el-table-column>
    <el-table-column label="目标库位ID" align="center" prop="toCellId"  min-width="120">
    </el-table-column>
    <el-table-column label="节点间距离，默认1.0" align="center" prop="distance"  min-width="120">
    </el-table-column>
    <el-table-column label="是否阻塞，0-正常通行，1-阻塞不可通行" align="center" prop="isBlocked"  min-width="120">
    </el-table-column>
    <el-table-column label="仓库信息，可存储仓库编号、区域标识等" align="center" prop="wareInfo"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:CellLink:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:CellLink:remove']"
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

    <!-- 添加或修改库位邻接关系，存储四向车调度系统的节点联通关系对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="起始库位ID" prop="fromCellId">
          <el-input v-model="form.fromCellId" placeholder="请输入起始库位ID" />
        </el-form-item>
        <el-form-item label="目标库位ID" prop="toCellId">
          <el-input v-model="form.toCellId" placeholder="请输入目标库位ID" />
        </el-form-item>
        <el-form-item label="节点间距离，默认1.0" prop="distance">
          <el-input v-model="form.distance" placeholder="请输入节点间距离，默认1.0" />
        </el-form-item>
        <el-form-item label="是否阻塞，0-正常通行，1-阻塞不可通行" prop="isBlocked">
          <el-input v-model="form.isBlocked" placeholder="请输入是否阻塞，0-正常通行，1-阻塞不可通行" />
        </el-form-item>
        <el-form-item label="仓库信息，可存储仓库编号、区域标识等" prop="wareInfo">
          <el-input v-model="form.wareInfo" placeholder="请输入仓库信息，可存储仓库编号、区域标识等" />
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
import { listCellLink, getCellLink, delCellLink, addCellLink, updateCellLink } from "@/api/wcs-base/CellLink";
import request from "@/utils/request";
export default {
  name: "CellLink",
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
      // 库位邻接关系，存储四向车调度系统的节点联通关系表格数据
      CellLinkList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fromCellId: null,
        toCellId: null,
        distance: null,
        isBlocked: null,
        wareInfo: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fromCellId: [
          { required: true, message: "起始库位ID不能为空", trigger: "blur" }
        ],
        toCellId: [
          { required: true, message: "目标库位ID不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "记录创建时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询库位邻接关系，存储四向车调度系统的节点联通关系列表 */
    getList() {
      this.loading = true;
      listCellLink(this.queryParams).then(response => {
          if(response.code==200){
            this.CellLinkList = response.rows;
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
        fromCellId: null,
        toCellId: null,
        distance: null,
        isBlocked: null,
        wareInfo: null,
        createTime: null
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
      this.title = "添加库位邻接关系，存储四向车调度系统的节点联通关系";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCellLink(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改库位邻接关系，存储四向车调度系统的节点联通关系";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCellLink(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addCellLink(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除库位邻接关系，存储四向车调度系统的节点联通关系编号为"' + ids + '"的数据项？').then(function() {
        return delCellLink(ids);
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
      this.download('wcs-base/CellLink/export', {
        ...this.queryParams
      }, `CellLink_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
