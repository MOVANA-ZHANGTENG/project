import request from '@/utils/request'

// ��ѯ工序基础信息�б�
export function listWork(query) {
  return request({
    url: '/wcs-base/work/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工序基础信息��ϸ
export function getWork(id) {
  return request({
    url: '/wcs-base/work/' + id,
    method: 'get'
  })
}

// ����工序基础信息
export function addWork(data) {
  return request({
    url: '/wcs-base/work',
    method: 'post',
    data: data
  })
}

// �޸�工序基础信息
export function updateWork(data) {
  return request({
    url: '/wcs-base/work',
    method: 'put',
    data: data
  })
}

// ɾ��工序基础信息
export function delWork(id) {
  return request({
    url: '/wcs-base/work/' + id,
    method: 'delete'
  })
}
