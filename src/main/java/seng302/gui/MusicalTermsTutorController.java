package seng302.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Pair;
import seng302.Environment;

import seng302.data.Term;
import seng302.utility.MusicalTermsTutorBackEnd;
import seng302.utility.TutorManager;
import seng302.utility.TutorRecord;

/**
 * Created by jmw280 on 16/04/16.
 */


public class MusicalTermsTutorController extends TutorController{


    @FXML
    TextField txtNumMusicalTerms;

    @FXML
    VBox questionRows;

    @FXML
    AnchorPane IntervalRecognitionTab;

    @FXML
    Button btnGo;

    MusicalTermsTutorBackEnd dataManager;

    Random rand;


    public void create(Environment env) {
        super.create(env);
        dataManager = env.getMttDataManager();
        rand = new Random();
    }

    @FXML
    void goAction(ActionEvent event) {
        paneQuestions.setVisible(true);
        paneResults.setVisible(false);
        record = new TutorRecord(new Date(), "Musical Terms");
        manager.questions = Integer.parseInt(txtNumMusicalTerms.getText());
        if (manager.questions >= 1) {
            ArrayList<Term> termArray = dataManager.getTerms();
            // Run the tutor
            questionRows.getChildren().clear();
            if(termArray.size() < 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("No Musical Terms Added");
                alert.setContentText("There are no terms to be tested on. \nTo add them use the 'add musical term' command");
                alert.showAndWait();

            }else {//if there are terms to display
                for (int i = 0; i < manager.questions; i++) {
                    Term term = termArray.get(rand.nextInt(termArray.size()));
                    HBox questionRow = generateQuestionPane(term);
                    questionRows.getChildren().add(questionRow);
                    questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Number of Musical Terms");
            alert.setContentText("Please select a positive number of intervals");
            alert.setResizable(false);
            alert.showAndWait();
        }
    }


    //generate origin combobox
    private ComboBox<String> generateOriginChoices() {
        ComboBox<String> options = new ComboBox<String>();

        for (Term term : dataManager.getTerms()) {
            options.getItems().add(term.getMusicalTermOrigin());
        }
        return options;
    }

    //generate category combobox
    private ComboBox<String> generateCategoryChoices() {
        ComboBox<String> options = new ComboBox<String>();

        for (Term term : dataManager.getTerms()) {
            options.getItems().add(term.getMusicalTermCategory());
        }
        return options;
    }

    //generate description combobox
    private ComboBox<String> generateDefinitionChoices() {
        ComboBox<String> options = new ComboBox<String>();
        for (Term term : dataManager.getTerms()) {
            options.getItems().add(term.getMusicalTermDefinition());
        }
        return options;
    }


    /**
     * Constructs the question panels.
     */
    public HBox generateQuestionPane(Term term) {

        final Term currentTerm = term;
        final HBox rowPane = new HBox();
        formatQuestionRow(rowPane);

        Label termLabel = new Label(currentTerm.getMusicalTermName());
        termLabel.setFont(Font.font("System Bold", 13));
        Button skip = new Button("Skip");
        Image imageSkip = new Image(getClass().getResourceAsStream("/images/right-arrow.png"), 20, 20, true, true);
        skip.setGraphic(new ImageView(imageSkip));

        final ComboBox<String> originOptions = generateOriginChoices();
        originOptions.setPrefHeight(30);
        final ComboBox<String> categoryOptions = generateCategoryChoices();
        categoryOptions.setPrefHeight(30);
        final ComboBox<String> definitionOptions = generateDefinitionChoices();
        definitionOptions.setPrefHeight(30);


        originOptions.setOnAction(new EventHandler<ActionEvent>() {
            // This handler colors the GUI depending on the user's input
            public void handle(ActionEvent event) {
                if (originOptions.getValue().equals(currentTerm.getMusicalTermOrigin())) {
                    originOptions.setStyle("-fx-background-color: green");

                } else {
                    originOptions.setStyle("-fx-background-color: red");
                }

                // Adds to record
                String[] question = new String[]{
                        String.format("Origin of term %s", currentTerm.getMusicalTermOrigin()),
                        originOptions.getValue(),
                        Boolean.toString(originOptions.getValue().equals(currentTerm.getMusicalTermOrigin()))
                };
                record.addQuestionAnswer(question);

                if(categoryOptions.getValue() != null && definitionOptions.getValue()!= null){
                    if(categoryOptions.getStyle() == "-fx-background-color: red" && definitionOptions.getStyle() == "-fx-background-color: red" && originOptions.getStyle() == "-fx-background-color: red" ){
                        rowPane.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 0);
                    }else if(categoryOptions.getStyle() == "-fx-background-color: green" && definitionOptions.getStyle() == "-fx-background-color: green" && originOptions.getStyle() == "-fx-background-color: green" ){
                        rowPane.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 1);
                    }else{
                        rowPane.setStyle("-fx-border-color: yellow; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 0);

                    }
                    rowPane.getChildren().get(7).setDisable(true);
                    manager.answered += 1;
                }

                rowPane.getChildren().get(2).setDisable(true);
                if (manager.answered == manager.questions) {
                    finished();
                }

            }
        });

        categoryOptions.setOnAction(new EventHandler<ActionEvent>() {
            // This handler colors the GUI depending on the user's input
            public void handle(ActionEvent event) {
                if (categoryOptions.getValue().equals(currentTerm.getMusicalTermCategory())) {
                    categoryOptions.setStyle("-fx-background-color: green");
                } else {
                    categoryOptions.setStyle("-fx-background-color: red");;
                }

                // Adds to record
                String[] question = new String[]{
                        String.format("Category of term %s", currentTerm.getMusicalTermCategory()),
                        originOptions.getValue(),
                        Boolean.toString(originOptions.getValue().equals(currentTerm.getMusicalTermCategory()))
                };
                record.addQuestionAnswer(question);

                if(definitionOptions.getValue() != null && originOptions.getValue()!= null){
                    if(categoryOptions.getStyle() == "-fx-background-color: red" && definitionOptions.getStyle() == "-fx-background-color: red" && originOptions.getStyle() == "-fx-background-color: red" ){
                        rowPane.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 0);
                    }else if(categoryOptions.getStyle() == "-fx-background-color: green" && definitionOptions.getStyle() == "-fx-background-color: green" && originOptions.getStyle() == "-fx-background-color: green" ){
                        rowPane.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 1);
                    }else{
                        rowPane.setStyle("-fx-border-color: yellow; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 0);

                    }
                    rowPane.getChildren().get(7).setDisable(true);
                    manager.answered += 1;
                }
                rowPane.getChildren().get(4).setDisable(true);

                if (manager.answered == manager.questions) {
                    finished();
                }

            }
        });

        definitionOptions.setOnAction(new EventHandler<ActionEvent>() {
            // This handler colors the GUI depending on the user's input
            public void handle(ActionEvent event) {
                if (definitionOptions.getValue().equals(currentTerm.getMusicalTermDefinition())) {
                    definitionOptions.setStyle("-fx-background-color: green");;
                } else {
                    definitionOptions.setStyle("-fx-background-color: red");;
                }

                // Adds to record
                String[] question = new String[]{
                        String.format("Definition of term %s", currentTerm.getMusicalTermDefinition()),
                        originOptions.getValue(),
                        Boolean.toString(originOptions.getValue().equals(currentTerm.getMusicalTermDefinition()))
                };
                record.addQuestionAnswer(question);

                if(categoryOptions.getValue() != null && originOptions.getValue()!= null){
                    if(categoryOptions.getStyle() == "-fx-background-color: red" && definitionOptions.getStyle() == "-fx-background-color: red" && originOptions.getStyle() == "-fx-background-color: red" ){
                        rowPane.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 0);
                    }else if(categoryOptions.getStyle() == "-fx-background-color: green" && definitionOptions.getStyle() == "-fx-background-color: green" && originOptions.getStyle() == "-fx-background-color: green" ){
                        rowPane.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 1);
                    }else{
                        rowPane.setStyle("-fx-border-color: yellow; -fx-border-width: 2px;");
                        manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 0);

                    }
                    rowPane.getChildren().get(7).setDisable(true);
                    manager.answered += 1;
                }

                rowPane.getChildren().get(6).setDisable(true);



                if (manager.answered == manager.questions) {
                    finished();
                }

            }
        });

        skip.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // Re-write this to be more specific
                String[] question = new String[]{
                        String.format("Information about %s", currentTerm.getMusicalTermName()),
                        currentTerm.getMusicalTermName()
                };
                record.addSkippedQuestion(question);

                rowPane.setStyle("-fx-border-color: grey; -fx-border-width: 2px;");
                manager.questions -= 1;
                manager.add(new Pair(currentTerm.getMusicalTermName(),currentTerm), 2);
                rowPane.getChildren().get(2).setDisable(true);
                rowPane.getChildren().get(4).setDisable(true);
                rowPane.getChildren().get(6).setDisable(true);
                rowPane.getChildren().get(7).setDisable(true);
                if (manager.answered == manager.questions) {
                    finished();
                }


            }
        });


        rowPane.getChildren().add(termLabel);
        rowPane.getChildren().add(new Label("Origin:"));
        rowPane.getChildren().add(originOptions);
        rowPane.getChildren().add(new Label("Category:"));
        rowPane.getChildren().add(categoryOptions);
        rowPane.getChildren().add(new Label("Definition:"));
        rowPane.getChildren().add(definitionOptions);
        rowPane.getChildren().add(skip);

        rowPane.prefWidthProperty().bind(paneQuestions.prefWidthProperty());


        return rowPane;
    }

}