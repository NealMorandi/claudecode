export const downloadBlob = (blob, filename) => {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

export const exportCsv = (headers, rows, filename = 'export.csv') => {
  const BOM = '﻿'
  const content = [headers.join(','), ...rows.map((r) => r.join(','))].join('\n')
  const blob = new Blob([BOM + content], { type: 'text/csv;charset=utf-8' })
  downloadBlob(blob, filename)
}
