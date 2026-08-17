<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="接口名称" prop="interfaceName">
        <el-input v-model="queryParams.interfaceName" placeholder="请输入接口名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="接口路径" prop="url">
        <el-input v-model="queryParams.url" placeholder="请输入接口路径" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发送方" prop="sendFrom">
        <el-input v-model="queryParams.sendFrom" placeholder="请输入发送方" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="接收方" prop="sendTo">
        <el-input v-model="queryParams.sendTo" placeholder="请输入接收方" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="通讯时间" prop="startTime">
        <el-date-picker clearable v-model="queryParams.startTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择通讯时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker clearable v-model="queryParams.endTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="发送次数" prop="sendCount">
        <el-input v-model="queryParams.sendCount" placeholder="请输入发送次数" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发送状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="请选择发送状态">
          <el-option v-for="item in sendStatuss" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="发送结果" prop="sendResult">
        <el-select v-model="queryParams.sendResult" placeholder="请选择发送结果">
          <el-option v-for="item in sendResults" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-base:interface:add']">新增</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-base:interface:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-base:interface:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-base:interface:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="interfaceList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="接口信息" align="center" min-width="200" class="info-column">
        <template slot-scope="scope">
          <div class="info-container">
            <!-- 核心信息（突出显示） -->
            <div class="info-item primary">
              <span class="info-label">接口名称:</span>
              <span class="info-value" :title="scope.row.interfaceName || '-'">{{ scope.row.interfaceName || '-' }}</span>
            </div>
            <div class="info-item primary">
              <span class="info-label">接口路径:</span>
              <span class="info-value" :title="scope.row.url || '-'">{{ scope.row.url || '-' }}</span>
            </div>

            <!-- 系统信息 -->
            <div class="info-item">
              <span class="info-label">客户端:</span>
              <span class="info-value" :title="scope.row.sendFrom || '-'">{{ scope.row.sendFrom || '-' }}</span>
              <span class="info-separator">→</span>
              <span class="info-label">服务端:</span>
              <span class="info-value" :title="scope.row.sendTo || '-'">{{ scope.row.sendTo || '-' }}</span>
            </div>

            <!-- 类型与状态（使用标签可视化） -->
            <div class="info-item">
              <span class="info-label">接口类型:</span>
              <el-tag :type="scope.row.type === CONSTANTS.INTERFACE_TYPE.SEND ? CONSTANTS.ELEMENT_TYPE.PRIMARY : CONSTANTS.ELEMENT_TYPE.SUCCESS" size="mini">
                {{ scope.row.type === CONSTANTS.INTERFACE_TYPE.SEND ? '发送' : '接收' }}
              </el-tag>

              <template v-if="scope.row.type === CONSTANTS.INTERFACE_TYPE.SEND">
                <span class="info-label ml-2">发送状态:</span>
                <el-tag :type="scope.row.sendStatus === CONSTANTS.SEND_STATUS.UNSENT ? CONSTANTS.ELEMENT_TYPE.WARNING : CONSTANTS.ELEMENT_TYPE.PRIMARY" size="mini">
                  {{ scope.row.sendStatus === CONSTANTS.SEND_STATUS.UNSENT ? '未发送' : '已发送' }}
                </el-tag>

                <span v-if="scope.row.sendStatus === CONSTANTS.SEND_STATUS.SENT">
                  <span class="info-label ml-2">发送结果:</span>
                  <el-tag :type="scope.row.sendResult === CONSTANTS.SEND_RESULT.FAILURE ? CONSTANTS.ELEMENT_TYPE.DANGER : CONSTANTS.ELEMENT_TYPE.SUCCESS" size="mini">
                    {{ scope.row.sendResult === CONSTANTS.SEND_RESULT.FAILURE ? '失败' : '成功' }}
                  </el-tag>

                  <span class="info-label">发送次数:</span>
                  <span class="info-value">{{ scope.row.sendCount || '0' }}</span>
                </span>

              </template>
            </div>

            <!-- 辅助信息（次要显示） -->
            <div class="info-item secondary">
              <span class="info-label ml-3">创建时间:</span>
              <span class="info-value" :title="scope.row.startTime || '-'">{{ scope.row.startTime || '-' }}</span>
            </div>
          </div>
        </template>
      </el-table-column>

      <!-- 请求参数列：优化JSON展示与交互 -->
      <el-table-column label="请求参数" align="left" prop="param" min-width="250" class="json-column">
        <template slot-scope="scope">
          <div class="json-wrapper">
            <div class="json-header">
              <el-button type="text" size="mini" class="copy-btn" @click="copyJson(scope.row.displayContent, CONSTANTS.MESSAGES.COPY_SUCCESS_REQUEST)">
                复制
              </el-button>
            </div>
            <div class="json-container">
              <json-viewer :value="safeParse(scope.row.displayContent)" boxed :expand-depth="expandDepth && expandDepth.displayContent ? expandDepth.displayContent : 1"
                :class="{ 'expanded': expandDepth && expandDepth.displayContent && expandDepth.displayContent > 1 }" class="json-viewer"></json-viewer>
            </div>
          </div>
        </template>
      </el-table-column>

      <!-- 返回值列：同参数列优化 -->
      <el-table-column label="返回值" align="left" prop="result" min-width="250" class="json-column">
        <template slot-scope="scope">
          <div class="json-wrapper">
            <div class="json-header">
              <el-button type="text" size="mini" class="copy-btn" @click="copyJson(scope.row.recv, CONSTANTS.MESSAGES.COPY_SUCCESS_RESPONSE)">
                复制
              </el-button>
            </div>
            <div class="json-container">
              <json-viewer :value="safeParse(scope.row.recv)" boxed :expand-depth="expandDepth && expandDepth.recv ? expandDepth.recv : 1"
                :class="{ 'expanded': expandDepth && expandDepth.recv && expandDepth.recv > 1 }" class="json-viewer"></json-viewer>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-base:interface:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-base:interface:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改接口记录对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="功能码" prop="code">
          <el-input disabled v-model="form.code" placeholder="请输入功能码" />
        </el-form-item>
        <el-form-item label="接口名称" prop="interfaceName">
          <el-input disabled v-model="form.interfaceName" placeholder="请输入接口名称" />
        </el-form-item>
        <el-form-item label="发送方" prop="sendFrom">
          <el-input disabled v-model="form.sendFrom" placeholder="请输入发送方" />
        </el-form-item>
        <el-form-item label="接收方" prop="sendTo">
          <el-input disabled v-model="form.sendTo" placeholder="请输入接收方" />
        </el-form-item>
        <el-form-item label="通讯时间" prop="startTime">
          <el-input disabled v-model="form.startTime" placeholder="请选择通讯时间">
          </el-input>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-input disabled v-model="form.endTime" placeholder="请选择结束时间">
          </el-input>
        </el-form-item>
        <el-form-item label="报文内容">
          <el-input disabled v-model="form.displayContent" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="应答内容" prop="recv">
          <el-input disabled v-model="form.displayRecv" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发送次数" prop="sendCount">
          <el-input disabled v-model="form.sendCount" placeholder="请输入发送次数" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="form.sendCount = 0; form.sendStatus = 0; submitForm();" type="warning">重新发送</el-button>
        <!-- <el-button type="primary" @click="submitForm">确 定</el-button> -->
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInterface, getInterface, delInterface, addInterface, updateInterface } from "@/api/wcs-base/interface";

