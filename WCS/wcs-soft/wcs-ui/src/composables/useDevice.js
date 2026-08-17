import { listDeviceInfo } from "@/api/wcs-base/DeviceInfo";

export function useDevice() {
  const devices = [];
  let devicesDebounceTimer = null;
  let lastFetchTime = 0;

  const getAllDevices = function(wareCode, callback) {
    if (!wareCode) return;
    
    const now = Date.now();
    if (now - lastFetchTime < 1000) return;

    if (devicesDebounceTimer) {
      clearTimeout(devicesDebounceTimer);
    }

    devicesDebounceTimer = setTimeout(() => {
      var query = {};
      query.wareCode = wareCode;
      listDeviceInfo(query).then((response) => {
        if (response.code == 200) {
          this.devices = response.rows;
          lastFetchTime = Date.now();
          if (typeof callback === 'function') {
            callback(response.rows);
          }
        }
      });
    }, 300);
  };

  const getDeviceInfoByCode = function(code) {
    var devices = this.devices;
    for (let index = 0; index < devices.length; index++) {
      const device = devices[index];
      if (device.code === code) {
        return device;
      }
    }
    return null;
  };

  const getDevices = function() {
    return this.devices;
  };

  const setDevices = function(data) {
    this.devices = data;
  };

  const clearDebounce = function() {
    if (devicesDebounceTimer) {
      clearTimeout(devicesDebounceTimer);
      devicesDebounceTimer = null;
    }
  };

  return {
    devices,
    getAllDevices,
    getDeviceInfoByCode,
    getDevices,
    setDevices,
    clearDebounce,
  };
}