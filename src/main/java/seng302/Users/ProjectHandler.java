package seng302.Users;

/**
 * ProjectHandler
 *
 * In charge of handling user project data, including saving, loading and validating.
 *
 *
 * Created by Jonty on 12-Apr-16.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import seng302.Environment;

public class ProjectHandler {

    private Project currentProject;
    Environment env;
    String lastOpened;
//    public Path projectsDirectory;
    String userName;

    //JSONArray projectList;

    JSONObject projectsInfo = new JSONObject();
    JSONParser parser = new JSONParser(); //parser for reading project


    /**
     * Creates a new project handler and loads all of a user's projects
     *
     * @param env  The environment in which the project handler is created
     * @param user The user whose projects are being loaded
     */
    public ProjectHandler(Environment env, String user){
        this.userName = user;

        this.env = env;

        //loadProjectList();
        loadDefaultProject();

    }


    /**
     * Loads last opened project, or if there are no projects, the default project.
     */
    public void loadDefaultProject(){

        if(lastOpened == null){
            setCurrentProject("default");
        }
        else{
            setCurrentProject(lastOpened);
        }
    }

    public Project getCurrentProject(){
        return currentProject;
    }

    /**
     * Given the name of a project, changes the user over to that project.
     * @param projName The name of the project to be loaded
     */
    public void setCurrentProject(String projName){

        this.currentProject = new Project(env, projName, this);
        //updateProjectList(projName);
        try {
            env.getRootController().updateLevelBadge();
            env.getUserPageController().updateLevelBadge();
            env.getUserPageController().updateGraphs();
            env.getUserPageController().getSummaryController().updateProgressBar();
        }
        catch (Exception e) {
            System.err.println("Root controller not initialised");
        }
    }



    /**
     * Updates the json list of project names, used to fill the quick load list.
     *
     */
    public void updateProjectList(String projectName) {
        //TODO: FIX THIS to work with firebase
//        if (!projectList.contains(projectName)) {
//            projectList.add(projectName);
//        }
//
//        try { //Save list of projects.
//            projectsInfo.put("projects", projectList);
//            projectsInfo.put("lastOpened", projectName);
//            FileWriter projectsJson = new FileWriter(projectsDirectory + "/projects.json");
//            projectsJson.write(projectsInfo.toJSONString());
//            projectsJson.flush();
//            projectsJson.close();
//
//
//        } catch (Exception e2) {
//            e2.printStackTrace();
//        }
    }


    /**
     * Loads the JSON file containing the list of projects.
     */
    private void loadProjectList(){
        //TODO: Fix this to work with firebase
//        JSONObject projectsInfo = new JSONObject();
//
//        JSONParser parser = new JSONParser(); //parser for reading project
//        try {
//             projectsInfo = (JSONObject) parser.parse(new FileReader(projectsDirectory + "/projects.json"));
//            this.projectList = (JSONArray) projectsInfo.get("projects");
//            this.lastOpened = projectsInfo.get("lastOpened").toString();
//
//        } catch (FileNotFoundException e) {
//            try {
//                System.err.println("projects.json Does not exist! - Creating new one");
//                this.projectList = new JSONArray();
//
//
//                projectsInfo.put("projects", projectList);
//                projectsInfo.put("lastOpened", "default");
//
//
//                if (!Files.isDirectory(projectsDirectory)) {
//
//                    //Create Projects path doesn't exist.
//                    try {
//                        Files.createDirectories(projectsDirectory);
//
//
//                    } catch (IOException eIO3) {
//                        //Failed to create the directory.
//                        System.err.println("Well UserData directory failed to create.. lost cause.");
//                    }
//                }
//
//                FileWriter file = new FileWriter(projectsDirectory + "/projects.json");
//                file.write(projectsInfo.toJSONString());
//                file.flush();
//                file.close();
//
//            } catch (IOException e2) {
//                System.err.println("Failed to create projects.json file.");
//
//            }
//
//
//        } catch (IOException e) {
//            //e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
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
                new Project(env, newProject, this);
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
