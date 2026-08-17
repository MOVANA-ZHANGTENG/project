<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="密炼机编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入密炼机编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="密炼机名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入密炼机名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库编码" prop="wareCode">
        <el-input
          v-model="queryParams.wareCode"
          placeholder="请输入仓库编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库名称" prop="wareName">
        <el-input
          v-model="queryParams.wareName"
          placeholder="请输入仓库名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="校称的砝码重量" prop="actualWeight">
        <el-input
          v-model="queryParams.actualWeight"
          placeholder="请输入校称的砝码重量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="电子秤生产日期" prop="produceDate">
        <el-input
          v-model="queryParams.produceDate"
          placeholder="请输入电子秤生产日期"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="站台编码" prop="positionCode">
        <el-input
          v-model="queryParams.positionCode"
          placeholder="请输入站台编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编码" prop="deviceCode">
        <el-input
          v-model="queryParams.deviceCode"
          placeholder="请输入设备编码"
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
          v-hasPermi="['wcs-ds:DsMljInfo:add']"
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
          v-hasPermi="['wcs-ds:DsMljInfo:edit']"
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
          v-hasPermi="['wcs-ds:DsMljInfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-ds:DsMljInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="DsMljInfoList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
    <el-table-column label="密炼机编码" align="center" prop="code"  min-width="120">
    </el-table-column>
    <el-table-column label="密炼机名称" align="center" prop="name"  min-width="120">
    </el-table-column>
    <el-table-column label="仓库编码" align="center" prop="wareCode"  min-width="120">
    </el-table-column>
    <el-table-column label="仓库名称" align="center" prop="wareName"  min-width="120">
    </el-table-column>
    <el-table-column label="校称的砝码重量" align="center" prop="actualWeight"  min-width="120">
    </el-table-column>
    <el-table-column label="电子秤生产日期" align="center" prop="produceDate"  min-width="120">
    </el-table-column>
    <el-table-column label="站台编码" align="center" prop="positionCode"  min-width="120">
    </el-table-column>
    <el-table-column label="出库口" align="center" prop="outPositionCode"  min-width="120">
      <template slot-scope="scope">
        <el-select      @change="allotPosition(scope.row.code,scope.row.outPositionCode)" v-model="scope.row.outPositionCode" placeholder="">
            <el-option
        
            :disabled="item.wareCode != scope.row.wareCode"
              v-for="item in positionInfos"
              :key="item.code"
              :label="item.code"
              :value="item.code">
            </el-option>
          </el-select>
      </template>
    </el-table-column>
    
    
    <el-table-column label="设备编码" align="center" prop="deviceCode"  min-width="120">
    </el-table-column>
    <el-table-column label="预警比例" align="center" prop="rate"  min-width="120">
    </el-table-column>
    <el-table-column label="PLC_IP" align="center" prop="ip"  min-width="120">
    </el-table-column>
    <el-table-column label="PLC_连接状态" align="center" prop="state"  min-width="120">
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds:DsMljInfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds:DsMljInfo:remove']"
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

    <!-- 添加或修改密炼机对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="密炼机编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入密炼机编码" />
        </el-form-item>
        <el-form-item label="密炼机名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入密炼机名称" />
        </el-form-item>
        <el-form-item label="仓库编码" prop="wareCode">
          <el-input v-model="form.wareCode" placeholder="请输入仓库编码" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="wareName">
          <el-input v-model="form.wareName" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="校称的砝码重量" prop="actualWeight">
          <el-input v-model="form.actualWeight" placeholder="请输入校称的砝码重量" />
        </el-form-item>
        <el-form-item label="电子秤生产日期" prop="produceDate">
          <el-input v-model="form.produceDate" placeholder="请输入电子秤生产日期" />
        </el-form-item>
        <el-form-item label="站台编码" prop="positionCode">
          <el-input v-model="form.positionCode" placeholder="请输入站台编码" />
        </el-form-item>
        <el-form-item label="设备编码" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item label="预警比例" prop="rate">
          <el-input v-model="form.rate" placeholder="预警比例" />
        </el-form-item>
        <el-form-item label="PLC_IP" prop="ip">
          <el-input v-model="form.ip" placeholder="PLC_IP" />
        </el-form-item>
        <el-form-item label="PLC_连接状态" prop="state">
          <el-input v-model="form.state" placeholder="PLC_连接状态" />
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
import { listDsMljInfo, getDsMljInfo, delDsMljInfo, addDsMljInfo, updateDsMljInfo } from "@/api/wcs-ds/DsMljInfo";
import request from "@/utils/request";
export default {
  name: "DsMljInfo",
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
      // 密炼机表格数据
      DsMljInfoList: [],
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
        wareCode: null,
        wareName: null,
        actualWeight: null,
        produceDate: null,
        positionCode: null,
        deviceCode: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      positionInfos:[],
    };
  },
  created() {
    this.getList();
    this.getPositions();
  },
  methods: {

    allotPosition(deviceId,positionCode) { 
      this.$confirm('绑定"' + deviceId + '到'+positionCode+" ?", '绑定', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          request({
            url: "/wcs-ds/DsRealPlan/allotPosition",
            method: "get",
            params: { deviceId: deviceId,positionCode:positionCode  },
          }).then((response) => {
            if (response.code == 200) {
              this.getList();
            }else{
              this.getList();
              this.$modal.msgError(response.msg||"绑定失败");
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          });          
        });
     
     
    },
    getPositions(){
      request({
            url: "/wcs-base/PositionInfo/list",
            method: "get",
            params: { type:4  },
          }).then((response) => {
            if (response.code == 200) {
              this.positionInfos=response.rows; 
              this.getList();
            } else {
              this.getList();
              this.$modal.msgError(response.msg );
            }
          });
    },
    /** 查询密炼机列表 */
    getList() {
      this.loading = true;
      listDsMljInfo(this.queryParams).then(response => {
          if(response.code==200){
            this.DsMljInfoList = response.rows;
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
        actualWeight: null,
        produceDate: null,
        positionCode: null,
        deviceCode: null
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
      this.title = "添加密炼机";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDsMljInfo(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改密炼机";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDsMljInfo(this.form).then(response => {
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addDsMljInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除密炼机编号为"' + ids + '"的数据项？').then(function() {
        return delDsMljInfo(ids);
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
      this.download('wcs-ds/DsMljInfo/export', {
        ...this.queryParams
      }, `DsMljInfo_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
