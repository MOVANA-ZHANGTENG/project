import request from '@/utils/request'

// ��ѯ产品�б�
export function listProductInfo(query) {
  return request({
    url: '/wcs-base/ProductInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ产品��ϸ
export function getProductInfo(id) {
  return request({
    url: '/wcs-base/ProductInfo/' + id,
    method: 'get'
  })
}

// ����产品
export function addProductInfo(data) {
  return request({
    url: '/wcs-base/ProductInfo',
    method: 'post',
    data: data
  })
}

// �޸�产品
export function updateProductInfo(data) {
  return request({
    url: '/wcs-base/ProductInfo',
    method: 'put',
    data: data
  })
}

// ɾ��产品
export function delProductInfo(id) {
  return request({
    url: '/wcs-base/ProductInfo/' + id,
    method: 'delete'
  })
}
