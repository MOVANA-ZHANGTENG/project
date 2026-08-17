import request from '@/utils/request'

// 查询料号袋子统计列表
export function listBagStatistics(query) {
  return request({
    url: '/wcs-ds/bagStatistics/list',
    method: 'get',
    params: query
  })
}

// 导出料号袋子统计
export function exportBagStatistics(query) {
  return request({
    url: '/wcs-ds/bagStatistics/export',
    method: 'post',
    params: query
  })
}

