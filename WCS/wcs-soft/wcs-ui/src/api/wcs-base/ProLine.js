import request from '@/utils/request'

// ��ѯ产线�б�
export function listProLine(query) {
  return request({
    url: '/wcs-base/ProLine/list',
    method: 'get',
    params: query
  })
}

// ��ѯ产线��ϸ
export function getProLine(id) {
  return request({
    url: '/wcs-base/ProLine/' + id,
    method: 'get'
  })
}

// ����产线
export function addProLine(data) {
  return request({
    url: '/wcs-base/ProLine',
    method: 'post',
    data: data
  })
}

// �޸�产线
export function updateProLine(data) {
  return request({
    url: '/wcs-base/ProLine',
    method: 'put',
    data: data
  })
}

// ɾ��产线
export function delProLine(id) {
  return request({
    url: '/wcs-base/ProLine/' + id,
    method: 'delete'
  })
}
