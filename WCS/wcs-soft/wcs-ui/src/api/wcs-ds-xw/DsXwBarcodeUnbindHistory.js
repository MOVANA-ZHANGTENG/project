import request from '@/utils/request'

// 根据条码号查询绑定/回绑记录列表
export function listByBarcode(barcode) {
  return request({
    url: '/wcs-ds-xw/DsXwBarcodeUnbindHistory/listByBarcode',
    method: 'get',
    params: { barcode }
  })
}

