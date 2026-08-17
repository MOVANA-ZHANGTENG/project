<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper" @scroll="handleScroll">
      <router-link
        v-for="(item, index) in visitedViews"
        :key="item.path"
        :ref="el => { if (el) tagRefs[index] = el }"
        :class="isActive(item)?'active':''"
        :to="{ path: item.path, query: item.query, fullPath: item.fullPath }"
        tag="span"
        class="tags-view-item"
        :style="activeStyle(item)"
        @click.middle.native="!isAffix(item)?closeSelectedTag(item):''"
        @contextmenu.prevent.native="openMenu(item,$event)"
      >
        {{ item.title }}
        <span v-if="!isAffix(item)" class="el-icon-close" @click.prevent.stop="closeSelectedTag(item)" />
      </router-link>
    </scroll-pane>
  </div>
</template>

<script>
import ScrollPane from './ScrollPane'
import path from 'path'

export default {
  components: { ScrollPane },
  data() {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: [],
      tagRefs: [],
      menuEl: null
    }
  },
  computed: {
    visitedViews() {
      return this.$store.state.tagsView.visitedViews
    },
    routes() {
      return this.$store.state.permission.routes
    },
    theme() {
      return this.$store.state.settings.theme;
    }
  },
  watch: {
    $route() {
      this.addTags()
      this.moveToCurrentTag()
    },
    visible(value) {
      if (value) {
        document.body.addEventListener('click', this.closeMenu)
        document.body.addEventListener('scroll', this.closeMenu)
        window.addEventListener('resize', this.closeMenu)
      } else {
        document.body.removeEventListener('click', this.closeMenu)
        document.body.removeEventListener('scroll', this.closeMenu)
        window.removeEventListener('resize', this.closeMenu)
      }
    }
  },
  mounted() {
    this.initTags()
    this.addTags()
  },
  beforeDestroy() {
    this.closeMenu()
  },
  methods: {
    isActive(route) {
      return route.path === this.$route.path
    },
    activeStyle(tag) {
      if (!this.isActive(tag)) return {};
      return {
        "background-color": this.theme,
        "border-color": this.theme
      };
    },
    isAffix(tag) {
      return tag.meta && tag.meta.affix
    },
    isFirstView() {
      try {
        return this.selectedTag.fullPath === '/index' || this.selectedTag.fullPath === this.visitedViews[1].fullPath
      } catch (err) {
        return false
      }
    },
    isLastView() {
      try {
        return this.selectedTag.fullPath === this.visitedViews[this.visitedViews.length - 1].fullPath
      } catch (err) {
        return false
      }
    },
    filterAffixTags(routes, basePath = '/') {
      let tags = []
      routes.forEach(route => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.path)
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta }
          })
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.path)
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags]
          }
        }
      })
      return tags
    },
    initTags() {
      const affixTags = this.affixTags = this.filterAffixTags(this.routes)
      for (const tag of affixTags) {
        if (tag.name) {
          this.$store.dispatch('tagsView/addVisitedView', tag)
        }
      }
    },
    addTags() {
      const { name } = this.$route
      if (name) {
        this.$store.dispatch('tagsView/addView', this.$route)
        if (this.$route.meta.link) {
          this.$store.dispatch('tagsView/addIframeView', this.$route)
        }
      }
      return false
    },
    moveToCurrentTag() {
      const tags = this.tagRefs.filter(el => el)
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            this.$refs.scrollPane.moveToTarget(tag)
            if (tag.to.fullPath !== this.$route.fullPath) {
              this.$store.dispatch('tagsView/updateVisitedView', this.$route)
            }
            break
          }
        }
      })
    },
    refreshSelectedTag(view) {
      this.$tab.refreshPage(view);
      if (this.$route.meta.link) {
        this.$store.dispatch('tagsView/delIframeView', this.$route)
      }
    },
    closeSelectedTag(view) {
      this.$tab.closePage(view).then(({ visitedViews }) => {
        if (this.isActive(view)) {
          this.toLastView(visitedViews, view)
        }
      })
    },
    closeRightTags() {
      this.$tab.closeRightPage(this.selectedTag).then(visitedViews => {
        if (!visitedViews.find(i => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews)
        }
      })
    },
    closeLeftTags() {
      this.$tab.closeLeftPage(this.selectedTag).then(visitedViews => {
        if (!visitedViews.find(i => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews)
        }
      })
    },
    closeOthersTags() {
      this.$router.push(this.selectedTag.fullPath).catch(()=>{});
      this.$tab.closeOtherPage(this.selectedTag).then(() => {
        this.moveToCurrentTag()
      })
    },
    closeAllTags(view) {
      this.$tab.closeAllPage().then(({ visitedViews }) => {
        if (this.affixTags.some(tag => tag.path === this.$route.path)) {
          return
        }
        this.toLastView(visitedViews, view)
      })
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else {
        if (view.name === 'Dashboard') {
          this.$router.replace({ path: '/redirect' + view.fullPath })
        } else {
          this.$router.push('/')
        }
      }
    },
    openMenu(tag, e) {
      e.preventDefault()
      e.stopPropagation()
      
      this.selectedTag = tag
      
      const menuMinWidth = 120
      const maxLeft = window.innerWidth - menuMinWidth - 20
      const maxTop = window.innerHeight - 200
      
      let left = e.clientX
      let top = e.clientY
      
      if (left > maxLeft) {
        left = maxLeft
      }
      if (top > maxTop) {
        top = maxTop - 100
      }
      
      this.left = left
      this.top = top
      this.visible = true
      
      this.$nextTick(() => {
        this.createMenuElement()
      })
    },
    createMenuElement() {
      this.removeMenuElement()
      
      const menu = document.createElement('ul')
      menu.className = 'tags-view-contextmenu'
      menu.style.cssText = `
        position: fixed;
        left: ${this.left}px;
        top: ${this.top}px;
        z-index: 99999;
        margin: 0;
        padding: 5px 0;
        background: rgba(15, 15, 30, 0.98);
        backdrop-filter: blur(15px);
        -webkit-backdrop-filter: blur(15px);
        border-radius: 8px;
        font-size: 12px;
        font-weight: 400;
        color: #ffffff;
        border: 1px solid rgba(255, 255, 255, 0.15);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
        min-width: 120px;
        list-style: none;
        outline: none;
      `
      
      const menuItems = [
        { text: '刷新页面', icon: 'el-icon-refresh-right', action: 'refresh' },
        { text: '关闭当前', icon: 'el-icon-close', action: 'close', disabled: this.isAffix(this.selectedTag) },
        { text: '关闭其他', icon: 'el-icon-circle-close', action: 'closeOthers' },
        { text: '关闭左侧', icon: 'el-icon-back', action: 'closeLeft', disabled: this.isFirstView() },
        { text: '关闭右侧', icon: 'el-icon-right', action: 'closeRight', disabled: this.isLastView() },
        { text: '全部关闭', icon: 'el-icon-circle-close', action: 'closeAll' }
      ]
      
      menuItems.forEach(item => {
        if (item.disabled) return
        
        const li = document.createElement('li')
        li.style.cssText = `
          margin: 0;
          padding: 8px 16px;
          cursor: pointer;
          transition: all 0.2s ease;
          display: flex;
          align-items: center;
        `
        
        li.onmouseenter = () => {
          li.style.background = 'rgba(79, 172, 254, 0.2)'
          li.style.color = '#4facfe'
        }
        
        li.onmouseleave = () => {
          li.style.background = ''
          li.style.color = '#ffffff'
        }
        
        const icon = document.createElement('i')
        icon.className = item.icon
        icon.style.cssText = `
          margin-right: 6px;
          color: #8a9ba8;
          transition: color 0.2s ease;
          font-size: 14px;
        `
        
        li.onmouseenter = () => {
          li.style.background = 'rgba(79, 172, 254, 0.2)'
          li.style.color = '#4facfe'
          icon.style.color = '#4facfe'
        }
        
        li.onmouseleave = () => {
          li.style.background = ''
          li.style.color = '#ffffff'
          icon.style.color = '#8a9ba8'
        }
        
        const text = document.createTextNode(item.text)
        li.appendChild(icon)
        li.appendChild(text)
        
        li.addEventListener('click', (e) => {
          e.stopPropagation()
          this.handleMenuAction(item.action)
        })
        
        menu.appendChild(li)
      })
      
      menu.addEventListener('contextmenu', (e) => {
        e.preventDefault()
      })
      
      document.body.appendChild(menu)
      this.menuEl = menu
      
      menu.focus()
    },
    removeMenuElement() {
      if (this.menuEl && this.menuEl.parentNode) {
        this.menuEl.parentNode.removeChild(this.menuEl)
        this.menuEl = null
      }
    },
    handleMenuAction(action) {
      this.closeMenu()
      
      switch (action) {
        case 'refresh':
          this.refreshSelectedTag(this.selectedTag)
          break
        case 'close':
          this.closeSelectedTag(this.selectedTag)
          break
        case 'closeOthers':
          this.closeOthersTags()
          break
        case 'closeLeft':
          this.closeLeftTags()
          break
        case 'closeRight':
          this.closeRightTags()
          break
        case 'closeAll':
          this.closeAllTags(this.selectedTag)
          break
      }
    },
    closeMenu() {
      this.visible = false
      this.removeMenuElement()
    },
    handleScroll() {
      this.closeMenu()
    }
  }
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #0f0f1e;
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  
  .tags-view-wrapper {
    .tags-view-item {
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: #ffffff;
      background: rgba(255, 255, 255, 0.08);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;
      border-radius: 4px;
      transition: all 0.3s ease;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      
      &:first-of-type {
        margin-left: 15px;
      }
      &:last-of-type {
        margin-right: 15px;
      }
      
      &:hover {
        background: rgba(255, 255, 255, 0.15);
        border-color: rgba(79, 172, 254, 0.6);
        color: #4facfe;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
      
      &.active {
        background: linear-gradient(135deg, rgba(79, 172, 254, 0.2) 0%, rgba(79, 172, 254, 0.1) 100%);
        color: #ffffff;
        border-color: #4facfe;
        box-shadow: 
          0 4px 16px rgba(79, 172, 254, 0.35),
          0 0 0 1px rgba(79, 172, 254, 0.25),
          inset 0 1px 0 rgba(255, 255, 255, 0.1);
        transform: translateY(-2px);
        position: relative;
        
        &::before {
          content: '';
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          display: inline-block;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          position: relative;
          margin-right: 4px;
          box-shadow: 
            0 0 6px rgba(79, 172, 254, 0.6),
            0 0 12px rgba(79, 172, 254, 0.4);
          animation: pulse 2s infinite;
        }
        
        &::after {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 0;
          right: 0;
          height: 2px;
          background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
          border-radius: 1px;
          box-shadow: 0 0 4px rgba(79, 172, 254, 0.5);
        }
      }
    }
  }
}
</style>

<style lang="scss">
.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close {
      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all .3s cubic-bezier(.645, .045, .355, 1);
      transform-origin: 100% 50%;
      color: #8a9ba8;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      
      &:before {
        transform: scale(.6);
        display: inline-block;
        vertical-align: -3px;
      }
      
      &:hover {
        background-color: rgba(245, 108, 108, 0.2);
        color: #f56c6c;
        box-shadow: 0 0 4px rgba(245, 108, 108, 0.4);
        transform: scale(1.1);
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}
</style>
