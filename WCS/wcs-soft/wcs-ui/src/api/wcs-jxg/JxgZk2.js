import request from '@/utils/request'

// 查询提升机状态（当前层数）
export function getElevatorStatus() {
  return request({
    url: '/wcs-jxg/zk2/tsjZ',
    method: 'get'
  })
}

