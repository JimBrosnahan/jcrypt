package jcrypt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
/*
 * @brief my attempt to understand java cryptography classes
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html#Introduction
 */
public class MyCipher {

    public enum CipherMode {UNDEFINED, ENCRYPT, DECRYPT}
    
    public static final String cryptoTransform = "AES/ECB/PKCS5Padding";
    public static final String cryptoTransformECB = "AES/ECB/PKCS5Padding";

    // for debug
    @SuppressWarnings("unused")
    private static void printIoNames(String in, String out) {
        System.out.println("input: " + in);
        System.out.println("output: " + out);        
    }
    
    //private static byte[] ciphertext;

    //https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html#SimpleEncrEx
    private static byte[] docryptECB(CipherMode m, byte[] in) throws Exception {
     
        // simulate char array
        //String strpass = "Test123";      
        //char[] password = strpass.toCharArray();
        char[] password = null;
        
        boolean passphrase_match = false;
        
        while (!passphrase_match) {

            password = System.console().readPassword("Enter passphrase: ");
            
            if (m == CipherMode.ENCRYPT) {
                // read passphrase 2nd time on encryption
                char[] password2 = System.console().readPassword("Enter again: ");
                
                passphrase_match = Arrays.equals(password, password2);
                
            } else {
                passphrase_match = true;
            }
        }
        

        //https://community.oracle.com/thread/1532613
        // convert UTF-8 chars to bytes
        byte[] orig_keybytes = new String(password).getBytes("UTF-8");

        // must be only 128bits (16 bytes)
        byte[] keybytes = Arrays.copyOf(orig_keybytes, 16);
        
        SecretKeySpec key = new SecretKeySpec(keybytes, "AES");
       
        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance(cryptoTransformECB);

        if ( m == CipherMode.ENCRYPT) {

            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(Cipher.ENCRYPT_MODE, key);            
            
        } else {
            
            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(Cipher.DECRYPT_MODE, key);
        }

        byte[] out = pbeCipher.doFinal(in);
        
        return out;
    }
    

    public static void encrypt(String input, String output) throws IOException, Exception {

        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            
            in = new FileInputStream(input);
            out = new FileOutputStream(output);
          
            int len = in.available();
            byte[] cleartext = new byte[len];
            
            in.read(cleartext,  0,  len);

            byte[] ciphertext = docryptECB(CipherMode.ENCRYPT, cleartext);
            
            out.write(ciphertext,  0, ciphertext.length);

        } finally {
            
            if (in != null) {
                in.close();
            }
            
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static void decrypt(String input, String output) throws IOException, Exception {


        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            
            in = new FileInputStream(input);
            out = new FileOutputStream(output);
          
            int len = in.available();
            byte[] ciphertext = new byte[len];
            
            in.read(ciphertext,  0,  len);

            byte[] cleartext = docryptECB(CipherMode.DECRYPT, ciphertext);
            
            out.write(cleartext,  0, cleartext.length);

        } finally {
            
            if (in != null) {
                in.close();
            }
            
            if (out != null) {
                out.close();
            }
        }
    }
}
