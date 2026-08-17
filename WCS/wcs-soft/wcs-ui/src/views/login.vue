<template>
  <div class="login">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration">
      <div class="grid-overlay"></div>
      <div class="tech-lines">
        <div class="line line-1"></div>
        <div class="line line-2"></div>
        <div class="line line-3"></div>
      </div>
      <!-- 粒子系统 -->
      <div class="particles-container">
        <div class="particle" v-for="n in 50" :key="n" :style="getParticleStyle(n)"></div>
      </div>
      <!-- 连接线 -->
      <div class="connections">
        <div class="connection" v-for="n in 20" :key="n" :style="getConnectionStyle(n)"></div>
      </div>
      <!-- 数据流 -->
      <div class="data-streams">
        <div class="data-stream" v-for="n in 8" :key="n" :style="getDataStreamStyle(n)"></div>
      </div>
      <!-- 工业自动化动画 -->
      <div class="industrial-animation">
        <!-- 机械臂 -->
        <div class="robot-arms">
          <div class="robot-arm" v-for="n in 3" :key="n" :style="getRobotArmStyle(n)">
            <div class="arm-base"></div>
            <div class="arm-segment segment-1"></div>
            <div class="arm-segment segment-2"></div>
            <div class="arm-gripper"></div>
          </div>
        </div>
        <!-- 传送带 -->
        <div class="conveyor-belts">
          <div class="conveyor-belt" v-for="n in 4" :key="n" :style="getConveyorStyle(n)">
            <div class="belt-line"></div>
            <div class="belt-item" v-for="i in 3" :key="i"></div>
          </div>
        </div>
        <!-- 传感器扫描 -->
        <div class="sensor-scans">
          <div class="sensor-scan" v-for="n in 6" :key="n" :style="getSensorStyle(n)">
            <div class="scan-beam"></div>
            <div class="scan-dot"></div>
          </div>
        </div>
        <!-- AGV小车系统 -->
        <div class="agv-system">
          <!-- 路线轨迹 -->
          <div class="agv-routes">
            <div class="route-path" v-for="n in 3" :key="n" :style="getRouteStyle(n)">
              <div class="route-line"></div>
              <div class="route-node" v-for="i in 4" :key="i"></div>
            </div>
          </div>
          <!-- AGV小车 -->
          <div class="agv-vehicles">
            <div class="agv-vehicle" v-for="n in 3" :key="n" :style="getAGVStyle(n)">
              <div class="agv-body"></div>
              <div class="agv-wheels">
                <div class="wheel wheel-left"></div>
                <div class="wheel wheel-right"></div>
              </div>
              <div class="agv-light"></div>
              <div class="agv-trail"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 登录表单容器 -->
    <div class="login-container">
      <div class="login-card">
        <!-- 系统标题 -->
        <div class="system-header">
          <div class="logo-container">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
                <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
                <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
          <h1 class="system-title">WCS仓库控制系统</h1>
          <p class="system-subtitle">Warehouse Control System</p>
        </div>

        <!-- 登录表单 -->
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <div class="input-group">
              <div class="input-icon">
                <svg-icon icon-class="user" />
              </div>
              <el-input 
                v-model="loginForm.username" 
                type="text" 
                auto-complete="off" 
                placeholder="请输入账号"
                class="modern-input">
              </el-input>
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="input-group">
              <div class="input-icon">
                <svg-icon icon-class="password" />
              </div>
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                auto-complete="off" 
                placeholder="请输入密码"
                class="modern-input"
                @keyup.enter.native="handleLogin">
              </el-input>
            </div>
          </el-form-item>
          
          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-group">
              <div class="input-group captcha-input">
                <div class="input-icon">
                  <svg-icon icon-class="validCode" />
                </div>
                <el-input 
                  v-model="loginForm.code" 
                  auto-complete="off" 
                  placeholder="请输入验证码"
                  class="modern-input"
                  @keyup.enter.native="handleLogin">
                </el-input>
              </div>
              <div class="captcha-image" @click="getCode">
                <img :src="codeUrl" class="captcha-img" />
                <div class="captcha-refresh">
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M23 4V10H17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M20.49 15A9 9 0 1 1 5.64 5.64L23 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
              </div>
            </div>
          </el-form-item>
          
          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe" class="remember-checkbox">
              记住密码
            </el-checkbox>
            <router-link v-if="register" class="register-link" :to="'/register'">
              立即注册
            </router-link>
          </div>
          
          <el-form-item class="login-button-item">
            <el-button 
              :loading="loading" 
              type="primary" 
              class="login-button"
              @click.native.prevent="handleLogin">
              <span v-if="!loading">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M15 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 17L15 12L10 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M15 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                登录系统
              </span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <!-- 底部信息 -->
    <div class="login-footer">
      <div class="footer-content">
        <p>&copy; 2025 WCS仓库控制系统. 工业4.0智能仓储解决方案</p>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";
