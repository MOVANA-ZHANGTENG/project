import request from '@/utils/request'

// ��ѯ路径执行器�б�
export function listPositionHandle(query) {
  return request({
    url: '/wcs-base/PositionHandle/list',
    method: 'get',
    params: query
  })
}

// ��ѯ路径执行器��ϸ
export function getPositionHandle(id) {
  return request({
    url: '/wcs-base/PositionHandle/' + id,
    method: 'get'
  })
}

// ����路径执行器
export function addPositionHandle(data) {
  return request({
    url: '/wcs-base/PositionHandle',
    method: 'post',
    data: data
  })
}

// �޸�路径执行器
export function updatePositionHandle(data) {
  return request({
    url: '/wcs-base/PositionHandle',
    method: 'put',
    data: data
  })
}

// ɾ��路径执行器
export function delPositionHandle(id) {
  return request({
    url: '/wcs-base/PositionHandle/' + id,
    method: 'delete'
  })
}
