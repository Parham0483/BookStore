/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dao;

import com.example.model.Order;
import com.example.model.Cart;
import com.example.model.Book;

import java.util.*;

public class OrderDAO {

    private static Map<Integer, Order> orders = new HashMap<>(); // Key: Order ID, Value: Order
    private static int orderCounter = 1;
    private BookDAO bookDAO = new BookDAO();
    private CartDAO cartDAO = new CartDAO();

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public Order getOrderById(int orderId) {
        return orders.get(orderId);
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> customerOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerId() == customerId) {
                customerOrders.add(order);
            }
        }
        return customerOrders;
    }

}
