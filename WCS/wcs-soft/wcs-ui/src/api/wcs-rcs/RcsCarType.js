import request from '@/utils/request'

// ��ѯ车型号�б�
export function listRcsCarType(query) {
  return request({
    url: '/wcs-rcs/RcsCarType/list',
    method: 'get',
    params: query
  })
}

// ��ѯ车型号��ϸ
export function getRcsCarType(id) {
  return request({
    url: '/wcs-rcs/RcsCarType/' + id,
    method: 'get'
  })
}

// ����车型号
export function addRcsCarType(data) {
  return request({
    url: '/wcs-rcs/RcsCarType',
    method: 'post',
    data: data
  })
}

// �޸�车型号
export function updateRcsCarType(data) {
  return request({
    url: '/wcs-rcs/RcsCarType',
    method: 'put',
    data: data
  })
}

// ɾ��车型号
export function delRcsCarType(id) {
  return request({
    url: '/wcs-rcs/RcsCarType/' + id,
    method: 'delete'
  })
}
