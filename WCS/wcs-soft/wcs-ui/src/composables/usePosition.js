import { listPositionInfo } from "@/api/wcs-base/PositionInfo";
import { listPositionRecord } from "@/api/wcs-base/PositionRecord";

export function usePosition() {
  const positions = [];
  let positionsDebounceTimer = null;
  let lastFetchTime = 0;

  const positionRecordLoading = false;
  const positionRecordTotal = 0;
  const positionRecordList = [];
  const positionRecordMockEnabled = false;
  
  const positionRecordQuery = {
    pageNum: 1,
    pageSize: 10,
    wareCode: null,
    positionCode: null,
  };

  const getPositionsByWareCode = function(wareCode, callback) {
    if (!wareCode) return;
    
    const now = Date.now();
    if (now - lastFetchTime < 1000) return;

    if (positionsDebounceTimer) {
      clearTimeout(positionsDebounceTimer);
    }

    positionsDebounceTimer = setTimeout(() => {
      var query = {};
      query.wareCode = wareCode;
      listPositionInfo(query).then((response) => {
        if (response.code == 200) {
          this.positions = response.rows;
          lastFetchTime = Date.now();
          if (typeof callback === 'function') {
            callback(response.rows);
          }
        }
      });
    }, 300);
  };

  const getPositionByCode = function(code) {
    var positions = this.positions;
    for (let index = 0; index < positions.length; index++) {
      const position = positions[index];
      if (position.code === code) {
        return position;
      }
    }
    return null;
  };

  const getPositions = function() {
    return this.positions;
  };

  const setPositions = function(data) {
    this.positions = data;
  };

  const buildMockPositionRecords = function(positionCode) {
    const total = 37;
    const now = Date.now();
    const rows = [];
    for (let i = 0; i < total; i++) {
      const ts = new Date(now - i * 60 * 1000);
      const yyyy = ts.getFullYear();
      const mm = String(ts.getMonth() + 1).padStart(2, "0");
      const dd = String(ts.getDate()).padStart(2, "0");
      const HH = String(ts.getHours()).padStart(2, "0");
      const MM = String(ts.getMinutes()).padStart(2, "0");
      const SS = String(ts.getSeconds()).padStart(2, "0");
      rows.push({
        id: total - i,
        positionCode,
        wareCode: this.wareCode,
        type: i % 9 === 0 ? 1 : 0,
        createTime: `${yyyy}-${mm}-${dd} ${HH}:${MM}:${SS}`,
        content:
          i % 9 === 0
            ? `站台(${positionCode}) 上报异常：通信超时，已触发重试`
            : `站台(${positionCode}) 状态上报正常，任务流转中`,
      });
    }
    return rows;
  };

  const positionRecordTypeLabel = function(type) {
    if (type === 0) return "INFO";
    if (type === 1) return "ERROR";
    return "-";
  };

  const fetchPositionRecords = function(positionCode, wareCode) {
    const self = this;
    
    if (!positionCode) {
      self.positionRecordTotal = 0;
      self.positionRecordList = [];
      return;
    }

    if (self.positionRecordMockEnabled) {
      const all = self.buildMockPositionRecords(positionCode);
      const start = (self.positionRecordQuery.pageNum - 1) * self.positionRecordQuery.pageSize;
      const end = start + self.positionRecordQuery.pageSize;
      self.positionRecordTotal = all.length;
      self.positionRecordList = all.slice(start, end);
      return;
    }

    self.positionRecordLoading = true;
    self.positionRecordQuery.positionCode = positionCode;
    self.positionRecordQuery.wareCode = wareCode;
    
    listPositionRecord(self.positionRecordQuery)
      .then((res) => {
        if (res && res.code == 200) {
          self.positionRecordList = res.rows || [];
          self.positionRecordTotal = res.total || 0;
        } else {
          self.positionRecordList = [];
          self.positionRecordTotal = 0;
        }
      })
      .finally(() => {
        self.positionRecordLoading = false;
      });
  };

  const handlePositionRecordCurrentChange = function(page) {
    this.positionRecordQuery.pageNum = page;
    this.fetchPositionRecords();
  };

  const handlePositionRecordSizeChange = function(size) {
    this.positionRecordQuery.pageSize = size;
    this.positionRecordQuery.pageNum = 1;
    this.fetchPositionRecords();
  };

  const clearDebounce = function() {
    if (positionsDebounceTimer) {
      clearTimeout(positionsDebounceTimer);
      positionsDebounceTimer = null;
    }
  };

  return {
    positions,
    positionRecordLoading,
    positionRecordTotal,
    positionRecordList,
    positionRecordMockEnabled,
    positionRecordQuery,
    getPositionsByWareCode,
    getPositionByCode,
    getPositions,
    setPositions,
    buildMockPositionRecords,
    positionRecordTypeLabel,
    fetchPositionRecords,
    handlePositionRecordCurrentChange,
    handlePositionRecordSizeChange,
    clearDebounce,
  };
}