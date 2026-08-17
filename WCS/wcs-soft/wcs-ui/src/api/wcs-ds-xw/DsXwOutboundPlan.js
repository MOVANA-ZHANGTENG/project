import request from '@/utils/request'

// ��ѯ出库计划�б�
export function listDsXwOutboundPlan(query) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlan/list',
    method: 'get',
    params: query
  })
}

// ��ѯ出库计划��ϸ
export function getDsXwOutboundPlan(id) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlan/' + id,
    method: 'get'
  })
}

// ����出库计划
export function addDsXwOutboundPlan(data) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlan',
    method: 'post',
    data: data
  })
}

// �޸�出库计划
export function updateDsXwOutboundPlan(data) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlan',
    method: 'put',
    data: data
  })
}

// ɾ��出库计划
export function delDsXwOutboundPlan(id) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlan/' + id,
    method: 'delete'
  })
}
