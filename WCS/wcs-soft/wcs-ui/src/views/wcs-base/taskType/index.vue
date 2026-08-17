<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="类型编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入任务类型编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入任务类型名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input v-model="queryParams.wareCode" placeholder="请输入仓库编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库名称" prop="wareName">
        <el-input v-model="queryParams.wareName" placeholder="请输入仓库名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-input v-model="queryParams.priority" placeholder="请输入优先级" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="删除标志" prop="delFlag">
        <el-select v-model="queryParams.delFlag" placeholder="请选择删除标志" clearable>
          <el-option v-for="item in dict.type.del_flag" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建人ID" prop="createUserId">
        <el-input v-model="queryParams.createUserId" placeholder="请输入创建人ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新人ID" prop="updateUserId">
        <el-input v-model="queryParams.updateUserId" placeholder="请输入更新人ID" clearable
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
          v-hasPermi="['wcs-base:taskType:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:taskType:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:taskType:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:taskType:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taskTypeList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" min-width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="类型编码" align="center" prop="code" min-width="120">
      </el-table-column>
      <el-table-column label="类型名称" align="center" prop="name" min-width="150">
        <template slot-scope="scope">
          <el-button 
            type="text" 
            @click="openFlowEditor(scope.row)" 
            class="link-type-btn"
          >
            <span>{{ scope.row.name }}</span>
          </el-button>
        </template>
      </el-table-column>


      <el-table-column label="仓库" align="center" prop="wareCode" min-width="200">
        <template slot-scope="scope">
          <div class="ware-info">
            <el-tag size="mini" type="primary" effect="plain" class="ware-code">
              {{ scope.row.wareCode }}
            </el-tag>
            <span class="ware-name">{{ scope.row.wareName || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="仓库名称" align="center" prop="wareName" min-width="150">
      </el-table-column> -->
      <el-table-column label="优先级" align="center" prop="priority" min-width="120">
      </el-table-column>
      <el-table-column label="删除标志" align="center" prop="delFlag" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.del_flag" :value="scope.row.delFlag" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="创建人ID" align="center" prop="createUserId" min-width="120">
      </el-table-column> -->
      <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">

      </el-table-column>
      <!-- <el-table-column label="更新人ID" align="center" prop="updateUserId" min-width="120">
      </el-table-column> -->
      <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:taskType:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-if="scope.row.delFlag == 0"
            @click="handleDelete(scope.row)" v-hasPermi="['wcs-base:taskType:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh-left" v-if="scope.row.delFlag == 1"
            @click="handleRecover(scope.row)" v-hasPermi="['wcs-base:taskType:recover']">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务类型对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入类型编码">
          </el-input>
        </el-form-item>
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类型名称">
          </el-input>
        </el-form-item>
        <el-form-item label="仓库" prop="wareCode">
          <el-select v-model="form.wareCode" placeholder="请选择仓库" clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="form.priority" placeholder="请输入优先级"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    
    <!-- 流程设计器抽屉 -->
    <el-drawer
      :visible.sync="showFlowEditor"
      :with-header="false"
      :modal="true"
      :close-on-press-escape="true"
      :wrapperClosable="false"
      size="100%"
      custom-class="flow-editor-drawer"
      @close="closeFlowEditor"
    >
      <TaskFlowEditor 
        v-if="showFlowEditor" 
        :taskTypeCode="currentTaskType.code" 
        :wareCode="currentTaskType.wareCode"
        @close="closeFlowEditor"
      />
    </el-drawer>
  </div>
</template>

<script>
import { listTaskType, getTaskType, delTaskType, addTaskType, updateTaskType } from "@/api/wcs-base/taskType";
import { listWareInfo } from "@/api/wcs-base/WareInfo";
import request from "@/utils/request";
import TaskFlowEditor from "../TaskDefine/TaskFlowEditor.vue";

export default {
  name: "TaskType",
  components: {
    TaskFlowEditor
  },
  dicts: ['del_flag'],
  data() {
    return {
      // 流程设计器相关
      showFlowEditor: false,
      currentTaskType: {
        code: null,
        wareCode: null
      },
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
      // 任务类型表格数据
      taskTypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        delFlag: '0',
        code: null,
        name: null,
        wareCode: null,
        wareName: null,
        priority: null,
        createUserId: null,
        updateUserId: null,
      },
      wareInfos: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },


      typeEnums: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /**
     * 打开流程设计器
     */
    openFlowEditor(taskType) {
      // console.log('打开流程设计器:', taskType);
      this.currentTaskType = {
        code: taskType.code,
        wareCode: taskType.wareCode
      };
      this.showFlowEditor = true;
    },
    
    /**
     * 关闭流程设计器
     */
    closeFlowEditor() {
      // console.log('关闭流程设计器');
      this.showFlowEditor = false;
      this.currentTaskType = {
        code: null,
        wareCode: null
      };
    },

    //获取所有仓库
    getWareInfos() {
      listWareInfo({ isDelete: 0 }).then((response) => {
        if (response.code == 200) {
          this.wareInfos = response.rows;
        }
      });
    },

    /** 查询任务类型列表 */
    getList() {
      this.loading = true;
      listTaskType(this.queryParams).then(response => {
        if (response.code == 200) {
          this.taskTypeList = response.rows;
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
        wareCode: null,
        wareName: null,
        priority: 10,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        delFlag: null
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
      this.getWareInfos()
      this.reset();
      this.open = true;
      this.title = "添加任务类型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getWareInfos()
      this.reset();
      const id = row.id || this.ids
      getTaskType(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改任务类型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTaskType(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addTaskType(this.form).then(response => {
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
    /** 恢复按钮操作 */
    handleRecover(row) {
      const id = row.id;
      this.$modal.confirm('是否确认恢复ID为"' + id + '"的数据项？').then(function () {
        return getTaskType(id)
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
          } else {
            this.$modal.msgError(response.msg || "恢复失败")
          }
        })
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除任务类型编号为"' + ids + '"的数据项？').then(function () {
        return delTaskType(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/taskType/export', {
        ...this.queryParams
      }, `taskType_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style lang="scss" scoped>
.ware-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  .ware-code {
    font-size: 12px;
    font-weight: 500;
    border-radius: 4px;
    padding: 2px 8px;
    
    &:deep(.el-tag__content) {
      color: #409eff;
    }
  }
  
  .ware-name {
    font-size: 13px;
    color: #606266;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.link-type-btn {
  font-size: 14px;
  font-weight: 500;
  color: #409eff;
  padding: 0;
  
  span {
    text-decoration: underline;
  }
  
  &:hover {
    color: #66b1ff;
    
    span {
      text-decoration: underline;
    }
  }
}

// 流程设计器抽屉深色样式
::v-deep .flow-editor-drawer {
  .el-drawer {
    background: #1a1a2e !important;
    box-shadow: -8px 0 32px rgba(0, 0, 0, 0.5);
  }
  
  .el-drawer__body {
    background: #1a1a2e;
    padding: 0;
    overflow: hidden;
    height: 100%;
  }
  
  .el-drawer__header {
    background: rgba(25, 30, 45, 0.95);
    border-bottom: 2px solid rgba(102, 126, 234, 0.3);
    color: #ffffff;
    padding: 20px;
  }
}

// 抽屉遮罩层深色
::v-deep .v-modal {
  background: rgba(0, 0, 0, 0.7) !important;
}
</style>
