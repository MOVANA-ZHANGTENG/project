import request from '@/utils/request'

// ��ѯ执行步骤�б�
export function listJob(query) {
  return request({
    url: '/wcs-task/job/list',
    method: 'get',
    params: query
  })
}

// ��ѯ执行步骤��ϸ
export function getJob(id) {
  return request({
    url: '/wcs-task/job/' + id,
    method: 'get'
  })
}

// ����执行步骤
export function addJob(data) {
  return request({
    url: '/wcs-task/job',
    method: 'post',
    data: data
  })
}

// �޸�执行步骤
export function updateJob(data) {
  return request({
    url: '/wcs-task/job',
    method: 'put',
    data: data
  })
}

// ɾ��执行步骤
export function delJob(id) {
  return request({
    url: '/wcs-task/job/' + id,
    method: 'delete'
  })
}
