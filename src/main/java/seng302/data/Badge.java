package seng302.data;

import org.controlsfx.control.Notifications;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

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

    public void updateBadgeProgress(double progress){
        badgeProgress += progress;
        if(badgeLevels == null){
            updateImage();
            // notify user
            return;
        }

        while (badgeProgress >= badgeLevels.get(currentBadgeType)){
            currentBadgeType += 1;
            Image unlock = new Image(getClass().getResourceAsStream("/images/unlock.png"), 75, 75, true, true);
            List<String> badgeTypes = new ArrayList<>();
            badgeTypes.add("Locked");
            badgeTypes.add("Bronze");
            badgeTypes.add("Silver");
            badgeTypes.add("Gold");
            badgeTypes.add("Platinum");

            Notifications.create()
                    .title("New Badge")
                    .text("Well done! \nYou have unlocked " + tutorName + ": " + name + " (" + badgeTypes.get(currentBadgeType) + ")")
                    .hideAfter(new Duration(10000))
                    .graphic(new ImageView(unlock))
                    .show();
        }
    }


    private void updateImage(){
        //this.image = new image
    }


}
