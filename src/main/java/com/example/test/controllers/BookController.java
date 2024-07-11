package com.example.test.controllers;

import com.example.test.models.entities.Book;
import com.example.test.scalardbv2.MyLibrary;
import com.scalar.db.exception.transaction.TransactionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping()
public class BookController {

    @GetMapping("/books")
    public String searchBooks(@RequestParam(name = "name", required = false) String name, Model model) throws TransactionException, IOException {
        // TODO: Implement actual book search logic based on the 'name' parameter
        // For now, we'll return a list of mock books
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Sample Book 1", "John Doe", 1, "1234567890"),
                new Book("2", "Sample Book 2", "Jane Smith", 0, "0987654321"),
                new Book("3", "Sample Book 3", "Mike Johnson", 0, "1122334455")
        );

        List<Book> books = MyLibrary.searchBooks(name);
        model.addAttribute("books", books);
        return "search-results"; // Return the name of the Thymeleaf template for displaying search results
    }
}