package seng302.gui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;

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

    private Environment env;

    public void create(Environment env) {
        this.env = env;
        env.getRootController().setHeader("Musical Term Settings");

        List<String> termNames = this.env.getMttDataManager().getTerms().stream().map(Term::getMusicalTermName).collect(Collectors.toList());

        termsListView.getItems().setAll(termNames);

        termsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Display musical term with newvalue
            selectedName.setText(newValue.toString());

            Term selectedTerm;

            for (Term term : env.getMttDataManager().getTerms()) {
                if (term.getMusicalTermName().equals(newValue.toString())) {
                    selectedTerm = term;
                    selectedDefinition.setText(selectedTerm.getMusicalTermDefinition());
                }
            }


        });
    }

}
