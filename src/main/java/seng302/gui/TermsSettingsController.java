package seng302.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
    private JFXTextField searchbar;

    @FXML
    private JFXButton editButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton addTerm;

    @FXML
    private Label errorMessage;

    private Environment env;

    private boolean isEditMode = false;

    private boolean isCreateMode = false;

    private Map<String, String> editInfo = new HashMap<>();

    private ImageView editImage = new ImageView(new Image(getClass().getResourceAsStream("/images/edit_mode_black_36dp.png"), 25, 25, false, false));

    private ImageView saveImage = new ImageView(new Image(getClass().getResourceAsStream("/images/plus-symbol.png"), 25, 25, false, false));

    private ImageView addImage = new ImageView(new Image(getClass().getResourceAsStream("/images/plus-symbol.png"), 25, 25, false, false));

    private ImageView deleteImage = new ImageView(new Image(getClass().getResourceAsStream("/images/trash-icon.png"), 25, 25, false, false));

    private ObservableList<String> termNames = FXCollections.observableArrayList();

    /**
     * Sets up the GUI elements of the musical terms page. Creates listeners for the list of terms
     * and the view of the list of terms. Also sets up buttons with appropriate graphics
     *
     * @param env The environment to which this page belongs
     */
    public void create(Environment env) {
        this.env = env;
        env.getRootController().setHeader("Musical Term Settings");

        editButton.setGraphic(editImage);
        deleteButton.setGraphic(deleteImage);
        addTerm.setGraphic(addImage);
        toggleInputs();

        termNames.addListener((ListChangeListener<String>) c -> {
            termsListView.getItems().setAll(termNames);
        });

        termNames.addAll(env.getMttDataManager().getTerms().stream().map(Term::getMusicalTermName).collect(Collectors.toList()));

        termsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                errorMessage.setVisible(false);
                isEditMode = false;
                isCreateMode = false;
                deleteButton.setVisible(true);
                editButton.setGraphic(editImage);
                // Display musical term with newvalue
                selectedName.setText(newValue.toString());

                Term selectedTerm = env.getMttDataManager().getTermByName(newValue.toString());

                if (selectedTerm != null) {
                    selectedDefinition.setText(selectedTerm.getMusicalTermDefinition());
                    selectedCategory.setText(selectedTerm.getMusicalTermCategory());
                    selectedOrigin.setText(selectedTerm.getMusicalTermOrigin());
                }
            }

        });

        termsListView.getSelectionModel().selectFirst();

    }

    @FXML
    private void toggleEditMode() {
        if (isEditMode) {
            try {
                //Save changes and close edit mode
                editInfo.put("name", selectedName.getText());
                editInfo.put("category", selectedCategory.getText());
                editInfo.put("origin", selectedOrigin.getText());
                editInfo.put("definition", selectedDefinition.getText());

                env.getMttDataManager().editTerm(editInfo);
                env.getUserHandler().getCurrentUser().checkMusicTerms();

                executeSearch();

                // If the name has changed, update the list
                if (!editInfo.get("oldName").equals(editInfo.get("name"))) {
                    for (int i = 0; i < termNames.size(); i++) {
                        if (termNames.get(i).equalsIgnoreCase(editInfo.get("name"))) {
                            termNames.set(i, editInfo.get("name"));
                            termsListView.getSelectionModel().selectIndices(i);
                        }
                    }
                }

                isEditMode = false;
                editButton.setGraphic(editImage);
                errorMessage.setVisible(false);
            } catch (Exception e) {
                errorMessage.setText(e.getMessage());
                errorMessage.setVisible(true);

            }


        } else if (isCreateMode) {

            try {
                // Save a new term and close this mode

                env.getMttDataManager().addTerm(new Term(selectedName.getText(),
                        selectedCategory.getText(), selectedOrigin.getText(),
                        selectedDefinition.getText()));

                isCreateMode = false;
                editButton.setGraphic(editImage);
                deleteButton.setVisible(true);

                env.getUserHandler().getCurrentUser().checkMusicTerms();
                executeSearch();
                errorMessage.setVisible(false);
            } catch (Exception e) {
                errorMessage.setText(e.getMessage());
                errorMessage.setVisible(true);
            }

        } else {
            //Enter edit mode
            isEditMode = true;
            editButton.setGraphic(saveImage);
            editInfo.put("oldName", selectedName.getText());
        }
        toggleInputs();
    }

    /**
     * Sets the fields of the displayed musical term to editable if the user is in edit or create
     * mode.
     */
    private void toggleInputs() {
        selectedDefinition.setEditable(isEditMode || isCreateMode);
        selectedOrigin.setEditable(isEditMode || isCreateMode);
        selectedCategory.setEditable(isEditMode || isCreateMode);
        selectedName.setEditable(isEditMode || isCreateMode);
    }

    @FXML
    private void deleteTerm() {

        FXMLLoader popupLoader = new FXMLLoader(getClass().getResource("/Views/PopUpModal.fxml"));
        try {
            BorderPane modal = popupLoader.load();
            JFXPopup popup = new JFXPopup();
            popup.setContent(modal);

            popup.setPopupContainer(env.getRootController().paneMain);
            popup.setSource(deleteButton);
            popup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
            Label header = (Label) modal.lookup("#lblHeader");

            JFXButton btnCancel = (JFXButton) modal.lookup("#btnCancel");
            btnCancel.setOnAction((e) -> popup.close());

            ((JFXButton) modal.lookup("#btnDelete")).
                    setOnAction((event) -> {
                        env.getMttDataManager().removeTerm(selectedName.getText());
                        env.getUserHandler().getCurrentUser().checkMusicTerms();
                        termNames.remove(selectedName.getText());
                        if (termNames.size() == 0) {
                            showPromptText();
                        }

                        termsListView.getSelectionModel().selectFirst();

                        popup.close();
                    });


            header.setText("Are you sure you wish to delete term: " + selectedName.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void executeSearch() {
        List<Term> results = env.getMttDataManager().search(searchbar.getText());
        termNames.setAll(results.stream().map(Term::getMusicalTermName).collect(Collectors.toList()));

    }

    /**
     * Sets and shows appropriate prompt text for the edit/add term screens. The setting of prompt
     * text is necessary as editing and saving share GUI elements.
     */
    private void showPromptText() {
        if (isCreateMode) {
            selectedName.setPromptText("New musical term name");
            selectedCategory.setPromptText("Write the category of your term");
            selectedOrigin.setPromptText("Write the origin of your term");
            selectedDefinition.setPromptText("Say a little about the meaning of this term");

        } else {
            selectedName.setPromptText("Musical Term Name");
            selectedCategory.setPromptText("");
            selectedOrigin.setPromptText("");
            selectedDefinition.setPromptText("");
        }

        selectedName.clear();
        selectedCategory.clear();
        selectedDefinition.clear();
        selectedOrigin.clear();

    }

    @FXML
    private void launchAddTerm() {
        errorMessage.setVisible(false);
        isCreateMode = true;
        showPromptText();
        isEditMode = false;
        editButton.setGraphic(saveImage);
        deleteButton.setVisible(false);
        toggleInputs();
    }

}
