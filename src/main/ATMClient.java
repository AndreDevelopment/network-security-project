package main;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class ATMClient {

    private static SecretKey oldSharedKey;
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


            //Sending the Client public key
            out.writeObject(fromClient);


            //Receiving Public key of KDC & Sending my ID
            if ((fromBankServer = in.readObject()) != null) {

            }//Sent the ID


        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }// end of main

}