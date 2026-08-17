<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<uni-card>
			<uni-easyinput type="text" v-model="params.code" placeholder="请输入金型编码" />
			<uni-easyinput type="text" v-model="params.groupCode" placeholder="请输入金型组合编码" />
			<uni-easyinput type="text" v-model="params.palletCode" placeholder="托盘号" />
			<uni-easyinput type="text" v-model="params.departCode" placeholder="部品号" />

			<uni-row style="margin-top:10px" class="demo-uni-row">
				<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
					<button class="button" style="width:90%;margin-left:5%; " size="mini" type="info"
						@click="clearParam()">重置</button>
				</uni-col>
				<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
					<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
						@click="findPallet()">查询</button>
				</uni-col>
			</uni-row>

		</uni-card>

		<uni-card v-for="item in list">
			<uni-row style="margin-top:10px" class="demo-uni-row">
				<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
					<view style="margin-top: 0px;">
						<span>托盘号：</span><span>{{item.palletCode}}</span>
					</view>
					<view style="margin-top: 0px;">
						<span>货位号：：</span><span>{{item.palletCode}}</span>
					</view>
				</uni-col>
				<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
					<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
						@click="open(item)">下架</button>
				</uni-col>
			</uni-row>

			<uni-table border stripe emptyText="暂无更多数据">
				<!-- 表头行 -->
				<uni-tr>
					<uni-th align="center">金型编码</uni-th>
					<uni-th align="center">部品号</uni-th>
				</uni-tr>
				<!-- 表格数据行 -->
				<uni-tr v-for="model in item.list">
					<uni-td align="center">{{model.code}}</uni-td>
					<uni-td align="center"><span v-for="code in model.list">{{ code }}、</span></uni-td>

				</uni-tr>


			</uni-table>
		</uni-card>

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
						<uni-td align="center">
							<span v-for="code in model.list">{{ code }}、</span>
						</uni-td>
					</uni-tr>
				</uni-table>
				<view class="chooseout">
					<view class="" @click="startDown(0)">左侧出口</view>
					<view class="" @click="startDown(1)">右侧出口</view>
				</view>
				<button @click="close">关闭</button>


			</uni-card>

		</uni-popup>

		<!-- 进行下架组件 -->
		<chooseOut :show="chooseOutShow" @colseBtn="colseBtn" @chooseOut="chooseOut"></chooseOut>
	</view>


</template>

<script>
	import request from '../../../../utils/request';
	export default {
		data() {
			return {
				palletInfo: {},
				loading: false,
				params: {},
				code: '', //金型编码
				departCode: '', //部品号
				palletCode: '', //托盘号
				groupCode: '', //金型组合编码
				cellCode: '', //货位号
				chooseOutShow: false, //是否显示下架弹框
				outState: '', //选择的左侧还是右侧  0-左 1-右
				list: [],
				inputIndex: '',
				dataObj: {},
				toCellCode: ''

			}
		},
		onLoad() {
			this.findPallet();
			this.findPallet();
		},
		methods: {
			clearParam() {
				this.params = {};
			},

			//下架模态框
			open(palletInfo) {
				this.palletInfo = palletInfo;
				this.$refs.popup.open('top')
			},
			//下架模态框
			close() {
				this.$refs.popup.close()
			},
			findPallet() {
				var that = this;
				that.loading = true;

				request({
					url: '/wcs-base/MetalMode/findPallet',
					method: 'get',
					params: this.params
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						this.list = res.rows;
					} else {
						uni.showToast({
							title: res.msg,
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
	.resrtting {
		background-color: transparent;
		color: #333333;
		border: solid 1rpx #333333;
		text-align: center;
		line-height: 80rpx;
		width: 40%;
		height: 80rpx;
		border-radius: 12rpx;
	}

	.operateBtn_ {
		display: flex;
		width: calc(100% - 56rpx);
		margin: 30rpx auto 0 auto;
		justify-content: space-between;
		align-items: center;
	}

	.input_search {
		width: calc(100% - 56rpx);
		margin: 20rpx auto;
	}

	.newTray {
		width: 40%;
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