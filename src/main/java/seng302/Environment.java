package seng302;

import seng302.gui.RootController;
import seng302.utility.MusicalTermsTutorBackEnd;
import seng302.utility.TutorManager;
import seng302.utility.TranscriptManager;

public class Environment {

    private DslExecutor executor;
    private TranscriptManager transcriptManager;
    private TutorManager pctManager;
    private TutorManager irtManager;
    private TutorManager mttManager;
    private MusicalTermsTutorBackEnd mttDataManager; ///////////////////////////////////////
    private MusicPlayer player;
    private String recordLocation;

    public RootController getRootController() {
        return rootController;
    }

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    // Root Controller
    private RootController rootController;



    private seng302.JSON.jsonHandler json;

    public Environment() {
        executor = new DslExecutor(this);
        player = new MusicPlayer();
        transcriptManager = new TranscriptManager();
        mttDataManager = new MusicalTermsTutorBackEnd(); ///////////////////////////////
        json = new seng302.JSON.jsonHandler(this);

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

    public MusicalTermsTutorBackEnd getMttDataManager(){ return mttDataManager;} //////////////////////////////

    public void setTranscriptManager(TranscriptManager t) {
        this.transcriptManager = t;
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

    public seng302.JSON.jsonHandler getJson() {
        return json;
    }

    public void setJson(seng302.JSON.jsonHandler json) {
        this.json = json;
    }




}