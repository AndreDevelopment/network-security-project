package auditlog;

public class Main {
    public static void main(String[] args) {
        // Create an instance of AuditLog
        AuditLog auditLog = new AuditLog();

        // Add a sample transaction
        auditLog.addSampleTransaction();

        // Write transactions to the log file
        auditLog.write_to_log();

        // Read transactions from the file
        String fileContent = AuditLog.read_from_file();

        // Print the file content (for testing purposes)
        System.out.println("Contents of the file:");
        System.out.println(fileContent);
    }
}
