import request from '@/utils/request'

// ��ѯ产品型号�б�
export function listProductModel(query) {
  return request({
    url: '/wcs-xlPro/ProductModel/list',
    method: 'get',
    params: query
  })
}

// ��ѯ产品型号��ϸ
export function getProductModel(id) {
  return request({
    url: '/wcs-xlPro/ProductModel/' + id,
    method: 'get'
  })
}

// ����产品型号
export function addProductModel(data) {
  return request({
    url: '/wcs-xlPro/ProductModel',
    method: 'post',
    data: data
  })
}

// �޸�产品型号
export function updateProductModel(data) {
  return request({
    url: '/wcs-xlPro/ProductModel',
    method: 'put',
    data: data
  })
}

// ɾ��产品型号
export function delProductModel(id) {
  return request({
    url: '/wcs-xlPro/ProductModel/' + id,
    method: 'delete'
  })
}
