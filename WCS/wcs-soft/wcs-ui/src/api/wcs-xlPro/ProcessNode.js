import request from '@/utils/request'

// ��ѯ工艺节点�б�
export function listProcessNode(query) {
  return request({
    url: '/wcs-xlPro/ProcessNode/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工艺节点��ϸ
export function getProcessNode(id) {
  return request({
    url: '/wcs-xlPro/ProcessNode/' + id,
    method: 'get'
  })
}

// ����工艺节点
export function addProcessNode(data) {
  return request({
    url: '/wcs-xlPro/ProcessNode',
    method: 'post',
    data: data
  })
}

// �޸�工艺节点
export function updateProcessNode(data) {
  return request({
    url: '/wcs-xlPro/ProcessNode',
    method: 'put',
    data: data
  })
}

// ɾ��工艺节点
export function delProcessNode(id) {
  return request({
    url: '/wcs-xlPro/ProcessNode/' + id,
    method: 'delete'
  })
}

// ��ѯ工艺节点�б�
export function findProcessNodeByRouteId() {
  return request({
    url: '/wcs-xlPro/ProcessNode/findProcessNodeByRouteId',
    method: 'get',
  })
}

// ��ѯ工艺节点根据工艺流程ID
export function listProcessNodeByRouteId(id) {
  return request({
    url: '/wcs-xlPro/ProcessNode/getNode/' + id,
    method: 'get',
  
  })
}
