<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="位置编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入位置编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
          <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="工序" prop="work">
        <el-select v-model="queryParams.work" placeholder="请选择工序" clearable>
          <el-option v-for="item in works" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="阴/阳" prop="mark">
        <el-select v-model="queryParams.mark" placeholder="请选择阴/阳" clearable>
          <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="区域" prop="area">
        <el-select v-model="queryParams.area" placeholder="请选择区域" clearable>
          <el-option v-for="item in areas" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="x" prop="x">
        <el-input v-model="queryParams.x" placeholder="请输入x" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="y" prop="y">
        <el-input v-model="queryParams.y" placeholder="请输入y" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="z" prop="z">
        <el-input v-model="queryParams.z" placeholder="请输入z" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="料架编码" prop="boxCode">
        <el-input v-model="queryParams.boxCode" placeholder="请输入料架编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="呼叫盒ip" prop="ip">
        <el-input v-model="queryParams.ip" placeholder="请输入呼叫盒ip" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="产线编码" prop="productionLineCode">
        <el-input v-model="queryParams.productionLineCode" placeholder="请输入产线编码" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="工位编码" prop="stationCode">
        <el-input v-model="queryParams.stationCode" placeholder="请输入工位编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="料架类型" prop="boxType">
        <el-select v-model="queryParams.boxType" placeholder="请选择类型" clearable>
          <el-option v-for="item in boxTypes" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="位置的序号" prop="cellIndex">
        <el-input v-model="queryParams.cellIndex" placeholder="请输入位置的序号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="位置状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable>
          <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="是否启用此位置" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择是否启用" clearable>
          <el-option v-for="item in status" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="电极编码" prop="batteryCode">
        <el-input v-model="queryParams.batteryCode" placeholder="请输入电极编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:cellInfoLG:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:cellInfoLG:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:cellInfoLG:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:cellInfoLG:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cellInfoLGList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="位置编码" align="center" prop="code">
      </el-table-column>
      <el-table-column label="类型" align="center" prop="type">
        <template slot-scope="scope">
          <div v-if="scope.row.type == 1">位置类型1</div>
          <div v-if="scope.row.type == 2">位置类型2</div>
          <div v-if="scope.row.type == 3">位置类型3</div>
        </template>
      </el-table-column>
      <el-table-column label="工序" align="center" prop="work">
        <template slot-scope="scope">
          <div v-if="scope.row.work == 1">Coater</div>
          <div v-if="scope.row.work == 2">Roller-Press</div>
          <div v-if="scope.row.work == 3">Rewinder</div>
          <div v-if="scope.row.work == 4">Tapping</div>
          <div v-if="scope.row.work == 5">Slitter</div>
          <div v-if="scope.row.work == 6">无工序</div>
        </template>
      </el-table-column>
      <el-table-column label="阴/阳" align="center" prop="mark">
        <template slot-scope="scope">
          <div v-if="scope.row.mark == 1">阴极</div>
          <div v-if="scope.row.mark == 2">阳极</div>
          <div v-if="scope.row.mark == 3">不区分</div>
        </template>
      </el-table-column>
      <el-table-column label="区域" align="center" prop="area">
        <template slot-scope="scope">
          <div v-if="scope.row.mark == 1">左幅</div>
          <div v-if="scope.row.mark == 2">右幅</div>
          <div v-if="scope.row.mark == 3">不区分</div>
        </template>
      </el-table-column>
      <el-table-column label="x" align="center" prop="x">
      </el-table-column>
      <el-table-column label="y" align="center" prop="y">
      </el-table-column>
      <el-table-column label="z" align="center" prop="z">
      </el-table-column>
      <el-table-column label="料架编码" align="center" prop="boxCode">
      </el-table-column>
      <el-table-column label="呼叫盒ip" align="center" prop="ip">
      </el-table-column>
      <el-table-column label="产线编码" align="center" prop="productionLineCode">
      </el-table-column>
      <el-table-column label="工位编码" align="center" prop="stationCode">
      </el-table-column>
      <el-table-column label="料架类型" align="center" prop="boxType">
        <template slot-scope="scope">
          <div v-if="scope.row.boxType == 1">料架类型1</div>
          <div v-if="scope.row.boxType == 2">料架类型3</div>
          <div v-if="scope.row.boxType == 3">料架类型3</div>
        </template>
      </el-table-column>
      <el-table-column label="位置的序号" align="center" prop="cellIndex">
      </el-table-column>
      <el-table-column label="位置状态" align="center" prop="state">
        <template slot-scope="scope">
          <div v-if="scope.row.state == 1">空闲</div>
          <div v-if="scope.row.state == 2">任务执行中</div>
          <div v-if="scope.row.state == 3">占用</div>
        </template>
      </el-table-column>
      <el-table-column label="是否启用位置" align="center" prop="status">
        <template slot-scope="scope">
          <div v-if="scope.row.status == 1">启用</div>
          <div v-if="scope.row.status == 2">不启用</div>
        </template>
      </el-table-column>
      <el-table-column label="电极编码" align="center" prop="batteryCode">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:cellInfoLG:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:cellInfoLG:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改位置基础信息对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="位置编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入位置编码" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" clearable>
            <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工序" prop="work">
          <el-select v-model="form.work" placeholder="请选择工序" clearable>
            <el-option v-for="item in works" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="阴/阳" prop="mark">
          <el-select v-model="form.mark" placeholder="请选择阴/阳" clearable>
            <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-select v-model="form.area" placeholder="请选择区域" clearable>
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
          <el-input v-model="form.productionLineCode" placeholder="请输入产线编码" />
        </el-form-item>
        <el-form-item label="工位编码" prop="stationCode">
          <el-input v-model="form.stationCode" placeholder="请输入工位编码" />
        </el-form-item>
        <el-form-item label="料架类型" prop="boxType">
          <el-select v-model="form.boxType" placeholder="请选择料架类型" clearable>
            <el-option v-for="item in boxTypes" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="位置的序号" prop="cellIndex">
          <el-input v-model="form.cellIndex" placeholder="请输入位置的序号" />
        </el-form-item>
        <el-form-item label="位置状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择状态" clearable>
            <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用这个位置" prop="status">
          <el-select v-model="form.status" placeholder="请选择料架类型">
            <el-option v-for="item in status" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="电极编码" prop="batteryCode">
          <el-input v-model="form.batteryCode" placeholder="请输入电极编码" />
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
import { listCellInfoLG, getCellInfoLG, delCellInfoLG, addCellInfoLG, updateCellInfoLG } from "@/api/wcs-base/cellInfoLG";

