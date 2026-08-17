<template>
	<view  >
		<zero-loading :mask="true" v-if="loading"></zero-loading> 
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-model="boxCode" placeholder="请扫描托盘码" /> 
		</view> 
		<view  > 
			<view  @click="toPick(param.outMasterId,row.boxCode)" style="width:90%;height:auto;padding:10px;background-color:#FFFFFF;margin: 10px;border-radius: 5px;" v-for="row in rows">
					 
					 <div class="nameValue">
						 <div class="name">产品编码:</div>
						 {{row.productCode}}
						 </div>
					 <div class="nameValue">
						 <div class="name" style="">产品名称:</div>
						 {{row.productName}}
					  </div>
					  <div class="nameValue">
						 <div class="name" style="">批次:</div>
						 {{row.batchNo}}
					   </div>
					   <div class="nameValue">
						 <div class="name" style="">质量:</div>
						 {{row.qcState}}
					    </div>
						<div class="nameValue">
							 <div class="name" style="">数量:</div>
							 {{row.quantity}}
						 </div>
						 <div class="nameValue">
						 	 <div class="name" style="">需拣货数:</div>
						 	 {{row.needQuantity}}
						  </div>
						 
						  <div class="nameValue">
						  	 <div class="name" style="">托盘号:</div>
						  	 {{row.boxCode}}
						   </div>
						   <div class="nameValue">
						   	 <div class="name" style="">货位:</div>
						   	 {{row.cellCode}}
						    </div> 
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
				loading:false,
				rows:[], 
				boxCode:'',
				param:{
					outMasterId:0,
					total:0,
					 pageNum: 1,
					 pageSize: 10,
					 moreStatus:"more", //more/loading/noMore
					 
					
					},
			}
		},
		watch:{
			boxCode(newValue) {
				var that = this; 
				if(newValue!=null && newValue.trim().length>0){ 
					setTimeout(
						that.toPickByBoxCode(newValue)
					,50); 
				} 
			}, 
			 
		},
		onLoad(option) {
			 
			this.param.outMasterId = option.outMasterId;
			this.getRows();
			 
		},
		 
		//下拉刷新
		onPullDownRefresh() {
			this.rows=[];
			this.param.pageNum=1;
			this.param.pageSize=10;
			this.getRows();
		},
		//触底加载
		onReachBottom : function(e) { //nvue暂不支持滚动监听，可用bindingx代替
			if(this.param.pageNum*this.param.pageSize<this.param.total){
				this.param.pageNum=this.param.pageNum+1;
				this.getRows();
			} 
		},
		methods: {  
			toPickByBoxCode(boxCode){
				if(boxCode!=this.boxCode){
					return;
				}
				for(var i=0;i<this.rows.length;i++){
					if(this.rows[i].boxCode == boxCode){
						this.toPick(this.param.outMasterId,boxCode);
					}
					
				} 
			},
			toPick(outMasterId,boxCode){
				uni.navigateTo({
					url: '/pages/work/out/billPick/billPick?outMasterId='+outMasterId+"&boxCode="+boxCode
				});
			},
			getRows(){
				var that = this;
				that.loading = true;
				that.moreStatus = "loading";
				request({ url: '/wcs-out/PickTask/findBoxInfoByOutMasterId', method: 'get', params: this.param }).then(response => {
					that.loading = false;
					uni.stopPullDownRefresh();
					if (response.code == 200) { 
						for(var i=0;i<response.rows.length;i++){
							var row = response.rows[i];
							this.rows.push(row);
						}
						 
						  
						 if(this.param.pageNum*this.param.pageSize>this.param.total){
						    that.param.moreStatus = "noMore";
						 } else{
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
	.nameValue{
		margin: 5px;
		.name{
			width:75px; text-align:left;  display:inline-block;
		}
	}

</style>
