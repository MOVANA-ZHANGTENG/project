<template>
	<view style="padding: 5px;">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
			<changeList v-model="status" :list="statusList"></changeList>
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
			<view @click="toDetail(row)"
				style="width:98%;height:auto;padding:10px;background-color:#FFFFFF;margin: 5px;border-radius: 5px;"
				v-for="row in rows">

				<div class="nameValue">
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


				<uni-table border stripe emptyText="暂无更多数据">
					<uni-tr>
						<uni-th align="center">金型编码/组合编码/部品名</uni-th>
						<uni-th align="center">状态</uni-th>
						<!-- <uni-th align="center">部品号</uni-th> -->
					</uni-tr>

					<uni-tr v-for="detail in row.list">
						<uni-td>{{detail.modelCode}}/{{detail.departName}}/{{detail.groupCode}}</uni-td>

						<uni-td>
							<span v-if="detail.state==0">未组盘</span>
							<span v-if="detail.state==1">已组盘</span>
						</uni-td>
						

					</uni-tr>
				</uni-table>
<view v-if="status==0" class="operateBtn_">
							<uni-data-checkbox v-model="row.reviewState2" :localdata="reviewStates"></uni-data-checkbox>
							<view class="cardContentright" @click="reviewCommit(row)">审核</view>
						</view>
			</view>
		</view>
		<image src="../../../../../static/gld/tianjia.png" mode="" class="icon_tianjia" @click="goAddInWare"></image>
		<!-- {{param}} -->
		<uni-load-more :status="param.moreStatus"></uni-load-more>
		<!-- <uni-pagination
			 style="width:90%;margin-left:5%;height:50px;background-color:#FFFFFF;padding: 5px;border-radius: 5px;"
			 title="标题文字" :total="param.total"></uni-pagination> -->
	</view>


</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				status: 1,
				statusList:[
					{
						status: 0,
						text: '未审核'
					}, {
						status: 1,
						text: '待组盘'
					}, {
						status: 2,
						text: '已组盘'
					}],
					reviewStates: [{
						value: 1,
						text: '通过'
					}, {
						value: -1,
						text: '拒绝'
					}
				],
				loading: false,
				rows: [],
				param: {
					total: 0,
					pageNum: 1,
					pageSize: 10,
					reviewState: 1,
					states : [0,1],
					type: 1,
					keywords: '', 
					moreStatus: "more", //more/loading/noMore


				},
			}
		},
		watch: {
			status(newValue) {
				this.param.pageNum = 1;
					
				if(newValue==0){
					this.param.reviewState = 0;
				} 
				if(newValue==1){
					this.param.reviewState = 1;
					this.param.states = [0,1];
				} 
				if(newValue==2){
					this.param.reviewState = null;
					this.param.states = [2,3];
				} 
				this.rows = [];
				this.getRows();
				this.$forceUpdate()
			}
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
			// 跳转创建入库单
			goAddInWare(){
				uni.navigateTo({
					url:'/pages/work/gld/mold/in/create'
				})
			},
			reviewCommit(bill) {
				var that = this;
				that.loading = true;
				request({
					url: '/wcs-inventory/MetalModelBill/review',
					method: 'GET',
					params: {
						ids: [bill.id],
						reviewState: bill.reviewState2,
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						this.rows=[];
						this.getRows();
			
					} else {
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			},
			// 按照金型入库单收货->收货
			toDetail(bill) {
				 
				if(bill.reviewState==0 ){
					return;
				}
					
				if(bill.state==0 ||bill.state==1 ){ 
					uni.navigateTo({
						url: '/pages/work/gld/mold/in/detail?billNo=' + bill.billNo
					})
				}
				
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
			toPick(waveId) {
				uni.navigateTo({
					url: '/pages/work/out/wavePick/wavePick?waveId=' + waveId
				});
			},
			getRows() {
				var that = this;
				that.loading = true;
				that.moreStatus = "loading";
				request({
					url: '/wcs-inventory/MetalModelBill/list',
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
	.cardContentright {
		color: #fff;
		background-color: #1888cc;
		line-height: 60rpx;
		height: 60rpx;
		text-align: center;
		width: 200rpx;
		border-radius: 12rpx;
	}
	
	.operateBtn_ {
		display: flex;
		justify-content: space-between;
		border-top: 1rpx solid;
		padding: 20rpx 0;
	
		view {
			text-align: center;
			line-height: 60rpx;
		}
	}
	.icon_tianjia{
		width: 100rpx;
		height: 100rpx;
		position: fixed;
		right: 30rpx;
		z-index: 22;
		bottom: 100rpx;
	}
</style>