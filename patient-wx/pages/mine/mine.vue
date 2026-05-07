<template>
	<view>
		<view class="top-container">
			<u-avatar :src="user.photo || '/static/default-avatar.png'" shape="square" size="52"></u-avatar>
			<view class="info">
				<view v-if="flag == 'logout'" open-type="getUserInfo" @tap="loginOrRegister">
					<text class="operate">注册 / 登陆</text>
					<text class="remark">请登陆小程序</text>
				</view>
				<view v-if="flag == 'login'">
					<text class="username">{{ user.username || '微信用户' }}</text>
					<text class="tel">{{ user.tel != null ? user.tel : '未绑定手机（需实名登记）' }}</text>
				</view>
			</view>
		</view>
		<view class="navigator-container">
			<u-grid :border="false" col="4" @click="navigatorHandle">
				<u-grid-item name="实名登记">
					<view class="navigator-icon"><view class="navigator-icon-1" /></view>
					<text class="title">实名登记</text>
				</u-grid-item>
				<u-grid-item name="我的信息">
					<view class="navigator-icon"><view class="navigator-icon-2" /></view>
					<text class="title">我的信息</text>
				</u-grid-item>
				<u-grid-item name="我的任务">
					<view class="navigator-icon"><view class="navigator-icon-3" /></view>
					<text class="title">我的任务</text>
				</u-grid-item>
				<u-grid-item name="隐患整改">
					<view class="navigator-icon"><view class="navigator-icon-4" /></view>
					<text class="title">隐患整改</text>
				</u-grid-item>
			</u-grid>
		</view>
		
		<view class="ad-container"><image :src="adBannerUrl[1]" mode="widthFix" class="banner" /></view>
		<u-toast ref="uToast" />
	</view>
</template>

