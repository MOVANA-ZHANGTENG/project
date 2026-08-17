import request from '@/utils/request'

// ��ѯ层�б�
export function listFloorInfo(query) {
  return request({
    url: '/wcs-base/FloorInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ层��ϸ
export function getFloorInfo(id) {
  return request({
    url: '/wcs-base/FloorInfo/' + id,
    method: 'get'
  })
}

// ����层
export function addFloorInfo(data) {
  return request({
    url: '/wcs-base/FloorInfo',
    method: 'post',
    data: data
  })
}

// �޸�层
export function updateFloorInfo(data) {
  return request({
    url: '/wcs-base/FloorInfo',
    method: 'put',
    data: data
  })
}

// ɾ��层
export function delFloorInfo(id) {
  return request({
    url: '/wcs-base/FloorInfo/' + id,
    method: 'delete'
  })
}
