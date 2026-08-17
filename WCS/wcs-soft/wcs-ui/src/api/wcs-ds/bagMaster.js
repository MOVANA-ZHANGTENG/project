import request from '@/utils/request'

// ��ѯ料袋主表�б�
export function listBagMaster(query) {
  return request({
    url: '/wcs-ds/bagMaster/list',
    method: 'get',
    params: query
  })
}

// ��ѯ料袋主表��ϸ
export function getBagMaster(id) {
  return request({
    url: '/wcs-ds/bagMaster/' + id,
    method: 'get'
  })
}

// ����料袋主表
export function addBagMaster(data) {
  return request({
    url: '/wcs-ds/bagMaster',
    method: 'post',
    data: data
  })
}

// �޸�料袋主表
export function updateBagMaster(data) {
  return request({
    url: '/wcs-ds/bagMaster',
    method: 'put',
    data: data
  })
}

// ɾ��料袋主表
export function delBagMaster(id) {
  return request({
    url: '/wcs-ds/bagMaster/' + id,
    method: 'delete'
  })
}
