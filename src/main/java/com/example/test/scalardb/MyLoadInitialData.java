package com.example.test.scalardb;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionAdmin;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
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
        transactionAdmin.truncateTable("mysqllibrary", "account");
        transactionAdmin.truncateTable("mysqllibrary", "transaction");
        transactionAdmin.truncateTable("mysqllibrary", "book");

        transactionAdmin.truncateTable("postgrelibrary", "transaction");
        transactionAdmin.truncateTable("postgrelibrary", "book");

        DistributedTransactionManager transactionManager = transactionFactory.getTransactionManager();
        DistributedTransaction tx = null;
        try {
            tx = transactionManager.start();
            putAccount(tx, "mysqllibrary", "account", "m1", "Andy");
            putAccount(tx, "mysqllibrary", "account", "m2", "Becky");
            putAccount(tx, "mysqllibrary", "account", "m3", "Clare");
            putBook(tx, "mysqllibrary", "book", "b1", "1984", 0, "George Orwell", "1234567890");
            putBook(tx, "postgrelibrary", "book", "b2", "To Kill a Mockingbird", 1, "Harper Lee", "0987654321");
            putTransaction(tx, "mysqllibrary", "transaction", "t1", "b1", "m1", "20240711", "20240725", null, "RESERVED");
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
            String name
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
                            .textValue("accountName", name)
                            .build()
            );
        }
    }

    public static void putTransaction(
            DistributedTransaction tx,
            String namespace,
            String table,
            String transactionId,
            String bookId,
            String accountId,
            String reservationDate,
            String dueDate,
            String returnDate,
            String status
    ) throws CrudException {
        Optional<Result> transaction =
                tx.get(
                        Get.newBuilder()
                                .namespace(namespace)
                                .table(table)
                                .partitionKey(Key.ofText("transactionId", transactionId))
                                .build()
                );
        if (!transaction.isPresent()) {
            tx.put(
                    Put.newBuilder()
                            .namespace(namespace)
                            .table(table)
                            .partitionKey(Key.ofText("transactionId", transactionId))
                            .textValue("bookId", bookId)
                            .textValue("accountId", accountId)
                            .textValue("reservationDate", reservationDate)
                            .textValue("dueDate", dueDate)
                            .textValue("returnDate", returnDate)
                            .textValue("status", status)
                            .build()
            );
        }
    }

    public static void putBook(
            DistributedTransaction tx,
            String namespace,
            String table,
            String bookId,
            String bookTitle,
            int isAvailable,
            String author,
            String isbn
    ) throws CrudException {
        Optional<Result> book =
                tx.get(
                        Get.newBuilder()
                                .namespace(namespace)
                                .table(table)
                                .partitionKey(Key.ofText("bookId", bookId))
                                .build()
                );
        if (!book.isPresent()) {
            tx.put(
                    Put.newBuilder()
                            .namespace(namespace)
                            .table(table)
                            .partitionKey(Key.ofText("bookId", bookId))
                            .textValue("bookTitle", bookTitle)
                            .intValue("isAvailable", isAvailable)
                            .textValue("author", author)
                            .textValue("ISBN", isbn)
                            .build()
            );
        }
    }
}
