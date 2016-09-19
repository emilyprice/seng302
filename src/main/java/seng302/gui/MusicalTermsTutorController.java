package seng302.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Term;
import seng302.utility.MusicalTermsTutorBackEnd;
import seng302.utility.TutorRecord;

/**
 * Created by jmw280 on 16/04/16.
 */


public class MusicalTermsTutorController extends TutorController {

    @FXML
    VBox questionRows;

    @FXML
    AnchorPane MusicalTermsAnchor;

    @FXML
    Button btnGo;

    /**
     * Stores the terms that have been saved
     */
    MusicalTermsTutorBackEnd dataManager;

    Random rand;

    ArrayList<Term> termsBeingViewed;


    //Number of terms to choose from
    int terms = 0;

    /**
     * sets up the class and initialises the main variables
     */
    public void create(Environment env) {
        super.create(env);
        dataManager = env.getMttDataManager();

        rand = new Random();
        initialiseQuestionSelector();
    }

    /**
     * Run when the user clicks the "Go" button. Generates and displays a new set of questions.
     *
     * @param event The mouse click that initiated the method.
     */
    @FXML
    void goAction(ActionEvent event) {
        paneInit.setVisible(false);
        paneQuestions.setVisible(true);
        record = new TutorRecord();
        manager.resetEverything();
        manager.questions = selectedQuestions;
        qPanes = new ArrayList<>();

        if (manager.questions >= 1) {
            termsBeingViewed = new ArrayList<Term>(dataManager.getTerms());
            // Run the tutor
            questionRows.getChildren().clear();
            if (termsBeingViewed.size() < 1) {
                showAlert(true);

            } else if (isCompMode && termsBeingViewed.size() < 10) {
                //User has some terms, but not enough to compete
                showAlert(false);

            } else {
                for (int i = 0; i < manager.questions; i++) {
                    if (termsBeingViewed.size() < 1) {
                        termsBeingViewed = new ArrayList<>(dataManager.getTerms());
                    }
                    int randomNumber = rand.nextInt(termsBeingViewed.size());
                    Term term = termsBeingViewed.get(randomNumber);
                    termsBeingViewed.remove(randomNumber);
                    Pair<String, Term> dummyPair = new Pair<>("", term);
                    HBox questionRow = generateQuestionPane(dummyPair);
                    TitledPane qPane = new TitledPane((i + 1) + ". What are the origin, category and definition of " + term.getMusicalTermName() + "?", questionRow);
                    qPane.setPadding(new Insets(2, 2, 2, 2));
                    qPanes.add(qPane);

                    VBox.setMargin(questionRow, new Insets(10, 10, 10, 10));
                }
                qAccordion.getPanes().addAll(qPanes);
                qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
                questionRows.getChildren().add(qAccordion);
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Number of Musical Terms");
            alert.setContentText("Please select a positive number of intervals");
            alert.setResizable(false);
            alert.showAndWait();
        }
    }


    /**
     * Generates and populates The Origin combo box
     */
    private ComboBox<String> generateOriginChoices(Term currentTerm) {
        Random rand = new Random();
        int correctPosition = rand.nextInt(5);
        ComboBox<String> options = new ComboBox<>();
        Collections.shuffle(dataManager.getTerms());
        int i = 0;
        Boolean alreadyAdded = false;
        while (options.getItems().size() < 5 && i < dataManager.getTerms().size()) {
            if (correctPosition == i && !alreadyAdded) {
                if (!(options.getItems().contains(currentTerm.getMusicalTermOrigin()))) {
                    options.getItems().add(currentTerm.getMusicalTermOrigin());
                }
                i -= 1;
                alreadyAdded = true;

            } else {
                if (!(options.getItems().contains(dataManager.getTerms().get(i).getMusicalTermOrigin()))) {
                    options.getItems().add(dataManager.getTerms().get(i).getMusicalTermOrigin());
                }
            }
            i += 1;
        }
        return options;
    }

    /**
     * Generates and populates The category combo box
     */
    private ComboBox<String> generateCategoryChoices(Term currentTerm) {
        Random rand = new Random();
        int correctPosition = rand.nextInt(5);
        ComboBox<String> options = new ComboBox<String>();
        Collections.shuffle(dataManager.getTerms());
        int i = 0;
        Boolean alreadyAdded = false;
        while (options.getItems().size() < 5 && i < dataManager.getTerms().size()) {
            if (correctPosition == i && !alreadyAdded) {
                if (!(options.getItems().contains(currentTerm.getMusicalTermCategory()))) {
                    options.getItems().add(currentTerm.getMusicalTermCategory());
                }
                i -= 1;
                alreadyAdded = true;

            } else {
                if (!(options.getItems().contains(dataManager.getTerms().get(i).getMusicalTermCategory()))) {
                    options.getItems().add(dataManager.getTerms().get(i).getMusicalTermCategory());
                }
            }
            i += 1;
        }
        return options;
    }

    /**
     * Generates and populates The definition combo box
     */
    private ComboBox<String> generateDefinitionChoices(Term currentTerm) {
        Random rand = new Random();
        int correctPosition = rand.nextInt(5);
        ComboBox<String> options = new ComboBox<String>();
        Collections.shuffle(dataManager.getTerms());
        int i = 0;
        Boolean alreadyAdded = false;
        while (options.getItems().size() < 5 && i < dataManager.getTerms().size()) {
            if (correctPosition == i && !alreadyAdded) {
                if (!(options.getItems().contains(currentTerm.getMusicalTermDefinition()))) {
                    options.getItems().add(currentTerm.getMusicalTermDefinition());
                }
                i -= 1;
                alreadyAdded = true;

            } else {
                if (!(options.getItems().contains(dataManager.getTerms().get(i).getMusicalTermDefinition()))) {
                    options.getItems().add(dataManager.getTerms().get(i).getMusicalTermDefinition());
                }
            }
            i += 1;
        }
        return options;
    }


    /**
     * Constructs the question panels.
     */
    public HBox generateQuestionPane(Pair dummyPair) {

        final Term currentTerm = (Term) dummyPair.getValue();
        final HBox rowPane = new HBox();

        formatQuestionRow(rowPane);

        Button skip = new Button("Skip");
        Image imageSkip = new Image(getClass().getResourceAsStream("/images/right-arrow.png"), 20, 20, true, true);
        skip.setGraphic(new ImageView(imageSkip));

        final ComboBox<String> originOptions = generateOriginChoices(currentTerm);
        originOptions.setPrefSize(100, 30);
        final ComboBox<String> categoryOptions = generateCategoryChoices(currentTerm);
        categoryOptions.setPrefSize(100, 30);
        final ComboBox<String> definitionOptions = generateDefinitionChoices(currentTerm);
        definitionOptions.setPrefSize(100, 30);


        originOptions.setOnAction(event -> {
            // This handler colors the GUI depending on the user's input
            if (originOptions.getValue().equals(currentTerm.getMusicalTermOrigin())) {
                originOptions.setStyle("-fx-border-color: green");

            } else {
                originOptions.setStyle("-fx-border-color: red");
                ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(0)).getChildren().add(new Label(currentTerm.getMusicalTermOrigin()));
            }
            Integer correct = 0;
            if (originOptions.getValue().equals(currentTerm.getMusicalTermOrigin())) {
                correct = 1;
            }

            // Adds to record
            String[] question = new String[]{
                    String.format("Origin of term %s", currentTerm.getMusicalTermName()),
                    originOptions.getValue(),
                    correct.toString()
            };
            record.addQuestionAnswer(question);

            styleAnswer(rowPane, currentTerm, originOptions, categoryOptions, definitionOptions);

            ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(0)).getChildren().get(1).setDisable(true);
            if (isQuestionComplete(originOptions, categoryOptions, definitionOptions)) {
                handleAccordion();
            }
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        categoryOptions.setOnAction(event -> {
            // This handler colors the GUI depending on the user's input
            if (categoryOptions.getValue().equals(currentTerm.getMusicalTermCategory())) {
                categoryOptions.setStyle("-fx-border-color: green");
            } else {
                categoryOptions.setStyle("-fx-border-color: red");
                ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(1)).getChildren().add(new Label(currentTerm.getMusicalTermCategory()));
            }
            Integer correct = 0;
            if (categoryOptions.getValue().equals(currentTerm.getMusicalTermCategory())) {
                correct = 1;
            }

            // Adds to record
            String[] question = new String[]{
                    String.format("Category of term %s", currentTerm.getMusicalTermName()),
                    categoryOptions.getValue(),
                    correct.toString()
            };
            record.addQuestionAnswer(question);

            styleAnswer(rowPane, currentTerm, categoryOptions, definitionOptions, originOptions);

            ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(1)).getChildren().get(1).setDisable(true);

            if (isQuestionComplete(originOptions, categoryOptions, definitionOptions)) {
                handleAccordion();
            }
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        definitionOptions.setOnAction(event -> {
            // This handler colors the GUI depending on the user's input
            if (definitionOptions.getValue().equals(currentTerm.getMusicalTermDefinition())) {
                definitionOptions.setStyle("-fx-border-color: green");
            } else {
                definitionOptions.setStyle("-fx-border-color: red");
                ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(2)).getChildren().add(new Label(currentTerm.getMusicalTermDefinition()));
            }
            Integer correct = 0;
            if (definitionOptions.getValue().equals(currentTerm.getMusicalTermDefinition())) {
                correct = 1;
            }

            // Adds to record
            String[] question = new String[]{
                    String.format("Definition of term %s", currentTerm.getMusicalTermName()),
                    definitionOptions.getValue(),
                    correct.toString()
            };
            record.addQuestionAnswer(question);

            styleAnswer(rowPane, currentTerm, definitionOptions, categoryOptions, originOptions);

            ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(2)).getChildren().get(1).setDisable(true);

            if (isQuestionComplete(originOptions, categoryOptions, definitionOptions)) {
                handleAccordion();
            }
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        skip.setOnAction(event -> {
            // Re-write this to be more specific
            String[] question = new String[]{
                    String.format("Information about %s", currentTerm.getMusicalTermName()),
                    currentTerm.getMusicalTermName(),
                    "2"
            };
            record.addQuestionAnswer(question);

            formatSkippedQuestion(rowPane);
                manager.add(new Pair(currentTerm.getMusicalTermName(), currentTerm), 2);


            ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(0)).getChildren().get(1).setDisable(true);
            ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(1)).getChildren().get(1).setDisable(true);
            ((HBox) ((VBox) (rowPane.getChildren().get(0))).getChildren().get(2)).getChildren().get(1).setDisable(true);
            ((VBox) (rowPane.getChildren().get(0))).getChildren().get(3).setDisable(true); //disable skip
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        VBox qLayout = new VBox();
        qLayout.setSpacing(5);

        HBox origin = new HBox();
        origin.setAlignment(Pos.CENTER_LEFT);
        origin.setSpacing(24);
        origin.getChildren().add(new Label("Origin:"));
        origin.getChildren().add(originOptions);

        HBox category = new HBox();
        category.setAlignment(Pos.CENTER_LEFT);

        category.setSpacing(9);
        category.getChildren().add(new Label("Category:"));
        category.getChildren().add(categoryOptions);

        HBox def = new HBox();
        def.setAlignment(Pos.CENTER_LEFT);
        def.setSpacing(5);
        def.getChildren().add(new Label("Definition:"));
        def.getChildren().add(definitionOptions);

        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }

        qLayout.getChildren().add(origin);
        qLayout.getChildren().add(category);
        qLayout.getChildren().add(def);
        qLayout.getChildren().add(skip);

        rowPane.getChildren().add(qLayout);
        rowPane.prefWidthProperty().bind(paneQuestions.prefWidthProperty());


        return rowPane;
    }

