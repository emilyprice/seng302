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
public class Student extends User {

    private ProjectHandler projectHandler;
    private Date lastSignIn;

    public Student(String userName, String password, Environment env) {

        env.getFirebase().createStudentSnapshot(env.getUserHandler().getClassRoom(), userName, true);
        userSnapshot = env.getFirebase().getUserSnapshot();

        userDirectory = Paths.get("UserData/" + userName);
        this.userName = userName;
        this.userPassword = password;
        this.env = env;

        loadProperties();
        saveProperties();
        projectHandler = new ProjectHandler(env, userName);

    }


    /**
     * Loads user properties, and any properties associated with students only.
     */
    public void loadProperties() {

        super.loadProperties();

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

        lastSignIn = new Date();

        projectHandler = new ProjectHandler(env, userName);
    }


    public void updateProperties() {
        super.updateProperties();
        Gson gson = new Gson();
        String musicalTermsJSON = gson.toJson(env.getMttDataManager().getTerms());
        properties.put("musicalTerms", musicalTermsJSON);
        String lastSignInJSON = gson.toJson(lastSignIn);
        properties.put("signInTime", lastSignInJSON);
        properties.put("profilePicUrl", profilePicUrl);
    }

    public void saveAll(){
        saveProperties();
        getProjectHandler().getCurrentProject().saveCurrentProject();
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
               saveAll();
            }
        } else {
            if (env.getRootController() != null) {
                env.getRootController().setWindowTitle(env.getRootController().getWindowTitle() + "*");
                //getProjectHandler().getCurrentProject().saved = false;
                saveAll();
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
