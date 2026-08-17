# 网格纹理生成器使用说明

## 功能概述

TGridTexture 是一个程序化纹理生成器，用于创建科技感的网格地板纹理。支持三种风格：
1. **标准网格** - 网格线 + 交点圆点
2. **简单网格** - 仅网格线
3. **发光网格** - 增强发光效果的网格

## 主要特性

✅ **程序生成** - 使用Canvas动态绘制，无需图片资源
✅ **发光圆点** - 网格交点处有发光的小圆
✅ **科技感配色** - 默认蓝色调，符合科技风格
✅ **高度可配置** - 网格大小、颜色、圆点等均可自定义
✅ **自动平铺** - Three.js纹理自动重复平铺

## API 文档

### createGridTexture(options) - 标准网格纹理

创建带圆点的网格纹理（推荐用于地板）

**参数：**
```javascript
{
  size: 512,                              // Canvas尺寸（像素）
  gridSize: 64,                           // 网格单元大小（像素）
  lineWidth: 1.5,                         // 网格线宽度
  lineColor: 'rgba(0, 150, 255, 0.35)',  // 网格线颜色
  dotRadius: 2.5,                         // 圆点半径
  dotColor: 'rgba(0, 200, 255, 0.9)',    // 圆点颜色
  dotGlow: true,                          // 是否有发光效果
  backgroundColor: 'rgba(2, 4, 10, 1.0)' // 背景颜色
}
```

**返回：** `THREE.CanvasTexture` - Three.js纹理对象

**示例：**
```javascript
import { createGridTexture } from './TGridTexture.js';

const texture = createGridTexture({
  gridSize: 64,
  dotRadius: 3,
  lineColor: 'rgba(0, 150, 255, 0.4)',
  dotColor: 'rgba(0, 200, 255, 1.0)',
});

const material = new THREE.MeshPhongMaterial({
  map: texture,
  color: 0x0a0a20,
});
```

### createSimpleGridTexture(options) - 简单网格纹理

创建仅包含网格线的纹理（无圆点）

**参数：**
```javascript
{
  size: 512,
  gridSize: 64,
  lineWidth: 1,
  lineColor: 'rgba(0, 150, 255, 0.3)',
  backgroundColor: 'rgba(2, 4, 10, 1.0)'
}
```

**示例：**
```javascript
import { createSimpleGridTexture } from './TGridTexture.js';

const texture = createSimpleGridTexture({
  gridSize: 32,  // 更密集的网格
  lineWidth: 0.5,
});
```

### createGlowGridTexture(options) - 发光网格纹理

创建增强发光效果的网格纹理（科技感更强）

**参数：**
```javascript
{
  size: 512,
  gridSize: 64,
  lineWidth: 2,                           // 更粗的线
  lineColor: 'rgba(0, 200, 255, 0.6)',
  dotRadius: 4,                           // 更大的圆点
  dotColor: 'rgba(0, 220, 255, 1.0)',
  backgroundColor: 'rgba(2, 4, 10, 1.0)'
}
```

**示例：**
```javascript
import { createGlowGridTexture } from './TGridTexture.js';

const texture = createGlowGridTexture({
  gridSize: 80,
  dotRadius: 5,  // 大圆点
  lineWidth: 3,  // 粗网格线
});
```

## 效果预览

### 标准网格纹理 (createGridTexture)
```
┌─────●─────┬─────●─────┬─────●─────┐
│           │           │           │
│           │           │           │
●           ●           ●           ●
│           │           │           │
│           │           │           │
├─────●─────┼─────●─────┼─────●─────┤
```
- 淡蓝色网格线
- 交点处有亮蓝色发光圆点
- 圆点周围有光晕效果

### 简单网格纹理 (createSimpleGridTexture)
```
┌───────────┬───────────┬───────────┐
│           │           │           │
│           │           │           │
│           │           │           │
│           │           │           │
│           │           │           │
├───────────┼───────────┼───────────┤
```
- 仅包含网格线
- 无圆点
- 更简洁

### 发光网格纹理 (createGlowGridTexture)
```
┌─────◉─────┬─────◉─────┬─────◉─────┐
│    ░░░    │    ░░░    │    ░░░    │
│   ░░●░░   │   ░░●░░   │   ░░●░░   │
◉   ░░░   ◉◉   ░░░   ◉◉   ░░░   ◉
│   ░░●░░   │   ░░●░░   │   ░░●░░   │
│    ░░░    │    ░░░    │    ░░░    │
├─────◉─────┼─────◉─────┼─────◉─────┤
```
- 更粗的网格线
- 更大的圆点
- 更强的发光效果

