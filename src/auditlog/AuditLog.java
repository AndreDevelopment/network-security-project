package auditlog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuditLog {
    // List to store transactions
    private List<Transaction> transactions;

    // Constructor
    public AuditLog() {
        transactions = new ArrayList<>();
    }

    // Method to add a transaction to the list
    public void addTransaction(Transaction t) {

        transactions.add(t);
        for (Transaction transaction : transactions) {
            System.out.println("Audit Log: \n" + transaction.getTime() + "," + transaction.getCustomerID()
                    + "," + transaction.getAction() +","+transaction.isStatus()
                    + "\n");
        }
        //write_to_log();
    }
    public void addEncryptTransaction(String s){
        try {
            FileWriter writer = new FileWriter("auditlog.txt");
            //String stringToWrite = "your string value";

            writer.write(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to write transactions to log file
    public void write_to_log() {
        try {
            FileWriter writer = new FileWriter("auditlog.txt");
            for (Transaction transaction : transactions) {
                writer.write(transaction.getTime() + "," + transaction.getCustomerID()
                        + "," + transaction.getAction() +","+transaction.isStatus()
                        + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to read transactions from file
    public static String read_from_file() {
        StringBuilder fileContent = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("auditlog.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    // Method to add a sample transaction to the list for testing
    public void addSampleTransaction() {
        // You can create a sample transaction with some arbitrary values for testing
        Date time = new Date(); // Current date and time
        int customerID = 123456;
        String action = "Sample Action";

        // Add the sample transaction to the list
        //addTransaction(time, customerID, action,true);
    }

}
