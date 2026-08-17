import request from '@/utils/request'

// ��ѯ交管日志�б�
export function listAgvZoneRecord(query) {
  return request({
    url: '/wcs-task/AgvZoneRecord/list',
    method: 'get',
    params: query
  })
}

// ��ѯ交管日志��ϸ
export function getAgvZoneRecord(id) {
  return request({
    url: '/wcs-task/AgvZoneRecord/' + id,
    method: 'get'
  })
}

// ����交管日志
export function addAgvZoneRecord(data) {
  return request({
    url: '/wcs-task/AgvZoneRecord',
    method: 'post',
    data: data
  })
}

// �޸�交管日志
export function updateAgvZoneRecord(data) {
  return request({
    url: '/wcs-task/AgvZoneRecord',
    method: 'put',
    data: data
  })
}

// ɾ��交管日志
export function delAgvZoneRecord(id) {
  return request({
    url: '/wcs-task/AgvZoneRecord/' + id,
    method: 'delete'
  })
}
