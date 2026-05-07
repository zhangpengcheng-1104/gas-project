<template>
	<div v-if="isAuth(['ROOT', 'SCHEDULE:SELECT'])">
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
			<el-form-item>
				<el-select v-model="dataForm.deptId" class="input" placeholder="选择科室" clearable="clearable">
					<el-option v-for="one in deptList" :label="one.name" :value="one.id" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-date-picker
					v-model="dataForm.startDate"
					type="date"
					placeholder="选择日期"
					:editable="false"
					format="YYYY-MM-DD"
					value-format="YYYY-MM-DD"
					style="width: 100%;"
					:clearable="false"
				/>
			</el-form-item>
			<el-form-item prop="workerName">
				<el-input v-model="dataForm.workerName" placeholder="巡检人姓名" class="input" clearable="clearable" />
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="searchHandle()">查询</el-button>
				<el-button v-if="isAuth(['ROOT', 'SCHEDULE:INSERT'])" type="primary" @click="addHandle()">
					新增
				</el-button>
			</el-form-item>
		</el-form>
		<el-table
			:data="dataList"
			border
			v-loading="dataListLoading"
			:cell-style="{ padding: '7px 0' }"
			:header-cell-style="{ 'background-color': '#e0e0e0' }"
			style="width: 100%;"
		>
			<el-table-column type="index" header-align="center" align="center" width="100" label="序号">
				<template #default="scope">
					<span>{{ scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column
				prop="deptSubName"
				header-align="center"
				align="center"
				label="班组名称"
				min-width="170"
				:show-overflow-tooltip="true"
			/>
			<el-table-column v-for="(item, index) in 7" :key="index" header-align="center" align="center" :label="dateList[index]" min-width="170">
				<template #default="scope">
					<div
						class="content"
						:class="scope.row.plan && scope.row.plan[index] && scope.row.plan[index].workers && scope.row.plan[index].workers.length > 3 ? 'alignStyle' : ''"
						@dblclick="viewWorkPlanHandle(scope.row.deptName, scope.row.deptSubId, scope.row.plan && scope.row.plan[index] ? scope.row.plan[index].date : null, scope.row.plan && scope.row.plan[index] ? scope.row.plan[index].planIds : [])"
					>
						<div v-if="scope.row.plan && scope.row.plan[index] && scope.row.plan[index].workers && scope.row.plan[index].workers.length > 0">
							<div>{{ scope.row.plan[index].workers.join('，') }}</div>
							<div class="count-info">({{ scope.row.plan[index].workers.length }}/{{ scope.row.plan[index].maximum }}人)</div>
						</div>
					</div>
				</template>
			</el-table-column>
		</el-table>
		<add ref="add" @refreshDataList="loadDataList"></add>
	</div>
</template>

<script>
import dayjs from 'dayjs';
import Add from './medical_dept_sub_work_plan-add.vue';
export default {
	components: {
		Add
	},
	data: function() {
		return {
			dataForm: {
				workerName: null,
				deptId: null,
				startDate: dayjs().format('YYYY-MM-DD'),
				endDate: dayjs().add(6, 'day').format('YYYY-MM-DD')
			},
			deptList: [],
			dataList: [],
			dateList: [],
			dataListLoading: false,
			dataRule: {
				workerName: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,20}$', message: '姓名格式错误' }]
			}
		};
	},
	methods: {
		loadDeptList: function() {
			let that = this;
			that.$http('/medical_dept/searchAll', 'GET', {}, true, function(resp) {
				that.deptList = resp.result;
			});
		},
		loadDataList: function() {
			let that = this;
			that.dataListLoading = true;
			let data = {
				startDate: that.dataForm.startDate,
				endDate: that.dataForm.endDate,
				deptId: that.dataForm.deptId
			};
			that.$http('/worker_plan/searchWorkerPlanInRange', 'POST', data, true, function(resp) {
				let result = resp.result || [];
				console.log('API返回数据:', result);
				that.dataList = that.formatData(result);
				console.log('格式化后数据:', that.dataList);
				that.dataListLoading = false;
			}).catch((err) => {
				console.error('请求失败:', err);
				that.dataListLoading = false;
			});
		},
		generateDateList: function(startDate) {
			let list = [];
			for (let i = 0; i < 7; i++) {
				let date = dayjs(startDate).add(i, 'day').format('YYYY-MM-DD');
				let weekday = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
				let day = weekday[dayjs(date).day()];
				list.push(date + '\n' + day);
			}
			return list;
		},
		formatData: function(data) {
			let that = this;
			let map = {};
			let workerDateMap = {};
			for (let item of data) {
				let deptSubId = item.deptSubId;
				let date = dayjs(item.date).format('YYYY-MM-DD');
				let workerName = item.workerName;
				
				if (!map[deptSubId]) {
					map[deptSubId] = {
						deptSubId: deptSubId,
						deptSubName: item.deptSubName,
						deptName: item.deptName,
						plan: {}
					};
				}
				
				let dateIndex = that.getDateIndex(date, that.dataForm.startDate);
				if (dateIndex >= 0 && dateIndex < 7) {
					if (!map[deptSubId].plan[dateIndex]) {
						map[deptSubId].plan[dateIndex] = {
							date: date,
							workers: [],
							planIds: [],
							maximum: item.maximum || 1
						};
					}
					let workerDateKey = workerName + '_' + date;
					if (workerName && !workerDateMap[workerDateKey]) {
						if (!map[deptSubId].plan[dateIndex].workers.includes(workerName)) {
							map[deptSubId].plan[dateIndex].workers.push(workerName);
						}
						workerDateMap[workerDateKey] = deptSubId;
					}
					if (item.id && !map[deptSubId].plan[dateIndex].planIds.includes(item.id)) {
						map[deptSubId].plan[dateIndex].planIds.push(item.id);
					}
				}
			}
			
			let result = [];
			for (let key in map) {
				let item = map[key];
				let plan = [];
				for (let i = 0; i < 7; i++) {
					if (item.plan[i]) {
						plan.push(item.plan[i]);
					} else {
						plan.push({
							date: dayjs(that.dataForm.startDate).add(i, 'day').format('YYYY-MM-DD'),
							workers: [],
							planIds: [],
							maximum: 1
						});
					}
				}
				result.push({
					deptSubId: item.deptSubId,
					deptSubName: item.deptSubName,
					deptName: item.deptName,
					plan: plan
				});
			}
			return result;
		},
		getDateIndex: function(date, startDate) {
			let start = dayjs(startDate);
			let current = dayjs(date);
			return current.diff(start, 'day');
		},
		searchHandle: function() {
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					this.$refs['dataForm'].clearValidate();
					this.dataForm.startDate = dayjs(this.dataForm.startDate).format('YYYY-MM-DD');
					this.dataForm.endDate = dayjs(this.dataForm.startDate).add(6, 'day').format('YYYY-MM-DD');
					this.dateList = this.generateDateList(this.dataForm.startDate);
					this.loadDataList();
				} else {
					return false;
				}
			});
		},
		addHandle: function() {
			this.$refs.add.init();
		},
		viewWorkPlanHandle: function(deptName, deptSubId, date, planIds) {
			console.log('查看巡检计划', deptName, deptSubId, date, planIds);
		}
	},
	created: function() {
		this.dataForm.startDate = dayjs().format('YYYY-MM-DD');
		this.dataForm.endDate = dayjs().add(6, 'day').format('YYYY-MM-DD');
		this.dateList = this.generateDateList(this.dataForm.startDate);
		this.loadDeptList();
		this.loadDataList();
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('medical_dept_sub_work_plan.less');
</style>
