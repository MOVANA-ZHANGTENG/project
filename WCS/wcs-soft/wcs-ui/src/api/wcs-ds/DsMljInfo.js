import request from '@/utils/request'

// ��ѯ密炼机�б�
export function listDsMljInfo(query) {
  return request({
    url: '/wcs-ds/DsMljInfo/list',
    method: 'get',
    params: query
  })
}

// ��ѯ密炼机��ϸ
export function getDsMljInfo(id) {
  return request({
    url: '/wcs-ds/DsMljInfo/' + id,
    method: 'get'
  })
}

// ����密炼机
export function addDsMljInfo(data) {
  return request({
    url: '/wcs-ds/DsMljInfo',
    method: 'post',
    data: data
  })
}

// �޸�密炼机
export function updateDsMljInfo(data) {
  return request({
    url: '/wcs-ds/DsMljInfo',
    method: 'put',
    data: data
  })
}

// ɾ��密炼机
export function delDsMljInfo(id) {
  return request({
    url: '/wcs-ds/DsMljInfo/' + id,
    method: 'delete'
  })
}
