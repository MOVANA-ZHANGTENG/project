import request from '@/utils/request'

// ��ѯ托盘类型�б�
export function listPalletType(query) {
  return request({
    url: '/system/palletType/list',
    method: 'get',
    params: query
  })
}

// ��ѯ托盘类型��ϸ
export function getPalletType(id) {
  return request({
    url: '/system/palletType/' + id,
    method: 'get'
  })
}

// ����托盘类型
export function addPalletType(data) {
  return request({
    url: '/system/palletType',
    method: 'post',
    data: data
  })
}

// �޸�托盘类型
export function updatePalletType(data) {
  return request({
    url: '/system/palletType',
    method: 'put',
    data: data
  })
}

// ɾ��托盘类型
export function delPalletType(id) {
  return request({
    url: '/system/palletType/' + id,
    method: 'delete'
  })
}
