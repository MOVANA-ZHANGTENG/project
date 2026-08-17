<template>
	<view class="work-container">
		<!-- 轮播图 -->
		<!-- <uni-swiper-dot class="uni-swiper-dot-box" :info="data" :current="current" field="content">
      <swiper class="swiper-box" :current="swiperDotIndex" @change="changeSwiper">
        <swiper-item v-for="(item, index) in data" :key="index">
          <view class="swiper-item" @click="clickBannerItem(item)">
            <image :src="item.image" mode="aspectFill" :draggable="false" />
          </view>
        </swiper-item>
      </swiper>
    </uni-swiper-dot> -->




		<view
			style="width:100%;margin-left: 0%;margin-top:10px;margin-bottom:10px;background-color: #FFF;border-radius: 30px;"
			v-for="menu in menus">
			<uni-section :title="menu.title" type="line"></uni-section>
			<view class="grid-body">
				<uni-grid :column="4" :showBorder="false">
					<uni-grid-item style="height:60px" v-for="subMenu in menu.subMenus">
						<view @click="goTo(subMenu.url)" class="grid-item-box">
							<uni-icons style="color: #409EFF;" :type="subMenu.icons" size="25"></uni-icons>
							<text class="text">{{subMenu.menuTitle}}</text>
						</view>
					</uni-grid-item>
				</uni-grid>
			</view>
		</view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				current: 0,
				swiperDotIndex: 0,
				data: [{
						image: '/static/images/banner/banner01.jpg'
					},
					{
						image: '/static/images/banner/banner02.jpg'
					},
					{
						image: '/static/images/banner/banner03.jpg'
					}
				],
				menus: [{
						title: 'AGV任务',
						subMenus: [{
								menuTitle: '扫码生成任务',
								icons: 'plus',
								url: '/pages/work/agv/agvTask'
							},
						]
					},
					{
						title: '入库管理',
						subMenus: [{
								menuTitle: '新建入库单',
								icons: 'plus',
								url: '/pages/work/gld/in/createIn/createIn'
							},

							// {
							// 	menuTitle: '组盘',
							// 	icons: 'plus',
							// 	url: '/pages/work/in/groupBox/groupBox'
							// },
							// {
							// 	menuTitle: '组盘记录',
							// 	icons: 'list',
							// 	url: '/pages/work/in/groupBox/groupBoxList'
							// },

							// {
							// 	menuTitle: '上架记录',
							// 	icons: 'list',
							// 	url: ''
							// },

							// {
							// 	menuTitle: '人工库位上架',
							// 	icons: 'list',
							// 	url: ''
							// },
							// 
							// {
							// 	menuTitle: '入库单列表',
							// 	icons: 'arrow-down',
							// 	url: '/pages/work/gld/in/inWareList'
							// },
							// {
							// 	menuTitle: '扫码收货',
							// 	icons: 'list',
							// 	url: '/pages/work/gld/in/scanCode'
							// }, 
							{
								menuTitle: '审核',
								icons: 'list',
								url: '/pages/work/gld/in/review'
							},
							{
								menuTitle: '入库验收',
								icons: 'list',
								url: '/pages/work/gld/in/yanshou'
							},
							{
								menuTitle: '组盘',
								icons: 'list',
								url: '/pages/work/gld/in/groupBox'
							},
							{
								menuTitle: '上架',
								icons: 'list',
								url: '/pages/work/gld/in/grounding'
							},
							{
								menuTitle: '组盘记录',
								icons: 'list',
								url: '/pages/work/gld/in/groupBoxList'
							},
							{
								menuTitle: '收货记录',
								icons: 'list',
								url: '/pages/work/gld/in/inRecord'
							},


						]
					},
					{
						title: '出库管理',
						subMenus: [{
								menuTitle: '新建出库单',
								icons: 'scan',
								url: '/pages/work/gld/out/createOut/createOut'
							}, {
								menuTitle: '审核',
								icons: 'list',
								url: '/pages/work/gld/out/review'
							}, {
								menuTitle: '订单拣货',
								icons: 'scan',
								url: '/pages/work/out/billPick/selectBill'
							},
							// {
							// 	menuTitle: '波次拣货',
							// 	icons: 'scan',
							// 	url: '/pages/work/out/wavePick/selectWave'
							// },
							// {
							// 	menuTitle: '分拣口拣货',
							// 	icons: 'starhalf',
							// 	url: '/pages/work/out/onlinePick/onlinePick'
							// },
							// {
							// 	menuTitle: '二次分拣',
							// 	icons: 'tune-filled',
							// 	url: ''
							// },
							// {
							// 	menuTitle: '复核',
							// 	icons: 'refresh-filled',
							// 	url: ''
							// },
							// {
							// 	menuTitle: '发运',
							// 	icons: 'paperplane-filled',
							// 	url: '/pages/work/out/commitOut/selectBill'
							// },
							// {
							// 	menuTitle: '手动下架',
							// 	icons: 'arrow-down',
							// 	url: '/pages/work/out/down/index'
							// },
							// 
							{
								menuTitle: '出库单列表',
								icons: 'arrow-down',
								url: '/pages/work/gld/out/outboundList'
							},
							{
								menuTitle: '拣货',
								icons: 'arrow-down',
								url: '/pages/work/gld/out/selectBill'
							}
						]
					},
					{
						title: '库存管理',
						subMenus: [
							// {
							// 	menuTitle: '货位信息',
							// 	icons: 'list',
							// 	url: '/pages/work/inventory/cellInfo'
							// },
							{
								menuTitle: '托盘信息',
								icons: 'list',
								url: '/pages/work/inventory/palletInfo'
							},
							{
								menuTitle: '原料盘点',
								icons: 'list',
								url: '/pages/work/gld/pd/itemPd'
							},
							{
								menuTitle: '模具盘点',
								icons: 'list',
								url: '/pages/work/gld/pd/modelPd'
							},
							// {
							// 	menuTitle: '箱号查询',
							// 	icons: 'list',
							// 	url: '/pages/work/inventory/twoCodeInfo'
							// },
							// {
							// 	menuTitle: '库存明细',
							// 	icons: 'list',
							// 	url: '/pages/work/inventory/inventory'
							// },
							// {
							// 	menuTitle: '出入库报表',
							// 	icons: 'list',
							// 	url: ''
							// },
							// {
							// 	menuTitle: '盘点',
							// 	icons: 'star-filled',
							// 	url: ''
							// },
							// {
							// 	menuTitle: '移库',
							// 	icons: 'star-filled',
							// 	url: ''
							// }
						]
					},
					{
						title: '托盘管理',
						subMenus: [{
								menuTitle: '录入新托盘',
								icons: 'list',
								url: '/pages/work/gld/in/emptyTaryIn'
							}, {
								menuTitle: '空托盘出库',
								icons: 'arrow-down',
								url: '/pages/work/gld/out/emptyTaryIn'
							}, {
								menuTitle: '空托盘入库',
								icons: 'arrow-up',
								url: '/pages/work/gld/in/emptyIn'
							},

						]
					},
					{
						title: '金型模具',
						subMenus: [
							// {
							// 	menuTitle: '模具组盘',
							// 	icons: 'list',
							// 	url: '/pages/work/gld/mold/moldGroup'
							// },
							// {
							// 	menuTitle: '模具下架',
							// 	icons: 'list',
							// 	url: '/pages/work/gld/mold/moldOutbound'
							// },
							// {
							// 	menuTitle: '模具分拣',
							// 	icons: 'list',
							// 	url: '/pages/work/gld/mold/outbound'
							// },
							 
							{
								menuTitle: '模具入库单',
								icons: 'list',
								url: '/pages/work/gld/mold/in/bills'
							},
							{
								menuTitle: '模具出库单',
								icons: 'list',
								url: '/pages/work/gld/mold/out/bills'
							},
							{
								menuTitle: '模具出库单分拣',
								icons: 'list',
								url: '/pages/work/gld/mold/out/pick'
							},
						]
					}
				],
				groupBoxInfo: {
					boxCode: '',
					twoLevelCode: '',
				},

			}
		},
		methods: {
			goTo(url) {
				uni.navigateTo({
					url: url
				})
			},
			clickBannerItem(item) {
				console.info(item)
			},
			changeSwiper(e) {
				this.current = e.detail.current
			},
			changeGrid(e) {
				this.$modal.showToast('模块建设中~')
			}
		}
	}
</script>

<style lang="scss">
	/* #ifndef APP-NVUE */
	page {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;

		min-height: 100%;
		height: auto;
	}

	view {
		font-size: 14px;
		line-height: inherit;
	}

	/* #endif */

	.text {
		text-align: center;
		font-size: 26rpx;
		margin-top: 10rpx;
	}

	.uni-grid-item {
		height: 70px
	}

	.grid-item-box {
		flex: 1;
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 5px 0;
	}

	.uni-margin-wrap {
		width: 690rpx;
		width: 100%;
		;
	}

	.swiper {
		height: 300rpx;
	}

	.swiper-box {
		height: 150px;
	}

	.swiper-item {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		justify-content: center;
		align-items: center;
		color: #fff;
		height: 300rpx;
		line-height: 300rpx;
	}

	@media screen and (min-width: 500px) {
		.uni-swiper-dot-box {
			width: 400px;
			/* #ifndef APP-NVUE */
			margin: 0 auto;
			/* #endif */
			margin-top: 8px;
		}

		.image {
			width: 100%;
		}
	}
</style>