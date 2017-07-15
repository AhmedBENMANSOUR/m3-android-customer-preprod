package com.dioolcustomer.security;



import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by ASUS on 31/01/2017.
 */

public class Encryption {
    private final static String ALGORITM = "Blowfish";//"BLOWFISH/CBC/PKCS5Padding";//"Blowfish";
    private final static String KEY = "2356a3a42ba5781f80a72dad3f90aeee8ba93c7637aaf218a8b8c18c";
    private final static String PLAIN_TEXT = "here is your text";

 /*   public void run(View v) {

        try {

            byte[] encrypted = encrypt(KEY, PLAIN_TEXT);
            Log.i("FOO", "Encrypted: " + bytesToHex(encrypted));

            String decrypted = decrypt(KEY, encrypted);
            Log.i("FOO", "Decrypted: " + decrypted);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }*/

  /*  public byte[] encrypt(String key, String plainText) throws GeneralSecurityException {

        SecretKey secret_key = new SecretKeySpec(key.getBytes(), ALGORITM);

        Cipher cipher = Cipher.getInstance(ALGORITM);
        cipher.init(Cipher.ENCRYPT_MODE, secret_key);

        return cipher.doFinal(plainText.getBytes());
    }

    public String decrypt(String key, byte[] encryptedText) throws GeneralSecurityException, UnsupportedEncodingException {

      //  System.out.println("encryptedText : "+new String(encryptedText,"UTF-8"));
        byte[] encryptedText1 = {105,106,107};
        SecretKey secret_key = new SecretKeySpec(key.getBytes(), ALGORITM);

        Cipher cipher = Cipher.getInstance(ALGORITM);
        cipher.init(Cipher.DECRYPT_MODE, secret_key);


        byte[] decrypted = cipher.doFinal(encryptedText1);

        return new String(decrypted);
    }

    public static String bytesToHex(byte[] data) {

        if (data == null)
            return null;

        String str = "";

        for (int i = 0; i < data.length; i++) {
            if ((data[i] & 0xFF) < 16)
                str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
            else
                str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
        }

        return str;

    }*/


    static String IV = "AAAAAAAAAAAAAAAAA";
    static String plaintext = "test text 123\0\0\0"; /*Note null padding*/
    static String encryptionKey = "0123456789abcdef";
   /* public static void main(String [] args) {
        try {

            System.out.println("==Java==");
            System.out.println("plain:   " + plaintext);

            byte[] cipher = encrypt(plaintext, encryptionKey);

            System.out.print("cipher:  ");
            for (int i=0; i<cipher.length; i++)
                System.out.print(new Integer(cipher[i])+" ");
            System.out.println("");

            String decrypted = decrypt(cipher, encryptionKey);

            System.out.println("decrypt: " + decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   /* public static byte[] encrypt(String encryptionKey,String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-32BE"), "AES");
        //System.out.println(); IV.getBytes("UTF-8").
        cipher.init(Cipher.ENCRYPT_MODE, key);//,new IvParameterSpec(IV.getBytes("UTF-32BE")));
        return cipher.doFinal(plainText.getBytes("UTF-32BE"));
    }

    public static String decrypt(byte[] cipherText, String encryptionKey) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-32BE"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);//,new IvParameterSpec(IV.getBytes("UTF-32BE")));
        return new String(cipher.doFinal(cipherText),"UTF-32BE");
    }*/



    public static String encrypt(String key, String pt) throws Exception{
        SecretKey secret_key = new SecretKeySpec(key.getBytes(), ALGORITM);
        Cipher cipher = Cipher.getInstance(ALGORITM);
        cipher.init(Cipher.ENCRYPT_MODE, secret_key);
        byte[] ptBytes = pt.getBytes("UTF-8");
        byte[] enc = cipher.doFinal(ptBytes);
        String str  = Base64.encodeToString(enc, Base64.DEFAULT);
        return str;
    }


    public static String decrypt(String key, String ct) throws Exception{
        SecretKey secret_key = new SecretKeySpec(key.getBytes(), ALGORITM);
        Cipher cipher = Cipher.getInstance(ALGORITM);
        cipher.init(Cipher.DECRYPT_MODE,secret_key);
        byte[] enc = Base64.decode(ct, Base64.DEFAULT);
        byte[] ptBytes = cipher.doFinal(enc);
        String str  = new String(ptBytes,"UTF-8");
        return str;
    }





}
