package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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
    void openSettings() {
        env.getRootController().launchTeacherSettings();
    }

    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setTeacherPageController(this);
    }

    public void load() {
        updateNameDisplay();

        env.getRootController().setWindowTitle("Allegro - " + env.getUserHandler().getClassRoom());

        updateProfilePicDisplay();

        env.getFirebase().getTeacherSnapshot().child(env.getUserHandler().getCurrentTeacher().getUserName() + "/classrooms").getChildren().forEach(classroomName -> {
            teacherClasses.add(classroomName.getValue().toString());
        });


        env.getRootController().updateClassroomsList(teacherClasses);
        populateUserOptions();
        showPage("Summary");
    }

    /**
     * Sets name under profile pic to display the user's name or username if they have no name.
     */
    public void updateNameDisplay() {
        try {

            txtFullName.setText(env.getUserHandler().getCurrentTeacher().getUserFirstName() + " "
                    + env.getUserHandler().getCurrentTeacher().getUserLastName());
        } catch (NullPointerException e) {
            txtFullName.setText(env.getUserHandler().getCurrentTeacher().getUserName());
            //txtFullName not initialized yet.
        }
    }

    /**
     * Updates the view of the teacher's profile picture.
     * Displays their picture in a circle
     */
    public void updateProfilePicDisplay() {
        Circle imageClip = new Circle(50, 50, 50);
        imageDP2.setClip(imageClip);
        imageDP2.setImage(env.getUserHandler().getCurrentTeacher().getUserPicture());
    }


    /**
     * Creates the treeview of all students in the current class.
     * Also creates a custom tree cell object for this treeview.
     */
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

            for (DataSnapshot data : userProjects.getChildren()) {
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

    /**
     * Displays either the summary page, or an overview of a student's project
     *
     * @param pageName The name of the page to be displayed
     */
    public void showPage(String pageName) {

        if (pageName.equals("Summary")) {
            showSummaryPage();
        } else {
            showUserPage(pageName);
        }


    }

    /**
     * Displays a page containing a summary of all data about students in the current classroom
     */
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

    /**
     * Shows the summary page of a specific student's project
     *
     * @param userInfo A string of the form studentname/projectname
     */
    public void showUserPage(String userInfo) {

        String project = userInfo.substring(userInfo.lastIndexOf('/') + 1);
        String userName = userInfo.substring(0, userInfo.lastIndexOf('/'));


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/UserSummary.fxml"));

        try {
            GridPane summary = loader.load();
            currentPage.setContent(summary);
            AnchorPane.setLeftAnchor(summary, 0.0);
            AnchorPane.setTopAnchor(summary, 0.0);
            AnchorPane.setBottomAnchor(summary, 0.0);
            AnchorPane.setRightAnchor(summary, 0.0);
            userSummaryController = loader.getController();
            //statsController = tutorStatsLoader.getController();

            userSummaryController.hideBadges();
            userSummaryController.setSecretInfo(userName, project);
            userSummaryController.setupFirebaseListener(userName, project);
            //change to be the user that was clicked on
            userSummaryController.createStudent(env, userName, "All Time", project);
            userSummaryController.showStudentStagemap(userName, project);
            userSummaryController.displayFeedbackInput(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Used whenever the current classroom is changed. Repopulates the list of students and changes the window title.
     */
    public void updateDisplay() {
        populateUserOptions();
        showPage("Summary");
        env.getRootController().setWindowTitle("Allegro - " + env.getUserHandler().getClassRoom());

    }

    @FXML
    public void onLogoutClick() {
        env.getRootController().logOutUser();
    }
}
