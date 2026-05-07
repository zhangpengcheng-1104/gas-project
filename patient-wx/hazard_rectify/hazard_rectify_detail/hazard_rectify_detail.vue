<template>
	<view class="container">
		<view class="section">
			<view class="section-title" @tap="toggleSection('content')">
				<text>隐患内容</text>
				<u-icon :name="expandedSections.content ? 'arrow-up' : 'arrow-down'" size="14" color="#666"></u-icon>
			</view>

			<view class="section-body" v-if="expandedSections.content">
				<view class="info-item">
					<text class="label">隐患等级</text>
					<text class="value level" :class="hazardInfo.hazardLevel === '重大' ? 'level-danger' : (hazardInfo.hazardLevel === '较大' ? 'level-warning' : 'level-info')">{{ hazardInfo.hazardLevel }}</text>
				</view>
				<view class="info-item">
					<text class="label">隐患分类</text>
					<text class="value">{{ hazardInfo.hazardType || '——' }}</text>
				</view>
				<view class="info-item">
					<text class="label">隐患来源</text>
					<text class="value">{{ hazardInfo.hazardSource || '——' }}</text>
				</view>
				<view class="info-item">
					<text class="label">关联主体</text>
					<text class="value">{{ hazardInfo.userName || '——' }}</text>
				</view>
				<view class="info-item">
					<text class="label">提交人</text>
					<text class="value">{{ hazardInfo.submitUserName || '——' }}</text>
				</view>
				<view class="info-item">
					<text class="label">提交时间</text>
					<text class="value">{{ formatDateTime(hazardInfo.submitTime) }}</text>
				</view>
				<view class="info-item">
					<text class="label">行政区域</text>
					<text class="value">{{ hazardInfo.areaNameDisplay || '——' }}</text>
				</view>
				<view class="info-item address-item" @tap="openMap">
					<text class="label">隐患地址</text>
					<view class="address-value">
						<text class="value">{{ hazardInfo.address || '——' }}</text>
						<u-icon name="map" size="18" color="#409eff"></u-icon>
					</view>
				</view>
				<view class="info-item">
					<text class="label">整改期限</text>
					<text class="value">{{ formatDateRange(hazardInfo.rectifyStartDate, hazardInfo.rectifyEndDate) }}</text>
				</view>
				<view class="info-item desc-item">
					<text class="label">隐患描述</text>
					<text class="value desc">{{ hazardInfo.hazardDesc || '暂无描述' }}</text>
				</view>

				<view class="photo-section" v-if="hazardInfo.hazardPhoto">
					<text class="label">隐患照片</text>
					<image 
						class="hazard-photo" 
						:src="getHazardPhotoUrl(hazardInfo.hazardPhoto)" 
						mode="aspectFill"
						@tap="previewImage(hazardInfo.hazardPhoto)"
					></image>
				</view>

				<view class="map-container" v-if="showMap && latitude && longitude">
					<map
						class="map"
						:latitude="latitude"
						:longitude="longitude"
						:markers="markers"
						:scale="16"
						:enable-scroll="true"
						:enable-zoom="true"
					></map>
					<view class="map-close" @tap="closeMap">
						<u-icon name="close" size="20" color="#fff"></u-icon>
					</view>
				</view>
			</view>
		</view>

		<view class="section" v-if="hazardInfo.rectifyStatus === 1">
			<view class="section-title" @tap="toggleSection('rectify')">
				<text>整改信息</text>
				<u-icon :name="expandedSections.rectify ? 'arrow-up' : 'arrow-down'" size="14" color="#666"></u-icon>
			</view>

			<view class="section-body" v-if="expandedSections.rectify">
				<view class="info-item">
					<text class="label">整改状态</text>
					<text class="value status-completed">已整改</text>
				</view>
				<view class="info-item">
					<text class="label">完成时间</text>
					<text class="value">{{ formatDateTime(hazardInfo.rectifyTime) }}</text>
				</view>

				<view class="photo-section" v-if="hazardInfo.rectifyPhoto">
					<text class="label">整改照片</text>
					<scroll-view scroll-x class="photo-scroll">
						<view class="photo-list">
							<image
								v-for="(photo, index) in getRectifyPhotos(hazardInfo.rectifyPhoto)"
								:key="index"
								class="rectify-photo"
								:src="getRectifyFileUrl(photo)"
								mode="aspectFill"
								@tap="previewRectifyImages(hazardInfo.rectifyPhoto, index)"
							></image>
						</view>
					</scroll-view>
				</view>

				<view class="video-section" v-if="hazardInfo.rectifyVideo">
					<text class="label">整改视频</text>
					<video
						class="rectify-video"
						:src="getRectifyFileUrl(hazardInfo.rectifyVideo)"
						controls
						object-fit="contain"
					></video>
				</view>
			</view>
		</view>

		<view class="submit-btn" v-if="hazardInfo.rectifyStatus === 0" @tap="goSubmit">
			<text>去整改</text>
		</view>

		<u-toast ref="uToast"></u-toast>
	</view>
