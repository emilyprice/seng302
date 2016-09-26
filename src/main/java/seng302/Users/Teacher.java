

package seng302.Users;


import seng302.Environment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seng302.Environment;

/**
 * Handles functionality for representing and manipulating a teacher's information. Also handles saving
 * and loading teachers. Inherits User, which handles the basic information a teacher shares with all users types.
 */


public class Teacher extends User {

    private List<String> classrooms;


    public Teacher(String userName, String password, Environment env, String classroom) {

        env.getFirebase().createTeacherSnapshot(userName, true);

        Map classroomInfo = new HashMap<>();
        classroomInfo.put("users", "none");
        env.getFirebase().getFirebase().child("classrooms/" + classroom).updateChildren(classroomInfo);
        userSnapshot = env.getFirebase().getUserSnapshot();

        this.userName = userName;
        this.userPassword = password;
        this.env = env;

        classrooms = new ArrayList<>();
        classrooms.add(classroom);
        //properties = new JSONObject();

        createUserFiles();

        loadProperties();
        saveProperties();

        env.getFirebase().getUserRef().child("classrooms").setValue(classrooms);

    }


    /**
     * Loads user properties, and any properties associated with teachers only.
     */
    public void loadProperties() {

        super.loadProperties();


    }


    public void updateProperties() {

        super.updateProperties();
    }







}
