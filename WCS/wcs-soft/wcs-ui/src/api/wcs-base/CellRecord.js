import request from '@/utils/request'

// ��ѯ库位日志记录�б�
export function listCellRecord(query) {
  return request({
    url: '/wcs-base/CellRecord/list',
    method: 'get',
    params: query
  })
}

// ��ѯ库位日志记录��ϸ
export function getCellRecord(id) {
  return request({
    url: '/wcs-base/CellRecord/' + id,
    method: 'get'
  })
}

// ����库位日志记录
export function addCellRecord(data) {
  return request({
    url: '/wcs-base/CellRecord',
    method: 'post',
    data: data
  })
}

// �޸�库位日志记录
export function updateCellRecord(data) {
  return request({
    url: '/wcs-base/CellRecord',
    method: 'put',
    data: data
  })
}

// ɾ��库位日志记录
export function delCellRecord(id) {
  return request({
    url: '/wcs-base/CellRecord/' + id,
    method: 'delete'
  })
}
