<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
      @toggleClick="toggleSideBar" />
    <!-- <div>fsdf</div>  -->
    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav" />
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav" />

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <search id="header-search" class="right-menu-item" />

        <i class="el-icon-bell bell-type alarm" @click="open2"></i>

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar" />
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import Breadcrumb from "@/components/Breadcrumb";
import TopNav from "@/components/TopNav";
import Hamburger from "@/components/Hamburger";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import Search from "@/components/HeaderSearch";
import RuoYiGit from "@/components/RuoYi/Git";
import RuoYiDoc from "@/components/RuoYi/Doc";
import request from "@/utils/request";
export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc,
  },
  data() {
    return {
      AlarmInformation: null,
      timer: null,
    };
  },
  computed: {
    ...mapGetters(["sidebar", "avatar", "device"]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings;
      },
      set(val) {
        this.$store.dispatch("settings/changeSetting", {
          key: "showSettings",
          value: val,
        });
      },
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav;
      },
    },
  },
  created() {
    // this.timer = setInterval(() => {
    //   this.getDeviceError();
    // }, 3000);
  },
  destroyed() {
    clearInterval(this.timer);
  },
  methods: {
    /** 查询货主下拉树结构 */
    getDeviceError() {
      request({
        url: "/wcs-base/AlarmInformation/getLast",
        method: "get",
        params: {},
      }).then((response) => {
        if (response.code == 200) {
          if (this.AlarmInformation == null) {
            this.AlarmInformation = response.data;
          } else if (
            this.AlarmInformation.alarmInformationId !=
            response.data.alarmInformationId
          ) {
            this.AlarmInformation = response.data;
            this.open2(this.AlarmInformation.content);
          }
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    open2(msg) {
      this.$notify({
        title: "任务执行报警",
        message: msg,
        type: "warning",
        duration: 0,
      });
    },
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar");
    },
    async logout() {
      this.$confirm("确定注销并退出系统吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$store.dispatch("LogOut").then(() => {
            location.href = "/#/login";
          });
        })
        .catch(() => { });
    },
  },
};
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #0f0f1e;
  backdrop-filter: blur(15px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: all 0.3s ease;
    -webkit-tap-highlight-color: transparent;
    color: #ffffff;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #ffffff;
      vertical-align: text-bottom;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);

      &.hover-effect {
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(10px);
          color: #4facfe;
        }
      }
    }

    .bell-type {
      display: inline-block;
      padding: 0 8px;
      height: 70%;
      font-size: 20px;
      color: #ffffff;
      vertical-align: text-bottom;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      transition: all 0.3s ease;
    }

    .alarm:hover {
      color: #F56C6C;
      transform-origin: center top;
      animation: alarm 0.3s linear infinite;
      text-shadow: 0 0 8px rgba(245, 108, 108, 0.6);
    }

    @keyframes alarm {
      0% {
        transform: rotateZ(0deg);
        /*从0度开始*/
      }

      25% {
        transform: rotateZ(20deg);
      }

      50% {
        transform: rotateZ(0deg);
      }

      75% {
        transform: rotateZ(-20deg);
      }

      100% {
        transform: rotateZ(0deg);
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
          border: 2px solid rgba(255, 255, 255, 0.2);
          transition: all 0.3s ease;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

          &:hover {
            border-color: #4facfe;
            box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
            transform: scale(1.05);
          }
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
          color: #ffffff;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
          transition: all 0.3s ease;

          &:hover {
            color: #4facfe;
          }
        }
      }
    }
  }
}

// 全局下拉菜单样式覆盖
::v-deep .el-dropdown-menu {
  background: rgba(15, 15, 30, 0.95) !important;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  border-radius: 8px !important;

  .el-dropdown-menu__item {
    color: #ffffff !important;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;

    &:hover {
        background: rgba(79, 172, 254, 0.2) !important;
        color: #4facfe !important;
      }

    &.is-disabled {
      color: #8a9ba8 !important;
      background: transparent !important;
    }
  }
}

</style>
