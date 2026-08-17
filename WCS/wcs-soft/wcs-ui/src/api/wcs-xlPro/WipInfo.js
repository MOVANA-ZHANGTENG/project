import request from '@/utils/request'

// ��ѯ在制品�б�
export function listWipInfo(query) {
  return request({
    url: '/wcs-xlPro/WipInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ在制品��ϸ
export function getWipInfo(id) {
  return request({
    url: '/wcs-xlPro/WipInfo/' + id,
    method: 'get'
  })
}

// ����在制品
export function addWipInfo(data) {
  return request({
    url: '/wcs-xlPro/WipInfo',
    method: 'post',
    data: data
  })
}

// �޸�在制品
export function updateWipInfo(data) {
  return request({
    url: '/wcs-xlPro/WipInfo',
    method: 'put',
    data: data
  })
}

// ɾ��在制品
export function delWipInfo(id) {
  return request({
    url: '/wcs-xlPro/WipInfo/' + id,
    method: 'delete'
  })
}
