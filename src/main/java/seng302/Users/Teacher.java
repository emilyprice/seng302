

package seng302.Users;


import seng302.Environment;


/**
 * Handles functionality for representing and manipulating a teacher's information. Also handles
 * saving and loading teachers. Inherits User, which handles the basic information a teacher shares
 * with all users types.
 */


public class Teacher extends User {


    public Teacher(String userName, String password, Environment env) {

        env.getFirebase().createTeacherSnapshot(userName, true);
        userSnapshot = env.getFirebase().getUserSnapshot();

        this.userName = userName;
        this.userPassword = password;
        this.env = env;


        loadProperties();
        saveProperties();

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
