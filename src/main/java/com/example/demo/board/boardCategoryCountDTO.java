package com.example.demo.board;

import lombok.Data;

// One row of the 3-tier category tree: a (categoryMain, categoryMid, categorySub) combination
// that actually exists on at least one post, plus how many posts have that exact combination.
// The frontend rolls these rows up into the 3 pill rows (대/중/소) instead of the backend
// having to serve a different shape per drill-down step.
@Data
public class boardCategoryCountDTO {
    private String categoryMain;
    private String categoryMid;
    private String categorySub;
    private Integer count;
}
