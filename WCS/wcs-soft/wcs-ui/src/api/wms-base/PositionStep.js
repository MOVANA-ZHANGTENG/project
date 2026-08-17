import request from "@/utils/request";

// ��ѯ流程�б�
export function listPositionStep(query) {
  return request({
    url: "/wms-base/PositionStep/list",
    method: "get",
    params: query,
  });
}

// ��ѯ流程��ϸ
export function getPositionStep(id) {
  return request({
    url: "/wms-base/PositionStep/" + id,
    method: "get",
  });
}

// ����流程
export function addPositionStep(data) {
  return request({
    url: "/wms-base/PositionStep",
    method: "post",
    data: data,
  });
}

// �޸�流程
export function updatePositionStep(data) {
  return request({
    url: "/wms-base/PositionStep",
    method: "put",
    data: data,
  });
}

// ɾ��流程
export function delPositionStep(id) {
  return request({
    url: "/wms-base/PositionStep/" + id,
    method: "delete",
  });
}
