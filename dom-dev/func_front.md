# func_front.md — 前端功能清单

**项目名称：** 社員寮管理システム (Employee Dormitory Management System)  
**技术栈：** Vue3 + Vite + Axios + Element-Plus + Pinia + Dayjs  
**项目位置：** `C:\ClassDemoClaudeCode\dom-dev`  
**文档生成日期：** 2026-06-09  

---

## 1. Views（页面视图）

### 1.1 核心页面

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/login` | `views/login/index.vue` | 用户认证登录页 | `useAuthStore().login(credentials)` |
| `/dashboard` | `views/dashboard/index.vue` | 仪表板：统计卡片（当月寮費に未計算/計算済みバッジ P1-A）、寮別稼働状況（サマリーカード・入居者数統一 P2-A・スクロールインジケーター P2-B・稼働率凡例 P3-B）、長期利用警告（寮名/部屋・超過日数・対応モーダル P1-B/P3-A）、クイックアクション（長期利用対応ボタン P3-C） | `getSummary()`, `getDormitoryOccupancy()`, `getLongTermAlertsTop(limit)`, `updateAlertStatus()`, `occupancySummary`（computed）, `rateColor(rate)`, `openActionDialog(item)`, `submitAction()` |

### 1.2 寮管理页面

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/dormitories` | `views/master/dormitory/index.vue` | 寮一覧：CRUD、搜索、分页。テーブルに「状態」「都道府県」「部屋別寮費」列。検索条件に「状態」（有効/退却/すべて）追加。削除操作は物理削除ではなく status='inactive'（退却）に変更、退却後も検索可能。退却済み行の「退却」ボタンは disabled。 | `getDormitoryList()`, `createDormitory()`, `updateDormitory()`, `deleteDormitory()` |
| `/dormitories/:id` | `views/master/dormitory/detail.vue` | 寮詳細：基本情報パネル＋部屋×在居者テーブル（部屋名・定員・在居人数・社員名・区分・部門・入居日・退寮日；同一部屋複数行はセル結合）＋備品一覧テーブル（部屋名・備品名・メーカー・種別・状態・備考） | `getDormitoryDetail(id)`, `getRoomResidents(id)`, `getDormitoryEquipment(id)` |
| `/dormitories/:id/rooms` | `views/master/room/index.vue` | 部屋一覧：按寮分组查看房间；**部屋編集**に寮費(円)項目追加（マイナス入力可、null=共通設定使用） | `getRoomsByDormitory(dormitoryId)`, `createRoom()`, `updateRoom()`, `deleteRoom()` |

### 1.3 社員寮一覧ページ

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/dorm-overview` | `views/dormitory/index.vue` | 社員寮一覧：寮名・都道府県・住所・種別・総部屋数・入居人数・入居可能人数・ステータス（入居可/満室/定員超）・詳細リンク。検索（寮名・都道府県・種別）・ページネーション付き。`社員寮管理` グループ内で `入居履歴一覧` の上に配置。 | `fetchList()`, `occStatus(row)`, `typeLabel(type)` |

### 1.4 入居管理页面

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/residences` | `views/residence/index.vue` | 入居履歴一覧：搜索（URLクエリ `?keyword=` 対応）、分页、退寮入口 | `getResidenceList(params)`, `useRoute()` でマウント時に `route.query.keyword` を検索欄にセット |
| `/residences/new` | `views/residence/new.vue` | 新規入居登録：社員搜索→房间选择→预览→提交；「新規」ボタンで社員をその場で新規登録可能（el-dialog内フォーム） | `searchEmployees()`, `createEmployee()`, `getAvailableRooms()`, `createResidence()` |
| `/residences/:id` | `views/residence/detail.vue` | 入居詳細：入居+社員+房间信息 | `getResidenceDetail(id)` |
| `/residences/:id/checkout` | `views/residence/checkout.vue` | 退寮処理：退寮日（入居日以降のみ選択可）・理由・備品処理（保管選択時は場所入力必須）；マウント時に `status !== 'active'` なら詳細ページへリダイレクト；備品テーブルはデータ取得中ローディング表示 | `checkoutResidence(id, data)`, `getResidenceItems(id)`, `disabledCheckoutDate(time)`, `useRoute()`, `useRouter()` |
| `/residences/:id/items` | `views/residence/items.vue` | 備品準備チェックリスト | `getResidenceItems(id)`, `updateResidenceItems(id, data)` |

