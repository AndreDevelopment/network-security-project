package auditlog;

import java.util.Date;

public class Transaction {
    // variables
    private Date time;
    private int customerID;
    private String action;

    // Constructor
    public Transaction(Date time, int customerID, String action) {
        this.time = time;
        this.customerID = customerID;
        this.action = action;
    }

    // Getters and setters
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
