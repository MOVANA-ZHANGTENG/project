import request from '@/utils/request'

// ��ѯ呼叫盒执行器�б�
export function listCallBoxHandle(query) {
  return request({
    url: '/wcs-task/callBoxHandle/list',
    method: 'get',
    params: query
  })
}

// ��ѯ呼叫盒执行器��ϸ
export function getCallBoxHandle(id) {
  return request({
    url: '/wcs-task/callBoxHandle/' + id,
    method: 'get'
  })
}

// ����呼叫盒执行器
export function addCallBoxHandle(data) {
  return request({
    url: '/wcs-task/callBoxHandle',
    method: 'post',
    data: data
  })
}

// �޸�呼叫盒执行器
export function updateCallBoxHandle(data) {
  return request({
    url: '/wcs-task/callBoxHandle',
    method: 'put',
    data: data
  })
}

// ɾ��呼叫盒执行器
export function delCallBoxHandle(id) {
  return request({
    url: '/wcs-task/callBoxHandle/' + id,
    method: 'delete'
  })
}
