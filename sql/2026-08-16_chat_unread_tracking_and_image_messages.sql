-- Migration: chat unread tracking (room list badges + read receipts) + image messages
-- Run manually against the `toyDB` MySQL database (no Flyway/Liquibase in this project -
-- see 2026-08-14_board_3tier_categories_and_comments.sql for the same convention).
--
-- Context: chat_room already has is_group and room_name (used today by the guest group
-- room) - no change needed there. What's missing:
--
-- 1) A per-member read pointer. chat_message.is_read (existing column) is a single boolean
--    per message and can only represent "read by the one other person" - it can't express
--    "read by 2 of 3 group members" once group chat rooms exist. last_read_message_id on
--    chat_room_member is the one mechanism that drives BOTH the per-room unread badge in
--    the room list AND the "읽음" indicator under my own sent messages: a message is "read
--    by member X" once X's last_read_message_id >= that message's id. chat_message.is_read
--    is left in place (not dropped) but the app stops writing/reading it - same "leave the
--    unused column, don't touch schema further" call as board_changeThumbnail.
--
-- 2) message_type/attachment_url - chat is text-only today (see chatMessage.java / the
--    insertMessage INSERT). Image messages reuse the existing MinIO upload path
--    (commonServiceImpl) and just record the resulting URL here.

-- 1) Per-member read pointer. NULL = "never opened this room" (existing members reading
--    everything as unread on migration day is the correct, expected behavior here - there's
--    no prior read history to backfill from with any confidence).
ALTER TABLE chat_room_member
  ADD COLUMN last_read_message_id BIGINT NULL AFTER user_id;

-- Enforces "one read pointer per (room, member)" and makes the UPDATE that advances it
-- unambiguous. If this fails, some room already has a duplicate (room_id, user_id) row from
-- before joinOrCreateGuestGroupRoom's app-level isMemberOfRoom check existed - clean that up
-- first (DELETE the older duplicate row) and re-run.
ALTER TABLE chat_room_member
  ADD CONSTRAINT uq_chat_room_member_room_user UNIQUE (room_id, user_id);

-- Speeds up the unread-count query (COUNT(*) WHERE room_id = ? AND id > ?), run once per
-- room per room-list render.
ALTER TABLE chat_message
  ADD INDEX idx_chat_message_room_id_id (room_id, id);

-- 2) Image message support.
ALTER TABLE chat_message
  ADD COLUMN message_type ENUM('TEXT', 'IMAGE') NOT NULL DEFAULT 'TEXT' AFTER message,
  ADD COLUMN attachment_url VARCHAR(500) NULL AFTER message_type;
