<template>
    <el-dialog
        :title="!dataForm.id ? '新增燃气用户' : '修改燃气用户'"
        :close-on-click-modal="false"
        v-model="visible"
        width="650px"
    >
        <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="100px">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="用户户号" prop="userNo">
                        <el-input v-model="dataForm.userNo" size="medium" clearable placeholder="请输入用户户号" maxlength="40" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="户名" prop="userName">
                        <el-input v-model="dataForm.userName" size="medium" clearable placeholder="请输入户名" maxlength="40" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="用户类型" prop="userType">
                        <el-select v-model="dataForm.userType" placeholder="请选择用户类型" style="width: 100%" size="medium">
                            <el-option label="居民用户" value="RESIDENT" />
                            <el-option label="非居民用户" value="NON_RESIDENT" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="dataForm.status">
                            <el-radio :label="1">正常</el-radio>
                            <el-radio :label="0">停用</el-radio>
                        </el-radio-group>
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
                    <el-form-item label="楼栋门牌" prop="buildingInfo">
                        <el-input v-model="dataForm.buildingInfo" size="medium" clearable placeholder="请输入楼栋门牌信息" maxlength="200" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="联系电话" prop="contactPhone">
                        <el-input v-model="dataForm.contactPhone" size="medium" clearable placeholder="请输入联系电话" maxlength="20" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="证件号码" prop="idCard">
                        <el-input v-model="dataForm.idCard" size="medium" clearable placeholder="请输入证件号码" maxlength="18" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="备注" prop="remark">
                        <el-input v-model="dataForm.remark" type="textarea" :rows="3" placeholder="请输入备注" maxlength="500" />
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

export default {
    components: {
        AmapSelector
    },
    data() {
        return {
            visible: false,
            amapSelectorVisible: false,
            dataForm: {
                id: null,
                userNo: '',
                userName: '',
                userType: 'RESIDENT',
                areaCode: '',
                areaCodeArr: [],
                areaName: '',
                address: '',
                buildingInfo: '',
                contactPhone: '',
                idCard: '',
                status: 1,
                orgId: null,
                remark: ''
            },
            dataRule: {
                userNo: [{ required: true, message: '用户户号不能为空', trigger: 'blur' }],
                userName: [{ required: true, message: '户名不能为空', trigger: 'blur' }],
                userType: [{ required: true, message: '用户类型不能为空', trigger: 'change' }],
                address: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }],
                contactPhone: [
                    { required: true, message: '联系电话不能为空', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '联系电话格式不正确', trigger: 'blur' }
                ],
                status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
            },
            areaOptions: [],
            areaProps: {
                value: 'code',
                label: 'areaName',
                children: 'children',
                checkStrictly: true
            },
            allAreas: []
        };
    },
    methods: {
        init(id) {
            this.visible = true;
            this.dataForm.id = id || null;
            return this.loadAreaData().then(() => {
                this.$nextTick(() => {
                    this.$refs.dataForm.resetFields();
                    if (this.dataForm.id) {
                        this.loadData();
                    } else {
                        this.dataForm = {
                            id: null,
                            userNo: '',
                            userName: '',
                            userType: 'RESIDENT',
                            areaCode: '',
                            areaCodeArr: [],
                            areaName: '',
                            address: '',
                            buildingInfo: '',
                            contactPhone: '',
                            idCard: '',
                            status: 1,
                            orgId: null,
                            remark: ''
                        };
                    }
                });
            }).catch((error) => {
                this.$message.error('加载区域数据失败');
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
        loadData() {
            this.$http('/gas_user/searchById', 'POST', { id: this.dataForm.id }, true, (resp) => {
                if (resp && (resp.code === 0 || resp.code === 200)) {
                    this.dataForm = {
                        id: resp.id,
                        userNo: resp.userNo || '',
                        userName: resp.userName || '',
                        userType: resp.userType || 'RESIDENT',
                        areaCode: resp.areaCode || '',
                        areaCodeArr: [],
                        areaName: resp.areaNameDisplay || resp.areaName || '',
                        address: resp.address || '',
                        buildingInfo: resp.buildingInfo || '',
                        contactPhone: resp.contactPhone || '',
                        idCard: resp.idCard || '',
                        status: resp.status || 1,
                        orgId: resp.orgId || null,
                        remark: resp.remark || ''
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
                const selectedNode = this.findAreaName(value[value.length - 1]);
                this.dataForm.areaName = selectedNode ? selectedNode.areaName : '';
            } else {
                this.dataForm.areaCode = '';
                this.dataForm.areaName = '';
            }
        },
        findAreaName(code) {
            return this.allAreas.find(item => item.code === code);
        },
        openMapSelector() {
            this.amapSelectorVisible = true;
            this.$nextTick(() => {
                this.$refs.amapSelector.init(this.dataForm.address, this.dataForm.areaName);
            });
        },
        onAddressSelect(data) {
            this.dataForm.address = data.address;
            if (data.location) {
                console.log('选中位置：', data.location);
            }
        },
        dataFormSubmit() {
            this.$refs.dataForm.validate(valid => {
                if (valid) {
                    const url = this.dataForm.id ? '/gas_user/update' : '/gas_user/save';
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
