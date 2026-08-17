import request from '@/utils/request'

// ��ѯ任务执行器�б�
export function listTaskHandle(query) {
  return request({
    url: '/wcs-base/TaskHandle/list',
    method: 'get',
    params: query
  })
}

// ��ѯ任务执行器��ϸ
export function getTaskHandle(id) {
  return request({
    url: '/wcs-base/TaskHandle/' + id,
    method: 'get'
  })
}

// ����任务执行器
export function addTaskHandle(data) {
  return request({
    url: '/wcs-base/TaskHandle',
    method: 'post',
    data: data
  })
}

// �޸�任务执行器
export function updateTaskHandle(data) {
  return request({
    url: '/wcs-base/TaskHandle',
    method: 'put',
    data: data
  })
}

// ɾ��任务执行器
export function delTaskHandle(id) {
  return request({
    url: '/wcs-base/TaskHandle/' + id,
    method: 'delete'
  })
}
