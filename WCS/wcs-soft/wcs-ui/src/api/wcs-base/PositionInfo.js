import request from '@/utils/request'

// ��ѯ站台�б�
export function listPositionInfo(query) {
  return request({
    url: '/wcs-base/PositionInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台��ϸ
export function getPositionInfo(id) {
  return request({
    url: '/wcs-base/PositionInfo/' + id,
    method: 'get'
  })
}

// ����站台
export function addPositionInfo(data) {
  return request({
    url: '/wcs-base/PositionInfo',
    method: 'post',
    data: data
  })
}

// �޸�站台
export function updatePositionInfo(data) {
  return request({
    url: '/wcs-base/PositionInfo',
    method: 'put',
    data: data
  })
}

// ɾ��站台
export function delPositionInfo(id) {
  return request({
    url: '/wcs-base/PositionInfo/' + id,
    method: 'delete'
  })
}
