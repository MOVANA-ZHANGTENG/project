<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">

      <el-form-item label="仓库" prop="wareCode">
        <el-select v-model="queryParams.wareCode"
          placeholder="仓库" clearable>
          <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="料箱编码" prop="palletCode">
        <el-input v-model="queryParams.palletCode" placeholder="料箱编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="机器编码" prop="mixerLine">
        <el-input v-model="queryParams.mixerLine" placeholder="请输入机器编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="料袋ID" prop="bagId">
        <el-input v-model="queryParams.bagId" placeholder="请输入料袋ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="料袋编码" prop="orderId">
        <el-input v-model="queryParams.orderId" placeholder="请输入料袋编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="计划编号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入计划编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="配方代码" prop="recipeId">
        <el-input v-model="queryParams.recipeId" placeholder="请输入配方代码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="配方名称" prop="recipeName">
        <el-input v-model="queryParams.recipeName" placeholder="请输入配方名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="配方版本" prop="recipeRel">
        <el-input v-model="queryParams.recipeRel" placeholder="请输入配方版本" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="下达袋子" prop="recipeBagNo">
        <el-input v-model="queryParams.recipeBagNo" placeholder="请输入下达袋子" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="下达条码" prop="recipeBagName">
        <el-input v-model="queryParams.recipeBagName" placeholder="请输入下达条码" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="总重量" prop="setAllWeight">
        <el-input v-model="queryParams.setAllWeight" placeholder="请输入总重量" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="实际重量" prop="bagAllWeight">
        <el-input v-model="queryParams.bagAllWeight" placeholder="请输入实际重量" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="开始时间" prop="proTime">
        <el-date-picker clearable v-model="queryParams.proTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker clearable v-model="queryParams.endTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="创建时间" prop="dtCreateTime">
        <el-input v-model="queryParams.dtCreateTime" placeholder="请输入创建时间" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="料袋状态" prop="bagStatus">
        <el-input v-model="queryParams.bagStatus" placeholder="请输入料袋状态" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="执行状态" prop="csStatus">
        <el-input v-model="queryParams.csStatus" placeholder="请输入执行状态" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="是否过期" prop="type">
        <el-select v-model="queryParams.isExpired" placeholder="是否过期" clearable>
          <el-option    label="不过期" :value="0" />
          <el-option    label="过期" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-drawer
      title="托盘记录"
      size="50%"
      :visible.sync="palletVisible"
      :direction="'rtl'"
     >
      <PalletRecord :palletCode="palletCode"/>
    </el-drawer>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['wcs-ds:bagMaster:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['wcs-ds:bagMaster:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['wcs-ds:bagMaster:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['wcs-ds:bagMaster:export']">导出</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-box" size="mini" :disabled="multiple" @click="handleOutTask"
          v-hasPermi="['wcs-ds:bagMaster:remove']">出库</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bagMasterList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="expand">
      <template slot-scope="scope">
        <el-table v-loading="loading" :data="scope.row.details"  border>
            <el-table-column label="ID" align="center" prop="mOrderId" min-width="100" />
            <el-table-column label="成分计数" align="center" prop="tolMatCode"  min-width="120">
            </el-table-column>
            <el-table-column label="成分编码" align="center" prop="matCode"  min-width="120">
            </el-table-column>
            <el-table-column label="成分批次" align="center" prop="matLot"  min-width="120">
            </el-table-column>
            <el-table-column label="成分名称
        " align="center" prop="matName"  min-width="120">
            </el-table-column>
            <el-table-column label="称量公差" align="center" prop="tolerance"  min-width="120">
            </el-table-column>
            <el-table-column label="开料用量
        " align="center" prop="setWeight"  min-width="120">
            </el-table-column>
            <el-table-column label="实际称量" align="center" prop="actWeight"  min-width="120">
            </el-table-column>
              <el-table-column label="开始时间" align="center" prop="matStartTime" width="180">

              </el-table-column>
              <el-table-column label="结束时间" align="center" prop="matEndTime" width="180">

              </el-table-column>
            </el-table>
      </template>
    </el-table-column>
      <el-table-column label="箱号" align="center" prop="palletCode" min-width="120">
        <template slot-scope="scope">
        <span style="color:#409EFF" @click="palletVisible=true;palletCode=scope.row.palletCode">{{ scope.row.palletCode }}</span>

        </template>
      </el-table-column>
      <el-table-column label="库位" align="center" prop="cellCode" min-width="120">
      </el-table-column>
      <el-table-column label="仓库" align="center" prop="wareName" min-width="120">
      </el-table-column>
      <el-table-column label="料袋ID" align="center" prop="bagId" min-width="120">
      </el-table-column>
      <el-table-column label="ID" align="center" prop="id" min-width="100" />
      <el-table-column label="机器编码" align="center" prop="mixerLine" min-width="120">
      </el-table-column>
      <el-table-column label="料袋编码" align="center" prop="orderId" min-width="120">
      </el-table-column>
      <el-table-column label="计划编号" align="center" prop="orderNo" min-width="120">
      </el-table-column>
      <el-table-column label="配方代码" align="center" prop="recipeId" min-width="120">
      </el-table-column>
      <el-table-column label="配方名称" align="center" prop="recipeName" min-width="150">
      </el-table-column>
      <el-table-column label="配方版本" align="center" prop="recipeRel" min-width="120">
      </el-table-column>
      <el-table-column label="下达N料袋" align="center" prop="recipeBagNo" min-width="120">
      </el-table-column>
      <el-table-column label="下达条码" align="center" prop="recipeBagName" min-width="120">
      </el-table-column>
      <el-table-column label="总重量" align="center" prop="setAllWeight" min-width="120">
      </el-table-column>
      <el-table-column label="实际重量" align="center" prop="bagAllWeight" min-width="120">
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="proTime" width="180">

      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">

      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="dtCreateTime" min-width="180">
      </el-table-column>
      <el-table-column label="保质期" align="center" prop="ShelfLifeDays" min-width="180">
        <template slot-scope="scope">
          <span>
            {{ scope.row.shelfLifeDays === null ? '中间表无' : scope.row.shelfLifeDays }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="过期日期" align="center" prop="expiryDate" min-width="180">
        <template slot-scope="scope">
          <span>
            {{ scope.row.shelfLifeDays === null ? '中间表无' : scope.row.expiryDate }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="入库类型" align="center" prop="type" min-width="180">
        <template slot-scope="scope">
          <span>
            {{ scope.row.type === 1 ? '落料入库' : scope.row.type === 2 ? '返库入库' : '-' }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="入库类型" align="center" prop="isExpired" min-width="180">
        <template slot-scope="scope">
          <span>
            {{ scope.row.isExpired === 1 ? '过期' : scope.row.isExpired === 0 ? '不过期' : '-' }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="延期时间(小时)" align="center" prop="delayHours" min-width="140">
        <template slot-scope="scope">
          <span :style="{color: scope.row.delayHours > 0 ? '#E6A23C' : ''}">
            {{ scope.row.delayHours || 0 }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="冻结状态" align="center" prop="lockStatus" min-width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.lockStatus === '冻结'" type="danger" size="small">冻结</el-tag>
          <el-tag v-else-if="scope.row.lockStatus === '正常'" type="success" size="small">正常</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>

      <el-table-column label="料袋状态" align="center" prop="bagStatus" min-width="100">
      </el-table-column>
      <el-table-column label="执行状态" align="center" prop="csStatus" min-width="100">
      </el-table-column>
      <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds:bagMaster:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds:bagMaster:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改料袋主表对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="仓库名称" prop="wareCode">
          <el-select v-model="form.wareCode"   placeholder="请选择仓库"
            clearable>
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="配方编码" prop="recipeId">
          <el-input v-model="form.recipeId" placeholder="请输入配方编码" />
        </el-form-item>
        <el-form-item label="配方名称" prop="recipeName">
          <el-input v-model="form.recipeName" placeholder="请输入配方名称" />
        </el-form-item>
        <el-form-item label="料袋ID" prop="bagId">
          <el-input v-model="form.bagId" placeholder="请输入料袋ID" />
        </el-form-item>
        <el-form-item label="RFID编码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入RFID编码" />
        </el-form-item>
        <el-form-item label="开始时间" prop="proTime">
          <el-date-picker
            clearable
            v-model="form.proTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            clearable
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束时间"
            style="width: 100%">
          </el-date-picker>
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
import { listBagMaster, getBagMaster, delBagMaster, addBagMaster, updateBagMaster } from "@/api/wcs-ds/bagMaster";
import PalletRecord from "../..//wcs-task/PalletRecord/index.vue"
import request from "@/utils/request";
import { listWareInfo } from "@/api/wcs-base/WareInfo";
export default {
  name: "BagMaster",
  data() {
    return {
      wareInfos:[],
      palletVisible:false,
      palletCode:null,
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
      // 料袋主表表格数据
      bagMasterList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        wareCode: null,
        palletCode: null,
        mixerLine: null,
        bagId: null,
        orderId: null,
        orderNo: null,
        recipeId: null,
        recipeName: null,
        recipeRel: null,
        recipeBagNo: null,
        recipeBagName: null,
        setAllWeight: null,
        bagAllWeight: null,
        proTime: null,
        endTime: null,
        dtCreateTime: null,
        bagStatus: null,
        csStatus: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  components: {
    PalletRecord,
  },
  created() {
    this.getList();
    this.getWareInfos();
  },
  methods: {
     //获取所有仓库
     getWareInfos() {
      var query = { isDelete: 0 }
      listWareInfo(query).then((response) => {
        if (response.code == 200) {
          this.wareInfos = response.rows;
        }
      });
    },
    /** 查询料袋主表列表 */
    getList() {
      this.loading = true;
      listBagMaster(this.queryParams).then(response => {
        if (response.code == 200) {
          this.bagMasterList = response.rows;
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
        recipeId: null,
        recipeName: null,
        bagId: null,
        rfidCode: null,
        proTime: null,
        endTime: null
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
      this.ids = selection.map(item => item.bagMasterId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加料袋主表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.bagMasterId || this.ids
      getBagMaster(id).then(response => {
        if (response.code == 200) {
          this.form = response.data;
        }
        this.open = true;
        this.title = "修改料袋主表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateBagMaster(this.form).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addBagMaster(this.form).then(response => {
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
      const ids = row.bagMasterId || this.ids;
      this.$modal.confirm('是否确认删除料袋主表编号为"' + ids + '"的数据项？').then(function () {
        return delBagMaster(ids);
      }).then((response) => {
        if (response.code == 200) {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        } else {
          this.$modal.msgError(response.msg || "删除失败")
        }
      }).catch(() => { });
    },
    handleOutTask(row){
      const ids = row.bagMasterId || this.ids;
      console.info(ids);
      this.$confirm("确定出库?", '出库', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          request({
            url: "/wcs-ds/bagMaster/outTask/"+ids,
            method: "get",
            params: { },
          }).then((response) => {
            if (response.code == 200) {
              this.getList();
            }else{
              this.$modal.msgError(response.msg||"出库失败");
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          });
        });



    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wcs-ds/bagMaster/export', {
        ...this.queryParams
      }, `bagMaster_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
