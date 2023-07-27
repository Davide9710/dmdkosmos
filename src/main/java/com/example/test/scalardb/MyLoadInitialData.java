/**
 *  By Tatsuya Mori
 *  
 *  References:
 *      https://scalardb.scalar-labs.com/docs/latest/api-guide/#get-operation
 */

package com.example.test.scalardb;

import com.scalar.db.api.*;
import com.scalar.db.exception.storage.ExecutionException;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class MyLoadInitialData {
    public static void run() throws IOException, TransactionException, ExecutionException {
        Path configFilePath = Paths.get("src/main/resources/scalardb.properties");
        TransactionFactory transactionFactory = TransactionFactory.create(configFilePath);

        DistributedTransactionAdmin transactionAdmin = transactionFactory.getTransactionAdmin();
        transactionAdmin.truncateTable("mmybank", "accounts");
        transactionAdmin.truncateTable("pmybank", "accounts");
//        transactionAdmin.dropTable("mmybank", "accounts");
//        transactionAdmin.dropTable("pmybank", "accounts");

        DistributedTransactionManager transactionManager = transactionFactory.getTransactionManager();
        DistributedTransaction tx = null;
        try {
            tx = transactionManager.start();
            putAccount(tx, "mmybank", "accounts", "m1", "Andy", 1000);
            putAccount(tx, "mmybank", "accounts", "m2", "Becky", 2000);
            putAccount(tx, "mmybank", "accounts", "m3", "Clare", 3000);
            putAccount(tx, "pmybank", "accounts", "p1", "Daniel", 10000);
            putAccount(tx, "pmybank", "accounts", "p2", "Elen", 20000);
            putAccount(tx, "pmybank", "accounts", "p3", "Francis", 30000);
            tx.commit();
        } catch (TransactionException e) {
            if (tx != null) {
                tx.abort();
            }
            throw e;
        }
    }

    public static void putAccount(
            DistributedTransaction tx,
            String namespace,
            String table,
            String id,
            String name,
            int balance
    ) throws CrudException {
        Optional<Result> account =
                tx.get(
                        Get.newBuilder()
                                .namespace(namespace)
                                .table(table)
                                .partitionKey(Key.ofText("accountId", id))
                                .build()
                );
        if (!account.isPresent()) {
            tx.put(
                    Put.newBuilder()
                            .namespace(namespace)
                            .table(table)
                            .partitionKey(Key.ofText("accountId", id))
                            .textValue("accountName", name) // TODO
                            .intValue("balance", balance)
                            .build()
            );
        }
    }
}
