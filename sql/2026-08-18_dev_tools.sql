-- Migration: 개발자 도구 section - 만능 테이블 (item 1), DB 관리 노트 (item 3), nav entry.
-- MySQL 8. Run manually (no Flyway/Liquibase in this project - see
-- 2026-08-14_board_3tier_categories_and_comments.sql for the same convention).
--
-- Items 2 (format converter) and 4 (text tools) are entirely client-side and need no schema.

-- 1) 만능 테이블 (item 1) - dynamic user-defined tables.
--
-- Deliberately NOT generating real SQL columns per user table (no ALTER TABLE churn every
-- time a column is added). custom_table/custom_table_column have a normal, fixed relational
-- shape because THEIR shape is fixed (a table always has a name/owner, a column always has a
-- name/type/sort order) - only custom_table_row.data is JSON, because row shape is exactly the
-- thing the user defines at runtime, which is the actual point of this tool. This is the only
-- JSON-blob table in this migration; db_note_column below is intentionally NOT built this way
-- (see its comment) because its columns have a fixed, known shape unrelated to this tool's
-- generic TEXT/NUMBER/DATE/CHECKBOX/SELECT vocabulary.
CREATE TABLE custom_table (
  table_id    INT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  user_id     VARCHAR(100) NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT NOW()
);

CREATE TABLE custom_table_column (
  column_id    INT PRIMARY KEY AUTO_INCREMENT,
  table_id     INT NOT NULL,
  name         VARCHAR(100) NOT NULL,
  type         ENUM('TEXT', 'NUMBER', 'DATE', 'CHECKBOX', 'SELECT') NOT NULL,
  options      JSON NULL,              -- SELECT only: e.g. ["옵션1","옵션2"] - null otherwise
  sort_order   INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_custom_table_column_table
    FOREIGN KEY (table_id) REFERENCES custom_table(table_id) ON DELETE CASCADE
);

CREATE TABLE custom_table_row (
  row_id       INT PRIMARY KEY AUTO_INCREMENT,
  table_id     INT NOT NULL,
  data         JSON NOT NULL,          -- {"<column_id>": value, ...} - see comment above
  sort_order   INT NOT NULL DEFAULT 0,
  created_at   DATETIME NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_custom_table_row_table
    FOREIGN KEY (table_id) REFERENCES custom_table(table_id) ON DELETE CASCADE
);

-- 2) DB 관리 노트 (item 3) - schema definitions + code generation source of truth. Own schema,
--    not built on custom_table_row's JSON-blob approach: a "column definition" here always has
--    exactly the same fields (sql_type, length, nullable, primary key, default, note) - that's
--    fixed metadata about a real SQL column, not user-arbitrary data, so real columns are the
--    right fit (and preserve type safety the JSON-blob approach would throw away for no
--    benefit here).
CREATE TABLE db_note_project (
  project_id  INT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT NOW()
);

CREATE TABLE db_note_table (
  table_id          INT PRIMARY KEY AUTO_INCREMENT,
  project_id        INT NOT NULL,
  name              VARCHAR(100) NOT NULL,
  -- Diff marker for the ALTER TABLE generator: columns with created_at after this are "added
  -- since last generated". NULL = never generated yet (ALTER output is empty; CREATE TABLE
  -- already covers everything). Stamped to NOW() server-side whenever the generate-code panel
  -- is opened - see dbNoteController.getGeneratedView.
  last_generated_at DATETIME NULL,
  created_at        DATETIME NOT NULL DEFAULT NOW(),
  updated_at        DATETIME NOT NULL DEFAULT NOW() ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_db_note_table_project
    FOREIGN KEY (project_id) REFERENCES db_note_project(project_id) ON DELETE CASCADE
);

CREATE TABLE db_note_column (
  column_id       INT PRIMARY KEY AUTO_INCREMENT,
  table_id        INT NOT NULL,
  name            VARCHAR(100) NOT NULL,
  sql_type        VARCHAR(50) NOT NULL,   -- e.g. VARCHAR, INT, DATETIME, DECIMAL, BOOLEAN
  length          VARCHAR(50) NULL,       -- e.g. "100" or "10,2" for DECIMAL precision,scale
  nullable        BOOLEAN NOT NULL DEFAULT TRUE,
  is_primary_key  BOOLEAN NOT NULL DEFAULT FALSE,
  default_value   VARCHAR(200) NULL,
  note            VARCHAR(500) NULL,
  sort_order      INT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT NOW(),  -- drives the ALTER TABLE diff, see above
  CONSTRAINT fk_db_note_column_table
    FOREIGN KEY (table_id) REFERENCES db_note_table(table_id) ON DELETE CASCADE
);

-- 3) Sidebar nav entry. No SQL seed file for the `menu` table exists in this repo (same caveat
--    as the 2026-08-16 dashboard menu migration) - built to match this app's inferred
--    conventions; adjust sort_no/depth/parent_id/sideYn if your actual rows differ.
INSERT INTO menu (menu_name, menu_url, sort_no, depth, parent_id, sideYn)
VALUES ('개발자 도구', '/tools', 4, 0, 0, 'Y');
