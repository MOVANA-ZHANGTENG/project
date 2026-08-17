/**
 * 优化的材质应用器，通过一次场景遍历完成所有材质的应用
 */
class OptimizedMaterialApplier {
  /**
   * 一次性应用所有材质（优化性能版）
   * @param {ThreeEngine} threeEngine - ThreeEngine实例
   */
  static applyAllMaterials(threeEngine) {
    console.time('applyAllMaterials');
    
    // 用于存储不同类型的对象
    const objectsToMaterialize = {
      floor: [],
      stackerBody: [],
      shelf: [],
      wall: [],
      platform: [],
      upperFork: [],
      lowerFork: [],
      rgvBody: [],
      rgvPlatform: [],
      rgvChain: [],
      rgvDecor: [],
      rgvTrack: [],
      conveyorFrame: [],
      conveyorBelt: [],
      conveyorRoller: [],
      conveyorChainGroove: [],
      conveyorGuideStrip: [],
      pallet: []
    };
    
    // 一次遍历场景，收集所有需要应用材质的对象
    threeEngine.scene.traverse((obj) => {
      if (!obj.name) return;
      
      const name = obj.name.toLowerCase();
      
      // 地板处理
      if (name.includes('floor')) {
        objectsToMaterialize.floor.push({ obj, name: obj.name });
      }
      // 堆垛机躯干处理
      else if (obj.name.includes('躯干')) {
        objectsToMaterialize.stackerBody.push({ obj, name: obj.name });
      }
      // 货架处理
      else if (obj.name.includes('货架')) {
        objectsToMaterialize.shelf.push(obj);
      }
      // 墙体处理
      else if (obj.name.includes('墙体')) {
        objectsToMaterialize.wall.push(obj);
      }
      // 载货台处理
      else if (obj.name.includes('载货台')) {
        objectsToMaterialize.platform.push(obj);
      }
      // 上货叉处理
      else if (obj.name.includes('上货叉')) {
        objectsToMaterialize.upperFork.push(obj);
      }
      // 下货叉处理
      else if (obj.name.includes('下货叉')) {
        objectsToMaterialize.lowerFork.push(obj);
      }
      // RGV处理
      else if (name.includes('rgv')) {
        if (name.includes('车体')) {
          objectsToMaterialize.rgvBody.push(obj);
        } else if (name.includes('载货台') && !name.includes('链条')) {
          objectsToMaterialize.rgvPlatform.push(obj);
        } else if (name.includes('链条')) {
          objectsToMaterialize.rgvChain.push(obj);
        } else if (name.includes('装饰条')) {
          objectsToMaterialize.rgvDecor.push(obj);
        } else if (name.includes('轨道')) {
          objectsToMaterialize.rgvTrack.push(obj);
        }
      }
      // 输送线处理
      else if (name.includes('输送线') || name.includes('conveyor')) {
        if (name.includes('框架') || name.includes('frame')) {
          objectsToMaterialize.conveyorFrame.push(obj);
        } else if (name.includes('带')) {
          objectsToMaterialize.conveyorBelt.push(obj);
        } else if (name.includes('滚筒') || name.includes('roller')) {
          objectsToMaterialize.conveyorRoller.push(obj);
        } else if (name.includes('链条槽') || name.includes('chain-groove') || name.includes('chain-slot') || name.includes('槽道')) {
          objectsToMaterialize.conveyorChainGroove.push(obj);
        } else if (name.includes('导向条') || name.includes('guide-strip') || name.includes('guide-bar') || name.includes('导轨') || name.includes('引导条')) {
          objectsToMaterialize.conveyorGuideStrip.push(obj);
        }
      }
      // 托盘处理
      else if (name.includes('pallet') || name.includes('托盘') || name.includes('货物')) {
        objectsToMaterialize.pallet.push(obj);
      }
    });
    
    // 应用地板材质
    const floorNames = ['floor-1', 'floor-2', 'floor-3', 'floor-4'];
    const floorStates = ['floor1', 'floor2', 'floor3', 'floor4'];
    
    let appliedFloorCount = 0;
    floorNames.forEach((floorName, index) => {
      const floor = threeEngine.getObjectByName(floorName);
      if (floor) {
        materialManager.applyMaterialToObject(floor, 'floor', 'main', floorStates[index]);
        appliedFloorCount++;
      }
    });
    
    // 如果没有找到指定名称的地板，使用收集到的地板对象
    if (appliedFloorCount === 0) {
      objectsToMaterialize.floor.forEach(({ obj, name }, index) => {
        const floorIndex = floorNames.indexOf(name);
        const stateIndex = floorIndex >= 0 ? floorIndex : Math.min(index, floorStates.length - 1);
        materialManager.applyMaterialToObject(obj, 'floor', 'main', floorStates[stateIndex]);
        appliedFloorCount++;
      });
    }
    
    // 批量应用其他材质
    const applyBatch = (objects, componentType, subType, state) => {
      objects.forEach(obj => {
        materialManager.applyMaterialToObject(obj, componentType, subType, state);
      });
      return objects.length;
    };
    
    // 应用各类型材质
    const stackerBodyCount = applyBatch(objectsToMaterialize.stackerBody.map(item => item.obj), 'stacker', 'body', 'default');
    const shelfCount = applyBatch(objectsToMaterialize.shelf, 'shelf', 'main', 'default');
    const wallCount = applyBatch(objectsToMaterialize.wall, 'wall', 'main', 'default');
    const platformCount = applyBatch(objectsToMaterialize.platform, 'stacker', 'platform', 'default');
    const upperForkCount = applyBatch(objectsToMaterialize.upperFork, 'stacker', 'upperFork', 'default');
    const lowerForkCount = applyBatch(objectsToMaterialize.lowerFork, 'stacker', 'lowerFork', 'default');
    const rgvBodyCount = applyBatch(objectsToMaterialize.rgvBody, 'rgv', 'body', 'default');
    const rgvPlatformCount = applyBatch(objectsToMaterialize.rgvPlatform, 'rgv', 'platform', 'default');
    const rgvChainCount = applyBatch(objectsToMaterialize.rgvChain, 'rgv', 'chain', 'default');
    const rgvDecorCount = applyBatch(objectsToMaterialize.rgvDecor, 'rgv', 'decor', 'default');
    const rgvTrackCount = applyBatch(objectsToMaterialize.rgvTrack, 'rgv', 'track', 'default');
    const conveyorFrameCount = applyBatch(objectsToMaterialize.conveyorFrame, 'conveyor', 'frame', 'default');
    const conveyorBeltCount = applyBatch(objectsToMaterialize.conveyorBelt, 'conveyor', 'belt', 'default');
    const conveyorRollerCount = applyBatch(objectsToMaterialize.conveyorRoller, 'conveyor', 'roller', 'default');
    const conveyorChainGrooveCount = applyBatch(objectsToMaterialize.conveyorChainGroove, 'conveyor', 'chainGroove', 'default');
    const conveyorGuideStripCount = applyBatch(objectsToMaterialize.conveyorGuideStrip, 'conveyor', 'guideStrip', 'default');
    const palletCount = applyBatch(objectsToMaterialize.pallet, 'pallet', 'main', 'default');
    
    // 输出统计信息
    console.timeEnd('applyAllMaterials');
  }
}

// 导出类（兼容CommonJS和ES6模块）
if (typeof module !== 'undefined' && module.exports) {
  module.exports = OptimizedMaterialApplier;
} else if (typeof window !== 'undefined') {
  window.OptimizedMaterialApplier = OptimizedMaterialApplier;
} else if (typeof define === 'function' && define.amd) {
  define([], () => OptimizedMaterialApplier);
}