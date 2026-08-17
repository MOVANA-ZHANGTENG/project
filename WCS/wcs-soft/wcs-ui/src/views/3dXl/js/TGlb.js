// 引入gltf模型加载库GLTFLoader.js
import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader.js";
export const allGlbs = []; // 返回所有基础模型
// 创建GLTF加载器对象
const loader = new GLTFLoader();
export class Loader {
  scene = null;
  constructor(scene) {
    this.scene = scene;
  }

  /**
   * 向场景中添加模型
   * @param   object 模型列表
   */
  addGlb(path) {
    var that = this;
    loader.load(path, function (glb) {
      // 模型无需旋转，直接立在XY平面上
      // glb.scene.rotation.x = Math.PI / 2; // 注释掉旋转代码
      that.scene.add(glb.scene); // 场景添加模型
    });
  }
}
