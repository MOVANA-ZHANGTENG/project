/**
 * 炮弹系统 - 支持发射、物理、爆炸效果
 */

import * as THREE from "three";

/**
 * 炮弹类
 */
class Projectile {
  constructor(scene, camera, startPosition, direction, speed = 30) {
    this.scene = scene;
    this.camera = camera; // 保存camera引用，用于Raycaster
    this.active = true;
    this.lifetime = 15; // 最大生存时间（秒）
    this.age = 0;
    
    // 物理参数（针对小场景优化 - 1/10尺寸）
    this.velocity = direction.clone().multiplyScalar(speed);
    this.gravity = new THREE.Vector3(0, -1.5, 0); // 适配小场景的重力
    
    // 创建炮弹模型
    this.createProjectileMesh(startPosition);
    
    // 轨迹线
    this.trail = [];
    this.maxTrailLength = 20;
  }
  
  /**
   * 创建炮弹网格
   */
  createProjectileMesh(position) {
    // 炮弹几何体（适配小场景）
    const geometry = new THREE.SphereGeometry(0.05, 8, 8);  // 再次减小炮弹大小
    
    // 炮弹材质（发光的橙红色）
    const material = new THREE.MeshPhongMaterial({
      color: new THREE.Color(1.0, 0.3, 0.0), // 橙红色
      emissive: new THREE.Color(1.0, 0.5, 0.0), // 强烈的橙色自发光
      emissiveIntensity: 2.0,
      shininess: 100,
    });
    
    this.mesh = new THREE.Mesh(geometry, material);
    this.mesh.position.copy(position);
    this.mesh.castShadow = true;
    
    this.scene.add(this.mesh);
    
    // 添加点光源（炮弹发光效果，适配小场景）
    this.light = new THREE.PointLight(0xff6600, 2, 5);  // 适配小场景的光源范围
    this.light.position.copy(position);
    this.scene.add(this.light);
  }
  
  /**
   * 更新炮弹位置（物理模拟）
   */
  update(delta) {
    if (!this.active) return false;
    
    this.age += delta;
    
    // 检查生存时间
    if (this.age > this.lifetime) {
      this.explode();
      return false;
    }
    
    // 记录旧位置（用于碰撞检测）
    const oldPosition = this.mesh.position.clone();
    
    // 应用重力
    this.velocity.add(this.gravity.clone().multiplyScalar(delta));
    
    // 计算移动向量
    const movement = this.velocity.clone().multiplyScalar(delta);
    
    // 碰撞检测（射线检测）
    const hasCollision = this.checkCollision(oldPosition, movement);
    if (hasCollision) {
      console.log('💥 炮弹碰撞到物体！');
      this.explode();
      return false;
    }
    
    // 更新位置
    this.mesh.position.add(movement);
    this.light.position.copy(this.mesh.position);
    
    // 检查是否落到地面（y < -1，给予一定容差）
    if (this.mesh.position.y < -1) {
      console.log('💥 炮弹落地！');
      this.explode();
      return false;
    }
    
    return true;
  }
  
