/**
 * 网格纹理生成器 - 用于生成带圆点的网格地板纹理
 */

import * as THREE from "three";

/**
 * 创建网格纹理（网格线 + 交点圆点）
 * @param {Object} options - 配置选项
 * @returns {THREE.CanvasTexture} Three.js纹理对象
 */
export function createGridTexture(options = {}) {
  // 默认配置
  const config = {
    // Canvas尺寸
    size: options.size || 512,
    
    // 网格配置
    gridSize: options.gridSize || 64,        // 网格大小（像素）
    lineWidth: options.lineWidth || 1,       // 网格线宽度
    lineColor: options.lineColor || 'rgba(0, 150, 255, 0.3)',  // 网格线颜色（淡蓝色）
    
    // 圆点配置
    dotRadius: options.dotRadius || 3,       // 圆点半径
    dotColor: options.dotColor || 'rgba(0, 180, 255, 0.8)',    // 圆点颜色（亮蓝色）
    dotGlow: options.dotGlow !== undefined ? options.dotGlow : true, // 是否发光
    
    // 背景配置
    backgroundColor: options.backgroundColor || 'rgba(2, 4, 10, 1.0)', // 深蓝黑色背景
  };
  
  // 创建Canvas
  const canvas = document.createElement('canvas');
  canvas.width = config.size;
  canvas.height = config.size;
  const ctx = canvas.getContext('2d');
  
  // 绘制背景
  ctx.fillStyle = config.backgroundColor;
  ctx.fillRect(0, 0, config.size, config.size);
  
  // 计算网格数量
  const gridCount = Math.floor(config.size / config.gridSize);
  
  // 绘制网格线
  ctx.strokeStyle = config.lineColor;
  ctx.lineWidth = config.lineWidth;
  ctx.beginPath();
  
  // 绘制垂直线
  for (let i = 0; i <= gridCount; i++) {
    const x = i * config.gridSize;
    ctx.moveTo(x, 0);
    ctx.lineTo(x, config.size);
  }
  
  // 绘制水平线
  for (let i = 0; i <= gridCount; i++) {
    const y = i * config.gridSize;
    ctx.moveTo(0, y);
    ctx.lineTo(config.size, y);
  }
  
  ctx.stroke();
  
  // 绘制网格交点的小圆
  for (let i = 0; i <= gridCount; i++) {
    for (let j = 0; j <= gridCount; j++) {
      const x = i * config.gridSize;
      const y = j * config.gridSize;
      
      // 如果启用发光效果
      if (config.dotGlow) {
        // 绘制发光外圈
        const gradient = ctx.createRadialGradient(x, y, 0, x, y, config.dotRadius * 2);
        gradient.addColorStop(0, config.dotColor);
        gradient.addColorStop(0.5, config.dotColor.replace(/[\d.]+\)$/g, '0.3)'));
        gradient.addColorStop(1, 'rgba(0, 180, 255, 0)');
        
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.arc(x, y, config.dotRadius * 2, 0, Math.PI * 2);
        ctx.fill();
      }
      
      // 绘制实心圆点
      ctx.fillStyle = config.dotColor;
      ctx.beginPath();
      ctx.arc(x, y, config.dotRadius, 0, Math.PI * 2);
      ctx.fill();
    }
  }
  
  // 创建Three.js纹理
  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.RepeatWrapping;
  texture.repeat.set(10, 10); // 重复10次，可以根据需要调整
  texture.needsUpdate = true;
  
  return texture;
}

/**
 * 创建简单网格纹理（只有网格线，无圆点）
 * @param {Object} options - 配置选项
 * @returns {THREE.CanvasTexture} Three.js纹理对象
 */
