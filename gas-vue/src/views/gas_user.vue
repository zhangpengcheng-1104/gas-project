<template>
    <div v-if="isAuth(['ROOT', 'GAS_USER:SELECT'])">
        <el-form :inline="true" :model="dataForm" ref="dataForm">
            <el-form-item>
                <el-input
                    v-model="dataForm.userNo"
                    placeholder="用户户号"
                    size="medium"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item>
                <el-input
                    v-model="dataForm.userName"
                    placeholder="户名"
                    size="medium"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item>
                <el-input
                    v-model="dataForm.address"
                    placeholder="详细地址"
                    size="medium"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.userType"
                    class="input"
                    placeholder="用户类型"
                    size="medium"
                    clearable
                >
                    <el-option label="全部" value="全部" />
                    <el-option label="居民用户" value="RESIDENT" />
                    <el-option label="非居民用户" value="NON_RESIDENT" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
                <el-button size="medium" @click="resetHandle()">重置</el-button>
                <el-button
                    size="medium"
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'GAS_USER:INSERT'])"
                    @click="addHandle()"
                >
                    新增
                </el-button>
                <el-button
                    size="medium"
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'GAS_USER:DELETE'])"
                    @click="deleteHandle()"
                >
                    批量删除
                </el-button>
            </el-form-item>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            :cell-style="{ padding: '8px 0' }"
            style="width: 100%;"
            size="medium"
            @selection-change="selectionChangeHandle"
        >
            <el-table-column type="selection" header-align="center" align="center" width="50" />
            <el-table-column prop="userNo" header-align="center" align="center" label="用户户号" min-width="120" show-overflow-tooltip />
            <el-table-column prop="userName" header-align="center" align="center" label="户名" min-width="100" show-overflow-tooltip />
            <el-table-column prop="userType" header-align="center" align="center" label="用户类型" min-width="100">
                <template #default="scope">
                    <span>{{ formatUserType(scope.row.userType) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="areaNameDisplay" header-align="center" align="center" label="行政区域" min-width="150" show-overflow-tooltip />
            <el-table-column prop="address" header-align="center" align="center" label="详细地址" min-width="200" show-overflow-tooltip />
            <el-table-column prop="buildingInfo" header-align="center" align="center" label="楼栋门牌" min-width="120" show-overflow-tooltip />
            <el-table-column prop="contactPhone" header-align="center" align="center" label="联系电话" min-width="120" />
            <el-table-column prop="idCard" header-align="center" align="center" label="证件号码" min-width="180" show-overflow-tooltip />
            <el-table-column prop="status" header-align="center" align="center" label="状态" width="80">
                <template #default="scope">
                    <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
                    <el-tag v-else type="danger">停用</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" header-align="center" align="center" label="创建时间" width="160" />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="small"
                        :disabled="!isAuth(['ROOT', 'GAS_USER:UPDATE'])"
                        @click="updateHandle(scope.row.id)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        size="small"
                        :disabled="!isAuth(['ROOT', 'GAS_USER:DELETE'])"
                        @click="deleteOneHandle(scope.row.id)"
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
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            :total="totalCount"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px; text-align: right;"
        />
        <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList" />
    </div>
</template>

<script>
import AddOrUpdate from './gas_user-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data() {
        return {
            dataForm: {
                userNo: '',
                userName: '',
                address: '',
                userType: '全部'
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false
        };
    },
    created() {
        this.getDataList();
    },
    methods: {
        getDataList() {
            this.dataListLoading = true;
            let data = {
                page: this.pageIndex,
                length: this.pageSize,
                userNo: this.dataForm.userNo == '' ? null : this.dataForm.userNo,
                userName: this.dataForm.userName == '' ? null : this.dataForm.userName,
                address: this.dataForm.address == '' ? null : this.dataForm.address,
                userType: this.dataForm.userType == '全部' ? null : this.dataForm.userType
            };
            console.log('请求数据:', data);
            this.$http('/gas_user/searchByPage', 'POST', data, true, (resp) => {
                console.log('响应数据:', resp);
                if (resp && (resp.code === 0 || resp.code === 200)) {
                    this.dataList = resp.result.list;
                    this.totalCount = resp.result.totalCount;
                    console.log('列表数据:', this.dataList);
                    console.log('总数:', this.totalCount);
                } else {
                    this.dataList = [];
                    this.totalCount = 0;
                    console.log('响应错误:', resp);
                }
                this.dataListLoading = false;
            }).catch((error) => {
                console.error('请求失败:', error);
                this.dataListLoading = false;
            });
        },
        searchHandle() {
            this.pageIndex = 1;
            this.getDataList();
        },
        resetHandle() {
            this.dataForm = {
                userNo: '',
                userName: '',
                address: '',
                userType: '全部'
            };
            this.pageIndex = 1;
            this.getDataList();
        },
        sizeChangeHandle(val) {
            this.pageSize = val;
            this.pageIndex = 1;
            this.getDataList();
        },
        currentChangeHandle(val) {
            this.pageIndex = val;
            this.getDataList();
        },
        selectionChangeHandle(val) {
            this.dataListSelections = val;
        },
        formatUserType(userType) {
            const typeMap = {
                'RESIDENT': '居民用户',
                'NON_RESIDENT': '非居民用户'
            };
            return typeMap[userType] || userType;
        },
        addHandle() {
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init().catch(() => {
                    this.$message.error('初始化失败');
                });
            });
        },
        updateHandle(id) {
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init(id);
            });
        },
        deleteHandle() {
            let ids = this.dataListSelections.map(item => {
                return item.id;
            });
            if (ids.length === 0) {
                this.$message.warning('请选择要删除的记录');
                return;
            }
            this.$confirm('确定要删除选中的记录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$http('/gas_user/delete', 'POST', { ids: ids }, true, (resp) => {
                if (resp && (resp.code === 0 || resp.code === 200)) {
                    this.$message.success('删除成功');
                    this.getDataList();
                } else {
                    this.$message.error(resp.msg || '删除失败');
                }
            });
            }).catch(() => {});
        },
        deleteOneHandle(id) {
            this.$confirm('确定要删除该记录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$http('/gas_user/delete', 'POST', { id: id }, true, (resp) => {
                    if (resp && (resp.code === 0 || resp.code === 200)) {
                        this.$message.success('删除成功');
                        this.getDataList();
                    } else {
                        this.$message.error(resp.msg || '删除失败');
                    }
                });
            }).catch(() => {});
        }
    }
};
</script>

<style scoped>
.input {
    width: 200px;
}
</style>
