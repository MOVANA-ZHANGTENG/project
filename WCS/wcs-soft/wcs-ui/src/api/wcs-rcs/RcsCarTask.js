import request from '@/utils/request'

// ��ѯ小车任务主体�б�
export function listRcsCarTask(query) {
  return request({
    url: '/wcs-rcs/RcsCarTask/list',
    method: 'get',
    params: query
  })
}

// ��ѯ小车任务主体��ϸ
export function getRcsCarTask(id) {
  return request({
    url: '/wcs-rcs/RcsCarTask/' + id,
    method: 'get'
  })
}

// ����小车任务主体
export function addRcsCarTask(data) {
  return request({
    url: '/wcs-rcs/RcsCarTask',
    method: 'post',
    data: data
  })
}

// �޸�小车任务主体
export function updateRcsCarTask(data) {
  return request({
    url: '/wcs-rcs/RcsCarTask',
    method: 'put',
    data: data
  })
}

// ɾ��小车任务主体
export function delRcsCarTask(id) {
  return request({
    url: '/wcs-rcs/RcsCarTask/' + id,
    method: 'delete'
  })
}
