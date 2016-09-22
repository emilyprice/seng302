package seng302.Users;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import seng302.Environment;
import seng302.data.Term;

import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Jonty on 23-Sep-16.
 */
public class Student extends User2 {

    private ProjectHandler projectHandler;
    private Date lastSignIn;

    public Student(String userName, String password, Environment env) {

        env.getFirebase().createStudentSnapshot(env.getUserHandler().getClassRoom(), userName, true);
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
        projectHandler = new ProjectHandler(env, userName);

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
            Type dateType = new TypeToken<Date>() {
            }.getType();
            lastSignIn = gson.fromJson((String) properties.get("signInTime"), dateType);

            if (lastSignIn == null) lastSignIn = new Date();
        } catch (Exception e) {
            lastSignIn = new Date();

        }

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


        lastSignIn = new Date();

        projectHandler = new ProjectHandler(env, userName);
    }

    @Override
    public void updateProperties() {
        Gson gson = new Gson();
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
        properties.put("profilePicUrl", profilePicUrl);
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
     * This needs to be called to unlock the project folders to allow them to be deleted.
     */
    public void delete() {
        this.projectHandler = null;
    }


    public ProjectHandler getProjectHandler() {
        return projectHandler;
    }

    public int getUserExperience() {
        return getProjectHandler().getCurrentProject().getExperience();
    }

    public int getUserLevel() {
        return getProjectHandler().getCurrentProject().getLevel();
    }


}
