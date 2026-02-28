package com.sleepywang.accentmap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sleepywang.accentmap.dao")
public class AccentMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccentMapApplication.class,args);
    }
}
