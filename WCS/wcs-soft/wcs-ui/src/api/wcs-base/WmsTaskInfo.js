import request from '@/utils/request'

// ��ѯwms任务�б�
export function listWmsTaskInfo(query) {
  return request({
    url: '/wcs-base/WmsTaskInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯwms任务��ϸ
export function getWmsTaskInfo(id) {
  return request({
    url: '/wcs-base/WmsTaskInfo/' + id,
    method: 'get'
  })
}

// ����wms任务
export function addWmsTaskInfo(data) {
  return request({
    url: '/wcs-base/WmsTaskInfo',
    method: 'post',
    data: data
  })
}

// �޸�wms任务
export function updateWmsTaskInfo(data) {
  return request({
    url: '/wcs-base/WmsTaskInfo',
    method: 'put',
    data: data
  })
}

// ɾ��wms任务
export function delWmsTaskInfo(id) {
  return request({
    url: '/wcs-base/WmsTaskInfo/' + id,
    method: 'delete'
  })
}
