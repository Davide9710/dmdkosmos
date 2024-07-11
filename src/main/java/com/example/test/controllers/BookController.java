package com.example.test.controllers;

import com.example.test.dto.ReservedBookDTO;
import com.example.test.models.entities.Book;
import com.example.test.scalardb.MyLibrary;
import com.scalar.db.exception.transaction.TransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @GetMapping()
    public String getBooks(@RequestParam(name = "name", required = false) String name,
                           @RequestParam(name = "accountId", required = false) String accountId,
                           @RequestParam(name = "accountName", required = false) String accountName,
                           Model model)
            throws TransactionException, IOException {

        List<Book> books = MyLibrary.searchBooks(name);

        model.addAttribute("books", books);
        model.addAttribute("accountId", accountId);
        model.addAttribute("accountName", accountName);

        return "search-results";
    }

    @PostMapping("/reserve")
    public String processReservation(
            @RequestParam("bookId") String bookId,
            @RequestParam("accountId") String accountId,
            @RequestParam("accountName") String accountName,
            Model model
    ) throws TransactionException, IOException {

        MyLibrary.reserveBook(bookId, accountId);

        List<ReservedBookDTO> reservedBookDTOS = MyLibrary.getReservedBooks(accountId);

        model.addAttribute("accountId", accountId);
        model.addAttribute("bookedBooks", reservedBookDTOS);
        model.addAttribute("accountName", accountName);

        return "index";
    }

    @PostMapping("/return")
    public String processReturn(
            @RequestParam("bookId") String bookId,
            @RequestParam("accountId") String accountId,
            @RequestParam("accountName") String accountName,
            Model model
    ) throws TransactionException, IOException, ParseException {

        MyLibrary.returnBook(bookId, accountId);

        List<ReservedBookDTO> reservedBookDTOS = MyLibrary.getReservedBooks(accountId);

        model.addAttribute("accountId", accountId);
        model.addAttribute("bookedBooks", reservedBookDTOS);
        model.addAttribute("accountName", accountName);

        return "index";
    }
}