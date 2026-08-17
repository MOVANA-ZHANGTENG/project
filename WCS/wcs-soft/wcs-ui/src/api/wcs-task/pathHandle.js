import request from '@/utils/request'

// ��ѯ路径方法�б�
export function listPathHandle(query) {
  return request({
    url: '/wcs-task/pathHandle/list',
    method: 'get',
    params: query
  })
}

// ��ѯ路径方法��ϸ
export function getPathHandle(id) {
  return request({
    url: '/wcs-task/pathHandle/' + id,
    method: 'get'
  })
}

// ����路径方法
export function addPathHandle(data) {
  return request({
    url: '/wcs-task/pathHandle',
    method: 'post',
    data: data
  })
}

// �޸�路径方法
export function updatePathHandle(data) {
  return request({
    url: '/wcs-task/pathHandle',
    method: 'put',
    data: data
  })
}

// ɾ��路径方法
export function delPathHandle(id) {
  return request({
    url: '/wcs-task/pathHandle/' + id,
    method: 'delete'
  })
}
