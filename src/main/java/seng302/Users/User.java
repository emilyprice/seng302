package seng302.Users;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javafx.scene.image.Image;
import seng302.Environment;
import seng302.data.Term;
import seng302.utility.FileHandler;

/**
 * Handles functionality for representing and manipulating a user's information. Also handles saving
 * and loading users.
 */
public class User {

    private String userFullName, userPassword, themePrimary, themeSecondary;

    private String userName;

    private Path profilePicPath;

    private ProjectHandler projectHandler;

    private Environment env;

    private HashMap properties = new HashMap();

    private Date lastSignIn;

    private Path userDirectory;

    private String userFirstName;

    private String userLastName;

    private DataSnapshot userSnapshot;


    /**
     * User constructor used for generating new users.
     *
     * @param userName username
     * @param password password to set for the corresponding user.
     */
    public User(String userName, String password, Environment env) {

        env.getFirebase().createUserSnapshot(env.getUserHandler().getClassRoom(), userName, true);
        System.out.println(env.getFirebase().getUserSnapshot());

        userSnapshot = env.getFirebase().getUserSnapshot();

        userDirectory = Paths.get("UserData/" + userName);
        this.userName = userName;
        this.userPassword = password;
        this.env = env;
        //properties = new JSONObject();

        createUserFiles();
        //loadBasicProperties();
        loadProperties();
        saveProperties();
//
//        Path filePath = Paths.get(this.userDirectory.toString() + "/profilePicture");
//        try {
//            URI defaultPath = getClass().getResource("/images/testDP.jpg").toURI();
//            FileHandler.copyFolder(new File(defaultPath), filePath.toFile());
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//        profilePicPath = Paths.get(userDirectory.toString() + "/profilePicture");

        projectHandler = new ProjectHandler(env, userName);

    }

    /**
     * Loads basic user properties (Picture, Name, Password etc.) Used when loading a collection of
     * users (Login screen) This should only load properties which NEED to be loaded before the user
     * logs in.
     *
     * @param user user name
     */
    public User(Environment env, String user) {
        userDirectory = Paths.get("UserData/classrooms/group5/users/" + user);
        this.env = env;
        this.userName = user;
        //properties = new JSONObject();
        //loadBasicProperties();
       // loadProperties();
        profilePicPath = Paths.get(userDirectory.toString() + "/profilePicture");


    }

    /**
     * loads extensive user properties (after user login) This should load all properties which
     * aren't neccessary before the user logs in.
     */
    public void loadFullProperties() {
        /**
         * Current Theme
         * Musical Terms
         * Full name
         * Project Handler
         * Theme
         */

        //Load musical terms property
        Gson gson = new Gson();
        Type termsType = new TypeToken<ArrayList<Term>>() {
        }.getType();
        ArrayList<Term> terms = gson.fromJson((String) properties.get("musicalTerms"), termsType);
        System.out.println("full properties: ");
        System.out.println(properties);

        if (terms != null) {
            env.getMttDataManager().setTerms(terms);
        }

        try {
            userFirstName = (properties.get("firstName")).toString();
        } catch (NullPointerException e) {
            userFirstName = "";
        }

        try {
            userLastName = (properties.get("lastName")).toString();
        } catch (NullPointerException e) {
            userLastName = "";
        }

        try {
            //Theme
            themePrimary = (properties.get("themePrimary")).toString();
        } catch (NullPointerException e) {
            System.err.println("theme doesn't exist - setting default.");
            themePrimary = "#1E88E5";
        }

        try {
            //Theme
            themeSecondary = (properties.get("themeSecondary")).toString();
        } catch (NullPointerException e) {
            themeSecondary = "white";
        }
        lastSignIn = new Date();



        projectHandler = new ProjectHandler(env, userName);

    }


    private void loadProperties(){
        /**
         * Current Theme
         * Musical Terms
         * Full name
         * Project Handler
         * Theme
         */


        properties = (HashMap<String,String>) userSnapshot.child("properties").getValue();

        if(properties == null) properties = new HashMap<String, Object>();



        System.out.println("user properties: " + properties);
        Gson gson = new Gson();
        try {
            Type dateType = new TypeToken<Date>() {
            }.getType();
            lastSignIn = gson.fromJson((String) properties.get("signInTime"), dateType);

            if (lastSignIn == null) lastSignIn = new Date();
        } catch (Exception e) {
            lastSignIn = new Date();

        }

        //Password
        //serPassword = (properties.get("password")).toString();

/*
        try {
            //Theme
            themePrimary = (properties.get("themeColor")).toString();
        } catch (NullPointerException e) {
            themePrimary = "white";
            System.out.println("theme primary not ");
        }
*/
        //Load musical terms property
       // Gson gson = new Gson();
        Type termsType = new TypeToken<ArrayList<Term>>() {
        }.getType();
        ArrayList<Term> terms = new ArrayList<>();
        try{
            terms = gson.fromJson((String) properties.get("musicalTerms"), termsType);
        }catch(NullPointerException n){

        }

        if (terms != null) {
            env.getMttDataManager().setTerms(terms);
        }

        try {
            userFirstName = (properties.get("firstName")).toString();
        } catch (NullPointerException e) {
            userFirstName = "";
        }

        try {
            userLastName = (properties.get("lastName")).toString();
        } catch (NullPointerException e) {
            userLastName = "";
        }

        try {
            //Theme
            themePrimary = (properties.get("themePrimary")).toString();
            System.out.println("theme primary: " + themePrimary);
        } catch (NullPointerException e) {
            themePrimary = "#1E88E5";
        }

        try {
            //Theme
            themeSecondary = (properties.get("themeSecondary")).toString();
        } catch (NullPointerException e) {
            themeSecondary = "white";
        }
        env.getThemeHandler().setTheme(themePrimary, themeSecondary);


        lastSignIn = new Date();

        projectHandler = new ProjectHandler(env, userName);
    }

