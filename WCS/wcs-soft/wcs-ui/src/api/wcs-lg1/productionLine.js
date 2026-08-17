import request from '@/utils/request'

// ��ѯ产线信息�б�
export function listProductionLine(query) {
  return request({
    url: '/wcs-lg1/productionLine/list',
    method: 'get',
    params: query
  })
}

// ��ѯ产线信息��ϸ
export function getProductionLine(id) {
  return request({
    url: '/wcs-lg1/productionLine/' + id,
    method: 'get'
  })
}

// ����产线信息
export function addProductionLine(data) {
  return request({
    url: '/wcs-lg1/productionLine',
    method: 'post',
    data: data
  })
}

// �޸�产线信息
export function updateProductionLine(data) {
  return request({
    url: '/wcs-lg1/productionLine',
    method: 'put',
    data: data
  })
}

// ɾ��产线信息
export function delProductionLine(id) {
  return request({
    url: '/wcs-lg1/productionLine/' + id,
    method: 'delete'
  })
}
