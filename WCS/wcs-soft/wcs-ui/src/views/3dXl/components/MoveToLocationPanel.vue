<template>
  <div class="move-to-location-panel">
    <div class="panel-header">
      <div class="header-title">
        <i class="el-icon-location"></i>
        <span>货物移动到指定位置</span>
      </div>
    </div>

    <div class="panel-content">
      <!-- 货位编码输入 -->
      <div class="input-section">
        <div class="input-label">
          <span>货位编码:</span>
        </div>
        <div class="input-wrapper">
          <el-input 
            v-model="locationCode" 
            placeholder="请输入货位编码 (格式: 货架号-列数-层数)" 
            size="small"
            clearable
            @keyup.enter="moveCargoToLocation"
          >
            <i slot="prefix" class="el-input__icon el-icon-box"></i>
          </el-input>
        </div>
        <div class="input-hint">
          <span>示例: 1-1-1 (第1货架-第1列-第1层)</span>
        </div>
      </div>

      <!-- 货物选择 -->
      <div class="input-section">
        <div class="input-label">
          <span>选择货物:</span>
        </div>
        <div class="input-wrapper">
          <el-select v-model="selectedCargo" placeholder="请选择要移动的货物" size="small" clearable>
            <el-option 
              v-for="cargo in cargoList" 
              :key="cargo.name" 
              :label="cargo.name" 
              :value="cargo.name"
            ></el-option>
          </el-select>
        </div>
      </div>

      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button 
          type="primary" 
          size="small" 
          @click="moveCargoToLocation"
          :disabled="!locationCode || !selectedCargo"
        >
          <i class="el-icon-right"></i>
          <span>移动货物</span>
        </el-button>
        <el-button 
          size="small" 
          @click="refreshCargoList"
        >
          <i class="el-icon-refresh"></i>
          <span>刷新货物列表</span>
        </el-button>
      </div>

      <!-- 状态信息 -->
      <div class="status-section" v-if="statusMessage">
        <el-alert 
          :title="statusMessage" 
          :type="statusType" 
          :show-icon="true"
          :closable="true"
          @close="clearStatus"
          size="small"
        ></el-alert>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MoveToLocationPanel",
  data() {
    return {
      locationCode: '', // 货位编码
      selectedCargo: '', // 选中的货物
      cargoList: [], // 货物列表
      statusMessage: '', // 状态信息
      statusType: 'info' // 状态类型: success, warning, error, info
    }
  },
  mounted() {
    // 组件挂载后刷新货物列表
    this.refreshCargoList()
  },
  methods: {
    // 刷新货物列表
    refreshCargoList() {
      try {
        // 向父组件请求货物列表数据
        this.$emit('refresh-cargo-list')
      } catch (error) {
        console.error('刷新货物列表失败:', error)
        this.showStatus('刷新货物列表失败', 'error')
      }
    },

    // 移动货物到指定位置
    moveCargoToLocation() {
      // 参数验证
      if (!this.locationCode) {
        this.showStatus('请输入货位编码', 'warning')
        return
      }

      if (!this.selectedCargo) {
        this.showStatus('请选择要移动的货物', 'warning')
        return
      }

      // 验证货位编码格式
      const parts = this.locationCode.split('-')
      if (parts.length !== 3) {
        this.showStatus('货位编码格式不正确，应为：货架号-列数-层数', 'warning')
        return
      }

      // 向父组件发送移动货物的请求
      this.$emit('move-cargo-to-location', {
        locationCode: this.locationCode,
        cargoName: this.selectedCargo
      })
    },

    // 显示状态信息
    showStatus(message, type = 'info') {
      this.statusMessage = message
      this.statusType = type
      
      // 5秒后自动清除状态信息
      setTimeout(() => {
        this.clearStatus()
      }, 5000)
    },

    // 清除状态信息
    clearStatus() {
      this.statusMessage = ''
      this.statusType = 'info'
    },

    // 更新货物列表（由父组件调用）
    updateCargoList(cargoList) {
      this.cargoList = cargoList
      // 如果没有选中货物，自动选中第一个
      if (cargoList.length > 0 && !this.selectedCargo) {
        this.selectedCargo = cargoList[0].name
      }
    },

    // 更新移动状态（由父组件调用）
    updateMoveStatus(success, message) {
      this.showStatus(message, success ? 'success' : 'error')
    }
  }
}
</script>

