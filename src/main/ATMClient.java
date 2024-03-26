package main;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class ATMClient {

    private static SecretKey oldSharedKey;
    private static SecretKey newMasterKey;
    public static void main(String[] args) throws IOException {

        oldSharedKey = KeyCipher.createSecretKey("thisismysecretkey24bytes");
        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");

        try (
                Socket clientSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        ) {

            Scanner input = new Scanner(System.in);
            Object fromBankServer, fromClient="";


            authenticateBankToATM(in, out);

            //Creating the two new keys
            byte[] info1 = "key_for_encryption".getBytes();
            byte[] info2 = "key_for_mac".getBytes();

            SecretKey msgEncryptionKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info1, 256);
            SecretKey macKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info2, 256);

            System.out.println("Creating the keys...");
            System.out.println("Created the encryption key: "+msgEncryptionKey);
            System.out.println("Created a MAC key: "+macKey);


            //authenticateCustomer(input, out);
            MAC("soon I'll be 60 years old", macKey, out);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }// end of main

    private static void authenticateCustomer(Scanner input, ObjectOutputStream out) throws IOException {
        Object fromClient;
        System.out.println("Enter your username: ");
        String userName = input.nextLine();
        System.out.println("Enter your password: ");
        String password = input.nextLine();

        fromClient = userName +","+password;

        //Sending the Client public key
        out.writeObject(fromClient);
    }

    private static void authenticateBankToATM(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Object fromBankServer;
        Object fromClient;
        if ((fromBankServer = in.readObject()) != null) {
            System.out.println(Colour.ANSI_YELLOW+"RECEIVED FROM BANK: "+Colour.ANSI_RESET);
            //Splitting the data
            String[] parts = ((String) fromBankServer).split(",");
            String bankID = parts[0];
            String bankNonce = parts[1];

            //Printing the result
            System.out.println("->Got bank Id: "+bankID);
            System.out.println("->Got bank Nonce: "+bankNonce);

            //Creating the master key
            String masterKeyString = KeyCipher.generateMasterKeyString();
            newMasterKey = KeyCipher.createSecretKey(masterKeyString);
            System.out.println(Colour.ANSI_GREEN+ "[GENERATED] Master Key "+Colour.ANSI_RESET+newMasterKey);
            //Generating a nonce
            int atmClientNonce = KeyCipher.generateNonce();
            System.out.println(Colour.ANSI_GREEN+ "[GENERATED] Nonce Value "+Colour.ANSI_RESET+atmClientNonce);

            //Sending message 2 | Encrypted with the old Shared key
            fromClient = KeyCipher.encrypt (oldSharedKey,atmClientNonce+","+masterKeyString);
            out.writeObject(fromClient);
            System.out.println("<-Sending encrypted nonce & master key...");

        }

        if ((fromBankServer = in.readObject()) != null) {
            System.out.println(Colour.ANSI_YELLOW+"RECEIVED FROM BANK: "+Colour.ANSI_RESET);
            System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+fromBankServer);
            fromBankServer = KeyCipher.decrypt(newMasterKey,(String)fromBankServer);

            System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+Colour.ANSI_RESET+fromBankServer);
        }
    }
    private static void MAC(String message, Key macKey, ObjectOutputStream out) {
        try {
            //String message = "This is a secret message";
            // String secretKeyString = "mySecretKey";

            // Convert the secret key string to Key type
            // Key secretKey = new SecretKeySpec(secretKeyString.getBytes(), "HmacSHA256");

            // Encrypt the message and get MAC code
            String[] Data = KeyCipher.createMAC(message, macKey);
            String macCode = Data[0];
            String Message = Data[1];

            System.out.println("MAC Code: " + macCode);
            System.out.println("Message: " + Message);
            out.writeObject(macCode + "," + Message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}