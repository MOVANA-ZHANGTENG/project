<template>
  <div class="crontab-container">
    <el-tabs class="crontab-tabs">
      <el-tab-pane label="秒" v-if="shouldHide('second')">
        <CrontabSecond @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronsecond" />
      </el-tab-pane>

      <el-tab-pane label="分钟" v-if="shouldHide('min')">
        <CrontabMin @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronmin" />
      </el-tab-pane>

      <el-tab-pane label="小时" v-if="shouldHide('hour')">
        <CrontabHour @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronhour" />
      </el-tab-pane>

      <el-tab-pane label="日" v-if="shouldHide('day')">
        <CrontabDay @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronday" />
      </el-tab-pane>

      <el-tab-pane label="月" v-if="shouldHide('month')">
        <CrontabMonth @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronmonth" />
      </el-tab-pane>

      <el-tab-pane label="周" v-if="shouldHide('week')">
        <CrontabWeek @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronweek" />
      </el-tab-pane>

      <el-tab-pane label="年" v-if="shouldHide('year')">
        <CrontabYear @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronyear" />
      </el-tab-pane>
    </el-tabs>

    <div class="crontab-section">
      <span class="crontab-section-title">时间表达式</span>
      <div class="popup-result">
        <table>
          <thead>
            <tr>
              <th v-for="item of tabTitles" width="80" :key="item">{{ item }}</th>
              <th style="min-width: 200px;">Cron 表达式</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>
                <span>{{ crontabValueObj.second }}</span>
              </td>
              <td>
                <span>{{ crontabValueObj.min }}</span>
              </td>
              <td>
                <span>{{ crontabValueObj.hour }}</span>
              </td>
              <td>
                <span>{{ crontabValueObj.day }}</span>
              </td>
              <td>
                <span>{{ crontabValueObj.month }}</span>
              </td>
              <td>
                <span>{{ crontabValueObj.week }}</span>
              </td>
              <td>
                <span>{{ crontabValueObj.year }}</span>
              </td>
              <td>
                <span>{{ crontabValueString }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="crontab-section">
      <span class="crontab-section-title">最近5次运行时间</span>
      <CrontabResult :ex="crontabValueString"></CrontabResult>
    </div>

    <div class="crontab-actions">
      <el-button size="small" type="primary" @click="submitFill">确定</el-button>
      <el-button size="small" type="warning" @click="clearCron">重置</el-button>
      <el-button size="small" @click="hidePopup">取消</el-button>
    </div>
  </div>
</template>

<script>
import CrontabSecond from "./second.vue";
import CrontabMin from "./min.vue";
import CrontabHour from "./hour.vue";
import CrontabDay from "./day.vue";
import CrontabMonth from "./month.vue";
import CrontabWeek from "./week.vue";
import CrontabYear from "./year.vue";
import CrontabResult from "./result.vue";

export default {
  data() {
    return {
      tabTitles: ["秒", "分钟", "小时", "日", "月", "周", "年"],
      tabActive: 0,
      myindex: 0,
      crontabValueObj: {
        second: "*",
        min: "*",
        hour: "*",
        day: "*",
        month: "*",
        week: "?",
        year: "",
      },
    };
  },
  name: "vcrontab",
  props: ["expression", "hideComponent"],
  methods: {
    shouldHide(key) {
      if (this.hideComponent && this.hideComponent.includes(key)) return false;
      return true;
    },
    resolveExp() {
      // 反解析 表达式
      if (this.expression) {
        let arr = this.expression.split(" ");
        if (arr.length >= 6) {
          //6 位以上是合法表达式
          let obj = {
            second: arr[0],
            min: arr[1],
            hour: arr[2],
            day: arr[3],
            month: arr[4],
            week: arr[5],
            year: arr[6] ? arr[6] : "",
          };
          this.crontabValueObj = {
            ...obj,
          };
          for (let i in obj) {
            if (obj[i]) this.changeRadio(i, obj[i]);
          }
        }
      } else {
        // 没有传入的表达式 则还原
        this.clearCron();
      }
    },
    // tab切换值
    tabCheck(index) {
      this.tabActive = index;
    },
    // 由子组件触发，更改表达式组成的字段值
    updateCrontabValue(name, value, from) {
      "updateCrontabValue", name, value, from;
      this.crontabValueObj[name] = value;
      if (from && from !== name) {
        console.log(`来自组件 ${from} 改变了 ${name} ${value}`);
        this.changeRadio(name, value);
      }
    },
    // 赋值到组件
    changeRadio(name, value) {
      let arr = ["second", "min", "hour", "month"],
        refName = "cron" + name,
        insValue;

      if (!this.$refs[refName]) return;

      if (arr.includes(name)) {
        if (value === "*") {
          insValue = 1;
        } else if (value.indexOf("-") > -1) {
          let indexArr = value.split("-");
          isNaN(indexArr[0])
            ? (this.$refs[refName].cycle01 = 0)
            : (this.$refs[refName].cycle01 = indexArr[0]);
          this.$refs[refName].cycle02 = indexArr[1];
          insValue = 2;
        } else if (value.indexOf("/") > -1) {
          let indexArr = value.split("/");
          isNaN(indexArr[0])
            ? (this.$refs[refName].average01 = 0)
            : (this.$refs[refName].average01 = indexArr[0]);
          this.$refs[refName].average02 = indexArr[1];
          insValue = 3;
        } else {
          insValue = 4;
          this.$refs[refName].checkboxList = value.split(",");
        }
      } else if (name == "day") {
        if (value === "*") {
          insValue = 1;
        } else if (value == "?") {
          insValue = 2;
        } else if (value.indexOf("-") > -1) {
          let indexArr = value.split("-");
          isNaN(indexArr[0])
            ? (this.$refs[refName].cycle01 = 0)
            : (this.$refs[refName].cycle01 = indexArr[0]);
          this.$refs[refName].cycle02 = indexArr[1];
          insValue = 3;
        } else if (value.indexOf("/") > -1) {
          let indexArr = value.split("/");
          isNaN(indexArr[0])
            ? (this.$refs[refName].average01 = 0)
            : (this.$refs[refName].average01 = indexArr[0]);
          this.$refs[refName].average02 = indexArr[1];
          insValue = 4;
        } else if (value.indexOf("W") > -1) {
          let indexArr = value.split("W");
          isNaN(indexArr[0])
            ? (this.$refs[refName].workday = 0)
            : (this.$refs[refName].workday = indexArr[0]);
          insValue = 5;
        } else if (value === "L") {
          insValue = 6;
        } else {
          this.$refs[refName].checkboxList = value.split(",");
          insValue = 7;
        }
      } else if (name == "week") {
        if (value === "*") {
          insValue = 1;
        } else if (value == "?") {
          insValue = 2;
        } else if (value.indexOf("-") > -1) {
          let indexArr = value.split("-");
          isNaN(indexArr[0])
            ? (this.$refs[refName].cycle01 = 0)
            : (this.$refs[refName].cycle01 = indexArr[0]);
          this.$refs[refName].cycle02 = indexArr[1];
          insValue = 3;
        } else if (value.indexOf("#") > -1) {
          let indexArr = value.split("#");
          isNaN(indexArr[0])
            ? (this.$refs[refName].average01 = 1)
            : (this.$refs[refName].average01 = indexArr[0]);
          this.$refs[refName].average02 = indexArr[1];
          insValue = 4;
        } else if (value.indexOf("L") > -1) {
          let indexArr = value.split("L");
          isNaN(indexArr[0])
            ? (this.$refs[refName].weekday = 1)
            : (this.$refs[refName].weekday = indexArr[0]);
          insValue = 5;
        } else {
          this.$refs[refName].checkboxList = value.split(",");
          insValue = 6;
        }
      } else if (name == "year") {
        if (value == "") {
          insValue = 1;
        } else if (value == "*") {
          insValue = 2;
        } else if (value.indexOf("-") > -1) {
          insValue = 3;
        } else if (value.indexOf("/") > -1) {
          insValue = 4;
        } else {
          this.$refs[refName].checkboxList = value.split(",");
          insValue = 5;
        }
      }
      this.$refs[refName].radioValue = insValue;
    },
    // 表单选项的子组件校验数字格式（通过-props传递）
    checkNumber(value, minLimit, maxLimit) {
      // 检查必须为整数
      value = Math.floor(value);
      if (value < minLimit) {
        value = minLimit;
      } else if (value > maxLimit) {
        value = maxLimit;
      }
      return value;
    },
    // 隐藏弹窗
    hidePopup() {
      this.$emit("hide");
    },
    // 填充表达式
    submitFill() {
      this.$emit("fill", this.crontabValueString);
      this.hidePopup();
    },
    clearCron() {
      // 还原选择项
      ("准备还原");
      this.crontabValueObj = {
        second: "*",
        min: "*",
        hour: "*",
        day: "*",
        month: "*",
        week: "?",
        year: "",
      };
      for (let j in this.crontabValueObj) {
        this.changeRadio(j, this.crontabValueObj[j]);
      }
    },
  },
  computed: {
    crontabValueString: function () {
      let obj = this.crontabValueObj;
      let str =
        obj.second +
        " " +
        obj.min +
        " " +
        obj.hour +
        " " +
        obj.day +
        " " +
        obj.month +
        " " +
        obj.week +
        (obj.year == "" ? "" : " " + obj.year);
      return str;
    },
  },
  components: {
    CrontabSecond,
    CrontabMin,
    CrontabHour,
    CrontabDay,
    CrontabMonth,
    CrontabWeek,
    CrontabYear,
    CrontabResult,
  },
  watch: {
    expression: "resolveExp",
    hideComponent(value) {
      // 隐藏部分组件
    },
  },
  mounted: function () {
    this.resolveExp();
  },
};
</script>
<style scoped>
.crontab-container {
  padding: 10px;
  background: rgba(26, 26, 46, 0.95);
}

.crontab-tabs {
  border: 1px solid #4e5969;
  border-radius: 4px;
  margin-bottom: 20px;
  background-color: rgba(26, 26, 46, 0.95);
}

.crontab-tabs /deep/ .el-tabs__header {
  margin: 0;
  border-bottom: 1px solid #4e5969;
}

.crontab-tabs /deep/ .el-tabs__nav-wrap {
  padding: 0 20px;
}

/* 调整标签文字颜色 */
.crontab-tabs /deep/ .el-tabs__item {
  color: #c0c4cc;
}

.crontab-tabs /deep/ .el-tabs__item.is-active {
  color: #409eff;
}

.crontab-tabs /deep/ .el-tabs__content {
  padding: 20px;
}

.crontab-section {
  margin-bottom: 24px;
  padding: 20px;
  background-color: rgba(26, 26, 46, 0.95);
  border-radius: 4px;
  border: 1px solid #4e5969;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.crontab-section-title {
  font-size: 16px;
  font-weight: 500;
  color: #ffffff;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #4e5969;
  display: block;
}

.popup-result {
  box-sizing: border-box;
  margin: 0 0 20px 0;
  padding: 0;
  border: none;
  position: relative;
}

.popup-result .title {
  position: static;
  margin: 0 0 15px 0;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #c0c4cc;
  background: transparent;
}

.popup-result table {
  text-align: center;
  width: 100%;
  margin: 0;
  border: 1px solid #4e5969;
  border-collapse: collapse;
}

.popup-result table th {
  font-weight: 600;
  color: #ffffff;
  padding: 10px 5px;
  background-color: rgba(40, 40, 66, 0.8);
  border: 1px solid #4e5969;
}

.popup-result table td {
  border: 1px solid #4e5969;
  padding: 0;
}

.popup-result table span {
  display: block;
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 36px;
  height: 36px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: 1px solid #4e5969;
  background-color: rgba(40, 40, 66, 0.8);
  color: #ffffff;
  font-weight: 500;
  padding: 0 10px;
  font-size: 14px;
}

.popup-result-scroll {
  font-size: 14px;
  line-height: 36px;
  height: 150px;
  overflow-y: auto;
  background-color: rgba(40, 40, 66, 0.8);
  padding: 15px;
  border: 1px solid #4e5969;
  border-radius: 4px;
  margin-bottom: 15px;
}

.popup-result-scroll li {
  color: #ffffff !important;
  font-weight: 600;
  padding: 5px 10px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: rgba(26, 26, 46, 0.95);
  margin-bottom: 5px;
  border-radius: 3px;
  border-left: 3px solid #409eff;
}

.crontab-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 15px;
  border-top: 1px solid #4e5969;
  margin-top: 20px;
  background-color: rgba(26, 26, 46, 0.95);
}

