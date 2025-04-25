/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

import com.example.exception.CustomerNotFoundException;
import com.example.exception.BookNotFoundException;

import com.example.dao.CustomerDAO;
import com.example.model.Customer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private CustomerDAO customerDAO = new CustomerDAO();
    private static final Logger logger = LoggerFactory.getLogger(CustomerResource.class);

    // GET /customers - Retrieve all customers
    @GET
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    // GET /customers/{id} - Retrieve a customer by ID
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") int customerId) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }
        logger.info("Successfully retrieved customers");
        return Response.ok(customer).build();
    }

    // POST /customers - Create a new customer with a unique ID
    @POST
    public Response addCustomer(Customer customer) {
        customer.setCustomerId(customerDAO.getNextUserId()); // Ensure unique ID
        customerDAO.addCustomer(customer);
        logger.info("Added new Customer with ID: {}", customer.getCustomerId());
        return Response.status(Response.Status.CREATED)
                .entity(customer)
                .build();
    }

    // PUT /customers/{id} - Update customer data by ID
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") int customerId, Customer updatedCustomer) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        updatedCustomer.setCustomerId(customerId);
        logger.info("Successfully updated Customer: {}", customerId);
        customerDAO.updateCustomer(updatedCustomer);
        return Response.ok(updatedCustomer).build();
    }

    // DELETE /customers/{id} - Remove customer by ID
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") int customerId) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            throw new CustomerNotFoundException("Cart with Customer ID " + customerId + " not found");
        }

        customerDAO.deleteCustomer(customerId);
        logger.info("Deleted customer with ID: {}", customerId);
        return Response.noContent().build();
    }

}
