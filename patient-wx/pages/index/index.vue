<template>
	<view class="page">
		<u-swiper height="230" interval="10000" :list="list1" easingFunction="linear" radius="0" indicator></u-swiper>
		<view class="top-panel">
			<image :src="img['icon-1']" mode="widthFix" class="icon" @tap="toPage('/registration/notice/notice')" />
			
		</view>
		
		
		
		<view class="publicity-container"><image :src="publicityBannerUrl" mode="widthFix" class="banner" /></view>
		<view class="doctor-container">
			
			<u-tabs :list="tab.list" @click="clickTabHandle"></u-tabs>
			<view class="panel">
				<view class="doctor" v-for="one in doctor">
					<u-avatar :src="one.photo" size="45"></u-avatar>
					<view class="info">
						<view class="row">
							<text class="name">{{ one.name }}</text>
							<text class="job">{{ one.job }}</text>
							<button class="btn">挂号</button>
						</view>
						<view class="row">
							<text class="remark">{{ one.remark }}</text>
						</view>
						<view class="row">
							<rich-text class="desc">{{ one.description }}</rich-text>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="ad-container"><image :src="adBannerUrl" mode="widthFix" class="banner" /></view>
		<view class="rescue-container">
			
			<u-grid :border="false" @click="click" col="3">
				<u-grid-item><image :src="rescue[0]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[1]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[2]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[3]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[4]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[5]" mode="widthFix" class="pic"></image></u-grid-item>
			</u-grid>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			img: {
				'icon-1': `${this.patientUrl}/page/index/icon-1.png`
			},
			list1: [
				`${this.patientUrl}/swiper/swiper-1.jpg`,
				`${this.patientUrl}/swiper/swiper-2.jpg`,
				`${this.patientUrl}/swiper/swiper-3.jpg`,
				`${this.patientUrl}/swiper/swiper-4.jpg`
			],
			otherBannerUrl: `${this.patientUrl}/banner/banner-1.jpg`,
			publicityBannerUrl: `${this.patientUrl}/banner/banner-2.jpg`,
			adBannerUrl: `${this.patientUrl}/banner/banner-7.jpg`,
			tab: {
				list: [],
				current: 0,
				swipperStyle: ''
			},
			doctor: [],
			rescue: [
				`${this.patientUrl}/rescue/rescue-1.jpg`,
				`${this.patientUrl}/rescue/rescue-2.jpg`,
				`${this.patientUrl}/rescue/rescue-3.jpg`,
				`${this.patientUrl}/rescue/rescue-4.jpg`,
				`${this.patientUrl}/rescue/rescue-5.jpg`,
				`${this.patientUrl}/rescue/rescue-6.jpg`
			]
		};
	},
	onLoad() {

	},
	methods: {
		
	}
};
</script>

<style lang="less">
@import url(index.less);
</style>
