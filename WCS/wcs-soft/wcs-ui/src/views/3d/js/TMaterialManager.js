/**
 * 材质管理器 - 统一管理3D场景中各种组件的材质配置
 * 支持按组件类型和状态获取不同的材质
 */

import * as THREE from "three";
import { createGridTexture, createGlowGridTexture } from './TGridTexture.js';

/**
 * 材质配置类
 * 管理所有3D组件的材质，按组件类型和状态分类
 */
class MaterialManager {
  constructor() {
    // 材质配置映射表
    this.materials = {
      // 地板组件材质 - 蓝灰色调（与边框统一），分4层
      floor: {
        // 地板主体材质 - 极低明度深蓝色
        main: {
          // 第1层 - 极低明度深蓝色，不透明
          floor1: {
            color: new THREE.Color(0.02, 0.025, 0.05), // 极低明度深蓝色（增加蓝度）
            emissive: new THREE.Color(0.005, 0.008, 0.020), // 偏蓝的自发光
            specular: new THREE.Color(0.06, 0.08, 0.18), // 偏蓝的高光
            shininess: 30, // 低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          
         },
        
        // 地板边界边框材质 - 深蓝色边框（降低明度，增加蓝度）
        border: {
          default: {
            color: new THREE.Color(0.08, 0.12, 0.22), // 深蓝色（降低明度，增加蓝度）
            emissive: new THREE.Color(0.025, 0.040, 0.10), // 偏蓝的自发光
            specular: new THREE.Color(0.18, 0.24, 0.45), // 偏蓝的高光泽
            shininess: 60, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          highlight: {
            color: new THREE.Color(0.12, 0.18, 0.35), // 高亮 - 更深的蓝色
            emissive: new THREE.Color(0.040, 0.070, 0.16), // 更强的蓝色自发光
            specular: new THREE.Color(0.25, 0.35, 0.55),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          }
        }
      },
      
      // 货架组件材质 - 深蓝色（降低明度，增加蓝度）
      shelf: {
        // 货架主体材质 - 深蓝色哑光
        main: {
          default: {
            color: new THREE.Color(0.08, 0.10, 0.16), // 深蓝色（降低明度，增加蓝度）
            emissive: new THREE.Color(0.012, 0.016, 0.035), // 偏蓝的自发光
            specular: new THREE.Color(0.06, 0.08, 0.18), // 偏蓝的高光，哑光效果
            shininess: 8, // 低光泽度，哑光效果
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.10, 0.12, 0.20), // 高亮 - 深蓝色
            emissive: new THREE.Color(0.018, 0.022, 0.045),
            specular: new THREE.Color(0.08, 0.10, 0.22),
            shininess: 10,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          occupied: {
            color: new THREE.Color(0.06, 0.12, 0.10), // 有货 - 带绿色调的深蓝色
            emissive: new THREE.Color(0.010, 0.020, 0.018),
            specular: new THREE.Color(0.06, 0.10, 0.12),
            shininess: 8,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          empty: {
            color: new THREE.Color(0.06, 0.08, 0.16), // 空货位 - 偏蓝的深蓝色
            emissive: new THREE.Color(0.010, 0.014, 0.035),
            specular: new THREE.Color(0.06, 0.08, 0.18),
            shininess: 8,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },
      
      // 输送线/链条机组件材质
      conveyor: {
        // 输送线框架材质 - 深蓝色（降低明度，增加蓝度）
        frame: {
          default: {
            color: new THREE.Color(0.05, 0.07, 0.14), // 深蓝色（框架层）
            emissive: new THREE.Color(0.010, 0.014, 0.030), // 偏蓝的自发光
            specular: new THREE.Color(0.12, 0.16, 0.32), // 偏蓝的高光
            shininess: 40, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.05, 0.09, 0.10), // 带绿色调的深蓝色
            emissive: new THREE.Color(0.010, 0.018, 0.020),
            specular: new THREE.Color(0.12, 0.18, 0.22),
            shininess: 42,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.10, 0.06, 0.08), // 带红色调的深蓝色
            emissive: new THREE.Color(0.020, 0.012, 0.016),
            specular: new THREE.Color(0.18, 0.14, 0.18),
            shininess: 40,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 输送带/平板材质 - 更深色橡胶
        belt: {
          default: {
            color: new THREE.Color(0.08, 0.08, 0.1), // 更深的灰黑色
            emissive: new THREE.Color(0.002, 0.002, 0.005), // 更微弱自发光
            specular: new THREE.Color(0.03, 0.03, 0.03), // 更低高光反射（橡胶质感）
            shininess: 5, // 更低光泽度，更哑光橡胶效果
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.08, 0.1, 0.1), // 稍亮的深灰色
            emissive: new THREE.Color(0.005, 0.01, 0.01),
            specular: new THREE.Color(0.03, 0.03, 0.03),
            shininess: 5,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 链条材质 - 深蓝灰色金属（降低明度，增加蓝度）
        chain: {
          default: {
            color: new THREE.Color(0.14, 0.16, 0.22), // 深蓝灰色金属
            emissive: new THREE.Color(0.020, 0.024, 0.040), // 偏蓝的自发光
            specular: new THREE.Color(0.22, 0.26, 0.38), // 偏蓝的高光
            shininess: 42, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.14, 0.18, 0.18), // 工作时带绿色调
            emissive: new THREE.Color(0.020, 0.030, 0.030),
            specular: new THREE.Color(0.22, 0.28, 0.32),
            shininess: 45,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 滚筒材质 - 深蓝色金属滚筒（适应全局深蓝色调）
        roller: {
          default: {
            color: new THREE.Color(0.12, 0.14, 0.20), // 深蓝色金属（活动部件层）
            emissive: new THREE.Color(0.018, 0.022, 0.040), // 偏蓝的自发光
            specular: new THREE.Color(0.24, 0.28, 0.42), // 偏蓝的高光，光滑质感
            shininess: 50, // 中高光泽度，滚筒光滑质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.12, 0.16, 0.16), // 工作时带绿色调的深蓝色
            emissive: new THREE.Color(0.018, 0.028, 0.032),
            specular: new THREE.Color(0.24, 0.30, 0.34),
            shininess: 52,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          },
          error: {
            color: new THREE.Color(0.18, 0.12, 0.14), // 错误时带红色调的深蓝色
            emissive: new THREE.Color(0.035, 0.024, 0.028),
            specular: new THREE.Color(0.32, 0.26, 0.30),
            shininess: 50,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          }
        }
      },
      
      // 堆垛机组件材质
      stacker: {
        // 躯干材质 - 橘黄色工业漆面（与OHT车体相同）
        body: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // 载货台材质 - 深蓝色金属（降低明度，增加蓝度）
        platform: {
          default: {
            color: new THREE.Color(0.08, 0.10, 0.18), // 深蓝色金属
            emissive: new THREE.Color(0.012, 0.016, 0.038), // 偏蓝的自发光
            specular: new THREE.Color(0.20, 0.24, 0.42), // 偏蓝的高光
            shininess: 55, // 金属光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.08, 0.14, 0.12), // 工作状态 - 带绿色调的深蓝色
            emissive: new THREE.Color(0.012, 0.025, 0.024),
            specular: new THREE.Color(0.20, 0.28, 0.28),
            shininess: 58,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.16, 0.09, 0.10), // 错误状态 - 带红色调的深蓝色
            emissive: new THREE.Color(0.032, 0.018, 0.020),
            specular: new THREE.Color(0.30, 0.20, 0.24),
            shininess: 55,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 上货叉材质 - 深蓝灰色金属
        upperFork: {
          default: {
            color: new THREE.Color(0.10, 0.12, 0.16), // 深蓝灰色金属
            emissive: new THREE.Color(0.015, 0.018, 0.032), // 偏蓝的自发光
            specular: new THREE.Color(0.30, 0.35, 0.50), // 偏蓝的高光
            shininess: 60, // 金属光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.10, 0.14, 0.14), // 工作状态 - 带绿色调的深蓝色
            emissive: new THREE.Color(0.015, 0.025, 0.028),
            specular: new THREE.Color(0.30, 0.38, 0.38),
            shininess: 62,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.16, 0.10, 0.12), // 错误状态 - 带红色调的深蓝色
            emissive: new THREE.Color(0.032, 0.020, 0.024),
            specular: new THREE.Color(0.36, 0.30, 0.34),
            shininess: 60,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 下货叉材质 - 深蓝灰色金属
        lowerFork: {
          default: {
            color: new THREE.Color(0.10, 0.12, 0.16), // 深蓝灰色金属
            emissive: new THREE.Color(0.015, 0.018, 0.032), // 偏蓝的自发光
            specular: new THREE.Color(0.30, 0.35, 0.50), // 偏蓝的高光
            shininess: 60, // 金属光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.10, 0.14, 0.14), // 工作状态 - 带绿色调的深蓝色
            emissive: new THREE.Color(0.015, 0.025, 0.028),
            specular: new THREE.Color(0.30, 0.38, 0.38),
            shininess: 62,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.16, 0.10, 0.12), // 错误状态 - 带红色调的深蓝色
            emissive: new THREE.Color(0.032, 0.020, 0.024),
            specular: new THREE.Color(0.36, 0.30, 0.34),
            shininess: 60,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 天地轨道材质 - 深蓝色（降低明度，增加蓝度）
        track: {
          default: {
            color: new THREE.Color(0.04, 0.06, 0.12), // 深蓝色（轨道层）
            emissive: new THREE.Color(0.008, 0.012, 0.028), // 偏蓝的自发光
            specular: new THREE.Color(0.10, 0.14, 0.28), // 偏蓝的高光
            shininess: 45, // 金属光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.04, 0.08, 0.08), // 工作状态 - 带绿色调的深蓝色
            emissive: new THREE.Color(0.008, 0.016, 0.016),
            specular: new THREE.Color(0.10, 0.16, 0.18),
            shininess: 47,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          highlight: {
            color: new THREE.Color(0.08, 0.12, 0.20), // 高亮状态 - 深蓝色
            emissive: new THREE.Color(0.016, 0.024, 0.045),
            specular: new THREE.Color(0.16, 0.22, 0.38),
            shininess: 55,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        }
      },
      
      // OHT天车组件材质
      oht: {
        // OHT支架材质 - 深蓝色（降低明度，增加蓝度）
        frame: {
          default: {
            color: new THREE.Color(0.06, 0.08, 0.16), // 深蓝色（支架层）
            emissive: new THREE.Color(0.012, 0.016, 0.035), // 偏蓝的自发光
            specular: new THREE.Color(0.14, 0.18, 0.34), // 偏蓝的高光
            shininess: 42, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.06, 0.10, 0.12), // 带绿色调的深蓝色
            emissive: new THREE.Color(0.012, 0.020, 0.024),
            specular: new THREE.Color(0.14, 0.20, 0.26),
            shininess: 44,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.12, 0.07, 0.09), // 带红色调的深蓝色
            emissive: new THREE.Color(0.024, 0.014, 0.018),
            specular: new THREE.Color(0.22, 0.16, 0.20),
            shininess: 42,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // OHT轨道材质 - 深蓝色（降低明度，增加蓝度）
        track: {
          default: {
            color: new THREE.Color(0.04, 0.06, 0.12), // 深蓝色（轨道层）
            emissive: new THREE.Color(0.008, 0.012, 0.028), // 偏蓝的自发光
            specular: new THREE.Color(0.10, 0.14, 0.28), // 偏蓝的高光
            shininess: 45, // 光滑轨道质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          worn: {
            color: new THREE.Color(0.05, 0.07, 0.13), // 磨损状态稍亮
            emissive: new THREE.Color(0.010, 0.014, 0.030),
            specular: new THREE.Color(0.11, 0.15, 0.29),
            shininess: 47,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // OHT车体材质 - 橘黄色工业车体
        body: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          idle: {
            color: new THREE.Color(0.60, 0.24, 0.06), // 空闲状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.010, 0.004, 0.001), // 极低自发光
            specular: new THREE.Color(0.48, 0.38, 0.30), // 降低高光
            shininess: 62, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // OHT车轮材质 - 深色橡胶/金属轮
        wheel: {
          default: {
            color: new THREE.Color(0.15, 0.15, 0.15), // 深灰黑色
            emissive: new THREE.Color(0.008, 0.008, 0.008), // 微弱自发光
            specular: new THREE.Color(0.20, 0.20, 0.20), // 低光泽，橡胶/金属混合质感
            shininess: 25, // 低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.16, 0.17, 0.16), // 工作时稍亮
            emissive: new THREE.Color(0.01, 0.012, 0.01),
            specular: new THREE.Color(0.22, 0.22, 0.22),
            shininess: 28,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // OHT固定车轮支架材质 - 橘黄色工业漆面（与车体相同）
        wheelBracket: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          idle: {
            color: new THREE.Color(0.60, 0.24, 0.06), // 空闲状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.010, 0.004, 0.001), // 极低自发光
            specular: new THREE.Color(0.48, 0.38, 0.30), // 降低高光
            shininess: 62, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        }
      },
      
      // 机械臂组件材质
      robotArm: {
        // 机械臂支架材质 - 深蓝色（降低明度，增加蓝度）
        base: {
          default: {
            color: new THREE.Color(0.06, 0.08, 0.16), // 深蓝色（支架层）
            emissive: new THREE.Color(0.012, 0.016, 0.035), // 偏蓝的自发光
            specular: new THREE.Color(0.14, 0.18, 0.34), // 偏蓝的高光
            shininess: 42, // 金属光泽
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.06, 0.10, 0.12), // 带绿色调的深蓝色
            emissive: new THREE.Color(0.012, 0.020, 0.024),
            specular: new THREE.Color(0.14, 0.20, 0.26),
            shininess: 44,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.12, 0.07, 0.09), // 带红色调的深蓝色
            emissive: new THREE.Color(0.024, 0.014, 0.018),
            specular: new THREE.Color(0.22, 0.16, 0.20),
            shininess: 42,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // 一关节材质 - 工业橘黄色（基座关节）
        joint1: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // 二关节材质 - 工业橘黄色（与一关节相同）
        joint2: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // 三关节材质 - 工业橘黄色（与一关节相同）
        joint3: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // 四关节材质 - 工业橘黄色（与一关节相同）
        joint4: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        }
      },
      
      // 提升机组件材质
      elevator: {
        // 提升机框架材质 - 深蓝色（降低明度，增加蓝度）
        frame: {
          default: {
            color: new THREE.Color(0.05, 0.07, 0.14), // 深蓝色（框架层）
            emissive: new THREE.Color(0.010, 0.014, 0.030), // 偏蓝的自发光
            specular: new THREE.Color(0.12, 0.16, 0.32), // 偏蓝的高光
            shininess: 40, // 金属光泽
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.05, 0.09, 0.10), // 带绿色调的深蓝色
            emissive: new THREE.Color(0.010, 0.018, 0.020),
            specular: new THREE.Color(0.12, 0.18, 0.22),
            shininess: 42,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.10, 0.06, 0.08), // 带红色调的深蓝色
            emissive: new THREE.Color(0.020, 0.012, 0.016),
            specular: new THREE.Color(0.18, 0.14, 0.18),
            shininess: 40,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // 提升机亚克力罩材质 - 透明淡蓝色
        acrylicCover: {
          default: {
            color: new THREE.Color(0.05, 0.12, 0.20), // 更淡的蓝色调
            emissive: new THREE.Color(0.008, 0.025, 0.05), // 大幅降低自发光
            specular: new THREE.Color(0.50, 0.55, 0.60), // 降低高光泽
            shininess: 80, // 降低光泽度
            transparent: true, // 透明
            opacity: 0.25, // 更透明（从45%降到25%）
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染，透明材质必须
            depthWrite: false, // 透明物体不写入深度缓冲
          },
          active: {
            color: new THREE.Color(0.08, 0.15, 0.28), // 激活状态更亮的蓝色
            emissive: new THREE.Color(0.015, 0.04, 0.08), // 降低自发光
            specular: new THREE.Color(0.55, 0.60, 0.65),
            shininess: 85,
            transparent: true,
            opacity: 0.35, // 降低透明度
            wireframe: false,
            side: THREE.DoubleSide,
            depthWrite: false,
          },
          dim: {
            color: new THREE.Color(0.03, 0.08, 0.15), // 暗淡状态
            emissive: new THREE.Color(0.005, 0.015, 0.03), // 更弱的自发光
            specular: new THREE.Color(0.45, 0.50, 0.55),
            shininess: 75,
            transparent: true,
            opacity: 0.18, // 更透明
            wireframe: false,
            side: THREE.DoubleSide,
            depthWrite: false,
          },
          warning: {
            color: new THREE.Color(0.25, 0.15, 0.05), // 警告状态 - 淡橙黄色调
            emissive: new THREE.Color(0.08, 0.05, 0.015), // 降低橙黄色自发光
            specular: new THREE.Color(0.60, 0.55, 0.50),
            shininess: 80,
            transparent: true,
            opacity: 0.30,
            wireframe: false,
            side: THREE.DoubleSide,
            depthWrite: false,
          }
        }
      },
      
      // 视觉系统组件材质
      vision: {
        // 视觉框架材质 - 深蓝色（降低明度，增加蓝度）
        frame: {
          default: {
            color: new THREE.Color(0.05, 0.07, 0.14), // 深蓝色（框架层）
            emissive: new THREE.Color(0.010, 0.014, 0.030), // 偏蓝的自发光
            specular: new THREE.Color(0.12, 0.16, 0.32), // 偏蓝的高光
            shininess: 40, // 金属光泽
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.05, 0.09, 0.10), // 带绿色调的深蓝色
            emissive: new THREE.Color(0.010, 0.018, 0.020),
            specular: new THREE.Color(0.12, 0.18, 0.22),
            shininess: 42,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.10, 0.06, 0.08), // 带红色调的深蓝色
            emissive: new THREE.Color(0.020, 0.012, 0.016),
            specular: new THREE.Color(0.18, 0.14, 0.18),
            shininess: 40,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // 视觉罩子材质 - 透明淡蓝色玻璃
        cover: {
          default: {
            color: new THREE.Color(0.05, 0.12, 0.20), // 淡蓝色调
            emissive: new THREE.Color(0.008, 0.025, 0.05), // 微弱蓝色自发光
            specular: new THREE.Color(0.50, 0.55, 0.60), // 玻璃光泽
            shininess: 80, // 高光泽度
            transparent: true, // 透明
            opacity: 0.25, // 非常透明
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染，透明材质必须
            depthWrite: false, // 透明物体不写入深度缓冲
          },
          active: {
            color: new THREE.Color(0.08, 0.15, 0.28), // 激活状态更亮的蓝色
            emissive: new THREE.Color(0.015, 0.04, 0.08), // 稍强的自发光
            specular: new THREE.Color(0.55, 0.60, 0.65),
            shininess: 85,
            transparent: true,
            opacity: 0.35,
            wireframe: false,
            side: THREE.DoubleSide,
            depthWrite: false,
          },
          dim: {
            color: new THREE.Color(0.03, 0.08, 0.15), // 暗淡状态
            emissive: new THREE.Color(0.005, 0.015, 0.03),
            specular: new THREE.Color(0.45, 0.50, 0.55),
            shininess: 75,
            transparent: true,
            opacity: 0.18, // 更透明
            wireframe: false,
            side: THREE.DoubleSide,
            depthWrite: false,
          },
          working: {
            color: new THREE.Color(0.08, 0.20, 0.15), // 工作中带绿色调
            emissive: new THREE.Color(0.015, 0.05, 0.03),
            specular: new THREE.Color(0.55, 0.60, 0.58),
            shininess: 82,
            transparent: true,
            opacity: 0.30,
            wireframe: false,
            side: THREE.DoubleSide,
            depthWrite: false,
          }
        }
      },
      
      // AGV组件材质
      agv: {
        // AGV车体材质 - 橘黄色工业车体（与OHT车体相同）
        body: {
          default: {
            color: new THREE.Color(0.65, 0.25, 0.06), // 降低明度的偏红橘色
            emissive: new THREE.Color(0.012, 0.005, 0.001), // 极低自发光
            specular: new THREE.Color(0.5, 0.4, 0.3), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.72, 0.28, 0.08), // 工作状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.018, 0.007, 0.002), // 极低自发光
            specular: new THREE.Color(0.55, 0.45, 0.35), // 降低高光
            shininess: 70, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.75, 0.22, 0.08), // 错误状态 - 降低明度的强烈偏红橘色
            emissive: new THREE.Color(0.025, 0.006, 0.002), // 极低自发光
            specular: new THREE.Color(0.50, 0.35, 0.28), // 降低高光
            shininess: 65, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          idle: {
            color: new THREE.Color(0.60, 0.24, 0.06), // 空闲状态 - 降低明度的偏红橘色
            emissive: new THREE.Color(0.010, 0.004, 0.001), // 极低自发光
            specular: new THREE.Color(0.48, 0.38, 0.30), // 降低高光
            shininess: 62, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // AGV上装框架材质 - 深蓝色（降低明度，增加蓝度）
        mountFrame: {
          default: {
            color: new THREE.Color(0.05, 0.07, 0.14), // 深蓝色（框架层）
            emissive: new THREE.Color(0.010, 0.014, 0.030), // 偏蓝的自发光
            specular: new THREE.Color(0.12, 0.16, 0.32), // 偏蓝的高光
            shininess: 40, // 金属光泽
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.05, 0.09, 0.10), // 带绿色调的深蓝色
            emissive: new THREE.Color(0.010, 0.018, 0.020),
            specular: new THREE.Color(0.12, 0.18, 0.22),
            shininess: 42,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          error: {
            color: new THREE.Color(0.10, 0.06, 0.08), // 带红色调的深蓝色
            emissive: new THREE.Color(0.020, 0.012, 0.016),
            specular: new THREE.Color(0.18, 0.14, 0.18),
            shininess: 40,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          }
        },
        
        // AGV上装滚筒材质 - 深蓝色金属滚筒（适应全局深蓝色调）
        mountRoller: {
          default: {
            color: new THREE.Color(0.12, 0.14, 0.20), // 深蓝色金属（活动部件层）
            emissive: new THREE.Color(0.018, 0.022, 0.040), // 偏蓝的自发光
            specular: new THREE.Color(0.24, 0.28, 0.42), // 偏蓝的高光，光滑质感
            shininess: 50, // 中高光泽度，滚筒光滑质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
          },
          working: {
            color: new THREE.Color(0.12, 0.16, 0.16), // 工作时带绿色调的深蓝色
            emissive: new THREE.Color(0.018, 0.028, 0.032),
            specular: new THREE.Color(0.24, 0.30, 0.34),
            shininess: 52,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          },
          error: {
            color: new THREE.Color(0.18, 0.12, 0.14), // 错误时带红色调的深蓝色
            emissive: new THREE.Color(0.035, 0.024, 0.028),
            specular: new THREE.Color(0.32, 0.26, 0.30),
            shininess: 50,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            side: THREE.DoubleSide,
          }
        }
      },
      
      // 货物/托盘组件材质
      pallet: {
        main: {
          default: {
            color: new THREE.Color(0.78, 0.78, 0.78), // 偏灰的乳白色
            emissive: new THREE.Color(0.06, 0.06, 0.06), // 轻微自发光，增强可见度
            specular: new THREE.Color(0.3, 0.3, 0.3), // 中等高光反射
            shininess: 25, // 中低光泽度，塑料/木质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      }
    };
  }

  /**
   * 获取材质配置
   * @param {string} componentType - 组件类型 (如: 'floor', 'shelf', 'conveyor')
   * @param {string} partType - 部件类型 (如: 'main', 'frame', 'belt')
   * @param {string} state - 状态 (如: 'default', 'floor1', 'floor2')
   * @returns {Object} 材质配置对象
   */
  getMaterialConfig(componentType, partType, state = 'default') {
    try {
      const config = this.materials[componentType]?.[partType]?.[state];
      if (!config) {
        console.warn(`未找到材质配置: ${componentType}.${partType}.${state}，使用默认配置`);
        return this.materials[componentType]?.[partType]?.default || this.getDefaultMaterialConfig();
      }
      return config;
    } catch (error) {
      console.error('获取材质配置失败:', error);
      return this.getDefaultMaterialConfig();
    }
  }

  /**
   * 创建Three.js材质对象
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   * @returns {THREE.MeshPhongMaterial} Three.js材质对象
   */
  createMaterial(componentType, partType, state = 'default') {
    const config = this.getMaterialConfig(componentType, partType, state);
    
    const materialOptions = {
      color: config.color.clone(),
      emissive: config.emissive.clone(),
      specular: config.specular.clone(),
      shininess: config.shininess,
      transparent: config.transparent,
      opacity: config.opacity,
      wireframe: config.wireframe,
      side: config.side || THREE.FrontSide,
      flatShading: config.flatShading !== undefined ? config.flatShading : false,
      depthWrite: config.depthWrite !== undefined ? config.depthWrite : true,
    };
    
    // 如果是地板材质，添加网格纹理
    // if (componentType === 'floor' && partType === 'main') {
    //   const gridTexture = createGridTexture({
    //     size: 10,
    //     gridSize: 10,
    //     lineWidth: 0.1,
    //     lineColor: 'rgba(0, 150, 255, 0.35)',     // 淡蓝色网格线
    //     dotRadius: 2.5,
    //     dotColor: 'rgba(0, 200, 255, 0.9)',       // 亮蓝色圆点
    //     dotGlow: true,
    //     backgroundColor: 'rgba(2, 4, 10, 1.0)',   // 深蓝黑色背景
    //   });
    //   materialOptions.map = gridTexture;
    // }
    
    return new THREE.MeshPhongMaterial(materialOptions);
  }

  /**
   * 获取默认材质配置
   * @returns {Object} 默认材质配置
   */
  getDefaultMaterialConfig() {
    return {
      color: new THREE.Color(0.5, 0.5, 0.5),
      emissive: new THREE.Color(0.0, 0.0, 0.0),
      specular: new THREE.Color(0.3, 0.3, 0.3),
      shininess: 30,
      transparent: false,
      opacity: 1.0,
      wireframe: false,
    };
  }

  /**
   * 应用材质到3D对象
   * @param {THREE.Object3D} object - 3D对象
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   */
  applyMaterialToObject(object, componentType, partType, state = 'default') {
    if (!object) {
      console.warn('对象不存在，无法应用材质');
      return;
    }

    const material = this.createMaterial(componentType, partType, state);
    
    // 如果对象是网格对象，直接替换材质
    if (object.isMesh) {
      object.material = material;
      object.material.needsUpdate = true; // 强制更新材质
      console.log(`✅ 已应用材质到: ${object.name} [${componentType}.${partType}.${state}]`);
    } 
    // 如果对象包含子对象，遍历子对象并替换材质
    else {
      object.traverse((child) => {
        if (child.isMesh) {
          const childMaterial = material.clone(); // 克隆材质
          
          // 确保克隆后的材质保留所有关键属性
          childMaterial.side = material.side;
          childMaterial.transparent = material.transparent;
          childMaterial.opacity = material.opacity;
          childMaterial.depthWrite = material.depthWrite;
          childMaterial.needsUpdate = true; // 强制更新材质
          
          // 额外确保双面渲染（针对消失问题的加强处理）
          if (material.side === THREE.DoubleSide) {
            childMaterial.side = THREE.DoubleSide;
          }
          
          child.material = childMaterial;
          child.material.needsUpdate = true; // 确保材质更新
          
          console.log(`✅ 已应用材质到子对象: ${child.name} [${componentType}.${partType}.${state}] side:${childMaterial.side}`);
        }
      });
    }
  }

  /**
   * 批量应用材质到多个对象
   * @param {Array} objectConfigs - 对象配置数组 [{object, componentType, partType, state}]
   */
  applyMaterialsToObjects(objectConfigs) {
    objectConfigs.forEach(config => {
      this.applyMaterialToObject(
        config.object,
        config.componentType,
        config.partType,
        config.state || 'default'
      );
    });
  }

  /**
   * 根据对象名称应用材质
   * @param {THREE.Scene} scene - Three.js场景
   * @param {string} objectName - 对象名称
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   */
  applyMaterialByName(scene, objectName, componentType, partType, state = 'default') {
    const object = scene.getObjectByName(objectName);
    if (object) {
      this.applyMaterialToObject(object, componentType, partType, state);
    } else {
      console.warn(`未找到对象: ${objectName}`);
    }
  }
}

// 创建单例实例
const materialManager = new MaterialManager();

// 导出单例实例和类
export { materialManager, MaterialManager };
export default materialManager;

