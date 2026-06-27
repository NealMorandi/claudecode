export const required = (message) => ({ required: true, message, trigger: 'blur' })

export const dateRule = (message = '日付を選択してください') => ({
  required: true, message, trigger: 'change',
})

export const maxLen = (len) => ({
  max: len, message: `${len}文字以内で入力してください`, trigger: 'blur',
})
