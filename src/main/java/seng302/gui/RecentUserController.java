package seng302.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Created by jmw280 on 21/07/16.
 */
public class RecentUserController {


    @FXML
    Label username;

    @FXML
    StackPane userPane;

    @FXML
    ImageView userPic;

    UserLoginController parent;

    public RecentUserController() {

    }

    public void setUsername(String name) {
        username.setText(name);
    }

    public void setUserPic(Image image) {
        userPic.setImage(image);
    }

    public void setParentController(UserLoginController parent) {
        this.parent = parent;
    }

    @FXML
    protected void selectUser() {
        parent.deselectUsers();

        String cssBordering = "-fx-border-color:dodgerblue ; \n" //#090a0c
                + "-fx-border-insets:3;\n"
                + "-fx-border-radius:3;\n"
                + "-fx-border-width:5.0";


        userPane.setStyle(cssBordering);

        //Set the user name input field to be the username and auto-select the password field
        parent.onRecentSelect(username.getText());


    }

    protected String getStyle() {
        return userPane.getStyle();
    }

    protected void deselect() {
        userPane.setStyle("");
    }

}
