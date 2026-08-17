import request from '@/utils/request'

// ��ѯ产线物料�б�
export function listLineItem(query) {
  return request({
    url: '/wcs-base/LineItem/list',
    method: 'get',
    params: query
  })
}

// ��ѯ产线物料��ϸ
export function getLineItem(id) {
  return request({
    url: '/wcs-base/LineItem/' + id,
    method: 'get'
  })
}

// ����产线物料
export function addLineItem(data) {
  return request({
    url: '/wcs-base/LineItem',
    method: 'post',
    data: data
  })
}

// �޸�产线物料
export function updateLineItem(data) {
  return request({
    url: '/wcs-base/LineItem',
    method: 'put',
    data: data
  })
}

// ɾ��产线物料
export function delLineItem(id) {
  return request({
    url: '/wcs-base/LineItem/' + id,
    method: 'delete'
  })
}
