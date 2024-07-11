package com.example.test.models.entities;

import lombok.Data;

@Data
public class Book {
    private String id;
    private String title;
    private String author;
    private int isAvailable;
    private String isbn;

    public Book(String id, String title, String author, int isAvailable, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
        this.isbn = isbn;
    }
}
