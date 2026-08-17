<template>
	<view  style="padding: 5px;" >
		<zero-loading :mask="true" v-if="loading"></zero-loading> 
		<view style="margin:10px 0px 10px 10px">
			<uni-easyinput type="text" v-model="param.keywords" placeholder="关键字查询" />
			<uni-easyinput type="text" v-model="param.cellCode" placeholder="货位号" />
			<uni-easyinput type="text" v-model="param.z"  placeholder="层" />
			<uni-easyinput type="text" v-model="param.type"  placeholder="类型" /> 
		</view> 
		<uni-row class="demo-uni-row">
			<uni-col    :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; " size="mini" type="info" @click="clearParam">重置</button>
			</uni-col> 
			<uni-col   :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
				<button class="button" style="width:90%;margin-left:5%; "size="mini" type="primary"  @click="rows=[];getRows()">查询</button>
			</uni-col> 
		</uni-row>
		<view  >
			<view   style="width:90%;height:auto;padding:10px;background-color:#FFFFFF;margin: 10px;border-radius: 5px;" v-for="row in rows">
				 
				 
					  
					   <div class="nameValue">
					   	 <div class="name" style="">货位编码:</div>
					   	 {{row.cellCode}}
					    </div> 
						<div class="nameValue">
							 <div class="name" style="">类型:</div>
							 {{row.type}}
						 </div>
						 <div class="nameValue">
						 	 <div class="name" style="">层:</div>
						 	 {{row.z}}
						  </div>
						<div class="nameValue">
							 <div class="name" style="">是否有货:</div>
							 {{row.invenState}}
						 </div> 
						 <div class="nameValue">
						 	 <div class="name" style="">是否有任务:</div>
						 	 {{row.taskState}}
						  </div> 
						  <div class="nameValue">
						  	 <div class="name" style="">是否被禁用:</div>
						  	 {{row.disableState}}
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
				param:{
					total:0,
					 pageNum: 1,
					 pageSize: 10,
					 moreStatus:"more", //more/loading/noMore
					 
					
					},
			}
		},
		watch:{
			 
		},
		onLoad() {
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
			clearParam(){
				this.rows=[];
				this.param={
					total:0,
					 pageNum: 1,
					 pageSize: 10,
					 moreStatus:"more", //more/loading/noMore
					 
					
					}
			},
		 
			getRows(){
				var that = this;
				that.loading = true;
				that.moreStatus = "loading";
				request({ url: '/wcs-base/CellInfo/list', method: 'get', params: this.param }).then(response => {
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
