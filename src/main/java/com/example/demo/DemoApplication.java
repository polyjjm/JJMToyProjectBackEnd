package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args)
    {
        System.out.println("지금 테스트중입니다 ");
        System.out.println("주종민 도커 파일 생성중 ");
        System.out.println("주종민 도커 파일 생성중 2");
        SpringApplication.run(DemoApplication.class, args);
    }

}
