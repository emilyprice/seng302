package seng302;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jat157 on 20/09/16.
 */
public class FirebaseUpdater {


    DatabaseReference firebase;


    DatabaseReference userRef;



    DataSnapshot userSnapshot;

    DataSnapshot classroomsSnapshot;

    public Cloudinary getImageCloud() {
        return imageCloud;
    }

    Cloudinary imageCloud;
    private Environment env;


    public FirebaseUpdater(Environment env){
        this.env = env;

        initializeFirebase();


//        DatabaseReference users = firebase.child("users/"+usernameInput.getText());



        firebase.child("classrooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classroomsSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        imageCloud = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "allegro123",
                "api_key", "732823974447246",
                "api_secret", "nGNnDUmFxWEG_lPZoJQCKyfz7hw"));
    }

    private void initializeFirebase(){

        try{
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("Allegro-e09e379e137e.json")) //Allegro-e09e379e137e.json
                    .setDatabaseUrl("https://allegro-8ce55.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
        }catch(FileNotFoundException fe){

        }


        // As an admin, the app has access to read and write all data, regardless of Security Rules
        firebase = FirebaseDatabase
                .getInstance()
                .getReference("");

    }


    private void updateUserProperties(){
        System.out.println("update user properties called");
        if(env.getUserHandler().getCurrentUser() != null){
            System.out.println("current user is not null!");
            env.getUserHandler().getCurrentUser().loadProperties();
        }
    }

    private void createUserSnapshot(String classroom, String user, Boolean blocking){

        final AtomicBoolean done = new AtomicBoolean(false);

        firebase.child("classrooms/"+classroom+"/users/"+user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userSnapshot = dataSnapshot;
                //updateUserProperties();
                //env.getUserHandler().getCurrentUser().
                //TODO: update user properties
                done.set(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        userRef = firebase.child("classrooms/" + classroom + "/users/" + user);
        while (!done.get() && blocking);
    }

    private void createUserSnapshot(String address, Boolean blocking){
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
        while (!done.get() && blocking);
    }

    public void createStudentSnapshot(String classroom, String user, Boolean blocking){
        String address = "classrooms/"+classroom+"/users/"+user;
        createUserSnapshot(address, blocking);

    }

    public void createTeacherSnapshot(String user, Boolean blocking){
        String address = "teachers/"+user;
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


}
