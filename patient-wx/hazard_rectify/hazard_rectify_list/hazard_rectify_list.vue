<template>
	<view class="container">
		<view class="tab-bar">
			<view :class="['tab-item', currentTab === 0 ? 'active' : '']" @tap="switchTab(0)">
				<text>待整改</text>
				<view class="badge" v-if="pendingCount > 0">{{ pendingCount > 99 ? '99+' : pendingCount }}</view>
			</view>
			<view :class="['tab-item', currentTab === 1 ? 'active' : '']" @tap="switchTab(1)">
				<text>已整改</text>
			</view>
		</view>

		<scroll-view 
			class="list-container" 
			scroll-y 
			:refresher-enabled="true" 
			:refresher-triggered="isRefreshing"
			@refresherrefresh="onRefresh"
			@scrolltolower="loadMore"
		>
			<view class="hazard-card" v-for="(item, index) in hazardList" :key="item.id" @tap="goDetail(item)">
				<view class="card-header">
					<view class="header-left">
						<text class="level-tag" :class="item.hazardLevel === '重大' ? 'level-danger' : (item.hazardLevel === '较大' ? 'level-warning' : 'level-info')">{{ item.hazardLevel }}</text>
						<text class="type-text">{{ item.hazardType }}</text>
					</view>
					<view class="status-tag" :class="item.rectifyStatus === 1 ? 'completed' : 'pending'">
						{{ item.rectifyStatus === 1 ? '已整改' : '待整改' }}
					</view>
				</view>

				<view class="card-body">
					<view class="info-row">
						<text class="label">整改期限：</text>
						<text class="value">{{ formatDateRange(item.rectifyStartDate, item.rectifyEndDate) }}</text>
					</view>
					<view class="info-row">
						<text class="label">隐患描述：</text>
						<text class="value desc">{{ item.hazardDesc || '暂无描述' }}</text>
					</view>
					<view class="info-row">
						<text class="label">整改人员：</text>
						<text class="value">{{ item.assigneeName || '未指派' }}</text>
					</view>
					<view class="info-row">
						<text class="label">完成时间：</text>
						<text class="value">{{ item.rectifyTime ? formatDateTime(item.rectifyTime) : '——' }}</text>
					</view>
				</view>
			</view>

			<u-empty v-if="!isLoading && hazardList.length === 0" mode="data" text="暂无隐患数据" margin-top="100"></u-empty>
			<u-loadmore v-if="hazardList.length > 0" :status="loadStatus" margin-top="20" margin-bottom="30"></u-loadmore>
		</scroll-view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			currentTab: 0,
			hazardList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			pendingCount: 0,
			isLoading: false,
			isRefreshing: false,
			loadStatus: 'loadmore',
			workerId: null
		};
	},
	methods: {
		onLoad() {
			this.getWorkerId();
		},

		getWorkerId() {
			let that = this;
			that.ajax(that.api.searchUserInfo, 'GET', {}, function(resp) {
				if (resp.data.code === 200 && resp.data.result) {
					let result = resp.data.result;
					that.workerId = result.workerId || result.id;
					that.loadHazardList(true);
				}
			}, true);
		},

		switchTab(index) {
			if (this.currentTab === index) return;
			this.currentTab = index;
			this.pageIndex = 1;
			this.hazardList = [];
			this.loadStatus = 'loadmore';
			this.loadHazardList(true);
		},

		loadHazardList(isRefresh) {
			let that = this;
			if (that.isLoading) return;
			
			if (!that.workerId) {
				uni.showToast({ icon: 'none', title: '请先登录' });
				return;
			}

			that.isLoading = true;

			let data = {
				assigneeId: that.workerId,
				rectifyStatus: that.currentTab === 0 ? 0 : 1,
				page: that.pageIndex,
				length: that.pageSize
			};

			that.ajax(that.api.searchHazardListByAssigneeId, 'POST', data, function(resp) {
				that.isRefreshing = false;
				
				if (resp.data.code === 200 && resp.data.result) {
					let result = resp.data.result;
					let list = result.list || [];

					if (isRefresh) {
						that.hazardList = list;
					} else {
						that.hazardList = that.hazardList.concat(list);
					}

					that.totalCount = result.totalCount || 0;

					if (that.currentTab === 0) {
						that.pendingCount = that.totalCount;
					}

					if (that.hazardList.length >= that.totalCount) {
						that.loadStatus = 'nomore';
					} else {
						that.loadStatus = 'loadmore';
					}
				} else {
					if (isRefresh) {
						that.hazardList = [];
					}
					that.loadStatus = 'nomore';
				}

				that.isLoading = false;
			}, false);
		},

		onRefresh() {
			this.isRefreshing = true;
			this.pageIndex = 1;
			this.loadHazardList(true);
		},

		loadMore() {
			if (this.isLoading || this.loadStatus === 'nomore') return;
			this.pageIndex++;
			this.loadStatus = 'loading';
			this.loadHazardList(false);
		},

		goDetail(item) {
			uni.navigateTo({
				url: '/hazard_rectify/hazard_rectify_detail/hazard_rectify_detail?id=' + item.id + '&rectifyStatus=' + item.rectifyStatus
			});
		},

		formatDateRange(start, end) {
			if (!start && !end) return '——';
			let s = start ? start.substring(0, 10) : '';
			let e = end ? end.substring(0, 10) : '';
			if (s && e) return s + ' 至 ' + e;
			return s || e || '——';
		},

		formatDateTime(dateTime) {
			if (!dateTime) return '——';
			return dateTime.replace('T', ' ').substring(0, 16);
		}
	}
};
</script>

