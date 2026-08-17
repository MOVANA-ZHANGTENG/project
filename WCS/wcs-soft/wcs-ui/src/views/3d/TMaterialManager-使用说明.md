# 材质管理器使用说明

## 功能概述

材质管理器（TMaterialManager）是一个统一管理3D场景中所有组件材质的工具类，支持：
- 按组件类型、部件类型和状态分类管理材质
- 快速批量应用材质到GLB模型
- 动态修改材质配置
- 支持多种状态切换（默认、工作中、错误等）

## 已实现的材质配置

### 1. 地板材质 (floor) ⭐ 科技感网格
- **组件类型**: `floor`
- **部件类型**: `main`
- **支持状态**:
  - `floor1` - 深邃的蓝黑色，不透明
  - `floor2` - 稍亮的蓝黑色，75%透明
  - `floor3` - 更亮的蓝黑色，75%透明
  - `floor4` - 最亮的蓝黑色，75%透明
- **特殊效果**:
  - ✅ **科技感网格**: 自动生成蓝色网格线
  - ✅ **发光圆点**: 网格交点处有发光的小圆点
  - ✅ **动态纹理**: 基于Canvas程序生成，非图片纹理
  - ✅ **可配置**: 网格大小、圆点大小、颜色均可调整

### 2. 货架材质 (shelf)
- **组件类型**: `shelf`
- **部件类型**: `main`
- **支持状态**:
  - `default` - 深灰色哑光
  - `highlight` - 稍亮的深灰色
  - `occupied` - 带绿色调的深灰色（有货）
  - `empty` - 带蓝色调的深灰色（空闲）

### 3. 链条机框架材质 (conveyor - frame)
- **组件类型**: `conveyor`
- **部件类型**: `frame`
- **支持状态**:
  - `default` - 深灰色金属色
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）

### 4. 链条机平板材质 (conveyor - belt)
- **组件类型**: `conveyor`
- **部件类型**: `belt`
- **支持状态**:
  - `default` - 深色橡胶质感
  - `working` - 稍亮的深灰色（工作中）

### 5. 链条机链条材质 (conveyor - chain)
- **组件类型**: `conveyor`
- **部件类型**: `chain`
- **支持状态**:
  - `default` - 灰色金属色
  - `working` - 稍亮的灰色金属（工作中）

### 5.5 输送线滚筒材质 (conveyor - roller)
- **组件类型**: `conveyor`
- **部件类型**: `roller`
- **支持状态**:
  - `default` - 深灰色金属滚筒
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）
- **特殊属性**: 
  - 双面渲染，避免某些角度消失
  - 光滑金属质感（光泽度45）

### 6. 堆垛机躯干材质 (stacker - body) 🟧 统一橘黄色
- **组件类型**: `stacker`
- **部件类型**: `body`
- **支持状态**:
  - `default` - 橘黄色工业漆面（与OHT车体相同）
  - `working` - 稍亮的橘黄色（工作中）
  - `error` - 偏红的橘黄色（错误）
- **设计理念**: 
  - 与OHT车体、机械臂关节使用相同的橘黄色
  - 所有主要移动设备统一醒目配色

### 7. 堆垛机载货台材质 (stacker - platform)
- **组件类型**: `stacker`
- **部件类型**: `platform`
- **支持状态**:
  - `default` - 柔和的蓝色金属
  - `working` - 绿色金属（工作中）
  - `error` - 红色金属（错误）

### 8. 堆垛机上货叉材质 (stacker - upperFork)
- **组件类型**: `stacker`
- **部件类型**: `upperFork`
- **支持状态**:
  - `default` - 黑色金属
  - `working` - 带绿色调的黑色金属（工作中）
  - `error` - 带红色调的黑色金属（错误）

### 9. 堆垛机下货叉材质 (stacker - lowerFork)
- **组件类型**: `stacker`
- **部件类型**: `lowerFork`
- **支持状态**:
  - `default` - 黑色金属
  - `working` - 带绿色调的黑色金属（工作中）
  - `error` - 带红色调的黑色金属（错误）

### 10. OHT支架材质 (oht - frame)
- **组件类型**: `oht`
- **部件类型**: `frame`
- **支持状态**:
  - `default` - 深灰色金属框架
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）

