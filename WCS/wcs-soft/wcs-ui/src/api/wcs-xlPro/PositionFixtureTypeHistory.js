import request from '@/utils/request'

// ��ѯ站台夹具类型历史�б�
export function listPositionFixtureTypeHistory(query) {
  return request({
    url: '/wcs-xlPro/PositionFixtureTypeHistory/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台夹具类型历史��ϸ
export function getPositionFixtureTypeHistory(id) {
  return request({
    url: '/wcs-xlPro/PositionFixtureTypeHistory/' + id,
    method: 'get'
  })
}

// ����站台夹具类型历史
export function addPositionFixtureTypeHistory(data) {
  return request({
    url: '/wcs-xlPro/PositionFixtureTypeHistory',
    method: 'post',
    data: data
  })
}

// �޸�站台夹具类型历史
export function updatePositionFixtureTypeHistory(data) {
  return request({
    url: '/wcs-xlPro/PositionFixtureTypeHistory',
    method: 'put',
    data: data
  })
}

// ɾ��站台夹具类型历史
export function delPositionFixtureTypeHistory(id) {
  return request({
    url: '/wcs-xlPro/PositionFixtureTypeHistory/' + id,
    method: 'delete'
  })
}
