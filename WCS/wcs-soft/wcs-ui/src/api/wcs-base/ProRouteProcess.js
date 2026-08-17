import request from '@/utils/request'

// ��ѯ工艺流程工序关联�б�
export function listProRouteProcess(query) {
  return request({
    url: '/wcs-base/ProRouteProcess/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工艺流程工序关联��ϸ
export function getProRouteProcess(id) {
  return request({
    url: '/wcs-base/ProRouteProcess/' + id,
    method: 'get'
  })
}

// ����工艺流程工序关联
export function addProRouteProcess(data) {
  return request({
    url: '/wcs-base/ProRouteProcess',
    method: 'post',
    data: data
  })
}

// �޸�工艺流程工序关联
export function updateProRouteProcess(data) {
  return request({
    url: '/wcs-base/ProRouteProcess',
    method: 'put',
    data: data
  })
}

// ɾ��工艺流程工序关联
export function delProRouteProcess(id) {
  return request({
    url: '/wcs-base/ProRouteProcess/' + id,
    method: 'delete'
  })
}
