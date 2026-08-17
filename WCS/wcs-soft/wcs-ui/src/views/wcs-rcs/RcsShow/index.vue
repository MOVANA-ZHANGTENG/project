<template>
  <div id="app" class="flex flex-col h-full font-inter bg-neutral-100 text-neutral-700 h-screen flex flex-col overflow-hidden">
    <!-- 顶部导航栏 -->
    <header class="bg-white border-b border-neutral-200 shadow-sm z-30">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <i class="fa fa-cubes text-primary text-2xl"></i>
          <h1 class="text-xl font-bold text-neutral-700">智能仓储2D可视化监控系统</h1>
        </div>

        <div class="flex items-center space-x-4">
          <div class="relative">
            <button class="flex items-center space-x-2 bg-neutral-100 hover:bg-neutral-200 px-3 py-1.5 rounded-md transition-colors">
              <i class="fa fa-calendar-o"></i>
              <span id="current-time"></span>
            </button>
          </div>

          <div class="flex items-center space-x-1">
            <span class="text-sm text-neutral-500">系统状态:</span>
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-success/10 text-success">
              <span class="w-1.5 h-1.5 rounded-full bg-success mr-1.5"></span>
              正常运行中
            </span>
          </div>

          <div class="relative">
            <button class="flex items-center space-x-2 text-neutral-500 hover:text-primary transition-colors">
              <i class="fa fa-bell-o"></i>
              <span class="absolute -top-1 -right-1 bg-danger text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">3</span>
            </button>
          </div>

          <div class="relative">
            <button class="flex items-center space-x-2 text-neutral-500 hover:text-primary transition-colors">
              <img src="https://picsum.photos/id/1005/200/200" alt="用户头像" class="w-7 h-7 rounded-full object-cover border border-neutral-200">
              <span class="hidden md:inline">管理员</span>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="flex-1 flex overflow-hidden">
      <!-- 左侧控制面板 -->
      <aside class="w-64 bg-white border-r border-neutral-200 flex flex-col overflow-hidden transition-all duration-300">
        <div class="p-4 border-b border-neutral-200">
          <h2 class="font-semibold text-neutral-700">控制面板</h2>
        </div>

        <div class="p-4 space-y-4">
          <div class="space-y-2">
            <label class="text-sm font-medium text-neutral-500">显示设置</label>
            <div class="flex flex-col space-y-2">
              <label class="flex items-center space-x-2">
                <input type="checkbox" id="show-cell-numbers" checked class="rounded text-primary focus:ring-primary">
                <span class="text-sm">显示库位编号</span>
              </label>
              <label class="flex items-center space-x-2">
                <input type="checkbox" id="show-vehicle-paths" checked class="rounded text-primary focus:ring-primary">
                <span class="text-sm">显示四向车路径</span>
              </label>
              <label class="flex items-center space-x-2">
                <input type="checkbox" id="show-elevators" checked class="rounded text-primary focus:ring-primary">
                <span class="text-sm">显示提升机</span>
              </label>
              <label class="flex items-center space-x-2">
                <input type="checkbox" id="show-conveyors" checked class="rounded text-primary focus:ring-primary">
                <span class="text-sm">显示输送线</span>
              </label>
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-neutral-500">筛选条件</label>
            <div class="space-y-2">
              <select id="selected-floor" class="w-full p-2 text-sm border border-neutral-300 rounded-md focus:ring-2 focus:ring-primary/30 focus:border-primary">
                <option value="all">所有楼层</option>
                <option value="F1">F1</option>
                <option value="F2">F2</option>
                <option value="F3">F3</option>
              </select>

              <select id="selected-vehicle" class="w-full p-2 text-sm border border-neutral-300 rounded-md focus:ring-2 focus:ring-primary/30 focus:border-primary">
                <option value="all">所有四向车</option>
                <option value="001">四向车-001</option>
                <option value="002">四向车-002</option>
                <option value="003">四向车-003</option>
              </select>

              <select id="selected-status" class="w-full p-2 text-sm border border-neutral-300 rounded-md focus:ring-2 focus:ring-primary/30 focus:border-primary">
                <option value="all">所有状态</option>
                <option value="running">运行中</option>
                <option value="idle">空闲</option>
                <option value="charging">充电中</option>
                <option value="error">故障</option>
              </select>
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-neutral-500">地图操作</label>
            <div class="flex space-x-2">
              <button id="zoom-in" class="flex-1 p-2 text-sm bg-primary text-white rounded-md hover:bg-primary/90 transition-colors">
                <i class="fa fa-search-plus mr-1"></i> 放大
              </button>
              <button id="zoom-out" class="flex-1 p-2 text-sm bg-primary text-white rounded-md hover:bg-primary/90 transition-colors">
                <i class="fa fa-search-minus mr-1"></i> 缩小
              </button>
            </div>
            <button id="reset-view" class="w-full p-2 text-sm bg-primary text-white rounded-md hover:bg-primary/90 transition-colors">
              <i class="fa fa-refresh mr-1"></i> 重置视图
            </button>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-neutral-500">路径控制</label>
            <button id="generate-new-paths" class="w-full p-2 text-sm bg-primary text-white rounded-md hover:bg-primary/90 transition-colors">
              <i class="fa fa-random mr-1"></i> 生成新路径
            </button>
            <button id="toggle-path-animation" class="w-full p-2 text-sm bg-primary text-white rounded-md hover:bg-primary/90 transition-colors">
              <i class="fa fa-pause mr-1"></i> 暂停路径动画
            </button>
          </div>
        </div>

        <div class="mt-auto p-4 border-t border-neutral-200">
          <button id="emergency-stop" class="w-full p-2 text-sm bg-danger text-white rounded-md hover:bg-danger/90 transition-colors flex items-center justify-center">
            <i class="fa fa-stop-circle mr-1"></i> 紧急停止所有四向车
          </button>
        </div>
      </aside>

      <!-- 中间地图区域 -->
      <section class="flex-1 flex flex-col overflow-hidden bg-neutral-100 relative">
        <div class="p-4 flex items-center justify-between border-b border-neutral-200 bg-white">
          <div class="flex items-center space-x-4">
            <h2 class="font-semibold text-neutral-700">仓库地图 - <span id="current-floor" class="text-primary">F1</span></h2>

            <div class="flex items-center space-x-1 text-sm">
              <span class="flex items-center"><span class="w-3 h-3 bg-success/10 border border-success/30 rounded-sm mr-1"></span> 空闲</span>
              <span class="flex items-center ml-2"><span class="w-3 h-3 bg-danger/10 border border-danger/30 rounded-sm mr-1"></span> 占用</span>
              <span class="flex items-center ml-2"><span class="w-3 h-3 bg-warning/10 border border-warning/30 rounded-sm mr-1"></span> 待入库</span>
              <span class="flex items-center ml-2"><span class="w-3 h-3 bg-neutral-200 border border-neutral-300 rounded-sm mr-1"></span> 禁用</span>
              <span class="flex items-center ml-2"><span class="w-3 h-3 bg-neutral-500 rounded-sm mr-1"></span> 输送线</span>
            </div>
          </div>

          <div class="flex items-center space-x-2">
            <button id="toggle-monitoring" class="p-2 text-sm bg-primary text-white rounded-md hover:bg-primary/90 transition-colors flex items-center">
              <i class="fa fa-pause mr-1"></i> 暂停监控
            </button>
            <button id="export-map" class="p-2 text-sm bg-white border border-neutral-300 text-neutral-700 rounded-md hover:bg-neutral-100 transition-colors flex items-center">
              <i class="fa fa-download mr-1"></i> 导出地图
            </button>
          </div>
        </div>

        <!-- 地图容器 -->
        <div class="flex-1 overflow-auto relative" id="map-container">
          <div class="absolute top-0 left-0 p-4 z-10 bg-white/80 backdrop-blur-sm rounded-md shadow-md">
            <div class="text-sm font-medium">仓库总览</div>
            <div class="flex items-center space-x-4 mt-2">
              <div class="text-center">
                <div class="text-2xl font-bold text-primary">120</div>
                <div class="text-xs text-neutral-500">总库位</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-success">78</div>
                <div class="text-xs text-neutral-500">空闲</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-danger">42</div>
                <div class="text-xs text-neutral-500">占用</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-warning">0</div>
                <div class="text-xs text-neutral-500">异常</div>
              </div>
            </div>
          </div>

          <!-- 仓库网格地图 -->
          <div id="warehouse-map" class="relative p-8 transition-all duration-300 transform origin-top-left mx-auto" style="transform: scale(1); width: 1000px; height: 800px;">
            <!-- 输送线系统 -->
            <div id="conveyors-container">
              <div class="conveyor-belt h-6 w-[900px]" style="top: 60px; left: 100px;">
                <div class="h-full w-full bg-[linear-gradient(to_right,rgba(255,255,255,0.2)_20%,transparent_20%,transparent_40%,rgba(255,255,255,0.2)_40%,rgba(255,255,255,0.2)_60%,transparent_60%,transparent_80%,rgba(255,255,255,0.2)_80%)] bg-[length:200px_100%] animated-marquee">
                </div>
              </div>

              <div class="conveyor-belt h-[900px] w-6" style="top: 60px; left: 100px;">
                <div class="h-full w-full bg-[linear-gradient(to_bottom,rgba(255,255,255,0.2)_20%,transparent_20%,transparent_40%,rgba(255,255,255,0.2)_40%,rgba(255,255,255,0.2)_60%,transparent_60%,transparent_80%,rgba(255,255,255,0.2)_80%)] bg-[length:100px_100%] animated-marquee">
                </div>
              </div>

              <div class="conveyor-belt h-6 w-[900px]" style="top: 700px; left: 100px;">
                <div class="h-full w-full bg-[linear-gradient(to_right,rgba(255,255,255,0.2)_20%,transparent_20%,transparent_40%,rgba(255,255,255,0.2)_40%,rgba(255,255,255,0.2)_60%,transparent_60%,transparent_80%,rgba(255,255,255,0.2)_80%)] bg-[length:200px_100%] animated-marquee">
                </div>
              </div>
            </div>

            <!-- 提升机 -->
            <div id="elevators-container">
              <div class="elevator-icon bg-white border-primary h-[120px] w-[60px]" style="top: 40px; left: 20px;">
                <div class="text-xs font-medium text-primary">提升机-1</div>
                <div class="mt-2 text-xs text-neutral-500">F1</div>
                <div class="h-8 w-8 bg-primary rounded-full flex items-center justify-center mt-2">
                  <i class="fa fa-arrows-v text-white"></i>
                </div>
              </div>
            </div>

            <!-- 库位网格 -->
            <div id="grid-cells-container" class="absolute top-[120px] left-[160px]">
              <div class="grid grid-cols-15 gap-2" style="grid-template-rows: repeat(8, 60px);">
                <!-- 生成8x15的库位网格 -->
                <!-- 第一行 -->
                <div class="grid-cell grid-cell-idle relative group" data-id="A101">
                  <span class="cell-number absolute top-1 left-1 text-xs text-neutral-500 opacity-0 group-hover:opacity-100 transition-opacity">A101</span>
                </div>
                <!-- 其他行库位省略 -->
              </div>
            </div>

            <!-- 四向车路径 -->
            <div id="vehicle-paths-container">
              <!-- 四向车1路径 -->
              <svg class="absolute top-0 left-0 w-full h-full" xmlns="http://www.w3.org/2000/svg">
                <path d="M 160 120 H 260 V 180 H 360 V 240 H 460 V 300 H 560 V 360 H 660 V 420"
                      stroke="#165DFF" stroke-width="3" fill="none" stroke-dasharray="5,5" class="animated-path" />
              </svg>
              <!-- 其他四向车路径省略 -->
            </div>

            <!-- 四向车 -->
            <div id="vehicles-container">
              <!-- 四向车1 -->
              <div class="agv-icon bg-primary text-white h-10 w-10" style="top: 115px; left: 155px; animation: move-vehicle-1 15s linear infinite;">
                <i class="fa fa-truck"></i>
                <span class="absolute -bottom-5 left-1/2 transform -translate-x-1/2 text-xs text-primary font-medium">四向车-001</span>
              </div>
              <!-- 其他四向车省略 -->
            </div>
          </div>
        </div>
      </section>

      <!-- 右侧信息面板 -->
      <aside class="w-80 bg-white border-l border-neutral-200 flex flex-col overflow-hidden transition-all duration-300">
        <div class="p-4 border-b border-neutral-200">
          <h2 class="font-semibold text-neutral-700">设备状态</h2>
        </div>

        <div class="p-4 space-y-4 overflow-y-auto scrollbar-hide flex-1">
          <!-- 四向车状态 -->
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <h3 class="font-medium text-neutral-700">四向车状态</h3>
              <span class="text-xs text-neutral-500">共3台</span>
            </div>

            <div class="space-y-2">
              <!-- 四向车1 -->
              <div class="bg-white p-3 rounded-md shadow-sm border border-neutral-200 hover:border-primary transition-colors">
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <div class="h-8 w-8 rounded-full bg-primary flex items-center justify-center text-white mr-3">
                      <i class="fa fa-truck"></i>
                    </div>
                    <div>
                      <div class="font-medium text-neutral-700">四向车-001</div>
                      <div class="text-xs text-success">运行中</div>
                    </div>
                  </div>
                  <div class="text-xs text-neutral-500">F1</div>
                </div>
                <div class="mt-2 grid grid-cols-2 gap-2">
                  <div class="text-xs">
                    <div class="text-neutral-500">电量</div>
                    <div class="text-neutral-700">85%</div>
                  </div>
                  <div class="text-xs">
                    <div class="text-neutral-500">任务</div>
                    <div class="text-neutral-700">出库</div>
                  </div>
                  <div class="text-xs">
                    <div class="text-neutral-500">位置</div>
                    <div class="text-neutral-700">A101</div>
                  </div>
                  <div class="text-xs">
                    <div class="text-neutral-500">速度</div>
                    <div class="text-neutral-700">1.2m/s</div>
                  </div>
                </div>
              </div>
              <!-- 其他四向车省略 -->
            </div>
          </div>
        </div>
      </aside>
    </main>
  </div>
