import request from '@/utils/request'

// ��ѯ库存变更�б�
export function listMapCellLog(query) {
  return request({
    url: '/wcs-rcs/MapCellLog/list',
    method: 'get',
    params: query
  })
}

// ��ѯ库存变更��ϸ
export function getMapCellLog(id) {
  return request({
    url: '/wcs-rcs/MapCellLog/' + id,
    method: 'get'
  })
}

// ����库存变更
export function addMapCellLog(data) {
  return request({
    url: '/wcs-rcs/MapCellLog',
    method: 'post',
    data: data
  })
}

// �޸�库存变更
export function updateMapCellLog(data) {
  return request({
    url: '/wcs-rcs/MapCellLog',
    method: 'put',
    data: data
  })
}

// ɾ��库存变更
export function delMapCellLog(id) {
  return request({
    url: '/wcs-rcs/MapCellLog/' + id,
    method: 'delete'
  })
}
