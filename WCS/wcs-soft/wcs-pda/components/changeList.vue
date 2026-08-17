<template>
	<view class="changeList">
		<view class="cont_zhaungtai">
			<view class="list_" v-for="(item,index) in list" :key="item.status" @click="change_list(item.status)">
				<view class="list_item" :class="{change: item.status== aa}">
					{{item.text}}
				</view>
				<view class="" :class="{bottomLine: item.status== aa}"></view>
			</view>
		</view>
	</view>
</template>

<script>
	import list from '../uni_modules/uview-ui/libs/config/props/list'
	export default {
		name: 'changeList',
		props: {
			list: {
				type: Array,
				default: () => {}
			},
			value: {
				type: [String, Number, Object],
				default: 0
			},
		},
		data() {
			return {
				aa: '',
				selectedState: 0
			}
		},
		mounted() {
			this.aa = this.value;
			console.log(this.list, "list");

		},
		watch: {
			value(newValue) {
				this.aa = newValue;
				console.log(newValue, "新值");
			},
			list(newValue) {
				console.log(newValue, "newValue");
			}
		},
		created() {},
		methods: {
			change_list(status) {
				var that = this;
				this.aa = status;
				this.selectedState = 1
				this.$emit("input", status)
			}
		}
	}
</script>
<style>
	/* @charset "UTF-8"; */
	.change {
		color: #1295ed;
	}
	.bottomLine{
		background-color: #1295ed;
		width: 80rpx;
		height: 4rpx;
		margin: auto;
		margin-bottom: 10rpx;
	}
	.navImage {
		width: 44rpx;
		height: 47rpx;
		padding-right: 12rpx;
	}

	.cont_zhaungtai {
		width: calc(100% - 56rpx);
		margin: auto;
		display: flex;
		justify-content: space-around;
		margin-bottom: 30rpx;
		line-height: 80rpx;
		color: #333333;
		/* background-color: #E6F5FC; */
		border-radius: 8rpx;
	}

	.list_ {
		width: calc(100%/2);
	}

	.list_item {
		text-align: center;
		line-height: 100rpx;
		width: 100%;
	}
</style>