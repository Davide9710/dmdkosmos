package com.example.test.controllers;

import com.example.test.dto.ReservedBookDTO;
import com.example.test.models.entities.Book;
import com.example.test.scalardb.MyBank;
import com.scalar.db.exception.transaction.TransactionException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping()
public class IndexController {
    @GetMapping()
    public String index(Authentication authentication,  Model model) throws IOException, TransactionException {
        boolean secondTime = false;
        String accountId = authentication.getName();
        String table = "";
        if (accountId.charAt(0) == 'm') {
            table = "mysql";
        } else {
            table = "postgres";
        }
        /*String accountName = MyBank.getAccountName(table, accountId);
        int balance = MyBank.getBalance(table, accountId);*/
        String accountName = "Lucas";

        List<Book> bookedBooks = Collections.emptyList();
        if(secondTime == true) {
            bookedBooks = Arrays.asList(
                    new Book(1L, "Sample Book 1", "John Doe", false, "1234567890")
            );
        }

        secondTime = true; // next time

        model.addAttribute("books", bookedBooks);
        model.addAttribute("accountId", accountId);
        model.addAttribute("accountName", accountName);
        model.addAttribute("bookedBooks", bookedBooks);
//        model.addAttribute("balance", balance);

        return "index";
    }

    @GetMapping("/reserved-books")
    public String reservedBooks(Authentication authentication,  Model model) throws IOException, TransactionException {
        String accountId = authentication.getName();
        String table = "";
        if (accountId.charAt(0) == 'm') {
            table = "mysql";
        } else {
            table = "postgres";
        }
        /*String accountName = MyBank.getAccountName(table, accountId);
        int balance = MyBank.getBalance(table, accountId);*/
        String accountName = "Lucas";

        List<ReservedBookDTO> bookedBooks;
        bookedBooks = Arrays.asList(
                new ReservedBookDTO("1", "Sample Book 1", "John Doe", "25/07/2024")
        );

        model.addAttribute("books", bookedBooks);
        model.addAttribute("accountId", accountId);
        model.addAttribute("accountName", accountName);
        model.addAttribute("bookedBooks", bookedBooks);
//        model.addAttribute("balance", balance);

        return "index";
    }
}