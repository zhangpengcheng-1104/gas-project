<template>
    <div v-if="isAuth(['ROOT', 'HAZARD_INFO:SELECT'])">
        <el-form :inline="true" :model="dataForm" ref="dataForm">
            <el-form-item>
                <el-select
                    v-model="dataForm.hazardType"
                    class="input"
                    placeholder="隐患分类"
                    size="medium"
                    clearable
                >
                    <el-option label="全部" value="" />
                    <el-option label="上报" value="上报" />
                    <el-option label="预警" value="预警" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.hazardLevel"
                    class="input"
                    placeholder="隐患等级"
                    size="medium"
                    clearable
                >
                    <el-option label="全部" value="" />
                    <el-option label="重大" value="重大" />
                    <el-option label="较大" value="较大" />
                    <el-option label="一般" value="一般" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.hazardSource"
                    class="input"
                    placeholder="隐患来源"
                    size="medium"
                    clearable
                >
                    <el-option label="全部" value="" />
                    <el-option label="管网" value="管网" />
                    <el-option label="厂站" value="厂站" />
                    <el-option label="三方施工" value="三方施工" />
                    <el-option label="用户" value="用户" />
                </el-select>
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
                <el-input
                    v-model="dataForm.submitUserName"
                    placeholder="提交人"
                    size="medium"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.rectifyStatus"
                    class="input"
                    placeholder="整改状态"
                    size="medium"
                    clearable
                >
                    <el-option label="全部" value="" />
                    <el-option label="待整改" value="0" />
                    <el-option label="已整改" value="1" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
                <el-button size="medium" @click="resetHandle()">重置</el-button>
                <el-button
                    size="medium"
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'HAZARD_INFO:INSERT'])"
                    @click="addHandle()"
                >
                    新增
                </el-button>
                <el-button
                    size="medium"
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'HAZARD_INFO:DELETE'])"
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
            <el-table-column prop="hazardType" header-align="center" align="center" label="隐患分类" width="80">
                <template #default="scope">
                    <el-tag :type="scope.row.hazardType === '上报' ? 'primary' : 'warning'" size="small">
                        {{ scope.row.hazardType }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="hazardLevel" header-align="center" align="center" label="隐患等级" width="80">
                <template #default="scope">
                    <el-tag :type="getHazardLevelType(scope.row.hazardLevel)" size="small">
                        {{ scope.row.hazardLevel }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="hazardSource" header-align="center" align="center" label="隐患来源" width="90" />
            <el-table-column prop="userName" header-align="center" align="center" label="关联用户" min-width="100" show-overflow-tooltip />
            <el-table-column prop="areaNameDisplay" header-align="center" align="center" label="行政区域" min-width="150" show-overflow-tooltip />
            <el-table-column prop="address" header-align="center" align="center" label="详细地址" min-width="200" show-overflow-tooltip />
            <el-table-column prop="hazardDesc" header-align="center" align="center" label="隐患描述" min-width="150" show-overflow-tooltip />
            <el-table-column prop="submitUserName" header-align="center" align="center" label="提交人" width="100" />
            <el-table-column prop="submitTime" header-align="center" align="center" label="提交时间" width="160">
                <template #default="scope">
                    {{ formatDateTime(scope.row.submitTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="assigneeName" header-align="center" align="center" label="指派人" width="100" />
            <el-table-column prop="rectifyStatus" header-align="center" align="center" label="整改状态" width="100">
                <template #default="scope">
                    <el-tag :type="scope.row.rectifyStatus === 1 ? 'success' : 'warning'" size="small">
                        {{ scope.row.rectifyStatus === 1 ? '已整改' : '待整改' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="small"
                        :disabled="!isAuth(['ROOT', 'HAZARD_INFO:UPDATE'])"
                        @click="updateHandle(scope.row.id)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        size="small"
                        :disabled="!isAuth(['ROOT', 'HAZARD_INFO:DELETE'])"
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
import AddOrUpdate from './hazard_info-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data() {
        return {
            dataForm: {
                hazardType: '',
                hazardLevel: '',
                hazardSource: '',
                address: '',
                submitUserName: '',
                rectifyStatus: ''
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
                hazardType: this.dataForm.hazardType == '' ? null : this.dataForm.hazardType,
                hazardLevel: this.dataForm.hazardLevel == '' ? null : this.dataForm.hazardLevel,
                hazardSource: this.dataForm.hazardSource == '' ? null : this.dataForm.hazardSource,
                address: this.dataForm.address == '' ? null : this.dataForm.address,
                submitUserName: this.dataForm.submitUserName == '' ? null : this.dataForm.submitUserName,
                rectifyStatus: this.dataForm.rectifyStatus == '' ? null : parseInt(this.dataForm.rectifyStatus)
            };
            this.$http('/hazard_info/searchByPage', 'POST', data, true, (resp) => {
                if (resp && (resp.code === 0 || resp.code === 200)) {
                    this.dataList = resp.result.list;
                    this.totalCount = resp.result.totalCount;
                } else {
                    this.dataList = [];
                    this.totalCount = 0;
                }
                this.dataListLoading = false;
            }).catch((error) => {
                this.dataListLoading = false;
            });
        },
        searchHandle() {
            this.pageIndex = 1;
            this.getDataList();
        },
        resetHandle() {
            this.dataForm = {
                hazardType: '',
                hazardLevel: '',
                hazardSource: '',
                address: '',
                submitUserName: '',
                rectifyStatus: ''
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
        getHazardLevelType(level) {
            const typeMap = {
                '重大': 'danger',
                '较大': 'warning',
                '一般': 'info'
            };
            return typeMap[level] || 'info';
        },
        formatDateTime(dateTime) {
            if (!dateTime) return '-';
            return dateTime.replace('T', ' ').substring(0, 19);
        },
        addHandle() {
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init();
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
                this.$http('/hazard_info/delete', 'POST', { ids: ids }, true, (resp) => {
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
                this.$http('/hazard_info/delete', 'POST', { id: id }, true, (resp) => {
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
    width: 160px;
}
</style>
