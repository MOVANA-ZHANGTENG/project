import request from '@/utils/request'

// ��ѯ小车任务详情�б�
export function listRcsCarJob(query) {
  return request({
    url: '/wcs-rcs/RcsCarJob/list',
    method: 'get',
    params: query
  })
}

// ��ѯ小车任务详情��ϸ
export function getRcsCarJob(id) {
  return request({
    url: '/wcs-rcs/RcsCarJob/' + id,
    method: 'get'
  })
}

// ����小车任务详情
export function addRcsCarJob(data) {
  return request({
    url: '/wcs-rcs/RcsCarJob',
    method: 'post',
    data: data
  })
}

// �޸�小车任务详情
export function updateRcsCarJob(data) {
  return request({
    url: '/wcs-rcs/RcsCarJob',
    method: 'put',
    data: data
  })
}

// ɾ��小车任务详情
export function delRcsCarJob(id) {
  return request({
    url: '/wcs-rcs/RcsCarJob/' + id,
    method: 'delete'
  })
}
