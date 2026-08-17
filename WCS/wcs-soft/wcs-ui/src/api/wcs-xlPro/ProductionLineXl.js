import request from '@/utils/request'

// ��ѯ产线�б�
export function listProductionLineXl(query) {
  return request({
    url: '/wcs-xlPro/ProductionLineXl/list',
    method: 'get',
    params: query
  })
}

// ��ѯ产线��ϸ
export function getProductionLineXl(id) {
  return request({
    url: '/wcs-xlPro/ProductionLineXl/' + id,
    method: 'get'
  })
}

// ����产线
export function addProductionLineXl(data) {
  return request({
    url: '/wcs-xlPro/ProductionLineXl',
    method: 'post',
    data: data
  })
}

// �޸�产线
export function updateProductionLineXl(data) {
  return request({
    url: '/wcs-xlPro/ProductionLineXl',
    method: 'put',
    data: data
  })
}

// ɾ��产线
export function delProductionLineXl(id) {
  return request({
    url: '/wcs-xlPro/ProductionLineXl/' + id,
    method: 'delete'
  })
}
