package seng302.data;

import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * Created by jmw280 on 24/08/16.
 */
public class Badge {

    public String name;

    public String tutorName;

    public int currentBadgeType;

    public String imageName;

    public double badgeProgress;

    public ArrayList<Integer> badgeLevels;


    public String description;


    public Badge(String name, String tutorName, String description, ArrayList<Integer> badgeLevels, double badgeProgress, Integer currentBadgeType, String imageName){
        this.name = name;
        this.tutorName = tutorName;
        this.currentBadgeType = currentBadgeType;
        this.description = description;
        this.badgeProgress = badgeProgress;
        this.badgeLevels = badgeLevels;
        this.imageName = imageName;
    }

    public void updateBadgeProgress(Integer progress){

        badgeProgress += progress;
        if(badgeLevels == null){
            updateImage();
            // notify user
            return;
        }

        if(badgeProgress > badgeLevels.get(currentBadgeType)){
            currentBadgeType += 1;
            updateImage();
        }
    }


    private void updateImage(){
        //this.image = new image
    }


}
