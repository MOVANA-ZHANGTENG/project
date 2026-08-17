import request from '@/utils/request'

// ��ѯ位置基础信息�б�
export function listCellInfoLG(query) {
  return request({
    url: '/wcs-base/cellInfoLG/list',
    method: 'get',
    params: query
  })
}

// ��ѯ位置基础信息��ϸ
export function getCellInfoLG(id) {
  return request({
    url: '/wcs-base/cellInfoLG/' + id,
    method: 'get'
  })
}

// ����位置基础信息
export function addCellInfoLG(data) {
  return request({
    url: '/wcs-base/cellInfoLG',
    method: 'post',
    data: data
  })
}

// �޸�位置基础信息
export function updateCellInfoLG(data) {
  return request({
    url: '/wcs-base/cellInfoLG',
    method: 'put',
    data: data
  })
}

// ɾ��位置基础信息
export function delCellInfoLG(id) {
  return request({
    url: '/wcs-base/cellInfoLG/' + id,
    method: 'delete'
  })
}
