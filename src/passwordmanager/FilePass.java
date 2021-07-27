package passwordmanager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * File management for PasswordManager
 * @author Simone Vannucci
 */
public class FilePass {
    
    private byte ver;
    private byte[] salt, cryptData;

    public byte getVer() {
        return ver;
    }

    public byte[] getSalt() {
        return salt;
    }
    
    public String getStrSalt(){
        return new String(this.getSalt(),Conf.CHR);
    }

    public byte[] getCryptData() {
        return cryptData;
    }

    public void setVer(byte ver) {
        this.ver = ver;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void setCryptData(byte[] cryptData) {
        this.cryptData = cryptData;
    }
    
     /**
     * Read file and return a string
     * @return
     * @throws IOException 
     */
    public static byte[] getFile(Path url) throws IOException{
        byte[] fileByte = Files.readAllBytes(url);
        return fileByte;
    }
    
    public static void addFile(Path url) throws IOException{
        Files.createDirectories(url.getParent());
        Files.createFile(url);
    }
    
    public static void writeFile(Path url, String data) throws IOException{
        byte[] dataFile = data.getBytes(Conf.CHR);
        Files.write(url, dataFile);
    }
    
    public static void witePassFile(Path url, int ver, String salt, byte[] data) throws IOException{
        String dataStr = ver + salt ;
        byte[] dataFile = dataStr.getBytes(Conf.CHR);
        Files.write(url, dataFile);
        Files.write(url, data,StandardOpenOption.APPEND);
    }
    
    public FilePass(byte ver, byte[] salt, byte[] cryptData){
        this.ver = ver;
        this.salt = salt;
        this.cryptData = cryptData;
    }
    
}