<script>
export default {
	data() {
		return {
			img: {
				'mine-icon-1': `${this.patientUrl}/page/mine/mine-icon-1.png`,
				'mine-icon-2': `${this.patientUrl}/page/mine/mine-icon-2.png`,
				'mine-icon-3': `${this.patientUrl}/page/mine/mine-icon-3.png`,
				'mine-icon-4': `${this.patientUrl}/page/mine/mine-icon-4.png`,
				'mine-icon-5': `${this.patientUrl}/page/mine/mine-icon-5.png`,
				'service-icon-1': `${this.patientUrl}/page/mine/service-icon-1.png`,
				'service-icon-2': `${this.patientUrl}/page/mine/service-icon-2.png`,
				'service-icon-3': `${this.patientUrl}/page/mine/service-icon-3.png`,
				'service-icon-4': `${this.patientUrl}/page/mine/service-icon-4.png`,
				'service-icon-5': `${this.patientUrl}/page/mine/service-icon-5.png`,
				'service-icon-6': `${this.patientUrl}/page/mine/service-icon-6.png`,
				'service-icon-7': `${this.patientUrl}/page/mine/service-icon-7.png`,
				'service-icon-8': `${this.patientUrl}/page/mine/service-icon-8.png`,
				'service-icon-9': `${this.patientUrl}/page/mine/service-icon-9.png`,
				'service-icon-10': `${this.patientUrl}/page/mine/service-icon-10.png`,
				'service-icon-11': `${this.patientUrl}/page/mine/service-icon-11.png`,
				'service-icon-12': `${this.patientUrl}/page/mine/service-icon-12.png`,
				'service-icon-13': `${this.patientUrl}/page/mine/service-icon-13.png`,
				'service-icon-14': `${this.patientUrl}/page/mine/service-icon-14.png`,
				'service-icon-15': `${this.patientUrl}/page/mine/service-icon-15.png`
			},
			flag: 'logout',
			code: null,
			user: {
				username: '神思者',
				photo: null,
				tel: null
			},
			bannerUrl: `${this.patientUrl}/banner/banner-5.jpg`,
			publicityBannerUrl: `${this.patientUrl}/banner/banner-2.jpg`,
			otherBannerUrl: `${this.patientUrl}/banner/banner-1.jpg`,
			adBannerUrl: [`${this.patientUrl}/banner/banner-8.jpg`, `${this.patientUrl}/banner/banner-5.jpg`]
		};
	},
	methods: {
		loginOrRegister: function() {
		    let that = this;
		    //获取微信用户的临时授权
		    uni.login({
		        provider: 'weixin',
		        success: function(resp) {
		            let code = resp.code;
		            that.code = code;
		        }
		    });
		
		    //获取用户的微信资料用于注册
		    uni.getUserProfile({
		        desc: '获取用户信息',
		        success: function(resp) {
		            let info = resp.userInfo;
		            let nickname = info.nickName; //昵称
		            let photo = info.avatarUrl;  //头像URL
		            let sex = info.gender == 0 ? '男' : '女'; //性别
		            let data = {
		                code: that.code,
		                nickname: nickname,
		                photo: photo,
		                sex: sex
		            };
		            //提交 Ajax 请求，执行登陆或注册
		            that.ajax(that.api.loginOrRegister, 'POST', data, function(resp) {
		                let msg = resp.data.msg;
		                that.$refs.uToast.show({
		                    message: msg,
		                    type: 'success',
		                    duration: 1200,
		                    complete: function() {
		                        let token = resp.data.token;
		                        //把 Token 保存到 Storage
		                        uni.setStorageSync('token', token);
		                        //更新页面标志位变量
		                        that.flag = 'login';
		                        that.user.username = nickname || '微信用户';
		                        that.user.photo = photo || '';
		                        //如果用户创建了患者信息卡，就把电话显示在页面上
		                        if (resp.data.hasOwnProperty('tel')) {
		                            that.user.tel = resp.data.tel;
		                        }
		                        console.log('Login success, user:', that.user);
		                    }
		                });
		            });
		        }
		    });
		},
		
		navigatorHandle: function(name) {
		    let token = uni.getStorageSync('token');
		    //跳转到某些医疗服务页面之前，先判断是否登陆
		    if (token == null || token.length == 0) {
		        uni.showToast({
		            icon: 'error',
		            title: '请先登录小程序'
		        });
		        return;
		    }
		
		    let url = null;
        if (name == '实名登记') {
            url = '/user/fill_user_info/fill_user_info';
        } 
        else if (name == '我的信息') {
			url = '/user/gas_worker_info/gas_worker_info';
        } 
        else if (name == '我的任务') {
			url = '/registration/worker_schedule/worker_plan';
        } 
        else if (name == '隐患整改') {
			url = '/hazard_rectify/hazard_rectify_list/hazard_rectify_list';
        } 
        
        
    //TODO 其他判断条件
    uni.navigateTo({
        url: url
    });
		},

	},
	

	onShow: function() {
	    let that = this;
	    let token = uni.getStorageSync('token');
	    if (token != null && token.length > 0) {
	        that.ajax(
	            that.api.searchUserInfo,
	            'GET',
	            {},
	            function(resp) {
	                if (resp.statusCode === 401) {
	                    uni.removeStorageSync('token');
	                    that.flag = 'logout';
	                    that.user = {
	                        username: '神思者',
	                        photo: null,
	                        tel: null
	                    };
	                } else if (resp.data.code === 200 && resp.data.hasOwnProperty('result')) {
	                    that.flag = 'login';
	                    let result = resp.data.result;
	                    that.user.username = result.nickname || '微信用户';
	                    that.user.tel = result.tel;
	                    that.ajax(
	                        that.api.searchContent,
	                        'POST',
	                        {},
	                        function(resp2) {
	                            let data = resp2.data.result;
	                            if (data && data.photo) {
	                                that.user.photo = that.minioUrl + data.photo;
	                            } else {
	                                that.user.photo = result.photo || '';
	                            }
	                        },
	                        false
	                    );
	                }
	            },
	            false
	        );
	    }
	}

};
</script>

<style lang="less">
@import url(mine.less);
</style>
