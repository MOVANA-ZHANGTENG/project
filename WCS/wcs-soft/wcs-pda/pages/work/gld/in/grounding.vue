<template>
	<view>
		<view class="shangjia">
			<view class="" @click="createInTask(0)">
				<view class="">
					任务数：{{taskCount.left}}
				</view>
				<view class="">
					左侧上架
				</view>
			</view>
			<view class="" @click="createInTask(1)">
				<view class="">
					任务数：{{taskCount.right}}
				</view>
				<view class="">
					右侧上架
				</view>
			</view>

		</view>
	</view>


</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				fromCellCode: '',
				taskCount: {},
			}
		},
		watch: {

		},
		onLoad() {
			this.getTaskCount();

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
			createInTask(e) {
				var that = this;
				if (e == 0) {
					that.fromCellCode = "1102"
				}
				if (e == 1) {
					that.fromCellCode = "1100"
				}
				that.inMasterList = [];
				that.loading = true;
				request({
					url: '/wcs-wcs/taskInfo/creatInTask',
					method: 'GET',
					data: {
						fromCellCode: that.fromCellCode
					}
				}).then(res => {
					that.loading = false;
					if (res.code == 200) {
						this.play(true);
						uni.showToast({
							title: '创建上架任务完成',
							icon: 'error'
						})
					} else {
						this.play(false);
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			},
			getTaskCount() {
				var that = this;
				request({
					url: '/wcs-wcs/taskInfo/taskCount',
					method: 'GET',
					data: {}
				}).then(res => {
					if (res.code == 200) {
						that.taskCount = res.data;
					} else {
						uni.showToast({
							title: res.msg,
							icon: 'error'
						})
					}
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.shangjia {
		width: calc(100% - 200rpx);
		margin: 300rpx auto 0 auto;
		display: flex;
		justify-content: space-between;

		view {
			width: 200rpx;
			height: 300rpx;
			text-align: center;
			line-height: 300rpx;
			background-color: #fff;
			border-radius: 12rpx;
		}
	}
</style>