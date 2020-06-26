package jcrypt;

import jcrypt.MyCipher.CipherMode;

public class Jcrypt {
    

    public static void main(String[] args) throws Exception {
        

        CipherMode mode = jcrypt.MyCipher.CipherMode.UNDEFINED;
        String inputfile = null;
        String outputfile = null;
        
        // validate 3 input args
        
        if (args.length > 2) {
                       
            // validate cipher mode
            if (args[0].toUpperCase().equals("E")) {
                mode = CipherMode.ENCRYPT;
                
            } else if (args[0].toUpperCase().equals("D")) {
                mode = CipherMode.DECRYPT;
                
            } else {
                System.out.println("invalid cipher mode");
                System.exit(1);
            }
            
            inputfile = args[1];
            outputfile = args[2];
            
        } else {
                System.out.println("e|d <input> <output>");
                System.exit(0);
            }


        // process
        if (mode.equals(CipherMode.DECRYPT)) {
            MyCipher.decrypt(inputfile, outputfile);

        } else {
            MyCipher.encrypt(inputfile, outputfile);
        }
    }
}
