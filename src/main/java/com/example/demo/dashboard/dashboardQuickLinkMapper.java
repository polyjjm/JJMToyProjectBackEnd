package com.example.demo.dashboard;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface dashboardQuickLinkMapper {
    List<dashboardQuickLinkDTO> selectAll();

    Integer selectMaxSortNo();

    void insert(dashboardQuickLinkDTO dto);

    void update(dashboardQuickLinkDTO dto);

    void updateSortNo(@Param("link_id") Integer linkId, @Param("sort_no") Integer sortNo);

    void delete(@Param("link_id") Integer linkId);
}
