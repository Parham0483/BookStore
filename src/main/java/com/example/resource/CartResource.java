/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

import com.example.exception.CartNotFoundException;
import com.example.exception.CustomerNotFoundException;
import com.example.exception.BookNotFoundException;
import com.example.exception.OutOfStockException;

import com.example.dao.CartDAO;
import com.example.dao.BookDAO;
import com.example.dao.CustomerDAO;

import com.example.model.Cart;
import com.example.model.Book;
import com.example.model.Customer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    private CartDAO cartDAO = new CartDAO();
    private BookDAO bookDAO = new BookDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private static final Logger logger = LoggerFactory.getLogger(CartResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<Integer, Cart> getAllCarts() {
        logger.info("GET request for all Carts");
        return (Map<Integer, Cart>) cartDAO.getAllCart();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCartByCustomerId(@PathParam("customerId") int customerId) {
        logger.info("GET request for Cart by Customer ID");
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        Cart cart = cartDAO.getCartByCustomerId(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            logger.error("No cart found or cart is empty for Customer ID: {}", customerId);
            throw new CartNotFoundException("Cart with Customer ID " + customerId + " not found");
        }
        logger.info("Successfully retrieved cart for Customer ID: {}", customerId);
        return Response.ok(cart).build();
    }

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBookToCart(@PathParam("customerId") int customerId, @QueryParam("bookId") int bookId, @QueryParam("quantity") int quantity) {
        logger.info("POST request to add Book ID {} with Quantity {} to Cart for Customer ID: {}", bookId, quantity, customerId);

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            logger.error("Book with ID {} not found.", bookId);
            throw new BookNotFoundException("Book with Book ID " + bookId + " not found");
        }
        // Check if the requested quantity is available in stock
        if (book.getStockQuantity() < quantity) {
            logger.error("Not enough stock for Book ID: {}. Requested Quantity: {}, Available Quantity: {}", bookId, quantity, book.getStockQuantity());
            throw new OutOfStockException("Book with ID " + bookId + " is out of stock. Available quantity: " + book.getStockQuantity());
        }

        cartDAO.addBookToCart(customerId, bookId, quantity);
        logger.info("Successfully added Book ID {} with Quantity {} to Cart for Customer ID: {}", bookId, quantity, customerId);
        return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Book added to cart\"}")
                .build();
    }

    @PUT
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBookQuantity(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId, @QueryParam("quantity") int quantity) {
        logger.info("PUT request to update Book ID {} with new Quantity {} in Cart for Customer ID: {}", bookId, quantity, customerId);

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            logger.error("Book with ID {} not found.", bookId);
            throw new BookNotFoundException("Book with Book ID " + bookId + " not found");
        }
        // Check if the requested quantity is available in stock
        if (book.getStockQuantity() < quantity) {
            logger.error("Not enough stock for Book ID: {}. Requested Quantity: {}, Available Quantity: {}", bookId, quantity, book.getStockQuantity());
            throw new OutOfStockException("Book with ID " + bookId + " is out of stock. Available quantity: " + book.getStockQuantity());
        }

        cartDAO.updateBookQuantity(customerId, bookId, quantity);
        logger.info("Successfully updated Book ID {} with new Quantity {} in Cart for Customer ID: {}", bookId, quantity, customerId);
        return Response.ok("{\"message\":\"Book quantity updated\"}").build();
    }

    @DELETE
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeBookFromCart(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId) {
        logger.info("DELETE request to remove Book ID {} from Cart for Customer ID: {}", bookId, customerId);

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        Cart cart = cartDAO.getCartByCustomerId(customerId);
        if (cart == null || !cart.getItems().containsKey(bookId)) {
            logger.error("Book with ID {} not found in Cart for Customer ID: {}", bookId, customerId);
            throw new CartNotFoundException("Cart with Book ID " + bookId + " not found");
        }

        cartDAO.removeBookFromCart(customerId, bookId);
        logger.info("Successfully removed Book ID {} from Cart for Customer ID: {}", bookId, customerId);

        return Response.ok("{\"message\":\"Book removed from cart\"}").build();
    }

}
