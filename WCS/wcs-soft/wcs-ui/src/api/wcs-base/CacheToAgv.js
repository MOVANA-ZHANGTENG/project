import request from '@/utils/request'

// ��ѯAGV缓存�б�
export function listCacheToAgv(query) {
  return request({
    url: '/wcs-base/CacheToAgv/list',
    method: 'get',
    params: query
  })
}

// ��ѯAGV缓存��ϸ
export function getCacheToAgv(id) {
  return request({
    url: '/wcs-base/CacheToAgv/' + id,
    method: 'get'
  })
}

// ����AGV缓存
export function addCacheToAgv(data) {
  return request({
    url: '/wcs-base/CacheToAgv',
    method: 'post',
    data: data
  })
}

// �޸�AGV缓存
export function updateCacheToAgv(data) {
  return request({
    url: '/wcs-base/CacheToAgv',
    method: 'put',
    data: data
  })
}

// ɾ��AGV缓存
export function delCacheToAgv(id) {
  return request({
    url: '/wcs-base/CacheToAgv/' + id,
    method: 'delete'
  })
}
