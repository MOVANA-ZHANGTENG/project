/**
 * 第一人称控制器
 * 支持WASD和方向键控制
 */

import * as THREE from "three";

export class FirstPersonController {
  constructor(camera, scene, domElement, onLockStateChange = null) {
    this.camera = camera;
    this.scene = scene;
    this.domElement = domElement;
    this.onLockStateChange = onLockStateChange; // 锁定状态变化回调
    
    // 移动状态
    this.moveState = {
      forward: false,
      backward: false,
      left: false,
      right: false,
      up: false,      // Space
      down: false,    // Shift
    };
    
    // 无人机飞行参数（针对小场景优化 - 1/10尺寸）
    this.moveSpeed = 0.1;          // 飞行速度，适合小场景
    this.lookSpeed = 0.15;          // 视角旋转速度
    this.height = 5;                // 无人机飞行高度（降低10倍）
    this.enabled = false;           // 是否启用控制
    this.droneMode = true;          // 无人机模式开关
    
    // 鼠标位置（归一化到-1到1）
    this.mouseX = 0;
    this.mouseY = 0;
    this.targetRotationX = 0;
    this.targetRotationY = 0;
    this.currentRotationX = 0;
    this.currentRotationY = 0;
    
    // 旋转平滑参数
    this.rotationSmoothing = 0.15;  // 平滑系数
    
    // 鼠标控制灵敏度
    this.mouseSensitivity = 0.3;    // 适中的灵敏度
    
    // 旋转角度限制
    this.maxPitchAngle = Math.PI / 2;   // 最大俯仰角度（90度，允许完全抬头/低头）
    this.maxRollAngle = Math.PI / 4;    // 最大滚转角度（45度，增大左右转向范围）
    
    // 旋转角度
    this.euler = new THREE.Euler(0, 0, 0, 'YXZ');
    
    // 速度向量
    this.velocity = new THREE.Vector3();
    this.direction = new THREE.Vector3();
    
    // 自动前进（改为手动控制）
    this.autoForward = false;
    
    // 绑定事件
    this.bindEvents();
    
    // 保存初始位置
    this.initialPosition = this.camera.position.clone();
    this.initialRotation = this.camera.rotation.clone();
  }
  
  /**
   * 绑定指针锁定事件（移除键盘事件）
   */
  bindEvents() {
    this.onPointerLockChange = this.onPointerLockChange.bind(this);
    this.onPointerLockError = this.onPointerLockError.bind(this);
    
    document.addEventListener('pointerlockchange', this.onPointerLockChange);
    document.addEventListener('pointerlockerror', this.onPointerLockError);
  }
  
  /**
   * 移除事件监听（移除键盘事件相关）
   */
  dispose() {
    document.removeEventListener('pointerlockchange', this.onPointerLockChange);
    document.removeEventListener('pointerlockerror', this.onPointerLockError);
  }
  
  /**
   * 键盘按下事件
   */
  onKeyDown(event) {
    if (!this.enabled) {
      console.warn('⚠️ 键盘事件触发但控制器未启用');
      return;
    }
    
    switch (event.code) {
      case 'ArrowUp':
      case 'KeyW':
        this.moveState.forward = true;
        break;
      case 'ArrowDown':
      case 'KeyS':
        this.moveState.backward = true;
        break;
      case 'ArrowLeft':
      case 'KeyA':
        this.moveState.left = true;
        break;
      case 'ArrowRight':
      case 'KeyD':
        this.moveState.right = true;
        break;
      case 'Space':
        this.moveState.up = true;
        break;
      case 'ShiftLeft':
      case 'ShiftRight':
        this.moveState.down = true;
        break;
    }
  }
  
  /**
   * 键盘抬起事件
   */
  onKeyUp(event) {
    if (!this.enabled) return;
    
    switch (event.code) {
      case 'ArrowUp':
      case 'KeyW':
        this.moveState.forward = false;
        break;
      case 'ArrowDown':
      case 'KeyS':
        this.moveState.backward = false;
        break;
      case 'ArrowLeft':
      case 'KeyA':
        this.moveState.left = false;
        break;
      case 'ArrowRight':
      case 'KeyD':
        this.moveState.right = false;
        break;
      case 'Space':
        this.moveState.up = false;
        break;
      case 'ShiftLeft':
      case 'ShiftRight':
        this.moveState.down = false;
        break;
    }
  }
  
