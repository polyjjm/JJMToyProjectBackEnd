-- Migration: sidebar menu changes for the chat-to-header move + weather/todo dashboard
-- consolidation. Run manually against the `toyDB` MySQL database (no Flyway/Liquibase - see
-- 2026-08-14_board_3tier_categories_and_comments.sql for the same convention).
--
-- No SQL seed file for the `menu` table exists in this repo, so the exact current row shape
-- (sort_no/depth/parent_id/sideYn conventions) couldn't be verified against live data before
-- writing this - it's built to match the app's inferred route paths (/, /board, /chat,
-- /weather, /todo) and the shape menuDTO.java/appShell.tsx expect. Check the existing 홈/게시판
-- rows' actual column values before running, and adjust the INSERT below to match if they
-- differ from what's assumed here.

-- 1) Chat moves from a sidebar nav item to the header bell + slide-in panel (see
--    appShell.tsx/ChatPanel.tsx) - it's no longer a page, so it no longer belongs in the menu
--    table at all.
DELETE FROM menu WHERE menu_url = '/chat';

-- 2) 날씨/할일 consolidate into one 대시보드 item (see Dashboard.tsx, which mounts the
--    existing WeatherMain/MainLayout components as-is).
DELETE FROM menu WHERE menu_url IN ('/weather', '/todo');

-- Adjust sort_no/depth/parent_id/sideYn to match this table's actual existing conventions -
-- these values assume a flat, sidebar-only, top-level menu (matching /, /board's apparent
-- shape) and a sort_no placed after 게시판.
INSERT INTO menu (menu_name, menu_url, sort_no, depth, parent_id, sideYn)
VALUES ('대시보드', '/dashboard', 3, 0, 0, 'Y');
