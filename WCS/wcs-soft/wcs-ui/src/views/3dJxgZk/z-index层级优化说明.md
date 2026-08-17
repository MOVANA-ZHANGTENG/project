# Z-Index 层级优化说明

## 📋 问题描述

3D页面内的调试面板、堆垛机弹出窗口、无人机模式UI等组件被外层的Header、Left、Bottom组件遮挡，导致无法正常显示和交互。

---

## ✅ 解决方案

采用**分层设计 + 事件穿透**的方案，确保：
1. 外层UI组件（Header、Left、Bottom）显示在3D场景上方
2. 3D内部的调试UI显示在最顶层，不被任何组件遮挡
3. 外层UI的透明区域可以穿透，不阻挡3D内部UI的交互

---

## 🎯 Z-Index 层级规划

### 层级架构
```
┌─────────────────────────────────────────┐
│  z-index: 999  模型加载进度条             │  最顶层（全屏覆盖）
├─────────────────────────────────────────┤
│  z-index: 120  无人机HUD + 准星           │  3D内部UI - 第二层
├─────────────────────────────────────────┤
│  z-index: 110  堆垛机信息面板             │  3D内部UI - 第三层
├─────────────────────────────────────────┤
│  z-index: 100  调试控制面板               │  3D内部UI - 第四层
├─────────────────────────────────────────┤
│  z-index: 10   Header / Left / Bottom   │  外层UI框架
├─────────────────────────────────────────┤
│  z-index: 0    3D场景容器                │  底层（3D渲染）
└─────────────────────────────────────────┘
```

### 具体数值分配

| 组件 | z-index | 位置 | 说明 |
|------|---------|------|------|
| `.progress-box` | 999 | 3d.vue | 模型加载进度（最高层） |
| `.drone-crosshair` | 120 | 3d.vue | 无人机准星 |
| `.drone-hud` | 120 | 3d.vue | 无人机HUD信息 |
| `.stacker-info-panel` | 110 | 3d.vue | 堆垛机信息弹窗 |
| `.control-panel` | 100 | 3d.vue | 调试控制面板 |
| Header组件 | 10 | index.vue | 顶部信息栏 |
| Left组件 | 10 | index.vue | 左侧数据面板 |
| Bottom组件 | 10 | index.vue | 底部任务轮播 |
| `.three-container` | 0 | index.vue | 3D场景容器 |

---

## 🔧 技术实现

### 1. 外层UI组件 - 事件穿透

**原理**：容器设置 `pointer-events: none`，内容设置 `pointer-events: auto`

#### Header 组件
```scss
.header-container {
  pointer-events: none; // 容器不捕获事件
  
  .header-content {
    pointer-events: auto; // 内容可交互
  }
}
```

#### Left 组件
```scss
.left-container {
  pointer-events: none; // 容器不捕获事件
  
  ::v-deep > div {
    pointer-events: auto; // 子组件可交互
  }
}
```

#### Bottom 组件
```scss
.bottom-container {
  pointer-events: none; // 容器不捕获事件
  
  .content {
    pointer-events: none; // 内容容器也不捕获
  }
  
  .task-card {
    pointer-events: auto; // 卡片可交互
  }
}
```

### 2. 3D内部UI组件 - 提高层级

#### 调试控制面板
```scss
.control-panel {
  z-index: 100; // 从15提升到100
}
```

#### 堆垛机信息面板
```scss
.stacker-info-panel {
  z-index: 110; // 从20提升到110
}
```

#### 无人机准星
```scss
.drone-crosshair {
  z-index: 120; // 从10提升到120
  pointer-events: none; // 准星不阻挡交互
}
```

#### 无人机HUD
```scss
.drone-hud {
  z-index: 120; // 新增，确保显示在最上层
}
```

---

## 📐 DOM结构调整

### 优化前
```vue
<div class="container">
  <Header z-index:10 />
  <Left z-index:10 />
  <Bottom z-index:10 />
  <Three3d z-index:0 />
</div>
```
**问题**：后渲染的Three3d在DOM末尾，但z-index最低，导致其内部UI被遮挡

### 优化后
```vue
<div class="container">
  <Three3d z-index:0 />        <!-- 提前，作为底层 -->
  <Header z-index:10 />         <!-- 外层UI -->
  <Left z-index:10 />           
  <Bottom z-index:10 />
</div>
```
**优势**：
1. Three3d作为底层基础
2. 外层UI浮在上方
3. Three3d内部UI通过更高z-index显示在最上层

---

## 🎯 效果验证