## 调整网格效果

### 调整网格密度

```javascript
// 稀疏网格（大格子）
gridSize: 128  // 大单元

// 密集网格（小格子）
gridSize: 32   // 小单元
```

### 调整圆点大小

```javascript
// 小圆点（精致）
dotRadius: 1.5

// 中等圆点（标准）
dotRadius: 2.5

// 大圆点（醒目）
dotRadius: 5
```

### 调整发光强度

```javascript
// 微弱发光
lineColor: 'rgba(0, 150, 255, 0.2)',
dotColor: 'rgba(0, 200, 255, 0.5)',

// 强烈发光
lineColor: 'rgba(0, 150, 255, 0.8)',
dotColor: 'rgba(0, 200, 255, 1.0)',
```

### 调整颜色主题

```javascript
// 蓝色主题（科技感）
lineColor: 'rgba(0, 150, 255, 0.35)',
dotColor: 'rgba(0, 200, 255, 0.9)',

// 绿色主题（Matrix风格）
lineColor: 'rgba(0, 255, 100, 0.35)',
dotColor: 'rgba(0, 255, 150, 0.9)',

// 红色主题（警告风格）
lineColor: 'rgba(255, 50, 50, 0.35)',
dotColor: 'rgba(255, 100, 100, 0.9)',

// 紫色主题（神秘风格）
lineColor: 'rgba(150, 50, 255, 0.35)',
dotColor: 'rgba(200, 100, 255, 0.9)',
```

## 性能优化

### Canvas尺寸
- 512x512：标准质量，性能好（推荐）
- 1024x1024：高质量，性能稍差
- 256x256：低质量，性能最好

### 纹理重复
```javascript
texture.repeat.set(10, 10);  // 重复10次
texture.repeat.set(20, 20);  // 重复20次（更密集）
texture.repeat.set(5, 5);    // 重复5次（更稀疏）
```

## 技术原理

### 绘制流程

1. **创建Canvas** - 指定尺寸的画布
2. **绘制背景** - 深色背景
3. **绘制网格线** - 使用 `stroke()` 绘制线条
4. **绘制圆点** - 在网格交点处绘制圆形
5. **添加光晕** - 使用径向渐变实现发光效果
6. **转换纹理** - Canvas转为Three.js CanvasTexture
7. **设置平铺** - 设置纹理重复模式

### Canvas绘制代码示例

```javascript
// 绘制发光圆点
const gradient = ctx.createRadialGradient(x, y, 0, x, y, dotRadius * 2);
gradient.addColorStop(0, dotColor);                          // 中心
gradient.addColorStop(0.5, dotColor.replace(..., '0.3)'));  // 中间
gradient.addColorStop(1, 'rgba(0, 180, 255, 0)');           // 外围透明

ctx.fillStyle = gradient;
ctx.arc(x, y, dotRadius * 2, 0, Math.PI * 2);
ctx.fill();
```

## 预设风格

### 科技蓝地板（默认）
```javascript
createGridTexture({
  lineColor: 'rgba(0, 150, 255, 0.35)',
  dotColor: 'rgba(0, 200, 255, 0.9)',
  backgroundColor: 'rgba(2, 4, 10, 1.0)',
});
```

### 黑客帝国风格
```javascript
createGridTexture({
  lineColor: 'rgba(0, 255, 100, 0.4)',
  dotColor: 'rgba(0, 255, 150, 1.0)',
  backgroundColor: 'rgba(0, 5, 0, 1.0)',
  dotGlow: true,
});
```

### 赛博朋克风格
```javascript
createGlowGridTexture({
  lineColor: 'rgba(255, 0, 150, 0.5)',
  dotColor: 'rgba(0, 255, 255, 1.0)',
  backgroundColor: 'rgba(10, 0, 20, 1.0)',
  gridSize: 48,
  dotRadius: 4,
});
```

## 总结

网格纹理生成器为3D场景提供了强大的程序化地板材质：
- 🎨 **自定义配色** - 支持任意颜色主题
- ⚡ **高性能** - Canvas生成，一次创建永久使用
- 🔧 **易于调整** - 所有参数可配置
- ✨ **科技感** - 完美适配现代工业3D场景

现在地板会自动拥有科技感的蓝色网格和发光圆点！🌐✨

