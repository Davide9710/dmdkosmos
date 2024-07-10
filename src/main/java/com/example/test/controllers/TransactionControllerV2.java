package com.example.test.controllers;

import com.example.test.models.entities.BookReservationEntity;
import com.example.test.scalardbv2.MyLibrary;
import com.scalar.db.exception.transaction.TransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequiredArgsConstructor
public class TransactionControllerV2 {

//    @GetMapping("/reserve")
//    public String showReserveForm(Model model) {
//        model.addAttribute("bookReservation", new BookReservationEntity());
//        return "reserve_form";
//    }

    @PostMapping("/reserve")
    public String processReservation(
            @RequestParam("bookId") String bookId,
            @ModelAttribute BookReservationEntity bookReservationEntity,
            Authentication authentication,
            Model model
    ) throws TransactionException, IOException {
        String accountId = authentication.getName();

        String transactionId = MyLibrary.reserveBook(bookId, accountId);

        model.addAttribute("transactionId", transactionId);
        model.addAttribute("bookId", bookId);
        model.addAttribute("accountId", accountId);
//        model.addAttribute("bookTitle", mylibrary.getBookTitle(bookId));

        return "reserve_result";
    }

//    @GetMapping("/return")
//    public String showReturnForm(Model model) {
//        model.addAttribute("bookReturn", new BookReservationEntity());
//        return "return_form";
//    }

    @PostMapping("/return")
    public String processReturn(
            @RequestParam("bookId") String bookId,
            @ModelAttribute BookReservationEntity bookReservationEntity,
            Authentication authentication,
            Model model
    ) throws TransactionException, IOException, ParseException {
        String accountId = authentication.getName();

        String result = MyLibrary.returnBook(bookId, accountId);

        model.addAttribute("result", result);
        model.addAttribute("bookId", bookId);
        model.addAttribute("accountId", accountId);
//        model.addAttribute("bookTitle", mylibrary.getBookTitle(bookId));

        return "return_result";
    }
}