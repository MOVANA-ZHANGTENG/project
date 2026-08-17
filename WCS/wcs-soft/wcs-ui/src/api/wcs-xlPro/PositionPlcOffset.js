import request from '@/utils/request'

// ��ѯ站台通信偏移量�б�
export function listPositionPlcOffset(query) {
  return request({
    url: '/wcs-xlPro/PositionPlcOffset/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台通信偏移量��ϸ
export function getPositionPlcOffset(id) {
  return request({
    url: '/wcs-xlPro/PositionPlcOffset/' + id,
    method: 'get'
  })
}

// ����站台通信偏移量
export function addPositionPlcOffset(data) {
  return request({
    url: '/wcs-xlPro/PositionPlcOffset',
    method: 'post',
    data: data
  })
}

// �޸�站台通信偏移量
export function updatePositionPlcOffset(data) {
  return request({
    url: '/wcs-xlPro/PositionPlcOffset',
    method: 'put',
    data: data
  })
}

// ɾ��站台通信偏移量
export function delPositionPlcOffset(id) {
  return request({
    url: '/wcs-xlPro/PositionPlcOffset/' + id,
    method: 'delete'
  })
}

export function getPositionExtendIdAll() {
  return request({
    url: '/wcs-xlPro/PositionPlcOffset/positionExtendIdAll',
    method: 'get'
  })
}

