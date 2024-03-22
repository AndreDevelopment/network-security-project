package main;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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


            if ((fromBankServer = in.readObject()) != null) {

                //Splitting the data
                String[] parts = ((String) fromBankServer).split(",");
                String bankID = parts[0];
                String bankNonce = parts[1];

                //Printing the result
                System.out.println("Got bank Id: "+bankID);
                System.out.println("Got bank Nonce: "+bankNonce);

                //Creating the master key
                String masterKeyString = KeyCipher.generateMasterKeyString();
                newMasterKey = KeyCipher.createSecretKey(masterKeyString);
                System.out.println("The master key is: "+newMasterKey);
                //Generating a nonce
                int atmClientNonce = KeyCipher.generateNonce();

                //Sending message 2 | Encrypted with the old Shared key
                fromClient = KeyCipher.encrypt (oldSharedKey,atmClientNonce+","+masterKeyString);
                out.writeObject(fromClient);

            }

            if ((fromBankServer = in.readObject()) != null) {

                fromBankServer = KeyCipher.decrypt(newMasterKey,(String)fromBankServer);

                System.out.println("Checking ATM original nonce: "+fromBankServer);
            }



//            //Isolate into userVerification method
//            System.out.println("Enter your username: ");
//            String userName = input.nextLine();
//            System.out.println("Enter your password: ");
//            String password = input.nextLine();
//
//            fromClient = userName +","+password;
//
//            //Sending the Client public key
//            out.writeObject(fromClient);
//            //Isolate into userVerification method




        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }// end of main




}