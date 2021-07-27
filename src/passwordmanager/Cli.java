package passwordmanager;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Command line interface for PasswordManager
 * 
 * Comandi:
 * - LOGIN             : login user
 * - NEWUSER      : create new user
 * - ADD                 : add new site
 * - LIST                : show all entries
 * - S                      : search
 * - MOD                : modify an entry
 * - DEL                : remove an entry
 * - RESTORE        : restore previews version of the password list
 * - SHOWPATH    : show where the file is stored
 * - SETPATH        : change path where get the file
 * - ?                      : help
 * - EXIT                : Exit
 * 
 * @author Simone Vannucci
 * @version 30.04.2021
 * @author Simone Vannucci
 * @version 1.0
 */
public class Cli {

    private Conf config;
    private CorePass core;
    
    private static String askUser(){
        return Cli.askUser("");
    }
    
    private static String askUser(String user) {
        return Cli.askUser(user,'>');
    }
    
    private static String askUser(String user, char c) {
        System.out.print( user + " " + c + " ");
        Scanner in = new Scanner(System.in);
        return in.nextLine().trim();
    }
    
    private static int askInt(int min, int max, String user, char c) {
        System.out.print( user + " " + c + " ");
        Scanner in = new Scanner(System.in);
        boolean exit = false;
        int val;
        do {
            while ( !in.hasNextInt() ) {
                System.out.println("Immettere solo cifre");
                in.nextLine();
            }
            val = in.nextInt();
            if (val >= min && val <= max) {
                exit = true;
            } else {
                System.out.println("Scelta non valida");
            }
            in.nextLine();
        } while (!exit);
        return val;
    }
    
    private static int askInt(int min, int max, String user) {
        return Cli.askInt(min, max, user, '>');
    }
    
    /**
     * Ask an integer between 2 values
     * @param min
     * @param max
     * @return values
     */
    public static int askInt(int min, int max) {
        return Cli.askInt(min, max, "");
    }
    
     /**
     * Stampa i comandi disponibili
     */
    public static void printHelp(){
        
        final short COMMAND = 0;
        final short DESCRIPTION = 1;
        final short PATH = 2;
        String[][] cmd = {
            {"LOGIN", "Login utente"},
            {"NEWUSER", "Aggiungi utente"},
            {"ADD", "Aggiungi utente"},
            {"LIST", "Mostra tutte le password"},
            {"S", "Cerca una password"},
            {"MOD", "Modifica una entry"},
            {"DEL", "Cancella una entry"},
            {"SHOWPATH", "Mostra il percorso di origine del file utente"},
            {"SETPATH", "Modifica il percorso di origine del file utente"},
            {"EXIT", "Uscita"}            
        };
        
        for (int i = 0; i < cmd.length; i++) {
            System.out.printf("- %-20s %s\n",cmd[i][COMMAND],cmd[i][DESCRIPTION] );            
        }
    
    }
    
    /**
    * Print error message
    */
    public static void printError(){      
        System.out.println("Comando non valido");  
    }
    
    /**
     * Get the login status, if false write an error message
     * @return boolean
     */
    public boolean isLogged(){
        if(this.core.userIsLogged()){
            return true;
        } else {
            System.out.println("Eseguire prima il login per utilizzare questo comando");
            return false;
        }
    }
    
    /**
     * Start the CLI
     */
    public void start(){
        System.out.println("####################################");
        System.out.println("# Password Manager by Simone Vannucci  #");
        System.out.printf("# ver. %d                                                          #\n", CorePass.SWVERSION);
        System.out.println("####################################");
        
        this.getMenuPrinc();
    }

