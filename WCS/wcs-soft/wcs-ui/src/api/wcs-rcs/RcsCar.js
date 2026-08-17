import request from '@/utils/request'

// ��ѯ小车信息�б�
export function listRcsCar(query) {
  return request({
    url: '/wcs-rcs/RcsCar/list',
    method: 'get',
    params: query
  })
}

// ��ѯ小车信息��ϸ
export function getRcsCar(id) {
  return request({
    url: '/wcs-rcs/RcsCar/' + id,
    method: 'get'
  })
}

// ����小车信息
export function addRcsCar(data) {
  return request({
    url: '/wcs-rcs/RcsCar',
    method: 'post',
    data: data
  })
}

// �޸�小车信息
export function updateRcsCar(data) {
  return request({
    url: '/wcs-rcs/RcsCar',
    method: 'put',
    data: data
  })
}

// ɾ��小车信息
export function delRcsCar(id) {
  return request({
    url: '/wcs-rcs/RcsCar/' + id,
    method: 'delete'
  })
}
