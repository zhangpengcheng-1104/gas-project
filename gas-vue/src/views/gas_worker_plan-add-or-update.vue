<template>
	<el-dialog
		:title="title"
		v-if="isAuth(['ROOT', 'SCHEDULE:INSERT', 'SCHEDULE:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="550px"
	>
		<el-scrollbar height="500px">
			<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
				<el-form-item label="科室班组" prop="deptSub">
					<el-cascader
						v-model="dataForm.deptSub"
						:options="dept"
						style="width:60%"
						@change="deptSubChangeHandle"
					/>
				</el-form-item>
				<el-form-item label="巡检人员" prop="workerId">
					<el-select v-model="dataForm.workerId">
						<el-option v-for="one in workerList" :label="one.name" :value="one.id"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="燃气用户" prop="userId">
					<el-select v-model="dataForm.userId" clearable filterable placeholder="请选择燃气用户">
						<el-option v-for="one in gasUserList" :label="one.userName" :value="one.id"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="巡检日期" prop="date">
					<el-date-picker
						v-model="dataForm.date"
						type="date"
						placeholder="选择日期"
						:editable="false"
						format="YYYY-MM-DD"
						value-format="YYYY-MM-DD"
						style="width: 100%;"
						:clearable="false"
						@change="dateChangeHandle"
					/>
				</el-form-item>
				<el-form-item label="最大人数" prop="maximum">
					<el-input-number
						v-model="dataForm.maximum"
						:min="dataForm.id ? actualCount : 1"
						:max="99"
						:step="1"
						style="width: 150px;"
					/>
					<span style="margin-left: 10px; color: #909399;">
						（当前实际人数：{{ actualCount }}人）
					</span>
				</el-form-item>
				<el-form-item label="实际人数">
					<span>{{ actualCount }}人</span>
					<span style="color: #909399; margin-left: 10px;">（当天该班组已安排人数，不可修改）</span>
					<el-tag v-if="actualCount >= dataForm.maximum" type="danger" style="margin-left: 10px;">已达上限</el-tag>
				</el-form-item>
				<el-form-item label="备注" prop="description">
					<el-input
						v-model="dataForm.description"
						type="textarea"
						:rows="3"
						placeholder="请输入备注信息"
						style="width: 100%;"
					/>
				</el-form-item>
			</el-form>
		</el-scrollbar>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button type="primary" @click="dataFormSubmit" :disabled="!dataForm.id && actualCount >= dataForm.maximum">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
