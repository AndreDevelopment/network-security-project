package main;


import auditlog.ProcessInfo;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.Scanner;


public class ATMClient {

    private static SecretKey oldSharedKey;
    private static SecretKey newMasterKey,msgEncryptionKey,macKey;

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

            authenticateBankToATM(in, out);

            createBothKeys();

            registerCustomer(in,out);

            authenticateCustomer(in, out);
            //deposit(in, out);
            //withdrawal(in,out);
            checkBalance(in,out);

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

            System.out.println("\n[LOGIN] Enter your username: ");
            String userName = input.nextLine();
            System.out.println("[LOGIN] Enter your password: ");
            String password = input.nextLine();

            //Concat message
            String msg = userName +","+password;
            //Encrypt & add MAC
            fromClient = KeyCipher.encrypt(msgEncryptionKey,msg)+","+KeyCipher.createMAC(msg,macKey);
            //Sending the user pass
            out.writeObject(fromClient);

            if ((fromBankServer = in.readObject()) != null) {

                signedInCustomer = (Customer) fromBankServer;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }//authenticate customer end

    private static void registerCustomer(ObjectInputStream in, ObjectOutputStream out)  {

        Object fromBankServer,fromClient;

        try {
            Scanner input = new Scanner(System.in);
            String username,password = "",rePassword="-1";

            System.out.println("\n[REGISTER] Enter a new username: ");
            username = input.nextLine();

            while (!password.equals(rePassword)) {
                System.out.println("[REGISTER] Enter a password");
                password = input.nextLine();

                System.out.println("[REGISTER] Re-enter a password");
                rePassword = input.nextLine();
            }

            String msg = username+","+password;
            //Gotta encrypt :) & create MAC
            String encryptedMsg = KeyCipher.encrypt(msgEncryptionKey,msg);
            String macCode = KeyCipher.createMAC(msg,macKey);

            //concat both and send it off
            fromClient = encryptedMsg+","+macCode;
            out.writeObject(fromClient);
            System.out.println("<-Sending customer info...");

            if ((fromBankServer = in.readObject()) != null) {
                System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM BANK: "+Colour.ANSI_RESET);

                String[] parts = ((String) fromBankServer).split(",");

                System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+parts[0]);
                System.out.println(Colour.ANSI_PURPLE+"->[MAC]: "+Colour.ANSI_RESET+parts[1]);
                //Decrypt
                String originalMsg = KeyCipher.decrypt(msgEncryptionKey,parts[0]);
                //Verify MAC
                KeyCipher.extendedVerifyMAC(originalMsg,parts[1],macKey);

                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+Colour.ANSI_RESET+originalMsg);
            }



        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }//end register customer

    private static void authenticateBankToATM(ObjectInputStream in, ObjectOutputStream out)  {
        try {
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
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }// end authenticate Bank to ATM
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

    public static void createBothKeys(){
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

    public static void withdrawal(ObjectInputStream in,ObjectOutputStream out){

        Object fromBankServer;

        try {
            ProcessInfo processInfo = new ProcessInfo(signedInCustomer,400);

            //Covert to a string
            String result = KeyCipher.objectToBase64String(processInfo);

            //Encrypt the object
            String encryptedResult = KeyCipher.encrypt(msgEncryptionKey,result);
            //Add the MAC code
            String fromClient = encryptedResult+","+KeyCipher.createMAC(result,macKey);
            //Send it off
            out.writeObject(fromClient);
            System.out.println("<-Sent a transaction...");

            if ((fromBankServer = in.readObject()) != null) {
                String originalMsg = getMsg((String) fromBankServer);

                //Just checking if the reply message is a balance or a message
                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+ Colour.ANSI_RESET
                        + (KeyCipher.isNumerical(originalMsg)?"Remaining balance: "+originalMsg:originalMsg));

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//end of withdrawal

    public static void deposit(ObjectInputStream in,ObjectOutputStream out){

        Object fromBankServer;

        try {
            ProcessInfo processInfo = new ProcessInfo(signedInCustomer,400);

            //Covert to a string
            String result = KeyCipher.objectToBase64String(processInfo);

            //Encrypt the object
            String encryptedResult = KeyCipher.encrypt(msgEncryptionKey,result);
            //Add the MAC code
            String fromClient = encryptedResult+","+KeyCipher.createMAC(result,macKey);
            //Send it off
            out.writeObject(fromClient);
            System.out.println("<-Sent a transaction...");

            if ((fromBankServer = in.readObject()) != null) {
                String originalMsg = getMsg((String) fromBankServer);

                //Just checking if the reply message is a balance or a message
                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+ Colour.ANSI_RESET
                        + (KeyCipher.isNumerical(originalMsg)?"New balance: "+originalMsg:originalMsg));

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//end of deposit

    private static String getMsg(String fromBankServer) {
        System.out.println("\n"+Colour.ANSI_YELLOW+"RECEIVED FROM BANK: "+Colour.ANSI_RESET);
        String[] parts = fromBankServer.split(",");
        String encryptedRes = parts[0];
        String recvMacCode = parts[1];

        System.out.println(Colour.ANSI_RED+"->[ENCRYPTED]: "+Colour.ANSI_RESET+encryptedRes);
        System.out.println(Colour.ANSI_PURPLE+"->[MAC]: "+Colour.ANSI_RESET+recvMacCode);
        //Decrypt
        String originalMsg = KeyCipher.decrypt(msgEncryptionKey,encryptedRes);

        //Verify MAC
        KeyCipher.extendedVerifyMAC(originalMsg,recvMacCode,macKey);
        //Final print
        return originalMsg;
    }

    public static void checkBalance(ObjectInputStream in,ObjectOutputStream out){

        Object fromBankServer;

        try {
                //Consider sending just the customer object?
            ProcessInfo processInfo = new ProcessInfo(signedInCustomer,0);
            //Covert to a string
            String result = KeyCipher.objectToBase64String(processInfo);
            //Encrypt the object
            String encryptedResult = KeyCipher.encrypt(msgEncryptionKey,result);
            //Add the MAC code
            String fromClient = encryptedResult+","+KeyCipher.createMAC(result,macKey);
            //Send it off
            out.writeObject(fromClient);
            System.out.println("<-Sent a transaction...");

            if ((fromBankServer = in.readObject()) != null) {
                String originalMsg = getMsg((String) fromBankServer);
                //Final print

                //Just checking if the reply message is a balance or a message
                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+ Colour.ANSI_RESET + "Your balance: "+originalMsg);

            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//end of withdrawal


}