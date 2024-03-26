package main;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;
import javax.crypto.Mac;

public class KeyCipher {

    public static int generateNonce(){

        return new Random().nextInt(900000) + 100000;
    }
    public static SecretKey createSecretKey(String keyBytes) {
        //String secretString = "thisismysecretkey24bytes";
        return new SecretKeySpec(keyBytes.getBytes(), "AES");
    }

    public static String generateMasterKeyString() {

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        int length = 24;

        //Using Streams API to generate a master key string
        return random.ints(length, 0, alphabet.length())
                .mapToObj(alphabet::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
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



    public static SecretKey deriveKeyUsingHkdf(SecretKey secretKey, byte[] info, int keySize) throws Exception {
        // Use HmacSHA256 for HKDF extract and expand steps
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(secretKey);
        // Extract phase (optional, can use secretKey directly)
        byte[] ikm = secretKey.getEncoded(); // Extract keying material

        // Expand phase
        byte[] prk = hmac.doFinal(ikm); // Pseudo-Random Key
        byte[] infoWithLength =  concat(info, new byte[]{(byte) info.length});
        byte[] okm = hmac.doFinal(concat(prk, infoWithLength)); // Output Keying Material

        return convertByteToSecretKey(truncate(okm, keySize),"AES"); // Truncate to desired key size
    }

    public static SecretKey convertByteToSecretKey(byte[] keyBytes, String algorithm) {
        // Check if algorithm is valid
        if (algorithm == null || algorithm.isEmpty()) {
            throw new IllegalArgumentException("Algorithm cannot be null or empty");
        }

        // Create SecretKeySpec object with the key bytes and algorithm
        return new SecretKeySpec(keyBytes, algorithm);
    }

    private static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private static byte[] truncate(byte[] data, int length) {


        // Ensure length is at least the size of the data array
        length = Math.max(length, data.length);

        byte[] truncated = new byte[length];

        // Copy elements from data and fill remaining with 0
        System.arraycopy(data, 0, truncated, 0, data.length);
        Arrays.fill(truncated, data.length, length, (byte) 0);

        return truncated;
    }
    // Method to encrypt a message and generate MAC code
    public static String[] createMAC(String message, Key secretKey) {
        try {
            String[] result = new String[2];

            // Initialize MAC with HmacSHA256 algorithm
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            // Generate MAC code for the message
            byte[] macBytes = mac.doFinal(message.getBytes());

            // Convert MAC bytes to hexadecimal string
            StringBuilder macStringBuilder = new StringBuilder();
            for (byte b : macBytes) {
                macStringBuilder.append(String.format("%02x", b));
            }

            result[0] = macStringBuilder.toString(); // MAC code
            result[1] = message; // Original message

            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyMAC(String Message, String macCode, Key secretKey) {
        try {
            // Initialize MAC with HmacSHA256 algorithm
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            // Generate MAC code for the encrypted message
            byte[] receivedMacBytes = mac.doFinal(Message.getBytes());

            // Convert received MAC bytes to hexadecimal string
            StringBuilder receivedMacStringBuilder = new StringBuilder();
            for (byte b : receivedMacBytes) {
                receivedMacStringBuilder.append(String.format("%02x", b));
            }

            String receivedMac = receivedMacStringBuilder.toString();

            // Compare received MAC with the provided MAC code
            return macCode.equals(receivedMac);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }




}
