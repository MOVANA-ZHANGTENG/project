import request from '@/utils/request'

// ��ѯ任务历史�б�
export function listHistory(query) {
  return request({
    url: '/wcs-task/history/list',
    method: 'get',
    params: query
  })
}

// ��ѯ任务历史��ϸ
export function getHistory(id) {
  return request({
    url: '/wcs-task/history/' + id,
    method: 'get'
  })
}

// ����任务历史
export function addHistory(data) {
  return request({
    url: '/wcs-task/history',
    method: 'post',
    data: data
  })
}

// �޸�任务历史
export function updateHistory(data) {
  return request({
    url: '/wcs-task/history',
    method: 'put',
    data: data
  })
}

// ɾ��任务历史
export function delHistory(id) {
  return request({
    url: '/wcs-task/history/' + id,
    method: 'delete'
  })
}
