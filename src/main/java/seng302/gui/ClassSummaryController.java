package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.spreadsheet.StringConverterWithFormat;
import seng302.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassSummaryController {

    @FXML
    private VBox classSummary;

    @FXML
    private HBox chartHolder;

    @FXML
    private Slider tutorSlider;

    @FXML
    private JFXListView studentsAtStage;


    private Environment env;

    private ArrayList<String> tutorNames = new ArrayList<>();

    private Map<String, String> converted = new HashMap<>();

    public void create(Environment env) {
        this.env = env;
        setupBarChart();
        setupTutorSlider();

        converted.put("Musical Terms Tutor", "musicalTermsTutor");
        converted.put("Pitch Comparison Tutor", "pitchTutor" );
        converted.put("Scale Recognition Tutor","scaleTutor");
        converted.put("Chord Recognition Tutor", "chordTutor");
        converted.put("Interval Recognition Tutor", "intervalTutor");
        converted.put("Scale Recognition TutorA", "scaleTutorAdvanced");
        converted.put("Chord Recognition Tutor", "chordTutorAdvanced");
        converted.put("Chord Spelling Tutor", "chordSpellingTutor");
        converted.put("Scale Spelling Tutor", "scaleSpellingTutor");
        converted.put("Key Signature Tutor", "keySignatureTutor");
        converted.put("Diatonic Chord Tutor", "diatonicChordTutor");
        converted.put("Scale Modes Tutor", "scaleModesTutor");
    }

    private void setupTutorSlider() {
        tutorNames.add("Musical Terms");
        tutorNames.add("Pitch Comparison");
        tutorNames.add("Scale Recognition");
        tutorNames.add("Chord Recognition");
        tutorNames.add("Interval Recognition");
        tutorNames.add("Chord Spelling");
        tutorNames.add("Key Signature");
        tutorNames.add("Diatonic Chord");
        tutorNames.add("Scale Modes");
        tutorNames.add("Scale Spelling");

        tutorSlider.setLabelFormatter(new StringConverterWithFormat<Double>() {
            @Override
            public String toString(Double object) {
                return tutorNames.get(object.intValue());
            }

            @Override
            public Double fromString(String string) {
                return (double) tutorNames.indexOf(string);
            }
        });

        tutorSlider.setMin(0);
        tutorSlider.setMax(tutorNames.size() - 1);
        tutorSlider.setMajorTickUnit(1.0);
        tutorSlider.setMinorTickCount(0);
        tutorSlider.setShowTickLabels(true);
        tutorSlider.setSnapToTicks(true);
        tutorSlider.setShowTickMarks(true);

        populateStageUsers(0);

        tutorSlider.setOnDragDone(event -> {
            populateStageUsers((int) tutorSlider.getValue());
        });

        tutorSlider.setOnMouseClicked(event -> {
            populateStageUsers((int) tutorSlider.getValue());
        });



    }

    private void populateStageUsers(int tutor) {
        DataSnapshot classroomData = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users");
        studentsAtStage.getItems().clear();
        classroomData.getChildren().forEach(user -> {
            for (Object child : user.child("/projects").getChildren()) {
                try {
                    String unlockMap = ((DataSnapshot) child).child("unlockMap").getValue().toString();
                    String key = converted.get(tutorNames.get(tutor) + " Tutor");
                    if (unlockMap.contains(key + "\":true") && !studentsAtStage.getItems().contains(user.getKey()) ) {
                        studentsAtStage.getItems().add(user.getKey());
                    }
                } catch (Exception e) {
                    //no map
                }
            }
        });

        if (studentsAtStage.getItems().size() == 0) {
            studentsAtStage.getItems().add("None");
        }

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
