import request from '@/utils/request'

// ��ѯplc读取站台信号�б�
export function listPlcReadStation(query) {
  return request({
    url: '/wcs-base/PlcReadStation/list',
    method: 'get',
    params: query
  })
}

// ��ѯplc读取站台信号��ϸ
export function getPlcReadStation(id) {
  return request({
    url: '/wcs-base/PlcReadStation/' + id,
    method: 'get'
  })
}

// ����plc读取站台信号
export function addPlcReadStation(data) {
  return request({
    url: '/wcs-base/PlcReadStation',
    method: 'post',
    data: data
  })
}

// �޸�plc读取站台信号
export function updatePlcReadStation(data) {
  return request({
    url: '/wcs-base/PlcReadStation',
    method: 'put',
    data: data
  })
}

// ɾ��plc读取站台信号
export function delPlcReadStation(id) {
  return request({
    url: '/wcs-base/PlcReadStation/' + id,
    method: 'delete'
  })
}
