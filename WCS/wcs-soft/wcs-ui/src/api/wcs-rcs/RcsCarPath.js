import request from '@/utils/request'

// 查询车路径列表
export function listRcsCarPath(query) {
  return request({
    url: '/wcs-rcs/RcsCarPath/list',
    method: 'get',
    params: query
  })
}

// 查询车路径详细
export function getRcsCarPath(id) {
  return request({
    url: '/wcs-rcs/RcsCarPath/' + id,
    method: 'get'
  })
}

// 新增车路径
export function addRcsCarPath(data) {
  return request({
    url: '/wcs-rcs/RcsCarPath',
    method: 'post',
    data: data
  })
}

// 修改车路径
export function updateRcsCarPath(data) {
  return request({
    url: '/wcs-rcs/RcsCarPath',
    method: 'put',
    data: data
  })
}

// 删除车路径
export function delRcsCarPath(id) {
  return request({
    url: '/wcs-rcs/RcsCarPath/' + id,
    method: 'delete'
  })
}
