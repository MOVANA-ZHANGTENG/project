import request from '@/utils/request'

// ��ѯ扫描失败率�б�
export function listSaoMaSuccess(query) {
  return request({
    url: '/wcs-task/SaoMaSuccess/list',
    method: 'get',
    params: query
  })
}

export function taskAllNumber(data) {
  return request({
    url: '/wcs-task/SaoMaSuccess/allNumber',
    method: 'get',
    params: data
  })
}

// ��ѯ扫描失败率��ϸ
export function getSaoMaSuccess(id) {
  return request({
    url: '/wcs-task/SaoMaSuccess/' + id,
    method: 'get'
  })
}

// ����扫描失败率
export function addSaoMaSuccess(data) {
  return request({
    url: '/wcs-task/SaoMaSuccess',
    method: 'post',
    data: data
  })
}

// �޸�扫描失败率
export function updateSaoMaSuccess(data) {
  return request({
    url: '/wcs-task/SaoMaSuccess',
    method: 'put',
    data: data
  })
}

// ɾ��扫描失败率
export function delSaoMaSuccess(id) {
  return request({
    url: '/wcs-task/SaoMaSuccess/' + id,
    method: 'delete'
  })
}
