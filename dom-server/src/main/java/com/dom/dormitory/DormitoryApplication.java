package com.dom.dormitory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社員寮管理システム 起動クラス
 */
@SpringBootApplication
@MapperScan("com.dom.dormitory.mapper")
public class DormitoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  社員寮管理システム 起動完了!");
        System.out.println("  起動: http://localhost:8080");
        System.out.println("===========================================");
    }
}
