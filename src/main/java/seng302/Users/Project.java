package seng302.Users;

/**
 * ProjectHandler
 *
 * In charge of handling user project data, including saving, loading and validating.
 *
 *
 * Created by Jonty on 12-Apr-16.
 */

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.controlsfx.control.Notifications;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.Instrument;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import seng302.Environment;
import seng302.data.Badge;
import seng302.managers.BadgeManager;
import seng302.utility.InstrumentUtility;
import seng302.utility.LevelCalculator;
import seng302.utility.OutputTuple;
import seng302.utility.TutorRecord;

public class Project {

    HashMap<String, Object> projectSettings;

    ProjectHandler projectHandler;


    private Integer experience, level;

    public String projectName;

    private Environment env;
    public TutorHandler tutorHandler;
    private BadgeManager badgeManager;

    private double inputVolumeThreshold;

    private Boolean isCompetitiveMode, visualiserOn;
    Boolean saved = true;

    public Boolean isUserMapData = true;

    public HashMap<String, TutorRecord> recentPracticeTutorRecordMap;


    /**
     * Constructor for creating a new project.
     *
     * @param env         The environment in which the project is being created
     * @param projectName What the project will be called
     * @param projectH    The ProjectHandler object which will manage this project
     */
    public Project(Environment env, String projectName, ProjectHandler projectH) {
        this.projectName = projectName;
        this.env = env;
        projectSettings = new HashMap<>();
        tutorHandler = new TutorHandler(env);
        projectHandler = projectH;
        isCompetitiveMode = true;
        this.experience = 0;
        this.level = 1;
        this.visualiserOn = false;
        badgeManager = new BadgeManager(env);
        recentPracticeTutorRecordMap = new HashMap<String, TutorRecord>();
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
        try {
            projectSettings.put("instrument", gson.toJson(env.getPlayer().getInstrument().getName()));
        } catch (NullPointerException e) {
            projectSettings.put("instrument", "Acoustic Grand Piano");
        }


        projectSettings.put("level", this.level);
        projectSettings.put("experience", this.experience);

        projectSettings.put("competitionMode", gson.toJson(isCompetitiveMode.toString()));

        projectSettings.put("visualiserOn", gson.toJson(visualiserOn.toString()));

        projectSettings.put("overallBadges", gson.toJson(badgeManager.getOverallBadges()));
        projectSettings.put("tutorBadges", gson.toJson(badgeManager.getTutorBadges()));
        projectSettings.put("tutor100Map", gson.toJson(badgeManager.get100TutorBadges()));

        projectSettings.put("tutorPracticeMap", gson.toJson(recentPracticeTutorRecordMap));


        try {
            projectSettings.put("unlockMap", gson.toJson(env.getStageMapController().unlockStatus));
        } catch (Exception e) {
            System.err.println("cant save unlock map");
        }

        try {
            projectSettings.put("unlockMapDescriptions", gson.toJson(env.getStageMapController().getUnlockDescriptions()));
        } catch (Exception e) {
            System.err.println("cant save unlock map descriptions");
        }

        projectSettings.put("inputVolumeThreshold", gson.toJson(inputVolumeThreshold));



    }


