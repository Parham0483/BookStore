package com.example.resource;

import com.example.exception.BookNotFoundException;
import com.example.dao.BookDAO;
import com.example.model.Book;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    private BookDAO bookDAO = new BookDAO();
    private static final Logger logger = LoggerFactory.getLogger(BookResource.class);

    // GET all books 
    @GET
    public List<Book> getAllBooks() {
        logger.info("GET request for all Books");
        return bookDAO.getAllBooks();
    }

    //  GET single book by ID
    @GET
    @Path("/{id}")
    public Book getBookById(@PathParam("id") int bookId) {
        logger.info("GET request for book with ID: {}", bookId);
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            logger.error("Book with ID {} not found.", bookId);
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }
        logger.info("Book with ID {} found: {}", bookId, book);
        return book;
    }

    //  Ensure client sends a complete Book object — especially the nested Author field

    //  POST new book 
    @POST
    public Response addBook(Book book) {
        bookDAO.addBook(book);
        logger.info("Added new Book with ID: {}", book.getBookId());
        return Response.status(Response.Status.CREATED)
                .entity(book)
                .build();
    }

     // PUT update book by ID
    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") int bookId, Book updatedBook) {
        logger.info("PUT request to update book with ID: {}", bookId);
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            logger.error("Book with ID {} not found for update.", bookId);
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }

        updatedBook.setBookId(bookId);
        logger.info("Updated book with ID: {}", bookId);
        bookDAO.updateBook(updatedBook);
        return Response.ok(updatedBook).build();
    }

    // DELETE book by ID
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") int bookId) {
        logger.info("DELETE request for book with ID: {}", bookId);
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            logger.error("Book with ID {} not found for deletion.", bookId);
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }

        bookDAO.deleteBook(bookId);
        logger.info("Deleted book with ID: {}", bookId);
        return Response.noContent().build();
    }
    
    
}

