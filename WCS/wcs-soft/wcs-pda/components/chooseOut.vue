<template>
	<view>
		<tanchu :show="show" @change="change" :custom="true" :mask-click="false">
			<view class="nicheng_content">
				<view class="tan_header">请您选择下架口
				<u-icon size="20" name="close" class="icon" @click="cancel"></u-icon>
				</view>
				<view class="chooseout">
					<view class="" @click="chooseOut(0)">左侧出口</view>
					<view class="" @click="chooseOut(1)">右侧出口</view>
				</view>
			</view>
		</tanchu>
	</view>
</template>

<script>
	export default {
		data() {
			return {}
		},
		props: {
			show: {
				type: Boolean,
				default: false
			},
		},
		onLoad() {

		},
		watch: {
			show(newValue) {
				if (newValue == true) {
					this.togglePopup('center', 'tip');
				}
			},
		},
		mounted() {
			// this.haibao();
		},
		methods: {
			change() {},
			cancel() {
				this.$emit("colseBtn", {
					show: false
				})
			},
			togglePopup(type, open) {
				switch (type) {
					case 'top':
						break

					case 'bottom':
						break
					case 'center':
						break
				}
				// this.type = type 
				if (open === 'tip') {
					// this.show = true
				} else {
					this.$refs[open].open()
				}
			},
			chooseOut(val) {
				this.$emit('chooseOut', {
					states: 1,
					outState:val,
				})
			},
		}
	}
</script>

<style lang="scss">
	.tan_header {
		margin: 30rpx 0;
		font-size: 30rpx;
		font-weight: 700;
		color: #333333;
		text-align: center;
		padding: 30rpx 0;
		position: relative;
	}
	.icon{
		position: absolute;
		right: 10rpx;
		top: 34rpx;
	}
	.nicheng_content {
		width: 85%;
		margin: 0 auto;
		border-radius: 10rpx;
		background-color: #fff;
	}
	.chooseout{
		width: 80%;
		margin: auto;
		display: flex;
		justify-content: space-between;
		padding-bottom: 40rpx;
		view{
			border: #333333 solid 1rpx;
			width: 140rpx;
			padding: 0 30rpx;
			height: 200rpx;
			text-align: center;
			padding-top: 10%;
		}
	}
</style>