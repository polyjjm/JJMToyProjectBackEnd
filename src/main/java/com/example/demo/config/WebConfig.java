package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    //public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";



    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //.allowedOrigins("http://localhost:8020","http://localhost:8082","http://3.36.127.136:8082", "http://3.36.127.136:80" , "http://3.36.127.136:8020" , "http://localhost:80" ,"http://localhost:3000" , "http://3.36.127.136:3000")
                .allowedOriginPatterns("*")
                //.allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowedHeaders("*") // ✅ 모든 헤더 허용
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    /*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler(resourceHandler).addResourceLocations(resourceLocation);

    }
    */
}
