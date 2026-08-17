import request from '@/utils/request'

// 查询目的地申请列表
export function listApply(query) {
  return request({
    url: '/system/apply/list',
    method: 'get',
    params: query
  })
}

// 查询目的地申请详细
export function getApply(boxTargetApplyId) {
  return request({
    url: '/system/apply/' + boxTargetApplyId,
    method: 'get'
  })
}

// 新增目的地申请
export function addApply(data) {
  return request({
    url: '/system/apply',
    method: 'post',
    data: data
  })
}

// 修改目的地申请
export function updateApply(data) {
  return request({
    url: '/system/apply',
    method: 'put',
    data: data
  })
}

// 删除目的地申请
export function delApply(boxTargetApplyId) {
  return request({
    url: '/system/apply/' + boxTargetApplyId,
    method: 'delete'
  })
}
