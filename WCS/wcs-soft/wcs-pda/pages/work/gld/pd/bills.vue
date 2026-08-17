<template>
	<view style="padding: 5px;">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-model="param.keywords" placeholder="金型编码/部品号/部品名" />
			<uni-easyinput type="text" v-model="param.billNo" placeholder="单号" />
		</view>
		<uni-row class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " size="mini" type="info"
					@click="clearParam">重置</button>
			</uni-col>
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
					@click="rows=[];getRows()">查询</button>
			</uni-col>
		</uni-row>
		<view>
			<view style="width:98%;height:auto;padding:10px;background-color:#FFFFFF;margin: 5px;border-radius: 5px;"
				v-for="row in rows">
				<div class=" nameValue">
					<div class="name">订单编号:</div>
					{{row.billNo}}
				</div>
				<div class="nameValue">
					<div class="name" style="">创建时间:</div>
					{{row.createTime}}
				</div>
				<div class="nameValue">
					<div class="name" style="">创建人:</div>
					{{row.createUserName}}
				</div>
				<div class="nameValue">
					<div class="name" style="">审核人:</div>
					{{row.reviewUserName}}
				</div>
				<div v-if="row.isTask==1" class="nameValue">
					<div class="name" style="">下架口:</div>
					<span style="color: #67c23a" v-if="row.toCellCode == '1102'">左侧</span>
					<span style="color: #67c23a" v-if="row.toCellCode == '1100'">右侧</span>
				</div>
				<div v-else>
					<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
						@click="open(row)">下架</button>
				</div>


				<uni-table border style="width:100%" stripe emptyText="暂无更多数据">
					<uni-tr>
						<uni-th align="center">金型编码/组合编码/部品名</uni-th>
						<uni-th align="center">托盘号</uni-th>
					</uni-tr>

					<uni-tr v-for="detail in row.list">
						<uni-td>{{detail.modelCode}}/{{detail.departName}}/{{detail.groupCode}}</uni-td>
						<uni-td>{{detail.palletCode}}</uni-td>
					</uni-tr>
				</uni-table>
			</view>
		</view>

		<uni-popup ref="popup" :mask-click="false">
			<uni-card>
				<div class=" nameValue">
					<div class="name">订单编号:</div>
					{{nowBill.billNo}}
				</div>
				<div class="nameValue">
					<div class="name" style="">创建时间:</div>
					{{nowBill.createTime}}
				</div>
				<div class="nameValue">
					<div class="name" style="">创建人:</div>
					{{nowBill.createUserName}}
				</div>
				<div class="nameValue">
					<div class="name" style="">审核人:</div>
					{{nowBill.reviewUserName}}
				</div>
				<uni-table border style="width:100%" stripe emptyText="暂无更多数据">
					<uni-tr>
						<uni-th align="center">金型编码/组合编码/部品名</uni-th>
						<uni-th align="center">托盘号</uni-th>
					</uni-tr>

					<uni-tr v-for="detail in nowBill.list">
						<uni-td>{{detail.modelCode}}/{{detail.departName}}/{{detail.groupCode}}</uni-td>
						<uni-td>{{detail.palletCode}}</uni-td>
					</uni-tr>
				</uni-table>
				<view class="chooseout">
					<view class="" @click="startDown(1102)">左侧出口</view>
					<view class="" @click="startDown(1100)">右侧出口</view>
				</view>
				<button @click="close">关闭</button>
			</uni-card>
		</uni-popup>

		<uni-load-more :status="param.moreStatus"></uni-load-more>

	</view>


</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				music: null,
				loading: false,
				nowBill: {},
				rows: [],
				param: {
					billNo: '',
					keywords: '',
					total: 0,
					pageNum: 1,
					pageSize: 10,
					reviewState: 1,
					type: 2,
					isCommit: 1,
					moreStatus: "more", //more/loading/noMore 
				},
			}
		},
		watch: {

		},
		onLoad() {
			this.getRows();

		},
		//下拉刷新
		onPullDownRefresh() {
			this.rows = [];
			this.param.pageNum = 1;
			this.param.pageSize = 10;
			this.getRows();
		},
		//触底加载
		onReachBottom: function(e) { //nvue暂不支持滚动监听，可用bindingx代替
			if (this.param.pageNum * this.param.pageSize < this.param.total) {
				this.param.pageNum = this.param.pageNum + 1;
				this.getRows();
			}
		},
		methods: {
			play(state) {
				if (state) {
					this.music = uni.createInnerAudioContext();
					this.music.src = '/static/mp3/success.mp3';
					this.music.play();

				} else {
					this.music = uni.createInnerAudioContext();
					this.music.src = '/static/mp3/fail.mp3';
					this.music.play();

				}
			},
			/** 下架按钮操作 */
			startDown(startDownToCellCode) {
				var that = this;
				const ids = [this.nowBill.id];
				if (startDownToCellCode == null || startDownToCellCode == "") {
					this.$modal.msgError("请选择出库口");
					return;
				}
				this.loading = true;
				request({
					url: "/wcs-inventory/PdMaster/startDown",
					method: "get",
					params: {
						ids: ids,
						toCellCode: startDownToCellCode,
					},
				}).then((response) => {
					this.loading = false;
					if (response.code == 200) {
						that.rows = [];
						that.getRows();
						that.close();
						that.play(true);
						uni.showToast({
							title: '成功',
							icon: 'seccess'
						})
					} else {
						that.play(false);
						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});
			},
			//下架模态框
			open(row) {
				this.nowBill = row;
				this.$refs.popup.open('top')
			},
			//下架模态框
			close() {
				this.$refs.popup.close()
			},
			// 按照金型入库单收货->收货
			toDetail(billNo) {
				console.log(billNo, "this.billNo");
				uni.navigateTo({
					url: '/pages/work/gld/mold/out/detail?billNo=' + billNo
				})
			},
			clearParam() {
				this.rows = [];
				this.param = {
					total: 0,
					pageNum: 1,
					pageSize: 10,
					moreStatus: "more", //more/loading/noMore 
				}
			},

			getRows() {
				var that = this;
				that.loading = true;
				that.moreStatus = "loading";
				request({
					url: '/wcs-inventory/PdMaster/list',
					method: 'get',
					params: this.param
				}).then(response => {
					that.loading = false;
					uni.stopPullDownRefresh();
					if (response.code == 200) {
						for (var i = 0; i < response.rows.length; i++) {
							var row = response.rows[i];
							this.rows.push(row);
						}
						if (this.param.pageNum * this.param.pageSize > this.param.total) {
							that.param.moreStatus = "noMore";
						} else {
							that.param.moreStatus = "more";
						}
						that.param.total = response.total;
					}
				});

			},
		}
	}
</script>

<style>
	.nameValue {
		margin: 5px;

		.name {
			width: 75px;
			text-align: left;
			display: inline-block;
		}
	}

	.chooseout {

		width: 80%;
		margin: auto;
		margin-top: 10px;
		display: flex;
		justify-content: space-between;
		padding-bottom: 40rpx;

		view {
			border: #FFFFFF;
			width: 140rpx;
			padding: 0 30rpx;
			height: 200rpx;
			text-align: center;
			padding-top: 10%;
			border-radius: 20px;
			background-color: #409EFF;
			color: #FFFFFF;
		}
	}
</style>