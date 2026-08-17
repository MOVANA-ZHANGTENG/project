import request from '@/utils/request'

// ��ѯRCS库位信息�б�
export function listRcsMapCellInfo(query) {
  return request({
    url: '/wcs-rcs/RcsMapCellInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯRCS库位信息��ϸ
export function getRcsMapCellInfo(id) {
  return request({
    url: '/wcs-rcs/RcsMapCellInfo/' + id,
    method: 'get'
  })
}

// ����RCS库位信息
export function addRcsMapCellInfo(data) {
  return request({
    url: '/wcs-rcs/RcsMapCellInfo',
    method: 'post',
    data: data
  })
}

// �޸�RCS库位信息
export function updateRcsMapCellInfo(data) {
  return request({
    url: '/wcs-rcs/RcsMapCellInfo',
    method: 'put',
    data: data
  })
}

// ɾ��RCS库位信息
export function delRcsMapCellInfo(id) {
  return request({
    url: '/wcs-rcs/RcsMapCellInfo/' + id,
    method: 'delete'
  })
}