// 常量定义
const CONSTANTS = {
  // 分页默认值
  DEFAULT_PAGE_NUM: 1,
  DEFAULT_PAGE_SIZE: 10,
  // 接口类型
  INTERFACE_TYPE: {
    SEND: 0,
    RECEIVE: 1
  },
  // 发送状态
  SEND_STATUS: {
    UNSENT: 0,
    SENT: 1
  },
  // 发送结果
  SEND_RESULT: {
    FAILURE: 0,
    SUCCESS: 1
  },
  // Element UI 类型
  ELEMENT_TYPE: {
    PRIMARY: 'primary',
    SUCCESS: 'success',
    WARNING: 'warning',
    DANGER: 'danger'
  },
  // 消息提示
  MESSAGES: {
    COPY_SUCCESS_REQUEST: '请求参数复制成功',
    COPY_SUCCESS_RESPONSE: '返回值复制成功',
    NO_CONTENT: '无内容可复制',
    COPY_FAILURE: '复制失败，请手动复制',
    UPDATE_SUCCESS: '修改成功',
    UPDATE_FAILURE: '修改失败',
    ADD_SUCCESS: '新增成功',
    ADD_FAILURE: '新增失败',
    DELETE_SUCCESS: '删除成功',
    DELETE_FAILURE: '删除失败'
  }
};

