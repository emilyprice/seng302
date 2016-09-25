package seng302.Users;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import seng302.Environment;

import java.nio.file.Path;

import java.util.Date;
import java.util.HashMap;


/**
 * Handles functionality for representing and manipulating a user's information. Also handles saving
 * and loading users.
 */
public abstract class User {

    String  userPassword, themePrimary, themeSecondary, userFirstName, userLastName,  profilePicUrl, userName;


    Environment env;

    HashMap properties = new HashMap();

    Path userDirectory;

    DataSnapshot userSnapshot;


    public User(){

    }


    /**
     * Loads the basic properties of a user from firebase.
     */
    public void loadProperties(){
        /**
         * Theme Primary/Secondary.
         * User name
         * Full name
         * Profile picture
         */

        properties = (HashMap<String,String>) userSnapshot.child("properties").getValue();

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





    /**
     * Updates the properties to be saved to firebase.
     */
    public  void updateProperties(){

        properties.put("userName", userName);
        properties.put("password", this.userPassword);
        properties.put("themePrimary", env.getThemeHandler().getPrimaryColour());
        properties.put("themeSecondary", env.getThemeHandler().getSecondaryColour());
        properties.put("firstName", this.userFirstName);
        properties.put("lastName", this.userLastName);
        properties.put("profilePicUrl", profilePicUrl);
    }
    /**
     * Writes JSON properties to disc
     */
    public void saveProperties() {

        updateProperties();

        env.getFirebase().getUserRef().child("properties").updateChildren(properties);

    }


    /**
     * Creates user directory files.
     */
    protected void createUserFiles() {

        env.getFirebase().getUserRef().child("properties");

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

    public void setUserPicture(String imageUrl) {
        this.profilePicUrl = imageUrl;
    }

    public Image getUserPicture() {
        return new Image(this.profilePicUrl);
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



}
