<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="策略编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入策略编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="策略名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入策略名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="频率策略" prop="isAbc">
        <el-select v-model="queryParams.isAbc" placeholder="请选择是否匹配使用频率abc" clearable>
          <el-option v-for="dict in dict.type.is_or_not" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="巷道策略" prop="lineBalanceType">
        <el-select v-model="queryParams.lineBalanceType" placeholder="请选择巷道策略" clearable>
          <el-option v-for="dict in dict.type.line_balance_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="X向策略" prop="xType">
        <el-select v-model="queryParams.xType" placeholder="请选择X向策略" clearable>
          <el-option v-for="dict in dict.type.xyz_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="Y向策略" prop="yType">
        <el-select v-model="queryParams.yType" placeholder="请选择Y向策略" clearable>
          <el-option v-for="dict in dict.type.xyz_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="Z向策略" prop="zType">
        <el-select v-model="queryParams.zType" placeholder="请选择Z向策略" clearable>
          <el-option v-for="dict in dict.type.xyz_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="系统内置" prop="isSys">
        <el-select v-model="queryParams.isSys" placeholder="请选择是否系统内置" clearable>
          <el-option v-for="dict in dict.type.is_or_not" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['wcs-base:tacics:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:tacics:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:tacics:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:tacics:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tacicsList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />

      <el-table-column label="策略名称" align="center" prop="name" min-width="120">
      </el-table-column>
      <el-table-column label="是否匹配abc" align="center" prop="isAbc" min-width="120">
        <template slot="header" slot-scope="scope">
          <el-tooltip content="是否根据库位使用频率进行匹配库位,a表示使用频率最高,b表示使用频率中,c表示使用频率最低" placement="top" effect="light">
            <span>是否匹配abc</span>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_or_not" :value="scope.row.isAbc" />
        </template>
      </el-table-column>
      <el-table-column label="巷道策略" align="center" prop="lineBalanceType" min-width="120">
        <template slot="header" slot-scope="scope">
          <el-tooltip content="根据巷道规则进行匹配库位" placement="top" effect="light">
            <span>巷道策略</span>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.line_balance_type" :value="scope.row.lineBalanceType" />
        </template>
      </el-table-column>
      <el-table-column label="X向策略" align="center" prop="xType" min-width="120">
        <template slot="header" slot-scope="scope">
          <el-tooltip content="根据库位X向递增,递减规则进行匹配库位" placement="top" effect="light">
            <span>X向策略</span>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.xyz_type" :value="scope.row.xType" />
        </template>
      </el-table-column>
      <el-table-column label="Y向策略" align="center" prop="yType" min-width="120">
        <template slot="header" slot-scope="scope">
          <el-tooltip content="根据库位Y向递增,递减规则进行匹配库位" placement="top" effect="light">
            <span>Y向策略</span>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.xyz_type" :value="scope.row.yType" />
        </template>
      </el-table-column>
      <el-table-column label="Z向策略" align="center" prop="zType" min-width="120">
        <template slot="header" slot-scope="scope">
          <el-tooltip content="根据库位Z向递增,递减规则进行匹配库位" placement="top" effect="light">
            <span>Z向策略</span>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.xyz_type" :value="scope.row.zType" />
        </template>
      </el-table-column>
      <el-table-column label="系统内置" align="center" prop="isSys" min-width="120">
        <template slot="header" slot-scope="scope">
          <el-tooltip content="是否为系统内置规则,系统内置规则无法删除，只能修改" placement="top" effect="light">
            <span>系统内置</span>
          </el-tooltip>
        </template>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_or_not" :value="scope.row.isSys" />
        </template>
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:tacics:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.isSys != 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-base:tacics:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改策略配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="策略编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入策略编码" />
        </el-form-item>
        <el-form-item label="策略名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入策略名称" />
        </el-form-item>
        <el-form-item label="是否匹配abc" prop="isAbc">
          <el-select v-model="form.isAbc" placeholder="请选择是否匹配abc">
            <el-option v-for="dict in dict.type.is_or_not" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="巷道策略" prop="lineBalanceType">
          <el-select v-model="form.lineBalanceType" placeholder="请选择巷道策略">
            <el-option v-for="dict in dict.type.line_balance_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="X向策略" prop="xType">
          <el-select v-model="form.xType" placeholder="请选择X向策略">
            <el-option v-for="dict in dict.type.xyz_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Y向策略" prop="yType">
          <el-select v-model="form.yType" placeholder="请选择Y向策略">
            <el-option v-for="dict in dict.type.xyz_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Z向策略" prop="zType">
          <el-select v-model="form.zType" placeholder="请选择Z向策略">
            <el-option v-for="dict in dict.type.xyz_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
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
import { listTacics, getTacics, delTacics, addTacics, updateTacics } from "@/api/wcs-base/tacics";

export default {
  name: "Tacics",
  dicts: ['xyz_type', 'is_or_not', 'line_balance_type'],
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
      // 策略配置表格数据
      tacicsList: [],
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
        isAbc: null,
        lineBalanceType: null,
        xType: null,
        yType: null,
        zType: null,
        isSys: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [{ required: true, message: "策略编码不能为空", trigger: "blur" }],
        name: [{ required: true, message: "策略名称不能为空", trigger: "blur" }],
        isAbc: [{ required: true, message: "是否根据ABC频率划分不能为空", trigger: "blur" }],
        lineBalanceType: [{ required: true, message: "巷道分配策略不能为空", trigger: "blur" }],
        xType: [{ required: true, message: "X向策略不能为空", trigger: "blur" }],
        yType: [{ required: true, message: "Y向策略不能为空", trigger: "blur" }],
        zType: [{ required: true, message: "Z向策略不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询策略配置列表 */
    getList() {
      this.loading = true;
      listTacics(this.queryParams).then(response => {
        if (response.code == 200) {
          this.tacicsList = response.rows;
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
        isAbc: null,
        lineBalanceType: null,
        xType: null,
        yType: null,
        zType: null,
        isSys: null
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
      this.title = "添加策略配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getTacics(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改策略配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTacics(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addTacics(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除策略配置编号为"' + ids + '"的数据项？').then(function () {
        return delTacics(ids); s
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.getList();
          this.$modal.msgError(response.msg || "删除失败");
        }

      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/tacics/export', {
        ...this.queryParams
      }, `tacics_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
