import request from '@/utils/request'

// ��ѯ库区�б�
export function listAreaInfo(query) {
  return request({
    url: '/wcs-base/AreaInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ库区��ϸ
export function getAreaInfo(id) {
  return request({
    url: '/wcs-base/AreaInfo/' + id,
    method: 'get'
  })
}

// ����库区
export function addAreaInfo(data) {
  return request({
    url: '/wcs-base/AreaInfo',
    method: 'post',
    data: data
  })
}

// �޸�库区
export function updateAreaInfo(data) {
  return request({
    url: '/wcs-base/AreaInfo',
    method: 'put',
    data: data
  })
}

// ɾ��库区
export function delAreaInfo(id) {
  return request({
    url: '/wcs-base/AreaInfo/' + id,
    method: 'delete'
  })
}
