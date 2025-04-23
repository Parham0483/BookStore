/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.model;


import java.util.HashMap;
import java.util.Map;

public class Cart {
    private int id;
    private Customer customer ;
    private Map<Integer, Integer> items;// key:Book ID, Value: Quantity

    public Cart() {
    }

    
    public Cart(Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Integer, Integer> items) {
        this.items = items;
    }
    
}
