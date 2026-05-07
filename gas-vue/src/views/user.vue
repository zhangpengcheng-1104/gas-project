<template>
	<div v-if="isAuth(['ROOT', 'MIS_USER:SELECT'])">
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
			<el-form-item prop="name">
				<el-input v-model="dataForm.name" placeholder="姓名" class="input" clearable="clearable" />
			</el-form-item>
			<el-form-item>
				<el-select
					v-model="dataForm.sex"
					class="input"
					placeholder="性别"
					clearable="clearable"
				>
					<el-option label="男" value="男" />
					<el-option label="女" value="女" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-select
					v-model="dataForm.deptId"
					class="input"
					placeholder="部门"
					clearable="clearable"
				>
					<el-option v-for="one in misDeptList" :key="one.id" :label="one.name" :value="one.id" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="searchHandle()">查询</el-button>
				<el-button v-if="isAuth(['ROOT', 'MIS_USER:INSERT'])" type="primary" @click="addHandle()">
					新增
				</el-button>
				<el-button v-if="isAuth(['ROOT', 'MIS_USER:DELETE']) && dataListSelections.length > 0" type="danger" @click="batchDeleteHandle()">
					批量删除
				</el-button>
			</el-form-item>
		</el-form>

		<el-table
			:data="dataList"
			border
			v-loading="dataListLoading"
			@selection-change="selectionChangeHandle"
			:cell-style="{ padding: '3px 0' }"
			style="width: 85%;"
		>
			<el-table-column
				type="selection"
				:selectable="selectable"
				header-align="center"
				align="center"
				width="50"
			/>
			<el-table-column type="index" header-align="center" align="center" width="50" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column
				prop="name"
				header-align="center"
				align="center"
				label="姓名"
				min-width="60"
				:show-overflow-tooltip="true"
			/>
			<el-table-column prop="username" header-align="center" align="center" label="登录名" min-width="60" />
			<el-table-column prop="sex" header-align="center" align="center" label="性别" min-width="40" />
			<el-table-column prop="tel" header-align="center" align="center" label="电话" min-width="60" />
			<el-table-column prop="email" header-align="center" align="center" label="邮箱" min-width="60" />
			<el-table-column prop="roles" header-align="center" align="center" label="角色" min-width="60" />
			<el-table-column prop="dept" header-align="center" align="center" label="部门" min-width="60" />
			<el-table-column header-align="center" align="center" label="状态" min-width="40">
				<template #default="scope">
					<span>{{ scope.row.status === 1 ? '在职' : '离职' }}</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" width="100" label="操作">
				<template #default="scope">
					<el-button
							v-if="isAuth(['ROOT', 'MIS_USER:UPDATE']) && scope.row.id !== 0"
							type="text"
							@click="updateHandle(scope.row)"
						>
							修改
						</el-button>
					<el-button
						v-if="isAuth(['ROOT', 'MIS_USER:DELETE']) && scope.row.id !== 0"
						type="text"
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
	</div>
	<add-or-update ref="addOrUpdate" @refresh="loadDataList" />
</template>

<script>
import AddOrUpdate from './user-add-or-update.vue';
export default {
	components: {
		AddOrUpdate
	},
	data: function() {
		return {
			dataForm: {
				name: null,
				sex: null,
				deptId: null
			},
			dataList: [],
			misDeptList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			dataRule: {
				name: [{ required: false, pattern: '^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,10}$', message: '姓名格式错误' }]
			}
		};
	},
	methods: {
		searchHandle: function() {
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					this.$refs['dataForm'].clearValidate();
					if (this.dataForm.name == '') {
					this.dataForm.name = null;
				}
				if (this.dataForm.sex == '') {
					this.dataForm.sex = null;
				}
				if (this.pageIndex != 1) {
					this.pageIndex = 1;
				}
				this.loadDataList();
				} else {
					return false;
				}
			});
		},

		loadDataList: function() {
			let that = this;
			that.dataListLoading = true;
			let data = {
				name: that.dataForm.name == '' ? null : that.dataForm.name,
				sex: that.dataForm.sex == '' ? null : that.dataForm.sex,
				deptId: that.dataForm.deptId == '' ? null : that.dataForm.deptId,
				page: that.pageIndex,
				length: that.pageSize
			};

			that.$http('/mis_user/searchByPage', 'POST', data, true, function(resp) {
			// 检查resp是否存在，并且code是0或200
			if (resp && (resp.code === 0 || resp.code === 200) && resp.result) {
				let result = resp.result;

				that.dataList = result.list || [];
				that.totalCount = result.totalCount || 0;
			} else {
				that.dataList = [];
				that.totalCount = 0;
				console.error('API response error:', resp);
			}
			that.dataListLoading = false;
		});
		},

		loadMisDeptList: function() {
			let that = this;
			that.$http('/mis_dept/searchAll', 'GET', {}, true, function(resp) {
				that.misDeptList = resp.result;
			});
		},



		addHandle: function() {
			this.$refs.addOrUpdate.init();
		},

		updateHandle: function(row) {
			this.$refs.addOrUpdate.init(row);
		},

		deleteHandle: function(id) {
			let that = this;
			this.$confirm('此操作将永久删除该用户，删除后数据不可恢复，是否继续？', '删除用户', {
				confirmButtonText: '确定删除',
				cancelButtonText: '取消',
				type: 'danger',
				center: true
			}).then(() => {
				that.dataListLoading = true;
				that.$http('/mis_user/delete', 'POST', { id: id }, true, function(resp) {
					that.dataListLoading = false;
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
			}).catch(() => {
				that.$message({
					message: '已取消删除',
					type: 'info',
					duration: 1500
				});
			});
		},

		batchDeleteHandle: function() {
			let that = this;
			if (this.dataListSelections.length === 0) {
				this.$message({
					message: '请选择要删除的用户',
					type: 'warning',
					duration: 1500
				});
				return;
			}
			this.$confirm(`此操作将永久删除选中的 ${this.dataListSelections.length} 个用户，删除后数据不可恢复，是否继续？`, '批量删除用户', {
				confirmButtonText: '确定删除',
				cancelButtonText: '取消',
				type: 'danger',
				center: true
			}).then(() => {
				that.dataListLoading = true;
				let ids = this.dataListSelections.map(item => item.id);
				that.$http('/mis_user/batch-delete', 'POST', { ids: ids }, true, function(resp) {
					that.dataListLoading = false;
					if (resp.code === 0 || resp.code === 200) {
						that.$message({
							message: `成功删除 ${that.dataListSelections.length} 个用户`,
							type: 'success',
							duration: 1500
						});
						that.loadDataList();
					} else {
						that.$message.error(resp.msg || '批量删除失败');
					}
				});
			}).catch(() => {
				that.$message({
					message: '已取消批量删除',
					type: 'info',
					duration: 1500
				});
			});
		},

		selectionChangeHandle: function(val) {
			this.dataListSelections = val;
		},

		selectable: function(row, index) {
			return row.id !== 0;
		},

		sizeChangeHandle: function(size) {
			this.pageSize = size;
			this.loadDataList();
		},

		currentChangeHandle: function(current) {
			this.pageIndex = current;
			this.loadDataList();
		}
	},
	created: function() {
		this.loadMisDeptList();
		this.loadDataList();
	}
};
</script>

<style lang="less" scoped="scoped">
.input {
	width: 200px;
	margin-right: 10px;
}
</style>