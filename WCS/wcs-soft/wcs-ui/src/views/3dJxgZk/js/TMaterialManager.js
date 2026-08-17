/**
 * 材质管理器 - 统一管理3D场景中各种组件的材质配置
 * 支持按组件类型和状态获取不同的材质
 */

import * as THREE from "three";

/**
 * 材质配置类
 * 管理所有3D组件的材质，按组件类型和状态分类
 */
class MaterialManager {
  constructor() {
    // 材质配置映射表
    this.materials = {
      // 堆垛机组件材质 - 科技感风格
      stacker: {
        // 躯干材质 - 深蓝黑色金属，带科技蓝色调
        body: {
          default: {
            color: new THREE.Color(0.20, 0.22, 0.30), // 深蓝黑色金属，科技感
            emissive: new THREE.Color(0.06, 0.08, 0.15), // 蓝色自发光，增强科技感
            specular: new THREE.Color(0.35, 0.4, 0.5), // 蓝色调高光反射
            shininess: 55, // 高光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.25, 0.30, 0.28), // 工作状态带青绿色调
            emissive: new THREE.Color(0.10, 0.15, 0.12), // 青绿色自发光
            specular: new THREE.Color(0.45, 0.5, 0.45),
            shininess: 65,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.40, 0.25, 0.25), // 错误状态红色调
            emissive: new THREE.Color(0.15, 0.05, 0.05), // 红色自发光
            specular: new THREE.Color(0.5, 0.35, 0.35),
            shininess: 55,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 载货台材质 - 科技蓝色，强烈发光
        platform: {
          // 默认状态 - 科技蓝色金属
          default: {
            color: new THREE.Color(0.15, 0.35, 0.65), // 科技蓝色金属色
            emissive: new THREE.Color(0.08, 0.18, 0.35), // 强烈蓝色自发光，科技感
            specular: new THREE.Color(0.5, 0.6, 0.7), // 蓝色调高光反射
            shininess: 70, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 青绿色发光
          working: {
            color: new THREE.Color(0.2, 0.75, 0.4),
            emissive: new THREE.Color(0.10, 0.35, 0.15), // 强烈青绿色自发光
            specular: new THREE.Color(0.5, 0.7, 0.6),
            shininess: 75,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色发光
          error: {
            color: new THREE.Color(0.85, 0.25, 0.25),
            emissive: new THREE.Color(0.40, 0.10, 0.10), // 强烈红色自发光
            specular: new THREE.Color(0.6, 0.5, 0.5),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 空闲状态 - 深蓝灰色
          idle: {
            color: new THREE.Color(0.25, 0.28, 0.35),
            emissive: new THREE.Color(0.03, 0.04, 0.06), // 微弱蓝色自发光
            specular: new THREE.Color(0.3, 0.35, 0.4),
            shininess: 50,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 上货叉材质 - 科技青色，强烈发光
        upperFork: {
          // 默认状态 - 科技青色
          default: {
            color: new THREE.Color(0.15, 0.45, 0.75), // 科技青色金属
            emissive: new THREE.Color(0.10, 0.25, 0.45), // 强烈青色自发光
            specular: new THREE.Color(0.5, 0.65, 0.8), // 青色高光反射
            shininess: 80, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 青绿色发光
          working: {
            color: new THREE.Color(0.2, 0.85, 0.5),
            emissive: new THREE.Color(0.15, 0.45, 0.25), // 强烈青绿色自发光
            specular: new THREE.Color(0.6, 0.8, 0.7),
            shininess: 85,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色发光
          error: {
            color: new THREE.Color(0.95, 0.25, 0.25),
            emissive: new THREE.Color(0.50, 0.15, 0.15), // 强烈红色自发光
            specular: new THREE.Color(0.8, 0.6, 0.6),
            shininess: 80,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 下货叉材质 - 更亮的科技青色
        lowerFork: {
          // 默认状态 - 更亮的科技青色
          default: {
            color: new THREE.Color(0.10, 0.55, 0.85), // 更亮的科技青色
            emissive: new THREE.Color(0.12, 0.30, 0.50), // 强烈青色自发光
            specular: new THREE.Color(0.6, 0.75, 0.9), // 青色高光反射
            shininess: 90, // 极高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 青绿色发光
          working: {
            color: new THREE.Color(0.15, 0.95, 0.6),
            emissive: new THREE.Color(0.18, 0.50, 0.30), // 极强青绿色自发光
            specular: new THREE.Color(0.7, 0.9, 0.8),
            shininess: 95,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色发光
          error: {
            color: new THREE.Color(1.0, 0.20, 0.20),
            emissive: new THREE.Color(0.60, 0.20, 0.20), // 极强红色自发光
            specular: new THREE.Color(0.9, 0.7, 0.7),
            shininess: 90,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },

      // RGV组件材质 - 科技感风格
      rgv: {
        // 车体材质 - 深蓝黑色金属，带科技蓝色调
        body: {
          default: {
            color: new THREE.Color(0.22, 0.24, 0.32), // 深蓝黑色金属，科技感
            emissive: new THREE.Color(0.08, 0.10, 0.18), // 蓝色自发光，增强科技感
            specular: new THREE.Color(0.4, 0.45, 0.55), // 蓝色调高光反射
            shininess: 65, // 高光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.28, 0.35, 0.30), // 工作状态带青绿色调
            emissive: new THREE.Color(0.12, 0.20, 0.15), // 青绿色自发光
            specular: new THREE.Color(0.5, 0.6, 0.55),
            shininess: 75,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.45, 0.25, 0.25), // 错误状态红色调
            emissive: new THREE.Color(0.25, 0.08, 0.08), // 红色自发光
            specular: new THREE.Color(0.6, 0.4, 0.4),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 载货台材质 - 科技橙色，强烈发光
        platform: {
          default: {
            color: new THREE.Color(1.0, 0.55, 0.15), // 科技橙色
            emissive: new THREE.Color(0.50, 0.25, 0.05), // 强烈橙色自发光，科技感
            specular: new THREE.Color(0.9, 0.7, 0.4), // 橙色高光反射
            shininess: 80, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(1.0, 0.65, 0.25),
            emissive: new THREE.Color(0.60, 0.35, 0.10), // 更强的橙色自发光
            specular: new THREE.Color(1.0, 0.8, 0.5),
            shininess: 85,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(1.0, 0.3, 0.2),
            emissive: new THREE.Color(0.50, 0.10, 0.05), // 红色自发光
            specular: new THREE.Color(0.9, 0.5, 0.3),
            shininess: 80,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 链条材质 - 深蓝灰色金属，带蓝色发光
        chain: {
          default: {
            color: new THREE.Color(0.30, 0.32, 0.40), // 深蓝灰色金属
            emissive: new THREE.Color(0.05, 0.06, 0.12), // 蓝色自发光
            specular: new THREE.Color(0.35, 0.38, 0.45), // 蓝色调高光反射
            shininess: 50, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 装饰条材质 - 强烈科技蓝色发光
        decoration: {
          default: {
            color: new THREE.Color(0.15, 0.55, 1.0), // 强烈科技蓝色
            emissive: new THREE.Color(0.08, 0.25, 0.45), // 强烈蓝色自发光
            specular: new THREE.Color(0.5, 0.7, 0.9), // 蓝色高光反射
            shininess: 90, // 极高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.20, 0.65, 1.0),
            emissive: new THREE.Color(0.12, 0.35, 0.55), // 更强的蓝色自发光
            specular: new THREE.Color(0.6, 0.8, 1.0),
            shininess: 95,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 轨道材质 - 深蓝黑色金属，带微弱蓝色发光
        track: {
          default: {
            color: new THREE.Color(0.20, 0.22, 0.28), // 深蓝黑色金属
            emissive: new THREE.Color(0.03, 0.04, 0.08), // 蓝色自发光
            specular: new THREE.Color(0.25, 0.28, 0.35), // 蓝色调高光反射
            shininess: 35, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },

      // 货架组件材质
      shelf: {
        // 货架主体材质 - 深灰色哑光
        main: {
          default: {
            color: new THREE.Color(0.25, 0.25, 0.25), // 深灰色，比原来暗很多
            emissive: new THREE.Color(0.01, 0.01, 0.01), // 更微弱自发光
            specular: new THREE.Color(0.05, 0.05, 0.05), // 更低高光反射
            shininess: 3, // 更低光泽度，更哑光效果
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.35, 0.35, 0.35), // 稍亮的深灰色
            emissive: new THREE.Color(0.02, 0.02, 0.02), // 稍强自发光
            specular: new THREE.Color(0.08, 0.08, 0.08),
            shininess: 5,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          occupied: {
            color: new THREE.Color(0.2, 0.3, 0.2), // 带绿色调的深灰色
            emissive: new THREE.Color(0.01, 0.03, 0.01),
            specular: new THREE.Color(0.05, 0.05, 0.05),
            shininess: 3,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          empty: {
            color: new THREE.Color(0.2, 0.2, 0.3), // 带蓝色调的深灰色
            emissive: new THREE.Color(0.01, 0.01, 0.03),
            specular: new THREE.Color(0.05, 0.05, 0.05),
            shininess: 3,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },

      // 墙体组件材质
      wall: {
        // 墙体主体材质 - 科技感深蓝色，带辉光效果
        main: {
          default: {
            color: new THREE.Color(0.06, 0.12, 0.24), // 深蓝色底色
            emissive: new THREE.Color(0.10, 0.20, 0.45), // 强烈的蓝色自发光，产生明显辉光效果
            specular: new THREE.Color(0.3, 0.4, 0.6), // 蓝色高光反射
            shininess: 50, // 中高光泽度，玻璃质感
            transparent: true, // 半透明
            opacity: 0.75, // 75%不透明度，增强科技通透感
            wireframe: false,
            side: THREE.DoubleSide, // 双面渲染
            flatShading: false, // 平滑着色
          },
          active: {
            color: new THREE.Color(0.08, 0.15, 0.30),
            emissive: new THREE.Color(0.15, 0.25, 0.55), // 更强的辉光
            specular: new THREE.Color(0.4, 0.5, 0.7),
            shininess: 60,
            transparent: true,
            opacity: 0.80,
            wireframe: false,
            side: THREE.DoubleSide,
            flatShading: false,
          },
          dim: {
            color: new THREE.Color(0.04, 0.08, 0.18),
            emissive: new THREE.Color(0.05, 0.10, 0.25), // 较弱的辉光
            specular: new THREE.Color(0.2, 0.3, 0.5),
            shininess: 40,
            transparent: true,
            opacity: 0.70,
            wireframe: false,
            side: THREE.DoubleSide,
            flatShading: false,
          }
        }
      },

      // 输送线组件材质
      conveyor: {
        // 输送线框架材质 - 深灰色金属
        frame: {
          default: {
            color: new THREE.Color(0.2, 0.2, 0.22), // 深灰色金属色
            emissive: new THREE.Color(0.005, 0.005, 0.005), // 更微弱自发光
            specular: new THREE.Color(0.15, 0.15, 0.15), // 降低高光反射
            shininess: 25, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.2, 0.22, 0.2), // 带绿色调的深灰色
            emissive: new THREE.Color(0.01, 0.02, 0.01),
            specular: new THREE.Color(0.15, 0.15, 0.15),
            shininess: 25,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.25, 0.18, 0.18), // 带红色调的深灰色
            emissive: new THREE.Color(0.04, 0.01, 0.01),
            specular: new THREE.Color(0.15, 0.15, 0.15),
            shininess: 25,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 输送带材质 - 更深色橡胶
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

        // 滚筒材质 - 深灰色金属
        roller: {
          default: {
            color: new THREE.Color(0.25, 0.25, 0.28), // 深灰色金属
            emissive: new THREE.Color(0.01, 0.01, 0.01), // 更微弱自发光
            specular: new THREE.Color(0.2, 0.2, 0.22), // 降低高光反射（金属感）
            shininess: 30, // 降低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.25, 0.28, 0.28), // 稍亮的深灰色金属
            emissive: new THREE.Color(0.015, 0.025, 0.025),
            specular: new THREE.Color(0.2, 0.22, 0.22),
            shininess: 32,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 链条槽材质 - 更深色金属槽道
        chainGroove: {
          default: {
            color: new THREE.Color(0.15, 0.15, 0.18), // 更深灰色金属槽
            emissive: new THREE.Color(0.004, 0.004, 0.005), // 更微弱自发光
            specular: new THREE.Color(0.08, 0.08, 0.1), // 更低高光反射
            shininess: 15, // 更低光泽度，更明显磨损感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.15, 0.17, 0.18), // 工作时稍亮
            emissive: new THREE.Color(0.005, 0.01, 0.01),
            specular: new THREE.Color(0.08, 0.1, 0.1),
            shininess: 15,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          worn: {
            color: new THREE.Color(0.17, 0.17, 0.19), // 磨损状态，稍亮
            emissive: new THREE.Color(0.005, 0.005, 0.008),
            specular: new THREE.Color(0.1, 0.1, 0.12),
            shininess: 18, // 磨损处稍亮
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 导向条材质 - 深色金属条
        guideStrip: {
          default: {
            color: new THREE.Color(0.22, 0.25, 0.28), // 深色灰蓝色金属
            emissive: new THREE.Color(0.008, 0.01, 0.012), // 更微弱蓝色自发光
            specular: new THREE.Color(0.18, 0.2, 0.22), // 降低高光反射，金属条感
            shininess: 25, // 降低光泽度，亚光金属条
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.22, 0.28, 0.28), // 工作时带绿色调
            emissive: new THREE.Color(0.01, 0.02, 0.015),
            specular: new THREE.Color(0.18, 0.22, 0.2),
            shininess: 26,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.25, 0.3, 0.32), // 高亮状态，稍亮的蓝色
            emissive: new THREE.Color(0.015, 0.025, 0.03),
            specular: new THREE.Color(0.22, 0.25, 0.28),
            shininess: 30, // 稍高光泽
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          warn: {
            color: new THREE.Color(0.55, 0.45, 0.35), // 警告状态，橙黄色调
            emissive: new THREE.Color(0.06, 0.03, 0.015),
            specular: new THREE.Color(0.50, 0.40, 0.30),
            shininess: 50,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },

      // 地板组件材质 - 深蓝黑色调，分4层，支持多层建筑
      floor: {
        // 地板主体材质
        main: {
          // 默认状态 - 中等深度的蓝黑色，适用于通用地板
          default: {
            color: new THREE.Color(0.03, 0.05, 0.12), // 中等深度的蓝黑色
            emissive: new THREE.Color(0.005, 0.01, 0.03), // 微弱蓝色自发光
            specular: new THREE.Color(0.08, 0.12, 0.20), // 蓝色调高光反射
            shininess: 15, // 低光泽度，磨砂质感
            transparent: false, // 不透明
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第1层 - 深邃的蓝黑色，不透明（底层）
          floor1: {
            color: new THREE.Color(0.02, 0.04, 0.10), // 最深的蓝黑色
            emissive: new THREE.Color(0.005, 0.01, 0.03), // 微弱蓝色自发光
            specular: new THREE.Color(0.08, 0.12, 0.20), // 蓝色调高光反射
            shininess: 15, // 低光泽度，磨砂质感
            transparent: false, // 不透明
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第2层 - 稍亮的蓝黑色，半透明（第二层）
          floor2: {
            color: new THREE.Color(0.03, 0.05, 0.12), // 稍亮的蓝黑色
            emissive: new THREE.Color(0.005, 0.01, 0.03), // 微弱蓝色自发光
            specular: new THREE.Color(0.08, 0.12, 0.20), // 蓝色调高光反射
            shininess: 15, // 低光泽度，磨砂质感
            transparent: true, // 半透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第3层 - 更亮的蓝黑色，半透明（第三层）
          floor3: {
            color: new THREE.Color(0.04, 0.06, 0.14), // 更亮的蓝黑色
            emissive: new THREE.Color(0.005, 0.01, 0.03), // 微弱蓝色自发光
            specular: new THREE.Color(0.08, 0.12, 0.20), // 蓝色调高光反射
            shininess: 15, // 低光泽度，磨砂质感
            transparent: true, // 半透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第4层 - 最亮的蓝黑色，半透明（顶层）
          floor4: {
            color: new THREE.Color(0.05, 0.07, 0.16), // 最亮的蓝黑色
            emissive: new THREE.Color(0.005, 0.01, 0.03), // 微弱蓝色自发光
            specular: new THREE.Color(0.08, 0.12, 0.20), // 蓝色调高光反射
            shininess: 15, // 低光泽度，磨砂质感
            transparent: true, // 半透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 高亮状态 - 用于选中或特殊显示的地板
          highlight: {
            color: new THREE.Color(0.06, 0.10, 0.20), // 更亮的蓝黑色
            emissive: new THREE.Color(0.02, 0.04, 0.08), // 增强蓝色自发光
            specular: new THREE.Color(0.12, 0.18, 0.30), // 增强高光反射
            shininess: 25, // 稍高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          }
        }
      },

      // 货物/托盘组件材质
      pallet: {
        // 托盘主体材质
        main: {
          // 默认状态 - 深蓝色（加深颜色）
          default: {
            color: new THREE.Color(0.15, 0.25, 0.5), // 深蓝色，加深颜色
            emissive: new THREE.Color(0.02, 0.03, 0.08), // 蓝色调自发光，增强可见度
            specular: new THREE.Color(0.2, 0.3, 0.4), // 蓝色调高光反射
            shininess: 30, // 中等光泽度，塑料质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 高亮状态 - 稍亮的深蓝色
          highlight: {
            color: new THREE.Color(0.25, 0.35, 0.6), // 稍亮的深蓝色
            emissive: new THREE.Color(0.03, 0.05, 0.12), // 更强的蓝色自发光
            specular: new THREE.Color(0.3, 0.4, 0.5),
            shininess: 35,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 选中状态 - 带青色调的深蓝色
          selected: {
            color: new THREE.Color(0.20, 0.40, 0.55), // 带青色调的深蓝色
            emissive: new THREE.Color(0.03, 0.08, 0.12), // 青蓝色调自发光
            specular: new THREE.Color(0.3, 0.4, 0.45),
            shininess: 40,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 警告状态 - 带橙色调的深蓝色
          warning: {
            color: new THREE.Color(0.35, 0.30, 0.50), // 带橙色调的深蓝色（偏紫）
            emissive: new THREE.Color(0.10, 0.08, 0.06), // 橙色调自发光
            specular: new THREE.Color(0.3, 0.3, 0.35),
            shininess: 30,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 带红色调的深蓝色
          error: {
            color: new THREE.Color(0.50, 0.25, 0.35), // 带红色调的深蓝色（偏粉）
            emissive: new THREE.Color(0.15, 0.04, 0.06), // 红色调自发光
            specular: new THREE.Color(0.35, 0.25, 0.28),
            shininess: 30,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },

      // 四向车组件材质 - 橘黄色系，与蓝色系形成强烈对比但整体协调
      fourWayVehicle: {
        // 车体材质 - 深金色/深橙色，高光装饰，形成视觉焦点（原金属边框材质）
        body: {
          default: {
            color: new THREE.Color(0.55, 0.40, 0.20), // 深金色/深橙色金属，加深颜色
            emissive: new THREE.Color(0.25, 0.18, 0.08), // 金色自发光，强烈视觉冲击
            specular: new THREE.Color(0.7, 0.6, 0.4), // 强高光反射
            shininess: 85, // 极高光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.65, 0.50, 0.30), // 更亮的深金色
            emissive: new THREE.Color(0.35, 0.25, 0.12), // 更强的金色自发光
            specular: new THREE.Color(0.8, 0.7, 0.5),
            shininess: 95,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.40, 0.15, 0.10), // 错误状态深红色
            emissive: new THREE.Color(0.20, 0.06, 0.04), // 红色自发光
            specular: new THREE.Color(0.5, 0.25, 0.20),
            shininess: 55,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 上盖板材质 - 深橘黄色金属，形成层次感
        topCover: {
          default: {
            color: new THREE.Color(0.45, 0.28, 0.12), // 深橘黄色金属，加深颜色
            emissive: new THREE.Color(0.20, 0.12, 0.05), // 橘黄色自发光，科技感
            specular: new THREE.Color(0.6, 0.45, 0.3), // 橘黄色调高光反射
            shininess: 70, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.55, 0.38, 0.20), // 更亮的深橘黄色
            emissive: new THREE.Color(0.30, 0.18, 0.08), // 更强的橘黄色自发光
            specular: new THREE.Color(0.7, 0.55, 0.35),
            shininess: 80,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 轮子材质 - 深黑色，保持中性，不破坏整体配色
        wheel: {
          default: {
            color: new THREE.Color(0.05, 0.05, 0.08), // 深黑色，中性色
            emissive: new THREE.Color(0.01, 0.01, 0.01), // 极微弱自发光
            specular: new THREE.Color(0.1, 0.1, 0.12), // 低高光反射
            shininess: 10, // 低光泽度，橡胶质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.08, 0.08, 0.12), // 工作时稍亮
            emissive: new THREE.Color(0.02, 0.02, 0.02), // 微弱自发光
            specular: new THREE.Color(0.15, 0.15, 0.18),
            shininess: 15,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 金属边框材质 - 更深橘红色金属，大胆用色，与蓝色形成互补色对比（原车体材质）
        metalFrame: {
          default: {
            color: new THREE.Color(0.30, 0.15, 0.08), // 更深橘红色金属，加深颜色
            emissive: new THREE.Color(0.15, 0.06, 0.03), // 橘红色自发光，科技感
            specular: new THREE.Color(0.45, 0.3, 0.2), // 橘红色调高光反射
            shininess: 55, // 高光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.40, 0.25, 0.12), // 更亮的深橘黄色
            emissive: new THREE.Color(0.25, 0.12, 0.05), // 更强的橘黄色自发光
            specular: new THREE.Color(0.55, 0.4, 0.3),
            shininess: 65,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 报警灯材质 - 强烈红色发光，科技感
        alarmLight: {
          default: {
            color: new THREE.Color(1.0, 0.2, 0.2), // 鲜艳红色
            emissive: new THREE.Color(0.8, 0.15, 0.15), // 强烈红色自发光
            specular: new THREE.Color(0.9, 0.5, 0.5), // 红色高光
            shininess: 70, // 高光泽度，玻璃质感
            transparent: true, // 半透明
            opacity: 0.95,
            wireframe: false,
          },
          active: {
            color: new THREE.Color(1.0, 0.3, 0.3), // 更亮的红色
            emissive: new THREE.Color(1.0, 0.25, 0.25), // 极强红色自发光，闪烁效果
            specular: new THREE.Color(1.0, 0.6, 0.6),
            shininess: 80,
            transparent: true,
            opacity: 0.98,
            wireframe: false,
          },
          warning: {
            color: new THREE.Color(1.0, 0.6, 0.2), // 橙色警告
            emissive: new THREE.Color(0.8, 0.4, 0.1), // 橙色自发光
            specular: new THREE.Color(1.0, 0.7, 0.4),
            shininess: 70,
            transparent: true,
            opacity: 0.95,
            wireframe: false,
          }
        },

        // 运行指示灯材质 - 强烈绿色/青色发光，科技感
        runningIndicator: {
          default: {
            color: new THREE.Color(0.2, 1.0, 0.5), // 青绿色，科技感
            emissive: new THREE.Color(0.1, 0.8, 0.4), // 强烈青绿色自发光
            specular: new THREE.Color(0.4, 1.0, 0.6), // 青绿色高光
            shininess: 70, // 高光泽度，玻璃质感
            transparent: true, // 半透明
            opacity: 0.95,
            wireframe: false,
          },
          active: {
            color: new THREE.Color(0.3, 1.0, 0.6), // 更亮的青绿色
            emissive: new THREE.Color(0.2, 1.0, 0.5), // 极强青绿色自发光
            specular: new THREE.Color(0.5, 1.0, 0.7),
            shininess: 80,
            transparent: true,
            opacity: 0.98,
            wireframe: false,
          },
          off: {
            color: new THREE.Color(0.15, 0.15, 0.18), // 关闭状态，深蓝灰色
            emissive: new THREE.Color(0.0, 0.0, 0.0), // 无自发光
            specular: new THREE.Color(0.1, 0.1, 0.12),
            shininess: 10,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },

        // 顶升材质 - 橘黄色系金属，与车体保持一致
        lifting: {
          default: {
            color: new THREE.Color(0.40, 0.25, 0.15), // 深橘红色金属，与车体协调
            emissive: new THREE.Color(0.18, 0.10, 0.05), // 橘红色自发光
            specular: new THREE.Color(0.55, 0.4, 0.3), // 橘红色调高光
            shininess: 50, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.50, 0.35, 0.22), // 工作时更亮的橘黄色
            emissive: new THREE.Color(0.25, 0.18, 0.10), // 更强的橘黄色自发光
            specular: new THREE.Color(0.65, 0.5, 0.4),
            shininess: 60,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          lifting: {
            color: new THREE.Color(0.60, 0.45, 0.30), // 顶升时更亮的橘黄色
            emissive: new THREE.Color(0.35, 0.25, 0.15), // 更强的橘黄色自发光
            specular: new THREE.Color(0.75, 0.6, 0.5),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },

      // 结构支撑组件材质
      structure: {
        // 立柱材质 - 深蓝灰色金属，增强对比度，清晰显示位置，添加透明度
        column: {
          default: {
            color: new THREE.Color(0.12, 0.14, 0.18), // 更深的蓝灰色，增强对比度
            emissive: new THREE.Color(0.02, 0.03, 0.05), // 增强蓝色自发光，让立柱更明显
            specular: new THREE.Color(0.25, 0.28, 0.35), // 增强高光反射，突出金属质感
            shininess: 35, // 提高光泽度，让立柱更清晰可见
            transparent: true, // 添加透明度
            opacity: 0.5, // 50%不透明度，半透明效果
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.18, 0.20, 0.25), // 高亮时更亮的蓝灰色
            emissive: new THREE.Color(0.04, 0.05, 0.08), // 更强的蓝色自发光
            specular: new THREE.Color(0.35, 0.38, 0.45),
            shininess: 45,
            transparent: true, // 添加透明度
            opacity: 0.6, // 高亮时稍高不透明度
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.15, 0.20, 0.18), // 工作时带青绿色调
            emissive: new THREE.Color(0.03, 0.05, 0.04), // 青绿色自发光
            specular: new THREE.Color(0.30, 0.35, 0.32),
            shininess: 40,
            transparent: true, // 添加透明度
            opacity: 0.55, // 工作时稍高不透明度
            wireframe: false,
          }
        },

        // 横梁材质 - 亮蓝灰色金属，与立柱形成明显对比，清晰显示位置，添加透明度
        beam: {
          default: {
            color: new THREE.Color(0.35, 0.38, 0.42), // 明显更亮的蓝灰色，与立柱形成强烈对比
            emissive: new THREE.Color(0.05, 0.08, 0.12), // 增强蓝色自发光，让横梁更明显
            specular: new THREE.Color(0.45, 0.48, 0.55), // 增强高光反射，突出金属质感
            shininess: 50, // 提高光泽度，让横梁更清晰可见
            transparent: true, // 添加透明度
            opacity: 0.6, // 60%不透明度，保持可见但不过于遮挡
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.42, 0.45, 0.50), // 高亮时更亮的蓝灰色
            emissive: new THREE.Color(0.08, 0.12, 0.18), // 更强的蓝色自发光
            specular: new THREE.Color(0.55, 0.58, 0.65),
            shininess: 60,
            transparent: true, // 添加透明度
            opacity: 0.7, // 高亮时稍高不透明度
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.38, 0.42, 0.40), // 工作时带青绿色调
            emissive: new THREE.Color(0.06, 0.10, 0.08), // 青绿色自发光
            specular: new THREE.Color(0.50, 0.55, 0.52),
            shininess: 55,
            transparent: true, // 添加透明度
            opacity: 0.65, // 工作时稍高不透明度
            wireframe: false,
          }
        },

        // 轨道材质 - 深灰色金属，用于堆垛机或其他设备的轨道
        rail: {
          default: {
            color: new THREE.Color(0.20, 0.20, 0.22), // 深灰色金属轨道
            emissive: new THREE.Color(0.006, 0.006, 0.006), // 微弱自发光
            specular: new THREE.Color(0.15, 0.15, 0.15), // 中等高光反射，轨道需要一定反光
            shininess: 25, // 中等光泽度，轨道表面相对光滑
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          highlight: {
            color: new THREE.Color(0.28, 0.28, 0.30), // 高亮时更亮
            emissive: new THREE.Color(0.02, 0.02, 0.02),
            specular: new THREE.Color(0.25, 0.25, 0.25),
            shininess: 35,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.22, 0.24, 0.22), // 工作时带绿色调
            emissive: new THREE.Color(0.01, 0.02, 0.01),
            specular: new THREE.Color(0.18, 0.18, 0.18),
            shininess: 28,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          warn: {
            color: new THREE.Color(0.35, 0.28, 0.20), // 警告时橙黄色调
            emissive: new THREE.Color(0.04, 0.02, 0.01),
            specular: new THREE.Color(0.3, 0.25, 0.2),
            shininess: 30,
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
   * @param {string} componentType - 组件类型 (如: 'stacker', 'rgv')
   * @param {string} partType - 部件类型 (如: 'platform', 'upperFork', 'lowerFork')
   * @param {string} state - 状态 (如: 'default', 'working', 'error', 'idle')
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
    return new THREE.MeshPhongMaterial({
      color: config.color.clone(),
      emissive: config.emissive.clone(),
      specular: config.specular.clone(),
      shininess: config.shininess,
      transparent: config.transparent,
      opacity: config.opacity,
      wireframe: config.wireframe,
    });
  }

  /**
   * 批量创建材质
   * @param {Array} configs - 配置数组 [{componentType, partType, state}]
   * @returns {Array} 材质对象数组
   */
  createMaterials(configs) {
    return configs.map(config =>
      this.createMaterial(config.componentType, config.partType, config.state || 'default')
    );
  }

  /**
   * 更新材质配置
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @param {string} state - 状态
   * @param {Object} config - 新的材质配置
   */
  updateMaterialConfig(componentType, partType, state, config) {
    if (!this.materials[componentType]) {
      this.materials[componentType] = {};
    }
    if (!this.materials[componentType][partType]) {
      this.materials[componentType][partType] = {};
    }
    this.materials[componentType][partType][state] = config;
    console.log(`已更新材质配置: ${componentType}.${partType}.${state}`);
  }

  /**
   * 添加新的组件类型材质配置
   * @param {string} componentType - 组件类型
   * @param {Object} config - 材质配置对象
   */
  addComponentMaterials(componentType, config) {
    this.materials[componentType] = config;
    console.log(`已添加新组件材质配置: ${componentType}`);
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
    }
    // 如果对象包含子对象，遍历子对象并替换材质
    else {
      object.traverse((child) => {
        if (child.isMesh) {
          child.material = material.clone(); // 使用克隆避免共享材质
        }
      });
    }
  }

  /**
   * 获取所有可用的组件类型
   * @returns {Array} 组件类型数组
   */
  getComponentTypes() {
    return Object.keys(this.materials);
  }

  /**
   * 获取指定组件的所有部件类型
   * @param {string} componentType - 组件类型
   * @returns {Array} 部件类型数组
   */
  getPartTypes(componentType) {
    return Object.keys(this.materials[componentType] || {});
  }

  /**
   * 获取指定部件的所有状态
   * @param {string} componentType - 组件类型
   * @param {string} partType - 部件类型
   * @returns {Array} 状态数组
   */
  getStates(componentType, partType) {
    return Object.keys(this.materials[componentType]?.[partType] || {});
  }
}

// 创建单例实例
const materialManager = new MaterialManager();

// 导出单例实例和类
export { materialManager, MaterialManager };
export default materialManager;

