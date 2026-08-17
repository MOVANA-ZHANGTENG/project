<template>
  <div  class="container3d">
   
    <div v-if="jiinduDisable" class="progress-box">
      <div class="name">
        模型加载中{{ jindu }}
      </div>
     
      <div class="progress-bar">
        <div :style="'width:'+jindu" class="progress"></div>
     </div>
     </div>
    
    <div v-if="isTest">
      <!-- <div class="control-panel panel-top">
        <input class="custom-input" v-model="cellCode" placeholder="货位编码" />
        <button class="custom-btn primary" @click="scTask(1, '123', cellCode)">堆垛机取货</button>
        <button class="custom-btn primary" @click="scTask(-1, '123', cellCode)">堆垛机放货</button>
        <button class="custom-btn primary" @click="scTaskRun('1-1-1', '1-14-5')">堆垛机1-1到14-5</button>
      </div>

      <div class="control-panel panel-middle">
        <input class="custom-input" v-model="fromCellCode" placeholder="起始位置" />
        <input class="custom-input" v-model="toCellCode" placeholder="目标位置" />
        <button class="custom-btn secondary" @click="ssx(fromCellCode, toCellCode, '123')">输送线移动</button>
        <button class="custom-btn secondary" @click="aaaaa()">拍照闪光</button>
      </div>

      <div class="control-panel panel-bottom">
        <button class="custom-btn success" @click="agvTaskRun(0)">agvTaskRun0</button>
        <button class="custom-btn success" @click="agvTaskRun(1)">agvTaskRun1</button>
        <button class="custom-btn success" @click="agvTaskRun(2)">agvTaskRun2</button>
        <button class="custom-btn success" @click="agvTaskRun(3)">agvTaskRun3</button>
        <button class="custom-btn warning" @click="shangxialiao(4)">AGV上料</button>
        <button class="custom-btn danger" @click="shangxialiao(5)">AGV下料</button>
      </div> -->

      <!-- 调试视角控制面板 -->
      <div class="control-panel panel-debug">
        <h3>调试视角控制</h3>
        <div class="debug-controls">
          <button class="custom-btn primary" @click="setCameraToFloor(1)">观测点1</button>
          <button class="custom-btn primary" @click="setCameraToFloor(2)">观测点2</button>
          <button class="custom-btn primary" @click="setCameraToFloor(3)">观测点3</button>
          <button class="custom-btn primary" @click="setCameraToFloor(4)">观测点4</button>
          <button class="custom-btn secondary" @click="resetCamera">重置视角</button>
        </div>
      </div>
      
      <!-- 堆垛机信息面板 -->
      <div v-if="stackerInfoVisible" class="stacker-info-panel panel-top">
        <h3>堆垛机信息</h3>
        <div class="info-content">
          <p>名称: {{ stackerInfo.name }}</p>
          <p>状态: {{ stackerInfo.status }}</p>
          <p>位置: {{ stackerInfo.position }}</p>
          <button class="custom-btn primary" @click="closeStackerInfo">关闭</button>
        </div>
      </div>
    </div>
   

    <div refs="3d" class="three-canvas" id="3d" ref="threeTarget">

      
      
    
    </div>
  </div>
</template>

