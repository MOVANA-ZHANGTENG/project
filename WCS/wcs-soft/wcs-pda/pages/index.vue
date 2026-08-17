<template>
	<view class="charts-box">
		<view style="
	  position:fixed; top:0; left:0; 
	  width:100%; 
	  height:45px;
	  background-color: #ffffff; 
	   text-align: center;
	  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	   line-height: 45px;
	   font-size: 16px;
	   font-weight:900;
	   z-index: 9;
	   ;
	  ">立体库PDA程序</view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="height: 45px;"></view>
		<!-- <uni-row  class="demo-uni-row">
		  <view style="width: 95%;margin-left:10px;margin-top: 10px">
			  <uni-col v-for="index in 4"   :xs="12" :sm="8" :md="6" :lg="6" :xl="6">
			  	 <view style="width:90%;
				 margin-left: 5%;
				 margin-top: 5px;
				 height:60px; 
				 background-color: #ffffff;
				 border-radius: 5px;
				 box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
				  
				 padding: 20px;
				 "></view>
			  </uni-col> 
			  
			  
		  </view>
	  	
	  	 
	  </uni-row> -->
		<uni-card title="货位一览" extra="">


			<uni-data-checkbox v-model="z" :localdata="zs" @change="getCellInfos(z)"></uni-data-checkbox>


			<div v-for="x in 30" style="float:left;width:3.3%;">
				<!-- y -->
				<div v-for="y in 30" style="height:0.8vh;border-style:solid;border-color:#FFFFFF">
					<div :id="x+'-'+(31-y)"
						style=" border-width: 3px; width:100%;height:100%;   font-size:8px;color:#C0C4CC;line-height:1.8vh;text-align: center ">

					</div>

				</div>

			</div>
			<!-- .kongCell { 
	  color: #409eff;
	  background-color: #409eff;
    }
    .noKongCell { 
		color: #67C23A;
      border-color: #67C23A;
    }
    .inCell { 
		color: #f56c6c;
      border-color: #f56c6c;
    }
    .outCell { 
		color: #E6A23C;
      background-color: #E6A23C; 
    } -->

			<div style="width:95%;height:5vh;background-color:#FFFFFF;margin-left:3%;margin-top:3vh;padding-top:0.7vh">


				<div class="kongCell" style="
				                  float:left;border-style:solid; 
				                  width:3%;height:1vh; margin-left:15px; margin-top:1.8vh"></div>
				<div style="float:left; margin-left:5px; margin-top:1vh  ">无货 </div>


				<div class="noKongCell" style="
				                  float:left;border-style:solid; 
				                  width:3%;height:1vh; margin-left:15px; margin-top:1.8vh"></div>
				<div style="float:left; margin-left:5px;margin-top:1vh ">有货 </div>


				<div class="inCell" style="
				                  float:left;border-style:solid; 
				                  width:3%;height:1vh; margin-left:15px; margin-top:1.8vh"></div>
				<div style="float:left; margin-left:5px;margin-top:1vh  ">入库中</div>


				<div class="outCell" style="
				                  float:left;border-style:solid; 
				                  width:3%;height:1vh; margin-left:15px; margin-top:1.8vh"></div>
				<div style="float:left; margin-left:5px; margin-top:1vh ">出库中</div>

			</div>
		</uni-card>


		<uni-card title="任务统计" extra="">
			<qiun-data-charts type="line" :opts="opts" :chartData="chartData" />
		</uni-card>
		<uni-card title="货位统计" extra="">
			<qiun-data-charts type="ring" :opts="cellReportOts" :chartData="cellReportData" />
		</uni-card>

		<view style="height: 80px;"></view>

	</view>
