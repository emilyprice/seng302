package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import seng302.Environment;

import java.io.IOException;
import java.util.ArrayList;

public class TeacherPageController {

    @FXML
    private VBox profilepic;

    @FXML
    private JFXListView listView;

//    @FXML
//    private JFXButton btnSettings;

    @FXML
    private Label txtFullName;

    @FXML
    private ImageView imageDP2;

    @FXML
    private AnchorPane teacherPage;

    @FXML
    private ScrollPane currentPage;

    @FXML
    private SplitPane userView;

    @FXML
    private Slider timeSlider;


    private Environment env;

    private UserSummaryController userSummaryController;

    private StringConverter convert;


    @FXML
    void openSettings(ActionEvent event) {

    }

    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setTeacherPageController(this);
    }

    public void load() {
        imageDP2.setImage(env.getUserHandler().getCurrentUser().getUserPicture());
        populateUserOptions();
    }


    public void populateUserOptions() {

        ArrayList<String> options = new ArrayList<>();
        options.add("Summary");

        DataSnapshot classroomData = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users");

        classroomData.getChildren().forEach(user -> {
            //TODO: filter out teachers
            //System.out.println(user.child("/properties/isTeacher"));
            options.add(user.getKey());

        });

        listView.getItems().addAll(FXCollections.observableArrayList(options));

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showPage((String) newValue);
        });


        listView.setMaxWidth(200);
        listView.setMinWidth(200);
        listView.setDepthProperty(1);


//        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            showPage((String) newValue);
//        });

        // Set after the listener so it loads user summary correctly
        listView.getSelectionModel().selectFirst();


    }

    public void showPage(String pageName) {

        setupTimeSlider();
        if (pageName.equals("Summary")) {
            showSummaryPage();
        } else {
            showUserPage(pageName);
        }


    }

    public void showSummaryPage() {
        env.getRootController().setHeader("Summary");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ClassSummary.fxml"));

        try {
            VBox summary = loader.load();
            currentPage.setContent(summary);
            AnchorPane.setLeftAnchor(summary, 0.0);
            AnchorPane.setTopAnchor(summary, 0.0);
            AnchorPane.setBottomAnchor(summary, 0.0);
            AnchorPane.setRightAnchor(summary, 0.0);
            ClassSummaryController classSummaryController = loader.getController();
            //statsController = tutorStatsLoader.getController();

            classSummaryController.create(env);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void showUserPage(String userName) {
        env.getRootController().setHeader("Student - " + userName);

       String password = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users/" + userName + "/properties/password").toString();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/UserSummary.fxml"));

        try {
            VBox summary = loader.load();
            currentPage.setContent(summary);
            AnchorPane.setLeftAnchor(summary, 0.0);
            AnchorPane.setTopAnchor(summary, 0.0);
            AnchorPane.setBottomAnchor(summary, 0.0);
            AnchorPane.setRightAnchor(summary, 0.0);
            userSummaryController = loader.getController();
            //statsController = tutorStatsLoader.getController();

            //change to be the user that was clicked on
            userSummaryController.createStudent(env, userName, getTimePeriod());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setupTimeSlider() {
        timeSlider.setMaxWidth(200);

        convert = new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if (object == 0) {
                    return "Last 24 Hours";
                } else if (object == 1) {
                    return "Last Week";
                } else if (object == 2) {
                    return "Last Month";
                } else if (object == 3) {
                    return "Last Six Months";
                } else if (object == 4) {
                    return "Last Year";
                } else if (object == 5) {
                    return "All Time";
                }
                return null;

            }

            @Override
            public Double fromString(String string) {
                if (string.equals("Last 24 Hours")) {
                    return 0d;
                } else if (string.equals("Last Week")) {
                    return 1d;
                } else if (string.equals("Last Month")) {
                    return 2d;
                } else if (string.equals("Last Six Months")) {
                    return 3d;
                } else if (string.equals("Last Year")) {
                    return 4d;
                } else if (string.equals("All Time")) {
                    return 5d;
                }
                return null;
            }
        };

        timeSlider.setLabelFormatter(convert);

    }

    public String getTimePeriod() {
        return convert.toString(timeSlider.getValue());
    }
}
