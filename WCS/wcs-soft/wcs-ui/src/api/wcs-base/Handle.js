import request from '@/utils/request'

// ��ѯ执行器�б�
export function listHandle(query) {
  return request({
    url: '/wcs-base/Handle/list',
    method: 'get',
    params: query
  })
}

// ��ѯ执行器�б�
export function listHandleAll(query) {
  return request({
    url: '/wcs-base/Handle/findAll',
    method: 'get',
    params: query
  })
}

// ��ѯ执行器��ϸ
export function getHandle(id) {
  return request({
    url: '/wcs-base/Handle/' + id,
    method: 'get'
  })
}

// ����执行器
export function addHandle(data) {
  return request({
    url: '/wcs-base/Handle',
    method: 'post',
    data: data
  })
}

// �޸�执行器
export function updateHandle(data) {
  return request({
    url: '/wcs-base/Handle',
    method: 'put',
    data: data
  })
}

// ɾ��执行器
export function delHandle(id) {
  return request({
    url: '/wcs-base/Handle/' + id,
    method: 'delete'
  })
}
