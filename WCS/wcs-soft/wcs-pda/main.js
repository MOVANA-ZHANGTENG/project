import Vue from 'vue'
import App from './App'
import store from './store' // store
import plugins from './plugins' // plugins
import './permission' // permission
import uView from '@/node_modules/uview-ui';

import tanchu from "./components/tanchu.vue"
Vue.component('tanchu', tanchu)


import chooseOut from "./components/chooseOut.vue"
Vue.component('chooseOut', chooseOut)

import changeList from "./components/changeList.vue"
Vue.component('changeList', changeList)


Vue.use(uView)
Vue.use(plugins)

Vue.config.productionTip = false
Vue.prototype.$store = store

App.mpType = 'app'

//网络请求
import requestServerData from './utils/requestServerData.js';
Vue.prototype.requestServerData = requestServerData

const app = new Vue({
  ...App
})

app.$mount()
