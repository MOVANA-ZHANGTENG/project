import request from '@/utils/request'

// ��ѯ库存信息�б�
export function listInventory(query) {
  return request({
    url: '/wcs-base/inventory/list',
    method: 'get',
    params: query
  })
}

// ��ѯ库存信息��ϸ
export function getInventory(id) {
  return request({
    url: '/wcs-base/inventory/' + id,
    method: 'get'
  })
}

// ����库存信息
export function addInventory(data) {
  return request({
    url: '/wcs-base/inventory',
    method: 'post',
    data: data
  })
}

// �޸�库存信息
export function updateInventory(data) {
  return request({
    url: '/wcs-base/inventory',
    method: 'put',
    data: data
  })
}

// ɾ��库存信息
export function delInventory(id) {
  return request({
    url: '/wcs-base/inventory/' + id,
    method: 'delete'
  })
}
