package seng302;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by jat157 on 20/09/16.
 */
public class FirebaseUpdater {


    DatabaseReference firebase;


    DatabaseReference userRef;



    DataSnapshot userSnapshot;

    DataSnapshot classroomsSnapshot;
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
        for(FirebaseApp a : FirebaseApp.getApps()){
            System.out.println(a);
        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        firebase = FirebaseDatabase
                .getInstance()
                .getReference("");



    }

    public void createUserSnapshot(String classroom, String user){
        firebase.child("classrooms/"+classroom+"/users/"+user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userSnapshot = dataSnapshot;
                //env.getUserHandler().getCurrentUser().
                //TODO: update user properties

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        userRef = firebase.child("classrooms/" + classroom + "/users/" + user);
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
