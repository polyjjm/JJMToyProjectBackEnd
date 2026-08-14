package com.example.demo.common;

import lombok.Data;

@Data
public class searchDTO {
    private String searchWord;
    private String searchTableName;
    private Integer scrollIndex;
    // Text search scope for board search: "title" | "content" | "titleContent" (default).
    // Independent of the category filters below so both can be combined in one request
    // (e.g. "MUI" within the 개발 category), unlike the old hashtag-vs-title exclusive scheme.
    private String searchScope;
    // 3-tier freeform category filter (board only). Any of the three may be null/blank,
    // meaning "전체" (no filter) at that level - the frontend simply omits params for
    // levels it hasn't narrowed down to yet.
    private String categoryMain;
    private String categoryMid;
    private String categorySub;
}
