<template>
  <div class="container">

    <SelectModel v-model="wareCode" />
    <DsCellDdjView  v-if="wareInfo.code == 'mlk' || wareInfo.code == 'zlk'   "  :wareCode="wareInfo.code" ></DsCellDdjView>
    <CellDdjView v-else-if="wareInfo.type == 1" :wareCode="wareInfo.code" />
    <CellSxcView v-else-if="wareInfo.type == 2" :wareCode="wareInfo.code" />
  </div>
</template>

<script>
import request from "@/utils/request";
import CellDdjView from "./CellDdjView.vue";
import CellSxcView from "./CellSxcViewNew.vue";
import DsCellDdjView from "./wcs-ds/DsCellDdjView.vue";
import SelectModel from "./wcs-base/WareInfo/SelectModel.vue";
import go from "@/lib/js/go.js"
export default {
  name: "container",
  components: {
    CellDdjView,
    CellSxcView,
    SelectModel,
    DsCellDdjView
  },
  data() {
    return {
      wareCode: localStorage.getItem("wareCode") || null,
      wareInfo:{},
      wareInfos: []
    }
  },
  watch: {
    wareCode(newValue, oldValue) {
      localStorage.setItem("wareCode", newValue);
      this.getWareInfoByCode(newValue);
    }
  },
  created() {
    var that=this; 
    if(that.wareCode!=null){
      this.getWareInfoByCode(that.wareCode);
    }
  },
  methods: {
    getWareInfoByCode(wareCode) {
      var that = this;
      request({
        url: "/wcs-base/WareInfo/findByCode",
        method: "get",
        params: { code: wareCode },
      }).then((response) => {
        if (response.code == 200) {
          that.wareInfo = response.data; 
        } else {
          that.$modal.msgError(response.msg);
        }
      });
    }
  }
}

</script>

<style lang="scss" scoped>
.container {
  background: #0f0f1e;
  min-height: calc(100vh - 84px);
  position: relative;
  padding: 20px;
}
</style>
