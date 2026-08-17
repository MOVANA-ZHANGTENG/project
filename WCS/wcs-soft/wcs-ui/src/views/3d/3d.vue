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
      <div class="control-panel panel-top">
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
      </div>

      <!-- 新增库位坐标输入界面 -->
      <div class="control-panel panel-position">
        <input class="custom-input" v-model="positionCellCode" placeholder="库位坐标（如：1-1-1）" />
        <button class="custom-btn position-btn" @click="moveCargoToPosition">移动货物到该位置</button>
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
import materialManager from './js/TMaterialManager.js';
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
      positionCellCode: "1-1-1",

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

    // 清理轨道控制器（已由TEngine管理）
    if (this.ThreeEngine && this.ThreeEngine.controls) {
      this.ThreeEngine.controls.dispose();
      this.ThreeEngine.controls = null;
    }
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
      that.init();
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
    createEquipmentLabel(objName, title, content) {
      var that = this;
        const container = document.createElement('div');
        container.className = 'equipment-label';
        container.innerHTML = `
            <div class="label-header">
                <h3>${title}</h3>
                <button class="close-btn">×</button>
            </div>
            <div class="label-content">
                <p>${content}</p>
                <div class="status-indicator active"></div>
            </div>
        `;

        const label = new CSS3DSprite(container);
        label.scale.set(0.1, 0.1, 0.1);
        label.position.set(0, 3, 0); // 在模型上方3个单位位置

        // 添加透视补偿
    label.onBeforeRender = function() {
        this.quaternion.copy(that.ThreeEngine.camera.quaternion);
        const scaleFactor = 50 / this.position.distanceTo(that.ThreeEngine.camera.position);
        this.scale.setScalar(scaleFactor * 0.15);
    };

         // 启用矩阵自动更新
    label.matrixAutoUpdate = true;

    const obj = this.ThreeEngine.getObjectByName(objName);
    obj.add(label);

    // 添加矩阵同步逻辑
    const originalUpdate = obj.updateMatrixWorld;
    obj.updateMatrixWorld = function() {
        originalUpdate.call(this);
        if(label.parent === this) {
            label.updateMatrixWorld(true);
        }
    };

        // 添加点击事件
        container.querySelector('.close-btn').onclick = () => {
            obj.remove(label);
        };

        return label;
    },

    // 在模型加载完成后调用
    // 添加自定义灯光配置
    addCustomLights() {
      // 1. 环境光 - 提供全局基础照明（适中亮度）
      const ambientLight = new THREE.AmbientLight(0xffffff, 0.35);
      ambientLight.name = 'customAmbientLight';
      this.ThreeEngine.addObject(ambientLight);

      // 2. 主方向光 - 主光源（适中亮度）
      const mainDirectionalLight = new THREE.DirectionalLight(0xffffff, 0.5);
      mainDirectionalLight.position.set(50, 100, 50);
      mainDirectionalLight.castShadow = true;
      mainDirectionalLight.shadow.camera.left = -100;
      mainDirectionalLight.shadow.camera.right = 100;
      mainDirectionalLight.shadow.camera.top = 100;
      mainDirectionalLight.shadow.camera.bottom = -100;
      mainDirectionalLight.shadow.camera.near = 0.5;
      mainDirectionalLight.shadow.camera.far = 500;
      mainDirectionalLight.shadow.mapSize.width = 2048;
      mainDirectionalLight.shadow.mapSize.height = 2048;
      mainDirectionalLight.name = 'customMainLight';
      this.ThreeEngine.addObject(mainDirectionalLight);

      // 3. 辅助方向光1 - 左侧补光（降低亮度）
      const fillDirectionalLight1 = new THREE.DirectionalLight(0xffffff, 0.25);
      fillDirectionalLight1.position.set(-50, 50, -50);
      fillDirectionalLight1.name = 'customFillLight1';
      this.ThreeEngine.addObject(fillDirectionalLight1);

      // 4. 辅助方向光2 - 右侧补光（降低亮度）
      const fillDirectionalLight2 = new THREE.DirectionalLight(0xffffff, 0.2);
      fillDirectionalLight2.position.set(50, 50, -50);
      fillDirectionalLight2.name = 'customFillLight2';
      this.ThreeEngine.addObject(fillDirectionalLight2);

      // 5. 半球光 - 天空和地面反射（降低亮度）
      const hemisphereLight = new THREE.HemisphereLight(0xffffff, 0x444444, 0.2);
      hemisphereLight.name = 'customHemisphereLight';
      this.ThreeEngine.addObject(hemisphereLight);

      // 6. 顶部补光 - 确保顶部照明充足（降低亮度）
      const topLight = new THREE.DirectionalLight(0xffffff, 0.15);
      topLight.position.set(0, 150, 0);
      topLight.name = 'customTopLight';
      this.ThreeEngine.addObject(topLight);

      console.log('✅ 已添加适中亮度灯光配置（6个光源）');
    },

    addStorageMachineLabels() {

        this.createSprite("堆垛机躯干", "堆垛机躯干",16);


    },

    // 递归输出GLB模型结构层级
    logGLBStructure(object, level) {
      const indent = '  '.repeat(level);

      // 获取对象类型
      let typeInfo = object.type;

      // 添加额外信息
      let extraInfo = '';
      if (object.isMesh) {
        extraInfo += ` [网格]`;
        if (object.geometry) {
          const vertexCount = object.geometry.attributes.position?.count || 0;
          extraInfo += ` (顶点:${vertexCount})`;
        }
        if (object.material) {
          const materialName = object.material.name || '未命名';
          extraInfo += ` 材质:${materialName}`;
        }
      }
      if (object.isLight) {
        extraInfo += ` [灯光] 强度:${object.intensity}`;
      }
      if (object.isCamera) {
        extraInfo += ` [相机]`;
      }

      // 输出位置信息
      const pos = object.position;
      const posInfo = `位置:(${pos.x.toFixed(2)}, ${pos.y.toFixed(2)}, ${pos.z.toFixed(2)})`;

      // 输出当前对象信息
      const objectName = object.name || '(无名称)';
      console.log(`${indent}├─ ${objectName} [${typeInfo}]${extraInfo} ${posInfo} 子节点:${object.children.length}`);

      // 递归输出子对象
      if (object.children && object.children.length > 0) {
        object.children.forEach(child => {
          this.logGLBStructure(child, level + 1);
        });
      }
    },

    // 强制修复双面渲染（解决某些角度消失的问题）
    fixDoubleSideRendering(scene) {
      console.log('========== 开始修复双面渲染 ==========');
      let fixedCount = 0;

      scene.traverse((object) => {
        if (object.isMesh && object.material) {
          // 强制设置为双面渲染
          if (object.material.side !== THREE.DoubleSide) {
            object.material.side = THREE.DoubleSide;
            object.material.needsUpdate = true;
            fixedCount++;
            console.log(`🔧 修复双面渲染: ${object.name}`);
          }
        }
      });

      console.log(`✅ 双面渲染修复完成，修复了 ${fixedCount} 个对象`);
      console.log('========================================');
    },

    // 应用材质到GLB模型的各个部件（智能模糊匹配）
    applyMaterialsToGLB(scene) {
      console.log('========== 开始应用材质配置（智能匹配） ==========');

      // 定义匹配规则：使用正则表达式或包含关键字进行模糊匹配
      const matchRules = [
        // 地板匹配规则
        {
          keywords: ['地板-' ],
          componentType: 'floor',
          partType: 'main',
          state: 'floor1',
          description: '地板-'
        },

        // 地板边界边框匹配规则
        {
          keywords: ['地板边界边框' ],
          componentType: 'floor',
          partType: 'border',
          state: 'default',
          description: '地板边界边框'
        },

        // 货架匹配规则
        {
          keywords: ['货架', 'shelf', 'Shelf', 'rack', 'Rack'],
          componentType: 'shelf',
          partType: 'main',
          state: 'default',
          description: '货架'
        },

        // 输送线框架匹配规则（包括链条机框架）
        {
          keywords: ['输送线框架', '链条机框架'],
          componentType: 'conveyor',
          partType: 'frame',
          state: 'default',
          description: '输送线框架'
        },

        // 输送线滚筒匹配规则
        {
          keywords: ['输送线滚筒' ],
          componentType: 'conveyor',
          partType: 'roller',
          state: 'default',
          description: '输送线滚筒'
        },

        // 链条机平板匹配规则（使用输送线框架材质）
        {
          keywords: ['链条机平板'],
          componentType: 'conveyor',
          partType: 'frame',
          state: 'default',
          description: '链条机平板（框架材质）'
        },

        // 输送线平板/皮带匹配规则
        {
          keywords: ['输送线平板' ],
          componentType: 'conveyor',
          partType: 'belt',
          state: 'default',
          description: '输送线平板'
        },

        // 链条匹配规则
        {
          keywords: ['链条机链条', '链条', 'chain', 'Chain'],
          componentType: 'conveyor',
          partType: 'chain',
          state: 'default',
          description: '链条'
        },

        // 堆垛机躯干匹配规则
        {
          keywords: ['堆垛机躯干' ],
          componentType: 'stacker',
          partType: 'body',
          state: 'default',
          description: '堆垛机躯干'
        },

        // 堆垛机载货台匹配规则
        {
          keywords: ['载货台' ],
          componentType: 'stacker',
          partType: 'platform',
          state: 'default',
          description: '堆垛机载货台'
        },

        // 堆垛机上货叉匹配规则
        {
          keywords: ['上货叉' ],
          componentType: 'stacker',
          partType: 'upperFork',
          state: 'default',
          description: '堆垛机上货叉'
        },

        // 堆垛机下货叉匹配规则
        {
          keywords: ['下货叉' ],
          componentType: 'stacker',
          partType: 'lowerFork',
          state: 'default',
          description: '堆垛机下货叉'
        },

        // 堆垛机天地轨道匹配规则
        {
          keywords: ['堆垛机天轨' ],
          componentType: 'stacker',
          partType: 'track',
          state: 'default',
          description: '堆垛机天地轨道'
        },

        // OHT支架匹配规则
        {
          keywords: ['OHT支架' ],
          componentType: 'oht',
          partType: 'frame',
          state: 'default',
          description: 'OHT支架'
        },

        // OHT轨道匹配规则
        {
          keywords: ['OHT轨道' ],
          componentType: 'oht',
          partType: 'track',
          state: 'default',
          description: 'OHT轨道'
        },

        // OHT车体匹配规则
        {
          keywords: ['OHT车体' ],
          componentType: 'oht',
          partType: 'body',
          state: 'default',
          description: 'OHT车体'
        },

        // OHT车轮匹配规则
        {
          keywords: ['OHT车轮' ],
          componentType: 'oht',
          partType: 'wheel',
          state: 'default',
          description: 'OHT车轮'
        },

        // OHT车轮支架匹配规则
        {
          keywords: ['OHT固定车轮支架' ],
          componentType: 'oht',
          partType: 'wheelBracket',
          state: 'default',
          description: 'OHT车轮支架'
        },

        // 机械臂支架匹配规则
        {
          keywords: ['机械臂支架' ],
          componentType: 'robotArm',
          partType: 'base',
          state: 'default',
          description: '机械臂支架'
        },

        // 机械臂一关节匹配规则
        {
          keywords: ['一关节' ],
          componentType: 'robotArm',
          partType: 'joint1',
          state: 'default',
          description: '机械臂一关节'
        },

        // 机械臂二关节匹配规则
        {
          keywords: ['二关节' ],
          componentType: 'robotArm',
          partType: 'joint2',
          state: 'default',
          description: '机械臂二关节'
        },

        // 机械臂三关节匹配规则
        {
          keywords: ['三关节' ],
          componentType: 'robotArm',
          partType: 'joint3',
          state: 'default',
          description: '机械臂三关节'
        },

        // 机械臂四关节匹配规则
        {
          keywords: ['四关节', '机械臂四关节', 'joint4', 'Joint4', 'robot_arm_joint4', '关节4', '末端执行器'],
          componentType: 'robotArm',
          partType: 'joint4',
          state: 'default',
          description: '机械臂四关节'
        },

        // 提升机框架匹配规则
        {
          keywords: ['提升机框架' ],
          componentType: 'elevator',
          partType: 'frame',
          state: 'default',
          description: '提升机框架'
        },

        // 提升机亚克力罩匹配规则
        {
          keywords: ['提升机亚克力罩' ],
          componentType: 'elevator',
          partType: 'acrylicCover',
          state: 'default',
          description: '提升机亚克力罩'
        },

        // 视觉框架匹配规则
        {
          keywords: ['视觉框架' ],
          componentType: 'vision',
          partType: 'frame',
          state: 'default',
          description: '视觉框架'
        },

        // 视觉罩子匹配规则
        {
          keywords: ['视觉罩子' ],
          componentType: 'vision',
          partType: 'cover',
          state: 'default',
          description: '视觉罩子'
        },

        // AGV车体匹配规则
        {
          keywords: [ 'AGV车体' ],
          componentType: 'agv',
          partType: 'body',
          state: 'default',
          description: 'AGV车体'
        },

        // AGV上装框架匹配规则
        {
          keywords: ['AGV上装框架', 'agv_mount_frame', 'AgvMountFrame'],
          componentType: 'agv',
          partType: 'mountFrame',
          state: 'default',
          description: 'AGV上装框架'
        },

        // AGV上装滚筒匹配规则
        {
          keywords: ['AGV上装滚筒', 'agv_mount_roller', 'AgvMountRoller'],
          componentType: 'agv',
          partType: 'mountRoller',
          state: 'default',
          description: 'AGV上装滚筒'
        },
      ];

      let appliedCount = 0;
      const appliedObjects = [];

      // 遍历场景中的所有对象
      scene.traverse((object) => {
        if (!object.name) return; // 跳过无名称对象

        // 对每个对象检查所有匹配规则
        for (const rule of matchRules) {
          // 检查对象名称是否包含任何关键字
          const matched = rule.keywords.some(keyword => object.name.includes(keyword));

          if (matched) {
            // 避免重复应用
            if (appliedObjects.includes(object.name)) {
              break;
            }

            materialManager.applyMaterialToObject(
              object,
              rule.componentType,
              rule.partType,
              rule.state
            );
            appliedCount++;
            appliedObjects.push(object.name);
            console.log(`🎯 智能匹配: ${object.name} → [${rule.description}]`);
            break; // 匹配到一个规则后跳出
          }
        }
      });

      console.log(`✅ 材质应用完成，成功应用 ${appliedCount} 个材质配置`);
      console.log('========================================');
    },
    init() {
      var that = this;
    var dom = document.getElementById("3d");
    this.ThreeEngine = new ThreeEngine(dom);

    // 配置 TEngine 已创建的 renderer
    this.ThreeEngine.renderer.setSize(dom.clientWidth, dom.clientHeight);
    this.ThreeEngine.renderer.shadowMap.enabled = true; // 启用阴影
    this.ThreeEngine.renderer.shadowMap.type = THREE.PCFSoftShadowMap; // 柔和阴影
    this.ThreeEngine.renderer.toneMapping = THREE.ACESFilmicToneMapping; // 色调映射
    this.ThreeEngine.renderer.toneMappingExposure = 1.0; // 曝光度

    // 优化相机参数
    this.ThreeEngine.camera.fov = 35; // FOV: 35度，进一步减弱透视效果（原50度）
    this.ThreeEngine.camera.near = 0.5; // near: 0.5
    this.ThreeEngine.camera.far = 2000; // far: 2000，确保大场景不被裁剪
    this.ThreeEngine.camera.aspect = dom.clientWidth / dom.clientHeight;
    this.ThreeEngine.camera.updateProjectionMatrix(); // 更新投影矩阵



    // 重新配置控制器（相机参数已更新）
    this.ThreeEngine.reconfigureControls();

    console.log('✅ Camera and Renderer configured');
    console.log('✅ OrbitControls ready:', this.ThreeEngine.controls);

    // Add CSS3D renderer
    // const cssRenderer = new CSS3DRenderer();
    // cssRenderer.setSize(dom.clientWidth, dom.clientHeight);
    // cssRenderer.domElement.style.position = 'absolute';
    // cssRenderer.domElement.style.top = '0';
    // dom.appendChild(cssRenderer.domElement);
    // this.ThreeEngine.cssRenderer = cssRenderer;


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
      loader.load("/glb/aaa.glb", function (glb) {

        // 输出GLB模型结构层级
        console.log('========== GLB模型结构层级 ==========');
        that.logGLBStructure(glb.scene, 0);
        console.log('====================================');

        // 禁用glb模型中的所有灯光
        glb.scene.traverse((object) => {
            if (object.isLight) {
              // 直接禁用模型中的灯光
              object.intensity = 0;
              console.log('已禁用模型灯光:', object.type);
            }
        });

        that.ThreeEngine.addObject(glb.scene);

        // 应用材质配置到GLB模型
         that.applyMaterialsToGLB(glb.scene);

        // 强制修复所有对象的双面渲染（解决消失问题）
        that.fixDoubleSideRendering(glb.scene);

        // 添加自定义灯光配置
        that.addCustomLights();
        that.addStorageMachineLabels();
        that.join();
       // that.agvTaskRun(0);
        var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
        pallet_agv.visible = false;
        that.createSprite("agv", "agv" );
        var agv = that.ThreeEngine.getObjectByName("agv");
        console.info(agv);
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
            // percentDiv.style.width = percent * 400 + "px"; //进度条元素长度
            // percentDiv.style.textIndent = percent * 400 + 5 + "px"; //缩进元素中的首行文本
            // // Math.floor:小数加载进度取整
            // percentDiv.innerHTML = Math.floor(percent * 100) + '%'; //进度百分比
        }

    );

      // 创建一个时钟对象Clock
      const clock = new THREE.Clock();
      function render() {
          TWEEN.update();
          that.requestId = requestAnimationFrame(render);

          // 更新轨道控制器（必须在每帧调用）
          if (that.ThreeEngine.controls) {
              that.ThreeEngine.controls.update();
          }

          if (mixer !== null) {
              mixer.update(clock.getDelta());
          }
          if(that.lift1texture!=undefined){
              that.lift1texture.offset.x -= 0.04;
          }
          if(that.texture!=undefined){
              that.texture.offset.x -= 0.01; // 改为原来的50%
          }

          // 渲染场景（关键：必须调用render才能看到画面）
          that.ThreeEngine.renderer.render(that.ThreeEngine.scene, that.ThreeEngine.camera);
      }
      render();
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
            var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
            pallet_agv.visible = false;
            that.agvTaskRun(0);
          }
          if (data.fromNode == "C01" && data.toNode == "R01") {
            var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
            pallet_agv.visible = true;
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
          that.remove("ababababab");
        }

        if (type == 5) {
          var pallet_agv = that.ThreeEngine.getObjectByName("pallet_agv");
          pallet_agv.visible = false;
          that.remove("ababababab");
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
      var agvRouter=this.agvRouter;
      AgvCar.tweens=[];
      that.agvTweenStop();
      if(agvRouter!=null && agvRouter.length>0){
        var position = agvRouter[0];
         for(var i=0;i<agvRouter.length;i++){

          var ele = agvRouter[i];
            if(i==0){
              AgvCar.position.set(position.x, position.y, position.z);
              AgvCar.lookAt(agvRouter[1].x,agvRouter[1].y,agvRouter[1].z);
            }
            else {

              AgvCar["tween"+i]=new TWEEN.Tween(AgvCar.position);
              that.agvLookAt(AgvCar,AgvCar["tween"+i],i);
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
      var time = distance*450;
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

    //  AGV移动动画（停用）
    // moveAgvCar2() {
    //   var that = this;
    //   const AgvCar = this.ThreeEngine.getObjectByName("agv");
    //   var curve;

    //   curve = this.Route(this.agvRouter);

    //   if (that.agvTask.isStart == 0) {
    //     this.agvtime = 0;
    //     that.agvTask.isStart = 1;
    //   }
    //   if (that.agvTask.isStart == 2) {
    //     return;
    //   }
    //   if (curve && curve.points.length > 0 && AgvCar) {
    //     this.agvtime = this.agvtime + 1;
    //     //把路径拆分为500份
    //     var agv_sudu = this.agv_sudu * this.agvRouter.length;
    //     let points = curve.getPoints(agv_sudu);
    //     let point = points[this.agvtime];
    //     let point1 = points[this.agvtime + 1];
    //     if (this.agvtime >= agv_sudu) {
    //       that.agvTask.isStart = 2;
    //       return;
    //       this.agvtime = 0;
    //     }

    //     if (point && point.x) {
    //       AgvCar.position.set(point.x, point.y, point.z);
    //     }
    //     if (point1 && point1.x) {
    //       AgvCar.lookAt(point1.x, point.y, point1.z);
    //     }
    //   }
    //   // 关键一步，循环改变贴图的位置，css的属性
    //   that.texture.offset.x -= 0.02;
    // },

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
      const zht = this.ThreeEngine.getObjectByName("载货台");
      const hc = this.ThreeEngine.getObjectByName("上货叉");
      const hc2 = this.ThreeEngine.getObjectByName("下货叉");

      // 🔥 切换堆垛机各部件材质为工作状态
      materialManager.applyMaterialToObject(mesh, 'stacker', 'body', 'working');
      materialManager.applyMaterialToObject(zht, 'stacker', 'platform', 'working');
      materialManager.applyMaterialToObject(hc, 'stacker', 'upperFork', 'working');
      materialManager.applyMaterialToObject(hc2, 'stacker', 'lowerFork', 'working');

      y = y - mesh.position.y;
      if (mesh.tweenA != undefined && mesh.tweenA != null) {
        mesh.tweenA.stop();
      }

      mesh.tweenA = new TWEEN.Tween(mesh.position);

      var timeA = 100 * (mesh.position.x - x);
      timeA = Math.abs(timeA);
      mesh.tweenA.to({ x: x }, timeA);

      if (zht.tweenB != undefined && zht.tweenB != null) {
        zht.tweenB.stop();
      }
      zht.tweenB = new TWEEN.Tween(zht.position);
      var timeB = 200 * (zht.position.y - y);
      timeB = Math.abs(timeB);
      zht.tweenB.to({ y: y - 0.4 * type }, timeB);

      zht.tweenC = new TWEEN.Tween(zht.position);

      zht.tweenC.to({ y: y + 0.4 * type }, 500);

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
      if (cellCode == "0-0-1" || cellCode == "0-0-2") {
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

      hc.tweenH.onComplete(function() {
        // ✅ 任务完成，切换堆垛机所有部件材质回默认状态
        materialManager.applyMaterialToObject(mesh, 'stacker', 'body', 'default');
        materialManager.applyMaterialToObject(zht, 'stacker', 'platform', 'default');
        materialManager.applyMaterialToObject(hc, 'stacker', 'upperFork', 'default');
        materialManager.applyMaterialToObject(hc2, 'stacker', 'lowerFork', 'default');
        console.log('✅ 堆垛机任务完成，材质已恢复为默认状态');

        // 执行回调函数
        if (aaa) {
          aaa();
        }
      });
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

    /**
     * 移动货物到指定库位坐标
     */
    moveCargoToPosition() {
      var that = this;
      const cellCode = that.positionCellCode;

      // 验证输入格式
      if (!cellCode || !/^\d+-\d+-\d+$/.test(cellCode)) {
        alert('请输入正确格式的库位坐标，如：1-1-1');
        return;
      }

      try {
        // 查找目标位置
        const targetCell = that.ThreeEngine.getObjectByName(cellCode);
        if (!targetCell) {
          alert(`未找到库位坐标：${cellCode}`);
          return;
        }

        // 使用现有的pallet方法创建货物并移动到目标位置
        const cargo = that.pallet('manualCargo', targetCell);
        console.log(`✅ 货物已成功移动到库位坐标：${cellCode}`);
        console.log(`📌 位置信息：x=${targetCell.position.x}, y=${targetCell.position.y}, z=${targetCell.position.z}`);

      } catch (error) {
        console.error('❌ 移动货物失败：', error);
        alert('移动货物失败，请查看控制台日志');
      }
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
  width: 100vw;
  height: 100vh;
  // background-color: #041e0b;
  //background-color: rgba(0, 0, 0, 0.8);

  background-image: url("/img/999.jpg"); /* 替换 'image-path.jpg' 为你的图片路径 */
  background-size: cover; /* 背景图片覆盖整个元素 */
  background-repeat: no-repeat; /* 背景图片不重复 */
  background-position: center; /* 背景图片居中 */
}

// 控制面板样式
.control-panel {
  position: absolute;
  z-index: 50;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  min-width: 300px;
  max-width: 400px;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 8px;
  backdrop-filter: blur(3px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  overflow: visible;
}

.panel-top {
  top: 15%;
  right: 20px;
}

.panel-middle {
  top: 27%;
  right: 20px;
}

.panel-bottom {
  top: 39%;
  right: 20px;
}

// 库位坐标输入控制面板样式
.panel-position {
  top: 51%;
  right: 20px;
}

// 自定义输入框样式
.custom-input {
  width: 100px;
  padding: 8px 12px;
  background: rgba(30, 30, 30, 0.8);
  border: 1px solid rgba(100, 150, 255, 0.5);
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;

  &:focus {
    border-color: #4A90E2;
    box-shadow: 0 0 8px rgba(74, 144, 226, 0.5);
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

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  }

  &:active {
    transform: translateY(0);
  }

  &:focus {
    outline: 2px solid rgba(255, 255, 255, 0.3);
  }

  // 按钮波纹效果
  &::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5px;
    height: 5px;
    background: rgba(255, 255, 255, 0.5);
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
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  color: white;
}

.custom-btn.secondary {
  background: linear-gradient(135deg, #50C878, #3A975A);
  color: white;
}

.custom-btn.success {
  background: linear-gradient(135deg, #00C9A7, #009B77);
  color: white;
}

.custom-btn.warning {
  background: linear-gradient(135deg, #FFA500, #CC8400);
  color: white;
}

.custom-btn.danger {
  background: linear-gradient(135deg, #FF4757, #C93A40);
  color: white;
}

// 波纹动画
@keyframes ripple {
  0% {
    transform: scale(0, 0);
    opacity: 0.5;
  }
  100% {
    transform: scale(50, 50);
    opacity: 0;
  }
}

.progress-box{
  border-radius:5vh;
  height: 25vh;
  background-color: rgba(0, 0, 0, 0.5);
  width: 30vw;
  margin-left: 30vw;
  margin-top: 30vh;
  position: absolute;
  z-index:999;
  .name{
    color: #fff;
    width: 100%;
    text-align: center;
    margin-top: 5vh;
    margin-bottom: 5vh;
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
  box-shadow: 0 0 5px #2d69a1;
  padding: 10px;
  background: #2d69a154;
}

.workshop-text p {
  font-size: 0.3rem;
  font-weight: bold;
  padding: 10px;
  color: #fff;
}
.workshop-text.IDLE {
  background: #ecd272 !important;
}

.workshop-text.RUNNING {
  background: #86efd4 !important;
}

.workshop-text.OFFLINE {
  background: #8ab1de !important;
}

.equipment-label {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    min-width: 200px;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(255,255,255,0.2);
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
    box-shadow: 0 0 8px rgba(46, 204, 113, 0.4);
}

</style>
