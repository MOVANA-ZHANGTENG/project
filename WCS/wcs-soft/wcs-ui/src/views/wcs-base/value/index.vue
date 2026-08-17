<template>
  <div class="app-container modern-container">
    <!-- 设备选择区域 -->
    <div class="device-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="el-icon-cpu"></i>
          设备控制中心
        </h3>
        <div class="header-decoration"></div>
      </div>

      <!-- 设备类型选择区域 -->
      <div class="device-type-section">
        <div class="type-section-header">
          <i class="el-icon-s-grid"></i>
          <span>设备类型</span>
        </div>
        <div class="type-selector">
          <el-tag :type="selectedType === 'all' ? 'primary' : 'info'" :closable="false" @click="selectDeviceType('all')"
            :class="['type-tag', { 'selected': selectedType === 'all' }]">
            全部设备
          </el-tag>
          <el-tag v-for="dict in dict.type.device_type" :key="dict.value"
            :type="selectedType === dict.value ? 'primary' : 'info'" :closable="false"
            @click="selectDeviceType(dict.value)" :class="['type-tag', { 'selected': selectedType === dict.value }]">
            {{ dict.label }}
          </el-tag>
        </div>
      </div>

      <el-row :gutter="20">
        <el-col :span="4" v-for="device in filteredDevices" :key="device.id">
          <div :class="['device-item', { 'device-selected': device.code == deviceCode }]"
            @click="deviceCode = device.code; getList(device.code)">
            <div class="device-icon">
              <i class="el-icon-connection"></i>
            </div>
            <div class="device-info">
              <div class="device-code">【{{ device.code }}】</div>
              <div class="device-name">{{ device.name }}</div>
              <div class="device-status">
                <span class="status-dot" :class="{ 'online': device.isOnline, 'offline': !device.isOnline }"></span>
                <span class="status-text">{{ device.isOnline ? '在线' : '离线' }}</span>
              </div>
            </div>
            <div v-if="device.code == deviceCode" class="selected-indicator">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </el-col>

        <el-col :span="4">
          <div class="control-panel">
            <div class="control-item">
              <div class="control-label">
                <i class="el-icon-refresh"></i>
                自动读取
              </div>
              <el-switch v-model="autoRead" active-color="#667eea" inactive-color="#f56c6c" class="modern-switch" />
            </div>
            <div class="control-item">
              <div class="control-label">
                <i class="el-icon-setting"></i>
                调试模式
              </div>
              <el-switch v-model="changeMode" active-color="#667eea" inactive-color="#f56c6c" class="modern-switch" />
            </div>
          </div>
        </el-col>
      </el-row>

    </div>

    <!-- 数据通信区域 -->
    <div v-show="changeMode && deviceCode != null" class="data-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="el-icon-data-line"></i>
          数据通信监控
        </h3>
        <div class="header-decoration"></div>
      </div>

      <el-row :gutter="20">
        <el-col :span="12">
          <div class="data-panel wcs-to-plc">
            <div class="panel-header">
              <div class="panel-title">
                <i class="el-icon-upload2"></i>
                <span>WCS → PLC</span>
                <div class="data-flow-indicator">
                  <div class="flow-arrow"></div>
                </div>
              </div>
              <div class="panel-actions">
                <el-button type="primary" size="mini" icon="el-icon-upload" @click="writePLCValueList"
                  class="modern-btn">
                  批量写入
                </el-button>
                <el-button type="success" size="mini" icon="el-icon-plus" @click="add(1)" class="modern-btn">
                  新增
                </el-button>
              </div>
            </div>
            <div class="data-list">
              <template v-for="value in valueList">
                <div v-if="parseInt(value.type) !== 0" class="data-item compact-item" :key="value.id">
                  <div class="item-content">
                    <div class="item-icon">
                      <i class="el-icon-connection"></i>
                    </div>
                    <div class="item-info">
                      <span class="item-code">【{{ value.code }}】</span>
                      <span class="item-name">{{ value.name }}</span>
                    </div>
                    <div class="item-type">
                      <el-tag size="mini" :type="getTypeTagType(value.javaType)">
                        {{ getJavaTypeName(value.javaType) }}
                      </el-tag>
                    </div>
                    <div class="item-controls">
                      <!-- 布尔类型 -->
                      <el-select v-if="value.javaType == 0" v-model="value.writeDeviceValue"
                        :placeholder="value.readValue" clearable size="mini" class="compact-input">
                        <el-option key="true" value="true" label="true" />
                        <el-option key="false" value="false" label="false" />
                      </el-select>

                      <!-- 数字类型 -->
                      <el-input-number v-if="[1, 2, 3, 4, 5, 6].includes(value.javaType)"
                        v-model="value.writeDeviceValue" :step="[5, 6].includes(value.javaType) ? 0.1 : 1"
                        :precision="[5, 6].includes(value.javaType) ? 2 : 0" :placeholder="value.readValue" size="mini"
                        class="compact-input" />

                      <!-- 字符串类型 -->
                      <el-input v-if="[7, 8, 9].includes(value.javaType)" v-model="value.writeDeviceValue"
                        :placeholder="value.readValue" clearable size="mini" class="compact-input" />
                    </div>
                    <el-button type="primary" size="mini" @click.stop="write(value)" class="write-btn compact-btn"
                      :loading="value.writing">
                      <i class="el-icon-upload"></i>
                    </el-button>
                  </div>
                </div>
              </template>
            </div>

          </div>
        </el-col>

        <el-col :span="12">
          <div class="data-panel plc-to-wcs">
            <div class="panel-header">
              <div class="panel-title">
                <i class="el-icon-download"></i>
                <span>PLC → WCS</span>
                <div class="data-flow-indicator">
                  <div class="flow-arrow reverse"></div>
                </div>
              </div>
              <div class="panel-actions">
                <el-button type="info" size="mini" icon="el-icon-download" @click="readPLCValueList" class="modern-btn">
                  批量读取
                </el-button>
                <el-button type="success" size="mini" icon="el-icon-plus" @click="add(0)" class="modern-btn">
                  新增
                </el-button>
              </div>
            </div>

            <div class="data-list">
              <template v-for="value in valueList">
                <div v-if="parseInt(value.type) !== 1" class="data-item read-only compact-item" :key="value.id">
                  <div class="item-content">
                    <div class="item-icon">
                      <i class="el-icon-connection"></i>
                    </div>
                    <div class="item-info">
                      <span class="item-code">【{{ value.code }}】</span>
                      <span class="item-name">{{ value.name }}</span>
                    </div>
                    <div class="item-type">
                      <el-tag size="mini" :type="getTypeTagType(value.javaType)">
                        {{ getJavaTypeName(value.javaType) }}
                      </el-tag>
                    </div>
                    <div class="item-value">
                      <span class="value-label">值：</span>
                      <span class="value-content">{{ value.readValue || '--' }}</span>
                    </div>
                    <div class="value-time" v-if="value.readTime">
                      <i class="el-icon-time"></i>
                      <span class="time-text">{{ value.readTime }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </el-col>
      </el-row>

    </div>
    <!-- <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="属性编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入属性编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="属性名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入属性名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备编码" prop="deviceCode">
        <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="通讯方式" prop="comType">
        <el-select v-model="queryParams.comType" placeholder="请选择通讯方式" clearable>
          <el-option v-for="dict in dict.type.com_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="s7类型" prop="s7Type">
        <el-select v-model="queryParams.s7Type" placeholder="请选择s7类型" clearable>
          <el-option v-for="dict in dict.type.s7_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="modbus类型" prop="modbusType">
        <el-select v-model="queryParams.modbusType" placeholder="请选择modbus类型" clearable>
          <el-option v-for="dict in dict.type.modbus_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="通讯地址" prop="ip">
        <el-input v-model="queryParams.ip" placeholder="请输入通讯地址" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="通讯端口" prop="port">
        <el-input v-model="queryParams.port" placeholder="请输入通讯端口" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="属性地址" prop="address">
        <el-input v-model="queryParams.address" placeholder="请输入属性地址" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="plc类型" prop="plcType">
        <el-select v-model="queryParams.plcType" placeholder="请选择属性类型" clearable>
          <el-option v-for="dict in dict.type.plc_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="java类型" prop="javaType">
        <el-select v-model="queryParams.javaType" placeholder="请选择modbus类型" clearable>
          <el-option v-for="dict in dict.type.java_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建人" prop="createUserName">
        <el-input v-model="queryParams.createUserName" placeholder="请输入创建人" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="更新人" prop="updateUserName">
        <el-input v-model="queryParams.updateUserName" placeholder="请输入更新人" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="删除标志" prop="isDelete">
        <el-select v-model="queryParams.isDelete" placeholder="请选择删除标志" clearable>
          <el-option v-for="item in dict.type.del_flag" :key="item.key" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form> -->
    <!-- 数据管理区域 -->
    <div v-show="!changeMode" class="management-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="el-icon-s-grid"></i>
          数据管理
        </h3>
        <div class="header-decoration"></div>
      </div>


      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd"
            v-hasPermi="['wcs-base:value:add']" class="modern-btn">
            新增
          </el-button>
          <el-button type="success" icon="el-icon-copy-document" size="mini" :disabled="single" @click="handleUpdate2"
            v-hasPermi="['wcs-base:value:edit']" class="modern-btn">
            复制新增
          </el-button>
          <el-button type="warning" icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
            v-hasPermi="['wcs-base:value:edit']" class="modern-btn">
            修改
          </el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
            v-hasPermi="['wcs-base:value:remove']" class="modern-btn">
            删除
          </el-button>
          <el-button type="info" icon="el-icon-download" size="mini" @click="handleExport"
            v-hasPermi="['wcs-base:value:export']" class="modern-btn">
            导出
          </el-button>
        </div>
        <div class="toolbar-right">
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </div>
      </div>

      <div class="table-container">
        <el-table v-loading="loading" :data="valueList" @selection-change="handleSelectionChange" class="modern-table"
          :header-cell-style="{ background: 'rgba(26, 26, 46, 0.9)', color: '#ffffff', borderBottom: '1px solid rgba(102, 126, 234, 0.3)' }"
          :header-row-style="{ background: 'rgba(26, 26, 46, 0.9)' }"
          :row-style="{ background: 'rgba(255, 255, 255, 0.01)' }" :cell-style="{ background: 'transparent' }"
          highlight-current-row>
          <el-table-column type="selection" min-width="55" align="center" />
          <!-- <el-table-column label="ID" align="center" prop="id" min-width="80" /> -->
          <el-table-column label="属性编码" align="center" prop="code" min-width="100">
            <template slot-scope="scope">
              <el-input @blur="update(scope.row)" v-model="scope.row.code" size="mini" class="table-input" />
            </template>
          </el-table-column>
          <el-table-column label="属性名称" align="center" prop="name" min-width="120">
            <template slot-scope="scope">
              <el-input @blur="update(scope.row)" v-model="scope.row.name" size="mini" class="table-input" />
            </template>
          </el-table-column>
          <el-table-column label="属性地址" align="center" prop="address" min-width="200">
            <template slot-scope="scope">
              <el-input @blur="update(scope.row)" v-model="scope.row.address" size="mini" class="table-input" />
            </template>
          </el-table-column>

          <el-table-column label="方向" align="center" prop="type" min-width="120">
            <template slot-scope="scope">
              <el-select @change="update(scope.row)" v-model="scope.row.type" size="mini" class="table-input">
                <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </template>
          </el-table-column>

          <el-table-column label="S7类型" align="center" prop="s7Type" min-width="100">
            <template slot-scope="scope">
              <span class="table-text">{{ getDictLabel(dict.type.s7_type, scope.row.s7Type) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="modbus类型" align="center" prop="modbusType" min-width="100">
            <template slot-scope="scope">
              <span class="table-text">{{ getDictLabel(dict.type.modbus_type, scope.row.modbusType) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="plc类型" align="center" prop="plcType" min-width="150">
            <template slot-scope="scope">
              <el-select @change="update(scope.row)" v-model="scope.row.plcType" size="mini" class="table-input">
                <el-option v-for="dict in dict.type.plc_type" :key="parseInt(dict.value)" :label="dict.label"
                  :value="parseInt(dict.value)"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="java类型" align="center" prop="javaType" min-width="100">
            <template slot-scope="scope">

              <el-select @change="update(scope.row)" v-model="scope.row.javaType" size="mini" class="table-input">
                <el-option v-for="dict in dict.type.java_type" :key="parseInt(dict.value)" :label="dict.label"
                  :value="parseInt(dict.value)"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="设备编码" align="center" prop="deviceCode" min-width="200">
          </el-table-column>
          <el-table-column label="设备名称" align="center" prop="deviceName" min-width="150">
          </el-table-column>
          <el-table-column label="通讯地址" align="center" prop="ip" min-width="120">
          </el-table-column>
          <el-table-column label="通讯端口" align="center" prop="port" min-width="100">
          </el-table-column>
          <el-table-column label="通讯方式" align="center" prop="comType" min-width="120">
            <template slot-scope="scope">
              <span class="table-text">{{ getDictLabel(dict.type.com_type, scope.row.comType) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="属性长度" align="center" prop="length" min-width="100">
          </el-table-column>
          <el-table-column label="读取属性" align="center" prop="readValue" min-width="100">
          </el-table-column>
          <el-table-column label="读取时间" align="center" prop="readTime" min-width="180">
          </el-table-column>
          <el-table-column label="写入属性" align="center" prop="writeValue" min-width="100">
          </el-table-column>
          <el-table-column label="写入时间" align="center" prop="writeTime" min-width="180">
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" min-width="180">
          </el-table-column>
          <el-table-column label="创建人" align="center" prop="createUserName" min-width="120">
          </el-table-column>
          <el-table-column label="更新时间" align="center" prop="updateTime" min-width="180">
          </el-table-column>
          <el-table-column label="更新人" align="center" prop="updateUserName" min-width="120">
          </el-table-column>
          <el-table-column label="删除标志" align="center" prop="isDelete" min-width="100">
            <template slot-scope="scope">
              <span class="table-text">{{ getDictLabel(dict.type.del_flag, scope.row.isDelete) }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column fixed="right" width="120" label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['wcs-base:value:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['wcs-base:value:remove']">删除</el-button>
          </template>
        </el-table-column> -->
        </el-table>
      </div>

      <div class="pagination-wrapper">
        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize" @pagination="getList(deviceCode)" />
      </div>

    </div>

    <!-- 添加或修改属性对话框 -->
    <el-dialog v-dialogDrags :title="title" :visible.sync="open" width="600px" append-to-body class="modern-dialog"
      :close-on-click-modal="false">
      <div class="dialog-header">
        <h4 class="dialog-title">
          <i class="el-icon-edit-outline"></i>
          {{ title }}
        </h4>
      </div>

      <el-form ref="form" :model="form" :rules="rules" label-width="120px" class="modern-form">
        <el-form-item label="属性编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入属性编码" />
        </el-form-item>
        <el-form-item label="属性名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入属性名称" />
        </el-form-item>
        <el-form-item label="属性地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入属性地址" />
        </el-form-item>
        <el-form-item label="设备编码" prop="deviceId">
          <template slot-scope="scope">
            <el-select v-model="form.deviceId" placeholder="请选择设备" clearable>
              <el-option v-for="item in devices" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="方向" prop="type">
          <template slot-scope="scope">
            <el-select v-model="form.type" placeholder="请选择方向" clearable>
              <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="modbus类型" prop="modbusType">
          <template slot-scope="scope">
            <el-select v-model="form.modbusType" placeholder="请选择属性类型" clearable>
              <el-option v-for="dict in dict.type.modbus_type" :key="parseInt(dict.value)" :label="dict.label"
                :value="parseInt(dict.value)"></el-option>
            </el-select>
          </template>
        </el-form-item>

        <el-form-item label="plc类型" prop="plcType">
          <template slot-scope="scope">
            <el-select v-model="form.plcType" placeholder="请选择属性类型" clearable>
              <el-option v-for="dict in dict.type.plc_type" :key="parseInt(dict.value)" :label="dict.label"
                :value="parseInt(dict.value)"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="java类型" prop="javaType">
          <template slot-scope="scope">
            <el-select v-model="form.javaType" placeholder="请选择属性类型" clearable>
              <el-option v-for="dict in dict.type.java_type" :key="parseInt(dict.value)" :label="dict.label"
                :value="parseInt(dict.value)"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="属性长度" prop="length">
          <el-input v-model="form.length" placeholder="请输入属性长度" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <div class="footer-buttons">
          <el-button @click="cancel" class="cancel-btn">
            <i class="el-icon-close"></i>
            取 消
          </el-button>
          <el-button type="primary" @click="submitForm" class="submit-btn">
            <i class="el-icon-check"></i>
            确 定
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listValue,
  getValue,
  delValue,
  addValue,
  updateValue,
} from "@/api/wcs-base/value";
import {
  listDeviceInfo,
  getDeviceInfo,
  delDeviceInfo,
  addDeviceInfo,
  updateDeviceInfo,
} from "@/api/wcs-base/DeviceInfo";
import request from "@/utils/request.js";
export default {
  name: "Value",
  dicts: [
    "com_type",
    "device_type",
    "java_type",
    "plc_type",
    "modbus_type",
    "s7_type",
    "del_flag",
  ],
  data() {
    return {
      //进入调试模式
      changeMode: false,
      // 自动读取
      autoRead: false,
      timer: null,
      // 遮罩层
      loading: false,
      deviceLoading: false,
      deviceCode: null,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 属性表格数据
      valueList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 200,
        code: null,
        name: null,
        deviceCode: null,
        deviceName: null,
        comType: null,
        ip: null,
        port: null,
        address: null,
        plcType: null,
        javaType: null,
        readValue: null,
        readTime: null,
        writeValue: null,
        writeTime: null,
        createUserId: null,
        createUserName: null,
        updateUserId: null,
        updateUserName: null,
        isDelete: '0',
        s7Type: null,
        modbusType: null,
        valueStyle: null,
        javaType: null,
      },
      devices: [],
      // 筛选后的设备列表
      filteredDevices: [],
      // 选中的设备类型
      selectedType: 'all',
      // 表单参数
      form: {},
      types: [
        { value: 0, label: "PLC->WCS", color: "#409EFF" },
        { value: 1, label: "WCS->PLC", color: "#67C23A" },
        { value: 2, label: "双向", color: "#E6A23C" },
      ],
      // 表单校验
      rules: {
        deviceCode: [
          { required: true, message: "设备编码不能为空", trigger: "blur" },
        ],
        deviceName: [
          { required: true, message: "设备名称不能为空", trigger: "blur" },
        ],
        address: [
          { required: true, message: "属性地址不能为空", trigger: "blur" },
        ],
        plcType: [
          { required: true, message: "属性类型不能为空", trigger: "change" },
        ],
        length: [
          { required: true, message: "属性长度不能为空", trigger: "blur" },
        ],
        version: [
          { required: true, message: "版本号不能为空", trigger: "blur" },
        ],
        isDelete: [
          { required: true, message: "删除标志不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getDevices();
    this.timer = setInterval(() => {
      if (
        this.autoRead &&
        this.deviceCode != undefined &&
        this.deviceCode != null
      ) {
        this.readPLCValueList();
      }
    }, 8000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    // 获取类型标签类型
    getTypeTagType(javaType) {
      const typeMap = {
        0: 'warning',  // 布尔
        1: 'success',  // 整数
        2: 'success',  // 长整数
        3: 'success',  // 短整数
        4: 'success',  // 字节
        5: 'primary',  // 浮点
        6: 'primary',  // 双精度
        7: 'info',     // 字符串
        8: 'info',     // 字符
        9: 'info'      // 其他
      };
      return typeMap[javaType] || 'info';
    },

    // 获取类型名称
    getTypeName(type) {
      // 如果是方向类型
      if (typeof type === 'number' && type >= 0 && type <= 2) {
        const directionMap = {
          0: 'PLC->WCS',
          1: 'WCS->PLC',
          2: '双向'
        };
        return directionMap[type] || '未知';
      }

      // 如果是Java类型
      const javaTypeMap = {
        0: '布尔',
        1: '整数',
        2: '长整数',
        3: '短整数',
        4: '字节',
        5: '浮点',
        6: '双精度',
        7: '字符串',
        8: '字符',
        9: '其他'
      };
      return javaTypeMap[type] || '未知';
    },

    // 获取类型名称
    getJavaTypeName(type) {


      // 如果是Java类型
      const javaTypeMap = {
        0: '布尔',
        1: '整数',
        2: '长整数',
        3: '短整数',
        4: '字节',
        5: '浮点',
        6: '双精度',
        7: '字符串',
        8: '字符',
        9: '其他'
      };
      return javaTypeMap[type] || '未知';
    },

    // 获取字典标签文字
    getDictLabel(dictOptions, value) {
      const option = dictOptions.find(item => item.value == value);
      return option ? option.label : value;
    },

    // 获取方向标签类型
    getDirectionTagType(type) {
      const typeMap = {
        0: 'info',     // PLC->WCS
        1: 'success',  // WCS->PLC
        2: 'warning'   // 双向
      };
      return typeMap[type] || 'info';
    },

    add(type) {
      this.form = this.valueList[0];
      this.form.id = null;
      this.form.type = type;
      this.form.code = "";
      this.form.name = "";
      this.open = true;
      this.title = "新增属性";
    },
    /**
     * 批量写入
     */
    writePLCValueList() {
      for (var i = 0; i < this.valueList.length; i++) {
        this.valueList[i].writeValue = this.valueList[i].writeDeviceValue;
        this.valueList[i].writeDeviceValue = null;
      }

      request({
        url: "/wcs-base/value/writePLCValueList",
        method: "post",
        data: this.valueList,
      }).then((response) => {
        if (response.code == 200) {
          this.getList(this.deviceCode);
          this.$modal.msgSuccess("写入成功");
        } else {
          this.$modal.msgError(response.msg || "写入失败");
        }
      });
    },

    /**
     * 批量读取
     */
    write(row) {
      row.writeValue = row.writeDeviceValue;
      request({
        url: "/wcs-base/value/write",
        method: "post",
        data: row,
      }).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("写入成功");
          //  this.readPLCValueList();
        } else {
          this.$modal.msgError(response.msg || "写入失败");
        }
      });
    },

    /**
     * 批量读取
     */
    readPLCValueList() {
      request({
        url: "/wcs-base/value/readPLCValueList",
        method: "get",
        params: {
          deviceCode: this.deviceCode,
        },
      }).then((response) => {
        if (response.code == 200) {
          // console.log(this.autoRead)
          if (!this.autoRead) {
            this.$modal.msgSuccess(response.msg || "读取成功");
          }
          this.getList(this.deviceCode);
        } else {
          this.$modal.msgError(response.msg || "读取失败");
        }
      });
    },
    /**
     * 获取设备信息
     */
    getDevices() {
      var that = this;
      that.deviceLoading = true;
      request({
        url: "/wcs-base/DeviceInfo/getDevices",
        method: "get",
      }).then((response) => {
        that.deviceLoading = false;
        if (response.code == 200) {
          that.devices = [];
          var data = response.data;
          for (var i = 0; i < data.length; i++) {
            // 使用后端返回的isOnline字段
            that.devices.push(data[i]);
          }
          // 初始筛选设备
          that.filterDevicesByType();
        }
      });
    },
    /** 查询属性列表 */
    getList(deviceCode) {
      this.loading = true;
      this.queryParams.deviceCode = deviceCode;
      listValue(this.queryParams).then((response) => {
        this.valueList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 选择设备类型
    selectDeviceType(type) {
      this.selectedType = type;
      this.filterDevicesByType();
      // 如果当前有选中的设备且不在筛选后的列表中，清除选中状态
      if (this.deviceCode) {
        const deviceExists = this.filteredDevices.some(device => device.code === this.deviceCode);
        if (!deviceExists) {
          this.deviceCode = null;
          this.valueList = [];
        }
      }
    },

    // 根据设备类型筛选设备
    filterDevicesByType() {
      if (this.selectedType === 'all') {
        this.filteredDevices = [...this.devices];
      } else {
        // 确保类型比较的准确性，将选中的类型转换为整数与设备的Integer类型type字段比较
        const selectedTypeInt = parseInt(this.selectedType);
        this.filteredDevices = this.devices.filter(device => {
          // 检查设备是否有type属性，并且确保类型为整数进行比较
          return device && device.type !== undefined && device.type !== null && device.type === selectedTypeInt;
        });
      }
    },

    // 表单重置
    reset() {
      this.form = {
        id: null,
        code: null,
        name: null,
        deviceId: null,
        deviceCode: null,
        deviceName: null,
        comType: null,
        ip: null,
        port: null,
        address: null,
        plcType: null,
        javaType: null,
        modbusType: null,
        length: null,
        readValue: null,
        readTime: null,
        writeValue: null,
        writeTime: null,
        createTime: null,
        createUserId: null,
        createUserName: null,
        updateTime: null,
        updateUserId: null,
        updateUserName: null,
        version: null,
        isDelete: null,
        s7Type: null,
        valueStyle: null,
        javaType: null,
        type: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.getDevices()
      this.reset();
      this.open = true;
      this.title = "添加属性";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.getDevices()
      this.reset();
      const id = row.id || this.ids;
      getValue(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改属性";
      });
    },
    /** 修改按钮操作 */
    handleUpdate2(row) {
      // this.getDevices()
      this.reset();
      const id = row.id || this.ids;
      getValue(id).then((response) => {
        this.form = response.data;
        this.form.id = null;
        this.open = true;
        this.title = "添加属性";
      });
    },
    update(row) {
      updateValue(row).then((response) => {
        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          // this.open = false;
          // this.getList(this.deviceCode);
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateValue(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("修改成功");

                this.open = false;
                this.getList(this.deviceCode);
              } else {
                this.$modal.msgError(response.msg || "修改失败");
              }
            });
          } else {
            addValue(this.form).then((response) => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                // this.form.code = null;
                // this.form.name = null;
                // this.form.address = null;
                ///this.open = false;
                this.getList(this.deviceCode);
              } else {
                this.$modal.msgError(response.msg || "新增失败");
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除属性编号为"' + ids + '"的数据项？')
        .then(function () {
          return delValue(ids);
        })
        .then((response) => {
          if (response.code == 200) {
            this.getList(this.deviceCode);
            this.$modal.msgSuccess("删除成功");
          } else {
            this.$modal.msgError(response.msg || "删除失败")
          }
        })
        .catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "wcs-base/value/export",
        {
          ...this.queryParams,
        },
        `value_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
// 现代化容器样式 - 与首页保持一致
.modern-container {
  background: #1a1a2e;
  min-height: calc(100vh - 84px);
  padding: 20px;
  position: relative;

  >* {
    position: relative;
    z-index: 1;
  }
}

// 设备类型选择区域样式 - 增强美化效果
.device-type-section {
  margin-top: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.08);
  /* 与设备卡片相同背景 */
  backdrop-filter: blur(15px);
  /* 与设备卡片相同毛玻璃效果 */
  border-radius: 12px;
  /* 保持一致的圆角大小 */
  border: 1px solid transparent;
  /* 透明边框 */
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  /* 与设备卡片相同阴影效果 */
  position: relative;
  overflow: hidden;

  // 添加微妙的渐变背景效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  }

  .type-section-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    color: #b8c5d1;
    /* 与设备卡片中的device-code和status-text字体颜色一致 */
    font-size: 14px;
    font-weight: 500;

    i {
      color: #667eea;
      font-size: 16px;
      transition: transform 0.3s ease;
    }

    &:hover i {
      transform: scale(1.1);
    }
  }

  .type-selector {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .type-tag {
    cursor: pointer;
    padding: 6px 16px;
    border-radius: 20px;
    /* 增加标签圆角大小，保持矩形框圆角风格 */
    font-size: 13px;
    font-weight: 500;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background-color: rgba(255, 255, 255, 0.05);
    color: #ffffff;
    /* 与设备卡片中的device-name字体颜色一致 */
    text-align: center;
    /* 确保字体居中对齐 */
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border: 1px solid transparent;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.1), transparent);
      transition: left 0.5s ease;
    }

    &:hover {
      transform: translateY(-1px) scale(1.02);
      background-color: rgba(255, 255, 255, 0.1);
      border-color: rgba(102, 126, 234, 0.3);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
    }

    &:hover::before {
      left: 100%;
    }

    // 选中状态样式
    &.selected {
      background-color: #667eea;
      color: #ffffff;
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
      border-color: #667eea;
      transform: translateY(-1px);

      &::before {
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
      }
    }
  }
}

// 区域标题样式 - 与首页保持一致
.section-header {
  margin-bottom: 20px;
  position: relative;

  .section-title {
    color: #ffffff;
    font-size: 18px;
    font-weight: 700;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 10px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);

    i {
      font-size: 20px;
      color: #667eea;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
    }
  }

  .header-decoration {
    position: absolute;
    bottom: -8px;
    left: 0;
    width: 60px;
    height: 3px;
    background: #667eea;
    border-radius: 2px;
    box-shadow: 0 2px 4px rgba(102, 126, 234, 0.3);
  }
}

// 设备卡片样式 - 与首页保持一致
.device-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.05);
    pointer-events: none;
  }
}

.device-item {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 12px;

  height: 100px;
  width: 180px;
  margin: 10px;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.05);
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
  }

  &:hover {
    transform: translateY(-2px) scale(1.02);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
    border-color: rgba(255, 255, 255, 0.2);

    &::before {
      opacity: 1;
    }
  }

  &.device-selected {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
    background: rgba(102, 126, 234, 0.1);

    &::before {
      opacity: 1;
      background: rgba(102, 126, 234, 0.1);
    }

    .device-icon i {
      color: #667eea;
    }
  }

  .device-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    background: rgba(102, 126, 234, 0.2);
    border-radius: 8px;
    flex-shrink: 0;

    i {
      font-size: 18px;
      color: #667eea;
      transition: all 0.3s ease;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
    }
  }

  .device-info {
    flex: 1;
    min-width: 0;

    .device-code {
      font-size: 12px;
      font-weight: 600;
      margin-bottom: 5px;
      color: #b8c5d1;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    }

    .device-name {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 8px;
      color: #ffffff;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    }

    .device-status {
      display: flex;
      align-items: center;

      .status-dot {
        width: 5px;
        height: 5px;
        border-radius: 50%;
        margin-right: 4px;
        flex-shrink: 0;

        &.online {
          background: #67c23a;
          box-shadow: 0 0 4px rgba(103, 194, 58, 0.5);
        }

        &.offline {
          background: #f56c6c;
          box-shadow: 0 0 4px rgba(245, 108, 108, 0.5);
        }
      }

      .status-text {
        font-size: 10px;
        font-weight: 500;
        color: #b8c5d1;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
        white-space: nowrap;
      }
    }
  }

  .selected-indicator {
    position: absolute;
    top: 6px;
    right: 6px;
    width: 14px;
    height: 14px;
    background: #67c23a;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 6px rgba(103, 194, 58, 0.3);
    flex-shrink: 0;

    i {
      font-size: 9px;
      color: #ffffff;
      font-weight: bold;
    }
  }
}

// 控制面板样式 - 与首页保持一致
.control-panel {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 20px;
  height: 100px;
  width: 180px;
  margin: 10px;

  .control-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    &:last-child {
      margin-bottom: 0;
    }

    .control-label {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 500;
      color: #b8c5d1;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);

      i {
        color: #667eea;
        font-size: 16px;
        filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
      }
    }
  }
}

.modern-switch {
  .el-switch__core {
    border-radius: 20px;
    height: 24px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);

    &::after {
      width: 20px;
      height: 20px;
      background: #ffffff;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    }
  }

  &.is-checked .el-switch__core {
    background: #667eea;
    border-color: #667eea;
  }
}

// 数据区域样式 - 与首页保持一致
.data-section {
  margin-top: 30px;
}

.data-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.05);
    pointer-events: none;
  }
}

.data-panel {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  position: relative;
  z-index: 2;

  &.wcs-to-plc {
    border-left: 4px solid #667eea;
  }

  &.plc-to-wcs {
    border-left: 4px solid #4facfe;
  }

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);

    .panel-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 16px;
      font-weight: 600;
      color: #ffffff;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);

      i {
        color: #667eea;
        font-size: 18px;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
      }

      .data-flow-indicator {
        margin-left: 10px;

        .flow-arrow {
          width: 20px;
          height: 2px;
          background: #667eea;
          position: relative;
          box-shadow: 0 0 4px rgba(102, 126, 234, 0.5);

          &::after {
            content: '';
            position: absolute;
            right: -5px;
            top: -3px;
            width: 0;
            height: 0;
            border-left: 6px solid #667eea;
            border-top: 4px solid transparent;
            border-bottom: 4px solid transparent;
          }

          &.reverse {
            background: #4facfe;
            box-shadow: 0 0 4px rgba(79, 172, 254, 0.5);

            &::after {
              border-left: none;
              border-right: 6px solid #4facfe;
              right: auto;
              left: -5px;
            }
          }
        }
      }
    }

    .panel-actions {
      display: flex;
      gap: 10px;
    }
  }
}

// 数据项样式
.data-list {
  max-height: 400px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;

    &:hover {
      background: #a8a8a8;
    }
  }
}

.data-item {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  z-index: 2;

  &:hover {
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
    transform: translateY(-2px) scale(1.02);
    border-color: rgba(255, 255, 255, 0.2);
  }

  &.read-only {
    background: rgba(255, 255, 255, 0.03);
  }

  // 紧凑布局样式
  &.compact-item {
    padding: 8px 12px;
    margin-bottom: 6px;

    .item-content {
      display: flex;
      align-items: center;
      gap: 12px;
      width: 100%;

      .item-icon {
        margin-right: 0;
        flex-shrink: 0;

        i {
          font-size: 14px;
        }
      }

      .item-info {
        flex: 1;
        min-width: 0;
        display: flex;
        align-items: center;
        gap: 8px;

        .item-code {
          font-size: 11px;
          color: #b8c5d1;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
          white-space: nowrap;
        }

        .item-name {
          font-size: 13px;
          font-weight: 500;
          color: #ffffff;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          max-width: 120px;
        }
      }

      .item-type {
        flex-shrink: 0;
        margin-left: 0;
      }

      .item-controls {
        flex: 0 0 auto;
        min-width: 0;
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: 4px;

        .compact-input {
          width: 100px;
          min-width: 80px;
        }
      }

      .write-btn {
        flex-shrink: 0;
        padding: 6px 10px;
        font-size: 11px;
        margin-left: 4px;

        &.compact-btn {
          padding: 6px 8px;
          min-width: 32px;

          i {
            margin-right: 0;
          }
        }
      }

      .item-value {
        flex: 0 0 auto;
        display: flex;
        align-items: center;
        gap: 4px;
        margin-left: 0;

        .value-label {
          font-size: 11px;
          color: #b8c5d1;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
          white-space: nowrap;
        }

        .value-content {
          font-size: 12px;
          font-weight: 500;
          color: #ffffff;
          background: rgba(255, 255, 255, 0.1);
          padding: 2px 6px;
          border-radius: 3px;
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.1);
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
          white-space: nowrap;
          max-width: 80px;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      .value-time {
        flex: 0 0 auto;
        font-size: 10px;
        color: #b8c5d1;
        display: flex;
        align-items: center;
        gap: 3px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
        margin-left: 0;

        i {
          font-size: 10px;
        }

        .time-text {
          white-space: nowrap;
          max-width: 60px;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }

  // 保留原有的非紧凑布局样式作为备用
  .item-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;

    .item-icon {
      margin-right: 10px;

      i {
        color: #667eea;
        font-size: 16px;
        filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
      }
    }

    .item-info {
      flex: 1;

      .item-code {
        font-size: 12px;
        color: #b8c5d1;
        margin-bottom: 2px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      }

      .item-name {
        font-size: 14px;
        font-weight: 500;
        color: #ffffff;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      }
    }

    .item-type {
      margin-left: 10px;
    }
  }

  .item-controls {
    display: flex;
    align-items: center;
    gap: 10px;

    .input-group {
      flex: 1;
    }

    .modern-input {
      width: 100%;
    }

    .write-btn {
      background: #667eea;
      border: none;
      border-radius: 6px;
      padding: 8px 16px;
      font-size: 12px;
      font-weight: 500;
      color: #ffffff;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);

      &:hover {
        background: #764ba2;
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
      }
    }
  }

  .item-value {
    .value-display {
      display: flex;
      align-items: center;
      margin-bottom: 5px;

      .value-label {
        font-size: 12px;
        color: #b8c5d1;
        margin-right: 8px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      }

      .value-content {
        font-size: 14px;
        font-weight: 500;
        color: #ffffff;
        background: rgba(255, 255, 255, 0.1);
        padding: 4px 8px;
        border-radius: 4px;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
      }
    }

    .value-time {
      font-size: 11px;
      color: #b8c5d1;
      display: flex;
      align-items: center;
      gap: 4px;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    }
  }
}

// 管理区域样式 - 与首页保持一致
.management-section {
  margin-top: 30px;
}

.management-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.05);
    pointer-events: none;
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 0;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 2;

  .toolbar-left {
    display: flex;
    gap: 10px;
  }

  .toolbar-right {
    display: flex;
    align-items: center;
  }
}

// 按钮样式 - 与首页保持一致
.modern-btn {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  backdrop-filter: blur(10px);

  &:hover {
    transform: translateY(-2px) scale(1.02);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  }
}

// 表格样式 - 现代化设计
.table-container {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  position: relative;
  z-index: 2;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(102, 126, 234, 0.02);
    pointer-events: none;
  }

  // 强制覆盖Element UI默认样式
  ::v-deep .el-table {
    background: transparent !important;
  }

  ::v-deep .el-table__header-wrapper {
    background: rgba(26, 26, 46, 0.9) !important;
  }

  ::v-deep .el-table__header {
    background: rgba(26, 26, 46, 0.9) !important;

    thead {
      background: rgba(26, 26, 46, 0.9) !important;
    }

    th {
      background: rgba(26, 26, 46, 0.9) !important;
      color: #ffffff !important;
      border-bottom: 1px solid rgba(102, 126, 234, 0.3) !important;
    }
  }

  // 更具体的样式覆盖
  ::v-deep .el-table th.el-table__cell {
    background: rgba(26, 26, 46, 0.9) !important;
    color: #ffffff !important;
    border-bottom: 1px solid rgba(102, 126, 234, 0.3) !important;
  }
}

.modern-table {
  background: transparent;

  ::v-deep .el-table__header {
    background: rgba(255, 255, 255, 0.08) !important;
    backdrop-filter: blur(15px);
    border-bottom: 2px solid rgba(102, 126, 234, 0.15);
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(102, 126, 234, 0.05);
      pointer-events: none;
    }

    th {
      background: transparent !important;
      color: #ffffff !important;
      border: none;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
      font-weight: 600;
      font-size: 13px;
      padding: 16px 12px;
      position: relative;
      z-index: 1;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 1px;
        background: rgba(102, 126, 234, 0.15);
      }
    }
  }

  ::v-deep .el-table__body {
    tr {
      background: rgba(255, 255, 255, 0.01);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;

      &:nth-child(even) {
        background: rgba(255, 255, 255, 0.02);
      }

      &:hover {
        background: rgba(102, 126, 234, 0.08) !important;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

        td {
          background: rgba(102, 126, 234, 0.08) !important;
          border-color: rgba(102, 126, 234, 0.2);
        }
      }

      td {
        border: none;
        border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        color: #ffffff;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
        padding: 12px;
        font-size: 13px;
        position: relative;

        &:first-child {
          border-left: 3px solid transparent;
          transition: border-left 0.3s ease;
        }
      }

      &:hover td:first-child {
        border-left-color: #667eea;
      }
    }
  }

  // 选择框样式
  ::v-deep .el-checkbox {
    .el-checkbox__input {
      .el-checkbox__inner {
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 4px;

        &:hover {
          border-color: #667eea;
          background: rgba(255, 255, 255, 0.08);
        }

        &::after {
          border-color: #ffffff;
        }
      }

      &.is-checked .el-checkbox__inner {
        background: #667eea;
        border-color: #667eea;
      }
    }
  }

  // 按钮样式
  ::v-deep .el-button--text {
    color: #667eea;
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 4px;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(102, 126, 234, 0.08);
      color: #ffffff;
    }
  }

  // 标签样式
  ::v-deep .el-tag {
    border: none;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 500;
    padding: 4px 8px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);

    &.el-tag--info {
      background: rgba(64, 158, 255, 0.15);
      color: #409eff;
    }

    &.el-tag--success {
      background: rgba(103, 194, 58, 0.15);
      color: #67c23a;
    }

    &.el-tag--warning {
      background: rgba(230, 162, 60, 0.15);
      color: #e6a23c;
    }

    &.el-tag--danger {
      background: rgba(245, 108, 108, 0.15);
      color: #f56c6c;
    }
  }
}

.table-text {
  font-weight: 500;
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.table-input {
  ::v-deep .el-input__inner {
    border-radius: 6px;
    border: 1px solid rgba(255, 255, 255, 0.15);
    background: rgba(255, 255, 255, 0.05);
    color: #ffffff;
    backdrop-filter: blur(10px);
    font-size: 12px;
    padding: 6px 10px;
    height: 28px;
    transition: all 0.3s ease;

    &:focus {
      border-color: #667eea;
      box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.15);
      background: rgba(255, 255, 255, 0.08);
    }

    &:hover {
      border-color: rgba(102, 126, 234, 0.4);
      background: rgba(255, 255, 255, 0.06);
    }

    &::placeholder {
      color: #b8c5d1;
      font-size: 11px;
    }
  }
}

.dialog-header {
  margin-bottom: 20px;

  .dialog-title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 16px;
    font-weight: 600;
    color: #ffffff;
    margin: 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);

    i {
      color: #667eea;
      font-size: 18px;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
    }
  }
}

.dialog-footer {
  padding: 20px 30px;
  background: rgba(26, 26, 46, 0.95);
  border-top: 1px solid rgba(255, 255, 255, 0.1);

  .footer-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 10px;

    .cancel-btn {
      background: rgba(108, 117, 125, 0.8);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: #ffffff;
      border-radius: 6px;
      padding: 10px 20px;
      font-weight: 500;
      backdrop-filter: blur(10px);

      &:hover {
        background: rgba(90, 98, 104, 0.9);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
      }
    }

    .submit-btn {
      background: #667eea;
      border: none;
      color: #ffffff;
      border-radius: 6px;
      padding: 10px 20px;
      font-weight: 500;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);

      &:hover {
        background: #764ba2;
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
      }
    }
  }
}

// 分页样式 - 与首页保持一致
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  position: relative;
  z-index: 2;
}

// 响应式设计
@media (max-width: 768px) {
  .modern-container {
    padding: 10px;
  }

  .device-item {
    margin-bottom: 15px;
  }

  .data-panel {
    margin-bottom: 20px;
  }

  .toolbar {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;

    .toolbar-left {
      justify-content: center;
      flex-wrap: wrap;
    }
  }
}

// 动画效果
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.device-section,
.data-section,
.management-section {
  animation: fadeInUp 0.6s ease-out;
}

// 保持原有的选中状态样式
.deviceSelected {
  background-color: #409eff;
  color: #ffffff;
}

// 全局表格头部样式覆盖 - 使用更高优先级
.modern-container ::v-deep .el-table th.el-table__cell,
.modern-container ::v-deep .el-table .el-table__header th,
.modern-container ::v-deep .el-table .el-table__header-wrapper th,
.modern-container ::v-deep .el-table thead th,
.modern-container ::v-deep .el-table .el-table__header thead th {
  background: rgba(26, 26, 46, 0.9) !important;
  color: #ffffff !important;
  border-bottom: 1px solid rgba(102, 126, 234, 0.3) !important;
}

.modern-container ::v-deep .el-table .el-table__header-wrapper {
  background: rgba(26, 26, 46, 0.9) !important;
}

.modern-container ::v-deep .el-table .el-table__header {
  background: rgba(26, 26, 46, 0.9) !important;
}

.modern-container ::v-deep .el-table thead {
  background: rgba(26, 26, 46, 0.9) !important;
}

// 针对Element UI 2.x版本的额外覆盖
.modern-container ::v-deep .el-table__header th,
.modern-container ::v-deep .el-table__header-wrapper th {
  background-color: rgba(26, 26, 46, 0.9) !important;
  color: #ffffff !important;
}

// 强制覆盖所有可能的表头样式
.modern-container ::v-deep .el-table .el-table__header th.el-table__cell {
  background-color: rgba(26, 26, 46, 0.9) !important;
  color: #ffffff !important;
  border-bottom-color: rgba(102, 126, 234, 0.3) !important;
}

// 使用深度选择器覆盖Element UI内部样式
.modern-container>>>.el-table__header th,
.modern-container>>>.el-table__header-wrapper th,
.modern-container>>>.el-table thead th {
  background: rgba(26, 26, 46, 0.9) !important;
  color: #ffffff !important;
  border-bottom: 1px solid rgba(102, 126, 234, 0.3) !important;
}

.modern-container>>>.el-table__header-wrapper {
  background: rgba(26, 26, 46, 0.9) !important;
}

.modern-container>>>.el-table__header {
  background: rgba(26, 26, 46, 0.9) !important;
}

// 表格行样式覆盖
.modern-container>>>.el-table__body tr:hover {
  background: rgba(102, 126, 234, 0.08) !important;
}

.modern-container>>>.el-table__body tr:hover td {
  background: rgba(102, 126, 234, 0.08) !important;
}

.modern-container>>>.el-table__body tr.current-row {
  background: rgba(102, 126, 234, 0.08) !important;
}

.modern-container>>>.el-table__body tr.current-row td {
  background: rgba(102, 126, 234, 0.08) !important;
}

// 强制覆盖Element UI默认的hover样式
.modern-container ::v-deep .el-table__body tr:hover>td {
  background-color: rgba(102, 126, 234, 0.08) !important;
}

.modern-container ::v-deep .el-table__body tr.current-row>td {
  background-color: rgba(102, 126, 234, 0.08) !important;
}
</style>
