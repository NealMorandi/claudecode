## 前端项目目录结构（路径清单）

dom-dev/
├── index.html
├── vite.config.js
├── package.json
├── .env
├── .env.development
├── .env.production
├── .gitignore
└── src/
    ├── main.js
    ├── App.vue
    │
    ├── api/                                    # M01-M07 全模块接口封装
    │   ├── request.js                          # Axios 实例、请求/响应拦截器、统一错误处理
    │   ├── auth.js                             # M07: 登录、登出、刷新Token
    │   ├── dashboard.js                        # M07: 仪表盘汇总、寮别稼働率
    │   ├── dormitory.js                        # M01-01: 寮マスタ CRUD
    │   ├── room.js                             # M01-02: 部屋マスタ CRUD
    │   ├── employee.js                         # M01-03: 社員マスタ参照、社員検索
    │   ├── equipment.js                        # M01-04 / M04: 备品マスタ、準備管理、保管管理
    │   ├── residence.js                        # M02: 入居登録、退寮処理、入居履歴照会
    │   ├── vacancy.js                          # M05: 空き室一覧、入居可能部屋検索
    │   ├── rent.js                             # M03: 月次寮費計算、確定、CSV出力
    │   ├── longTermAlert.js                    # M02-04: 長期利用警告一覧、対応ステータス更新
    │   ├── importData.js                       # M06: ファイルアップロード、バリデーション、取り込み実行
    │   ├── user.js                             # M07: ユーザー管理 CRUD、ロール割当
    │   ├── role.js                             # M07: ロール管理、権限設定
    │   ├── operationLog.js                     # M07: 操作ログ検索、CSV出力
    │   └── settings.js                         # M07: システム設定（長期利用年数、寮費単価）
    │
    ├── assets/
    │   ├── images/
    │   │   └── logo.svg
    │   └── styles/
    │       ├── index.css                       # グローバルスタイルエントリ
    │       ├── variables.css                   # CSS変数（カラー、フォント、スペーシング）
    │       └── reset.css                       # ブラウザデフォルトスタイルリセット
    │
    ├── components/
    │   ├── common/
    │   │   ├── PageHeader.vue                  # ページタイトル + パンくずリスト
    │   │   ├── SearchForm.vue                  # 汎用検索フォーム（条件フィルター）
    │   │   ├── DataTable.vue                   # 汎用テーブル（序号、多選、ソート、行操作）
    │   │   ├── Pagination.vue                  # 汎用ページネーション
    │   │   ├── ConfirmDialog.vue               # 削除・確定操作の確認ダイアログ
    │   │   ├── StatusTag.vue                   # 各種ステータスバッジ（空き/入居中/警告等）
    │   │   ├── ExportButton.vue                # CSV/Excelエクスポートボタン
    │   │   └── DormitoryTypeTag.vue            # 男性寮/女性寮バッジ
    │   └── charts/
    │       ├── OccupancyBarChart.vue           # 寮別稼働率棒グラフ（Echarts）
    │       ├── RentTrendLineChart.vue          # 月次寮費推移折れ線グラフ（Echarts）
    │       └── VacancyPieChart.vue             # 空き室割合円グラフ（Echarts）
    │
    ├── layout/
    │   ├── index.vue                           # メインレイアウト（Sidebar + Header + Main）
    │   └── components/
    │       ├── Sidebar.vue                     # 左サイドメニュー（RBAC対応）
    │       ├── SidebarItem.vue                 # 再帰メニューアイテム
    │       ├── Header.vue                      # 上部ナビバー（ユーザー情報・ログアウト）
    │       ├── Breadcrumb.vue                  # パンくずリスト
    │       └── TabsNav.vue                     # タブ型ページナビゲーション
    │
    ├── router/
    │   ├── index.js                            # ルーターインスタンス、ルート統合
    │   ├── guard.js                            # ルートガード（認証チェック・権限判定）
    │   └── modules/
    │       ├── master.js                       # M01: 寮/部屋/社員/備品マスタルート
    │       ├── residence.js                    # M02: 入退寮管理ルート
    │       ├── vacancy.js                      # M05: 空き室管理ルート
    │       ├── rent.js                         # M03: 寮費管理ルート
    │       ├── equipment.js                    # M04: 備品保管管理ルート
    │       ├── longTermAlert.js                # M02-04: 長期利用警告ルート
    │       ├── importData.js                   # M06: Excelデータ取り込みルート
    │       └── admin.js                        # M07: ユーザー/ロール/ログ/設定ルート
    │
    ├── store/
    │   ├── index.js                            # Piniaストアエントリ
    │   └── modules/
    │       ├── auth.js                         # ログイン状態、Token、ユーザー情報
    │       ├── permission.js                   # ロール・権限・動的メニュー
    │       └── app.js                          # サイドバー開閉、テーマ、グローバルローディング
    │
    ├── utils/
    │   ├── auth.js                             # Token読み書き（localStorage）
    │   ├── date.js                             # 日付フォーマット、日割り計算補助
    │   ├── export.js                           # CSV/Excelダウンロード共通処理
    │   ├── validate.js                         # 共通フォームバリデーションルール
    │   └── permission.js                       # ボタン権限判定ヘルパー
    │
    └── views/
        ├── login/
        │   └── index.vue                       # [画面01] /login ログイン画面
        │
        ├── dashboard/
        │   └── index.vue                       # [画面02] /dashboard ダッシュボード
        │
        ├── master/                             # M01 マスタ管理
        │   ├── dormitory/
        │   │   ├── index.vue                   # [画面03] /dormitories 寮マスタ一覧
        │   │   └── detail.vue                  # [画面04] /dormitories/:id 寮詳細・編集
        │   ├── room/
        │   │   ├── index.vue                   # [画面05] /dormitories/:id/rooms 部屋一覧
        │   │   └── detail.vue                  # [画面06] /rooms/:id 部屋詳細・編集
        │   ├── employee/
        │   │   └── index.vue                   # M01-03 社員マスタ参照
        │   └── equipment/
        │       └── index.vue                   # [画面15] /items 備品マスタ管理
        │
        ├── vacancy/                            # M05 空き室管理
        │   └── index.vue                       # [画面07] /vacancies 空き室一覧
        │
        ├── residence/                          # M02 入退寮管理
        │   ├── index.vue                       # [画面09] /residences 入居履歴一覧
        │   ├── new.vue                         # [画面08] /residences/new 入居登録
        │   ├── detail.vue                      # [画面10] /residences/:id 入居履歴詳細
        │   ├── checkout.vue                    # [画面11] /residences/:id/checkout 退寮処理
        │   ├── items.vue                       # [画面16] /residences/:id/items 備品準備チェックリスト
        │   └── long-term-alert/
        │       └── index.vue                   # [画面12] /long-term-alerts 長期利用警告一覧
        │
        ├── rent/                               # M03 寮費管理
        │   ├── calculate.vue                   # [画面13] /rent/calculate 寮費計算・確定
        │   └── history.vue                     # [画面14] /rent/history 寮費履歴一覧
        │
        ├── equipment/                          # M04 備品管理
        │   └── storage.vue                     # [画面17] /items/storage 保管備品一覧
        │
        ├── import/                             # M06 Excelデータ取り込み
        │   └── index.vue                       # [画面18] /import 取り込みウィザード
        │
        └── admin/                              # M07 共通・管理機能
            ├── users/
            │   └── index.vue                   # [画面19] /admin/users ユーザー管理
            ├── roles/
            │   └── index.vue                   # ロール管理（権限設定）
            ├── settings/
            │   └── index.vue                   # [画面20] /admin/settings システム設定
            └── logs/
                └── index.vue                   # [画面21] /admin/logs 操作ログ一覧
