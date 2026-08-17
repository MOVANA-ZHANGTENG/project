import request from '@/utils/request'

// ��ѯ执行路径�б�
export function listPathInfo(query) {
  return request({
    url: '/wcs-base/pathInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ执行路径��ϸ
export function getPathInfo(id) {
  return request({
    url: '/wcs-base/pathInfo/' + id,
    method: 'get'
  })
}

// ����执行路径
export function addPathInfo(data) {
  return request({
    url: '/wcs-base/pathInfo',
    method: 'post',
    data: data
  })
}

// �޸�执行路径
export function updatePathInfo(data) {
  return request({
    url: '/wcs-base/pathInfo',
    method: 'put',
    data: data
  })
}

// ɾ��执行路径
export function delPathInfo(id) {
  return request({
    url: '/wcs-base/pathInfo/' + id,
    method: 'delete'
  })
}


