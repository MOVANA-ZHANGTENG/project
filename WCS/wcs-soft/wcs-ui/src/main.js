import Vue from "vue";

import Cookies from "js-cookie";

import Element from "element-ui";
import "./assets/styles/element-variables.scss";
// 将自动注册所有组件为全局组件
import * as echarts from "echarts";
//国际化
import i18n from './lang';

import "@/assets/styles/index.scss"; // global css
import "@/assets/styles/ruoyi.scss"; // ruoyi css
import App from "./App";
import store from "./store";
import router from "./router";
import directive from "./directive"; // directive
import plugins from "./plugins"; // plugins
import dataV from "@jiaminghi/data-view";
import "./assets/icons"; // icon
import "./permission"; // permission control
import { getDicts } from "@/api/system/dict/data";
import { getConfigKey } from "@/api/system/config";
import { download } from "@/utils/request";
import { parseTime,resetForm,addDateRange,selectDictLabel,selectDictLabels,handleTree,} from "@/utils/ruoyi";
// 分页组件
import Pagination from "@/components/Pagination";
// 自定义表格工具组件
import RightToolbar from "@/components/RightToolbar";
// 富文本组件
import Editor from "@/components/Editor";
// 文件上传组件
import FileUpload from "@/components/FileUpload";
// 图片上传组件
import ImageUpload from "@/components/ImageUpload";
// 图片预览组件
import ImagePreview from "@/components/ImagePreview";
// 字典标签组件
import DictTag from "@/components/DictTag";
// 头部标签组件
import VueMeta from "vue-meta";
// 字典数据组件
import DictData from "@/components/DictData";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import jquery from "./lib/js/jq.js";
// 格式化json字符串显示
import JsonViewer from 'vue-json-viewer'
// 导入拖拽指令插件 - 自动为所有Element UI对话框添加拖拽功能
import dialogDragPlugin from "@/assets/dialog/drag";

// 全局方法挂载
Vue.prototype.getDicts = getDicts;
Vue.prototype.getConfigKey = getConfigKey;
Vue.prototype.parseTime = parseTime;
Vue.prototype.resetForm = resetForm;
Vue.prototype.addDateRange = addDateRange;
Vue.prototype.selectDictLabel = selectDictLabel;
Vue.prototype.selectDictLabels = selectDictLabels;
Vue.prototype.download = download;
Vue.prototype.handleTree = handleTree;
Vue.prototype.$echarts = echarts;

// 插件使用
Vue.use(dialogDragPlugin);
// 全局组件挂载
Vue.component("DictTag", DictTag);
Vue.component("Pagination", Pagination);
Vue.component("RightToolbar", RightToolbar);
Vue.component("Editor", Editor);
Vue.component("FileUpload", FileUpload);
Vue.component("ImageUpload", ImageUpload);
Vue.component("ImagePreview", ImagePreview);
Vue.component("Treeselect", Treeselect);

Vue.use(directive);
Vue.use(plugins);
Vue.use(VueMeta);
Vue.use(dataV);
Vue.use(jquery);
// jQuery UI不是Vue插件，不需要使用Vue.use()加载
Vue.use(JsonViewer)

DictData.install();

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Element.Dialog.props.closeOnClickModal.default = false; //弹框点及其他区域不关闭
Vue.use(Element, {
  i18n: (key, value) => i18n.t(key, value),
  size: Cookies.get('size') || 'medium'
});

Vue.config.productionTip = false;

// 只创建一个Vue实例
new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)
});
