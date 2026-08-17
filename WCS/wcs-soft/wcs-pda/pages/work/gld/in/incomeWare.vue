<template>
	<view class="">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view class="" style="font-weight: 700;padding-left: 28rpx;">单号：{{billNo}}</view>
		<uni-card v-for="item in inDetailList">
			<view class="cardContent" v-if="item.quantity != item.acceptQuantity">
				<view>
					<span>物料编码：</span><span>{{item.itemCode}}</span>
				</view>
				<view>
					<span>物料名称：</span><span>{{item.itemName}}</span>
				</view>
				<!-- <view>
					<span>批次号：</span><span>{{item.batchCode}}</span>
				</view> -->
				<view style="padding-bottom: 20rpx;border-bottom: solid 1rpx #ccc;">
					<span>已验收数/采购数量：</span><span>{{item.acceptQuantity}}/{{item.quantity}}{{item.unitCode}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">数量：</view>
					<uni-easyinput type="text" placeholder="请输入验收数量" v-model="item.inQuantity" /> 件
				</view>
				<view class="input_search_">
					<view class="input_search_text">货物状态：</view>
					<uni-data-select :localdata="goodsStatus" v-model="item.goodsStatus" class=""
						placeholder=" 请选择货物状态"></uni-data-select>
				</view>
				<view class="input_search_">
					<view class="input_search_text">lot号：</view>
					<uni-easyinput type="text" placeholder="请输入lot号" v-model="item.batchNo" />
				</view>
				<view class="input_search_">
					<view class="input_search_text">收货人：</view>
					<uni-easyinput type="text" placeholder="请扫描收货人二维码" v-model="item.userCardNo" />
				</view>
				<view class="operateBtn">
					<view class="" style="background-color:transparent;color: #333333;border: solid 1rpx #333333;"
						@click="commit(item)">
						破损入库
					</view>
					<view class="" @click="commit2(item)">正常入库</view>
				</view>
			</view>

			<view class="cardContent" v-if="item.quantity == item.acceptQuantity">
				<view>
					<span>物料编码：</span><span>{{item.itemCode}}</span>
				</view>
				<view>
					<span>物料名称：</span><span>{{item.itemName}}</span>
				</view>
				<view style="padding-bottom: 20rpx;border-bottom: solid 1rpx #ccc;">
					<span>已验收数/采购数量：</span><span>{{item.acceptQuantity}}/{{item.quantity}}{{item.unitCode}}</span>
				</view>
				<view class="">
					<view class="btmTxet">本商品已全部验收</view>
				</view>
			</view>
		</uni-card>
		<!-- <uni-card  v-for="item in inDetailList">
			<view class="cardContent">
				<view>
					<span>物料编码：</span><span>{{item.itemCode}}</span>
				</view>
				<view>
					<span>物料名称：</span><span>{{item.itemName}}</span>
				</view>
				<view style="padding-bottom: 20rpx;border-bottom: solid 1rpx #ccc;">
					<span>已验收数/采购数量：</span><span>{{item.acceptQuantity}}/{{item.quantity}}{{item.unitCode}}</span>
				</view>
			</view>
			<view class="">
				<view class="btmTxet">本商品已全部验收</view>
			</view>
		</uni-card> -->
	</view>
</template>

<script>
	import request from '@/utils/request';
	export default {
		data() {
			return {
				loading: false,
				// billNo: this.$route.params.billNo,
				// billNo: "IN-231223-3",
				billNo: '',
				batchCode: '',
				inDetailList: [],
				goodsStatus: [{
						value: 1,
						text: "正常"
					},
					{
						value: 2,
						text: "散料"
					},
					{
						value: 3,
						text: "退料"
					},
				],


				// type:''
			}
		},
		onLoad(option) {
			this.billNo = option.billNo;
			this.getInDetailList();
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
			onChange(item) {
				console.log(item, "type");
			},
			scanCode() {
				console.log("111");
			},
			getInDetailList() {
				var that = this;
				that.inMasterList = [];
				that.loading = true;
				request({
					url: '/wcs-in/inMaster/list',
					method: 'GET',
					data: {
						billNo: that.billNo
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						for (let i = 0; i < res.rows[0].list.length; i++) {
							res.rows[0].list[i].type = null
							//res.rows[0].list[i].billNo = null
							res.rows[0].list[i].inQuantity = null
							res.rows[0].list[i].acceptUserName = null
						}
						that.inDetailList = res.rows[0].list;
						console.log(that.inDetailList, "that.inDetailList");
					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			},
			commit(item) {
				var that = this;
				if (item.inQuantity > item.quantity - item.acceptQuantity) {
					uni.showToast({ //提示信息
						title: '验收数量不可以超过采购数量', //提示内容
						icon: "none" //提示图标
					});
					return false
				}
				that.loading = true;
				request({
					url: "/wcs-in/InRecord/in",
					method: "get",
					params: item,
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.play(true);
						uni.showToast({
							title: '入库成功',
							icon: 'error'
						})
						this.getInDetailList();
					} else {
						that.play(false);
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			},
			commit2(item) {
				console.log(item);
				if (item.inQuantity > item.quantity - item.acceptQuantity) {
					uni.showToast({ //提示信息
						title: '验收数量不可以超过采购数量', //提示内容
						icon: "none" //提示图标
					});
					return false
				}
				var that = this;
				that.inMasterList = [];
				that.loading = true;
				request({
					url: "/wcs-in/InRecord/in",
					method: "get",
					params: item,
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.play(true);
						uni.showToast({
							title: '入库成功',
							icon: 'error'
						})
						this.getInDetailList();
					} else {
						that.play(false);
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			},
		}
	}
</script>

<style lang="scss" scoped>
	.btmTxet {
		color: red;
		text-align: center;
		line-height: 60rpx;
		margin-top: 10px;
	}

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

	.input_search {
		width: calc(100% - 56rpx);
		margin: 20rpx auto;

	}

	.input_search_ {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 20rpx;
	}

	.input_search_text {
		width: 140rpx;
		text-align-last: end;
	}
</style>