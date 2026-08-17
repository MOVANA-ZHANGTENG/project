import request from '@/utils/request'

// ��ѯ1厂基础位置�б�
export function listCellInfoLg(query) {
  return request({
    url: '/wcs-lg1/cellInfoLg/list',
    method: 'get',
    params: query
  })
}

// ��ѯ1厂基础位置��ϸ
export function getCellInfoLg(id) {
  return request({
    url: '/wcs-lg1/cellInfoLg/' + id,
    method: 'get'
  })
}

// ����1厂基础位置
export function addCellInfoLg(data) {
  return request({
    url: '/wcs-lg1/cellInfoLg',
    method: 'post',
    data: data
  })
}

// �޸�1厂基础位置
export function updateCellInfoLg(data) {
  return request({
    url: '/wcs-lg1/cellInfoLg',
    method: 'put',
    data: data
  })
}

// ɾ��1厂基础位置
export function delCellInfoLg(id) {
  return request({
    url: '/wcs-lg1/cellInfoLg/' + id,
    method: 'delete'
  })
}

// ɾ��1厂基础位置
export function getByBatteryCode() {
  return request({
    url: '/wcs-lg1/cellInfoLg/batteryCode',
    method: 'get'
  })
}