    /**
     * If all parts of a question has been answered then the border is coloured
     */
    private void styleAnswer(HBox rowPane, Term currentTerm, ComboBox currentSelection, ComboBox secondBox, ComboBox thirdBox) {
        if (secondBox.getValue() != null && thirdBox.getValue() != null) {
            if (secondBox.getStyle() == "-fx-border-color: red" && thirdBox.getStyle() == "-fx-border-color: red" && currentSelection.getStyle() == "-fx-border-color: red") {
                // All parts incorrect
                formatIncorrectQuestion(rowPane);
                manager.add(new Pair(currentTerm.getMusicalTermName(), currentTerm), 0);
            } else if (secondBox.getStyle() == "-fx-border-color: green" && thirdBox.getStyle() == "-fx-border-color: green" && currentSelection.getStyle() == "-fx-border-color: green") {
                // All parts correct
                formatCorrectQuestion(rowPane);
                manager.add(new Pair(currentTerm.getMusicalTermName(), currentTerm), 1);
            } else {
                // Some parts correct, some parts incorrect
                formatPartiallyCorrectQuestion(rowPane);
                manager.add(new Pair(currentTerm.getMusicalTermName(), currentTerm), 0);

            }
            ((VBox) (rowPane.getChildren().get(0))).getChildren().get(3).setDisable(true);
        }
    }

