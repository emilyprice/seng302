package seng302.data;

import javafx.scene.image.Image;

/**
 * Created by jmw280 on 24/08/16.
 */
public class Badge {

    private String name;

    private Integer type; // bronze silver gold plat

    private Integer dificulty;

    private Image image;

    private Integer nextBadgeProgress;

    private String description;


    public Badge(String name, String description){
        this.name = name;
        //this.image = image;
        this.description = description;
        this.type = 0;
        this.nextBadgeProgress = 30;

    }


    public void updateBadgeProgress(){



        if(nextBadgeProgress < 0){
            //set new nextbadge progress
            updateImage();
        }

    }


    private void updateImage(){
        //this.image = new image

    }
}