### 11. OHT轨道材质 (oht - track)
- **组件类型**: `oht`
- **部件类型**: `track`
- **支持状态**:
  - `default` - 深灰色金属轨道
  - `worn` - 磨损状态（稍亮）

### 12. OHT车体材质 (oht - body)
- **组件类型**: `oht`
- **部件类型**: `body`
- **支持状态**:
  - `default` - 鲜艳的橘黄色工业漆面
  - `working` - 更亮的橘黄色（工作中）
  - `error` - 偏红的橘黄色（错误）
  - `idle` - 稍暗的橘黄色（空闲）

### 13. OHT车轮材质 (oht - wheel)
- **组件类型**: `oht`
- **部件类型**: `wheel`
- **支持状态**:
  - `default` - 深灰黑色橡胶/金属混合质感
  - `working` - 稍亮的深灰色（工作中）

### 14. OHT固定车轮支架材质 (oht - wheelBracket) 🟧 与车体统一
- **组件类型**: `oht`
- **部件类型**: `wheelBracket`
- **支持状态**:
  - `default` - 橘黄色工业漆面（与车体相同）
  - `working` - 更亮的橘黄色（工作中）
  - `error` - 偏红的橘黄色（错误）
  - `idle` - 稍暗的橘黄色（空闲）
- **设计理念**: 
  - 与OHT车体使用相同的橘黄色，视觉统一
  - 整个OHT设备呈现完整的工业橘黄色外观

### 15. 机械臂支架材质 (robotArm - base)
- **组件类型**: `robotArm`
- **部件类型**: `base`
- **支持状态**:
  - `default` - 深灰色金属基座
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）

### 16. 机械臂一/二/三/四关节材质 (robotArm - joint1/2/3/4) 🟧 统一配色
- **组件类型**: `robotArm`
- **部件类型**: `joint1` / `joint2` / `joint3` / `joint4`
- **支持状态**:
  - `default` - 工业橘黄色（统一配色）
  - `working` - 更亮的橘黄色（工作中）
  - `error` - 偏红的橘黄色（错误）
- **设计理念**: 
  - 所有关节使用相同的橘黄色，整体视觉统一
  - 醒目的工业标准色，易于识别
  - 与OHT车体颜色协调

### 17. 提升机框架材质 (elevator - frame)
- **组件类型**: `elevator`
- **部件类型**: `frame`
- **支持状态**:
  - `default` - 深灰色金属框架
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）

### 18. 提升机亚克力罩材质 (elevator - acrylicCover) ⭐透明材质
- **组件类型**: `elevator`
- **部件类型**: `acrylicCover`
- **支持状态**:
  - `default` - 透明科技蓝（25%透明度）
  - `active` - 更亮的透明蓝（激活状态）
  - `dim` - 暗淡的透明蓝（18%透明度）
  - `warning` - 透明橙黄色（警告状态）
- **特殊属性**: 
  - 透明材质，带科技感蓝色自发光
  - 高光泽度，亚克力质感
  - 双面渲染，从任何角度可见

### 19. 视觉框架材质 (vision - frame)
- **组件类型**: `vision`
- **部件类型**: `frame`
- **支持状态**:
  - `default` - 深灰色金属框架
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）

### 20. 视觉罩子材质 (vision - cover) ⭐透明材质
- **组件类型**: `vision`
- **部件类型**: `cover`
- **支持状态**:
  - `default` - 透明淡蓝色（25%透明度）
  - `active` - 激活状态更亮的蓝色
  - `dim` - 暗淡状态（18%透明度）
  - `working` - 工作中带绿色调的透明蓝
- **特殊属性**: 
  - 透明玻璃材质
  - 高光泽度（80），玻璃质感
  - 双面渲染
  - 支持工作状态（带绿色调）

### 21. AGV车体材质 (agv - body) 🟧 统一橘黄色
- **组件类型**: `agv`
- **部件类型**: `body`
- **支持状态**:
  - `default` - 橘黄色工业漆面（与OHT车体相同）
  - `working` - 稍亮的橘黄色（工作中）
  - `error` - 偏红的橘黄色（错误）
  - `idle` - 稍暗的橘黄色（空闲）
