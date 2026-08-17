<template>
  <el-card class="diagram-miniCard" shadow="never">
    <div slot="header" class="diagram-miniHeader">
      <div class="diagram-miniHeaderLeft">
        <span class="diagram-miniTitle">设备信息</span>
        <span class="diagram-miniBadge" v-if="device && device.code">{{ device.code }}</span>
        <span class="diagram-miniBadge is-empty" v-else>无</span>
      </div>
      <div class="diagram-miniHeaderRight" v-if="device && device.code">
        <span class="diagram-pill">
          <dict-tag :options="dict.type.is_online" :value="device.isOnline" />
        </span>
        <span class="diagram-pill is-ghost">
          <dict-tag :options="dict.type.device_state" :value="device.state" />
        </span>
      </div>
    </div>
    <div class="diagram-miniBody">
      <div v-if="device && device.code" class="diagram-kvGrid">
        <div class="diagram-kv">
          <div class="diagram-k">名称</div>
          <div class="diagram-v">{{ device.name || "-" }}</div>
        </div>
        <div class="diagram-kv">
          <div class="diagram-k">IP</div>
          <div class="diagram-v diagram-mono">{{ device.ip || "-" }}</div>
        </div>
        <div class="diagram-kv">
          <div class="diagram-k">端口</div>
          <div class="diagram-v diagram-mono">{{ device.port || "-" }}</div>
        </div>
      </div>
      <div v-else class="diagram-miniEmpty">无绑定设备</div>
    </div>
  </el-card>
</template>

<script>
export default {
  name: "DevicePanel",
  dicts: ["is_online", "device_state"],
  props: {
    device: {
      type: Object,
      default: null,
    },
  },
};
</script>

<style lang="scss" scoped>
.diagram-miniCard {
  border: 1px solid rgba(15, 23, 42, 0.12);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.diagram-miniCard:hover {
  box-shadow: 0 4px 16px rgba(2, 6, 23, 0.08);
}

.diagram-miniHeader {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.diagram-miniHeaderLeft {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.diagram-miniHeaderRight {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.diagram-miniTitle {
  font-weight: 700;
  color: rgba(15, 23, 42, 0.86);
}

.diagram-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  background: rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.18);
  color: rgba(30, 64, 175, 0.9);
  transition: all 0.2s ease;
}

.diagram-pill.is-ghost {
  background: rgba(15, 23, 42, 0.04);
  border-color: rgba(15, 23, 42, 0.1);
  color: rgba(15, 23, 42, 0.7);
}

.diagram-miniBadge {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono",
    "Courier New", monospace;
  font-size: 12px;
  color: rgba(15, 23, 42, 0.7);
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px dashed rgba(15, 23, 42, 0.18);
  background: rgba(255, 255, 255, 0.7);
}

.diagram-miniBadge.is-empty {
  color: rgba(15, 23, 42, 0.5);
  border-style: solid;
  border-color: rgba(15, 23, 42, 0.12);
}

.diagram-miniBody {
  padding: 10px 12px 12px;
  background:
    radial-gradient(500px 140px at 20% 0%, rgba(99, 102, 241, 0.12), rgba(255, 255, 255, 0) 60%),
    rgba(255, 255, 255, 0.6);
}

.diagram-kvGrid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
}

.diagram-kv {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(15, 23, 42, 0.08);
  transition: all 0.2s ease;
}

.diagram-kv:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(15, 23, 42, 0.12);
}

.diagram-k {
  color: rgba(15, 23, 42, 0.55);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.4px;
  flex-shrink: 0;
}

.diagram-v {
  color: rgba(15, 23, 42, 0.84);
  font-size: 13px;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.diagram-mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono",
    "Courier New", monospace;
}

.diagram-miniEmpty {
  padding: 18px 10px 14px;
  text-align: center;
  color: rgba(15, 23, 42, 0.55);
  background: rgba(255, 255, 255, 0.45);
  border-radius: 10px;
  border: 1px dashed rgba(15, 23, 42, 0.14);
}
</style>