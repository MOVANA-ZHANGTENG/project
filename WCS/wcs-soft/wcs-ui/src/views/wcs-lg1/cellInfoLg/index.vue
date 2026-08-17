<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="位置编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入位置编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择" clearable>
          <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="工序 " prop="work">
        <el-select v-model="queryParams.work" placeholder="请选择" clearable>
          <el-option v-for="item in works" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="上一道工序 " prop="upperWork">
        <el-select v-model="queryParams.upperWork" placeholder="请选择" clearable>
          <el-option v-for="item in upperWorks" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="下一道工序 " prop="nextWork">
        <el-select v-model="queryParams.nextWork" placeholder="请选择" clearable>
          <el-option v-for="item in nextWorks" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="阴/阳" prop="mark">
        <el-select v-model="queryParams.mark" placeholder="请选择" clearable>
          <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="区域" prop="area">
        <el-select v-model="queryParams.area" placeholder="请选择" clearable>
          <el-option v-for="item in areas" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="x" prop="x">
        <el-input
          v-model="queryParams.x"
          placeholder="请输入x"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="y" prop="y">
        <el-input
          v-model="queryParams.y"
          placeholder="请输入y"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="z" prop="z">
        <el-input
          v-model="queryParams.z"
          placeholder="请输入z"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="料架编码" prop="boxCode">
        <el-input
          v-model="queryParams.boxCode"
          placeholder="请输入料架编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="呼叫盒ip" prop="ip">
        <el-input
          v-model="queryParams.ip"
          placeholder="请输入呼叫盒ip"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产线编码" prop="productionLineCode">
        <el-select  v-model="queryParams.productionLineCode"  placeholder="请输入电极编码" clearable>
                        <el-option
                          v-for="item in productionLineCodes"
                          :key="item"
                          :label="item"
                          :value="item"
                         />
          </el-select>
      </el-form-item>
      <el-form-item label="工位编码" prop="stationCode">
        <el-input
          v-model="queryParams.stationCode"
          placeholder="请输入工位编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="料架类型" prop="boxType">
        <el-select v-model="queryParams.boxType" placeholder="请选择" clearable>
          <el-option v-for="item in boxTypes" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="位置的序号" prop="cellIndex">
        <el-input
          v-model="queryParams.cellIndex"
          placeholder="请输入位置的序号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="位置状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择" clearable>
          <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="是否启用" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option v-for="item in statuss" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="电极编码" prop="batteryCode">
        <el-select  v-model="queryParams.batteryCode"  placeholder="请输入电极编码" clearable>
                        <el-option
                          v-for="item in batteryCodes"
                          :key="item"
                          :label="item"
                          :value="item"
                         />
          </el-select>
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
          v-hasPermi="['wcs-lg1:cellInfoLg:add']"
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
          v-hasPermi="['wcs-lg1:cellInfoLg:edit']"
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
          v-hasPermi="['wcs-lg1:cellInfoLg:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-lg1:cellInfoLg:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cellInfoLgList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="位置编码" align="center" prop="code"  min-width="120">
    </el-table-column>
      <el-table-column label="类型" align="center" prop="type"  min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.type == 1">上料口</div>
          <div v-if="scope.row.type == 2">下料口</div>
          <div v-if="scope.row.type == 3">缓存站台</div>
        </template>
      </el-table-column>
    <el-table-column label="工序 " align="center" prop="work"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.work == 1">Coater</div>
          <div v-if="scope.row.work == 2">RollerPress</div>
          <div v-if="scope.row.work == 3">Rewinder</div>
          <div v-if="scope.row.work == 4">Slitterr</div>
          <div v-if="scope.row.work == 5">无工序</div>
        </template>
    </el-table-column>
    <el-table-column label="上一道工序 " align="center" prop="upperWork"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.upperWork == 1">Coater</div>
          <div v-if="scope.row.upperWork == 2">RollerPress</div>
          <div v-if="scope.row.upperWork == 3">Rewinder</div>
          <div v-if="scope.row.upperWork == 4">Slitterr</div>
          <div v-if="scope.row.upperWork == 5">无</div>
        </template>
    </el-table-column>
    <el-table-column label="下一道工序 " align="center" prop="nextWork"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.nextWork == 1">Coater</div>
          <div v-if="scope.row.nextWork == 2">RollerPress</div>
          <div v-if="scope.row.nextWork == 3">Rewinder</div>
          <div v-if="scope.row.nextWork == 4">Slitterr</div>
          <div v-if="scope.row.nextWork == 5">无</div>
        </template>
    </el-table-column>
    <el-table-column label="阴/阳" align="center" prop="mark"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.mark == 1">阴</div>
          <div v-if="scope.row.mark == 2">阳</div>
          <div v-if="scope.row.mark == 3">不区分</div>
        </template>
    </el-table-column>
    <el-table-column label="区域" align="center" prop="area"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.area == 1">左幅</div>
          <div v-if="scope.row.area == 2">右幅</div>
          <div v-if="scope.row.area == 3">不区分</div>
        </template>
    </el-table-column>
    <el-table-column label="x" align="center" prop="x"  min-width="120">
    </el-table-column>
    <el-table-column label="y" align="center" prop="y"  min-width="120">
    </el-table-column>
    <el-table-column label="z" align="center" prop="z"  min-width="120">
    </el-table-column>
    <el-table-column label="料架编码" align="center" prop="boxCode"  min-width="120">
    </el-table-column>
    <el-table-column label="呼叫盒ip" align="center" prop="ip"  min-width="120">
    </el-table-column>
    <el-table-column label="产线编码" align="center" prop="productionLineCode"  min-width="120">
    </el-table-column>
    <el-table-column label="工位编码" align="center" prop="stationCode"  min-width="120">
    </el-table-column>
      <el-table-column label="料架类型" align="center" prop="boxType"  min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.boxType == 1">类型1</div>
          <div v-if="scope.row.boxType == 2">类型2</div>
          <div v-if="scope.row.boxType == 3">类型3</div>
        </template>
      </el-table-column>
    <el-table-column label="位置的序号" align="center" prop="cellIndex"  min-width="120">
    </el-table-column>
    <el-table-column label="状态" align="center" prop="state"  min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.state == 1">空闲</div>
          <div v-if="scope.row.state == 2">任务中</div>
          <div v-if="scope.row.state == 3">占用</div>
        </template>
    </el-table-column>
      <el-table-column label="是否启用这个位置" align="center" prop="status"  min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.status == 1">启用</div>
          <div v-if="scope.row.status == 2">不启用</div>
        </template>
      </el-table-column>
    <el-table-column label="电极编码" align="center" prop="batteryCode"  min-width="120">
    </el-table-column>
    <el-table-column label="plc点位" align="center" prop="plcAddress"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-lg1:cellInfoLg:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-lg1:cellInfoLg:remove']"
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

    <!-- 添加或修改1厂基础位置对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="位置编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入位置编码" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择" clearable>
            <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工序 " prop="work">
          <el-select v-model="form.work" placeholder="请选择" clearable>
            <el-option v-for="item in works" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上一道工序 " prop="upperWork">
          <el-select v-model="form.upperWork" placeholder="请选择" clearable>
            <el-option v-for="item in upperWorks" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="下一道工序 " prop="nextWork">
          <el-select v-model="form.nextWork" placeholder="请选择" clearable>
            <el-option v-for="item in nextWorks" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="阴/阳" prop="mark">
          <el-select v-model="form.mark" placeholder="请选择" clearable>
            <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-select v-model="form.area" placeholder="请选择" clearable>
            <el-option v-for="item in areas" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="x" prop="x">
          <el-input v-model="form.x" placeholder="请输入x" />
        </el-form-item>
        <el-form-item label="y" prop="y">
          <el-input v-model="form.y" placeholder="请输入y" />
        </el-form-item>
        <el-form-item label="z" prop="z">
          <el-input v-model="form.z" placeholder="请输入z" />
        </el-form-item>
        <el-form-item label="料架编码" prop="boxCode">
          <el-input v-model="form.boxCode" placeholder="请输入料架编码" />
        </el-form-item>
        <el-form-item label="呼叫盒ip" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入呼叫盒ip" />
        </el-form-item>
        <el-form-item label="产线编码" prop="productionLineCode">
          <el-select  v-model="form.productionLineCode"  placeholder="请输入电极编码" clearable>
                        <el-option
                          v-for="item in productionLineCodes"
                          :key="item"
                          :label="item"
                          :value="item"
                         />
          </el-select>
        </el-form-item>
        <el-form-item label="工位编码" prop="stationCode">
          <el-input v-model="form.stationCode" placeholder="请输入工位编码" />
        </el-form-item>
        <el-form-item label="料架类型" prop="boxType">
          <el-select v-model="form.boxType" placeholder="请选择" clearable>
            <el-option v-for="item in boxTypes" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="位置的序号" prop="cellIndex">
          <el-input v-model="form.cellIndex" placeholder="请输入位置的序号" />
        </el-form-item>
        <el-form-item label="状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择" clearable>
            <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" prop="status">
          <el-select v-model="form.status" placeholder="请选择" clearable>
            <el-option v-for="item in statuss" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="电极编码" prop="batteryCode">
          <el-select  v-model="form.batteryCode"  placeholder="请输入电极编码" clearable>
                        <el-option
                          v-for="item in batteryCodes"
                          :key="item"
                          :label="item"
                          :value="item"
                         />
          </el-select>
        </el-form-item>
        <el-form-item label="plc点位地址" prop="plcAddress">
          <el-input v-model="form.plcAddress" placeholder="请输入plc点位地址" />
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
import { listCellInfoLg, getCellInfoLg, delCellInfoLg, addCellInfoLg, updateCellInfoLg,getByBatteryCode } from "@/api/wcs-lg1/cellInfoLg";
import request from "@/utils/request.js";

