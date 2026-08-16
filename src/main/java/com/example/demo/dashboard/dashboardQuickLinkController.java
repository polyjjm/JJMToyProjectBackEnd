package com.example.demo.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/links")
@RequiredArgsConstructor
public class dashboardQuickLinkController {
    private final dashboardQuickLinkMapper mapper;

    @GetMapping
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", mapper.selectAll());
        return result;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody dashboardQuickLinkDTO dto) {
        dto.setSort_no(mapper.selectMaxSortNo() + 1);
        mapper.insert(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", dto);
        return result;
    }

    @PostMapping("/{id}/update")
    public Map<String, Object> update(@PathVariable Integer id, @RequestBody dashboardQuickLinkDTO dto) {
        dto.setLink_id(id);
        mapper.update(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }

    // See monitoredServiceController.move for the same "swap sort_no with the neighbor" approach.
    @PostMapping("/{id}/move")
    public Map<String, Object> move(@PathVariable Integer id, @RequestParam String direction) {
        List<dashboardQuickLinkDTO> all = mapper.selectAll();
        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getLink_id().equals(id)) { index = i; break; }
        }
        int targetIndex = "up".equals(direction) ? index - 1 : index + 1;
        if (index >= 0 && targetIndex >= 0 && targetIndex < all.size()) {
            dashboardQuickLinkDTO current = all.get(index);
            dashboardQuickLinkDTO target = all.get(targetIndex);
            Integer currentSort = current.getSort_no();
            mapper.updateSortNo(current.getLink_id(), target.getSort_no());
            mapper.updateSortNo(target.getLink_id(), currentSort);
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
