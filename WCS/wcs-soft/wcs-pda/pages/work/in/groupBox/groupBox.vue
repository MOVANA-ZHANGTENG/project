<template>
	<view  >
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput  confirm-type="search" 
			@change="nextScanTwoCode()" :disabled="false" type="text"  
			:focus="boxCodefocus"  
			v-model="boxCode" 
			placeholder="请扫描托盘码" />
			<uni-easyinput  
			:focus="twoLevelCodefocus"  
			 :disabled="twoLevelCodeDisabled" 
			 type="text" v-model="twoLevelCode" 
			  placeholder="请扫描二级码" />
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
		<uni-card title="产品信息" :extra="'箱数'+twoLevelCodes.length">
			 <view>
			 	<span>产品编码：</span><span>{{productInfo.productCode}}</span>
			 </view>
			 <view>
			 	<span>产品名称：</span><span>{{productInfo.productName}}</span>
			 </view>
			 <view>
			 	<span>批次：</span><span>{{productInfo.batchNo}}</span>
			 </view>
			 <view>
			 	<span>生产日期：</span><span>{{productInfo.madeDate}}</span>
			 </view>
			 <view>
			 	<span>有效期：</span><span>{{productInfo.validateDate}}</span>
			 </view>
			 <view>
			 	<span>产品编码：</span><span>{{productInfo.cascadeNo}}</span>
			 </view>
			 <view>
			 	<span>产品编码：</span><span>{{productInfo.packageSpec}}</span>
			 </view>
		</uni-card>
		<view  style="margin-top: 20px;margin-left: 5%;width: 90%;">
			<view style="font-size: 25px;  margin-top:10px;margin-left: 5%;width: 85%;background-color:darkgray;color: white;padding-left: 10px;"  >
				托盘号：{{boxCode+'  '}} 
			</view>
			<view  style="width: 100%;">
				
			</view> 
			
			<uni-row style="margin-top: 20px;margin-left: 20px;" class="demo-uni-row">
				<uni-col  v-for="code in twoLevelCodes" :xs="12" :sm="6" :md="4" :lg="3" :xl="1">
					<view>
						<uni-tag    :text="code" type="success" />
					</view>
					
				</uni-col> 
			</uni-row>
		</view>
	</view>
	
	
</template>

<script>
	import request from '@/utils/request'
	export default {
		data() {
			return {
				boxCodefocus:true,
				twoLevelCodefocus:false,
				music:null,
				twoLevelCodeDisabled:false,
				intShow:1,
				boxCode:'',
				twoLevelCode:'',
				twoLevelCodes:[ ],
				productInfo:{} 
			}
		},
		watch:{
			twoLevelCode(newValue) { 
				var that = this;  
				if(newValue!=null && newValue.trim()!=""   && newValue.trim().length==13){ 
					that.addCode(newValue) ;
				} 
			}, 
		},
		onLoad() {
			 
			 
		},
		methods: {  
			play(state){
				if(state){
					this.music = uni.createInnerAudioContext();
					this.music.src = '/static/mp3/success.mp3';
					this.music.play();
					
				}else{
					this.music = uni.createInnerAudioContext();
					this.music.src = '/static/mp3/fail.mp3';
					this.music.play();
					
				}  
			},
			commitBoxCode(){
				var that = this;
				that.nextScanTwoCode();
			},
			addCode(newValue){
				var that = this;  
				 if(that.twoLevelCode==null||that.twoLevelCode==""){
					 return;
				 }
				for(var i=0;i<that.twoLevelCodes.length;i++){
					var code = that.twoLevelCodes[i];
					if(code.trim()==newValue.trim()){
						uni.showToast({ //提示信息
							title:  '重复扫码', //提示内容
							icon: "fail" //提示图标
						});
						that.play(false);
						that.nextScanTwoCode();
						return; 
					}  
				}  
				that.getTwoInfo(that.twoLevelCode); 
				 
			},
			getTwoInfo(code){
				var that = this;
				var productInfo  =that.productInfo;
				that.twoLevelCodeDisabled=true; 
				request({ url: '/wcs-inventory/TwoLevelInfo/getInfo', method: 'get', params: { "code": code } }).then(response => {
					that.twoLevelCodeDisabled=false;  
					that.twoLevelCodefocus = false; 
					if (response.code == 200) {
						var nowProductInfo= response.data;
						if (nowProductInfo == null) {
							uni.showToast({ //提示信息
								title: '无数据', //提示内容
								icon: "fail" //提示图标
							});
							that.play(false);
							that.nextScanTwoCode();
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
								that.play(false);
								that.nextScanTwoCode();
								return;
							}
							if(that.productInfo.batchNo!=nowProductInfo.batchNo){
								uni.showToast({ //提示信息
									title: '不同批次无法组托', //提示内容
									icon: "fail" //提示图标
								});
								that.play(false);
								that.nextScanTwoCode();
								return;
							} 
						}  
						that.play(true);
						 that.twoLevelCodes.push(code); 
						 that.nextScanTwoCode();
					}
				});
				
			}, 
			nextScanTwoCode(){
				var that = this;
				that.twoLevelCode="";
				that.twoLevelCodefocus = false;
				that.$nextTick(() => {
					that.twoLevelCodefocus = true;
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
						that.play(true);
					}else{
						 this.$modal.msgError(response.msg)
						that.play(false);
					}
					
				})
			}, 
		}
	}
</script>

<style>

</style>
