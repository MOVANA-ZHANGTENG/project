import request from '@/utils/request'

// ��ѯ工序�б�
export function listProProcess(query) {
  return request({
    url: '/wcs-base/ProProcess/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工序��ϸ
export function getProProcess(id) {
  return request({
    url: '/wcs-base/ProProcess/' + id,
    method: 'get'
  })
}

// ����工序
export function addProProcess(data) {
  return request({
    url: '/wcs-base/ProProcess',
    method: 'post',
    data: data
  })
}

// �޸�工序
export function updateProProcess(data) {
  return request({
    url: '/wcs-base/ProProcess',
    method: 'put',
    data: data
  })
}

// ɾ��工序
export function delProProcess(id) {
  return request({
    url: '/wcs-base/ProProcess/' + id,
    method: 'delete'
  })
}
