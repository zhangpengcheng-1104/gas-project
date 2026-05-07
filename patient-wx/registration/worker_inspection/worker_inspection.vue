<template>
	<view class="container">
		<view class="section">
			<view class="section-title">当前定位</view>
			<view class="form-item">
				<text class="label">行政区域</text>
				<text class="value">{{ areaName || '暂无' }}</text>
			</view>
			<view class="form-item">
				<text class="label">详细地址</text>
				<text class="value">{{ fullAddress || address || '暂无' }}</text>
			</view>
			<view class="map-container" v-if="latitude && longitude">
				<map 
					class="map" 
					:latitude="latitude" 
					:longitude="longitude" 
					:markers="markers"
					:scale="16"
				></map>
			</view>
		</view>

		<view class="section">
			<view class="section-title">环节</view>

			<view class="item-card" v-for="(item, index) in inspectItems" :key="index">
				<view class="item-header" @tap="toggleItem(index)">
					<text class="item-name">{{ item.name }}</text>
					<u-icon :name="item.expanded ? 'arrow-up' : 'arrow-down'" size="14" color="#999"></u-icon>
				</view>
				
				<view class="item-body" v-if="item.expanded">
					<view class="field-row">
						<text class="field-label required">有无隐患：</text>
						<radio-group @change="(e) => onDangerChange(e, index)">
							<label class="radio-label"><radio value="0" :checked="item.hasDanger === 0" />有</label>
							<label class="radio-label"><radio value="1" :checked="item.hasDanger === 1" />无</label>
						</radio-group>
					</view>
					
					<view class="field-row" v-if="item.hasDanger === 0">
						<text class="field-label required">建议整改措施：</text>
					</view>
					<textarea 
						v-if="item.hasDanger === 0"
						class="textarea" 
						v-model="item.desc"
						placeholder="请输入整改措施"
						placeholder-class="placeholder"
						:auto-height="true"
						:maxlength="255"
					></textarea>
					
					<view class="photo-section">
						<text class="field-label">巡检照片：</text>
						<view class="photo-list">
							<view class="photo-item" v-for="(photo, pIndex) in item.photos" :key="pIndex">
								<image class="photo-img" :src="photo" mode="aspectFill" @tap="previewPhoto(item.photos, pIndex)"></image>
								<view class="photo-delete" @tap="deletePhoto(index, pIndex)">
									<u-icon name="close" size="12" color="#fff"></u-icon>
								</view>
							</view>
							<view class="photo-add" @tap="choosePhoto(index)" v-if="item.photos.length < 3">
								<u-icon name="plus" size="24" color="#c0c4cc"></u-icon>
								<text class="add-text">拍照({{ 3 - item.photos.length }})</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="section">
			<view class="section-title">结果</view>
			
			<view class="form-item">
				<text class="label required">巡检结果：</text>
				<picker mode="selector" :range="resultOptions" range-key="name" @change="onResultChange" :value="resultIndex">
					<view class="picker-value">{{ resultOptions[resultIndex].name }}</view>
				</picker>
			</view>
		</view>

		<view class="submit-btn" @tap="submitInspection">提交</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			workerPlanId: null,
			areaName: '',
			address: '',
			fullAddress: '',
			latitude: null,
			longitude: null,
			markers: [],
			existingId: null,
			resultIndex: 0,
			resultOptions: [
				{ name: '完成巡检', value: '完成巡检' },
				{ name: '未用气', value: '未用气' },
				{ name: '拒绝入户', value: '拒绝入户' },
				{ name: '到访不遇', value: '到访不遇' }
			],
			inspectItems: [
				{ name: '燃气管道', hasDanger: 1, desc: '', photos: [], expanded: false },
				{ name: '燃气表', hasDanger: 1, desc: '', photos: [], expanded: false },
				{ name: '燃气具', hasDanger: 1, desc: '', photos: [], expanded: false },
				{ name: '连接配件', hasDanger: 1, desc: '', photos: [], expanded: false },
				{ name: '使用环境', hasDanger: 1, desc: '', photos: [], expanded: false }
			],
			amapConfig: null
		};
	},
	computed: {
		hasDanger() {
			return this.inspectItems.some(item => item.hasDanger === 0) ? 0 : 1;
		},
		allPhotos() {
			let photos = [];
			this.inspectItems.forEach(item => {
				item.photos.forEach(photo => {
					if (photo.startsWith('http')) {
						try {
							let urlObj = new URL(photo);
							let path = urlObj.searchParams.get('path');
							if (path) {
								photos.push(decodeURIComponent(path));
							} else {
								let pathFromUrl = photo.replace(this.inspectionMinioUrl, '');
								photos.push(pathFromUrl);
							}
						} catch(e) {
							let pathFromUrl = photo.replace(this.inspectionMinioUrl, '');
							photos.push(pathFromUrl);
						}
					} else if (photo) {
						photos.push(photo);
					}
				});
			});
			return photos.join(',');
		},
		allDesc() {
			let descs = [];
			this.inspectItems.forEach((item, index) => {
				if (item.desc) {
					descs.push('【' + item.name + '】' + item.desc);
				}
			});
			return descs.join('\n');
		}
	},
	methods: {
		onLoad(options) {
			let that = this;
			that.workerPlanId = options.workerPlanId;
			that.address = options.address || '';
			that.areaName = options.areaName || '';
			that.loadExistingInspection();
		},

		loadExistingInspection() {
			let that = this;
			that.ajax(that.api.searchByWorkerPlanId, 'POST', { workerPlanId: that.workerPlanId }, function(resp) {
				let result = resp.data.result;
				
				if (result && result.areaName) {
					that.areaName = result.areaName;
				}
				if (result && result.userAddress) {
					that.address = result.userAddress;
				}
				if (result && result.fullAddress) {
					that.fullAddress = result.fullAddress;
					that.loadMapLocation(result.fullAddress);
				}
				
				if (result && result.id) {
					that.existingId = result.id;
					
					if (result.inspectResult) {
						let idx = that.resultOptions.findIndex(r => r.value === result.inspectResult);
						if (idx >= 0) that.resultIndex = idx;
						
						if (result.inspectResult === '完成巡检') {
							that.parseAndFillInspectionData(result);
						}
					}
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
						that.markers = [{
							id: 1,
							latitude: that.latitude,
							longitude: that.longitude,
							title: address,
							width: 28,
							height: 28,
							callout: {
								content: address,
								color: '#333',
								fontSize: 12,
								borderRadius: 4,
								padding: 6,
								display: 'ALWAYS'
							}
						}];
					}
				}
			}, false);
		},

		parseAndFillInspectionData(result) {
			let that = this;
			
			if (result.inspectDesc) {
				let descLines = result.inspectDesc.split('\n');
				descLines.forEach(line => {
					let match = line.match(/【(.+?)】(.+)/);
					if (match) {
						let itemName = match[1];
						let desc = match[2];
						let item = that.inspectItems.find(i => i.name === itemName);
						if (item) {
							item.desc = desc;
							item.hasDanger = 0;
							item.expanded = true;
						}
					}
				});
			}
			
			if (result.inspectPhoto) {
				let photos = result.inspectPhoto.split(',').filter(p => p && p.trim());
				if (photos.length > 0) {
					let itemsWithDanger = that.inspectItems.filter(item => item.hasDanger === 0);
					let photosPerItem = itemsWithDanger.length > 0 ? Math.ceil(photos.length / itemsWithDanger.length) : 0;
					let photoIndex = 0;
					
					itemsWithDanger.forEach(item => {
						item.photos = [];
						for (let i = 0; i < photosPerItem && photoIndex < photos.length; i++) {
							let photoPath = photos[photoIndex];
							item.photos.push(that.inspectionMinioUrl + photoPath.trim());
							photoIndex++;
						}
					});
				}
			}
		},

		toggleItem(index) {
			this.inspectItems[index].expanded = !this.inspectItems[index].expanded;
		},

		onDangerChange(e, index) {
			this.inspectItems[index].hasDanger = parseInt(e.detail.value);
		},

		choosePhoto(itemIndex) {
			let that = this;
			uni.chooseImage({
				count: 3 - that.inspectItems[itemIndex].photos.length,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success(res) {
					const tempFilePaths = res.tempFilePaths;
					tempFilePaths.forEach(filePath => {
						that.uploadPhoto(filePath, itemIndex);
					});
				}
			});
		},

		uploadPhoto(filePath, itemIndex) {
			let that = this;
			uni.uploadFile({
				url: that.api.uploadInspectionPhoto,
				filePath: filePath,
				name: 'file',
				header: {
					token: uni.getStorageSync('token')
				},
				formData: {
					workerPlanId: that.workerPlanId
				},
				success(res) {
					if (res.statusCode === 200) {
						let data = JSON.parse(res.data);
						if (data.code === 200 && data.result) {
							let photoPath = data.result.photo;
							that.inspectItems[itemIndex].photos.push(
								that.inspectionMinioUrl + photoPath
							);
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
					uni.showToast({
						icon: 'none',
						title: '上传异常'
					});
				}
			});
		},

		deletePhoto(itemIndex, photoIndex) {
			this.inspectItems[itemIndex].photos.splice(photoIndex, 1);
		},

		previewPhoto(photos, current) {
			uni.previewImage({
				current: current,
				urls: photos
			});
		},

		onResultChange(e) {
			this.resultIndex = parseInt(e.detail.value);
			let selectedValue = this.resultOptions[this.resultIndex].value;
			
			if (selectedValue !== '完成巡检') {
				uni.showModal({
					title: '提示',
					content: `选择"${selectedValue}"后，环节信息将不需要填写，确定吗？`,
					success(res) {
						if (!res.confirm) {
							// revert
						}
					}
				});
			}
		},

		submitInspection() {
			let that = this;
			let result = that.resultOptions[that.resultIndex].value;

			if (result === '完成巡检') {
				for (let i = 0; i < that.inspectItems.length; i++) {
					let item = that.inspectItems[i];
					if (item.hasDanger === 0 && !item.desc.trim()) {
						uni.showToast({
							icon: 'none',
							title: `请填写${item.name}的整改措施`
						});
						return;
					}
				}
			}

			let data = {
				workerPlanId: that.workerPlanId,
				hasDanger: that.hasDanger,
				inspectDesc: that.allDesc,
				inspectPhoto: that.allPhotos,
				inspectResult: result
			};

			let url = that.existingId ? that.api.updateInspection : that.api.saveInspection;
			if (that.existingId) {
				data.id = that.existingId;
			}

			that.ajax(url, 'POST', data, function(resp) {
				uni.showToast({
					icon: 'success',
					title: '提交成功'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
			}, true);
		}
	}
};
</script>

<style lang="less">
@import url(worker_inspection.less);
</style>
