package seng302.gui;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng302.Environment;

public class ClassSummaryController {

    @FXML
    private VBox classSummary;

    @FXML
    private HBox chartHolder;


    private Environment env;

    public void create(Environment env) {
        this.env = env;
        setupBarChart();
    }

    private void setupBarChart() {

        Map<String, Integer> numLevels = new HashMap<>();

        final int[] highestLevel = {0};

        DataSnapshot classroomData = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users");

        classroomData.getChildren().forEach(user -> {
            for (Object child : user.child("/projects").getChildren()) {
                String level = ((DataSnapshot) child).child("level").getValue().toString();
                if (Integer.parseInt(level) > highestLevel[0]) {
                    highestLevel[0] = Integer.parseInt(level);
                }
                if (numLevels.get(level) != null) {
                    int count = numLevels.get(level) + 1;
                    numLevels.put(level, count);
                } else {
                    numLevels.put(level, 1);
                }

            }
        });

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> levelBar =
                new BarChart<String, Number>(xAxis, yAxis);


        xAxis.setLabel("Level");
        yAxis.setLabel("Number of Projects");

        levelBar.setLegendVisible(false);

        levelBar.setPrefHeight(400);
        levelBar.setMinSize(400, 400);


        XYChart.Series series1 = new XYChart.Series();

        for (int i = 1; i <= highestLevel[0]; i++) {
            if (numLevels.containsKey(Integer.toString(i))) {
                series1.getData().add(new XYChart.Data(Integer.toString(i), numLevels.get(Integer.toString(i))));
            } else {
                series1.getData().add(new XYChart.Data(Integer.toString(i), 0));
            }
        }

        levelBar.getData().add(series1);

        chartHolder.getChildren().add(levelBar);
    }

}
