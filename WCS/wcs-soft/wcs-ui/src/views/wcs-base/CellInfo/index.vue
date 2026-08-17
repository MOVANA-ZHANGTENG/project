<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" placeholder="仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="上位编码" prop="hostCode">
        <el-input v-model="queryParams.hostCode" placeholder="请输入上位编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="下位编码" prop="subCode">
        <el-input v-model="queryParams.subCode" placeholder="请输入下位编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!--

      <el-form-item label="库区" prop="areaCode">
        <el-select @change="queryParams.lineCode = null; getLineInfos(queryParams.areaCode);"
          v-model="queryParams.areaCode" placeholder="库区" clearable>
          <el-option v-for="item in areaInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item> -->
      <el-form-item label="巷道" prop="lineCode">
        <el-select v-model="queryParams.lineCode" placeholder="巷道" clearable>
          <el-option v-for="item in lineInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="前置货位" prop="preCode">
        <el-input v-model="queryParams.preCode" placeholder="请输入前置货位" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="使用频率" prop="abc">
        <el-select v-model="queryParams.abc" placeholder="请选择使用频率" clearable>
          <el-option v-for="dict in dict.type.abc_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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
      <el-form-item label="禁用状态" prop="disableState">
        <el-select v-model="queryParams.disableState" placeholder="请选择禁用状态" clearable>
          <el-option v-for="item in dict.type.disable_state" :key="item.key" :label="item.label" :value="item.value" />
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
          v-hasPermi="['wcs-base:CellInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddBatch"
          v-hasPermi="['wcs-base:CellInfo:add']">批量新增堆垛机库位</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddSsxBatch"
          v-hasPermi="['wcs-base:CellInfo:add']">批量新增四向车库位</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:CellInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:CellInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:CellInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="CellInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" align="center" width="55" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="上位编码" align="center" prop="hostCode" min-width="120">
      </el-table-column>
      <el-table-column label="下位编码" align="center" prop="subCode" min-width="120">
      </el-table-column>
      <el-table-column label="巷道编码" align="center" prop="lineCode" min-width="120">
      </el-table-column>
      <el-table-column label="巷道名称" align="center" prop="lineName" min-width="150">
      </el-table-column>
      <el-table-column label="X" align="center" prop="x" width="80"> </el-table-column>
      <el-table-column label="Y" align="center" prop="y" width="80"> </el-table-column>
      <el-table-column label="Z" align="center" prop="z" width="80"> </el-table-column>
      <!-- <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="150">
      </el-table-column>
      <el-table-column label="库区编码" align="center" prop="areaCode" min-width="120">
      </el-table-column>
      <el-table-column label="库区名称" align="center" prop="areaName" min-width="150">
      </el-table-column>
      <el-table-column label="前置货位" align="center" prop="preCode" min-width="120">
      </el-table-column> -->
      <el-table-column label="使用频率" align="center" prop="abc" min-width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.abc_type" :value="scope.row.abc" />
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
      <el-table-column label="禁用状态" align="center" prop="disableState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.disable_state" :value="scope.row.disableState" />
        </template>
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="isDelete" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.isDelete" />
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:CellInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.isDelete == 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-base:CellInfo:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.isDelete == 1"
            @click="handleRecover(scope.row)" v-hasPermi="['wcs-base:CellInfo:recover']">恢复</el-button>
          <el-button size="mini" type="text" icon="el-icon-document" @click="handleViewRecord(scope.row)"
            style="color: #E6A23C;">日志</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改库位对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="上位编码" prop="hostCode">
          <el-input v-model="form.hostCode" placeholder="请输入上位编码" />
        </el-form-item>
        <el-form-item label="下位编码" prop="subCode">
          <el-input v-model="form.subCode" placeholder="请输入下位编码" />
        </el-form-item>
        <el-form-item label="前置货位" prop="preCode">
          <el-input v-model="form.preCode" placeholder="请输入前置货位" />
        </el-form-item>
        <el-form-item label="仓库" prop="wareCode">
          <SelectWareModel v-model="form.wareCode" />
        </el-form-item>
        <!-- <el-form-item label="库区" prop="areaCode">
          <el-select @change="form.lineCode = null; getLineInfos(form.areaCode);" v-model="form.areaCode"
            placeholder="库区" clearable>
            <el-option v-for="item in areaInfos" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="巷道" prop="lineCode">
          <el-select v-model="form.lineCode" placeholder="巷道" clearable>
            <el-option v-for="item in lineInfos" :key="item.code" :label="'[' + item.code + ']-' + item.name"
              :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="X" prop="x">
          <el-input v-model="form.x" placeholder="请输入X" />
        </el-form-item>
        <el-form-item label="Y" prop="y">
          <el-input v-model="form.y" placeholder="请输入Y" />
        </el-form-item>
        <el-form-item label="Z" prop="z">
          <el-input v-model="form.z" placeholder="请输入Z" />
        </el-form-item>
        <el-form-item label="使用频率" prop="abc">
          <el-select v-model="form.abc" placeholder="请选择使用频率">
            <el-option v-for="dict in dict.type.abc_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="库位类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择库位类型">
            <el-option key="0" label="普通库位" value="0"></el-option>
            <el-option key="1" label="入库接驳位" value="1"></el-option>
            <el-option key="2" label="出库接驳位" value="2"></el-option>
            <el-option key="3" label="通用接驳位" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否为预留空位" prop="isMove">
          <el-select v-model="form.isMove" placeholder="请选择是否为预留空位">
            <el-option key="0" label="否" value="0"></el-option>
            <el-option key="1" label="是" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否禁用" prop="disableState" v-if="form.id != null">
          <el-select v-model="form.disableState" placeholder="请选择禁用状态" clearable>
            <el-option v-for="dict in dict.type.disable_state" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)" />
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量创建库位 -->
    <el-dialog title="批量创建库位(排)" :visible.sync="openCreateCell" width="600px" append-to-body>
      <el-form ref="batchAddCellForm" :model="batchAddCellForm" :rules="batchAddCellRules" label-width="80px">

        <el-form-item label="仓库" prop="wareCode">
          <SelectWareModel v-model="batchAddCellForm.wareCode" />
        </el-form-item>
        <el-form-item label="巷道" prop="lineCode">
          <el-select v-model="batchAddCellForm.lineCode" placeholder="请选择巷道" clearable>
            <el-option v-if="line.wareCode == batchAddCellForm.wareCode" v-for="line in lineInfos" :key="line.code"
              :label="'[' + line.code + ']-' + line.name" :value="line.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="A/B面" prop="ab">
          <el-select v-model="batchAddCellForm.ab" placeholder="请选择A/B面" clearable>
            <el-option v-for="item in areaTypes" :key=item.value :label=item.label :value=item.value></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="伸位" prop="priority">
          <el-input-number v-model="batchAddCellForm.priority" placeholder="请输入伸位" :step="1" :min="1"
            :max="15"></el-input-number>
        </el-form-item>
        <el-form-item label="第几排" prop="x">
          <el-input-number v-model="batchAddCellForm.x" placeholder="请输入第几排" :step="1" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="最大列" prop="y">
          <el-input-number v-model="batchAddCellForm.y" placeholder="请输入列数" :step="1" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="最大层" prop="z">
          <el-input-number v-model="batchAddCellForm.z" placeholder="请输入层数" :step="1" :min="1"></el-input-number>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFormBatch">确 定</el-button>
        <el-button @click="openCreateCell = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="批量创建库位(四向车层)" :visible.sync="openCreateSxcCell" width="600px" append-to-body>
      <el-form ref="batchAddSxcCellForm" :model="batchAddSxcCellForm" :rules="batchAddSxcCellRules" label-width="80px">
        <el-form-item label="仓库" prop="wareCode">
          <SelectWareModel v-model="batchAddSxcCellForm.wareCode" />
        </el-form-item>
        <el-form-item label="零点坐标" prop="xy">
          <el-select v-model="batchAddSxcCellForm.xy" placeholder="请选择零点坐标">
            <el-option :value="1" label="左上"> </el-option>
            <el-option :value="2" label="右上"> </el-option>
            <el-option :value="3" label="左下"> </el-option>
            <el-option :value="4" label="右下"> </el-option>
          </el-select>

        </el-form-item>
        <el-form-item label="X" prop="x">
          <el-input-number v-model="batchAddSxcCellForm.x" placeholder="请输入X" :step="1" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="Y" prop="y">
          <el-input-number v-model="batchAddSxcCellForm.y" placeholder="请输入Y" :step="1" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="Z" prop="z">
          <el-input-number v-model="batchAddSxcCellForm.z" placeholder="请输入Z" :step="1" :min="1"></el-input-number>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFormSxcBatch">确 定</el-button>
        <el-button @click="openCreateSxcCell = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 库位日志抽屉 -->
    <CellRecordDrawer
      :show.sync="recordDrawerVisible"
      :cellCode="currentCellCode"
      :wareCode="currentWareCode"
    />
  </div>
