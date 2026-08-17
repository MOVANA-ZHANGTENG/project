<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<changeList v-model="status" :list="statusList"></changeList>
		<view class="input_search_">
			<uni-easyinput type="text" v-model="billNo" placeholder="请输入单号" />
		</view>
		<view class="operateBtn">
			<view class="" @click="getInMasterList()">查询</view>
			<view class="" style="background-color:transparent;color: #333333;border: solid 1rpx #333333;"
				@click="clearBillNo()">重置
			</view>
		</view>
		<uni-card v-for="item in inMasterList">
			<view class="cardContent">
				<view class="cardContentleft">
					<view>
						<span>单号：</span><span>{{item.billNo}}</span>
					</view>
					<view>
						<span>创建人：</span><span>{{item.createUserName}}</span>
					</view>
					<view>
						<span>状态：</span><span>{{item.state === 2 ? '全部收货' : '可收货'}}</span>
					</view>
					<view style="padding-bottom: 20rpx;">
						<span>创建时间：</span><span>{{item.createTime}}</span>
					</view>
				</view>
				<view v-if="item.state<2" class="cardContentright" @click="goshouhuo(item.billNo)">验收</view>
			</view>

			<view class="operateBtn_">
				<uni-table border stripe emptyText="暂无更多数据">
					<uni-tr>
						<uni-th align="center">物料名称</uni-th>
						<uni-th align="center">已验收数/采购数量+单位</uni-th>
					</uni-tr>

					<uni-tr v-for="detail in item.list">
						<uni-td>{{detail.itemName}}</uni-td>
						<uni-td>{{detail.acceptQuantity}}/{{detail.quantity}}{{detail.unitCode}}</uni-td>
					</uni-tr>
				</uni-table>
			</view>
		</uni-card>
		<!-- 部分收货 -->
		<!-- <view class="" v-if="status == 0">
			<uni-card v-for="item in inMasterList">
				<view class="cardContent">
					<view class="cardContentleft">
						<view>
							<span>单号：</span><span>{{item.billNo}}</span>
						</view>
						<view>
							<span>创建人：</span><span>{{item.createUserName}}</span>
						</view>
						<view>
							<span>状态：</span><span>{{item.state === 0 ? '部分验收' : '全部验收'}}</span>
						</view>
						<view style="padding-bottom: 20rpx;">
							<span>创建时间：</span><span>{{item.createTime}}</span>
						</view>
					</view>
					<view class="cardContentright" @click="goshouhuo(item.billNo)">验收</view>
				</view>

				<view class="operateBtn_">
					<uni-table border stripe emptyText="暂无更多数据">

						<uni-tr>
							<uni-th align="center">物料名称</uni-th>
							<uni-th align="center">已验收数/采购数量+单位</uni-th>
						</uni-tr>

						<uni-tr v-for="detail in item.list">
							<uni-td>{{detail.itemName}}</uni-td>
							<uni-td>{{detail.acceptQuantity}}/{{detail.quantity}}{{detail.unitCode}}</uni-td>
						</uni-tr>


					</uni-table>
					<!-- <view class="">物料名称</view>
					<view class="">已验收数/采购数量+单位</view>  
	</view>
	</uni-card>
	</view> -->

		<!-- 全部收货 -->
		<!-- <view class="" v-if="status == 1">
			<uni-card v-for="item in inMasterList">
				<view class="cardContent">
					<view class="cardContentleft">
						<view>
							<span>单号：</span><span>{{item.billNo}}</span>
						</view>
						<view>
							<span>创建人：</span><span>{{item.createUserName}}</span>
						</view>
						<view>
							<span>状态：</span><span>{{item.state === 1 ? '全部验收' : '部分验收'}}</span>
						</view>
						<view style="padding-bottom: 20rpx;">
							<span>创建时间：</span><span>{{item.createTime}}</span>
						</view>
					</view>
				</view>

				<view class="operateBtn_">
					<uni-table border stripe emptyText="暂无更多数据">
						<uni-tr>
							<uni-th align="center">物料名称</uni-th>
							<uni-th align="center">已验收数/采购数量+单位</uni-th>
						</uni-tr>

						<uni-tr v-for="detail in item.list">
							<uni-td>{{detail.itemName}}</uni-td>
							<uni-td>{{detail.acceptQuantity}}/{{detail.quantity}}{{detail.unitCode}}</uni-td>
						</uni-tr>
					</uni-table>
				</view>
			</uni-card>
		</view> -->


	</view>


</template>

<script>
	import request from '@/utils/request';
	export default {
		data() {
			return {
				loading: false,
				inMasterList: [],
				billNo: '',
				statusList: [{
					status: 0,
					text: '未收货'
				}, {
					status: 1,
					text: '部分收货'
				}, {
					status: 2,
					text: '全部收货'
				}],
				status: 0,
				param: {
					total: 0,
					pageNum: 1,
					pageSize: 10,
					moreStatus: "more", //more/loading/noMore
				},
			}
		},
		watch: {
			status(newValue) {
				this.param.pageNum = 1;
				this.status = newValue
				this.getInMasterList();
				this.$forceUpdate()
			}
		},
		onLoad() {
			this.getInMasterList();
		},
		//下拉刷新
		onPullDownRefresh() {
			this.rows = [];
			this.param.pageNum = 1;
			this.param.pageSize = 10;
			this.getInMasterList();
		},
		//触底加载
		onReachBottom: function(e) { //nvue暂不支持滚动监听，可用bindingx代替
			if (this.param.pageNum * this.param.pageSize < this.param.total) {
				this.param.pageNum = this.param.pageNum + 1;
				this.getInMasterList();
			}
		},
		methods: {
			clearParam() {
				this.rows = [];
				this.param = {
					total: 0,
					pageNum: 1,
					pageSize: 10,
					moreStatus: "more", //more/loading/noMore
				}
			},
			// 按照入库单收货->收货
			goshouhuo(billNo) {
				console.log(billNo, "this.billNo");
				uni.navigateTo({
					url: '/pages/work/gld/in/incomeWare?billNo=' + billNo
				})
			},
			clearBillNo() {
				this.billNo = '';
			},
			getInMasterList() {
				var that = this;
				that.inMasterList = [];
				that.loading = true;
				console.log(this.status, "this.status");
				request({
					url: '/wcs-in/inMaster/list',
					method: 'GET',
					params: {
						state: this.status,
						billNo: this.billNo,
						reviewState: 1
					}
				}).then(res => {
					that.loading = false;
					uni.stopPullDownRefresh();
					if (res.code == 200) {
						that.inMasterList = res.rows;
						for (var i = 0; i < that.inMasterList.length; i++) {
							var master = that.inMasterList[i];
						}

						if (this.param.pageNum * this.param.pageSize > this.param.total) {
							that.param.moreStatus = "noMore";
						} else {
							that.param.moreStatus = "more";
						}
						that.param.total = res.total;

					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			},
		}
	}
</script>

<style lang="scss" scoped>
	.operateBtn {
		display: flex;
		width: calc(100% - 56rpx);
		margin: 30rpx auto 0 auto;
		justify-content: space-between;

		view {
			background-color: #1888cc;
			border-radius: 12rpx;
			color: white;
			text-align: center;
			line-height: 60rpx;
			width: 40%;
		}
	}

	.input_search_ {
		width: calc(100% - 56rpx);
		margin: auto;
		margin-top: 20rpx;
	}

	.cardContent {
		display: flex;
		justify-content: space-between;
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
</style>