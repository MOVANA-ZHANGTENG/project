<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="呼叫盒ID" prop="callBoxInfoId">
        <el-input
          v-model="queryParams.callBoxInfoId"
          placeholder="请输入呼叫盒ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入呼叫盒编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="呼叫盒IP" prop="ip">
        <el-input
          v-model="queryParams.ip"
          placeholder="请输入呼叫盒IP"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
          <el-option
            v-for="dict in types"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in states"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
       
      </el-form-item>
      <el-form-item label="按钮" prop="btn">
        <el-input
          v-model="queryParams.btn"
          placeholder="请输入按钮"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="灯" prop="lampColor">
        <el-input
          v-model="queryParams.lampColor"
          placeholder="请输入灯号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="亮灯模式" prop="lampMode">
        <el-select v-model="queryParams.lampMode" placeholder="亮灯模式" clearable>
          <el-option
            v-for="dict in lampModes"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="时间ms" prop="lampTime">
        <el-input
          v-model="queryParams.lampTime"
          placeholder="请输入亮灯持续时间ms"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="控制类型" prop="lampCtrl">
        <el-select v-model="queryParams.lampCtrl" placeholder="控制类型" clearable>
          <el-option
            v-for="dict in lampCtrls"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="memo">
        <el-input
          v-model="queryParams.memo"
          placeholder="请输入备注"
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
          v-hasPermi="['wcs-task:CallBoxRecord:add']"
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
          v-hasPermi="['wcs-task:CallBoxRecord:edit']"
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
          v-hasPermi="['wcs-task:CallBoxRecord:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-task:CallBoxRecord:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="CallBoxRecordList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
    <el-table-column label="呼叫盒ID" align="center" prop="callBoxInfoId"  min-width="120">
    </el-table-column>
    <el-table-column label="按钮时间" align="center" prop="createTime"  min-width="120">
    </el-table-column>
    <el-table-column label="处理时间" align="center" prop="updateTime"  min-width="120">
    </el-table-column>
    <!-- <el-table-column label="呼叫盒编码" align="center" prop="code"  min-width="120">
    </el-table-column>
    <el-table-column label="呼叫盒IP" align="center" prop="ip"  min-width="120">
    </el-table-column> -->
      <el-table-column label="类型" align="center" prop="type"  min-width="120">
        <template slot-scope="scope">
          <span :style="'color:'+item.color" v-for="item in types " v-if="scope.row.type == item.value">{{ item.label }}</span>
        </template>
      </el-table-column>
    <el-table-column label="状态 " align="center" prop="state"  min-width="120">
      
        <template slot-scope="scope">
          <span  :style="'color:'+item.color"  v-for="item in states " v-if="scope.row.state == item.value">{{ item.label }}</span>
        </template>
     
    </el-table-column>
    <el-table-column label="按钮" align="center" prop="btn"  min-width="120">
    </el-table-column>
    <el-table-column label="灯" align="center" prop="lampColor"  min-width="120">
    </el-table-column>
    <el-table-column label="亮灯模式" align="center" prop="lampMode"  min-width="120">
      <template slot-scope="scope">
          <span  :style="'color:'+item.color"  v-for="item in lampModes " v-if="scope.row.lampMode == item.value">{{ item.label }}</span>
        </template>
     
    </el-table-column>
    <el-table-column label="亮灯持续时间ms" align="center" prop="lampTime"  min-width="120">
      
    </el-table-column>
    <el-table-column label="控制类型" align="center" prop="lampCtrl"  min-width="120">
      <template slot-scope="scope">
          <span  :style="'color:'+item.color"  v-for="item in lampCtrls " v-if="scope.row.lampCtrl == item.value">{{ item.label }}</span>
        </template>
    </el-table-column>
    <el-table-column label="备注" align="center" prop="memo"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-task:CallBoxRecord:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-task:CallBoxRecord:remove']"
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

    <!-- 添加或修改呼叫盒记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="呼叫盒" prop="callBoxInfoId">
          <el-select v-model="form.callBoxInfoId" placeholder="呼叫盒" clearable>
            <el-option v-for="item in callBoxInfos" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择" clearable>
            <el-option v-for="item in states"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
     
        <!-- <el-form-item label="呼叫盒IP" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入呼叫盒IP" />
        </el-form-item> -->
        <el-form-item label="类型" prop="type">
 
          <el-radio v-for="item in types" v-model="form.type"   :label="item.value">{{ item.label }}</el-radio>     
 
          <!-- <el-select v-model="form.type" placeholder="请选择" clearable>
            <el-option v-for="item in types"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select> -->
        </el-form-item>
        <!-- <el-form-item label="0-按钮按下  1-控制灯" prop="type">
          <el-input v-model="form.type" placeholder="0-按钮按下  1-控制灯" />
        </el-form-item> -->
     
        <!-- <el-form-item label="状态" prop="state">
          <el-input v-model="form.state" placeholder="请输入状态  0-初始化 1-已经处理" />
        </el-form-item> -->
        <el-form-item  v-if="form.type==0"  label="按钮" prop="btn">
          <el-input-number :min="0" v-model="form.btn" placeholder="请输入按钮" />
        </el-form-item>
        <el-form-item v-if="form.type==1" label="灯" prop="lampColor">
          <el-input-number   :min="0"  v-model="form.lampColor" placeholder="请输入灯号" />
        </el-form-item>

             <!-- <el-form-item label="控制 0-灭灯 1-亮灯" prop="lampCtrl">
          <el-input v-model="form.lampCtrl" placeholder="请输入控制 0-灭灯 1-亮灯" />
        </el-form-item> -->
        <el-form-item  v-if="form.type==1"  label="控制类型" prop="lampCtrl">
          <el-radio v-for="item in lampCtrls" v-model="form.lampCtrl"   :label="item.value">{{ item.label }}</el-radio>  
          <!-- <el-select v-model="form.lampCtrl" placeholder="请选择" clearable>
            <el-option v-for="item in lampCtrls"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select> -->
        </el-form-item>
        <el-form-item  v-if="form.type==1 && form.lampCtrl==1"  label="亮灯模式" prop="lampMode">
          <el-radio v-for="item in lampModes" v-model="form.lampMode"   :label="item.value">{{ item.label }}</el-radio>     
          <!-- <el-select v-model="form.lampMode" placeholder="请选择" clearable>
            <el-option v-for="item in lampModes"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select> -->
        </el-form-item>
        <!-- <el-form-item label="0 - 常量 1-闪烁" prop="lampMode">
          <el-input v-model="form.lampMode" placeholder="请输入0 - 常量 1-闪烁" />
        </el-form-item> -->
        <el-form-item  v-if="form.type==1 &&  form.lampCtrl==1  "  label="时间 ms" prop="lampTime">
          <el-input v-model="form.lampTime" placeholder="请输入亮灯持续时间 ms" />
        </el-form-item>
     
   
        <el-form-item  v-if="form.type==1"  label="备注" prop="memo">
          <el-input v-model="form.memo" placeholder="请输入备注" />
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
import { listCallBoxRecord, getCallBoxRecord, delCallBoxRecord, addCallBoxRecord, updateCallBoxRecord } from "@/api/wcs-task/CallBoxRecord";
import request from "@/utils/request.js";
export default {
  name: "CallBoxRecord",
  data() {
    return {
      callBoxInfos:[],
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
      // 呼叫盒记录表格数据
      CallBoxRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        callBoxInfoId: null,
        code: null,
        ip: null,
        type: null,
        state: null,
        btn: null,
        lampColor: null,
        lampMode: null,
        lampTime: null,
        lampCtrl: null,
        memo: null
      },
      // 表单参数
      form: { type: 0,
        state: 0,},
      // 表单校验
      rules: {
      },
      types:[
        {value:0,label:"按钮按下",color:"#67C23A"}
        ,{value:1,label:"控制灯",color:"#E6A23C"}
      ],
      states:[  
        {value:0,label:"初始化",color:"#909399"}
        ,{value:1,label:"已经处理",color:"#409EFF"}
      ],
      lampModes:[
        {value:0,label:"常量",color:"#303133"}
        ,{value:1,label:"闪烁",color:"#303133"}
      ],
      lampCtrls:[
        {value:0,label:"灭灯",color:"#303133"}
        ,{value:1,label:"亮灯",color:"#67C23A"}
      ],
    
     
       
       
    };
  },
  created() {
    this.getList();
    this.getCallBoxInfos();
  },
  methods: {

     //查询所有呼叫盒的执行器
     getCallBoxInfos() {
      var that = this;
      request({
        url: "/wcs-task/CallBoxInfo/list",
        method: "get",
      }).then((response) => {
        if (response.code == 200) {
          that.callBoxInfos=response.rows;
        }
      });

    },
    /** 查询呼叫盒记录列表 */
    getList() {
      this.loading = true;
      listCallBoxRecord(this.queryParams).then(response => {
          if(response.code==200){
            this.CallBoxRecordList = response.rows;
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
        callBoxInfoId: null,
        code: null,
        ip: null,
        type: 0,
        state: 0,
        btn: null,
        lampColor: null,
        lampMode: null,
        lampTime: null,
        lampCtrl: null,
        memo: null
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
      this.title = "添加呼叫盒记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCallBoxRecord(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改呼叫盒记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCallBoxRecord(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addCallBoxRecord(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除呼叫盒记录编号为"' + ids + '"的数据项？').then(function() {
        return delCallBoxRecord(ids);
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
      this.download('wcs-task/CallBoxRecord/export', {
        ...this.queryParams
      }, `CallBoxRecord_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
