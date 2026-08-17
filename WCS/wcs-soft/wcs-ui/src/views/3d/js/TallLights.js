import { AmbientLight, PointLight ,PointLightHelper} from "three";
import {GUI} from 'dat.gui';

/**
 * 光线
 */
export const allLights = [];

// 添加环境光（自然光），设置自然光的颜色，设置自然光的强度（0 最暗， 1 最强）
export const ambientLight = new AmbientLight("rgb(200,200,200)", 0.8); // 暖色调环境光

// 优化后的点光源配置（减少数量，调整参数）
export const pointLight1 = new PointLight("rgb(255,245,235)", 1.5, 50, 2);
pointLight1.position.set(-30, 30, 30);
pointLight1.castShadow = true; // 启用阴影投射
pointLight1.shadow.mapSize.width = 1024; // 阴影质量
pointLight1.shadow.mapSize.height = 1024;
pointLight1.shadow.camera.near = 0.5;
pointLight1.shadow.camera.far = 100;

export const pointLight2 = new PointLight("rgb(235,245,255)", 1.5, 50, 2);
pointLight2.position.set(30, 30, -30);
pointLight2.castShadow = true;

// 删除冗余点光源3、4
export const pointLight3 = new PointLight("rgb(255,255,255)", 1.5, 2000, 1); // 替换未定义的intensity变量
pointLight3.position.set(30, 30, -30);

export const pointLight4 = new PointLight("rgb(255,255,255)", 1.5, 2000, 1); // 替换未定义的intensity变量
pointLight4.position.set(30, 30, 30);

// 启用必要光源
allLights.push(ambientLight);
allLights.push(pointLight1);
allLights.push(pointLight2);

// 添加光源调试辅助线（开发环境）- 已禁用
// if (process.env.NODE_ENV === 'development') {
//     const pointLightHelper = new PointLightHelper(pointLight1, 5);
//     allLights.push(pointLightHelper);
// }

// 光源位置辅助

// const sphereSize = 10;
// const pointLightHelper = new PointLightHelper( pointLight1, sphereSize );

// allLights.push(pointLightHelper); 

 

// const gui = new GUI();
// //改变交互界面style属性
// gui.domElement.style.right = '0px';
// gui.domElement.style.width = '300px';
// gui.add(pointLight1.position, 'x', -800, 800);
// gui.add(pointLight1.position, 'y', 0, 800);
// gui.add(pointLight1.position, 'z', -800, 800);