- **设计理念**: 
  - 与OHT、机械臂、堆垛机使用相同的橘黄色
  - 所有移动设备统一醒目配色

### 22. AGV上装框架材质 (agv - mountFrame)
- **组件类型**: `agv`
- **部件类型**: `mountFrame`
- **支持状态**:
  - `default` - 深灰色金属框架
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）
- **特殊属性**: 
  - 双面渲染，避免消失

### 23. AGV上装滚筒材质 (agv - mountRoller)
- **组件类型**: `agv`
- **部件类型**: `mountRoller`
- **支持状态**:
  - `default` - 深灰色金属滚筒
  - `working` - 带绿色调的深灰色（工作中）
  - `error` - 带红色调的深灰色（错误）
- **特殊属性**: 
  - 双面渲染，避免消失
  - 光滑金属质感（光泽度45）

## 使用方法

### 1. 基本使用 - 智能模糊匹配 ⭐

在GLB模型加载完成后，会**自动遍历场景中所有对象**，根据对象名称进行**智能模糊匹配**并应用材质。

**工作原理：**
- 系统会遍历GLB场景中的所有对象
- 检查对象名称是否**包含**预设的关键字
- 自动匹配并应用相应的材质
- 无需手动指定具体对象名称

**示例匹配：**
```
对象名称: "输送线框架1" → 包含"输送线框架" → 应用输送线框架材质 ✅
对象名称: "输送线框架2" → 包含"输送线框架" → 应用输送线框架材质 ✅
对象名称: "输送线框架99" → 包含"输送线框架" → 应用输送线框架材质 ✅
对象名称: "conveyor_frame_01" → 包含"conveyor_frame" → 应用输送线框架材质 ✅
```

**优势：**
✅ 无需预先知道有多少条输送线
✅ 自动处理所有带数字后缀的对象
✅ 支持中英文和各种命名规则
✅ 添加新组件时只需更新匹配规则

```javascript
// 在3d.vue中已经自动集成
loader.load("/glb/aaa.glb", function (glb) {
  // 智能遍历并应用材质配置
  that.applyMaterialsToGLB(glb.scene);
});
```

### 2. 查看模型结构

打开浏览器控制台，可以看到完整的GLB模型结构层级，包括所有对象的名称：

```
========== GLB模型结构层级 ==========
├─ Scene [Scene] 位置:(0.00, 0.00, 0.00) 子节点:5
  ├─ 地板 [Mesh] [网格] (顶点:1234) ...
  ├─ 货架 [Group] ...
====================================
```

### 3. 添加新的匹配规则

如果控制台显示的对象名称与预设不匹配，可以在 `applyMaterialsToGLB` 方法的 `matchRules` 中添加新的关键字：

```javascript
const matchRules = [
  // 添加新的匹配规则
  { 
    keywords: ['您的对象名称关键字', 'your_object_keyword'], // 关键字列表
    componentType: 'floor',  // 组件类型
    partType: 'main',        // 部件类型
    state: 'floor1',         // 状态
    description: '显示名称' // 控制台显示的描述
  },
  // ... 其他规则
];
```

**示例：**假设您的GLB中有对象叫 "底座板01"，想应用地板材质：
```javascript
{ 
  keywords: ['底座板', 'base_plate'],
  componentType: 'floor', 
  partType: 'main', 
  state: 'floor1',
  description: '底座板'
},
```
这样所有包含"底座板"的对象（底座板01、底座板02...）都会自动应用地板材质！

### 4. 查看智能匹配结果

刷新页面后，控制台会显示所有匹配成功的对象：

```
========== 开始应用材质配置（智能匹配） ==========
🎯 智能匹配: 输送线框架1 → [输送线框架]
🎯 智能匹配: 输送线框架2 → [输送线框架]
🎯 智能匹配: 输送线滚筒1 → [输送线滚筒]
🎯 智能匹配: 输送线滚筒2 → [输送线滚筒]
🎯 智能匹配: 地板 → [地板]
🎯 智能匹配: 货架 → [货架]
...
✅ 材质应用完成，成功应用 25 个材质配置
========================================
```

### 5. 手动应用材质到指定对象

除了自动匹配，也可以手动应用材质：

