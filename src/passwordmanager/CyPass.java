package passwordmanager;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cripting and Decripting data
 * @author Simone Vannucci
 * @version 1.0
 */
public class CyPass {
    
    public static final String PEPPER = "!4l*à+L¢IFt&+wéN7aBäu%ö6[àkLöZ{cDa}fg4!%Sn<6Lzà¢7TI$L29lDöMàKne";
    public static final char ENDSALT = '%';
    
    /**
     * Generate a secret key
     * @param password
     * @param salt
     * @return secret key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     */
    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException{
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password,salt,65536,256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }
    
    /**
     * Generate a secret salt from user name and a given salt
     * @param user
     * @param salt
     * @return secret salt
     */
    public static String getSecretSalt(String user, String salt){
        String secretSalt = salt.substring(0, 30) + user.substring(0,2) + CyPass.PEPPER.substring(13)+ salt +
                CyPass.PEPPER.substring(0, 13) + salt.substring(30);
        return secretSalt;
    }
    
    /**
     * Translate a string in a char array
     * @param key
     * @return 
     */
    public static char[] getArrayFromString(String key){
        char[] keyChar = new char[key.length()];
        for (int i = 0; i < keyChar.length; i++) {
            keyChar[i] = key.charAt(i);
        }
        return keyChar;
    }
    
    /**
     * Return a crypted message
     * @param user
     * @param pass
     * @param salt
     * @param msg
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException 
     */
    public static byte[] cipherMsg(String user, String pass, String salt, String msg) throws NoSuchAlgorithmException, 
            InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            NoSuchPaddingException, UnsupportedEncodingException{
         String SecretSalt = CyPass.getSecretSalt(user, salt);
        char[] keyChr = CyPass.getArrayFromString(pass);
        SecretKey psw = CyPass.getAESKeyFromPassword(keyChr, SecretSalt.getBytes());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, psw);
        byte[] encMsg = cipher.doFinal(msg.getBytes());
        return encMsg;
    }
    
    /**
     * Return a decrypted message
     * @param user
     * @param pass
     * @param salt
     * @param msg
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException 
     */
    public static String decipherMsg(String user, String pass, String salt, byte[] msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidAlgorithmParameterException{
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        String SecretSalt = CyPass.getSecretSalt(user, salt);
        char[] keyChr = CyPass.getArrayFromString(pass);
        SecretKey psw = CyPass.getAESKeyFromPassword(keyChr, SecretSalt.getBytes());
        Cipher decipher = Cipher.getInstance("AES"); 
        decipher.init(Cipher.DECRYPT_MODE, psw);
        byte[] decMsg = decipher.doFinal(msg);
        return new String(decMsg);
    }
    
    public static String createSalt(){
        int lenSalt = 150;
        String salt = "";
        String upCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowCase = upCase.toLowerCase();
        String num = "1234567890";
        String spec = "+*?!&€@#èéàüöä$£[]{}<>/¢|";
        String set = upCase + lowCase + num + spec;
        Random random = new Random();
        for (int i = 0; i < lenSalt; i++) {
            int index = random.nextInt(set.length());
            char c = set.charAt(index);
            salt += c;
        }
        salt += CyPass.ENDSALT;
        return salt;
    }

}
