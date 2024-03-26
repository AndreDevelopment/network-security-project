package main;


import auditlog.AuditLog;
import auditlog.ProcessInfo;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;


public class BankServerThread extends Thread {

    private final Socket clientSocket;

    private SecretKey oldSharedKey;
    private SecretKey newMasterKey;

    List<Customer> customerList;

    private static AuditLog auditLog;


    public BankServerThread(Socket socket) {
        clientSocket = socket;
        oldSharedKey = KeyCipher.createSecretKey("thisismysecretkey24bytes");
        customerList = new ArrayList<>();
        //Dummy values for our list
        customerList.add(new Customer(1234,"Andre","password1",700));
        customerList.add(new Customer(4567,"Arshroop","ILoveAndre",10000000));
        auditLog = new AuditLog();

    }

    @Override
    public void run() {


        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {

            Object inputLine, outputLine="";


            authenticateBankToATM(out, in);
            //Creating the two new keys
            byte[] info1 = "key_for_encryption".getBytes();
            byte[] info2 = "key_for_mac".getBytes();

            SecretKey msgEncryptionKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info1, 256);
            SecretKey macKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info2, 256);

            System.out.println("Creating the keys...");
            System.out.println("Created the encryption key: "+msgEncryptionKey);
            System.out.println("Created a MAC key: "+macKey);
            authenticateCustomer(in,out);
            withdrawal(in,out);

            //MAC verification
//            if ((inputLine = in.readObject()) != null) {
//
//                String[] parts = ((String) inputLine).split(",");
//                String macCode = parts[0];
//                String Message = parts[1];
//                verifyMAC(Message, macCode, macKey);
//
//            }





            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }// end of main

    private void authenticateCustomer(ObjectInputStream in,ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Object inputLine;
        //Isolate into userVerification method
        //Username & Password received
        if ((inputLine = in.readObject()) != null) {

            String[] parts = ((String) inputLine).split(",");
            String userName = parts[0];
            String password = parts[1];

           Customer c = customerList.stream()
                    .filter(customer -> customer.getUsername()
                            .equals(userName)).findFirst().orElse(null);

            if (verifyUser(userName, password)) {
                System.out.println(Colour.ANSI_GREEN + "User is verified :)" + Colour.ANSI_RESET);

            } else {
                System.out.println(Colour.ANSI_RED + "User was not verified :(" + Colour.ANSI_RESET);

            }

            out.writeObject(c);

        }
    }

    private void authenticateBankToATM(ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
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
            System.out.println(Colour.ANSI_YELLOW+"RECEIVED FROM ATM: "+Colour.ANSI_RESET);
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
    }

    public boolean verifyUser(String username,String password){

        Customer c = customerList.stream()
                .filter(customer -> customer.getUsername()
                        .equals(username)).findFirst().orElse(null);

        return c != null && c.getPassword().equals(password);


    }
    private boolean verifyMAC(String Message, String macCode, Key secretKey){
        // Decrypt the message and verify MAC code
        boolean isMACValid = KeyCipher.verifyMAC(Message, macCode, secretKey);
        if (isMACValid) {
            System.out.println("MAC verification successful. Message integrity maintained.");
            System.out.println("Message: " + Message);
        } else {
            System.out.println("MAC verification failed! Message may have been tampered with.");
        }
        return isMACValid;
    }


    public void withdrawal(ObjectInputStream in,ObjectOutputStream out){
        Object inputLine;
        Object outputLine;
        try {

            //Reading in the Request
            if ((inputLine = in.readObject()) != null) {

                //General Withdrawal code
                double balance = ((ProcessInfo) inputLine).getCustomer().getBankBalance();
                double withdraw = ((ProcessInfo) inputLine).getAmount();

                System.out.println("Current balance: "+balance+" | Withdraw amount: "+withdraw);
                if (withdraw>balance){
                    outputLine = "You cannot withdraw that amount broke bitch";
                }else if (withdraw<0){
                    outputLine = "Are you stupid? How can you withdraw negative money";
                }else{

                    ((ProcessInfo) inputLine).getCustomer().setBankBalance(balance-withdraw);
                    System.out.println("New Balance: "+((ProcessInfo) inputLine).getCustomer().getBankBalance());
                    outputLine = ((ProcessInfo) inputLine).getCustomer().getBankBalance()+"";
                }//closing if

                out.writeObject(outputLine);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//closing withdraw


}
