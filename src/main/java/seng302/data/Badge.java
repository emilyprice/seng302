package seng302.data;

import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * Created by jmw280 on 24/08/16.
 */
public class Badge {

    private String name;


    public Integer currentBadgeType;

    public Image image;

    private Integer badgeProgress;

    public ArrayList<Integer> badgeLevels;


    private String description;


    public Badge(String name, String description, ArrayList badgeLevels, Integer badgeProgress, Integer currentBadgeType){
        this.name = name;
        this.currentBadgeType = currentBadgeType;
        //this.image = image;
        this.description = description;
        this.badgeProgress = badgeProgress;
        this.badgeLevels = badgeLevels;

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
