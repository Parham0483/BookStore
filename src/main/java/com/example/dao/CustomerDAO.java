/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dao;

import com.example.model.Customer;
import java.util.*;

public class CustomerDAO {

    private static List<Customer> customers = new ArrayList<>();

    static {
        customers.add(new Customer(1, "Alice John", "alice@example.com", "alice123"));
        customers.add(new Customer(2, "Bob Smith", "bob@example.com", "bob123"));
        customers.add(new Customer(3, "Charlie Dave", "charlie@example.com", "charlie123"));
        customers.add(new Customer(4, "David black", "david@example.com", "david123"));
        customers.add(new Customer(5, "Emily Brown", "emily@example.com", "emily123"));
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }

    //Get a customer by their ID
    public Customer getCustomerById(int id) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == id) {
                return customer;
            }
        }
        return null; //Return null if user not found
    }

    //Add a new customer to list
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    //Update an existing customer 
    public void updateCustomer(Customer updatedCustomer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId() == updatedCustomer.getCustomerId()) {
                customers.set(i, updatedCustomer);
                return;
            }
        }
    }

    //Delete a customer by using their ID
    public void deleteCustomer(int id) {
        customers.removeIf(customer -> customer.getCustomerId() == id);
    }

    public int getNextUserId() {
        // Initialize maxUserId with a value lower than any possible userId
        int maxUserId = Integer.MIN_VALUE;

        // Iterate through the list to find the maximum userId
        for (Customer customer : customers) {
            int userId = customer.getCustomerId();
            if (userId > maxUserId) {
                maxUserId = userId;
            }
        }

        // Increment the maximum userId to get the next available userId
        return maxUserId + 1;
    }

}
