import * as THREE from "three";
function createSpriteTextLabel(config) {
    // 合并默认配置
    const mergedConfig = {
        global: {
           
            textStyle: {
                fontFamily: 'Arial',
                fontSize: 14,
                color: '#333333',
                align: 'left',
                ...config.global?.textStyle
            },
            backgroundColor: '#FFFFFF',
            ...config.global
        },
        rows: config.rows || []
    };

    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d', { antialias: true });

    // 动态计算画布尺寸
    let totalHeight = mergedConfig.rows.reduce((acc, row) => acc + (row.rowHeight || 40), 0);
    totalHeight += mergedConfig.global.headerHeight; // 标题区域高度
    const totalWidth = mergedConfig.global.totalWidth;

    // 高清画布设置
    const scale = window.devicePixelRatio * 2;
    canvas.width = totalWidth * scale;
    canvas.height = totalHeight * scale;
    canvas.style.width = totalWidth + 'px';
    canvas.style.height = totalHeight + 'px';
    ctx.scale(scale, scale);

    // 绘制全局背景（带圆角）
    ctx.fillStyle = mergedConfig.global.backgroundColor;
    ctx.beginPath();
    ctx.roundRect(0, 0, totalWidth, totalHeight, 12); // 增加整体圆角
    ctx.fill();

    // 绘制标题栏
    ctx.fillStyle = mergedConfig.global.headerTextColor;
    ctx.font = 'bold 22px 微软雅黑';
    ctx.textAlign = 'left';
    ctx.fillText(mergedConfig.global.deviceName, 50, 32);

    // 绘制状态指示灯
    const statusColors = { active: '#52c41a', warning: '#faad14', error: '#f5222d' };
    ctx.fillStyle = statusColors[config.status || 'active'];
    ctx.beginPath();
    ctx.arc(totalWidth - 60, 22, 12, 0, Math.PI * 2);
    ctx.fill();

    // 动态绘制表格内容
    let currentY = 60;
    mergedConfig.rows.forEach(row => {
        // 应用行背景
        if(row.backgroundColor) {
            ctx.fillStyle = row.backgroundColor;
            ctx.fillRect(0, currentY, totalWidth, row.rowHeight);
        }

        // 绘制列内容
        let currentX = 0;
        row.columns?.forEach(col => {
            const colWidth = (col.widthRatio / 10) * totalWidth;
            
            // 合并样式层级
            const textStyle = { 
                ...mergedConfig.global.textStyle,
                ...(row.textStyle || {}),
                ...(col.textStyle || {})
            };

            // 设置文本样式
            ctx.fillStyle = textStyle.color;
            const fontWeight = textStyle.fontWeight || 'normal';
            const fontSize = textStyle.fontSize || 14;
            const fontFamily = textStyle.fontFamily || 'Arial';
            ctx.font = fontWeight +' '+fontSize+'px '+ fontFamily;
            ctx.textAlign = textStyle.align;

            // 计算文本位置
            const textX = currentX + (textStyle.align === 'center' ? colWidth/2 : 
                             textStyle.align === 'right' ? colWidth - 10 : 10);
            
            // 绘制文本
            ctx.fillText(col.content, textX, currentY + (row.rowHeight/2) + 8);
            
            currentX += colWidth;
        });
        
        currentY += row.rowHeight || 40;
    });

    // 创建材质
    const texture = new THREE.CanvasTexture(canvas);
    texture.anisotropy = 16;
    texture.minFilter = THREE.LinearFilter;

    const material = new THREE.SpriteMaterial({
        map: texture,
        transparent: true,
        opacity: 1,
        depthTest: false
    });

     const sprite = new THREE.Sprite(material);
    sprite.scale.set(totalWidth * 0.03, totalHeight * 0.03, 1);

    // 新增箭头部分
    const arrow = new THREE.Mesh(
        new THREE.CylinderGeometry(0, 0.2, 4, 8),
        new THREE.MeshBasicMaterial({ color: 0x00008b }) // 颜色从0xff0000改为深蓝色
    );
    arrow.rotation.z = Math.PI;
    arrow.position.y = -totalHeight * 0.015 - 2.0;  // 下移位置从
    
    // 创建容器组合对象
    const group = new THREE.Group();
    group.add(sprite);
    group.add(arrow);
    
    group.updateContent = (partialConfig) => {
        Object.assign(config, partialConfig); // 合并新配置
        // 重绘逻辑
        texture.needsUpdate = true;
    };
    return group;
}

/**
 * {
  "global": {
    "textStyle": {
      "fontFamily": "Arial",     // 全局字体
      "fontSize": 14,            // 全局字号(px)
      "color": "#333333",        // 全局字体颜色
      "align": "left"            // 全局对齐方式(left/center/right)
    },
    "backgroundColor": "#FFFFFF" // 全局背景色
  },
  "rows": [
    {
      "rowHeight": 40,           // 行高(px)
      "backgroundColor": "#F5F5F5", // 行背景色(可选，覆盖全局)
      "textStyle": {             // 行级文字样式(可选，覆盖全局)
        "fontSize": 16,
        "color": "#666666"
      },
      "columns": [
        {
          "content": "列1内容", // 列展示内容
          "widthRatio": 5,      // 列宽占比(总和建议为10)
          "textStyle": {         // 列级文字样式(可选，覆盖行/全局)
            "color": "#FF0000",
            "align": "center"
          }
        },
        {
          "content": "列2内容",
          "widthRatio": 5,
          "textStyle": {
            "fontFamily": "Microsoft YaHei",
            "fontSize": 18
          }
        }
      ]
    },
    {
      "rowHeight": 60,
      "columns": [
        {
          "content": "跨列内容",
          "widthRatio": 10,      // 独占整行
          "textStyle": {
            "align": "center",
            "fontWeight": "bold"
          }
        }
      ]
    }
  ]
}
 */

// 添加导出语句以便在vue中引用
export { createSpriteTextLabel};