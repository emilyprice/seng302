package seng302.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import seng302.Environment;

public class ClassSummaryController {

    @FXML
    private VBox classSummary;

    private Environment env;

    public void create(Environment env) {
        this.env = env;
    }

}
