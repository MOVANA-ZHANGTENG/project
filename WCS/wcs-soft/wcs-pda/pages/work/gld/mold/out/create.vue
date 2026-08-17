<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<!-- master -->
		<uni-card>
			<uni-forms :modelValue="form.master">
				 <uni-forms-item label="货主" name="name">
				 	<uni-data-select v-model="form.master.deptId" @change="changeDept"
				 		:localdata="deptOptions"></uni-data-select>
				 	<!-- <uni-easyinput type="text" v-model="form.inMaster.deptId" placeholder="请选择货主" /> -->
				 </uni-forms-item>
				<uni-forms-item label="备注" name="memo">
					<uni-easyinput type="text" v-model="form.master.memo" placeholder="请输入备注" />
				</uni-forms-item>
			 
			</uni-forms>
		</uni-card>

		<!-- indetail -->
		<uni-card>
			<uni-card
				style="width:100%;height:auto;padding:1px;background-color:#FFFFFF;margin: 1px;border-radius: 5px;"
				v-for="(detail, index) in form.list">
				<uni-row class="demo-uni-row">
					<uni-col :span="22">
						<div class="nameValue">
							<div class="name">金型条形码:</div>
							{{detail.code}}
						</div>
						<div class="nameValue">
							<div class="name">金型NO.:</div>
							{{detail.code2}}
						</div>
						<div class="nameValue">
							<div class="name">部品名:</div>
							{{detail.departName}}
						</div>
						<div class="nameValue">
							<div class="name">重量:</div>
							{{detail.weight}}
						</div>
						<div class="nameValue">
							<div class="name">长-宽-高:</div>
							{{detail.length}}-{{detail.width}}-{{detail.high}}
						</div>
						<div class="nameValue">
							<div class="name">组合编号:</div>
							{{detail.groupCode}} 
						</div>
						<div class="nameValue">
							<div class="name">托盘号:</div>
							{{detail.palletCode}} 
						</div>
						<div class="nameValue">
							<div class="name">机种:</div>
							{{detail.models}} 
						</div>
						<div class="nameValue">
							<div class="name">资产号:</div>
							{{detail.assetNumber}} 
						</div>
						<div class="nameValue">
							<div class="name">类型:</div>
							{{detail.metalType}} 
						</div>
						 
       
					 
					 
					</uni-col>
					<uni-col :span="2">
						<view style="height:100%;padding-top: 5%;">
							<uni-icons @click="deleteRow(index)" type="closeempty" size="15"
								style="color: red"></uni-icons>
						</view>
					</uni-col>
				</uni-row>
			</uni-card>
			<uni-card @click="showItemSelect" style="text-align: center;">
				<uni-icons type="plusempty" size="15" style="color: #67C23A">新增一行</uni-icons>
			</uni-card>
			<uni-card @click="save" style="text-align: center;">
				<uni-icons type="checkmarkempty" size="15" style="color: #67C23A">保存</uni-icons>
			</uni-card>
		</uni-card>
		<!-- <button
			style="width:50px;height:50px;line-height: 50px;border-radius:25px;border-style: solid;padding-left: 8px;border-color: #67C23A;color: #67C23A;position: absolute;bottom: 50px;right: 10px;;"
			@click="showItemSelect"><uni-icons type="plusempty" size="30" style="color: #67C23A"></uni-icons> </button> -->


		<!-- 选择物料 -->
		<uni-drawer :width="350" ref="ItemSelect" mode="right" :mask-click="false">
			<!-- 物料搜索 -->
			<uni-card style="height:10vh; ">
				<uni-easyinput class="uni-mt-5" suffixIcon="search" v-model="item.param.keyword" placeholder="关键字搜索"
					@iconClick="getItemInfos">查询</uni-easyinput>
			</uni-card>
			<!-- 物料选择 -->
			<uni-card style="height:72vh;overflow-y: auto; ">
				<uni-card @click="addDetail(row)"
					style="width:95%;height:auto;padding:1px;background-color:#FFFFFF;margin: 1px;border-radius: 5px;"
					v-for="row in item.data">
					<div class="nameValue">
						<div class="name">金型条形码:</div>
						{{row.code}}
					</div>
					<div class="nameValue">
						<div class="name">金型NO.:</div>
						{{row.code2}}
					</div>
					<div class="nameValue">
						<div class="name">部品名:</div>
						{{row.departName}}
					</div>
					<div class="nameValue">
						<div class="name">重量:</div>
						{{row.weight}}
					</div>
					<div class="nameValue">
						<div class="name">长-宽-高:</div>
						{{row.length}}-{{row.width}}-{{row.high}}
					</div>
					<div class="nameValue">
						<div class="name">组合编号:</div>
						{{row.groupCode}} 
					</div>
					<div class="nameValue">
						<div class="name">托盘号:</div>
						{{row.palletCode}} 
					</div>
					<div class="nameValue">
						<div class="name">机种:</div>
						{{row.models}} 
					</div>
					<div class="nameValue">
						<div class="name">资产号:</div>
						{{row.assetNumber}} 
					</div>
					<div class="nameValue">
						<div class="name">类型:</div>
						{{row.metalType}} 
					</div>
					 
				</uni-card>
				<uni-pagination title="分页" @change="changeItemPage" show-icon="true" :total="item.param.total"
					:current="item.param.pageNum"></uni-pagination>
			</uni-card>
			<button style="height:6vh" @click="closeItemSelect" type="primary">关闭Drawer</button>
		</uni-drawer>
	</view>