    public void getMenuPrinc() {    
        Boolean exit = false;
        System.out.println("Inserisci comando o ? ");
        do{    
                System.out.println("");
                String userName = "";
                if(this.core.userIsLogged()) userName = this.core.getUsername();
                String in = askUser(userName.toUpperCase()).toUpperCase();           
                int strlenght = in.length();           
                if( strlenght == 1 && in.charAt(0) == '?'){
                    printHelp();
                } else if( strlenght == 1 && in.charAt(0) == 'S'){
                    if(this.isLogged()) this.getSearch();
                } else if( strlenght >= 3){
                    if(in.equals("EXIT")){
                        exit = true;
                    } else if(in.equals("LOGIN")){
                        this.getLoginForm();
                    } else if(in.equals("NEWUSER")){
                        this.addUser();
                    } else if(in.equals("RESTORE")){
                        if(this.isLogged())this.restoreList();
                    }else if(in.equals("ADD")){
                        if(this.isLogged()) this.core.createBackup();
                        if(this.isLogged()) this.addSite();
                    } else if(in.equals("LIST")){
                        if(this.isLogged()) this.getSite(this.core.getList());
                    } else if(in.equals("MOD")){
                        if(this.isLogged()) this.core.createBackup();
                        if(this.isLogged()) this.modSiteMenu(this.core.getList());                    
                    } else if(in.equals("DEL")){
                        if(this.isLogged()) this.core.createBackup();
                        if(this.isLogged()) this.remSiteMenu(this.core.getList());
                    } else if(in.equals("SHOWPATH")){
                        System.out.println(" > " + this.core.getConfig().getUri());
                    } else if(in.equals("SETPATH")){
                        this.changePath();
                    } else {
                        printError();
                    }
                } else {
                    printError();
                }
            } while(!exit);
        }
    
