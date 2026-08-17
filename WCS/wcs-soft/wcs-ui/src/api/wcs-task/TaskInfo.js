import request from '@/utils/request'

// 查询任务列表
export function listTaskInfo(query) {
  return request({
    url: '/wcs-task/TaskInfo/list',
    method: 'get',
    params: query
  })
}

// 查询任务详细
export function getTaskInfo(id) {
  return request({
    url: '/wcs-task/TaskInfo/' + id,
    method: 'get'
  })
}

// 新增任务
export function addTaskInfo(data) {
  return request({
    url: '/wcs-task/TaskInfo',
    method: 'post',
    data: data
  })
}

// 修改任务
export function updateTaskInfo(data) {
  return request({
    url: '/wcs-task/TaskInfo',
    method: 'put',
    data: data
  })
}

// 删除任务
export function delTaskInfo(id) {
  return request({
    url: '/wcs-task/TaskInfo/' + id,
    method: 'delete'
  })
}

// 获取7天任务类型统计
export function getSevenDaysStatistics(wareCode) {
  return request({
    url: '/wcs-task/TaskInfo/statistics/sevenDays',
    method: 'get',
    params: { wareCode }
  })
}

// 获取任务基础统计（总数、今日新增等）
export function getBasicStatistics(wareCode) {
  return request({
    url: '/wcs-task/TaskInfo/statistics/basic',
    method: 'get',
    params: { wareCode }
  })
}
