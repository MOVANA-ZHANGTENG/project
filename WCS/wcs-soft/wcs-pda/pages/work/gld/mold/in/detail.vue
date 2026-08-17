<template>
	<view class="">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view class="" style="font-weight: 700;padding-left: 28rpx;">单号：{{billNo}}</view>
		<uni-card>
			<view class="cardContent">
				<view class="input_search_">
					<view class="input_search_text">金型编码：</view>
					<uni-easyinput type="text" placeholder="金型编码" v-model="nowModelCode" />
				</view>
				<view class="input_search_">
					<view class="input_search_text">组合编码：</view>
					<span>{{nowDetail.groupCode}}</span>
				</view>

				<view class="input_search_">
					<view class="input_search_text">部品名：</view>
					<span>{{nowDetail.departName}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">部品号：</view>
					<span v-for="code in nowDetail.list">{{code}};</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">托盘号：</view>
					<uni-easyinput :disabled="nowDetail.id==null" type="text" placeholder="请输入托盘号"
						v-model="nowDetail.groupPalletCode" />
				</view>
				<view class="input_search_">
					<view class="input_search_text">员工卡号：</view>
					<uni-easyinput :disabled="nowDetail.id==null" type="text" placeholder="请输入员工卡号"
						v-model="nowDetail.userCardNo" />
				</view>



				<view class="operateBtn">
					<button class="button" :disabled="nowDetail.id==null" style="width:90%;margin-left:5%; " size="mini"
						type="primary" @click="commit(nowDetail)">提交</button>

				</view>
			</view>

		</uni-card>
		<uni-card v-for="item in inDetailList">

			<view class="cardContent" v-if="item.state==1">
				<view>
					<span>组合编码：</span><span>{{item.groupCode}}</span>
				</view>
				<view>
					<span>金型编码：</span><span>{{item.modelCode}}</span>
				</view>
				<view>
					<span>部品名：</span><span>{{item.departName}}</span>
				</view>

				<view class="input_search_">
					<view class="input_search_text">部品号：</view>
					<span v-for="code in item.list">{{code}};</span>
				</view>
				<view class="">
					<view class="btmTxet">本金型已组盘到{{item.palletCode}}</view>
				</view>
			</view>
			<view class="cardContent" v-else>
				<uni-row class="demo-uni-row">
					<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
						<view class="input_search_">
							<view class="input_search_text">组合编码：</view>
							<span>{{item.groupCode}}</span>
						</view>

						<view class="input_search_">
							<view class="input_search_text">金型编码：</view>
							<span>{{item.modelCode}}</span>
						</view>
						<view class="input_search_">
							<view class="input_search_text">部品名号：</view>
							<span>{{item.departName}}</span>
						</view>
						<view class="input_search_">
							<view class="input_search_text">部品号：</view>
							<span v-for="code in item.list">{{code}};</span>
						</view>

						<view class="">
							<view class="btmTxet">待组盘</view>
						</view>
					</uni-col>
					<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
						<button :disabled="item.groupCode==null || item.groupCode==''" class="button"
							tyle="width:90%;margin-top:120px; " size="mini" type="primary"
							@click="getGroupMetalByCode(item.groupCode)">查看同组金型模具</button>
					</uni-col>
				</uni-row>



				<!-- <view class="input_search_">
					<view class="input_search_text">lot号：</view>
					<uni-easyinput type="text" placeholder="请输入托盘号" v-model="item.palletCode" />
				</view> -->



				<!-- <view class="operateBtn">
					<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
						@click="commit(item)">提交</button>

				</view> -->
			</view>

		</uni-card>

		<uni-popup ref="popup" :mask-click="false">
			<uni-card v-for="pallet in groupPalletInfo">
				<view class="input_search_">
					<view class="input_search_text">托盘号：</view>
					<span>{{pallet.palletCode}}</span>
				</view>

				<view class="input_search_">
					<view class="input_search_text">货位号：</view>
					<span v-if="pallet.cellCode==null">暂存区</span>
					<span v-else-if="pallet.cellCode==''">暂存区</span>
					<span v-else-if="pallet.cellCode=='000'">暂存区</span>
					<span v-else-if="pallet.cellCode=='1102'">左侧入库口</span>
					<span v-else-if="pallet.cellCode=='1100'">右侧入库口</span>
					<span v-else>{{pallet.cellCode}}</span>
				</view>
				<uni-table border stripe emptyText="暂无更多数据">
					<uni-tr>
						<uni-th align="center">金型编码/组合编码/部品名</uni-th>


					</uni-tr>

					<uni-tr v-for="detail in pallet.list">
						<uni-td>{{detail.code}}/{{detail.groupCode}}/{{detail.departName}}</uni-td>

					</uni-tr>
				</uni-table>
				<view class="chooseout">

					<button
						:disabled="pallet.cellCode==null || pallet.cellCode=='' || pallet.cellCode=='1102' || pallet.cellCode=='1100'"
						type="primary" @click="palletIsTask(pallet.palletCode,0)">左侧出口下架</button>
					<button
						:disabled="pallet.cellCode==null || pallet.cellCode=='' || pallet.cellCode=='1102' || pallet.cellCode=='1100'"
						type="primary" @click="palletIsTask(pallet.palletCode,1)">右侧出口下架</button>
				</view>

				<button @click="close">关闭</button>
			</uni-card>
		</uni-popup>
	</view>
</template>

<script>
	import request from '@/utils/request';
	export default {
		data() {
			return {
				groupPalletInfo: [],
				nowModelCode: '',
				nowDetail: {},
				loading: false,
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
		watch: {
			//当nowModelCode发生变化时， 触发这个回调函数
			nowModelCode(newNum, oldNum) {
				for (var i = 0; i < this.inDetailList.length; i++) {
					if (this.inDetailList[i].modelCode == newNum) {
						this.nowDetail = this.inDetailList[i];
					}
				}
			},
		},
		onLoad(option) {
			this.billNo = option.billNo;
			this.getInDetailList();
		},
		methods: {
			//下架模态框
			open() {
				this.$refs.popup.open('top')
			},
			//下架模态框
			close() {
				this.$refs.popup.close()
			},
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
			palletIsTask(palletCode, outCode) {
				var that = this;
				that.loading = true;

				debugger
				request({
					url: '/wcs-wcs/taskInfo/startTaskPallet',
					method: 'get',
					params: {
						palletCode: palletCode,
						outCode: outCode
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						this.play(true);
						uni.showToast({
							title: "下架任务发送成功",
							icon: 'success'
						})
						this.close();
					} else {
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})

			},

			//根据金型编码获取列表
			getGroupMetalByCode(groupCode) {
				var that = this;
				that.loading = true;

				debugger
				request({
					url: '/wcs-base/MetalMode/getPalletInfoByGroupCode',
					method: 'get',
					params: {
						groupCode: groupCode
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						this.groupPalletInfo = res.data;
						if (this.groupPalletInfo.length == 0) {
							uni.showToast({
								title: "同组金型模具均不未组盘，无需合托",
								icon: 'error'
							})
						} else {
							this.open();
						}

					} else {
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			},
			getInDetailList() {
				var that = this;
				that.inMasterList = [];
				that.loading = true;
				request({
					url: '/wcs-inventory/MetalModelBill/findByBillNo',
					method: 'GET',
					data: {
						billNo: that.billNo
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.inDetailList = res.data.list;

					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			},
			commit(row) {
				var that = this;
				if (row.groupPalletCode == null || row.groupPalletCode == "") {
					this.$modal.msgError("请输入托盘号");
					return;
				}
				if (row.userCardNo == null || row.userCardNo == "") {
					this.$modal.msgError("请扫描工卡");
					return;
				}
				that.loading = true;
				request({
					url: "/wcs-inventory/MetalModelBillDetail/group",
					method: "get",
					params: {
						id: row.id,
						palletCode: row.groupPalletCode,
						userCardNo: row.userCardNo,
					},
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.play(true);
						uni.showToast({
							title: '组盘成功',
							icon: 'error'
						})
						this.nowModelCode = "";
						this.nowDetail = {};
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
		align-items: center;
		margin-top: 20rpx;
	}

	.input_search_text {
		width: 140rpx;
		text-align-last: end;
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