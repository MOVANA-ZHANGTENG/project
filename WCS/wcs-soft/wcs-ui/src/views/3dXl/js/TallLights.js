import { AmbientLight, DirectionalLight, PointLight, PointLightHelper, HemisphereLight } from "three";
import {GUI} from 'dat.gui';

/**
 * 光线
 */
export const allLights = [];

// 全局亮度系数，用于调整整体亮度 (0.5-2.0)
const BRIGHTNESS_FACTOR = 3.5; // 降低亮度系数以更好地显示深色地板

// 添加环境光（自然光），提供基础照明
export const ambientLight = new AmbientLight("rgb(200,200,200)", 0.15 * BRIGHTNESS_FACTOR); // 环境光强度

// 添加半球光，模拟天空和地面的自然光照效果
export const hemisphereLight = new HemisphereLight("rgb(200,200,255)", "rgb(150,150,200)", 0.15 * BRIGHTNESS_FACTOR);
hemisphereLight.position.set(0, 50, 0);

// 添加主方向光，模拟主要光源
export const directionalLight = new DirectionalLight("rgb(255,250,240)", 0.4 * BRIGHTNESS_FACTOR);
directionalLight.position.set(20, 30, 10);
directionalLight.castShadow = true; // 启用阴影投射
directionalLight.shadow.mapSize.width = 2048; // 提高阴影质量
directionalLight.shadow.mapSize.height = 2048;
directionalLight.shadow.camera.near = 0.5;
directionalLight.shadow.camera.far = 200;
directionalLight.shadow.camera.left = -50;
directionalLight.shadow.camera.right = 50;
directionalLight.shadow.camera.top = 50;
directionalLight.shadow.camera.bottom = -50;

// 添加辅助点光源，增强局部照明
export const pointLight1 = new PointLight("rgb(255,245,235)", 0.3 * BRIGHTNESS_FACTOR, 100);
pointLight1.position.set(-30, 20, 30);

export const pointLight2 = new PointLight("rgb(235,245,255)", 0.3 * BRIGHTNESS_FACTOR, 100);
pointLight2.position.set(30, 20, -30);

// 橙色灯光已移除

// 启用所有光源
allLights.push(ambientLight);
allLights.push(hemisphereLight);
allLights.push(directionalLight);
allLights.push(pointLight1);
allLights.push(pointLight2);