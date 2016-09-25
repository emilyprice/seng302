package seng302.gui;

import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import seng302.Environment;

public class TeacherPageController {

    @FXML
    private VBox profilepic;

    @FXML
    private VBox studentInfo;

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
    void openSettings(MouseEvent event) {
        env.getRootController().launchTeacherSettings();
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

        TreeItem<String> root = new TreeItem<>("Root");
        root.getChildren().add(new TreeItem<>("Summary"));
        root.setExpanded(true);

        classroomData.getChildren().forEach(user -> {

            options.add(user.getKey());
            TreeItem<String> thisStudent = new TreeItem<>(user.getKey());

            DataSnapshot userProjects = user.child("projects");

            for (DataSnapshot data: userProjects.getChildren()) {
                thisStudent.getChildren().add(new TreeItem<>(user.getKey() + "/" + data.getKey()));
            }
            thisStudent.setExpanded(false);
            root.getChildren().add(thisStudent);
        });

        TreeView studentTree = new TreeView<>(root);
        studentTree.setShowRoot(false);
        studentTree.setCellFactory(thing -> new TreeCell<String>() {

            @Override
            public void updateItem(String text, boolean empty) {

                super.updateItem(text, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);

                } else {
                    setText(null);
                    AnchorPane view = new AnchorPane();
                    Label label = new Label();
                    view.getChildren().addAll(label);
                    view.setStyle("-fx-fill-color: gray");
                    view.setPadding(new Insets(5));
                    AnchorPane.setLeftAnchor(label, 15.0);
                    label.setText(text.substring(text.lastIndexOf('/') + 1));
                    setGraphic(view);
                    view.setOnMouseClicked(event -> {
                        if (text.contains("/") || text.equalsIgnoreCase("summary")) {
                            showPage(text);
                        }
                    });
                }

            }
        });
        studentInfo.getChildren().add(studentTree);

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

    public void showUserPage(String userInfo) {

        String project = userInfo.substring(userInfo.lastIndexOf('/') + 1);
        String userName = userInfo.substring(0, userInfo.lastIndexOf('/'));


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
            userSummaryController.createStudent(env, userName, getTimePeriod(), project);

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
