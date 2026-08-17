import { BoxGeometry,Geometry, Mesh, MeshStandardMaterial } from "three";
import * as THREE from "three";
export const allBaseObject = []; // 返回所有基础模型
// 创建立方体
export const box = new Mesh(
  new BoxGeometry(20, 20, 20), // 设置立方体的大小 (x 长度, y 高度 ,z 长度)
  new MeshStandardMaterial({
    // 设置材质
    color: "rgb(36, 172, 242)", // 设置材质的颜色
  })
);

 //绘制星空背景
 var myGeometry = new THREE.BufferGeometry();

const vertices = [];
 for (var i = 0; i < 5000; i++) {

  const x = THREE.MathUtils.randFloatSpread(1800);
  const y = THREE.MathUtils.randFloatSpread(1800);
  const z = THREE.MathUtils.randFloatSpread(1800);

	vertices.push( x, y, z );

 }

 myGeometry.setAttribute( 'position', new THREE.Float32BufferAttribute( vertices, 3 ) );
// const material = new THREE.PointsMaterial( { color: 0xff0000,size:15, } );
// const points = new THREE.Points( geometry, material );

 var myPoints = new THREE.Points(myGeometry,
     new THREE.PointsMaterial({ color: 0xffffff }));


allBaseObject.push(myPoints); // 添加到模型数组
// allBaseObject.push(box); // 添加到模型数组
