import request from '@/utils/request'

// ��ѯ设备�б�
export function listDeviceInfo(query) {
  return request({
    url: '/wcs-base/DeviceInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ设备��ϸ
export function getDeviceInfo(id) {
  return request({
    url: '/wcs-base/DeviceInfo/' + id,
    method: 'get'
  })
}

// ����设备
export function addDeviceInfo(data) {
  return request({
    url: '/wcs-base/DeviceInfo',
    method: 'post',
    data: data
  })
}

// �޸�设备
export function updateDeviceInfo(data) {
  return request({
    url: '/wcs-base/DeviceInfo',
    method: 'put',
    data: data
  })
}

// ɾ��设备
export function delDeviceInfo(id) {
  return request({
    url: '/wcs-base/DeviceInfo/' + id,
    method: 'delete'
  })
}
