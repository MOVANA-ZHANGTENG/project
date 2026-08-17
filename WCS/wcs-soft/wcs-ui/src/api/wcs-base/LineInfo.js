import request from '@/utils/request'

// ��ѯ巷道�б�
export function listLineInfo(query) {
  return request({
    url: '/wcs-base/LineInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ巷道��ϸ
export function getLineInfo(id) {
  return request({
    url: '/wcs-base/LineInfo/' + id,
    method: 'get'
  })
}

// ����巷道
export function addLineInfo(data) {
  return request({
    url: '/wcs-base/LineInfo',
    method: 'post',
    data: data
  })
}

// �޸�巷道
export function updateLineInfo(data) {
  return request({
    url: '/wcs-base/LineInfo',
    method: 'put',
    data: data
  })
}

// ɾ��巷道
export function delLineInfo(id) {
  return request({
    url: '/wcs-base/LineInfo/' + id,
    method: 'delete'
  })
}
