package seng302.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Map<String, String> editInfo = new HashMap<>();

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

            Term selectedTerm = env.getMttDataManager().getTermByName(newValue.toString());

            if (selectedTerm != null) {
                selectedDefinition.setText(selectedTerm.getMusicalTermDefinition());
                selectedCategory.setText(selectedTerm.getMusicalTermCategory());
                selectedOrigin.setText(selectedTerm.getMusicalTermOrigin());
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

            editInfo.put("name", selectedName.getText());
            editInfo.put("category", selectedCategory.getText());
            editInfo.put("origin", selectedOrigin.getText());
            editInfo.put("definition", selectedDefinition.getText());

            env.getMttDataManager().editTerm(editInfo);
            env.getUserHandler().getCurrentUser().checkMusicTerms();
        } else {
            //Enter edit mode
            isEditMode = true;
            editButton.setGraphic(saveImage);
            editInfo.put("oldName", selectedName.getText());
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
