<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="托盘编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入托盘编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="托盘名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入托盘名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="当前位置" prop="cellCode">
        <el-input v-model="queryParams.cellCode" placeholder="请输入当前位置" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型编码" prop="typeCode">
        <el-input v-model="queryParams.typeCode" placeholder="请输入类型编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型名称" prop="typeName">
        <el-input v-model="queryParams.typeName" placeholder="请输入类型名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="父级托盘" prop="parentCode">
        <el-input v-model="queryParams.parentCode" placeholder="请输入父级托盘" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="是否存货" prop="isEmpty">
        <el-select v-model="queryParams.isEmpty" placeholder="请选择是否存货" clearable>
          <el-option v-for="dict in dict.type.is_empty" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="托盘状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择托盘状态" clearable>
          <el-option v-for="dict in dict.type.pallet_state_ds" :key="dict.value" :label="dict.label"
            :value="dict.value" />
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
          v-hasPermi="['wcs-base:palletInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:palletInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:palletInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning " plain icon="el-icon-upload2" size="mini" @click="handleImport">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain  icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:palletInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="palletInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" min-width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="托盘编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="托盘名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="仓库" align="center" prop="wareName" min-width="120">
      </el-table-column>
      <el-table-column label="当前位置" align="center" prop="cellCode" min-width="120">
      </el-table-column>
      <el-table-column label="类型编码" align="center" prop="typeCode" min-width="120">
      </el-table-column>
      <el-table-column label="类型名称" align="center" prop="typeName" min-width="150">
      </el-table-column>
      <el-table-column label="父级托盘" align="center" prop="parentCode" min-width="120">
      </el-table-column>
      <el-table-column label="是否存货" align="center" prop="isEmpty" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_empty" :value="scope.row.isEmpty" />
        </template>
      </el-table-column>
      <el-table-column label="托盘状态" align="center" prop="state" min-width="180">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pallet_state_ds" :value="scope.row.state" />
        </template>
      </el-table-column>
      <el-table-column label="当前重量/kg" align="center" prop="realWeight" min-width="120">
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="120" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:palletInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:palletInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改托盘信息对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="托盘编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入托盘编码" />
        </el-form-item>
        <el-form-item label="托盘名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入托盘名称" />
        </el-form-item>
        <el-form-item label="当前位置" prop="cellCode">
          <el-input v-model="form.cellCode" placeholder="请输入当前位置" />
        </el-form-item>
        <el-form-item label="托盘类型" prop="typeCode">
          <el-select v-model="form.typeCode" placeholder="请输入托盘类型" @change="paddingTypeName()" clearable>
            <el-option v-for="item in palletTypes" :key="item.code" :label="item.name" :value="item.code + ''" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级托盘" prop="parentCode">
          <el-input v-model="form.parentCode" placeholder="请输入父级托盘" />
        </el-form-item>
        <el-form-item label="当前重量" prop="realWeight">
          <el-input v-model="form.realWeight" placeholder="请输入当前重量" />
        </el-form-item>
        <el-form-item label="是否存货" prop="isEmpty" v-if="form.id!=null">
          <el-select v-model="form.isEmpty" placeholder="请选择是否存货" clearable>
            <el-option v-for="dict in dict.type.is_empty" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="托盘状态" prop="state" v-if="form.id!=null">
          <el-select v-model="form.state" placeholder="请选择托盘状态" clearable>
            <el-option v-for="dict in dict.type.pallet_state_ds" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px">
      <el-upload ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading"
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
import { listPalletInfo, getPalletInfo, delPalletInfo, addPalletInfo, updatePalletInfo } from "@/api/wcs-base/palletInfo";
import { listPalletType, getPalletType, delPalletType, addPalletType, updatePalletType } from "@/api/wcs-base/palletType";
import { getToken } from "@/utils/auth";

export default {
  name: "PalletInfo",
  dicts: ["is_empty", "pallet_state_ds"],
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
      // 托盘信息表格数据
      palletInfoList: [],
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
        cellCode: null,
        typeCode: null,
        typeName: null,
        isEmpty: null,
        parentCode: null,
        state: null
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/wcs-base/palletInfo/importData"
      },

      palletTypes: [],

      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "托盘编码不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "托盘名称不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getPalletTypes()
  },
  methods: {
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "托盘信息导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    downloadTemplate() {
      this.download('/wcs-base/palletInfo/importTemplate', {}, `palletTemplate_${new Date().getTime()}.xlsx`)
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


    //填入托盘类型名称
    paddingTypeName() {
      this.palletTypes.forEach(element => {
        if (element.code == this.form.typeCode) {
          this.form.typeName = element.name
          console.log(this.form)
        }
      });
    },

    /**获取所有的托盘类型 */
    getPalletTypes() {
      listPalletType(this.queryParams).then(response => {
        this.palletTypes = response.rows;
      });
    },
    /** 查询托盘信息列表 */
    getList() {
      this.loading = true;
      listPalletInfo(this.queryParams).then(response => {
        this.palletInfoList = response.rows;
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
        cellCode: null,
        typeCode: null,
        typeName: null,
        isEmpty: null,
        parentCode: null,
        state: null
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
      this.getPalletTypes()
      this.reset();
      this.open = true;
      this.title = "添加托盘信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getPalletTypes()
      this.reset();
      const id = row.id || this.ids
      getPalletInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改托盘信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePalletInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addPalletInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除托盘信息编号为"' + ids + '"的数据项？').then(function () {
        return delPalletInfo(ids);
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
      this.download('wcs-base/palletInfo/export', {
        ...this.queryParams
      }, `palletInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
