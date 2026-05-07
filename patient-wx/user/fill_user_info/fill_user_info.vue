<template>
    <view>
        <view class="top-container">
            <view class="step-container">
                <view class="icon-1">
                    <u-avatar v-if="photo" :src="photo" size="80"></u-avatar>
                    <text class="desc">个人基本信息</text>
                </view>
                <view class="line"></view>
                
            </view>
        </view>
        <view class="info-1-container" v-if="showInfoPanel == 1">
            <text class="title">填写个人基本信息</text>
            <text class="desc">填写如下信息</text>
            <u-form
                labelPosition="top"
                :model="dataForm"
                :rules="rules"
                ref="form1"
                labelWidth="110"
                :labelStyle="labelStyle"
            >
                <u-form-item
                    label="本人姓名"
                    prop="name"
                    borderBottom
                    ref="name"
                    leftIcon="account"
                    :leftIconStyle="iconStyle"
                    required
                >
                    <u-input v-model="dataForm.name" border="none" placeholder="输入姓名" />
                </u-form-item>
                <u-form-item
                    label="性别"
                    prop="sex"
                    borderBottom
                    ref="sex"
                    @click="showSex = true"
                    leftIcon="man"
                    :leftIconStyle="iconStyle"
                    required
                >
                    <u-input
                        v-model="dataForm.sex"
                        disabled
                        disabledColor="#ffffff"
                        placeholder="请选择性别"
                        border="none"
                    ></u-input>
                    <u-icon slot="right" name="arrow-right"></u-icon>
                </u-form-item>
                <u-form-item
                    label="身份证号"
                    prop="pid"
                    borderBottom
                    ref="sex"
                    leftIcon="file-text"
                    :leftIconStyle="iconStyle"
                    required
                >
                    <u-input v-model="dataForm.pid" border="none" placeholder="输入身份证号码" />
                    <view class="ocr">
                        <u-icon slot="right" size="26" :name="img['icon-1']" @tap="scanIdcardFront"></u-icon>
                    </view>
                </u-form-item>
                <u-form-item
                    label="手机号码"
                    prop="tel"
                    borderBottom
                    ref="tel"
                    leftIcon="phone"
                    :leftIconStyle="iconStyle"
                    required
                >
                    <u-input v-model="dataForm.tel" border="none" placeholder="输入手机号码" />
                </u-form-item>
                <u-form-item
                    label="出生日期"
                    prop="birthday"
                    borderBottom
                    ref="birthday"
                    @click="showDatetime = true"
                    leftIcon="calendar"
                    :leftIconStyle="iconStyle"
                    required
                >
                    <u-input
                        v-model="dataForm.birthday"
                        disabled
                        disabledColor="#ffffff"
                        placeholder="请选择您的生日"
                        border="none"
                    ></u-input>
                    <u-icon slot="right" name="arrow-right"></u-icon>
                </u-form-item>
				
				
				
            </u-form>
             <view class="operate">
				<u-button type="primary" text="提交信息" size="large" color="#2074fd" @click="submitHandle"></u-button>
			 </view>
        </view>
        
        <u-action-sheet
            :show="showSex"
            :actions="sexList"
            title="请选择性别"
            @close="
                showSex = false;
                this.$refs.form1.validateField('sex');
            "
            @select="selectSex"
        ></u-action-sheet>
        <u-datetime-picker
            :show="showDatetime"
            mode="date"
            minDate="-2209017600000"
            @confirm="confirmBirthday"
            @cancel="
                showDatetime = false;
                this.$refs.form1.validateField('birthday');
            "
        ></u-datetime-picker>
		
        <image :src="bannerUrl" mode="widthFix" class="banner"></image>
    </view>
</template>

<script>
const dayjs = require('dayjs');

