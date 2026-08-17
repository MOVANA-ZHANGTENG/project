<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" @change="getAreaInfos(queryParams.wareCode)" placeholder="仓库"
          clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="扫描值" prop="value">
        <el-input v-model="queryParams.value" placeholder="请输入扫描值" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="位置编码" prop="positionCode">
        <el-input v-model="queryParams.positionCode" placeholder="请输入位置编码" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="执行器" prop="handId">
        <el-select v-model="form.handId" placeholder="请选择" clearable>
          <el-option v-for="item in callBoxCmdList" :key="item.id" :label="item.name" :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择事件处理状态" clearable>
          <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>

      </el-form-item>
      <el-form-item label="请求状态" prop="isNext">
        <el-select v-model="queryParams.isNext" placeholder="请选择是否请求目的地" clearable>
          <el-option v-for="item in isNexts" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="删除标志" prop="delFlag">
        <el-select v-model="queryParams.delFlag" placeholder="请选择删除标志" clearable>
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
          v-hasPermi="['wcs-task:smqInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-task:smqInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-task:smqInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-task:smqInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="smqInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="150">
      </el-table-column>
      <el-table-column label="位置编码" align="center" prop="positionCode" min-width="120">
      </el-table-column>
      <el-table-column label="扫描值" align="center" prop="value" min-width="120">
      </el-table-column>
      <el-table-column label="请求目的地" align="center" prop="isNext" min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.isNext == 0">不请求目的地</div>
          <div v-if="scope.row.isNext == 1">请求目的地</div>
        </template>
      </el-table-column>
      <el-table-column label="执行命令" align="center" prop="handleName" min-width="180">
      </el-table-column>
      <el-table-column label="事件处理状态" align="center" prop="state" min-width="120">
        <template slot-scope="scope">
          <div v-if="scope.row.state == 0" style="color: #F56C6C;">未处理</div>
          <div v-if="scope.row.state == 1" style="color: #67C23A;">已处理</div>
        </template>
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="delFlag" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.delFlag" />
        </template>
      </el-table-column>

      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:smqInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.delFlag == 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-task:smqInfo:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.delFlag == 1"
            @click="handleRecover(scope.row)">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改扫码器对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="仓库" prop="wareCode">
          <el-select v-model="form.wareCode" placeholder="仓库" clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置编码" prop="positionCode">
          <el-input v-model="form.positionCode" placeholder="请输入位置编码" />
        </el-form-item>
        <el-form-item label="扫描值" prop="value">
          <el-input v-model="form.value" placeholder="请输入扫描值" />
        </el-form-item>
        <el-form-item label="执行器" prop="handId">
          <el-select v-model="form.handId" placeholder="请选择" clearable>
            <el-option v-for="item in callBoxCmdList" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="事件状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择" clearable>
            <el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="请求目的地" prop="isNext">
          <el-select v-model="form.isNext" placeholder="请选择" clearable>
            <el-option v-for="item in isNexts" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
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
import { listSmqInfo, getSmqInfo, delSmqInfo, addSmqInfo, updateSmqInfo } from "@/api/wcs-task/smqInfo";
import request from "@/utils/request.js";
import { listWareInfo } from "@/api/wcs-base/WareInfo";
export default {
  name: "SmqInfo",
  dicts:['del_flag'],
  data() {
    return {
      callBoxCmdList: [],
      wareInfos: [],
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
      // 扫码器表格数据
      smqInfoList: [],
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
        value: null,
        handId: null,
        state: null,
        isNext: null,
        positionCode: null,
        wareCode: null,
        delFlag:'0',
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [{ required: true, message: "编码不能为空", trigger: "blur" }],
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        positionCode: [{ required: true, message: "位置编码不能为空", trigger: "blur" }],
        wareCode: [
          { required: true, message: "仓库不能为空", trigger: "blur" },
        ],
      },
      states: [
        { value: 0, label: "未处理" },
        { value: 1, label: "已处理" },
      ],
      isNexts: [
        { value: 0, label: "不请求目的地" },
        { value: 1, label: "请求目的地" },
      ],
    };
  },
  created() {
    this.getList();
    this.getSmqCmdList();
    this.getWareInfos();
  },
  methods: {

    //获取所有仓库
    getWareInfos() {
      listWareInfo({ isDelete: 0 }).then((response) => {
        if (response.code == 200) {
          this.wareInfos = response.rows;
        }
      });
    },

    //查询所有扫描器的执行器
    getSmqCmdList() {
      var that = this;
      request({
        url: "/wcs-task/smqInfo/getSmqHandle",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          var data = response.data;

          for (var i = 0; i < data.length; i++) {

            //console.log(data[i]);
            that.callBoxCmdList.push(data[i]);
            // console.log(that.callBoxCmdList);
          }
        }
      });
    },

    /** 查询扫码器列表 */
    getList() {
      this.loading = true;
      listSmqInfo(this.queryParams).then(response => {
        if (response.code == 200) {
          this.smqInfoList = response.rows;
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
        value: null,
        handId: null,
        positionCode: null,
        wareCode: null,
        state: null,
        isNext: null,
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
      this.title = "添加扫码器";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getSmqInfo(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改扫码器";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSmqInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addSmqInfo(this.form).then(response => {
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
    /** 恢复按钮操作 */
    handleRecover(row) {
      const id = row.id;
      this.$modal.confirm('是否确认恢复ID为"' + id + '"的数据项？').then(function () {
        return getSmqInfo(id)
      }).then((response) => {
        var info = response.data
        if (info == null) {
          this.$modal.msgError("选择数据项有误！")
          return;
        }
        info.isDelete = 0
        updateTaskType(info).then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("恢复成功");
          }else{
            this.$modal.msgError(response.msg||"恢复失败")
          }
        })
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除扫码器编号为"' + ids + '"的数据项？').then(function () {
        return delSmqInfo(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }else{
          this.$modal.msgError(response.msg || "删除失败");
        }
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-task/smqInfo/export', {
        ...this.queryParams
      }, `smqInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