### 1.4 空室・警告页面

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/vacancies` | `views/vacancy/index.vue` | 空き室一覧：卡片/表格双视图，按性別过滤 | `getVacancies(params)` |
| `/long-term-alerts` | `views/longTermAlert/index.vue` | 長期利用警告一覧：ステータス・最低在居年数フィルタ、ページネーション、対応状況更新ダイアログ、Excelエクスポート | `getLongTermAlerts(params)`, `updateAlertStatus(employeeId, data)`, `exportAlertCsv(params)` |

### 1.5 寮費管理页面

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/rent/calculate` | `views/rent/calculate.vue` | 寮費計算：三步骤（选月份→确认→完了） | `calculateRent()`, `confirmRent()`, `exportRentCsv()` |
| `/rent/history` | `views/rent/history.vue` | 寮費履歴：历史确定记录查询 | `getRentHistory(params)` |

### 1.6 备品管理页面

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/items` | `views/master/equipment/index.vue` | 備品マスタ：库存管理、分类筛选 | `getEquipmentList()`, `createEquipment()`, `updateEquipment()`, `deleteEquipment()` |
| `/items/storage` | `views/equipment/storage.vue` | 備品保管：備品保管一覧（検索・ページネーション）。テーブル列：No. / 備品名 / 種別（日本語表示）/ 保管場所 / 部屋 / 個数 / 関連社員 / 状態 / 操作。データソースは `equipment_history`（`action='storage'`）。操作列の「編集」クリックで **備品明細管理** ダイアログ（行データで事前入力・更新送信）が開く。「新規備品登録」ダイアログは新規作成。両ダイアログは共通フォームで `dialogMode`（`'create'`/`'edit'`）で制御。 | `getStorageList()`, `registerEquipmentStorage(id, data)`, `updateEquipmentHistory(id, data)`, `openRegister()`, `openEdit(row)`, `handleSave()`, `getEquipmentList()`, `categoryLabel(val)` |

### 1.7 系统管理页面

| 路由路径 | 文件位置 | 功能描述 | 关键函数 |
|---|---|---|---|
| `/admin/users` | `views/admin/users/index.vue` | ユーザー管理：CRUD・ロール/状態フィルター・サマリーカード（総数/有効/管理者/無効）。編集時は `username` フィールドを `:disabled="!!editRow"` で無効化（backend は username 更新不可のため）。`handleSave` / `handleDelete` にエラーハンドリング追加済み | `getUserList()`, `createUser()`, `updateUser()`, `deleteUser()` |
| `/admin/settings` | `views/admin/settings/index.vue` | 基準値マスタ：系统参数（寮费单价等） | `getSettings()`, `updateSettings()` |
| `/admin/logs` | `views/admin/logs/index.vue` | 操作ログ：审计日志、导出 | `getLogList()`, `exportLogs()` |
| `/import` | `views/import/index.vue` | データ取り込み：文件上传→验证→执行→报告 | `uploadFile()`, `validateImport()`, `executeImport()`, `getImportReport()` |

### 1.8 UI 共通規約

| 項目 | 規約 | 対象ファイル |
|---|---|---|
| ページネーション件数表示 | `総{{ total }}件`（旧: `共 X 件 / 件款`） | 全一覧ページ（11ファイル） |

---

## 2. Components（公共组件）

| 组件名 | 文件位置 | Props | Emits | 功能描述 |
|---|---|---|---|---|
| `AppSelect` | `components/AppSelect.vue` | `modelValue`, `options`, `placeholder`, `clearable`, `disabled`, `size`, `filterable`, `remote`, `remoteMethod`, `loading` | `update:modelValue`, `change` | 自定义下拉选择（支持远程搜索、本地过滤） |
| `ArcSpinner` | `components/ArcSpinner.vue` | `size`, `strokeWidth` | — | 圆弧加载动画 |
| `NavIcon` | `components/NavIcon.vue` | `icon` | — | 导航图标 |
| `TableLoadState` | `components/TableLoadState.vue` | — | — | 表格加载状态占位符 |
| `SystemAnnouncement` | `components/SystemAnnouncement.vue` | — | — | システム全体告知：`Teleport to="body"` で DOM 最上位に配置。スクリム（`.sys-scrim`）とカード（`.sys-announcement`）を**独立した `position:fixed` 要素**として並置し、サイドメニュー等の親要素の stacking context に依存しない。スクリム: `inset:0; z-index:9998`。カード: `top:50%; left:50%; transform:translate(-50%,-50%); z-index:9999`。スクリムクリック or × ボタンで `dismissAnnouncement()` を呼び出して非表示。 |
| `Breadcrumb` | `layout/components/Breadcrumb.vue` | — | — | 面包屑导航 |
| `Header` | `layout/components/Header.vue` | — | — | 顶部栏（用户菜单、退出） |
| `Sidebar` | `layout/components/Sidebar.vue` | — | — | 侧边菜单栏（可折叠） |

---

## 3. Api（接口模块）

### 3.1 请求基础配置（`api/request.js`）
- Base URL：`VITE_API_BASE_URL` 环境变量，默认 `/api`
- 超时：15000ms
- 认证：`Authorization: Bearer <token>`
- 响应格式：`{ code, data, message }`；401 自动跳转登录

### 3.2 各模块接口

#### `api/auth.js`
```
login(credentials)     → POST /auth/login
logout()               → POST /auth/logout
getProfile()           → GET  /auth/profile
```

#### `api/dashboard.js`
```
getSummary()                  → GET /dashboard/summary
getDormitoryOccupancy()       → GET /dashboard/dormitory-occupancy
                                     レスポンス: { id, name, totalRooms, occupiedRooms,
                                                  availableRooms, residentCount,
                                                  availableCapacity, occupancyRate }[]