    /**
     * This needs to be called to unlock the project folders to allow them to be deleted.
     */
    public void delete() {

        this.projectHandler = null;

    }


    public ProjectHandler getProjectHandler() {

        return projectHandler;
    }

    /**
     * Loads basic properties which need be read by the login screen.
     */
    private void loadBasicProperties() {
        /**
         * Basic properties:
         *  PhotoID - Stored as default 'userPicture.png'
         *  Last sign in time
         *  username
         *  Password
         *
         */
        System.out.println("ss: " + userSnapshot.child("properties"));

        properties = (HashMap<String,Object>) userSnapshot.child("properties").getValue();
        System.out.println(properties);
        Gson gson = new Gson();
        try {
            Type dateType = new TypeToken<Date>() {
            }.getType();
            lastSignIn = gson.fromJson((String) properties.get("signInTime"), dateType);

            if (lastSignIn == null) lastSignIn = new Date();
        } catch (Exception e) {
            lastSignIn = new Date();

        }

        //Password
        userPassword = (properties.get("password")).toString();


        try {
            //Theme
            themePrimary = (properties.get("themeColor")).toString();
        } catch (NullPointerException e) {
            themePrimary = "white";
        }



    }


    /**
     * Updates project property JSON files to be written to disc.
     */
    public void updateProperties() {
        Gson gson = new Gson();
        System.out.println("update properties 2");
        System.out.println(properties);

        properties.put("userName", userName);
        properties.put("fullName", userFullName);
        properties.put("password", this.userPassword);
        properties.put("themePrimary", env.getThemeHandler().getPrimaryColour());
        properties.put("themeSecondary", env.getThemeHandler().getSecondaryColour());
        properties.put("firstName", this.userFirstName);
        properties.put("lastName", this.userLastName);
        String musicalTermsJSON = gson.toJson(env.getMttDataManager().getTerms());
        properties.put("musicalTerms", musicalTermsJSON);
        String lastSignInJSON = gson.toJson(lastSignIn);
        properties.put("signInTime", lastSignInJSON);


    }

    /**
     * Writes JSON properties to disc
     */
    public void saveProperties() {

        updateProperties();
        System.out.println("saveProeprties: " + properties);
        env.getFirebase().getUserRef().child("properties").updateChildren(properties);

    }


    /**
     * Creates user directory files.
     */
    private void createUserFiles() {
        //Add all settings to such as tempo speed to the project here.
        System.out.println("added properties");
        env.getFirebase().getUserRef().child("properties");
        /*
        try {

            if (!Files.isDirectory(userDirectory)) {
                try {
                    Files.createDirectories(userDirectory);
                    saveProperties();

                } catch (IOException e) {
                    //Failed to create the directory.
                    e.printStackTrace();
                }

            } else {
                env.getRootController().errorAlert("The user " + userName + " already exists.");
                //createNewProject();
            }

        } catch (InvalidPathException invPath) {
            //invalid path (Poor project naming)
            env.getRootController().errorAlert("Invalid file name - try again.");

        }
        */


    }


    /**
     * Checking functionality specifically for musical saved musical terms.
     */
    public void checkMusicTerms() {
        if (properties.containsKey("musicalTerms")) {
            Type termsType = new TypeToken<ArrayList<Term>>() {
            }.getType();
            if (!properties.get("musicalTerms").equals(new Gson().fromJson((String) properties.get("muscalTerms"), termsType))) {
                env.getRootController().setWindowTitle(env.getRootController().getWindowTitle() + "*");
                getProjectHandler().getCurrentProject().saved = false;
            }
        } else {
            if (env.getRootController() != null) {
                env.getRootController().setWindowTitle(env.getRootController().getWindowTitle() + "*");
                getProjectHandler().getCurrentProject().saved = false;
            }

        }


    }

    /**
     * Returns the users's persisted theme colours. Used when setting the ThemeHandler colours to
     * the user's persisted colours.
     *
     * @return an Array containing two elements: primary and secondary theme colours.
     */
    public String[] getThemeColours() {
        return new String[]{themePrimary, themeSecondary};
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserPicture(Path imagePath) {
        this.profilePicPath = imagePath;
    }

    public Image getUserPicture() {
        return new Image(profilePicPath.toUri().toString());
    }

    public void setUserFirstName(String name) {
        userFirstName = name;
    }

    public void setUserLastName(String name) {
        userLastName = name;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public int getUserExperience() {
        return getProjectHandler().getCurrentProject().getExperience();
    }

    public int getUserLevel() {
        return getProjectHandler().getCurrentProject().getLevel();
    }

}
