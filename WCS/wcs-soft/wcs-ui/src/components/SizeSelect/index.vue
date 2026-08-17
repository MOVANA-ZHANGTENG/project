<template>
  <el-dropdown trigger="click" @command="handleSetSize">
    <div class="size-select-container">
      <svg-icon class-name="size-icon" icon-class="size" />
    </div>
    <el-dropdown-menu slot="dropdown" class="size-dropdown-menu">
      <el-dropdown-item v-for="item of sizeOptions" :key="item.value" :disabled="size===item.value" :command="item.value">
        {{ item.label }}
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  data() {
    return {
      sizeOptions: [
        { label: 'Default', value: 'default' },
        { label: 'Medium', value: 'medium' },
        { label: 'Small', value: 'small' },
        { label: 'Mini', value: 'mini' }
      ]
    }
  },
  computed: {
    size() {
      return this.$store.getters.size
    }
  },
  methods: {
    handleSetSize(size) {
      this.$ELEMENT.size = size
      this.$store.dispatch('app/setSize', size)
      this.refreshView()
      this.$message({
        message: 'Switch Size Success',
        type: 'success'
      })
    },
    refreshView() {
      // In order to make the cached page re-rendered
      this.$store.dispatch('tagsView/delAllCachedViews', this.$route)

      const { fullPath } = this.$route

      this.$nextTick(() => {
        this.$router.replace({
          path: '/redirect' + fullPath
        })
      })
    }
  }

}
</script>

<style lang="scss" scoped>
.size-select-container {
  .size-icon {
    color: #ffffff;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;
    cursor: pointer;

    &:hover {
      color: #667eea;
      filter: drop-shadow(0 0 4px rgba(102, 126, 234, 0.4));
    }
  }
}

::v-deep .size-dropdown-menu {
  background: rgba(26, 26, 46, 0.95) !important;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;

  .el-dropdown-menu__item {
    color: #ffffff !important;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(102, 126, 234, 0.2) !important;
      color: #667eea !important;
    }

    &.is-disabled {
      color: #8a9ba8 !important;
      background: transparent !important;
    }
  }
}
</style>
