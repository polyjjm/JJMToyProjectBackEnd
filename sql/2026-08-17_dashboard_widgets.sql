-- Migration: dashboard build-out - todo priority, monitored services, quick links.
-- Run manually against the `toyDB` MySQL database (no Flyway/Liquibase - see
-- 2026-08-14_board_3tier_categories_and_comments.sql for the same convention).

-- 1) Todo priority (category/date already exist per the previous round's check - date already
--    doubles as the due-date shown in the dashboard-mockup.html todo widget's due tags).
ALTER TABLE todo
  ADD COLUMN priority ENUM('HIGH', 'MID', 'LOW') NOT NULL DEFAULT 'LOW' AFTER important;

-- 2) Server status widget. No live Docker access from the backend container (confirmed with
--    the user - no docker-compose file, socket not mounted), so status is a periodic HTTP
--    health check instead of real container state - see MonitoredServiceHealthChecker.java.
--    health_check_url is nullable on purpose: a service with no URL configured (e.g. mysql,
--    minio - nothing meaningful to GET) shows as "not monitored" rather than a faked up/down.
CREATE TABLE monitored_service (
  service_id        INT PRIMARY KEY AUTO_INCREMENT,
  service_name      VARCHAR(100) NOT NULL,
  container_name    VARCHAR(100) NULL,      -- display label only, not queried directly
  health_check_url  VARCHAR(500) NULL,      -- NULL = not monitored (shown as unknown, not "up")
  sort_no           INT NOT NULL DEFAULT 0,
  enabled           BOOLEAN NOT NULL DEFAULT TRUE,
  last_status       VARCHAR(10) NULL,       -- 'UP' | 'DOWN' | NULL (never checked / not monitored)
  last_checked_at   DATETIME NULL
);

-- 3) Quick links widget.
CREATE TABLE dashboard_quick_link (
  link_id   INT PRIMARY KEY AUTO_INCREMENT,
  label     VARCHAR(100) NOT NULL,
  subtitle  VARCHAR(200) NULL,
  url       VARCHAR(500) NOT NULL,
  icon      VARCHAR(10) NOT NULL DEFAULT '🔗',  -- single emoji, matches dashboard-mockup.html
  sort_no   INT NOT NULL DEFAULT 0
);
