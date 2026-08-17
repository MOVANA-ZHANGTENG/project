import request from '@/utils/request'

// ��ѯ步骤执行器�б�
export function listJobHandle(query) {
  return request({
    url: '/wcs-task/jobHandle/list',
    method: 'get',
    params: query
  })
}

// ��ѯ步骤执行器��ϸ
export function getJobHandle(id) {
  return request({
    url: '/wcs-task/jobHandle/' + id,
    method: 'get'
  })
}

// ����步骤执行器
export function addJobHandle(data) {
  return request({
    url: '/wcs-task/jobHandle',
    method: 'post',
    data: data
  })
}

// �޸�步骤执行器
export function updateJobHandle(data) {
  return request({
    url: '/wcs-task/jobHandle',
    method: 'put',
    data: data
  })
}

// ɾ��步骤执行器
export function delJobHandle(id) {
  return request({
    url: '/wcs-task/jobHandle/' + id,
    method: 'delete'
  })
}
