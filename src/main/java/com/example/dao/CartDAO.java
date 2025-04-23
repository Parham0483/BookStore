/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dao;

import com.example.model.Cart;
import com.example.model.Customer;
import com.example.model.Book;
import java.util.*;

public class CartDAO {

    private static Map<Integer, Cart> carts = new HashMap<>(); // Key: Customer ID, Value: Cart
    private BookDAO bookDAO = new BookDAO();
    
    public List<Cart> getAllCart() {
        return (List<Cart>) carts;
    }

    public Cart getCartByCustomerId(int customerId) {
        return carts.get(customerId);
    }

    //It checks if the carts map already has a Cart for the given customer ID. If it does, it returns it.
    //If not, it creates a new Cart for that customer, adds it to the map, and then returns it.
    public Cart createCart(Customer customer) {
        return carts.computeIfAbsent(customer.getCustomerId(), id -> new Cart(customer)); // this line of code was coppied from web
    }

    public void addBookToCart(int customerId, int bookId, int quantity) {
        Cart cart = carts.get(customerId);
        if (cart == null) {
            return;
        }

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            return;
        }

        // Add or update the quantity
        cart.getItems().put(bookId, cart.getItems().getOrDefault(bookId, 0) + quantity);
    }

    public void updateBookQuantity(int customerId, int bookId, int quantity) {
        Cart cart = carts.get(customerId);
        if (cart == null) {
            return;
        }

        if (!cart.getItems().containsKey(bookId)) {
            return;
        }

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            return;
        }

        // Update quantity or remove book if quantity is zero
        if (quantity > 0) {
            cart.getItems().put(bookId, quantity);
        } else {
            cart.getItems().remove(bookId);
        }
    }

    public void removeBookFromCart(int customerId, int bookId) {
        Cart cart = carts.get(customerId);
        if (cart == null) {
            return;
        }

        cart.getItems().remove(bookId);
    }

    public void deleteCart(int customerId) {
        carts.remove(customerId);
    }

}
