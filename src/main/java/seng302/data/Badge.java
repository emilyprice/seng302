package seng302.data;

import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * Created by jmw280 on 24/08/16.
 */
public class Badge {

    private String name;

    private Integer type; // bronze silver gold plat

    private Integer currentBadge;

    private Image image;

    private Integer badgeProgress;

    private ArrayList<Integer> badgeLevels;


    private String description;


    public Badge(String name, String description, ArrayList badgeLevels){
        this.name = name;
        this.currentBadge = 0;
        //this.image = image;
        this.description = description;
        this.type = 0;
        this.badgeProgress = 0;
        this.badgeLevels = badgeLevels;

    }


    public void updateBadgeProgress(Integer progress){

        badgeProgress += progress;

        if(badgeProgress > badgeLevels.get(currentBadge)){
            currentBadge += 1;
            updateImage();
        }
    }


    private void updateImage(){
        //this.image = new image

    }
}
