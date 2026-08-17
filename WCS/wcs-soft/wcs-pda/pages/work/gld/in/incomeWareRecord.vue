<template>
	<view class="">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view class="" style="padding-left: 30rpx;line-height: 80rpx;">基本筛选</view>
		<view class="keywords_search">
			<view class="keywords_cont">
				<view class="keywords_text">收货日期选择</view>
				<view class="example-body content">
					<uni-datetime-picker v-model="range" type="daterange" @change="maskClick" />
				</view>
			</view>
			<view class="keywords_cont">
				<view class="keywords_text">物料名称</view>
				<view class="content">
					<input type="text" placeholder="请输入物料名称" class="search_input" placeholder-class="inputplaceholder">
				</view>
			</view>
			<view class="keywords_cont">
				<view class="keywords_text">物料编码</view>
				<view class="content">
					<input type="text" placeholder="请输入物料编码" class="search_input" placeholder-class="inputplaceholder">
				</view>
			</view>
			<view class="keywords_cont">
				<view class="keywords_text">订单编号</view>
				<view class="content">
					<input type="text" placeholder="请输入订单编号" class="search_input" placeholder-class="inputplaceholder">
				</view>
			</view>
		</view>
		<view class="" style="padding-left: 30rpx;line-height: 60rpx;">内容</view>
		<uni-card v-for="item in 2">
			<view class="cardContent">
				<view class="cardContentleft">
					<view>
						<span>订单编号：</span><span>IN-231106-1</span>
					</view>
					<view>
						<span>物料名称：</span><span>PL金型模具</span>
					</view>
					<view>
						<span>商品编码：</span><span>5135156121212</span>
					</view>
					<view class="operateBtn_">
						<view>
							<span>货位编码：</span><span>774545</span>
						</view>
						<view>
							<span>lot号：</span><span>005</span>
						</view>
					</view>
					<view class="operateBtn_">
						<view>
							<span>收货数量：</span><span>20件</span>
						</view>
						<view>
							<span>收货人：</span><span>管理员</span>
						</view>
					</view>
					<view>
						<span>收货时间：</span><span>11-06 18:30</span>
					</view>
				</view>
				<view class="cardContentright" @click="goTakeMoney">验收 </view>
			</view>
		</uni-card>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				loading: false,
				range: ['', ''], //日期
			}
		},
		onLoad() {

		},
		methods: {
			maskClick(e) {
				console.log('maskClick事件:', e);
			},
		}
	}
</script>

<style lang="scss" scoped>
	.operateBtn_ {
		display: flex;
		justify-content: space-between;
		width: 100%;

		view {
			width: 50%;
			text-align: left;
		}
	}

	.keywords_search {
		width: calc(100% - 60rpx);
		margin: 0 auto;
	}

	.keywords_cont {
		display: flex;
		align-items: center;
		line-height: 80rpx;
		background-color: #fff;
		margin-bottom: 10rpx;
		box-shadow: 0rpx 0rpx 12rpx 2rpx rgba(74, 188, 132, 0.1);
		border-radius: 12rpx;
	}

	.keywords_text {
		width: 28%;
		font-size: 28rpx;
		padding-left:20rpx;
	}

	.inputplaceholder {
		font-size: 28rpx;
	}

	.content {
		width: 500rpx;
	}

	::v-deep .uni-date-x--border {
		border: none !important;
	}

	.cardContent {
		position: relative;
	}

	.cardContentright {
		position: absolute;
		right: 20rpx;
		top: 0;
		color: #fff;
		background-color: #1888cc;
		line-height: 60rpx;
		height: 60rpx;
		text-align: center;
		width: 120rpx;
		border-radius: 12rpx;
	}
</style>