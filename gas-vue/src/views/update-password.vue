<template>
	<el-dialog title="提示" v-model="visible" width="25%">
		<el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
			<el-form-item label="原密码" prop="password" :error="passwordError">
				<el-input type="password" v-model="dataForm.password" size="medium" clearable @blur="clearPasswordError" />
			</el-form-item>
			<el-form-item label="新密码" prop="newPassword">
				<el-input type="password" v-model="dataForm.newPassword" size="medium" clearable />
			</el-form-item>
			<el-form-item label="确认密码" prop="confirmPassword">
				<el-input type="password" v-model="dataForm.confirmPassword" size="medium" clearable />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data() {
		const validateConfirmPassword = (rule, value, callback) => {
			if (value != this.dataForm.newPassword) {
				callback(new Error('第二次密码和第一次不同'));
			} else {
				callback();
			}
		};

		return {
			visible: false,
			passwordError: '',
			dataForm: {
				password: '',
				newPassword: '',
				confirmPassword: ''
			},
			dataRule: {
				password: [{ required: true, pattern: '^[a-zA-Z0-9]{6,20}$', message: '密码格式错误' }],
				newPassword: [{ required: true, pattern: '^[a-zA-Z0-9]{6,20}$', message: '密码格式错误' }],
				confirmPassword: [{ required: true, pattern: '^[a-zA-Z0-9]{6,20}$', message: '密码格式错误' }, { validator: validateConfirmPassword, trigger: 'blur' }]
			}
		};
	},
	methods: {
		init() {
			this.visible = true;
			this.passwordError = '';
			this.dataForm.password = '';
			this.dataForm.newPassword = '';
			this.dataForm.confirmPassword = '';
		},
		clearPasswordError() {
			this.passwordError = '';
		},
		dataFormSubmit() {
			this.passwordError = '';
			this.$refs.dataForm.validate((valid) => {
				if (valid) {
					this.$http('/mis_user/update-password', 'POST', {
						oldPassword: this.dataForm.password,
						newPassword: this.dataForm.newPassword
					}, true, (resp) => {
						if (resp.code === 200) {
							this.$message.success('密码修改成功');
							// 直接关闭弹窗
							this.visible = false;
						} else {
							// 直接在输入框下方显示错误信息，不使用弹窗
							this.passwordError = resp.msg || '原密码错误';
							// 聚焦到原密码输入框
							this.$nextTick(() => {
								this.$refs.dataForm.$el.querySelector('input').focus();
							});
						}
					});
				}
			});
		}
	}
};
</script>

<style></style>
