<template>
	<el-dialog
		title="新增巡检计划"
		v-if="isAuth(['ROOT', 'SCHEDULE:INSERT'])"
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
				<el-form-item label="巡检用户" prop="userId">
					<el-select v-model="dataForm.userId" clearable filterable placeholder="请选择巡检用户">
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
						:min="1"
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
				<el-button type="primary" @click="dataFormSubmit" :disabled="actualCount >= dataForm.maximum">确定</el-button>
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
			dept: [],
			workerList: [],
			gasUserList: [],
			actualCount: 0,
			dataForm: {
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
				]
			}
		};
	},
	methods: {
		init: function() {
			let that = this;
			that.visible = true;
			that.actualCount = 0;
			that.dataForm = {
				deptSub: null,
				deptSubId: null,
				workerId: null,
				userId: null,
				date: new dayjs().format('YYYY-MM-DD'),
				maximum: 1,
				description: ''
			};
			that.workerList = [];
			that.gasUserList = [];
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
				that.loadDept();
				that.loadGasUserList();
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
			that.$http('/medical_dept/searchAll', 'GET', {}, true, function(resp) {
				let result = resp.result;
				that.dept = [];
				for (let one of result) {
					that.dept.push({
						value: one.id,
						label: one.name,
						children: []
					});
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
					});
				}
			});
		},
		deptSubChangeHandle: function() {
			let that = this;
			that.dataForm.deptSubId = that.dataForm.deptSub[1];
			that.dataForm.workerId = null;
			that.workerList = [];
			that.searchByDeptSubId();
			if (that.dataForm.date) {
				that.loadActualCount();
				that.loadExistingMaximum();
			}
		},
		loadActualCount: function() {
			let that = this;
			if (!that.dataForm.deptSubId || !that.dataForm.date) {
				return;
			}
			that.$http('/worker_plan/countByDateAndDeptSub', 'POST', { 
				deptSubId: that.dataForm.deptSubId, 
				date: that.dataForm.date
			}, true, function(resp) {
				if (resp && (resp.code === 0 || resp.code === 200)) {
					that.actualCount = resp.count || 0;
				}
			});
		},
		loadExistingMaximum: function() {
			let that = this;
			if (!that.dataForm.deptSubId || !that.dataForm.date) {
				return;
			}
			that.$http('/worker_plan/searchMaximumByDateAndDeptSub', 'POST', { 
				deptSubId: that.dataForm.deptSubId, 
				date: that.dataForm.date
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
				that.loadActualCount();
				that.loadExistingMaximum();
			}
		},
		searchByDeptSubId: function() {
			let that = this;
			if (!that.dataForm.deptSubId) {
				return;
			}
			that.$http('/gas_worker/searchByDeptSubId', 'POST', { deptSubId: that.dataForm.deptSubId }, true, function(resp) {
				that.workerList = resp.result || [];
			});
		},
		dataFormSubmit: function() {
			let that = this;
			that.$refs['dataForm'].validate(valid => {
				if (valid) {
					if (that.actualCount >= that.dataForm.maximum) {
						ElMessage.error('当天该班组已达到最大人数，无法继续派遣');
						return;
					}
					let data = {
						deptSubId: that.dataForm.deptSubId,
						workerId: that.dataForm.workerId,
						userId: that.dataForm.userId,
						date: that.dataForm.date,
						maximum: that.dataForm.maximum,
						description: that.dataForm.description
					};
					that.$http('/worker_plan/insert', 'POST', data, true, function(resp) {
						if (resp.code === 0 || resp.code === 200) {
							ElMessage.success('新增巡检计划成功');
							that.visible = false;
							that.$emit('refreshDataList');
						} else {
							ElMessage.error(resp.msg || '新增失败');
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
