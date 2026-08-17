<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view class="input_search">
			<uni-easyinput type="text" v-model="numbers" placeholder="请输入单号" />
			<uni-easyinput type="text" v-model="date" placeholder="请选择预计到货时间" />
			<view class="operateBtn">
				<view class="" style="background-color:transparent;color: #333333;border: solid 1rpx #333333;">重置</view>
				<view class="">查询</view>
			</view>
		</view>
		<uni-card v-for="item in 2">
			<view>
				<span>单号：</span><span>IN-231106-1</span>
			</view>
			<view>
				<span>状态：</span><span>部分收货</span>
			</view>
			<view>
				<span>预计到货时间：</span><span>2023-11-16 15:00:00</span>
			</view>
			<view>
				<span>创建时间：</span><span>2023-11-06 18:30:49</span>
			</view>
			<view class="operateBtn_">
				<view class="">物料名称</view>
				<view class="">数量+单位</view>
			</view>
		</uni-card>
		<image src="../../../../static/gld/tianjia.png" mode="" class="icon_tianjia" @click="goAddInWare"></image>
	</view>


</template>

<script>
	export default {
		data() {
			return {
				loading: false,
				numbers:'',//单号，
				date:''//到货时间
			}
		},
		onLoad() {},
		methods: {
			// 跳转创建入库单
			goAddInWare(){
				uni.navigateTo({
					url:'/pages/work/gld/in/addInWare'
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.input_search {
		width: calc(100% - 56rpx);
		margin: 20rpx auto;
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
		width: calc(100% - 56rpx);
		margin: 30rpx auto 0 auto;
		justify-content: space-between;

		view {
			text-align: center;
			line-height: 60rpx;
			width: 40%;
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
</style>