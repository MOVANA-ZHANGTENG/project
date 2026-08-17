import request from '@/utils/request'

// ��ѯ任务定义�б�
export function listTaskDefine(query) {
  return request({
    url: '/wcs-base/TaskDefine/list',
    method: 'get',
    params: query
  })
}

// ��ѯ任务定义��ϸ
export function getTaskDefine(id) {
  return request({
    url: '/wcs-base/TaskDefine/' + id,
    method: 'get'
  })
}

// ����任务定义
export function addTaskDefine(data) {
  return request({
    url: '/wcs-base/TaskDefine',
    method: 'post',
    data: data
  })
}

// �޸�任务定义
export function updateTaskDefine(data) {
  return request({
    url: '/wcs-base/TaskDefine',
    method: 'put',
    data: data
  })
}

// ɾ��任务定义
export function delTaskDefine(id) {
  return request({
    url: '/wcs-base/TaskDefine/' + id,
    method: 'delete'
  })
}

// 查询任务定义详细（包含所有处理器列表）
export function getTaskDefineDetail(id) {
  return request({
    url: '/wcs-base/TaskDefine/detail/' + id,
    method: 'get'
  })
}

// 更新任务定义的连线关系（更新lastId）- 旧接口
export function updateTaskDefineLink(data) {
  return request({
    url: '/wcs-base/TaskDefine/updateLink',
    method: 'put',
    data: data
  })
}

// 更新任务定义连线（支持判断步骤）- 新接口
export function updateTaskDefineLink2(data) {
  return request({
    url: '/wcs-base/TaskDefine/link',
    method: 'put',
    data: data
  })
}

// 删除任务定义连线
export function deleteTaskDefineLink(stepId) {
  return request({
    url: '/wcs-base/TaskDefine/link/' + stepId,
    method: 'delete'
  })
}

// 更新步骤位置
export function updateStepPosition(data) {
  return request({
    url: '/wcs-base/TaskDefine/position',
    method: 'put',
    data: data
  })
}