.crontab-actions .el-button {
  margin-left: 12px;
}

/* Element UI 组件深色主题样式覆盖 */
.crontab-tabs /deep/ .el-form-item__label {
  color: #c0c4cc !important;
}

.crontab-tabs /deep/ input,
.crontab-tabs /deep/ select,
.crontab-tabs /deep/ .el-input__inner,
.crontab-tabs /deep/ .el-textarea__inner {
  background-color: rgba(40, 40, 66, 0.8) !important;
  border-color: #4e5969 !important;
  color: #ffffff !important;
}

.crontab-tabs /deep/ .el-input__inner::placeholder,
.crontab-tabs /deep/ .el-textarea__inner::placeholder {
  color: #8a919f !important;
}

.crontab-tabs /deep/ .el-radio__label,
.crontab-tabs /deep/ .el-checkbox__label {
  color: #c0c4cc;
}

.crontab-tabs /deep/ .el-radio__input.is-checked .el-radio__inner,
.crontab-tabs /deep/ .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #409eff !important;
  border-color: #409eff !important;
}

.crontab-tabs /deep/ .el-input-number .el-input-number__decrease,
.crontab-tabs /deep/ .el-input-number .el-input-number__increase {
  background-color: rgba(40, 40, 66, 0.8) !important;
  border-color: #4e5969 !important;
  color: #c0c4cc !important;
}