</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				loading: false,
				item: {
					param: {
						metalType:null,
						
isPallet:0,
						pageNum: 1,
						pageSize: 8,
						total: 0,
					},
					data: [{
							itemCode: "2234"
						},
						{
							itemName: "fadsf"
						}
					],
				},
				  form: {
				        //金型组合数据
				        master: {
							type:2
						},
				        //金型列表
				        list: [],
				      },
				deptOptions: {},
				inTypes: [],
			}
		},
		created() {
			this.getItemInfos();
			this.getDeptTree();
			this.getInTypes();
		},
		methods: {
			changeDept(e) {
				this.form.inDetails = [];
			},
			deleteRow(index) {
				this.form.inDetails.splice(index, 1);
			},
			addDetail(row) {
				 
				       for (var i = 0; i < this.form.list.length; i++) {
				         var item = this.form.list[i];
				         if (item.code == row.code) {
							 	this.closeItemSelect();
				           return;
				         }
				       }
				       this.form.list.push(row);
				 
				this.closeItemSelect();
			},
			showItemSelect() {
				if (this.form.master.deptId == null) {
					uni.showToast({
						title: '请先选择货主',
						icon: 'error'
					});
					return;
				}
				this.$refs.ItemSelect.open();
				this.item.param.deptId = this.form.master.deptId;
				this.getItemInfos();
			},
			closeItemSelect() {
				this.$refs.ItemSelect.close();
			},
			changeItemPage(e) {
				var that = this;
				console.info(e);
				this.item.param.pageNum = e.current;
				this.getItemInfos();
			},
			getItemInfos() {
				var that = this;
				that.loading = true;
				request({
					url: '/wcs-base/MetalMode/list',
					method: 'get',
					params: this.item.param
				}).then(response => {
					that.loading = false;
					if (response.code == 200) {

						that.item.data = response.rows;
						that.item.param.total = response.total;
					}
				});
			},
			/** 查询货主下拉树结构 */
			getDeptTree() {
				request({
					url: "/system/role/selectDeptTreeListByUserId",
					method: "get",
					params: {},
				}).then((response) => {
					if (response.code == 200) {
						var depts = response.data;
						this.deptOptions = [];
						for (var i = 0; i < depts.length; i++) {
							this.deptOptions.push({
								value: depts[i].id,
								text: depts[i].label,
							});
						}
					} else {
						uni.showToast({
							title: response.msg,
							icon: 'error'
						});
					}
				});
			},
			/** 查询货主下拉树结构 */
			getInTypes() {
				request({
					url: "/system/dict/data/type/in_type",
					method: "get",
					params: {},
				}).then((response) => {
					if (response.code == 200) {
						var inTypes = response.data;
						this.inTypes = [];
						for (var i = 0; i < inTypes.length; i++) {
							this.inTypes.push({
								value: inTypes[i].dictValue,
								text: inTypes[i].dictLabel,
							});
						}
					} else {
						uni.showToast({
							title: response.msg,
							icon: 'error'
						});
					}
				});
			},
			save() {
				if (this.form.list == null || this.form.list.length == 0) {
				   uni.showToast({
					title: '请选择金型后再提交',
					icon: 'error'
				   });
					return;
				  }
				  var list = [];
				  this.form.list.forEach((detail) => {
					list.push({ modelCode: detail.code });
				  });
				 
				  this.form.master.type = 1;
				this.loading = true;
				let url = "/wcs-inventory/MetalModelBill/insert";
				request({
					url: url,
					method: "post",
					 data: { master: this.form.master, list: list },
				}).then((response) => {
					this.loading = false;
					if (response.code == 200) {
						this.form = {
							master: {
								billNo: null,
								type: null,
								planningTime: null,
								createUserId: null,
								createUserName: null,
							},
							inDetails: [],
						};
						uni.showToast({
							title: "保存成功",
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: response.msg,
							icon: 'error'
						});
					}

				});
			},
		}
	}
</script>

<style>
	.nameValue {
		margin: 1px;

		.name {
			width: 75px;
			text-align: left;
			display: inline-block;
		}
	}
</style>