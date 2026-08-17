import request from '@/utils/request'

// ��ѯ属性�б�
export function listValue(query) {
  return request({
    url: '/wcs-base/value/list',
    method: 'get',
    params: query
  })
}

// ��ѯ属性��ϸ
export function getValue(id) {
  return request({
    url: '/wcs-base/value/' + id,
    method: 'get'
  })
}

// ����属性
export function addValue(data) {
  return request({
    url: '/wcs-base/value',
    method: 'post',
    data: data
  })
}

// �޸�属性
export function updateValue(data) {
  return request({
    url: '/wcs-base/value',
    method: 'put',
    data: data
  })
}

// ɾ��属性
export function delValue(id) {
  return request({
    url: '/wcs-base/value/' + id,
    method: 'delete'
  })
}
