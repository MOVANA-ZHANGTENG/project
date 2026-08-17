import request from '@/utils/request'

// ��ѯ库位�б�
export function listCellInfo(query) {
  return request({
    url: '/wcs-base/CellInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ库位��ϸ
export function getCellInfo(id) {
  return request({
    url: '/wcs-base/CellInfo/' + id,
    method: 'get'
  })
}

// ����库位
export function addCellInfo(data) {
  return request({
    url: '/wcs-base/CellInfo',
    method: 'post',
    data: data
  })
}

// �޸�库位
export function updateCellInfo(data) {
  return request({
    url: '/wcs-base/CellInfo',
    method: 'put',
    data: data
  })
}

// 编辑模式：更新库位配置（type、preCode、subX、subY、subZ等）
export function updateCellConfig(data) {
  return request({
    url: '/wcs-base/CellInfo/updateConfig',
    method: 'put',
    data: data
  })
}

// ɾ��库位
export function delCellInfo(id) {
  return request({
    url: '/wcs-base/CellInfo/' + id,
    method: 'delete'
  })
}
