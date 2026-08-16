package com.example.demo.dashboard;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface monitoredServiceMapper {
    List<monitoredServiceDTO> selectAll();

    List<monitoredServiceDTO> selectEnabledWithHealthCheck();

    Integer selectMaxSortNo();

    void insert(monitoredServiceDTO dto);

    void update(monitoredServiceDTO dto);

    void updateSortNo(@Param("service_id") Integer serviceId, @Param("sort_no") Integer sortNo);

    void updateStatus(@Param("service_id") Integer serviceId, @Param("status") String status);

    void delete(@Param("service_id") Integer serviceId);
}
