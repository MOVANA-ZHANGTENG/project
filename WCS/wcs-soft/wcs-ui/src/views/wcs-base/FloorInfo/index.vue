<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
      label-width="68px">
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" placeholder="仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="库区编码" prop="areaCode">
        <el-input v-model="queryParams.areaCode" placeholder="请输入库区编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="楼层Z" prop="z">
        <el-input v-model="queryParams.z" placeholder="请输入楼层Z" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="最大排数" prop="totalX">
        <el-input v-model="queryParams.totalX" placeholder="请输入最大排数" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="最大列数" prop="totalY">
        <el-input v-model="queryParams.totalY" placeholder="请输入最大列数" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="层总面积" prop="xy">
        <el-input v-model="queryParams.xy" placeholder="请输入层总面积" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="禁用状态" prop="disableState">
        <el-input v-model="queryParams.disableState" placeholder="请输入禁用状态" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:FloorInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:FloorInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:FloorInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:FloorInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="FloorInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="120">
      </el-table-column>
      <el-table-column label="库区编码" align="center" prop="areaCode" min-width="120">
      </el-table-column>
      <el-table-column label="楼层Z" align="center" prop="z" min-width="120">
      </el-table-column>
      <el-table-column label="最大排数" align="center" prop="totalX" min-width="120">
      </el-table-column>
      <el-table-column label="最大列数" align="center" prop="totalY" min-width="120">
      </el-table-column>
      <el-table-column label="层总面积" align="center" prop="xy" min-width="120">
      </el-table-column>
      <el-table-column label="禁用状态" align="center" prop="disableState" min-width="120">
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:FloorInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:FloorInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改层对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="仓库编码" prop="wareCode">
          <el-input v-model="form.wareCode" placeholder="请输入仓库编码" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="wareName">
          <el-input v-model="form.wareName" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="库区编码" prop="areaCode">
          <el-input v-model="form.areaCode" placeholder="请输入库区编码" />
        </el-form-item>
        <el-form-item label="楼层Z" prop="z">
          <el-input v-model="form.z" placeholder="请输入楼层Z" />
        </el-form-item>
        <el-form-item label="最大排数" prop="totalX">
          <el-input v-model="form.totalX" placeholder="请输入最大排数" />
        </el-form-item>
        <el-form-item label="最大列数" prop="totalY">
          <el-input v-model="form.totalY" placeholder="请输入最大列数" />
        </el-form-item>
        <el-form-item label="层总面积" prop="xy">
          <el-input v-model="form.xy" placeholder="请输入层总面积" />
        </el-form-item>
        <el-form-item label="禁用状态" prop="disableState">
          <el-input v-model="form.disableState" placeholder="请输入禁用状态" />
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
import { listFloorInfo, getFloorInfo, delFloorInfo, addFloorInfo, updateFloorInfo } from "@/api/wcs-base/FloorInfo";
import request from "@/utils/request";
export default {
  name: "FloorInfo",
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
      // 层表格数据
      FloorInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 所有仓库
      wareInfos: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wareCode: null,
        wareName: null,
        areaCode: null,
        z: null,
        disableState: null,
        createUserId: null,
        createUserName: null,
        totalX: null,
        totalY: null,
        xy: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getWareInfos();
    this.getList();
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
    /** 查询层列表 */
    getList() {
      this.loading = true;
      listFloorInfo(this.queryParams).then(response => {
        if (response.code == 200) {
          this.FloorInfoList = response.rows;
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
        wareCode: null,
        wareName: null,
        areaCode: null,
        z: null,
        disableState: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        totalX: null,
        totalY: null,
        xy: null
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
      this.title = "添加层";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getFloorInfo(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改层";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFloorInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addFloorInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除层编号为"' + ids + '"的数据项？').then(function () {
        return delFloorInfo(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg || "删除失败");
        }

      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/FloorInfo/export', {
        ...this.queryParams
      }, `FloorInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
