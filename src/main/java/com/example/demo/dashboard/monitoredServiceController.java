package com.example.demo.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/services")
@RequiredArgsConstructor
public class monitoredServiceController {
    private final monitoredServiceMapper mapper;

    @GetMapping
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectAll());
        return result;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody monitoredServiceDTO dto) {
        dto.setSort_no(mapper.selectMaxSortNo() + 1);
        if (dto.getEnabled() == null) dto.setEnabled(true);
        mapper.insert(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    @PostMapping("/{id}/update")
    public Map<String, Object> update(@PathVariable Integer id, @RequestBody monitoredServiceDTO dto) {
        dto.setService_id(id);
        mapper.update(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    // Swaps sort_no with the immediate neighbor in the requested direction - simplest possible
    // reorder for a short, manually-curated list, no drag-and-drop/bulk-reindex needed.
    @PostMapping("/{id}/move")
    public Map<String, Object> move(@PathVariable Integer id, @RequestParam String direction) {
        List<monitoredServiceDTO> all = mapper.selectAll();
        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getService_id().equals(id)) { index = i; break; }
        }
        int targetIndex = "up".equals(direction) ? index - 1 : index + 1;
        if (index >= 0 && targetIndex >= 0 && targetIndex < all.size()) {
            monitoredServiceDTO current = all.get(index);
            monitoredServiceDTO target = all.get(targetIndex);
            Integer currentSort = current.getSort_no();
            mapper.updateSortNo(current.getService_id(), target.getSort_no());
            mapper.updateSortNo(target.getService_id(), currentSort);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectAll());
        return result;
    }

    @PostMapping("/{id}/delete")
    public Map<String, Object> delete(@PathVariable Integer id) {
        mapper.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }
}
