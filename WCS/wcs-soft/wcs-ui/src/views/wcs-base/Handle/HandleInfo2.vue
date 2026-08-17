<template>
  <div style="width: 100%">
    <el-card>
      <el-tag :key="tag" :disable-transitions="false" v-for="tag in value">
        <el-button type="primary"> {{ tag.name }}</el-button>
      </el-tag>
    </el-card>
  </div>
</template>

<script>
import {
  listHandle,
  getHandle,
  delHandle,
  addHandle,
  updateHandle,
} from "@/api/wcs-base/Handle";
import draggable from "vuedraggable";
export default {
  name: "Handle",
  data() {
    return {
      isUpdate: false,
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
        pageSize: 999,
        className: null,
        methodName: null,
        code: null,
        name: null,
        createUserId: null,
        createUserName: null,
        udpateTime: null,
        udpateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  props: {
    value: {
      type: Array,
      default: function () {
        return [];
      },
    },
  },
  watch: {
    // 使用handler函数进行深度监听
    value: {
      handler: function (newValue, oldValue) {
        this.$emit("input", newValue);
      },
      deep: true, // 开启深度监听
    },
  },
  components: {
    draggable,
  },
  created() {
    this.getList();
  },
  methods: {
    rigth() {
      debugger;
      this.value = this.value.filter(function (element) {
        return element.isSelect == null || element.isSelect == false;
      });
      this.value.forEach((element) => {
        element.isSelect = false;
      });

      // this.$emit("input", data);
    },
    left() {
      var data = this.value;
      this.HandleList.forEach((element) => {
        if (element.isSelect) {
          this.push(data, element);
          //data.push(element);
        }
      });
      data.forEach((element) => {
        element.isSelect = false;
      });

      // this.$emit("input", data);
    },
    has(code) {
      for (let index = 0; index < this.value.length; index++) {
        const element = this.value[index];
        if (element.code == code) {
          return true;
        }
      }
      return false;
    },
    push(data, value) {
      for (let index = 0; index < data.length; index++) {
        const element = data[index];
        if (element.code == value.code) {
          return;
        }
      }
      data.push(value);
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
        createTime: null,
        createUserId: null,
        createUserName: null,
        udpateTime: null,
        udpateUserId: null,
        updateUserName: null,
        isDelete: null,
        version: null,
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
      this.reset();
      this.open = true;
      this.title = "添加执行器";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getHandle(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改执行器";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateHandle(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addHandle(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
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
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
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
