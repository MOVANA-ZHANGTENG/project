# 🎯 FPS指针锁定测试指南

## 🔍 详细测试步骤

### 第一步：刷新页面
1. 按 `Ctrl + Shift + R` 硬刷新
2. 按 `F12` 打开控制台
3. 等待模型加载完成

### 第二步：进入无人机模式
4. 点击「🚁 无人机飞行」按钮
5. 应该看到：
```
✅ 鼠标事件已绑定（指针锁定模式）
📌 正在请求指针锁定，请点击画面确认...
🚁 无人机飞行模式已启用（FPS风格）
```

### 第三步：观察画面
6. 画面中心应该显示**脉冲提示框**：
```
🖱️
点击画面锁定鼠标
准星将固定在中心
```

### 第四步：点击锁定
7. **点击3D画面任意位置**
8. 观察控制台，应该显示：
```
🔒 鼠标已锁定
```
9. 提示框消失，准星出现

### 第五步：测试鼠标
10. **移动鼠标**（向左、向右、向上、向下）
11. 观察控制台，每次移动应该输出：
```
🔍 onMouseMove触发, isLocked: true, movementX: 15, movementY: -8
✅ 开始处理鼠标移动: 15 -8
📐 新视角: X: -0.234 Y: 1.567
```

## 🐛 问题诊断

### 如果点击后没有显示「🔒 鼠标已锁定」

**可能原因**：指针锁定API失败

**检查方法**：
```javascript
// 在控制台执行
document.getElementById('3d').requestPointerLock()

// 然后点击画面，观察是否能锁定
```

**解决方案**：
1. 确保使用Chrome或Edge浏览器
2. 确保页面通过http或https访问（不是file://）
3. 检查浏览器是否禁用了指针锁定

### 如果显示「🔒 鼠标已锁定」但移动鼠标没有日志

**可能原因**：鼠标事件没有绑定到document

**检查方法**：
```javascript
// 测试document上的鼠标事件
let testCount = 0
document.addEventListener('mousemove', (e) => {
  testCount++
  if (testCount % 10 === 0) {
    console.log('✅ document鼠标事件触发', testCount, 'movementX:', e.movementX, 'movementY:', e.movementY)
  }
})
```

### 如果有日志但isLocked是false

**可能原因**：指针锁定状态同步问题

**解决方案**：
```javascript
// 手动设置锁定状态
this.firstPersonController.isLocked = true
console.log('已手动设置isLocked=true')
```

### 如果所有日志都正常但视角不动

**可能原因**：相机旋转被其他代码覆盖

**检查方法**：
```javascript
// 监控相机旋转
setInterval(() => {
  console.log('相机:', 
    'X:', this.ThreeEngine.camera.rotation.x.toFixed(3),
    'Y:', this.ThreeEngine.camera.rotation.y.toFixed(3)
  )
}, 500)
```

## 🔧 完整诊断脚本

复制到控制台执行：

```javascript
console.clear()
console.log('╔════════════════════════════════════════╗')
console.log('║   FPS指针锁定完整诊断                 ║')
console.log('╚════════════════════════════════════════╝\n')

// 1. 检查基础状态
console.log('【1】基础状态:')
console.log('  控制器存在:', !!this.firstPersonController)
console.log('  控制器启用:', this.firstPersonController?.enabled)
console.log('  第一人称模式:', this.isFirstPersonMode)
console.log('  鼠标锁定:', this.firstPersonController?.isLocked)

// 2. 检查指针锁定API
console.log('\n【2】指针锁定API:')
console.log('  pointerLockElement:', document.pointerLockElement)
console.log('  期望元素:', this.firstPersonController?.domElement)
console.log('  是否匹配:', document.pointerLockElement === this.firstPersonController?.domElement)

// 3. 尝试手动锁定
console.log('\n【3】尝试手动锁定:')
const dom = document.getElementById('3d')
console.log('  目标元素:', dom)

dom.requestPointerLock()
console.log('  ✅ 已请求锁定，请观察画面并移动鼠标')

// 4. 监听下一次鼠标移动
console.log('\n【4】监听鼠标移动事件:')
let eventCount = 0
const testHandler = (e) => {
  eventCount++
  console.log(`  🖱️ 鼠标移动事件 #${eventCount}:`, 
    'movementX:', e.movementX, 
    'movementY:', e.movementY,
    'isLocked:', !!document.pointerLockElement
  )
  
  if (eventCount >= 5) {
    document.removeEventListener('mousemove', testHandler)
    console.log('  ✅ 测试完成（已捕获5次事件）')
  }
}

document.addEventListener('mousemove', testHandler)
console.log('  ✅ 测试handler已添加，请移动鼠标...')

console.log('\n════════════════════════════════════════')
console.log('请：1.点击画面 2.移动鼠标 3.观察日志输出')
```

## 📝 请执行以下步骤并反馈

1. **刷新页面**
2. **进入无人机模式**（点击按钮）
3. **点击3D画面**（锁定鼠标）
4. **移动鼠标**
5. **复制控制台所有日志** 并告诉我

## 🎯 关键信息需要确认

请告诉我：

### A. 点击画面后
- [ ] 控制台是否显示「🔒 鼠标已锁定」？
- [ ] 鼠标指针是否消失？
- [ ] 提示框是否消失？
- [ ] 准星是否出现？

### B. 移动鼠标后
- [ ] 控制台是否有「🔍 onMouseMove触发」日志？
- [ ] isLocked的值是true还是false？
- [ ] movementX和movementY是否有非零值？
- [ ] 是否看到「✅ 开始处理鼠标移动」？
- [ ] 是否看到「📐 新视角」？

### C. 视觉效果
- [ ] 视角是否跟随鼠标旋转？
- [ ] 准星是否固定在中心？

这些信息将帮我精确定位问题！🔍

