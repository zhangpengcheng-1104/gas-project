import App from './App'

// #ifndef VUE3
import Vue from 'vue'
Vue.config.productionTip = false

//使用uView组件库
import uView from 'uview-ui'
Vue.use(uView)

App.mpType = 'app'
const app = new Vue({
	...App
})
app.$mount()
// #endif

// #ifdef VUE3
// import {
// 	createSSRApp
// } from 'vue'
// export function createApp() {
// 	const app = createSSRApp(App)
// 	return {
// 		app
// 	}
// }
// #endif


let minioUrl = "http://localhost:9000/worker"
Vue.prototype.minioUrl = minioUrl

let inspectionMinioUrl = "http://localhost:9000/inspection"
Vue.prototype.inspectionMinioUrl = inspectionMinioUrl

let rectificationImageUrl = baseUrl + "/image/rectification?path="
Vue.prototype.rectificationMinioUrl = rectificationImageUrl

let hazardMinioUrl = "http://localhost:9000/hidden-danger"
Vue.prototype.hazardMinioUrl = hazardMinioUrl

let patientUrl = minioUrl + "/patient-wx"
Vue.prototype.patientUrl = patientUrl

Vue.prototype.tencent = {
	trtc: {
		appid: "1600134241"
	}
}

let baseUrl = "http://localhost:8091/patient-wx-api"

let inspectionImageUrl = baseUrl + "/image/inspection?path="
Vue.prototype.inspectionImageUrl = inspectionImageUrl

let workerImageUrl = baseUrl + "/image/worker?path="
Vue.prototype.workerImageUrl = workerImageUrl

Vue.prototype.api = {
   loginOrRegister: baseUrl + "/user/loginOrRegister",
   insertUserInfoCard: baseUrl + "/user/info/card/insert",
   updateUserInfoCard: baseUrl + "/user/info/card/update",
   searchUserInfo:baseUrl + "/user/searchUserInfo",
   searchUserInfoCard: baseUrl + "/user/info/card/searchUserInfoCard",
   hasUserInfoCard: baseUrl + "/user/info/card/hasUserInfoCard",
   searchMedicalDeptList: baseUrl + "/medical/dept/searchMedicalDeptList",
   searchMedicalDeptSubList: baseUrl + "/medical/dept/sub/searchMedicalDeptSubList",
   searchCanRegisterInDateRange: baseUrl + "/registration/searchCanRegisterInDateRange",
   searchDeptSubDoctorPlanInDay: baseUrl + "/registration/searchDeptSubDoctorPlanInDay",
   searchContent: baseUrl + "/worker/searchContent",
   searchCanWorkInDateRange: baseUrl + "/worker_plan/searchCanWorkInDateRange",
   searchWorkerPlanInDay: baseUrl + "/worker_plan/searchWorkerPlanInDay",
   searchByWorkerPlanId: baseUrl + "/worker_inspection/searchByWorkerPlanId",
   saveInspection: baseUrl + "/worker_inspection/save",
   updateInspection: baseUrl + "/worker_inspection/update",
   uploadInspectionPhoto: baseUrl + "/upload/inspectionPhoto",
   getAmapConfig: baseUrl + "/amap/config",
   geocode: baseUrl + "/amap/geocode",
   searchHazardListByAssigneeId: baseUrl + "/hazard_info/wx/searchByAssigneeId",
   searchHazardById: baseUrl + "/hazard_info/searchById",
   submitRectification: baseUrl + "/hazard_info/wx/submitRectification",
   uploadRectificationPhoto: baseUrl + "/hazard_info/wx/uploadRectificationPhoto",
   uploadRectificationVideo: baseUrl + "/hazard_info/wx/uploadRectificationVideo",
}




Vue.prototype.ajax = function(url, method, data, fun, load) {
	let timer = null
	if (load == true || load == undefined) {
		uni.showLoading({
			title: "执行中"
		})
		timer = setTimeout(function() {
			uni.hideLoading()
		}, 60 * 1000)
	}
	uni.request({
		"url": url,
		"method": method,
		"header": {
			token: uni.getStorageSync("token")
		},
		"data": data,
		success: function(resp) {
			if (load == true || load == undefined) {
				clearTimeout(timer)
				uni.hideLoading()
			}
			if (resp.statusCode == 401) {
				uni.removeStorageSync("token")
				uni.showToast({
					icon: "error",
					title: "请登录小程序"
				})
				setTimeout(() => {
					uni.switchTab({
						"url":"/pages/mine/mine"
					})
				}, 2000);
			} else if (resp.statusCode == 200 && resp.data.code == 200) {
				let data = resp.data
				if (data.hasOwnProperty("token")) {
					let token = data.token
					uni.setStorageSync("token", token)
				}
				fun(resp)
			} else {
				uni.showToast({
					icon: "none",
					title: "执行异常"
				})
				console.error(resp.data)
			}
		},
		fail: function(error) {
			if (load == true || load == undefined) {
				clearTimeout(timer)
				uni.hideLoading()
			}
		}
	})
}
