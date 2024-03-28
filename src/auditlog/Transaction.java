package auditlog;

import java.util.Date;

public class Transaction {
    // variables
    private Date time;
    private int customerID;
    private String action;

    private boolean status;

    // Constructor
    public Transaction(Date time, int customerID, String action,boolean status) {
        this.time = time;
        this.customerID = customerID;
        this.action = action;
        this.status=status;
    }

    // Getters and setters

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

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
    @Override
    public String toString() {
        return time.toString() + "," + customerID + "," + action + "," + status;
    }
}
