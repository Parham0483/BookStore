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

/**
 * Resource class for managing customer orders.
 * Supports retrieving all orders, retrieving an order by ID,
 * and creating a new order from the customer's cart.
 */
@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderDAO orderDAO = new OrderDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private static final Logger logger = LoggerFactory.getLogger(OrderResource.class);

    // GET /customers/{customerId}/orders
    @GET
    public Response getOrdersByCustomerId(@PathParam("customerId") int customerId) {
        logger.info("GET request for orders by Customer ID: {}", customerId);
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }

        List<Order> orders = orderDAO.getOrdersByCustomerId(customerId);
        if (orders.isEmpty()) {
            logger.warn("No orders found for Customer ID: {}", customerId);
            throw new CustomerNotFoundException("No orders found for Customer ID " + customerId);
        }

        logger.info("Retrieved {} orders for Customer ID: {}", orders.size(), customerId);
        return Response.ok(orders).build();
    }

    // GET /customers/{customerId}/orders/{orderId}
    @GET
    @Path("/{orderId}")
    public Response getOrderById(@PathParam("customerId") int customerId, @PathParam("orderId") int orderId) {
        logger.info("GET request for Order ID {} by Customer ID: {}", orderId, customerId);
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }

        Order order = orderDAO.getOrderById(orderId);
        if (order == null || order.getCustomerId() != customerId) {
            logger.error("Order not found or does not belong to Customer ID: {}", customerId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Order not found or does not belong to this customer\"}")
                    .build();
        }

        logger.info("Order ID {} retrieved for Customer ID: {}", orderId, customerId);
        return Response.ok(order).build();
    }

    // POST /customers/{customerId}/orders
    @POST
    public Response createOrder(@PathParam("customerId") int customerId) {
        logger.info("POST request to create order for Customer ID: {}", customerId);
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }

        Cart cart = cartDAO.getCartByCustomerId(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            logger.error("Cart is empty or does not exist for Customer ID: {}", customerId);
            throw new CartNotFoundException("Cart is empty or does not exist");
        }

        logger.info("Valid cart found with {} items for Customer ID: {}", cart.getItems().size(), customerId);

        double totalPrice = 0;
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookById(entry.getKey());
            if (book == null) {
                logger.warn("Book with ID {} not found, skipping...", entry.getKey());
                continue;
            }

            int quantity = entry.getValue();
            if (book.getStockQuantity() < quantity) {
                logger.error("Insufficient stock for Book ID: {}. Requested: {}, Available: {}",
                        book.getBookId(), quantity, book.getStockQuantity());
                throw new OutOfStockException("Book ID " + book.getBookId() + " is out of stock");
            }

            totalPrice += book.getPrice() * quantity;
            logger.debug("Item processed - Book ID: {}, Quantity: {}, Subtotal: {}", book.getBookId(), quantity, book.getPrice() * quantity);
        }

        logger.info("Total order price for Customer ID {}: ${}", customerId, totalPrice);

        Order newOrder = orderDAO.createOrder(customerId, cart.getItems(), totalPrice);
        logger.info("Order created with ID: {} for Customer ID: {}", newOrder.getOrderId(), customerId);

        cartDAO.deleteCart(customerId);  // Clear cart after order
        logger.info("Cart cleared for Customer ID: {}", customerId);

        return Response.status(Response.Status.CREATED).entity(newOrder).build();
    }
}
