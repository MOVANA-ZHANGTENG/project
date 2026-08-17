import request from '@/utils/request'

// ��ѯ托盘信息�б�
export function listPalletInfo(query) {
  return request({
    url: '/wcs-base/palletInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ托盘信息��ϸ
export function getPalletInfo(id) {
  return request({
    url: '/wcs-base/palletInfo/' + id,
    method: 'get'
  })
}

// ����托盘信息
export function addPalletInfo(data) {
  return request({
    url: '/wcs-base/palletInfo',
    method: 'post',
    data: data
  })
}

// �޸�托盘信息
export function updatePalletInfo(data) {
  return request({
    url: '/wcs-base/palletInfo',
    method: 'put',
    data: data
  })
}

// ɾ��托盘信息
export function delPalletInfo(id) {
  return request({
    url: '/wcs-base/palletInfo/' + id,
    method: 'delete'
  })
}
