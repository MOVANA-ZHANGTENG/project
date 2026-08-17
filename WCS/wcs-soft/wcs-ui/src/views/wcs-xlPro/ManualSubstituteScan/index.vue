<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="100px">
      <el-form-item label="产品型号" prop="modelId">
        <el-select v-model="queryParams.modelId" placeholder="请选择产品型号">
            <el-option
              v-for="dict in modelAll"
              :key="dict.modelId"
              :label="dict.modelName"
              :value="dict.modelId"
            ></el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="托盘编码" prop="palletCode">
        <el-input
          v-model="queryParams.palletCode"
          placeholder="请输入托盘编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="站台" prop="positionId">
        <el-select v-model="queryParams.positionId" placeholder="请选择站台" clearable>
          <el-option v-for="item in positionAll" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
          <el-option
            v-for="dict in types"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择类型" clearable>
          <el-option
            v-for="dict in states"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
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
          v-hasPermi="['wcs-xlPro:ManualSubstituteScan:add']"
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
          v-hasPermi="['wcs-xlPro:ManualSubstituteScan:edit']"
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
          v-hasPermi="['wcs-xlPro:ManualSubstituteScan:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:ManualSubstituteScan:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ManualSubstituteScanList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
      <el-table-column label="产品型号" align="center" prop="modelId"  min-width="120">
        <template slot-scope="scope">
          <!-- 遍历positionAll，匹配id与当前行的positionId -->
          <span v-for="item in modelAll" :key="item.id">
            <span v-if="item.id === scope.row.modelId">{{ item.modelName }}</span>
          </span>
          <!-- 无匹配时显示默认值（可选） -->
          <span v-if="!modelAll.some(item => item.id === scope.row.modelId)">
            无匹配站台
          </span>
        </template>
    </el-table-column>
    <el-table-column label="托盘编码" align="center" prop="palletCode"  min-width="120">
    </el-table-column>
    <el-table-column label="位置编码" align="center" prop="positionId"  min-width="120">
      <template slot-scope="scope">
          <!-- 遍历positionAll，匹配id与当前行的positionId -->
          <span v-for="item in positionAll" :key="item.id">
            <span v-if="item.id === scope.row.positionId">{{ item.code }}</span>
          </span>
          <!-- 无匹配时显示默认值（可选） -->
          <span v-if="!positionAll.some(item => item.id === scope.row.positionId)">
            无匹配站台
          </span>
        </template>
    </el-table-column>
    <el-table-column label="类型" align="center" prop="type"  min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.type == 0">空托盘</div>
          <div v-if="scope.row.type == 1">满托盘</div>
        </template>
    </el-table-column>
    <el-table-column label="状态" align="center" prop="state"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.state == 0" style="color: #F56C6C;">未通知PLC</div>
          <div v-if="scope.row.state == 1" style="color: #67C23A;">已通知PLC</div>
      </template>
    </el-table-column>
    <el-table-column label="是否离开站台" align="center" prop="leaveState"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.leaveState == 0" style="color: #F56C6C;">未离开</div>
          <div v-if="scope.row.leaveState == 1" style="color: orange;">已离开待处理</div>
          <div v-if="scope.row.leaveState == 2" style="color: #67C23A;">已处理</div>
        </template>
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:ManualSubstituteScan:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:ManualSubstituteScan:remove']"
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

    <!-- 添加或修改手动叉料对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="产品型号" prop="modelId">
          <el-select v-model="form.modelId" placeholder="请选择产品型号">
            <el-option
              v-for="dict in modelAll"
              :key="dict.id"
              :label="dict.modelName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="托盘编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘编码" />
        </el-form-item>
        <el-form-item label="站台" prop="positionId">
          <el-select v-model="form.positionId" placeholder="请选择站台" clearable>
            <el-option v-for="item in positionAll" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option
              v-for="dict in types"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择状态">
            <el-option
              v-for="dict in states"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="是否离开站台" prop="leaveState">
          <el-select v-model="form.leaveState" placeholder="请选择是否离开站台" clearable>
            <el-option
              v-for="dict in leaveStates"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
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
import { listManualSubstituteScan, getManualSubstituteScan, delManualSubstituteScan, addManualSubstituteScan, updateManualSubstituteScan } from "@/api/wcs-xlPro/ManualSubstituteScan";
import { findPositionAll } from "@/api/wcs-xlPro/PositionInfoExtend";
import { findModelAll } from "@/api/wcs-xlPro/ProcessRoute";
import request from "@/utils/request";
export default {
  name: "ManualSubstituteScan",
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
      // 手动叉料表格数据
      ManualSubstituteScanList: [],
      positionAll: [],
      modelAll: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        modelId: null,
        palletCode: null,
        positionId: null,
        type: null,
        state: null,
        leaveState: 0,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      states: [
        { value: 0, label: "未通知PLC" },
        { value: 1, label: "已通知PLC" },
      ],
      types: [
        { value: 0, label: "空托盘" },
        { value: 1, label: "满托盘" },
      ],
      leaveStates: [
        // { value: 0, label: "未离开" },
        { value: 1, label: "已离开待处理" },
        // { value: 2, label: "已处理" },
      ],
    };
  },
  created() {
    this.getList();
    this.getPositionAll();
    this.getModelAll();
  },
  methods: {
    //查询所有的产品型号
    getModelAll(){
      findModelAll().then(response => {
          if(response.code==200){
            this.modelAll = response.data;
          }
      });
    },
    //查询所有的站台编码
    getPositionAll(){
      findPositionAll().then(response => {
          if(response.code==200){
            this.positionAll = response.data;
          }
      });
    },
    /** 查询手动叉料列表 */
    getList() {
      this.loading = true;
      listManualSubstituteScan(this.queryParams).then(response => {
          if(response.code==200){
            this.ManualSubstituteScanList = response.rows;
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
        modelId: null,
        palletCode: null,
        positionId: null,
        type: null,
        state: null,
        createTime: null
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
      this.title = "添加手动叉料";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getManualSubstituteScan(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改手动叉料";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateManualSubstituteScan(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addManualSubstituteScan(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
              }else{
                this.$modal.msgError(response.msg||"新增失败");
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除手动叉料编号为"' + ids + '"的数据项？').then(function() {
        return delManualSubstituteScan(ids);
      }).then((response) => {
          if(response.code==200){
            this.getList();
            this.$modal.msgSuccess("删除成功");
          }else{
            this.$modal.msgError(response.msg||"删除失败");
          }

      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-xlPro/ManualSubstituteScan/export', {
        ...this.queryParams
      }, `ManualSubstituteScan_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
