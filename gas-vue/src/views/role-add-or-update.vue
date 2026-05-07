<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		:close-on-click-modal="false"
		v-model="visible"
		width="800px"
	>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
            v-model="dataForm.roleName"
            size="medium"
            clearable
            :disabled="dataForm.id === 0"
            style="width: 300px"
            maxlength="10"
            placeholder="请输入2-10个字的角色名称"
        />
      </el-form-item>
      <el-form-item label="角色描述" prop="remark">
        <el-input
            v-model="dataForm.remark"
            size="medium"
            clearable
            :disabled="dataForm.id === 0"
            style="width: 300px"
            maxlength="50"
            placeholder="角色描述不超过50字"
        />
				</el-form-item>
				<el-form-item label="权限" prop="permissions">
				<div v-if="dataForm.id === 0" style="color: #909399; font-size: 14px; margin-bottom: 10px;">
					超级管理员拥有所有权限，无需分配
				</div>
				<div class="permission-transfer-container" :style="{ opacity: dataForm.id === 0 ? 0.5 : 1, 'pointer-events': dataForm.id === 0 ? 'none' : 'auto' }">
					<div class="permission-box">
						<div class="permission-header">
							<el-checkbox v-model="allLeftChecked" @change="handleAllLeftChange" />
							<span class="permission-title">权限列表</span>
							<span class="permission-count">{{ leftChecked.length }}/{{ leftPermissions.length }}</span>
						</div>
						<el-input
							v-model="leftSearch"
							placeholder="请输入权限"
							size="small"
							prefix-icon="Search"
							class="permission-search"
						/>
						<div class="permission-list">
							<el-checkbox-group v-model="leftChecked">
								<el-checkbox
									v-for="item in filteredLeftPermissions"
									:key="item.id"
									:label="item.id"
									class="permission-item"
								>
									{{ item.label }}
								</el-checkbox>
							</el-checkbox-group>
						</div>
					</div>
					<div class="permission-buttons">
						<el-button
							type="primary"
							size="small"
							:disabled="leftChecked.length === 0"
							@click="moveToRight"
						>
							<el-icon><ArrowRight /></el-icon>
						</el-button>
						<el-button
							type="primary"
							size="small"
							:disabled="rightChecked.length === 0"
							@click="moveToLeft"
						>
							<el-icon><ArrowLeft /></el-icon>
						</el-button>
					</div>
					<div class="permission-box">
						<div class="permission-header">
							<el-checkbox v-model="allRightChecked" @change="handleAllRightChange" />
							<span class="permission-title">具备权限</span>
							<span class="permission-count">{{ rightChecked.length }}/{{ rightPermissions.length }}</span>
						</div>
						<el-input
							v-model="rightSearch"
							placeholder="请输入权限"
							size="small"
							prefix-icon="Search"
							class="permission-search"
						/>
						<div class="permission-list">
							<el-checkbox-group v-model="rightChecked">
								<el-checkbox
									v-for="item in filteredRightPermissions"
									:key="item.id"
									:label="item.id"
									class="permission-item"
								>
									{{ item.label }}
								</el-checkbox>
							</el-checkbox-group>
						</div>
					</div>
				</div>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button 
					type="primary" 
					size="medium" 
					@click="dataFormSubmit" 
					:loading="loading"
					:disabled="dataForm.id === 0"
				>
					确定
				</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
