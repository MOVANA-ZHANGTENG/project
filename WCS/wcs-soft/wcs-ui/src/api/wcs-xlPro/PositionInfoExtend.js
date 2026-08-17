import request from '@/utils/request'

// ��ѯ站台扩展�б�
export function listPositionInfoExtend(query) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台扩展��ϸ
export function getPositionInfoExtend(id) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/' + id,
    method: 'get'
  })
}

export function getModelId(code) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/getModelId',
    method: 'get',
    params:  { code: code }
  })
}



// ����站台扩展
export function addPositionInfoExtend(data) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend',
    method: 'post',
    data: data
  })
}

export function addPositionExtend(data) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/addPositionExtend',
    method: 'post',
    data: data
  })
}



// �޸�站台扩展
export function updatePositionInfoExtend(data) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend',
    method: 'put',
    data: data
  })
}

// �޸�站台扩展
export function updatePositionInfoExtendModel(data) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/updatePositionInfoExtend',
    method: 'put',
    data: data
  })
}

// ɾ��站台扩展
export function delPositionInfoExtend(id) {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/' + id,
    method: 'delete'
  })
}


// ��ѯ站台扩展��ϸ
export function findPositionAll() {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/getPositionAll',
    method: 'get'
  })
}


export function findLineAll() {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/getLineAll',
    method: 'get'
  })
}

export function findFixtureTypeAll() {
  return request({
    url: '/wcs-xlPro/PositionInfoExtend/getFixtureTypeAll',
    method: 'get'
  })
}