export default {
  name: "CellInfoLG",
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
      // 位置基础信息表格数据
      cellInfoLGList: [],
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
      types: [
        { value: 1, label: "位置类型1" }
        , { value: 2, label: "位置类型2" }
        , { value: 3, label: "位置类型3" }
      ],
      works: [
        { value: 1, label: "Coater" }
        , { value: 2, label: "Roller-Press" }
        , { value: 3, label: "Rewinder" }
        , { value: 4, label: "Tapping" }
        , { value: 5, label: "Slitter" }
        , { value: 6, label: "无工序" }
      ],
      marks: [
        { value: 1, label: "阴极" }
        , { value: 2, label: "阳极" }
        , { value: 3, label: "不区分" }
      ],
      areas: [
        { value: 1, label: "左幅" }
        , { value: 2, label: "右幅" }
        , { value: 3, label: "不区分" }
      ],
      boxTypes: [
        { value: 1, label: "料架类型1" }
        , { value: 2, label: "料架类型2" }
        , { value: 3, label: "料架类型3" }
      ],
      states: [
        { value: 1, label: "空闲" }
        , { value: 2, label: "任务中" }
        , { value: 3, label: "占用" }
      ],
      status: [
        { value: 1, label: "启用" }
        , { value: 2, label: "不启用" }
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询位置基础信息列表 */
    getList() {
      this.loading = true;
      listCellInfoLG(this.queryParams).then(response => {
        if (response.code == 200) {
          this.cellInfoLGList = response.rows;
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加位置基础信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCellInfoLG(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改位置基础信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCellInfoLG(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addCellInfoLG(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
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
      this.$modal.confirm('是否确认删除位置基础信息编号为"' + ids + '"的数据项？').then(function () {
        return delCellInfoLG(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/cellInfoLG/export', {
        ...this.queryParams
      }, `cellInfoLG_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
