<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-position="left" label-width="120px">
      <el-form-item label="站台id" prop="positionId">
        <el-select v-model="queryParams.positionId" placeholder="站台id" clearable>
          <el-option v-for="item in positionAll" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="产线id" prop="lineId">
        <el-select v-model="queryParams.lineId" placeholder="产线id" clearable>
          <el-option v-for="item in lineAll" :key="item.id" :label="item.lineName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="站台类型" prop="positionType">
        <el-select v-model="queryParams.positionType" placeholder="请选择站台类型" clearable>
          <el-option
            v-for="dict in positionTypes"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="适用产品型号ID" prop="modelId">
        <el-select v-model="queryParams.modelId" placeholder="请选择产品型号ID">
            <el-option
              v-for="dict in modelAll"
              :key="dict.id"
              :label="dict.modelName"
              :value="dict.id"
            ></el-option>
          </el-select>
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
          v-hasPermi="['wcs-xlPro:PositionInfoExtend:add']"
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
          v-hasPermi="['wcs-xlPro:PositionInfoExtend:edit']"
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
          v-hasPermi="['wcs-xlPro:PositionInfoExtend:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wcs-xlPro:PositionInfoExtend:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="PositionInfoExtendList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" min-width="100" />
    <el-table-column label="站台id" align="center" prop="positionId"  min-width="120">
      <!-- slot-scope="scope" 拿到当前行数据 -->
      <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.positionId" placeholder="请选择站台">
              <el-option
                v-for="item in positionAll"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
        </template>
    </el-table-column>
    <el-table-column label="产线id" align="center" prop="lineId"  min-width="120">
      <!-- slot-scope="scope" 拿到当前行数据 -->
      <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.lineId" placeholder="请选择产线">
              <el-option
                v-for="item in lineAll"
                :key="item.id"
                :label="item.lineName"
                :value="item.id">
              </el-option>
            </el-select>
        </template>
    </el-table-column>
      <el-table-column label="站台类型" align="center" prop="positionType"  min-width="120">
        <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.positionType" placeholder="请选择站台类型">
              <el-option
                v-for="item in positionTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
        </template>
      </el-table-column>
      <el-table-column label="适用产品型号ID" align="center" prop="modelId"  min-width="120">
     <template slot-scope="scope">
          <el-select @change="update(scope.row)" v-model="scope.row.modelId" placeholder="请选择适用产品型号ID">
              <el-option
                v-for="item in modelAll"
                :key="item.id"
                :label="item.modelName"
                :value="item.id">
              </el-option>
            </el-select>
        </template>
    </el-table-column>
    <el-table-column label="plc的ip" align="center" prop="plcIp" min-width="100" >
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.plcIp"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="plc的端口" align="center" prop="plcPort" min-width="100" >
      <template slot-scope="scope">
        <el-input @blur="update(scope.row)" v-model="scope.row.plcPort"></el-input>
      </template>
    </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-xlPro:PositionInfoExtend:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-xlPro:PositionInfoExtend:remove']"
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

    <!-- 添加或修改站台扩展对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="站台id" prop="positionId">
          <el-select v-model="form.positionId" placeholder="站台id" clearable>
            <el-option v-for="item in positionAll" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="产线id" prop="lineId">
          <el-select v-model="form.lineId" placeholder="产线id" clearable>
            <el-option v-for="item in lineAll" :key="item.id" :label="item.lineName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="站台类型" prop="positionType">
          <el-select v-model="form.positionType" placeholder="请选择站台类型" clearable>
            <el-option
              v-for="dict in positionTypes"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="适用产品型号ID" prop="modelId">
          <el-select v-model="form.modelId" placeholder="请选择产品型号ID">
            <el-option
              v-for="dict in modelAll"
              :key="dict.id"
              :label="dict.modelName"
              :value="dict.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="plc的ip" prop="plcIp">
          <el-input v-model="form.plcIp" placeholder="请输入plc的ip" />
        </el-form-item>
        <el-form-item label="plc的端口" prop="plcPort">
          <el-input v-model="form.plcPort" placeholder="请输入plc的端口" />
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
import { listPositionInfoExtend, getPositionInfoExtend, delPositionInfoExtend, addPositionInfoExtend, updatePositionInfoExtend,findPositionAll,findLineAll,findFixtureTypeAll } from "@/api/wcs-xlPro/PositionInfoExtend";
import { findModelAll } from "@/api/wcs-xlPro/ProcessRoute";
import request from "@/utils/request";
export default {
  name: "PositionInfoExtend",
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
      // 站台扩展表格数据
      PositionInfoExtendList: [],
      modelAll: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        positionId: null,
        lineId: null,
        positionType: null,
        currentFixtureTypeId: null,
      },
      positionAll: [],
      lineAll: [],
      fixtureTypeAll: [],
      positionTypes: [
        { value: "1", label: "机台" },
        { value: "2", label: "入口" },
        { value: "3", label: "出口" },
        { value: "4", label: "抽检口" },
        { value: "5", label: "机械臂" },
      ],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        currentFixtureTypeId: [
          {
            required: true,
            message: "当前夹具类型ID不能为空",
            trigger: "change",
            validator: (rule, value, callback) => {
              if (this.form.positionType === 1 || this.form.positionType === '1') {
                if (!value) {
                  return callback(new Error("当前夹具类型ID不能为空"));
                }
              }
              callback();
            }
          }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getPositionAll();
    this.getLineAll();
    this.getFixtureTypeAll();
    this.getModelAll();
  },
  
  watch: {
    'form.positionType': {
      handler(newVal) {
        // 当站台类型不是1时，清空夹具类型ID
        if (newVal !== 1 && newVal !== '1') {
          this.form.currentFixtureTypeId = null;
        }
      },
      immediate: true
    }
  },
  
  methods: {
    //查询所有的产品型号
    getModelAll(){
      findModelAll().then(response => {
          if(response.code==200){
            this.modelAll = response.data;
          }
      });
    },
    update(row) {
      updatePositionInfoExtend(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getList();
        } else {
          this.getList();
          this.$modal.msgError(response.msg);
          
        }
      });
    },
    //查询所有的夹具信息
    getFixtureTypeAll(){
      findFixtureTypeAll().then(response => {
          if(response.code==200){
            this.fixtureTypeAll = response.data;
          }
      });
    },
    //查询所有的站台编码
    getPositionAll(){
      findPositionAll().then(response => {
          if(response.code==200){
            this.positionAll = response.data;
          }
      });
    },
    getLineAll(){
      findLineAll().then(response => {
          if(response.code==200){
            this.lineAll = response.data;
          }
      });
    },
    /** 查询站台扩展列表 */
    getList() {
      this.loading = true;
      listPositionInfoExtend(this.queryParams).then(response => {
          if(response.code==200){
            this.PositionInfoExtendList = response.rows;
            //console.log(this.PositionInfoExtendList);
            this.total = response.total;
            this.getPositionAll();
            this.getLineAll();
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
        lineId: null,
        positionType: null,
        currentFixtureTypeId: null,
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加站台扩展";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPositionInfoExtend(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改站台扩展";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePositionInfoExtend(this.form).then(response => {
             if(response.code==200){
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addPositionInfoExtend(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除站台扩展编号为"' + ids + '"的数据项？').then(function() {
        return delPositionInfoExtend(ids);
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
      this.download('wcs-xlPro/PositionInfoExtend/export', {
        ...this.queryParams
      }, `PositionInfoExtend_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
