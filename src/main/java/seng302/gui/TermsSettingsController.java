package seng302.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import seng302.Environment;

public class TermsSettingsController {

    @FXML
    private AnchorPane termsSettingsAnchor;

    private Environment env;

    public void create(Environment env) {
        this.env = env;
        env.getRootController().setHeader("Musical Term Settings");
    }

}
