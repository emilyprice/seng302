package seng302.data;

import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * Created by jmw280 on 24/08/16.
 */
public class Badge {

    public String name;


    public int currentBadgeType;

    public Image badgeImage;

    public int badgeProgress;

    public ArrayList<Integer> badgeLevels;


    public String description;


    public Badge(String name, String description, ArrayList<Integer> badgeLevels, Integer badgeProgress, Integer currentBadgeType){
        this.name = name;
        this.currentBadgeType = currentBadgeType;
        this.description = description;
        this.badgeProgress = badgeProgress;
        this.badgeLevels = badgeLevels;
        this.badgeImage = null;
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