    /**
     * If the user either has no musical terms loaded, or does not have enough terms loaded to
     * compete, an informational alert is shown
     *
     * @param isNoTermAlert true or false. If true, the user has no terms loaded. If false, they do
     *                      not have enough terms to compete.
     */
    public void showAlert(boolean isNoTermAlert) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (isNoTermAlert) {
            alert.setHeaderText("No Musical Terms Added");
            alert.setContentText("There are no terms to be tested on. \nTo add them use the 'add musical term' command");
        } else {
            // It's a can't compete alert
            alert.setHeaderText("Not Enough Musical Terms Added");
            alert.setContentText("You need a minimum of 10 terms to compete. \nTo add them use the 'add musical term' command");
        }

        alert.setOnCloseRequest(Event -> {
            try {
                String tutorName = env.getRootController().getHeader();
                env.getRootController().showUserPage();
                env.getUserPageController().showPage(tutorName);
            } catch (Exception e) {
                paneQuestions.setVisible(false);
                paneInit.setVisible(true);
            }
        });
        alert.showAndWait();
        paneInit.setVisible(true);
    }

    /**
     * This function checks whether or not all parts of the question have been filled out
     *
     * @param firstSelection  One of the three comboboxes to be filled out
     * @param secondSelection One of the three comboboxes to be filled out
     * @param thirdSelection  One of the three comboboxes to be filled out
     * @return true if all three combo boxes have had selections made, false otherwise
     */
    private boolean isQuestionComplete(ComboBox firstSelection, ComboBox secondSelection, ComboBox thirdSelection) {
        if (firstSelection.getValue() != null && secondSelection.getValue() != null && thirdSelection.getValue() != null) {
            return true;
        } else {
            return false;
        }
    }


    public void resetInputs() {
        dataManager = env.getMttDataManager();
        numQuestions.setValue(1);
    }

}