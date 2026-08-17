import request from '@/utils/request'

// ��ѯ任务类型�б�
export function listTaskType(query) {
  return request({
    url: '/wcs-base/taskType/list',
    method: 'get',
    params: query
  })
}

// ��ѯ任务类型��ϸ
export function getTaskType(id) {
  return request({
    url: '/wcs-base/taskType/' + id,
    method: 'get'
  })
}

// ����任务类型
export function addTaskType(data) {
  return request({
    url: '/wcs-base/taskType',
    method: 'post',
    data: data
  })
}

// �޸�任务类型
export function updateTaskType(data) {
  return request({
    url: '/wcs-base/taskType',
    method: 'put',
    data: data
  })
}

// ɾ��任务类型
export function delTaskType(id) {
  return request({
    url: '/wcs-base/taskType/' + id,
    method: 'delete'
  })
}
