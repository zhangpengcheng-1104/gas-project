<template>
    <el-dialog
        :title="!dataForm.id ? '新增隐患信息' : '修改隐患信息'"
        :close-on-click-modal="false"
        v-model="visible"
        width="750px"
    >
        <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="100px">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="隐患分类" prop="hazardType">
                        <el-select v-model="dataForm.hazardType" placeholder="请选择隐患分类" style="width: 100%" size="medium">
                            <el-option label="上报" value="上报" />
                            <el-option label="预警" value="预警" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="隐患等级" prop="hazardLevel">
                        <el-select v-model="dataForm.hazardLevel" placeholder="请选择隐患等级" style="width: 100%" size="medium">
                            <el-option label="重大" value="重大" />
                            <el-option label="较大" value="较大" />
                            <el-option label="一般" value="一般" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="隐患来源" prop="hazardSource">
                        <el-select v-model="dataForm.hazardSource" placeholder="请选择隐患来源" style="width: 100%" size="medium">
                            <el-option label="管网" value="管网" />
                            <el-option label="厂站" value="厂站" />
                            <el-option label="三方施工" value="三方施工" />
                            <el-option label="用户" value="用户" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="关联用户" prop="userId">
                        <el-select v-model="dataForm.userId" placeholder="请选择关联用户" style="width: 100%" size="medium" filterable clearable>
                            <el-option v-for="user in userList" :key="user.id" :label="user.userName" :value="user.id" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="行政区域" prop="areaCode">
                        <el-cascader
                            v-model="dataForm.areaCodeArr"
                            :options="areaOptions"
                            :props="areaProps"
                            placeholder="请选择行政区域"
                            style="width: 100%"
                            size="medium"
                            clearable
                            @change="handleAreaChange"
                        />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="详细地址" prop="address">
                        <el-input v-model="dataForm.address" size="medium" clearable placeholder="请输入详细地址或点击地图选择" maxlength="500">
                            <template #append>
                                <el-button icon="el-icon-location" @click="openMapSelector">地图选择</el-button>
                            </template>
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="经度">
                        <el-input v-model="dataForm.longitude" size="medium" placeholder="地图选点自动填充" disabled />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="纬度">
                        <el-input v-model="dataForm.latitude" size="medium" placeholder="地图选点自动填充" disabled />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="隐患描述" prop="hazardDesc">
                        <el-input v-model="dataForm.hazardDesc" type="textarea" :rows="3" placeholder="请输入隐患描述" maxlength="500" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="整改开始日期">
                        <el-date-picker
                            v-model="dataForm.rectifyStartDate"
                            type="date"
                            placeholder="选择整改开始日期"
                            size="medium"
                            style="width: 100%"
                            format="YYYY-MM-DD"
                            value-format="YYYY-MM-DD"
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="整改结束日期">
                        <el-date-picker
                            v-model="dataForm.rectifyEndDate"
                            type="date"
                            placeholder="选择整改结束日期"
                            size="medium"
                            style="width: 100%"
                            format="YYYY-MM-DD"
                            value-format="YYYY-MM-DD"
                        />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="提交人姓名" prop="submitUserName">
                        <el-input v-model="dataForm.submitUserName" size="medium" clearable placeholder="请输入提交人姓名" maxlength="100" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="指派人">
                        <el-select v-model="dataForm.assigneeId" placeholder="请选择指派人" style="width: 100%" size="medium" filterable clearable>
                            <el-option v-for="worker in workerList" :key="worker.id" :label="worker.name" :value="worker.id" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="是否本人整改">
                        <el-radio-group v-model="dataForm.isSelfRectify">
                            <el-radio :label="1">是</el-radio>
                            <el-radio :label="0">否</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="整改状态">
                        <el-select v-model="dataForm.rectifyStatus" placeholder="请选择整改状态" style="width: 100%" size="medium">
                            <el-option label="待整改" :value="0" />
                            <el-option label="已整改" :value="1" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="隐患图片">
                        <el-upload
                            class="avatar-uploader"
                            :action="uploadUrl"
                            :headers="uploadHeaders"
                            :data="uploadData"
                            :show-file-list="false"
                            :on-success="handleUploadSuccess"
                            :before-upload="beforeUpload"
                            accept=".jpg,.jpeg,.png"
                        >
                            <img v-if="dataForm.hazardPhoto" :src="photoUrl" class="avatar" />
                            <el-icon v-else class="avatar-uploader-icon"><plus /></el-icon>
                        </el-upload>
                        <div class="upload-tip">支持JPG、JPEG、PNG格式，大小不超过5MB</div>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="visible = false">取消</el-button>
                <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
            </span>
        </template>
        <amap-selector v-if="amapSelectorVisible" ref="amapSelector" @select="onAddressSelect" />
    </el-dialog>