```javascript
// 通过对象名称应用材质
materialManager.applyMaterialByName(
  scene,           // Three.js场景
  '对象名称',      // GLB中的对象名称
  'floor',         // 组件类型
  'main',          // 部件类型
  'floor1'         // 状态
);

// 或者直接通过对象引用应用
const object = scene.getObjectByName('地板');
materialManager.applyMaterialToObject(object, 'floor', 'main', 'floor1');
```

### 6. 动态切换材质状态

```javascript
// 例如：货架从空闲切换到有货状态
const shelf = this.ThreeEngine.getObjectByName('货架');
materialManager.applyMaterialToObject(shelf, 'shelf', 'main', 'occupied');

// 链条机从默认切换到工作中状态
const conveyorFrame = this.ThreeEngine.getObjectByName('链条机框架');
materialManager.applyMaterialToObject(conveyorFrame, 'conveyor', 'frame', 'working');
```

### 7. 批量应用材质

```javascript
// 批量配置多个对象的材质
materialManager.applyMaterialsToObjects([
  { object: obj1, componentType: 'floor', partType: 'main', state: 'floor1' },
  { object: obj2, componentType: 'shelf', partType: 'main', state: 'default' },
  { object: obj3, componentType: 'conveyor', partType: 'chain', state: 'working' },
]);
```

## 智能匹配调整步骤

### 步骤1：查看控制台输出的模型结构

刷新页面，在浏览器控制台查看：
1. **"GLB模型结构层级"** - 查看所有对象的名称
2. **"智能匹配结果"** - 查看哪些对象成功匹配了材质

### 步骤2：添加新的匹配关键字

如果某些对象没有匹配到，在 `3d.vue` 的 `applyMaterialsToGLB` 方法的 `matchRules` 中添加关键字：

```javascript
// 假设您的地板对象叫 "底面板01"
{ 
  keywords: ['地板', 'floor', '底面板'],  // 添加"底面板"关键字
  componentType: 'floor', 
  partType: 'main', 
  state: 'floor1',
  description: '地板'
},
```

**智能匹配的优势：**
- 添加一个关键字 "底面板"，所有底面板01、底面板02...底面板99 都会自动匹配 ✅
- 无需为每个对象单独配置 ✅

### 步骤3：调整材质参数（可选）

如果需要调整材质的颜色、光泽等参数，可以修改 `TMaterialManager.js` 中的配置：

```javascript
floor: {
  main: {
    floor1: {
      color: new THREE.Color(0.02, 0.04, 0.10),  // RGB颜色值
      emissive: new THREE.Color(0.005, 0.01, 0.03),  // 自发光颜色
      specular: new THREE.Color(0.08, 0.12, 0.20),  // 高光反射颜色
      shininess: 15,  // 光泽度 (0-100)
      transparent: false,  // 是否透明
      opacity: 1.0,  // 不透明度 (0-1)
    }
  }
}
```

### 步骤4：测试效果

保存文件后刷新页面，查看智能匹配效果。控制台会输出：

```
========== 开始应用材质配置（智能匹配） ==========
🎯 智能匹配: 地板 → [地板]
🎯 智能匹配: 货架 → [货架]
🎯 智能匹配: 输送线框架1 → [输送线框架]
🎯 智能匹配: 输送线框架2 → [输送线框架]
...
✅ 材质应用完成，成功应用 25 个材质配置
========================================
```

## 常见问题

### Q: 材质没有应用成功？
A: 
1. 查看控制台 "智能匹配" 输出，看对象是否被匹配到
2. 查看 "GLB模型结构层级" 输出，确认对象的实际名称
3. 在 `matchRules` 中添加新的关键字来匹配该对象
4. 关键字匹配是**包含匹配**，不需要完全一致

### Q: 某些角度看不到模型/模型消失？
A: 
已实现**三重保护机制**确保双面渲染：

**1. 材质配置层面** - 所有易消失的组件（机械臂关节等）已配置：
```javascript
side: THREE.DoubleSide, // 双面渲染
```

**2. 材质克隆层面** - 克隆材质时强制保留双面渲染属性：
```javascript
childMaterial.side = material.side; // 确保克隆后保留
```

