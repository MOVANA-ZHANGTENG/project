// 引入three.js
import * as THREE from "three";
import { WebGLRenderer, Scene, PerspectiveCamera, Vector3, MOUSE } from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls";
import { GUI } from "dat.gui";
export class ThreeEngine {
  dom = null; // 挂载的 DOM
  scene = null; // 场景
  renderer = null; // 渲染器
  camera = null; // 相机
  controls = null; // 轨道控制器
  
  constructor(dom) {
    // 创建渲染器
    this.renderer = new WebGLRenderer({
      antialias: true, // 开启抗锯齿
      alpha: true,
    });
    //this.renderer.outputEncoding = THREE.sRGBEncoding;

    dom.appendChild(this.renderer.domElement); // 将渲染器挂载到dom
    this.renderer.setSize(dom.offsetWidth, dom.offsetHeight, true);
    this.scene = new Scene(); // 实例化场景

    // 实例化相机
    this.camera = new PerspectiveCamera(
      45,
      dom.offsetWidth / dom.offsetHeight,
      1,
      5000
    );
    this.camera.position.set(0, 30, 80); // 设置相机位置
      // this.camera.position.set(-200, 200, 500); // 设置相机位置
    this.camera.lookAt(new Vector3(0, 0, 0)); // 设置相机看先中心点
    //this.camera.up = new Vector3(0, 1, 0); // 设置相机自身方向
    this.dom = dom;
    // const gui = new GUI();
    // //改变交互界面style属性
    // gui.domElement.style.right = "0px";
    // gui.domElement.style.width = "300px";

    // gui.add(camera.position, "x", -2000, 2000);
    // gui.add(camera.position, "y", -2000, 2000);
    // gui.add(camera.position, "z", -200, 2000);

    // 创建轨道控制器并保存到this上
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.mouseButtons = {
      // 设置鼠标功能键（轨道控制器）
      LEFT: MOUSE.ROTATE, // 左键旋转
      MIDDLE: MOUSE.DOLLY, // 中键缩放
      RIGHT: MOUSE.PAN, // 右键平移
    };
    
    // 配置控制器参数
    this.controls.enableDamping = false; // 启用阻尼
    this.controls.dampingFactor = 0.05; // 阻尼系数
    this.controls.screenSpacePanning = false;
    this.controls.minDistance = 20;
    this.controls.maxDistance = 500;
    this.controls.maxPolarAngle = Math.PI / 2;
    
    // 启用自动旋转
    this.controls.autoRotate = true; // 开启自动旋转
    this.controls.autoRotateSpeed = 0.3; // 旋转速度（度/秒），正值顺时针，负值逆时针
    
    console.log('✅ TEngine initialized with OrbitControls');
    
    // 响应窗口大小变化
    const self = this;
    window.onresize = function () {
      self.renderer.setSize(window.innerWidth, window.innerHeight);
      // 相机宽高比
      self.camera.aspect = window.innerWidth / window.innerHeight;
      // 更新摄像机投影矩阵
      self.camera.updateProjectionMatrix();
    };
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

  /**
   * 重新配置控制器
   */
  reconfigureControls() {
    if (this.controls) {
      this.controls.dispose();
    }
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.mouseButtons = {
      LEFT: MOUSE.ROTATE, // 左键旋转
      MIDDLE: MOUSE.DOLLY, // 中键缩放
      RIGHT: MOUSE.PAN, // 右键平移
    };
    this.controls.enableDamping = true;
    this.controls.dampingFactor = 0.05;
    this.controls.screenSpacePanning = false;
    this.controls.minDistance = 20;
    this.controls.maxDistance = 500;
    this.controls.maxPolarAngle = Math.PI / 2;
    
    // 启用自动旋转
    this.controls.autoRotate = true;
    this.controls.autoRotateSpeed = 2;
    
    console.log('✅ OrbitControls reconfigured with auto-rotate');
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
    console.log("clearScene");
  }
}
