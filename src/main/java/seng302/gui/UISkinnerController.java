package seng302.gui;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;
import seng302.Environment;

import java.io.*;
import java.lang.Math;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;


public class UISkinnerController {

    @FXML
    private HBox settings;

    @FXML
    ColorPicker colourPicker;

    @FXML
    void skinGui(ActionEvent event) {
        skinNode();
    }

    // RGB colour values
    private String baseRGB;
    private String compRGB;
    private float[] lighterRGB;
    private float[] darkerRGB;
    private String lighterRGBString;
    private String darkerRGBString;

    private AnchorPane baseNode;
    private Environment env;
    private ArrayList<String> rules = new ArrayList<String>();


    /**
     * initialises parameters needed for the gui
     *
     * @param env  Environment
     * @param node Node to style (main pane)
     */
    public void create(Environment env, AnchorPane node) {
        this.env = env;
        this.baseNode = node;
    }

    /**
     *  Returns a string of formatted rgb values that can be used to format the stylesheet.
     * @param color Color to convert
     * @return Returns a string of formatted rgb values that can be used to format the stylesheet.
     */
    private String toRGBString(Color color) {
        int red = (int) (color.getRed()*255);
        int blue = (int) (color.getBlue()*255);
        int green = (int) (color.getGreen()*255);
        return (String.format("rgb(%s, %s, %s)", red, green, blue));
    }

    /**
     * Gets the complementary colour of the given colour
     * @param color Color value
     * @return Complementary Colour
     */
    private Color getComplementaryColourString(Color color) {
        Color comp_color;
        int red = (int) (color.getRed()*255);
        int blue = (int) (color.getBlue()*255);
        int green = (int) (color.getGreen()*255);
        double newRed = Math.sqrt(Math.pow(255, 2)-Math.pow(red, 2)) / 255;
        double newBlue = Math.sqrt(Math.pow(255, 2)-Math.pow(blue, 2)) / 255;
        double newGreen = Math.sqrt(Math.pow(255, 2)-Math.pow(green, 2)) / 255;
        comp_color = new Color(newRed, newBlue, newGreen, 1);
        return comp_color;
    }


    /**
     * Applies css to the node given in create. Generates colours based on user selected colour.
     */
    private void skinNode() {
        Color base = colourPicker.getValue();
        String baseRgb = toRGBString(base);
        Color comp_colour = getComplementaryColourString(base);
        setDarkerRGB(base);
        setLighterRGB(base);
        String complementary_rgb = toRGBString(comp_colour);
        String styleString = "";
        String lighterOrDarker;
        double luma = 0.2126 * (base.getRed()*255) + 0.7152 * (base.getGreen()*255) + 0.0722 * (base.getBlue()*255);
        if (luma < 126) {
            lighterOrDarker = floatToRGBString(lighterRGB);
        }else {
            lighterOrDarker = floatToRGBString(darkerRGB);
        }

        generateStyleSheet(baseRgb, lighterOrDarker);

        baseNode.getStylesheets().clear();
        String filePath = "userstyle.css";
        File f = new File(filePath);
        baseNode.getStylesheets().clear();
        baseNode.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
    }

    /**
     * Creates the stylesheet for the application to use.
     * @param baseRGB color the user selected
     * @param ldRGB lighter or darker color generated by the theme
     */
    private void generateStyleSheet(String baseRGB, String ldRGB) {
        ArrayList<String> templateCSS = new ArrayList<String>();

        String line = null;

        try {
            FileReader fileReader =
                    new FileReader(getClass().getResource("/css/templatecss.txt").getFile());

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("{0}{1}")) {
                    templateCSS.add(MessageFormat.format(line, "", ldRGB));
                } else if(line.contains("{0}")) {
                    templateCSS.add(MessageFormat.format(line, baseRGB));
                }else {
                    templateCSS.add(line);
                }
            }

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String fileName = "userstyle.css";

