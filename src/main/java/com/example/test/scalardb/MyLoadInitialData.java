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
            // Accounts (already provided)
            putAccount(tx, "mysqllibrary", "account", "1", "davide");
            putAccount(tx, "mysqllibrary", "account", "2", "daichi");
            putAccount(tx, "mysqllibrary", "account", "3", "masaya");

            // Books
            putBook(tx, "mysqllibrary", "book", "1", "1984", 0, "George Orwell", "1234567890");
            putBook(tx, "postgrelibrary", "book", "2", "To Kill a Mockingbird", 1, "Harper Lee", "0987654321");
            putBook(tx, "mysqllibrary", "book", "3", "The Great Gatsby", 0, "F. Scott Fitzgerald", "1122334455");
            putBook(tx, "postgrelibrary", "book", "4", "Pride and Prejudice", 0, "Jane Austen", "2233445566");
            putBook(tx, "mysqllibrary", "book", "5", "The Catcher in the Rye", 0, "J.D. Salinger", "3344556677");
            putBook(tx, "postgrelibrary", "book", "6", "One Hundred Years of Solitude", 1, "Gabriel García Márquez", "4455667788");
            putBook(tx, "postgrelibrary", "book", "7", "Two Hundred Years of Solitude", 1, "Gabriel García Márquez", "4455667789");

            // Transactions
            // For davide (accountId: 1)
            putTransaction(tx, "mysqllibrary", "transaction", "1", "1", "1", "20240711", "20240725", null, "RESERVED");
            putTransaction(tx, "postgrelibrary", "transaction", "2", "2", "1", "20240712", "20240726", "20240702", "RETURNED");
            putTransaction(tx, "mysqllibrary", "transaction", "3", "3", "1", "20240713", "20240727", null, "RESERVED");

            // For daichi (accountId: 2)
            putTransaction(tx, "postgrelibrary", "transaction", "4", "4", "2", "20240714", "20240728", null, "RESERVED");
            putTransaction(tx, "mysqllibrary", "transaction", "5", "5", "2", "20240715", "20240729", null, "RESERVED");
            putTransaction(tx, "postgrelibrary", "transaction", "6", "6", "2", "20240716", "20240730", "20240705", "RETURNED");

            // For masaya (accountId: 3)
            putTransaction(tx, "postgrelibrary", "transaction", "7", "2", "3", "20240717", "20240731", "20240725", "RETURNED");

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
                            .textValue("isbn", isbn)
                            .build()
            );
        }
    }
}
