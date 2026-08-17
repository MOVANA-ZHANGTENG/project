<template>
  <div class="containerSxc">
    <el-row :gutter="0">
      <!-- 左侧：模式切换 + 楼层选择 -->
      <el-col :span="3">
        <el-card class="sidebar-card">
          <!-- 模式切换按钮 -->
          <div class="mode-toggle-sidebar">
            <div class="mode-header">
              <i class="el-icon-setting"></i>
              <span>显示模式</span>
            </div>
            <el-button 
              size="small" 
              :type="displayMode === 'normal' ? 'primary' : 'default'"
              @click="switchDisplayMode('normal')"
              class="mode-btn"
            >
              <i class="el-icon-document"></i>
              库位模式
            </el-button>
            <el-button 
              size="small" 
              :type="displayMode === 'link' ? 'primary' : 'default'"
              @click="switchDisplayMode('link')"
              class="mode-btn"
            >
              <i class="el-icon-edit"></i>
              编辑模式
            </el-button>
            <el-button 
              size="small" 
              :type="displayMode === 'monitor' ? 'primary' : 'default'"
              @click="switchDisplayMode('monitor')"
              class="mode-btn"
            >
              <i class="el-icon-view"></i>
              监控模式
            </el-button>
          </div>
          
          <!-- 楼层选择 -->
          <div class="floor-header">
            <i class="el-icon-office-building"></i>
            <span>楼层选择</span>
          </div>

          <el-row :gutter="16" class="floor-container">
            <el-col v-for="item in floors" :key="item.z" :span="24" @click.native="selectFloor(item.z)">
              <div class="floor-card" :class="{ active: floor === item.z }">
                <div class="card-left">
                  <i class="el-icon-guide"></i>
                  <span class="floor-title">第 {{ item.z }} 层</span>
                </div>
                <div class="card-right">
                  <!-- <el-tag v-if="floor === item.z" type="success" effect="dark" size="small">
                    选中
                  </el-tag> -->
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      
      <!-- 中间和右侧：由子组件自己管理布局 -->
      <el-col :span="21">
        <!-- 动态组件：根据displayMode切换不同的显示模式 -->
        <component 
          :is="currentModeComponent"
          :ware-code="wareCode"
          :floor="floor"
          :grid-size="gridSize"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listCellInfo } from "@/api/wcs-base/CellInfo";
import request from "@/utils/request";

// 导入三个模式组件
import CellNormalView from "./cell-modes/CellNormalView.vue";
import CellEditView from "./cell-modes/CellEditView.vue";
import CellMonitorView from "./cell-modes/CellMonitorView.vue";

export default {
  name: "CellSxcViewNew",
  components: {
    CellNormalView,
    CellEditView,
    CellMonitorView
  },
  props: {
    wareCode: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      displayMode: 'normal', // 'normal' | 'link' | 'monitor'
      floors: [],
      floor: null,
      
      gridSize: {
        width: 50,
        height: 50,
      },
    };
  },
  computed: {
    // 根据displayMode返回对应的组件名称
    currentModeComponent() {
      const modeMap = {
        'normal': 'CellNormalView',
        'link': 'CellEditView',
        'monitor': 'CellMonitorView'
      };
      return modeMap[this.displayMode] || 'CellNormalView';
    }
  },
  watch: {
    wareCode(newValue, oldValue) {
      if (newValue) {
        this.getFloors(newValue);
      }
    },
    // floor 变化由子组件自己监听，父组件不需要做额外处理
  },
  created() {
    if (this.wareCode) {
      this.getFloors(this.wareCode);
    }
    
    // 定时刷新由子组件自己管理
  },
  methods: {
    // 切换显示模式
    switchDisplayMode(mode) {
      this.displayMode = mode;
      this.$message.success(`已切换到${mode === 'normal' ? '库位模式' : mode === 'link' ? '编辑' : '监控'}模式`);
    },
    
    // 选择楼层
    selectFloor(z) {
      this.floor = z;
    },
    
    // 获取楼层列表
    getFloors(wareCode) {
      request({
        url: "/wcs-base/FloorInfo/list",
        method: "get",
        params: { wareCode: wareCode },
      }).then((response) => {
        if (response.code == 200) {
          this.floors = response.rows;
          if (this.floors.length > 0) {
            this.floor = this.floors[0].z;
          }
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
  }
};
</script>

<style scoped>
@import './CellSxcView-styles.css';
</style>

