<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="工序编码" prop="work">
        <el-select v-model="queryParams.work" placeholder="请选择" clearable>
          <el-option v-for="item in works" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="阴/阳" prop="mark">
        <el-select v-model="queryParams.mark" placeholder="请选择" clearable>
          <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="下一道工序编码" prop="nextWork">
        <el-select v-model="queryParams.nextWork" placeholder="请选择" clearable>
          <el-option v-for="item in nextWorks" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="工序描述" prop="message">
        <el-input
          v-model="queryParams.message"
          placeholder="请输入工序描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['lg1:work:add']"
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
          v-hasPermi="['lg1:work:edit']"
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
          v-hasPermi="['lg1:work:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['lg1:work:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="workList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="工序编码" align="center" prop="work"  min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.work == 1">Coater</div>
          <div v-if="scope.row.work == 2">RollerPress</div>
          <div v-if="scope.row.work == 3">Rewinder</div>
          <div v-if="scope.row.work == 4">Slitterr</div>
          <div v-if="scope.row.work == 5">无工序</div>
        </template>
    </el-table-column>
    <el-table-column label="阴/阳" align="center" prop="mark"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.mark == 1">阴</div>
          <div v-if="scope.row.mark == 2">阳</div>
        </template>
    </el-table-column>
    <el-table-column label="下一道工序编码" align="center" prop="nextWork"  min-width="120">
      <template slot-scope="scope">
          <div v-if="scope.row.nextWork == 1">Coater</div>
          <div v-if="scope.row.nextWork == 2">RollerPress</div>
          <div v-if="scope.row.nextWork == 3">Rewinder</div>
          <div v-if="scope.row.nextWork == 4">Slitterr</div>
          <div v-if="scope.row.nextWork == 5">无工序</div>
        </template>
    </el-table-column>
    <el-table-column label="工序描述" align="center" prop="message"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['lg1:work:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['lg1:work:remove']"
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

    <!-- 添加或修改工序信息对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工序编码" prop="work">
          <el-select v-model="form.work" placeholder="请选择" clearable>
            <el-option v-for="item in works" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="阴/阳" prop="mark">
          <el-select v-model="form.mark" placeholder="请选择" clearable>
            <el-option v-for="item in marks" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="下一道工序编码" prop="nextWork">
          <el-select v-model="form.nextWork" placeholder="请选择" clearable>
            <el-option v-for="item in nextWorks" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工序描述" prop="message">
          <el-input v-model="form.message" placeholder="请输入工序描述" />
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
import { listWork, getWork, delWork, addWork, updateWork } from "@/api/lg1/work";

export default {
  name: "Work",
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
      // 工序信息表格数据
      workList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        work: null,
        mark: null,
        nextWork: null,
        message: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      marks: [
        { value: 1, label: "阴" },
        { value: 2, label: "阳" },
      ],
      works: [
        { value: 1, label: "Coater" },
        { value: 2, label: "RollerPress" },
        { value: 3, label: "Rewinder" },
        { value: 4, label: "Slitterr" },
        { value: 5, label: "无工序" },
      ],
      nextWorks: [
        { value: 1, label: "Coater" },
        { value: 2, label: "RollerPress" },
        { value: 3, label: "Rewinder" },
        { value: 4, label: "Slitterr" },
        { value: 5, label: "无工序" },
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询工序信息列表 */
    getList() {
      this.loading = true;
      listWork(this.queryParams).then(response => {
          if(response.code==200){
            this.workList = response.rows;
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
        work: null,
        mark: null,
        nextWork: null,
        message: null,
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
      this.title = "添加工序信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getWork(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改工序信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateWork(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg);
              }
            });
          } else {
            addWork(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除工序信息编号为"' + ids + '"的数据项？').then(function() {
        return delWork(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('lg1/work/export', {
        ...this.queryParams
      }, `work_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
