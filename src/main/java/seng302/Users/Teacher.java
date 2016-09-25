

package seng302.Users;

import com.google.gson.Gson;

import seng302.Environment;


import java.util.HashMap;

/**
 * Created by Jonty on 23-Sep-16.
 */


public class Teacher extends User {


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


    /**
     * Loads user properties, and any properties associated with teachers only.
     */
    public void loadProperties() {

        super.loadProperties();


    }


    public void updateProperties() {

        super.updateProperties();
    }







}
