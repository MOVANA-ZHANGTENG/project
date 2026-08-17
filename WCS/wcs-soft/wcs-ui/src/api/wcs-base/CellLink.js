import request from '@/utils/request'

// ��ѯ库位邻接关系，存储四向车调度系统的节点联通关系�б�
export function listCellLink(query) {
  return request({
    url: '/wcs-base/CellLink/list',
    method: 'get',
    params: query
  })
}

// ��ѯ库位邻接关系，存储四向车调度系统的节点联通关系��ϸ
export function getCellLink(id) {
  return request({
    url: '/wcs-base/CellLink/' + id,
    method: 'get'
  })
}

// ����库位邻接关系，存储四向车调度系统的节点联通关系
export function addCellLink(data) {
  return request({
    url: '/wcs-base/CellLink',
    method: 'post',
    data: data
  })
}

// �޸�库位邻接关系，存储四向车调度系统的节点联通关系
export function updateCellLink(data) {
  return request({
    url: '/wcs-base/CellLink',
    method: 'put',
    data: data
  })
}

// ɾ��库位邻接关系，存储四向车调度系统的节点联通关系
export function delCellLink(id) {
  return request({
    url: '/wcs-base/CellLink/' + id,
    method: 'delete'
  })
}

// 根据起始库位ID、目标库位ID和仓库编码删除连接线
export function deleteByFromCellIdAndToCellIdAndWareCode(params) {
  return request({
    url: '/wcs-base/CellLink/deleteByFromCellIdAndToCellIdAndWareCode',
    method: 'get',
    params: params
  })
}