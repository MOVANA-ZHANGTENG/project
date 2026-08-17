import request from '@/utils/request'

// ��ѯ工艺流程�б�
export function listProcessRoute(query) {
  return request({
    url: '/wcs-xlPro/ProcessRoute/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工艺流程��ϸ
export function getProcessRoute(id) {
  return request({
    url: '/wcs-xlPro/ProcessRoute/' + id,
    method: 'get'
  })
}

// ����工艺流程
export function addProcessRoute(data) {
  return request({
    url: '/wcs-xlPro/ProcessRoute',
    method: 'post',
    data: data
  })
}

// �޸�工艺流程
export function updateProcessRoute(data) {
  return request({
    url: '/wcs-xlPro/ProcessRoute',
    method: 'put',
    data: data
  })
}

// ɾ��工艺流程
export function delProcessRoute(id) {
  return request({
    url: '/wcs-xlPro/ProcessRoute/' + id,
    method: 'delete'
  })
}


export function findModelAll() {
  return request({
    url: '/wcs-xlPro/ProcessRoute/findModelAll',
    method: 'get'
  })
}

