package com.example.demo.dbnote;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DB 관리 노트 (item 3) - schema definitions + code-generation source of truth. NOT a live DB
// client: no real database connections, no query execution against arbitrary databases here,
// by design (see the task's explicit security scoping). All 5 generated artifacts (CREATE
// TABLE/ALTER TABLE/sample INSERT/Java DTO/MyBatis XML) are computed client-side from the
// columns this controller returns - this class only does CRUD plus the one thing that has to
// be server-side: stamping the "last generated" diff marker.
@RestController
@RequestMapping("/api/tools/db-notes")
@RequiredArgsConstructor
public class dbNoteController {
    private final dbNoteMapper mapper;

    @GetMapping("/projects")
    public Map<String, Object> listProjects() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectProjects());
        return result;
    }

    @PostMapping("/projects")
    public Map<String, Object> createProject(@RequestBody dbNoteProjectDTO dto) {
        mapper.insertProject(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    @PostMapping("/projects/{id}/update")
    public Map<String, Object> renameProject(@PathVariable Integer id, @RequestBody dbNoteProjectDTO dto) {
        dto.setProject_id(id);
        mapper.updateProject(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @PostMapping("/projects/{id}/delete")
    public Map<String, Object> deleteProject(@PathVariable Integer id) {
        mapper.deleteProject(id);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @GetMapping("/projects/{id}/tables")
    public Map<String, Object> listTables(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectTables(id));
        return result;
    }

    @PostMapping("/projects/{id}/tables")
    public Map<String, Object> createTable(@PathVariable Integer id, @RequestBody dbNoteTableDTO dto) {
        dto.setProject_id(id);
        mapper.insertTable(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    @GetMapping("/tables/{id}")
    public Map<String, Object> getTable(@PathVariable Integer id) {
        Map<String, Object> data = new HashMap<>();
        data.put("table", mapper.selectTable(id));
        data.put("columns", mapper.selectColumns(id));
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return result;
    }

    // The one endpoint with a side effect: fetches the table as it currently stands (still
    // carrying the PRE-touch last_generated_at, so the frontend can diff columns.created_at
    // against it to find what's new since the last view) and only then stamps
    // last_generated_at = NOW() for next time - "opening this panel" IS "acknowledging the
    // current schema", same read-marks-seen idea as the chat feature's unread pointer.
    @GetMapping("/tables/{id}/generated")
    public Map<String, Object> getGeneratedView(@PathVariable Integer id) {
        dbNoteTableDTO table = mapper.selectTable(id);
        List<dbNoteColumnDTO> columns = mapper.selectColumns(id);
        mapper.touchLastGeneratedAt(id);

        Map<String, Object> data = new HashMap<>();
        data.put("table", table);
        data.put("columns", columns);
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return result;
    }

    @PostMapping("/tables/{id}/update")
    public Map<String, Object> renameTable(@PathVariable Integer id, @RequestBody dbNoteTableDTO dto) {
        dto.setTable_id(id);
        mapper.updateTable(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @PostMapping("/tables/{id}/delete")
    public Map<String, Object> deleteTable(@PathVariable Integer id) {
        mapper.deleteTable(id);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @PostMapping("/tables/{id}/columns")
    public Map<String, Object> addColumn(@PathVariable Integer id, @RequestBody dbNoteColumnDTO dto) {
        dto.setTable_id(id);
        dto.setSort_order(mapper.selectMaxColumnSortNo(id) + 1);
        mapper.insertColumn(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    @PostMapping("/tables/{id}/columns/{columnId}/update")
    public Map<String, Object> updateColumn(@PathVariable Integer id, @PathVariable Integer columnId, @RequestBody dbNoteColumnDTO dto) {
        dto.setColumn_id(columnId);
        mapper.updateColumn(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    @PostMapping("/tables/{id}/columns/{columnId}/move")
    public Map<String, Object> moveColumn(@PathVariable Integer id, @PathVariable Integer columnId, @RequestParam String direction) {
        List<dbNoteColumnDTO> all = mapper.selectColumns(id);
        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getColumn_id().equals(columnId)) { index = i; break; }
        }
        int targetIndex = "up".equals(direction) ? index - 1 : index + 1;
        if (index >= 0 && targetIndex >= 0 && targetIndex < all.size()) {
            dbNoteColumnDTO current = all.get(index);
            dbNoteColumnDTO target = all.get(targetIndex);
            Integer currentSort = current.getSort_order();
            mapper.updateColumnSortNo(current.getColumn_id(), target.getSort_order());
            mapper.updateColumnSortNo(target.getColumn_id(), currentSort);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectColumns(id));
        return result;
    }

    @PostMapping("/tables/{id}/columns/{columnId}/delete")
    public Map<String, Object> deleteColumn(@PathVariable Integer id, @PathVariable Integer columnId) {
        mapper.deleteColumn(columnId);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }
}
