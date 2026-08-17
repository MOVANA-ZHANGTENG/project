<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-if="!modelCodeFocus" :focus="!modelCodeFocus" v-model="palletCode"
				placeholder="托盘码" />
			<uni-easyinput type="text" :focus="modelCodeFocus" v-model="modelCode" placeholder="金型编码" />


		</view>
		<uni-card style="margin-top: 20px;margin-left: 5%;width: 90%;">



			<uni-row style="margin-top: 20px;margin-left: 20px;" class="demo-uni-row">
				<uni-col v-for="code in modelCodes" :xs="12" :sm="6" :md="4" :lg="3" :xl="1">
					<view style="margin-top: 20px; ">
						<uni-tag :text="code" type="success" />
					</view>

				</uni-col>
			</uni-row>

			<view
				style="font-size: 25px;padding-top: 5px; height: 30px; margin-top:10px;margin-left: 5%;width: 85%;background-color:darkgray;color: white;padding-left: 10px;">
				托盘号：{{palletCode+' '}}
			</view>
		</uni-card>

		<!-- <view class="input_search" v-if="state == 0">
			<view class="tuopan" @click="scanCode">请点击扫描金型编码</view>
			<view class="tuopan">请点击扫描托盘号</view>
		</view>

	 
		<view class="input_search" v-if="state == 1"> 
			<uni-easyinput type="text" v-model="code" placeholder="请输入金型编码 " /> 
			<view class="tuopan">{{palletCode}}</view>
		</view> -->

		<uni-row class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="info"
					@click="palletCode='';modelCode='';modelCodes=[]; modelCodeFocus=false;">清空</button>
			</uni-col>
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="primary"
					@click="modelGroupBox">提交</button>
			</uni-col>
		</uni-row>



	</view>


</template>

<script>
	import request from '@/utils/request';
	export default {
		data() {
			return {
				modelCodeFocus: false,
				palletInfo: {
					list: [{}, {}]
				},
				loading: false,
				state: '', //用来判断是直接选择托盘还是前往组盘    显示不同内容 
				palletCode: '',
				modelCode: '',
				modelCodes: [],
				code: '',
				scanPallentCode: '11',
				scancode: '1'
			}
		},
		watch: {
			modelCode(newValue) {
				var that = this;
				if (newValue != null && newValue.trim() != "" && newValue.trim().length >= 8) {
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
		onLoad(option) {
			//this.state = option.state
			//this.palletCode = option.palletCode
			//this.insertByCodeAndPallent()
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
			addCode(newValue) {
				var that = this;
				if (that.modelCode == null || that.modelCode == "") {
					return;
				}
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
				that.check(that.modelCode, that.palletCode);

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
					url: '/wcs-base/MetalMode/checkPallet',
					method: 'get',
					params: {
						"palletCode": palletCode,
					}
				}).then(response => {
					if (response.code == 200) {
						that.play(true);
						that.nextScanTwoCode();
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
			check(modelCode, palletCode) {
				var that = this;
				request({
					url: '/wcs-base/MetalMode/modelGroupBoxCheck',
					method: 'get',
					params: {
						"modelCode": modelCode,
						"palletCode": palletCode,
					}
				}).then(response => {
					that.nextScanTwoCode();
					if (response.code == 200) {
						if (response.data) {
							that.play(true);
							that.modelCodes.push(modelCode);
						} else {
							that.play(false);
							uni.showToast({
								title: '该托盘不允许放',
								icon: 'error'
							})
						}
					} else {
						that.play(false);
						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});

			},
			clear() {
				this.modelCodeFocus = false;
				this.palletCode = '';
				this.modelCode = '';
				this.modelCodes = [];

			},
			modelGroupBox() {
				var that = this;
				that.loading = true;
				request({
					url: '/wcs-base/MetalMode/modelGroupBox',
					method: 'get',
					params: {
						"list": this.modelCodes,
						"palletCode": this.palletCode,
					}
				}).then(response => {
					that.loading = false;
					that.nextScanTwoCode();
					if (response.code == 200) {
						uni.showToast({
							title: "组盘成功",
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
			// 扫码
			scanCode() {
				var that = this;
				uni.scanCode({
					onlyFromCamera: true,
					success: function(res) {
						console.log('条码类型：' + res.scanType);
						console.log('条码内容：' + res.result);
					}
				});
			},
			//前往组盘 state 1
			insertBycode() {
				console.log(this.code, "this.code");
				let data = {
					code: this.code
				}
				request({
					url: '/wcs-base/MetalMode/insertBycode',
					method: 'Get',
					data: data
				}).then(res => {
					if (res.code == 200) {
						this.code = res.data
						console.log(res, "后端返回信息")
					} else {
						uni.showToast({
							title: '错误信息',
							icon: 'error'
						})
					}
				})
			},
			// 直接使用新托盘 state 2
			insertByCodeAndPallent() {
				console.log(this.code, "this.code");
				let data = {
					code: this.scancode,
					pallentCode: this.scanPallentCode
				}
				request({
					url: '/wcs-base/MetalMode/insertByCodeAndPallent',
					method: 'Get',
					data: data
				}).then(res => {
					if (res.code == 200) {
						this.code = res.data
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

		.tuopan {
			background-color: #fff;
			border: solid 1rpx #333333;
			text-align: center;
			line-height: 80rpx;
			border-radius: 12rpx;
			margin-top: 50rpx;
		}
	}

	.operateBtn {
		position: fixed;
		bottom: 300rpx;
		left: 28rpx;
		display: flex;
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
</style>