package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args)
    {
        System.out.println("지금 테스트중입니다 ");
        SpringApplication.run(DemoApplication.class, args);
    }

}