**3. 强制修复层面** - GLB加载后自动执行 `fixDoubleSideRendering()`：
```javascript
// 遍历所有网格对象，强制设置双面渲染
scene.traverse((object) => {
  if (object.isMesh && object.material) {
    object.material.side = THREE.DoubleSide;
    object.material.needsUpdate = true;
  }
});
```

控制台会显示修复过程：
```
========== 开始修复双面渲染 ==========
🔧 修复双面渲染: 四关节
🔧 修复双面渲染: 三关节
...
✅ 双面渲染修复完成，修复了 X 个对象
========================================
```

如果问题仍然存在，请查看控制台确认该对象是否被修复。

### Q: 如何让材质更亮/更暗？
A: 调整以下参数：
- `color` - 增大RGB值使颜色更亮
- `emissive` - 增强自发光效果
- `shininess` - 增加光泽度

### Q: 如何添加新的组件类型？
A: 在 `TMaterialManager.js` 的 `materials` 对象中添加新的配置：

```javascript
materials: {
  // 添加新组件
  myComponent: {
    myPart: {
      default: {
        color: new THREE.Color(0.5, 0.5, 0.5),
        // ... 其他配置
      }
    }
  }
}
```

## 材质参数说明

### color (颜色)
物体的基本颜色，使用RGB值 (0-1范围)
- `new THREE.Color(1, 0, 0)` - 红色
- `new THREE.Color(0, 1, 0)` - 绿色
- `new THREE.Color(0, 0, 1)` - 蓝色

### emissive (自发光)
物体的自发光颜色，不受光照影响
- 数值越大，物体看起来越"发光"
- 适合用于夜间场景或突出重点

### specular (高光反射)
物体的高光反射颜色
- 影响物体在光照下的反光效果
- 金属材质通常使用较高的值

### shininess (光泽度)
物体表面的光泽程度 (0-100)
- 0-10: 哑光表面（橡胶、布料）
- 20-40: 半光泽（塑料、木材）
- 50-100: 高光泽（金属、玻璃）

### transparent (透明)
是否启用透明效果
- `true` - 启用透明，需配合 `opacity` 使用
- `false` - 不透明

### opacity (不透明度)
物体的不透明程度 (0-1)
- 0: 完全透明
- 1: 完全不透明
- 0.5: 半透明

## 地板网格纹理配置

地板材质使用程序生成的网格纹理，可在 `TMaterialManager.js` 的 `createMaterial` 方法中调整参数：

```javascript
const gridTexture = createGridTexture({
  size: 512,                                    // Canvas大小
  gridSize: 64,                                 // 网格单元大小
  lineWidth: 1.5,                               // 网格线宽度
  lineColor: 'rgba(0, 150, 255, 0.35)',        // 网格线颜色（淡蓝色）
  dotRadius: 2.5,                               // 圆点半径
  dotColor: 'rgba(0, 200, 255, 0.9)',          // 圆点颜色（亮蓝色）
  dotGlow: true,                                // 圆点是否发光
  backgroundColor: 'rgba(2, 4, 10, 1.0)',      // 背景颜色（深蓝黑色）
});
```

### 参数说明

**gridSize** - 网格单元大小
- 更小的值 = 更密集的网格
- 建议值：32-128

**dotRadius** - 圆点大小
- 建议值：1-5
- 过大会导致圆点重叠

**dotGlow** - 圆点发光效果
- `true` - 圆点周围有发光光晕
- `false` - 纯实心圆点

**颜色配置**
- `lineColor` - 网格线颜色，建议使用半透明
- `dotColor` - 圆点颜色，建议使用较高透明度
- `backgroundColor` - 背景色，建议深色

## 示例场景

### 场景1：设置货架有货状态
```javascript
const shelf = this.ThreeEngine.getObjectByName('货架01');
materialManager.applyMaterialToObject(shelf, 'shelf', 'main', 'occupied');
```

### 场景2：设置输送线工作中状态
```javascript
const frame = this.ThreeEngine.getObjectByName('输送线框架');
const belt = this.ThreeEngine.getObjectByName('输送线平板');
const chain = this.ThreeEngine.getObjectByName('链条');

materialManager.applyMaterialToObject(frame, 'conveyor', 'frame', 'working');
materialManager.applyMaterialToObject(belt, 'conveyor', 'belt', 'working');
materialManager.applyMaterialToObject(chain, 'conveyor', 'chain', 'working');
```

