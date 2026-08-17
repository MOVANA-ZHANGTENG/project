<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-model="palletCode" placeholder="请扫描托盘码" />
			<!-- <uni-easyinput type="text" v-model="modelCode" placeholder="请扫描物料编码" /> -->


		</view>
		<uni-card>
			<div class="nameValue">
				<div class="name">当前托盘号:</div>
				{{nowPalletCode}}
			</div>
			<div class="nameValue">
				<div class="name">任务数：:</div>
				{{pickTasks.length}}
			</div>
		</uni-card>
		<view class="" style="padding-left: 28rpx;padding-top: 30rpx;">拣货任务</view>
		<uni-card v-for="task in pickTasks">
			<div class="nameValue">
				<div class="name">出库单号:</div>
				{{task.billNo}}
			</div>
			<div class="nameValue">
				<div class="name">需求数:</div>
				{{task.allotQuantity-task.pickQuantity}}
			</div>
			<div class="nameValue">
				<div class="name">物料编码:</div>
				{{task.itemCode}}
			</div>
			<div class="nameValue">
				<div class="name">物料名称:</div>
				{{task.itemName}}
			</div>

			<!-- <view class="newTray">扫描物料编码</view> -->


			<view class="operateBtn_">
				<view class="operateBtn_Num">扫描数量
					<uni-easyinput type="text" v-model="task.quantity" placeholder="" />

					<!-- <input type="number" v-model="task.quantity" class="input" />/ -->
					/{{task.allotQuantity-task.pickQuantity}}
				</view>
				<view class="" @click="commit(task.pickTaskId,task.quantity)">拣货确认</view>
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
				palletCode: '',
				nowPalletCode: '',
				pickTasks: []
			}
		},
		watch: {
			palletCode(newNum, oldNum) {
				if (newNum != null && newNum.length == 8) {
					this.nowPalletCode = newNum;
					this.palletCode = "";
				}
			},
			nowPalletCode(newNum, oldNum) {
				if (newNum != null && newNum.length == 8) {
					this.getList();
				}
			},
		},
		onLoad() {

		},
		methods: {
			getList() {
				var params = {
					palletCode: this.nowPalletCode
				};
				this.loading = true;
				request({
					url: "/wcs-out/PickTask/findByPalletCode",
					method: "get",
					params: params,
				}).then((response) => {
					this.loading = false;
					if (response.code == 200) {
						this.pickTasks = response.data;
					} else {
						this.$modal.msgError(response.msg);
					}
				});
			},

			commit(pickTaskId, quantity) {
				if (pickTaskId == null) {
					return;
				}
				if (quantity == null || quantity == 0) {
					this.$modal.msgError("请输入拣货数量");
					return;
				}
				// if (userCardNo == null || userCardNo == "") {
				// 	this.$modal.msgError("请扫描工卡");
				// 	return;
				// }
				this.loading = true;
				request({
					url: "/wcs-out/PickTask/commit",
					method: "get",
					params: {
						pickTaskId: pickTaskId,
						quantity: quantity,

					},
				}).then((response) => {
					this.loading = false;
					if (response.code == 200) {
						this.getList();
					} else {
						this.$modal.msgError(response.msg);
					}
				});
			},

		}
	}
</script>

<style>
	.newTray {
		width: calc(100% - 56rpx);
		margin: 30rpx auto;
		background-color: #1888cc;
		border-radius: 12rpx;
		color: white;
		text-align: center;
		line-height: 80rpx;
	}

	.input {
		background-color: #fff;
		border-radius: 12rpx;
		border: solid 1rpx #333;
		width: 100rpx;
		height: 70rpx;
		margin: 0 20rpx;
	}

	.operateBtn_Num {
		display: flex;
		align-items: center;
	}

	.operateBtn_ {
		display: flex;
		width: 100%;
		margin: 30rpx auto 0 auto;
		justify-content: space-between;

		view {
			text-align: center;
			line-height: 60rpx;
		}
	}

	.operateBtn_ view:last-child {
		width: 200rpx;
		background-color: #1888cc;
		border-radius: 12rpx;
		color: white;
		text-align: center;
		line-height: 80rpx;
	}

	.nameValue {
		margin: 5px;

		.name {
			width: 75px;
			text-align: left;
			display: inline-block;
		}
	}
</style>