</template>

<script>
import AmapSelector from '../components/AmapSelector.vue';
import { Plus } from '@element-plus/icons-vue';

export default {
    components: {
        AmapSelector,
        Plus
    },
    data() {
        return {
            visible: false,
            amapSelectorVisible: false,
            dataForm: {
                id: null,
                hazardType: '',
                hazardLevel: '',
                hazardSource: '',
                userId: null,
                areaCode: '',
                areaCodeArr: [],
                address: '',
                longitude: '',
                latitude: '',
                hazardDesc: '',
                rectifyStartDate: '',
                rectifyEndDate: '',
                submitUserName: '',
                assigneeId: null,
                isSelfRectify: 0,
                rectifyStatus: 0,
                hazardPhoto: ''
            },
            dataRule: {
                hazardType: [{ required: true, message: '隐患分类不能为空', trigger: 'change' }],
                hazardLevel: [{ required: true, message: '隐患等级不能为空', trigger: 'change' }],
                hazardSource: [{ required: true, message: '隐患来源不能为空', trigger: 'change' }],
                address: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }],
                hazardDesc: [{ required: true, message: '隐患描述不能为空', trigger: 'blur' }],
                submitUserName: [{ required: true, message: '提交人姓名不能为空', trigger: 'blur' }]
            },
            areaOptions: [],
            areaProps: {
                value: 'code',
                label: 'areaName',
                children: 'children',
                checkStrictly: true
            },
            allAreas: [],
            userList: [],
            workerList: [],
            uploadUrl: '',
            uploadHeaders: {}
        };
    },
    computed: {
        photoUrl() {
            if (this.dataForm.hazardPhoto) {
                return this.$baseUrl + '/hazard_info/downloadPhoto?filePath=' + encodeURIComponent(this.dataForm.hazardPhoto) + '&token=' + localStorage.getItem('token');
            }
            return '';
        },
        uploadData() {
            if (this.dataForm.id) {
                return { hazardId: this.dataForm.id };
            }
            return {};
        }
    },
    methods: {
        init(id) {
            this.visible = true;
            this.dataForm.id = id || null;
            this.uploadUrl = this.$baseUrl + '/hazard_info/uploadPhoto';
            this.uploadHeaders = {
                token: localStorage.getItem('token')
            };
            
            return Promise.all([
                this.loadAreaData(),
                this.loadUserList(),
                this.loadWorkerList()
            ]).then(() => {
                this.$nextTick(() => {
                    this.$refs.dataForm.resetFields();
                    if (this.dataForm.id) {
                        this.loadData();
                    } else {
                        this.dataForm = {
                            id: null,
                            hazardType: '',
                            hazardLevel: '',
                            hazardSource: '',
                            userId: null,
                            areaCode: '',
                            areaCodeArr: [],
                            address: '',
                            longitude: '',
                            latitude: '',
                            hazardDesc: '',
                            rectifyStartDate: '',
                            rectifyEndDate: '',
                            submitUserName: '',
                            assigneeId: null,
                            isSelfRectify: 0,
                            rectifyStatus: 0,
                            hazardPhoto: ''
                        };
                    }
                });
            }).catch((error) => {
                this.$message.error('加载数据失败');
                throw error;
            });
        },
        loadAreaData() {
            return new Promise((resolve, reject) => {
                this.$http('/gas_user/areaAll', 'GET', {}, true, (resp) => {
                    if (resp && (resp.code === 0 || resp.code === 200)) {
                        this.allAreas = resp.result;
                        this.buildAreaTree();
                        resolve();
                    } else {
                        reject(new Error('加载区域数据失败'));
                    }
                }).catch((error) => {
                    reject(error);
                });
            });
        },
        buildAreaTree() {
            const map = {};
            const roots = [];
            
            this.allAreas.forEach(item => {
                map[item.code] = { ...item, children: [] };
            });
            
            this.allAreas.forEach(item => {
                if (item.parentCode === '0' || !item.parentCode) {
                    roots.push(map[item.code]);
                } else if (map[item.parentCode]) {
                    map[item.parentCode].children.push(map[item.code]);
                }
            });
            
            const removeEmptyChildren = (nodes) => {
                nodes.forEach(node => {
                    if (node.children && node.children.length === 0) {
                        delete node.children;
                    } else if (node.children) {
                        removeEmptyChildren(node.children);
                    }
                });
            };
            
            removeEmptyChildren(roots);
            this.areaOptions = roots;
        },
        loadUserList() {
            return new Promise((resolve, reject) => {
                this.$http('/gas_user/searchAll', 'GET', {}, true, (resp) => {
                    if (resp && (resp.code === 0 || resp.code === 200)) {
                        this.userList = resp.result || [];
                        resolve();
                    } else {
                        resolve();
                    }
                }).catch(() => {
                    resolve();
                });
            });
        },
        loadWorkerList() {
            return new Promise((resolve, reject) => {
                this.$http('/gas_worker/searchAll', 'GET', {}, true, (resp) => {
                    if (resp && (resp.code === 0 || resp.code === 200)) {
                        this.workerList = resp.result || [];
                        resolve();
                    } else {
                        resolve();
                    }
                }).catch(() => {
                    resolve();
                });
            });
        },
        loadData() {
            this.$http('/hazard_info/searchById', 'POST', { id: this.dataForm.id }, true, (resp) => {
                if (resp && (resp.code === 0 || resp.code === 200)) {
                    this.dataForm = {
                        id: resp.id,
                        hazardType: resp.hazardType || '',
                        hazardLevel: resp.hazardLevel || '',
                        hazardSource: resp.hazardSource || '',
                        userId: resp.userId || null,
                        areaCode: resp.areaCode || '',
                        areaCodeArr: [],
                        address: resp.address || '',
                        longitude: resp.longitude || '',
                        latitude: resp.latitude || '',
                        hazardDesc: resp.hazardDesc || '',
                        rectifyStartDate: resp.rectifyStartDate || '',
                        rectifyEndDate: resp.rectifyEndDate || '',
                        submitUserName: resp.submitUserName || '',
                        assigneeId: resp.assigneeId || null,
                        isSelfRectify: resp.isSelfRectify || 0,
                        rectifyStatus: resp.rectifyStatus != null ? resp.rectifyStatus : 0,
                        hazardPhoto: resp.hazardPhoto || ''
                    };
                    if (this.dataForm.areaCode) {
                        this.dataForm.areaCodeArr = this.parseAreaCode(this.dataForm.areaCode);
                    }
                }
            });
        },
        parseAreaCode(areaCode) {
            if (!areaCode) return [];
            return areaCode.split('/');
        },
        handleAreaChange(value) {
            if (value && value.length > 0) {
                this.dataForm.areaCode = value.join('/');
            } else {
                this.dataForm.areaCode = '';
            }
        },
        openMapSelector() {
            this.amapSelectorVisible = true;
            this.$nextTick(() => {
                let areaName = '';
                if (this.dataForm.areaCodeArr && this.dataForm.areaCodeArr.length > 0) {
                    const lastCode = this.dataForm.areaCodeArr[this.dataForm.areaCodeArr.length - 1];
                    const area = this.allAreas.find(a => a.code === lastCode);
                    if (area) {
                        areaName = area.areaName;
                    }
                }
                this.$refs.amapSelector.init(this.dataForm.address, areaName);
            });
        },
        onAddressSelect(data) {
            this.dataForm.address = data.address;
            if (data.location) {
                this.dataForm.longitude = data.location.lng;
                this.dataForm.latitude = data.location.lat;
            }
        },
        beforeUpload(file) {
            const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg';
            const isLt5M = file.size / 1024 / 1024 < 5;

            if (!isImage) {
                this.$message.error('只能上传JPG、JPEG、PNG格式的图片!');
                return false;
            }
            if (!isLt5M) {
                this.$message.error('图片大小不能超过5MB!');
                return false;
            }
            return true;
        },
        handleUploadSuccess(response) {
            if (response && (response.code === 0 || response.code === 200)) {
                this.dataForm.hazardPhoto = response.result.path;
                this.$message.success('上传成功');
            } else {
                this.$message.error(response.msg || '上传失败');
            }
        },
        dataFormSubmit() {
            this.$refs.dataForm.validate(valid => {
                if (valid) {
                    const url = this.dataForm.id ? '/hazard_info/update' : '/hazard_info/save';
                    this.$http(url, 'POST', this.dataForm, true, (resp) => {
                        if (resp && (resp.code === 0 || resp.code === 200)) {
                            this.$message.success('操作成功');
                            this.visible = false;
                            this.$emit('refreshDataList');
                        } else {
                            this.$message.error(resp.msg || '操作失败');
                        }
                    });
                }
            });
        }
    }
};
</script>

<style scoped>
.avatar-uploader {
    display: inline-block;
}

.avatar-uploader :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;
}

.avatar-uploader :deep(.el-upload:hover) {
    border-color: #409eff;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 150px;
    height: 150px;
    line-height: 150px;
    text-align: center;
}

.avatar {
    width: 150px;
    height: 150px;
    display: block;
    object-fit: cover;
}

.upload-tip {
    font-size: 12px;
    color: #999;
    margin-top: 8px;
}
</style>