<script>
// 引入three.js
import * as THREE from "three";
import * as echarts from "echarts";
import request from "@/utils/request";
import { ThreeEngine } from "./js/TEngine.js";
import { allBaseObject } from "./js/TBaseObject";
import { allLights } from "./js/TallLights";
import { allHelper } from "./js/TGridHelper.js";
import { AmbientLight, PointLight, SpotLight } from "three";
import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader.js";
import TWEEN from "@tweenjs/tween.js";
import { createSpriteTextLabel } from './js/Sprite.js';
import { DRACOLoader } from "three/examples/jsm/loaders/DRACOLoader.js";
import {
  CSS3DRenderer,
  CSS3DSprite,
} from "three/examples/jsm/renderers/CSS3DRenderer.js";
export default {
  data() {
    return {
      isTest:true,
      jindu:"0%",
      jiinduDisable:true,
     
      ThreeEngine: null,
      cellCode: "1-1-1",
      fromCellCode: "15",
      toCellCode: "16",

      url:
        "ws://localhost:8007/" +
        process.env.VUE_APP_ROUTER +
        "/websocket/message",
      message: "",
      text_content: "",
      ws: null,

      texture: { offset: { x: 0 } },
      lift1texture: { offset: { x: 0 } },

      agv_fx: 1,

      agvtime: 0,
      agvRouter: [],

      labelRender: null,

      agv_sudu: 500,
      agvTask: {
        agv_fx: 1,
        isStart: 2,
      },

      requestId: null,
      
      // 堆垛机信息面板相关数据
      stackerInfoVisible: false,
      stackerInfo: {
        name: '',
        status: '',
        position: ''
      }
    };
  },
  watch: {},
  created() { },
  mounted() {
    this.open();
  },

  beforeDestroy() {
    this.exit();
    this.cancelAnimation();
  },
  methods: {
    cancelAnimation() {
      if (this.requestId!=null) {
        cancelAnimationFrame(this.requestId);
        this.requestId = null;
      }
    },
    open() {
      var that = this;
      // 初始化three.js
      that.init();
      //添加模型点击事件
      this.initObjClickHandler();
      // this.$confirm("该界面需要加载3维模型数据， 是否加载?", "提示", {
      //   confirmButtonText: "是",
      //   cancelButtonText: "否",
      //   type: "warning",
      // })
      //   .then(() => {
      //     that.init();
      //   })
      //   .catch(() => {});
    },
   
    // 关闭堆垛机信息面板
    closeStackerInfo() {
      this.stackerInfoVisible = false;
    },
    
    // 在模型加载完成后调用
    addStorageMachineLabels() {
     
        this.createSprite("堆垛机躯干", "堆垛机躯干",16);
        
      
    },

    // 为四层地板应用自定义材质
    applyCustomFloorMaterials() {
      // 定义四层地板的名称
      const floorNames = ['floor-1', 'floor-2', 'floor-3', 'floor-4'];
      
      // 为每层地板定义不同的颜色，创建层次感
      const floorColors = [
        new THREE.Color(0.30, 0.30, 0.30),  // 第1层 - 基础深灰色
        new THREE.Color(0.25, 0.30, 0.45),  // 第2层 - 蓝灰色
        new THREE.Color(0.30, 0.45, 0.25),  // 第3层 - 绿灰色
        new THREE.Color(0.45, 0.25, 0.30)   // 第4层 - 红灰色
      ];
      
      // 首先遍历场景中的所有对象，查看实际的对象名称
      console.log('场景中的所有对象:');
      const allObjectNames = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name) {
          allObjectNames.push(obj.name);
        }
      });
      console.log('所有对象名称:', allObjectNames);
      
      // 检查是否包含任何包含"floor"的名称
      const floorObjects = allObjectNames.filter(name => name.toLowerCase().includes('floor'));
      console.log('包含"floor"的对象:', floorObjects);
      
      // 为每层地板创建带有颜色层次的材质（第1层不透明，第2、3、4层透明）
      floorNames.forEach((floorName, index) => {
        const floor = this.ThreeEngine.getObjectByName(floorName);
        if (floor) {
          // 第1层地板不透明，第2、3、4层地板透明
          const isTransparent = index > 0; // floor-1不透明，floor-2、floor-3、floor-4透明
          
          // 创建带有颜色层次的地板材质
          const customMaterial = new THREE.MeshPhongMaterial({
            color: floorColors[index], // 使用预定义的颜色
            emissive: new THREE.Color(0.03, 0.03, 0.03), // 微弱自发光
            specular: new THREE.Color(0.15, 0.15, 0.15), // 适中高光反射
            shininess: 20, // 适中的光泽度
            transparent: isTransparent, // 第2、3、4层透明
            opacity: isTransparent ? 0.7 : 1.0, // 透明层使用0.7透明度，不透明层使用1.0
            wireframe: false,
            flatShading: false // 使用平滑着色
          });
          
          // 记录找到的对象类型
          console.log(`找到对象 ${floorName}:`, floor);
          
          // 如果地板对象是网格对象，直接替换材质
          if (floor.isMesh) {
            console.log(`直接替换 ${floorName} 的材质`);
            floor.material = customMaterial;
          } 
          // 如果地板对象包含子对象，遍历子对象并替换材质
          else {
            console.log(`遍历 ${floorName} 的子对象并替换材质`);
            let meshCount = 0;
            floor.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
                meshCount++;
              }
            });
            console.log(`为 ${floorName} 的 ${meshCount} 个网格对象替换了材质`);
          }
          
          console.log(`已为 ${floorName} 应用${isTransparent ? '透明' : '不透明'}彩色地板材质`);
        } else {
          console.warn(`未找到名为 ${floorName} 的地板对象`);
        }
      });
      
      // 如果没有找到指定名称的地板，尝试查找其他可能的地板名称（第1层不透明，其他层透明）
      if (floorObjects.length > 0) {
        console.log('尝试使用找到的其他地板对象名称:', floorObjects);
        floorObjects.forEach((floorName, index) => {
          const floor = this.ThreeEngine.getObjectByName(floorName);
          if (floor) {
            // 根据楼层索引确定是否透明（假设floorObjects数组中floor-1在前面）
            const floorIndex = floorNames.indexOf(floorName);
            const isTransparent = floorIndex > 0; // 第1层不透明，其他层透明
            
            // 确保floorIndex在有效范围内
            const colorIndex = Math.min(floorIndex, floorColors.length - 1);
            
            // 创建带有颜色层次的地板材质
            const customMaterial = new THREE.MeshPhongMaterial({
              color: floorColors[colorIndex], // 使用预定义的颜色
              emissive: new THREE.Color(0.03, 0.03, 0.03), // 微弱自发光
              specular: new THREE.Color(0.15, 0.15, 0.15), // 适中高光反射
              shininess: 20, // 适中的光泽度
              transparent: isTransparent, // 第2、3、4层透明
              opacity: isTransparent ? 0.7 : 1.0, // 透明层使用0.7透明度，不透明层使用1.0
              wireframe: false,
              flatShading: false // 使用平滑着色
            });
            
            // 如果地板对象是网格对象，直接替换材质
            if (floor.isMesh) {
              console.log(`直接替换 ${floorName} 的材质`);
              floor.material = customMaterial;
            } 
            // 如果地板对象包含子对象，遍历子对象并替换材质
            else {
              console.log(`遍历 ${floorName} 的子对象并替换材质`);
              let meshCount = 0;
              floor.traverse((child) => {
                if (child.isMesh) {
                  child.material = customMaterial;
                  meshCount++;
                }
              });
              console.log(`为 ${floorName} 的 ${meshCount} 个网格对象替换了材质`);
            }
            
            console.log(`已为 ${floorName} 应用${isTransparent ? '透明' : '不透明'}彩色地板材质`);
          }
        });
      }
    },

    // 为堆垛机躯干应用自定义材质
    applyStackerBodyMaterials() {
      // 定义堆垛机躯干的名称模式
      const stackerBodyNames = ['躯干1', '躯干2', '躯干3', '躯干4'];
      
      // 为每个堆垛机躯干创建磨砂喷漆材质，几乎不反光
      stackerBodyNames.forEach((bodyName, index) => {
        const stackerBody = this.ThreeEngine.getObjectByName(bodyName);
        if (stackerBody) {
          // 创建磨砂喷漆材质，几乎不反光
          const customMaterial = new THREE.MeshPhongMaterial({
            color: new THREE.Color(0.3, 0.3, 0.35), // 深灰色磨砂金属色
            emissive: new THREE.Color(0.005, 0.005, 0.005), // 极微弱自发光
            specular: new THREE.Color(0, 0, 0), // 无高光反射
            shininess: 0, // 无光泽
            transparent: false, // 不透明
            opacity: 1.0, // 完全不透明
            wireframe: false,
          });
          
          // 如果堆垛机躯干对象是网格对象，直接替换材质
          if (stackerBody.isMesh) {
            stackerBody.material = customMaterial;
          } 
          // 如果堆垛机躯干对象包含子对象，遍历子对象并替换材质
          else {
            stackerBody.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
              }
            });
          }
          
          console.log(`已为 ${bodyName} 应用几乎不反光的磨砂喷漆材质`);
        } else {
          console.warn(`未找到名为 ${bodyName} 的堆垛机躯干对象`);
        }
      });
    },

    // 为货架应用自定义材质
    applyShelfMaterials() {
      // 定义货架的名称模式
      const shelfNames = ['货架1', '货架2', '货架3', '货架4','货架5', '货架6', '货架7', '货架8', '货架9', '货架10'];
      
      // 为每个货架创建更暗的灰白色哑光材质
      shelfNames.forEach((shelfName, index) => {
        const shelf = this.ThreeEngine.getObjectByName(shelfName);
        if (shelf) {
          // 创建更暗的灰白色哑光材质，几乎不反光
          const customMaterial = new THREE.MeshPhongMaterial({
            color: new THREE.Color(0.6, 0.6, 0.6), // 更暗的灰白色 (从0.85降低到0.4)
            emissive: new THREE.Color(0.02, 0.02, 0.02), // 微弱自发光
            specular: new THREE.Color(0.1, 0.1, 0.1), // 极低高光反射
            shininess: 5, // 极低光泽度
            transparent: false, // 不透明
            opacity: 1.0, // 完全不透明
            wireframe: false
          });
          
          // 如果货架对象是网格对象，直接替换材质
          if (shelf.isMesh) {
            shelf.material = customMaterial;
          } 
          // 如果货架对象包含子对象，遍历子对象并替换材质
          else {
            shelf.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
              }
            });
          }
          
          console.log(`已为 ${shelfName} 应用更暗的灰白色哑光材质`);
        } else {
          console.warn(`未找到名为 ${shelfName} 的货架对象`);
        }
      });
    },

    // 为载货台应用自定义材质
    applyPlatformMaterials() {
      // 查找所有载货台组件（带索引）
      const platforms = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('载货台')) {
          platforms.push(obj);
        }
      });
      
      platforms.forEach((platform) => {
        // 创建载货台的突出显示金属材质
        const customMaterial = new THREE.MeshPhongMaterial({
          color: new THREE.Color(0.2, 0.4, 0.7), // 更柔和的蓝色金属色
          emissive: new THREE.Color(0.05, 0.1, 0.2), // 更柔和的自发光效果
          specular: new THREE.Color(0.5, 0.5, 0.5), // 中等高光反射
          shininess: 60, // 中等光泽度
          transparent: false,
          opacity: 1.0,
          wireframe: false,
        });
        
        // 如果组件对象是网格对象，直接替换材质
        if (platform.isMesh) {
          platform.material = customMaterial;
          console.log(`已为 ${platform.name} 应用突出显示材质`);
        } 
        // 如果组件对象包含子对象，遍历子对象并替换材质
        else {
          platform.traverse((child) => {
            if (child.isMesh) {
              child.material = customMaterial;
            }
          });
          console.log(`已为 ${platform.name} 及其子对象应用突出显示材质`);
        }
      });
      
      if (platforms.length === 0) {
        console.warn('未找到载货台组件');
      }
    },

    // 为上货叉应用自定义材质
    applyUpperForkMaterials() {
      // 查找所有上货叉组件（带索引）
      const upperForks = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('上货叉')) {
          upperForks.push(obj);
        }
      });
      
      upperForks.forEach((fork) => {
        // 创建上货叉的突出显示金属材质
        const customMaterial = new THREE.MeshPhongMaterial({
          color: new THREE.Color(0.2, 0.5, 0.8), // 更柔和的蓝色金属色
          emissive: new THREE.Color(0.08, 0.15, 0.3), // 更柔和的自发光效果
          specular: new THREE.Color(0.6, 0.6, 0.6), // 中等高光反射
          shininess: 70, // 中等光泽度
          transparent: false,
          opacity: 1.0,
          wireframe: false,
        });
        
        // 如果组件对象是网格对象，直接替换材质
        if (fork.isMesh) {
          fork.material = customMaterial;
          console.log(`已为 ${fork.name} 应用突出显示材质`);
        } 
        // 如果组件对象包含子对象，遍历子对象并替换材质
        else {
          fork.traverse((child) => {
            if (child.isMesh) {
              child.material = customMaterial;
            }
          });
          console.log(`已为 ${fork.name} 及其子对象应用突出显示材质`);
        }
      });
      
      if (upperForks.length === 0) {
        console.warn('未找到上货叉组件');
      }
    },

    // 为下货叉应用自定义材质
    applyLowerForkMaterials() {
      // 查找所有下货叉组件（带索引）
      const lowerForks = [];
      this.ThreeEngine.scene.traverse((obj) => {
        if (obj.name && obj.name.includes('下货叉')) {
          lowerForks.push(obj);
        }
      });
      
      lowerForks.forEach((fork) => {
        // 创建下货叉的突出显示金属材质
        const customMaterial = new THREE.MeshPhongMaterial({
          color: new THREE.Color(0.15, 0.6, 0.9), // 更柔和的蓝色金属色
          emissive: new THREE.Color(0.1, 0.2, 0.4), // 更柔和的自发光效果
          specular: new THREE.Color(0.7, 0.7, 0.7), // 中等高光反射
          shininess: 80, // 中等光泽度
          transparent: false,
          opacity: 1.0,
          wireframe: false,
        });
        
        // 如果组件对象是网格对象，直接替换材质
        if (fork.isMesh) {
          fork.material = customMaterial;
          console.log(`已为 ${fork.name} 应用突出显示材质`);
        } 
        // 如果组件对象包含子对象，遍历子对象并替换材质
        else {
          fork.traverse((child) => {
            if (child.isMesh) {
              child.material = customMaterial;
            }
          });
          console.log(`已为 ${fork.name} 及其子对象应用突出显示材质`);
        }
      });
      
      if (lowerForks.length === 0) {
        console.warn('未找到下货叉组件');
      }
    },

    applyRgvMaterials() { 
       const rgvBodya = []; // 存储所有rgv车体组件
        const rgvZht = []; // 存储所有rgv货斗组件
        const rgvZhtLt = []; // 存储所有rgv链条组件
        const rgvZst = []; // 存储所有rgv装饰条组件
        const rgvGd = []; // 存储所有rgv轨道组件
        this.ThreeEngine.scene.traverse((obj) => {
          if (obj.name && obj.name.includes('rgv车体-')) {
            rgvBodya.push(obj);
          };
          if (obj.name && obj.name.includes('rgv载货台-')) {
            rgvZht.push(obj);
          };
           if (obj.name && obj.name.includes('rgv载货台链条-')) {
            rgvZhtLt.push(obj);
          };
           if (obj.name && obj.name.includes('rgv轨道-')) {
            rgvGd.push(obj);
          };
           if (obj.name && obj.name.includes('rgv装饰条-')) {
            rgvZst.push(obj);
          };
        });

        // 为RGV车体应用材质
        rgvBodya.forEach((obj) => {
          // 创建RGV车体的金属材质
          const customMaterial = new THREE.MeshPhongMaterial({
            color: new THREE.Color(0.3, 0.3, 0.35), // 深灰色金属色
            emissive: new THREE.Color(0.01, 0.01, 0.01), // 微弱自发光
            specular: new THREE.Color(0.2, 0.2, 0.2), // 中等高光反射
            shininess: 30, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          });
          
          // 如果组件对象是网格对象，直接替换材质
          if (obj.isMesh) {
            obj.material = customMaterial;
            console.log(`已为 ${obj.name} 应用RGV车体金属材质`);
          } 
          // 如果组件对象包含子对象，遍历子对象并替换材质
          else {
            obj.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
              }
            });
            console.log(`已为 ${obj.name} 及其子对象应用RGV车体金属材质`);
          }
        });
        
        // 为RGV载货台应用材质
        rgvZht.forEach((obj) => {
          // 创建RGV载货台的突出显示材质
          const customMaterial = new THREE.MeshPhongMaterial({
            color: new THREE.Color(0.2, 0.4, 0.7), // 蓝色金属色
            emissive: new THREE.Color(0.05, 0.1, 0.2), // 蓝色自发光
            specular: new THREE.Color(0.5, 0.5, 0.5), // 中等高光反射
            shininess: 60, // 较高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          });
          
          // 如果组件对象是网格对象，直接替换材质
          if (obj.isMesh) {
            obj.material = customMaterial;
            console.log(`已为 ${obj.name} 应用RGV载货台突出显示材质`);
          } 
          // 如果组件对象包含子对象，遍历子对象并替换材质
          else {
            obj.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
              }
            });
            console.log(`已为 ${obj.name} 及其子对象应用RGV载货台突出显示材质`);
          }
        });
        
        // 为RGV链条应用材质
        rgvZhtLt.forEach((obj) => {
          // 创建RGV链条的金属材质
          const customMaterial = new THREE.MeshPhongMaterial({
            color: new THREE.Color(0.4, 0.4, 0.45), // 灰色金属色
            emissive: new THREE.Color(0.02, 0.02, 0.02), // 微弱自发光
            specular: new THREE.Color(0.3, 0.3, 0.3), // 中等高光反射
            shininess: 40, // 中等光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          });
          
          // 如果组件对象是网格对象，直接替换材质
          if (obj.isMesh) {
            obj.material = customMaterial;
            console.log(`已为 ${obj.name} 应用RGV链条金属材质`);
          } 
          // 如果组件对象包含子对象，遍历子对象并替换材质
          else {
            obj.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
              }
            });
            console.log(`已为 ${obj.name} 及其子对象应用RGV链条金属材质`);
          }
        });
        
        // 为RGV装饰条应用材质
        rgvZst.forEach((obj) => {
          // 创建RGV装饰条的高亮材质
          const customMaterial = new THREE.MeshPhongMaterial({
            color: new THREE.Color(0.1, 0.5, 0.9), // 蓝色高亮
            emissive: new THREE.Color(0.03, 0.1, 0.2), // 蓝色自发光
            specular: new THREE.Color(0.6, 0.6, 0.6), // 较强高光反射
            shininess: 80, // 高光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          });
          
          // 如果组件对象是网格对象，直接替换材质
          if (obj.isMesh) {
            obj.material = customMaterial;
            console.log(`已为 ${obj.name} 应用RGV装饰条高亮材质`);
          } 
          // 如果组件对象包含子对象，遍历子对象并替换材质
          else {
            obj.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
              }
            });
            console.log(`已为 ${obj.name} 及其子对象应用RGV装饰条高亮材质`);
          }
        });

        // 为RGV轨道应用材质
        rgvGd.forEach((obj) => {
          // 创建RGV轨道的金属材质
          const customMaterial = new THREE.MeshPhongMaterial({
            color: new THREE.Color(0.25, 0.25, 0.3), // 深灰色金属色
            emissive: new THREE.Color(0.01, 0.01, 0.01), // 微弱自发光
            specular: new THREE.Color(0.15, 0.15, 0.15), // 低高光反射
            shininess: 20, // 低光泽度
            transparent: false,
            opacity: 1.0,
            wireframe: false,
          });
          
          // 如果组件对象是网格对象，直接替换材质
          if (obj.isMesh) {
            obj.material = customMaterial;
            console.log(`已为 ${obj.name} 应用RGV轨道金属材质`);
          } 
          // 如果组件对象包含子对象，遍历子对象并替换材质
          else {
            obj.traverse((child) => {
              if (child.isMesh) {
                child.material = customMaterial;
              }
            });
            console.log(`已为 ${obj.name} 及其子对象应用RGV轨道金属材质`);
          }
        });

    },

    // 设置摄像头到指定楼层的观测点
    setCameraToFloor(floorNumber) {
      if (!this.ThreeEngine || !this.ThreeEngine.camera) {
        console.warn('摄像头未初始化');
        return;
      }

      // 根据楼层设置不同的摄像头位置
      const positions = {
        1: { x: -700, y: -70, z: -150 },
        2: { x: -700, y: 20, z: -150 },
        3: { x: -700, y: 100, z: -150 },
        4: { x: -700, y: 150, z: -150 }
      };

      const lookAtPositions = {
        1: { x: 0, y: -50, z: 0 },
        2: { x: 0, y: -50, z: 0 },
        3: { x: 0, y: 100, z: 0 },
        4: { x: 0, y: 150, z: 0 }
      };

      const pos = positions[floorNumber];
      const lookAt = lookAtPositions[floorNumber];

      if (pos && lookAt) {
        // 平滑过渡到新视角
        const positionTween = new TWEEN.Tween(this.ThreeEngine.camera.position)
          .to(pos, 1000)
          .easing(TWEEN.Easing.Quadratic.InOut);
          
        // 在位置动画完成后设置看向目标点
        positionTween.onComplete(() => {
          this.ThreeEngine.camera.lookAt(lookAt.x, lookAt.y, lookAt.z);
        });
        
        positionTween.start();
      //  

        console.log(`摄像头已设置到楼层${floorNumber}的观测点`);
      } else {
        console.warn(`无效的楼层号: ${floorNumber}`);
      }
    },

    // 重置摄像头视角
    resetCamera() {
      var that = this;
      if (!that.ThreeEngine || !that.ThreeEngine.camera) {
        console.warn('摄像头未初始化');
        return;
      }

      // 重置到初始视角
      const positionTween = new TWEEN.Tween(this.ThreeEngine.camera.position)
        .to({ x: -700, y: 0, z: 0 }, 1000)
        .easing(TWEEN.Easing.Quadratic.InOut);
        
      // 在位置动画完成后设置看向目标点
      positionTween.onComplete(() => {
        this.ThreeEngine.camera.lookAt(0, 0, 0);
      });
      
      positionTween.start();

      console.log('摄像头视角已重置');
    },

    init() {
      var that = this;
      var dom = document.getElementById("3d");
      this.ThreeEngine = new ThreeEngine(dom); 
      
      
     

      // 添加基础模型
      this.ThreeEngine.addObjects(...allBaseObject);
      // 添加灯光
      this.ThreeEngine.addObjects(...allHelper);
      this.ThreeEngine.addObjects(...allLights);

      const loader = new GLTFLoader();
      //
      const dracoLoader = new DRACOLoader();
      dracoLoader.setDecoderPath('/examples/jsm/libs/draco/');
      loader.setDRACOLoader(dracoLoader);
      let mixer = null; //声明一个播放器变量
      loader.load("/讯联新工厂.glb", function (glb) {
       
        //blander中灯光强度与three.js不同，这个需要做一个计算
        glb.scene.traverse((object) => {
            if (object.isLight) {
              // 禁用模型中的所有光源，使用我们自定义的灯光设置
              object.visible = false;
              console.log('已禁用模型中的光源:', object);
            }
            if(object.userData.aaaaa=="bbbbb"){
              console.log('ccccccccccccc');
            }
        });
        that.ThreeEngine.addObject(glb.scene);
        that.addStorageMachineLabels();
        // 添加自定义地板材质
        that.applyCustomFloorMaterials();
        // 添加堆垛机躯干材质
        that.applyStackerBodyMaterials();
        // 添加货架材质
        that.applyShelfMaterials();
        // 添加载货台材质
        that.applyPlatformMaterials();
        // 添加上货叉材质
        that.applyUpperForkMaterials();
        // 添加下货叉材质
        that.applyLowerForkMaterials();
        // 添加RGV材质
        that.applyRgvMaterials();
        
      }
      , function (xhr) {
            const percent = xhr.loaded / xhr.total;
            that.jindu=Math.round(100*percent) +"%";
          //   console.log('加载进度' + percent);
             if(percent>=1){
              setTimeout(function() {
                that.jiinduDisable=false;
              }, 300);
          
             }
            } 
 
    );

      // 创建一个时钟对象Clock
      const clock = new THREE.Clock();
      function render() {
          TWEEN.update();
          that.requestId = requestAnimationFrame(render);
          if (mixer !== null) {
              mixer.update(clock.getDelta());
          }
          if(that.lift1texture!=undefined){
              that.lift1texture.offset.x -= 0.04;
          }
          if(that.texture!=undefined){
              that.texture.offset.x -= 0.01; // 改为原来的50%
          }
      }
      render();
    },

    initObjClickHandler() { 
       // 重写ThreeEngine的点击事件处理函数
      this.ThreeEngine.onObjectClick = (intersect) => {
        // 检查点击的对象是否为"堆垛机躯干"
        if (intersect.object.name === "躯干1") {
          this.handleStackerClick(intersect.object);
        }
         if (intersect.object.name === "载货台1") {
          this.handleStackerClick(intersect.object);
        }
      };
    },

    // 处理堆垛机点击事件
    handleStackerClick(stackerObject) {
      console.log('点击了堆垛机躯干:', stackerObject.name);
      
      // 显示堆垛机信息面板
      this.stackerInfoVisible = true;
      this.stackerInfo = {
        name: stackerObject.name,
        status: '运行中',
        position: `X: ${stackerObject.position.x.toFixed(2)}, Y: ${stackerObject.position.y.toFixed(2)}, Z: ${stackerObject.position.z.toFixed(2)}`
      };
      
      // 可以在这里添加其他处理逻辑，例如：
      // 1. 高亮显示堆垛机
      // 2. 显示详细信息面板
      // 3. 触发相关操作等
    },

    createSpritePallet(objName, text){
      const spriteText = createSpriteTextLabel({
        global: {
          headerHeight:50,
          totalWidth:300,
            deviceName: text,
            status: "active",
            backgroundColor: 'rgba(9, 28, 64, 0.9)', // 深蓝透明背景
            headerBackground: 'rgba(16, 78, 126, 0.8)', // 更深的渐变蓝
            headerTextColor: '#FFF', // 浅青文字
            textStyle: {
                fontFamily: "Microsoft YaHei",
                fontSize: 16,
                color: "#E6F7FF", // 全局浅青文字
                textShadow: '0 0 2px rgba(173,216,230,0.8)' // 文字发光效果
            },
            borderColor: '#4A90E2' // 新增科技蓝边框
        },
        
    });
      spriteText.position.y = 8; //标签底部箭头和空对象标注点重合
      const obj = this.ThreeEngine.getObjectByName(objName);
      obj.add(spriteText); //tag会标注在空对象obj对应的位置
    },

    //给设备加上方标记
    createSprite(objName, text,y) {
      if(!y){
        y=8;
      }
      // 使用
      const spriteText = createSpriteTextLabel({
    global: {
      headerHeight:50,
      totalWidth:450,
        deviceName: "AGV小车01",
        status: "active",
        backgroundColor: 'rgba(9, 28, 64, 0.9)', // 深蓝透明背景
        headerBackground: 'rgba(16, 78, 126, 0.8)', // 更深的渐变蓝
        headerTextColor: '#FFF', // 浅青文字
        textStyle: {
            fontFamily: "Microsoft YaHei",
            fontSize: 16,
            color: "#E6F7FF", // 全局浅青文字
            textShadow: '0 0 2px rgba(173,216,230,0.8)' // 文字发光效果
        },
        borderColor: '#4A90E2' // 新增科技蓝边框
    },
    rows: [
        {
            rowHeight: 50,
            backgroundColor: 'rgba(26, 67, 117, 0.6)', // 半透明科技蓝
            columns: [
                {
                    content: "运行状态",
                    widthRatio: 5,
                    textStyle: {
                        color: "#4A90E2",
                        fontWeight: "bold",
                        align: "center",
                        fontSize: 25
                    }
                },
                {
                    content: "在线",
                    widthRatio: 5,
                    textStyle: {
                        color: "#00F2FE", // 霓虹青
                        align: "center",
                        fontSize: 25
                    }
                }
            ]
        },
        {
            rowHeight: 50,
            columns: [
                {
                    content: "任务队列",
                    widthRatio: 5,
                    textStyle: {
                        color: "#4A90E2",
                        fontWeight: "bold",
                        align: "center",
                        fontSize: 25
                    }
                },
                {
                    content: "3/5",
                    widthRatio: 5,
                    textStyle: {
                        color: "#7FFFD4", // 宝石绿
                        align: "center",
                        fontSize: 25
                    }
                }
            ]
        }
    ]
});
      spriteText.position.y = y; //标签底部箭头和空对象标注点重合
      const obj = this.ThreeEngine.getObjectByName(objName);
      if(!obj){
        return; 
      }
      obj.add(spriteText); //tag会标注在空对象obj对应的位置
    },

    removeaa(objName) {
      this.ThreeEngine.removeByName(objName);
    },

    // 连接
    join() {
      var that = this;
      const wsuri = this.url;
      this.ws = new WebSocket(wsuri);
      const self = this;
      this.ws.onopen = function (event) {
        console.info("连接");
      };

      this.ws.onmessage = function (event) {
        // self.text_content = event.data + "\n";
        var data = JSON.parse(event.data);
        console.info("接收", data);
        if (data.type == "agv") {
          if (data.fromNode == "00" && data.toNode == "C01") {
            that.agvTaskRun(0);
          }
          if (data.fromNode == "C01" && data.toNode == "R01") {
            that.agvTaskRun(1);
          }
          if (data.fromNode == "R01" && data.toNode == "00") {
            that.agvTaskRun(2);
          }
        }
        if (data.type == "agv_shangliao") {
          that.shangxialiao(4);
        }
        if (data.type == "agv_xialiao") {
          that.shangxialiao(5);
        }
        if (data.type == "scTask") {
          that.scTaskRun(data.fromNode, data.toNode);
        }

        if (data.type == "ssx") {
          if (data.ssxType == "leave") {
            that.ssx(data.fromNode, data.toNode, data.palletCode);
          }

          if (data.ssxType == "hasPallet") {
            that.hasPallet(data.fromNode, data.palletCode);
          }
          if (data.ssxType == "noHasPallet") {
            that.noHasPallet(data.fromNode, data.palletCode);
          }
        }
      };
      this.ws.onclose = function (event) {
        self.text_content = self.text_content + "已经关闭连接!" + "\n";
      };
    },
    exit() {
      if (this.ws) {
        this.ws.close();
        this.ws = null;
      }
    },
    send() {
      if (this.ws) {
        this.ws.send(this.message);
      } else {
        alert("未连接到服务器");
      }
    },

    hasPallet(cellCode, palletCode) {
      var that = this;
      if (palletCode == null || palletCode == "" || palletCode == "123") {
        palletCode = cellCode + "palletCode";
      }
      that.pallet(palletCode, that.ThreeEngine.getObjectByName(cellCode));
    },
    noHasPallet(cellCode, palletCode) {
      var that = this;
      if (palletCode == null || palletCode == "" || palletCode == "123") {
        palletCode = cellCode + "palletCode";
      }
      that.ThreeEngine.removeByName(palletCode);
    },
    aaaaa() {
      var that = this;
      var spotLight = new SpotLight(0xffffff);
      const sjwz = this.ThreeEngine.getObjectByName("视觉位置");
      const sjfx = this.ThreeEngine.getObjectByName("视觉方向");
      spotLight.position.set(sjwz.position.x, sjwz.position.y, sjwz.position.z);
      spotLight.intensity = 100;
      spotLight.distance = 25;
      spotLight.angle = 0.3;
      spotLight.target = sjfx; //光源自动追踪方块
      this.ThreeEngine.addObjects(spotLight);
      // 设置一个1000毫秒后执行的定时器
      setTimeout(function () {
        that.ThreeEngine.remove(spotLight);
      }, 50);
    },

    agv_router(type) {
      // var that=this;
      // that.agv_fx=type;
    },

    createPath(pointsArr) {
      // 将参数数组转换成点数组的形式
      pointsArr = pointsArr.map((point) => new THREE.Vector3(...point));
      // 自定义三维路径 curvePath
      const path = new THREE.CurvePath();
      for (let i = 0; i < pointsArr.length - 1; i++) {
        // 每两个点之间形成一条三维直线
        const lineCurve = new THREE.LineCurve3(pointsArr[i], pointsArr[i + 1]);
        // curvePath有一个curves属性，里面存放组成该三维路径的各个子路径
        path.curves.push(lineCurve);
      }
      return path;
    },

    // type 0 ：00-起点   1 C01--R01  2:R01-00 3:R01-C01  4 上料  5 下料
    agvTaskRun(type) {
      var that = this;
      that.agvTask.isStart = 2;
      that.createAgvRouter(type);
      that.agvTask.isStart = 0;
      that.moveAgvCar();
    },

    shangxialiao(type) {
      var that = this;
      that.agvTweenStop();
      if (type == 4) {
        var from = "29";
        var to = "29-001";
        var agv_wz_code = "agv_router10";
        var agv_wz_fx_code = "agv_router11";

        var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
        pallet_agv.visible = false;
      }

      if (type == 5) {
        var from = "03-001";
        var to = "03";
        var agv_wz_code = "agv_router1";
        var agv_wz_fx_code = "agv_router0";
        var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
        pallet_agv.visible = false;
      }
      var that = this;
      that.agvTask.isStart = 2;
      const fromCell = that.ThreeEngine.getObjectByName(from);
      var pallet = that.pallet("123", fromCell);
      const toCell = that.ThreeEngine.getObjectByName(to);
      const agv = that.ThreeEngine.getObjectByName("agv");
      const agv_wz = that.ThreeEngine.getObjectByName(agv_wz_code);
      const agv_fx = that.ThreeEngine.getObjectByName(agv_wz_fx_code);
      agv.position.x = agv_wz.position.x;
      agv.position.y = agv_wz.position.y;
      agv.position.z = agv_wz.position.z;
      agv.lookAt(agv_fx.position.x, agv_fx.position.y, agv_fx.position.z);
      if (agv.tweenA != undefined && agv.tweenA != null) {
        agv.tweenA.stop();
      }
      agv.tweenA = new TWEEN.Tween(pallet.position);
      agv.tweenA.to(
        { x: toCell.position.x, y: toCell.position.y, z: toCell.position.z },
        2000
      );
      agv.tweenA.start();
      agv.tweenA.onComplete(function () {
        if (type == 4) {
          var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
          pallet_agv.visible = true;
        }

        if (type == 5) {
          var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
          pallet_agv.visible = false;
        }
      });
    },

    // type 0 ：00-起点   1 C01--R01  2:R01-00 3:R01-C01
    createAgvRouter(type) {
      var that = this;
      var yy = 20;
      const pointsArr = [];
      var fx;
      var from;
      var to;
      if (type == 0) {
        fx = 1;
        from = 1;
        to = 10;
        that.agvTask.agv_fx = fx;
      }

      if (type == 1) {
        fx = -1;
        from = 10;
        to = 1;
        that.agvTask.agv_fx = fx;
      }

      if (type == 2) {
        fx = 1;
        from = 1;
        to = 5;
        that.agvTask.agv_fx = fx;
      }
      if (type == 3) {
        fx = 1;
        from = 1;
        to = 10;
        that.agvTask.agv_fx = fx;
      }

      that.agvRouter = [];
      that.ThreeEngine.removeByName("agvRouter11");
      // this.ThreeEngine.getObjectByName("agvRouter");
      if (fx == -1) {
        for (let index = from; index >= to; index--) {
          var agv_router = this.ThreeEngine.getObjectByName(
            "agv_router" + index
          );
          pointsArr.push([
            agv_router.position.x,
            agv_router.position.y,
            agv_router.position.z,
          ]);
          that.agvRouter.push({
            x: agv_router.position.x,
            y: agv_router.position.y,
            z: agv_router.position.z,
          });
        }
      }

      if (fx == 1) {
        for (let index = from; index <= to; index++) {
          var agv_router = this.ThreeEngine.getObjectByName(
            "agv_router" + index
          );
          pointsArr.push([
            agv_router.position.x,
            agv_router.position.y,
            agv_router.position.z,
          ]);
          that.agvRouter.push({
            x: agv_router.position.x,
            y: agv_router.position.y,
            z: agv_router.position.z,
          });
        }
      }

      console.info(that.agvRouter);

      const curve = this.createPath(pointsArr);

      // 2. 创建管道体
      const tubeGeometry = new THREE.TubeGeometry(curve, 1000, 1, 2, false);
      // 纹理贴图：一定要使用透明背景的图片，否则贴图会全部叠在一起，看不出来效果
      const texLoader = new THREE.TextureLoader();
      // 图片可以用这张：http://pic.yupoo.com/mazhenghjj/e546038d/9610773f.jpg

      this.texture = texLoader.load("/img/hhh.png");
      const texture = this.texture;
      // 允许横纵设置矩阵（人话就是可以平铺）
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      texture.repeat.x = 20;
      texture.repeat.y = 1;
      // texture.offset.z =1;

      // 3. 创建管道材质
      const tubeMaterial = new THREE.MeshPhongMaterial({
        map: texture, // 颜色贴图
        transparent: true,
        color: 0x47d8fa,
        side: THREE.DoubleSide,
      });

      const mesh = new THREE.Mesh(tubeGeometry, tubeMaterial);
      mesh.position.y = 0;
      mesh.position.x = 0;
      mesh.name = "agvRouter11";
      // 4. 把几何体（管道）和 材质 生成的网格物体添加到场景中
      this.ThreeEngine.addObject(mesh);
    },

    //  AGV移动动画
    moveAgvCar() {
  var that = this;
  const AgvCar = this.ThreeEngine.getObjectByName("agv"); 
  var agvRouter = this.agvRouter;
  AgvCar.tweens = [];
  that.agvTweenStop(); 
  if (agvRouter != null && agvRouter.length > 0) {
    var position = agvRouter[0]; 
    for (var i = 0; i < agvRouter.length; i++) {
      var ele = agvRouter[i]; 
      if (i === 0) {
        AgvCar.position.set(position.x, position.y, position.z);
        AgvCar.lookAt(agvRouter[1].x, agvRouter[1].y, agvRouter[1].z); 
        // 初始化摄像头位置，让摄像头在 AGV 后方一定距离跟随
        const followDistance = 10; // 可根据实际情况调整
        that.ThreeEngine.camera.position.set(
          AgvCar.position.x - followDistance,
          AgvCar.position.y,
          AgvCar.position.z
        );
        that.ThreeEngine.camera.lookAt(AgvCar.position);
      } else {
        AgvCar["tween" + i] = new TWEEN.Tween(AgvCar.position);   
        that.agvLookAt(AgvCar, AgvCar["tween" + i], i); 
        // 在每个 tween 动画完成后更新摄像头位置
        AgvCar["tween" + i].onUpdate(function () {
          const followDistance = 10; // 可根据实际情况调整
          that.ThreeEngine.camera.position.set(
            AgvCar.position.x - followDistance,
            AgvCar.position.y,
            AgvCar.position.z
          );
          that.ThreeEngine.camera.lookAt(AgvCar.position);
        });
      } 
    };  
    AgvCar.tween.start();
  } 
},

    agvTweenStop(){
      const AgvCar = this.ThreeEngine.getObjectByName("agv"); 
      for(var i =0;i<20;i++){
        var aaaaa =   AgvCar["tween"+i];
          if(aaaaa!=undefined && aaaaa!=null){
            aaaaa.stop();
          } 
          AgvCar["tween"+i]=null;
      } 
    },

    agvLookAt(AgvCar,tween,i){
      var that = this;
      var agvRouter=that.agvRouter; 
      var from  = agvRouter[i-1];
      var to  = agvRouter[i];
      var distance = that.distance3D(from,to);
      var time = distance*150;
      tween.to(to , time);  
      if(i<agvRouter.length-1){
        tween.onComplete(function () {
          var agvRouter=that.agvRouter;
          AgvCar.lookAt(agvRouter[i+1].x,agvRouter[i+1].y,agvRouter[i+1].z);
        });
      };
      that.agvTween(AgvCar,tween,i);
    },

    distance3D(point1, point2) {
        return Math.sqrt(
            Math.pow(point2.x - point1.x, 2) +
            Math.pow(point2.y - point1.y, 2) +
            Math.pow(point2.z - point1.z, 2)
        );
    },

    agvTween(AgvCar,tween ,i){ 
      if(i==1){
        AgvCar.tween=tween;    
      }else if(i>1){
        AgvCar["tween"+(i-1)].chain(tween);
      } 
    },

    
    createLiftRouter(fromCode, toCode) {
      var that = this;
      var yy = 20;
      var from = this.ThreeEngine.getObjectByName(fromCode);
      var to = this.ThreeEngine.getObjectByName(toCode);
      const pointsArr = [];
      pointsArr.push([from.position.x, from.position.y, from.position.z]);
      pointsArr.push([to.position.x, to.position.y, to.position.z]);
      const curve = this.createPath(pointsArr);

      // 2. 创建管道体
      const tubeGeometry = new THREE.TubeGeometry(curve, 10, 2, 10, false);
      // 纹理贴图：一定要使用透明背景的图片，否则贴图会全部叠在一起，看不出来效果
      const texLoader = new THREE.TextureLoader();
      // 图片可以用这张：http://pic.yupoo.com/mazhenghjj/e546038d/9610773f.jpg

      this.lift1texture = texLoader.load("/img/hhh.png");
      const texture = this.lift1texture;
      // 允许横纵设置矩阵（人话就是可以平铺）
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      texture.repeat.y = 30;
      texture.repeat.x = 3;
      texture.offset.z = 11;

      // 3. 创建管道材质
      const tubeMaterial = new THREE.MeshPhongMaterial({
        map: texture, // 颜色贴图
        transparent: true,
        color: 0x47d8fa,
        side: THREE.DoubleSide,
      });

      that.ThreeEngine.removeByName("liftRouter" + fromCode);
      that.ThreeEngine.removeByName("liftRouter" + toCode);
      const mesh = new THREE.Mesh(tubeGeometry, tubeMaterial);
      mesh.position.y = 0;
      mesh.position.x = 0;
      mesh.name = "liftRouter" + fromCode;
      // 4. 把几何体（管道）和 材质 生成的网格物体添加到场景中
      this.ThreeEngine.addObject(mesh);
    },

    //createLiftRouter() {
    //   var that = this;
    //   var yy = 20;
    //   const pointsArr = [];
    //   for (let index = 1; index <= 2; index++) {
    //     var lift_router1 = this.ThreeEngine.getObjectByName(
    //       "lift_router1-" + index
    //     );
    //     pointsArr.push([
    //       lift_router1.position.x,
    //       lift_router1.position.y,
    //       lift_router1.position.z,
    //     ]);
    //   }
    //   const curve = this.createPath(pointsArr);

    //   // 2. 创建管道体
    //   const tubeGeometry = new THREE.TubeGeometry(curve, 10, 2, 10, false);
    //   // 纹理贴图：一定要使用透明背景的图片，否则贴图会全部叠在一起，看不出来效果
    //   const texLoader = new THREE.TextureLoader();
    //   // 图片可以用这张：http://pic.yupoo.com/mazhenghjj/e546038d/9610773f.jpg

    //   this.lift1texture = texLoader.load("/img/hhh.png");
    //   const texture = this.lift1texture;
    //   // 允许横纵设置矩阵（人话就是可以平铺）
    //   texture.wrapS = THREE.RepeatWrapping;
    //   texture.wrapT = THREE.RepeatWrapping;
    //   texture.repeat.y = 30;
    //   texture.repeat.x = 3;
    //   texture.offset.z = 11;

    //   // 3. 创建管道材质
    //   const tubeMaterial = new THREE.MeshPhongMaterial({
    //     map: texture, // 颜色贴图
    //     transparent: true,
    //     color: 0x47d8fa,
    //     side: THREE.DoubleSide,
    //   });

    //   const mesh = new THREE.Mesh(tubeGeometry, tubeMaterial);
    //   mesh.position.y = 0;
    //   mesh.position.x = 0;
    //   // 4. 把几何体（管道）和 材质 生成的网格物体添加到场景中
    //   this.ThreeEngine.addObject(mesh);
    // },

    // 根据几个关键点，创建路径

    Route(points) {
      let curvePoints = new Array();
      for (let i = 0; i < points.length; i++) {
        let point = points[i];
        curvePoints.push(new THREE.Vector3(point.x, point.y, point.z));
      }
      let curve = new THREE.CatmullRomCurve3(
        curvePoints,
        false /*是否闭合*/,
        "catmullrom",
        0.000000001
      );
      return curve;
    },

    aaa() {
      this.task();
    },

    scTaskRun(from, to) {
      var that = this;

      this.scTask(1, 123, from, function name() {
        that.scTask(-1, 123, to);
      });
    },

    scTask(type, palletCode, cellCode, aaa) {
      var that = this;

      var x = 0;
      var y = 0;

      if (type == 1) {
        var scPallet = that.ThreeEngine.getObjectByName("pallet");
        scPallet.visible = false;
      } else if (type == -1) {
        var scPallet = that.ThreeEngine.getObjectByName("pallet");
        scPallet.visible = true;
      }

      const meshCell = this.ThreeEngine.getObjectByName(cellCode);

      x = meshCell.position.x;

      y = meshCell.position.y;
      const mesh = this.ThreeEngine.getObjectByName("堆垛机躯干");
      y = y - mesh.position.y;
      if (mesh.tweenA != undefined && mesh.tweenA != null) {
        mesh.tweenA.stop();
      }

      mesh.tweenA = new TWEEN.Tween(mesh.position);

      var timeA = 100 * (mesh.position.x - x);
      timeA = Math.abs(timeA);
      mesh.tweenA.to({ x: x }, timeA);
      const zht = this.ThreeEngine.getObjectByName("载货台");

      if (zht.tweenB != undefined && zht.tweenB != null) {
        zht.tweenB.stop();
      }
      zht.tweenB = new TWEEN.Tween(zht.position);
      var timeB = 200 * (zht.position.y - y);
      timeB = Math.abs(timeB);
      zht.tweenB.to({ y: y - 0.4 * type }, timeB);

      zht.tweenC = new TWEEN.Tween(zht.position);

      zht.tweenC.to({ y: y + 0.4 * type }, 500);

      const hc = this.ThreeEngine.getObjectByName("上货叉");
      const hc2 = this.ThreeEngine.getObjectByName("下货叉");
      const zhtwz = this.ThreeEngine.getObjectByName("载货台位置");
      if (type == 1) {
        zhtwz.children = [];
      }
      //为了解决货叉位置莫名其妙出问题，直接把货叉额原始位置存在堆垛机里
      if (mesh.hcX == undefined || mesh.hcX == null) {
        mesh.hcX = hc.position.x;
      }else{
        hc.position.x=mesh.hcX;
      }
      if (mesh.hc2X == undefined || mesh.hc2X == null) {
        mesh.hc2X = hc2.position.x;
      }else{
        hc2.position.x=mesh.hc2X;
      }

      if (hc.tweenS != undefined && hc.tweenS != null) {
        hc.tweenS.stop();
      }
      var fx;
      if (cellCode == "22" || cellCode == "01") {
        fx = -1;
      } else {
        fx = 1;
      }
      debugger;
      //下货叉伸
      hc2.tweenS = new TWEEN.Tween(hc2.position);
      hc2.tweenS.to({ z: mesh.hc2X - 1.3 * fx }, 1000);
      //下货叉回
      hc2.tweenH = new TWEEN.Tween(hc2.position);
      hc2.tweenH.to({ z: mesh.hc2X }, 1000);
      //上货叉伸
      hc.tweenS = new TWEEN.Tween(hc.position);
      hc.tweenS.to({ z: mesh.hcX - 3.8 * fx }, 2000);
      //上货叉回
      hc.tweenH = new TWEEN.Tween(hc.position);
      hc.tweenH.to({ z: mesh.hcX }, 2000);
      if (type == 1) {
        hc.tweenS.onComplete(function () {
          const cell = that.ThreeEngine.getObjectByName(cellCode);

          that.remove(palletCode);
          var scPallet = that.ThreeEngine.getObjectByName("pallet");
          scPallet.visible = true;
        });
      } else if (type == -1) {
        zht.tweenC.onComplete(function () {
          that.remove(palletCode);
          const cell = that.ThreeEngine.getObjectByName(cellCode);

          var scPallet = that.ThreeEngine.getObjectByName("pallet");
          scPallet.visible = false;
          that.pallet(palletCode, cell);
        });
      }
      //动画执行先后顺序

      //躯干行走--》载货台到位
      mesh.tweenA.chain(zht.tweenB);
      //载货台到位 --》下货叉伸
      zht.tweenB.chain(hc2.tweenS);
      //下货叉伸--》上货叉伸
      hc2.tweenS.chain(hc.tweenS);
      //上货叉伸--》载货台动
      hc.tweenS.chain(zht.tweenC);
      //载货台动--》下货叉收
      zht.tweenC.chain(hc2.tweenH);
      //下货叉收--》上货叉收
      hc2.tweenH.chain(hc.tweenH);

      hc.tweenH.onComplete(aaa);
      mesh.tweenA.start();
      //tween动画开始执行
    },

    //输送线动画
    ssx(fromCellCode, toCellCode, palletCode) {
      var that = this;
      if (palletCode == null || palletCode == "" || palletCode == "123") {
        palletCode = toCellCode + "palletCode";
      }
      that.ThreeEngine.removeByName(fromCellCode + "palletCode");
      if (fromCellCode == "05" && toCellCode == "06") {
        this.ssx2(fromCellCode, "05-001", toCellCode, palletCode);
        return;
      }
      if (fromCellCode == "15" && toCellCode == "16") {
        this.ssx2(fromCellCode, "15-001", toCellCode, palletCode);
        return;
      }
      if (fromCellCode == "21" && toCellCode == "22") {
        this.ssx2(fromCellCode, "21-001", toCellCode, palletCode);
        return;
      }

      const fromCell = that.ThreeEngine.getObjectByName(fromCellCode);
      var pallet = that.pallet(palletCode, fromCell);
      const toCell = that.ThreeEngine.getObjectByName(toCellCode);
      var tweenA = new TWEEN.Tween(pallet.position);
      tweenA.to(
        { x: toCell.position.x, y: toCell.position.y, z: toCell.position.z },
        2000
      );

      tweenA.start();
    },

    //专门给提升机用
    ssx2(fromCellCode, zzCellCode, toCellCode, palletCode) {
      var that = this;
      const fromCell = that.ThreeEngine.getObjectByName(fromCellCode);
      var pallet = that.pallet(palletCode, fromCell);
      const toCell = that.ThreeEngine.getObjectByName(toCellCode);

      const zzwCell = that.ThreeEngine.getObjectByName(zzCellCode);

      var liftFrom = fromCellCode + "-a";
      var liftTo = zzCellCode + "-a";
      that.createLiftRouter(liftFrom, liftTo);

      var tweenA = new TWEEN.Tween(pallet.position);
      tweenA.to(
        {
          x: zzwCell.position.x,
          y: zzwCell.position.y,
          z: zzwCell.position.z,
        },
        3000
      );
      tweenA.onComplete(function () {
        that.ThreeEngine.removeByName("liftRouter" + liftFrom);
        that.ThreeEngine.removeByName("liftRouter" + liftTo);
      });

      var tweenB = new TWEEN.Tween(pallet.position);
      tweenB.to(
        { x: toCell.position.x, y: toCell.position.y, z: toCell.position.z },
        2000
      );
      tweenA.chain(tweenB);
      tweenA.start();
    },

    remove(name) {
      var that = this;
      const obj = that.ThreeEngine.getObjectByName(name);
      if (obj != null) {
        const parentObj = obj.parent;
        if (parentObj != null) {
          if (parentObj.name != "") {
            parentObj.children = [];
          }
        }
      }
      that.ThreeEngine.removeByName(name);
    },

    pallet(palletCode, obj) {
      var that = this;

      that.remove(palletCode);

      var newPallet = that.ThreeEngine.getObjectByName("pallet").clone();
      newPallet.visible = true;
      newPallet.position.set(obj.position.x, obj.position.y, obj.position.z); // 将Y坐标增加1，表示在父物体上方1单位长度的位置
      newPallet.name = palletCode;
      that.ThreeEngine.addObject(newPallet);
      if (
        palletCode != null &&
        palletCode.length > 5 &&
        palletCode.substring(0, 3) == "OSA"
      ) {
        this.createSprite(palletCode, palletCode);
      }

      return newPallet;
    },

    getChildByName(obj, name) {
      obj.children.forEach((child) => {
        if (child.name == name) {
          return child;
        }
      });
    },
  },
};
</script>

