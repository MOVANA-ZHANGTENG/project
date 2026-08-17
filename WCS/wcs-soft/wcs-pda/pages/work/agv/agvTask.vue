<template>
	<view>
		<view style="margin:10px 0px 10px 10px">
			<!-- ✅ 修复1：删掉错误的:v-model="form"，v-model是指令不能加冒号，且该标签无需绑定form -->
			<uni-forms style="margin: 0px 10px 0px 10px;" label-width="120px">
				<uni-forms-item label="请扫描托盘码" name="palletCode">
					<uni-easyinput :disabled="false" type="text" v-model="form.palletCode" placeholder="托盘码" />
				</uni-forms-item>
				<!-- 起点选择 -->
				<uni-forms-item label="起点" name="fromCode">
					<picker 
						:range="selectOptions" 
						range-key="name"  
						v-model="fromIndex"
						@change="handleFromChange"
						style="flex: 1; padding: 8px; border: 1px solid #eee; border-radius: 4px;"
					>
						<view>
							<!-- ✅ 修复3：增加数组非空判断，避免报错中断代码 -->
							{{ selectOptions.length > 0 && form.fromCode ? selectOptions[fromIndex].name : "请选择起点" }}
						</view>
					</picker>
				</uni-forms-item>
				<!-- 终点选择 -->
				<uni-forms-item label="终点" name="toCode">
					<picker 
						:range="selectOptions" 
						range-key="name"  
						v-model="toIndex"
						@change="handleToChange"
						style="flex: 1; padding: 8px; border: 1px solid #eee; border-radius: 4px;"
					>
						<view>
							<!-- ✅ 修复3：增加数组非空判断，避免报错中断代码 -->
							{{ selectOptions.length > 0 && form.toCode ? selectOptions[toIndex].name : "请选择终点" }}
						</view>
					</picker>
				</uni-forms-item>
			</uni-forms>
		</view>
		
		<uni-row class="demo-uni-row">
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="info" @click="resetAll">清空</button>
			</uni-col> 
			<uni-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="primary"  @click="groupBoxInsert">提交</button>
			</uni-col> 
		</uni-row>
		
		<view style="font-size: 25px;  margin-top:10px;margin-left: 5%;width: 85%;background-color:darkgray;color: white;padding-left: 10px;">
			托盘号：{{form.palletCode || '暂无'}} 
		</view>
		
		<uni-row style="margin-top: 20px;margin-left: 20px;" class="demo-uni-row">
			<uni-col  v-for="(code,idx) in twoLevelCodes" :key="idx" :xs="12" :sm="6" :md="4" :lg="3" :xl="1">
				<view>
					<uni-tag :text="code" type="success" />
				</view>
			</uni-col> 
		</uni-row>
	</view>
</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				form: {
					palletCode: '',
					fromCode: null,
					toCode: null,
				},
				selectOptions: [], 
				fromIndex: 0,
				toIndex: 0,
				twoLevelCodes: []
			}
		},
		onLoad() {
			this.getSelectData();
		},
		methods: {  
			// 一键清空所有数据
			resetAll(){
				this.form.palletCode = '';
				this.form.fromCode = null;
				this.form.toCode = null;
				this.fromIndex = 0;
				this.toIndex = 0;
				this.twoLevelCodes = [];
			},
			// 起点选择事件
			handleFromChange(e) {
				this.fromIndex = e.detail.value;
				const selectedItem = this.selectOptions[this.fromIndex];
				this.form.fromCode = selectedItem.code;
			},
			// 终点选择事件
			handleToChange(e) {
				this.toIndex = e.detail.value;
				const selectedItem = this.selectOptions[this.toIndex];
				this.form.toCode = selectedItem.code;
			},
			// 获取下拉框数据
			getSelectData(){
				request({
					url: "/wcs-xlPro/AgvTaskMsg/findAGVPosition",
					method: 'GET' 
				}).then(response => {
					if(response.code==200){
						this.$modal.msgSuccess("数据加载成功");
						this.resetAll();
						this.selectOptions = response.data;
						console.log("下拉数据：",this.selectOptions)
					}else{
						this.$modal.msgError(response.msg);
					}
				})
			},
			// 提交方法 - 所有校验+请求修复完毕
			groupBoxInsert() {
				// ✅ 新增调试打印：查看托盘码真实值，方便排查
				console.log("触发提交，托盘码真实值：", this.form.palletCode);
				console.log("触发提交，起点值：", this.form.fromCode);
				console.log("触发提交，终点值：", this.form.toCode);
				
				// 1. 校验托盘码：先去除首尾空格，再判断是否为空 【加固版，100%触发】
				const palletCode = this.form.palletCode.trim();
				if (!palletCode) {
					this.$modal.msgWarning("请先扫描托盘码");
					return;
				}
				// 2. 校验起点必须选择
				if(!this.form.fromCode) {
					this.$modal.msgWarning("请选择起点位置");
					return;
				}
				// 3. 校验终点必须选择
				if(!this.form.toCode) {
					this.$modal.msgWarning("请选择终点位置");
					return;
				}
				// 4. 校验起点终点不能一致
				if(this.form.fromCode === this.form.toCode) {
					this.$modal.msgWarning("起点和终点不能选择相同位置，请重新选择");
					return;
				}
				
				// ✅ 修复2：POST请求 传参由 params → data 【后端能正常接收参数】
				request({
					url: "/wcs-xlPro/AgvTaskMsg/groupBoxInsert",
					data: {
						"palletCode": palletCode, 
						"fromCode": this.form.fromCode,
						"toCode": this.form.toCode,
					},
					method: 'POST' 
				}).then(response => {
					if(response.code==200){
						this.$modal.msgSuccess("生成任务成功");
						this.resetAll();
					}else{
						this.$modal.msgError(response.msg)
					}
				}).catch(err=>{
					console.log("请求异常信息：", err);
					this.$modal.msgError("接口请求失败，请检查网络或联系管理员");
				})
			}
		}
	}
</script>

<style>
	.button{margin-top: 8px;padding: 10px 0;border-radius: 6px;}
</style>