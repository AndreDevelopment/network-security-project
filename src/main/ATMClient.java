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

    private  SecretKey oldSharedKey;
    private  SecretKey newMasterKey,msgEncryptionKey,macKey;

    private  Customer signedInCustomer;

    public ATMClient() {
        oldSharedKey = KeyCipher.createSecretKey("thisismysecretkey24bytes");
    }





    public  boolean authenticateCustomer(ObjectInputStream in, ObjectOutputStream out,String username,String password)  {
        signedInCustomer =null;
        try {

            Object fromClient;
            Object fromBankServer;

            //Concat message
            String msg = username +","+password;
            //Encrypt & add MAC
            fromClient = KeyCipher.encrypt(msgEncryptionKey,msg)+","+KeyCipher.createMAC(msg,macKey);
            //Sending the user pass
            out.writeObject(fromClient);

            if ((fromBankServer = in.readObject()) != null) {

                //Expecting either a String "Bad" or an actual customer object
                if (fromBankServer instanceof Customer)
                    signedInCustomer = (Customer) fromBankServer;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        //Since the customer will be null if not logged in properly I'm return that truth value
        return signedInCustomer != null;

    }//authenticate customer end

    public void registerCustomer(ObjectInputStream in, ObjectOutputStream out,String username,String password)  {

        Object fromBankServer,fromClient;

        try {


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

    public void authenticateBankToATM(ObjectInputStream in, ObjectOutputStream out)  {
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
    private  void MAC(String message, Key macKey, ObjectOutputStream out) {
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

    public  void withdrawal(ObjectInputStream in,ObjectOutputStream out, String amount){

        Object fromBankServer;
        double withdrawal_amount = Double.parseDouble(amount);
        System.out.println(withdrawal_amount);
        try {
            ProcessInfo processInfo = new ProcessInfo(signedInCustomer,withdrawal_amount);

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

                //Updating the Customer reference on this end
                if(KeyCipher.isNumerical(originalMsg)){
                    signedInCustomer.setBankBalance(Double.parseDouble(originalMsg));
                }

            }//end of final message



        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//end of withdrawal

    public  void deposit(ObjectInputStream in,ObjectOutputStream out, String amount){

        Object fromBankServer;
        double deposit_amount = Double.parseDouble(amount);
        System.out.println(deposit_amount);
        try {
            ProcessInfo processInfo = new ProcessInfo(signedInCustomer,deposit_amount);

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

                //Updating the Customer reference on this end
                if(KeyCipher.isNumerical(originalMsg)){
                    signedInCustomer.setBankBalance(Double.parseDouble(originalMsg));
                    System.out.println("Bank balance: "+signedInCustomer.getBankBalance());
                }

            }//end of final message

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }//end of deposit

    private  String getMsg(String fromBankServer) {
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

    public  String checkBalance(ObjectInputStream in,ObjectOutputStream out){

        Object fromBankServer;

        String finalBalance = "Bad balance";

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
                finalBalance = originalMsg;
                //Just checking if the reply message is a balance or a message
                System.out.println(Colour.ANSI_CYAN+"->[DECRYPTED]: "+ Colour.ANSI_RESET + "Your balance: "+originalMsg);

            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("The customers current balance is: "+signedInCustomer.getBankBalance());
        System.out.println("The final balance is: "+finalBalance);
        return finalBalance;
    }//end of withdrawal


}