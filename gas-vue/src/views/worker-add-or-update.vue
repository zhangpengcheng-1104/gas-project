<template>
	<el-dialog
		:title="!dataForm.id ? '新增巡检员' : '修改巡检员'"
		:close-on-click-modal="false"
		v-model="visible"
		width="550px"
	>
		<el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="100px">
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="姓名" prop="name">
						<el-input v-model="dataForm.name" size="medium" clearable placeholder="请输入姓名" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="性别" prop="sex">
						<el-radio-group v-model="dataForm.sex">
							<el-radio label="男">男</el-radio>
							<el-radio label="女">女</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="身份证号" prop="pid">
						<el-input v-model="dataForm.pid" size="medium" clearable placeholder="请输入身份证号" maxlength="18" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="出生日期" prop="birthday">
						<el-date-picker v-model="dataForm.birthday" type="date" placeholder="选择出生日期" style="width: 100%" value-format="YYYY-MM-DD" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="电话" prop="tel">
						<el-input v-model="dataForm.tel" size="medium" clearable placeholder="请输入电话" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="电子邮箱" prop="email">
						<el-input v-model="dataForm.email" size="medium" clearable placeholder="请输入电子邮箱" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="学历" prop="degree">
						<el-select v-model="dataForm.degree" placeholder="请选择学历" style="width: 100%">
							<el-option label="小学" value="小学" />
							<el-option label="中学" value="中学" />
							<el-option label="大专" value="大专" />
							<el-option label="大学" value="大学" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="职位" prop="job">
						<el-select v-model="dataForm.job" placeholder="请选择职位" style="width: 100%">
							<el-option label="助工" value="助工" />
							<el-option label="工程师" value="工程师" />
							<el-option label="高工" value="高工" />
							<el-option label="总工" value="总工" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="毕业学校" prop="school">
						<el-input v-model="dataForm.school" size="medium" clearable placeholder="请输入毕业学校" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="入职时间" prop="hiredate">
						<el-date-picker v-model="dataForm.hiredate" type="date" placeholder="选择入职时间" style="width: 100%" value-format="YYYY-MM-DD" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="科室" prop="deptId">
						<el-select v-model="dataForm.deptId" placeholder="请选择科室" style="width: 100%" @change="handleDeptChange">
							<el-option v-for="dept in deptList" :key="dept.id" :label="dept.name" :value="dept.id" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="班组" prop="subId">
						<el-select v-model="dataForm.subId" placeholder="请选择班组" style="width: 100%" :disabled="!dataForm.deptId">
							<el-option v-for="sub in subList" :key="sub.id" :label="sub.name" :value="sub.id" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="24">
					<el-form-item label="住址" prop="address">
						<el-input v-model="dataForm.address" size="medium" clearable placeholder="请输入住址" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="照片" prop="photo">
						<el-upload
							class="avatar-uploader"
							:action="uploadUrl"
							:show-file-list="false"
							:on-success="handlePhotoSuccess"
							:before-upload="beforePhotoUpload"
							:headers="uploadHeaders"
							name="file">
							<img v-if="dataForm.photo" :src="dataForm.photo" class="avatar">
							<i v-else class="el-icon-plus avatar-uploader-icon"></i>
						</el-upload>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="优先推荐" prop="recommended">
						<el-radio-group v-model="dataForm.recommended">
							<el-radio :label="true">是</el-radio>
							<el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="状态" prop="status">
						<el-radio-group v-model="dataForm.status">
							<el-radio :label="1">在职</el-radio>
							<el-radio :label="2">离职</el-radio>
							<el-radio :label="3">退休</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="24">
					<el-form-item label="备注" prop="remark">
						<el-input v-model="dataForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="24">
					<el-form-item label="标签描述" prop="tag">
						<el-select v-model="dataForm.tag" multiple placeholder="请选择标签描述" style="width: 100%" collapse-tags collapse-tags-tooltip>
							<el-option v-for="tag in tagOptions" :key="tag" :label="tag" :value="tag" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row :gutter="20">
				<el-col :span="24">
					<el-form-item label="详细介绍" prop="description">
						<el-input v-model="dataForm.description" type="textarea" :rows="3" placeholder="请输入详细介绍" />
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit" :disabled="!hasChanges">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data() {
		return {
			visible: false,
			dataForm: {
				id: '',
				name: '',
				pid: '',
				uuid: '',
				sex: '男',
				photo: '',
				birthday: '',
				school: '',
				degree: '',
				tel: '',
				address: '',
				email: '',
				job: '',
				remark: '',
				description: '',
				hiredate: '',
				tag: [],
				deptId: '',
				subId: '',
				recommended: false,
				status: 1
			},
			tagOptions: ['经验丰富', '技术过硬', '责任心强', '沟通能力强', '团队协作', '学习能力强', '工作认真', '积极主动', '吃苦耐劳', '专业素养高'],
			originalForm: {},
			dataRule: {
				name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
				pid: [
					{ required: true, message: '请输入身份证号', trigger: 'blur' },
					{ pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
				],
				tel: [
					{ required: true, message: '请输入电话', trigger: 'blur' },
					{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
				],
				email: [
					{ type: 'email', message: '请输入正确的电子邮箱', trigger: 'blur' }
				],
				deptId: [{ required: true, message: '请选择科室', trigger: 'change' }]
			},
			deptList: [],
			subList: [],
			uploadUrl: this.$baseUrl + '/upload/photo',
			uploadHeaders: {
				token: localStorage.getItem('token')
			}
		};
	},
	computed: {
		hasChanges() {
			if (!this.originalForm.id) {
				return true;
			}
			const fields = ['name', 'pid', 'uuid', 'sex', 'photo', 'birthday', 'school', 'degree', 'tel', 'address', 'email', 'job', 'remark', 'description', 'hiredate', 'tag', 'deptId', 'subId', 'recommended', 'status'];
			for (let field of fields) {
				if (this.dataForm[field] !== this.originalForm[field]) {
					return true;
				}
			}
			return false;
		}
	},
	methods: {
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
		init(row) {
			this.visible = true;
			this.getDeptList();
			if (row) {
				let id = typeof row === 'object' ? row.id : row;
				this.dataForm.id = id;
				this.getWorkerInfo(id);
			} else {
				this.dataForm = {
					id: '',
					name: '',
					pid: '',
					uuid: '',
					sex: '男',
					photo: '',
					birthday: '',
					school: '',
					degree: '',
					tel: '',
					address: '',
					email: '',
					job: '',
					remark: '',
					description: '',
					hiredate: '',
					tag: [],
					deptId: '',
					subId: '',
					recommended: false,
					status: 1
				};
				this.subList = [];
				this.originalForm = {};
			}
		},
		getDeptList() {
			let that = this;
			that.$http('/medical_dept/searchAll', 'GET', {}, true, function(resp) {
				if (resp.code === 0 || resp.code === 200) {
					that.deptList = resp.result || resp.list || [];
				}
			});
		},
		getSubList(deptId) {
			let that = this;
			that.$http('/medical_dept_sub/searchByDeptId?deptId=' + deptId, 'GET', {}, true, function(resp) {
				if (resp.code === 0 || resp.code === 200) {
					that.subList = resp.result || resp.list || [];
				}
			});
		},
		getWorkerInfo(id) {
			let that = this;
			that.$http('/gas_worker/info/' + id, 'GET', {}, true, function(resp) {
				if (resp.code === 0 || resp.code === 200) {
					let info = resp.result || resp.worker || resp.data || {};
					that.dataForm = {
						id: info.id || id,
						name: info.name || '',
						pid: info.pid || '',
						uuid: info.uuid || '',
						sex: info.sex || '男',
						photo: info.photo ? that.resolvePhotoUrl(info.photo) : '',
						birthday: info.birthday || '',
						school: info.school || '',
						degree: info.degree || '',
						tel: info.tel || '',
						address: info.address || '',
						email: info.email || '',
						job: info.job || '',
						remark: info.remark || '',
						description: info.description || '',
						hiredate: info.hiredate || '',
						tag: info.tag ? (Array.isArray(info.tag) ? info.tag : info.tag.split(',')) : [],
						deptId: info.deptId || '',
						subId: info.subId || '',
						recommended: info.recommended || false,
						status: info.status || 1
					};
					that.originalForm = { ...that.dataForm };
					if (info.deptId) {
						that.getSubList(info.deptId);
					}
				}
			});
		},
		handleDeptChange(val) {
			this.dataForm.subId = '';
			this.subList = [];
			if (val) {
				this.getSubList(val);
			}
		},
		handlePhotoSuccess(response) {
			if (response.code === 0 || response.code === 200) {
				let photoUrl = '';
				if (response.result) {
					photoUrl = response.result.photo || response.result.url || response.result.path;
				} else {
					photoUrl = response.photo || response.url || response.path;
				}
				this.dataForm.photo = this.resolvePhotoUrl(photoUrl);
				this.$message.success('照片上传成功');
			} else {
				this.$message.error(response.msg || '照片上传失败');
			}
		},
		beforePhotoUpload(file) {
			const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
			const isLt2M = file.size / 1024 / 1024 < 2;
			if (!isJPG) {
				this.$message.error('上传图片只能是 JPG/PNG 格式!');
			}
			if (!isLt2M) {
				this.$message.error('上传图片大小不能超过 2MB!');
			}
			return isJPG && isLt2M;
		},
		dataFormSubmit() {
			this.$refs.dataForm.validate((valid) => {
				if (valid) {
					let that = this;
					let url = this.dataForm.id ? '/gas_worker/update' : '/gas_worker/save';
					let submitData = { ...this.dataForm };
					submitData.tag = Array.isArray(submitData.tag) ? submitData.tag.join(',') : submitData.tag;
					
					if (submitData.photo && submitData.photo.startsWith('http')) {
						if (submitData.photo.includes('/worker/')) {
							submitData.photo = submitData.photo.substring(submitData.photo.indexOf('/worker/') + 7);
							let separator = submitData.photo.includes('?') ? '?' : '&';
							if (submitData.photo.includes(separator)) {
								submitData.photo = submitData.photo.split(separator)[0];
							}
						}
					}
					
					that.$http(url, 'POST', submitData, true, function(resp) {
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

<style lang="less" scoped>
.avatar-uploader {
	.el-upload {
		border: 1px dashed #d9d9d9;
		border-radius: 6px;
		cursor: pointer;
		position: relative;
		overflow: hidden;
		&:hover {
			border-color: #409EFF;
		}
	}
}
.avatar-uploader-icon {
	font-size: 28px;
	color: #8c939d;
	width: 148px;
	height: 148px;
	line-height: 148px;
	text-align: center;
}
.avatar {
	width: 148px;
	height: 148px;
	display: block;
	object-fit: cover;
}
</style>
