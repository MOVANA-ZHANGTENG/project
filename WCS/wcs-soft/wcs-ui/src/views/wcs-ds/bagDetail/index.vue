<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成分计数" prop="tolMatCode">
        <el-input
          v-model="queryParams.tolMatCode"
          placeholder="请输入成分计数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成分编码" prop="matCode">
        <el-input
          v-model="queryParams.matCode"
          placeholder="请输入成分编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成分批次" prop="matLot">
        <el-input
          v-model="queryParams.matLot"
          placeholder="请输入成分批次"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成分名称
" prop="matName">
        <el-input
          v-model="queryParams.matName"
          placeholder="请输入成分名称
"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="称量公差" prop="tolerance">
        <el-input
          v-model="queryParams.tolerance"
          placeholder="请输入称量公差"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开料用量
" prop="setWeight">
        <el-input
          v-model="queryParams.setWeight"
          placeholder="请输入开料用量
"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实际称量" prop="actWeight">
        <el-input
          v-model="queryParams.actWeight"
          placeholder="请输入实际称量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开始时间" prop="matStartTime">
        <el-date-picker clearable
          v-model="queryParams.matStartTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="matEndTime">
        <el-date-picker clearable
          v-model="queryParams.matEndTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择结束时间">
        </el-date-picker>
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
          v-hasPermi="['wcs-ds:bagDetail:add']"
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
          v-hasPermi="['wcs-ds:bagDetail:edit']"
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
          v-hasPermi="['wcs-ds:bagDetail:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-ds:bagDetail:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bagDetailList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="mOrderId" min-width="100" />
    <el-table-column label="成分计数" align="center" prop="tolMatCode"  min-width="120">
    </el-table-column>
    <el-table-column label="成分编码" align="center" prop="matCode"  min-width="120">
    </el-table-column>
    <el-table-column label="成分批次" align="center" prop="matLot"  min-width="120">
    </el-table-column>
    <el-table-column label="成分名称
" align="center" prop="matName"  min-width="120">
    </el-table-column>
    <el-table-column label="称量公差" align="center" prop="tolerance"  min-width="120">
    </el-table-column>
    <el-table-column label="开料用量
" align="center" prop="setWeight"  min-width="120">
    </el-table-column>
    <el-table-column label="实际称量" align="center" prop="actWeight"  min-width="120">
    </el-table-column>
      <el-table-column label="开始时间" align="center" prop="matStartTime" width="180">

      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="matEndTime" width="180">

      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds:bagDetail:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds:bagDetail:remove']"
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

    <!-- 添加或修改料袋详情对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="成分计数" prop="tolMatCode">
          <el-input v-model="form.tolMatCode" placeholder="请输入成分计数" />
        </el-form-item>
        <el-form-item label="成分编码" prop="matCode">
          <el-input v-model="form.matCode" placeholder="请输入成分编码" />
        </el-form-item>
        <el-form-item label="成分批次" prop="matLot">
          <el-input v-model="form.matLot" placeholder="请输入成分批次" />
        </el-form-item>
        <el-form-item label="成分名称
" prop="matName">
          <el-input v-model="form.matName" placeholder="请输入成分名称
" />
        </el-form-item>
        <el-form-item label="称量公差" prop="tolerance">
          <el-input v-model="form.tolerance" placeholder="请输入称量公差" />
        </el-form-item>
        <el-form-item label="开料用量
" prop="setWeight">
          <el-input v-model="form.setWeight" placeholder="请输入开料用量
" />
        </el-form-item>
        <el-form-item label="实际称量" prop="actWeight">
          <el-input v-model="form.actWeight" placeholder="请输入实际称量" />
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
import { listBagDetail, getBagDetail, delBagDetail, addBagDetail, updateBagDetail } from "@/api/wcs-ds/bagDetail";

export default {
  name: "BagDetail",
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
      // 料袋详情表格数据
      bagDetailList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tolMatCode: null,
        matCode: null,
        matLot: null,
        matName: null,
        tolerance: null,
        setWeight: null,
        actWeight: null,
        matStartTime: null,
        matEndTime: null
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
    /** 查询料袋详情列表 */
    getList() {
      this.loading = true;
      listBagDetail(this.queryParams).then(response => {
          if(response.code==200){
            this.bagDetailList = response.rows;
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
        mOrderId: null,
        tolMatCode: null,
        matCode: null,
        matLot: null,
        matName: null,
        tolerance: null,
        setWeight: null,
        actWeight: null,
        matStartTime: null,
        matEndTime: null
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
      this.ids = selection.map(item => item.mOrderId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加料袋详情";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const mOrderId = row.mOrderId || this.ids
      getBagDetail(mOrderId).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改料袋详情";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.mOrderId != null) {
            updateBagDetail(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addBagDetail(this.form).then(response => {
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
      const mOrderIds = row.mOrderId || this.ids;
      this.$modal.confirm('是否确认删除料袋详情编号为"' + mOrderIds + '"的数据项？').then(function() {
        return delBagDetail(mOrderIds);
      }).then((response) => {
        if(response.code==200){
        this.getList();
        this.$modal.msgSuccess("删除成功");
        }else{
          this.$modal.msgError(response.msg||"删除失败")
        }
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-ds/bagDetail/export', {
        ...this.queryParams
      }, `bagDetail_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
