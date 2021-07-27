package passwordmanager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration utility for PasswordManager
 * @author Simone Vannucci
 * @version 1.0
 */
public class Conf {

    public final static Charset CHR = Charset.forName("UTF-8");
    public final static int UIDEF = 0;
    public final static String URIDEF = ".\\";
    public final static String NAMECONFFILE = "configuration.conf";
    public final static String PATHCONFFiLE = ".\\";
    private int ui = Conf.UIDEF;
    private String uri = Conf.URIDEF;

    public int getUi() {
        return ui;
    }

    public String getUri() {
        return uri;
    }

    public void setUi(int ui) {
        this.ui = ui;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    private void createFile(Path url){
        try {
            Files.createDirectories(url.getParent());
            Files.createFile(url);
            String str = "";
            str += "ui;" + Conf.UIDEF + "\n";
            str += "uri;" + Conf.URIDEF + "\n";
            FilePass.writeFile(url, str);
        } catch (IOException ex) {
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadData(){
        Path loc = Paths.get(Conf.PATHCONFFiLE);
        Path file = Paths.get(Conf.NAMECONFFILE);
        Path url = loc.resolve(file);
        if( !Files.exists(url)) this.createFile(url);
        try {
            byte[] data = FilePass.getFile(url);
            StringTokenizer str = new StringTokenizer(new String(data),"\n",false);
            String token;
            while(str.hasMoreTokens()){
                token = str.nextToken();
                String[] cols = token.split(";");
                if(cols[0].trim().charAt(0) != '#'){
                    if(cols[0].trim().equals("ui")) this.setUi(Integer.parseInt(cols[1]));
                    if(cols[0].trim().equals("uri")) this.setUri(cols[1]);
                }
            }    
        } catch (IOException ex) {
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void changeUri(String uri){
        String str = "";
        str += "ui;" + this.getUi() + "\n";
        str += "uri;" + uri + "\n";
        Path loc = Paths.get(Conf.PATHCONFFiLE);
        Path file = Paths.get(Conf.NAMECONFFILE);
        Path url = loc.resolve(file);
        try {
            this.setUri(uri);
            FilePass.writeFile(url, str);
        } catch (IOException ex) {
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Conf(){
        this.loadData();
    }
}
