<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="按钮" prop="btn">
        <el-input v-model="queryParams.btn" placeholder="请输入按钮" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <!-- <el-form-item label="按钮状态" prop="btnState">
        <el-select v-model="queryParams.btnState" placeholder="请选择" clearable>
          <el-option v-for="item in btnStates" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="事件处理状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择" clearable>
          <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="执行器" prop="handId">
        <el-select v-model="form.handId" placeholder="请选择" clearable>
          <el-option v-for="item in callBoxCmdList" :key="item.id" :label="item.name" :value="item.id">
          </el-option>
        </el-select>
        <!-- <el-input
          v-model="queryParams.handId"
          placeholder="请输入执行器"
          clearable
          @keyup.enter.native="handleQuery"
        /> -->
      </el-form-item>
      <el-form-item label="位置编码" prop="positionCode">
        <el-input v-model="queryParams.positionCode" placeholder="请输入位置编码" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="ip" prop="ip">
        <el-input v-model="queryParams.ip" placeholder="请输入ip" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" @change="getAreaInfos(queryParams.wareCode)" placeholder="仓库"
          clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="删除标志" prop="delFlag">
        <el-select v-model="queryParams.delFlag" placeholder="请选择删除标志" clearable>
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
          v-hasPermi="['wcs-task:CallBoxInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-task:CallBoxInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-task:CallBoxInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-task:CallBoxInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="CallBoxInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" width="150">
      </el-table-column>
      <el-table-column label="位置编码" align="center" prop="positionCode" min-width="120">
      </el-table-column>
      <el-table-column label="ip" align="center" prop="ip" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="按钮" align="center" prop="btn" min-width="120">
      </el-table-column> -->
      <!-- <el-table-column label="按钮状态" align="center" prop="btnState" min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.btnState == 0" style="color: #67C23A;">未按下</div>
          <div v-if="scope.row.btnState == 1" style="color: #F56C6C;">已按下</div>
        </template>
      </el-table-column>
      <el-table-column label="事件处理状态" align="center" prop="state" min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.state == 0" style="color: #F56C6C;">未处理</div>
          <div v-if="scope.row.state == 1" style="color: #67C23A;">已处理</div>
        </template>
      </el-table-column> -->
      <el-table-column label="处理器" align="center" prop="handleName" min-width="180">
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="delFlag" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.delFlag" />
        </template>
      </el-table-column>

      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:CallBoxInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.delFlag == 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-task:CallBoxInfo:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.delFlag == 1"
            @click="handleRecover(scope.row)">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改呼叫盒对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
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
        <el-form-item label="位置编码" prop="positionCode">
          <el-input v-model="form.positionCode" placeholder="请输入位置编码" />
        </el-form-item>
        <el-form-item label="ip" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入ip" />
        </el-form-item>
        <!-- <el-form-item label="按钮" prop="btn">
          <el-input v-model="form.btn" placeholder="请输入按钮" />
        </el-form-item>
        <el-form-item label="按钮状态" prop="btnState">
          <el-select v-model="form.btnState" placeholder="请选择" clearable>
            <el-option v-for="item in btnStates" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="事件处理状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择" clearable>
            <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="处理器" prop="handId">
          <el-select v-model="form.handId" placeholder="请选择" clearable>
            <el-option v-for="item in callBoxCmdList" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
          <!-- <HandleInfo :handleType="1" :type="2" v-model="form.cmdList" /> -->
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
  listCallBoxInfo,
  getCallBoxInfo,
  delCallBoxInfo,
  addCallBoxInfo,
  updateCallBoxInfo,
  getCallBoxHandle,
} from "@/api/wcs-task/CallBoxInfo";
import request from "@/utils/request.js";
import HandleInfo from "../../wcs-base/Handle/HandleInfo";
import { listWareInfo } from "@/api/wcs-base/WareInfo";
export default {
  name: "CallBoxInfo",
  dicts:['del_flag'],
  data() {
    return {
      methodList: [],
      checkedMethods: [],
      callBoxCmdList: [],
      handleList: [],
      wareInfos: [],
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
      // 呼叫盒表格数据
      CallBoxInfoList: [],
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
        btn: null,
        btnState: null,
        state: null,
        handId: null,
        positionCode: null,
        wareCode: null,
        delFlag: '0',
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [{ required: true, message: "编码不能为空", trigger: "blur" }],
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        positionCode: [{ required: true, message: "位置编码不能为空", trigger: "blur" }],
        wareCode: [
          { required: true, message: "仓库不能为空", trigger: "blur" },
        ],
      },
      btnStates: [
        { value: 0, label: "未按下" },
        { value: 1, label: "已按下" },
      ],
      states: [
        { value: 0, label: "未处理" },
        { value: 1, label: "已处理" },
      ],
    };
  },
  components: {
    HandleInfo,
  },
  created() {
    this.getList();
    this.getCallBoxCmdList();
    this.getWareInfos();
  },
  methods: {

    //获取所有仓库
    getWareInfos() {
      listWareInfo({ isDelete: 0 }).then((response) => {
        if (response.code == 200) {
          this.wareInfos = response.rows;
        }
      });
    },

    //查询所有呼叫盒的执行器
    getCallBoxCmdList() {
      var that = this;
      request({
        url: "/wcs-task/CallBoxInfo/getCallBoxHandle",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          var data = response.data;

          for (var i = 0; i < data.length; i++) {

            //console.log(data[i]);
            that.callBoxCmdList.push(data[i]);
            // console.log(that.callBoxCmdList);
          }
        }
      });

    },

    /** 查询呼叫盒列表 */
    getList() {
      this.loading = true;
      listCallBoxInfo(this.queryParams).then((response) => {
        if (response.code == 200) {
          this.CallBoxInfoList = response.rows;
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
        callBoxType: null,
        btn: null,
        btnState: null,
        state: null,
        handId: null,
        positionCode: null,
        wareCode: null,
        createTime: null,
        updateTime: null,
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
      this.reset();
      this.open = true;
      this.title = "添加呼叫盒";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getCallBoxInfo(id).then((response) => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改呼叫盒";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateCallBoxInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addCallBoxInfo(this.form).then((response) => {
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
        return  getCallBoxInfo(id)
      }).then((response) => {
        var info = response.data
        if (info == null) {
          this.$modal.msgError("选择数据项有误！")
          return;
        }
        info.isDelete = 0
        updateCallBoxInfo(info).then((response) => {
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
        .confirm('是否确认删除呼叫盒编号为"' + ids + '"的数据项？')
        .then(function () {
          return delCallBoxInfo(ids);
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
        "wcs-task/CallBoxInfo/export",
        {
          ...this.queryParams,
        },
        `CallBoxInfo_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
