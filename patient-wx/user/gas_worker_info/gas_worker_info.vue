<template>
    <view>
        <view class="top-container">
            <view class="header-title">巡检员基本信息</view>
        </view>
        <view class="info-1-container" v-if="hasData">
            <view class="avatar-container">
                <u-avatar :src="photo" size="80"></u-avatar>
            </view>

            <u-form
                labelPosition="left"
                :model="dataForm"
                ref="form1"
                labelWidth="100"
                :labelStyle="labelStyle"
            >
                <u-form-item
                    label="姓名"
                    borderBottom
                    leftIcon="account"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.name }}</text>
                </u-form-item>
                <u-form-item
                    label="性别"
                    borderBottom
                    leftIcon="man"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.sex }}</text>
                </u-form-item>
                <u-form-item
                    label="身份证号"
                    borderBottom
                    leftIcon="file-text"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.pid }}</text>
                </u-form-item>
                <u-form-item
                    label="手机号码"
                    borderBottom
                    leftIcon="phone"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.tel }}</text>
                </u-form-item>
                <u-form-item
                    label="出生日期"
                    borderBottom
                    leftIcon="calendar"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.birthday }}</text>
                </u-form-item>
                <u-form-item
                    label="学历"
                    borderBottom
                    leftIcon="level"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.degree || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="毕业院校"
                    borderBottom
                    leftIcon="home"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.school || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="职位"
                    borderBottom
                    leftIcon="order"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.job || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="入职日期"
                    borderBottom
                    leftIcon="calendar"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.hiredate || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="邮箱"
                    borderBottom
                    leftIcon="email"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.email || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="地址"
                    borderBottom
                    leftIcon="map"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.address || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="备注"
                    borderBottom
                    leftIcon="edit-pen"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.remark || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="简介"
                    borderBottom
                    leftIcon="info-circle"
                    :leftIconStyle="iconStyle"
                >
                    <text class="value-text">{{ dataForm.description || '-' }}</text>
                </u-form-item>
                <u-form-item
                    label="标签"
                    borderBottom
                    leftIcon="tags"
                    :leftIconStyle="iconStyle"
                    v-if="dataForm.tag && dataForm.tag.length > 0"
                >
                    <view class="tag-container">
                        <view class="tag-item" v-for="(item, index) in dataForm.tag" :key="index">
                            {{ item }}
                        </view>
                    </view>
                </u-form-item>
            </u-form>
        </view>
        <view class="info-1-container" v-else>
            <view class="empty-container">
                <u-empty text="暂无巡检员信息，请先完成实名登记" mode="data"></u-empty>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            photo: '',
            hasData: false,
            labelStyle: {
                color: '#2074fd',
                'font-weight': 'bold'
            },
            iconStyle: {
                color: '#0764FD',
                'font-size': '34rpx',
                'margin-top': '3rpx'
            },
            dataForm: {
                id: null,
                name: null,
                sex: null,
                pid: null,
                tel: null,
                birthday: null,
                photo: null,
                school: null,
                degree: null,
                address: null,
                email: null,
                job: null,
                remark: null,
                description: null,
                hiredate: null,
                tag: []
            }
        };
    },
    methods: {
    },
    onLoad: function(ref) {
        let that = this;
        that.ajax(that.api.searchContent, 'POST', {}, function(resp) {
            let data = resp.data.result;
            if (data && Object.keys(data).length > 0) {
                that.hasData = true;
                that.dataForm.id = data.id;
                that.dataForm.name = data.name;
                that.dataForm.sex = data.sex;
                that.dataForm.pid = data.pid;
                that.dataForm.tel = data.tel;
                that.dataForm.birthday = data.birthday;
                that.dataForm.photo = data.photo;
                that.dataForm.school = data.school;
                that.dataForm.degree = data.degree;
                that.dataForm.address = data.address;
                that.dataForm.email = data.email;
                that.dataForm.job = data.job;
                that.dataForm.remark = data.remark;
                that.dataForm.description = data.description;
                that.dataForm.hiredate = data.hiredate;
                that.dataForm.tag = data.tag || [];
                
                if (data.photo) {
                    that.photo = `${that.minioUrl}${data.photo}`;
                }
            } else {
                that.hasData = false;
            }
        }, true);
    }
};
</script>

<style lang="less">
@import url(gas_worker_info.less);

.top-container {
    background-color: #2074fd;
    padding: 40rpx 30rpx;
}

.header-title {
    color: #fff;
    font-size: 36rpx;
    font-weight: bold;
    text-align: center;
}

.avatar-container {
    display: flex;
    justify-content: center;
    padding: 30rpx 0;
    margin-bottom: 20rpx;
}

.value-text {
    color: #333;
    font-size: 28rpx;
}

.tag-container {
    display: flex;
    flex-wrap: wrap;
    padding: 10rpx 0;
}

.tag-item {
    background-color: #e8f4ff;
    color: #2074fd;
    padding: 10rpx 20rpx;
    margin-right: 15rpx;
    margin-bottom: 10rpx;
    border-radius: 8rpx;
    font-size: 24rpx;
}

.empty-container {
    padding: 100rpx 0;
}
</style>