export default {
    data() {
        return {
            img: {
                'icon-1': `${this.patientUrl}/page/user/fill_user_info/icon-1.png`
            },
            photo: '',
            flag: 'insert',
            showInfoPanel: 1,
            labelStyle: {
                color: '#2074fd',
                'font-weight': 'bold'
            },
            iconStyle: {
                color: '#0764FD',
                'font-size': '34rpx',
                'margin-top': '3rpx'
            },
            showSex: false,
            showDatetime: false,
            sexList: [
                {
                    name: '男'
                },
                {
                    name: '女'
                }
            ],
			
            validate: true,
            rules: {
                name: [
                    {
                        required: true,
                        message: '请输入姓名',
                        trigger: ['change', 'blur']
                    },
                    {
                        type: 'string',
                        pattern: '^[\\u4e00-\\u9fa5]{2,15}$',
                        required: true,
                        message: '姓名不正确',
                        trigger: ['blur', 'change']
                    }
                ],
                sex: {
                    type: 'string',
                    max: 1,
                    required: true,
                    message: '请选择男或女',
                    trigger: ['blur', 'change']
                },
				education: {
				    type: 'string',
				    max: 1,
				    required: true,
				    message: '请选择学历',
				    trigger: ['blur', 'change']
				},
                pid: [
                    {
                        required: true,
                        message: '请输入身份证号码',
                        trigger: ['change', 'blur']
                    },
                    {
                        type: 'string',
                        pattern:
                            '^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$',
                        required: true,
                        message: '身份证号码不正确',
                        trigger: ['blur', 'change']
                    }
                ],
                tel: [
                    {
                        required: true,
                        message: '请输入手机号码',
                        trigger: ['change', 'blur']
                    },
                    {
                        type: 'string',
                        pattern: '^1[0-9]{10}$',
                        required: true,
                        message: '手机号码不正确',
                        trigger: ['blur', 'change']
                    }
                ],
                birthday: {
                    type: 'string',
                    required: true,
                    message: '请选择出生日期',
                    trigger: ['blur', 'change']
                }
            },
            dataForm: {
                id: null,
                name: null,
                sex: null,
                pid: null,
                tel: null,
                birthday: null,
                medicalHistory: ['无'],
                insuranceType: '无',
				workerId: null
            },
            bannerUrl: `${this.patientUrl}/banner/banner-4.jpg`
        };
    },
    methods: {
        selectSex: function(e) {
            this.dataForm.sex = e.name;
        },
        selectWorker: function(e) {
			console.log(e.workerId);
            this.dataForm.workerId = e.workerId;
        },
        confirmBirthday: function(e) {
            this.showDatetime = false;
            let date = new Date();
            date.setTime(e.value);
            this.dataForm.birthday = dayjs(date).format('YYYY-MM-DD');
            this.$refs.form1.validateField('birthday');
        },

        nextHandle: function() {
            let that = this;
            that.validate = true;
            that.$refs.form1.validateField('name', function(errors) {
                that.validateFieldHandle(that, errors);
            });
            that.$refs.form1.validateField('pid', function(errors) {
                that.validateFieldHandle(that, errors);
            });
            that.$refs.form1.validateField('tel', function(errors) {
                that.validateFieldHandle(that, errors);
            });
            that.$refs.form1.validateField('sex', function(errors) {
                that.validateFieldHandle(that, errors);
            });
            that.$refs.form1.validateField('birthday', function(errors) {
                that.validateFieldHandle(that, errors);
            });
            //以上验证是异步的，所以需要定时器预估时间
            setTimeout(function() {
                if (!that.validate) {
                    uni.showToast({
                        icon: 'error',
                        title: '数据不正确'
                    });
                    return;
                }
                that.showInfoPanel = 2;
            }, 500);
        },
        
        validateFieldHandle: function(ref, errors) {
            if (errors.length > 0) {
                ref.validate = false;
            }
        },
		
		

        insuranceTypeHandle: function(name) {
            this.dataForm.insuranceType = name;
        },

        submitHandle: function() {
            let that = this;
            uni.showModal({
                title: '提示消息',
                content: that.flag == 'insert' ? '确定创建？' : '确定更新信息？',
                success: function(resp) {
                    if (resp.confirm) {
                        let data = that.dataForm;
                        let url = that.flag == 'insert' ? that.api.insertUserInfoCard : that.api.updateUserInfoCard;
                        that.ajax(url, 'POST', data, function(resp) {
                            uni.showToast({
                                icon: 'success',
                                title: that.flag == 'insert' ? '创建成功' : '更新成功'
                            });
                            setTimeout(function() {
                                uni.switchTab({
                                    url: '/pages/mine/mine'
                                });
                            }, 1500);
                        });
                    }
                }
            });
        }

    },
    onLoad: function() {
        let that = this;
        that.ajax(
            that.api.searchUserInfoCard,
            'GET',
            {},
            function(resp) {
                let result = resp.data.result;
                if (result && Object.keys(result).length > 0) {
                    that.flag = 'update';
                    that.dataForm.id = result.id;
                    that.dataForm.name = result.name;
                    that.dataForm.sex = result.sex;
                    that.dataForm.pid = result.pid;
                    that.dataForm.tel = result.tel;
                    that.dataForm.birthday = result.birthday;
                    
                    if (result.medicalHistory) {
                        try {
                            that.dataForm.medicalHistory = JSON.parse(result.medicalHistory);
                        } catch (e) {
                            that.dataForm.medicalHistory = ['无'];
                        }
                    }
                    
                    that.dataForm.insuranceType = result.insuranceType || '无';
                    that.dataForm.workerId = result.workerId;
                    
                    if (result.photo) {
                        that.photo = that.minioUrl + result.photo;
                    }
                } else {
                    that.flag = 'insert';
                }
            },
            false
        );
    }

};
</script>

<style lang="less">
@import url(fill_user_info.less);
</style>
