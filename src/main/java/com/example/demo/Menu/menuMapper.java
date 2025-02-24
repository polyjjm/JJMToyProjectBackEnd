package com.example.demo.Menu;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface menuMapper {
   public List<menuDTO> menuServiceList();
}
