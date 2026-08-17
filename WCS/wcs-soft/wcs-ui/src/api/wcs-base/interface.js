import request from '@/utils/request'

// ��ѯ接口记录�б�
export function listInterface(query) {
  return request({
    url: '/wcs-base/interface/list',
    method: 'get',
    params: query
  })
}

// ��ѯ接口记录��ϸ
export function getInterface(id) {
  return request({
    url: '/wcs-base/interface/' + id,
    method: 'get'
  })
}

// ����接口记录
export function addInterface(data) {
  return request({
    url: '/wcs-base/interface',
    method: 'post',
    data: data
  })
}

// �޸�接口记录
export function updateInterface(data) {
  return request({
    url: '/wcs-base/interface',
    method: 'put',
    data: data
  })
}

// ɾ��接口记录
export function delInterface(id) {
  return request({
    url: '/wcs-base/interface/' + id,
    method: 'delete'
  })
}
