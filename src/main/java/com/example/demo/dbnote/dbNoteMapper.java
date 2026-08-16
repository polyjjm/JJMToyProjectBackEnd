package com.example.demo.dbnote;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface dbNoteMapper {
    // Projects
    List<dbNoteProjectDTO> selectProjects();
    void insertProject(dbNoteProjectDTO dto);
    void updateProject(dbNoteProjectDTO dto);
    void deleteProject(@Param("project_id") Integer projectId);

    // Tables
    List<dbNoteTableDTO> selectTables(@Param("project_id") Integer projectId);
    dbNoteTableDTO selectTable(@Param("table_id") Integer tableId);
    void insertTable(dbNoteTableDTO dto);
    void updateTable(dbNoteTableDTO dto);
    void touchLastGeneratedAt(@Param("table_id") Integer tableId);
    void deleteTable(@Param("table_id") Integer tableId);

    // Columns
    List<dbNoteColumnDTO> selectColumns(@Param("table_id") Integer tableId);
    Integer selectMaxColumnSortNo(@Param("table_id") Integer tableId);
    void insertColumn(dbNoteColumnDTO dto);
    void updateColumn(dbNoteColumnDTO dto);
    void updateColumnSortNo(@Param("column_id") Integer columnId, @Param("sort_order") Integer sortOrder);
    void deleteColumn(@Param("column_id") Integer columnId);
}
