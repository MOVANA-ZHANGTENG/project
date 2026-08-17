import Vue from 'vue';

/**
 * 模态框拖拽指令 - 全局自动应用版
 * 修复了拖拽时自动下移的问题，实现更平滑的拖拽体验，并全局自动应用到所有Element UI对话框
 */

// 拖拽实现的核心函数
function setupDialogDrag(el) {
  // 验证el是否有效
  if (!el || typeof el.querySelector !== 'function') return;
  
  const dialogHeaderEl = el.querySelector('.el-dialog__header');
  const dragDom = el.querySelector('.el-dialog');
  
  if (!dialogHeaderEl || !dragDom) return;
  
  dialogHeaderEl.style.cursor = 'move';
  
  // 添加CSS过渡效果，使拖拽更平滑
  dragDom.style.transition = 'transform 0.05s ease-out';
  
  // 状态变量
  let isDragging = false;
  let hasSetFixed = false;
  let animationFrameId = null;
  
  dialogHeaderEl.onmousedown = function(e) {
    // 阻止默认事件和冒泡
    e.preventDefault();
    e.stopPropagation();
    
    // 获取对话框的当前位置
    const dialogRect = dragDom.getBoundingClientRect();
    
    // 精确计算鼠标相对于对话框左上角的偏移量
    const mouseOffsetX = e.clientX - dialogRect.left;
    const mouseOffsetY = e.clientY - dialogRect.top;
    
    // 设置为拖拽状态
    isDragging = true;
    
    // 临时移除过渡效果，避免初始移动时的延迟感
    dragDom.style.transition = 'none';
    
    // 只在首次拖拽时切换到fixed定位
    if (!hasSetFixed) {
      // 保存原始样式到元素上，以便清理时恢复
      dragDom.__originalStyles = {
        position: dragDom.style.position,
        margin: dragDom.style.margin,
        transform: dragDom.style.transform,
        left: dragDom.style.left,
        top: dragDom.style.top,
        transition: dragDom.style.transition
      };
      
      // 切换到fixed定位
      dragDom.style.position = 'fixed';
      dragDom.style.left = dialogRect.left + 'px';
      dragDom.style.top = dialogRect.top + 'px';
      dragDom.style.margin = '0';
      dragDom.style.transform = 'none';
      
      hasSetFixed = true;
      
      // 重新获取切换定位后的位置，确保位置精确
      const newRect = dragDom.getBoundingClientRect();
      
      // 检测并消除可能的微小偏移，特别是垂直方向的下移问题
      const positionShiftX = dialogRect.left - newRect.left;
      const positionShiftY = dialogRect.top - newRect.top;
      
      if (Math.abs(positionShiftX) > 0.1 || Math.abs(positionShiftY) > 0.1) {
        dragDom.style.left = (dialogRect.left + positionShiftX) + 'px';
        dragDom.style.top = (dialogRect.top + positionShiftY) + 'px';
      }
    }
    
    // 鼠标移动时更新位置
    document.onmousemove = function(e) {
      if (!isDragging) return;
      
      // 取消之前的动画帧，避免积累延迟
      if (animationFrameId) {
        cancelAnimationFrame(animationFrameId);
      }
      
      // 使用requestAnimationFrame优化性能，使移动更流畅
      animationFrameId = requestAnimationFrame(() => {
        // 使用鼠标当前位置减去预先计算好的偏移量
        const newLeft = e.clientX - mouseOffsetX;
        const newTop = e.clientY - mouseOffsetY -50;
        
        dragDom.style.left = newLeft + 'px';
        dragDom.style.top = newTop + 'px';
      });
    };
    
    document.onmouseup = function() {
      isDragging = false;
      
      // 恢复过渡效果，使最终位置调整更平滑
      dragDom.style.transition = 'transform 0.05s ease-out';
      
      // 清理动画帧和事件监听
      if (animationFrameId) {
        cancelAnimationFrame(animationFrameId);
        animationFrameId = null;
      }
      document.onmousemove = null;
      document.onmouseup = null;
    };
  };
  
  // 防止头部文字被选中
  dialogHeaderEl.onselectstart = function() {
    return false;
  };
}

// 清理拖拽相关事件和样式
function cleanupDialogDrag(el) {
  // 验证el是否有效
  if (!el || typeof el.querySelector !== 'function') return;
  
  const dialogHeaderEl = el.querySelector('.el-dialog__header');
  if (dialogHeaderEl) {
    dialogHeaderEl.onmousedown = null;
    dialogHeaderEl.onselectstart = null;
  }
  
  const dragDom = el.querySelector('.el-dialog');
  if (dragDom && dragDom.__originalStyles) {
    // 恢复原始样式
    dragDom.style.position = dragDom.__originalStyles.position || '';
    dragDom.style.margin = dragDom.__originalStyles.margin || '';
    dragDom.style.transform = dragDom.__originalStyles.transform || '';
    dragDom.style.left = dragDom.__originalStyles.left || '';
    dragDom.style.top = dragDom.__originalStyles.top || '';
    dragDom.style.transition = dragDom.__originalStyles.transition || '';
    
    // 清理保存的样式属性
    delete dragDom.__originalStyles;
  }
}

// 注册原有的指令，保持兼容性
Vue.directive('dialogDrags', {
  bind(el) {
    setupDialogDrag(el);
  },
  unbind(el) {
    cleanupDialogDrag(el);
  }
});

// 同时注册dialogDrag指令，保持兼容性
Vue.directive('dialogDrag', {
  bind(el) {
    setupDialogDrag(el);
  },
  unbind(el) {
    cleanupDialogDrag(el);
  }
});

// 全局自动应用拖拽功能到所有Element UI对话框
// 通过覆盖Dialog组件的mounted和beforeDestroy钩子
Vue.mixin({
  mounted() {
    // 关键修复：确保this.$el存在且是一个有效的DOM元素
    if (this.$el && typeof this.$el.querySelectorAll === 'function') {
      // 检查当前组件是否包含el-dialog
      const dialogs = this.$el.querySelectorAll('.el-dialog');
      if (dialogs && dialogs.length > 0) {
        dialogs.forEach(dialog => {
          // 只处理未被手动添加指令的对话框
          if (!dialog.__hasDragApplied) {
            dialog.__hasDragApplied = true;
            // 确保dialog.parentNode存在
            if (dialog.parentNode) {
              setupDialogDrag(dialog.parentNode);
            }
          }
        });
      }
    }
  },
  beforeDestroy() {
    // 关键修复：确保this.$el存在且是一个有效的DOM元素
    if (this.$el && typeof this.$el.querySelectorAll === 'function') {
      // 清理拖拽相关事件
      const dialogs = this.$el.querySelectorAll('.el-dialog');
      if (dialogs && dialogs.length > 0) {
        dialogs.forEach(dialog => {
          if (dialog.__hasDragApplied) {
            delete dialog.__hasDragApplied;
            // 确保dialog.parentNode存在
            if (dialog.parentNode) {
              cleanupDialogDrag(dialog.parentNode);
            }
          }
        });
      }
    }
  }
});

// 创建并导出插件
const dialogDragPlugin = {
  install(Vue) {
    // 插件安装逻辑已在上面实现
  }
};

export default dialogDragPlugin;