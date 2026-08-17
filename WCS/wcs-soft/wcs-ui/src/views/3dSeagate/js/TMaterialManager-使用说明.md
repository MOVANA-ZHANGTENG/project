# 材质管理器使用说明

## 📚 概述

`TMaterialManager.js` 是一个专门用于管理3D场景中各种组件材质的工具类，支持按**组件类型**和**状态**来管理和应用材质。

## 🏗️ 架构设计

### 三级分类结构

```
材质配置
├── 组件类型 (componentType)
│   ├── 部件类型 (partType)
│   │   ├── 状态1 (state)
│   │   │   └── 材质配置
│   │   ├── 状态2
│   │   │   └── 材质配置
│   │   └── ...
```

### 示例结构

```javascript
materials = {
  stacker: {                    // 组件类型：堆垛机
    platform: {                 // 部件类型：载货台
      default: {...},           // 状态：默认
      working: {...},           // 状态：工作中
      error: {...},             // 状态：错误
      idle: {...}               // 状态：空闲
    },
    upperFork: {                // 部件类型：上货叉
      default: {...},
      working: {...},
      error: {...}
    }
  }
}
```

## 🚀 快速开始

### 1. 导入材质管理器

```javascript
import materialManager from './js/TMaterialManager.js';
```

### 2. 应用材质到3D对象

```javascript
// 方法1：直接应用材质
materialManager.applyMaterialToObject(object, 'stacker', 'platform', 'default');

// 方法2：创建材质后手动应用
const material = materialManager.createMaterial('stacker', 'platform', 'default');
object.material = material;

// 方法3：获取配置后自定义创建
const config = materialManager.getMaterialConfig('stacker', 'platform', 'default');
const customMaterial = new THREE.MeshPhongMaterial(config);
```

## 📦 已支持的组件

### 堆垛机 (stacker)

#### 载货台 (platform)
- `default` - 默认状态（蓝色金属）
- `working` - 工作状态（绿色）
- `error` - 错误状态（红色）
- `idle` - 空闲状态（灰色）

#### 上货叉 (upperFork)
- `default` - 默认状态（亮蓝色）
- `working` - 工作状态（绿色）
- `error` - 错误状态（红色）

#### 下货叉 (lowerFork)
- `default` - 默认状态（更亮的蓝色）
- `working` - 工作状态（绿色）
- `error` - 错误状态（红色）

### RGV (rgv)

#### 载货台 (platform)
- `default` - 默认状态（橘黄色）

## 💡 使用示例

### 示例1：在Vue组件中应用材质

```javascript
// 在3d.vue的methods中
applyPlatformMaterials() {
  const platforms = [];
  this.ThreeEngine.scene.traverse((obj) => {
    if (obj.name && obj.name.includes('载货台')) {
      platforms.push(obj);
    }
  });
  
  platforms.forEach((platform) => {
    // 使用材质管理器应用默认材质
    materialManager.applyMaterialToObject(platform, 'stacker', 'platform', 'default');
  });
}
```

### 示例2：根据状态切换材质

```javascript
// 设备状态改变时更新材质
updateEquipmentStatus(equipment, status) {
  let state = 'default';
  
  switch(status) {
    case 'RUNNING':
      state = 'working';
      break;
    case 'ERROR':
      state = 'error';
      break;
    case 'IDLE':
      state = 'idle';
      break;
  }
  
  // 应用新状态的材质
  materialManager.applyMaterialToObject(
    equipment, 
    'stacker', 
    'platform', 
    state
  );
}
```

### 示例3：批量创建材质

```javascript
// 为多个组件批量创建材质
const configs = [
  { componentType: 'stacker', partType: 'platform', state: 'default' },
  { componentType: 'stacker', partType: 'upperFork', state: 'default' },
  { componentType: 'stacker', partType: 'lowerFork', state: 'default' }
];

const materials = materialManager.createMaterials(configs);
```

## 🔧 高级功能

### 1. 添加新的组件类型