import LangSelect from '@/components/LangSelect'

export default {
  name: "Login",
  components: { LangSelect },
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: true,
        code: "",
        uuid: "",
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }],
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined,
      // 粒子系统数据
      particles: [],
      connections: [],
      dataStreams: [],
      // 工业动画数据
      robotArms: [],
      conveyorBelts: [],
      sensorScans: [],
      // AGV系统数据
      agvRoutes: [],
      agvVehicles: [],
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true,
    },
  },
  created() {
    this.getCode();
    this.getCookie();
    this.initParticles();
  },
  methods: {
    getCode() {
      getCodeImg().then((res) => {
        this.captchaEnabled =
          res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get("rememberMe");
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password:
          password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), {
              expires: 30,
            });
            Cookies.set("rememberMe", this.loginForm.rememberMe, {
              expires: 30,
            });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove("rememberMe");
          }
          this.$store.dispatch("Login", this.loginForm).then((res) => {
            if (res.code == 200) {
              if (res.res_code && res.res_code === 1001) {
                // 判断到后端接口返回的重置密码标识码
                // 先设置token
                localStorage.setItem("reset_token", res.token);
                // 重定向到重置密码页，并带上校验参数
                this.redirect =
                  "/reset?" +
                  "sign=" +
                  res.reset_sign +
                  "&username=" +
                  this.loginForm.username;
              }
              this.$router.push({ path: this.redirect || "/index" }).catch(() => { });
            } else {
              this.$modal.msgError(res.msg);
              this.loading = false;
              if (this.captchaEnabled) {
                this.getCode();
              }
            }
          });
          // .catch(() => {

          // });
        }
      });
    },
    // 初始化粒子系统
    initParticles() {
      this.particles = [];
      this.connections = [];
      this.dataStreams = [];
      
      // 生成有序粒子数据
      for (let i = 0; i < 20; i++) {
        this.particles.push({
          id: i,
          x: (i % 5) * 20 + 10, // 5列布局
          y: Math.floor(i / 5) * 25 + 15, // 4行布局
          size: 2,
          speedX: 0,
          speedY: 0,
          opacity: 0.3,
          color: '#00ffff'
        });
      }
      
      // 生成有序连接线数据
      for (let i = 0; i < 8; i++) {
        this.connections.push({
          id: i,
          x1: 10 + (i % 4) * 30,
          y1: 20 + Math.floor(i / 4) * 60,
          x2: 10 + (i % 4) * 30,
          y2: 80 + Math.floor(i / 4) * 60,
          opacity: 0.2,
          delay: i * 0.5
        });
      }
      
      // 生成有序数据流
      for (let i = 0; i < 4; i++) {
        this.dataStreams.push({
          id: i,
          x: 15 + i * 25,
          y: 30,
          width: 150,
          height: 1,
          delay: i * 0.8,
          speed: 0.4
        });
      }
      
      // 生成工业动画数据
      this.robotArms = [];
      this.conveyorBelts = [];
      this.sensorScans = [];
      this.agvRoutes = [];
      this.agvVehicles = [];
      
      // 机械臂数据 - 有序布局
      for (let i = 0; i < 2; i++) {
        this.robotArms.push({
          id: i,
          x: 20 + i * 60,
          y: 25,
          angle1: 0,
          angle2: 0,
          delay: i * 2,
          speed: 0.3
        });
      }
      
      // 传送带数据 - 有序布局
      for (let i = 0; i < 2; i++) {
        this.conveyorBelts.push({
          id: i,
          x: 15 + i * 70,
          y: 70,
          length: 200,
          angle: 0,
          delay: i * 1.5,
          speed: 0.4
        });
      }
      
      // 传感器扫描数据 - 有序布局
      for (let i = 0; i < 4; i++) {
        this.sensorScans.push({
          id: i,
          x: 15 + (i % 2) * 70,
          y: 15 + Math.floor(i / 2) * 70,
          angle: 0,
          delay: i * 1.2,
          speed: 0.4
        });
      }
      
      // AGV路线数据 - 有序布局
      for (let i = 0; i < 2; i++) {
        this.agvRoutes.push({
          id: i,
          startX: 10,
          startY: 50 + i * 20,
          endX: 90,
          endY: 50 + i * 20,
          delay: i * 3,
          speed: 0.3
        });
      }
      
      // AGV小车数据 - 有序布局
      for (let i = 0; i < 2; i++) {
        this.agvVehicles.push({
          id: i,
          routeId: i,
          progress: i * 50,
          angle: 0,
          delay: i * 4,
          speed: 0.4,
          color: i === 0 ? '#00ffff' : '#0080ff'
        });
      }
      
      this.animateParticles();
    },
    
    // 动画粒子
    animateParticles() {
      const animate = () => {
        this.particles.forEach(particle => {
          particle.x += particle.speedX;
          particle.y += particle.speedY;
          
          // 边界检测
          if (particle.x < 0 || particle.x > 100) particle.speedX *= -1;
          if (particle.y < 0 || particle.y > 100) particle.speedY *= -1;
          
          // 保持粒子在边界内
          particle.x = Math.max(0, Math.min(100, particle.x));
          particle.y = Math.max(0, Math.min(100, particle.y));
        });
        
        requestAnimationFrame(animate);
      };
      animate();
    },
    
    // 获取粒子样式
    getParticleStyle(index) {
      if (!this.particles[index]) return {};
      const particle = this.particles[index];
      return {
        left: particle.x + '%',
        top: particle.y + '%',
        width: particle.size + 'px',
        height: particle.size + 'px',
        opacity: particle.opacity,
        backgroundColor: particle.color,
        animationDelay: Math.random() * 2 + 's'
      };
    },
    
    // 获取连接线样式
    getConnectionStyle(index) {
      if (!this.connections[index]) return {};
      const connection = this.connections[index];
      const length = Math.sqrt(
        Math.pow(connection.x2 - connection.x1, 2) + 
        Math.pow(connection.y2 - connection.y1, 2)
      );
      const angle = Math.atan2(connection.y2 - connection.y1, connection.x2 - connection.x1) * 180 / Math.PI;
      
      return {
        left: connection.x1 + '%',
        top: connection.y1 + '%',
        width: length + '%',
        opacity: connection.opacity,
        transform: `rotate(${angle}deg)`,
        animationDelay: connection.delay + 's'
      };
    },
    
    // 获取数据流样式
    getDataStreamStyle(index) {
      if (!this.dataStreams[index]) return {};
      const stream = this.dataStreams[index];
      return {
        left: stream.x + '%',
        top: stream.y + '%',
        width: stream.width + 'px',
        height: stream.height + 'px',
        animationDelay: stream.delay + 's',
        animationDuration: (3 / stream.speed) + 's'
      };
    },
    
    // 获取机械臂样式
    getRobotArmStyle(index) {
      if (!this.robotArms[index]) return {};
      const arm = this.robotArms[index];
      return {
        left: arm.x + '%',
        top: arm.y + '%',
        '--angle1': arm.angle1 + 'deg',
        '--angle2': arm.angle2 + 'deg',
        animationDelay: arm.delay + 's',
        animationDuration: (4 / arm.speed) + 's'
      };
    },
    
    // 获取传送带样式
    getConveyorStyle(index) {
      if (!this.conveyorBelts[index]) return {};
      const belt = this.conveyorBelts[index];
      return {
        left: belt.x + '%',
        top: belt.y + '%',
        width: belt.length + 'px',
        transform: `rotate(${belt.angle}deg)`,
        animationDelay: belt.delay + 's',
        animationDuration: (3 / belt.speed) + 's'
      };
    },
    
    // 获取传感器样式
    getSensorStyle(index) {
      if (!this.sensorScans[index]) return {};
      const sensor = this.sensorScans[index];
      return {
        left: sensor.x + '%',
        top: sensor.y + '%',
        transform: `rotate(${sensor.angle}deg)`,
        animationDelay: sensor.delay + 's',
        animationDuration: (5 / sensor.speed) + 's'
      };
    },
    
    // 获取AGV路线样式
    getRouteStyle(index) {
      if (!this.agvRoutes[index]) return {};
      const route = this.agvRoutes[index];
      return {
        animationDelay: route.delay + 's',
        animationDuration: (6 / route.speed) + 's'
      };
    },
    
    // 获取AGV小车样式
    getAGVStyle(index) {
      if (!this.agvVehicles[index]) return {};
      const agv = this.agvVehicles[index];
      const route = this.agvRoutes[agv.routeId];
      if (!route) return {};
      
      // 简化的直线路径计算
      const t = agv.progress / 100;
      const x = route.startX + (route.endX - route.startX) * t;
      const y = route.startY + (route.endY - route.startY) * t;
      
      return {
        left: x + '%',
        top: y + '%',
        transform: `rotate(${agv.angle}deg)`,
        '--agv-color': agv.color,
        animationDelay: agv.delay + 's',
        animationDuration: (8 / agv.speed) + 's'
      };
    },
    
  },
};
</script>

