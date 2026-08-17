import request from '@/utils/request'

// ��ѯ夹具类型�б�
export function listFixtureType(query) {
  return request({
    url: '/wcs-xlPro/FixtureType/list',
    method: 'get',
    params: query
  })
}

// ��ѯ夹具类型��ϸ
export function getFixtureType(id) {
  return request({
    url: '/wcs-xlPro/FixtureType/' + id,
    method: 'get'
  })
}

// ����夹具类型
export function addFixtureType(data) {
  return request({
    url: '/wcs-xlPro/FixtureType',
    method: 'post',
    data: data
  })
}

// �޸�夹具类型
export function updateFixtureType(data) {
  return request({
    url: '/wcs-xlPro/FixtureType',
    method: 'put',
    data: data
  })
}

// ɾ��夹具类型
export function delFixtureType(id) {
  return request({
    url: '/wcs-xlPro/FixtureType/' + id,
    method: 'delete'
  })
}
