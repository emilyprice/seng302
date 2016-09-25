package seng302.utility;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import javafx.stage.Window;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import seng302.Environment;
import seng302.data.Term;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jonty on 25-Sep-16.
 */
public class UserImporter {

    public static void importUser(Environment env, Window stage){
        DirectoryChooser dirChooser = new DirectoryChooser();

        dirChooser.setTitle("Select a project directory");
        Path path = Paths.get("/");

        dirChooser.setInitialDirectory(path.toFile());

        File folder = dirChooser.showDialog(stage);

        if(validProjectFolder(folder)){
            System.out.println("valid profile!!");

            uploadUserProperties(env, folder);
            //uploadProjectProperties(env, folder);
        }



    }


    /**
     * Loads
     * @param env
     * @param path
     */
    public static void uploadUserProperties(Environment env, File path) {

        String userFirstName, userLastName, userName, themePrimary, themeSecondary,  userPassword;
        Date lastSignIn;
        JSONObject properties = new JSONObject();
        Gson gson = new Gson();
        Path userDirectory = path.toPath(); //Default user path for now, before user compatibility is set up.
        JSONParser parser = new JSONParser(); //parser for reading project
        try {
            properties = (JSONObject) parser.parse(new FileReader(userDirectory + "/user_properties.json"));
        } catch (Exception e) {

        }

        try {
            Type dateType = new TypeToken<Date>() {
            }.getType();
            lastSignIn = gson.fromJson((String) properties.get("signInTime"), dateType);

            if (lastSignIn == null) lastSignIn = new Date();
        } catch (Exception e) {
            lastSignIn = new Date();

        }


        userPassword = (properties.get("password")).toString();


        //Load musical terms property

        Type termsType = new TypeToken<ArrayList<Term>>() {
        }.getType();
        ArrayList<Term> terms = gson.fromJson((String) properties.get("musicalTerms"), termsType);


        userName = (properties.get("userName")).toString();



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
            themePrimary = "#1E88E5";
        }

        try {
            //Theme
            themeSecondary = (properties.get("themeSecondary")).toString();
        } catch (NullPointerException e) {
            themeSecondary = "white";
        }


        env.getFirebase().createStudentSnapshot("test", userName, true);
        env.getFirebase().getUserRef().child("properties").updateChildren(properties);
        //env.getFirebase().getUserRef().child("projects").updateChildren();

//        projectHandler = new ProjectHandler(env, userName);

    }






    private static Boolean validProjectFolder(File folder){

        if (folder != null) {
            if (folder.isDirectory()) {

                boolean user_properties = true, projectsFolder = false;


                for (File f : folder.listFiles()) {

                    if (f.getName().endsWith(".json") && f.getName().substring(0, f.getName().length() - 5).equals("user_properties")) {
                        user_properties = true;


                    }
                    else if(f.isDirectory() && f.getName().equals("Projects")){
                        projectsFolder = true;
                    }

                }

                if(user_properties && projectsFolder ){
                    return true;
                }


            }

        }
        return false;
    }


}
