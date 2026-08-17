// 引入three.js
import * as THREE from "three";
import { WebGLRenderer, Scene, PerspectiveCamera, Vector3, MOUSE } from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls";
import { GUI } from "dat.gui";

export class ThreeEngine {
  dom = null; // 挂载的 DOM
  scene = null; // 场景
  camera = null; // 相机
  renderer = null; // 渲染器
  controls = null; // 轨道控制器
  raycaster = null; // 射线检测器
  mouse = null; // 鼠标位置

  constructor(dom) {
    // 创建渲染器 - 关闭不必要功能以提升性能
    let renderer = new WebGLRenderer({
      antialias: true, // 关闭抗锯齿以提升性能
      alpha: true,    // 关闭透明通道以减少渲染开销
      powerPreference: "high-performance" // 优先使用高性能模式
    });
    //renderer.outputEncoding = THREE.sRGBEncoding;

    // 禁用阴影渲染以大幅提升性能（如果不需要阴影效果）
    renderer.shadowMap.enabled = true; // 关闭阴影渲染
    // renderer.shadowMap.type = THREE.PCFSoftShadowMap;

    dom.appendChild(renderer.domElement); // 将渲染器挂载到dom
    renderer.setSize(dom.offsetWidth, dom.offsetHeight, true);
    let scene = new Scene(); // 实例化场景

    // 实例化相机（优化为小场景）
    let camera = new PerspectiveCamera(
      35,      // 视场角FOV：35度减弱透视效果，更接近建筑制图风格
      dom.offsetWidth / dom.offsetHeight,
      0.1,     // 近裁剪面：0.1可以看到很近的物体
      1000     // 远裁剪面：1000确保能看到整个场景
    );
    camera.position.set(-15, 12, 12); // 设置相机位置（小场景）
    camera.lookAt(new Vector3(-10, 0, 0)); // 看向中心点
    this.dom = dom;
    this.scene = scene;
    this.camera = camera; // 暴露摄像头
    this.renderer = renderer; // 修复渲染器引用

    // 初始化射线检测器和鼠标位置
    this.raycaster = new THREE.Raycaster();
    this.mouse = new THREE.Vector2();
    
    // 已取消点击事件

    // const gui = new GUI();
    // //改变交互界面style属性
    // gui.domElement.style.right = "0px";
    // gui.domElement.style.width = "300px";

    // gui.add(camera.position, "x", -2000, 2000);
    // gui.add(camera.position, "y", -2000, 2000);
    // gui.add(camera.position, "z", -200, 2000);

    let orbitControls = new OrbitControls(camera, renderer.domElement);
    orbitControls.mouseButtons = {
      // 设置鼠标功能键（轨道控制器）
      LEFT: 2, // 左键无功能
      MIDDLE: MOUSE.DOLLY, // 中键缩放
      RIGHT: MOUSE.ROTATE, // 右键旋转
    };

    // 限制旋转角度，防止看到底部
    orbitControls.minPolarAngle = Math.PI / 6;      // 最小角度30度（俯视角度限制）
    orbitControls.maxPolarAngle = Math.PI / 2.2;   // 最大角度约82度（防止看到底部）

    // 可选：限制水平旋转范围（如果需要的话）
    // orbitControls.minAzimuthAngle = -Math.PI / 2; // 最小水平角度
    // orbitControls.maxAzimuthAngle = Math.PI / 2;  // 最大水平角度

    // 其他有用的控制器配置
    orbitControls.enableDamping = false;            // 启用阻尼（惯性）
    orbitControls.dampingFactor = 0.05;            // 阻尼系数
    orbitControls.minDistance = 5;               // 最小缩放距离
    orbitControls.maxDistance = 2000;              // 最大缩放距离
    orbitControls.enablePan = true;                // 启用平移
    orbitControls.panSpeed = 1.0;                  // 平移速度
    orbitControls.rotateSpeed = 0.5;               // 旋转速度

    // 保存轨道控制器引用
    this.controls = orbitControls;

    // 逐帧渲染threejs
    let animate = () => {
      // 更新 TWEEN 动画系统
      if (typeof TWEEN !== 'undefined' && TWEEN.update) {
        TWEEN.update();
      }
      // 更新控制器
      if (orbitControls.enabled) {
        orbitControls.update();
      }
      renderer.render(scene, this.camera); // 渲染场景
      requestAnimationFrame(animate);
    };
    animate();

    window.onresize = () => {
      renderer.setSize(window.innerWidth, window.innerHeight);
      // 相机宽高比
      this.camera.aspect = window.innerWidth / window.innerHeight;
      // 更新摄像机投影矩阵
      this.camera.updateProjectionMatrix();
    };
  }

  /**
   * 绑定点击事件（已禁用）
   */
  bindClickEvent() {
    // 已禁用鼠标点击事件
    }

  /**
   * 对象点击事件处理函数（可被重写）
   * @param {Object} intersect 射线检测到的交点信息
   */
  onObjectClick(intersect) {
    // 物体点击事件处理，可被外部重写
    const obj = intersect.object;
  }

  /**
   * 向场景中添加模型
   * @param  {...any} object 模型列表
   */
  addObjects(...object) {
    object.forEach((elem) => {
      this.scene.add(elem); // 场景添加模型
    });
  }

  /**
   * 向场景中添加模型
   * @param   object 模型列表
   */
  addObject(object) {
    this.scene.add(object); // 场景添加模型
  }

  removeByName(name) {
    var obj = this.getObjectByName(name);
    if (obj != null) {
      this.scene.remove(obj); //
    }
  }
  remove(obj) {
    this.scene.remove(obj); // 场景添加模型
  }
  /**
   * 向场景中添加模型
   * @param   object 模型列表
   */
  getObjectByName(name) {
    return this.scene.getObjectByName(name);
  }

  clearScene() {
    //cancelAnimationFrame(this.animationId);
    this.scene.traverse((child) => {
      if (child.material) {
        child.material.dispose();
      }
      if (child.geometry) {
        child.geometry.dispose();
      }
      child = null;
    });
    this.dom.innerHTML = "";
    // this.renderer.forceContextLoss();
    // this.renderer.dispose();
    this.scene.clear();
    this.flows = [];
    this.scene = null;
    this.camera = null;
    this.controls = null;
    this.renderer.domElement = null;
    this.renderer = null;
    this.dom = null;
    }
}
