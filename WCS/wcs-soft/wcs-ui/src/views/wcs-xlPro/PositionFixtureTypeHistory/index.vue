<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="120px">
      <el-form-item label="站台ID" prop="positionId">
       <el-select v-model="queryParams.positionId" placeholder="站台id" clearable>
          <el-option v-for="item in positionAll" :key="item.id" :label="item.code" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="夹具类型ID" prop="fixtureTypeId">
        <el-select v-model="queryParams.fixtureTypeId" placeholder="请选择夹具类型ID" clearable>
         <el-option
              v-for="dict in fixtureTypeAll"
              :key="dict.id"
              :label="dict.typeName"
              :value="dict.id"
            ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="请选择变更类型" clearable>
          <el-option
            v-for="item in changeTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="更换时间" prop="changeTime">
        <el-input
          v-model="queryParams.changeTime"
          placeholder="请输入更换时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人ID" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人姓名" prop="operatorName">
        <el-input
          v-model="queryParams.operatorName"
          placeholder="请输入操作人姓名"
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
          v-hasPermi="['wcs-xlPro:PositionFixtureTypeHistory:add']"
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
          v-hasPermi="['wcs-xlPro:PositionFixtureTypeHistory:edit']"
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
          v-hasPermi="['wcs-xlPro:PositionFixtureTypeHistory:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:PositionFixtureTypeHistory:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="PositionFixtureTypeHistoryList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="站台ID" align="center" prop="positionId"  min-width="120">
      <template slot-scope="scope">
        <!-- 遍历positionAll，匹配id与当前行的positionId -->
        <span v-for="item in positionAll" :key="item.id">
          <span v-if="item.id === scope.row.positionId">{{ item.name }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <span v-if="!positionAll.some(item => item.id === scope.row.positionId)">
          无匹配站台
        </span>
      </template>
    </el-table-column>
    <el-table-column label="夹具类型ID" align="center" prop="fixtureTypeId"  min-width="120">
       <template slot-scope="scope">
        <!-- 遍历positionAll，匹配id与当前行的positionId -->
        <span v-for="item in fixtureTypeAll" :key="item.id">
          <span v-if="item.id === scope.row.fixtureTypeId">{{ item.typeName }}</span>
        </span>
        <!-- 无匹配时显示默认值（可选） -->
        <span v-if="!fixtureTypeAll.some(item => item.id === scope.row.fixtureTypeId)">
          无匹配站台
        </span>
      </template>
    </el-table-column>
      <el-table-column label="变更类型" align="center" prop="changeType"  min-width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.changeType === 0">安装</span>
          <span v-else-if="scope.row.changeType === 1">拆卸</span>
          <span v-else>{{ scope.row.changeType }}</span>
        </template>
      </el-table-column>
    <el-table-column label="更换时间" align="center" prop="changeTime"  min-width="120">
    </el-table-column>
    <el-table-column label="操作人ID" align="center" prop="operatorId"  min-width="120">
    </el-table-column>
    <el-table-column label="操作人姓名" align="center" prop="operatorName"  min-width="120">
    </el-table-column>
    <el-table-column label="备注" align="center" prop="remark"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:PositionFixtureTypeHistory:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:PositionFixtureTypeHistory:remove']"
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

    <!-- 添加或修改站台夹具类型历史对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="站台ID" prop="positionId">
          <el-select v-model="form.positionId" placeholder="请选择站台ID">
            <el-option
              v-for="item in positionAll"
              :key="item.id"
              :label="item.code"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="夹具类型ID" prop="fixtureTypeId">
          <el-select v-model="form.fixtureTypeId" placeholder="请选择夹具类型ID">
             <el-option
              v-for="dict in fixtureTypeAll"
              :key="dict.id"
              :label="dict.typeName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="变更类型" prop="changeType">
          <el-select v-model="form.changeType" placeholder="请选择变更类型">
            <el-option
              v-for="item in changeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="更换时间" prop="changeTime">
          <el-input v-model="form.changeTime" placeholder="请输入更换时间" />
        </el-form-item>
        <el-form-item label="操作人ID" prop="operatorId">
          <el-input v-model="form.operatorId" placeholder="请输入操作人ID" />
        </el-form-item>
        <el-form-item label="操作人姓名" prop="operatorName">
          <el-input v-model="form.operatorName" placeholder="请输入操作人姓名" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
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
import { listPositionFixtureTypeHistory, getPositionFixtureTypeHistory, delPositionFixtureTypeHistory, addPositionFixtureTypeHistory, updatePositionFixtureTypeHistory } from "@/api/wcs-xlPro/PositionFixtureTypeHistory";
import { findPositionAll,findFixtureTypeAll } from "@/api/wcs-xlPro/PositionInfoExtend";
export default {
  name: "PositionFixtureTypeHistory",
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
      // 站台夹具类型历史表格数据
      PositionFixtureTypeHistoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        positionId: null,
        fixtureTypeId: null,
        changeType: null,
        changeTime: null,
        operatorId: null,
        operatorName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 站台列表
      positionAll: [],
      // 夹具类型列表
      fixtureTypeAll: [],
      // 变更类型选项
      changeTypeOptions: [
        { label: "安装", value: 0 },
        { label: "拆卸", value: 1 }
      ]
    };
  },
  created() {
    this.getList();
    this.getPositionAll();
    this.getFixtureTypeAll();
  },
  methods: {
      /** 获取站台列表 */
      getPositionAll() {
        findPositionAll().then(response => {
          if (response.code == 200) {
            this.positionAll = response.data;
          }
        });
      },
      /** 获取夹具类型列表 */
      getFixtureTypeAll() {
        findFixtureTypeAll().then(response => {
          if (response.code == 200) {
            this.fixtureTypeAll = response.data;
          }
        });
      },
      /** 查询站台夹具类型历史列表 */
      getList() {
      this.loading = true;
      listPositionFixtureTypeHistory(this.queryParams).then(response => {
          if(response.code==200){
            this.PositionFixtureTypeHistoryList = response.rows;
            this.total = response.total;
            this.getPositionAll();
            this.getFixtureTypeAll();
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
        positionId: null,
        fixtureTypeId: null,
        changeType: null,
        changeTime: null,
        operatorId: null,
        operatorName: null,
        remark: null,
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
      this.title = "添加站台夹具类型历史";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionFixtureTypeHistory(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改站台夹具类型历史";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePositionFixtureTypeHistory(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addPositionFixtureTypeHistory(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除站台夹具类型历史编号为"' + ids + '"的数据项？').then(function() {
        return delPositionFixtureTypeHistory(ids);
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
      this.download('wcs-xlPro/PositionFixtureTypeHistory/export', {
        ...this.queryParams
      }, `PositionFixtureTypeHistory_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