</template>

<script>
export default {
	data() {
		return {
			hazardId: null,
			rectifyStatus: 0,
			hazardInfo: {},
			latitude: null,
			longitude: null,
			markers: [],
			showMap: false,
			expandedSections: {
				content: true,
				rectify: true
			}
		};
	},
	methods: {
		onLoad(options) {
			this.hazardId = options.id;
			this.rectifyStatus = parseInt(options.rectifyStatus) || 0;
			this.loadHazardDetail();
		},

		loadHazardDetail() {
			let that = this;
			that.ajax(that.api.searchHazardById, 'POST', { id: that.hazardId }, function(resp) {
				if (resp.data.code === 200 && resp.data.result) {
					that.hazardInfo = resp.data.result;

					if (that.hazardInfo.address) {
						that.loadMapLocation(that.hazardInfo.address);
					}

					if (that.hazardInfo.longitude && that.hazardInfo.latitude) {
						that.longitude = parseFloat(that.hazardInfo.longitude);
						that.latitude = parseFloat(that.hazardInfo.latitude);
						that.markers = [{
							id: 1,
							latitude: that.latitude,
							longitude: that.longitude,
							title: that.hazardInfo.address || '',
							width: 28,
							height: 28,
							callout: {
								content: that.hazardInfo.address || '',
								color: '#333',
								fontSize: 12,
								borderRadius: 4,
								padding: 6,
								display: 'ALWAYS'
							}
						}];
					}
				} else {
					uni.showToast({ icon: 'none', title: '加载失败' });
				}
			}, true);
		},

		loadMapLocation(address) {
			let that = this;
			that.ajax(that.api.geocode + '?address=' + encodeURIComponent(address) + '&city=重庆', 'GET', null, function(resp) {
				let result = resp.data.result;
				if (result && result.status === '1' && result.geocodes && result.geocodes.length > 0) {
					let location = result.geocodes[0].location;
					let coords = location.split(',');
					if (coords.length === 2) {
						that.longitude = parseFloat(coords[0]);
						that.latitude = parseFloat(coords[1]);
					}
				}
			}, false);
		},

		toggleSection(section) {
			this.expandedSections[section] = !this.expandedSections[section];
		},

		openMap() {
			if (this.latitude && this.longitude) {
				this.showMap = true;
			} else {
				uni.showToast({ icon: 'none', title: '暂无位置信息' });
			}
		},

		closeMap() {
			this.showMap = false;
		},

		getHazardPhotoUrl(photoPath) {
			if (!photoPath) return '';
			if (photoPath.startsWith('http')) return photoPath;
			return this.hazardMinioUrl + photoPath;
		},

		getRectifyFileUrl(path) {
			if (!path) return '';
			if (path.startsWith('http')) return path;
			return this.rectificationMinioUrl + path;
		},

		getRectifyPhotos(photoStr) {
			if (!photoStr) return [];
			return photoStr.split(',').filter(p => p && p.trim());
		},

		previewImage(photoPath) {
			let url = this.getHazardPhotoUrl(photoPath);
			uni.previewImage({
				current: url,
				urls: [url]
			});
		},

		previewRectifyImages(photoStr, currentIndex) {
			let photos = this.getRectifyPhotos(photoStr);
			let urls = photos.map(p => this.getRectifyFileUrl(p));
			uni.previewImage({
				current: urls[currentIndex],
				urls: urls
			});
		},

		goSubmit() {
			uni.navigateTo({
				url: '/hazard_rectify/hazard_rectify_submit/hazard_rectify_submit?id=' + this.hazardId
			});
		},

		formatDateTime(dateTime) {
			if (!dateTime) return '——';
			return dateTime.replace('T', ' ').substring(0, 16);
		},

		formatDateRange(start, end) {
			if (!start && !end) return '——';
			let s = start ? start.substring(0, 10) : '';
			let e = end ? end.substring(0, 10) : '';
			if (s && e) return s + ' 至 ' + e;
			return s || e || '——';
		}
	}
};
</script>