    private void modSiteMenu(ArrayList<PassList> list){
        PassList item = new PassList();
        System.out.println("");
        if(list.size() >= 2){
            System.out.println("Inserisci indice del record da modificare o 0 per annullare");
            int val = Cli.askInt(0,list.size(),this.core.getUsername());
            if (val != 0) {
                item = this.modSiteForm();
                try {
                   this.core.modSite(item, val);
                }  catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | 
                   IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex) {
                Logger.getLogger(CorePass.class.getName()).log(Level.SEVERE, null, ex);
                }                               
            } else {
                System.out.println("Azione annullata");
            }
        } else {
            System.out.println("Nessun record da modificare");
        }
    }
    
    private PassList modSiteForm(){
        PassList item = new PassList();
        System.out.println("");
        System.out.println("Inserisci il nome del sito o lascia vuoto se non vuoi modificarlo");
        item.setSite(Cli.askUser(this.core.getUsername()));
        System.out.println("Inserisci user name o lascia vuoto se non vuoi modificarlo");
        item.setUser(Cli.askUser(this.core.getUsername()));
        System.out.println("Inserisci password o lascia vuoto se non vuoi modificarlo");
        item.setPass(Cli.askUser(this.core.getUsername()));
        System.out.println("Inserisci URL di login o lascia vuoto se non vuoi modificarlo");
        item.setUrl(Cli.askUser(this.core.getUsername()));
        return item;
    }
    
    private void remSiteMenu(ArrayList<PassList> list){
        PassList item = new PassList();
        System.out.println("");
        if(list.size() >= 2){
            System.out.println("Inserisci indice del record da cancellare, o 0 per annullare");
            int val = Cli.askInt(0,list.size(),this.core.getUsername());
            if (val != 0) {
                System.out.println("Sei sicuro? [S = si ; N = no]  ");
                String verif = Cli.askUser(this.core.getUsername(), '?');
                if ( verif.toUpperCase().equals("S")) {
                    try {
                        this.core.remSite(val);
                        System.out.println("Record cancellato correttamente");
                    }  catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | 
                        IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex) {
                    Logger.getLogger(CorePass.class.getName()).log(Level.SEVERE, null, ex);
                    }              
                } else {
                    System.out.println("Cancellazione annullata");
                }                
            } else {
                System.out.println("azione annullata");
            }
        } else {
            System.out.println("Nessun record da cancellare");
        }
    }
    
    private void addSite(){
        PassList item = new PassList();
        System.out.println("");
        System.out.println("Inserisci il nome del sito");
        item.setSite(Cli.askUser(this.core.getUsername()));
        System.out.println("Inserisci user name");
        item.setUser(Cli.askUser(this.core.getUsername()));
        System.out.println("Inserisci password");
        item.setPass(Cli.askUser(this.core.getUsername()));
        System.out.println("Inserisci URL di login");
        item.setUrl(Cli.askUser(this.core.getUsername()));
        try {
            this.core.addNewSite(item);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex) {
            Logger.getLogger(CorePass.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.getSite(this.core.getList());
    }
    
    private void getSearch(){
        System.out.println("");
        System.out.println("Cerca:");
        this.searchSite(this.core.getList(), Cli.askUser(this.core.getUsername() , '?'));
    }

    private void getLoginForm() {
        this.core.clearUserData();
        System.out.println("");
        System.out.println("Inserisci il tuo user");
        this.core.setUsername(Cli.askUser());
        System.out.println("Inserisci la tua password");
        this.core.setPassword(Cli.askUser());
        if(!this.core.login()){
            System.out.println("Errore nel caricamento del file o login non valido");
            this.core.clearUserData();
        }
    }

    private void addUser() {
        System.out.println("Inserisci il tuo user");
        this.core.setUsername(Cli.askUser());
        System.out.println("Inserisci la tua password");
        this.core.setPassword(Cli.askUser());
        System.out.println("Conserva questi dati in modo sicuro, pechè una volta persi non c'è "
                + "modo di recuperarli");
        if ( !this.core.addUser()) {
            System.out.println("Non è possibile creare un utente");           
        }
    }
    
    private void changePath(){
        System.out.println("Inserisci il percorso di origine del file");
        this.core.getConfig().changeUri(Cli.askUser());
        if(this.isLogged()) System.out.println("Per rendere effettiva la modifica devi rifare login");
    }
    
    public void getSite(ArrayList<PassList> list){
        //this.getHeadResult();
        int i = 0;
        for (PassList item : list) {
            if ( i > 0) {
               this.getSite( i, item.getSite(), item.getUser(), item.getPass(), item.getUrl());               
            }
            i++;
        }
    }
    
    public void getSite(int i, String site, String user, String pass, String url) {
        System.out.printf("| %5d | %50s | %50s | %50s | %50s |\n", i, site, user, pass, url);
    }
    
    private void searchSite(ArrayList<PassList> list, String key){
        //this.getHeadResult();
           int i = 0;
        for (PassList item : list) {
            if ( i > 0 && (item.getSite().startsWith(key) || item.getUser().startsWith(key) || item.getUrl().startsWith(key))) {
                this.getSite( i, item.getSite(), item.getUser(), item.getPass(), item.getUrl());               
            }
            i++;
        }
        if (i == 0) {
            System.out.println("Nessun risultato");
        }
    }

    public void getHeadResult() {
        String site = "Site name";
        String user = "User name";
        String pass = "Password";
        String url = "URL";
        System.out.println("+-----+--------------------------------------+"
                + "----------------------------------------------+"
                + "----------------------------------------------+"
                + "-----------------------------------------------+");
        System.out.printf("| %-5s | %-50s | %-50s | %-50s | %-50s |\n", "n", "Site name", "User name", "Password", "URL di login");
        //System.out.println("|       | Site name                              | User name                                     | Password                                       | URL                                                |");
        System.out.println("+-----+--------------------------------------+"
                + "----------------------------------------------+"
                + "----------------------------------------------+"
                + "-----------------------------------------------+");
    }
    
    private void restoreList(){
        if ( this.core.getBackupList() != null) {
            try {
                this.core.restoreList();
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex) {
                System.out.println("Errore nel ripristino");
                Logger.getLogger(Cli.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Lista ripristinata");           
        } else {
            System.out.println("Nessun backup presente");
        }
    }    
    
    public Cli() {
        this.core = new CorePass();
    }

}
