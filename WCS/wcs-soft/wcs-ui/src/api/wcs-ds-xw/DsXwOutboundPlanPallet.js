import request from '@/utils/request'

// ��ѯ出库计划框子关联�б�
export function listDsXwOutboundPlanPallet(query) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlanPallet/list',
    method: 'get',
    params: query
  })
}

// ��ѯ出库计划框子关联��ϸ
export function getDsXwOutboundPlanPallet(id) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlanPallet/' + id,
    method: 'get'
  })
}

// ����出库计划框子关联
export function addDsXwOutboundPlanPallet(data) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlanPallet',
    method: 'post',
    data: data
  })
}

// �޸�出库计划框子关联
export function updateDsXwOutboundPlanPallet(data) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlanPallet',
    method: 'put',
    data: data
  })
}

// ɾ��出库计划框子关联
export function delDsXwOutboundPlanPallet(id) {
  return request({
    url: '/wcs-ds-xw/DsXwOutboundPlanPallet/' + id,
    method: 'delete'
  })
}
