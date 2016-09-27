package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import seng302.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private TreeView studentTree;


    private Environment env;

    private UserSummaryController userSummaryController;

    private ClassSummaryController classSummaryController;

    private StringConverter convert;

    private List<String> teacherClasses = new ArrayList<>();



    @FXML
    void openSettings(MouseEvent event) {
        env.getRootController().launchTeacherSettings();
    }

    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setTeacherPageController(this);
    }

    public void load() {
        String fullName = env.getUserHandler().getCurrentTeacher().getUserFirstName() + " " + env.getUserHandler().getCurrentTeacher().getUserLastName();

        if (fullName.length() > 1) {
            txtFullName.setText(env.getUserHandler().getCurrentTeacher().getUserName());
        } else {
            txtFullName.setText(fullName);
        }


        env.getRootController().setWindowTitle("Allegro - " + env.getUserHandler().getClassRoom());

        imageDP2.setImage(env.getUserHandler().getCurrentTeacher().getUserPicture());

        env.getFirebase().getTeacherSnapshot().child(env.getUserHandler().getCurrentTeacher().getUserName() + "/classrooms").getChildren().forEach(classroomName -> {
            teacherClasses.add(classroomName.getValue().toString());
        });


        env.getRootController().updateClassroomsList(teacherClasses);
        populateUserOptions();
        showPage("Summary");
    }


    private void populateUserOptions() {

        DataSnapshot classroomData = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users");

        ArrayList<String> options = new ArrayList<>();
        options.add("Summary");

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

        studentTree.setRoot(root);
        studentTree.setShowRoot(false);


        studentTree.setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public TreeCell call(TreeView param) {
                final TreeCell<String> cell = new TreeCell<String>() {
                    @Override
                    public void updateItem(String text, boolean empty) {

                        super.updateItem(text, empty);
                        setDisclosureNode(null);
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
                };

                cell.setOnMouseClicked(event -> {
                    if (!cell.isEmpty()) {
                        cell.getTreeItem().setExpanded(!cell.getTreeItem().isExpanded());
                    }
                });



                return cell;
            }
        });

    }

    public void showPage(String pageName) {

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
            classSummaryController = loader.getController();

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
            FlowPane summary = loader.load();
            currentPage.setContent(summary);
            AnchorPane.setLeftAnchor(summary, 0.0);
            AnchorPane.setTopAnchor(summary, 0.0);
            AnchorPane.setBottomAnchor(summary, 0.0);
            AnchorPane.setRightAnchor(summary, 0.0);
            userSummaryController = loader.getController();
            //statsController = tutorStatsLoader.getController();

            userSummaryController.hideBadges();
            //change to be the user that was clicked on
            userSummaryController.createStudent(env, userName,"All Time", project);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateDisplay() {
        populateUserOptions();
        showPage("Summary");

    }
}
