package seng302;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles any communication to the application's Firebase database.
 * The database contains information for all classrooms/teachers and users.
 */
public class FirebaseUpdater {


    private DatabaseReference firebase;
    private DatabaseReference userRef;

    private DataSnapshot userSnapshot;

    private DataSnapshot classroomsSnapshot;


    private DatabaseReference classroomRef;

    private DataSnapshot teacherSnapshot;

    public Cloudinary getImageCloud() {
        return imageCloud;
    }

    private Cloudinary imageCloud;
    private Environment env;


    public FirebaseUpdater(Environment env) {
        this.env = env;

        initializeFirebase();
        createClassroomSnapshot(true);
        createTeacherSnapshot(true);

        // Where profile pictures are stored
        imageCloud = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "allegro123",
                "api_key", "732823974447246",
                "api_secret", "nGNnDUmFxWEG_lPZoJQCKyfz7hw"));
    }

    /**
     * Creates a snapshot of the current data in the classroom object in Firebase
     *
     * @param blocking Whether or not the function is blocking (waits for the snapshot to be retrieved)
     */
    public void createClassroomSnapshot(Boolean blocking) {

        final AtomicBoolean done = new AtomicBoolean(false);
        firebase.child("classrooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classroomsSnapshot = dataSnapshot;
                done.set(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        while (!done.get() && blocking) ;
    }

    public void createTeacherSnapshot(Boolean blocking) {
        final AtomicBoolean done = new AtomicBoolean(false);


        firebase.child("teachers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherSnapshot = dataSnapshot;
                done.set(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        while (!done.get() && blocking) ;
    }

    /**
     * Connects to the firebase database containing user information.
     * Uses credentials from local JSON file.
     */
    private void initializeFirebase() {

        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("Allegro-e09e379e137e.json")) //Allegro-e09e379e137e.json
                    .setDatabaseUrl("https://allegro-8ce55.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IllegalStateException fe) {

        } catch (FileNotFoundException e) {

        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        firebase = FirebaseDatabase
                .getInstance()
                .getReference("");

    }


    /**
     * Creates a snapshot for the given address.
     *
     * @param address  address for the student/teacher firebase location.
     * @param blocking Whether or not the function is asynchronous or not. If true, the caller is
     *                 blocked until the snapshot is loaded.
     */
    private void createUserSnapshot(String address, Boolean blocking) {
        final AtomicBoolean done = new AtomicBoolean(false);

        firebase.child(address).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userSnapshot = dataSnapshot;

                done.set(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
        userRef = firebase.child(address);
        while (!done.get() && blocking) ;
    }

    /**
     * Loads a firebase snapshot of a given user/classroom.
     *
     * @param classroom the classroom the user belongs too.
     * @param user      The user's username.
     * @param blocking  False for an asynchronous call.
     */
    public void createStudentSnapshot(String classroom, String user, Boolean blocking) {
        String address = "classrooms/" + classroom + "/users/" + user;
        createUserSnapshot(address, blocking);

    }

    /**
     * Loads a firebase snapshot of the given teacher's account.
     *
     * @param user     The teacher's username.
     * @param blocking False for an asynchronous call.
     */
    public void createTeacherSnapshot(String user, Boolean blocking) {
        String address = "teachers/" + user;
        createUserSnapshot(address, blocking);
    }


    public DataSnapshot getUserSnapshot() {
        return userSnapshot;
    }

    public DatabaseReference getFirebase() {
        return firebase;
    }

    public DataSnapshot getClassroomsSnapshot() {
        return classroomsSnapshot;
    }


    public DatabaseReference getUserRef() {
        return userRef;
    }

    public DataSnapshot getTeacherSnapshot() {
        return teacherSnapshot;
    }


}