<style lang="scss">
.container3d {
  width: 100%;
  height: 100%;
  position: relative;
  // background-color: #041e0b;
  //background-color: rgba(0, 0, 0, 0.8);

  background-image: url("/img/999.jpg"); /* 替换 'image-path.jpg' 为你的图片路径 */
  background-size: cover; /* 背景图片覆盖整个元素 */
  background-repeat: no-repeat; /* 背景图片不重复 */
  background-position: center; /* 背景图片居中 */
  
  // 添加轻微的遮罩层，使3D区域与报表区域更好地融合
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(
      ellipse at center,
      rgba(0, 0, 0, 0.1) 0%,
      rgba(0, 0, 0, 0.3) 70%,
      rgba(0, 0, 0, 0.5) 100%
    );
    pointer-events: none;
  }
}

// 控制面板样式
.control-panel {
  position: absolute;
  z-index: 15;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  // 更自然的背景，减少边缘对比度
  background: radial-gradient(
    ellipse at center,
    rgba(20, 30, 48, 0.7) 0%,
    rgba(20, 30, 48, 0.5) 40%,
    rgba(20, 30, 48, 0.2) 70%,
    transparent 100%
  );
  border-radius: 8px;
  // 更柔和的边框效果
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow: 
    0 0 5px rgba(64, 158, 255, 0.1),
    inset 0 0 10px rgba(0, 0, 0, 0.3);
  // 更轻的毛玻璃效果
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
}