```javascript
// 添加新的组件材质配置
materialManager.addComponentMaterials('conveyor', {
  belt: {
    default: {
      color: new THREE.Color(0.3, 0.3, 0.3),
      emissive: new THREE.Color(0.0, 0.0, 0.0),
      specular: new THREE.Color(0.2, 0.2, 0.2),
      shininess: 20,
      transparent: false,
      opacity: 1.0,
      wireframe: false,
    },
    running: {
      color: new THREE.Color(0.2, 0.6, 0.3),
      emissive: new THREE.Color(0.05, 0.15, 0.08),
      specular: new THREE.Color(0.3, 0.3, 0.3),
      shininess: 30,
      transparent: false,
      opacity: 1.0,
      wireframe: false,
    }
  }
});
```

### 2. 更新现有材质配置

```javascript
// 更新特定状态的材质配置
materialManager.updateMaterialConfig('stacker', 'platform', 'default', {
  color: new THREE.Color(0.3, 0.5, 0.8),
  emissive: new THREE.Color(0.08, 0.12, 0.25),
  specular: new THREE.Color(0.6, 0.6, 0.6),
  shininess: 65,
  transparent: false,
  opacity: 1.0,
  wireframe: false,
});
```

### 3. 查询可用配置

```javascript
// 获取所有组件类型
const componentTypes = materialManager.getComponentTypes();
console.log(componentTypes); // ['stacker', 'rgv']

// 获取指定组件的所有部件类型
const partTypes = materialManager.getPartTypes('stacker');
console.log(partTypes); // ['platform', 'upperFork', 'lowerFork']

// 获取指定部件的所有状态
const states = materialManager.getStates('stacker', 'platform');
console.log(states); // ['default', 'working', 'error', 'idle']
```

## 📋 材质配置参数说明

每个材质配置对象包含以下参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `color` | THREE.Color | 基础颜色 |
| `emissive` | THREE.Color | 自发光颜色 |
| `specular` | THREE.Color | 高光反射颜色 |
| `shininess` | Number | 光泽度 (0-100) |
| `transparent` | Boolean | 是否透明 |
| `opacity` | Number | 不透明度 (0-1) |
| `wireframe` | Boolean | 是否显示线框 |

## 🎨 材质配色方案

### 堆垛机载货台

| 状态 | 颜色 | 用途 |
|------|------|------|
| default | 蓝色 (0.2, 0.4, 0.7) | 正常待机 |
| working | 绿色 (0.2, 0.7, 0.3) | 工作中 |
| error | 红色 (0.8, 0.2, 0.2) | 错误/故障 |
| idle | 灰色 (0.4, 0.4, 0.4) | 空闲 |

### 堆垛机货叉

- **上货叉**：较亮的蓝色系，光泽度70
- **下货叉**：最亮的蓝色系，光泽度80
- 区分度高，便于视觉识别

### RGV载货台

- **橘黄色** (1.0, 0.50, 0.10)：醒目、警示效果
- 高光泽度70，金属质感
- 橘黄色自发光，增强视觉冲击

## 🔄 迁移说明

### 已迁移的组件
- ✅ 堆垛机载货台 (stacker.platform)
- ✅ 堆垛机上货叉 (stacker.upperFork)
- ✅ 堆垛机下货叉 (stacker.lowerFork)

### 待迁移的组件
- ⏳ RGV车体
- ⏳ RGV链条
- ⏳ RGV装饰条
- ⏳ RGV轨道
- ⏳ 货架
- ⏳ 地板
- ⏳ 墙体
- ⏳ 堆垛机躯干

## 🎯 最佳实践

1. **统一管理**：所有材质配置都在 `TMaterialManager.js` 中定义
2. **状态驱动**：通过状态切换材质，而不是硬编码
3. **命名规范**：组件类型、部件类型、状态使用有意义的英文命名
4. **配色一致**：同类状态使用一致的配色方案
5. **性能优化**：使用材质克隆避免共享材质导致的问题

## 📝 注意事项

1. 材质管理器是单例模式，全局共享一个实例
2. 如果找不到指定的材质配置，会自动降级到默认配置
3. 对于包含子对象的3D对象，会自动遍历并应用材质
4. 材质的 `color`、`emissive`、`specular` 会被克隆，避免意外修改原配置

## 🔗 相关文件

- `TMaterialManager.js` - 材质管理器主文件
- `3d.vue` - Vue组件，使用材质管理器
- `TEngine.js` - Three.js引擎封装

## 📞 支持

如需添加新的组件材质配置或修改现有配置，请参考本文档的"高级功能"章节。

