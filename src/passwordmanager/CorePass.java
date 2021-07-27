package passwordmanager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Core functions of PasswordManager
 * @author Simone Vannucci
 * @version 1.0
 */
public class CorePass {
    
    public static final int SWVERSION = 1;
    private int dataVer;
    private PasswordManager user;
    private Conf config;
    private ArrayList<PassList> backupList;

    public int getDataVer() {
        return dataVer;
    }

    private PasswordManager getUser() {
        return user;
    }
    
    public String getUsername(){
        return this.user.getUser();
    }

    public void setConfig(Conf config) {
        this.config = config;
    }

    public void setBackupList(ArrayList<PassList> backupList) {
        this.backupList = backupList;
    }
    
    public boolean userIsLogged(){
        return this.user.isLogged();
    }

    public ArrayList<PassList> getBackupList() {
        return backupList;
    }

    public Conf getConfig() {
        return config;
    }
    
    public ArrayList<PassList> getList(){
        return this.user.getList();
    }
    
    public void setDataVer(int dataVer) {
        this.dataVer = dataVer;
    }

    private void setUser(PasswordManager user) {
        this.user = user;
    }
    
    public void setUsername(String user){
        this.user.setUser(user);
    }
    
    public void setPassword(String password){
        this.user.setPass(password);
    }
    
    public void clearUserData(){
        this.user.setList(null);
        this.user.setLogged(false);
        this.user.setSalt(null);
        this.user.setPass(null);
        this.user.setUser(null);
        this.user.setUri(null);
        this.user.setVer(0);
    }
    
    public static String createSalt(){
        return CyPass.createSalt();
    }
  
    /**
     * Get the complete file url from user name and path in config
     * @param user
     * @param location
     * @return complete file url
     */
    public Path getFileUrl(String user, String location){
        Path loc = Paths.get(location);
        Path file = Paths.get(user + ".dat");
        Path filePath = loc.resolve(file);
        return filePath;
    }
    
    public boolean login(){
        return login(this.user.getUser(),this.user.getPass());
    }
    
    public boolean login(String user, String password) {
        // Get the complete url of file
        this.user.setUri(this.getFileUrl(user, this.config.getUri()));
        // Load all infos to compleate the user attributes
        FilePass infos;
        try {
            infos = this.loadData(this.user);
            this.user.setVer(infos.getVer());
            this.user.setSalt(infos.getStrSalt());
            this.user.setList( this.getListPass(this.user, infos.getCryptData()));
            this.user.setLogged(true);     
            return true;
        } catch (Exception ex) {
            Logger.getLogger(CorePass.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     *  Load file infos
     * @param obj
     * @return array with version, salt, data
     * @throws IOException 
     */
    public FilePass loadData(PasswordManager obj) throws IOException, Exception{
        byte[] data = FilePass.getFile(obj.getUri());
        if(data[0] != CorePass.SWVERSION){
            int endSalt = 2;
            for (int i = 0; i < data.length; i++) {
                if(data[i] == CyPass.ENDSALT){
                    endSalt = i;
                    break;
                }
            }
            byte[] salt = Arrays.copyOfRange(data, 1, endSalt + 1);
            byte[] crypto = Arrays.copyOfRange(data, endSalt + 1, data.length);
            FilePass file = new FilePass(data[0],salt,crypto);
            return file;
        } else {
            throw new Exception("File version not compatible");
        }
    }
    
    /**
     *  Create an array of login infos 
     * @param obj
     * @param data
     * @return array of login infos
     */
    public ArrayList<PassList> getListPass(PasswordManager obj, byte[] data) throws NoSuchAlgorithmException, 
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, 
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        ArrayList<PassList> list = new ArrayList<>();
        String dataStr = CyPass.decipherMsg(obj.getUser(), obj.getPass(), obj.getSalt(), data);
        StringTokenizer str = new StringTokenizer(dataStr,"\n",false);
        String token;
        while(str.hasMoreTokens()){
            token = str.nextToken();
            String[] cols = token.split(";");
            PassList item = new PassList(cols[0],cols[1],cols[2],cols[3]);
            list.add(item);
        }
        return list;
    }
    
    public boolean addUser(){
        return addUser(this.user.getUser(), this.user.getPass());
    }
    
    public boolean addUser(String user, String password){
        try {
            this.user.setUri(this.getFileUrl(this.user.getUser(), this.config.getUri()));
            this.user.setSalt(CorePass.createSalt());
            ArrayList<PassList> list = new ArrayList<>();
            PassList item = new PassList();
            list.add(item);
            this.user.setList(list);
            this.crateUserFile(this.user);
            return true;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex) {
            Logger.getLogger(CorePass.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void crateUserFile(PasswordManager obj) throws IOException, NoSuchAlgorithmException, 
            InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, 
            NoSuchPaddingException{
       String list = this.getCsv(obj.getList());
       byte[] cyData = CyPass.cipherMsg(obj.getUser(), obj.getPass(), obj.getSalt(), list);
       FilePass.addFile(obj.getUri());
       FilePass.witePassFile(obj.getUri(), CorePass.SWVERSION, obj.getSalt(), cyData);
    }
    
    public void saveList() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, 
            IOException{
        String data = getCsv();
        byte[] cyData = CyPass.cipherMsg(this.user.getUser(), this.user.getPass(), this.user.getSalt(), data);
        FilePass.witePassFile(this.user.getUri(), CorePass.SWVERSION, this.user.getSalt(), cyData);
    }

    public void addNewSite(PassList item) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, 
            BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, IOException{     
        ArrayList<PassList> list = this.user.getList();
        list.add(item);
        this.user.setList(list);
        this.saveList();
        
    }
    
    public void modSite(PassList newItem, int index ) throws NoSuchAlgorithmException, InvalidKeySpecException, 
            InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, IOException{
        ArrayList<PassList> list = this.user.getList();
        PassList oldItem = list.get(index);
        if (!newItem.getSite().equals("---")) {
            oldItem.setSite(newItem.getSite());
        }
        if(!newItem.getUrl().equals("---")){
            oldItem.setUser(newItem.getSite());
        }
        if(newItem.getPass().equals("---")){
            oldItem.setPass(newItem.getPass());
        }
        if(newItem.getUrl().endsWith("---")){
            oldItem.setUrl(newItem.getUrl());
        }
        list.set(index, oldItem);
        this.saveList();
    }
    
    public void remSite( int index ) throws NoSuchAlgorithmException, InvalidKeySpecException, 
            InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, IOException{
        ArrayList<PassList> list = this.user.getList();
        list.remove(index);
        this.user.setList(list);
        this.saveList();
    }
    
    private String getCsv(){
        ArrayList<PassList> list = this.getList();
        return getCsv(list);
    }
    
    private String getCsv(ArrayList<PassList> list){
        String str = "";
        for (PassList item : list) {
            str += item.toCsv();
        }
        return str;
    }
    
    public void restoreList() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, 
            BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, IOException{
            for (PassList x : this.getBackupList()) {
                System.out.println( x.getSite() + " " + x.getUser() );               
            }
        this.user.setList(this.getBackupList());
        this.saveList();
    }
    
    public void createBackup(){
        ArrayList<PassList> oldList = this.user.getList();
        ArrayList<PassList> bkList = new ArrayList<>(oldList);
        this.setBackupList(bkList);
    }
    
        
    public CorePass(){
        PasswordManager user = new PasswordManager();
        Conf config = new Conf();
        this.user = user;
        this.config = config;
    }
    
}