.panel-top {
  top: 20px;
  left: 20px;
}

.panel-middle {
  top: 80px;
  left: 20px;
}

.panel-bottom {
  top: 140px;
  left: 20px;
}

.panel-debug {
  top: 20px;
  right: 20px;
  width: 200px;
}

.panel-debug h3 {
  color: #fff;
  margin: 0 0 10px 0;
  font-size: 16px;
  text-align: center;
  // 更柔和的发光效果
  text-shadow: 0 0 2px rgba(255, 255, 255, 0.3);
}

.debug-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

// 堆垛机信息面板样式
.stacker-info-panel {
  position: absolute;
  z-index: 20;
  top: 20px;
  left: 250px;
  width: 300px;
  padding: 15px;
  background: radial-gradient(
    ellipse at center,
    rgba(20, 30, 48, 0.9) 0%,
    rgba(20, 30, 48, 0.7) 40%,
    rgba(20, 30, 48, 0.4) 70%,
    transparent 100%
  );
  border-radius: 8px;
  border: 1px solid rgba(64, 158, 255, 0.3);
  box-shadow: 
    0 0 10px rgba(64, 158, 255, 0.2),
    inset 0 0 15px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  color: #fff;
}

.stacker-info-panel h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  text-align: center;
  text-shadow: 0 0 3px rgba(64, 158, 255, 0.5);
}

