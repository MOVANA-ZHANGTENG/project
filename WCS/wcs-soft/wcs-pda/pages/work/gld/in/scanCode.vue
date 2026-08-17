<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view class="newTray">扫码（物料编码+LOT）</view>
		<uni-card v-for="item in 2">
			<view class="cardContent">
				<view class="cardContentleft">
					<view>
						<span>单号：</span><span>IN-231106-1</span>
					</view>
					<view>
						<span>创建人：</span><span>张三</span>
					</view>
					<view>
						<span>状态：</span><span>部分收货</span>
					</view>
					<view style="padding-bottom: 20rpx;">
						<span>创建时间：</span><span>2023-11-06 18:30:49</span>
					</view>
				</view>
				<view class="cardContentright" @click="goTakeMoney">收货</view>
			</view>
			
			<view class="operateBtn_">
				<view class="">物料名称</view>
				<view class="">已验收数/采购数量+单位</view>
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
			// 前往收货
			goTakeMoney(){
				uni.navigateTo({
					url: '/pages/work/gld/in/takeMoney'
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.cardContent{
		display: flex;
		justify-content: space-between;
	}
	.cardContentright{
		color: #fff;
		background-color: #1888cc;
		line-height: 60rpx;
		height: 60rpx;
		text-align: center;
		width: 200rpx;
		border-radius: 12rpx;
	}
	.newTray {
		width: calc(100% - 56rpx);
		margin: 30rpx auto;
		background-color: #1888cc;
		border-radius: 12rpx;
		color: white;
		text-align: center;
		line-height: 80rpx;
	}

	.operateBtn_ {
		display: flex;
		justify-content: space-between;
		border-top: 1rpx solid;
		padding: 20rpx  0;
		view {
			text-align: center;
			line-height: 60rpx;
			// width: 40%;
		}
	}
</style>