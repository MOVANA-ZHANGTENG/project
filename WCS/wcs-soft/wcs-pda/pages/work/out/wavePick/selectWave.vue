<template>
	<view  >
		<zero-loading :mask="true" v-if="loading"></zero-loading> 
		<view  >
			<view @click="toPick(wave.waveId)" style="width:90%;height:auto;padding:10px;background-color:#FFFFFF;margin: 10px;border-radius: 5px;" v-for="wave in waves">
				 
				 <div>波次ID:{{wave.waveId}}</div>
				 <div>波次类型:{{wave.type}}</div>
				              <div>状态:{{wave.state}}</div>
				              <div>时间:{{wave.createTime}}</div>
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
				waves:[], 
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
			this.getWaves();
			 
		},
		//下拉刷新
		onPullDownRefresh() {
			this.waves=[];
			this.param.pageNum=1;
			this.param.pageSize=10;
			this.getWaves();
		},
		//触底加载
		onReachBottom : function(e) { //nvue暂不支持滚动监听，可用bindingx代替
			if(this.param.pageNum*this.param.pageSize<this.param.total){
				this.param.pageNum=this.param.pageNum+1;
				this.getWaves();
			} 
		},
		methods: {  
			toPick(waveId){
				uni.navigateTo({
					url: '/pages/work/out/wavePick/selectBox?waveId='+waveId
				});
			},
			getWaves(){
				var that = this;
				that.loading = true;
				that.moreStatus = "loading";
				request({ url: '/wcs-out/WaveInfo/list', method: 'get', params: this.param }).then(response => {
					that.loading = false;
					uni.stopPullDownRefresh();
					if (response.code == 200) { 
						for(var i=0;i<response.rows.length;i++){
							var row = response.rows[i];
							this.waves.push(row);
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

</style>