</template>

<script>
export default {
  name: 'RcsShow',
  data() {
    return {
      // 可添加组件数据
    };
  },
  mounted() {
    // 可添加组件挂载后执行的逻辑
  },
  methods: {
    // 可添加组件方法
  }
};
</script>

<style scoped>
@layer utilities {
  .content-auto {
    content-visibility: auto;
  }
  .scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
  .scrollbar-hide::-webkit-scrollbar {
    display: none;
  }
  .grid-cell {
    @apply border border-neutral-200 rounded-md transition-all duration-300 cursor-pointer;
  }
  .grid-cell-idle {
    @apply bg-success/10 border-success/30 hover:bg-success/20;
  }
  .grid-cell-occupied {
    @apply bg-danger/10 border-danger/30 hover:bg-danger/20;
  }
  .grid-cell-pending {
    @apply bg-warning/10 border-warning/30 hover:bg-warning/20;
  }
  .grid-cell-disabled {
    @apply bg-neutral-200 border-neutral-300 hover:bg-neutral-300;
  }
  .agv-icon {
    @apply absolute rounded-md shadow-md flex items-center justify-center cursor-pointer transition-all duration-200 z-20;
  }
  .elevator-icon {
    @apply absolute rounded-md border-2 flex flex-col items-center justify-center shadow-md z-20;
  }
  .conveyor-belt {
    @apply absolute bg-neutral-500 rounded-sm overflow-hidden;
  }
  .animated-marquee {
    animation: marquee 2s linear infinite;
  }
  .animated-path {
    stroke-dasharray: 1000;
    stroke-dashoffset: 1000;
    animation: dash 15s linear infinite;
  }
  .animated-pulse {
    animation: pulse 2s infinite;
  }
  @keyframes marquee {
    0% {
      background-position: 0 0;
    }
    100% {
      background-position: -200px 0;
    }
  }
  @keyframes dash {
    to {
      stroke-dashoffset: 0;
    }
  }
  @keyframes pulse {
    0% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.05);
    }
    100% {
      transform: scale(1);
    }
  }
}

/* 引入 Tailwind CSS */
@tailwind base;
@tailwind components;
@tailwind utilities;

/* 扩展 Tailwind 主题 */
@layer base {
  :root {
    --primary: #165DFF;
    --success: #00B42A;
    --warning: #FF7D00;
    --danger: #F53F3F;
    --info: #0FC6C2;
    --neutral-100: #F3F4F6;
    --neutral-200: #E5E7EB;
    --neutral-300: #D1D5DB;
    --neutral-400: #9CA3AF;
    --neutral-500: #6B7280;
    --neutral-600: #4B5563;
    --neutral-700: #374151;
    --neutral-800: #1F2937;
    --neutral-900: #111827;
  }
}

@layer utilities {
  .font-inter {
    font-family: 'Inter', system-ui, sans-serif;
  }
}
</style>
