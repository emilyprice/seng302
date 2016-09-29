package seng302.utility;

import com.google.gson.Gson;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import seng302.Environment;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class used to import local user profiles from previous versions of the application before
 * cloud storage.
 */
public class UserImporter {
    static HashMap<String, String> converted = new HashMap<>();

    static{

        converted.put("MusicalTermsTutor.json", "musicalTermsTutor");
        converted.put("PitchComparisonTutor.json", "PitchComparisonTutor");
        converted.put("DiatonicChordTutor.json", "diatonicChordTutor");
        converted.put("ScaleRecognitionTutor.json", "basicScaleTutor");
        converted.put("ChordRecognitionTutor.json", "basicChordTutor");
        converted.put("IntervalRecognitionTutor.json", "intervalTutor");
        converted.put("ChordSpellingTutor.json", "chordSpellingTutor");
        converted.put("ScaleSpellingTutor.json", "scaleSpellingTutor");
        converted.put("KeySignatureTutor.json", "keySignatureTutor");
        converted.put("DiatonicChordsTutor.json", "diatonicChordTutor");

        converted.put("ScaleModesTutor.json", "scaleModesTutor");

    }

    /**
     * Imports locally stored user data into firebase
     * @param classroom The classroom to import the user into
     * @param env The environment the application runs in
     * @param stage The stage on which to show the directory chooser
     */
    public static String[] importUser(Environment env, String classroom, Window stage) {
        DirectoryChooser dirChooser = new DirectoryChooser();

        dirChooser.setTitle("Select a project directory");
        Path path = Paths.get("/");

        dirChooser.setInitialDirectory(path.toFile());

        File folder = dirChooser.showDialog(stage);
        String[] ret;

        if (validUserFolder(folder)) {
            if (!env.getUserHandler().getUserNames().contains(folder.getName())) {
                ret = uploadUserProperties(env, classroom, folder);
                uploadUserProjects(env, new File(folder.getPath() + "/Projects"));
                return ret;
            } else {
                System.err.println("Classroom already contains a user of the selected username");
                env.getRootController().errorAlert("Could not import the given user." +
                        "\nA user with the given username already exists!");
            }

        }
        return new String[]{};

    }


    /**
     * Uploads the specified user's data to firebase.
     *
     * @param env  Environment
     * @param path User folder path.
     */
    public static String[] uploadUserProperties(Environment env, String classroom, File path) {

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
        userPassword = (properties.get("password")).toString();

        env.getFirebase().createStudentSnapshot(classroom, userName, true);
        env.getFirebase().getUserRef().child("properties").updateChildren(properties);

        return new String[] {userName, userPassword};


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

                    JSONArray tutorRecords = (JSONArray) parser.parse(new FileReader(f.getPath() + "/" + projectFile.getName()));
//
                        //tutorData.put(projectFile.getName(), tutorRecords);
                    for(Object record: tutorRecords){
                        TutorRecord rclass = new TutorRecord();

                        JSONObject r = (JSONObject) record;
                        List questionList = new ArrayList<>();
                        JSONArray questions = (JSONArray) r.get("questions");

                        for(Object questionMap : questions ){
                            HashMap<String, Object> question = (HashMap<String, Object>) questionMap;
                            questionList.add(question);

                        }
                        HashMap<String, Number> stats = (HashMap) r.get("stats");
                        rclass.setStats(stats);

                        String dateString = (String) r.get("date");

                        DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                        Date d = df.parse(dateString);
                        rclass.setDate(d);

                        rclass.setQuestions(questionList);
                        env.getFirebase().getUserRef().child("projects/" + f.getName()+"/" + converted.get(projectFile.getName()) +"/"+rclass.getDate().getTime()).setValue(rclass);
                        //tutorData.put(projectFile.getName(), rclass);



                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Could not load the tutor properties for tutor: " + projectFile.getName());
                }

            }
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
