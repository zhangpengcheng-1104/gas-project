<template>
    <div v-if="isAuth(['ROOT', 'GAS_WORKER:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            <el-form-item prop="name">
                <el-input
                    v-model="dataForm.name"
                    placeholder="姓名"
                    size="medium"
                    class="input"
                    clearable="clearable"
                />
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.deptId"
                    class="input"
                    placeholder="科室"
                    size="medium"
                    clearable="clearable"
                >
                    <el-option v-for="one in medicalDeptList" :key="one.id" :label="one.name" :value="one.id" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.degree"
                    class="input"
                    placeholder="学历"
                    size="medium"
                    clearable="clearable"
                >
                    <el-option label="小学" value="小学" />
                    <el-option label="中学" value="中学" />
                    <el-option label="大专" value="大专" />
                    <el-option label="大学" value="大学" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.job" class="input" placeholder="职位" size="medium" clearable="clearable">
                    <el-option label="助工" value="助工" />
                    <el-option label="工程师" value="工程师" />
                    <el-option label="高工" value="高工" />
                    <el-option label="总工" value="总工" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.recommended" class="input" placeholder="级别" clearable="clearable">
                    <el-option label="优先" value="true" />
                    <el-option label="非优先" value="false" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
                <el-button
                    size="medium"
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'GAS_WORKER:INSERT'])"
                    @click="addHandle()"
                >
                    新增
                </el-button>
                <el-button
                    size="medium"
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'GAS_WORKER:DELETE'])"
                    @click="deleteHandle()"
                >
                    批量删除
                </el-button>
            </el-form-item>
            <div style="float: right">
                <el-radio-group v-model="dataForm.status" @change="searchHandle()">
                    <el-radio-button label="在职" />
                    <el-radio-button label="离职" />
                    <el-radio-button label="退休" />
                </el-radio-group>
            </div>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            :cell-style="{ padding: '3px 0' }"
            style="width: 100%;"
            size="medium"
            @selection-change="selectionChangeHandle"
            @expand-change="expand"
            :row-key="getRowKeys"
            :expand-row-keys="expands"
            @sort-change="orderHandle"
        >
            <el-table-column type="expand">
                <template #default="scope">
                    <div>
                        <table class="content">
                            <tr>
                                <th width="140">身份证号</th>
                                <td width="320">{{ content.pid }}</td>
                                <th width="140">出生日期</th>
                                <td width="320">{{ content.birthday }}</td>
                                <td width="110" rowspan="3" align="center">
                                    <el-upload
                                        class="avatar-uploader"
                                        :http-request="uploadWorkerPhoto"
                                        with-credentials="true"
                                        :on-success="updatePhotoSuccess"
                                        :on-error="updatePhotoError"
                                        :show-file-list="false"
                                        :data="{ workerId: scope.row.id }"
                                        name="file"
                                    >
                                        <el-image style="width: 100px; height: 100px" :src="content.photo" :fit="fit">
                                            <template #error>
                                                <div class="error-img">
                                                    <el-icon><Picture /></el-icon>
                                                </div>
                                            </template>
                                        </el-image>
                                    </el-upload>
                                </td>
                            </tr>
                            <tr>
                                <th>编号</th>
                                <td>{{ content.uuid }}</td>
                                <th>入职日期</th>
                                <td>{{ content.hiredate }}</td>
                            </tr>
                            <tr>
                                <th>电子信箱</th>
                                <td>{{ content.email }}</td>
                                <th>备注信息</th>
                                <td>{{ content.remark }}</td>
                            </tr>
                            <tr>
                                <th>标签描述</th>
                                <td>
                                    <el-tag v-for="one of content.tag" :key="one" style="margin-right: 5px;">{{ one }}</el-tag>
                                </td>
                                <th>家庭住址</th>
                                <td colspan="2">{{ content.address }}</td>
                            </tr>
                            <tr>
                                <th>介绍信息</th>
                                <td colspan="4">{{ content.description }}</td>
                            </tr>
                        </table>
                    </div>
                </template>
            </el-table-column>
            <el-table-column type="selection" header-align="center" align="center" width="50" />
            <el-table-column type="index" header-align="center" align="center" width="100" label="序号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="name"
                header-align="center"
                align="center"
                min-width="120"
                label="姓名"
                :show-overflow-tooltip="true"
            />
            <el-table-column prop="sex" header-align="center" align="center" min-width="70" label="性别" />
            <el-table-column prop="tel" header-align="center" align="center" min-width="120" label="电话" />
            <el-table-column prop="job" header-align="center" align="center" min-width="100" label="职务" />
            <el-table-column
                prop="deptName"
                header-align="center"
                align="center"
                min-width="120"
                label="科室"
                :show-overflow-tooltip="true"
                sortable
            />
            <el-table-column
                prop="subName"
                header-align="center"
                align="center"
                min-width="120"
                label="班组"
                :show-overflow-tooltip="true"
            />
            <el-table-column
                prop="school"
                header-align="center"
                align="center"
                min-width="170"
                label="毕业学校"
                :show-overflow-tooltip="true"
            />
            <el-table-column prop="degree" header-align="center" align="center" min-width="100" label="学历" />
            <el-table-column prop="status" header-align="center" align="center" min-width="80" label="状态" />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'GAS_WORKER:UPDATE'])"
                        @click="updateHandle(scope.row.id)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'GAS_WORKER:DELETE'])"
                        @click="deleteHandle(scope.row.id)"
                    >
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="totalCount"
            layout="total, sizes, prev, pager, next, jumper"
        ></el-pagination>
        <add-or-update ref="addOrUpdate" @refresh="loadDataList"></add-or-update>
    </div>
