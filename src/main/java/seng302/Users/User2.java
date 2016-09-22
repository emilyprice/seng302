package seng302.Users;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import seng302.Environment;
import seng302.data.Term;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javafx.scene.image.Image;
import seng302.Environment;
import seng302.data.Term;

/**
 * Handles functionality for representing and manipulating a user's information. Also handles saving
 * and loading users.
 */
public abstract class User2 {

    String userFullName, userPassword, themePrimary, themeSecondary;

    String userName;

    Environment env;

    HashMap properties = new HashMap();

    private Date lastSignIn;

    Path userDirectory;

    String userFirstName;

    String userLastName;

    DataSnapshot userSnapshot;

    String profilePicUrl;

    public User2(){

    }



    public abstract void loadProperties();






    /**
     * Updates project property JSON files to be written to disc.
     */
    public abstract void updateProperties();
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
