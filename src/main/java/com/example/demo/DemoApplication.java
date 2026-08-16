package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

// UserDetailsServiceAutoConfiguration excluded: auth is JWT-only (see signin package),
// httpBasic/formLogin are disabled in securityConfig, so the auto-generated in-memory
// user/password Spring Boot would otherwise create (and log on every startup) is unused.
// EnableScheduling: drives MonitoredServiceHealthChecker's periodic dashboard status poll.
@EnableScheduling
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class DemoApplication {
    private static final  Logger log = LoggerFactory.getLogger(DemoApplication.class);
    public static void main(String[] args)
    {
        log.info("JJM BackEnd Server Start !!! ");
        SpringApplication.run(DemoApplication.class, args);
    }

}
