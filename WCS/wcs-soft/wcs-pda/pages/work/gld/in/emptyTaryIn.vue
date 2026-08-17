<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput style="margin-top: 20px; " type="text" v-model="palletCode" placeholder="请扫描全新托盘码" />
			<uni-data-checkbox style="margin-top: 20px; " placeholder="请选择托盘类型" v-model="palletType"
				:localdata="palletTypes"></uni-data-checkbox>

		</view>

		<uni-row style="margin-top: 20px; " class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="info"
					@click="palletCode='';palletType=null; ">清空</button>
			</uni-col>
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="primary" @click="commit">提交</button>
			</uni-col>
		</uni-row>
	</view>


</template>

<script>
	import request from '@/utils/request';
	export default {
		data() {
			return {
				loading: false,
				palletCode: '',
				palletType: null,
				palletTypes: [{
					value: 0,
					text: 'fasdf'
				}],
			}
		},
		onLoad() {
			this.gettypes();
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
			gettypes() {
				var that = this;
				that.loading = true;
				request({
					url: '/wcs-base/palletType/list',
					method: 'get',
					params: {

					}
				}).then(response => {
					that.loading = false;
					if (response.code == 200) {
						var rows = response.rows;
						that.palletTypes = [];
						for (var i = 0; i < rows.length; i++) {
							that.palletTypes.push({
								value: rows[i].code,
								text: rows[i].name
							});
						}

					} else {

						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});

			},
			commit() {
				var that = this;

				if (that.palletCode == null || that.palletCode == "") {
					uni.showToast({
						title: "请扫描托盘号后再提交",
						icon: 'error'
					})
					return;
				}
				if (that.palletType == null || that.palletType == "") {
					uni.showToast({
						title: "请选择托盘类型后再提交",
						icon: 'error'
					})
					return;
				}
				that.loading = true;
				request({
					url: '/wcs-inventory/palletInfo/insert',
					method: 'get',
					params: {
						palletCode: this.palletCode,
						palletType: this.palletType

					}
				}).then(response => {
					that.loading = false;
					if (response.code == 200) {
						uni.showToast({
							title: "新增成功",
							icon: 'success'
						})
						that.palletCode = '';


					} else {

						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});

			},
		}
	}
</script>

<style lang="scss" scoped>
	.input_search {
		width: calc(100% - 56rpx);
		margin: 20rpx auto;

		.input_search_ {
			display: flex;
			justify-content: space-between;
			align-items: center;
		}
	}

	.input_search_text {
		width: 150rpx;
		text-align-last: end;
	}

	::v-deep .uni-easyinput__content {
		width: 90% !important;
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
		width: calc(100% - 120rpx);
		margin: 80rpx auto 0 auto;
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