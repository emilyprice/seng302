package seng302.utility;

import com.google.gson.Gson;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import seng302.Environment;

/**
 * Utility class used to import local user profiles from previous versions of the application before
 * cloud storage.
 */
public class UserImporter {

    /**
     * Imports locally stored user data, fo
     */
    public static void importUser(Environment env, String classroom, Window stage) {
        DirectoryChooser dirChooser = new DirectoryChooser();

        dirChooser.setTitle("Select a project directory");
        Path path = Paths.get("/");

        dirChooser.setInitialDirectory(path.toFile());

        File folder = dirChooser.showDialog(stage);

        if (validUserFolder(folder)) {
            if (!env.getUserHandler().getUserNames().contains(folder.getName())) {
                uploadUserProperties(env, classroom, folder);
                uploadUserProjects(env, new File(folder.getPath() + "/Projects"));
            } else {
                System.err.println("Classroom already contains a user of the selected username");
                env.getRootController().errorAlert("Could not import the given user." +
                        "\nA user with the given username already exists!");
            }

        }

    }


    /**
     * Uploads the specified user's data to firebase.
     *
     * @param env  Environment
     * @param path User folder path.
     */
    public static void uploadUserProperties(Environment env, String classroom, File path) {

        String userFirstName, userLastName, userName, themePrimary, themeSecondary, userPassword;
        Date lastSignIn;
        JSONObject properties = new JSONObject();
        Gson gson = new Gson();
        Path userDirectory = path.toPath(); //Default user path for now, before user compatibility is set up.
        JSONParser parser = new JSONParser(); //parser for reading project
        try {
            properties = (JSONObject) parser.parse(new FileReader(userDirectory + "/user_properties.json"));
        } catch (Exception e) {

        }

        userName = (properties.get("userName")).toString();

        env.getFirebase().createStudentSnapshot(classroom, userName, true);
        env.getFirebase().getUserRef().child("properties").updateChildren(properties);


    }


    /**
     * Uploads a user's project data, given the user's project directory.
     *
     * @param env Environment
     * @param f   User project directory. (User -> Projects -> Project)
     */
    private static void uploadProjectData(Environment env, File f) {

        HashMap<String, Object> tutorData = new HashMap<>();
        JSONObject projectSettings = new JSONObject();
        JSONParser parser = new JSONParser();

        for (File projectFile : f.listFiles()) {

            if (projectFile.getName().endsWith(".json") && projectFile.getName().substring(0, projectFile.getName().length() - 5).equals(f.getName())) {
                try {

                    projectSettings = (JSONObject) parser.parse(new FileReader(f.getPath() + "/" + projectFile.getName()));

                } catch (Exception e) {
                    System.err.println("Could not load the project properties for project: " + projectFile.getName());
                }

            } else if (projectFile.getName().endsWith(".json")) {
                //Handle tutor data upload
                try {
                    JSONObject tutorRecords = (JSONObject) parser.parse(new FileReader(f.getPath() + "/" + projectFile.getName()));
                    tutorData.put(projectFile.getName(), tutorRecords);

                } catch (Exception e) {
                    System.err.println("Could not load the tutor properties for tutor: " + projectFile.getName());
                }

            }
        }
        for (String key : tutorData.keySet()) {
            projectSettings.put(key, tutorData.get(key));

        }
        env.getFirebase().getUserRef().child("projects/" + f.getName()).updateChildren(projectSettings);

    }

    /**
     * Uploads the selected user's projects to firebase.
     *
     * @param env            Environment
     * @param projectsFolder the User -> Projects folder.
     */
    private static void uploadUserProjects(Environment env, File projectsFolder) {


        JSONParser parser = new JSONParser();

        String path = projectsFolder.toString();

        for (File f : projectsFolder.listFiles()) {
            if (f.isDirectory()) { //Is a project directory.
                uploadProjectData(env, f);

            }

        }


    }

    /**
     * Determines whether a given folder is a valid User folder or not.
     *
     * @param folder User folder.
     * @return True if the given folder contains a projects folder, and user_properties.json.
     */
    private static Boolean validUserFolder(File folder) {

        if (folder != null) {
            if (folder.isDirectory()) {
                boolean user_properties = true, projectsFolder = false;

                for (File f : folder.listFiles()) {

                    if (f.getName().endsWith(".json") && f.getName().substring(0, f.getName().length() - 5).equals("user_properties")) {
                        user_properties = true;

                    } else if (f.isDirectory() && f.getName().equals("Projects")) {
                        projectsFolder = true;
                    }

                }

                if (user_properties && projectsFolder) {
                    return true;
                }

            }

        }
        return false;
    }


}
