import request from '@/utils/request'

// ��ѯ电极信息�б�
export function listBatteryInfo(query) {
  return request({
    url: '/wcs-lg1/batteryInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ电极信息��ϸ
export function getBatteryInfo(id) {
  return request({
    url: '/wcs-lg1/batteryInfo/' + id,
    method: 'get'
  })
}

// ����电极信息
export function addBatteryInfo(data) {
  return request({
    url: '/wcs-lg1/batteryInfo',
    method: 'post',
    data: data
  })
}

// �޸�电极信息
export function updateBatteryInfo(data) {
  return request({
    url: '/wcs-lg1/batteryInfo',
    method: 'put',
    data: data
  })
}

// ɾ��电极信息
export function delBatteryInfo(id) {
  return request({
    url: '/wcs-lg1/batteryInfo/' + id,
    method: 'delete'
  })
}
