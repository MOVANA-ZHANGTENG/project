import request from '@/utils/request'

// ��ѯAGV交管�б�
export function listAgvZone(query) {
  return request({
    url: '/wcs-task/AgvZone/list',
    method: 'get',
    params: query
  })
}

// ��ѯAGV交管��ϸ
export function getAgvZone(id) {
  return request({
    url: '/wcs-task/AgvZone/' + id,
    method: 'get'
  })
}

// ����AGV交管
export function addAgvZone(data) {
  return request({
    url: '/wcs-task/AgvZone',
    method: 'post',
    data: data
  })
}

// �޸�AGV交管
export function updateAgvZone(data) {
  return request({
    url: '/wcs-task/AgvZone',
    method: 'put',
    data: data
  })
}

// ɾ��AGV交管
export function delAgvZone(id) {
  return request({
    url: '/wcs-task/AgvZone/' + id,
    method: 'delete'
  })
}
