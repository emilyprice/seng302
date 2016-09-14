package seng302.utility;

import javafx.scene.paint.Color;

/**
 * Contains a collection of functions used for calculating and converting colours.
 */
public class ColourUtils {
    /**
     * Returns a string of formatted rgb values that can be used to format the stylesheet.
     *
     * @param color Color to convert
     * @return Returns a string of formatted rgb values that can be used to format the stylesheet.
     */
    public static String toRGBString(Color color) {
        int red = (int) (color.getRed() * 255);
        int blue = (int) (color.getBlue() * 255);
        int green = (int) (color.getGreen() * 255);
        return (String.format("rgb(%s, %s, %s)", red, green, blue));
    }

    /**
     * Gets the complementary colour of the given colour
     *
     * @param color Color value
     * @return Complementary Colour
     */
    public static Color getComplementaryColourString(Color color) {
        Color comp_color;
        int red = (int) (color.getRed() * 255);
        int blue = (int) (color.getBlue() * 255);
        int green = (int) (color.getGreen() * 255);
        double newRed = Math.sqrt(Math.pow(255, 2) - Math.pow(red, 2)) / 255;
        double newBlue = Math.sqrt(Math.pow(255, 2) - Math.pow(blue, 2)) / 255;
        double newGreen = Math.sqrt(Math.pow(255, 2) - Math.pow(green, 2)) / 255;
        comp_color = new Color(newRed, newBlue, newGreen, 1);
        return comp_color;
    }


    /**
     * converts a color to rgb values
     *
     * @param color Color value
     * @return Float array of rgb values
     */
    public static float[] colorToRgb(Color color) {
        float[] rgbVals = new float[3];
        rgbVals[0] = (float) (color.getRed());
        rgbVals[1] = (float) (color.getGreen());
        rgbVals[2] = (float) (color.getBlue());

        return rgbVals;
    }

    /**
     * converts rgb values to hsl values
     *
     * @param rgb Float array of rgb values
     * @return Returns a float array of hsl values
     */
    public static float[] rgbToHsl(float[] rgb) {
        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float h, s, l;
        h = s = l = ((max + min) / (float) 2);

        if (max == min) {
            h = s = 0; // achromatic
        } else {

            float d = max - min;

            if (l > (float) 0.5) {
                s = d / (2 - max - min);
            } else {
                s = d / (max + min);
            }

            if (r == max) {
                float tempVar = 0;
                if (g < b) {
                    tempVar = (float) 6;
                } else {
                    tempVar = (float) 0;
                }
                h = (g - b) / d + tempVar;
            } else if (r == max) {
                h = (b - r) / d + (float) 2;
            } else if (r == max) {
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
     *
     * @param hsl Float array of hsl values
     * @return Float array of rgb values
     */
    public static int[] hslToRgb(float[] hsl) {
        float h = hsl[0];
        float s = hsl[1];
        float l = hsl[2];

        float r, g, b;

        //Implements formula
        if (s == (float) 0) {
            r = g = b = l; // achromatic
        } else {
            float temp1 = 0;
            if (l < (float) 0.5) {
                temp1 = l * (1 + s);
            } else {
                temp1 = (l + s) - (l * s);
            }
            float temp2 = (float) 2 * l - temp1;
            r = hueToRGB(temp2, temp1, h + (float) 1 / 3);
            g = hueToRGB(temp2, temp1, h);
            b = hueToRGB(temp2, temp1, h - (float) 1 / 3);
        }

        int[] rgbVals = new int[3];
        rgbVals[0] = Math.round(r * 255);
        rgbVals[1] = Math.round(g * 255);
        rgbVals[2] = Math.round(b * 255);

        return rgbVals;
    }

    /**
     * helper for the hsl to rgb values. Implements part of algorithm to convert hsl value to rgb
     *
     * @param p temporary variable used to store part of the formula
     * @param q temporary variable used to store part of the formula
     * @param t temporary variable used to determine part of the formula, red, green, or blue - outcome.
     * @return Float representation of specific rgb value
     */
    public static float hueToRGB(float p, float q, float t) {
        if (t < (float) 0) {
            t += 1;
        }
        if (t > (float) 1) {
            t -= 1;
        }
        if (t < (float) 1 / 6) {
            return p + ((q - p) * (float) 6 * t);
        }
        if (t < (float) 1 / 2) {
            return q;
        }
        if (t < (float) 2 / 3) {
            return p + ((q - p) * ((float) 2 / 3 - t) * (float) 6);
        }
        return p;
    }


}
