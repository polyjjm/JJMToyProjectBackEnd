-- Migration: board 3-tier category system + minimal comments feature
-- Run manually against the `toyDB` MySQL database (this project has no
-- migration tooling — no Flyway/Liquibase — so this file is a plain script,
-- not something the app applies automatically).
--
-- Context: replaces the old freeform `board_hashTag` column (comma-joined
-- tag list) with 3 separate freeform text columns representing a strict
-- hierarchy: 대분류(main) > 중분류(mid) > 소분류(sub). All 3 are still
-- freeform text entered by the user, not picked from a fixed preset list —
-- only the *shape* (3 levels) is fixed, not the values.

-- 1) board: drop hashtag, add 3-tier category columns
ALTER TABLE board
  DROP COLUMN board_hashTag,
  ADD COLUMN board_categoryMain VARCHAR(50) NULL AFTER board_userName,
  ADD COLUMN board_categoryMid  VARCHAR(50) NULL AFTER board_categoryMain,
  ADD COLUMN board_categorySub  VARCHAR(50) NULL AFTER board_categoryMid;

-- 2) new minimal comment table (flat, no nested replies/edits/likes by design)
CREATE TABLE board_comment (
  comment_no        INT PRIMARY KEY AUTO_INCREMENT,
  board_no          INT NOT NULL,
  comment_userName  VARCHAR(100) NOT NULL,
  comment_content   VARCHAR(500) NOT NULL,
  comment_date      DATETIME NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_board_comment_board
    FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE
);
