<template>
  <div class="app-container">
    <el-row>

  <el-col :span="16">
    <!-- <el-card style="margin-left: 10px;margin-right: 10px;margin-bottom: 10px;">
      <el-row>
        <el-col   v-for="position in positionInfos" :span="6">
          <div class="position" v-if="position.invenState==queryParams.planId &&position.taskState!=queryParams.planId  " style="background-color: #409EFF;">
            <div>{{ position.code }}</div>
            -
            <div>{{ position.invenState }}</div>
          </div>
          <div class="position" v-else-if="position.taskState==queryParams.planId " style="background-color: #67C23A;">
            <div>{{ position.code }}</div>
            -
            <div>{{ position.invenState }}</div>
          </div>
          <div class="position" v-else-if="position.invenState>0" style="background-color: #E6A23C;">
            <div>{{ position.code }}</div>
            -
            <div>{{ position.invenState }}</div>
          </div>
          <div class="position" v-else-if="position.invenState==0 " style="background-color: #909399;">
            <div>{{ position.code }}</div>
          </div>

        </el-col>
      </el-row>
    </el-card> -->

    <el-card style="margin-left: 10px;margin-right: 10px;margin-bottom: 10px;">
      <el-row>
        <el-col    v-if="plan!=null && plan.deviceId!=undefined && plan.deviceId!=null"  v-for="position in positionInfos" :span="6">
          <div @click="allotPosition(position)"  v class="position" v-if="position.parentCode!=null  && position.parentCode!='' && position.parentCode!=plan.deviceId   " style="background-color:brown;">
            <div>{{ position.code }}</div>
            -
            <div>{{ position.parentCode}}</div> 
          </div>
          <div @click="allotPosition(position)"  v class="position" v-else-if="position.parentCode==plan.deviceId " style="background-color: #67C23A;">
            <div>{{ position.code }}</div>
            -
            <div>{{ position.parentCode}}</div> 
          </div>
          <div @click="allotPosition(position)"  v class="position" v-else-if="position.parentCode==plan.deviceId " style="background-color: #E6A23C;">
            <div>{{ position.code }}</div>
            -
            <div>{{ position.parentCode}}</div> 
          </div>
          <div @click="allotPosition(position)"  v class="position" v-if="position.parentCode==null  || position.parentCode==''  " style="background-color: #909399;">
            <div>{{ position.code }}</div>
            -
            <div>{{ position.parentCode}}</div> 
          </div>
      
        </el-col>
      </el-row>
    </el-card>
    <el-card style="margin-left: 10px;margin-right: 10px">
    <el-table v-loading="loading" :data="DsPlanPalletList"   border>
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!-- <el-table-column label="ID" align="center" prop="id" min-width="100" />
    <el-table-column label="计划ID" align="center" prop="planId"  min-width="120">
    </el-table-column> -->
    <el-table-column label="托盘码" align="center" prop="palletCode"  min-width="120">
    </el-table-column>
    <el-table-column label="仓库" align="center" prop="wareName" min-width="120">
      </el-table-column>
      <el-table-column label="当前位置" align="center" prop="cellCode" min-width="120">
        <template slot-scope="scope">
          <span   v-if="scope.row.state>2">- </span>
          <span   v-else >{{ scope.row.cellCode }} </span>
        </template>
      </el-table-column>
    <el-table-column label="出库口" align="center" prop="toCode"  min-width="120">
    </el-table-column>
    <el-table-column label="状态" align="center" prop="state"  min-width="120">
      <template slot-scope="scope">
          <span v-for="item in states" v-if="scope.row.state==item.value" :style="'color:'+item.color">{{item.label }}</span>
        </template>
    </el-table-column>
    <el-table-column label="下架时间" align="center" prop="downTime"  min-width="120">
    </el-table-column>
    <el-table-column label="上AGV时间" align="center" prop="agvTime"  min-width="120">
    </el-table-column>
    <el-table-column label="上密炼机时间" align="center" prop="mljTime"  min-width="120">
    </el-table-column>
    <el-table-column label="投料时间" align="center" prop="tlTime"  min-width="120">
    </el-table-column>
      <!-- <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wcs-ds:DsPlanPallet:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wcs-ds:DsPlanPallet:remove']"
          >删除</el-button>
        </template>
      </el-table-column> -->
    </el-table>
  </el-card>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </el-col>
  <el-col :span="8">
    <el-card header="任务履历">
          <el-timeline>
            <!-- job -->
            <el-timeline-item type="primary" size="large" v-loading="loading" :timestamp="record.createTime"
              v-for="record in records" placement="top">
              <!-- <el-card shadow="hover">
                {{ record.content }}
              </el-card> -->
              {{ record.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
  </el-col>
</el-row>


    <!-- 添加或修改计划分配料箱对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="计划ID" prop="planId">
          <el-input v-model="form.planId" placeholder="请输入计划ID" />
        </el-form-item>
        <el-form-item label="托盘码" prop="palletCode">
          <el-input v-model="form.palletCode" placeholder="请输入托盘码" />
        </el-form-item>
        <el-form-item label="0-初始化 1-下架中 2-agv 3-投料中 4-已经投料 " prop="state">
          <el-input v-model="form.state" placeholder="请输入0-初始化 1-下架中 2-agv 3-投料中 4-已经投料 " />
        </el-form-item>
        <el-form-item label="下架时间" prop="downTime">
          <el-input v-model="form.downTime" placeholder="请输入下架时间" />
        </el-form-item>
        <el-form-item label="上AGV时间" prop="agvTime">
          <el-input v-model="form.agvTime" placeholder="请输入上AGV时间" />
        </el-form-item>
        <el-form-item label="上密炼机时间" prop="mljTime">
          <el-input v-model="form.mljTime" placeholder="请输入上密炼机时间" />
        </el-form-item>
        <el-form-item label="投料时间" prop="tlTime">
          <el-input v-model="form.tlTime" placeholder="请输入投料时间" />
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
import { listDsPlanPallet, getDsPlanPallet, delDsPlanPallet, addDsPlanPallet, updateDsPlanPallet } from "@/api/wcs-ds/DsPlanPallet";
import request from "@/utils/request";
export default {
  name: "DsPlanPallet",
  data() {
    return {
      //0-初始化 1-下架中 2-agv 3-投料中 4-已经投料
      states:[
        {value: 0, label: '未开始',color: 'primary'}
        ,    {value: 1, label: '下架中',color: '#409EFF'}
        ,    {value: 2, label: '到达出库口',color: '#E6A23C'}
        ,    {value: 3, label: '已上AGV',color: '#67C23A'}
        ,    {value: 4, label: '已经投料',color: '#000000'}
      ],
      positionInfos:[],
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
      // 计划分配料箱表格数据
      DsPlanPalletList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        planId: null,
        palletCode: null,
        state: null,
        downTime: null,
        agvTime: null,
        mljTime: null,
        tlTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      timer:[],
      records:[],
      deviceId:null,
      plan:{},
      wareCode:null,
    };
  },
  created() {
    var planId = this.$route.params.planId;
    this.getPlan(planId);
    this.deviceId = this.$route.params.deviceId;
    this.queryParams.planId = planId;
    this.getList( );
    this.recordList( );
    this.timer = setInterval(() => {
      this.recordList( );
      this.getList( );
    }, 5000);
  },
  beforeDestroy() {
    if (this.timer) {
      //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
  },
  methods: {

    allotPosition(position) {
      console.log(position);
      this.$confirm('绑定"' + this.plan.deviceId + '到'+position.code+" ?", '绑定', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          request({
            url: "/wcs-ds/DsRealPlan/allotPosition",
            method: "get",
            params: { deviceId: this.plan.deviceId,positionCode:position.code  },
          }).then((response) => {
            if (response.code == 200) {
              this.getPositionInfos();
            }else{
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
    getPlan(planId ) {
      request({
        url: "/wcs-ds/DsRealPlan/"+planId,
        method: "get",
        params: {  },
      }).then((response) => {
        if (response.code == 200) {
          this.plan = response.data;
        }
      });
    },
    getPositionInfos(  ) {
      request({
        url: "/wcs-ds/DsPlanPallet/findMxPositionInfoByWareCode",
        method: "get",
        params: { wareCode: this.wareCode },
      }).then((response) => {
        if (response.code == 200) {
          this.positionInfos = response.data;
        }
      });
    },
    recordList( ) {
      request({
        url: "/bill_record/findByBillNo",
        method: "get",
        params: { billNo: this.queryParams.planId },
      }).then((response) => {
        if (response.code == 200) {
          this.records = response.data;
          this.loading = false;
        }
      });
    },
    /** 查询计划分配料箱列表 */
    getList() {
      this.loading = true;
      listDsPlanPallet(this.queryParams).then(response => {
          if(response.code==200){
            this.DsPlanPalletList = response.rows;
            this.total = response.total;
            if(response.rows.length>0){
              this.wareCode = response.rows[0].wareCode;
              this.getPositionInfos( );
            }
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
        planId: null,
        palletCode: null,
        state: null,
        createTime: null,
        downTime: null,
        agvTime: null,
        mljTime: null,
        tlTime: null
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
      this.title = "添加计划分配料箱";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDsPlanPallet(id).then(response => {
          if(response.code==200){
            this.form = response.data;
          }
        this.open = true;
        this.title = "修改计划分配料箱";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading=true;
          if (this.form.id != null) {
            updateDsPlanPallet(this.form).then(response => {
              this.loading=false;
             if(response.code==200){
                  this.$modal.msgSuccess("修改成功");
                 this.open = false;
                 this.getList();
              }else{
                 this.$modal.msgError(response.msg||"修改失败");
              }
            });
          } else {
            addDsPlanPallet(this.form).then(response => {
              this.loading=false;
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
      this.$modal.confirm('是否确认删除计划分配料箱编号为"' + ids + '"的数据项？').then(function() {
        return delDsPlanPallet(ids);
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
      this.download('wcs-ds/DsPlanPallet/export', {
        ...this.queryParams
      }, `DsPlanPallet_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>


<style lang="scss" scoped>
  .position {
    width: 90%;
    margin-right: 10%;
    height: 10vh;
    border: 1cm;
    border-radius: 10px;

    /* 2. 内部内容居中：使用flex布局（通用且简单） */
    display: flex; /* 开启flex布局 */
    justify-content: center; /* 水平居中 */
    align-items: center; /* 垂直居中 */
  }
</style>
