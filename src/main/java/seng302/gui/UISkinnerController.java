package seng302.gui;

import com.jfoenix.controls.JFXColorPicker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import seng302.Environment;
import seng302.utility.ColourUtils;

import java.util.ArrayList;

/**
 * Controller for the themeSettings Page. (UISKinner.fxml)
 */
public class UISkinnerController {

    @FXML
    private HBox settings;


    @FXML
    private JFXColorPicker primaryColour;

    @FXML
    private JFXColorPicker secondaryColour;


    // RGB colour values
    private String baseRGB;
    private String compRGB;
    private int[] lighterRGB;
    private int[] darkerRGB;
    private String lighterRGBString;
    private String darkerRGBString;

    private AnchorPane baseNode;
    private Environment env;
    private ArrayList<String> rules = new ArrayList<String>();
    private String lighterOrDarker;


    /**
     * initialises parameters needed for the gui
     *
     * @param env  Environment
     * @param node Node to style (main pane)
     */
    public void create(Environment env, AnchorPane node) {
        this.env = env;

        env.getThemeHandler().setBaseNode(env.getRootController().paneMain);
        String colour = Color.valueOf(env.getThemeHandler().getPrimaryColour()).toString();
        colour = "#" + colour.substring(2, 8);

        this.primaryColour.setValue(Color.valueOf(colour));
        env.getRootController().setHeader("Theme Settings");

    }


    /**
     * Applies css to the node given in create. Generates colours based on user selected colour.
     */
    @FXML
    void changeColour(ActionEvent event) {
        Color base = primaryColour.getValue();
        this.baseRGB = ColourUtils.toRGBString(base);
        Color comp_colour = ColourUtils.getComplementaryColourString(base);
//        Color comp_colour = secondaryColour.getValue();
        setDarkerRGB(base);
        setLighterRGB(base);
        String complementary_rgb = ColourUtils.toRGBString(comp_colour);
        String styleString = "";
//        String lighterOrDarker;
        double luma = 0.2126 * (base.getRed() * 255) + 0.7152 * (base.getGreen() * 255) + 0.0722 * (base.getBlue() * 255);
        if (luma < 126) {
            lighterOrDarker = floatToRGBString(lighterRGB);
        } else {
            lighterOrDarker = floatToRGBString(darkerRGB);
        }

        String newSecondary = hex2Rgb(secondaryColour.getValue().toString().substring(0, 8));

        if (secondaryColour.getValue().equals(Color.WHITE)) {
            env.getThemeHandler().setTheme(baseRGB, lighterOrDarker);
        } else {
            env.getThemeHandler().setTheme(baseRGB, newSecondary);
        }

        try {
            env.getUserHandler().getCurrentUser().saveProperties();
            if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().getBadge("Creative").currentBadgeType == 0) {
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().unlockBadge("Creative");
            }
        } catch (NullPointerException e ) {
            env.getUserHandler().getCurrentTeacher().saveProperties();
        }


    }


    /**
     * Sets a colour lighter than the user selected colour to be used in the theme
     *
     * @param color Color vaue
     */
    private void setLighterRGB(Color color) {
        float[] hsl = ColourUtils.rgbToHsl(ColourUtils.colorToRgb(color));
        float lightness = hsl[2];
        double lCoefficient;
        if (lightness > 0.8) {
            lCoefficient = 3;
        } else if (lightness < 0.6) {
            lCoefficient = 2;
        } else {
            lCoefficient = 1.5;
        }
        float[] lighterHslArray = new float[3];
        lighterHslArray[0] = hsl[0];
        lighterHslArray[1] = hsl[1];
        if (hsl[2] < 0.05) {
            lighterHslArray[2] = (float) 0.15;
        } else {
            lighterHslArray[2] = (float) (hsl[2] * lCoefficient);
        }

        int[] lighterRGBArray = ColourUtils.hslToRgb(lighterHslArray);

        lighterRGB = lighterRGBArray;
    }

    /**
     * Sets a colour darker than the user selected colour to be used in the theme
     *
     * @param color Color value
     */
    private void setDarkerRGB(Color color) {
        float[] hsl = ColourUtils.rgbToHsl(ColourUtils.colorToRgb(color));
        float lightness = hsl[2];
        double lCoefficient;
        if (lightness > 0.8) {
            lCoefficient = 0.7;
        } else if (lightness > 0.4) {
            lCoefficient = 0.5;
        } else {
            lCoefficient = 0.2;
        }
        float[] darkerHslArray = new float[3];
        darkerHslArray[0] = hsl[0];
        darkerHslArray[1] = hsl[1];
        darkerHslArray[2] = (float) (hsl[2] * lCoefficient);


        int[] darkerRGBArray = ColourUtils.hslToRgb(darkerHslArray);


        darkerRGB = darkerRGBArray;
    }


    /**
     * Converts an array of floats to a formatted string.
     */
    private String floatToRGBString(int[] rgbArray) {
        return (String.format("rgb(%s, %s, %s)", rgbArray[0], rgbArray[1], rgbArray[2]));
    }

    /**
     * Converts a color represented as a hex to an rgb
     *
     * @param colorStr e.g. "#FFFFFF"
     * @return rgb Color
     */
    private static String hex2Rgb(String colorStr) {
        int r = Integer.valueOf(colorStr.substring(2, 4), 16);
        int g = Integer.valueOf(colorStr.substring(4, 6), 16);
        int b = Integer.valueOf(colorStr.substring(6, 8), 16);

        String result = "rgb(" + r + ", " + g + ", " + b + ")";

        return result;
    }

    public void generateSecondaryColour(ActionEvent actionEvent) {
        env.getThemeHandler().setTheme(baseRGB, lighterOrDarker);
        env.getUserHandler().getCurrentUser().saveProperties();
        env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().unlockBadge("Creative");
    }
}
