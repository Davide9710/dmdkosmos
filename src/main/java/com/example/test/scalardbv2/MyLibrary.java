package com.example.test.scalardbv2;

import com.example.test.dto.ReservedBookDTO;
import com.example.test.models.entities.Book;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MyLibrary {

    public static void reserveBook(String bookId, String accountId) throws TransactionException, IOException {
        Path configFilePath = Paths.get("src/main/resources/scalardb.properties");
        TransactionFactory transactionFactory = TransactionFactory.create(configFilePath);
        DistributedTransactionManager transactionManager = transactionFactory.getTransactionManager();
        DistributedTransaction tx = transactionManager.start();

        try {
            String namespace = null;

            // Check in which namespace the book exists
            for (String ns : new String[]{"mysqllibrary", "postgrelibrary"}) {
                Get bookGet = Get.newBuilder()
                        .namespace(ns)
                        .table("book")
                        .partitionKey(Key.ofText("bookId", bookId))
                        .build();
                Optional<Result> bookResult = tx.get(bookGet);
                if (bookResult.isPresent()) {
                    namespace = ns;
                    break;
                }
            }

            if (namespace == null) {
                throw new RuntimeException("Book not found in any namespace");
            }

            // Check if the book is available
            Get bookGet = Get.newBuilder()
                    .namespace(namespace)
                    .table("book")
                    .partitionKey(Key.ofText("bookId", bookId))
                    .build();
            Optional<Result> bookResult = tx.get(bookGet);

            if (bookResult.isEmpty() || bookResult.get().getInt("isAvailable") == 0) {
                throw new RuntimeException("Book is not available for reservation");
            }

            // Update book availability
            Put bookPut = Put.newBuilder()
                    .namespace(namespace)
                    .table("book")
                    .partitionKey(Key.ofText("bookId", bookId))
                    .intValue("isAvailable", 0)
                    .build();
            tx.put(bookPut);

            // Generate transaction ID and dates
            String transactionId = bookId + '/' + accountId + '/' + System.currentTimeMillis();
            String reservationDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

            // Calculate due date (14 days from now) in "yyyyMMdd" format
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 14);
            String dueDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

            // Create transaction record
            Put transactionPut = Put.newBuilder()
                    .namespace(namespace)
                    .table("transaction")
                    .partitionKey(Key.ofText("transactionId", transactionId))
                    .textValue("bookId", bookId)
                    .textValue("accountId", accountId)
                    .textValue("reservationDate", reservationDate)
                    .textValue("dueDate", dueDate)
                    .textValue("returnDate", "")
                    .textValue("status", "RESERVED")
                    .build();
            tx.put(transactionPut);

            // Commit the transaction
            tx.commit();

        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    public static void returnBook(String bookId, String accountId) throws TransactionException, IOException, ParseException {
        Path configFilePath = Paths.get("src/main/resources/scalardb.properties");
        TransactionFactory transactionFactory = TransactionFactory.create(configFilePath);
        DistributedTransactionManager transactionManager = transactionFactory.getTransactionManager();
        DistributedTransaction tx = transactionManager.start();

        try {
            String namespace = null;

            // Check in which namespace the book exists
            for (String ns : new String[]{"mysqllibrary", "postgrelibrary"}) {
                Get bookGet = Get.newBuilder()
                        .namespace(ns)
                        .table("book")
                        .partitionKey(Key.ofText("bookId", bookId))
                        .build();
                Optional<Result> bookResult = tx.get(bookGet);
                if (bookResult.isPresent()) {
                    namespace = ns;
                    break;
                }
            }

            if (namespace == null) {
                throw new RuntimeException("Book not found in any namespace");
            }

            // Check if the book is currently unavailable (i.e., it's borrowed)
            Get bookGet = Get.newBuilder()
                    .namespace(namespace)
                    .table("book")
                    .partitionKey(Key.ofText("bookId", bookId))
                    .build();
            Optional<Result> bookResult = tx.get(bookGet);

            if (bookResult.isEmpty() || bookResult.get().getInt("isAvailable") == 1) {
                throw new RuntimeException("Book is not currently borrowed");
            }

            // Find the latest transaction for this book
            Scan transactionScan = Scan.newBuilder()
                    .namespace(namespace)
                    .table("transaction")
                    .all()
                    .build();
            List<Result> transactions = tx.scan(transactionScan);

            Optional<Result> latestTransaction = transactions.stream()
                    .filter(t -> t.getText("bookId").equals(bookId) && t.getText("status").equals("RESERVED"))
                    .max(Comparator.comparing(t -> t.getText("reservationDate")));

            if (latestTransaction.isEmpty()) {
                throw new RuntimeException("No active reservation found for this book");
            }

            Result transaction = latestTransaction.get();
            if (!transaction.getText("accountId").equals(accountId)) {
                throw new RuntimeException("This book was not reserved by the provided account ID");
            }

            // Update book availability
            Put bookPut = Put.newBuilder()
                    .namespace(namespace)
                    .table("book")
                    .partitionKey(Key.ofText("bookId", bookId))
                    .intValue("isAvailable", 1)
                    .build();
            tx.put(bookPut);

            // Calculate return date and check for late return
            String returnDate = new SimpleDateFormat("ddMMyyyy").format(new Date());
            String dueDate = transaction.getText("dueDate");
            String message = "";

            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            Date dueDateTime = sdf.parse(dueDate);
            Date returnDateTime = sdf.parse(returnDate);

            if (returnDateTime.after(dueDateTime)) {
                long diffInMillies = Math.abs(returnDateTime.getTime() - dueDateTime.getTime());
                long daysLate = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                int lateFee = (int) (daysLate * 30);
                message = "Late, this is the calculated fee: " + lateFee + " yen";
            }

            // Update transaction record
            Put transactionPut = Put.newBuilder()
                    .namespace(namespace)
                    .table("transaction")
                    .partitionKey(Key.ofText("transactionId", transaction.getText("transactionId")))
                    .textValue("returnDate", returnDate)
                    .textValue("status", "RETURNED")
                    .build();
            tx.put(transactionPut);

            // Commit the transaction
            tx.commit();

        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    public static List<Book> searchBooks(String searchText) throws TransactionException, IOException {
        Path configFilePath = Paths.get("src/main/resources/scalardb.properties");
        TransactionFactory transactionFactory = TransactionFactory.create(configFilePath);
        DistributedTransactionManager transactionManager = transactionFactory.getTransactionManager();
        DistributedTransaction tx = transactionManager.start();

        List<Book> results = new ArrayList<>();

        try {
            // Search in both namespaces
            for (String namespace : new String[]{"mysqllibrary", "postgrelibrary"}) {
                Scan scan = Scan.newBuilder()
                        .namespace(namespace)
                        .table("book")
                        .all()
                        .build();

                List<Result> scanResults = tx.scan(scan);

                for (Result result : scanResults) {
                    String name = result.getText("bookTitle");
                    String author = result.getText("author");
                    if (name.toLowerCase().contains(searchText.toLowerCase()) ||
                            author.toLowerCase().contains(searchText.toLowerCase())) {
                        Book book = new Book(
                                result.getText("bookId"),
                                name,
                                author,
                                result.getInt("isAvailable"),
                                result.getText("isbn")
                        );
                        results.add(book);
                    }
                }
            }

            tx.commit();
            return results;
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    public static String getAccountId(String accountName) throws TransactionException, IOException {
        Path configFilePath = Paths.get("src/main/resources/scalardb.properties");
        TransactionFactory transactionFactory = TransactionFactory.create(configFilePath);
        DistributedTransactionManager transactionManager = transactionFactory.getTransactionManager();
        DistributedTransaction tx = transactionManager.start();
        String NAMESPACE = "mysqllibrary";

        try {
            // First, scan the table to find the account by name
            Scan scan = Scan.newBuilder()
                    .namespace(NAMESPACE)
                    .table("account")
                    .all()
                    .build();

            List<Result> results = tx.scan(scan);

            String accountId = null;
            for (Result res : results) {
                if (accountName.equals(res.getText("accountName"))) {
                    accountId = res.getText("accountId");
                    break;
                }
            }

            if (accountId == null) {
                throw new RuntimeException("The name is invalid");
            }

            // Now perform the Get operation using the correct partition key
            Get get = Get.newBuilder()
                    .namespace(NAMESPACE)
                    .table("account")
                    .partitionKey(Key.ofText("accountId", accountId))
                    .build();
            Optional<Result> result = tx.get(get);

            if (!result.isPresent()) {
                throw new RuntimeException("Account not found");
            }

            // Commit the transaction
            tx.commit();

            return accountId;
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    public static List<ReservedBookDTO> getReservedBooks(String accountId) throws TransactionException, IOException {
        Path configFilePath = Paths.get("src/main/resources/scalardb.properties");
        TransactionFactory transactionFactory = TransactionFactory.create(configFilePath);
        DistributedTransactionManager transactionManager = transactionFactory.getTransactionManager();
        DistributedTransaction tx = transactionManager.start();

        List<ReservedBookDTO> reservedBooks = new ArrayList<>();

        // 1. Get all transactions for the given accountId
        Scan transactionScan = Scan.newBuilder()
                .namespace("mysqllibrary")
                .table("transaction")
                .all()
                .build();
        List<Result> transactions = tx.scan(transactionScan);

        // 2. Filter and group transactions by bookId
        Map<String, Result> latestTransactions = new HashMap<>();
        for (Result transaction : transactions) {
            String transactionAccountId = transaction.getText("accountId");
            String bookId = transaction.getText("bookId");
            String status = transaction.getText("status");

            if (transactionAccountId.equals(accountId) && status.equals("RESERVED")) {
                latestTransactions.put(bookId, transaction);
            }
        }

        // 3. Fetch book details for reserved books
        for (Map.Entry<String, Result> entry : latestTransactions.entrySet()) {
            String bookId = entry.getKey();
            Result transaction = entry.getValue();

            Get bookGet = Get.newBuilder()
                    .namespace("mysqllibrary")
                    .table("book")
                    .partitionKey(Key.ofText("bookId", bookId))
                    .build();
            Optional<Result> bookResult = tx.get(bookGet);

            if (bookResult.isPresent()) {
                Result book = bookResult.get();
                ReservedBookDTO dto = new ReservedBookDTO(
                        book.getText("bookTitle"),
                        book.getText("author"),
                        book.getText("isbn"),
                        bookId,
                        transaction.getText("dueDate"));

                reservedBooks.add(dto);
            }
        }

        //Same for PostgreSQL
        transactionScan = Scan.newBuilder()
                .namespace("postgrelibrary")
                .table("transaction")
                .all()
                .build();
        transactions = tx.scan(transactionScan);

        // 2. Filter and group transactions by bookId
        for (Result transaction : transactions) {
            String transactionAccountId = transaction.getText("accountId");
            String bookId = transaction.getText("bookId");
            String status = transaction.getText("status");

            if (transactionAccountId.equals(accountId) && status.equals("RESERVED")) {
                latestTransactions.put(bookId, transaction);
            }
        }

        // 3. Fetch book details for reserved books
        for (Map.Entry<String, Result> entry : latestTransactions.entrySet()) {
            String bookId = entry.getKey();
            Result transaction = entry.getValue();

            Get bookGet = Get.newBuilder()
                    .namespace("postgrelibrary")
                    .table("book")
                    .partitionKey(Key.ofText("bookId", bookId))
                    .build();
            Optional<Result> bookResult = tx.get(bookGet);

            if (bookResult.isPresent()) {
                Result book = bookResult.get();
                ReservedBookDTO dto = new ReservedBookDTO(
                        book.getText("bookTitle"),
                        book.getText("author"),
                        book.getText("isbn"),
                        bookId,
                        transaction.getText("dueDate"));

                reservedBooks.add(dto);
            }
        }

        return reservedBooks;
    }
}
