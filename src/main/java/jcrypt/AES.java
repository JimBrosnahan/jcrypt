/*
** Copyright (c) 2020 - Jim Brosnahan
**
** This demonstration source is distributed in the hope it will be useful,
** BUT WITHOUT ANY WARRANTY.
**
** 
*/

package jcrypt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public enum CipherMode {UNDEFINED, ENCRYPT, DECRYPT}    
    
    private static final String salt = "ssshhhhhhhhhhh!!!!";
    private static final String cryptoTransformCBC = "AES/CBC/PKCS5Padding";

    // for debug
    @SuppressWarnings("unused")
    private static void printIoNames(String in, String out) {
        System.out.println("input: " + in);
        System.out.println("output: " + out);        
    }
  
    /*
     * @brief - encrypt/decrypt given byte stream
     * @return - dec/enc byte stream
     */
    private static byte[] docryptCBC(CipherMode m, char[] password, byte[] in) throws Exception {
        
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password, salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            
            // Create cipher instance
            Cipher cipher = Cipher.getInstance(cryptoTransformCBC);
            
            if (m == CipherMode.ENCRYPT) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);                
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);                                
            }
            return cipher.doFinal(in);
        } 
        catch (Exception e) 
        {
            System.out.println("Error in docryptECB: " + e.toString());
        }
        return null;
    }
 

    public static void encrypt(char[] pw, String input, String output) throws IOException, Exception {

        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            
            in = new FileInputStream(input);
            out = new FileOutputStream(output);
          
            int len = in.available();
            byte[] cleartext = new byte[len];
            
            in.read(cleartext,  0,  len);

            byte[] ciphertext = docryptCBC(CipherMode.ENCRYPT, pw, cleartext);
            
            if (ciphertext != null) {
                out.write(ciphertext,  0, ciphertext.length);
            }

        } finally {
            
            if (in != null) {
                in.close();
            }
            
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static void decrypt(char[] pw, String input, String output) throws IOException, Exception {


        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            
            in = new FileInputStream(input);
            out = new FileOutputStream(output);
          
            int len = in.available();
            byte[] ciphertext = new byte[len];
            
            in.read(ciphertext,  0,  len);
            
            byte[] cleartext = docryptCBC(CipherMode.DECRYPT, pw, ciphertext);
            
            if (cleartext != null) {
                out.write(cleartext,  0, cleartext.length);
            }

        } finally {
            
            if (in != null) {
                in.close();
            }
            
            if (out != null) {
                out.close();
            }
        }
    }
    
    /*
     * @brief - read passphrase
     * @input - read2x indicate number of reads for encryption
     */
    public static char[] readPassPhrase(int read2x) {
        
        char[] password = null;
        
        boolean passphrase_match = false;
        
        while (!passphrase_match) {

            password = System.console().readPassword("Enter passphrase: ");
            
            if (read2x > 1) {
                
                // read passphrase a 2nd time for encryption confirmation
                char[] password2 = System.console().readPassword("Enter again: ");
                
                passphrase_match = Arrays.equals(password, password2);
                
            } else {
                passphrase_match = true;
            }
        }
        
        return password;
    }    
}
