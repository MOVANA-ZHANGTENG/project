import request from '@/utils/request'

// ��ѯ呼叫盒任务�б�
export function listCallBox(query) {
  return request({
    url: '/wcs-base/callBox/list',
    method: 'get',
    params: query
  })
}

// ��ѯ呼叫盒任务��ϸ
export function getCallBox(id) {
  return request({
    url: '/wcs-base/callBox/' + id,
    method: 'get'
  })
}

// ����呼叫盒任务
export function addCallBox(data) {
  return request({
    url: '/wcs-base/callBox',
    method: 'post',
    data: data
  })
}

// �޸�呼叫盒任务
export function updateCallBox(data) {
  return request({
    url: '/wcs-base/callBox',
    method: 'put',
    data: data
  })
}

// ɾ��呼叫盒任务
export function delCallBox(id) {
  return request({
    url: '/wcs-base/callBox/' + id,
    method: 'delete'
  })
}
