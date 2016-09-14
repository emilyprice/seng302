package seng302.Users;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng302.Environment;
import seng302.utility.FileHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jmw280 on 22/07/16.
 */
public class UserHandler {



    private User currentUser;

    Environment env;
    JSONArray userList;
    JSONParser parser = new JSONParser(); //parser for reading project
    JSONObject UsersInfo = new JSONObject();
    JSONArray recentUsers = new JSONArray();

    final Path userDirectory = Paths.get("UserData"); //Default user path for now, before user compatibility is set up.


    public UserHandler(Environment env){
        this.env = env;

        populateUsers();

    }

    public JSONArray getUserNames(){
        return userList;
    }

    /**
     * Returns a Collection of User Objects, which containing basic information about users.
     * To be used before logging into a user.
     *
     * @return
     */
    public HashMap<String, User> getUsers(){
        ArrayList<String> names = (ArrayList<String>) userList;
        HashMap<String, User> users = new HashMap<>();

        for(String un : names){
            users.put(un, new User(env, un));
        }
        return users;
    }


    /**
     * Returns a collection of recent users to be displayed on the login screen.
     * @return
     */
    public ArrayList<User> getRecentUsers(){
        ArrayList<User> recentUsersTemp = new ArrayList<User>();


        for (Object user: recentUsers) {
            User tempUser = new User(env, user.toString());
            recentUsersTemp.add(tempUser);
        }

        return  recentUsersTemp;


    }

    /**
     * Populates the list of users from the users json file.
     *
     */
    private void populateUsers(){
        try {
            UsersInfo = (JSONObject) parser.parse(new FileReader(userDirectory + "/user_list.json"));
            this.userList = (JSONArray) UsersInfo.get("users");

            this.recentUsers = (JSONArray) UsersInfo.get("recentUsers");

        } catch (FileNotFoundException e) {
            try {
                System.err.println("users.json Does not exist! - Creating new one");
                userList = new JSONArray();


                UsersInfo.put("users", userList);
                UsersInfo.put("recentUsers", recentUsers);

                if (!Files.isDirectory(userDirectory)) {
                    //Create Projects path doesn't exist.
                    try {
                        Files.createDirectories(userDirectory);


                    } catch (IOException eIO3) {
                        //Failed to create the directory.
                        System.err.println("Well UserData directory failed to create.. lost cause.");
                    }
                }

                FileWriter file = new FileWriter(userDirectory + "/user_list.json");
                file.write(UsersInfo.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e2) {
                System.err.println("Failed to create users.json file.");


            }
        }catch (IOException e) {
                //e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    /**
     * Checks if the given login credentials are valid.
     * @param userName
     * @param password
     * @return true/false depending on login result.
     */
    public boolean userPassExists(String userName, String password){
        if(userList.contains(userName)){
            User tempUser = new User(env,userName);
            if (password.equals(tempUser.getUserPassword())){
                return true;
            }
        }
        return false;
    }


    /**
     * Creates a new user for the given username/password.
     * @param user
     * @param password
     */
    public void createUser(String user, String password){
        this.currentUser = new User(user, password, env);
        updateUserList(user);

    }

    public void logOut() {

    }


    /**
     * Updates the json list of user names, used to fill the quick load list.
     *
     */
    public void updateUserList(String username) {
        if (!userList.contains(username)) {
            userList.add(username);
        }
        if(!recentUsers.contains(username)){
            if(recentUsers.size() == 4){
                recentUsers.remove(0);
            }
            recentUsers.add(username);
        }
        saveUserList();


    }

    /**
     * Writes the currently saved user settings to disc.
     */
    private void saveUserList() {
        try { //Save list of projects.
            UsersInfo.put("users", userList);
            UsersInfo.put("recentUsers", recentUsers);
            FileWriter projectsJson = new FileWriter(userDirectory + "/user_list.json");
            projectsJson.write(UsersInfo.toJSONString());
            projectsJson.flush();
            projectsJson.close();


        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }



    public User getCurrentUser(){

        return currentUser;
    }

    public Path getCurrentUserPath(){
        return Paths.get("UserData/"+getCurrentUser().getUserName());
    }


    /**
     * Sets the current user and loads user related properties.
     * @param userName
     */
    public void setCurrentUser(String userName){
        this.currentUser = new User(env, userName);
        currentUser.loadFullProperties();
        updateUserList(userName);

        //update User drop down to display user's name
        env.getRootController().updateUserInfo(userName);

    }


    /**
     * Full deletes the specified user incl. project files.
     * @param username
     */
    public void deleteUser(String username) {

        //Step 1.For some reason this needs to be called? (all it does is delete the project handler
        this.getCurrentUser().delete();

        //Step 2. Delete from list of users/recent users.
        this.userList.remove(username);
        this.recentUsers.remove(username);
        saveUserList();

        //Step 3. Close the main window, which helps remove any file locks and request garbage collection.
        env.getRootController().getStage().close();
        System.gc();

        //Step 4. Delete all user folders and files.
        File userDir = Paths.get("UserData/" + username).toFile();

        if (userDir.isDirectory()) {
            Boolean res = FileHandler.deleteDirectory(userDir);
            if (!res) env.getRootController().errorAlert("Failed to fully remove the deleted user directory.");
        } else {
            System.err.println("Could not delete the user directory");
        }


        //Step 5. Open the User login window.

        this.env.getRootController().logOutUser();



    }

}
