/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

import com.example.exception.AuthorNotFoundException;
import com.example.dao.AuthorDAO;
import com.example.dao.BookDAO;
import com.example.model.Author;
import com.example.model.Book;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    private AuthorDAO authorDAO = new AuthorDAO();
    private BookDAO bookDAO = new BookDAO();
    private static final Logger logger = LoggerFactory.getLogger(BookResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Author> getAllAuthors() {
        logger.info("GET request for all Authors");
        return authorDAO.getAllAuthors();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorById(@PathParam("id") int authorId) {
        logger.info("GET request for Author with ID: {}", authorId);

        Author author = authorDAO.getAuthorById(authorId);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + authorId + " not found");
        }
        return Response.ok(author).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAuthor(Author author) {
        logger.info("Added new Author with ID: {}", author.getAuthorId());
        authorDAO.addAuthor(author);
        return Response.status(Response.Status.CREATED)
                .entity(author)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAuthor(@PathParam("id") int authorId, Author updatedAuthor) {
        logger.info("PUT request to update Author with ID: {}", authorId);
        Author author = authorDAO.getAuthorById(authorId);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + authorId + " not found");
        }

        updatedAuthor.setAuthorId(authorId);
        logger.info("Updated AUTHOR with ID: {}", authorId);
        authorDAO.updateAuthor(updatedAuthor); // No return value, so just execute
        return Response.ok(updatedAuthor).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") int authorId) {
        Author author = authorDAO.getAuthorById(authorId);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + authorId + " not found");
        }

        authorDAO.deleteAuthor(authorId);
        logger.info("DELETE request for author with ID: {}", authorId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/books")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByAuthor(@PathParam("id") int authorId) {
        logger.info("GET request for books for author with ID: {}", authorId);

        // Filter books by authorId
        List<Book> books = bookDAO.getAllBooks()
                .stream()
                .filter(book -> book.getAuthor() != null && book.getAuthor().getAuthorId() == authorId)
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            logger.info("No books found for author with ID: {}", authorId);
            throw new AuthorNotFoundException("No books found for author with ID " + authorId);
        } else {
            return Response.ok(books).build();  // Return the list of books for the author
        }
    }
}
