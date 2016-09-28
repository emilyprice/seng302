package seng302.gui;

import javafx.fxml.FXML;
import seng302.Environment;

public class TeacherSettingsController {

    private Environment env;

    @FXML
    private UserSettingsController userSettingsController;

    @FXML
    private UISkinnerController themeController;

    public void create(Environment env) {
        this.env = env;
        userSettingsController.create(env);
        themeController.create(env, env.getRootController().paneMain);

    }

}