  /**
   * 鼠标移动事件（指针锁定模式 - FPS风格）
   */
  onMouseMove(event) {
    if (!this.isLocked) {
      return;  // 只有在锁定状态下才响应
    }
    
    // 使用鼠标移动量（movementX/Y）而不是位置
    const movementX = event.movementX || 0;
    const movementY = event.movementY || 0;
    

    
    // 直接应用到相机旋转
    this.euler.setFromQuaternion(this.camera.quaternion);
    
    // 左右旋转（偏航）
    this.euler.y -= movementX * this.mouseSensitivity * 0.002;
    
    // 上下旋转（俯仰）
    this.euler.x -= movementY * this.mouseSensitivity * 0.002;
    
    // 限制俯仰角度，防止翻转
    this.euler.x = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, this.euler.x));
    
    // 应用旋转
    this.camera.quaternion.setFromEuler(this.euler);
  }
  
  /**
   * 指针锁定状态变化
   */
  onPointerLockChange() {
    
    if (document.pointerLockElement === this.domElement) {
      this.isLocked = true;
      
      // 通知Vue组件状态变化
      if (this.onLockStateChange) {
        this.onLockStateChange(true);
      }
    } else {
      this.isLocked = false;
      
      // 通知Vue组件状态变化
      if (this.onLockStateChange) {
        this.onLockStateChange(false);
      }
    }
  }
  
  /**
   * 指针锁定错误
   */
  onPointerLockError() {
    console.error('❌ 指针锁定失败');
  }
  
  /**
   * 请求指针锁定
   */
  lock() {
    if (!this.domElement) {
      console.error('❌ domElement不存在，无法锁定');
      return;
    }
    
    try {
      this.domElement.requestPointerLock();
    } catch (error) {
      console.error('❌ 请求指针锁定失败:', error);
    }
  }
  
  /**
   * 解锁指针
   */
  unlock() {
    document.exitPointerLock();
  }
  
  
  /**
   * 启用无人机控制（FPS指针锁定模式）
   */
  enable() {
    this.enabled = true;
    
    // 不改变相机位置，保持当前位置
    // 移除了：this.camera.position.y = this.height;
    
    // 重置状态
    this.isLocked = false;
    
    // 绑定鼠标移动事件到document（指针锁定后事件在document上）
    document.addEventListener('mousemove', this.onMouseMove);
    
    // 自动请求指针锁定
    setTimeout(() => {
      this.lock();
    }, 500);
  }
  
  /**
   * 禁用无人机控制
   */
  disable() {
    this.enabled = false;
    
    // 解锁指针
    this.unlock();
    
    // 移除鼠标移动事件监听
    document.removeEventListener('mousemove', this.onMouseMove);
    
    // 重置移动状态
    Object.keys(this.moveState).forEach(key => {
      this.moveState[key] = false;
    });
    
    // 重置状态
    this.isLocked = false;
    

  }
  
  /**
   * 更新位置（FPS指针锁定模式）
   */
  update(delta) {
    if (!this.enabled) {
      console.warn('⚠️ Update called but not enabled');
      return;
    }
    
    // FPS模式下，旋转已经在onMouseMove中直接处理
    // 这里只处理位移
    
    // 计算移动方向
    this.direction.set(0, 0, 0);
    
    // 自动前进（无人机默认行为）
    if (this.autoForward) {
      this.direction.z -= 1;
    }
    
    // 键盘辅助控制（相对于视角方向）
    if (this.moveState.forward) this.direction.z -= 1;   // 方向键↑ 或 W：前进
    if (this.moveState.backward) this.direction.z += 1;  // 方向键↓ 或 S：后退
    if (this.moveState.left) this.direction.x -= 1;      // 方向键← 或 A：向左平移
    if (this.moveState.right) this.direction.x += 1;     // 方向键→ 或 D：向右平移
    
    // 标准化方向向量
    if (this.direction.length() > 0) {
      this.direction.normalize();
    }
    
    // 计算速度
    this.velocity.x = this.direction.x * this.moveSpeed;
    this.velocity.z = this.direction.z * this.moveSpeed;
    this.velocity.y = 0;
    
    // 垂直移动
    if (this.moveState.up) this.velocity.y = this.moveSpeed;
    if (this.moveState.down) this.velocity.y = -this.moveSpeed;
    
    // 应用相机旋转到移动方向（相对于相机朝向）
    const moveVector = this.velocity.clone();
    moveVector.applyQuaternion(this.camera.quaternion);
    
    // 更新相机位置
    this.camera.position.add(moveVector);
    
    // 限制最小高度（无人机不能飞太低）
    if (this.camera.position.y < 0.5) {
      this.camera.position.y = 0.5;
    }
  }
  
  /**
   * 设置位置
   */
  setPosition(x, y, z) {
    this.camera.position.set(x, y || this.height, z);
  }
  
  /**
   * 重置到初始位置
   */
  reset() {
    this.camera.position.copy(this.initialPosition);
    this.camera.rotation.copy(this.initialRotation);
    this.euler.setFromQuaternion(this.camera.quaternion);
  }
  
  /**
   * 设置移动速度
   */
  setMoveSpeed(speed) {
    this.moveSpeed = speed;
  }
  
  /**
   * 设置视角旋转速度
   */
  setLookSpeed(speed) {
    this.lookSpeed = speed;
  }
  
  /**
   * 设置人物高度
   */
  setHeight(height) {
    this.height = height;
    if (this.enabled) {
      this.camera.position.y = height;
    }
  }
  
  /**
   * 获取当前位置
   */
  getPosition() {
    return this.camera.position.clone();
  }
  
  /**
   * 获取前方向量
   */
  getForwardVector() {
    const direction = new THREE.Vector3(0, 0, -1);
    direction.applyQuaternion(this.camera.quaternion);
    return direction;
  }
  
  /**
   * 切换自动前进
   */
  toggleAutoForward() {
    this.autoForward = !this.autoForward;
    return this.autoForward;
  }
  
  /**
   * 设置鼠标灵敏度
   */
  setMouseSensitivity(sensitivity) {
    this.mouseSensitivity = sensitivity;
  }
  
  /**
   * 设置旋转平滑度
   */
  setRotationSmoothing(smoothing) {
    this.rotationSmoothing = smoothing;
  }
}

export default FirstPersonController;

