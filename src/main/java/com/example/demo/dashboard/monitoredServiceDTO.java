package com.example.demo.dashboard;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class monitoredServiceDTO {
    private Integer service_id;
    private String service_name;
    // Display-only label (e.g. "jjm-frontend") - not queried against Docker directly, see
    // MonitoredServiceHealthChecker's class comment for why.
    private String container_name;
    // NULL = not monitored (no periodic check runs, last_status stays NULL/"unknown" rather
    // than a faked "up").
    private String health_check_url;
    private Integer sort_no;
    private Boolean enabled;
    // 'UP' | 'DOWN' | null (never checked / not monitored)
    private String last_status;
    private LocalDateTime last_checked_at;
}
