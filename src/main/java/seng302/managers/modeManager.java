package seng302.managers;

import java.util.HashMap;

/**
 * Created by Joseph on 26/08/2016.
 */
public class ModeManager {


    public Integer currentUnlocks = 3;

    public Boolean isCompetitiveMode = true;



    HashMap<Integer, String> unlockHelp = new HashMap<>();
    public HashMap<String, Integer> tutorNumUnlocksMap = new HashMap<>();




    public ModeManager(){
        populateUnlockHelp();
        populateTutorNumUnlocksMap();
    }


    private void populateUnlockHelp(){
        unlockHelp.put(2, "You need to first add a musical term and complete one Musical terms tutor session");
        unlockHelp.put(3, "A score of ... must be achieved on the Pitch Comparison tutor");
        unlockHelp.put(4, "");
        unlockHelp.put(5, "");
        unlockHelp.put(6, "");
        unlockHelp.put(7, "");
        unlockHelp.put(8, "");
        unlockHelp.put(9, "");
        unlockHelp.put(10, "");
        unlockHelp.put(11, "");

    }

    private void populateTutorNumUnlocksMap(){
        tutorNumUnlocksMap.put("Musical Terms Tutor", 1);
        tutorNumUnlocksMap.put("Pitch Comparison Tutor", 2);
        tutorNumUnlocksMap.put("Scale Recognition Tutor(basic)", 3);
        tutorNumUnlocksMap.put("Chord Recognition Tutor (basic)", 4);
        tutorNumUnlocksMap.put("Interval Recognition Tutor", 5);
        tutorNumUnlocksMap.put("Scale Recognition Tutor", 6);
        tutorNumUnlocksMap.put("Chord Recognition Tutor", 7);
        tutorNumUnlocksMap.put("Chord Spelling Tutor", 8);
        tutorNumUnlocksMap.put("Key Signature Tutor", 9);
        tutorNumUnlocksMap.put("Diatonic Chord Tutor", 11);
    }


}
