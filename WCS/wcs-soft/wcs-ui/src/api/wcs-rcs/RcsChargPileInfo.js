import request from '@/utils/request'

// ��ѯ充电桩�б�
export function listRcsChargPileInfo(query) {
  return request({
    url: '/wcs-rcs/RcsChargPileInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ充电桩��ϸ
export function getRcsChargPileInfo(id) {
  return request({
    url: '/wcs-rcs/RcsChargPileInfo/' + id,
    method: 'get'
  })
}

// ����充电桩
export function addRcsChargPileInfo(data) {
  return request({
    url: '/wcs-rcs/RcsChargPileInfo',
    method: 'post',
    data: data
  })
}

// �޸�充电桩
export function updateRcsChargPileInfo(data) {
  return request({
    url: '/wcs-rcs/RcsChargPileInfo',
    method: 'put',
    data: data
  })
}

// ɾ��充电桩
export function delRcsChargPileInfo(id) {
  return request({
    url: '/wcs-rcs/RcsChargPileInfo/' + id,
    method: 'delete'
  })
}
