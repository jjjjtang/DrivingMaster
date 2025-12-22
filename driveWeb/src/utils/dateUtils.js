// dateUtils.js
// 日期时间格式化工具函数

/**
 * 格式化日期时间
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @param {string} format - 格式字符串，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期时间字符串
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!date) return ''

    const d = date instanceof Date ? date : new Date(date)

    // 如果日期无效，返回空字符串
    if (isNaN(d.getTime())) {
        return ''
    }

    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')

    const formatMap = {
        'YYYY': year,
        'MM': month,
        'DD': day,
        'HH': hours,
        'mm': minutes,
        'ss': seconds,
        'Y': year,
        'M': month,
        'D': day,
        'H': hours,
        'm': minutes,
        's': seconds
    }

    return format.replace(/(YYYY|YY|MM|M|DD|D|HH|H|mm|m|ss|s)/g, match => formatMap[match] || match)
}

/**
 * 格式化日期（不含时间）
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @param {string} format - 格式字符串，默认 'YYYY-MM-DD'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
    return formatDateTime(date, format)
}

/**
 * 格式化时间（不含日期）
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @param {string} format - 格式字符串，默认 'HH:mm:ss'
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(date, format = 'HH:mm:ss') {
    return formatDateTime(date, format)
}

/**
 * 获取相对时间描述（如：刚刚、1分钟前、1小时前等）
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @returns {string} 相对时间描述
 */
export function getRelativeTime(date) {
    if (!date) return ''

    const d = date instanceof Date ? date : new Date(date)

    if (isNaN(d.getTime())) {
        return ''
    }

    const now = new Date()
    const diffMs = now - d
    const diffSec = Math.floor(diffMs / 1000)
    const diffMin = Math.floor(diffSec / 60)
    const diffHour = Math.floor(diffMin / 60)
    const diffDay = Math.floor(diffHour / 24)

    if (diffSec < 60) {
        return '刚刚'
    } else if (diffMin < 60) {
        return `${diffMin}分钟前`
    } else if (diffHour < 24) {
        return `${diffHour}小时前`
    } else if (diffDay === 1) {
        return '昨天'
    } else if (diffDay === 2) {
        return '前天'
    } else if (diffDay < 7) {
        return `${diffDay}天前`
    } else if (diffDay < 30) {
        const weeks = Math.floor(diffDay / 7)
        return `${weeks}周前`
    } else if (diffDay < 365) {
        const months = Math.floor(diffDay / 30)
        return `${months}个月前`
    } else {
        const years = Math.floor(diffDay / 365)
        return `${years}年前`
    }
}

/**
 * 获取友好的日期时间显示
 * 今天显示时间，昨天显示"昨天"，其他显示日期
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @returns {string} 友好的日期时间字符串
 */
export function getFriendlyDateTime(date) {
    if (!date) return ''

    const d = date instanceof Date ? date : new Date(date)

    if (isNaN(d.getTime())) {
        return ''
    }

    const now = new Date()
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const targetDate = new Date(d.getFullYear(), d.getMonth(), d.getDate())
    const diffDays = Math.floor((today - targetDate) / (1000 * 60 * 60 * 24))

    if (diffDays === 0) {
        // 今天，显示时间
        return formatTime(d, 'HH:mm')
    } else if (diffDays === 1) {
        // 昨天
        return `昨天 ${formatTime(d, 'HH:mm')}`
    } else if (diffDays < 7) {
        // 一周内，显示星期几
        const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
        const weekday = weekdays[d.getDay()]
        return `${weekday} ${formatTime(d, 'HH:mm')}`
    } else if (d.getFullYear() === now.getFullYear()) {
        // 今年内，显示月日
        return formatDate(d, 'MM-DD HH:mm')
    } else {
        // 跨年，显示年月日
        return formatDateTime(d, 'YYYY-MM-DD HH:mm')
    }
}

/**
 * 检查是否为有效的日期
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @returns {boolean} 是否为有效日期
 */
export function isValidDate(date) {
    if (!date) return false

    const d = date instanceof Date ? date : new Date(date)
    return !isNaN(d.getTime())
}

/**
 * 获取日期所在周的周一
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @returns {Date} 周一的Date对象
 */
export function getWeekStart(date) {
    const d = date instanceof Date ? date : new Date(date)
    const day = d.getDay()
    const diff = day === 0 ? -6 : 1 - day // 周日的特殊情况处理
    const monday = new Date(d)
    monday.setDate(d.getDate() + diff)
    monday.setHours(0, 0, 0, 0)
    return monday
}

/**
 * 获取日期所在月的第一天
 * @param {string|Date} date - 日期时间字符串或Date对象
 * @returns {Date} 月第一天的Date对象
 */
