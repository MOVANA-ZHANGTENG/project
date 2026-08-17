import request from '@/utils/request'

// ��ѯ计划分配料箱�б�
export function listDsPlanPallet(query) {
  return request({
    url: '/wcs-ds/DsPlanPallet/list',
    method: 'get',
    params: query
  })
}

// ��ѯ计划分配料箱��ϸ
export function getDsPlanPallet(id) {
  return request({
    url: '/wcs-ds/DsPlanPallet/' + id,
    method: 'get'
  })
}

// ����计划分配料箱
export function addDsPlanPallet(data) {
  return request({
    url: '/wcs-ds/DsPlanPallet',
    method: 'post',
    data: data
  })
}

// �޸�计划分配料箱
export function updateDsPlanPallet(data) {
  return request({
    url: '/wcs-ds/DsPlanPallet',
    method: 'put',
    data: data
  })
}

// ɾ��计划分配料箱
export function delDsPlanPallet(id) {
  return request({
    url: '/wcs-ds/DsPlanPallet/' + id,
    method: 'delete'
  })
}
