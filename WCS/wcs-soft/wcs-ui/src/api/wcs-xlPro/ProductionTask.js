import request from '@/utils/request'

// ��ѯ生产任务�б�
export function listProductionTask(query) {
  return request({
    url: '/wcs-xlPro/ProductionTask/list',
    method: 'get',
    params: query
  })
}

// ��ѯ生产任务��ϸ
export function getProductionTask(id) {
  return request({
    url: '/wcs-xlPro/ProductionTask/' + id,
    method: 'get'
  })
}

// ����生产任务
export function addProductionTask(data) {
  return request({
    url: '/wcs-xlPro/ProductionTask',
    method: 'post',
    data: data
  })
}

// �޸�生产任务
export function updateProductionTask(data) {
  return request({
    url: '/wcs-xlPro/ProductionTask',
    method: 'put',
    data: data
  })
}

// ɾ��生产任务
export function delProductionTask(id) {
  return request({
    url: '/wcs-xlPro/ProductionTask/' + id,
    method: 'delete'
  })
}
