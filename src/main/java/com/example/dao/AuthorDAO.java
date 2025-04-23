/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dao;

import com.example.model.Author;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author parhamgolmohammadi
 */
public class AuthorDAO {    
    
    private static List<Author> authors = new ArrayList<>();

    static {
        authors.add(new Author(1, "J.D. Salinger", "American writer known for 'The Catcher in the Rye'."));
        authors.add(new Author(2, "Harper Lee", "Author of 'To Kill a Mockingbird', Pulitzer Prize winner."));
        authors.add(new Author(3, "George Orwell", "British writer famous for '1984' and 'Animal Farm'."));
        authors.add(new Author(4, "Jane Austen", "Renowned English novelist, best known for 'Pride and Prejudice'."));
    }

    // Method GET all authors
    public static List<Author> getAllAuthors() {
        return authors;
    }

    // Method to get an author by ID
    public static Author getAuthorById(int authorId) {
        for (Author author : authors) {
            if (author.getAuthorId() == authorId) {
                return author;
            }
        }
        return null; // Return null if not found
    }
    // Add a new author
    public void addAuthor(Author author) {
        authors.add(author);
    }

    // Update an existing author
    public void updateAuthor(Author updatedAuthor) {
        for (int i = 0; i < authors.size(); i++) {
            if (authors.get(i).getAuthorId() == updatedAuthor.getAuthorId()) {
                authors.set(i, updatedAuthor);
                return;
            }
        }
    }

    // Delete an author by ID
    public void deleteAuthor(int id) {
        authors.removeIf(author -> author.getAuthorId() == id);
    }
}