import { ArrowRight, ArrowLeft, Search } from '@element-plus/icons-vue'
export default {
	components: {
		ArrowRight,
		ArrowLeft,
		Search
	},
	data: function() {
			return {
				visible: false,
				loading: false,
				dataForm: {
					id: '',
					roleName: '',
					remark: ''
				},
			dataRule: {
				roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
			},
			// 所有权限列表（从后端获取）
			allPermissions: [],
			// 左侧权限（未分配）
			leftPermissions: [],
			leftChecked: [],
			leftSearch: '',
			allLeftChecked: false,
			// 右侧权限（已分配）
			rightPermissions: [],
			rightChecked: [],
			rightSearch: '',
			allRightChecked: false
		};
	},
	computed: {
		filteredLeftPermissions() {
			if (!this.leftSearch) return this.leftPermissions;
			return this.leftPermissions.filter(item => 
				item.label.toLowerCase().includes(this.leftSearch.toLowerCase())
			);
		},
		filteredRightPermissions() {
			if (!this.rightSearch) return this.rightPermissions;
			return this.rightPermissions.filter(item => 
				item.label.toLowerCase().includes(this.rightSearch.toLowerCase())
			);
		}
	},
	methods: {
		init(row) {
			this.visible = true;
			this.leftSearch = '';
			this.rightSearch = '';
			this.leftChecked = [];
			this.rightChecked = [];
			this.allLeftChecked = false;
			this.allRightChecked = false;
			
			if (row) {
					this.dataForm = { 
						id: row.id,
						roleName: row.roleName,
						remark: row.remark || ''
					};
					// 先获取所有权限，再获取角色已有权限
					this.getAllPermissions(() => {
						this.getRolePermissions(row.id);
					});
				} else {
					this.dataForm = {
						id: '',
						roleName: '',
						remark: ''
					};
					// 获取所有权限，新增时所有权限都在左侧
					this.getAllPermissions(() => {
						this.leftPermissions = [...this.allPermissions];
						this.rightPermissions = [];
					});
				}
		},
		getAllPermissions(callback) {
			this.$http('/role/all-permissions', 'GET', {}, true, (resp) => {
				if (resp.code === 0 || resp.code === 200) {
					const rawPermissions = resp.list || [];
					this.allPermissions = rawPermissions
						.filter(perm => perm.permissionCode !== 'ROOT')
						.map(perm => ({
							id: perm.permissionCode,
							label: this.formatPermissionLabel(perm)
						}));
					callback && callback();
				} else {
					this.$message.error(resp.msg || '获取权限列表失败');
				}
			});
		},
		formatPermissionLabel(perm) {
			if (perm.permissionName) return perm.permissionName;
			const [module, action] = perm.permissionCode.split(':');
			const actionMap = { INSERT: '添加', DELETE: '删除', UPDATE: '修改', SELECT: '查询', DIAGNOSE: '诊断' };
			const moduleMap = {
				MIS_USER: '用户管理', MIS_ROLE: '角色管理', DEPT: '部门管理',
				GAS_WORKER: '巡检人员管理', GAS_USER: '燃气用户管理',
				MEDICAL_DEPT: '部门科室管理', MEDICAL_DEPT_SUB: '科室队组管理'
			};
			return `${moduleMap[module] || module}${actionMap[action] || action}`;
		},
		getRolePermissions(roleId) {
			let that = this;
			that.$http('/role/permissions/' + roleId, 'GET', {}, true, function(resp) {
				if (resp.code === 0 || resp.code === 200) {
					const rolePermissions = resp.permissions || [];
					// 已分配的权限在右侧
					that.rightPermissions = that.allPermissions.filter(item => 
						rolePermissions.includes(item.id)
					);
					// 未分配的权限在左侧
					that.leftPermissions = that.allPermissions.filter(item => 
						!rolePermissions.includes(item.id)
					);
				}
			});
		},
		handleAllLeftChange(val) {
			if (val) {
				this.leftChecked = this.filteredLeftPermissions.map(item => item.id);
			} else {
				this.leftChecked = [];
			}
		},
		handleAllRightChange(val) {
			if (val) {
				this.rightChecked = this.filteredRightPermissions.map(item => item.id);
			} else {
				this.rightChecked = [];
			}
		},
		moveToRight() {
			// 将选中的左侧权限移到右侧
			const movedItems = this.leftPermissions.filter(item => 
				this.leftChecked.includes(item.id)
			);
			this.rightPermissions = [...this.rightPermissions, ...movedItems];
			this.leftPermissions = this.leftPermissions.filter(item => 
				!this.leftChecked.includes(item.id)
			);
			this.leftChecked = [];
			this.allLeftChecked = false;
		},
		moveToLeft() {
			// 将选中的右侧权限移到左侧
			const movedItems = this.rightPermissions.filter(item => 
				this.rightChecked.includes(item.id)
			);
			this.leftPermissions = [...this.leftPermissions, ...movedItems];
			this.rightPermissions = this.rightPermissions.filter(item => 
				!this.rightChecked.includes(item.id)
			);
			this.rightChecked = [];
			this.allRightChecked = false;
		},
		dataFormSubmit() {
			this.$refs.dataForm.validate((valid) => {
				if (valid) {
					let that = this;
					that.loading = true;
					let url = this.dataForm.id ? '/role/update' : '/role/save';
					
					// 先保存角色基本信息
					that.$http(url, 'POST', this.dataForm, true, function(resp) {
						if (resp.code === 0 || resp.code === 200) {
							const roleId = that.dataForm.id || resp.id;
							// 保存角色权限
							const permissions = that.rightPermissions.map(item => item.id);
							that.$http('/role/save-permissions', 'POST', {
								roleId: roleId,
								permissions: permissions
							}, true, function(resp2) {
								that.loading = false;
								if (resp2.code === 0 || resp2.code === 200) {
									that.$message.success('操作成功');
									that.visible = false;
									that.$emit('refresh');
								} else {
									that.$message.error(resp2.msg || '权限保存失败');
								}
							});
						} else {
							that.loading = false;
							that.$message.error(resp.msg || '操作失败');
						}
					});
				}
			});
		}
	}
};
</script>

<style lang="less" scoped>
.permission-transfer-container {
	display: flex;
	align-items: center;
	gap: 10px;
	border: 1px solid #e4e7ed;
	border-radius: 4px;
	padding: 10px;
}
.permission-box {
	flex: 1;
	border: 1px solid #e4e7ed;
	border-radius: 4px;
	background: #fff;
}
.permission-header {
	display: flex;
	align-items: center;
	padding: 10px;
	border-bottom: 1px solid #e4e7ed;
	background: #f5f7fa;
}
.permission-title {
	margin-left: 8px;
	font-weight: bold;
	flex: 1;
}
.permission-count {
	color: #909399;
	font-size: 12px;
}
.permission-search {
	padding: 10px;
}
.permission-list {
	height: 300px;
	overflow-y: auto;
	padding: 0 10px 10px;
}
.permission-item {
	display: block;
	margin: 5px 0;
}
.permission-buttons {
	display: flex;
	flex-direction: row;
	gap: 10px;
	justify-content: center;
	align-items: center;
}
</style>
