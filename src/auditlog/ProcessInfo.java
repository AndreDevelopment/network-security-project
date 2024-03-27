package auditlog;

import main.Customer;

import java.io.Serializable;

public class ProcessInfo implements Serializable {

    private Customer customer;
    double amount;

    public ProcessInfo(Customer customer, double amount) {
        this.customer = customer;
        this.amount = amount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
