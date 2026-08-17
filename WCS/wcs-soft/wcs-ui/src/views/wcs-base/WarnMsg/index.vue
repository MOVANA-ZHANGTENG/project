<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left"
      label-width="100px">
      <el-form-item label="报警编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入报警编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择设备类型" clearable>
          <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="报警内容" prop="warnMsg">
        <el-input v-model="queryParams.warnMsg" placeholder="请输入报警内容" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:WarnMsg:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" :disabled="single" @click="handleAdd2"
          v-hasPermi="['wcs-base:WarnMsg:add']">复制新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:WarnMsg:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:WarnMsg:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload" size="mini" @click="handleImport"
          v-hasPermi="['wcs-base:WarnMsg:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:WarnMsg:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="WarnMsgList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="报警编码" align="center" prop="code" min-width="60">
      </el-table-column>
      <el-table-column label="设备类型" align="center" prop="type" min-width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="报警内容" align="center" prop="warnMsg" min-width="300">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:WarnMsg:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:WarnMsg:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改报警代码对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="报警编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入报警编码" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型">
            <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报警内容" prop="warnMsg">
          <el-input v-model="form.warnMsg" placeholder="请输入报警内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px">

      <el-form>
        <!-- 新增设备类型选择 -->
        <el-form-item label="设备类型：">
          <el-select v-model="upload.type" placeholder="请选择设备类型">
            <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <el-upload ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport+'&deviceType='+upload.type" :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" slot="tip">
          <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
          <el-link type="info" style="font-size:12px" @click="downloadTemplate">下载模板</el-link>
        </div>
        <div class="el-upload__tip" style="color:red" slot="tip">提示：仅允许导入“xls”或“xlsx”格式文件！</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listWarnMsg, getWarnMsg, delWarnMsg, addWarnMsg, updateWarnMsg } from "@/api/wcs-base/WarnMsg";
import { getToken } from "@/utils/auth";
import request from "@/utils/request";
export default {
  name: "WarnMsg",
  dicts: [
    "device_type",
  ],
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
      // 报警代码表格数据
      WarnMsgList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: null,
        type: null,
        warnMsg: null
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 新增设备类型
        type: null,
        // 是否更新已经存在的用户数据
        updateSupport: true,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/wcs-base/WarnMsg/importData"
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
  },
  methods: {
    /** 查询报警代码列表 */
    getList() {
      this.loading = true;
      listWarnMsg(this.queryParams).then(response => {
        if (response.code == 200) {
          this.WarnMsgList = response.rows;
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
        type: null,
        warnMsg: null
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
      this.title = "添加报警代码";
    },
    /** 新增按钮操作 */
    handleAdd2(row) {
      this.reset();
      const id = row.id || this.ids;
      getWarnMsg(id).then((response) => {
        this.form = response.data;
        this.form.isAdd = true;
        this.open = true;
        this.title = "添加设备";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getWarnMsg(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }

        this.form.isAdd = false;
        this.open = true;
        this.title = "修改报警代码";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null && this.form.isAdd) {
            addWarnMsg(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "新增失败");
              }
            });
          }
          else if (this.form.id != null && !this.form.isAdd) {
            updateWarnMsg(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          }
          else {
            addWarnMsg(this.form).then((response) => {
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
      this.$modal.confirm('是否确认删除报警代码编号为"' + ids + '"的数据项？').then(function () {
        return delWarnMsg(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg || "删除失败");
        }

      }).catch(() => { });
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "报警代码信息导入";
      this.upload.open = true;
    },

    /** 下载模板操作 */
    downloadTemplate() {
      this.download('/wcs-base/WarnMsg/importTemplate', {}, `报警代码模板_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/WarnMsg/export', {
        ...this.queryParams
      }, `报警代码_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
