package seng302.gui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import seng302.Environment;
import seng302.data.Term;

public class TermsSettingsController {

    @FXML
    private AnchorPane termsSettingsAnchor;

    @FXML
    private JFXListView termsListView;

    @FXML
    private Label selectedName;

    @FXML
    private JFXTextArea selectedDefinition;

    @FXML
    private JFXTextField selectedCategory;

    @FXML
    private JFXTextField selectedOrigin;

    private Environment env;

    public void create(Environment env) {
        this.env = env;
        env.getRootController().setHeader("Musical Term Settings");

        List<String> termNames = this.env.getMttDataManager().getTerms().stream().map(Term::getMusicalTermName).collect(Collectors.toList());

        termsListView.getItems().setAll(termNames);

        termsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Display musical term with newvalue
            selectedName.setText('"' + newValue.toString() + '"');


            for (Term term : env.getMttDataManager().getTerms()) {
                if (term.getMusicalTermName().equals(newValue.toString())) {
                    selectedDefinition.setText(term.getMusicalTermDefinition());
                    selectedCategory.setText(term.getMusicalTermCategory());
                    selectedOrigin.setText(term.getMusicalTermOrigin());
                    break;
                }
            }


        });

        termsListView.getSelectionModel().selectFirst();
    }

}
