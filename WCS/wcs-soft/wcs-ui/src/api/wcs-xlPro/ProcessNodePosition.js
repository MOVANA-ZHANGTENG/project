import request from '@/utils/request'

// ��ѯ工艺节点站台关联�б�
export function listProcessNodePosition(query) {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工艺节点站台关联��ϸ
export function getProcessNodePosition(id) {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition/' + id,
    method: 'get'
  })
}

// ����工艺节点站台关联
export function addProcessNodePosition(data) {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition',
    method: 'post',
    data: data
  })
}

// �޸�工艺节点站台关联
export function updateProcessNodePosition(data) {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition',
    method: 'put',
    data: data
  })
}

// ɾ��工艺节点站台关联
export function delProcessNodePosition(id) {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition/' + id,
    method: 'delete'
  })
}

// ��ѯ工艺节点根据工艺流程ID
export function getProcessNodeListByRouteId() {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition/getProcessNodeListByRouteId',
    method: 'get',
  
  })
}

// ��ѯ站台列表
export function getPositionList() {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition/getPositionList',
    method: 'get',
  })
}

// ��ѯ夹具类型列表
export function getFixtureTypeList() {
  return request({
    url: '/wcs-xlPro/ProcessNodePosition/getFixtureTypeList',
    method: 'get',
  })
}
