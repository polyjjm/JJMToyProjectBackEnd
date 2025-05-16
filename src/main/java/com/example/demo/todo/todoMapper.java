package com.example.demo.todo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface todoMapper {
    List<todoDTO> selectByDate(@Param("date") String date, @Param("userId") String userId);

    List<todoDTO> selectAll(@Param("userId") String userId);

    void insertTodo(todoDTO todo);
    void updateTodo(@Param("id") Long id, @Param("fields") Map<String, Object> fields);

    void deleteTodo(@Param("id") Long id);
}
