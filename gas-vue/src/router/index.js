import {
	createRouter,
	createWebHashHistory
}
from 'vue-router'

const routes = [{
		path: '/login',
		name: 'Login',
		component: () => import('../views/login.vue')
	},
	{
		path: '/',
		name: 'Main',
		component: () => import("../views/main.vue"),
		children: [{
				path: '/home',
				name: 'Home',
				component: () => import("../views/home.vue"),
				meta: {
					title: '首页',
				}
			},
			{
				path: "/role",
				name: "Role",
				component: () => import("../views/role.vue"),
				meta: {
					title: "角色管理",
					isTab: true,
					permission: "MIS_ROLE:SELECT"
				}
			},
			{
				path: '/user',
				name: 'User',
				component: () => import("../views/user.vue"),
				meta: {
					title: '用户管理',
					isTab: true,
					permission: "MIS_USER:SELECT"
				}
			},
			{
				path: '/dept',
				name: 'Dept',
				component: () => import("../views/dept.vue"),
				meta: {
					title: '部门管理',
					isTab: true,
					permission: "DEPT:SELECT"
				}
			},
			{
				path: '/worker',
				name: 'Worker',
				component: () => import("../views/worker.vue"),
				meta: {
					title: '巡检人员管理',
					isTab: true,
					permission: "GAS_WORKER:SELECT"
				}
			},
			{
				path: '/gas_user',
				name: 'GasUser',
				component: () => import("../views/gas_user.vue"),
				meta: {
					title: '燃气用户管理',
					isTab: true,
					permission: "GAS_USER:SELECT"
				}
			},
			{
				path: '/work_plan',
				name: 'WorkPlan',
				component: () => import("../views/medical_dept_sub_work_plan.vue"),
				meta: {
					title: '巡检日程表',
					isTab: true,
					permission: "SCHEDULE:SELECT"
				}
			},
			{
				path: '/gas_worker_plan',
				name: 'GasWorkerPlan',
				component: () => import("../views/gas_worker_plan.vue"),
				meta: {
					title: '燃气巡检计划',
					isTab: true,
					permission: "SCHEDULE:SELECT"
				}
			},
			{
				path: '/hazard_info',
				name: 'HazardInfo',
				component: () => import("../views/hazard_info.vue"),
				meta: {
					title: '隐患管理',
					isTab: true,
					permission: "HAZARD_INFO:SELECT"
				}
			},
		]
	},
	{
		path: "/404",
		name: "NotFound",
		component: () => import("../views/404.vue")
	},
	{
		path: '/:pathMatch(.*)*',
		redirect: "/404"
	}
]

const router = createRouter({
	history: createWebHashHistory(),
	routes
})
router.beforeEach((to, from, next) => {
	if (to.name != "Login") {
		let permissions = localStorage.getItem("permissions")
		let token = localStorage.getItem("token")
		if (permissions == null || permissions == "" || token == null || token == "") {
			next({
				name: 'Login'
			})
			return
		}
		
		if (to.meta.permission) {
			let permissionList = []
			try {
				permissionList = JSON.parse(permissions)
			} catch (e) {
				console.error('Failed to parse permissions:', e)
				next({ name: 'Login' })
				return
			}
			
			if (!permissionList.includes('ROOT') && !permissionList.includes(to.meta.permission)) {
				next({ name: 'Home' })
				return
			}
		}
	}
	next()
})

export default router
