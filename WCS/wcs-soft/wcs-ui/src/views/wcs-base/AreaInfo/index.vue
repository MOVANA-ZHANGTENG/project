<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" placeholder="仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择库区类型" clearable>
          <el-option v-for="item in dict.type.area_type" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="巷道分配" prop="lineAllotType">
        <el-select v-model="queryParams.lineAllotType" placeholder="请选择巷道分配方式" clearable>
          <el-option v-for="item in dict.type.line_allot_type" :key="item.key" :label="item.label"
            :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="删除标志" prop="isDelete">
        <el-select v-model="queryParams.isDelete" placeholder="请选择删除标志" clearable>
          <el-option v-for="item in dict.type.del_flag" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:AreaInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:AreaInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:AreaInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:AreaInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="AreaInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="150">
      </el-table-column>
      <el-table-column label="类型" align="center" prop="type" min-width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.area_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="巷道分配" align="center" prop="lineAllotType" min-width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.line_allot_type" :value="scope.row.lineAllotType" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="创建人ID" align="center" prop="createUserId">

      </el-table-column> -->
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="更新人ID" align="center" prop="updateUserId">

      </el-table-column> -->
      <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="版本号" align="center" prop="version">

      </el-table-column> -->
      <el-table-column label="删除标志" align="center" prop="isDelete" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.isDelete" />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="120" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:AreaInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.isDelete == 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-base:AreaInfo:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.isDelete == 1"
            @click="handleRecover(scope.row)" v-hasPermi="['wcs-base:AreaInfo:recover']">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改库区对话框 -->
    <el-dialog v-dialogDrags  :title="title" 
    :visible.sync="open" width="500px" append-to-body >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="仓库" prop="wareCode">
          <el-select v-model="form.wareCode" placeholder="仓库" clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型 " prop="type">
          <el-select v-model="form.type" placeholder="请选择库区类型">
            <el-option v-for="item in dict.type.area_type" :key="item.value" :label="item.label"
              :value="parseInt(item.value)" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="删除标志" prop="isDelete">
          <el-input v-model="form.isDelete" placeholder="请输入删除标志" />
        </el-form-item> -->
        <el-form-item label="巷道分配" prop="lineAllotType">
          <el-select v-model="form.lineAllotType" placeholder="请选择巷道分配方式">
            <el-option v-for="item in dict.type.line_allot_type" :key="item.value" :label="item.label"
              :value="parseInt(item.value)" />
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
import {
  listAreaInfo,
  getAreaInfo,
  delAreaInfo,
  addAreaInfo,
  updateAreaInfo,
} from "@/api/wcs-base/AreaInfo";
import request from "@/utils/request.js";

export default {
  name: "AreaInfo",
  dicts: ["area_type", "line_allot_type", "del_flag", "disable_state"],
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
      // 库区表格数据
      AreaInfoList: [],
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
        wareCode: null,
        wareName: null,
        type: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: '0',
        lineAllotType: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [{ required: true, message: "编码不能为空", trigger: "blur" }],
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        wareCode: [
          { required: true, message: "仓库不能为空", trigger: "blur" },
        ],
      },

      wareInfos: [],
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
  },
  methods: {
    //获取所有仓库
    getWareInfos() {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/findAll",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          that.wareInfos = response.data;
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    },

    /** 查询库区列表 */
    getList() {
      this.loading = true;
      listAreaInfo(this.queryParams).then((response) => {
        this.AreaInfoList = response.rows;
        this.total = response.total;
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
        wareCode: null,
        wareName: null,
        type: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
        lineAllotType: null,
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
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.getWareInfos();
      this.reset();
      this.open = true;
      this.title = "添加库区";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getWareInfos();
      this.reset();
      const id = row.id || this.ids;
      getAreaInfo(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改库区";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateAreaInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addAreaInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg||"新增失败");
              }
            });
          }
        }
      });
    },
    /** 恢复按钮操作 */
    handleRecover(row) {
      const id = row.id;
      this.$modal.confirm('是否确认恢复ID为"' + id + '"的数据项？').then(function () {
        return getAreaInfo(id)
      }).then((response) => {
        var info = response.data
        if (info == null) {
          this.$modal.msgError("选择数据项有误！")
          return;
        }
        info.isDelete = 0
        updateAreaInfo(info).then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("恢复成功");
          }else{
            this.$modal.msgError(response.msg||"恢复失败")
          }
        })
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除库区编号为"' + ids + '"的数据项？')
        .then(function () {
          return delAreaInfo(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-base/AreaInfo/export",
        {
          ...this.queryParams,
        },
        `AreaInfo_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