</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				loading: false,
				z: 1,
				zs: [{
					"value": 1,
					"text": "1层"
				}, {
					"value": 2,
					"text": "2层"
				}, {
					"value": 3,
					"text": "3层"
				}, {
					"value": 4,
					"text": "4层"
				}, {
					"value": 5,
					"text": "5层"
				}],
				cellList: [],
				chartData: {},
				//您可以通过修改 config-ucharts.js 文件中下标为 ['line'] 的节点来配置全局默认参数，如都是默认参数，此处可以不传 opts 。实际应用过程中 opts 只需传入与全局默认参数中不一致的【某一个属性】即可实现同类型的图表显示不同的样式，达到页面简洁的需求。
				opts: {
					color: ["#1890FF", "#91CB74", "#FAC858", "#EE6666", "#73C0DE", "#3CA272", "#FC8452", "#9A60B4",
						"#ea7ccc"
					],
					padding: [10, 10, 0, 10],
					enableScroll: false,
					legend: {},
					xAxis: {
						disableGrid: true
					},
					yAxis: {
						gridType: "dash",
						dashLength: 2
					},
					extra: {
						line: {
							type: "curve",
							width: 2,
							activeType: "hollow"
						}
					}
				},
				cellReportData: {
					series: [{
						data: [{
								"name": "空闲",
								"value": 50
							}, {
								"name": "有货",
								"value": 30
							}, {
								"name": "出库中",
								"value": 20
							}, {
								"name": "入库中",
								"value": 18
							}

						]
					}]
				},

				cellReportOts: {
					rotate: false,
					rotateLock: false,
					color: ["#1890FF", "#91CB74", "#FAC858", "#EE6666", "#73C0DE", "#3CA272", "#FC8452", "#9A60B4",
						"#ea7ccc"
					],
					padding: [5, 5, 5, 5],
					dataLabel: true,
					enableScroll: false,
					legend: {
						show: true,
						position: "right",
						lineHeight: 25
					},
					title: {
						name: " ",
						fontSize: 15,
						color: "#666666"
					},
					subtitle: {
						name: "70%",
						fontSize: 25,
						color: "#7cb5ec"
					},
					extra: {
						ring: {
							ringWidth: 60,
							activeOpacity: 0.5,
							activeRadius: 10,
							offsetAngle: 0,
							labelWidth: 15,
							border: false,
							borderWidth: 3,
							borderColor: "#FFFFFF"
						}
					}
				}
			};
		},
		watch: {

		},
		onReady() {
			this.getServerData();
			this.getCellInfos();
		},
		methods: {
			getCellInfos(z) {
				var that = this;
				that.loading = true;

				for (var x = 1; x <= 30; x++) {
					for (var y = 1; y <= 30; y++) {
						var div = document.getElementById(x + '-' + y);
						div.className = "";
					}
				}

				request({
					url: '/wcs-base/CellInfo/findByZ',
					method: 'get',
					params: {
						z: z
					}
				}).then(response => {
					that.loading = false;
					console.log(that.loading);
					if (response.code == 200) {
						that.loading = false;
						that.cellList = response.data;
						that.cellList.forEach(cell => {
							var aaa = '#' + cell.x + '-' + cell.y;

							// var selectorQuery = uni.createSelectorQuery();
							// // 2.1 查找单个节点
							// let div = uni.createSelectorQuery().in(that).select(aaa);
							let div = document.getElementById(cell.x + '-' + cell.y);
							if (cell.invenState == 0) {
								if (cell.taskState == 0) {
									//  var div = document.getElementById(cell.x + '-' + cell.y);
									div.className = "kongCell";
								}
								if (cell.taskState == 1) {
									div.className = "inCell";
									document.getElementById(cell.x + '-' + cell.y).className = "inCell";
								}
							}
							if (cell.invenState == 1) {
								if (cell.taskState == 0) {
									// var div = document.getElementById(cell.x + '-' + cell.y);
									div.className = "noKongCell";
								}
								if (cell.taskState == 1) {
									div.className = "outCell";
								}
							}

						});
					} else {
						that.loading = false;
					}
					that.loading = false;
				});
			},
			getServerData() {
				//模拟从服务器获取数据时的延时
				setTimeout(() => {
					//模拟服务器返回数据，如果数据格式和标准格式不同，需自行按下面的格式拼接
					let res = {
						categories: ["周一", "周二", "周二", "周二", "周二", "周二"],
						series: [{
								name: "入库任务",
								lineType: "dash",
								data: [35, 8, 25, 37, 4, 20]
							},
							{
								name: "出库任务",
								data: [70, 40, 65, 100, 44, 68]
							},
							{
								name: "回库任务",
								data: [100, 80, 95, 150, 112, 132]
							}
						]
					};
					this.chartData = JSON.parse(JSON.stringify(res));
				}, 500);
			},
		}
	};
</script>

<style scoped>
	/* 请根据实际需求修改父元素尺寸，组件自动识别宽高 */
	.charts-box {
		width: 100%;
		height: 300px;
	}

	.kongCell {
		color: #DCDFE6;
		background-color: #DCDFE6;
	}

	.noKongCell {
		color: #409EFF;
		background-color: #409EFF;
	}

	.inCell {
		color: #f56c6c;
		background-color: #f56c6c;
	}

	.outCell {
		color: #E6A23C;
		background-color: #E6A23C;
	}
</style>