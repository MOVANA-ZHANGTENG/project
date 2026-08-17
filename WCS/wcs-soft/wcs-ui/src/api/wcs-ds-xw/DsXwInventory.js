import request from '@/utils/request'

// ��ѯ验布库存�б�
export function listDsXwInventory(query) {
  return request({
    url: '/wcs-ds-xw/DsXwInventory/list',
    method: 'get',
    params: query
  })
}

// ��ѯ验布库存��ϸ
export function getDsXwInventory(id) {
  return request({
    url: '/wcs-ds-xw/DsXwInventory/' + id,
    method: 'get'
  })
}

// ����验布库存
export function addDsXwInventory(data) {
  return request({
    url: '/wcs-ds-xw/DsXwInventory',
    method: 'post',
    data: data
  })
}

// �޸�验布库存
export function updateDsXwInventory(data) {
  return request({
    url: '/wcs-ds-xw/DsXwInventory',
    method: 'put',
    data: data
  })
}

// ɾ��验布库存
export function delDsXwInventory(id) {
  return request({
    url: '/wcs-ds-xw/DsXwInventory/' + id,
    method: 'delete'
  })
}
