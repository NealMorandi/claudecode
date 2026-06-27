-- ========================================
-- 寮管理システム DBマイグレーション履歴
-- schema.sql が実際のDB構造を表す正とします。
-- このファイルは適用済みの変更履歴として保持します。
-- ========================================

-- [適用済み] dormitories テーブルへカラム追加
-- ALTER TABLE dormitories ADD COLUMN layout VARCHAR(200) NULL AFTER address;
-- ALTER TABLE dormitories ADD COLUMN remark TEXT NULL AFTER layout;

-- [適用済み] rooms テーブルへカラム追加
-- ALTER TABLE rooms ADD COLUMN rent INT NULL AFTER capacity;
-- ALTER TABLE rooms ADD COLUMN status ENUM('available','occupied','maintenance') NOT NULL DEFAULT 'available' AFTER capacity;
-- ALTER TABLE rooms ADD COLUMN remark TEXT NULL AFTER status;

-- [適用済み] residences テーブルへカラム追加
-- ALTER TABLE residences ADD COLUMN checkout_reason VARCHAR(100) NULL AFTER checkout_date;
-- ALTER TABLE residences ADD COLUMN is_deleted TINYINT NOT NULL DEFAULT 0 AFTER status;

-- [適用済み] equipment テーブル変更（カタログ方式へ）
-- ALTER TABLE equipment ADD COLUMN category VARCHAR(50) NULL AFTER name;
-- ALTER TABLE equipment ADD COLUMN maker VARCHAR(100) NULL AFTER name;
-- ALTER TABLE equipment CHANGE remark note TEXT NULL;
-- ALTER TABLE equipment DROP COLUMN room_id;
-- ALTER TABLE equipment DROP COLUMN quantity;
-- ALTER TABLE equipment DROP COLUMN status;

-- [適用済み] equipment_history テーブル変更（保管情報カラム追加）
-- ALTER TABLE equipment_history DROP COLUMN from_status;
-- ALTER TABLE equipment_history DROP COLUMN to_status;
-- ALTER TABLE equipment_history ADD COLUMN storage_location VARCHAR(200) NULL AFTER to_room_id;
-- ALTER TABLE equipment_history ADD COLUMN quantity INT NOT NULL DEFAULT 1 AFTER storage_location;
-- ALTER TABLE equipment_history ADD COLUMN equipment_condition VARCHAR(30) NULL AFTER quantity;

-- [適用済み] long_term_alert_status テーブル: id カラム追加
-- ALTER TABLE long_term_alert_status DROP PRIMARY KEY;
-- ALTER TABLE long_term_alert_status ADD COLUMN id INT NOT NULL AUTO_INCREMENT FIRST;
-- ALTER TABLE long_term_alert_status ADD PRIMARY KEY (id);
-- ALTER TABLE long_term_alert_status ADD UNIQUE KEY uq_employee_id (employee_id);

-- [適用済み] residence_items テーブル: quantity・residence_id NULL 対応
-- ALTER TABLE residence_items ADD COLUMN quantity INT NOT NULL DEFAULT 1 AFTER prepared;
-- ALTER TABLE residence_items MODIFY COLUMN residence_id INT NULL;

-- [適用済み] rent_records テーブル: employee_id・month_days 追加
-- ALTER TABLE rent_records ADD COLUMN employee_id INT NULL AFTER id;
-- ALTER TABLE rent_records ADD COLUMN month_days INT NULL AFTER days;

-- [適用済み] operation_logs テーブル: detail カラム追加
-- ALTER TABLE operation_logs ADD COLUMN detail TEXT NULL AFTER target_desc;

-- [適用済み] code_master テーブル作成
-- (schema.sql の CREATE TABLE IF NOT EXISTS code_master を参照)
