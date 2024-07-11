package com.example.test.controllers;

import com.example.test.models.entities.BookReservationEntity;
import com.example.test.scalardbv2.MyLibrary;
import com.scalar.db.exception.transaction.TransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> processReservation(
            @RequestParam("bookId") String bookId,
            Authentication authentication
    ) {
        String accountId = authentication.getName();

        try {
            MyLibrary.reserveBook(bookId, accountId);

            return ResponseEntity.ok("Ok");
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.badRequest().body("Ko");
        }
    }

//    @GetMapping("/return")
//    public String showReturnForm(Model model) {
//        model.addAttribute("bookReturn", new BookReservationEntity());
//        return "return_form";
//    }

    @PostMapping("/returnV2")
    public ResponseEntity<String> processReturn(
            @RequestParam("bookId") String bookId,
            Authentication authentication
    ) {
        String accountId = authentication.getName();

        try {
            MyLibrary.returnBook(bookId, accountId);

            return ResponseEntity.ok("Ok");
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.badRequest().body("Ko");
        }
    }
}