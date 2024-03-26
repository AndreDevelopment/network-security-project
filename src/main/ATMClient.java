package main;


import auditlog.ProcessInfo;

import javax.crypto.SecretKey;
import java.awt.*;
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

    private static Customer signedInCustomer;
    public static void main(String[] args) throws IOException {

        oldSharedKey = KeyCipher.createSecretKey("thisismysecretkey24bytes");
        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");

        try (
                Socket clientSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        ) {


            Object fromBankServer, fromClient="";


            authenticateBankToATM(in, out);

            //Creating the two new keys
            byte[] info1 = "key_for_encryption".getBytes();
            byte[] info2 = "key_for_mac".getBytes();

            SecretKey msgEncryptionKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info1, 32);
            SecretKey macKey = KeyCipher.deriveKeyUsingHkdf(newMasterKey, info2, 32);


            System.out.println(Colour.ANSI_GREEN+ "[CREATED] Encryption key: "+Colour.ANSI_RESET+msgEncryptionKey);
            System.out.println(Colour.ANSI_GREEN+ "[CREATED]  MAC key: "+Colour.ANSI_RESET+macKey);


            authenticateCustomer(in, out);
            //MAC("soon I'll be 60 years old", macKey, out);
            withdrawal(in,out,macKey,msgEncryptionKey);

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

    private static void authenticateCustomer(ObjectInputStream in, ObjectOutputStream out)  {
        try {
            Scanner input = new Scanner(System.in);
            Object fromClient;
            Object fromBankServer;

            System.out.println("\nEnter your username: ");
            String userName = input.nextLine();
            System.out.println("Enter your password: ");
            String password = input.nextLine();


            fromClient = userName +","+password;

            //Sending the Client public key
            out.writeObject(fromClient);

            if ((fromBankServer = in.readObject()) != null) {
                //System.out.println("Got the Customer Object!");
                signedInCustomer = (Customer) fromBankServer;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void authenticateBankToATM(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Object fromBankServer;
        Object fromClient;
        if ((fromBankServer = in.readObject()) != null) {
            System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM BANK: "+Colour.ANSI_RESET);
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
            System.out.println(Colour.ANSI_GREEN+ "[GENERATED] Master Key: "+Colour.ANSI_RESET+newMasterKey);
            //Generating a nonce
            int atmClientNonce = KeyCipher.generateNonce();
            System.out.println(Colour.ANSI_GREEN+ "[GENERATED] Nonce Value: "+Colour.ANSI_RESET+atmClientNonce);

            //Sending message 2 | Encrypted with the old Shared key
            fromClient = KeyCipher.encrypt (oldSharedKey,atmClientNonce+","+masterKeyString);
            out.writeObject(fromClient);
            System.out.println("<-Sending encrypted nonce & master key...");

        }

        if ((fromBankServer = in.readObject()) != null) {
            System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM BANK: "+Colour.ANSI_RESET);
            System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+fromBankServer);
            fromBankServer = KeyCipher.decrypt(newMasterKey,(String)fromBankServer);

            System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+Colour.ANSI_RESET+fromBankServer);
        }
    }
    private static void MAC(String message, Key macKey, ObjectOutputStream out) {
        try {

            // Encrypt the message and get MAC code
            String macCode = KeyCipher.createMAC(message, macKey);

            System.out.println("MAC Code: " + macCode);
            System.out.println("Message: " + message);
            out.writeObject(macCode + "," + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }//end of MAC

    public static void withdrawal(ObjectInputStream in,ObjectOutputStream out,Key macKey,Key encryptKey){

        Object fromBankServer;

        try {
            ProcessInfo processInfo = new ProcessInfo(signedInCustomer,900);

            //Covert to a string
            String result = KeyCipher.objectToBase64String(processInfo);

            //Encrypt the object
            String encryptedResult = KeyCipher.encrypt(encryptKey,result);
            //Add the MAC code
            String fromClient = encryptedResult+","+KeyCipher.createMAC(result,macKey);
            //Send it off
            out.writeObject(fromClient);
            System.out.println("<-Sent a transaction...");

            if ((fromBankServer = in.readObject()) != null) {
                System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM BANK: "+Colour.ANSI_RESET);
                String[] parts = ((String) fromBankServer).split(",");
                String encryptedRes = parts[0];
                String recvMacCode = parts[1];

                System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+encryptedRes);
                System.out.println(Colour.ANSI_PURPLE+"->[MAC]: "+Colour.ANSI_RESET+recvMacCode);
                //Decrypt
                String originalMsg = KeyCipher.decrypt(encryptKey,encryptedRes);

                //Verify MAC
                KeyCipher.extendedVerifyMAC(originalMsg,recvMacCode,macKey);
                //Final print

                //Just checking if the reply message is a balance or a message
                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+ Colour.ANSI_RESET
                        + (KeyCipher.isNumerical(originalMsg)?"Remaining balance: "+originalMsg:originalMsg));

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }//end of withdrawal


}