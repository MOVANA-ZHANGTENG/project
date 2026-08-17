import { AxesHelper, GridHelper } from "three";

export const allHelper = [];

// 坐标辅助
export const axesHelper = new AxesHelper(100, 1000); // 创建坐标辅助

// 创建地面网格辅助
export const gridHelper = new GridHelper(100, 100, "red", "rgb(100, 100, 100)");

// allHelper.push(   gridHelper);
//allHelper.push(axesHelper);
