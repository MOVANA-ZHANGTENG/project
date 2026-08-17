<template>
	<view class="">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-model="palletCode" placeholder="托盘号" />
			<button class="button" style="width:90%;margin-left:5%;margin-top: 8px; " type="primary"
				@click="getInDetailList()">查询</button>
		</view>
		<uni-card>
			<view class="cardContent">
				<view class="input_search_">
					<view class="input_search_text">金型编码：</view>
					<uni-easyinput type="text" placeholder="金型编码" v-model="nowModelCode" />
				</view>
				<view class="input_search_">
					<view class="input_search_text">单号：</view>
					<span>{{nowDetail.billNo}}</span>
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
					<view class="input_search_text">员工卡号：</view>
					<uni-easyinput :disabled="nowDetail.id==null" type="text" placeholder="请输入员工卡号"
						v-model="nowDetail.userCardNo" />
				</view>
				<!-- 	<view class="input_search_">
					<view class="input_search_text">托盘号：</view>
					<uni-easyinput :disabled="nowDetail.id==null" type="text" placeholder="请输入托盘号"
						v-model="nowDetail.groupPalletCode" />
				</view>
				 -->



				<view class="operateBtn">
					<button class="button" :disabled="nowDetail.id==null" style="width:90%;margin-left:5%; "
						type="primary" @click="commit(nowDetail)">分拣</button>

				</view>
			</view>

		</uni-card>
		<uni-card v-for="item in inDetailList">

			<view class="cardContent" v-if="item.palletCode !=null && item.palletCode != ''">
				<view class="input_search_">
					<view class="input_search_text">单号：</view>
					<span>{{item.billNo}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">组合编码：</view>
					<span>{{item.groupCode}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">金型编码：</view>
					<span>{{item.modelCode}}</span>
				</view>
				<view class="input_search_">
					<view class="input_search_text">部品名：</view>
					<span>{{item.departName}}</span>
				</view>


				<view class="input_search_">
					<view class="input_search_text">部品号：</view>
					<span v-for="code in item.list">{{code}};</span>
				</view>
				<view class="">
					<view class="btmTxet">待分拣</view>
				</view>
			</view>
			<view class="cardContent" v-else>
				<!-- <view class="input_search_">
					<view class="input_search_text">lot号：</view>
					<uni-easyinput type="text" placeholder="请输入托盘号" v-model="item.palletCode" />
				</view> -->
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
				that.loading = true;
				request({
					url: "/wcs-inventory/MetalModelBillDetail/findPickByPalletCode",
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
				if (row.id == null) {
					return;
				}
				if (row.userCardNo == null || row.userCardNo == "") {
					this.$modal.msgError("请扫描工卡");
					return;
				}
				that.loading = true;
				request({
					url: "/wcs-inventory/MetalModelBillDetail/commit",
					method: "get",
					params: {
						id: row.id,
						userCardNo: row.userCardNo
					},
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						that.play(true);
						uni.showToast({
							title: '分拣成功',
							icon: 'error'
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
</style>