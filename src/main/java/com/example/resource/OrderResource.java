/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

import com.example.exception.BookNotFoundException;
import com.example.exception.CustomerNotFoundException;
import com.example.exception.OutOfStockException;
import com.example.exception.CartNotFoundException;

import com.example.dao.BookDAO;
import com.example.dao.OrderDAO;
import com.example.dao.CartDAO;
import com.example.dao.CustomerDAO;
import com.example.model.Order;
import com.example.model.Cart;
import com.example.model.Customer;
import com.example.model.Book;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private static final Logger logger = LoggerFactory.getLogger(OrderResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrdersByCustomerId(@PathParam("customerId") int customerId) {
        logger.info("GET request for Order by Customer ID");
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        List<Order> orders = orderDAO.getOrdersByCustomerId(customerId);
        if (orders.isEmpty()) {
            logger.warn("No orders found for Customer with ID {}.", customerId);  // Warn if no orders are found
            throw new CustomerNotFoundException("No orders found for Customer ID " + customerId);
        }
        logger.info("Successfully retrieved {} orders for Customer ID: {}", orders.size(), customerId);

        return Response.ok(orders).build();
    }

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("customerId") int customerId, @PathParam("orderId") int orderId) {
        logger.info("GET request for Order with Order ID: {} for Customer ID: {}", orderId, customerId);
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            logger.error("Order with ID {} not found for Customer ID: {}", orderId, customerId); // Log error if order is not found
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Order not found\"}")
                    .build();
        }

        // Check if the order belongs to the given customer
        if (order.getCustomerId() != customerId) {
            logger.error("Order with ID {} does not belong to Customer ID: {}", orderId, customerId); // Log error if order does not belong to the customer
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Order does not belong to this customer\"}")
                    .build();
        }
        logger.info("Successfully retrieved Order with ID: {} for Customer ID: {}", orderId, customerId); // Log success when order is found

        return Response.ok(order).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(@PathParam("customerId") int customerId) {
        logger.info("POST request to create an order for Customer ID: {}", customerId);
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");

        }

        Cart cart = cartDAO.getCartByCustomerId(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            logger.error("Cart is empty or does not exist for Customer ID: {}", customerId);
            throw new CartNotFoundException("Cart not found");
        }
        logger.info("Customer ID {} has a valid cart with {} items.", customerId, cart.getItems().size());

        double totalPrice = 0;
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = new BookDAO().getBookById(entry.getKey());
            if (book != null) {

                // Check if there's enough stock for the requested quantity
                if (book.getStockQuantity() < entry.getValue()) {
                    logger.error("Not enough stock for Book ID: {}. Requested Quantity: {}, Available Quantity: {}",
                            entry.getKey(), entry.getValue(), book.getStockQuantity());
                    throw new OutOfStockException("Book with ID " + entry.getKey() + " is out of stock");
                }

                totalPrice += book.getPrice() * entry.getValue();
                logger.debug("Book ID: {}, Quantity: {}, Price: {}, Total Price so far: {}", entry.getKey(), entry.getValue(), book.getPrice(), totalPrice);
            } else {
                logger.warn("Book with ID {} not found in the cart.", entry.getKey());
            }
        }
        logger.info("Total price for the order: {}", totalPrice);

        // Create order
        Order newOrder = new Order(orderDAO.getAllOrders().size() + 1, customerId, cart.getItems(), totalPrice);
        orderDAO.getAllOrders().add(newOrder);

        logger.info("Order with ID {} created for Customer ID: {}", newOrder.getOrderId(), customerId);
        // Clear cart after purchase
        cartDAO.deleteCart(customerId);

        logger.info("Cart for Customer ID {} cleared after order creation.", customerId);

        return Response.status(Response.Status.CREATED)
                .entity(newOrder)
                .build();
    }
}
