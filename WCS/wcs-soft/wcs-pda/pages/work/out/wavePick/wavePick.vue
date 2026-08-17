<template>
	<view  >
		<zero-loading :mask="true" v-if="loading"></zero-loading> 
		{{waveId}}
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-model="boxCode" placeholder="请扫描托盘码" />
			<uni-easyinput type="text" v-model="twoLevelCode"  placeholder="请扫描箱码" />
		</view> 
		
		<uni-row class="demo-uni-row">
			<uni-col    :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="info" @click="boxCode='';twoLevelCode='';twoLevelCodes=[];productInfo={}">清空</button>
			</uni-col> 
			<uni-col   :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " type="primary"  @click="groupBoxInsert">提交</button>
			</uni-col> 
		</uni-row>
		<view>
		</view>
		<uni-card v-for="pickTask in pickTasks" :title="'订单号：'+pickTask.billNo" :extra="'箱数'+twoLevelCodes.length"> 
			 <view>
			 	<span>产品编码：</span><span>{{pickTask.productCode}}</span>
			 </view>
			 <view>
			 	<span>产品名称：</span><span>{{pickTask.productName}}</span>
			 </view>
			 <view>
			 	<span>批次：</span><span>{{pickTask.batchNo}}</span>
			 </view>
			 <view>
			 	<span>数量：</span>
				<span
				 v-if="pickTask!=null && pickTask.allotQuantity!=undefined"
				>{{pickTask.allotQuantity-pickTask.pickQuantity}}</span>
			 </view> 
		</uni-card>
		
		<view  style="margin-top: 20px;margin-left: 5%;width: 90%;"> 
			<view style="font-size: 20px;  margin-top:10px;margin-left: 5%;width: 85%;background-color:darkgray;color: white;padding-left: 10px;"  >
				托盘总箱数：
				<span v-if="boxInfo!=null && boxInfo.quantity!=undefined">{{boxInfo.quantity+'  '}} </span>
				<span v-else>0</span>
			</view> 
			<view style="font-size: 20px;  margin-top:10px;margin-left: 5%;width: 85%;background-color:darkgray;color: white;padding-left: 10px;"  >
				需求总箱数：{{totalPickQuantity+'  '}} 
			</view> 
			<view style="font-size: 20px;  margin-top:10px;margin-left: 5%;width: 85%;background-color:darkgray;color: white;padding-left: 10px;"  >
				扫描总箱数：{{scanQuantity+'  '}} 
			</view> 
		</view>
	</view>
	
	
</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				loading:false,
				intShow:1,
				boxCode:'',
				waveId:null,
				twoLevelCode:'',
				twoLevelCodes:[ ],
				pickTasks:[
					{},
					{}
				],
				scanQuantity:0,
				totalPickQuantity:0,
				boxInfo:{},
				productInfo:{}
				
			}
		},
		watch:{
			boxCode(newValue) {
				var that = this; 
				if(newValue!=null && newValue.trim().length>0){ 
					setTimeout(
						that.getPickTask(newValue)
					,50); 
				} 
			}, 
			twoLevelCode(newValue) { 
				var that = this; 
				if(newValue!=null && newValue.trim().length>0){ 
					setTimeout(
						that.getTwoInfo(newValue)
					,50); 
				} 
			}, 
		},
		onLoad(option) { 
			this.waveId = option.waveId;
			this.boxCode = option.boxCode 
		},
		methods: {  
			getPickTask(boxCode){
				var that = this;
				if(this.boxCode!=boxCode){
					return;
				}
				that.loading = true;
				request({ url: '/wcs-out/PickTask/findByBoxAndWaveId', method: 'get', params: { "boxCode": boxCode,"waveId":this.waveId } }).then(response => {
					 
					that.loading = false;
					if (response.code == 200) {
						that.totalPickQuantity = 0;
						
						 this.pickTasks = response.data;
						 for(var i =0;i<this.pickTasks.length;i++){
							var task = this.pickTasks[i];
							var quantity = task.allotQuantity - task.pickQuantity;
							that.totalPickQuantity+=quantity;
						 }
						 this.getByCode(boxCode);
					}
				}); 
			},
			getByCode(boxCode){
				var that = this;
				if(this.boxCode!=boxCode){
					return;
				} 
				request({ url: '/wcs-inventory/inventory/findByBoxInfo', method: 'get', params: { "boxCode": boxCode  } }).then(response => {
				 
					if (response.code == 200) {
						 this.boxInfo = response.data;
					}
				}); 
			},
			addCode(newValue){
				var that = this;
				if(that.twoLevelCode==newValue){
					setTimeout( that.clearTwoLevelCode, 50);
					for(var i=0;i<that.twoLevelCodes.length;i++){
						var code = that.twoLevelCodes[i];
						if(code==newValue){
							uni.showToast({ //提示信息
								title:  '重复扫码', //提示内容
								icon: "fail" //提示图标
							});
							return; 
						}  
					}
					that.getTwoInfo(newValue);
					
				}
			},
			getTwoInfo(code){
				var that = this;
				var productInfo  =that.productInfo;
				 
				request({ url: '/wcs-inventory/TwoLevelInfo/getInfo', method: 'get', params: { "code": code } }).then(response => {
					if (response.code == 200) {
						var nowProductInfo= response.data;
						if (nowProductInfo == null) {
							uni.showToast({ //提示信息
								title: '无数据', //提示内容
								icon: "fail" //提示图标
							});
							return; 
						}  
						if(that.productInfo.productCode==undefined){
							that.productInfo = nowProductInfo;
						}else{
							if(that.productInfo.productCode!=nowProductInfo.productCode){
								uni.showToast({ //提示信息
									title: '不同产品无法组托', //提示内容
									icon: "fail" //提示图标
								});
								return;
							}
							if(that.productInfo.batchNo!=nowProductInfo.batchNo){
								uni.showToast({ //提示信息
									title: '不同批次无法组托', //提示内容
									icon: "fail" //提示图标
								});
								return;
							} 
						}  
						 that.twoLevelCodes.push(code); 
					}
				});
				
			}, 
			clearTwoLevelCode(){
				var that = this;
				that.twoLevelCode=null;
			},
			groupBoxInsert() {
				var that = this;
				request({
					url: "/wcs-in/GroupBox/groupBoxInsert",
					params: {
						"boxCode":this.boxCode,
						"twoLevelCodes":this.twoLevelCodes
					},
					method: 'GET' 
				}).then(response => {
					if(response.code==200){
						this.$modal.msgSuccess("组盘成功");
						this.boxCode="";
						this.twoLevelCodes=[];
						this.productInfo={};
						this.twoLevelCode="";
					}else{
						
					}
					
				})
			}, 
		}
	}
</script>

<style>

</style>
