<template>
	<view style="padding: 5px;">
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<view style="margin:10px 0px 10px 10px">
			<!-- <uni-easyinput type="text" v-model="param.keywords" placeholder="关键字查询" /> -->
			<uni-easyinput type="text" v-model="param.code" placeholder="托盘码" />
			<!-- <uni-easyinput type="text" v-model="param.cellCode" placeholder="货位号" /> -->
		</view>
		<uni-row class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " size="mini" type="info"
					@click="clearParam">重置</button>
			</uni-col>
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
					@click="rows=[];getRows()">查询</button>
			</uni-col>
		</uni-row>
		<view>
			<view style="width:90%;height:auto;padding:10px;background-color:#FFFFFF;margin: 10px;border-radius: 5px;"
				v-for="row in rows">

				<div class="nameValue">
					<div class="name">托盘编码:</div>
					{{row.code}}
				</div>

				<div class="nameValue">
					<div class="name" style="">托盘类型:</div>
					{{row.palletTypeName}}
				</div>
				<div class="nameValue">
					<div class="name" style="">托盘尺寸:</div>
					{{row.size}}
				</div>
				<div class="nameValue">
					<div class="name" style="">货位编码:</div>
					<uni-easyinput style="width:200px;margin-top: -28px;margin-left: 80px;" type="text"
						placeholder="请输入货位编码,暂存区请输入000" v-model="row.cellCode" />
				</div>
				<button class="button" style="width:90%;margin-left:5%; " size="mini" type="primary"
					@click="commit(row)">修改托盘货位</button>



			</view>
		</view>
		<!-- {{param}} -->
		<uni-load-more :status="param.moreStatus"></uni-load-more>
		<!-- <uni-pagination
			 style="width:90%;margin-left:5%;height:50px;background-color:#FFFFFF;padding: 5px;border-radius: 5px;"
			 title="标题文字" :total="param.total"></uni-pagination> -->
	</view>


</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				loading: false,
				rows: [],
				param: {
					code: '',
					total: 0,
					pageNum: 1,
					pageSize: 10,
					moreStatus: "more", //more/loading/noMore 
				},
			}
		},
		watch: {

		},
		onLoad() {
			this.getRows();

		},
		//下拉刷新
		onPullDownRefresh() {
			this.rows = [];
			this.param.pageNum = 1;
			this.param.pageSize = 10;
			this.getRows();
		},
		//触底加载
		onReachBottom: function(e) { //nvue暂不支持滚动监听，可用bindingx代替
			if (this.param.pageNum * this.param.pageSize < this.param.total) {
				this.param.pageNum = this.param.pageNum + 1;
				this.getRows();
			}
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
			clearParam() {
				this.rows = [];
				this.param = {
					code: '',
					total: 0,
					pageNum: 1,
					pageSize: 10,
					moreStatus: "more", //more/loading/noMore


				}
			},
			commit(row) {
				var that = this;
				that.loading = true;
				request({
					url: '/wcs-inventory/palletInfo/updateCellCode',
					method: 'get',
					params: row
				}).then(response => {
					that.loading = false;
					if (response.code == 200) {
						uni.showToast({
							title: row.code + "的位置成功修改为" + row.cellCode,
							icon: 'success'
						})
						this.play(true);
					} else {
						this.play(false);
						uni.showToast({
							title: response.msg,
							icon: 'error'
						})
					}
				});

			},

			getRows() {
				var that = this;
				that.loading = true;
				that.moreStatus = "loading";
				request({
					url: '/wcs-inventory/palletInfo/list',
					method: 'get',
					params: this.param
				}).then(response => {
					that.loading = false;
					uni.stopPullDownRefresh();
					if (response.code == 200) {
						for (var i = 0; i < response.rows.length; i++) {
							var row = response.rows[i];
							this.rows.push(row);
						}


						if (this.param.pageNum * this.param.pageSize > this.param.total) {
							that.param.moreStatus = "noMore";
						} else {
							that.param.moreStatus = "more";
						}
						that.param.total = response.total;
					}
				});

			},
		}
	}
</script>

<style>
	.nameValue {
		margin: 5px;

		.name {
			width: 75px;
			text-align: left;
			display: inline-block;
		}
	}
</style>