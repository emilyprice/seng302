

package seng302.Users;

import com.google.gson.Gson;

import seng302.Environment;


import java.util.Date;
import java.util.HashMap;

/**
 * Created by Jonty on 23-Sep-16.
 */


public class Teacher extends User2 {


    public Teacher(String userName, String password, Environment env) {

        env.getFirebase().createTeacherSnapshot(userName, true);
        userSnapshot = env.getFirebase().getUserSnapshot();

        //userDirectory = Paths.get("UserData/" + userName);
        this.userName = userName;
        this.userPassword = password;
        this.env = env;
        //properties = new JSONObject();

        createUserFiles();
        //loadBasicProperties();
        loadProperties();
        saveProperties();

    }




    @Override
    public void loadProperties() {
        /**
         * Current Theme
         * Musical Terms
         * Full name
         * Project Handler
         * Theme
         */

        properties = (HashMap<String,String>) userSnapshot.child("properties").getValue();

        if(properties == null) properties = new HashMap<String, Object>();

        Gson gson = new Gson();

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

        try {
            //profile pic
            profilePicUrl = (properties.get("profilePicUrl")).toString();
        } catch (NullPointerException e) {
            profilePicUrl = "http://res.cloudinary.com/allegro123/image/upload/v1474434800/testDP_qmwncc.jpg";
        }
        env.getThemeHandler().setTheme(themePrimary, themeSecondary);

    }

    @Override
    public void updateProperties() {
        Gson gson = new Gson();
        properties.put("userName", userName);

        properties.put("password", this.userPassword);
        properties.put("themePrimary", env.getThemeHandler().getPrimaryColour());
        properties.put("themeSecondary", env.getThemeHandler().getSecondaryColour());
        properties.put("firstName", this.userFirstName);
        properties.put("lastName", this.userLastName);

        properties.put("profilePicUrl", profilePicUrl);
    }




}