    /**
     * load all saved project properties from the project json file. This currently supports Tempo,
     * working transcript and rhythm setting.
     */
    private void loadProperties() {


        DataSnapshot projectSnapshot = env.getFirebase().getUserSnapshot().child("projects/" + projectName);

        projectSettings = (HashMap<String, Object>) projectSnapshot.getValue();

        int tempo;
        Gson gson = new Gson();

        try {
            tempo = ((Long) projectSettings.get("tempo")).intValue();
        } catch (Exception e) {
            tempo = 120;
        }

        env.getPlayer().setTempo(tempo);


        try {
            inputVolumeThreshold = Double.valueOf(projectSettings.get("inputVolumeThreshold").toString());
        } catch (Exception e) {
            inputVolumeThreshold = -60.0;
        }

        //Transcript
        ArrayList<OutputTuple> transcript;
        Type transcriptType = new TypeToken<ArrayList<OutputTuple>>() {
        }.getType();
        try {
            transcript = gson.fromJson((String) projectSettings.get("transcript"), transcriptType);
            env.getTranscriptManager().setTranscriptContent(transcript);
            env.getRootController().setTranscriptPaneText(env.getTranscriptManager().convertToText());
        } catch (NullPointerException np) {
        }

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
        try {
            String mode = gson.fromJson((String) projectSettings.get("competitionMode"), String.class);
            if (mode.equals("true")) {
                setToCompetitionMode();
            } else {
                setToPracticeMode();
            }
        } catch (Exception e) {
            // Defaults to comp mode
            setToCompetitionMode();
        }

        try {
            String isVisOn = gson.fromJson((String) projectSettings.get("visualiserOn"), String.class);
            if (isVisOn.equals("true")) {
                visualiserOn = true;
            } else {
                visualiserOn = false;
            }
        } catch (Exception e) {
            // Off by default
            visualiserOn = false;
        }


        try {
            HashMap<String, TutorRecord> practiceMap;
            Type mapType = new TypeToken<HashMap<String, TutorRecord>>() {
            }.getType();
            practiceMap = gson.fromJson((String) projectSettings.get("tutorPracticeMap"), mapType);
            if (practiceMap != null) {
                recentPracticeTutorRecordMap = practiceMap;
            }

        } catch (Exception e) {
            System.err.println("failed to load tutorPracticeMap");

        }


        //badges
        //overallBadges
        try {
            ArrayList<Badge> overallBadges;
            Type overallBadgeType = new TypeToken<ArrayList<Badge>>() {
            }.getType();
            overallBadges = gson.fromJson((String) projectSettings.get("overallBadges"), overallBadgeType);


            //tutorBadges

            HashMap<String, ArrayList<Badge>> tutorBadges;
            Type tutorBadgeType = new TypeToken<HashMap<String, ArrayList<Badge>>>() {
            }.getType();
            tutorBadges = gson.fromJson((String) projectSettings.get("tutorBadges"), tutorBadgeType);
            if (tutorBadges != null && overallBadges != null) {
                badgeManager.replaceBadges(tutorBadges, overallBadges);
            }

        } catch (Exception e) {
            System.err.println("failed to import badge data");
        }

    }


