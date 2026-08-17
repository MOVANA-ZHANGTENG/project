import request from '@/utils/request'

// ��ѯ站台资源�б�
export function listSource(query) {
  return request({
    url: '/wcs-base/source/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台资源��ϸ
export function getSource(id) {
  return request({
    url: '/wcs-base/source/' + id,
    method: 'get'
  })
}

// ����站台资源
export function addSource(data) {
  return request({
    url: '/wcs-base/source',
    method: 'post',
    data: data
  })
}

// �޸�站台资源
export function updateSource(data) {
  return request({
    url: '/wcs-base/source',
    method: 'put',
    data: data
  })
}

// ɾ��站台资源
export function delSource(id) {
  return request({
    url: '/wcs-base/source/' + id,
    method: 'delete'
  })
}
