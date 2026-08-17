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
      that.scene.add(glb.scene); // 场景添加模型
    });
  }
}