<style rel="stylesheet/scss" lang="scss">
// 主容器
.login {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f1419 0%, #1a2332 50%, #0f1419 100%);
  overflow: hidden;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

// 背景装饰
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  
  .grid-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: 
      linear-gradient(rgba(0, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0, 255, 255, 0.03) 1px, transparent 1px);
    background-size: 50px 50px;
    animation: gridMove 20s linear infinite;
  }
  
  .tech-lines {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    
    .line {
      position: absolute;
      background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.4), transparent);
      height: 1px;
      animation: techLineMove 8s linear infinite;
      
      &.line-1 {
        top: 20%;
        width: 100%;
        animation-delay: 0s;
      }
      
      &.line-2 {
        top: 60%;
        width: 80%;
        left: 10%;
        animation-delay: 2s;
      }
      
      &.line-3 {
        top: 80%;
        width: 60%;
        left: 20%;
        animation-delay: 4s;
      }
    }
  }
  
  // 粒子系统
  .particles-container {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    
    .particle {
      position: absolute;
      border-radius: 50%;
      background: #00ffff;
      box-shadow: 0 0 4px rgba(0, 255, 255, 0.6);
      animation: particlePulse 4s ease-in-out infinite;
      
      &::before {
        content: '';
        position: absolute;
        top: -2px;
        left: -2px;
        right: -2px;
        bottom: -2px;
        border-radius: 50%;
        background: radial-gradient(circle, rgba(0, 255, 255, 0.3) 0%, transparent 70%);
        animation: particleGlow 3s ease-in-out infinite;
      }
    }
  }
  
  // 连接线
  .connections {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    
    .connection {
      position: absolute;
      height: 1px;
      background: linear-gradient(180deg, transparent, rgba(0, 255, 255, 0.4), transparent);
      transform-origin: top center;
      animation: connectionFlow 6s ease-in-out infinite;
      
      &::before {
        content: '';
        position: absolute;
        top: -1px;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.2), transparent);
        border-radius: 2px;
      }
    }
  }
  
  // 数据流
  .data-streams {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    
    .data-stream {
      position: absolute;
      background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.8), transparent);
      border-radius: 2px;
      animation: dataFlow 3s linear infinite;
      
      &::before {
        content: '';
        position: absolute;
        top: -1px;
        left: 0;
        right: 0;
        bottom: -1px;
        background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.3), transparent);
        border-radius: 3px;
        filter: blur(1px);
      }
    }
  }
  
  // 工业自动化动画
  .industrial-animation {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    
    // 机械臂
    .robot-arms {
      .robot-arm {
        position: absolute;
        width: 60px;
        height: 60px;
        animation: robotArmMove 4s ease-in-out infinite;
        
        .arm-base {
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 12px;
          height: 12px;
          background: linear-gradient(45deg, #333, #666);
          border-radius: 50%;
          box-shadow: 0 0 8px rgba(0, 255, 255, 0.3);
        }
        
        .arm-segment {
          position: absolute;
          background: linear-gradient(90deg, #444, #777);
          border-radius: 2px;
          box-shadow: 0 0 4px rgba(0, 255, 255, 0.2);
          
          &.segment-1 {
            width: 20px;
            height: 4px;
            bottom: 6px;
            left: 50%;
            transform-origin: left center;
            transform: translateX(-50%) rotate(var(--angle1, 0deg));
            animation: armSegment1Rotate 4s ease-in-out infinite;
          }
          
          &.segment-2 {
            width: 16px;
            height: 3px;
            bottom: 8px;
            left: 50%;
            transform-origin: left center;
            transform: translateX(-50%) rotate(var(--angle2, 0deg));
            animation: armSegment2Rotate 4s ease-in-out infinite;
          }
        }
        
        .arm-gripper {
          position: absolute;
          bottom: 10px;
          left: 50%;
          transform: translateX(-50%);
          width: 6px;
          height: 6px;
          background: radial-gradient(circle, #00ffff, rgba(0, 255, 255, 0.6));
          border-radius: 50%;
          box-shadow: 0 0 6px rgba(0, 255, 255, 0.8);
          animation: gripperPulse 2s ease-in-out infinite;
        }
      }
    }
    
    // 传送带
    .conveyor-belts {
      .conveyor-belt {
        position: absolute;
        height: 8px;
        animation: conveyorMove 3s linear infinite;
        
        .belt-line {
          position: absolute;
          top: 50%;
          left: 0;
          right: 0;
          height: 2px;
          background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.6), transparent);
          transform: translateY(-50%);
        }
        
        .belt-item {
          position: absolute;
          top: 50%;
          width: 6px;
          height: 6px;
          background: linear-gradient(45deg, #00ffff, rgba(0, 255, 255, 0.4));
          border-radius: 2px;
          transform: translateY(-50%);
          animation: beltItemMove 3s linear infinite;
          
          &:nth-child(2) {
            animation-delay: 1s;
          }
          
          &:nth-child(3) {
            animation-delay: 2s;
          }
        }
      }
    }
    
    // 传感器扫描
    .sensor-scans {
      .sensor-scan {
        position: absolute;
        width: 20px;
        height: 20px;
        animation: sensorRotate 5s linear infinite;
        
        .scan-beam {
          position: absolute;
          top: 50%;
          left: 50%;
          width: 1px;
          height: 30px;
          background: linear-gradient(180deg, transparent, rgba(0, 255, 255, 0.8), transparent);
          transform-origin: center bottom;
          transform: translate(-50%, -50%);
          animation: scanBeamRotate 5s linear infinite;
        }
        
        .scan-dot {
          position: absolute;
          top: 50%;
          left: 50%;
          width: 4px;
          height: 4px;
          background: radial-gradient(circle, #00ffff, transparent);
          border-radius: 50%;
          transform: translate(-50%, -50%);
          box-shadow: 0 0 8px rgba(0, 255, 255, 0.6);
          animation: scanDotPulse 2s ease-in-out infinite;
        }
      }
    }
    
    // AGV系统
    .agv-system {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      
      // AGV路线
      .agv-routes {
        .route-path {
          position: absolute;
          width: 100%;
          height: 100%;
          animation: routeGlow 6s ease-in-out infinite;
          
          .route-line {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            opacity: 0;
          }
          
          .route-node {
            position: absolute;
            width: 4px;
            height: 4px;
            background: radial-gradient(circle, #00ffff, transparent);
            border-radius: 50%;
            box-shadow: 0 0 6px rgba(0, 255, 255, 0.8);
            animation: nodeBlink 2s ease-in-out infinite;
            
            &:nth-child(2) {
              top: 20%;
              left: 30%;
              animation-delay: 0.5s;
            }
            
            &:nth-child(3) {
              top: 50%;
              left: 60%;
              animation-delay: 1s;
            }
            
            &:nth-child(4) {
              top: 80%;
              left: 20%;
              animation-delay: 1.5s;
            }
            
            &:nth-child(5) {
              top: 30%;
              left: 80%;
              animation-delay: 2s;
            }
          }
        }
      }
      
      // AGV小车
      .agv-vehicles {
        .agv-vehicle {
          position: absolute;
          width: 20px;
          height: 12px;
          animation: agvMove 8s linear infinite;
          
          .agv-body {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(45deg, var(--agv-color, #00ffff), rgba(0, 255, 255, 0.6));
            border-radius: 6px;
            box-shadow: 0 0 8px rgba(0, 255, 255, 0.6);
            animation: agvBodyPulse 3s ease-in-out infinite;
          }
          
          .agv-wheels {
            position: absolute;
            bottom: -2px;
            left: 0;
            right: 0;
            height: 4px;
            
            .wheel {
              position: absolute;
              width: 3px;
              height: 3px;
              background: linear-gradient(45deg, #333, #666);
              border-radius: 50%;
              animation: wheelRotate 1s linear infinite;
              
              &.wheel-left {
                left: 2px;
              }
              
              &.wheel-right {
                right: 2px;
              }
            }
          }
          
          .agv-light {
            position: absolute;
            top: -2px;
            left: 50%;
            transform: translateX(-50%);
            width: 2px;
            height: 2px;
            background: radial-gradient(circle, #ffffff, rgba(255, 255, 255, 0.6));
            border-radius: 50%;
            box-shadow: 0 0 4px rgba(255, 255, 255, 0.8);
            animation: lightBlink 2s ease-in-out infinite;
          }
          
          .agv-trail {
            position: absolute;
            top: 50%;
            left: -10px;
            width: 8px;
            height: 1px;
            background: linear-gradient(90deg, transparent, var(--agv-color, #00ffff));
            transform: translateY(-50%);
            opacity: 0.6;
            animation: trailFade 1s ease-out infinite;
          }
        }
      }
    }
  }
}

// 登录容器
.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

// 登录卡片
.login-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 255, 255, 0.2);
  border-radius: 18px;
  padding: 36px 32px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
  // 黄金分割比例：宽度400px，高度约247px (400/1.618)
  min-height: 247px;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.8), transparent);
  }
}

// 系统标题
.system-header {
  text-align: center;
  margin-bottom: 32px;
  
  .logo-container {
    margin-bottom: 18px;
    
    .logo-icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 56px;
      height: 56px;
      background: linear-gradient(135deg, #00ffff, #0080ff);
      border-radius: 14px;
      margin: 0 auto;
      box-shadow: 0 10px 20px rgba(0, 255, 255, 0.3);
      
      svg {
        width: 30px;
        height: 30px;
        color: white;
      }
    }
  }
  
  .system-title {
    font-size: 26px;
    font-weight: 700;
    color: #ffffff;
    margin: 0 0 6px 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    letter-spacing: 1px;
  }
  
  .system-subtitle {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);
    margin: 0;
    font-weight: 300;
    letter-spacing: 1.5px;
    text-transform: uppercase;
  }
}

// 表单样式
.login-form {
  .el-form-item {
    margin-bottom: 20px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

// 输入框组
.input-group {
  position: relative;
  display: flex;
  align-items: center;
  
  .input-icon {
    position: absolute;
    left: 14px;
    z-index: 3;
    color: rgba(255, 255, 255, 0.6);
    font-size: 15px;
    transition: color 0.3s ease;
  }
  
  .modern-input {
    .el-input__inner {
      height: 42px;
      background: rgba(255, 255, 255, 0.08);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 10px;
      color: #ffffff;
      font-size: 15px;
      padding-left: 45px;
      transition: all 0.3s ease;
      
      &::placeholder {
        color: rgba(255, 255, 255, 0.4);
      }
      
      &:focus {
        background: rgba(255, 255, 255, 0.12);
        border-color: #00ffff;
        box-shadow: 0 0 0 2px rgba(0, 255, 255, 0.2);
      }
      
      &:hover {
        border-color: rgba(0, 255, 255, 0.4);
      }
    }
  }
  
  &:focus-within .input-icon {
    color: #00ffff;
  }
}

// 验证码组
.captcha-group {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  
  .captcha-input {
    flex: 1;
  }
  
  .captcha-image {
    position: relative;
    width: 120px;
    height: 42px;
    border-radius: 10px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #00ffff;
      box-shadow: 0 0 0 2px rgba(0, 255, 255, 0.2);
    }
    
    .captcha-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .captcha-refresh {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 20px;
      height: 20px;
      background: rgba(0, 0, 0, 0.6);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s ease;
      
      svg {
        width: 12px;
        height: 12px;
        color: #00ffff;
      }
    }
    
    &:hover .captcha-refresh {
      opacity: 1;
    }
  }
}

// 表单选项
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  .remember-checkbox {
    .el-checkbox__label {
      color: rgba(255, 255, 255, 0.8);
      font-size: 14px;
    }
    
    .el-checkbox__input.is-checked .el-checkbox__inner {
      background-color: #00ffff;
      border-color: #00ffff;
    }
  }
  
  .register-link {
    color: #00ffff;
    text-decoration: none;
    font-size: 14px;
    transition: color 0.3s ease;
    
    &:hover {
      color: #ffffff;
    }
  }
}

// 登录按钮
.login-button-item {
  margin-bottom: 0 !important;
}

.login-button {
  width: 100%;
  height: 42px;
  background: linear-gradient(135deg, #00ffff, #0080ff);
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.5s ease;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px rgba(0, 255, 255, 0.4);
    
    &::before {
      left: 100%;
    }
  }
  
  &:active {
    transform: translateY(0);
  }
  
  span {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    
    svg {
      width: 16px;
      height: 16px;
    }
  }
  
  &.is-loading {
    background: linear-gradient(135deg, #666, #888);
  }
}

// 底部
.login-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 2;
  padding: 20px;
  
  .footer-content {
    text-align: center;
    
    p {
      color: rgba(255, 255, 255, 0.4);
      font-size: 12px;
      margin: 0;
      letter-spacing: 0.5px;
    }
  }
}

// 动画
@keyframes gridMove {
  0% { transform: translate(0, 0); }
  100% { transform: translate(50px, 50px); }
}

@keyframes techLineMove {
  0% { transform: translateX(-100%); opacity: 0; }
  50% { opacity: 1; }
  100% { transform: translateX(100%); opacity: 0; }
}

@keyframes particlePulse {
  0%, 100% { 
    transform: scale(1);
    opacity: 0.3;
  }
  50% { 
    transform: scale(1.2);
    opacity: 0.6;
  }
}

@keyframes particleGlow {
  0%, 100% { 
    opacity: 0.3;
    transform: scale(1);
  }
  50% { 
    opacity: 0.8;
    transform: scale(1.2);
  }
}

@keyframes connectionFlow {
  0%, 100% { 
    opacity: 0.2;
    transform: scaleY(1);
  }
  50% { 
    opacity: 0.4;
    transform: scaleY(1.2);
  }
}

@keyframes dataFlow {
  0% { 
    transform: translateX(-100%);
    opacity: 0;
  }
  10% { 
    opacity: 1;
  }
  90% { 
    opacity: 1;
  }
  100% { 
    transform: translateX(100%);
    opacity: 0;
  }
}

// 机械臂动画
@keyframes robotArmMove {
  0%, 100% { 
    transform: translateY(0px);
  }
  50% { 
    transform: translateY(-5px);
  }
}

@keyframes armSegment1Rotate {
  0%, 100% { 
    transform: translateX(-50%) rotate(0deg);
  }
  25% { 
    transform: translateX(-50%) rotate(45deg);
  }
  50% { 
    transform: translateX(-50%) rotate(90deg);
  }
  75% { 
    transform: translateX(-50%) rotate(135deg);
  }
}

@keyframes armSegment2Rotate {
  0%, 100% { 
    transform: translateX(-50%) rotate(0deg);
  }
  33% { 
    transform: translateX(-50%) rotate(-30deg);
  }
  66% { 
    transform: translateX(-50%) rotate(-60deg);
  }
}

@keyframes gripperPulse {
  0%, 100% { 
    transform: translateX(-50%) scale(1);
    box-shadow: 0 0 6px rgba(0, 255, 255, 0.8);
  }
  50% { 
    transform: translateX(-50%) scale(1.2);
    box-shadow: 0 0 12px rgba(0, 255, 255, 1);
  }
}

// 传送带动画
@keyframes conveyorMove {
  0% { 
    transform: translateX(-100%);
  }
  100% { 
    transform: translateX(100%);
  }
}

@keyframes beltItemMove {
  0% { 
    left: 0%;
    opacity: 0;
  }
  10% { 
    opacity: 1;
  }
  90% { 
    opacity: 1;
  }
  100% { 
    left: 100%;
    opacity: 0;
  }
}

// 传感器扫描动画
@keyframes sensorRotate {
  0% { 
    transform: rotate(0deg);
  }
  100% { 
    transform: rotate(360deg);
  }
}

@keyframes scanBeamRotate {
  0% { 
    transform: translate(-50%, -50%) rotate(0deg);
    opacity: 0.8;
  }
  50% { 
    opacity: 1;
  }
  100% { 
    transform: translate(-50%, -50%) rotate(360deg);
    opacity: 0.8;
  }
}

@keyframes scanDotPulse {
  0%, 100% { 
    transform: translate(-50%, -50%) scale(1);
    box-shadow: 0 0 8px rgba(0, 255, 255, 0.6);
  }
  50% { 
    transform: translate(-50%, -50%) scale(1.3);
    box-shadow: 0 0 16px rgba(0, 255, 255, 0.9);
  }
}

// AGV系统动画
@keyframes routeGlow {
  0%, 100% { 
    opacity: 0.4;
  }
  50% { 
    opacity: 0.8;
  }
}

@keyframes routePulse {
  0%, 100% { 
    transform: scale(1);
  }
  50% { 
    transform: scale(1.05);
  }
}

@keyframes nodeBlink {
  0%, 100% { 
    opacity: 0.6;
    transform: scale(1);
  }
  50% { 
    opacity: 1;
    transform: scale(1.2);
  }
}

@keyframes agvMove {
  0% { 
    transform: translateX(0) rotate(0deg);
  }
  25% { 
    transform: translateX(25%) rotate(5deg);
  }
  50% { 
    transform: translateX(50%) rotate(0deg);
  }
  75% { 
    transform: translateX(75%) rotate(-5deg);
  }
  100% { 
    transform: translateX(100%) rotate(0deg);
  }
}

@keyframes agvBodyPulse {
  0%, 100% { 
    transform: scale(1);
    box-shadow: 0 0 8px rgba(0, 255, 255, 0.6);
  }
  50% { 
    transform: scale(1.05);
    box-shadow: 0 0 12px rgba(0, 255, 255, 0.8);
  }
}

@keyframes wheelRotate {
  0% { 
    transform: rotate(0deg);
  }
  100% { 
    transform: rotate(360deg);
  }
}

@keyframes lightBlink {
  0%, 100% { 
    opacity: 0.8;
    box-shadow: 0 0 4px rgba(255, 255, 255, 0.8);
  }
  50% { 
    opacity: 1;
    box-shadow: 0 0 8px rgba(255, 255, 255, 1);
  }
}

@keyframes trailFade {
  0% { 
    opacity: 0.8;
    transform: translateY(-50%) scaleX(1);
  }
  100% { 
    opacity: 0;
    transform: translateY(-50%) scaleX(0.5);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 15px;
  }
  
  .login-card {
    padding: 28px 24px;
    min-height: 220px;
  }
  
  .system-title {
    font-size: 22px;
  }
  
  .captcha-group {
    flex-direction: column;
    
    .captcha-image {
      width: 100%;
      height: 50px;
    }
  }
}

// Element UI 样式覆盖
.el-form-item__error {
  color: #ff6b6b;
  font-size: 12px;
  margin-top: 5px;
}

.el-checkbox__inner {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}

.el-checkbox__inner:hover {
  border-color: #00ffff;
}
</style>
