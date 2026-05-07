/**
 * 验证邮箱
 * @param {*} s
 */
export function isEmail(s) {
	return /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(s)
}

/**
 * 验证手机号码
 * @param {*} s
 */
export function isMobile(s) {
	return /^1[0-9]{10}$/.test(s)
}

/**
 * 验证电话号码
 * @param {*} s
 */
export function isPhone(s) {
	return /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(s)
}

/**
 * 验证URL地址
 * @param {*} s
 */
export function isURL(s) {
	return /^http[s]?:\/\/.*/.test(s)
}

/**
 * 验证用户名
 * @param {Object} s
 */
export function isUsername(s) {
	if (!s) return false
	let len = 0
	for (let i = 0; i < s.length; i++) {
		let c = s.charCodeAt(i)
		if (c >= 0x0001 && c <= 0x007F) {
			len += 1
		} else if (c >= 0x0080 && c <= 0x07FF) {
			len += 2
		} else if (c >= 0x0800 && c <= 0xFFFF) {
			len += 3
		} else {
			len += 4
		}
	}
	return len >= 2 && len <= 18 && /^[a-zA-Z0-9\u4e00-\u9fa5_]+$/.test(s)
}


/**
 * 验证密码
 * @param {Object} s
 */
export function isPassword(s) {
	return /^[a-zA-Z0-9]{6,20}$/.test(s)
}
