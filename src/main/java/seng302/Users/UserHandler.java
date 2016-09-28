package seng302.Users;

import com.google.firebase.database.DataSnapshot;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng302.Environment;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles Users.
 */
public class UserHandler {

    private Student currentUser;
    private Teacher currentTeacher;

    Environment env;

    ArrayList<String> userList = new ArrayList<>();
    JSONParser parser = new JSONParser(); //parser for reading project

    JSONObject localData = new JSONObject();
    HashMap<String, Object> recentClassrooms = new HashMap<>();
    ArrayList<String> recentUsers = new ArrayList<>();

    final Path userDirectory = Paths.get("UserData/classrooms/group5/users/"); //Default user path for now, before user compatibility is set up.


    private String classroom;

    public UserHandler(Environment env) {
        this.env = env;

        populateUsers();

    }

    public ArrayList<String> getUserNames() {
        return userList;
    }

    public ArrayList<String> getRecentUserNames() {
        return recentUsers;
    }

    public void loadRecentUsers() {
        Path userDirectory = Paths.get("UserData");
        try {
            localData = (JSONObject) parser.parse(new FileReader(userDirectory+ "/local_data.json"));
            recentUsers = new ArrayList<>();
            try {
                recentClassrooms = (HashMap<String, Object>) localData.get("recentUsers");

                if (recentClassrooms.containsKey(this.classroom)) {
                    recentUsers = (ArrayList<String>) recentClassrooms.get(this.classroom);

                } else {
                    //Classroom doesn't exist inside the classrooms hashmap

                    recentUsers = new ArrayList<>();
                    recentClassrooms.put(classroom, recentUsers);
                }
            } catch (NullPointerException e) {
                //Classroom hashmap doesn't exist inside localData
                recentClassrooms = new HashMap<>();

            }

        } catch (FileNotFoundException e) {
            try {
                System.err.println("local_data.json Does not exist! - Creating new one");
                if (!Files.isDirectory(userDirectory)) {
                    //Create Projects path doesn't exist.
                    try {
                        Files.createDirectories(userDirectory);


                    } catch (IOException eIO3) {
                        //Failed to create the directory.
                        System.err.println("Well UserData directory failed to create.. lost cause.");
                    }
                }


                recentClassrooms = new HashMap<>();
                recentClassrooms.put(classroom, recentUsers);
                localData.put("recentUsers", recentClassrooms);


                FileWriter file = new FileWriter(userDirectory + "/local_data.json");
                file.write(localData.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e2) {
                System.err.println("Failed to create local_data.json file.");
                e2.printStackTrace();

            }
        } catch (IOException e) {
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the list of users from the users json file.
     */
    public void populateUsers() {

        userList.clear();
        for (DataSnapshot user : env.getFirebase().getClassroomsSnapshot().child(classroom + "/users/").getChildren()) {

            userList.add(user.getKey());

        }

    }


    /**
     * Checks if the given login credentials are valid.
     *
     * @return true/false depending on login result.
     */
    public boolean userPassExists(String userName, String password) {

        if (env.getFirebase().getClassroomsSnapshot().child(classroom + "/users/" + userName).exists()) {

            String fbPass = (String) env.getFirebase().getUserSnapshot().child("properties/password").getValue();
            if (password.equals(fbPass)) {

                return true;
            }
        }

        return false;
    }


    /**
     * Creates a new user for the given username/password.
     */
    public void createUser(String user, String password) {
        this.currentUser = new Student(user, password, env);

    }

    public void createTeacher(String user, String password) {
        this.currentTeacher = new Teacher(user, password, env);
    }


    public void updateRecentUsers(String username) {
        if (!recentUsers.contains(username)) {
            if (recentUsers.size() >= 4) {
                recentUsers.remove(recentUsers.size() - 1);
            }
            recentUsers.add(0, username);
        }

        saveLocalData();
    }


    /**
     * Writes the currently saved user settings to disc.
     */
    private void saveLocalData() {
        try { //Save list of projects.
            recentClassrooms.put(classroom, recentUsers);

            localData.put("recentUsers", recentClassrooms); // -> RecentUsers -> Classroom -> User
            FileWriter projectsJson = new FileWriter("UserData/local_data.json");
            projectsJson.write(localData.toJSONString());
            projectsJson.flush();
            projectsJson.close();


        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }


    public Student getCurrentUser() {

        return currentUser;
    }




    /**
     * Sets the current user and loads user related properties.
     */
    public void setCurrentUser(String userName, String classroom, String password) {
        this.classroom = classroom;
        this.currentUser = new Student(userName, password, env);
        updateRecentUsers(userName);
    }


    /**
     * Full deletes the specified user incl. project files.
     */
    public void deleteUser(String classroom, String username) {

        this.recentUsers.remove(username);
        saveLocalData();

        env.getRootController().getStage().close();

        this.env.getRootController().logOutUser();
        env.getFirebase().getFirebase().child("classrooms/" + classroom + "/users/" + username).removeValue();


    }

    public String getClassRoom() {
        return classroom;
    }

    public void setClassRoom(String classRoom) {
        this.classroom = classRoom;
    }

}
