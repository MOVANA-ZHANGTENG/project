import request from '@/utils/request'

// ��ѯ仓库设置�б�
export function listWareInfo(query) {
  return request({
    url: '/wcs-base/WareInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ仓库设置��ϸ
export function getWareInfo(id) {
  return request({
    url: '/wcs-base/WareInfo/' + id,
    method: 'get'
  })
}

// ����仓库设置
export function addWareInfo(data) {
  return request({
    url: '/wcs-base/WareInfo',
    method: 'post',
    data: data
  })
}

// �޸�仓库设置
export function updateWareInfo(data) {
  return request({
    url: '/wcs-base/WareInfo',
    method: 'put',
    data: data
  })
}

// �޸�仓库设置
export function updateWareModel(data) {
  return request({
    url: '/wcs-base/WareInfo/editWareModel',
    method: 'put',
    data: data
  })
}
// ɾ��仓库设置
export function delWareInfo(id) {
  return request({
    url: '/wcs-base/WareInfo/' + id,
    method: 'delete'
  })
}
