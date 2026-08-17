import request from '@/utils/request'

// ��ѯ料袋详情�б�
export function listBagDetail(query) {
  return request({
    url: '/wcs-ds/bagDetail/list',
    method: 'get',
    params: query
  })
}

// ��ѯ料袋详情��ϸ
export function getBagDetail(mOrderId) {
  return request({
    url: '/wcs-ds/bagDetail/' + mOrderId,
    method: 'get'
  })
}

// ����料袋详情
export function addBagDetail(data) {
  return request({
    url: '/wcs-ds/bagDetail',
    method: 'post',
    data: data
  })
}

// �޸�料袋详情
export function updateBagDetail(data) {
  return request({
    url: '/wcs-ds/bagDetail',
    method: 'put',
    data: data
  })
}

// ɾ��料袋详情
export function delBagDetail(mOrderId) {
  return request({
    url: '/wcs-ds/bagDetail/' + mOrderId,
    method: 'delete'
  })
}
