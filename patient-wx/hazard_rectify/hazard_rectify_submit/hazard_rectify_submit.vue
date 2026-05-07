<template>
	<view class="container">
		<view class="section">
			<view class="section-title">隐患信息</view>
			<view class="info-list">
				<view class="info-item">
					<text class="label">隐患等级</text>
					<text class="value level" :class="hazardInfo.hazardLevel === '重大' ? 'level-danger' : (hazardInfo.hazardLevel === '较大' ? 'level-warning' : 'level-info')">{{ hazardInfo.hazardLevel }}</text>
				</view>
				<view class="info-item">
					<text class="label">隐患分类</text>
					<text class="value">{{ hazardInfo.hazardType }}</text>
				</view>
				<view class="info-item">
					<text class="label">整改期限</text>
					<text class="value">{{ formatDateRange(hazardInfo.rectifyStartDate, hazardInfo.rectifyEndDate) }}</text>
				</view>
				<view class="info-item desc-item">
					<text class="label">隐患描述</text>
					<text class="value desc">{{ hazardInfo.hazardDesc }}</text>
				</view>
			</view>
		</view>

		<view class="section">
			<view class="section-title">整改照片（最多3张）</view>
			<view class="photo-section">
				<view class="photo-list">
					<view class="photo-item" v-for="(photo, index) in rectifyPhotos" :key="index">
						<image class="photo-img" :src="photo" mode="aspectFill" @tap="previewPhoto(index)"></image>
						<view class="photo-delete" @tap="deletePhoto(index)">
							<u-icon name="close" size="12" color="#fff"></u-icon>
						</view>
					</view>
					<view class="photo-add" @tap="choosePhoto" v-if="rectifyPhotos.length < 3">
						<u-icon name="plus" size="24" color="#c0c4cc"></u-icon>
						<text class="add-text">拍照({{ 3 - rectifyPhotos.length }})</text>
					</view>
				</view>
			</view>
		</view>

		<view class="section">
			<view class="section-title">整改视频（可选）</view>
			<view class="video-section">
				<view v-if="rectifyVideo" class="video-preview">
					<video
						class="video-player"
						:src="rectifyVideo"
						controls
						object-fit="contain"
					></video>
					<view class="video-delete" @tap="deleteVideo">
						<u-icon name="close" size="16" color="#f56c6c"></u-icon>
						<text>删除视频</text>
					</view>
				</view>
				<view v-else class="video-add" @tap="chooseVideo">
					<u-icon name="plus" size="32" color="#409eff"></u-icon>
					<text class="add-text">添加整改视频</text>
				</view>
			</view>
		</view>

		<view class="submit-btn" @tap="submitRectification" :class="{ disabled: isSubmitting }">
			<text>{{ isSubmitting ? '提交中...' : '提交整改' }}</text>
		</view>

		<u-toast ref="uToast"></u-toast>
	</view>
</template>