getLongTermAlertsTop(limit)   → GET /long-term-alerts?limit=N
```

#### `api/codeMaster.js`
```
getCodeList(category)    → GET /code-master?category=PREFECTURE など
```

#### `api/dormitory.js`
```
getDormitoryList(params)      → GET    /dormitories
                                     レスポンス(DormitoryVO): { id, name, type, status, prefecture, address,
                                       totalRooms, availableRooms, residentCount, availableCapacity, totalCapacity, roomRents }
getDormitoryDetail(id)        → GET    /dormitories/:id
createDormitory(data)         → POST   /dormitories
updateDormitory(id, data)     → PUT    /dormitories/:id
deleteDormitory(id)           → DELETE /dormitories/:id
getRoomResidents(id)          → GET    /dormitories/:id/room-residents
                                     レスポンス(RoomResidentVO[]): [{ roomId, roomName, capacity, residentCount,
                                       residenceId, employeeName, employeeType, department, checkinDate, checkoutDate }]
getDormitoryEquipment(id)     → GET    /dormitories/:id/equipment
                                     レスポンス(EquipmentVO[]): [{ id, roomId, roomName, name, maker,
                                       category, status, quantity, remark }]
```

#### `api/room.js`
```
getRoomsByDormitory(dormitoryId, params)   → GET    /dormitories/:id/rooms
getRoomDetail(id)                          → GET    /rooms/:id
createRoom(dormitoryId, data)              → POST   /dormitories/:id/rooms
updateRoom(id, data)                       → PUT    /rooms/:id
deleteRoom(id)                             → DELETE /rooms/:id
getAvailableRooms(params)                  → GET    /rooms/available
```

#### `api/residence.js`
```
getResidenceList(params)      → GET    /residences
getResidenceDetail(id)        → GET    /residences/:id
createResidence(data)         → POST   /residences
updateResidence(id, data)     → PUT    /residences/:id
checkoutResidence(id, data)   → PUT    /residences/:id/checkout
```

#### `api/vacancy.js`
```
getVacancies(params)          → GET /vacancies
```

#### `api/employee.js`
```
searchEmployees(keyword)      → GET /employees/search?q=keyword
getEmployeeList(params)       → GET /employees
```

#### `api/rent.js`
```
calculateRent(data)           → POST /rent/calculate
confirmRent(data)             → POST /rent/confirm
getRentHistory(params)        → GET  /rent/history
getRentDetail(id)             → GET  /rent/:id
exportRentCsv(params)         → GET  /rent/export  (Blob)
```

#### `api/longTermAlert.js`
```
getLongTermAlerts(params)              → GET  /long-term-alerts
updateAlertStatus(employeeId, data)    → PUT  /long-term-alerts/:employeeId/status
exportAlertCsv(params)                 → GET  /long-term-alerts/export  (Blob)
```

#### `api/equipment.js`
```
getEquipmentList(params)               → GET    /equipment
createEquipment(data)                  → POST   /equipment
updateEquipment(id, data)              → PUT    /equipment/:id
deleteEquipment(id)                    → DELETE /equipment/:id
getStorageList(params)                 → GET    /equipment/storage
getEquipmentHistory(id, params)        → GET    /equipment/:id/history
registerEquipmentStorage(id, data)     → POST   /equipment/:id/storage
getResidenceItems(residenceId)         → GET    /residences/:id/items
updateResidenceItems(residenceId, data)→ PUT    /residences/:id/items
updateHistoryStorageLocation(id, sl)   → PUT    /equipment/history/:id/storage-location
updateEquipmentHistory(id, data)       → PUT    /equipment/history/:id
```

#### `api/user.js`
```
getUserList(params)           → GET    /users
createUser(data)              → POST   /users
updateUser(id, data)          → PUT    /users/:id
deleteUser(id)                → DELETE /users/:id
resetPassword(id, data)       → POST   /users/:id/reset-password
```

#### `api/settings.js`
```
getSettings()                 → GET /settings
updateSettings(data)          → PUT /settings
```

#### `api/operationLog.js`
```
getLogList(params)            → GET /operation-logs
exportLogs(params)            → GET /operation-logs/export  (Blob)
```

#### `api/importData.js`
```
uploadFile(formData)          → POST /import/upload
validateImport(data)          → POST /import/validate
executeImport(data)           → POST /import/execute
getImportReport(jobId)        → GET  /import/report/:jobId
```

---

## 4. Store（状态管理）

### `store/modules/auth.js`

| 类型 | 名称 | 说明 |
|---|---|---|
| State | `token` | JWT token（持久化到 localStorage `dom_token`） |
| State | `userInfo` | 用户信息对象（持久化到 localStorage `dom_user`） |
| Getter | `isLoggedIn` | `!!token` |
| Getter | `role` | `userInfo?.role`（admin / staff / viewer） |
| Action | `login(credentials)` | 调用 API → 存储 token + userInfo |
| Action | `logout()` | 调用 API → 清除 state + localStorage |

### `store/modules/app.js`

| 类型 | 名称 | 说明 |
|---|---|---|
| State | `sidebarCollapsed` | 侧边栏折叠状態 |
| State | `announcement.text` | 告知メッセージ本文（空文字 = 非表示） |
| State | `announcement.visible` | 告知表示フラグ |
| Action | `toggleSidebar()` | 切換折叠 |
| Action | `showAnnouncement(text)` | `text` をセットして `visible = true`。任意のページから呼び出し可能 |
| Action | `dismissAnnouncement()` | `visible = false`。SystemAnnouncement の × ボタンから呼び出し |

---

## 5. Utils（工具函数）

### `utils/auth.js`
```javascript
getToken()       // localStorage.getItem('dom_token')
setToken(token)  // localStorage.setItem('dom_token', token)
removeToken()    // localStorage.removeItem('dom_token')
getUser()        // JSON.parse(localStorage.getItem('dom_user'))
setUser(user)    // localStorage.setItem('dom_user', JSON.stringify(user))
removeUser()     // localStorage.removeItem('dom_user')
```

### `utils/date.js`（依赖 Dayjs）
```javascript
formatDate(date)          // → 'YYYY-MM-DD'
formatDateTime(date)      // → 'YYYY-MM-DD HH:mm:ss'
formatYearMonth(date)     // → 'YYYY年MM月'
calcYears(startDate)      // → 从 startDate 到今天的年数
daysInMonth(year, month)  // → 指定月份的天数
```

### `utils/export.js`
```javascript
downloadBlob(blob, filename)         // 触发浏览器下载 Blob 文件
exportCsv(headers, rows, filename)   // 生成 CSV（含 UTF-8 BOM）并下载
```

### `utils/permission.js`
```javascript
hasRole(...roles)  // 检查当前用户是否拥有指定角色
isAdmin()          // 快捷：hasRole('admin')
```

### `utils/validate.js`（Element-Plus 表单规则）
```javascript
required(message)    // 必填规则
dateRule(message)    // 日期选择规则
maxLen(len)          // 最大长度规则
```

---

## 6. Router（路由配置）

### 路由模块

| 文件 | 覆盖路由 |
|---|---|
| `router/index.js` | `/login`、`/`（主布局）、`/dashboard` |
| `router/modules/master.js` | `/dormitories`、`/rooms`、`/items` |
| `router/modules/residence.js` | `/residences` 及其子路由 |
| `router/modules/vacancy.js` | `/vacancies`、`/long-term-alerts` |
| `router/modules/rent.js` | `/rent/calculate`、`/rent/history` |
| `router/modules/admin.js` | `/admin/users`、`/admin/settings`、`/admin/logs`、`/import` |

### 路由守卫（`router/guard.js`）

- 白名单：`['/login']`
- 未登录 → 重定向 `/login?redirect=原路径`
- `meta.roles` 不匹配 → 重定向 `/dashboard`
- 角色枚举：`admin` / `staff` / `viewer`

---

## 7. CSS / スタイル設計

### グローバルスタイルファイル

| ファイル | 役割 |
|---|---|
| `assets/styles/variables.css` | CSS カスタムプロパティ定義（カラー・レイアウト・z-index 階層） |
| `assets/styles/index.css` | Element Plus グローバルテーマオーバーライド（ダークネイビー） |

### z-index 階層（`variables.css` で管理）

| 変数 | 値 | 対象要素 |
|---|---|---|
| `--z-bg` | 0 | orb / grid-overlay（背景装飾） |
| `--z-layout` | 1 | `.layout-wrapper` |
| `--z-header` | 10 | `.header-wrapper`（`backdrop-filter` が stacking context を生成するため明示） |
| `--z-dropdown` | 100 | select / popper パネル |
| `--z-modal` | 1000 | `.el-overlay`（dialog 共通オーバーレイ） |
| `--z-toast` | 9999 | `.el-message` トースト / `.el-overlay-message-box`（確認ダイアログ） / `.sys-announcement`（カード） |

> **ルール：** 新規 UI 要素の `z-index` は必ず上記変数を使用すること。直接数値を書かない。

### `index.css` での前面固定設定

| セレクタ | z-index | 備考 |
|---|---|---|
| `.el-message` | `var(--z-toast)` = 9999 | トースト通知。`position: fixed` 付与済み。ダークテーマスタイル統一 |
| `.el-overlay` | `var(--z-modal)` = 1000 | dialog 共通オーバーレイ。`position: fixed` 付与済み |
| `.el-overlay-message-box` | `var(--z-toast)` = 9999 | 確認ダイアログ。ヘッダー等すべての UI より前面に固定 |

### SystemAnnouncement の z-index 構造

| 要素 | position | z-index | 役割 |
|---|---|---|---|
| `.sys-scrim` | `fixed; inset:0` | `calc(var(--z-toast) - 1)` = 9998 | 背景暗転・クリックで閉じる |
| `.sys-announcement` | `fixed; top:50%; left:50%; transform:translate(-50%,-50%)` | `var(--z-toast)` = 9999 | メッセージカード本体 |

> スクリムとカードは**独立した `position:fixed` 要素**として `Teleport to="body"` で配置。`position:absolute` + 親要素基準の配置（左下へのずれ）を防ぐため、ビューポートを基準に直接配置する。

### SystemAnnouncement レスポンシブ対応

| ブレイクポイント | 対応内容 |
|---|---|
| 全幅（デフォルト） | `max-width: min(520px, calc(100vw - 32px))` — 画面幅が 552px 未満でも左右 16px マージンを確保 |
| `≤ 400px` | `min-width: 0; padding: 24px 24px 20px` — 狭い端末でカードが画面幅を超えないよう縮小 |

### SystemAnnouncement 配置堅牢性の検証

| 仮説 | 結果 | 根拠 |
|---|---|---|
| 高さの計算ミス | ✅ 非該当 | `transform:translate(-50%,-50%)` はブラウザが要素サイズを自動参照。JS による高さ計算なし |
| レスポンシブの不備 | ✅ 対応済み | `position:fixed` でサイドバー幅変化の影響を受けない。メディアクエリで狭画面対応 |
| 非同期タイミングのずれ | ✅ 非該当 | JS 位置計算ゼロ（`style.top`・`offsetTop`・`getBoundingClientRect` 不使用）。Vue リアクティビティのみで DOM 管理 |

---

## 8. 典型业务调用链路

### 仪表板初始化
```
views/dashboard/index.vue  onMounted()
    ↓