### 场景3：错误状态高亮显示
```javascript
const frame = this.ThreeEngine.getObjectByName('输送线框架');
materialManager.applyMaterialToObject(frame, 'conveyor', 'frame', 'error');
```

### 场景4：OHT天车工作状态切换
```javascript
// 获取OHT各个部件
const ohtFrame = this.ThreeEngine.getObjectByName('OHT支架');
const ohtTrack = this.ThreeEngine.getObjectByName('OHT轨道');
const ohtBody = this.ThreeEngine.getObjectByName('OHT车体');
const ohtWheel = this.ThreeEngine.getObjectByName('OHT车轮');
const ohtBracket = this.ThreeEngine.getObjectByName('OHT固定车轮支架');

// 批量设置为工作状态
materialManager.applyMaterialsToObjects([
  { object: ohtFrame, componentType: 'oht', partType: 'frame', state: 'working' },
  { object: ohtBody, componentType: 'oht', partType: 'body', state: 'working' },
  { object: ohtWheel, componentType: 'oht', partType: 'wheel', state: 'working' },
  { object: ohtBracket, componentType: 'oht', partType: 'wheelBracket', state: 'working' },
]);
```

### 场景5：OHT错误状态
```javascript
// OHT出现错误时，车体和支架变红
const ohtBody = this.ThreeEngine.getObjectByName('OHT车体');
const ohtFrame = this.ThreeEngine.getObjectByName('OHT支架');

materialManager.applyMaterialToObject(ohtBody, 'oht', 'body', 'error');
materialManager.applyMaterialToObject(ohtFrame, 'oht', 'frame', 'error');
```

### 场景6：机械臂完整状态切换
```javascript
// 获取机械臂各个关节
const armBase = this.ThreeEngine.getObjectByName('机械臂支架');
const joint1 = this.ThreeEngine.getObjectByName('一关节');
const joint2 = this.ThreeEngine.getObjectByName('二关节');
const joint3 = this.ThreeEngine.getObjectByName('三关节');
const joint4 = this.ThreeEngine.getObjectByName('四关节');

// 批量设置为工作状态
materialManager.applyMaterialsToObjects([
  { object: armBase, componentType: 'robotArm', partType: 'base', state: 'working' },
  { object: joint1, componentType: 'robotArm', partType: 'joint1', state: 'working' },
  { object: joint2, componentType: 'robotArm', partType: 'joint2', state: 'working' },
  { object: joint3, componentType: 'robotArm', partType: 'joint3', state: 'working' },
  { object: joint4, componentType: 'robotArm', partType: 'joint4', state: 'working' },
]);
```

### 场景7：机械臂错误状态 - 高亮末端执行器
```javascript
// 机械臂末端执行器发生错误
const joint4 = this.ThreeEngine.getObjectByName('四关节');
materialManager.applyMaterialToObject(joint4, 'robotArm', 'joint4', 'error');
```

### 场景8：提升机运行状态切换
```javascript
// 获取提升机部件
const elevatorFrame = this.ThreeEngine.getObjectByName('提升机框架');
const acrylicCover = this.ThreeEngine.getObjectByName('提升机亚克力罩');

// 设置为工作状态 - 框架变绿色调，罩子变亮
materialManager.applyMaterialToObject(elevatorFrame, 'elevator', 'frame', 'working');
materialManager.applyMaterialToObject(acrylicCover, 'elevator', 'acrylicCover', 'active');
```

### 场景9：提升机警告状态 - 亚克力罩变橙黄色
```javascript
// 提升机出现警告
const acrylicCover = this.ThreeEngine.getObjectByName('提升机亚克力罩');
materialManager.applyMaterialToObject(acrylicCover, 'elevator', 'acrylicCover', 'warning');
```

