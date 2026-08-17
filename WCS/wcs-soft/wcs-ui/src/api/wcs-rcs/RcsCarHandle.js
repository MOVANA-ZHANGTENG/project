import request from '@/utils/request'

// ��ѯRcsCarHandle�б�
export function listRcsCarHandle(query) {
  return request({
    url: '/wcs-rcs/RcsCarHandle/list',
    method: 'get',
    params: query
  })
}

// ��ѯRcsCarHandle��ϸ
export function getRcsCarHandle(id) {
  return request({
    url: '/wcs-rcs/RcsCarHandle/' + id,
    method: 'get'
  })
}

// ����RcsCarHandle
export function addRcsCarHandle(data) {
  return request({
    url: '/wcs-rcs/RcsCarHandle',
    method: 'post',
    data: data
  })
}

// �޸�RcsCarHandle
export function updateRcsCarHandle(data) {
  return request({
    url: '/wcs-rcs/RcsCarHandle',
    method: 'put',
    data: data
  })
}

// ɾ��RcsCarHandle
export function delRcsCarHandle(id) {
  return request({
    url: '/wcs-rcs/RcsCarHandle/' + id,
    method: 'delete'
  })
}