.info-content p {
  margin: 10px 0;
  font-size: 14px;
}

// 自定义输入框样式
.custom-input {
  width: 100px;
  padding: 8px 12px;
  // 更自然的背景
  background: rgba(30, 30, 30, 0.6);
  border: 1px solid rgba(100, 150, 255, 0.3);
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;

  &:focus {
    border-color: #4A90E2;
    box-shadow: 0 0 5px rgba(74, 144, 226, 0.3);
  }

  &::placeholder {
    color: #aaa;
  }
}

// 自定义按钮样式
.custom-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  outline: none;
  
  // 更柔和的背景渐变
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.8), rgba(53, 122, 189, 0.6));

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }

  &:active {
    transform: translateY(0);
  }

  &:focus {
    outline: 1px solid rgba(255, 255, 255, 0.2);
  }

  // 更柔和的按钮波纹效果
  &::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5px;
    height: 5px;
    background: rgba(255, 255, 255, 0.3);
    opacity: 0;
    border-radius: 100%;
    transform: scale(1, 1) translate(-50%);
    transform-origin: 50% 50%;
  }

  &:focus:not(:active)::after {
    animation: ripple 1s ease-out;
  }
}

// 按钮颜色变体
.custom-btn.primary {
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.8), rgba(53, 122, 189, 0.6));
  color: white;
}

