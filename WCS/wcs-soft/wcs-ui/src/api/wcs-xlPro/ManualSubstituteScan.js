import request from '@/utils/request'

// ��ѯ手动叉料�б�
export function listManualSubstituteScan(query) {
  return request({
    url: '/wcs-xlPro/ManualSubstituteScan/list',
    method: 'get',
    params: query
  })
}

// ��ѯ手动叉料��ϸ
export function getManualSubstituteScan(id) {
  return request({
    url: '/wcs-xlPro/ManualSubstituteScan/' + id,
    method: 'get'
  })
}

// ����手动叉料
export function addManualSubstituteScan(data) {
  return request({
    url: '/wcs-xlPro/ManualSubstituteScan',
    method: 'post',
    data: data
  })
}

// �޸�手动叉料
export function updateManualSubstituteScan(data) {
  return request({
    url: '/wcs-xlPro/ManualSubstituteScan',
    method: 'put',
    data: data
  })
}

// ɾ��手动叉料
export function delManualSubstituteScan(id) {
  return request({
    url: '/wcs-xlPro/ManualSubstituteScan/' + id,
    method: 'delete'
  })
}
