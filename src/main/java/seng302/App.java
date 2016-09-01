package seng302;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seng302.gui.RootController;
import seng302.gui.UserLoginController;


public class App extends Application {
    Stage primaryStage;
    Environment env;

    public static void main(String[] args) {


        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        env = new Environment();
        try {


            Stage stage = new Stage();
            /*
            stage.setTitle("Allegro");
            stage.setScene(new Scene(new Group()));
            stage.show();

            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(getClass().getResource("/Views/userLogin.fxml"));

            Parent root1 = loader1.load();
            Scene scene1 = new Scene(root1);

            stage.setTitle("Allegro");
            stage.setScene(scene1);
            stage.show();
            UserLoginController userLoginController = loader1.getController();
            userLoginController.setEnv(env);
            userLoginController.displayRecentUsers();

            */

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Views/newGui.fxml"));

            Parent root = loader.load();


            //Parent root = loader;
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());


            //Font.loadFont(ClassLoader.getSystemResource("/css/fonts/Roboto-Medium.tff").toExternalForm(), 24);


            primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Allegro - No Project");
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

            //set Stage boundaries to visible bounds of the main screen
            primaryStage.setX(primaryScreenBounds.getMinX());
            primaryStage.setY(primaryScreenBounds.getMinY());
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());

            primaryStage.setMinHeight(450);
            primaryStage.setMinWidth(700);

            RootController controller = loader.getController();
            try {
                controller.setEnvironment(env);
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode().equals(KeyCode.SHIFT)) {
                        env.setShiftPressed(true);
                    }
                });

                scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                    if (event.getCode().equals(KeyCode.SHIFT)) {
                        env.setShiftPressed(false);
                    }
                });

                controller.setStage(primaryStage);

                controller.showLoginWindow(new Stage());
                //primaryStage.show();
                //controller.setPrimaryStage(primaryStage);
            } catch (NullPointerException e) {
                System.err.println("Controller is null");
            }



            //controller.createWindow(scene);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