### 验证点
- ✅ 调试控制面板不被Header遮挡
- ✅ 堆垛机信息弹窗不被Left遮挡
- ✅ 无人机HUD和准星不被任何组件遮挡
- ✅ 模型加载进度条覆盖全屏
- ✅ Header/Left/Bottom的透明区域可穿透
- ✅ Header/Left/Bottom的内容区域正常交互

### 测试场景
1. **调试面板测试**
   - 打开调试控制面板
   - 验证按钮可点击
   - 验证不被Header右侧遮挡

2. **堆垛机面板测试**
   - 点击堆垛机弹出信息面板
   - 验证面板完整显示
   - 验证不被Left遮挡

3. **无人机模式测试**
   - 启动无人机飞行模式
   - 验证准星显示在中心
   - 验证HUD信息显示在顶部
   - 验证不被任何组件遮挡

4. **外层UI交互测试**
   - 点击Header的各个功能
   - 滚动Left的数据列表
   - 悬浮Bottom的任务卡片
   - 验证所有交互正常

---

## 💡 设计原则

### 1. 分层清晰
```
应用层（z-index: 900+）    - 全局提示、加载
顶层UI（z-index: 100+）    - 3D内部UI
中层UI（z-index: 10-20）   - 框架UI
底层（z-index: 0-5）       - 3D场景
```

### 2. 事件穿透
- 容器层：`pointer-events: none`
- 内容层：`pointer-events: auto`
- 准星等纯显示：`pointer-events: none`

### 3. 语义化命名
```scss
// ❌ 不推荐
z-index: 9999;  // 数值过大，难以维护

// ✅ 推荐
z-index: 100;  // 3D UI层
z-index: 10;   // 框架UI层
```

---

## 🔄 扩展建议

### 添加新的3D内部UI

如需在3D场景内添加新的UI组件，建议按以下规则设置z-index：

```scss
// 全屏覆盖类（如加载、遮罩）
z-index: 900-999;

// 重要提示类（如无人机HUD）
z-index: 120-150;

// 信息面板类（如堆垛机详情）
z-index: 110-119;

// 调试工具类
z-index: 100-109;
```

### 动态层级管理

对于需要动态调整层级的组件（如弹窗），可以使用：

```javascript
// 方法1：使用变量
data() {
  return {
    zIndex: 110
  };
}

// 方法2：动态计算
computed: {
  modalZIndex() {
    return this.isActive ? 150 : 110;
  }
}

// 方法3：组件激活时提升
methods: {
  bringToFront() {
    this.$el.style.zIndex = 200;
  }
}
```

---

## 📝 注意事项

### 1. z-index 只在定位元素生效
```scss
// ❌ 无效
.element {
  z-index: 100;  // 没有position
}

// ✅ 有效
.element {
  position: relative; // 或absolute/fixed
  z-index: 100;
}
```

### 2. 父子层级关系
```scss
// 子元素的z-index不能超越父元素的层叠上下文
.parent {
  z-index: 10;
  position: relative;
  
  .child {
    z-index: 9999;  // 仍然低于z-index:11的其他元素
  }
}
```

### 3. pointer-events 兼容性
```scss
// 现代浏览器支持良好
pointer-events: none;

// 如需兼容IE10及以下，需使用polyfill或其他方案
```

---

## 🐛 常见问题

### Q1: 为什么外层UI还是遮挡3D内部UI？
**A**: 检查以下几点：
1. 3D内部UI的z-index是否 > 10
2. 3D内部UI是否有`position`属性
3. 父元素是否创建了新的层叠上下文

### Q2: 为什么外层UI无法交互？
**A**: 检查以下几点：
1. 容器设置了`pointer-events: none`
2. 内容区域是否设置了`pointer-events: auto`
3. 是否有其他透明元素覆盖

### Q3: 如何调试z-index问题？
**A**: 使用浏览器开发工具：
1. 打开Chrome DevTools
2. 选择Elements面板
3. 在Styles中查看computed值
4. 使用3D视图查看层叠顺序

---

## 📊 性能影响

### 优化前后对比

| 指标 | 优化前 | 优化后 | 说明 |
|------|--------|--------|------|
| 渲染层数 | 3层 | 4层 | 增加了层级分离 |
| 重绘区域 | 大 | 小 | pointer-events减少事件捕获 |
| 交互延迟 | 无 | 无 | 事件穿透不影响性能 |
| 内存占用 | 无变化 | 无变化 | 仅CSS改动 |

### 性能优化建议
1. 避免频繁修改z-index
2. 使用CSS而非JS控制层级
3. 合理使用`will-change`提示浏览器优化
4. 减少不必要的`backdrop-filter`

---

**优化日期**: 2025年10月  
**优化版本**: v1.0  
**适用场景**: 3D场景与2D UI混合显示

