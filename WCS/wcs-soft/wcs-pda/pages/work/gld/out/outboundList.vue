<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view class="input_search">
			<uni-easyinput type="text" v-model="numbers" placeholder="请输入单号" />
			<uni-data-select :localdata="typelist" v-model="type" @change="onChange" class=""
				placeholder="请选择入库单类型"></uni-data-select>
			<view class="keywords_cont">
				<view class="example-body content">
					<uni-datetime-picker placeholder="请选择计划出库时间" v-model="date" type="date" @change="maskClick" />
				</view>
			</view>
			<view class="operateBtn">
				<view class="" style="background-color:transparent;color: #333333;border: solid 1rpx #333333;">重置</view>
				<view class="">查询</view>
			</view>
		</view>
		<view class="" style="padding-left: 30rpx;">内容</view>
		<uni-card>
			<view class="cardContent">
				<view>
					<span>单号：</span><span>IN-231106-1</span>
				</view>
				<view>
					<span>创建人：</span><span>李小刚</span>
				</view>
				<view>
					<span>状态：</span><span>等待中</span>
				</view>
				<view>
					<span>审核状态：</span><span>已审核</span>
				</view>
				<view>
					<span>预计出库时间：</span><span>2023-11-16 15:00:00</span>
				</view>
				<view style="padding-bottom: 20rpx;border-bottom: solid 1rpx #ccc;">
					<span>创建时间：</span><span>2023-11-06 18:30:49</span>
				</view>
				<view class="operateBtn_">
					<view class="">物料名称</view>
					<view class="">出库数量+单位</view>
				</view>
			</view>
		</uni-card>
		<image src="/static/gld/tianjia.png" mode="" class="icon_tianjia" @click="goAddInWare"></image>
	</view>


</template>

<script>
	export default {
		data() {
			return {
				loading: false,
				numbers: '', //单号，
				date: '', //到货时间
				typelist: [{
					value: 0,
					text: '正常'
				}, {
					value: 1,
					text: '破损'
				}],
				type: '' //下拉框
			}
		},
		onLoad() {},
		methods: {
			maskClick(e) {
				console.log('maskClick事件:', e);
			},
			// 跳转创建出库单
			goAddInWare() {
				uni.navigateTo({
					url: '/pages/work/gld/out/addOutWare'
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	::v-deep .uni-stat__select{
		background-color: #fff;
	}
	::v-deep .uni-date__x-input {
		color: #333;
		font-size: 28rpx;
	}

	::v-deep .uni-easyinput__placeholder-class {
		color: #333;
		font-size: 28rpx;
	}

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

	.icon_tianjia {
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