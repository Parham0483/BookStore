/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dao;

import com.example.model.Author; 
import com.example.model.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author parhamgolmohammadi
 */
public class BookDAO {
    private static List<Book> books = new ArrayList<>();
    static {
        AuthorDAO authorDAO = new AuthorDAO();
        List<Author> authors = authorDAO.getAllAuthors();
        
        books.add(new Book(1, "The Catcher in the Rye", authors.get(0), "978-0316769488", 1951, 10.99, 20)); 
        books.add(new Book(2, "To Kill a Mockingbird", authors.get(1), "978-0061120084", 1960, 12.99, 15));
        books.add(new Book(3, "1984", authors.get(2), "978-0451524935", 1949, 14.50, 25));
        books.add(new Book(4, "Pride and Prejudice", authors.get(3), "978-1503290563", 1813, 9.99, 30));
    } 

    //Get all books
    public List<Book> getAllBooks() {
        return books;
    }

    //Get a book by  their ID
    public Book getBookById(int bookId) {
        for (Book book : books) {
            if (book.getBookId() == bookId) {
                return book;
            }
        }
        return null;
    }

    // Add a new book (Auto-incrementing book ID)
    public void addBook(Book book) {
        int newBookId = getNextBookId();
        book.setBookId(newBookId);
        books.add(book);
    }

    //Update an existing book in store
    public void updateBook(Book updatedBook) {
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if (book.getBookId() == updatedBook.getBookId()) {
                books.set(i, updatedBook);
                System.out.println("Book updated: " + updatedBook.getTitle());
                return;
            }
        }
    }

    //Delete a book using their ID
    public void deleteBook(int id) {
        books.removeIf(book -> book.getBookId() == id);
    }

    // Ensures each new book gets a unique ID
    public int getNextBookId() {
        int maxBookId = 0;
        for (Book book : books) {
            if (book.getBookId() > maxBookId) {
                maxBookId = book.getBookId();
            }
        }
        return maxBookId + 1;
    }
}



