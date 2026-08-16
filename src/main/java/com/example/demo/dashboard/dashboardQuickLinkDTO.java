package com.example.demo.dashboard;

import lombok.Data;

@Data
public class dashboardQuickLinkDTO {
    private Integer link_id;
    private String label;
    private String subtitle;
    private String url;
    private String icon; // single emoji, matches dashboard-mockup.html's icon treatment
    private Integer sort_no;
}
