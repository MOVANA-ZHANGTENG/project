<template>
  <div class="app-breadcrumb">
    <transition-group name="breadcrumb">
      <div v-for="(item,index) in levelList" :key="item.path" class="breadcrumb-item">
        <span v-if="item.redirect === 'noRedirect' || index == levelList.length - 1" class="no-redirect">{{ item.meta.title }}</span>
        <a v-else @click.prevent="handleLink(item)" class="breadcrumb-link">{{ item.meta.title }}</a>
        <span v-if="index < levelList.length - 1" class="breadcrumb-separator">/</span>
      </div>
    </transition-group>
  </div>
</template>

<script>
export default {
  data() {
    return {
      levelList: null
    }
  },
  watch: {
    $route(route) {
      // if you go to the redirect page, do not update the breadcrumbs
      if (route.path.startsWith('/redirect/')) {
        return
      }
      this.getBreadcrumb()
    }
  },
  created() {
    this.getBreadcrumb()
  },
  methods: {
    getBreadcrumb() {
      // only show routes with meta.title
      let matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      const first = matched[0]

      if (!this.isDashboard(first)) {
        matched = [{ path: '/index', meta: { title: '首页' }}].concat(matched)
      }

      this.levelList = matched.filter(item => item.meta && item.meta.title && item.meta.breadcrumb !== false)
    },
    isDashboard(route) {
      const name = route && route.name
      if (!name) {
        return false
      }
      return name.trim() === 'Index'
    },
    handleLink(item) {
      const { redirect, path } = item
      if (redirect) {
        this.$router.push(redirect)
        return
      }
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);

  .breadcrumb-item {
    display: inline-block;
    position: relative;

    .breadcrumb-link {
      color: #ffffff;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      transition: all 0.3s ease;
      text-decoration: none;
      cursor: pointer;
      padding: 2px 4px;
      border-radius: 4px;

      &:hover {
        color: #667eea;
        text-shadow: 0 0 4px rgba(102, 126, 234, 0.4);
        background: rgba(102, 126, 234, 0.1);
        backdrop-filter: blur(5px);
      }
    }

    .no-redirect {
      color: #8a9ba8;
      cursor: text;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      padding: 2px 4px;
    }

    .breadcrumb-separator {
      color: #8a9ba8;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      margin: 0 8px;
      font-weight: 500;
    }
  }
}

// 面包屑过渡动画
.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.3s ease;
}

.breadcrumb-enter,
.breadcrumb-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.breadcrumb-move {
  transition: transform 0.3s ease;
}
</style>
