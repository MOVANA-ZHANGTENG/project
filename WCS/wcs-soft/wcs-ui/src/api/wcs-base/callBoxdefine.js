import request from '@/utils/request'

// ��ѯ呼叫盒的定义�б�
export function listCallBoxdefine(query) {
  return request({
    url: '/wcs-task/callBoxdefine/list',
    method: 'get',
    params: query
  })
}

// ��ѯ呼叫盒的定义��ϸ
export function getCallBoxdefine(id) {
  return request({
    url: '/wcs-task/callBoxdefine/' + id,
    method: 'get'
  })
}

// ����呼叫盒的定义
export function addCallBoxdefine(data) {
  return request({
    url: '/wcs-task/callBoxdefine',
    method: 'post',
    data: data
  })
}

// �޸�呼叫盒的定义
export function updateCallBoxdefine(data) {
  return request({
    url: '/wcs-task/callBoxdefine',
    method: 'put',
    data: data
  })
}

// ɾ��呼叫盒的定义
export function delCallBoxdefine(id) {
  return request({
    url: '/wcs-task/callBoxdefine/' + id,
    method: 'delete'
  })
}
