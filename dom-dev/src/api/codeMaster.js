import request from './request'

/**
 * カテゴリ別コード一覧取得
 * @param {string} category - カテゴリコード (例: 'PREFECTURE')
 * @returns {Promise<{code:string, name:string, sortOrder:number}[]>}
 */
export const getCodeList = (category) => request.get('/code-master', { params: { category } })
