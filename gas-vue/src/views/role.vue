<template>
	<div v-if="isAuth(['ROOT', 'MIS_ROLE:SELECT'])">
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
			<el-form-item prop="name">
				<el-input v-model="dataForm.name" placeholder="角色名称" class="input" clearable="clearable" />
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="searchHandle()">查询</el-button>
				<el-button v-if="isAuth(['ROOT', 'MIS_ROLE:INSERT'])" type="primary" @click="addHandle()">
					新增
				</el-button>
				<el-button v-if="isAuth(['ROOT', 'MIS_ROLE:DELETE']) && dataListSelections.length > 0" type="danger" @click="batchDeleteHandle()">
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
				prop="roleName"
				header-align="center"
				align="center"
				label="角色名称"
				min-width="100"
				:show-overflow-tooltip="true"
			/>
			<el-table-column
				prop="remark"
				header-align="center"
				align="center"
				label="备注"
				min-width="120"
				:show-overflow-tooltip="true"
			/>
			<el-table-column header-align="center" align="center" label="权限数量" min-width="100">
				<template #default="scope">
					<span>{{ scope.row.permissionCount || 0 }}个</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" label="用户数量" min-width="100">
				<template #default="scope">
					<span>{{ scope.row.userCount || 0 }}人</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button
						v-if="isAuth(['ROOT', 'MIS_ROLE:UPDATE']) && scope.row.id !== 0"
						type="text"
						@click="updateHandle(scope.row)"
					>
						修改
					</el-button>
					<el-button
						v-if="isAuth(['ROOT', 'MIS_ROLE:DELETE']) && scope.row.id !== 0"
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
import AddOrUpdate from './role-add-or-update.vue';
export default {
	components: {
		AddOrUpdate
	},
	data: function() {
		return {
			dataForm: {
				name: null
			},
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			dataRule: {
				name: [{ required: false, message: '角色名称格式错误' }]
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
				page: that.pageIndex,
				length: that.pageSize
			};

			that.$http('/role/list', 'POST', data, true, function(resp) {
				if (resp && (resp.code === 0 || resp.code === 200)) {
					that.dataList = resp.list || [];
					that.totalCount = resp.total || resp.list?.length || 0;
				} else {
					that.dataList = [];
					that.totalCount = 0;
					console.error('API response error:', resp);
				}
				that.dataListLoading = false;
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
			this.$confirm('此操作将永久删除该角色，删除后数据不可恢复，是否继续？', '删除角色', {
				confirmButtonText: '确定删除',
				cancelButtonText: '取消',
				type: 'danger',
				center: true
			}).then(() => {
				that.dataListLoading = true;
				that.$http('/role/delete', 'POST', { id: id }, true, function(resp) {
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
					message: '请选择要删除的角色',
					type: 'warning',
					duration: 1500
				});
				return;
			}
			this.$confirm(`此操作将永久删除选中的 ${this.dataListSelections.length} 个角色，删除后数据不可恢复，是否继续？`, '批量删除角色', {
				confirmButtonText: '确定删除',
				cancelButtonText: '取消',
				type: 'danger',
				center: true
			}).then(() => {
				that.dataListLoading = true;
				let ids = this.dataListSelections.map(item => item.id);
				that.$http('/role/batch-delete', 'POST', { ids: ids }, true, function(resp) {
					that.dataListLoading = false;
					if (resp.code === 0 || resp.code === 200) {
						that.$message({
							message: `成功删除 ${that.dataListSelections.length} 个角色`,
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
