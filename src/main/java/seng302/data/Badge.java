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

    public Integer expWorth;

    public String description;


    public Badge(String name, String tutorName, String description, ArrayList<Integer> badgeLevels, double badgeProgress, Integer currentBadgeType, String imageName, Integer expWorth){
        this.name = name;
        this.tutorName = tutorName;
        this.currentBadgeType = currentBadgeType;
        this.description = description;
        this.badgeProgress = badgeProgress;
        this.badgeLevels = badgeLevels;
        this.imageName = imageName;
        this.expWorth = expWorth;
    }

    public boolean isLocked() {
        return currentBadgeType == 0;
    }

    /**
     * Used to update a badge's progress either after a tutor session or once the badges criteria
     * has been met.
     * @param env the Environment
     * @param progress the amount of progress the badge as increased in
     */
    public void updateBadgeProgress(seng302.Environment env, double progress){
        Image unlock = new Image(getClass().getResourceAsStream("/images/unlock.png"), 75, 75, true, true);
        badgeProgress += progress;

        while (badgeProgress >= badgeLevels.get(currentBadgeType)){
            currentBadgeType += 1;
            try {
                if (badgeLevels.get(currentBadgeType) == - 1) {
                    // Badge progress is complete
                    badgeProgress = badgeLevels.get(currentBadgeType);
                    currentBadgeType = 4;
                    Notifications.create()
                            .title("New Badge")
                            .text("Well done! \nYou have completed " + tutorName + ": " + name + " (Platinum)")
                            .hideAfter(new Duration(10000))
                            .graphic(new ImageView(unlock))
                            .show();
                    return;
                }
            } catch (IndexOutOfBoundsException i) {
                currentBadgeType = 4;
                badgeProgress = badgeLevels.get(currentBadgeType - 1);
                return;
            }
            try {
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().addExperience(expWorth * currentBadgeType);
            } catch (NullPointerException e) {}

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
}
