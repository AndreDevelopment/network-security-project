package main;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


public class BankServerThread extends Thread {

    private final Socket clientSocket;

    private SecretKey oldSharedKey;
    private SecretKey newMasterKey;

    List<Customer> customerList = List.of(new Customer(1234,"Andre","password1")
            ,new Customer(4567,"Arshroop","ILoveAndre"));


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


            //Sending the 1st message line
            String bankId = "BankServer";
            int bankNonce = KeyCipher.generateNonce();

            outputLine = bankId+","+bankNonce;
            out.writeObject(outputLine);



            if ((inputLine = in.readObject()) != null) {

                inputLine = KeyCipher.decrypt(oldSharedKey,(String)inputLine);

                String[] parts = ((String) inputLine).split(",");
                String atmNonce = parts[0];
                String masterKeyString = parts[1];

                System.out.println("ATM Nonce: "+atmNonce);

                //Generate the master key
                newMasterKey = KeyCipher.createSecretKey(masterKeyString);
                System.out.println("Master Key: "+newMasterKey);

                //Final message Line
                outputLine = KeyCipher.encrypt(newMasterKey,atmNonce);
                out.writeObject(outputLine);


            }

            //Isolate into userVerification method
            //Username & Password received
//            if ((inputLine = in.readObject()) != null) {
//
//                String[] parts = ((String) inputLine).split(",");
//                String userName = parts[0];
//                String password = parts[1];
//
//                if (verifyUser(userName,password)){
//                    System.out.println(Colour.ANSI_GREEN+"User is verified :)" +Colour.ANSI_RESET);
//                }else {
//                    System.out.println(Colour.ANSI_RED+ "User was not verified :("+Colour.ANSI_RESET);
//                }
//
//            }
            //Isolate into userVerification method





            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }// end of main

    public boolean verifyUser(String username,String password){

        Customer c = customerList.stream()
                .filter(customer -> customer.getUsername()
                        .equals(username)).findFirst().orElse(null);

        return c != null && c.getPassword().equals(password);


    }


}
