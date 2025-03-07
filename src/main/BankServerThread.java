package main;


import auditlog.AuditLog;
import auditlog.ProcessInfo;
import auditlog.Transaction;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BankServerThread extends Thread {

    private final Socket clientSocket;

    private SecretKey oldSharedKey;
    private SecretKey newMasterKey,msgEncryptionKey,macKey;

    static List<Customer> customerList;

    private static AuditLog auditLog;


    public BankServerThread(Socket socket) {
        clientSocket = socket;
        oldSharedKey = KeyCipher.createSecretKey("thisismysecretkey24bytes");
        customerList = new ArrayList<>();
        //Dummy values for our list
        customerList.add(new Customer(1234,"Andre","password1",700));
        customerList.add(new Customer(4567,"Arshroop","ILoveAndre",100000));
        auditLog = new AuditLog();

    }

    @Override
    public void run() {


        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {

            Object inputLine;
            //Need to assess the flow of GUI

            authenticateBankToATM(out,in);
            createBothKeys();
            while ((inputLine = in.readObject()) != null) {

                switch ((String)inputLine){
                    case "R":
                        registerCustomer(in,out);
                        break;
                    case "L":
                        authenticateCustomer(in,out);
                        break;
                    case "W":
                        withdrawal(in,out);
                        break;
                    case "D":
                        deposit(in,out);
                        break;
                    case "C":
                        checkBalance(in,out);
                        break;
                    default:
                        System.out.println("Wrong call!");
                }

            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }// end of main

    private void authenticateCustomer(ObjectInputStream in,ObjectOutputStream out)  {

        /*
        * parts 0 -> encrypted message
        * parts 1 -> MAC code
        * */
        try {
            Object inputLine;
            //Isolate into userVerification method
            //Username & Password received
            if ((inputLine = in.readObject()) != null) {

                System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM ATM: "+Colour.ANSI_RESET);
                String[] parts = ((String) inputLine).split(",");

                System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+parts[0]);
                System.out.println(Colour.ANSI_PURPLE+"->[MAC]: "+Colour.ANSI_RESET+parts[1]);
                //Decrypt message
                String decryptMessage = KeyCipher.decrypt(msgEncryptionKey,parts[0]);
                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+Colour.ANSI_RESET+decryptMessage);
                //Verify MAC
                KeyCipher.extendedVerifyMAC(decryptMessage,parts[1],macKey);

                //Adding the customer to our List
                String[] userPass = decryptMessage.split(",");


                //Returning customer if they exist& verified, if not return "Bad" string
                if (verifyUser(userPass[0], userPass[1])) {
                    System.out.println(Colour.ANSI_GREEN + "\nUser is verified :)" + Colour.ANSI_RESET);
                    out.writeObject(findCustomer(userPass[0]));

                } else {
                    System.out.println(Colour.ANSI_RED + "\nUser was not verified :(" + Colour.ANSI_RESET);
                    out.writeObject("Bad");
                }




            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }//end of Customer authentication

    private  void registerCustomer(ObjectInputStream in, ObjectOutputStream out)  {
        Object inputLine,outputLine;

        try {
            if ((inputLine = in.readObject()) != null) {
                String decryptMessage = getMessage((String) inputLine);

                //Adding the customer to our List
                String[] userPass = decryptMessage.split(",");
                customerList.add(new Customer(Customer.generateCustomerID(),userPass[0],userPass[1],0));

                //Send a confirmation message
                String message = "User has been registered!";
                outputLine = KeyCipher.encrypt(msgEncryptionKey,message)+","+KeyCipher.createMAC(message,macKey);
                out.writeObject(outputLine);
                System.out.println("<-Sending confirm message...");

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }//end register customer

    private  String getMessage(String inputLine) {
        System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM ATM: "+Colour.ANSI_RESET);
        String[] parts = inputLine.split(",");
        String encryptedRes = parts[0];
        String recvMacCode = parts[1];

        System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+encryptedRes);
        System.out.println(Colour.ANSI_PURPLE+"->[MAC]: "+Colour.ANSI_RESET+recvMacCode);
        //Decrypt message
        String decryptMessage = KeyCipher.decrypt(msgEncryptionKey,encryptedRes);
        System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+Colour.ANSI_RESET+decryptMessage);
        //Verify MAC
        KeyCipher.extendedVerifyMAC(decryptMessage,recvMacCode,macKey);
        return decryptMessage;
    }

    public  void createBothKeys(){
        try {
            //Creating the two new keys
            byte[] info1 = "key_for_encryption".getBytes();
            byte[] info2 = "key_for_mac".getBytes();

            msgEncryptionKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info1, 32);
            macKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info2, 32);


            System.out.println(Colour.ANSI_GREEN+ "[CREATED] Encryption key: "+Colour.ANSI_RESET+msgEncryptionKey);
            System.out.println(Colour.ANSI_GREEN+ "[CREATED]  MAC key: "+Colour.ANSI_RESET+macKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private void authenticateBankToATM(ObjectOutputStream out, ObjectInputStream in)  {
        try {
            Object inputLine;
            Object outputLine;
            //Sending the 1st message line
            String bankId = "BankServer";
            int bankNonce = KeyCipher.generateNonce();
            System.out.println(Colour.ANSI_GREEN+ "[GENERATED] Nonce Value "+bankNonce+Colour.ANSI_RESET);

            outputLine = bankId+","+bankNonce;
            out.writeObject(outputLine);
            System.out.println("<-Sending ID & Nonce...");


            if ((inputLine = in.readObject()) != null) {

                //Receiving the nonce and Key
                System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM ATM: "+Colour.ANSI_RESET);
                System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+inputLine);
                inputLine = KeyCipher.decrypt(oldSharedKey,(String)inputLine);


                String[] parts = ((String) inputLine).split(",");
                String atmNonce = parts[0];
                String masterKeyString = parts[1];

                //Generate the master key
                newMasterKey = KeyCipher.createSecretKey(masterKeyString);
                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+Colour.ANSI_RESET+((String) inputLine).substring(0,6)+","+newMasterKey);

                //Final message Line
                outputLine = KeyCipher.encrypt(newMasterKey,atmNonce);
                out.writeObject(outputLine);
                System.out.println("<-Sending encrypted nonce...");

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }//end of authenticate ATM-BANK

    public boolean verifyUser(String username,String password){

        Customer c = findCustomer(username);

        return c != null && c.getPassword().equals(password);


    }


    public  Customer findCustomer(String username){
       return customerList.stream()
                .filter(customer -> customer.getUsername()
                        .equals(username)).findFirst().orElse(null);
    }



    public void withdrawal(ObjectInputStream in,ObjectOutputStream out){
        Object inputLine;
        String outputLine;
        try {

            //Reading in the Request
            if ((inputLine = in.readObject()) != null) {
                String decryptMessage = getMessage((String) inputLine);
                //Covert back to ProcessInfo Object
                ProcessInfo p = KeyCipher.convertToProcessInfo(decryptMessage);
                //Start operations
                Customer c = findCustomer(p.getCustomer().getUsername()) ;
                //General Withdrawal code
                double balance = c.getBankBalance();
                double withdraw = p.getAmount();

                Transaction t = new Transaction(new Date(),c.getCustomerID(),"withdrawal",true);

                System.out.println("\tCurrent balance: "+balance+" | Withdraw amount: "+withdraw);
                if (withdraw>balance){
                    outputLine = "You cannot withdraw that amount";
                    t.setStatus(false);
                }else if (withdraw<0){
                    outputLine = "You cannot withdraw negative money";
                    t.setStatus(false);
                }else{

                    c.setBankBalance(balance-withdraw);
                    System.out.println("\tNew Balance: "+(balance-withdraw));
                    outputLine = c.getBankBalance()+"";
                }//closing if

                /*1. Change t to a String
                * 2. encrypt the String
                * REMEMBER U DON'T NEED TO DECRYPT
                * */
                String transactionString = t.toString();
                String encryptedResult = KeyCipher.encrypt(msgEncryptionKey,transactionString);
                auditLog.addEncryptTransaction(encryptedResult);
                auditLog.addTransaction(t);
                //Encrypt
               String encryptedOutput = KeyCipher.encrypt(msgEncryptionKey, outputLine);
                //Add MAC
                outputLine = encryptedOutput + ","+KeyCipher.createMAC(outputLine,macKey);
                //Send off
                out.writeObject(outputLine);
                System.out.println("<-Sending final balance...");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//closing withdraw

    public void deposit(ObjectInputStream in,ObjectOutputStream out){
        Object inputLine;
        String outputLine;
        try {

            //Reading in the Request
            if ((inputLine = in.readObject()) != null) {
                String decryptMessage = getMessage((String) inputLine);
                //Covert back to ProcessInfo Object
                ProcessInfo p = KeyCipher.convertToProcessInfo(decryptMessage);
                //Start operations
                Customer c = findCustomer(p.getCustomer().getUsername()) ;
                //General Withdrawal code
                double balance = c.getBankBalance();
                double deposit = p.getAmount();

                Transaction t = new Transaction(new Date(),c.getCustomerID(),"deposit",true);

                System.out.println("\tCurrent balance: "+balance+" | Deposit amount: "+deposit);
                if (deposit<0){
                    outputLine = "You cannot deposit negative money";
                    t.setStatus(false);
                }else{

                    c.setBankBalance(balance+deposit);
                    System.out.println("\tNew Balance: "+(balance+deposit));
                    outputLine = c.getBankBalance()+"";

                }//closing if

                /*1. Change t to a String
                 * 2. encrypt the String
                 * REMEMBER U DON'T NEED TO DECRYPT
                 * */
                String transactionString = t.toString();
                String encryptedResult = KeyCipher.encrypt(msgEncryptionKey,transactionString);
                auditLog.addEncryptTransaction(encryptedResult);
                auditLog.addTransaction(t);
                //Encrypt
                String encryptedOutput = KeyCipher.encrypt(msgEncryptionKey, outputLine);
                //Add MAC
                outputLine = encryptedOutput + ","+KeyCipher.createMAC(outputLine,macKey);
                //Send off
                out.writeObject(outputLine);
                System.out.println("<-Sending final balance...");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//closing withdraw

    public void checkBalance(ObjectInputStream in,ObjectOutputStream out){
        Object inputLine;
        String outputLine;
        try {

            //Reading in the Request
            if ((inputLine = in.readObject()) != null) {
                String decryptMessage = getMessage((String) inputLine);
                //Covert back to ProcessInfo Object
                ProcessInfo p = KeyCipher.convertToProcessInfo(decryptMessage);
                //Start operations
                Customer c = p.getCustomer();
                //General Withdrawal code
                double balance = findCustomer(c.getUsername()).getBankBalance();


                Transaction t = new Transaction(new Date(),c.getCustomerID(),"check_balance",true);
                //auditLog.addTransaction(t);
                /*1. Change t to a String
                 * 2. encrypt the String
                 * REMEMBER U DON'T NEED TO DECRYPT
                 * */
                String transactionString = t.toString();
                String encryptedResult = KeyCipher.encrypt(msgEncryptionKey,transactionString);
                auditLog.addEncryptTransaction(encryptedResult);
                auditLog.addTransaction(t);

                //Encrypt
                String encryptedOutput = KeyCipher.encrypt(msgEncryptionKey, balance+"");
                //Add MAC
                outputLine = encryptedOutput + ","+KeyCipher.createMAC(balance+"",macKey);
                //Send off
                out.writeObject(outputLine);
                System.out.println("<-Sending final balance...");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//check balance


}
