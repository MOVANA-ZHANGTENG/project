// 应用全局配置
module.exports = {

	baseUrl: 'http://localhost:8007/wcs',
	//baseUrl: 'http://192.168.2.16:8080/wms',
	//baseUrl: ' http://47.122.3.29:8099/wms',
	// 应用信息
	appInfo: {
		// 应用名称
		name: "wms",
		// 应用版本A
		version: "1.1.0",
		// 应用logo
		logo: "/static/logo.png",
		// 官方网站
		site_url: "https://www.wmswcs.com",
		// 政策协议
		agreements: [{
				title: "隐私政策",
				url: "https://www.wmswcs.com/protocol.html"
			},
			{
				title: "用户服务协议",
				url: "https://www.wmswcs.com/protocol.html"
			}
		]
	}
}