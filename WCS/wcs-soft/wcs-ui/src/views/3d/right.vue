<template>
  <div class="right-container">
    <!-- <Warnning></Warnning> -->
  </div>
</template>

<script> 
import Warnning from "./Warnning";
export default {
  name: "Index",

  data() {
    return {};
  },
  components: { 
    Warnning,
  },
  methods: {},
};
</script>

<style lang="scss" scoped>
.right-container {
  width: 20vw;
  height: 95vh;
  padding: 10px 10px 10px 10px;
  // background-color: rgba(0, 0, 0, 0.5);
  // background: linear-gradient(to left, rgba(0, 0, 0, 1), rgba(0, 0, 0, 0));
}
</style>
