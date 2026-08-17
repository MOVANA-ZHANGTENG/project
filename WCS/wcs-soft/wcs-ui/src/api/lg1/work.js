import request from '@/utils/request'

// ��ѯ工序信息�б�
export function listWork(query) {
  return request({
    url: '/lg1/work/list',
    method: 'get',
    params: query
  })
}

// ��ѯ工序信息��ϸ
export function getWork(id) {
  return request({
    url: '/lg1/work/' + id,
    method: 'get'
  })
}

// ����工序信息
export function addWork(data) {
  return request({
    url: '/lg1/work',
    method: 'post',
    data: data
  })
}

// �޸�工序信息
export function updateWork(data) {
  return request({
    url: '/lg1/work',
    method: 'put',
    data: data
  })
}

// ɾ��工序信息
export function delWork(id) {
  return request({
    url: '/lg1/work/' + id,
    method: 'delete'
  })
}
