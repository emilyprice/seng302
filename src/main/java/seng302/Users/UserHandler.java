package seng302.Users;

import com.google.firebase.database.DataSnapshot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import seng302.Environment;

/**
 * Created by jmw280 on 22/07/16.
 */
public class UserHandler {


    private Student currentUser;
    private Teacher currentTeacher;

    Environment env;
    //JSONArray userList;
    ArrayList<String> userList = new ArrayList<>();
    JSONParser parser = new JSONParser(); //parser for reading project
    //JSONObject UsersInfo = new JSONObject();
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
        try {
            localData = (JSONObject) parser.parse(new FileReader("UserData/local_data.json"));
            recentUsers = new ArrayList<>();
            try {
                recentClassrooms = (HashMap<String, Object>) localData.get("recentUsers");


                if (recentClassrooms.containsKey(this.classroom)) {
                    recentUsers = (ArrayList<String>) recentClassrooms.get(this.classroom);
                    System.out.println("recent users");
                    System.out.println(recentUsers);


                } else {
                    //Classroom doesn't exist inside the classrooms hashmap
                    System.out.println("classroom hasn't been added to recent users yet, adding it.");
                    recentUsers = new ArrayList<>();
                    recentClassrooms.put(classroom, recentUsers);
                }
            } catch (NullPointerException e) {
                //TODO: Handle having adding a new HashMap of classrooms.
                //Classroom hashmap doesn't exist inside localData
                recentClassrooms = new HashMap<>();

            }

        } catch (FileNotFoundException e) {
            try {
                System.err.println("local_data.json Does not exist! - Creating new one");

                //UsersInfo.put("users", userList);
                recentClassrooms = new HashMap<>();
                recentClassrooms.put(classroom, recentUsers);
                localData.put("recentUsers", recentClassrooms);


                FileWriter file = new FileWriter("UserData/local_data.json");
                file.write(localData.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e2) {
                System.err.println("Failed to create local_data.json file.");

            }
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the list of users from the users json file.
     */
    public void populateUsers() {

        while (env.getFirebase().getClassroomsSnapshot() == null) {
            continue;
            //TODO: Fix this hack (And the other similar instance)
        }

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
        System.out.println("user list: " + userList.size());
        if (env.getFirebase().getClassroomsSnapshot().child(classroom + "/users/" + userName).exists()) {
            //System.out.println("FIREBASE CONTAINS the Deets");
            //User tempUser = new User(env,userName);
            String fbPass = (String) env.getFirebase().getUserSnapshot().child("properties/password").getValue();
            if (password.equals(fbPass)) {
                System.out.println("password is equaaal");
                return true;
            }
        }

        /* 23sept
        if(userList.contains(userName)){


            User tempUser = new User(env,userName);
            if (password.equals(tempUser.getUserPassword())){
                return true;
            }
        }
         */
        return false;
    }


    /**
     * Creates a new user for the given username/password.
     */
    public void createUser(String user, String password) {
        this.currentUser = new Student(user, password, env);
        //updateUserList(user);

    }

    public void createTeacher(String user, String password, String classroom) {
        this.currentTeacher = new Teacher(user, password, env, classroom);
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
            System.out.println(recentClassrooms);
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

    public Teacher getCurrentTeacher() {
        return currentTeacher;
    }

    public Path getCurrentUserPath() {
        return Paths.get("UserData/classrooms/group5/users/" + getCurrentUser().getUserName());
    }


    /**
     * Sets the current user and loads user related properties.
     */
    public void setCurrentUser(String userName, String classroom, String password) {
        this.classroom = classroom;
        this.currentUser = new Student(userName, password, env);
        //currentUser.loadFullProperties();
        //updateUserList(userName);
        updateRecentUsers(userName);
        //update User drop down to display user's name
        env.getRootController().updateUserInfo(userName);

    }

    public void setCurrentTeacher(String userName, String classroom, String password) {
        this.classroom = classroom;
        this.currentTeacher = new Teacher(userName, password, env, classroom);

    }

    public void removeCurrentTeacher() {
        this.currentTeacher = null;
    }


    /**
     * Full deletes the specified user incl. project files.
     */
    public void deleteUser(String username) {

        //Step 1.For some reason this needs to be called? (all it does is delete the project handler
        //this.getCurrentUser().delete(); //23sept temp deleted

        //Step 2. Delete from list of users/recent users.
        this.userList.remove(username);
        this.recentUsers.remove(username);
        saveLocalData();

        //Step 3. Close the main window, which helps remove any file locks and request garbage collection.
        env.getRootController().getStage().close();
        /*System.gc();*/

        //Step 4. Delete all user folders and files.
        /*File userDir = Paths.get("UserData/classrooms/group5/users/" + username).toFile();

        if (userDir.isDirectory()) {
            Boolean res = FileHandler.deleteDirectory(userDir);
            if (!res) env.getRootController().errorAlert("Failed to fully remove the deleted user directory.");
        } else {
            System.err.println("Could not delete the user directory");
        }*/


        //Step 5. Open the User login window.

        this.env.getRootController().logOutUser();


    }

    public String getClassRoom() {
        return classroom;
    }

    public void setClassRoom(String classRoom) {
        this.classroom = classRoom;
    }

}