</template>

<script>
import axios from 'axios';
import AddOrUpdate from './worker-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data() {
        return {
            token: localStorage.getItem('token'),
            dataForm: {
                name: '',
                deptId: '',
                degree: '',
                job: '',
                recommended: '',
                status: '在职',
                order: null
            },
            dataList: [],
            medicalDeptList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            dataRule: {
                name: [{ required: false, pattern: '^[\\u4e00-\\u9fa5]{1,10}$', message: '姓名格式错误' }]
            },
            expands: [],
            getRowKeys(row) {
                return row.id;
            },
            content: {
                id: null,
                photo: '',
                pid: '',
                birthday: '',
                uuid: '',
                hiredate: '',
                email: '',
                remark: '',
                tag: '',
                address: '',
                description: ''
            }
        };
    },
    methods: {
        uploadWorkerPhoto: async function(option) {
            let formData = new FormData();
            formData.append('file', option.file);
            formData.append('workerId', option.data.workerId);
            try {
                let uploadResp = await axios.post(`${this.$baseUrl}/gas_worker/updatePhoto`, formData, {
                    headers: {
                        token: this.token,
                        'Content-Type': 'multipart/form-data'
                    },
                    withCredentials: true
                });
                let uploadResult = uploadResp.data || {};
                if (!(uploadResult.code === 0 || uploadResult.code === 200)) {
                    throw new Error(uploadResult.msg || '照片上传失败');
                }
                option.onSuccess(uploadResult);
            } catch (err) {
                option.onError(err);
            }
        },
        resolvePhotoUrl: function(photo) {
            if (!photo || typeof photo !== 'string') {
                return '';
            }
            let finalPhoto = photo.startsWith('http')
                ? photo
                : `${this.$minioUrl}${photo.startsWith('/') ? '' : '/'}${photo}`;
            let separator = finalPhoto.includes('?') ? '&' : '?';
            return `${finalPhoto}${separator}random=${Math.random()}`;
        },
        loadDataList: function() {
            let that = this;
            that.dataListLoading = true;
            let json = { 在职: 1, 离职: 2, 退休: 3 };
            let data = {
                page: that.pageIndex,
                length: that.pageSize,
                name: that.dataForm.name == '' ? null : that.dataForm.name,
                deptId: that.dataForm.deptId == '' ? null : that.dataForm.deptId,
                degree: that.dataForm.degree == '' ? null : that.dataForm.degree,
                job: that.dataForm.job == '' ? null : that.dataForm.job,
                recommended: that.dataForm.recommended == '' ? null : (that.dataForm.recommended === 'true'),
                status: json[that.dataForm.status],
                order: that.dataForm.order
            };
            console.log('请求数据:', data);
            that.$http('/gas_worker/searchByPage', 'POST', data, true, function(resp) {
                console.log('响应数据:', resp);
                let result = resp.result;
                let temp = {
                    '1': '在职',
                    '2': '离职',
                    '3': '退休'
                };
                for (let one of result.list) {
                    one.status = temp[one.status];
                }
                that.dataList = result.list;
                that.totalCount = result.totalCount;
                that.dataListLoading = false;
            }).catch(error => {
                console.error('请求失败:', error);
                that.dataListLoading = false;
            });
        },
        loadMedicalDeptList: function() {
            let that = this;
            that.$http('/medical_dept/searchAll', 'GET', {}, true, function(resp) {
                that.medicalDeptList = resp.result;
            });
        },
        searchHandle: function() {
            this.$refs['dataForm'].validate(valid => {
                if (valid) {
                    this.$refs['dataForm'].clearValidate();
                    if (this.pageIndex != 1) {
                        this.pageIndex = 1;
                    }
                    this.loadDataList();
                } else {
                    return false;
                }
            });
        },
        addHandle: function() {
            this.$refs.addOrUpdate.init();
        },
        updateHandle: function(id) {
            this.$refs.addOrUpdate.init(id);
        },
        deleteHandle: function(id) {
            let that = this;
            let ids = id ? [id] : that.dataListSelections.map(item => item.id);
            if (ids.length === 0) {
                that.$message({
                    message: '请选择要删除的记录',
                    type: 'warning',
                    duration: 1500
                });
                return;
            }
            that.$confirm(`确定要删除选中的${ids.length}条记录吗？`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                that.$http('/gas_worker/delete', 'POST', { ids: ids }, true, function(resp) {
                    if (resp.code === 0 || resp.code === 200) {
                        that.$message({
                            message: '删除成功',
                            type: 'success',
                            duration: 1500
                        });
                        that.loadDataList();
                    } else {
                        that.$message.error(resp.msg || '删除失败');
                    }
                });
            }).catch(() => {});
        },
        selectionChangeHandle: function(val) {
            this.dataListSelections = val;
        },
        sizeChangeHandle: function(val) {
            this.pageSize = val;
            this.pageIndex = 1;
            this.loadDataList();
        },
        currentChangeHandle: function(val) {
            this.pageIndex = val;
            this.loadDataList();
        },
        orderHandle: function(column) {
            if (column.order) {
                this.dataForm.order = column.order === 'ascending' ? 'ASC' : 'DESC';
            } else {
                this.dataForm.order = null;
            }
            this.loadDataList();
        },
        expand: function(row, expandedRows) {
            let that = this;
            if (expandedRows.length > 0) {
                that.expands = [];
                that.expands.push(row.id);
                let data = {
                    id: row.id
                };
                that.$http('/gas_worker/searchContent', 'POST', data, false, function(resp) {
                    that.content.id = row.id;
                    that.content.photo = resp.photo ? that.resolvePhotoUrl(resp.photo) : '';
                    that.content.pid = resp.pid;
                    that.content.birthday = resp.birthday;
                    that.content.uuid = resp.uuid;
                    that.content.hiredate = resp.hiredate;
                    that.content.email = resp.email;
                    that.content.remark = resp.remark;
                    that.content.tag = resp.tag ? (Array.isArray(resp.tag) ? resp.tag : resp.tag.split(',')) : [];
                    that.content.address = resp.address;
                    that.content.description = resp.description;
                });
            } else {
                that.expands = [];
            }
        },
        updatePhotoSuccess: function(resp) {
            if (!resp) return;
            if (resp.code === 0 || resp.code === 200) {
                this.$message({
                    message: '照片上传成功',
                    type: 'success',
                    duration: 1500
                });
                this.content.photo = this.resolvePhotoUrl(`/worker-${this.content.id}.jpg`);
            } else {
                this.$message.error(resp.msg || '照片上传失败');
            }
        },
        updatePhotoError: function(err) {
            let message = '照片上传失败';
            if (err && err.response && err.response.data) {
                message = err.response.data.msg || err.response.data.error || message;
            }
            if (err && err.message) {
                try {
                    let error = JSON.parse(err.message);
                    message = error.msg || error.error || message;
                } catch (e) {
                    message = err.message || message;
                }
            }
            this.$message.error(message);
        }
    },
    created: function() {
        this.loadMedicalDeptList();
        this.loadDataList();
    }
};
</script>

<style lang="less" scoped>
.input {
    width: 200px;
    margin-right: 10px;
}
.content {
    width: 100%;
    border-collapse: collapse;
    th {
        background-color: #f5f7fa;
        text-align: right;
        padding: 8px 10px;
        border: 1px solid #ebeef5;
    }
    td {
        padding: 8px 10px;
        border: 1px solid #ebeef5;
    }
}
.error-img {
    width: 100px;
    height: 100px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
    font-size: 30px;
    color: #909399;
}
</style>
