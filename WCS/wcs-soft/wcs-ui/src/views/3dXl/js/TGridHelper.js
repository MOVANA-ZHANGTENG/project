import { AxesHelper, GridHelper, TextGeometry, Sprite, SpriteMaterial, CanvasTexture, Color } from "three";

// 创建文本标签函数
function createAxisLabel(text, color, position) {
  // 创建 canvas 纹理
  const canvas = document.createElement('canvas');
  canvas.width = 256;
  canvas.height = 256;
  const context = canvas.getContext('2d');
  
  // 清空画布
  context.fillStyle = 'rgba(0, 0, 0, 0)';
  context.fillRect(0, 0, canvas.width, canvas.height);
  
  // 设置文本样式
  context.font = 'bold 96px Arial';
  context.fillStyle = color;
  context.textAlign = 'center';
  context.textBaseline = 'middle';
  
  // 绘制文本
  context.fillText(text, canvas.width / 2, canvas.height / 2);
  
  // 创建纹理和精灵
  const texture = new CanvasTexture(canvas);
  const material = new SpriteMaterial({ 
    map: texture, 
    transparent: true, 
    depthTest: false // 禁用深度测试，确保标签始终可见
  });
  const sprite = new Sprite(material);
  
  // 设置精灵大小和位置
  sprite.scale.set(100, 100, 1);
  sprite.position.set(position.x, position.y, position.z);
  
  return sprite;
}

export const allHelper = [];

// 坐标辅助
export const axesHelper = new AxesHelper(1000); // 创建坐标辅助，设置长度为1000

// 创建地面网格辅助
export const gridHelper = new GridHelper(100, 100, "red", "rgb(100, 100, 100)");
// 网格无需旋转，直接位于XY平面上
// gridHelper.rotation.x = -Math.PI / 2; // 注释掉旋转代码

// 调整坐标辅助，使其符合z轴朝上的要求
axesHelper.rotation.x = 0;
axesHelper.rotation.y = 0;
axesHelper.rotation.z = Math.PI / 2;

// 创建轴标记
// 在z轴朝上的坐标系中，轴的方向和位置
// X轴：红色，指向右侧
// Y轴：绿色，指向屏幕内
// Z轴：蓝色，指向上方
const xLabel = createAxisLabel('X', '#ff0000', { x: 500, y: 0, z: 0 });
const yLabel = createAxisLabel('Y', '#00ff00', { x: 0, y: 500, z: 0 });
const zLabel = createAxisLabel('Z', '#0000ff', { x: 0, y: 0, z: 500 });

// 添加辅助线到allHelper数组中，以便在场景中显示
// 隐藏网格线以移除xy平面上的红色线
gridHelper.visible = false;
allHelper.push(gridHelper);

// 隐藏坐标轴及其标注
axesHelper.visible = false;
xLabel.visible = false;
yLabel.visible = false;
zLabel.visible = false;

// 如果需要重新显示坐标轴，只需将上面的visible属性改为true即可
