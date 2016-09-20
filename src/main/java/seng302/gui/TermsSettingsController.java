package seng302.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import seng302.Environment;
import seng302.data.Term;

public class TermsSettingsController {

    @FXML
    private AnchorPane termsSettingsAnchor;

    @FXML
    private JFXListView termsListView;

    @FXML
    private JFXTextField selectedName;

    @FXML
    private JFXTextArea selectedDefinition;

    @FXML
    private JFXTextField selectedCategory;

    @FXML
    private JFXTextField selectedOrigin;

    @FXML
    private JFXButton editButton;

    private Environment env;

    private boolean isEditMode = false;

    ImageView editImage = new ImageView(new Image(getClass().getResourceAsStream("/images/edit_mode_black_36dp.png"), 25, 25, false, false));

    ImageView saveImage = new ImageView(new Image(getClass().getResourceAsStream("/images/plus-symbol.png"), 25, 25, false, false));

    public void create(Environment env) {
        this.env = env;
        env.getRootController().setHeader("Musical Term Settings");

        editButton.setGraphic(editImage);


        List<String> termNames = this.env.getMttDataManager().getTerms().stream().map(Term::getMusicalTermName).collect(Collectors.toList());

        termsListView.getItems().setAll(termNames);

        termsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            isEditMode = false;
            toggleInputs();
            // Display musical term with newvalue
            selectedName.setText(newValue.toString());


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

    @FXML
    private void toggleEditMode() {
        if (isEditMode) {
            //Save changes and close edit mode
            isEditMode = false;
            editButton.setGraphic(editImage);
        } else {
            //Enter edit mode
            isEditMode = true;
            editButton.setGraphic(saveImage);
        }
        toggleInputs();
    }

    private void toggleInputs() {
        selectedDefinition.setEditable(isEditMode);
        selectedOrigin.setEditable(isEditMode);
        selectedCategory.setEditable(isEditMode);
        selectedName.setEditable(isEditMode);
    }

}
