<template>
  <div class="app-container">
    <el-card>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="仓库编码" prop="code">
          <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
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
    </el-card>

    <el-card>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <!-- <router-link :to="'WareInfo/wareModel/0'" class="link-type">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" v-hasPermi="['wcs-base:WareInfo:add']">
            <span>新增</span>
          </el-button>
        </router-link> -->
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
            v-hasPermi="['wcs-base:WareInfo:add']">新增</el-button>
        </el-col>
        <!-- <el-col :span="1.5">
        <router-link :to="'WareInfo/wareModel/' + ids" class="link-type">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single"
            v-hasPermi="['wcs-base:WareInfo:edit']">
            <span>修改</span>
          </el-button>
        </router-link>
      </el-col> -->
        <el-col :span="1.5">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdateModel"
            v-hasPermi="['wcs-base:WareInfo:edit']">流程设计</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
            v-hasPermi="['wcs-base:WareInfo:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
            v-hasPermi="['wcs-base:WareInfo:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
            v-hasPermi="['wcs-base:WareInfo:export']">导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="WareInfoList" @selection-change="handleSelectionChange" border>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" min-width="100" />
        <el-table-column label="编码" align="center" prop="code" min-width="120">
        </el-table-column>
        <el-table-column label="名称" align="center" prop="name" min-width="150">
        </el-table-column>
        <el-table-column label="类型" align="center" prop="type" min-width="150">
          <template slot-scope="scope">
            <span v-if="scope.row.type == 1">堆垛机仓库</span>
            <span v-else-if="scope.row.type == 2">四向车仓库</span>
          </template>
        </el-table-column>
        <el-table-column label="上架策略" align="center" prop="inCellTactics" min-width="120">
          <template slot-scope="scope">
            <span v-for="item in tacicses" v-if="scope.row.inCellTactics == item.code">{{ item.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" min-width="180">
        </el-table-column>
        <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
        </el-table-column>
        <el-table-column label="更新时间" align="center" prop="updateTime" min-width="180">
        </el-table-column>

        <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">
        </el-table-column>

        <el-table-column label="删除标志" align="center" prop="isDelete" min-width="120">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.del_flag" :value="scope.row.isDelete" />
          </template>
        </el-table-column>

        <!-- <el-table-column label="是否禁用" align="center" prop="disableState" min-width="120">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.disable_state" :value="scope.row.disableState" />
          </template>
        </el-table-column> -->

        <el-table-column label="测试模式" align="center" prop="isTest" min-width="130">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.isTest == 0 ? '已关闭' : '已打开'" placement="top">
              <el-switch v-model="scope.row.isTest" @change="updateWareInfoIsTest(scope.row)" inactive-color="#13ce66"
                active-text="on" inactive-text="off" active-color="#ff4949" active-value="1" inactive-value="0">
              </el-switch>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="120" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['wcs-base:WareInfo:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.isDelete == 0"
              @click="handleDelete(scope.row)" v-hasPermi="['wcs-base:WareInfo:remove']">删除</el-button>
            <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.isDelete == 1"
              @click="handleRecover(scope.row)" v-hasPermi="['wcs-base:WareInfo:recover']">恢复</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList" />
    </el-card>

    <!-- 添加或修改仓库设置对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="堆垛机仓库" :value="1"></el-option>
            <el-option label="四向车仓库" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="底图路径" prop="backgroundImg">
          <el-input v-model="form.backgroundImg" placeholder="底图路径" />
        </el-form-item>
        <el-form-item label="上架策略" prop="inCellTactics">
          <el-select v-model="form.inCellTactics" placeholder="请选择上架策略">
            <el-option v-for="dict in tacicses" :key="dict.code" :label="dict.name" :value="dict.code"></el-option>
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
import {
  listWareInfo,
  getWareInfo,
  delWareInfo,
  addWareInfo,
  updateWareInfo,
} from "@/api/wcs-base/WareInfo";

export default {
  name: "WareInfo",
  dicts: ["del_flag", "disable_state"],
  data() {
    return {
      tacicses: [],
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
      // 仓库设置表格数据
      WareInfoList: [],
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
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: '0',
        disableState: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  created() {
    console.info("fasdfasfsdaf");
    this.getList();
    this.getAllMessage();
  },
  methods: {
    getAllMessage() {
      this.getTacics();
    },
    getTacics() {
      listTacics({ pageSize: 999 }).then(response => {
        if (response.code == 200) {
          this.tacicses = response.rows
        }
      })
    },
    /**
     * 修改仓库测试模式
     */
    updateWareInfoIsTest(object) {
      var that = this;
      // that.$modal.loading("正在切换模式，请稍等")
      updateWareInfo(object).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.getList();
        } else {
          this.$modal.msgError(response.msg || "修改失败");
        }
      });
      // that.$modal.closeLoading()
    },
    /** 查询仓库设置列表 */
    getList() {
      this.loading = true;
      listWareInfo(this.queryParams).then((response) => {
        this.WareInfoList = response.rows;
        this.total = response.total;
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
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
        disableState: null,
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
      this.title = "添加仓库设置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getWareInfo(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改仓库设置";
      });
    },
    /** 修改按钮操作 */
    handleUpdateModel(row) {
      this.reset();
      const id = row.id || this.ids;
      this.$router.push({ path: "/base/WareInfo/wareModel/" + id });
      // getWareInfo(id).then(response => {
      //   this.form = response.data;
      //   this.open = true;
      //   this.title = "修改仓库设置";
      // });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateWareInfo(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败")
              }
            });
          } else {
            addWareInfo(this.form).then((response) => {
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
    /** 恢复按钮操作 */
    handleRecover(row) {
      const id = row.id;
      this.$modal.confirm('是否确认恢复ID为"' + id + '"的数据项？').then(function () {
        return getWareInfo(id)
      }).then((response) => {
        var info = response.data
        if (info == null) {
          this.$modal.msgError("选择数据项有误！")
          return;
        }
        info.isDelete = 0
        updateWareInfo(info).then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("恢复成功");
          } else {
            this.$modal.msgError(response.msg || "恢复失败")
          }
        })
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除仓库设置编号为"' + ids + '"的数据项？')
        .then(function () {
          return delWareInfo(ids);
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
        "wcs-base/WareInfo/export",
        {
          ...this.queryParams,
        },
        `WareInfo_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