.custom-btn.secondary {
  background: linear-gradient(135deg, rgba(80, 200, 120, 0.8), rgba(58, 151, 90, 0.6));
  color: white;
}

.custom-btn.success {
  background: linear-gradient(135deg, rgba(0, 201, 167, 0.8), rgba(0, 155, 119, 0.6));
  color: white;
}

.custom-btn.warning {
  background: linear-gradient(135deg, rgba(255, 165, 0, 0.8), rgba(204, 132, 0, 0.6));
  color: white;
}

.custom-btn.danger {
  background: linear-gradient(135deg, rgba(255, 71, 87, 0.8), rgba(201, 58, 64, 0.6));
  color: white;
}

// 波纹动画
@keyframes ripple {
  0% {
    transform: scale(0, 0);
    opacity: 0.3;
  }
  100% {
    transform: scale(50, 50);
    opacity: 0;
  }
}

.progress-box{ 
  border-radius:5vh;
  height: 25vh; 
  // 更自然的背景
  background: radial-gradient(
    ellipse at center,
    rgba(20, 30, 48, 0.8) 0%,
    rgba(20, 30, 48, 0.6) 40%,
    rgba(20, 30, 48, 0.3) 70%,
    rgba(20, 30, 48, 0.1) 100%
  );
  width: 30vw;
  margin-left: 30vw;
  margin-top: 30vh;
  position: absolute;
  z-index:999; 
  // 更柔和的边框效果
  border: 1px solid rgba(64, 158, 255, 0.2);
  box-shadow: 
    0 0 10px rgba(64, 158, 255, 0.1),
    inset 0 0 15px rgba(0, 0, 0, 0.3);
  // 更轻的毛玻璃效果
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  .name{
    color: #fff;
    width: 100%;
    text-align: center;
    margin-top: 5vh;
    margin-bottom: 5vh;
    // 更柔和的发光效果
    text-shadow: 0 0 2px rgba(255, 255, 255, 0.3);
  }
  .progress-bar { 
    width: 80%;
    margin-left: 10%;
    background-color: #e0e0e0;
    border-radius: 4px;
    overflow: hidden;
  } 
  .progress {
    width: 50%; /* 进度条的初始宽度，可以根据需要更改 */
    height: 20px;
    background-color: #4caf50;
    border-radius: 4px;
    transition: width 1s ease-in-out; /* 进度条变化的动画效果 */
  }
}

