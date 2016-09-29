package seng302.Users;

/**
 * ProjectHandler
 *
 * In charge of handling user project data, including saving, loading and validating.
 *
 *
 * Created by Jonty on 12-Apr-16.
 */

import javafx.scene.control.TextInputDialog;
import org.json.simple.JSONArray;
import seng302.Environment;

import java.util.Optional;

public class ProjectHandler {

    private Project currentProject;
    Environment env;
    String lastOpened;
    String userName;


    /**
     * Creates a new project handler and loads all of a user's projects
     *
     * @param env  The environment in which the project handler is created
     * @param user The user whose projects are being loaded
     */
    public ProjectHandler(Environment env, String user) {
        this.userName = user;

        this.env = env;

        loadDefaultProject();

    }


    /**
     * Loads last opened project, or if there are no projects, the default project.
     */
    public void loadDefaultProject() {

        if (lastOpened == null) {
            setCurrentProject("default");
        } else {
            setCurrentProject(lastOpened);
        }
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Given the name of a project, changes the user over to that project.
     *
     * @param projName The name of the project to be loaded
     */
    public void setCurrentProject(String projName) {

        this.currentProject = new Project(env, projName, this);
        try {
            env.getUserPageController().updateLevelBadge();
            env.getUserPageController().updateGraphs(env.getUserPageController().getTimePeriod());
            env.getUserPageController().getSummaryController().updateProgressBar();


        } catch (Exception e) {
            System.err.println("Root controller not initialised. (updating level badge, update graphs and progress bar failed");
        }

    }

    public void deleteProject(String projName){

        env.getFirebase().getFirebase().child("classrooms/" + env.getUserHandler().getClassRoom()+ "/users/" +
                env.getUserHandler().getCurrentUser().getUserName()
                +"/projects/"+projName).removeValue();

        env.getRootController().getStage().close();

        this.env.getRootController().logOutUser();

    }



    /**
     * Creates a new project.
     */
    public void createNewProject() {


        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Project");
        dialog.setHeaderText("New Project");
        dialog.setContentText("Please enter the project name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newProject = result.get();
            JSONArray projects = getProjectList();
            if (!projects.contains(newProject)) {
                //new Project(env, newProject, this);
                setCurrentProject(newProject);
            } else {
                env.getRootController().errorAlert("The project: " + newProject + " already exists.");
                createNewProject();
            }

        }

    }


    public JSONArray getProjectList() {
        JSONArray projects = new JSONArray();
        env.getFirebase().getUserSnapshot().child("projects").getChildren().forEach((k) -> {
            projects.add(k.getKey());
        });


        return projects;

    }


}
