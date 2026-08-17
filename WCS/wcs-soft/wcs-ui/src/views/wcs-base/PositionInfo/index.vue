<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode" placeholder="仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="下位编码" prop="subCode">
        <el-input v-model="queryParams.subCode" placeholder="请输入下位站台编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择设备类型" clearable>
            <el-option v-for="dict in dict.type.position_type" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable>
          <el-option v-for="item in dict.type.position_state" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:PositionInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini"  :disabled="single" @click="handleAdd2"
          v-hasPermi="['wcs-base:PositionInfo:add']">复制新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:PositionInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:PositionInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:PositionInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="PositionInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="60" />
      <el-table-column label="编码" align="center" prop="code" min-width="120"> 
      </el-table-column>
      <el-table-column label="下位站台编码" align="center" prop="subCode" min-width="120">
      </el-table-column>
      <el-table-column label="分组编码" align="center" prop="parentCode" min-width="120">
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="150">
      </el-table-column>
      <el-table-column label="类型" align="center" prop="type" min-width="120">
        <template slot-scope="scope">
            <dict-tag :options="dict.type.position_type" :value="scope.row.type" />
          </template>
      </el-table-column>
      <el-table-column label="仓库编码" align="center" prop="wareCode" min-width="120"> 
      </el-table-column>
      <el-table-column label="仓库名称" align="center" prop="wareName" min-width="120"> 
      </el-table-column>
      <el-table-column label="库存状态" align="center" prop="invenState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.inven_state" :value="scope.row.invenState" />
        </template>
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="taskState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_task" :value="scope.row.taskState" />
        </template>
      </el-table-column>
      <el-table-column label="禁用状态" align="center" prop="disableState" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.disable_state" :value="scope.row.disableState" />
        </template>
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="isDelete" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.isDelete" />
        </template>
      </el-table-column>
     
      <!-- <el-table-column label="是否为组" align="center" prop="isGroup" min-width="100">
        <template slot-scope="scope">
          {{ scope.row.isGroup ? "是" : "否" }}
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="状态" align="center" prop="state" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.position_state" :value="scope.row.state" />
        </template>
      </el-table-column> -->
 
      <!-- <el-table-column label="创建人ID" align="center" prop="createUserId">

      </el-table-column> -->
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">

      </el-table-column>
      <!-- <el-table-column label="更新人ID" align="center" prop="updateUserId">

      </el-table-column> -->
      <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">

      </el-table-column>
      <el-table-column label="PLC IP" align="center" prop="plcIp" min-width="150">
      </el-table-column>
      <el-table-column label="地址1" align="center" prop="address1" min-width="120">
      </el-table-column>
      <el-table-column label="地址2" align="center" prop="address2" min-width="120">
      </el-table-column>
      <el-table-column label="地址3" align="center" prop="address3" min-width="120">
      </el-table-column>
      <!-- <el-table-column label="版本号" align="center" prop="version">

      </el-table-column> -->
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:PositionInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.isDelete == 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-base:PositionInfo:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.isDelete == 1"
            @click="handleRecover(scope.row)" v-hasPermi="['wcs-base:PositionInfo:recover']">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改站台对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="仓库" prop="wareCode">
          <el-select v-model="form.wareCode" placeholder="仓库" clearable @change="handleWareChange">
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="巷道" prop="lineCode">
          <el-select v-model="form.lineCode" placeholder="巷道" clearable>
            <el-option v-for="item in lineInfos" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="分组编码" prop="parentCode">
          <el-input v-model="form.parentCode" placeholder="请输入分组编码" />
        </el-form-item> 
        <el-form-item label="下位站台编码" prop="subCode">
          <el-input v-model="form.subCode" placeholder="请输入下位站台编码" />
        </el-form-item> 
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型" clearable>
            <el-option v-for="dict in dict.type.position_type" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态" prop="invenState">
          <el-input v-model="form.invenState" placeholder="库存状态">
          </el-input>
        </el-form-item>
        <el-form-item label="任务状态" prop="taskState">
          <el-input v-model="form.taskState" placeholder="任务状态">
          </el-input>
        </el-form-item>
        <el-form-item label="禁用状态" prop="disableState">
          <el-input v-model="form.disableState" placeholder="禁用状态">
          </el-input>
        </el-form-item>
        <el-form-item label="信息" prop="memo">
          <el-input v-model="form.memo" placeholder="禁用状态">
          </el-input>
        </el-form-item>
        <el-form-item label="PLC IP" prop="plcIp">
          <el-input v-model="form.plcIp" placeholder="请输入PLC IP">
          </el-input>
        </el-form-item>
        <el-form-item label="地址1" prop="address1">
          <el-input v-model="form.address1" placeholder="请输入地址1">
          </el-input>
        </el-form-item>
        <el-form-item label="地址2" prop="address2">
          <el-input v-model="form.address2" placeholder="请输入地址2">
          </el-input>
        </el-form-item>
        <el-form-item label="地址3" prop="address3">
          <el-input v-model="form.address3" placeholder="请输入地址3">
          </el-input>
        </el-form-item>
      
        <!-- <el-form-item label="状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择状态" clearable>
            <el-option v-for="item in dict.type.position_state" :key="item.key" :label="item.label"
              :value="parseInt(item.value)" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="删除标志" prop="isDelete">
          <el-select v-model="form.isDelete" placeholder="请选择删除标志" clearable>
            <el-option v-for="item in dict.type.del_flag" :key="item.key" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPositionInfo, getPositionInfo, delPositionInfo, addPositionInfo, updatePositionInfo } from "@/api/wcs-base/PositionInfo";
