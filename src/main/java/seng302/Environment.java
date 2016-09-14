package seng302;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import seng302.Users.UserHandler;
import seng302.gui.RootController;
import seng302.gui.UserPageController;
import seng302.managers.ThemeHandler;
import seng302.managers.TranscriptManager;
import seng302.utility.EditHistory;
import seng302.utility.MusicalTermsTutorBackEnd;

public class Environment {

    private DslExecutor executor;
    private TranscriptManager transcriptManager;
    private MusicalTermsTutorBackEnd mttDataManager;
    private MusicPlayer player;
    private String recordLocation;
    private EditHistory em = new EditHistory(this);
    private BooleanProperty shiftPressed;
    private ThemeHandler themeHandler;

    public RootController getRootController() {
        return rootController;
    }

    public UserPageController getUserPageController() {
        return userPageController;
    }

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    public void setUserPageController(UserPageController userPageController) {
        this.userPageController = userPageController;
    }

    // Root Controller
    private RootController rootController;

    //userpage
    private UserPageController userPageController;

    private UserHandler userHandler;

    public Environment() {
        executor = new DslExecutor(this);
        player = new MusicPlayer();
        transcriptManager = new TranscriptManager();
        mttDataManager = new MusicalTermsTutorBackEnd();
        shiftPressed = new SimpleBooleanProperty(false);
        userHandler = new UserHandler(this);
        themeHandler = new ThemeHandler();
    }


    /**
     * Similar to the restEnvironment function, except it doesn't reset the MusicalTermsManager.
     */
    public void resetProjectEnvironment() {
        executor = new DslExecutor(this);
        player = new MusicPlayer();
        transcriptManager = new TranscriptManager();
        recordLocation = null;
        em = new EditHistory(this);
    }

    /**
     * Resets the environment so it clears the existing saved information.
     */
    public void resetEnvironment() {
        executor = new DslExecutor(this);
        player = new MusicPlayer();
        transcriptManager = new TranscriptManager();
        mttDataManager = new MusicalTermsTutorBackEnd();
        recordLocation = null;
        themeHandler = new ThemeHandler();
        em = new EditHistory(this);
    }

    /**
     * All errors are handled through here. They are then passed to the transcriptmanager to be
     * displayed.
     *
     * @param error_message The error message to be display to the user.
     */
    public void error(String error_message) {
        transcriptManager.setResult(String.format("[ERROR] %s", error_message));
    }

    public DslExecutor getExecutor() {
        return executor;
    }

    public TranscriptManager getTranscriptManager() {
        return transcriptManager;
    }

    public MusicalTermsTutorBackEnd getMttDataManager() {
        return mttDataManager;
    }

    public void setTranscriptManager(TranscriptManager t) {
        this.transcriptManager = t;
    }

    public void setMttDataManager(MusicalTermsTutorBackEnd t) {
        this.mttDataManager = t;
    }

    public String getRecordLocation() {
        return recordLocation;
    }

    public void setRecordLocation(String recordLocation) {
        this.recordLocation = recordLocation;
    }

    public MusicPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MusicPlayer m) {
        this.player = m;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public EditHistory getEditManager() {
        return this.em;
    }

    public ThemeHandler getThemeHandler() {
        return this.themeHandler;
    }

    public Boolean isShiftPressed() {
        return this.shiftPressed.getValue();
    }

    public BooleanProperty shiftPressedProperty() {
        return this.shiftPressed;
    }

    public void setShiftPressed(boolean shiftPressed) {
        this.shiftPressed.setValue(shiftPressed);
    }
}