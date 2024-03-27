package main;

import java.io.Serializable;

public class Customer implements Serializable {
    // variables
    private int customerID;
    private String username;
    private String password;

    private double bankBalance;

    // Constructor
    public Customer(int customerID, String username, String password,double bankBalance) {
        this.customerID = customerID;
        this.username = username;
        this.password = password;
        this.bankBalance=bankBalance;
    }

    // Getters and setters
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(double bankBalance) {
        this.bankBalance = bankBalance;
    }
}
