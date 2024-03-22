package main;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Mac;

public class KeyCipher {

    public static int generateNonce(){

        return new Random().nextInt(900000) + 100000;
    }
    public static SecretKey createSecretKey(String keyBytes) {
        //String secretString = "thisismysecretkey24bytes";
        return new SecretKeySpec(keyBytes.getBytes(), "AES");
    }
    public static byte[] convertToByteArray(Object obj){

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            byte[] yourBytes = bos.toByteArray();

            // Always close streams
            out.close();
            bos.close();

            return yourBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }//end of byte conversion

    public static String convertToString(byte[]arr)  {

        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInput in;
        try {
            in = new ObjectInputStream(bis);
            return (String) in.readObject();
        } catch (IOException  | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return "Bad Convert";
    }


    public static String encrypt(Key key, String msg){

        //"RSA/ECB/PKCS1Padding"
        //"AES"
        try {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,key);

            //This message will contain an encrypted byte array
            return encode(cipher.doFinal(KeyCipher.convertToByteArray(msg)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Encrypt";
    }//encrypt


    public static String decrypt(Key key, String encryptedBytes){

        //"RSA/ECB/PKCS1Padding"
        //"AES"

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] objDecrypt = cipher.doFinal(decode(encryptedBytes));

            return KeyCipher.convertToString(objDecrypt);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Decrypt";
    }//end of decrypt

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

}
