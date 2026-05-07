<template>
	<view>
		<view class="date-container">
			<view class="item" v-for="one in dateList">
				<text class="day">{{ one.day }}</text>
				<view :class="one.date == date ? 'selector active' : 'selector'" @tap="clickDateHandle(one.date)">
					<text class="date">{{ one.dateOfMonth }}</text>
					<text :class="one.status == '无' ? 'status gray' : 'status'">{{ one.status }}</text>
				</view>
			</view>
		</view>
		<view class="doctor" v-for="one in workList">
			<u-avatar :src="one.photo" size="45"></u-avatar>
			<view class="info">
				<view class="row">
					<text class="no">{{ one.userNo }}</text>
					<text class="no">{{ one.userName }}</text>
					<text class="tel">{{ one.contactPhone }}</text>
					<button class="btn" @tap="clickBtnHandle(one.id, date)">巡检</button>
				</view>
				<view class="row">
					<text class="num">地址:{{ one.address }}</text>
					<text class="price">{{ one.buildingInfo }}</text>
				</view>
				<view class="row">
					<rich-text class="desc">{{ one.description }}</rich-text>
				</view>
			</view>
		</view>
		<u-empty
			v-if="showEmpty"
			mode="list"
			text="无巡检记录"
			width="150"
			icon="http://cdn.uviewui.com/uview/empty/order.png"
		></u-empty>
	</view>
</template>

<script>
let dayjs = require('dayjs');
export default {
	data() {
		return {
			deptSubId: null,
			showEmpty: false,
			date: dayjs().format('YYYY-MM-DD'),
			dateList: [],
			workList: []
		};
	},
	methods: {
		searchCanWorkInDateRange: function(ref) {
		    let startDate = dayjs().format('YYYY-MM-DD');
		    let endDate = dayjs().add(6, 'day').format('YYYY-MM-DD');
		    let data = {
		        startDate: startDate,
		        endDate: endDate
		    };
		    ref.ajax(ref.api.searchCanWorkInDateRange, 'POST', data,
		        function(resp) {
		            let result = resp.data.result;
		            let array = ['日', '一', '二', '三', '四', '五', '六'];
		            for (let one of result) {
		                let day = array[dayjs(one.date).day()];
		                one.day = day;
		                one.dateOfMonth = dayjs(one.date).date();
		            }
		            ref.dateList = result;
		        },
		        false
		    );
		},
		
		searchWorkerPlanInDay: function(ref) {
		    let data = {
		        date: ref.date
		    };
		
		    ref.ajax(ref.api.searchWorkerPlanInDay, 'POST', data,
		        function(resp) {
		            let result = resp.data.result;
		            for (let one of result) {
		                one.photo = `${ref.minioUrl}${one.photo}`;
		            }
		            ref.workList = result;
		            if (result.length == 0) {
		                ref.showEmpty = true;
		            } else {
		                ref.showEmpty = false;
		            }
		        },
		        false
		    );
		},
		
		clickDateHandle: function(date) {
		    let that = this;
		    that.date = date;
		    that.searchWorkerPlanInDay(that);
		},

		clickBtnHandle: function(id) {
			let that = this;
			let selected = that.workList.find(item => item.id === id);
			uni.navigateTo({
				url: `/registration/worker_inspection/worker_inspection?workerPlanId=${id}&address=${encodeURIComponent(selected.address || '')}`
			});
		}

	},
	onLoad: function(options) {
		    let that = this;
		    that.searchCanWorkInDateRange(that)
		    that.searchWorkerPlanInDay(that);
	
	}
};
</script>

<style lang="less">
@import url(worker_plan.less);
</style>
