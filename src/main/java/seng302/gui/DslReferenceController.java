package seng302.gui;

import org.controlsfx.control.PopOver;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import seng302.command.Command;
import seng302.data.CommandData;

/**
 * This class contains the logic for a popover containing help info of all commands.
 */
public class DslReferenceController {

    private PopOver dslRef;
    private TranscriptPaneController parentController;
    private VBox popoverContent = new VBox();

    public DslReferenceController(TranscriptPaneController parentController) {
        this.parentController = parentController;
        this.dslRef = createDslReference();
    }

    /**
     * Initialises a popover containing help info about popoverContent. Each command, when clicked,
     * is copied to the input text field.
     */
    private PopOver createDslReference() {
        VBox scrollContent = new VBox();
        HBox sortingOptions = getSortingOptions();
        HBox helpfulInfo = getInfoBox();

        sortingOptions.setSpacing(5);
        sortingOptions.setPadding(new Insets(10));

        scrollContent.getChildren().add(sortingOptions);
        scrollContent.getChildren().add(helpfulInfo);
        scrollContent.getChildren().add(popoverContent);

        popoverContent.getChildren().add(new Text("Click a command to copy to input field"));
        popoverContent.setSpacing(5);
        popoverContent.setPadding(new Insets(10));

        ScrollPane commandScrollPane = new ScrollPane();
        commandScrollPane.setPrefSize(500, 200);
        commandScrollPane.setContent(scrollContent);
        alphabetiseCommands();
        dslRef = new PopOver(commandScrollPane);
        dslRef.setHeaderAlwaysVisible(true);
        dslRef.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
        dslRef.setTitle("DSL Reference Card");
        dslRef.getRoot().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
//        dslRef.setConsumeAutoHidingEvents(false);
//        System.out.println("dslAutoHide: " + dslRef.isAutoHide());
//        dslRef.autoHideProperty().setValue(false);
//        System.out.println("dslFocusTraversable: " + dslRef.getRoot().isFocusTraversable());
//        dslRef.getRoot().setFocusTraversable(true);
        return dslRef;
    }

    /**
     * Creates an HBox with text explaining the syntax of the DSL reference card.
     */
    private HBox getInfoBox() {
        HBox helpfulInfo = new HBox();
        helpfulInfo.setPadding(new Insets(10));
        helpfulInfo.getChildren().add(new Text("Note: Arguments surrounded by ( ) are mandatory." +
                '\n' + "Arguments surrounded by [ ] are optional." + '\n' +
                "Brackets are indicators only, and should be removed." + '\n' +
                "Examples of each command are given below the command."));

        return helpfulInfo;
    }

    /**
     * Creates two radio buttons, for sorting the DSL reference popup.
     *
     * @return A javafx HBox containing the radio buttons
     */
    private HBox getSortingOptions() {
        HBox sortButtons = new HBox();
        final ToggleGroup sortingOptions = new ToggleGroup();
        final RadioButton sortAlphabetically = new RadioButton("Sort Alphabetically");
        sortAlphabetically.setToggleGroup(sortingOptions);
        sortAlphabetically.setSelected(true);
        RadioButton sortByGroup = new RadioButton("Sort by Group");
        sortByGroup.setToggleGroup(sortingOptions);
        sortingOptions.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (sortingOptions.getSelectedToggle().equals(sortAlphabetically)) {
                    alphabetiseCommands();
                } else {
                    sortCommands();
                }
            }
        });


        sortButtons.getChildren().add(sortAlphabetically);
        sortButtons.getChildren().add(sortByGroup);
        return sortButtons;
    }

    /**
     * Displays all commands in the popover, in alphabetical order.
     */
    private void alphabetiseCommands() {
        popoverContent.getChildren().clear();
        VBox allCommands = new VBox();
        popoverContent.getChildren().add(allCommands);

        for (Command command : CommandData.allCommands) {
            prepareCommand(command, allCommands);
        }
    }

    /**
     * Displays all commands in the popover, sorted by category.
     */
    private void sortCommands() {
        popoverContent.getChildren().clear();
        final VBox categories = new VBox();
        final VBox categoryContent = new VBox();
        final HBox categorisedCommands = new HBox();
        categorisedCommands.getChildren().add(categories);
        categorisedCommands.getChildren().add(categoryContent);

        String[] categoryNames = {"Play", "Show", "Special", "Translation", "Terms", "Settings"};

        for (final String categoryName : categoryNames) {
            Text category = new Text(categoryName + " >");
            category.setCursor(Cursor.HAND);
            categories.getChildren().add(category);
            category.setOnMouseClicked(event -> {
                categoryContent.getChildren().clear();
                for (Command command : CommandData.getCommands(categoryName)) {
                    prepareCommand(command, categoryContent);
                }
            });

        }

        popoverContent.getChildren().add(categorisedCommands);
    }

    /**
     * Creates a text object that contains a single command. When clicked, the command is copied to
     * the input text field.
     *
     * @param entry     A single command
     * @param container Where the command text box will display
     */
    private void prepareCommand(Command entry, VBox container) {
        final Command data = entry;
        HBox commandInfo = new HBox();
        VBox infoAndExample = new VBox();

        final Text content = new Text("-" + getDisplayText(entry));
        commandInfo.setCursor(Cursor.HAND);
        commandInfo.setOnMouseClicked(event -> {
            //copy to input field
            setCommandText(data);
        });
        commandInfo.getChildren().add(content);
        infoAndExample.getChildren().add(commandInfo);
        infoAndExample.getChildren().add(new Text('\t' + data.getExample()));
        container.getChildren().add(infoAndExample);
    }

    /**
     * Given a command, sets the input text field to display information about that command.
     *
     * @param command The command to display information about
     */
    public void setCommandText(Command command) {
        parentController.getTxtCommand().clear();
        parentController.getTxtCommand().setText(getDisplayText(command));
    }

    /**
     * Gets the fully made reference popover
     */
    public PopOver getPopover() {
        return dslRef;
    }

    /**
     * Turns all the parameters of a command into a pretty string
     *
     * @return A string containing all parameters of a command, surrounded in ()
     */
    public String getParamText(Command command) {
        String parameterString = "";
        for (String parameter : command.getParams()) {
            if (!parameter.equals("")) {
                parameterString += "(" + parameter + ") ";
            }
        }
        return parameterString;
    }

    /**
     * Turns all the options of a command into a pretty string
     *
     * @return A string containing all options of a command, surrounded in []
     */
    public String getOptionText(Command command) {
        String optionsString = "";
        for (String option : command.getOptions()) {
            if (!option.equals("")) {
                optionsString += "[" + option + "] ";
            }
        }
        return optionsString;
    }


    /**
     * Displays all information of a command in a pretty string
     *
     * @return A string of the form command name (parameters) [options]
     */
    public String getDisplayText(Command command) {
        String displayText = "";
        displayText += command.getCommandText() + " " + getParamText(command);
        if (!getOptionText(command).equals("")) {
            displayText += getOptionText(command);
        }
        return displayText;
    }
}
