<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<uni-card v-for="item in 2">
			<view class="cardContent">
				<view>
					<span>入库单号：</span><span>IN-231106-1</span>
				</view>
				<view>
					<span>物料编码：</span><span>5135156121212</span>
				</view>
				<view>
					<span>物料名称：</span><span>PL金型模具</span>
				</view>
				<view style="padding-bottom: 20rpx;border-bottom: solid 1rpx #ccc;">
					<span>已验收数/采购数量：</span><span>20/50件</span>
				</view>
			</view>
			<view class="input_search_">
				<view class="input_search_text">LOT：</view>
				<uni-easyinput type="text"  placeholder="请输入" />
			</view>
			<view class="input_search_">
				<view class="input_search_text">数量：</view>
				<uni-easyinput type="text"  placeholder="请输入验收数量" /> 件
			</view>
			<view class="input_search_">
				<view class="input_search_text">托盘号：</view>
				<uni-easyinput type="text" placeholder="请输入" />
			</view>
			<view class="operateBtn">
				<view class="" style="background-color:transparent;color: #333333;border: solid 1rpx #333333;">破损入库
				</view>
				<view class="">正常入库</view>
			</view>
		</uni-card>
	</view>


</template>

<script>
	export default {
		data() {
			return {
				loading: false,
			}
		},
		onLoad() {},
		methods: {
			
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
		width: 120rpx;
		text-align-last: end;
	}
</style>