api/dashboard.js  getSummary() / getDormitoryOccupancy() / getLongTermAlertsTop()
    ↓
后端 DashboardController
    ↓（occupancyList 取得後）
computed: occupancySummary  ← 総部屋数・入居中・空き室・全体稼働率を集計
rateColor(rate)             ← 稼働率に応じた色（緑/橙/赤）を返す
```

### 寮別稼働状況 → 入居履歴への遷移
```
views/dashboard/index.vue  「入居状況」ボタン click
    ↓  $router.push({ path: '/residences', query: { keyword: row.name } })
views/residence/index.vue  onMounted()
    ↓  useRoute() → route.query.keyword を query.keyword にセット
    ↓  fetchList()
api/residence.js  getResidenceList({ keyword: '寮名', ... })
    ↓
后端 ResidenceController.list()
```

### 新規入居登録
```
views/residence/new.vue  handleSubmit()
    ↓
api/employee.js  searchEmployees()           ← 社員搜索
api/room.js      getAvailableRooms(params)   ← 可用房间
    ↓
api/residence.js  createResidence(data)
    ↓
后端 ResidenceController.create()
```

### 新規社員登録（新規入居登録画面内ダイアログ）
```
views/residence/new.vue  openNewEmpDialog()  ← 「新規」ボタン押下
    ↓  el-dialog 表示（フォーム: 社員番号/氏名/性別/社員区分/部署）
    ↓  submitNewEmployee()
    ↓  api/employee.js  createEmployee(data)
    ↓  后端 EmployeeController.create()
    ↓  登録完了後：employeeOptions に追加 → form.employeeId に自動セット