import { listLineInfo } from "@/api/wcs-base/LineInfo";
import { addPositionExtend } from "@/api/wcs-xlPro/PositionInfoExtend";
import request from "@/utils/request.js";

export default {
  name: "PositionInfo",
  dicts: ["del_flag", "position_state","position_type"

  , "disable_state", "inven_state" ,"is_task"
  ],
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
      // 站台表格数据
      PositionInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: null,
        subCode: null,
        name: null,
        type: null,
        state: null,
        isDelete: '0',
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        lineCode: null
      },
      wareInfos: [],
      lineInfos: [],
      // 表单参数
    form: {
      plcIp: null,
      address1: null,
      address2: null,
      address3: null
    },
      // 表单校验
      rules: {
        wareCode: [{ required: true, message: "仓库不能为空", trigger: "blur" }],
      
        code: [{ required: true, message: "编码不能为空", trigger: "blur" }],
        // name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
    this.getWareInfos();
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
    // 获取线体信息
    getLineInfos(wareCode) {
      this.lineInfos = [];
      if (!wareCode) return;
      listLineInfo({ wareCode: wareCode }).then(response => {
        if (response.code == 200) {
          this.lineInfos = response.rows;
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    /** 查询站台列表 */
    getList() {
      this.loading = true;
      listPositionInfo(this.queryParams).then(response => {
        this.PositionInfoList = response.rows;
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
        subCode: null,
        name: null,
        type: null,
        state: null,
        isDelete: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        lineCode: null,
        plcIp: null,
        address1: null,
        address2: null,
        address3: null
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
      this.title = "添加站台";
    },
        /** 修改按钮操作 */
    handleAdd2(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionInfo(id).then(response => {
        this.form = response.data;
        this.form.name="";
        this.form.id=null;
        this.open = true;
        this.title = "添加站台";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改站台";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePositionInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addPositionInfo(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                //this.open = false;
                this.getList();
                this.addPositionInfoExtend(this.form);
              } else {
                this.$modal.msgError(response.msg || "新增失败");
              }
            });
          }
        }
      });
    },
    addPositionInfoExtend(form) {
      console.log(form);
      addPositionExtend(form).then(response => {
        if (response.code == 200) {
         
        } else {
          
        }
      });
    },
    /** 恢复按钮操作 */
    handleRecover(row) {
      const id = row.id;
      this.$modal.confirm('是否确认恢复ID为"' + id + '"的数据项？').then(function () {
        return getPositionInfo(id)
      }).then((response) => {
        var info = response.data
        if (info == null) {
          this.$modal.msgError("选择数据项有误！")
          return;
        }
        info.isDelete = 0
        updatePositionInfo(info).then((response) => {
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
      this.$modal.confirm('是否确认删除站台编号为"' + ids + '"的数据项？').then(function () {
        return delPositionInfo(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }else{
          this.$modal.msgError(response.msg || "删除失败")
        }
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/PositionInfo/export', {
        ...this.queryParams
      }, `PositionInfo_${new Date().getTime()}.xlsx`)
    },
    /** 仓库选择变化事件 */
    handleWareChange(value) {
      this.getLineInfos(value);
      this.form.lineCode = null; // 清空线体选择
    }
  }
};
</script>
