/**
 *  By Tatsuya
 *
 *  References:
 *      https://scalardb.scalar-labs.com/docs/latest/schema-loader/
 */
package com.example.test.scalardb;

import com.scalar.db.schemaloader.SchemaLoader;
import com.scalar.db.schemaloader.SchemaLoaderException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MySchemaLoader {
    public static void run() throws SchemaLoaderException {
        Path configFilePath = Paths.get("src/main/resources/scalardb.properties");
        Path schemaFilePath = Paths.get("src/main/resources/schema.json");
        Map<String, String> tableCreationOptions = new HashMap<>();
        boolean createCoordinatorTables = true; // whether to create the coordinator tables or not

        // Create tables
        SchemaLoader.load(configFilePath, schemaFilePath, tableCreationOptions, createCoordinatorTables);
    }
}