    /**
     * Loads in the data for the stage map in regards to which tutors are locked and unlocked.
     */
    public void loadStageMapData() {
        try {
            Gson gson = new Gson();
            HashMap<String, Boolean> unlockMap;
            Type mapType = new TypeToken<HashMap<String, Boolean>>() {
            }.getType();
            unlockMap = gson.fromJson((String) projectSettings.get("unlockMap"), mapType);
            if (unlockMap != null) {
                env.getStageMapController().unlockStatus = unlockMap;
                env.getStageMapController().setDescription();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            Gson gson = new Gson();
            HashMap<String, HashMap<String, Boolean>> unlockMapDescriptions;
            Type mapType = new TypeToken<HashMap<String, HashMap<String, Boolean>>>() {
            }.getType();
            unlockMapDescriptions = gson.fromJson((String) projectSettings.get("unlockMapDescriptions"), mapType);
            if (unlockMapDescriptions != null) {
                env.getStageMapController().unlockDescriptions = unlockMapDescriptions;
                env.getStageMapController().setDescription();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    /**
     * Saves the current project, or if there is no current working project; launches the New
     * project dialog.
     */
    public void saveCurrentProject() {
        if (projectName != null) {
            saveProject(projectName);
        } else {
            projectHandler.createNewProject();
        }
        saved = true;

    }


    /**
     * Handles Saving a .json Project file, for the specified project address
     *
     * @param projectName Project directory address.
     */
    public void saveProject(String projectName) {
        saveProperties();
        env.getFirebase().getUserRef().child("projects/" + projectName).updateChildren(projectSettings);
        env.getRootController().removeUnsavedChangesIndicator();
    }


    /**
     * Compares a specified project property to the saved value If there is a difference, adds an
     * asterix indicator to the project title
     *
     * @param propName property id which is stored in the Json project file.
     */
    public void checkChanges(String propName) {
        Object currentValue = null;
        switch (propName) {
            case "tempo":
                currentValue = String.valueOf(env.getPlayer().getTempo());
                break;
            case "rhythm":
                currentValue = env.getPlayer().getRhythmHandler().getRhythmTimings();
                break;
            case "instrument":
                currentValue = env.getPlayer().getInstrument().getName();
                break;
            case "competitionMode":
                currentValue = this.isCompetitiveMode;
                break;
            case "visualiserOn":
                currentValue = this.visualiserOn;
                break;
            case "unlockMap":
                currentValue = env.getStageMapController().getUnlockStatus();
                break;
            case "unlockMapDescriptions":
                currentValue = env.getStageMapController().getUnlockDescriptions();
        }

        try {

            env.getUserHandler().getCurrentUser().saveAll();

        } catch (Exception e) {
            System.err.println("Invalid property being checked for save");
        }

    }


    /**
     * Loads a project, specifed by the project name. All projects must be located in the user's
     * projects directory to be correctly loaded.
     *
     * @param pName project name string
     */
    public void loadProject(String pName) {
        DataSnapshot project = env.getFirebase().getUserSnapshot().child("projects/" + pName);
        env.resetProjectEnvironment();
        if (project.exists()) {

        } else {
            System.err.println("Tried to load project but it didn't exist");
            saveProject(pName);

        }
        this.projectName = pName;
        env.getRootController().setWindowTitle("Allegro - " + pName);


    }


    /**
     * @return true if the project JSON is up to date.
     */
    public boolean isSaved() {
        return saved;
    }

    public boolean getIsCompetitiveMode() {
        return isCompetitiveMode;
    }

    private void setToCompetitionMode() {
        try {
            this.isCompetitiveMode = true;
            env.getRootController().getKeyboardPaneController().displayScalesButton.setDisable(true); //disable display scales
            env.getRootController().getKeyboardPaneController().disableLabels(true); //disable keyboard labels
            env.getRootController().disallowTranscript();
            env.getRootController().getTranscriptController().hideTranscript();
            env.getRootController().setWindowTitle(env.getRootController().getWindowTitle().replace(" [Practice Mode]", ""));
            env.getUserPageController().getSummaryController().loadStageMap();
            env.getUserPageController().populateUserOptions();
            env.getStageMapController().hideNextUnlockDescription(false);

        } catch (NullPointerException e) {
            // User Page might not exist yet so it doesn't have to load stuff
        }

    }

    private void setToPracticeMode() {
        try {
            env.getRootController().getKeyboardPaneController().displayScalesButton.setDisable(false); //enable display scales
            env.getRootController().getKeyboardPaneController().disableLabels(false); //enable keyboard labels
            this.isCompetitiveMode = false;
            env.getRootController().allowTranscript();
            env.getRootController().setWindowTitle(env.getRootController().getWindowTitle() + " [Practice Mode]");
            env.getUserPageController().getSummaryController().loadStageMap();
            env.getUserPageController().populateUserOptions();
            env.getStageMapController().hideNextUnlockDescription(true);


        } catch (NullPointerException e) {
            // User page might not exist yet
        }
    }

    public void setIsCompetitiveMode(boolean isCompetitiveMode) {
        if (isCompetitiveMode) {
            setToCompetitionMode();
        } else {
            setToPracticeMode();
        }
        checkChanges("competitionMode");
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

    public void setVisualiserOn(boolean isOn) {
        visualiserOn = isOn;
        checkChanges("visualiserOn");
    }

    public void addRecentPracticeTutorRecordMap(String tutor, TutorRecord record) {
        recentPracticeTutorRecordMap.put(tutor, record);
    }

    public boolean getVisualiserOn() {
        return visualiserOn;
    }

    public HashMap<String, TutorRecord> getRecentPracticeTutorRecordMap() {
        return recentPracticeTutorRecordMap;
    }

    public BadgeManager getBadgeManager() {
        return badgeManager;
    }

    public void setInputVolumeThreshold(double threshold) {
        this.inputVolumeThreshold = threshold;
    }

    public double getInputVolumeThreshold() {
        return inputVolumeThreshold;
    }

}
