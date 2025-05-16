package com.example.demo.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TodoService {
    @Autowired
    private com.example.demo.todo.todoMapper todoMapper;

    public List<todoDTO> getTodosByDate(String date, String userId) {
        return todoMapper.selectByDate(date ,userId);
    }

    public void addTodo(todoDTO todo) {
        todoMapper.insertTodo(todo);
    }
    public List<todoDTO> getAllTodos(String userId) {
        return todoMapper.selectAll(userId);
    }
    public void updateTodo(Long id, Map<String, Object> updateFields) {
        todoMapper.updateTodo(id, updateFields);
    }

    public void deleteTodo(Long id) {
        todoMapper.deleteTodo(id);
    }
}