export default {
  name: "Interface",
  data() {
    return {
        // 常量
        CONSTANTS,
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
        // 接口记录表格数据
        interfaceList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // JSON展开深度配置
        expandDepth: {
          displayContent: 1,
          recv: 1
        },
      // 查询参数
      queryParams: {
        pageNum: CONSTANTS.DEFAULT_PAGE_NUM,
        pageSize: CONSTANTS.DEFAULT_PAGE_SIZE,
        code: null,
        sendFrom: null,
        sendTo: null,
        startTime: null,
        endTime: null,
        content: null,
        recv: null,
        sendCount: null,
        sendStatus: null,
        sendResult: null,
        interfaceName: null,
      },
      sendStatuss: [
        { value: CONSTANTS.SEND_STATUS.UNSENT, label: "初始化", color: "#909399" },
        { value: CONSTANTS.SEND_STATUS.SENT, label: "发送成功", color: "#67C23A" }
      ],
      sendResults: [
        { value: CONSTANTS.SEND_RESULT.FAILURE, label: "未通过", color: "#F56C6C" },
        { value: CONSTANTS.SEND_RESULT.SUCCESS, label: "已通过", color: "#67C23A" }
      ],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "功能码不能为空", trigger: "blur" }
        ],
        sendFrom: [
          { required: true, message: "发送方不能为空", trigger: "blur" }
        ],
        sendTo: [
          { required: true, message: "接收方不能为空", trigger: "blur" }
        ],
        startTime: [
          {
            validator: (rule, value, callback) => {
              if (this.queryParams.endTime && value > this.queryParams.endTime) {
                callback(new Error("开始时间不能大于结束时间"));
              } else {
                callback();
              }
            },
            trigger: "change"
          }
        ],
        endTime: [
          {
            validator: (rule, value, callback) => {
              if (this.queryParams.startTime && value < this.queryParams.startTime) {
                callback(new Error("结束时间不能小于开始时间"));
              } else {
                callback();
              }
            },
            trigger: "change"
          }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    
    /** 安全解析JSON（避免格式错误报错） */
    safeParse(str) {
      if (!str) return {};
      try {
        return JSON.parse(str);
      } catch (e) {
        console.error('JSON解析错误:', e, '内容:', str);
        return { error: "参数格式错误" };
      }
    },
    /** 复制JSON内容到剪贴板 */
    copyJson(content, successMsg) {
      if (!content) {
        this.$message.warning(CONSTANTS.MESSAGES.NO_CONTENT);
        return;
      }
      // 尝试格式化JSON再复制（更易读）
      let text = content;
      try {
        text = JSON.stringify(JSON.parse(content), null, 2);
      } catch (e) {
        // 非JSON格式直接复制原内容
        console.debug('非JSON格式内容，直接复制原内容:', content);
      }
      navigator.clipboard.writeText(text).then(() => {
        this.$message.success(successMsg);
      }).catch(() => {
        this.$message.error(CONSTANTS.MESSAGES.COPY_FAILURE);
        console.error('复制到剪贴板失败:', text);
      });
    },

    /** 查询接口记录列表 */
    getList() {
      this.loading = true;
      listInterface(this.queryParams).then(response => {
        this.interfaceList = response.rows || [];
        this.total = response.total || 0;
        this.loading = false;
      }).catch(error => {
        this.loading = false;
        this.$message.error('查询接口记录失败：' + (error.message || '未知错误'));
        console.error('查询接口记录失败:', error);
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
        sendFrom: null,
        sendTo: null,
        startTime: null,
        endTime: null,
        content: null,
        recv: null,
        sendCount: null,
        sendStatus: null,
        sendResult: null,
        interfaceName: null,
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
      this.title = "添加接口记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInterface(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改接口记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 统一处理API响应
          const handleResponse = (response, successMsg, failureMsg) => {
            if (response.code === 200) {
              this.$modal.msgSuccess(successMsg);
              this.open = false;
              this.getList();
            } else {
              this.$modal.msgError(response.msg || failureMsg);
              console.error(`${failureMsg}:`, response);
            }
          };

          try {
            if (this.form.id != null) {
              updateInterface(this.form).then(response => {
                handleResponse(response, CONSTANTS.MESSAGES.UPDATE_SUCCESS, CONSTANTS.MESSAGES.UPDATE_FAILURE);
              }).catch(error => {
                this.$modal.msgError(CONSTANTS.MESSAGES.UPDATE_FAILURE + ': ' + (error.message || '未知错误'));
                console.error('修改接口记录失败:', error);
              });
            } else {
              addInterface(this.form).then(response => {
                handleResponse(response, CONSTANTS.MESSAGES.ADD_SUCCESS, CONSTANTS.MESSAGES.ADD_FAILURE);
              }).catch(error => {
                this.$modal.msgError(CONSTANTS.MESSAGES.ADD_FAILURE + ': ' + (error.message || '未知错误'));
                console.error('新增接口记录失败:', error);
              });
            }
          } catch (error) {
            this.$modal.msgError('操作失败: ' + (error.message || '未知错误'));
            console.error('表单提交异常:', error);
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除接口记录编号为"' + ids + '"的数据项？').then(() => {
        return delInterface(ids);
      }).then((response) => {
          if (response.code === 200) {
            this.getList();
            this.$modal.msgSuccess(CONSTANTS.MESSAGES.DELETE_SUCCESS);
          } else {
            this.$modal.msgError(response.msg || CONSTANTS.MESSAGES.DELETE_FAILURE);
            console.error('删除接口记录失败:', response);
          }
        }).catch((error) => {
          if (error !== 'cancel') {
            this.$modal.msgError(CONSTANTS.MESSAGES.DELETE_FAILURE + ': ' + (error.message || '未知错误'));
            console.error('删除接口记录异常:', error);
          }
        });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-base/interface/export', {
        ...this.queryParams
      }, `interface_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
/* 搜索区域样式 */
.search-card {
  margin-bottom: 16px;
  border-radius: 6px;
  overflow: hidden;
}

.search-form {
  padding: 12px 0;
}

.search-input {
  width: 180px;
}

.search-date {
  width: 180px;
}

.more-btn {
  color: #409eff;
  padding: 0 5px;
}

.more-search-row {
  width: 100%;
  margin-top: 8px;
  padding-left: 78px;
  /* 对齐label宽度 */
}

.more-search-item {
  margin-right: 10px;
  margin-bottom: 8px;
}

.search-btn-group {
  margin-left: 10px;
}

.search-btn,
.reset-btn {
  margin-right: 8px;
}

/* 操作按钮区域 */
.operation-bar {
  display: flex;
  align-items: center;
  padding: 4px 0;
}

.operation-btn {
  transition: all 0.2s;
}

.operation-btn:hover {
  transform: translateY(-1px);
}

.right-toolbar {
  margin-left: auto;
}

/* 表格区域样式 */
.table-card {
  border-radius: 6px;
  overflow: hidden;
}

.main-table {
  border-radius: 4px 4px 0 0;
}

.main-table th {
  background-color: #f5f7fa;
  font-weight: 500;
}

.main-table tr:hover>td {
  background-color: #fafafa;
}

.info-container {
  text-align: left;
  padding: 4px 0;
}

.info-item {
  line-height: 1.6;
  font-size: 13px;
}

.info-label {
  color: #606266;
  font-weight: 500;
  display: inline-block;
  width: 60px;
}

.json-container {
  max-height: 200px;
  overflow-y: auto;
  padding: 4px;
  text-align: left;
}

.json-viewer {
  font-size: 12px;
  line-height: 1.5;
  text-align: left;
}

/* JSON列样式优化 */
.json-column .cell {
  text-align: left !important;
  padding-left: 8px !important;
}

.json-wrapper {
  text-align: left;
}

.json-header {
  text-align: left;
  margin-bottom: 4px;
}

/* 分页样式 */
.pagination {
  margin-top: 12px;
  text-align: right;
}

/* 弹窗样式 */
.form-dialog {
  border-radius: 8px;
}

.dialog-form {
  padding: 10px 0;
}

.dialog-input {
  width: 100%;
}

.dialog-textarea {
  width: 100%;
  resize: vertical;
}

.dialog-footer {
  padding: 10px 20px 15px;
  text-align: right;
}

.dialog-btn {
  margin-left: 10px;
  padding: 6px 16px;
}

.confirm-btn {
  background-color: #409eff;
  border-color: #409eff;
}

/* 响应式调整 */
@media (max-width: 1200px) {

  .search-input,
  .search-date {
    width: 150px;
  }
}

@media (max-width: 992px) {

  .search-input,
  .search-date {
    width: 130px;
  }

  .operation-bar {
    flex-wrap: wrap;
  }
}
</style>