.three-canvas {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.workshop-text {
  box-shadow: 0 0 3px #2d69a1;
  padding: 10px;
  background: rgba(45, 105, 161, 0.3);
}

.workshop-text p {
  font-size: 0.3rem;
  font-weight: bold;
  padding: 10px;
  color: #fff;
}
.workshop-text.IDLE {
    background: rgba(236, 210, 114, 0.3) !important;
}

.workshop-text.RUNNING {
    background: rgba(134, 239, 212, 0.3) !important;
}

.workshop-text.OFFLINE {
    background: rgba(138, 177, 222, 0.3) !important;
}

.equipment-label {
    background: rgba(255, 255, 255, 0.7);
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    min-width: 200px;
    // 更轻的毛玻璃效果
    backdrop-filter: blur(3px);
    border: 1px solid rgba(255,255,255,0.1);
}

.label-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.label-header h3 {
    margin: 0;
    color: #2c3e50;
    font-size: 16px;
}

.close-btn {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    color: #999;
    transition: color 0.2s;
}

.close-btn:hover {
    color: #ff4757;
}

.label-content p {
    margin: 8px 0;
    color: #666;
    font-size: 14px;
    white-space: pre-wrap;
}

.status-indicator {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    margin-top: 8px;
}

.status-indicator.active {
    background: #2ecc71;
    box-shadow: 0 0 5px rgba(46, 204, 113, 0.3);
}
</style>