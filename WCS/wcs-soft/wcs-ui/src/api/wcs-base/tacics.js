import request from '@/utils/request'

// ��ѯ策略配置�б�
export function listTacics(query) {
  return request({
    url: '/wcs-base/tacics/list',
    method: 'get',
    params: query
  })
}

// ��ѯ策略配置��ϸ
export function getTacics(id) {
  return request({
    url: '/wcs-base/tacics/' + id,
    method: 'get'
  })
}

// ����策略配置
export function addTacics(data) {
  return request({
    url: '/wcs-base/tacics',
    method: 'post',
    data: data
  })
}

// �޸�策略配置
export function updateTacics(data) {
  return request({
    url: '/wcs-base/tacics',
    method: 'put',
    data: data
  })
}

// ɾ��策略配置
export function delTacics(id) {
  return request({
    url: '/wcs-base/tacics/' + id,
    method: 'delete'
  })
}
