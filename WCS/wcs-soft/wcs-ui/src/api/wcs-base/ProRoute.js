import request from '@/utils/request'

// ��ѯ工艺流程�б�
export function listProRoute(query) {
  return request({
    url: '/wcs-base/ProRoute/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工艺流程��ϸ
export function getProRoute(id) {
  return request({
    url: '/wcs-base/ProRoute/' + id,
    method: 'get'
  })
}

// ����工艺流程
export function addProRoute(data) {
  return request({
    url: '/wcs-base/ProRoute',
    method: 'post',
    data: data
  })
}

// �޸�工艺流程
export function updateProRoute(data) {
  return request({
    url: '/wcs-base/ProRoute',
    method: 'put',
    data: data
  })
}

// ɾ��工艺流程
export function delProRoute(id) {
  return request({
    url: '/wcs-base/ProRoute/' + id,
    method: 'delete'
  })
}
