import request from '@/utils/request'

// ��ѯ设备任务回传�б�
export function listDeviceTaskResult(query) {
  return request({
    url: '/wcs-task/DeviceTaskResult/list',
    method: 'get',
    params: query
  })
}

// ��ѯ设备任务回传��ϸ
export function getDeviceTaskResult(id) {
  return request({
    url: '/wcs-task/DeviceTaskResult/' + id,
    method: 'get'
  })
}

// ����设备任务回传
export function addDeviceTaskResult(data) {
  return request({
    url: '/wcs-task/DeviceTaskResult',
    method: 'post',
    data: data
  })
}

// �޸�设备任务回传
export function updateDeviceTaskResult(data) {
  return request({
    url: '/wcs-task/DeviceTaskResult',
    method: 'put',
    data: data
  })
}

// ɾ��设备任务回传
export function delDeviceTaskResult(id) {
  return request({
    url: '/wcs-task/DeviceTaskResult/' + id,
    method: 'delete'
  })
}
