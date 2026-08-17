import request from '@/utils/request'

// ��ѯ托盘记录�б�
export function listPalletRecord(query) {
  return request({
    url: '/wcs-task/PalletRecord/list',
    method: 'get',
    params: query
  })
}

// ��ѯ托盘记录��ϸ
export function getPalletRecord(id) {
  return request({
    url: '/wcs-task/PalletRecord/' + id,
    method: 'get'
  })
}

// ����托盘记录
export function addPalletRecord(data) {
  return request({
    url: '/wcs-task/PalletRecord',
    method: 'post',
    data: data
  })
}

// �޸�托盘记录
export function updatePalletRecord(data) {
  return request({
    url: '/wcs-task/PalletRecord',
    method: 'put',
    data: data
  })
}

// ɾ��托盘记录
export function delPalletRecord(id) {
  return request({
    url: '/wcs-task/PalletRecord/' + id,
    method: 'delete'
  })
}
