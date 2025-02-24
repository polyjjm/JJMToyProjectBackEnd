package com.example.demo.Menu;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public interface menuService {

    public List<menuDTO> menuListService () throws  Exception;
}
