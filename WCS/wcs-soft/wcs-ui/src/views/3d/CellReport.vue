<template>
  <div class="cell-container">
    <div class="name">
      库位
    </div>
    <el-row>
      <el-col   :span="9">
        <dv-active-ring-chart :config="config" style="width:100%; height: 10vh;" />
         
      </el-col>
      <el-col   :span="15">
        <div style="margin-bottom: 8px;" v-for="data in config.data" :key="data.name">
          <el-row>
            <el-col   :span="3">
              <div :style="'width:12px;height:12px;border-radius: 6px;background-color: '+data.color+'; opacity: 0.8;'"></div> 
            </el-col>
            <el-col   :span="8">
              <div style="color: #7ea8c8;font-size: 1.1vh;">{{ data.name }}</div> 
            </el-col>
            <el-col   :span="6">
              <div style="color: #e8f4f8;font-size: 1.2vh;font-weight: 500;">{{ data.value }}</div> 
            </el-col>
            <el-col   :span="7">
              <div style="color: #b8d4e8;font-size: 1.1vh;">{{ data.value2 }}</div> 
            </el-col>
            
          </el-row>
        </div>
       
         
      </el-col>
      
    </el-row>
  </div>
</template>

<script>
import * as echarts from "echarts";
export default {
  name: "cell-container",

  data() {
    return {
      
      config: {
        color: ['#3d85c6', '#7e37d9', '#7d6518', '#4a81a9', '#658d53'],
        radius: '40%',
        activeRadius: '45%',
        data: [
          {
            name: '高温库',
            value: 150,
            value2: "200",
            color:"#F56C6C"
          },
          {
            name: '常温库',
            value: 30,
            value2: "200",
            color:"#E6A23C"
          },
          {
            name: '成品库',
            value: 20,
            value2: "200",
            color:"#67C23A"
          }
          
        ],
        lineWidth: 5,
        digitalFlopStyle: {
          fontSize: 0
        },
        showOriginValue: true
      }
    };
  },
  mounted() {},
  methods: {},
};
</script>

<style lang="scss" scoped>
.cell-container {
  padding: 0;
  width: 100%;
  height: auto;
  background: transparent;

  .name{
    color: #e8f4f8;
    font-size: 1.6vh;
    font-weight: 500;
    margin-bottom: 0.8vh;
    padding-left: 0.5vw;
    text-shadow: 0 0 3px rgba(255, 255, 255, 0.2);
    letter-spacing: 1px;
    
    // 添加微妙的左边框装饰
    border-left: 2px solid rgba(0, 150, 255, 0.3);
  }
  
  // 优化数据行样式
  ::v-deep .el-row {
    .el-col {
      div {
        color: #b8d4e8 !important;
        font-size: 1.1vh !important;
      }
    }
  }
}
</style>