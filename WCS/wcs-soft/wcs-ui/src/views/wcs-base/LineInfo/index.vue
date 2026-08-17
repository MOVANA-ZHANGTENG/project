<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input v-model="queryParams.type" placeholder="请输入类型" clearable @keyup.enter.native="handleQuery">
        </el-input>
      </el-form-item>
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode"
          @change="queryParams.areaCode = null; getAreaInfos(queryParams.wareCode)" placeholder="仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>

      <el-form-item label="库区" prop="areaCode">
        <el-select v-model="queryParams.areaCode" placeholder="库区" clearable>
          <el-option v-for="item in areaInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备" prop="deviceCode">
        <el-select v-model="queryParams.deviceCode" placeholder="设备" clearable>
          <el-option v-for="item in deviceInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>

      <el-form-item label="库存状态" prop="invenState">
        <el-select v-model="queryParams.invenState" placeholder="请选择库存状态" clearable>
          <el-option v-for="item in dict.type.inven_state" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态" prop="taskState">
        <el-select v-model="queryParams.taskState" placeholder="请选择任务状态" clearable>
          <el-option v-for="item in dict.type.is_task" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="禁用状态" prop="disableState">
        <el-select v-model="queryParams.disableState" placeholder="请选择禁用状态" clearable>
          <el-option v-for="item in dict.type.disable_state" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item> -->

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
          v-hasPermi="['wcs-base:LineInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:LineInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:LineInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:LineInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="LineInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="类型" align="center" prop="type" min-width="100">
      </el-table-column>
      <el-table-column label="宽度" align="center" prop="maxY" min-width="100">
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="150">
      </el-table-column>
      <el-table-column label="库区编码" align="center" prop="areaCode" min-width="120">
      </el-table-column>
      <el-table-column label="库区名称" align="center" prop="areaName" min-width="150">
      </el-table-column>
      <el-table-column label="设备" align="center" prop="deviceCode" min-width="100">
        <template slot-scope="scope">
          <el-select v-model="scope.row.deviceCode" placeholder="设备" :disabled="true">
            <el-option v-for="item in deviceInfos" :key="item.code" :label="item.name" :value="item.code + ''" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="库存状态" align="center" prop="invenState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.inven_state" :value="scope.row.invenState" />
        </template>
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="taskState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_task" :value="scope.row.taskState" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="禁用状态" align="center" prop="disableState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.disable_state" :value="scope.row.disableState" />
        </template>
      </el-table-column> -->
      <el-table-column label="删除标志" align="center" prop="isDelete" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.isDelete" />
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:LineInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.isDelete == 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-base:LineInfo:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.isDelete == 1"
            @click="handleRecover(scope.row)" v-hasPermi="['wcs-base:LineInfo:recover']">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改巷道对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="宽度" prop="maxY">
          <el-input v-model="form.maxY" placeholder="请输入宽度" />
        </el-form-item>
        <el-form-item label="仓库" prop="wareCode">
          <el-select v-model="form.wareCode" @change="form.areaCode = null; getAreaInfos(form.wareCode)"
            placeholder="仓库" clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code + ''" />
          </el-select>
        </el-form-item>
        <el-form-item label="库区" prop="areaCode">
          <el-select v-model="form.areaCode" placeholder="库区" clearable>
            <el-option v-for="item in areaInfos" :key="item.code" :label="item.name" :value="item.code + ''" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备" prop="deviceCode">
          <el-select v-model="form.deviceCode" placeholder="设备" clearable>
            <el-option v-for="item in deviceInfos" :key="item.code" :label="item.name" :value="item.code + ''" />
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
  listLineInfo,
  getLineInfo,
  delLineInfo,
  addLineInfo,
  updateLineInfo,
} from "@/api/wcs-base/LineInfo";
import {
  listAreaInfo,
  getAreaInfo,
  delAreaInfo,
  addAreaInfo,
  updateAreaInfo,
} from "@/api/wcs-base/AreaInfo";
import {
  listDeviceInfo,
  getDeviceInfo,
  delDeviceInfo,
  addDeviceInfo,
  updateDeviceInfo,
} from "@/api/wcs-base/DeviceInfo";
import request from "@/utils/request.js";

export default {
  name: "LineInfo",
  dicts: ["del_flag", "disable_state", "inven_state", "is_task"],
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
      // 巷道表格数据
      LineInfoList: [],
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
        type: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        invenState: null,
        taskState: null,
        disableState: null,
        isDelete: '0',
        version: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
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
        areaCode: [
          { required: true, message: "库区不能为空", trigger: "blur" },
        ],
      },
      wareInfos: [],
      areaInfos: [],
      deviceInfos: [],
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
    this.getDeviceInfos();
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
     //获取所有仓库
     getDeviceInfos() {
      var query = { isDelete: 0 }
      listDeviceInfo(query).then((response) => {
        if (response.code == 200) {
          this.deviceInfos = response.rows;
        }
      });
    },
    //获取所有库区
    getAreaInfos(wareCode) {
      if (wareCode == null || wareCode == '') {
        this.areaInfos = []
        return
      }
      this.areaInfos = [];
      if (wareCode != null) {
        listAreaInfo({ wareCode: wareCode }).then((response) => {
          if (response.code == 200) {
            this.areaInfos = response.rows;
            if (this.form.id != null && this.open == false) {
              this.open = true;
              this.title = "修改巷道";
            }
          }
        });
      }
    },
    /** 查询巷道列表 */
    getList() {
      this.loading = true;
      listLineInfo(this.queryParams).then((response) => {
        this.LineInfoList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.areaInfos = []
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        code: null,
        name: null,
        type: null,
        wareCode: null,
        wareName: null,
        areaCode: null,
        areaName: null,
        invenState: null,
        taskState: null,
        disableState: null,
        isDelete: null,
        version: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
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
      this.title = "添加巷道";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getWareInfos();
      this.reset();
      const id = row.id || this.ids;
      getLineInfo(id).then((response) => {
        this.form = response.data;
        if (this.form.wareCode != null && this.form.wareCode != "") {
          this.getAreaInfos(this.form.wareCode)
        } else {
          this.open = true;
          this.title = "修改巷道";
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.areaInfos = []
          if (this.form.id != null) {
            updateLineInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addLineInfo(this.form).then((response) => {
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

    /** 恢复按钮操作 */
    handleRecover(row) {
      const id = row.id;
      this.$modal.confirm('是否确认恢复ID为"' + id + '"的数据项？').then(function () {
        return getLineInfo(id)
      }).then((response) => {
        var info = response.data
        if (info == null) {
          this.$modal.msgError("选择数据项有误！")
          return;
        }
        info.isDelete = 0
        updateLineInfo(info).then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("恢复成功");
          } else {
            this.$modal.msgError(response.msg || "恢复失败")
          }
        })
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除巷道编号为"' + ids + '"的数据项？')
        .then(function () {
          return delLineInfo(ids);
        })
        .then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("删除成功");
          } else {
            this.$modal.msgError(response.msg || "删除失败")
          }
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-base/LineInfo/export",
        {
          ...this.queryParams,
        },
        `LineInfo_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
