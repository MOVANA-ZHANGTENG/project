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
      // 堆垛机组件材质
      stacker: {
        // 躯干材质 - 突出显示的金属框架
        body: {
          default: {
            color: new THREE.Color(0.45, 0.45, 0.5), // 更亮的银灰色金属色
            emissive: new THREE.Color(0.02, 0.02, 0.025), // 增强自发光
            specular: new THREE.Color(0.3, 0.3, 0.3), // 增加高光反射
            shininess: 40, // 增加光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.5, 0.55, 0.5), // 工作状态更亮
            emissive: new THREE.Color(0.05, 0.08, 0.05), // 绿色调自发光
            specular: new THREE.Color(0.4, 0.4, 0.4),
            shininess: 50,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.55, 0.35, 0.35), // 错误状态红色调
            emissive: new THREE.Color(0.08, 0.02, 0.02), // 红色自发光
            specular: new THREE.Color(0.4, 0.3, 0.3),
            shininess: 45,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 载货台材质
        platform: {
          // 默认状态 - 蓝色金属
          default: {
            color: new THREE.Color(0.2, 0.4, 0.7), // 柔和的蓝色金属色
            emissive: new THREE.Color(0.05, 0.1, 0.2), // 柔和的自发光效果
            specular: new THREE.Color(0.5, 0.5, 0.5), // 中等高光反射
            shininess: 60, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 绿色
          working: {
            color: new THREE.Color(0.2, 0.7, 0.3),
            emissive: new THREE.Color(0.05, 0.2, 0.1),
            specular: new THREE.Color(0.5, 0.5, 0.5),
            shininess: 60,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色
          error: {
            color: new THREE.Color(0.8, 0.2, 0.2),
            emissive: new THREE.Color(0.3, 0.05, 0.05),
            specular: new THREE.Color(0.5, 0.5, 0.5),
            shininess: 60,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 空闲状态 - 灰色
          idle: {
            color: new THREE.Color(0.4, 0.4, 0.4),
            emissive: new THREE.Color(0.05, 0.05, 0.05),
            specular: new THREE.Color(0.3, 0.3, 0.3),
            shininess: 40,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 上货叉材质
        upperFork: {
          // 默认状态 - 亮蓝色
          default: {
            color: new THREE.Color(0.2, 0.5, 0.8), // 柔和的蓝色金属色
            emissive: new THREE.Color(0.08, 0.15, 0.3), // 柔和的自发光效果
            specular: new THREE.Color(0.6, 0.6, 0.6), // 中等高光反射
            shininess: 70, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 绿色
          working: {
            color: new THREE.Color(0.2, 0.8, 0.4),
            emissive: new THREE.Color(0.1, 0.3, 0.15),
            specular: new THREE.Color(0.6, 0.6, 0.6),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色
          error: {
            color: new THREE.Color(0.9, 0.2, 0.2),
            emissive: new THREE.Color(0.35, 0.08, 0.08),
            specular: new THREE.Color(0.6, 0.6, 0.6),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 下货叉材质
        lowerFork: {
          // 默认状态 - 更亮的蓝色
          default: {
            color: new THREE.Color(0.15, 0.6, 0.9), // 柔和的蓝色金属色
            emissive: new THREE.Color(0.1, 0.2, 0.4), // 柔和的自发光效果
            specular: new THREE.Color(0.7, 0.7, 0.7), // 中等高光反射
            shininess: 80, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 工作状态 - 绿色
          working: {
            color: new THREE.Color(0.15, 0.9, 0.5),
            emissive: new THREE.Color(0.12, 0.35, 0.2),
            specular: new THREE.Color(0.7, 0.7, 0.7),
            shininess: 80,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          // 错误状态 - 红色
          error: {
            color: new THREE.Color(0.95, 0.15, 0.15),
            emissive: new THREE.Color(0.4, 0.1, 0.1),
            specular: new THREE.Color(0.7, 0.7, 0.7),
            shininess: 80,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        }
      },
      
      // RGV组件材质
      rgv: {
        // 车体材质 - 突出显示的金属车体
        body: {
          default: {
            color: new THREE.Color(0.5, 0.5, 0.55), // 更亮的银灰色金属色
            emissive: new THREE.Color(0.03, 0.03, 0.04), // 增强自发光
            specular: new THREE.Color(0.4, 0.4, 0.4), // 增强高光反射
            shininess: 60, // 高光泽度，突出金属质感
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.55, 0.6, 0.55), // 工作状态更亮
            emissive: new THREE.Color(0.08, 0.12, 0.08), // 绿色调自发光
            specular: new THREE.Color(0.5, 0.5, 0.5),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(0.6, 0.3, 0.3), // 错误状态红色调
            emissive: new THREE.Color(0.2, 0.05, 0.05), // 红色自发光
            specular: new THREE.Color(0.5, 0.4, 0.4),
            shininess: 65,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 载货台材质 - 橘黄色
        platform: {
          default: {
            color: new THREE.Color(1.0, 0.50, 0.10), // 鲜艳的橘黄色
            emissive: new THREE.Color(0.35, 0.18, 0.02), // 橘黄色自发光
            specular: new THREE.Color(0.8, 0.6, 0.3), // 金属光泽，带橘黄色调
            shininess: 70, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(1.0, 0.55, 0.15),
            emissive: new THREE.Color(0.4, 0.22, 0.05),
            specular: new THREE.Color(0.8, 0.6, 0.3),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          error: {
            color: new THREE.Color(1.0, 0.2, 0.1),
            emissive: new THREE.Color(0.4, 0.05, 0.02),
            specular: new THREE.Color(0.8, 0.4, 0.2),
            shininess: 70,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 链条材质 - 灰色金属
        chain: {
          default: {
            color: new THREE.Color(0.4, 0.4, 0.45), // 灰色金属色
            emissive: new THREE.Color(0.02, 0.02, 0.02), // 微弱自发光
            specular: new THREE.Color(0.3, 0.3, 0.3), // 中等高光反射
            shininess: 40, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 装饰条材质 - 蓝色高亮
        decoration: {
          default: {
            color: new THREE.Color(0.1, 0.5, 0.9), // 蓝色高亮
            emissive: new THREE.Color(0.03, 0.1, 0.2), // 蓝色自发光
            specular: new THREE.Color(0.6, 0.6, 0.6), // 较强高光反射
            shininess: 80, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          },
          working: {
            color: new THREE.Color(0.1, 0.6, 1.0),
            emissive: new THREE.Color(0.05, 0.15, 0.3),
            specular: new THREE.Color(0.7, 0.7, 0.7),
            shininess: 85,
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          }
        },
        
        // 轨道材质 - 深灰色金属
        track: {
          default: {
            color: new THREE.Color(0.25, 0.25, 0.3), // 深灰色金属色
            emissive: new THREE.Color(0.01, 0.01, 0.01), // 微弱自发光
            specular: new THREE.Color(0.15, 0.15, 0.15), // 低高光反射
            shininess: 20, // 低光泽度
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
      
      // 地板组件材质 - 深蓝黑色调，分4层
      floor: {
        // 地板主体材质
        main: {
          // 第1层 - 深邃的蓝黑色，不透明
          floor1: {
            color: new THREE.Color(0.02, 0.04, 0.10),
            emissive: new THREE.Color(0.005, 0.01, 0.03), // 微弱蓝色自发光
            specular: new THREE.Color(0.08, 0.12, 0.20), // 蓝色调高光反射
            shininess: 15, // 低光泽度，磨砂质感
            transparent: false, // 不透明
            opacity: 1.0,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第2层 - 稍亮的蓝黑色，透明
          floor2: {
            color: new THREE.Color(0.03, 0.05, 0.12),
            emissive: new THREE.Color(0.005, 0.01, 0.03),
            specular: new THREE.Color(0.08, 0.12, 0.20),
            shininess: 15,
            transparent: true, // 透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第3层 - 更亮的蓝黑色，透明
          floor3: {
            color: new THREE.Color(0.04, 0.06, 0.14),
            emissive: new THREE.Color(0.005, 0.01, 0.03),
            specular: new THREE.Color(0.08, 0.12, 0.20),
            shininess: 15,
            transparent: true, // 透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
          },
          // 第4层 - 最亮的蓝黑色，透明
          floor4: {
            color: new THREE.Color(0.05, 0.07, 0.16),
            emissive: new THREE.Color(0.005, 0.01, 0.03),
            specular: new THREE.Color(0.08, 0.12, 0.20),
            shininess: 15,
            transparent: true, // 透明
            opacity: 0.75,
            wireframe: false,
            flatShading: false,
            side: THREE.DoubleSide,
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

