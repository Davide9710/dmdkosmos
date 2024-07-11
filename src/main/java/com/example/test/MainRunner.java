/**
 *  By Tatsuya Mori
 */

package com.example.test;

import com.example.test.scalardb.MyLoadInitialData;
import com.example.test.scalardb.MySchemaLoader;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MainRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("==================== TestApplicationRunner ====================");
        MySchemaLoader.run();
        System.out.println("MySchemaLoader.run(): Succeeded!");
        //MyLoadInitialData.run();
        System.out.println("MyLoadInitialData.run(): Succeeded!");
    }
}