import dayjs from 'dayjs'

export const formatDate = (date) => date ? dayjs(date).format('YYYY-MM-DD') : '-'
export const formatDateTime = (date) => date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
export const formatYearMonth = (date) => date ? dayjs(date).format('YYYY年MM月') : '-'

export const calcYears = (startDate) => {
  if (!startDate) return 0
  return dayjs().diff(dayjs(startDate), 'year', true).toFixed(1)
}

export const daysInMonth = (year, month) => dayjs(`${year}-${month}`).daysInMonth()