### 场景10：视觉系统工作状态
```javascript
// 获取视觉系统部件
const visionFrame = this.ThreeEngine.getObjectByName('视觉框架');
const visionCover = this.ThreeEngine.getObjectByName('视觉罩子');

// 设置为工作状态 - 框架变绿色调，罩子也变绿色调
materialManager.applyMaterialToObject(visionFrame, 'vision', 'frame', 'working');
materialManager.applyMaterialToObject(visionCover, 'vision', 'cover', 'working');
```

### 场景11：视觉系统激活状态 - 罩子变亮
```javascript
// 视觉系统拍照激活
const visionCover = this.ThreeEngine.getObjectByName('视觉罩子');
materialManager.applyMaterialToObject(visionCover, 'vision', 'cover', 'active');
```

### 场景12：AGV完整状态切换
```javascript
// 获取AGV所有部件
const agvBody = this.ThreeEngine.getObjectByName('AGV车体');
const agvFrame = this.ThreeEngine.getObjectByName('AGV上装框架');
const agvRoller = this.ThreeEngine.getObjectByName('AGV上装滚筒');

// 批量设置为工作状态
materialManager.applyMaterialsToObjects([
  { object: agvBody, componentType: 'agv', partType: 'body', state: 'working' },
  { object: agvFrame, componentType: 'agv', partType: 'mountFrame', state: 'working' },
  { object: agvRoller, componentType: 'agv', partType: 'mountRoller', state: 'working' },
]);

// 设置为空闲状态
materialManager.applyMaterialToObject(agvBody, 'agv', 'body', 'idle');

// 设置为错误状态
materialManager.applyMaterialsToObjects([
  { object: agvBody, componentType: 'agv', partType: 'body', state: 'error' },
  { object: agvFrame, componentType: 'agv', partType: 'mountFrame', state: 'error' },
  { object: agvRoller, componentType: 'agv', partType: 'mountRoller', state: 'error' },
]);
```

## 🟧 橘黄色设备家族

现在场景中所有主要移动设备都使用统一的橘黄色工业漆面：

1. ✅ **AGV车体** - 橘黄色
2. ✅ **OHT车体** - 橘黄色
3. ✅ **OHT车轮支架** - 橘黄色
4. ✅ **堆垛机躯干** - 橘黄色
5. ✅ **机械臂一关节** - 橘黄色
6. ✅ **机械臂二关节** - 橘黄色
7. ✅ **机械臂三关节** - 橘黄色
8. ✅ **机械臂四关节** - 橘黄色

**统一配色方案：**
- 🟧 **橘黄色** - 所有移动设备（AGV/OHT/堆垛机/机械臂）
- 🔲 **深灰色** - 结构件（框架、支架、轨道、货架）
- ⚫ **黑色** - 工具件（货叉、车轮）
- 🔵 **蓝色** - 功能件（载货台）
- 💠 **透明蓝** - 防护罩（亚克力罩、视觉罩）

**视觉效果：** 统一的橘黄色让所有活动设备在场景中一眼就能识别！🏗️

## 总结

材质管理器提供了一套完整的材质配置和管理方案：

### 核心特性
1. ✅ **智能模糊匹配** - 遍历所有对象，自动匹配关键字
2. ✅ **统一管理材质** - 集中配置所有组件的材质
3. ✅ **支持多种状态** - 默认/工作中/错误/空闲等状态切换
4. ✅ **自动批量应用** - 无需为每个对象单独配置
5. ✅ **详细日志输出** - 清晰显示匹配和应用结果
6. ✅ **双面渲染支持** - 避免某些角度看不见
7. ✅ **透明材质支持** - 亚克力罩等透明材质

### 智能匹配优势
- 🚀 **无限扩展**: 无论有多少条输送线（1-999），只要包含关键字就自动匹配
- 🎯 **灵活匹配**: 支持中英文、驼峰、下划线等各种命名规则
- 🛠️ **易于维护**: 只需维护匹配规则，不需要维护庞大的对象列表
- 📊 **清晰追踪**: 控制台显示所有匹配结果，一目了然

### 快速开始
1. 刷新页面，查看控制台的 "GLB模型结构层级" 和 "智能匹配" 输出
2. 如有未匹配的对象，在 `matchRules` 中添加关键字
3. 如需调整材质效果，修改 `TMaterialManager.js` 中的配置参数

通过智能匹配，您可以轻松管理成百上千个3D对象的材质！🎨✨

