import request from '@/utils/request'

// ��ѯ路径�б�
export function listPositionCondition(query) {
  return request({
    url: '/wcs-base/PositionCondition/list',
    method: 'get',
    params: query
  })
}

// ��ѯ路径��ϸ
export function getPositionCondition(id) {
  return request({
    url: '/wcs-base/PositionCondition/' + id,
    method: 'get'
  })
}

// ����路径
export function addPositionCondition(data) {
  return request({
    url: '/wcs-base/PositionCondition',
    method: 'post',
    data: data
  })
}

// �޸�路径
export function updatePositionCondition(data) {
  return request({
    url: '/wcs-base/PositionCondition',
    method: 'put',
    data: data
  })
}

// ɾ��路径
export function delPositionCondition(id) {
  return request({
    url: '/wcs-base/PositionCondition/' + id,
    method: 'delete'
  })
}
