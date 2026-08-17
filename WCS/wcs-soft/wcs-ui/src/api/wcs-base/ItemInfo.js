import request from '@/utils/request'

// ��ѯ物料�б�
export function listItemInfo(query) {
  return request({
    url: '/wcs-base/ItemInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ物料��ϸ
export function getItemInfo(id) {
  return request({
    url: '/wcs-base/ItemInfo/' + id,
    method: 'get'
  })
}

// ����物料
export function addItemInfo(data) {
  return request({
    url: '/wcs-base/ItemInfo',
    method: 'post',
    data: data
  })
}

// �޸�物料
export function updateItemInfo(data) {
  return request({
    url: '/wcs-base/ItemInfo',
    method: 'put',
    data: data
  })
}

// ɾ��物料
export function delItemInfo(id) {
  return request({
    url: '/wcs-base/ItemInfo/' + id,
    method: 'delete'
  })
}
