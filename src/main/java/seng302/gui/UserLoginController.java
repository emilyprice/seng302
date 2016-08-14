package seng302.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.Users.User;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jmw280 on 21/07/16.
 */
public class UserLoginController {

    @FXML
    HBox recentUsersHbox;

    @FXML
    TextField usernameInput;

    @FXML
    PasswordField passwordInput;

    @FXML
    Button btnRegister;

    @FXML
    Label labelError;

    @FXML
    Button btnLogIn;

    Environment env;

    ArrayList<RecentUserController> recentUsers = new ArrayList<>();


    public UserLoginController(){

    }

    public void setEnv(Environment env){
        this.env = env;
    }



    protected void deselectUsers(){
        for(RecentUserController recentUser: recentUsers){
            System.out.println(recentUser.getStyle());
            recentUser.deselect();
        }
    }

    protected void onRecentSelect(String username){
        usernameInput.setText(username);
        passwordInput.clear();
        passwordInput.requestFocus();
    }


    private Node generateRecentUser(String username, Image image){

        Node recentUser;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/recentUser.fxml"));
        try {
            recentUser = loader.load();
            RecentUserController recentUserController = loader.getController();
            recentUserController.setParentController(this);

            recentUserController.setUsername(username);
            recentUserController.setUserPic(image);

            recentUsers.add(recentUserController);
            return recentUser;

        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }



    public void displayRecentUsers(){
        String name;
        //Image image = new Image(getClass().getResourceAsStream
                //("/images/gear-1119298_960_720.png"), 10, 10, true, true);

        System.out.println(env.getUserHandler().getRecentUsers());
        for(User user: env.getUserHandler().getRecentUsers()) {
            name = user.getUserName();

            Image image = user.getUserPicture();
            recentUsersHbox.getChildren().add(generateRecentUser(name, image));
        }


    }

    @FXML
    protected void register(){


        if (!(env.getUserHandler().getUserNames().contains(usernameInput.getText()))){
            env.getUserHandler().createUser(usernameInput.getText(), passwordInput.getText());
            logIn();
        }
        else{
            System.out.println("user already exists!");
            labelError.setText("User already exists!");
            labelError.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    protected void logIn(){



        if(env.getUserHandler().userPassExists(usernameInput.getText(), passwordInput.getText())){


            env.getUserHandler().setCurrentUser(usernameInput.getText());

            //Close login window.
            Stage stage = (Stage) btnLogIn.getScene().getWindow();
            stage.close();

            env.getRootController().showWindow(true);
        }else{

            final Label message = new Label("");
            labelError.setText("Invalid username or password.");
            labelError.setTextFill(javafx.scene.paint.Color.RED);

        }

    }

}