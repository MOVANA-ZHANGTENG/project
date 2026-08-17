import store from '@/store'
import config from '@/config'
import {
	getToken
} from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import {
	toast,
	showConfirm,
	tansParams
} from '@/utils/common'

let timeout = 10000
const baseUrl = config.baseUrl

const request = config => {
	// 是否需要设置 token
	const isToken = (config.headers || {}).isToken === false
	config.header = config.header || {}
	if (getToken() && !isToken) {
		config.header['Authorization'] = 'Bearer ' + getToken()
	}
	// get请求映射params参数
	if (config.params) {
		let url = config.url + '?' + tansParams(config.params)
		url = url.slice(0, -1)
		config.url = url
	}
	return new Promise((resolve, reject) => {
		uni.request({
				method: config.method || 'get',
				timeout: config.timeout || timeout,
				url: config.baseUrl || baseUrl + config.url,
				data: config.data,
				header: config.header,
				dataType: 'json'
			}).then(response => {
				let [error, res] = response
				if (error) {
					var data = {
						code: 500,
						msg: '请检查网络信号...'
					}
					resolve(data);
					toast('请检查网络信号...')
					//reject('请检查网络信号...')
					return
				}
				const code = res.data.code || 200
				const msg = errorCode[code] || res.data.msg || errorCode['default']
				if (code === 401) {
					showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(res => {
						if (res.confirm) {
							store.dispatch('LogOut').then(res => {
								uni.reLaunch({
									url: '/pages/login'
								})
							})
						}
					})
					reject('无效的会话，或者会话已过期，请重新登录。')
				} else if (code === 600) {
					toast(msg)
					reject('600')
				}

				resolve(res.data)
			})
			.catch(error => {
				let {
					message
				} = error
				if (message === 'Network Error') {
					message = '后端接口连接异常'
				} else if (message.includes('timeout')) {
					message = '系统接口请求超时'
				} else if (message.includes('Request failed with status code')) {
					message = '系统接口' + message.substr(message.length - 3) + '异常'
				}
				toast(message)
				reject(error)
			})
	})
}

const ajax = (config) => {
	config.header = {
		'X-Requested-With': 'XMLHttpRequest',
		'Authorization': 'Bearer ' + getToken()
	};
	config.success = config.success || function() {};
	var data = JSON.stringify(config.data);
	uni.request({
		url: config.baseUrl || baseUrl + config.url,
		data: config.data,
		method: config.method || 'POST',
		header: config.header,
		dataType: 'json',
		timeout: 4000,
		success: (res) => {
			const code = res.data.code || 200
			const msg = errorCode[code] || res.data.msg || errorCode['default']
			if (code === 401) {
				showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(res => {
					if (res.confirm) {
						store.dispatch('LogOut').then(res => {
							uni.reLaunch({
								url: '/pages/login'
							})
						})
					}
				})
				reject('无效的会话，或者会话已过期，请重新登录。')
			} else if (code === 500) {
				toast(msg)
				reject('500')
				config.success(res.data);
			} else if (code !== 200) {
				toast(msg)
				reject(code)
				config.success(res.data);
			} else {
				config.success(res.data);
			}
		},
		fail: (err) => {
			toast('后端接口连接异常')
			reject('后端接口连接异常')
			return
		},
		complete: (err) => {

		},
	})
}
export default request