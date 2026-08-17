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

    // 绘制标题栏（根据配置的字体大小调整）
    ctx.fillStyle = mergedConfig.global.headerTextColor;
    const headerFontSize = mergedConfig.global.headerFontSize || (mergedConfig.global.textStyle.fontSize * 1.3);
    ctx.font = `bold ${headerFontSize}px 微软雅黑`;
    ctx.textAlign = 'left';
    ctx.fillText(mergedConfig.global.deviceName, 50, mergedConfig.global.headerHeight * 0.6);

    // 绘制状态指示灯（位置根据配置动态调整）
    // 如果配置了自定义状态颜色（statusColor），优先使用；否则使用默认状态颜色
    const statusColors = { active: '#52c41a', warning: '#faad14', error: '#f5222d', idle: '#FFD700' };
    const indicatorColor = mergedConfig.global.statusColor || statusColors[config.global?.status || 'active'];
    ctx.fillStyle = indicatorColor;
    ctx.beginPath();
    const indicatorSize = mergedConfig.global.headerHeight * 0.25; // 指示灯大小随标题高度缩放
    ctx.arc(totalWidth - 30, mergedConfig.global.headerHeight * 0.5, indicatorSize, 0, Math.PI * 2);
    ctx.fill();
    
    // 添加指示灯外圈（发光效果）
    ctx.strokeStyle = indicatorColor;
    ctx.lineWidth = 2;
    ctx.stroke();

    // 动态绘制表格内容（起始位置根据headerHeight动态调整）
    let currentY = mergedConfig.global.headerHeight + 10;
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
            ctx.font = fontWeight+' '+fontSize+'px '+' '+fontFamily;
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
        depthTest: false, // 始终显示在最前面，像UI元素
        depthWrite: false
    });

     const sprite = new THREE.Sprite(material);
    sprite.renderOrder = 1000; // 信息框渲染顺序最高，确保在箭头之上
    // 使用配置中的缩放比例，如果没有则使用默认值0.03
    const scaleRatio = mergedConfig.global.scaleRatio || 0.03;
    sprite.scale.set(totalWidth * scaleRatio, totalHeight * scaleRatio, 1);

    // 新增箭头部分（虚化样式，避免与场景实体混淆）
    const arrowSize = scaleRatio / 0.03; // 相对于默认比例的缩放
    const arrowColor = mergedConfig.global.arrowColor || 0x00D4FF; // 使用配置的颜色或默认科技青色
    
    // 添加连接线（虚化效果）- 主体，比较长
    const rodGeometry = new THREE.CylinderGeometry(0.08 * arrowSize, 0.08 * arrowSize, 3.5 * arrowSize, 8);
    const rodMaterial = new THREE.MeshBasicMaterial({ 
        color: arrowColor,
        transparent: true,
        opacity: 0.6, // 连接线透明度
        depthTest: false,
        depthWrite: false
    });
    
    const rod = new THREE.Mesh(rodGeometry, rodMaterial);
    rod.position.y = -totalHeight * scaleRatio / 2 - 1.2 * arrowSize;  // 连接线位置
    rod.renderOrder = 999;
    
    // 创建精致的箭头（锥形）- 点缀，比较小
    const arrowGeometry = new THREE.CylinderGeometry(0, 0.18 * arrowSize, 1.2 * arrowSize, 8);
    const arrowMaterial = new THREE.MeshBasicMaterial({ 
        color: arrowColor,
        transparent: true,
        opacity: 0.7, // 箭头稍微更实一点
        depthTest: false, // 禁用深度测试，始终显示在前面（像UI元素）
        depthWrite: false // 不写入深度缓冲
    });
    
    const arrow = new THREE.Mesh(arrowGeometry, arrowMaterial);
    arrow.rotation.z = Math.PI;
    arrow.position.y = -totalHeight * scaleRatio / 2 - 3.0 * arrowSize;  // 箭头在连接线底部
    arrow.renderOrder = 999; // 渲染顺序靠后，确保在其他物体之上
    
    // 添加连接线发光外圈（增强虚化UI感）
    const glowGeometry = new THREE.CylinderGeometry(0.12 * arrowSize, 0.12 * arrowSize, 3.5 * arrowSize, 8);
    const glowMaterial = new THREE.MeshBasicMaterial({
        color: arrowColor,
        transparent: true,
        opacity: 0.15, // 非常透明的外圈
        depthTest: false,
        depthWrite: false
    });
    
    const glow = new THREE.Mesh(glowGeometry, glowMaterial);
    glow.position.y = -totalHeight * scaleRatio / 2 - 1.2 * arrowSize;
    glow.renderOrder = 998; // 在连接线下一层
    
    // 创建容器组合对象
    const group = new THREE.Group();
    group.add(sprite);
    group.add(glow); // 添加发光外圈（最底层）
    group.add(arrow); // 添加箭头主体
    group.add(rod); // 添加连接杆
    
    // 标记为UI元素，避免被炮弹系统检测到
    group.userData.isUI = true;
    sprite.userData.isUI = true;
    glow.userData.isUI = true;
    arrow.userData.isUI = true;
    rod.userData.isUI = true;
    
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