```

### 退寮処理
```
views/residence/checkout.vue  onMounted()
    ↓  Promise.all([getResidenceDetail(id), getResidenceItems(id)])
    ↓  res.status !== 'active' → ElMessage.warning → router.replace('/residences/:id')  ← ガード
    ↓  residence / items をセット（既存 disposition / storageLocation を引き継ぎ）
    ↓
views/residence/checkout.vue  handleSubmit()
    ↓  formRef.validate()
    ↓  ElMessageBox.confirm（キャンセル時は return）
    ↓  checkoutResidence(id, { checkoutDate, checkoutReason, items })
    ↓  成功 → ElMessage.success → router.push('/residences/:id')
    ↓  失敗 → ElMessage.error('退寮処理に失敗しました')
    ↓
api/residence.js  checkoutResidence(id, data)  → PUT /residences/:id/checkout
    ↓
后端 ResidenceController.checkout()
    ↓  checkoutDate / checkoutReason の null バリデーション → PARAM_INVALID
    ↓  LocalDate.parse() 失敗 → DateTimeParseException → PARAM_INVALID
    ↓  residence.status != 'active' → RESIDENCE_NOT_ACTIVE
    ↓  checkoutDate < checkinDate → CHECKOUT_DATE_INVALID（"退寮日は入居日以降…"）
    ↓  residence.status = 'checked_out' に更新
    ↓  残入居者数で rooms.status を occupied / available に更新
    ↓  items の所有権チェック（item.residenceId == id のみ更新）
    ↓  disposition が 'storage' 以外は storageLocation を null にクリア