export default {
  name: "CellInfoLg",
  data() {
    return {
      batteryCodes: [],
      productionLineCodes: [],
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
      // 1厂基础位置表格数据
      cellInfoLgList: [],
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
        work: null,
        mark: null,
        area: null,
        x: null,
        y: null,
        z: null,
        boxCode: null,
        ip: null,
        productionLineCode: null,
        stationCode: null,
        boxType: null,
        cellIndex: null,
        state: null,
        status: null,
        batteryCode: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      works: [
        { value: 1, label: "Coater" },
        { value: 2, label: "RollerPress" },
        { value: 3, label: "Rewinder" },
        { value: 4, label: "Slitterr" },
        { value: 5, label: "无" },
      ],
      upperWorks: [
        { value: 1, label: "Coater" },
        { value: 2, label: "RollerPress" },
        { value: 3, label: "Rewinder" },
        { value: 4, label: "Slitterr" },
        { value: 5, label: "无" },
      ],
      nextWorks: [
        { value: 1, label: "Coater" },
        { value: 2, label: "RollerPress" },
        { value: 3, label: "Rewinder" },
        { value: 4, label: "Slitterr" },
        { value: 5, label: "无" },
      ],
      marks: [
        { value: 1, label: "阴" },
        { value: 2, label: "阳" },
        { value: 3, label: "不区分" },
      ],
      areas: [
        { value: 1, label: "左幅" },
        { value: 2, label: "右幅" },
        { value: 3, label: "不区分" },
      ],
      types: [
        { value: 1, label: "上料口" },
        { value: 2, label: "下料口" },
        { value: 3, label: "缓存站台" },
      ],
      boxTypes: [
        { value: 1, label: "类型1" },
        { value: 2, label: "类型2" },
        { value: 3, label: "类型3" },
      ],
      states: [
        { value: 1, label: "空闲" },
        { value: 2, label: "任务中" },
        { value: 3, label: "占用" },
      ],
      statuss: [
        { value: 1, label: "启用" },
        { value: 2, label: "不启用" },
      ],
    };
  },
  created() {
    this.getList();
    this.getBatteryCodes();
    this.getProductionLineCodes();
  },
  methods: {

    //查询所有电极
    getBatteryCodes() {
      request({
        url: "/wcs-lg1/cellInfoLg/batteryCode",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          this.batteryCodes = response.data;
          console.log(this.batteryCodes)
        }
      });
    },

    //查询所有产线
    getProductionLineCodes() {
      request({
        url: "/wcs-lg1/cellInfoLg/productionLineCode",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          this.productionLineCodes = response.data;
          console.log(this.productionLineCodes)
        }
      });
    },

    /** 查询1厂基础位置列表 */
    getList() {
      this.loading = true;
      listCellInfoLg(this.queryParams).then(response => {
          if(response.code==200){
            this.cellInfoLgList = response.rows;
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
        work: null,
        mark: null,
        area: null,
        x: null,
        y: null,
        z: null,
        boxCode: null,
        ip: null,
        productionLineCode: null,
        stationCode: null,
        boxType: null,
        cellIndex: null,
        state: null,
        status: null,
        batteryCode: null,
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
      this.title = "添加1厂基础位置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCellInfoLg(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改1厂基础位置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCellInfoLg(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg);
              }
            });
          } else {
            addCellInfoLg(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
              }else{
                this.$modal.msgError(response.msg);
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除1厂基础位置编号为"' + ids + '"的数据项？').then(function() {
        return delCellInfoLg(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-lg1/cellInfoLg/export', {
        ...this.queryParams
      }, `cellInfoLg_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
