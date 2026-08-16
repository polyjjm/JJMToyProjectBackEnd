package com.example.demo.customtable;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface customTableMapper {
    // Tables
    List<customTableDTO> selectTables(@Param("user_id") String userId);
    customTableDTO selectTable(@Param("table_id") Integer tableId);
    void insertTable(customTableDTO dto);
    void updateTable(customTableDTO dto);
    void deleteTable(@Param("table_id") Integer tableId);

    // Columns
    List<customTableColumnDTO> selectColumns(@Param("table_id") Integer tableId);
    Integer selectMaxColumnSortNo(@Param("table_id") Integer tableId);
    void insertColumn(customTableColumnDTO dto);
    void updateColumn(customTableColumnDTO dto);
    void updateColumnSortNo(@Param("column_id") Integer columnId, @Param("sort_order") Integer sortOrder);
    void deleteColumn(@Param("column_id") Integer columnId);

    // Rows
    List<customTableRowDTO> selectRows(@Param("table_id") Integer tableId);
    void insertRow(customTableRowDTO dto);
    void updateRow(customTableRowDTO dto);
    void deleteRow(@Param("row_id") Integer rowId);
}