```

### 寮費計算（三步骤）
```
views/rent/calculate.vue  step1: calculateRent({ yearMonth })
    ↓
api/rent.js  calculateRent(data)
    ↓
views/rent/calculate.vue  step2: confirmRent()
    ↓
api/rent.js  confirmRent(data)
    ↓
views/rent/calculate.vue  step3: exportRentCsv()
    ↓
api/rent.js  exportRentCsv(params)  → utils/export.js  downloadBlob()
    ↓
后端 RentController
```

### 長期利用警告状態更新
```
views/residence/long-term-alert/index.vue  handleStatusChange()
    ↓
api/longTermAlert.js  updateAlertStatus(employeeId, data)
    ↓
后端 LongTermAlertController.updateStatus()
```

### ユーザー管理 CRUD
```
【一覧取得】
views/admin/users/index.vue  onMounted() / fetchList()
    ↓  api/user.js  getUserList({ keyword, role, active })  → GET /admin/users
    ↓  后端 UserController.list()
    ↓  UserMapper.selectList() ← keyword/role/active で絞り込み

【新規登録】
views/admin/users/index.vue  handleSave()  ← editRow が null の場合
    ↓  api/user.js  createUser(form)  → POST /admin/users
    ↓  后端 UserController.create()
    ↓  UserServiceImpl: username 重複チェック → passwordEncoder.encode() → insert
    ↓  成功: ElMessage.success / 失敗: ElMessage.error（重複等）

【編集】
views/admin/users/index.vue  openDialog(row)  ← username は :disabled="!!editRow"
    ↓  handleSave()  ← editRow がある場合
    ↓  api/user.js  updateUser(id, form)  → PUT /admin/users/:id
    ↓  后端 UserController.update()
    ↓  UserServiceImpl: name / role / active のみ更新（username は変更不可）

【削除】
views/admin/users/index.vue  handleDelete(row)
    ↓  ElMessageBox.confirm（キャンセル時は return）
    ↓  api/user.js  deleteUser(id)  → DELETE /admin/users/:id
    ↓  后端 UserController.delete()
    ↓  成功: ElMessage.success / 失敗: ElMessage.error
```

---

## 9. 文件统计

| 类型 | 文件数 |
|---|---|
| Views（.vue） | 17 |
| Components（.vue） | 8 |
| Api（.js） | 14 |
| Store（.js） | 2 |
| Utils（.js） | 5 |
| Router（.js） | 6 |
| **合计** | **52** |
