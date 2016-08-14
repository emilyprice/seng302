package seng302.Users;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import seng302.App;
import seng302.Environment;
import seng302.data.Term;
import seng302.utility.FileHandler;
import seng302.utility.OutputTuple;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jmw280 on 22/07/16.
 */
public class User {

    private String userFullName, userPassword, themeColor;


    private String userName;

    private Image profilePic;

    private ProjectHandler projectHandler;

    private Environment env;

    private JSONObject properties;

    private Date lastSignIn;

    private Path userDirectory;






    public User(String userName, String password, Environment env){
        userDirectory = Paths.get("UserData/"+userName);
        this.userName = userName;
        this.userPassword = password;
        this.env = env;



        properties = new JSONObject();

        createUserFiles();
        loadBasicProperties();
        saveProperties();

        Path filePath = Paths.get(this.userDirectory.toString()+"/profilePicture");
        try {
            URI defaultPath = getClass().getResource("/images/testDP.jpg").toURI();
            FileHandler.copyFolder(new File(defaultPath), filePath.toFile());
        }
        catch (Exception e){
            e.printStackTrace();

        }
        this.profilePic = new Image(userDirectory.toUri() + "/profilePicture");

        projectHandler = new ProjectHandler(env, userName);
        //loadFullProperties();
    }

    /**
     * Loads basic user properties (Picture, Name, Password etc.)
     * @param env
     * @param user user name
     */
    public User(Environment env, String user){
        userDirectory = Paths.get("UserData/"+user);
        this.env = env;
        this.userName = user;
        properties = new JSONObject();
        loadBasicProperties();
        this.profilePic = new Image(userDirectory.toUri() + "/profilePicture");


    }

    public void loadFullProperties(){
        /**
         * Current Theme
         * Musical Terms
         * Project Handler
         *
         */

        //Load musical terms property
        Gson gson = new Gson();
        Type termsType = new TypeToken<ArrayList<Term>>() {
        }.getType();
        ArrayList<Term> terms = gson.fromJson((String) properties.get("musicalTerms"), termsType);

        if (terms != null) {

            env.getMttDataManager().setTerms(terms);
        }

        projectHandler = new ProjectHandler(env, userName);




    }


    public ProjectHandler getProjectHandler(){
        System.out.println("project handler: " + projectHandler.toString());
        return projectHandler;}

    private void loadBasicProperties() {
        /**
         * Properties:
         *  PhotoID - Stored as default 'userPicture.png'
         *  Last sign in time
         *  name
         *  Password
         *  Musical terms
         *  Maybe default tempo?
         *  Theme
         *
         */
        Gson gson = new Gson();
        Path userDirectory = Paths.get("UserData/"+userName); //Default user path for now, before user compatibility is set up.
        JSONParser parser = new JSONParser(); //parser for reading project
        try {
            properties = (JSONObject) parser.parse(new FileReader(userDirectory + "/user_properties.json"));
        }catch(Exception e){

        }



        //env.getTranscriptManager().unsavedChanges = false;


        try {
            Type dateType = new TypeToken<Date>() {
            }.getType();
            lastSignIn = gson.fromJson((String) properties.get("signInTime"), dateType);

            if(lastSignIn == null)  lastSignIn = new Date();
        }catch(Exception e){
            lastSignIn = new Date();

        }

        try {
            //name
            userFullName = (properties.get("fullName")).toString();
        }catch (NullPointerException e){
            userFullName = userName;
        }


        //Password
        userPassword = (properties.get("password")).toString();


        try {
            //Theme
            themeColor = (properties.get("themeColor")).toString();
        }catch(NullPointerException e){
            themeColor = "white";
        }
    }


    private void updateProperties() {
        Gson gson = new Gson();
        properties.put("userName", userName);
        properties.put("fullName", userFullName);
        properties.put("password", this.userPassword);
        properties.put("themeColor", this.themeColor);


        String musicalTermsJSON = gson.toJson(env.getMttDataManager().getTerms());
        properties.put("musicalTerms", musicalTermsJSON);

        String lastSignInJSON = gson.toJson(lastSignIn);
        properties.put("signInTime", lastSignInJSON);



    }

    private void saveProperties(){
        try {
            Gson gson = new Gson();
            updateProperties();


            FileWriter file = new FileWriter(userDirectory + "/user_properties.json");
            file.write(properties.toJSONString());
            file.flush();
            file.close();


        } catch (IOException e) {

            e.printStackTrace();
        }
        /*catch (FileNotFoundException e) {
            try {
                System.err.println("user_properties.json Does not exist! - Creating new one");
                properties = new JSONArray();


                projectsInfo.put("projects", projectList);

                if (!Files.isDirectory(userDirectory)) {
                    //Create Projects path doesn't exist.
                    try {
                        Files.createDirectories(userDirectory);


                    } catch (IOException eIO3) {
                        //Failed to create the directory.
                        System.err.println("Well UserData directory failed to create.. lost cause.");
                    }
                }

                FileWriter file = new FileWriter(userDirectory + "/projects.json");
                file.write(projectsInfo.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e2) {
                System.err.println("Failed to create projects.json file.");

            }
            */


    }


    private void createUserFiles(){
        //Add all settings to such as tempo speed to the project here.


        Path path;
        try {
            //path = Paths.get("UserData/" + userName + "/" + resultString);

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
            //createNewProject();
        }


    }


    public String getUserPassword(){return userPassword;}

    public String getUserName(){return  userName;}


    public void setUserPicture(Image image) {
        this.profilePic = image;
    }

    public Image getUserPicture() {
        return this.profilePic;
    }












}