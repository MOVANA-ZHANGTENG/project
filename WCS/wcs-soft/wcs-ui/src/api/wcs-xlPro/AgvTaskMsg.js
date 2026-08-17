import request from '@/utils/request'

// ��ѯagv任务�б�
export function listAgvTaskMsg(query) {
  return request({
    url: '/wcs-xlPro/AgvTaskMsg/list',
    method: 'get',
    params: query
  })
}

// ��ѯagv任务��ϸ
export function getAgvTaskMsg(id) {
  return request({
    url: '/wcs-xlPro/AgvTaskMsg/' + id,
    method: 'get'
  })
}

export function getAgvPosition() {
  return request({
    url: '/wcs-xlPro/AgvTaskMsg/findAGVPosition',
    method: 'get'
  })
}

// ����agv任务
export function addAgvTaskMsg(data) {
  return request({
    url: '/wcs-xlPro/AgvTaskMsg',
    method: 'post',
    data: data
  })
}

// �޸�agv任务
export function updateAgvTaskMsg(data) {
  return request({
    url: '/wcs-xlPro/AgvTaskMsg',
    method: 'put',
    data: data
  })
}

// ɾ��agv任务
export function delAgvTaskMsg(id) {
  return request({
    url: '/wcs-xlPro/AgvTaskMsg/' + id,
    method: 'delete'
  })
}
