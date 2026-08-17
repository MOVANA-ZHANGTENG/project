import request from '@/utils/request'

// ��ѯ生产计划�б�
export function listDsRealPlan(query) {
  return request({
    url: '/wcs-ds/DsRealPlan/list',
    method: 'get',
    params: query
  })
}

// ��ѯ生产计划��ϸ
export function getDsRealPlan(id) {
  return request({
    url: '/wcs-ds/DsRealPlan/' + id,
    method: 'get'
  })
}

// ����生产计划
export function addDsRealPlan(data) {
  return request({
    url: '/wcs-ds/DsRealPlan',
    method: 'post',
    data: data
  })
}

// �޸�生产计划
export function updateDsRealPlan(data) {
  return request({
    url: '/wcs-ds/DsRealPlan',
    method: 'put',
    data: data
  })
}

// ɾ��生产计划
export function delDsRealPlan(id) {
  return request({
    url: '/wcs-ds/DsRealPlan/' + id,
    method: 'delete'
  })
}