</template>

<script>
import request from "@/utils/request.js";
import {
  listCellInfo,
  getCellInfo,
  delCellInfo,
  addCellInfo,
  updateCellInfo,
} from "@/api/wcs-base/CellInfo";

import {
  listLineInfo
} from "@/api/wcs-base/LineInfo";

import SelectWareModel from "../../wcs-base/WareInfo/SelectModel.vue";
import CellRecordDrawer from "../../wcs-base/CellRecord/CellRecordDrawer.vue";


export default {
  name: "CellInfo",
  dicts: ["del_flag", "disable_state", "inven_state", "is_task", "abc_type"],
  components: {
    SelectWareModel,
    CellRecordDrawer,
  },
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
      // 库位表格数据
      CellInfoList: [],
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
        hostCode: null,
        subCode: null,
        lineCode: null,
        lineName: null,
        x: null,
        wareCode: null,
        wareName: null,
        y: null,
        z: null,
        areaCode: null,
        areaName: null,
        preCode: null,
        invenState: null,
        taskState: null,
        disableState: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: '0',
      },
      // 表单参数
      form: {},
      batchAddCellForm: {
        wareCode: null,
        areaCode: null,
        lineCode: null,
        ab: null,
        priority: null,
        x: null,
        y: null,
        z: null,
      },

      batchAddCellRules: {
        // wareCode: [{ required: true, message: "仓库不能为空", trigger: "blur" },],
        // areaCode: [{ required: true, message: "区域不能为空", trigger: "blur" },],
        // lineCode: [{ required: true, message: "巷道不能为空", trigger: "blur" },],
        ab: [{ required: true, message: "ad面选择不能为空", trigger: "blur" },],
        priority: [{ required: true, message: "伸位不能为空", trigger: "blur" }],
        x: [{ required: true, message: "排数不能为空", trigger: "blur" }],
        y: [{ required: true, message: "列数不能为空", trigger: "blur" }],
        z: [{ required: true, message: "层数不能为空", trigger: "blur" }],
      },

      batchAddSxcCellForm: {},
      openCreateSxcCell: false,
      batchAddSxcCellRules: {
        wareCode: [{ required: true, message: "仓库不能为空", trigger: "blur" },],
        x: [{ required: true, message: "排数不能为空", trigger: "blur" }],
        y: [{ required: true, message: "列数不能为空", trigger: "blur" }],
        z: [{ required: true, message: "层数不能为空", trigger: "blur" }],
      },
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
        lineCode: [
          { required: true, message: "巷道不能为空", trigger: "blur" },
        ],
      },
      wareInfos: [],
      areaInfos: [],
      lineInfos: [],
      areaTypes: [
        { value: "A", label: "A" },
        { value: "B", label: "B" },
      ],
      openCreateCell: false,
      // 库位日志抽屉
      recordDrawerVisible: false,
      currentCellCode: "",
      currentWareCode: "",
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
    this.getLineInfos();
  },
  methods: {
    batchAddGetAreaInfos(wareCode) {
      // this.batchAddCellForm.areaCode = null
      // this.batchAddCellForm.lineCode = null
      this.batchAddCellForm = { wareCode: wareCode, areaCode: null, lineCode: null }
      this.getAreaInfos(wareCode)
    },
    batchAddGetLineInfos(areaCode) {
      this.batchAddCellForm = {
        wareCode: this.batchAddCellForm.wareCode,
        areaCode: this.batchAddCellForm.areaCode,
        lineCode: null,
        ab: this.batchAddCellForm.ab,
        priority: this.batchAddCellForm.priority,
        x: this.batchAddCellForm.x,
        y: this.batchAddCellForm.y,
        z: this.batchAddCellForm.z,
      }
      this.getLineInfos()
    },

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
    //获取所有库区
    getAreaInfos(wareCode) {
      if (wareCode == null || wareCode == '') {
        this.areaInfos = []
        return
      }
      this.form.areaCode = null;
      this.areaInfos = [];
      if (wareCode != null) {
        listAreaInfo({ wareCode: wareCode }).then((response) => {
          if (response.code == 200) {
            this.areaInfos = response.rows;
          }
        });
      }
    },
    getLineInfos() {

      this.form.lineCode = null;
      this.lineInfos = [];
      listLineInfo({ pageSize: 9999, pageNum: 1 }).then((response) => {
        if (response.code == 200) {
          this.lineInfos = response.rows;
        }
      });
    },
    /** 查询库位列表 */
    getList() {
      this.loading = true;
      listCellInfo(this.queryParams).then((response) => {
        if (response.code == 200) {
          this.CellInfoList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
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
        hostCode: null,
        subCode: null,
        lineCode: null,
        lineName: null,
        x: null,
        wareCode: null,
        wareName: null,
        y: null,
        z: null,
        areaCode: null,
        areaName: null,
        preCode: null,
        invenState: null,
        taskState: null,
        disableState: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
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
      this.title = "添加库位";
    },
    /** 新增按钮操作 */
    handleAddBatch() {
      this.batchAddCellForm = {}
      this.openCreateCell = true;
    },

    /** 新增按钮操作 */
    handleAddSsxBatch() {
      this.batchAddSxcCellForm = {}
      this.openCreateSxcCell = true;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getWareInfos();
      this.reset();
      const id = row.id || this.ids;
      getCellInfo(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改库位";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.areaInfos = []
          this.lineInfos = []
          if (this.form.id != null) {
            updateCellInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败")
              }
            });
          } else {
            addCellInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "新增失败")
              }
            });
          }
        }
      });
    },
    /** 提交按钮 */
    submitFormBatch() {
      var that = this;
      this.$refs["batchAddCellForm"].validate((valid) => {
        if (valid) {
          this.loading = true;
          request({
            url: "/wcs-base/CellInfo/batchAddCell",
            method: "post",
            data: this.batchAddCellForm,
          }).then((response) => {
            this.loading = false;
            if (response.code == 200) {
              this.$modal.msgSuccess("新增成功");
              this.openCreateCell = false;
              this.getList();
            } else {
              that.$modal.msgError(response.msg || "新增失败");
            }
          });
        }
      });
    },
    /** 提交按钮 */
    submitFormSxcBatch() {
      var that = this;
      this.$refs["batchAddSxcCellForm"].validate((valid) => {
        if (valid) {
          this.loading = true;
          request({
            url: "/wcs-base/CellInfo/batchAddSxcCell",
            method: "post",
            data: this.batchAddSxcCellForm,
          }).then((response) => {
            this.loading = false;
            if (response.code == 200) {
              this.$modal.msgSuccess("新增成功");
              this.openCreateCell = false;
              this.getList();
            } else {
              that.$modal.msgError(response.msg || "新增失败");
            }
          });
        }
      });
    },

    /** 恢复按钮操作 */
    handleRecover(row) {
      const id = row.id;
      this.$modal.confirm('是否确认恢复ID为"' + id + '"的数据项？').then(function () {
        return getCellInfo(id)
      }).then((response) => {
        var info = response.data
        if (info == null) {
          this.$modal.msgError("选择数据项有误！")
          return;
        }
        info.isDelete = 0
        updateCellInfo(info).then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("恢复成功");
          } else {
            that.$modal.msgError(response.msg || "恢复失败");
          }
        })
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除库位编号为"' + ids + '"的数据项？')
        .then(function () {
          return delCellInfo(ids);
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
        "wcs-base/CellInfo/export",
        {
          ...this.queryParams,
        },
        `CellInfo_${new Date().getTime()}.xlsx`
      );
    },
    /** 查看库位日志 */
    handleViewRecord(row) {
      this.currentCellCode = row.code;
      this.currentWareCode = row.wareCode;
      this.recordDrawerVisible = true;
    },
  },
};
</script>
