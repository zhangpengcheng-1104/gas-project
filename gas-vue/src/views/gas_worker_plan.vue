<template>
	<div v-if="isAuth(['ROOT', 'SCHEDULE:SELECT'])">
		<el-form :inline="true" :model="dataForm" ref="dataForm">
			<el-form-item>
				<el-input
					v-model="dataForm.workerName"
					placeholder="巡检人姓名"
					size="medium"
					class="input"
					clearable
				/>
			</el-form-item>
			<el-form-item>
				<el-select
					v-model="dataForm.deptId"
					class="input"
					placeholder="选择科室"
					size="medium"
					clearable
				>
					<el-option v-for="one in deptList" :label="one.name" :value="one.id" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-date-picker
					v-model="dataForm.startDate"
					type="date"
					placeholder="开始日期"
					size="medium"
					:editable="false"
					format="YYYY-MM-DD"
					value-format="YYYY-MM-DD"
					style="width: 100%;"
					clearable
				/>
			</el-form-item>
			<el-form-item>
				<el-date-picker
					v-model="dataForm.endDate"
					type="date"
					placeholder="结束日期"
					size="medium"
					:editable="false"
					format="YYYY-MM-DD"
					value-format="YYYY-MM-DD"
					style="width: 100%;"
					clearable
				/>
			</el-form-item>
			<el-form-item>
				<el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
				<el-button size="medium" @click="resetHandle()">重置</el-button>
				<el-button
					size="medium"
					type="primary"
					:disabled="!isAuth(['ROOT', 'SCHEDULE:INSERT'])"
					@click="addHandle()"
				>
					新增
				</el-button>
				<el-button
					size="medium"
					type="danger"
					:disabled="!isAuth(['ROOT', 'SCHEDULE:DELETE'])"
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
			<el-table-column type="index" header-align="center" align="center" width="60" label="序号">
				<template #default="scope">
					{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}
				</template>
			</el-table-column>
			<el-table-column prop="deptName" header-align="center" align="center" label="科室" min-width="120" show-overflow-tooltip />
			<el-table-column prop="deptSubName" header-align="center" align="center" label="班组" min-width="120" show-overflow-tooltip />
			<el-table-column prop="workerName" header-align="center" align="center" label="巡检人" min-width="100" show-overflow-tooltip />
			<el-table-column prop="date" header-align="center" align="center" label="巡检时间" width="120" />
			<el-table-column prop="maximum" header-align="center" align="center" label="最大人数" width="100">
				<template #default="scope">
					{{ scope.row.maxPeople || scope.row.maximum || 1 }}人
				</template>
			</el-table-column>
			<el-table-column prop="actualCount" header-align="center" align="center" label="实际人数" width="100">
				<template #default="scope">
					{{ scope.row.actualCount || 0 }}人
				</template>
			</el-table-column>
			<el-table-column prop="userName" header-align="center" align="center" label="燃气用户" min-width="120" show-overflow-tooltip />
			<el-table-column prop="description" header-align="center" align="center" label="备注" min-width="200" show-overflow-tooltip />
			<el-table-column header-align="center" align="center" label="报告状态" width="100">
				<template #default="scope">
					<el-tag v-if="scope.row.filePath" type="success">已生成</el-tag>
					<el-tag v-else type="info">未生成</el-tag>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" width="200" label="操作">
				<template #default="scope">
					<el-button
						type="text"
						size="small"
						:disabled="!isAuth(['ROOT', 'SCHEDULE:UPDATE'])"
						@click="updateHandle(scope.row.id)"
					>
						修改
					</el-button>
					<el-button
						type="text"
						size="small"
						:disabled="!isAuth(['ROOT', 'SCHEDULE:DELETE'])"
						@click="deleteOneHandle(scope.row.id)"
					>
						删除
					</el-button>
					<el-button
						type="text"
						size="small"
						:disabled="!isAuth(['ROOT', 'SCHEDULE:SELECT'])"
						@click="generateReportHandle(scope.row.id)"
					>
						生成报告
					</el-button>
					<el-button
						type="text"
						size="small"
						:disabled="!scope.row.filePath"
						@click="downloadReportHandle(scope.row.filePath, scope.row.userName)"
					>
						下载报告
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
import AddOrUpdate from './gas_worker_plan-add-or-update.vue';
export default {
	components: {
		AddOrUpdate
	},
	data() {
		return {
			dataForm: {
				workerName: '',
				deptId: null,
				startDate: '',
				endDate: ''
			},
			dataList: [],
			deptList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			addOrUpdateVisible: false
		};
	},
	created() {
		this.loadDeptList();
		this.getDataList();
	},
	methods: {
		loadDeptList() {
			let that = this;
			that.$http('/medical_dept/searchAll', 'GET', {}, true, function(resp) {
				that.deptList = resp.result || [];
			});
		},
		getDataList() {
			this.dataListLoading = true;
			let data = {
				page: this.pageIndex,
				length: this.pageSize,
				workerName: this.dataForm.workerName == '' ? null : this.dataForm.workerName,
				deptId: this.dataForm.deptId,
				startDate: this.dataForm.startDate == '' ? null : this.dataForm.startDate,
				endDate: this.dataForm.endDate == '' ? null : this.dataForm.endDate
			};
			this.$http('/worker_plan/searchByPage', 'POST', data, true, (resp) => {
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
				workerName: '',
				deptId: null,
				startDate: '',
				endDate: ''
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
				this.$http('/worker_plan/deleteBatch', 'POST', { ids: ids }, true, (resp) => {
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
				this.$http('/worker_plan/delete', 'POST', { id: id }, true, (resp) => {
					if (resp && (resp.code === 0 || resp.code === 200)) {
						this.$message.success('删除成功');
						this.getDataList();
					} else {
						this.$message.error(resp.msg || '删除失败');
					}
				});
			}).catch(() => {});
		},
		generateReportHandle(planId) {
			let that = this;
			
			that.$confirm('确定要生成报告吗？生成后将上传到服务器。', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'info'
			}).then(() => {
				that.$message({
					message: '正在生成报告，请稍候...',
					type: 'info'
				});
				
				that.$http('/inspection_report/generateAndUpload?planId=' + planId, 'POST', {}, true, (resp) => {
					if (resp && (resp.code === 0 || resp.code === 200)) {
						that.$message.success('报告生成成功');
						that.getDataList();
					} else {
						that.$message.error(resp.msg || '报告生成失败');
					}
				});
			}).catch(() => {});
		},
		downloadReportHandle(filePath, userName) {
			if (!filePath) {
				this.$message.warning('报告未生成，请先生成报告');
				return;
			}
			
			let fileName = (userName || '巡检') + '的巡检报告.docx';
			let url = this.$baseUrl + '/inspection_report/download?filePath=' + encodeURIComponent(filePath) + '&fileName=' + encodeURIComponent(fileName) + '&token=' + localStorage.getItem('token');
			let a = document.createElement('a');
			a.href = url;
			a.click();
		}
	}
};
</script>

<style scoped>
.input {
	width: 200px;
}
</style>
