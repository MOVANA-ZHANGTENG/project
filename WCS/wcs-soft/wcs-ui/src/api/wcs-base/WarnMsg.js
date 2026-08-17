import request from '@/utils/request'

// ��ѯ报警代码�б�
export function listWarnMsg(query) {
  return request({
    url: '/wcs-base/WarnMsg/list',
    method: 'get',
    params: query
  })
}

// ��ѯ报警代码��ϸ
export function getWarnMsg(id) {
  return request({
    url: '/wcs-base/WarnMsg/' + id,
    method: 'get'
  })
}

// ����报警代码
export function addWarnMsg(data) {
  return request({
    url: '/wcs-base/WarnMsg',
    method: 'post',
    data: data
  })
}

// �޸�报警代码
export function updateWarnMsg(data) {
  return request({
    url: '/wcs-base/WarnMsg',
    method: 'put',
    data: data
  })
}

// ɾ��报警代码
export function delWarnMsg(id) {
  return request({
    url: '/wcs-base/WarnMsg/' + id,
    method: 'delete'
  })
}
