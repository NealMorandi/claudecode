-- 社員寮管理システム DDL
-- Database: dom_db
-- MySQL 8.x
-- このファイルは実際のDB構造を反映しています（最終更新: 2026-06-21）

CREATE DATABASE IF NOT EXISTS dom_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dom_db;

-- ユーザーテーブル
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主キー',
    username    VARCHAR(50) NOT NULL                COMMENT 'ユーザー名（一意）',
    name        VARCHAR(100) NOT NULL               COMMENT '氏名',
    password    VARCHAR(255) NOT NULL               COMMENT 'パスワード（BCrypt）',
    role        ENUM('admin','staff','viewer') NOT NULL DEFAULT 'staff' COMMENT 'ロール',
    is_active   TINYINT     NOT NULL DEFAULT 1      COMMENT '有効フラグ(1:有効,0:無効)',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='システムユーザー';

-- ロールテーブル
CREATE TABLE IF NOT EXISTS roles (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '主キー',
    name         VARCHAR(50)  NOT NULL               COMMENT 'ロールコード',
    display_name VARCHAR(100) NOT NULL               COMMENT '表示名',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ロールマスタ';

-- 社員マスタ
CREATE TABLE IF NOT EXISTS employees (
    id             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主キー',
    employee_no    VARCHAR(50) NOT NULL               COMMENT '社員番号（一意）',
    name           VARCHAR(100) NOT NULL              COMMENT '氏名',
    gender         ENUM('男','女') NOT NULL           COMMENT '性別',
    type           ENUM('japan','china') NOT NULL DEFAULT 'japan' COMMENT '社員区分',
    department     VARCHAR(100)                       COMMENT '所属部門',
    first_use_date DATE                               COMMENT '初回入居日',
    is_deleted     TINYINT     NOT NULL DEFAULT 0     COMMENT '論理削除(0:有効,1:削除)',
    created_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_employee_no (employee_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社員マスタ';

-- 寮マスタ
CREATE TABLE IF NOT EXISTS dormitories (
    id         INT          NOT NULL AUTO_INCREMENT COMMENT '主キー',
    name       VARCHAR(100) NOT NULL               COMMENT '寮名',
    type       ENUM('男','女') NOT NULL             COMMENT '寮種別',
    status     VARCHAR(10)  NOT NULL DEFAULT 'active' COMMENT 'ステータス(active=有効,inactive=退却)',
    prefecture VARCHAR(10)                         COMMENT '都道府県',
    address    VARCHAR(255)                        COMMENT '住所',
    is_deleted TINYINT      NOT NULL DEFAULT 0     COMMENT '論理削除',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    layout     VARCHAR(200)                        COMMENT '間取り',
    remark     TEXT                                COMMENT '備考',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寮マスタ';

-- 部屋マスタ
CREATE TABLE IF NOT EXISTS rooms (
    id           INT           NOT NULL AUTO_INCREMENT COMMENT '主キー',
    dormitory_id INT           NOT NULL               COMMENT '寮ID',
    name         VARCHAR(50)   NOT NULL               COMMENT '部屋名',
    area         DECIMAL(6,2)  NOT NULL               COMMENT '面積（㎡）',
    capacity     INT           NOT NULL DEFAULT 1     COMMENT '定員',
    rent         INT           NULL                   COMMENT '部屋個別寮費（円/日、NULLは共通設定を使用）',
    status       ENUM('available','occupied','maintenance') NOT NULL DEFAULT 'available' COMMENT 'ステータス',
    remark       TEXT                                 COMMENT '備考',
    is_deleted   TINYINT       NOT NULL DEFAULT 0     COMMENT '論理削除',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_dormitory_id (dormitory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部屋マスタ';

-- 備品マスタ（カタログ方式: 物理在庫は equipment_history で管理）
CREATE TABLE IF NOT EXISTS equipment (
    id         BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主キー',
    name       VARCHAR(100) NOT NULL                 COMMENT '備品名',
    maker      VARCHAR(100)                          COMMENT 'メーカー',
    category   VARCHAR(50)  NOT NULL                 COMMENT 'カテゴリ',
    note       TEXT                                  COMMENT '備考',
    is_deleted TINYINT      NOT NULL DEFAULT 0        COMMENT '論理削除',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='備品マスタ';

-- 入居記録
CREATE TABLE IF NOT EXISTS residences (
    id                     INT      NOT NULL AUTO_INCREMENT COMMENT '主キー',
    employee_id            INT      NOT NULL               COMMENT '社員ID',
    room_id                INT      NOT NULL               COMMENT '部屋ID',
    checkin_date           DATE     NOT NULL               COMMENT '入居日',
    expected_checkout_date DATE                            COMMENT '退寮予定日',
    checkout_date          DATE                            COMMENT '実際の退寮日',
    checkout_reason        VARCHAR(100)                    COMMENT '退寮理由',
    status                 ENUM('active','checked_out') NOT NULL DEFAULT 'active' COMMENT 'ステータス',
    is_deleted             TINYINT  NOT NULL DEFAULT 0     COMMENT '論理削除',
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_employee_id (employee_id),
    KEY idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入居記録';

-- 入居備品
CREATE TABLE IF NOT EXISTS residence_items (
    id               INT         NOT NULL AUTO_INCREMENT COMMENT '主キー',
    residence_id     INT         NULL                   COMMENT '入居記録ID（NULL=保管専用）',
    equipment_id     INT         NOT NULL               COMMENT '備品ID',
    prepared         TINYINT(1)  NOT NULL DEFAULT 0     COMMENT '準備済みフラグ',
    quantity         INT         NOT NULL DEFAULT 1     COMMENT '数量',
    storage_location VARCHAR(200)                       COMMENT '保管場所',
    disposition      VARCHAR(50)                        COMMENT '処分方法(discard/storage/reuse)',
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_residence_id (residence_id),
    KEY idx_equipment_id (equipment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入居備品';

-- 寮費
CREATE TABLE IF NOT EXISTS rent_records (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '主キー',
    employee_id  INT          NULL                   COMMENT '社員ID',
    residence_id INT          NOT NULL               COMMENT '入居記録ID',
    year_month   CHAR(7)      NOT NULL               COMMENT '対象年月(YYYY-MM)',
    area         DECIMAL(6,2) NOT NULL               COMMENT '面積（㎡）',
    unit_price   DECIMAL(10,2) NOT NULL              COMMENT '単価（円/日）',
    days         INT          NOT NULL               COMMENT '入居日数',
    month_days   INT          NULL                   COMMENT '月の日数',
    amount       DECIMAL(10,2) NOT NULL              COMMENT '寮費（円）',
    status       ENUM('draft','confirmed') NOT NULL DEFAULT 'draft' COMMENT '確定ステータス',
    confirmed_at DATETIME     NULL                   COMMENT '確定日時',
    confirmed_by INT          NULL                   COMMENT '確定者ID',
    is_deleted   TINYINT      NOT NULL DEFAULT 0     COMMENT '論理削除',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_residence_id (residence_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寮費';

-- 備品履歴
CREATE TABLE IF NOT EXISTS equipment_history (
    id                  BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主キー',
    equipment_id        BIGINT       NOT NULL                 COMMENT '備品ID',
    action              VARCHAR(30)  NOT NULL                 COMMENT '操作種別(created/assigned/returned/moved/storage/disposed/reused)',
    from_room_id        BIGINT                               COMMENT '移動元部屋ID',
    to_room_id          BIGINT                               COMMENT '移動先部屋ID',
    storage_location    VARCHAR(200)                         COMMENT '保管場所',
    quantity            INT          NOT NULL DEFAULT 1       COMMENT '数量',
    equipment_condition VARCHAR(30)                          COMMENT '状態',
    residence_id        BIGINT                               COMMENT '関連入居記録ID',
    employee_id         BIGINT                               COMMENT '関連社員ID',
    operator_id         BIGINT                               COMMENT '操作者ID',
    note                TEXT                                 COMMENT '備考',
    action_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作日時',
    PRIMARY KEY (id),
    KEY idx_eh_equipment_id (equipment_id),
    KEY idx_eh_action_at    (action_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='備品履歴';

-- 長期利用警告ステータス
CREATE TABLE IF NOT EXISTS long_term_alert_status (
    id           INT      NOT NULL AUTO_INCREMENT COMMENT '主キー',
    employee_id  INT      NOT NULL UNIQUE         COMMENT '社員ID',
    alert_status ENUM('pending','notified','applied','done') NOT NULL DEFAULT 'pending' COMMENT '警告ステータス',
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='長期利用警告ステータス';

-- 操作ログ
CREATE TABLE IF NOT EXISTS operation_logs (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主キー',
    operator_id   BIGINT                              COMMENT '操作者ID',
    operator_name VARCHAR(100)                        COMMENT '操作者氏名',
    action        VARCHAR(100) NOT NULL               COMMENT 'アクション',
    target_desc   VARCHAR(200)                        COMMENT '操作対象説明',
    target_id     VARCHAR(50)                         COMMENT '操作対象ID',
    detail        TEXT                                COMMENT '詳細情報(JSON)',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作ログ';

-- システム設定（単一行フラット構造）
CREATE TABLE IF NOT EXISTS settings (
    id               INT          NOT NULL AUTO_INCREMENT COMMENT '主キー',
    long_term_years  INT          NOT NULL DEFAULT 3     COMMENT '長期利用警告基準年数',
    unit_price_japan DECIMAL(10,2) NOT NULL DEFAULT 2000 COMMENT '日本人社員単価（円/日）',
    unit_price_china DECIMAL(10,2) NOT NULL DEFAULT 0    COMMENT '中国人社員単価（円/日）',
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='システム設定';

-- コードマスタ（都道府県・間取り等のドロップダウン選択肢）
CREATE TABLE IF NOT EXISTS code_master (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主キー',
    category   VARCHAR(50) NOT NULL               COMMENT 'カテゴリ（PREFECTURE/LAYOUT等）',
    code       VARCHAR(50) NOT NULL               COMMENT 'コード値',
    name       VARCHAR(100) NOT NULL              COMMENT '表示名',
    sort_order INT         NOT NULL DEFAULT 0     COMMENT '表示順',
    is_deleted TINYINT     NOT NULL DEFAULT 0     COMMENT '論理削除',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='コードマスタ';

-- 初期データ: 管理者ユーザー (password: admin123)
INSERT IGNORE INTO users (username, name, password, role, is_active)
VALUES ('admin', '管理者', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', 1);

-- 初期データ: ロール
INSERT IGNORE INTO roles (name, display_name) VALUES ('admin', '管理者');
INSERT IGNORE INTO roles (name, display_name) VALUES ('staff', 'スタッフ');
INSERT IGNORE INTO roles (name, display_name) VALUES ('viewer', '閲覧者');

-- 初期データ: システム設定
INSERT IGNORE INTO settings (id, long_term_years, unit_price_japan, unit_price_china)
VALUES (1, 3, 2000.00, 0.00);

-- 初期データ: コードマスタ（都道府県）
INSERT IGNORE INTO code_master (category, code, name, sort_order) VALUES
('PREFECTURE', '01', '北海道', 1), ('PREFECTURE', '13', '東京都', 13),
('PREFECTURE', '14', '神奈川県', 14), ('PREFECTURE', '23', '愛知県', 23),
('PREFECTURE', '27', '大阪府', 27), ('PREFECTURE', '28', '兵庫県', 28),
('PREFECTURE', '40', '福岡県', 40), ('PREFECTURE', '47', '沖縄県', 47);

-- 初期データ: コードマスタ（間取り）
INSERT IGNORE INTO code_master (category, code, name, sort_order) VALUES
('LAYOUT', '1K', '1K', 1), ('LAYOUT', '1DK', '1DK', 2),
('LAYOUT', '1LDK', '1LDK', 3), ('LAYOUT', '2K', '2K', 4),
('LAYOUT', '2DK', '2DK', 5), ('LAYOUT', '2LDK', '2LDK', 6),
('LAYOUT', '3LDK', '3LDK', 7);
