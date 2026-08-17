import request from '@/utils/request'

// ��ѯ分组管理�б�
export function listHandleGroup(query) {
  return request({
    url: '/wcs-base/handleGroup/list',
    method: 'get',
    params: query
  })
}

// ��ѯ分组管理��ϸ
export function getHandleGroup(id) {
  return request({
    url: '/wcs-base/handleGroup/' + id,
    method: 'get'
  })
}

// ����分组管理
export function addHandleGroup(data) {
  return request({
    url: '/wcs-base/handleGroup',
    method: 'post',
    data: data
  })
}

// �޸�分组管理
export function updateHandleGroup(data) {
  return request({
    url: '/wcs-base/handleGroup',
    method: 'put',
    data: data
  })
}

// ɾ��分组管理
export function delHandleGroup(id) {
  return request({
    url: '/wcs-base/handleGroup/' + id,
    method: 'delete'
  })
}
