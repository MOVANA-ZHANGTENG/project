<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<uni-card v-for="item in emptyPalletData">
			<view>
				<span>尺寸：</span><span>{{item.size}}</span>
			</view>
			<!-- <view>
				<span>高度：</span><span>800</span>
			</view> -->
			<view>
				<span>数量：</span><span>{{item.quantity}}</span>
			</view>
			<view class="operateBtn">
				<uni-easyinput type="text" v-model="item.outQuantity" placeholder="请输入数量" />
				<button class="button" style="width:50%;margin-left:5%; " type="primary"
					@click="downEmptyPallet(item)">下架</button>
			</view>
		</uni-card>
	</view>


</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				loading: false,
				emptyPalletData: [],
				quantity: 0,
				outboundNum: '' //出库数量
			}
		},
		onLoad() {
			this.findEmptyData();
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
			findEmptyData() {
				this.loading = true;
				request({
					url: "/wcs-inventory/palletInfo/findEmptyData",
					method: "get",
					params: {},
				}).then((response) => {
					this.emptyPalletData = response.data;
					this.loading = false;
				});
			},
			downEmptyPallet(item) {
				this.loading = true;
				console.log(item,"item");
				var that = this;
				if(item.outQuantity == null || item.outQuantity == "" || item.outQuantity == "0"){
					uni.showToast({
						title: "需要下架的托盘数量不能为0",
						icon: 'error'
					})
					this.loading = false;
					return false;
				}
				request({
					url: "/wcs-inventory/palletInfo/downEmptyPallet",
					method: "get",
					params: {
						type: item.code,
						quantity: item.outQuantity
					},
				}).then((response) => {
					this.loading = false;
					if (response.code == 200) {
						uni.showToast({
							title: "下架任务下发成功",
							icon: 'success'
						})
						that.play(true);
						this.findEmptyData();
					} else {
						that.play(false);
						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});
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
		align-items: center;

		view {
			background-color: #1888cc;
			border-radius: 12rpx;
			color: white;
			text-align: center;
			line-height: 70rpx;
			width: 40%;

		}
	}
</style>