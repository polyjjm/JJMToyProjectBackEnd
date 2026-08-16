package com.example.demo.customtable;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 만능 테이블 (item 1) - dynamic user-defined tables/columns/rows. See the 2026-08-18
// migration's comment on custom_table_row.data for why only row data is JSON here.
@RestController
@RequestMapping("/api/tools/tables")
@RequiredArgsConstructor
public class customTableController {
    private final customTableMapper mapper;

    // Every endpoint here is behind anyRequest().authenticated() (no securityConfig entry
    // added for /api/tools/**) - this is the same JWT-subject-as-owner pattern
    // boardServiceImpl/boardCommentServiceImpl use, appropriate for a personal single-user site.
    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @GetMapping
    public Map<String, Object> listTables() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectTables(currentUserId()));
        return result;
    }

    @PostMapping
    public Map<String, Object> createTable(@RequestBody customTableDTO dto) {
        dto.setUser_id(currentUserId());
        mapper.insertTable(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    // Combined table + columns fetch, so the grid page loads in one round trip.
    @GetMapping("/{id}")
    public Map<String, Object> getTable(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("table", mapper.selectTable(id));
        data.put("columns", mapper.selectColumns(id));
        result.put("data", data);
        return result;
    }

    @PostMapping("/{id}/update")
    public Map<String, Object> renameTable(@PathVariable Integer id, @RequestBody customTableDTO dto) {
        dto.setTable_id(id);
        mapper.updateTable(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @PostMapping("/{id}/delete")
    public Map<String, Object> deleteTable(@PathVariable Integer id) {
        mapper.deleteTable(id);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @PostMapping("/{id}/columns")
    public Map<String, Object> addColumn(@PathVariable Integer id, @RequestBody customTableColumnDTO dto) {
        dto.setTable_id(id);
        dto.setSort_order(mapper.selectMaxColumnSortNo(id) + 1);
        mapper.insertColumn(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    @PostMapping("/{id}/columns/{columnId}/update")
    public Map<String, Object> updateColumn(@PathVariable Integer id, @PathVariable Integer columnId, @RequestBody customTableColumnDTO dto) {
        dto.setColumn_id(columnId);
        mapper.updateColumn(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    // Swaps sort_order with the immediate neighbor - same reorder pattern as the dashboard's
    // monitored-service/quick-link widgets.
    @PostMapping("/{id}/columns/{columnId}/move")
    public Map<String, Object> moveColumn(@PathVariable Integer id, @PathVariable Integer columnId, @RequestParam String direction) {
        List<customTableColumnDTO> all = mapper.selectColumns(id);
        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getColumn_id().equals(columnId)) { index = i; break; }
        }
        int targetIndex = "up".equals(direction) ? index - 1 : index + 1;
        if (index >= 0 && targetIndex >= 0 && targetIndex < all.size()) {
            customTableColumnDTO current = all.get(index);
            customTableColumnDTO target = all.get(targetIndex);
            Integer currentSort = current.getSort_order();
            mapper.updateColumnSortNo(current.getColumn_id(), target.getSort_order());
            mapper.updateColumnSortNo(target.getColumn_id(), currentSort);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectColumns(id));
        return result;
    }

    @PostMapping("/{id}/columns/{columnId}/delete")
    public Map<String, Object> deleteColumn(@PathVariable Integer id, @PathVariable Integer columnId) {
        mapper.deleteColumn(columnId);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @GetMapping("/{id}/rows")
    public Map<String, Object> listRows(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectRows(id));
        return result;
    }

    @PostMapping("/{id}/rows")
    public Map<String, Object> addRow(@PathVariable Integer id, @RequestBody customTableRowDTO dto) {
        dto.setTable_id(id);
        mapper.insertRow(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    @PostMapping("/{id}/rows/{rowId}/update")
    public Map<String, Object> updateRow(@PathVariable Integer id, @PathVariable Integer rowId, @RequestBody customTableRowDTO dto) {
        dto.setRow_id(rowId);
        mapper.updateRow(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @PostMapping("/{id}/rows/{rowId}/delete")
    public Map<String, Object> deleteRow(@PathVariable Integer id, @PathVariable Integer rowId) {
        mapper.deleteRow(rowId);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }
}
