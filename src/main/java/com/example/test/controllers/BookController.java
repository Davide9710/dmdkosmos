package com.example.test.controllers;

import com.example.test.models.entities.Book;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping()
public class BookController {

    @GetMapping("/books")
    public String searchBooks(@RequestParam(name = "name", required = false) String name, Model model) {
        // TODO: Implement actual book search logic based on the 'name' parameter
        // For now, we'll return a list of mock books
        List<Book> mockBooks = Arrays.asList(
                new Book(1L, "Sample Book 1", "John Doe", true, "1234567890"),
                new Book(2L, "Sample Book 2", "Jane Smith", false, "0987654321"),
                new Book(3L, "Sample Book 3", "Mike Johnson", true, "1122334455")
        );

        model.addAttribute("books", mockBooks);
        return "search-results"; // Return the name of the Thymeleaf template for displaying search results
    }

    @PostMapping("/books/reserve")
    public String reserveBook(@RequestParam(name = "bookId", required = false) String bookId, Model model) {
        // TODO: Implement actual book search logic based on the 'name' parameter
        // For now, we'll return a list of mock books
        List<Book> mockBooks = Arrays.asList(
                new Book(1L, "Sample Book 1", "John Doe", false, "1234567890"),
                new Book(2L, "Sample Book 2", "Jane Smith", false, "0987654321"),
                new Book(3L, "Sample Book 3", "Mike Johnson", true, "1122334455")
        );

        model.addAttribute("books", mockBooks);
        return "search-results"; // Return the name of the Thymeleaf template for displaying search results
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam(name = "bookId", required = false) String bookId,
                             @RequestParam(name = "accountId", required = false) String accountId,
                             Model model) {


        List<Book> mockBooks = Collections.emptyList();

        model.addAttribute("books", mockBooks);
        model.addAttribute("accountName", "Lucas");

        return "index"; // Return the name of the Thymeleaf template for displaying search results
    }
}