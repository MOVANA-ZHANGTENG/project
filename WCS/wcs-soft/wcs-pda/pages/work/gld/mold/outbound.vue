<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>

		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-if="!modelCodeFocus" :focus="!modelCodeFocus" v-model="palletCode"
				placeholder="托盘码" />
			<uni-easyinput type="text" :focus="modelCodeFocus" v-model="modelCode" placeholder="金型编码" />


		</view>

		<uni-row class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<uni-card style="width:90%;margin-left: 5%" title="原托盘金型编码" :isFull="true">
					<view
						style="width:90%;height:auto;padding:3px;background-color:#FFFFFF;margin: 3px;border-radius: 5px;"
						v-for="model in models">
						{{model.code}}
					</view>
				</uni-card>
				<!-- <view class="" style="padding-left: 28rpx;padding-top: 30rpx;">原托盘金型编码</view>
				<view class="moldNum">
					<view
						style="width:90%;height:auto;padding:3px;background-color:#FFFFFF;margin: 3px;border-radius: 5px;"
						v-for="model in models">
						{{model.code}}
					</view>
				</view> -->
			</uni-col>
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<uni-card style="width:90%;margin-left: 5%" title="分拣金型编码" :isFull="true">
					<view
						style="width:90%;height:auto;padding:3px;background-color:#FFFFFF;margin: 3px;border-radius: 5px;"
						v-for="model in modelCodes">
						{{model}}
					</view>
				</uni-card>
			</uni-col>
		</uni-row>

		<uni-row style="margin-top: 10px;" class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="info" @click="clear()">清空</button>
			</uni-col>
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="primary"
					@click="noGroupPallet()">提交</button>
			</uni-col>
		</uni-row>







		<!-- <view class="operateBtn">
			<view class="" style="background-color:#fff;color: #333333;border: solid 1rpx #333333;">清空</view>
			<view class="" @click="">确认出库</view>
		</view> -->
	</view>


</template>

<script>
	import request from '../../../../utils/request';
	export default {
		data() {
			return {
				loading: false,
				modelCodeFocus: false,
				palletCode: '',
				modelCode: '',
				modelCodes: [],
				models: [],
				pickModels: [],
			}
		},
		watch: {
			modelCode(newValue) {
				var that = this;
				if (newValue != null && newValue.trim() != "" && newValue.trim().length >= 3) {
					that.addCode(newValue);
				}
			},
			palletCode(newValue) {
				var that = this;
				if (newValue != null && newValue.trim() != "" && newValue.trim().length >= 3) {
					this.checkPallet(newValue);

				}
			},
		},
		onLoad() {},
		methods: {
			clear() {
				this.modelCodeFocus = false;
				this.palletCode = '';
				this.modelCode = '';
				this.modelCodes = [];
				this.models = [];
				this.pickModels = [];

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
			addCode(newValue) {
				var that = this;
				if (that.modelCode == null || that.modelCode == "") {
					return;
				}
				for (var i = 0; i < that.models.length; i++) {
					var code = that.models[i].code;
					if (code.trim() == newValue.trim()) {
						that.addCode2(newValue);
					}
				}
			},
			addCode2(newValue) {
				var that = this;
				for (var i = 0; i < that.modelCodes.length; i++) {
					var code = that.modelCodes[i];
					if (code.trim() == newValue.trim()) {
						uni.showToast({ //提示信息
							title: '重复扫码', //提示内容
							icon: "fail" //提示图标
						});
						that.play(false);
						that.nextScanTwoCode();
						return;
					}
				}
				that.play(true);
				that.modelCodes.push(newValue);
				that.nextScanTwoCode();
			},
			nextScanTwoCode() {
				var that = this;
				that.modelCode = "";
				that.modelCodeFocus = false;
				that.$nextTick(() => {
					that.modelCodeFocus = true;
				});
			},
			checkPallet(palletCode) {
				var that = this;
				request({
					url: '/wcs-base/MetalMode/getModelsByPalletCode',
					method: 'get',
					params: {
						"palletCode": palletCode,
					}
				}).then(response => {
					if (response.code == 200) {
						if (response.data != null && response.data.length > 0) {
							that.play(true);
							that.nextScanTwoCode();
							that.models = response.data;
						} else {
							that.palletCode = "";
							that.play(false);
							uni.showToast({
								title: "该托盘无金型",
								icon: 'error'
							})
						}
					} else {
						that.palletCode = "";
						that.play(false);
						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});

			},
			noGroupPallet() {
				var that = this;
				request({
					url: '/wcs-base/MetalMode/noGroupPallet',
					method: 'get',
					params: {
						"palletCode": that.palletCode,
						"list": this.modelCodes,
					}
				}).then(response => {
					if (response.code == 200) {
						uni.showToast({
							title: "分拣成功",
							icon: 'success'
						})
						that.play(true);
						that.clear();
					} else {

						that.play(false);
						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});

			},
			//分拣
			fenJian() {
				var that = this;
				var data = {
					code: this.code
				}
				request({
					url: "/wcs-wcs/taskInfo/fenJian",
					methods: 'get',
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

			}
		}
	}
</script>

<style lang="scss" scoped>
	.input_search {
		width: calc(100% - 56rpx);
		margin: 20rpx auto;

		view {
			background-color: #fff;
			border: solid 1rpx #333333;
			text-align: center;
			line-height: 80rpx;
			border-radius: 12rpx;
			margin-top: 50rpx;
		}
	}

	.operateBtn {
		left: 28rpx;
		width: calc(100% - 56rpx);
		margin: 30rpx auto 0 auto;
		justify-content: space-between;

		view {
			background-color: #1888cc;
			border-radius: 12rpx;
			color: white;
			text-align: center;
			line-height: 80rpx;
			width: 40%;
		}
	}

	.moldNum {
		width: calc(100% - 56rpx);
		margin: 20rpx auto;
		background-color: #fff;
		border: solid 1rpx #333333;
		text-align: center;
		// line-height: 80rpx;
		border-radius: 12rpx;
		margin-top: 50rpx;
		padding: 30rpx 0;
	}
</style>