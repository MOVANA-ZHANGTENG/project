<template>
	<view>
		<view class="scan_code_input">
			<input placeholder="请输入托盘号" v-model="palletCode" class="input_" placeholder-class="input">
			<view class="scan_code_search" @click="getPalletMaxNum">搜索</view>

		</view>
		<view>此托盘最多可以容纳{{pallet.maxNum}}包物料，现已放了<span>{{count}}</span>包物料</view>
		<!-- <view class="newTray">请扫描托盘码</view> -->
		<view class="scan_code_input">
			<input placeholder="请输入物料编码" v-model="itemCode" class="input_" placeholder-class="input">
			<view class="scan_code_search" @click="getItemDetail">搜索</view>
		</view>
		<!-- <view class="newTray" @click="saomiao" v-if="status == 0">请扫描物料编码</view> -->

		<uni-card v-if="nowInven!=null">
			<view>
				<span>物料编码：</span><span>{{nowInven.itemCode}}</span>
			</view>
			<view>
				<span>物料名称：</span><span>{{nowInven.itemName}}</span>
			</view>
			<view>
				<span>批次：</span><span>{{nowInven.batchNo}}</span>
			</view>
			<view>
				<span>数量：</span><span>{{nowInven.quantity}}</span>
			</view>
			<view>
				<span>物料状态： </span><span v-for="dict in goodsStatus"
					v-if="nowInven.goodsStatus == dict.value">{{ dict.label }}</span>
			</view>
			<view>

				<uni-easyinput placeholder="组盘数量" stype="width:100px" type="text" v-model="groupQuantity" />

			</view>


		</uni-card>
		<uni-card v-else @click="nowInven=item" v-for="item in itemDetailList">
			<view>
				<span>物料编码：</span><span>{{item.itemCode}}</span>
			</view>
			<view>
				<span>物料名称：</span><span>{{item.itemName}}</span>
			</view>
			<view>
				<span>批次：</span><span>{{item.batchNo}}</span>
			</view>
			<view>
				<span>数量：</span><span>{{item.quantity}}</span>
			</view>
			<view>
				<span>物料状态： </span><span v-for="dict in goodsStatus"
					v-if="item.goodsStatus == dict.value">{{ dict.label }}</span>
					<span v-if="item.qcState == 1">-破损入库</span>
			</view>


			<!-- <view>
					<span>入库时间：</span><span>{{item.inDae}}</span>
				</view> -->

			<!-- <view>
				<span>已验收数/采购数量：</span><span>30/30件</span>
			</view> -->
		</uni-card>

		<!-- <view class="newTray" v-if="status == 1">点击继续绑定下个物料</view> -->
		<uni-row class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="info" @click="clearInput()">清空</button>
			</uni-col>
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="primary"
					@click="groupBoxInsert">确认</button>
			</uni-col>
		</uni-row>
	</view>


</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				//组盘数量
				groupQuantity: null,
				//当前组盘的库存
				nowInven: null,
				status: 1,
				palletCode: '',
				itemCode: '',
				itemDetail: {},
				itemDetailList: [],
				count: 0,
				num: 1,
				pallet: {},
				itemCodes: [],
				goodsStatus: [{
						value: 1,
						label: "供应商来料"
					},
					{
						value: 2,
						label: "散料"
					},
					{
						value: 3,
						label: "退料"
					},
				],
			}
		},
		watch: {

		},
		onLoad() {


		},
		methods: {
			saomiao() {
				// 容器绑定成功请求后
				this.status = 1
			},
			clearInput() {
				this.palletCode = '';
				this.itemCode = '';
				this.nowInven = null;
				this.itemDetailList = [];
			},
			getItemDetail() {
				var that = this;
				if (that.palletCode == null || that.palletCode == '') {
					uni.showToast({
						title: '请先扫描托盘码',
						icon: 'none'
					})
					return;
				}

				that.loading = true;
				request({
					url: '/wcs-inventory/inventory/list',
					method: 'GET',
					data: {
						itemCode: that.itemCode,
						state: 0
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.itemDetailList = res.rows;
					} else {
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			},
			getPalletMaxNum() {
				var that = this;
				that.loading = true;
				request({
					url: '/wcs-inventory/palletInfo/findMaxNum',
					method: 'GET',
					data: {
						code: that.palletCode
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.pallet = res.data;
						this.getPalletHasNum();
						if (that.pallet.maxNum == null) {
							uni.showToast({
								title: '此托盘不是物料托盘，请选择其他托盘',
								icon: 'error'
							})
						}
					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			},
			getPalletHasNum() {
				var that = this;
				that.loading = true;
				request({
					url: '/wcs-inventory/palletInfo/findHasNum',
					method: 'GET',
					data: {
						code: that.palletCode
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.count = res.data.sumNum;
					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			},
			groupBoxInsert() {
				var that = this;
				if (that.nowInven == null) {
					uni.showToast({
						title: '请选择要组盘的库存后在提交',
						icon: 'error'
					})
					return;
				};
				if (that.groupQuantity == null || that.groupQuantity == 0) {
					uni.showToast({
						title: '请输入组盘数量后再提交',
						icon: 'error'
					})
					return;
				};
				if (that.groupQuantity > that.nowInven.quantity) {
					uni.showToast({
						title: '组盘数量不得大于库存数量',
						icon: 'error'
					})
					return;
				};
				if (that.groupQuantity > that.nowInven.quantity) {
					uni.showToast({
						title: '组盘数量不得大于库存数量',
						icon: 'error'
					})
					return;
				};

				if (that.palletCode == null || that.palletCode == "") {
					uni.showToast({
						title: '请扫描托盘号后再提交',
						icon: 'error'
					})
					return;
				}

				that.loading = true;
				request({
					url: '/wcs-in/GroupBox/groupBoxInsert',
					method: 'GET',
					data: {
						palletCode: that.palletCode,
						inventoryId: that.nowInven.inventoryId,
						quantity: that.groupQuantity
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						this.clearInput();
						uni.showToast({
							title: '组盘成功',
							icon: 'success'
						})
					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			}
		}
	}
</script>

<style>
	.newTray {
		width: calc(100% - 56rpx);
		margin: 30rpx auto;
		background-color: #fff;
		border-radius: 12rpx;
		color: #333;
		text-align: center;
		line-height: 80rpx;
	}

	.scan_code_input {
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-radius: 8rpx;
		width: calc(100% - 56rpx);
		margin: auto;
		margin-top: 20rpx;

		.input_ {
			background-color: #ffffff;
			height: 76rpx;
			width: 80%;
			border-radius: 12rpx;
			box-shadow: 0rpx 0rpx 4rpx 2rpx rgba(27, 141, 85, 0.1);
			padding-left: 30rpx;
			margin-right: 10rpx;
		}
	}

	.scan_code_search {
		width: 20%;
		border-radius: 8rpx;
		text-align: center;
		line-height: 70rpx;
		color: white;
		font-size: 32rpx;
		background-color: #4ABC84;
		// margin: 40rpx auto;
	}
</style>