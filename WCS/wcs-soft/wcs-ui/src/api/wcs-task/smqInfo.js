import request from '@/utils/request'

// ��ѯ扫码器�б�
export function listSmqInfo(query) {
  return request({
    url: '/wcs-task/smqInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ扫码器��ϸ
export function getSmqInfo(id) {
  return request({
    url: '/wcs-task/smqInfo/' + id,
    method: 'get'
  })
}

// ����扫码器
export function addSmqInfo(data) {
  return request({
    url: '/wcs-task/smqInfo',
    method: 'post',
    data: data
  })
}

// �޸�扫码器
export function updateSmqInfo(data) {
  return request({
    url: '/wcs-task/smqInfo',
    method: 'put',
    data: data
  })
}

// ɾ��扫码器
export function delSmqInfo(id) {
  return request({
    url: '/wcs-task/smqInfo/' + id,
    method: 'delete'
  })
}
