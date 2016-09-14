package seng302.Users;

/**
 * ProjectHandler
 *
 * In charge of handling user project data, including saving, loading and validating.
 *
 *
 * Created by Jonty on 12-Apr-16.
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.controlsfx.control.Notifications;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.Instrument;

import seng302.data.Badge;
import seng302.managers.BadgeManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import seng302.Environment;
import seng302.utility.InstrumentUtility;
import seng302.utility.LevelCalculator;
import seng302.utility.OutputTuple;

public class Project {

    JSONObject projectSettings;

    JSONParser parser = new JSONParser(); //parser for reading project

    ProjectHandler projectHandler;

    // Levels variables, will be updated as the user gains experience

    private Integer experience;
    private Integer level;

    Path projectDirectory;
    public String currentProjectPath, projectName;

    private seng302.managers.BadgeManager badgeManager;
    boolean saved = true;

    Environment env;
    public TutorHandler tutorHandler;

    public Boolean isCompetitiveMode;

    /**
     * Constructor for creating a new project.
     *
     * @param env         The environment in which the project is being created
     * @param projectName What the project will be called
     * @param projectH    The ProjectHandler object which will manage this project
     */
    public Project(Environment env, String projectName, ProjectHandler projectH) {
        this.projectName = projectName;
        this.projectDirectory = Paths.get(projectH.projectsDirectory + "/" + projectName);
        this.env = env;
        projectSettings = new JSONObject();
        tutorHandler = new TutorHandler(env);
        projectHandler = projectH;
        isCompetitiveMode = true;
        this.experience = 0;
        this.level = 1;
        badgeManager = new BadgeManager(env);

        loadProject(projectName);
        loadProperties();


    }


    public TutorHandler getTutorHandler() {
        return tutorHandler;
    }


    /**
     * Updates the Project properties variable to contain the latest project settings (Does not
     * write to disk)
     */
    private void saveProperties() {
        Gson gson = new Gson();
        projectSettings.put("tempo", env.getPlayer().getTempo());
        String transcriptString = gson.toJson(env.getTranscriptManager().getTranscriptTuples());

        projectSettings.put("transcript", transcriptString);


        projectSettings.put("rhythm", gson.toJson(env.getPlayer().getRhythmHandler().getRhythmTimings()));

        projectSettings.put("instrument", gson.toJson(env.getPlayer().getInstrument().getName()));

        // User level for current project
        projectSettings.put("level", this.level);
        projectSettings.put("experience", this.experience);


        projectSettings.put("overallBadges", gson.toJson(badgeManager.getOverallBadges()));
        projectSettings.put("tutorBadges", gson.toJson(badgeManager.getTutorBadges()));
        projectSettings.put("tutor100Map", gson.toJson(badgeManager.get100TutorBadges()));



    }


    /**
     * load all saved project properties from the project json file. This currently supports Tempo,
     * working transcript and rhythm setting.
     */
    private void loadProperties() {
        int tempo;
        Gson gson = new Gson();

        try {
            tempo = ((Long) projectSettings.get("tempo")).intValue();
        } catch (Exception e) {
            tempo = 120;
        }

        env.getPlayer().setTempo(tempo);


        //Transcript
        ArrayList<OutputTuple> transcript;
        Type transcriptType = new TypeToken<ArrayList<OutputTuple>>() {
        }.getType();
        transcript = gson.fromJson((String) projectSettings.get("transcript"), transcriptType);

        env.getTranscriptManager().setTranscriptContent(transcript);
        env.getRootController().setTranscriptPaneText(env.getTranscriptManager().convertToText());


        //Rhythm
        int[] rhythms;


        try {
            rhythms = ((int[]) gson.fromJson((String) projectSettings.get("rhythm"), int[].class));
            rhythms = rhythms == null ? new int[]{12} : rhythms;
        } catch (Exception e) {
            rhythms = new int[]{12};
        }
        env.getPlayer().getRhythmHandler().setRhythmTimings(rhythms);


        //Instrument
        //Uses the default instrument to start with
        Instrument instrument;
        try {
            String instrumentName = gson.fromJson((String) projectSettings.get("instrument"), String.class);
            instrument = InstrumentUtility.getInstrumentByName(instrumentName, env);
            if (instrument == null) {
                // Uses the default instrument if there's a problem
                instrument = InstrumentUtility.getDefaultInstrument(env);
            }
        } catch (Exception e) {
            // Uses the default instrument if there's a problem
            System.err.println("Could not load instrument - setting to default.");
            instrument = InstrumentUtility.getDefaultInstrument(env);
        }
        env.getPlayer().setInstrument(instrument);

        //User experience
        try {
            experience = Integer.parseInt(projectSettings.get("experience").toString());
        } catch (NullPointerException e) {
            //If XP has never been set (ie old account), default to 0
            experience = 0;
        }

        //Level
        try {
            level = Integer.parseInt(projectSettings.get("level").toString());
        } catch (NullPointerException e) {
            //If level has never been set, (ie old account), default to 1
            level = 1;
        }

        //badges
        //overallBadges
        ArrayList<Badge> overallBadges;
        Type overallBadgeType = new TypeToken<ArrayList<Badge>>() {
        }.getType();
        overallBadges = gson.fromJson((String) projectSettings.get("overallBadges"), overallBadgeType);

        //tutorBadges
        HashMap<String, ArrayList<Badge>> tutorBadges;
        Type tutorBadgeType = new TypeToken<HashMap<String, ArrayList<Badge>>>() {
        }.getType();
        tutorBadges = gson.fromJson((String) projectSettings.get("tutorBadges"), tutorBadgeType);

        badgeManager.replaceBadges(tutorBadges, overallBadges);

        //100tutorMap
        HashMap<String, Boolean> tutor100Map;
        Type tutor100BadgeType = new TypeToken<HashMap<String, Boolean>>() {
        }.getType();
        tutor100Map = gson.fromJson((String) projectSettings.get("tutor100Map"), tutor100BadgeType);

        badgeManager.replaceTutor100AllMap(tutor100Map);

        env.getTranscriptManager().unsavedChanges = false;


    }


    /**
     * Saves the current project, or if there is no current working project; launches the New
     * project dialog.
     */
    public void saveCurrentProject() {
        if (currentProjectPath != null) {
            saveProject(currentProjectPath);
        } else {
            projectHandler.createNewProject();
        }
        saved = true;

    }


    /**
     * Handles Saving a .json Project file, for the specified project address
     * @param projectAddress Project directory address.
     */
    public void saveProject(String projectAddress) {

        //Add all settings such as tempo speed to the project here.

        try {
            Gson gson = new Gson();
            saveProperties();

            FileWriter file = new FileWriter(projectAddress + "/" + projectName + ".json");
            file.write(projectSettings.toJSONString());
            file.flush();
            file.close();

            projectSettings.put("tempo", env.getPlayer().getTempo());
            env.getRootController().setWindowTitle(projectName);
            currentProjectPath = projectAddress;
        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    /**
     * Compares a specified project property to the saved value If there is a difference, adds an
     * asterix indicator to the project title
     *
     * @param propName property id which is stored in the Json project file.
     */
    public void checkChanges(String propName) {

        String saveName = (projectName == null) ? "No Project" : this.projectName;

        if (propName.equals("tempo")) {


            if (projectSettings.containsKey("tempo") && !(projectSettings.get("tempo").equals(String.valueOf(env.getPlayer().getTempo())))) { //If not equal

                env.getRootController().setWindowTitle(saveName + "*");
                saved = false;
            }
        } else if (propName.equals("rhythm")) {


            if (projectSettings.containsKey("rhythm") && !(projectSettings.get("rhythm").equals(env.getPlayer().getRhythmHandler().getRhythmTimings()))) { //If not equal

                env.getRootController().setWindowTitle(saveName + "*");
                saved = false;
            }
        } else if (propName.equals("instrument")) {


            if (projectSettings.containsKey("instrument") && !(projectSettings.get("instrument").equals(env.getPlayer().getInstrument().getName()))) { //If not equal

                env.getRootController().setWindowTitle(saveName + "*");
                saved = false;
            }
        }


    }


    /**
     * Loads a project, specifed by the project name. All projects must be located in the user's
     * projects directory to be correctly loaded.
     *
     * @param pName project name string
     */
    public void loadProject(String pName) {
        try {

            env.resetProjectEnvironment();
            String path = projectDirectory.toString();

            try {

                projectSettings = (JSONObject) parser.parse(new FileReader(path + "/" + pName + ".json"));

            } catch (FileNotFoundException f) {
                //Project doesn't exist? Create it.
                System.err.println("Tried to load project but the path didn't exist");

                if (!Paths.get(path).toFile().isDirectory()) {
                    //If the Project directory folder doesn't exist.
                    System.err.println("Project directory missing - Might have been moved, renamed or deleted.\n Will remove the project from the projects json");

                    try {
                        Files.createDirectories(projectDirectory);
                        saveProject(path);

                    } catch (IOException eIO3) {
                        //Failed to create the directory.
                        System.err.println("Well UserData directory failed to create.. lost cause.");
                    }

                    return;
                } else {
                    //.json project files are corrupt.
                    saveProject(path);
                }

            }

            this.projectName = pName;
            loadProperties();
            currentProjectPath = path;
            env.getRootController().setWindowTitle(pName);
            //ignore

        } catch (FileNotFoundException e) {
            System.err.println("File not found, printing exception..");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException when trying to parse project .json");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("Parse exception. Project json is corrupt, cannot open.");
            env.getRootController().errorAlert("Project Corrupt - Cannot open.");


        }
    }


    /**
     * @return true if the project JSON is up to date.
     */
    public boolean isSaved() {
        return saved;
    }

    public Boolean isProject() {
        return currentProjectPath != null;
    }

    public String getCurrentProjectPath() {
        return currentProjectPath;
    }


    /**
     * When a user gains XP, eg by completing a tutoring session, this function is called. It adds
     * their newly gained experience to their overall experience, and saves this info to their user
     * file.
     *
     * @param addedExperience The integer amount of gained experience, to be added to the user
     */
    public void addExperience(int addedExperience) {
        experience += addedExperience;

        // Increases user levels one by one until the user cannot level up any further
        while (LevelCalculator.isLevelUp(level, experience)) {
            level += 1;
            env.getRootController().updateLevelBadge();
            Image image = new Image(getClass().getResourceAsStream("/images/arrow.png"), 110, 75, true, true);
            Notifications.create()
                    .title("Level Up")
                    .text("Well done! \nYou are now level " + String.valueOf(level) + ".")
                    .hideAfter(new Duration(10000))
                    .graphic(new ImageView(image))
                    .show();

        }

        saveProperties();
    }

    public Integer getExperience() {
        return this.experience;
    }

    public Integer getLevel() {
        return this.level;
    }

    public BadgeManager getBadgeManager(){return badgeManager;}

}
