/**
 *  By Kazuki Ozeki
 */

package com.example.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {
    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Main.class, args);
    }
}