import request from '@/utils/request'

// ��ѯ呼叫盒记录�б�
export function listCallBoxRecord(query) {
  return request({
    url: '/wcs-task/CallBoxRecord/list',
    method: 'get',
    params: query
  })
}

// ��ѯ呼叫盒记录��ϸ
export function getCallBoxRecord(id) {
  return request({
    url: '/wcs-task/CallBoxRecord/' + id,
    method: 'get'
  })
}

// ����呼叫盒记录
export function addCallBoxRecord(data) {
  return request({
    url: '/wcs-task/CallBoxRecord',
    method: 'post',
    data: data
  })
}

// �޸�呼叫盒记录
export function updateCallBoxRecord(data) {
  return request({
    url: '/wcs-task/CallBoxRecord',
    method: 'put',
    data: data
  })
}

// ɾ��呼叫盒记录
export function delCallBoxRecord(id) {
  return request({
    url: '/wcs-task/CallBoxRecord/' + id,
    method: 'delete'
  })
}
