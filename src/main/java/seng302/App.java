package seng302;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seng302.gui.RootController;


public class App extends Application {
    Environment env;

    public static void main(String[] args) {


        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        env = new Environment();
        try {


            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Views/newGui.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

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
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.err.println("Controller is null");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