.crontab-tabs /deep/ .el-input-number .el-input-number__decrease:hover,
.crontab-tabs /deep/ .el-input-number .el-input-number__increase:hover {
  background-color: rgba(64, 158, 255, 0.1) !important;
  color: #409eff !important;
}

.crontab-tabs /deep/ .el-select .el-input.is-focus .el-input__inner {
  border-color: #409eff !important;
}

.crontab-tabs /deep/ .el-select-dropdown {
  background-color: rgba(40, 40, 66, 0.95) !important;
  border-color: #4e5969 !important;
}

.crontab-tabs /deep/ .el-select-dropdown__item {
  color: #c0c4cc !important;
}

.crontab-tabs /deep/ .el-select-dropdown__item:hover,
.crontab-tabs /deep/ .el-select-dropdown__item.hover,
.crontab-tabs /deep/ .el-select-dropdown__item.selected {
  background-color: rgba(64, 158, 255, 0.1) !important;
  color: #409eff !important;
}

/* 按钮样式适配 */
.crontab-actions /deep/ .el-button {
  background-color: rgba(64, 158, 255, 0.8);
  border-color: #409eff;
  color: #ffffff;
}

.crontab-actions /deep/ .el-button:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.crontab-actions /deep/ .el-button--warning {
  background-color: rgba(230, 162, 60, 0.8);
  border-color: #e6a23c;
}

.crontab-actions /deep/ .el-button--warning:hover {
  background-color: #ebb563;
  border-color: #ebb563;
}

.crontab-actions /deep/ .el-button--default {
  background-color: rgba(40, 40, 66, 0.8);
  border-color: #4e5969;
  color: #c0c4cc;
}

.crontab-actions /deep/ .el-button--default:hover {
  background-color: rgba(64, 158, 255, 0.1);
  border-color: #409eff;
  color: #409eff;
}

/* 自定义滚动条样式 */
.popup-result-scroll::-webkit-scrollbar {
  width: 6px;
}

.popup-result-scroll::-webkit-scrollbar-track {
  background: rgba(26, 26, 46, 0.95);
}

.popup-result-scroll::-webkit-scrollbar-thumb {
  background: #4e5969;
  border-radius: 3px;
}

.popup-result-scroll::-webkit-scrollbar-thumb:hover {
  background: #616e84;
}
</style>