import dayjs from 'dayjs';
import { ElMessage } from 'element-plus';
export default {
	data: function() {
		return {
			visible: false,
			title: '新增燃气巡检计划',
			dept: [],
			workerList: [],
			gasUserList: [],
			isLoading: false,
			actualCount: 0,
			dataForm: {
				id: null,
				deptId: null,
				deptSub: null,
				deptSubId: null,
				workerId: null,
				userId: null,
				date: new dayjs().format('YYYY-MM-DD'),
				maximum: 1,
				description: ''
			},
			dataRule: {
				deptSub: [
					{
						required: true,
						message: '科室班组不能为空'
					}
				],
				workerId: [
					{
						required: true,
						message: '巡检人员不能为空'
					}
				],
				date: [
					{
						required: true,
						message: '巡检日期不能为空'
					}
				]
			}
		};
	},
	methods: {
		init: function(id) {
			let that = this;
			that.visible = true;
			that.title = id ? '编辑燃气巡检计划' : '新增燃气巡检计划';
			that.workerList = [];
			that.gasUserList = [];
			that.actualCount = 0;
			if (!id) {
				that.dataForm = {
					id: null,
					deptId: null,
					deptSub: null,
					deptSubId: null,
					workerId: null,
					userId: null,
					date: dayjs().format('YYYY-MM-DD'),
					maximum: 1,
					description: ''
				};
			}
			that.loadDept().then(() => {
				that.loadGasUserList();
				that.$nextTick(() => {
					if (that.$refs['dataForm']) {
						that.$refs['dataForm'].clearValidate();
					}
					if (id) {
						that.loadPlanData(id);
					}
				});
			});
		},
		loadPlanData: function(id) {
			let that = this;
			that.isLoading = true;
			that.$http('/worker_plan/searchById', 'POST', { id: id }, true, function(resp) {
				if (resp && (resp.code === 0 || resp.code === 200)) {
					that.dataForm.id = resp.id;
					that.dataForm.userId = resp.userId;
					that.dataForm.date = resp.date;
					that.dataForm.description = resp.description;
					that.dataForm.deptId = resp.deptId;
					that.dataForm.deptSubId = resp.deptSubId;
					that.dataForm.workerId = resp.workerId;
					that.dataForm.maximum = resp.maximum || 1;
					that.dataForm.deptSub = [resp.deptId, resp.deptSubId];
					that.loadActualCount(resp.deptSubId, resp.date, id);
					that.$nextTick(() => {
						that.loadWorkersByDeptSub(resp.deptSubId, resp.workerId);
						that.isLoading = false;
					});
				} else {
					that.isLoading = false;
				}
			});
		},
		loadActualCount: function(deptSubId, date, excludeId) {
			let that = this;
			that.$http('/worker_plan/countByDateAndDeptSub', 'POST', { 
				deptSubId: deptSubId, 
				date: date
			}, true, function(resp) {
				if (resp && (resp.code === 0 || resp.code === 200)) {
					that.actualCount = resp.count || 0;
					if (that.dataForm.id && that.dataForm.maximum < that.actualCount) {
						that.dataForm.maximum = that.actualCount;
					}
				}
			});
		},
		loadExistingMaximum: function(deptSubId, date) {
			let that = this;
			that.$http('/worker_plan/searchMaximumByDateAndDeptSub', 'POST', { 
				deptSubId: deptSubId, 
				date: date
			}, true, function(resp) {
				if (resp && (resp.code === 0 || resp.code === 200)) {
					if (resp.maximum && resp.maximum > 0) {
						that.dataForm.maximum = resp.maximum;
					}
				}
			});
		},
		dateChangeHandle: function() {
			let that = this;
			if (that.dataForm.deptSubId && that.dataForm.date) {
				that.loadActualCount(that.dataForm.deptSubId, that.dataForm.date, that.dataForm.id);
				if (!that.dataForm.id) {
					that.loadExistingMaximum(that.dataForm.deptSubId, that.dataForm.date);
				}
			}
		},
		loadWorkersByDeptSub: function(deptSubId, selectedWorkerId) {
			let that = this;
			if (!deptSubId) {
				return;
			}
			that.$http('/gas_worker/searchByDeptSubId', 'POST', { deptSubId: deptSubId }, true, function(resp) {
				that.workerList = resp.result || [];
				if (selectedWorkerId) {
					that.dataForm.workerId = selectedWorkerId;
				}
			});
		},
		loadGasUserList: function() {
			let that = this;
			that.$http('/gas_user/searchAll', 'GET', {}, true, function(resp) {
				that.gasUserList = resp.result || [];
			});
		},
		loadDept: function() {
			let that = this;
			return new Promise((resolve) => {
				that.$http('/medical_dept/searchAll', 'GET', {}, true, function(resp) {
					let result = resp.result;
					that.dept = [];
					let promises = [];
					for (let one of result) {
						that.dept.push({
							value: one.id,
							label: one.name,
							children: []
						});
						let promise = new Promise((res) => {
							that.$http('/medical_dept_sub/searchByDeptId?deptId=' + one.id, 'GET', {}, true, function(resp2) {
								let index = that.dept.findIndex(item => item.value === one.id);
								if (index !== -1) {
									let children = [];
									for (let sub of resp2.result) {
										children.push({
											value: sub.id,
											label: sub.name
										});
									}
									that.dept[index].children = children;
								}
								res();
							});
						});
						promises.push(promise);
					}
					Promise.all(promises).then(() => {
						resolve();
					});
				});
			});
		},
		deptSubChangeHandle: function() {
			let that = this;
			if (that.isLoading) {
				return;
			}
			if (that.dataForm.deptSub && that.dataForm.deptSub.length === 2) {
				that.dataForm.deptSubId = that.dataForm.deptSub[1];
				that.dataForm.workerId = null;
				that.workerList = [];
				that.loadWorkersByDeptSub(that.dataForm.deptSubId);
				if (that.dataForm.date) {
					that.loadActualCount(that.dataForm.deptSubId, that.dataForm.date, that.dataForm.id);
					if (!that.dataForm.id) {
						that.loadExistingMaximum(that.dataForm.deptSubId, that.dataForm.date);
					}
				}
			}
		},
		dataFormSubmit: function() {
			let that = this;
			that.$refs['dataForm'].validate(valid => {
				if (valid) {
					let data = {
						id: that.dataForm.id,
						deptSubId: that.dataForm.deptSubId,
						workerId: that.dataForm.workerId,
						userId: that.dataForm.userId,
						date: that.dataForm.date,
						maximum: that.dataForm.maximum,
						description: that.dataForm.description
					};
					let url = that.dataForm.id ? '/worker_plan/update' : '/worker_plan/insert';
					that.$http(url, 'POST', data, true, function(resp) {
						if (resp.code === 0 || resp.code === 200) {
							ElMessage.success(that.dataForm.id ? '更新燃气巡检计划成功' : '新增燃气巡检计划成功');
							that.visible = false;
							that.$emit('refreshDataList');
						} else {
							ElMessage.error(resp.msg || '操作失败');
						}
					});
				}
			});
		}
	}
};
</script>

<style lang="less" scoped>
</style>
