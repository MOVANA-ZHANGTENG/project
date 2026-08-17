<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>


		<view class="input_search">
			<uni-easyinput type="text" v-model="code" placeholder="请输入金型编码" />
			<uni-easyinput type="text" v-model="departCode" placeholder="请输入部品号" />
		</view>
		<view class="newTray" @click="searchMetalModel">查询同组金型</view>
		<view class="newTray" @click="insertByCodeAndPallent">前往组盘</view>
		<!-- <view class="" style="padding-left: 28rpx;padding-top: 30rpx;">内容</view> -->
		<uni-card v-for="item in list">

			<view>
				<span>托盘号：</span><span>{{item.palletCode}}</span>
			</view>
			<view>
				<span>货位号：</span><span>{{item.cellCode }}</span>
			</view>
			<uni-table border stripe emptyText="暂无更多数据">
				<!-- 表头行 -->
				<uni-tr>
					<uni-th align="center">金型编码</uni-th>
					<uni-th align="center">部品号</uni-th>
				</uni-tr>
				<!-- 表格数据行 -->
				<uni-tr v-for="model in item.list">
					<uni-td align="center">{{model.code}}</uni-td>
					<uni-td align="center">
						{{ model.list}}
						<span v-for="code in model.list">{{ code }}、</span>

					</uni-td>

				</uni-tr>


			</uni-table>


			<view class="operateBtn">
				<view class="" @click="open(item)">进行下架</view>
				<!-- <view class="" @click="insertBycode">前往组盘</view> -->
			</view>
		</uni-card>
		<!-- <view class="" @click="chooseOut(0)">左侧出口</view>
					<view class="" @click="chooseOut(1)">右侧出口</view> -->
		<!-- 进行下架组件 -->

		<uni-popup ref="popup" :mask-click="false">
			<uni-card>
				<view>
					<span>托盘号：</span><span>{{palletInfo.palletCode}}</span>
				</view>
				<view>
					<span>货位号：</span><span>{{palletInfo.cellCode }}</span>
				</view>
				<uni-table border stripe emptyText="暂无更多数据">
					<!-- 表头行 -->
					<uni-tr>
						<uni-th align="center">金型编码</uni-th>
						<uni-th align="center">部品号</uni-th>
					</uni-tr>
					<!-- 表格数据行 -->
					<uni-tr v-for="model in palletInfo.list">
						<uni-td align="center">{{model.code}}</uni-td>
						<uni-td align="center">{{model.cellCode }}</uni-td>
					</uni-tr>
				</uni-table>
				<view class="chooseout">
					<view class="" @click="startDown(0)">左侧出口</view>
					<view class="" @click="startDown(1)">右侧出口</view>
				</view>
				<button @click="close">关闭</button>


			</uni-card>

		</uni-popup>
		<!-- <chooseOut :show="chooseOutShow" @colseBtn="colseBtn" @chooseOut="chooseOut"></chooseOut> -->
	</view>
</template>

<script>
	import request from '@/utils/request';
	export default {
		data() {
			return {
				loading: false,
				chooseOutShow: false, //是否显示下架弹框
				code: '', //后面获取的code
				list: {},
				outState: '', //选择的左侧还是右侧  0-左 1-右
				departCode: '',
				//进行下架的数据
				palletInfo: {},

			}
		},

		onLoad() {
			// this.getList()
		},
		methods: {
			//下架模态框
			open(palletInfo) {
				this.palletInfo = palletInfo;
				this.$refs.popup.open('top')
			},
			//下架模态框
			close() {
				this.$refs.popup.close()
			},
			// 直接使用新托盘
			insertByCodeAndPallent() {
				uni.navigateTo({
					url: '/pages/work/gld/mold/newTary?state=0'
				})
			},
			// 前往组盘
			insertBycode() {
				uni.navigateTo({
					url: '/pages/work/gld/mold/newTary?state=1&palletCode=' + this.list.palletCode
				})
				console.log(this.list, "+this.list.palletCode");
			},
			// 显示下架弹框
			showChooseOut() {
				this.chooseOutShow = true;
			},
			// 关闭弹框
			colseBtn() {
				this.chooseOutShow = false;
			},
			// 获取自组件点的是左侧出口还是右侧出口   0-左侧  1-右侧
			chooseOut(e) {
				//左侧
				if (e.states == 0) {
					this.outState = e.outState;

				}
				this.chooseOutShow = false;
			},

			//查询按钮
			searchMetalModel() {
				var that = this;
				if (this.code == null || this.code.trim() == "") {
					uni.showToast({
						title: '请输入金型编码',
						icon: 'error'
					})
					return;
				}

				that.getGroupMetalByCode();
			},
			//根据金型编码获取列表
			getGroupMetalByCode() {
				var that = this;
				that.loading = true;
				var data = {
					code: this.code
				}
				debugger
				request({
					url: '/wcs-base/MetalMode/findGroupsByCode',
					method: 'get',
					params: data
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						this.list = res.data;
						if (this.list.length == 0) {
							uni.showToast({
								title: "无此金型数据",
								icon: 'success'
							})
						}

						console.log(res, "后端返回信息")
					} else {
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			},
			//根据部品号获取金型列表
			getGroupMetalByDepartCode() {
				var that = this;
				// that.loading = true;
				var data = {
					departCode: this.departCode
				}
				request({
					url: '/wcs-base/MetalMode/getGroupMetalByDepartCode',
					method: 'get',
					params: data
				}).then(res => {
					if (res.code == 200) {
						this.list = res.data
						console.log(res, "后端返回信息")
					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			},

			//开始下架
			startDown(outCode) {
				var that = this;
				that.loading = true;
				var data = {
					palletCode: this.palletInfo.palletCode,
					outCode: outCode
				}
				request({
					url: '/wcs-wcs/taskInfo/startTaskPallet',
					method: 'get',
					params: data
				}).then(res => {
					that.loading = false;
					this.close();
					if (res.code == 200) {
						uni.showToast({
							title: "下架任务发送成功",
							icon: 'success'
						})
					} else {
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

	.chooseout {

		width: 80%;
		margin: auto;
		margin-top: 10px;
		display: flex;
		justify-content: space-between;
		padding-bottom: 40rpx;

		view {
			border: #FFFFFF;
			width: 140rpx;
			padding: 0 30rpx;
			height: 200rpx;
			text-align: center;
			padding-top: 10%;
			border-radius: 20px;
			background-color: #409EFF;
			color: #FFFFFF;
		}
	}
</style>