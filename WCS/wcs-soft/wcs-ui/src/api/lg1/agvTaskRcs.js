import request from '@/utils/request'

// ��ѯ接收agv回传信息�б�
export function listAgvTaskRcs(query) {
  return request({
    url: '/lg1/agvTaskRcs/list',
    method: 'get',
    params: query
  })
}

// ��ѯ接收agv回传信息��ϸ
export function getAgvTaskRcs(id) {
  return request({
    url: '/lg1/agvTaskRcs/' + id,
    method: 'get'
  })
}

// ����接收agv回传信息
export function addAgvTaskRcs(data) {
  return request({
    url: '/lg1/agvTaskRcs',
    method: 'post',
    data: data
  })
}

// �޸�接收agv回传信息
export function updateAgvTaskRcs(data) {
  return request({
    url: '/lg1/agvTaskRcs',
    method: 'put',
    data: data
  })
}

// ɾ��接收agv回传信息
export function delAgvTaskRcs(id) {
  return request({
    url: '/lg1/agvTaskRcs/' + id,
    method: 'delete'
  })
}
