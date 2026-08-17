import request from '@/utils/request'

// ��ѯ站台日志�б�
export function listPositionRecord(query) {
  return request({
    url: '/wcs-base/PositionRecord/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台日志��ϸ
export function getPositionRecord(id) {
  return request({
    url: '/wcs-base/PositionRecord/' + id,
    method: 'get'
  })
}

// ����站台日志
export function addPositionRecord(data) {
  return request({
    url: '/wcs-base/PositionRecord',
    method: 'post',
    data: data
  })
}

// �޸�站台日志
export function updatePositionRecord(data) {
  return request({
    url: '/wcs-base/PositionRecord',
    method: 'put',
    data: data
  })
}

// ɾ��站台日志
export function delPositionRecord(id) {
  return request({
    url: '/wcs-base/PositionRecord/' + id,
    method: 'delete'
  })
}