  /**
   * 碰撞检测
   */
  checkCollision(startPos, movement) {
    // 创建射线检测器
    const raycaster = new THREE.Raycaster();
    
    // 设置camera（用于Sprite检测）
    raycaster.camera = this.camera;
    
    // 设置射线起点和方向
    const direction = movement.clone().normalize();
    raycaster.set(startPos, direction);
    
    // 设置检测距离（移动距离）
    raycaster.far = movement.length();
    
    // 检测所有场景中的物体
    const intersects = raycaster.intersectObjects(this.scene.children, true);
    
    // 过滤掉炮弹自己、光源、UI元素等
    for (let i = 0; i < intersects.length; i++) {
      const obj = intersects[i].object;
      
      // null检查
      if (!obj) continue;
      
      // 检查是否是UI元素（递归检查父级）
      let isUIElement = false;
      let currentObj = obj;
      
      // 向上遍历父级，检查是否是UI元素
      while (currentObj) {
        if (currentObj === this.mesh || 
            currentObj === this.light ||
            currentObj.type === 'Light' ||
            currentObj.type === 'Sprite' ||
            currentObj.isSprite === true ||
            currentObj.name?.includes('sprite') ||
            currentObj.name?.includes('Sprite') ||
            currentObj.userData?.isUI === true) {
          isUIElement = true;
          break;
        }
        currentObj = currentObj.parent;
      }
      
      // 如果是UI元素，跳过
      if (isUIElement) {
        continue;
      }
      
      // 检测到碰撞
      if (intersects[i].distance < movement.length()) {
        console.log('🎯 命中目标:', obj.name || '未命名对象', '距离:', intersects[i].distance.toFixed(2));
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * 爆炸效果
   */
  explode() {
    if (!this.active) return;
    this.active = false;
    
    console.log('💥 炮弹爆炸于:', this.mesh.position);
    
    // 创建爆炸粒子效果
    this.createExplosion();
    
    // 移除炮弹
    this.scene.remove(this.mesh);
    this.scene.remove(this.light);
  }
  
  /**
   * 创建爆炸效果（针对小场景优化 - 1/10尺寸）
   */
  createExplosion() {
    const explosionPosition = this.mesh.position.clone();
    
    // 创建多个爆炸粒子
    const particleCount = 40;
    const particles = [];
    
    for (let i = 0; i < particleCount; i++) {
      // 粒子几何体（适配小场景）
      const particleGeometry = new THREE.SphereGeometry(0.05, 6, 6);  // 减小爆炸粒子大小
      const particleMaterial = new THREE.MeshBasicMaterial({
        color: new THREE.Color(1.0, Math.random() * 0.5, 0.0),
        transparent: true,
        opacity: 1.0,
      });
      
      const particle = new THREE.Mesh(particleGeometry, particleMaterial);
      particle.position.copy(explosionPosition);
      
      // 随机速度（适配小场景）
      const angle = Math.random() * Math.PI * 2;
      const elevation = Math.random() * Math.PI - Math.PI / 2;
      const speed = 2 + Math.random() * 5;  // 小场景飞散速度
      
      particle.velocity = new THREE.Vector3(
        Math.cos(angle) * Math.cos(elevation) * speed,
        Math.sin(elevation) * speed,
        Math.sin(angle) * Math.cos(elevation) * speed
      );
      
      particle.lifetime = 1.2 + Math.random() * 0.8;
      particle.age = 0;
      
      this.scene.add(particle);
      particles.push(particle);
    }
    
    // 爆炸光效（适配小场景）
    const explosionLight = new THREE.PointLight(0xff6600, 10, 15);  // 适配小场景的光源
    explosionLight.position.copy(explosionPosition);
    this.scene.add(explosionLight);
    
    // 爆炸球体（冲击波，适配小场景）
    const shockwaveGeometry = new THREE.SphereGeometry(0.2, 16, 16);  // 减小冲击波初始大小
    const shockwaveMaterial = new THREE.MeshBasicMaterial({
      color: 0xff6600,
      transparent: true,
      opacity: 0.7,
      wireframe: false,
    });
    const shockwave = new THREE.Mesh(shockwaveGeometry, shockwaveMaterial);
    shockwave.position.copy(explosionPosition);
    this.scene.add(shockwave);
    
    // 爆炸动画
    const animationDuration = 1500; // 1.5秒
    const startTime = Date.now();
    
    const animateExplosion = () => {
      const elapsed = Date.now() - startTime;
      const progress = elapsed / animationDuration;
      
      if (progress >= 1.0) {
        // 清理爆炸效果
        particles.forEach(p => this.scene.remove(p));
        this.scene.remove(explosionLight);
        this.scene.remove(shockwave);
        return;
      }
      
      // 更新粒子
      particles.forEach((particle, index) => {
        if (!particle) return;
        
        particle.age += 0.016;
        if (particle.age > particle.lifetime) {
          this.scene.remove(particle);
          particles[index] = null;
          return;
        }
        
        // 应用重力（适配小场景）
        particle.velocity.y -= 1.5 * 0.016;  // 匹配炮弹重力
        
        // 更新位置
        particle.position.add(particle.velocity.clone().multiplyScalar(0.016));
        
        // 淡出
        particle.material.opacity = 1.0 - (particle.age / particle.lifetime);
      });
      
      // 更新冲击波（适配小场景）
      const scale = 1 + progress * 10;  // 适配小场景的扩散范围
      shockwave.scale.set(scale, scale, scale);
      shockwave.material.opacity = 0.7 * (1 - progress);
      
      // 更新爆炸光
      explosionLight.intensity = 10 * (1 - progress);
      
      requestAnimationFrame(animateExplosion);
    };
    
    animateExplosion();
  }
  
  /**
   * 清理资源
   */
  dispose() {
    if (this.mesh) {
      this.scene.remove(this.mesh);
      this.mesh.geometry.dispose();
      this.mesh.material.dispose();
    }
    if (this.light) {
      this.scene.remove(this.light);
    }
  }
}

/**
 * 炮弹系统管理器
 */
export class ProjectileSystem {
  constructor(scene, camera) {
    this.scene = scene;
    this.camera = camera;
    this.projectiles = [];
    this.enabled = false;
    
    // 发射音效（可选，需要音频文件）
    this.canFire = true;
    this.fireRate = 0.3; // 发射间隔（秒），适当降低射速
  }
  
  /**
   * 发射炮弹
   */
  fire() {
    if (!this.enabled || !this.canFire) return;
    
    // 发射冷却
    this.canFire = false;
    setTimeout(() => {
      this.canFire = true;
    }, this.fireRate * 1000);
    
    // 获取相机位置和朝向
    const startPosition = this.camera.position.clone();
    const direction = new THREE.Vector3(0, 0, -1);
    direction.applyQuaternion(this.camera.quaternion);
    direction.normalize();
    
    // 创建炮弹（传递camera参数）
    const projectile = new Projectile(this.scene, this.camera, startPosition, direction);
    this.projectiles.push(projectile);
    
    console.log('🚀 发射炮弹！位置:', startPosition, '方向:', direction);
    
    // 清理已销毁的炮弹
    this.cleanup();
  }
  
  /**
   * 更新所有炮弹
   */
  update(delta) {
    if (!this.enabled) return;
    
    this.projectiles = this.projectiles.filter(projectile => {
      return projectile.update(delta);
    });
  }
  
  /**
   * 清理已销毁的炮弹
   */
  cleanup() {
    this.projectiles = this.projectiles.filter(p => p.active);
  }
  
  /**
   * 启用炮弹系统
   */
  enable() {
    this.enabled = true;
    console.log('🎯 炮弹系统已启用');
    console.log('- 点击鼠标左键发射炮弹');
    console.log('- 炮弹会受重力影响下落');
    console.log('- 落地或超时会爆炸');
  }
  
  /**
   * 禁用炮弹系统
   */
  disable() {
    this.enabled = false;
    // 清理所有炮弹
    this.projectiles.forEach(p => p.dispose());
    this.projectiles = [];
    console.log('🎯 炮弹系统已禁用');
  }
  
  /**
   * 清理所有资源
   */
  dispose() {
    this.projectiles.forEach(p => p.dispose());
    this.projectiles = [];
  }
}

export default ProjectileSystem;

