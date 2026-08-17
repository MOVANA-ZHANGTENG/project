import request from '@/utils/request'

// ��ѯ呼叫盒�б�
export function listCallBoxInfo(query) {
  return request({
    url: '/wcs-task/CallBoxInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ呼叫盒��ϸ
export function getCallBoxInfo(id) {
  return request({
    url: '/wcs-task/CallBoxInfo/' + id,
    method: 'get'
  })
}

// ����呼叫盒
export function addCallBoxInfo(data) {
  return request({
    url: '/wcs-task/CallBoxInfo',
    method: 'post',
    data: data
  })
}

// �޸�呼叫盒
export function updateCallBoxInfo(data) {
  return request({
    url: '/wcs-task/CallBoxInfo',
    method: 'put',
    data: data
  })
}

// ɾ��呼叫盒
export function delCallBoxInfo(id) {
  return request({
    url: '/wcs-task/CallBoxInfo/' + id,
    method: 'delete'
  })
}
