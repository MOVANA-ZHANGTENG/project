import request from '@/utils/request'

// ��ѯ物料类型�б�
export function listItemType(query) {
  return request({
    url: '/wcs-base/ItemType/list',
    method: 'get',
    params: query
  })
}

// ��ѯ物料类型��ϸ
export function getItemType(id) {
  return request({
    url: '/wcs-base/ItemType/' + id,
    method: 'get'
  })
}

// ����物料类型
export function addItemType(data) {
  return request({
    url: '/wcs-base/ItemType',
    method: 'post',
    data: data
  })
}

// �޸�物料类型
export function updateItemType(data) {
  return request({
    url: '/wcs-base/ItemType',
    method: 'put',
    data: data
  })
}

// ɾ��物料类型
export function delItemType(id) {
  return request({
    url: '/wcs-base/ItemType/' + id,
    method: 'delete'
  })
}
