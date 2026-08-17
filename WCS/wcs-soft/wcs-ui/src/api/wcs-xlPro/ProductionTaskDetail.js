import request from '@/utils/request'

// ��ѯ生产任务明细�б�
export function listProductionTaskDetail(query) {
  return request({
    url: '/wcs-xlPro/ProductionTaskDetail/list',
    method: 'get',
    params: query
  })
}

// ��ѯ生产任务明细��ϸ
export function getProductionTaskDetail(id) {
  return request({
    url: '/wcs-xlPro/ProductionTaskDetail/' + id,
    method: 'get'
  })
}

// ����生产任务明细
export function addProductionTaskDetail(data) {
  return request({
    url: '/wcs-xlPro/ProductionTaskDetail',
    method: 'post',
    data: data
  })
}

// �޸�生产任务明细
export function updateProductionTaskDetail(data) {
  return request({
    url: '/wcs-xlPro/ProductionTaskDetail',
    method: 'put',
    data: data
  })
}

// ɾ��生产任务明细
export function delProductionTaskDetail(id) {
  return request({
    url: '/wcs-xlPro/ProductionTaskDetail/' + id,
    method: 'delete'
  })
}
