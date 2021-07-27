package passwordmanager;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Password Manager multiplatform
 * @author Simone Vannucci
 * @version 1.0
 */
public class PasswordManager {
    
    private String user, pass, salt;
    private int ver;
    private Path url;
    private boolean logged = false;
    private ArrayList<PassList> list;
    
    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setUri(Path uri) {
        this.url = uri;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public void setList(ArrayList<PassList> list) {
        this.list = list;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public int getVer() {
        return ver;
    }

    public String getSalt() {
        return salt;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public Path getUri() {
        return url;
    }

    public boolean isLogged() {
        return logged;
    }

    public ArrayList<PassList> getList() {
        return list;
    }
    
    public ArrayList<PassList> search(String str){
        ArrayList<PassList> res = new ArrayList<>();
        for(PassList e: this.getList()){
            if(e.getSite().contains(str) || e.getUrl().contains(str)){
                res.add(e);
            }
        }
        return res;
    }
        
    public static void main(String[] args) {

        Conf config = new Conf();
        if(config.getUi() == 0){
            Cli cli = new Cli();
            cli.start();
        }
    }
    
}
