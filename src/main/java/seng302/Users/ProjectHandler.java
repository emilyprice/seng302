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
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import seng302.Environment;

public class ProjectHandler {

    private Project currentProject;
    //private ArrayList<String> projects = new ArrayList<String>();
    Environment env;
    String lastOpened;
    public Path projectsDirectory;
    String userName;

    JSONArray projectList;

    JSONObject projectsInfo = new JSONObject();
    JSONParser parser = new JSONParser(); //parser for reading project


    public ProjectHandler(Environment env, String user){
        //Iterate through user directory and load all
        this.userName = user;
        projectsDirectory = Paths.get("UserData/"+user+"/Projects");
        this.env = env;

        loadProjectList();
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

    public void setCurrentProject(String projName){

        this.currentProject = new Project(env, projName, this);
        updateProjectList(projName);


    }



    /**
     * Updates the json list of project names, used to fill the quick load list.
     *
     */
    public void updateProjectList(String projectName) {
        if (!projectList.contains(projectName)) {
            projectList.add(projectName);
        }

        try { //Save list of projects.
            projectsInfo.put("projects", projectList);
            projectsInfo.put("lastOpened", projectName);
            FileWriter projectsJson = new FileWriter(projectsDirectory + "/projects.json");
            projectsJson.write(projectsInfo.toJSONString());
            projectsJson.flush();
            projectsJson.close();


        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }


    /**
     * Loads the JSON file containing the list of projects.
     */
    private void loadProjectList(){
        JSONObject projectsInfo = new JSONObject();

        JSONParser parser = new JSONParser(); //parser for reading project
        try {
             projectsInfo = (JSONObject) parser.parse(new FileReader(projectsDirectory + "/projects.json"));
            this.projectList = (JSONArray) projectsInfo.get("projects");
            this.lastOpened = projectsInfo.get("lastOpened").toString();

        } catch (FileNotFoundException e) {
            try {
                System.err.println("projects.json Does not exist! - Creating new one");
                this.projectList = new JSONArray();


                projectsInfo.put("projects", projectList);
                projectsInfo.put("lastOpened", "default");


                if (!Files.isDirectory(projectsDirectory)) {

                    //Create Projects path doesn't exist.
                    try {
                        Files.createDirectories(projectsDirectory);


                    } catch (IOException eIO3) {
                        //Failed to create the directory.
                        System.err.println("Well UserData directory failed to create.. lost cause.");
                    }
                }

                FileWriter file = new FileWriter(projectsDirectory + "/projects.json");
                file.write(projectsInfo.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e2) {
                System.err.println("Failed to create projects.json file.");

            }


        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            String resultString = result.get().toString();
            Path path;
            try {
                path = Paths.get(projectsDirectory+"/" + resultString);

                if (!Files.isDirectory(path)) {
                    try {

                        Files.createDirectories(path);
                        setCurrentProject(resultString);

                        getCurrentProject().saveProject(path.toString().replace("\\", "/"));
                        //setWindowTitle(resultString);

                    } catch (IOException e) {
                        //Failed to create the directory.
                        e.printStackTrace();
                    }

                } else {
                    env.getRootController().errorAlert("The project: " + resultString + " already exists.");
                    createNewProject();
                }

            } catch (InvalidPathException invPath) {
                //invalid path (Poor project naming)
                env.getRootController().errorAlert("Invalid file name - try again.");
                createNewProject();
            }

        }
    }


    public JSONArray getProjectList() {
        return this.projectList;
    }



}