<style lang="scss" scoped>
.move-to-location-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.6vh 0.5vw;
    margin-bottom: 1vh;
    border-bottom: 1px solid rgba(0, 150, 255, 0.1);

    .header-title {
      display: flex;
      align-items: center;
      gap: 0.4vw;
      font-size: 1.5vh;
      font-weight: 600;
      color: #4facfe;
      text-shadow: 0 0 4px rgba(79, 172, 254, 0.3);

      i {
        font-size: 1.7vh;
      }
    }
  }

  .panel-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0.5vw;
    gap: 1vh;

    // 输入区域
    .input-section {
      display: flex;
      flex-direction: column;
      gap: 0.5vh;

      .input-label {
        font-size: 1.2vh;
        color: #7ea8c8;
        text-shadow: 0 0 2px rgba(100, 150, 200, 0.15);
      }

      .input-wrapper {
        width: 100%;

        .el-input,
        .el-select {
          width: 100%;

          .el-input__inner,
          .el-select__input {
            font-size: 1.1vh;
            padding: 0.5vh 0.5vw;
            background: rgba(0, 30, 60, 0.25);
            border: 1px solid rgba(0, 150, 255, 0.15);
            color: #ffffff;

            &::placeholder {
              color: #7ea8c8;
              opacity: 0.7;
            }

            &:focus {
              border-color: #4facfe;
              box-shadow: 0 0 8px rgba(79, 172, 254, 0.3);
              background: rgba(0, 30, 60, 0.35);
            }
          }

          .el-select-dropdown {
            background: rgba(0, 20, 40, 0.95);
            border: 1px solid rgba(0, 150, 255, 0.2);

            .el-select-dropdown__item {
              font-size: 1.1vh;
              color: #ffffff;
              padding: 0.6vh 0.8vw;

              &:hover {
                background: rgba(79, 172, 254, 0.15);
                color: #4facfe;
              }

              &.selected {
                background: rgba(79, 172, 254, 0.2);
                color: #4facfe;
              }
            }
          }
        }
      }

      .input-hint {
        font-size: 1vh;
        color: #7ea8c8;
        opacity: 0.8;
        font-style: italic;
      }
    }

    // 按钮区域
    .button-section {
      display: flex;
      gap: 0.8vw;
      padding: 1vh 0;

      .el-button {
        font-size: 1.1vh;
        padding: 0.6vh 1.2vw;
        border-radius: 4px;
        box-shadow: 0 2px 8px rgba(0, 100, 200, 0.1);

        &.el-button--primary {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          border: none;
          color: #ffffff;

          &:hover {
            background: linear-gradient(135deg, #3a9bfc 0%, #00e0fe 100%);
            box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
          }

          &:disabled {
            background: rgba(79, 172, 254, 0.3);
            box-shadow: none;
          }
        }

        &:not(.el-button--primary) {
          background: rgba(0, 30, 60, 0.25);
          border: 1px solid rgba(0, 150, 255, 0.15);
          color: #7ea8c8;

          &:hover {
            background: rgba(0, 40, 80, 0.35);
            border-color: rgba(0, 150, 255, 0.3);
            color: #4facfe;
          }
        }
      }
    }

    // 状态区域
    .status-section {
      .el-alert {
        padding: 0.6vh 0.8vw;
        font-size: 1.1vh;
        background: rgba(0, 30, 60, 0.2);
        border: 1px solid rgba(0, 150, 255, 0.1);
        color: #ffffff;

        .el-alert__title {
          font-size: 1.1vh;
        }
      }
    }
  }
}
</style>