<style lang="less" scoped>
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f6fa;
}

.tab-bar {
	display: flex;
	background-color: #fff;
	padding: 20rpx 30rpx;
	border-bottom: 1rpx solid #eee;

	.tab-item {
		flex: 1;
		text-align: center;
		padding: 16rpx 0;
		position: relative;
		color: #666;
		font-size: 30rpx;

		&.active {
			color: #409eff;
			font-weight: bold;

			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 50%;
				transform: translateX(-50%);
				width: 80rpx;
				height: 4rpx;
				background-color: #409eff;
				border-radius: 2rpx;
			}
		}

		.badge {
			position: absolute;
			top: -8rpx;
			right: 20%;
			background-color: #f56c6c;
			color: #fff;
			font-size: 20rpx;
			padding: 2rpx 10rpx;
			border-radius: 20rpx;
			min-width: 32rpx;
			text-align: center;
		}
	}
}

.list-container {
	flex: 1;
	overflow: hidden;
}

.hazard-card {
	margin: 20rpx 24rpx;
	background-color: #fff;
	border-radius: 16rpx;
	padding: 28rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;

		.header-left {
			display: flex;
			align-items: center;
			gap: 16rpx;
		}

		.level-tag {
			font-size: 24rpx;
			padding: 6rpx 16rpx;
			border-radius: 6rpx;
			color: #fff;

			&.level-danger { background-color: #f56c6c; }
			&.level-warning { background-color: #e6a23c; }
			&.level-info { background-color: #909399; }
		}

		.type-text {
			font-size: 26rpx;
			color: #333;
		}

		.status-tag {
			font-size: 24rpx;
			padding: 6rpx 20rpx;
			border-radius: 20rpx;

			&.pending {
				background-color: #fef0f0;
				color: #f56c6c;
				border: 1rpx solid #fde2e2;
			}

			&.completed {
				background-color: #f0f9eb;
				color: #67c23a;
				border: 1rpx solid #e1f3d8;
			}
		}
	}

	.card-body {
		.info-row {
			display: flex;
			margin-bottom: 12rpx;
			font-size: 26rpx;
			line-height: 1.5;

			.label {
				color: #999;
				width: 160rpx;
				flex-shrink: 0;
			}

			.value {
				color: #333;
				flex: 1;

				&.desc {
					overflow: hidden;
					text-overflow: ellipsis;
					display: -webkit-box;
					-webkit-line-clamp: 2;
					-webkit-box-orient: vertical;
				}
			}
		}
	}
}
</style>
