package com.example.demo.Menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class menuServiceImpl implements menuService {

    @Autowired
    menuMapper menuMapper;
    private static final Logger logger = LoggerFactory.getLogger(menuServiceImpl.class);
    @Override
    public List<menuDTO> menuListService() throws Exception {
        List<menuDTO> dto = new ArrayList<>();
        dto =  menuMapper.menuServiceList();

        logger.info(menuMapper.menuServiceList().toString());
        return dto;
    }
}