        try {
            FileWriter fileWriter =
                    new FileWriter(fileName);

            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            for (String rule : templateCSS) {
                bufferedWriter.write(rule);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets a colour lighter than the user selected colour to be used in the theme
     * @param color Color vaue
     */
    private void setLighterRGB(Color color) {
        float[] hsl = rgbToHsl(colorToRgb(color));
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

        float[] lighterRGBArray = hslToRgb(lighterHslArray);

        lighterRGB = lighterRGBArray;
    }

    /**
     * Sets a colour darker than the user selected colour to be used in the theme
     * @param color Color value
     */
    private void setDarkerRGB(Color color) {
        float[] hsl = rgbToHsl(colorToRgb(color));
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


        float[] darkerRGBArray = hslToRgb(darkerHslArray);


        darkerRGB = darkerRGBArray;
    }

    /**
     * converts a color to rgb values
     * @param color Color value
     * @return Float array of rgb values
     */
    private float[] colorToRgb(Color color) {
        float[] rgbVals = new float[3];
        rgbVals[0] = (float) (color.getRed());
        rgbVals[1] = (float) (color.getGreen());
        rgbVals[2] = (float) (color.getBlue());

        return rgbVals;
    }

    /**
     * converts rgb values to hsl values
     * @param rgb Float array of rgb values
     * @return Returns a float array of hsl values
     */
    private float[] rgbToHsl(float[] rgb) {
        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float h, s, l;
        h = s = l = ((max+min)/(float)2);

        if(max == min){
            h = s = 0; // achromatic
        }else{

            float d = max - min;

            if(l > (float)0.5) {s = d / (2 - max - min);}
            else{s = d / (max + min);}

            if (r == max) {
                float tempVar = 0;
                if (g < b) {tempVar = (float) 6;}
                else {tempVar = (float) 0;}
                h = (g - b) / d + tempVar;
            }
            else if (r == max) {
                h = (b - r) / d + (float) 2;
            }
            else if (r == max) {
                h = (r - g) / d + (float) 4;
            }
            h = h / (float) 6;
        }

        float[] hslVals = new float[3];
        hslVals[0] = h;
        hslVals[1] = s;
        hslVals[2] = l;

        return hslVals;
    }

    /**
     * converts hsl colours to rgb colours
     * @param hsl Float array of hsl values
     * @return Float array of rgb values
     */
    private float[] hslToRgb(float[] hsl) {
        float h = hsl[0];
        float s = hsl[1];
        float l = hsl[2];


        float r, g, b;

        //Implements formula
        if(s == (float) 0){
            r = g = b = l; // achromatic
        }else{
            float temp1 = 0;
            if(l < (float) 0.5) {
                temp1 = l * (1 + s);
            } else {
                temp1 = (l + s) - (l * s);
            }
            float temp2 = (float) 2 * l - temp1;
            r = hueToRGB(temp2, temp1, h + (float) 1 / 3);
            g = hueToRGB(temp2, temp1, h);
            b = hueToRGB(temp2, temp1, h - (float) 1 / 3);
        }

        float[] rgbVals = new float[3];
        rgbVals[0] = Math.round(r * 255);
        rgbVals[1] = Math.round(g * 255);
        rgbVals[2] = Math.round(b * 255);

        return rgbVals;
    }

    /**
     * helper for the hsl to rgb values. Implements part of algorithm to convert hsl value to rgb
     * @param p temporary variable used to store part of the formula
     * @param q temporary variable used to store part of the formula
     * @param t temporary variable used to determine part of the formula, red, green, or blue - outcome.
     * @return Float representation of specific rgb value
     */
    private float hueToRGB(float p, float q, float t) {
        if(t < (float) 0) {
            t += 1;
        }
        if(t > (float) 1) {
            t -= 1;
        }
        if(t < (float) 1/6) {
            return p + ((q - p) * (float) 6 * t);
        }
        if(t < (float) 1/2) {
            return q;
        }
        if(t < (float) 2/3) {
            return p + ((q - p) * ((float) 2/3 - t) * (float) 6);
        }
        return p;
    }

    /**
     * Converts an array of floats to a formatted string.
     * @param rgbArray
     * @return
     */
    private String floatToRGBString(float[] rgbArray) {
        return (String.format("rgb(%s, %s, %s)", rgbArray[0], rgbArray[1], rgbArray[2]));
    }

}
