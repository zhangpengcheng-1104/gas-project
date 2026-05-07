<template>
	<el-dialog
		:title="!dataForm.id ? '新增部门' : '修改部门'"
		:close-on-click-modal="false"
		v-model="visible"
		width="500px"
	>
		<el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
			<el-form-item label="部门名称" prop="name">
				<el-input v-model="dataForm.name" size="medium" clearable />
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
	data: function() {
		return {
			visible: false,
			loading: false,
			dataForm: {
				id: null,
				name: ''
			},
			dataRule: {
				name: [{ required: true, message: '请输入部门名称', pattern: '^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,20}$', trigger: 'blur' }]
			}
		};
	},
	methods: {
		init(row) {
			this.visible = true;
			if (row) {
				this.dataForm = { ...row };
			} else {
				this.dataForm = {
					id: null,
					name: ''
				};
			}
		},
		dataFormSubmit() {
			this.$refs.dataForm.validate((valid) => {
				if (valid) {
					let that = this;
					that.loading = true;
					let url = this.dataForm.id ? '/mis_dept/update' : '/mis_dept/save';
					that.$http(url, 'POST', this.dataForm, true, function(resp) {
						that.loading = false;
						if (resp.code === 0 || resp.code === 200) {
							that.$message.success('操作成功');
							that.visible = false;
							that.$emit('refresh');
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
