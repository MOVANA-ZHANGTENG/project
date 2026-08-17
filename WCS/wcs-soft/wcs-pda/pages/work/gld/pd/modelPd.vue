<template>
	<view class="">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-model="palletCode" placeholder="托盘号" />
			<button class="button" style="width:90%;margin-left:5%;margin-top: 8px; " type="primary"
				@click="getInDetailList()">查询</button>
		</view>

		<uni-card style="width:100%;margin-left: 0;">

			<view class="cardContent">
				<view class="input_search_">
					<view class="input_search_text">金型编码：</view>
					<uni-easyinput type="text" placeholder="金型编码" v-model="nowModelCode" />
				</view>
				<view class="input_search_">
					<view class="input_search_text">组合编码：</view>
					<span>{{nowDetail.groupCode}}</span>
				</view>
				<view>
					<span>部品名：</span><span>{{nowDetail.departName}}</span>
				</view>

				<view class="input_search_">
					<view class="input_search_text">部品号：</view>
					<span v-for="code in nowDetail.list">{{code}};</span>
				</view>





				<view class="operateBtn">
					<button :disabled="nowDetail.metalModeId==null" class="button" style="width:90%;margin-left:5%; "
						size="mini" type="primary" @click="nowDetail.has=1;commit(nowDetail)">确定</button>

				</view>




			</view>

		</uni-card>

		<uni-section class="mb-10" title="库存" sub-title="以下为该托盘所有库存"></uni-section>
		<uni-card v-for="item in inDetailList">

			<view class="cardContent" v-if="item.pdDetailId>0">
				<view class="input_search_">
					<view class="input_search_text">金型编码：</view>
					<span>{{item.code}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">组合编码：</view>
					<span>{{item.groupCode}}</span>
				</view>
				<view>
					<span>部品名：</span><span>{{item.departName}}</span>
				</view>

				<view class="input_search_">
					<view class="input_search_text">部品号：</view>
					<span v-for="code in item.list">{{code}};</span>
				</view>

				<!-- <view class="input_search_">
					<view class="input_search_text">盘点数量号：</view>
					<uni-easyinput type="text" placeholder="请输入盘点数量" v-model="item.pdQuantity" />
				</view> -->



				<view class="operateBtn">
					<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
						@click="item.has=0;commit(item)">无此金型</button>

				</view>



				<view class="">
					<view class="btmTxet">待盘点</view>
				</view>
			</view>
			<view class="cardContent" v-else>

				<view class="input_search_">
					<view class="input_search_text">物料编码：</view>
					<span>{{item.itemCode}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">物料名称：</view>
					<span>{{item.itemName}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">批次：</view>
					<span>{{item.batchNo}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">数量：</view>
					<span>{{item.quantity}}</span>
				</view>

				<view class="">
					<view class="btmTxet">已盘点</view>
				</view>

			</view>

			</uni-row>
		</uni-card>

	</view>
</template>

<script>
	import request from '@/utils/request';
	export default {
		data() {
			return {
				palletCode: '',
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
					if (this.inDetailList[i].code == newNum) {
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
			getInDetailList() {
				var that = this;
				that.inMasterList = [];
				this.nowDetail = {};
				that.loading = true;
				request({
					url: "/wcs-inventory/inventory/findPdModelByPalletCode",
					method: 'GET',
					data: {
						palletCode: that.palletCode
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {

						that.inDetailList = res.data;
						console.log(that.inDetailList, "that.inDetailList");
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
				// if (row.id == null) {
				// 	return;
				// }
				// if (row.userCardNo == null || row.userCardNo == "") {
				// 	this.$modal.msgError("请扫描工卡");
				// 	return;
				// }
				// if (row.pdQuantity == null || row.pdQuantity == "") {
				// 	this.$modal.msgError("请输入盘点数量");
				// 	return;
				// }
				that.loading = true;
				request({
					url: "/wcs-inventory/PdDetail/commitModel",
					method: "get",
					params: {
						pdDetailId: row.pdDetailId,
						palletCode: row.palletCode,
						modelCode: row.code,
						has: row.has,
					},
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.play(true);
						uni.showToast({
							title: '盘点成功',
							icon: 'success'
						})
						this.nowDetail = {};
						this.nowModelCode = "";
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
</style>