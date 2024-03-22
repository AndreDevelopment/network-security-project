package main;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class BankServerThread extends Thread {

    private final Socket clientSocket;

    private SecretKey oldSharedKey;



    public BankServerThread(Socket socket) {
        clientSocket = socket;
        oldSharedKey = KeyCipher.createSecretKey("thisismysecretkey24bytes");

    }

    @Override
    public void run() {


        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {

            Object inputLine, outputLine="";


            // Key exchange
            if ((inputLine = in.readObject()) != null) {

            }

            // Receive client ID and send KDC Nonce & KDC ID
            if ((inputLine = in.readObject()) != null) {

                out.writeObject(outputLine);
            }



            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }// end of main




}
