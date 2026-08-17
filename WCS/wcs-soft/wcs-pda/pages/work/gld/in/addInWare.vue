<template>
	<view class="">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view class="" style="padding-left: 28rpx;padding-top: 30rpx;">基础信息</view>
		<uni-data-select :localdata="typelist" v-model="type" @change="onChange" class=""
			placeholder="请选择入库单类型"></uni-data-select>
		<view class="keywords_cont">
			<view class="example-body content">
				<uni-datetime-picker placeholder="请选择计划出库时间" v-model="date" type="date" @change="maskClick" />
			</view>
		</view>
		<view class="uni-textarea">
			<textarea @blur="bindTextAreaBlur" placeholder="如您有需要备注的内容,请在此处填写......" />
		</view>
		<view class="" style="padding-left: 28rpx;padding-top: 30rpx;">入库信息</view>
		<view class="chooseOutCont" @click="showDrawer('showRight')">选择出库内容</view>
		<view class="example-body">
			<uni-drawer ref="showRight" mode="right" :mask-click="false" @change="change($event,'showRight')">
				<view class="scroll-view">
					<scroll-view class="scroll-view-box" scroll-y="true">
						<view class="scrollTop">
							<view class="">筛选</view>
							<view class="close">
								<u-icon size="20" @click="closeDrawer('showRight')" name="close"></u-icon>
							</view>
						</view>
						<view class="input_search">
							<uni-easyinput type="text" placeholder="请输入物料编码或物料名称" />
							<uni-data-select style="width: 100%;" :localdata="outList" v-model="outStatus" @change="onChange" class=""
								placeholder="请选择类型"></uni-data-select>
							<view class="operateBtn">
								<view class="" style="background-color:transparent;color: #333333;border: solid 1rpx #333333;">重置</view>
								<view class="">查询</view>
							</view>
						</view>
						<uni-card v-for="item in 2">
							<view class="cardContent">
								<view class="cardContentleft">
									<view>
										<span>物料编码：</span><span>001</span>
									</view>
									<view>
										<span>物料名称：</span><span>PL金型模具</span>
									</view>
									<view>
										<span>类型：</span><span>原材料</span>
									</view>
									<view>
										<span>库存总数：</span><span>1000</span>
									</view>
								</view>
								<view class="cardContentright" @click="goTakeMoney">选择</view>
							</view>
						</uni-card>
					</scroll-view>
				</view>
			</uni-drawer>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				loading: false,
				type: '', //入库单类型
				typelist: [{
					text: '采购',
					value: 1
				}, {
					text: '采购2',
					value: 2
				}],
				date:'',
				outList:[
					{
						text: '金型模具',
						value: 1
					}, {
						text: '原材料',
						value: 2
					}
				],
				outStatus:''
			}
		},
		methods: {
			onChange(e) {
				console.log('change事件:', e);
			},
			// 文本域失去焦点事件
			bindTextAreaBlur: function(e) {
				console.log(e.detail.value)
			},
			// 打开右侧弹框
			showDrawer(e) {
				let that = this
				this.$refs[e].open()
			},
			// 抽屉状态发生变化触发
			change(e, type) {
				console.log((type === 'showLeft' ? '左窗口' : '右窗口') + (e ? '打开' : '关闭'));
				this[type] = e
			},
			// 关闭窗口
			closeDrawer(e) {
				this.$refs[e].close()
			},
			maskClick(e) {
				console.log('maskClick事件:', e);
			},
		},

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
		width: 120rpx;
		border-radius: 12rpx;
	}
	.keywords_cont{
		width: calc(100% - 56rpx);
		margin: auto;
	}
	.input_search {
		width: calc(100% - 56rpx);
		margin: 20rpx auto;
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
	::v-deep .uni-select {
		border: none !important;
		border-bottom: none !important;
	}

	::v-deep .uni-select__input-placeholder {
		font-size: 30rpx !important;
	}

	::v-deep .uni-date-x--border {
		border: none;
	}

	::v-deep .uni-stat__select {
		background-color: white;
		width: calc(100% - 56rpx);
		margin: 20rpx auto;
	}

	.chooseDate {
		background-color: #fff;
		width: calc(100% - 56rpx);
		margin: 20rpx auto;
		line-height: 70rpx;
		text-align: center;
	}

	.uni-textarea {
		background-color: #fff;
		width: calc(100% - 56rpx);
		margin: 20rpx auto;
		// line-height: 90rpx;
		padding: 20rpx;
		height: 300rpx;
	}

	.chooseOutCont {
		width: 300rpx;
		color: #fff;
		text-align: center;
		line-height: 80rpx;
		background-color: #1888cc;
		border-radius: 12rpx;
		margin: 50rpx auto;
	}
	.scrollTop{
		display: flex;
		justify-content: space-between;
		padding-top: 30rpx;
	}
	.close{
		display: flex;
		align-items: center;
	}
	::v-deep .uni-drawer__content {
		width: 100% !important;
		background-color: #F5F8FB;
	}
	
	::v-deep .uni-drawer {
		height: 100vh !important;
		margin:90rpx auto 0 !important;
	}
	
	.scroll-view {
		/* #ifndef APP-NVUE */
		width: 100%;
		height: 100%;
		/* #endif */
		flex: 1
	}
	
	// 处理抽屉内容滚动
	.scroll-view-box {
		flex: 1;
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
	}
	::v-deep .uni-scroll-view-content	{
		width: calc(100% - 56rpx) !important;
		margin: auto;
	}
</style>