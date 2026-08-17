<template>
	<view>
		<zero-loading :mask="true" v-if="loading"></zero-loading>
		<!-- inmaster -->
		<uni-card>
			<uni-forms :modelValue="form.inMaster">
				<uni-forms-item label="货主" name="name">
					<uni-data-select v-model="form.inMaster.deptId" @change="changeDept"
						:localdata="deptOptions"></uni-data-select>
					<!-- <uni-easyinput type="text" v-model="form.inMaster.deptId" placeholder="请选择货主" /> -->
				</uni-forms-item>
				<uni-forms-item label="入库单类型" name="memo">
					<uni-data-select v-model="form.inMaster.type" :localdata="inTypes"></uni-data-select>
				</uni-forms-item>
				<uni-forms-item label="备注" name="memo">
					<uni-easyinput type="text" v-model="form.inMaster.memo" placeholder="请输入备注" />
				</uni-forms-item>
				<uni-forms-item label="预计到货日期" name="planningTime">
					<uni-datetime-picker type="date" v-model="form.inMaster.planningTime" @change="changeLog" />
				</uni-forms-item>
			</uni-forms>
		</uni-card>

		<!-- indetail -->
		<uni-card>
			<uni-card
				style="width:100%;height:auto;padding:1px;background-color:#FFFFFF;margin: 1px;border-radius: 5px;"
				v-for="(detail, index) in form.inDetails">
				<uni-row class="demo-uni-row">
					<uni-col :span="22">
						<div class="nameValue">
							【{{detail.itemCode}}】{{detail.itemName}}
						</div>
						<div class="nameValue">
							<uni-data-checkbox style="display: inline;" v-model="detail.unitCode"
								:localdata="detail.units" @change="change"></uni-data-checkbox>
						</div>
						<div class="nameValue">
							<uni-easyinput type="digit" v-model="detail.quantity" placeholder="请输入数量"></uni-easyinput>
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
						<div class="name">物料编码:</div>
						{{row.itemCode}}
					</div>
					<div class="nameValue">
						<div class="name">物料名称:</div>
						{{row.itemName}}
					</div>
					<div class="nameValue">
						<div class="name">基础单位:</div>
						{{row.unitCode1}}
					</div>
					<div class="nameValue">
						<div class="name">包装单位:</div>
						{{row.unitCode2}}
					</div>
					<div class="nameValue">
						<div class="name">单位系数:</div>
						{{row.rate2}}
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
					inMaster: {
						deptId: null,
						billNo: null,
						type: null,
						planningTime: null,
						createUserId: null,
						createUserName: null,
					},
					inDetails: [],
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
				if (row.unitCode1 == null) {
					uni.showToast({
						title: '未设置基础单位',
						icon: 'error'
					});
					return;
				}
				if (row.unitCode2 == null) {
					uni.showToast({
						title: '未设置包装单位',
						icon: 'error'
					});
					return;
				}
				if (row.rate2 == null) {
					uni.showToast({
						title: '未设置单位系数',
						icon: 'error'
					});
					return;
				}
				this.form.inDetails.push({
					itemCode: row.itemCode,
					itemName: row.itemName,
					batchNo: "",
					unitCode: row.unitCode1,
					units: [{
							value: row.unitCode1,
							text: row.unitCode1
						},
						{
							value: row.unitCode2,
							text: row.unitCode2 + "（" + row.rate2 + row.unitCode1 + "）"
						}
					],
					quantity: null,
				});
				this.closeItemSelect();
			},
			showItemSelect() {
				if (this.form.inMaster.deptId == null) {
					uni.showToast({
						title: '请先选择货主',
						icon: 'error'
					});
					return;
				}
				this.$refs.ItemSelect.open();
				this.item.param.deptId = this.form.inMaster.deptId;
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
					url: '/system/ItemInfo/list',
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
				if (this.form.inDetails.length == 0) {
					uni.showToast({
						title: '请选择物料后再提交',
						icon: 'error'
					});
					return;
				}
				for (var i = 0; i < this.form.inDetails.length; i++) {
					var detail = this.form.inDetails[i];
					if (detail.itemCode == null || detail.itemCode.trim() == "") {
						uni.showToast({
							title: '请输入物料编码',
							icon: 'error'
						});
						return;
					}
					if (detail.quantity == null || detail.quantity == 0) {
						uni.showToast({
							title: '请输入数量',
							icon: 'error'
						});
						return;
					}
				}
				this.loading = true;
				let url = "/wcs-in/inMaster/add";
				request({
					url: url,
					method: "post",
					data: this.form,
				}).then((response) => {
					this.loading = false;
					if (response.code == 200) {
						this.form = {
							inMaster: {
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