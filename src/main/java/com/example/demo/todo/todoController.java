package com.example.demo.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todo")
public class todoController {
    @Autowired
    private TodoService todoService;

    // ✅ GET - 날짜별 할 일 목록 조회
    @PostMapping("/list")
    public Map<String, Object> getTodos(@RequestBody Map<String,String>body) {
        String date = body.get("date");
        String userId = body.get("user_id");
        Map returnMap = new HashMap();
        returnMap.put("data" , todoService.getTodosByDate(date , userId));
        return returnMap;
    }
    // ✅ POST - 전체 할 일 목록 조회
    @PostMapping("/list/all")
    public Map<String, Object> getAllTodos(@RequestBody Map<String, String> body) {
        String userId = body.get("user_id");
        Map returnMap = new HashMap();
        returnMap.put("data" , todoService.getAllTodos(userId));
        return returnMap;
    }
    // ✅ POST - 새 할 일 추가
    @PostMapping
    public void addTodo(@RequestBody todoDTO todo) {
        todoService.addTodo(todo);
    }

    // ✅ POST - 특정 할 일 수정
    @PostMapping("/{id}")
    public void updateTodo(@PathVariable Long id, @RequestBody Map<String, Object> updateFields) {
        todoService.updateTodo(id, updateFields);
    }

    // ✅ POST - 특정 할 일 삭제
    @PostMapping("/delete/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }
}
