import request from '@/utils/request'

// ��ѯ四向车/AGV�б�
export function listRcsCarInfo(query) {
  return request({
    url: '/wcs-rcs/RcsCarInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ四向车/AGV��ϸ
export function getRcsCarInfo(id) {
  return request({
    url: '/wcs-rcs/RcsCarInfo/' + id,
    method: 'get'
  })
}

// ����四向车/AGV
export function addRcsCarInfo(data) {
  return request({
    url: '/wcs-rcs/RcsCarInfo',
    method: 'post',
    data: data
  })
}

// �޸�四向车/AGV
export function updateRcsCarInfo(data) {
  return request({
    url: '/wcs-rcs/RcsCarInfo',
    method: 'put',
    data: data
  })
}

// ɾ��四向车/AGV
export function delRcsCarInfo(id) {
  return request({
    url: '/wcs-rcs/RcsCarInfo/' + id,
    method: 'delete'
  })
}
