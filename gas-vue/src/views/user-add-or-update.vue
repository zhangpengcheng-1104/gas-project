<template>
	<el-dialog
		:title="!dataForm.id ? '新增用户' : '修改用户'"
		:close-on-click-modal="false"
		v-model="visible"
		width="500px"
	>
		<div v-if="dataForm.id === 0" style="color: #909399; font-size: 14px; margin-bottom: 10px;">
			超级管理员不可修改
		</div>
		<el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px" :style="{ opacity: dataForm.id === 0 ? 0.5 : 1, 'pointer-events': dataForm.id === 0 ? 'none' : 'auto' }">
			<el-form-item label="用户名" prop="username">
				<el-input v-model="dataForm.username" size="medium" clearable :disabled="!!dataForm.id" />
			</el-form-item>

			<el-form-item label="姓名" prop="name">
				<el-input v-model="dataForm.name" size="medium" clearable />
			</el-form-item>
			<el-form-item label="电话" prop="tel">
				<el-input v-model="dataForm.tel" size="medium" clearable />
			</el-form-item>
			<el-form-item label="邮箱" prop="email">
				<el-input v-model="dataForm.email" size="medium" clearable />
			</el-form-item>
			<el-form-item label="性别" prop="sex">
				<el-radio-group v-model="dataForm.sex">
					<el-radio label="男">男</el-radio>
					<el-radio label="女">女</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item label="部门" prop="deptId">
				<el-select v-model="dataForm.deptId" placeholder="请选择部门" style="width: 100%">
					<el-option v-for="dept in deptList" :key="dept.id" :label="dept.name" :value="dept.id" />
				</el-select>
			</el-form-item>
			<el-form-item label="状态" prop="status">
				<el-radio-group v-model="dataForm.status">
					<el-radio :label="1">在职</el-radio>
					<el-radio :label="0">离职</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item label="角色" prop="roles">
				<el-select v-model="dataForm.roles" multiple placeholder="请选择角色" style="width: 100%">
					<el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
				</el-select>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit" :disabled="!hasChanges || dataForm.id === 0">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
import { isUsername } from '../utils/validate.js';
export default {
	data: function() {
			return {
				visible: false,
				dataForm: {
					id: '',
					username: '',
					password: '',
					name: '',
					sex: '男',
					tel: '',
					email: '',
					deptId: '',
					status: 1,
					roles: []
				},
				originalForm: {}, // 原始表单数据，用于对比
				dataRule: {
					username: [
						{ required: true, message: '请输入用户名', trigger: 'blur' },
						{ validator: (rule, value, callback) => {
							if (!isUsername(value)) {
								callback(new Error('用户名2~18字节，仅支持字母、数字、中文和下划线'));
							} else {
								callback();
							}
						}, trigger: 'blur' }
					],
					name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
				},
				roleList: [],
				deptList: []
			};
		},
		computed: {
			// 检查是否有修改
			hasChanges() {
				if (!this.originalForm.id) {
					// 新增用户，始终启用确认按钮
					return true;
				}
				
				// 对比基本字段
				const basicFields = ['username', 'name', 'sex', 'tel', 'email', 'deptId', 'status'];
				for (let field of basicFields) {
					if (this.dataForm[field] !== this.originalForm[field]) {
						return true;
					}
				}
				
				// 对比角色数组
				if (this.dataForm.roles.length !== this.originalForm.roles.length) {
					return true;
				}
				
				// 检查角色是否相同（排序后对比）
				const sortedRoles = [...this.dataForm.roles].sort();
				const sortedOriginalRoles = [...this.originalForm.roles].sort();
				for (let i = 0; i < sortedRoles.length; i++) {
					if (sortedRoles[i] !== sortedOriginalRoles[i]) {
						return true;
					}
				}
				
				return false;
			}
		},
	methods: {
		init(row) {
			this.visible = true;
			if (row) {
				let userId = typeof row === 'object' ? row.id : row;
				this.dataForm.id = userId;
				
				let that = this;
				that.$http('/mis_user/info/' + userId, 'GET', {}, true, function(resp) {
					if (resp.code === 0 || resp.code === 200) {
						let userInfo = resp.user || {};
						that.dataForm = {
							id: userInfo.id || userId,
							username: userInfo.username || '',
							name: userInfo.name || '',
							sex: userInfo.sex || '男',
							tel: userInfo.tel || '',
							email: userInfo.email || '',
							deptId: userInfo.deptId || '',
							status: userInfo.status || 1,
							roles: []
						};
						that.originalForm = { ...that.dataForm };
						that.getUserRoles(userId);
					}
				});
			} else {
				this.dataForm = {
					id: '',
					username: '',
					password: 'abc123456',
					name: '',
					sex: '男',
					tel: '',
					email: '',
					deptId: '',
					status: 1,
					roles: []
				};
				this.originalForm = {};
			}
			this.getRoleList();
			this.getDeptList();
		},
		getRoleList() {
			let that = this;
			that.$http('/role/simple-list', 'GET', {}, true, function(resp) {
				if (resp.code === 0 || resp.code === 200) {
					that.roleList = resp.list || [];
				}
			});
		},
		getDeptList() {
			let that = this;
			that.$http('/mis_dept/searchAll', 'GET', {}, true, function(resp) {
				if (resp.code === 0 || resp.code === 200) {
					that.deptList = resp.result || [];
				}
			});
		},
		getUserRoles(userId) {
			let that = this;
			that.$http('/mis_user/roles/' + userId, 'GET', {}, true, function(resp) {
				if (resp.code === 0 || resp.code === 200) {
					that.dataForm.roles = resp.roles || [];
					// 保存角色到原始数据
					that.originalForm.roles = [...that.dataForm.roles];
				}
			});
		},
		dataFormSubmit() {
			this.$refs.dataForm.validate((valid) => {
				if (valid) {
					let that = this;
					let url = this.dataForm.id ? '/mis_user/update' : '/mis_user/save';
					that.$http(url, 'POST', this.dataForm, true, function(resp) {
						if (resp.code === 0 || resp.code === 200) {
							// 保存用户角色
							if (that.dataForm.roles && that.dataForm.roles.length > 0) {
								that.$http('/mis_user/save-roles', 'POST', {
									userId: that.dataForm.id || resp.id,
									roles: that.dataForm.roles
								}, true, function(resp) {
									if (resp.code === 0 || resp.code === 200) {
										that.$message.success('操作成功');
										that.visible = false;
										that.$emit('refresh');
									} else {
										that.$message.error(resp.msg || '保存角色失败');
									}
								});
							} else {
								that.$message.success('操作成功');
								that.visible = false;
								that.$emit('refresh');
							}
						} else {
							that.$message.error(resp.msg || '操作失败');
						}
					});
				}
			});
		}
	}
};
</script>

<style lang="less" scoped="scoped"></style>