<script>
export default {
	data() {
		return {
			hazardId: null,
			hazardInfo: {},
			rectifyPhotos: [],
			rectifyVideo: '',
			isSubmitting: false
		};
	},
	methods: {
		onLoad(options) {
			this.hazardId = options.id;
			this.loadHazardDetail();
		},

		loadHazardDetail() {
			let that = this;
			that.ajax(that.api.searchHazardById, 'POST', { id: that.hazardId }, function(resp) {
				if (resp.data.code === 200 && resp.data.result) {
					that.hazardInfo = resp.data.result;
				} else {
					uni.showToast({ icon: 'none', title: '加载失败' });
				}
			}, true);
		},

		choosePhoto() {
			let that = this;
			let remainCount = 3 - that.rectifyPhotos.length;

			uni.chooseImage({
				count: remainCount,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success(res) {
					const tempFilePaths = res.tempFilePaths;
					tempFilePaths.forEach(filePath => {
						that.uploadPhoto(filePath, that.rectifyPhotos.length);
					});
				}
			});
		},

		uploadPhoto(filePath, index) {
			let that = this;
			uni.showLoading({ title: '上传中...' });

			uni.uploadFile({
				url: that.api.uploadRectificationPhoto,
				filePath: filePath,
				name: 'file',
				header: {
					token: uni.getStorageSync('token')
				},
				formData: {
					hazardId: that.hazardId,
					index: index + 1
				},
				success(res) {
					uni.hideLoading();
					if (res.statusCode === 200) {
						let data = JSON.parse(res.data);
						if (data.code === 200 && data.result) {
							let photoPath = data.result.path;
							that.rectifyPhotos.push(that.rectificationMinioUrl + photoPath);
						} else {
							uni.showToast({
								icon: 'none',
								title: data.msg || '上传失败'
							});
						}
					} else {
						uni.showToast({
							icon: 'none',
							title: '上传失败'
						});
					}
				},
				fail() {
					uni.hideLoading();
					uni.showToast({
						icon: 'none',
						title: '上传异常'
					});
				}
			});
		},

		deletePhoto(index) {
			this.rectifyPhotos.splice(index, 1);
		},

		previewPhoto(current) {
			uni.previewImage({
				current: current,
				urls: this.rectifyPhotos
			});
		},

		chooseVideo() {
			let that = this;

			uni.chooseVideo({
				sourceType: ['album', 'camera'],
				compressed: true,
				maxDuration: 60,
				camera: 'back',
				success(res) {
					let tempFilePath = res.tempFilePath;
					that.uploadVideo(tempFilePath);
				},
				fail(err) {
					console.log('选择视频失败', err);
				}
			});
		},

		uploadVideo(filePath) {
			let that = this;
			uni.showLoading({ title: '上传视频中...' });

			uni.uploadFile({
				url: that.api.uploadRectificationVideo,
				filePath: filePath,
				name: 'file',
				header: {
					token: uni.getStorageSync('token')
				},
				formData: {
					hazardId: that.hazardId
				},
				success(res) {
					uni.hideLoading();
					if (res.statusCode === 200) {
						let data = JSON.parse(res.data);
						if (data.code === 200 && data.result) {
							let videoPath = data.result.path;
							that.rectifyVideo = that.rectificationMinioUrl + videoPath;
						} else {
							uni.showToast({
								icon: 'none',
								title: data.msg || '上传失败'
							});
						}
					} else {
						uni.showToast({
							icon: 'none',
							title: '上传失败'
						});
					}
				},
				fail() {
					uni.hideLoading();
					uni.showToast({
						icon: 'none',
						title: '上传异常'
					});
				}
			});
		},

		deleteVideo() {
			this.rectifyVideo = '';
		},

		submitRectification() {
			let that = this;

			if (that.isSubmitting) return;

			if (that.rectifyPhotos.length === 0 && !that.rectifyVideo) {
				uni.showToast({
					icon: 'none',
					title: '请至少上传一张整改照片或一个视频'
				});
				return;
			}

			that.isSubmitting = true;

			let photoPaths = [];
			that.rectifyPhotos.forEach(photo => {
				if (photo.startsWith('http')) {
					try {
						let urlObj = new URL(photo);
						let pathFromUrl = photo.replace(that.rectificationMinioUrl, '');
						photoPaths.push(pathFromUrl);
					} catch(e) {
						let pathFromUrl = photo.replace(that.rectificationMinioUrl, '');
						photoPaths.push(pathFromUrl);
					}
				} else if (photo) {
					photoPaths.push(photo);
				}
			});

			let videoPath = '';
			if (that.rectifyVideo) {
				if (that.rectifyVideo.startsWith('http')) {
					videoPath = that.rectifyVideo.replace(that.rectificationMinioUrl, '');
				} else {
					videoPath = that.rectifyVideo;
				}
			}

			let data = {
				id: that.hazardId,
				rectifyPhoto: photoPaths.join(','),
				rectifyVideo: videoPath
			};

			that.ajax(that.api.submitRectification, 'POST', data, function(resp) {
				that.isSubmitting = false;

				if (resp.data.code === 200) {
					uni.showToast({
						icon: 'success',
						title: '整改提交成功'
					});

					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				} else {
					uni.showToast({
						icon: 'none',
						title: resp.data.msg || '提交失败'
					});
				}
			}, true);
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
	padding-bottom: 140rpx;
}

.section {
	margin: 20rpx 24rpx;
	background-color: #fff;
	border-radius: 16rpx;
	padding: 28rpx;

	.section-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 24rpx;
		padding-bottom: 16rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}
}

.info-list {
	.info-item {
		display: flex;
		padding: 16rpx 0;
		font-size: 28rpx;
		line-height: 1.5;

		.label {
			color: #999;
			width: 180rpx;
			flex-shrink: 0;
		}

		.value {
			color: #333;
			flex: 1;

			&.level-danger { color: #f56c6c; font-weight: bold; }
			&.level-warning { color: #e6a23c; font-weight: bold; }
		}

		&.desc-item {
			flex-direction: column;

			.label {
				width: auto;
				margin-bottom: 12rpx;
			}

			.desc {
				width: 100%;
				line-height: 1.6;
			}
		}
	}
}

.photo-section {
	.photo-list {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
	}

	.photo-item {
		position: relative;
		width: 200rpx;
		height: 200rpx;

		.photo-img {
			width: 100%;
			height: 100%;
			border-radius: 12rpx;
		}

		.photo-delete {
			position: absolute;
			top: -10rpx;
			right: -10rpx;
			width: 40rpx;
			height: 40rpx;
			background-color: #f56c6c;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}

	.photo-add {
		width: 200rpx;
		height: 200rpx;
		border: 2rpx dashed #dcdfe6;
		border-radius: 12rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 8rpx;

		.add-text {
			font-size: 22rpx;
			color: #909399;
		}
	}
}

.video-section {
	.video-preview {
		position: relative;

		.video-player {
			width: 100%;
			height: 400rpx;
			border-radius: 12rpx;
		}

		.video-delete {
			position: absolute;
			top: 16rpx;
			right: 16rpx;
			background-color: rgba(255, 255, 255, 0.9);
			padding: 8rpx 16rpx;
			border-radius: 20rpx;
			display: flex;
			align-items: center;
			gap: 6rpx;
			font-size: 24rpx;
			color: #f56c6c;
		}
	}

	.video-add {
		height: 200rpx;
		border: 2rpx dashed #409eff;
		border-radius: 12rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 12rpx;

		.add-text {
			font-size: 26rpx;
			color: #409eff;
		}
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

	&.disabled {
		background-color: #a0cfff;
		pointer-events: none;
	}
}
</style>