export function createSimpleGridTexture(options = {}) {
  const config = {
    size: options.size || 512,
    gridSize: options.gridSize || 64,
    lineWidth: options.lineWidth || 1,
    lineColor: options.lineColor || 'rgba(0, 150, 255, 0.3)',
    backgroundColor: options.backgroundColor || 'rgba(2, 4, 10, 1.0)',
  };
  
  const canvas = document.createElement('canvas');
  canvas.width = config.size;
  canvas.height = config.size;
  const ctx = canvas.getContext('2d');
  
  // 绘制背景
  ctx.fillStyle = config.backgroundColor;
  ctx.fillRect(0, 0, config.size, config.size);
  
  const gridCount = Math.floor(config.size / config.gridSize);
  
  // 绘制网格线
  ctx.strokeStyle = config.lineColor;
  ctx.lineWidth = config.lineWidth;
  ctx.beginPath();
  
  for (let i = 0; i <= gridCount; i++) {
    const x = i * config.gridSize;
    ctx.moveTo(x, 0);
    ctx.lineTo(x, config.size);
  }
  
  for (let i = 0; i <= gridCount; i++) {
    const y = i * config.gridSize;
    ctx.moveTo(0, y);
    ctx.lineTo(config.size, y);
  }
  
  ctx.stroke();
  
  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.RepeatWrapping;
  texture.repeat.set(10, 10);
  texture.needsUpdate = true;
  
  return texture;
}

/**
 * 创建发光网格纹理（科技感）
 * @param {Object} options - 配置选项
 * @returns {THREE.CanvasTexture} Three.js纹理对象
 */
export function createGlowGridTexture(options = {}) {
  const config = {
    size: options.size || 512,
    gridSize: options.gridSize || 64,
    lineWidth: options.lineWidth || 2,
    lineColor: options.lineColor || 'rgba(0, 200, 255, 0.6)',
    dotRadius: options.dotRadius || 4,
    dotColor: options.dotColor || 'rgba(0, 220, 255, 1.0)',
    backgroundColor: options.backgroundColor || 'rgba(2, 4, 10, 1.0)',
  };
  
  const canvas = document.createElement('canvas');
  canvas.width = config.size;
  canvas.height = config.size;
  const ctx = canvas.getContext('2d');
  
  // 绘制背景
  ctx.fillStyle = config.backgroundColor;
  ctx.fillRect(0, 0, config.size, config.size);
  
  const gridCount = Math.floor(config.size / config.gridSize);
  
  // 绘制发光网格线
  ctx.strokeStyle = config.lineColor;
  ctx.lineWidth = config.lineWidth;
  ctx.shadowBlur = 10;
  ctx.shadowColor = config.lineColor;
  ctx.beginPath();
  
  for (let i = 0; i <= gridCount; i++) {
    const x = i * config.gridSize;
    ctx.moveTo(x, 0);
    ctx.lineTo(x, config.size);
  }
  
  for (let i = 0; i <= gridCount; i++) {
    const y = i * config.gridSize;
    ctx.moveTo(0, y);
    ctx.lineTo(config.size, y);
  }
  
  ctx.stroke();
  
  // 绘制发光圆点
  ctx.shadowBlur = 15;
  ctx.shadowColor = config.dotColor;
  
  for (let i = 0; i <= gridCount; i++) {
    for (let j = 0; j <= gridCount; j++) {
      const x = i * config.gridSize;
      const y = j * config.gridSize;
      
      // 绘制发光外圈
      const gradient = ctx.createRadialGradient(x, y, 0, x, y, config.dotRadius * 3);
      gradient.addColorStop(0, config.dotColor);
      gradient.addColorStop(0.3, config.dotColor.replace(/[\d.]+\)$/g, '0.6)'));
      gradient.addColorStop(0.7, config.dotColor.replace(/[\d.]+\)$/g, '0.2)'));
      gradient.addColorStop(1, 'rgba(0, 220, 255, 0)');
      
      ctx.fillStyle = gradient;
      ctx.beginPath();
      ctx.arc(x, y, config.dotRadius * 3, 0, Math.PI * 2);
      ctx.fill();
      
      // 绘制实心圆点
      ctx.fillStyle = config.dotColor;
      ctx.beginPath();
      ctx.arc(x, y, config.dotRadius, 0, Math.PI * 2);
      ctx.fill();
    }
  }
  
  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.RepeatWrapping;
  texture.repeat.set(10, 10);
  texture.needsUpdate = true;
  
  return texture;
}

export default {
  createGridTexture,
  createSimpleGridTexture,
  createGlowGridTexture
};

