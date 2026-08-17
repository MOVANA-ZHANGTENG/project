import request from '@/utils/request'

// ��ѯ工序站台�б�
export function listProProcessPosition(query) {
  return request({
    url: '/wcs-base/ProProcessPosition/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工序站台��ϸ
export function getProProcessPosition(id) {
  return request({
    url: '/wcs-base/ProProcessPosition/' + id,
    method: 'get'
  })
}

// ����工序站台
export function addProProcessPosition(data) {
  return request({
    url: '/wcs-base/ProProcessPosition',
    method: 'post',
    data: data
  })
}

// �޸�工序站台
export function updateProProcessPosition(data) {
  return request({
    url: '/wcs-base/ProProcessPosition',
    method: 'put',
    data: data
  })
}

// ɾ��工序站台
export function delProProcessPosition(id) {
  return request({
    url: '/wcs-base/ProProcessPosition/' + id,
    method: 'delete'
  })
}
