import request from '@/utils/request'

// ��ѯ提升机�б�
export function listRcsTsj(query) {
  return request({
    url: '/wcs-rcs/RcsTsj/list',
    method: 'get',
    params: query
  })
}

// ��ѯ提升机��ϸ
export function getRcsTsj(id) {
  return request({
    url: '/wcs-rcs/RcsTsj/' + id,
    method: 'get'
  })
}

// ����提升机
export function addRcsTsj(data) {
  return request({
    url: '/wcs-rcs/RcsTsj',
    method: 'post',
    data: data
  })
}

// �޸�提升机
export function updateRcsTsj(data) {
  return request({
    url: '/wcs-rcs/RcsTsj',
    method: 'put',
    data: data
  })
}

// ɾ��提升机
export function delRcsTsj(id) {
  return request({
    url: '/wcs-rcs/RcsTsj/' + id,
    method: 'delete'
  })
}
