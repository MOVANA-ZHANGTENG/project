import request from '@/utils/request'

// ��ѯ用于记录任务执行的每一步历史�б�
export function listHistoryTaskList(query) {
  return request({
    url: '/wcs-task/historyTaskList/list',
    method: 'get',
    params: query
  })
}

// ��ѯ用于记录任务执行的每一步历史��ϸ
export function getHistoryTaskList(id) {
  return request({
    url: '/wcs-task/historyTaskList/' + id,
    method: 'get'
  })
}

// ����用于记录任务执行的每一步历史
export function addHistoryTaskList(data) {
  return request({
    url: '/wcs-task/historyTaskList',
    method: 'post',
    data: data
  })
}

// �޸�用于记录任务执行的每一步历史
export function updateHistoryTaskList(data) {
  return request({
    url: '/wcs-task/historyTaskList',
    method: 'put',
    data: data
  })
}

// ɾ��用于记录任务执行的每一步历史
export function delHistoryTaskList(id) {
  return request({
    url: '/wcs-task/historyTaskList/' + id,
    method: 'delete'
  })
}