export function getMonthStart(date) {
    const d = date instanceof Date ? date : new Date(date)
    const firstDay = new Date(d.getFullYear(), d.getMonth(), 1)
    firstDay.setHours(0, 0, 0, 0)
    return firstDay
}

/**
 * 获取两个日期之间的天数差
 * @param {string|Date} date1 - 第一个日期
 * @param {string|Date} date2 - 第二个日期
 * @returns {number} 天数差（取绝对值）
 */
export function getDaysBetween(date1, date2) {
    const d1 = date1 instanceof Date ? date1 : new Date(date1)
    const d2 = date2 instanceof Date ? date2 : new Date(date2)

    if (isNaN(d1.getTime()) || isNaN(d2.getTime())) {
        return 0
    }

    const diffMs = Math.abs(d1 - d2)
    return Math.floor(diffMs / (1000 * 60 * 60 * 24))
}

/**
 * 在日期上添加天数
 * @param {string|Date} date - 基准日期
 * @param {number} days - 要添加的天数（可以为负数）
 * @returns {Date} 新的Date对象
 */
export function addDays(date, days) {
    const d = date instanceof Date ? date : new Date(date)
    const newDate = new Date(d)
    newDate.setDate(d.getDate() + days)
    return newDate
}

/**
 * 在日期上添加月份
 * @param {string|Date} date - 基准日期
 * @param {number} months - 要添加的月份数（可以为负数）
 * @returns {Date} 新的Date对象
 */
export function addMonths(date, months) {
    const d = date instanceof Date ? date : new Date(date)
    const newDate = new Date(d)
    newDate.setMonth(d.getMonth() + months)
    return newDate
}

/**
 * 获取日期的开始时间（00:00:00）
 * @param {string|Date} date - 日期
 * @returns {Date} 开始时间的Date对象
 */
export function getStartOfDay(date) {
    const d = date instanceof Date ? date : new Date(date)
    const start = new Date(d)
    start.setHours(0, 0, 0, 0)
    return start
}

/**
 * 获取日期的结束时间（23:59:59）
 * @param {string|Date} date - 日期
 * @returns {Date} 结束时间的Date对象
 */
export function getEndOfDay(date) {
    const d = date instanceof Date ? date : new Date(date)
    const end = new Date(d)
    end.setHours(23, 59, 59, 999)
    return end
}

/**
 * 格式化持续时间为可读字符串
 * @param {number} seconds - 秒数
 * @returns {string} 格式化的持续时间
 */
export function formatDuration(seconds) {
    if (seconds < 60) {
        return `${seconds}秒`
    } else if (seconds < 3600) {
        const minutes = Math.floor(seconds / 60)
        const remainingSeconds = seconds % 60
        return remainingSeconds > 0 ? `${minutes}分${remainingSeconds}秒` : `${minutes}分钟`
    } else {
        const hours = Math.floor(seconds / 3600)
        const remainingMinutes = Math.floor((seconds % 3600) / 60)
        if (remainingMinutes === 0) {
            return `${hours}小时`
        }
        return `${hours}小时${remainingMinutes}分钟`
    }
}

// 为日志页面特别优化的格式化函数
/**
 * 为日志列表格式化日期时间
 * 如果日志是今天的，只显示时间；如果是昨天的，显示"昨天 + 时间"；否则显示完整日期时间
 * @param {string|Date} date - 日志日期时间
 * @returns {string} 优化后的日期时间字符串
 */
export function formatLogDateTime(date) {
    if (!date) return ''

    const d = date instanceof Date ? date : new Date(date)

    if (isNaN(d.getTime())) {
        return ''
    }

    const now = new Date()
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const logDate = new Date(d.getFullYear(), d.getMonth(), d.getDate())

    const diffDays = Math.floor((today - logDate) / (1000 * 60 * 60 * 24))

    if (diffDays === 0) {
        // 今天，只显示时间
        return formatTime(d, 'HH:mm:ss')
    } else if (diffDays === 1) {
        // 昨天
        return `昨天 ${formatTime(d, 'HH:mm:ss')}`
    } else if (d.getFullYear() === now.getFullYear()) {
        // 今年，显示月日和时间
        return formatDateTime(d, 'MM-DD HH:mm:ss')
    } else {
        // 跨年，显示完整日期时间
        return formatDateTime(d, 'YYYY-MM-DD HH:mm:ss')
    }
}

export default {
    formatDateTime,
    formatDate,
    formatTime,
    getRelativeTime,
    getFriendlyDateTime,
    isValidDate,
    getWeekStart,
    getMonthStart,
    getDaysBetween,
    addDays,
    addMonths,
    getStartOfDay,
    getEndOfDay,
    formatDuration,
    formatLogDateTime
}