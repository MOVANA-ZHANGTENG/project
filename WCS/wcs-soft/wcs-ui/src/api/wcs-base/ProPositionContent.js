import request from '@/utils/request'

// ��ѯ站台扩展�б�
export function listProPositionContent(query) {
  return request({
    url: '/wcs-base/ProPositionContent/list',
    method: 'get',
    params: query
  })
}

// ��ѯ站台扩展��ϸ
export function getProPositionContent(id) {
  return request({
    url: '/wcs-base/ProPositionContent/' + id,
    method: 'get'
  })
}

// ����站台扩展
export function addProPositionContent(data) {
  return request({
    url: '/wcs-base/ProPositionContent',
    method: 'post',
    data: data
  })
}

// �޸�站台扩展
export function updateProPositionContent(data) {
  return request({
    url: '/wcs-base/ProPositionContent',
    method: 'put',
    data: data
  })
}

// ɾ��站台扩展
export function delProPositionContent(id) {
  return request({
    url: '/wcs-base/ProPositionContent/' + id,
    method: 'delete'
  })
}
