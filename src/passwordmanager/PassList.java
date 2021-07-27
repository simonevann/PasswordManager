package passwordmanager;

/**
 * Password Container
 * @author Simone Vannucci
 * @version 1.0
 */
public class PassList {   
    
    private String site, user, pass, url;

    public String getSite() {
        return site;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getUrl() {
        return url;
    }

    public void setSite(String site) {
        if (site == "") {
            site = "---";
        }
        this.site = site;
    }

    public void setUser(String user) {
        if (user == "") {
            user = "---";
        }
        this.user = user;
    }

    public void setPass(String pass) {
         if (pass == "") {
            pass = "---";
        }
        this.pass = pass;
    }

    public void setUrl(String url) {
        if (url == "") {
            url = "---";
        }
        this.url = url;
    }
    
    public String toCsv(){
        String str = "";
        str += this.getSite() + ";";
        str += this.getUser() + ";";
        str += this.getPass() + ";";
        str += this.getUrl() + "\n";
        return str;
    }
    
    public PassList(String site, String user, String pass, String url){       
        this.setSite(site);
        this.setUser(user);
        this.setPass(pass);
        this.setUrl(url);
    }
    
    public PassList(){
        this.site = "???";
        this.user = "???";
        this.pass = "???";
        this.url = "???";
    }
    
}