<style lang="less" scoped>
.container {
	min-height: 100vh;
	background-color: #f5f6fa;
	padding-bottom: 120rpx;
}

.section {
	margin: 20rpx 24rpx;
	background-color: #fff;
	border-radius: 16rpx;
	overflow: hidden;

	.section-title {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 28rpx;
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.section-body {
		padding: 8rpx 28rpx 28rpx;
	}
}

.info-item {
	display: flex;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
	font-size: 28rpx;
	line-height: 1.5;

	&:last-child {
		border-bottom: none;
	}

	.label {
		color: #999;
		width: 180rpx;
		flex-shrink: 0;
	}

	.value {
		color: #333;
		flex: 1;
		word-break: break-all;

		&.level-danger { color: #f56c6c; font-weight: bold; }
		&.level-warning { color: #e6a23c; font-weight: bold; }
		&.status-completed { color: #67c23a; font-weight: bold; }

		&.desc {
			display: block;
			margin-top: 10rpx;
			line-height: 1.6;
		}
	}

	&.address-item {
		.address-value {
			flex: 1;
			display: flex;
			align-items: center;
			justify-content: space-between;
		}
	}

	&.desc-item {
		flex-direction: column;

		.label {
			width: auto;
			margin-bottom: 12rpx;
		}

		.desc {
			width: 100%;
		}
	}
}

.photo-section, .video-section {
	padding: 20rpx 0;

	.label {
		display: block;
		color: #999;
		font-size: 28rpx;
		margin-bottom: 16rpx;
	}
}

.hazard-photo {
	width: 400rpx;
	height: 300rpx;
	border-radius: 12rpx;
}

.photo-scroll {
	white-space: nowrap;
	width: 100%;
}

.photo-list {
		display: inline-flex;
	gap: 16rpx;
}

.rectify-photo {
	width: 200rpx;
	height: 200rpx;
	border-radius: 12rpx;
	flex-shrink: 0;
}

.rectify-video {
	width: 100%;
	height: 400rpx;
	border-radius: 12rpx;
}

.map-container {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 999;
	background-color: rgba(0, 0, 0, 0.5);

	.map {
		width: 100%;
		height: 60vh;
		margin-top: 30vh;
	}

	.map-close {
		position: absolute;
		top: calc(30vh + 20rpx);
		right: 20rpx;
		width: 60rpx;
		height: 60rpx;
		background-color: rgba(0, 0, 0, 0.5);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}
}

.submit-btn {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #409eff;
	color: #fff;
	text-align: center;
	padding: 28rpx 0;
	font-size: 32rpx;
	font-weight: bold;
	z-index: 100;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
}
</style>
