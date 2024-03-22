package auditlog;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class AuditLog {
    // List to store transactions
    private List<Transaction> transactions;

    // Constructor
    public AuditLog() {
        transactions = new ArrayList<>();
    }

    // Method to add a transaction to the list
    public void addTransaction(Date time, int customerID, String action) {
        Transaction transaction = new Transaction(time, customerID, action);
        transactions.add(transaction);
    }

    // Method to write transactions to log file
    public void write_to_log() {
        try {
            FileWriter writer = new FileWriter("auditlog.txt");
            for (Transaction transaction : transactions) {
                writer.write(transaction.getTime() + "," + transaction.getCustomerID() + "," + transaction.getAction()
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
        addTransaction(time, customerID, action);
    }

}
