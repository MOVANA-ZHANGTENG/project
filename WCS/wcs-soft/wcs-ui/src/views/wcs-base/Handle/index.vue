<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="4" :xs="24">
        <el-input placeholder="关键字" clearable v-model="groupKeyword" class="input-with-select">
          <el-button slot="append" icon="el-icon-search"></el-button>
        </el-input>

        <el-card style="height: 79vh;margin-top: 3%;">
          <div slot="header" class="clearfix">
            <span>{{ tabName }}</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="newGroup(tabValue)">添加分组</el-button>
          </div>

          <el-tabs tab-position="top" v-model="tabValue" style="height: 75vh;" @tab-click="updateTabName()">

            <el-tab-pane label="1">
              <div>
                <el-collapse v-model="activeName" accordion>
                  <el-collapse-item @click.native="selectByGroup(item)" v-if="item.type == 0 &&
                    (groupKeyword == null || groupKeyword == '' || item.name.includes(groupKeyword))"
                    v-for="item in groupList">
                    <template slot="title">
                      <div>{{ item.name }}</div>
                      <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                    </template>
                    <el-form label-position="left" size="small" :inline="false">
                      <el-form-item label="分组描述:">
                        <div>{{ item.memo }}</div>
                      </el-form-item>
                    </el-form>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-tab-pane>

            <el-tab-pane label="2">
              <div class="head-container">
                <el-collapse v-model="activeName" accordion>
                  <el-collapse-item @click.native="selectByGroup(item)" v-if="
                    item.type == 1 &&
                    (groupKeyword == null ||
                      groupKeyword == '' ||
                      item.name.includes(groupKeyword))
                  " v-for="item in groupList">
                    <template slot="title">
                      <div>{{ item.name }}</div>
                      <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                    </template>
                    <el-form label-position="left" size="small" :inline="false">
                      <el-form-item label="分组描述:">
                        <div>{{ item.memo }}</div>
                      </el-form-item>
                    </el-form>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-tab-pane>

            <el-tab-pane label="3">
              <div class="head-container">
                <el-collapse v-model="activeName" accordion>
                  <el-collapse-item @click.native="selectByGroup(item)" v-if="
                    item.type == 2 &&
                    (groupKeyword == null ||
                      groupKeyword == '' ||
                      item.name.includes(groupKeyword))
                  " v-for="item in groupList">
                    <template slot="title">
                      <div>{{ item.name }}</div>
                      <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                    </template>
                    <el-form label-position="left" size="small" :inline="false">
                      <el-form-item label="分组描述:">
                        <div>{{ item.memo }}</div>
                      </el-form-item>
                    </el-form>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-tab-pane>

            <el-tab-pane label="4">
              <div class="head-container">
                <el-collapse v-model="activeName" accordion>
                  <el-collapse-item @click.native="selectByGroup(item)" v-if="
                    item.type == 3 &&
                    (groupKeyword == null ||
                      groupKeyword == '' ||
                      item.name.includes(groupKeyword))
                  " v-for="item in groupList">
                    <template slot="title">
                      <div>{{ item.name }}</div>
                      <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                    </template>
                    <el-form label-position="left" size="small" :inline="false">
                      <el-form-item label="分组描述:">
                        <div>{{ item.memo }}</div>
                      </el-form-item>
                    </el-form>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-tab-pane>

          </el-tabs>
        </el-card>


        <!-- <el-card class="box-card" shadow="hover">
          <div slot="header" class="clearfix"
            @click="showHandleGroup = [false, false, false, false]; showHandleGroup[0] = true">
            <span>JOB执行器</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="newGroup(0)">添加分组</el-button>
          </div>
          <div class="head-container" v-show="showHandleGroup[0]">
            <el-collapse v-model="activeName" accordion>
              <el-collapse-item @click.native="selectByGroup(item)" v-if="
                item.type == 0 &&
                (groupKeyword == null ||
                  groupKeyword == '' ||
                  item.name.includes(groupKeyword))
              " v-for="item in groupList">
                <template slot="title">
                  <div>{{ item.name }}</div>
                  <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                </template>
                <el-form label-position="left" size="small" :inline="false">
                  <el-form-item label="分组描述:">
                    <div>{{ item.memo }}</div>
                  </el-form-item>
                </el-form>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>

        <el-card class="box-card" shadow="hover">
          <div slot="header" class="clearfix"
            @click="showHandleGroup = [false, false, false, false]; showHandleGroup[1] = true">
            <span>PATH执行器</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="newGroup(1)">添加分组</el-button>
          </div>
          <div class="head-container" v-show="showHandleGroup[1]">
            <el-collapse v-model="activeName" accordion>
              <el-collapse-item @click.native="selectByGroup(item)" v-if="
                item.type == 1 &&
                (groupKeyword == null ||
                  groupKeyword == '' ||
                  item.name.includes(groupKeyword))
              " v-for="item in groupList">
                <template slot="title">
                  <div>{{ item.name }}</div>
                  <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                </template>
                <el-form label-position="left" size="small" :inline="false">
                  <el-form-item label="分组描述:">
                    <div>{{ item.memo }}</div>
                  </el-form-item>
                </el-form>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>

        <el-card class="box-card" shadow="hover">
          <div slot="header" class="clearfix"
            @click="showHandleGroup = [false, false, false, false]; showHandleGroup[2] = true">
            <span>呼叫盒执行器</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="newGroup(2)">添加分组</el-button>
          </div>
          <div class="head-container" v-show="showHandleGroup[2]">
            <el-collapse v-model="activeName" accordion>
              <el-collapse-item @click.native="selectByGroup(item)" v-if="
                item.type == 2 &&
                (groupKeyword == null ||
                  groupKeyword == '' ||
                  item.name.includes(groupKeyword))
              " v-for="item in groupList">
                <template slot="title">
                  <div>{{ item.name }}</div>
                  <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                </template>
                <el-form label-position="left" size="small" :inline="false">
                  <el-form-item label="分组描述:">
                    <div>{{ item.memo }}</div>
                  </el-form-item>
                </el-form>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>

        <el-card class="box-card" shadow="hover">
          <div slot="header" class="clearfix"
            @click="showHandleGroup = [false, false, false, false]; showHandleGroup[3] = true">
            <span>扫码器执行器</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="newGroup(3)">添加分组</el-button>
          </div>
          <div class="head-container" v-show="showHandleGroup[3]">
            <el-collapse v-model="activeName" accordion>
              <el-collapse-item @click.native="selectByGroup(item)" v-if="
                item.type == 3 &&
                (groupKeyword == null ||
                  groupKeyword == '' ||
                  item.name.includes(groupKeyword))
              " v-for="item in groupList">
                <template slot="title">
                  <div>{{ item.name }}</div>
                  <i class="header-icon el-icon-info" style="margin-left: 3%"></i>
                </template>
                <el-form label-position="left" size="small" :inline="false">
                  <el-form-item label="分组描述:">
                    <div>{{ item.memo }}</div>
                  </el-form-item>
                </el-form>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card> -->

      </el-col>
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <!-- <el-form :model="queryParams" label-position="left" ref="queryForm" size="small" :inline="true"
          v-show="showSearch" label-width="80px">
          <el-form-item label="类名" prop="className">
            <el-input v-model="queryParams.className" placeholder="请输入类名" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item label="方法名" prop="methodName">
            <el-input v-model="queryParams.methodName" placeholder="请输入方法名" clearable
              @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item label="编码" prop="code">
            <el-input v-model="queryParams.code" placeholder="请输入编码" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item label="类型" prop="type">
            <el-select v-model="queryParams.type" placeholder="类型" clearable>
              <el-option v-for="dict in handleTypes" :key="dict.value" :label="dict.label"
                :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            <el-date-picker clearable v-model="queryParams.createTime" type="date" value-format="yyyy-MM-dd"
              placeholder="请选择创建时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="创建人" prop="createUserName">
            <el-input v-model="queryParams.createUserName" placeholder="请输入创建人" clearable
              @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item label="更新时间" prop="updateTime">
            <el-date-picker clearable v-model="queryParams.updateTime" type="date" value-format="yyyy-MM-dd"
              placeholder="请选择更新时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="更新人" prop="updateUserName">
            <el-input v-model="queryParams.updateUserName" placeholder="请输入更新人" clearable
              @keyup.enter.native="handleQuery" />
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
        </el-form> -->

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button v-if="form.groupId != null" type="primary" plain icon="el-icon-plus" size="mini"
              @click="handleAdd" v-hasPermi="['wcs-base:Handle:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate2"
              v-hasPermi="['wcs-base:Handle:edit']">复制新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
              v-hasPermi="['wcs-base:Handle:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
              v-hasPermi="['wcs-base:Handle:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
              v-hasPermi="['wcs-base:Handle:export']">导出</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="HandleList" @selection-change="handleSelectionChange" border>
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="ID" align="center" prop="id" min-width="60" />
          <el-table-column label="分组ID" align="center" prop="groupId" min-width="120">
          </el-table-column>
          <el-table-column label="分组名称" align="center" prop="groupName" min-width="150">
          </el-table-column>
          <el-table-column label="类名" align="center" prop="className" min-width="180">
          </el-table-column>
          <el-table-column label="方法名" align="center" prop="methodName" min-width="120">
          </el-table-column>
          <!-- <el-table-column label="编码" align="center" prop="code"  width="120">
      </el-table-column> -->
          <el-table-column label="名称" align="center" prop="name" min-width="150">
          </el-table-column>
          <el-table-column label="类型" align="center" prop="handleType" min-width="100">
            <template slot-scope="scope">
              <div v-for="item in hTypes">
                <span v-if="scope.row.handleType == item.value" :style="'color:' + item.color">{{ item.label }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          </el-table-column>
          <!-- <el-table-column label="创建人ID" align="center" prop="createUserId">
      </el-table-column> -->
          <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
          </el-table-column>
          <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
          </el-table-column>
          <!-- <el-table-column label="更新人ID" align="center" prop="udpateUserId">
      </el-table-column> -->
          <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">
          </el-table-column>
          <el-table-column label="删除标志" align="center" prop="isDelete">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.del_flag" :value="scope.row.isDelete" />
            </template>
          </el-table-column>
          <!-- <el-table-column label="版本号" align="center" prop="version">
      </el-table-column> -->
          <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                v-hasPermi="['wcs-base:Handle:edit']">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                v-hasPermi="['wcs-base:Handle:remove']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改执行器对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分组" prop="groupId">
          <template slot-scope="slot">
            <el-select  v-model="form.groupId" placeholder="请选择分组">
              <el-option v-for="item in groups" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="类名" prop="className">
          <el-input v-model="form.className" placeholder="请输入类名" />
        </el-form-item>
        <el-form-item label="方法名" prop="methodName">
          <el-input v-model="form.methodName" placeholder="请输入方法名" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="类型" prop="handleType">
          <el-select v-model="form.handleType" placeholder="请选择类型" clearable>
            <el-option v-for="item in hTypes" :key="item.value" :value="item.value" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- {{ data }}
    <HandleInfo v-model="data" /> -->

    <!-- 添加或修改分组管理对话框 -->
    <el-dialog v-dialogDrags title="添加分组" :visible.sync="openGroupForm" width="500px" append-to-body>
      <el-form :model="groupForm" label-width="100px">
        <el-form-item label="分组名称" prop="name">
          <el-input v-model="groupForm.name" placeholder="请输入分组名称" />
        </el-form-item>
        <el-form-item label="分组类型" prop="type">
          <el-select v-model="groupForm.type" placeholder="请选择分组类型">
            <el-option v-for="item in handleTypes" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分组描述" prop="memo">
          <el-input v-model="groupForm.memo" type="textarea" placeholder="请输入分组描述"
            :autosize="{ minRows: 4, maxRows: 4 }" :style="{ width: '100%' }" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitGroup">确 定</el-button>
        <el-button @click="openGroupForm = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listHandleGroup,
  getHandleGroup,
  delHandleGroup,
  addHandleGroup,
  updateHandleGroup,
} from "@/api/wcs-base/handleGroup";
import {
  listHandle,
  getHandle,
  delHandle,
  addHandle,
  updateHandle,
} from "@/api/wcs-base/Handle";
import HandleInfo from "./HandleInfo";
import request from "@/utils/request.js";
export default {
  name: "Handle",
  dicts: ["del_flag"],
  data() {
    return {
      groupKeyword: null,
      activeName: "1",
      data: [{ name: "123", isSelect: false }],
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
      // 执行器表格数据
      HandleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        className: null,
        methodName: null,
        code: null,
        name: null,
        type: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        udpateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null,
      },
      handleTypes: [
        { value: 0, label: "jobInfo", color: "#409EFF" },
        { value: 1, label: "pathInfo", color: "#67C23A" },
        { value: 2, label: 'callBox', color: '#F56C6C' },
        { value: 3, label: 'scanCode', color: '#E6A23C' },
      ],
      hTypes: [
        { value: 0, label: "执行条件", color: "#409EFF" },
        { value: 1, label: "执行函数", color: "#67C23A" },
        { value: 2, label: "成功条件", color: "#E6A23C" },
        { value: 3, label: "成功回调", color: "#F56C6C" },
        { value: 4, label: "删除回调", color: "#909399" },
      ],
      //存放处理后的分组数据
      groups: [],
      //存放处理前的分组数据
      groupList: [],
      // 表单参数
      form: {},
      openGroupForm: false,
      showHandleGroup: [false, false, false, false],
      groupForm: {},
      // 表单校验
      rules: {},
      tabValue: 0,
      tabName: "JOB执行器",
    };
  },
  components: {
    HandleInfo,
  },

  created() {
    this.getList();
    this.getHandleGroupInfo();
  },
  methods: {
    updateTabName() {
      if (this.tabValue == 0) {
        this.tabName = "JOB执行器"

      } else if (this.tabValue == 1) {
        this.tabName = "PATH执行器"
      } else if (this.tabValue == 2) {
        this.tabName = "呼叫盒执行器"
      } else if (this.tabValue == 3) {
        this.tabName = "扫码器执行器"
      }
    },
    newGroup(type) {
      this.resetForm();
      if (type == 0) {
        this.groupForm.type = 0;
      } else if (type == 1) {
        this.groupForm.type = 1;
      } else if (type == 2) {
        this.groupForm.type = 2;
      } else if (type == 3) {
        this.groupForm.type = 3;
      }
      this.openGroupForm = true;
    },
    resetForm() {
      this.groupForm = {
        name: null,
        type: null,
        memo: null,
      };
    },
    submitGroup() {
      addHandleGroup(this.groupForm).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("新增成功");
          this.openGroupForm = false;
          this.getHandleGroupInfo();
        } else {
          this.$modal.msgError(response.msg || "新增失败");
        }
      });
    },

    selectByGroup(group) {
      this.queryParams.pageNum = 1;
      this.queryParams.groupId = group.id;
      this.form.groupId = group.id;
      this.getList();
    },
    getHandleGroupInfo() {
      var that = this;
      request({
        url: "/wcs-base/handleGroup/findCanAllotGroup",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          that.groups = [];
          var data = response.data;
          that.groupList = response.data;
          for (var i = 0; i < data.length; i++) {
            var obj = {};
            obj.value = data[i].id;
            obj.label = data[i].name;
            that.groups.push(obj);
          }
        }
      });
    },
    /** 查询执行器列表 */
    getList() {
      this.loading = true;
      listHandle(this.queryParams).then((response) => {
        this.HandleList = response.rows;
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
        className: null,
        methodName: null,
        code: null,
        name: null,
        type: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        udpateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null,
        groupName: null,
        groupId: this.form.groupId,
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
      this.getHandleGroupInfo();
      this.reset();
      this.open = true;
      this.title = "添加执行器";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getHandleGroupInfo();
      this.reset();
      const id = row.id || this.ids;
      getHandle(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改执行器";
      });
    },
    /** 修改按钮操作 */
    handleUpdate2(row) {
      this.getHandleGroupInfo();
      this.reset();
      const id = row.id || this.ids;
      getHandle(id).then((response) => {
        this.form = response.data;
        this.form.id = null;
        this.open = true;
        this.title = "添加执行器";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.groupId != null) {
            for (var i = 0; i < this.groups.length; i++) {
              if (this.form.groupId == this.groups[i].value) {
                this.form.groupName = this.groups[i].label;
              }
            }
          }
          if (this.form.id != null) {
            updateHandle(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addHandle(this.form).then((response) => {
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
      this.$modal
        .confirm('是否确认删除执行器编号为"' + ids + '"的数据项？')
        .then(function () {
          return delHandle(ids);
        })
        .then((response) => {
          if (response.code == 200) {
            this.getList();
            this.$modal.msgSuccess("删除成功");
          }else{
            this.$modal.msgError(response.msg||"删除失败")
          }
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-base/Handle/export",
        {
          ...this.queryParams,
        },
        `Handle_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
