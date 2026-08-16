package com.example.demo.dashboard;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// Periodic status check for the dashboard's server-status widget.
//
// This does NOT query Docker - the backend container has no confirmed access to the Docker
// socket (no docker-compose file exists in either repo, and /var/run/docker.sock being mounted
// into this container wasn't confirmed), so real container state (running/stopped, actual
// uptime) isn't available without an infra change outside this codebase. Instead, each
// monitored_service optionally carries a health_check_url; this poll does a plain HTTP GET
// against it and records "UP" on any 2xx response, "DOWN" otherwise (including timeouts/
// connection errors). Services with no health_check_url configured (e.g. mysql, minio - not
// HTTP services) are simply skipped and stay at last_status = NULL, shown as "확인 안함"
// (not monitored) in the UI rather than a faked "up" - see selectEnabledWithHealthCheck.
//
// If Docker socket access becomes available later, this class is the one place to swap the
// HTTP GET for a real `docker inspect`/socket call - the DB shape (last_status/last_checked_at)
// doesn't need to change either way.
@Component
@RequiredArgsConstructor
public class MonitoredServiceHealthChecker {
    private static final Logger logger = LoggerFactory.getLogger(MonitoredServiceHealthChecker.class);
    private static final int TIMEOUT_MS = 3000;

    private final monitoredServiceMapper mapper;
    private final RestTemplate restTemplate = buildRestTemplate();

    private static RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_MS);
        factory.setReadTimeout(TIMEOUT_MS);
        return new RestTemplate(factory);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 5000)
    public void checkAll() {
        List<monitoredServiceDTO> services = mapper.selectEnabledWithHealthCheck();
        for (monitoredServiceDTO service : services) {
            String status;
            try {
                boolean ok = restTemplate.getForEntity(service.getHealth_check_url(), String.class)
                        .getStatusCode().is2xxSuccessful();
                status = ok ? "UP" : "DOWN";
            } catch (Exception e) {
                status = "DOWN";
                logger.debug("Health check failed for {}: {}", service.getService_name(), e.toString());
            }
            mapper.updateStatus(service.getService_id(), status);
        }
    }
}
