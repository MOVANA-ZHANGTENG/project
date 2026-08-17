import request from '@/utils/request'

// ��ѯ站台资源�б�
export function listPositionsource(query) {
  return request({
    url: '/wcs-base/positionsource/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台资源��ϸ
export function getPositionsource(id) {
  return request({
    url: '/wcs-base/positionsource/' + id,
    method: 'get'
  })
}

// ����站台资源
export function addPositionsource(data) {
  return request({
    url: '/wcs-base/positionsource',
    method: 'post',
    data: data
  })
}

// �޸�站台资源
export function updatePositionsource(data) {
  return request({
    url: '/wcs-base/positionsource',
    method: 'put',
    data: data
  })
}

// ɾ��站台资源
export function delPositionsource(id) {
  return request({
    url: '/wcs-base/positionsource/' + id,
    method: 'delete'
  })
}
