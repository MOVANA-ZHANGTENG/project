<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" placeholder="请选择仓库" clearable @change="handleWareChange">
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="站台" prop="positionCode">
        <el-select v-model="queryParams.positionCode" placeholder="请选择站台" clearable filterable>
          <el-option v-for="item in positionInfos" :key="item.code" :label="item.name + '(' + item.code + ')'" :value="item.code" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="站台ID" prop="positionId">
        <el-input v-model="queryParams.positionId" placeholder="请输入站台ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="日志类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择日志类型" clearable>
          <el-option v-for="dict in types" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:PositionRecord:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:PositionRecord:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:PositionRecord:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:PositionRecord:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row> -->

    <el-table v-loading="loading" :data="PositionRecordList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="ID" align="center" prop="id" min-width="100" /> -->
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="仓库名称" align="center" prop="wareName" min-width="120">
      </el-table-column> -->
      <el-table-column label="站台编码" align="center" prop="positionCode" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="站台ID" align="center" prop="positionId" min-width="100">
      </el-table-column> -->
      <!-- <el-table-column label="日志类型" align="center" prop="type" min-width="100">
        <template slot-scope="scope">
          <span v-for="dict in types" :key="dict.value" v-if="scope.row.type === dict.value">{{ dict.label }}</span>
        </template>
      </el-table-column> -->
      <el-table-column label="内容" align="center" prop="content" min-width="300">
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="180">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:PositionRecord:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:PositionRecord:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改站台日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="站台ID" prop="positionId">
          <el-input v-model="form.positionId" placeholder="请输入站台ID" />
        </el-form-item>
        <el-form-item label="日志类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择日志类型">
            <el-option v-for="dict in types" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" placeholder="请输入内容" />
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
import { listPositionRecord, getPositionRecord, delPositionRecord, addPositionRecord, updatePositionRecord } from "@/api/wcs-base/PositionRecord";
import { listWareInfo } from "@/api/wcs-base/WareInfo";
import { listPositionInfo } from "@/api/wcs-base/PositionInfo";
import request from "@/utils/request";
export default {
  name: "PositionRecord",
  data() {
    return {
      // 仓库列表
      wareInfos: [],
      // 站台列表
      positionInfos: [],
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
      // 站台日志表格数据
      PositionRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wareCode: null,
        positionCode: null,
        positionId: null,
        content: null,
        type: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      types: [
        { value: 0, label: "INFO" }
        , { value: 1, label: "ERROR" }
      ]
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
  },
  methods: {
    /** 获取仓库列表 */
    getWareInfos() {
      var query = { 
        isDelete: 0,
        pageNum: 1,
        pageSize: 9999  // 获取所有仓库，不分页
      }
      listWareInfo(query).then((response) => {
        if (response.code == 200) {
          this.wareInfos = response.rows;
        }
      });
    },
    /** 获取站台列表 */
    getPositionInfos(wareCode) {
      var query = { 
        wareCode: wareCode,
        pageNum: 1,
        pageSize: 9999  // 获取所有站台，不分页
      }
      listPositionInfo(query).then((response) => {
        if (response.code == 200) {
          this.positionInfos = response.rows;
        }
      });
    },
    /** 仓库变更事件 */
    handleWareChange(val) {
      this.queryParams.positionCode = null;
      this.positionInfos = [];
      if (val) {
        this.getPositionInfos(val);
      }
    },
    /** 查询站台日志列表 */
    getList() {
      this.loading = true;
      listPositionRecord(this.queryParams).then(response => {
        if (response.code == 200) {
          this.PositionRecordList = response.rows;
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
        positionId: null,
        content: null,
        createTime: null,
        type: null
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加站台日志";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionRecord(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改站台日志";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePositionRecord(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addPositionRecord(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "新增失败");
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除站台日志编号为"' + ids + '"的数据项？').then(function () {
        return delPositionRecord(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg || "删除失败");
        }

      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/PositionRecord/export', {
        ...this.queryParams
      }, `PositionRecord_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
