package seng302.managers;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.layout.AnchorPane;

/**
 * This class is responsible for setting the GUI to display in a user's theme colours.
 */
public class ThemeHandler {

    AnchorPane baseNode;

    String primaryColour, secondaryColour;

    /**
     * Sets window to which attach the developed css.
     *
     * @param node The Node to which apply the CSS class.
     */
    public void setBaseNode(AnchorPane node) {
        this.baseNode = node;

    }

    /**
     * Creates a theme CSS file given a primary and secondary colours, then links the created CSS to
     * a Node (this.baseNode)
     *
     * @param primary   primary theme colour
     * @param secondary secondary theme colour
     */
    public void setTheme(String primary, String secondary) {

        this.primaryColour = primary;
        this.secondaryColour = secondary;
        generateStyleSheet(primary, secondary);
        setNodeCss(this.baseNode);

    }

    /**
     * Links the created css to a specified node.
     */
    private void setNodeCss(AnchorPane node) {
        if (node != null) {
            node.getStylesheets().clear();
            String filePath = "userstyle.css";
            File f = new File(filePath);
            node.getStylesheets().clear();
            node.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

        }

    }


    /**
     * Creates the stylesheet for the application to use.
     *
     * @param baseRGB color the user selected
     * @param ldRGB   lighter or darker color generated by the theme
     */
    public void generateStyleSheet(String baseRGB, String ldRGB) {
        this.primaryColour = baseRGB;
        this.secondaryColour = ldRGB;


        String primaryFont;
        String secondaryFont;

        Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
        Matcher m = c.matcher(baseRGB);

        java.awt.Color primary;
        if (m.matches()) {
            primary = new Color(Integer.valueOf(m.group(1)),  // r
                    Integer.valueOf(m.group(2)),  // g
                    Integer.valueOf(m.group(3))); // b
        } else {
            try {
                primary = java.awt.Color.decode(baseRGB);
            } catch (Exception e) {
                primary = Color.decode("#1E88E5"); //Default blue
            }
        }


        Matcher m2 = c.matcher(ldRGB);

        java.awt.Color secondary;
        if (m2.matches()) {
            secondary = new Color(Integer.valueOf(m.group(1)),  // r
                    Integer.valueOf(m.group(2)),  // g
                    Integer.valueOf(m.group(3))); // b
        } else {
            try {
                secondary = java.awt.Color.decode(ldRGB);
            } catch (Exception e) {
                secondary = Color.white;
            }
        }

        if ((float) ((float) ((float) secondary.getRed() * 0.299f) + (float) (secondary.getGreen() * 0.587f) + (float) (secondary.getBlue() * 0.144f)) > 186) {

            secondaryFont = "black";

        } else secondaryFont = "white";


        if ((float) ((float) ((float) primary.getRed() * 0.299f) + (float) (primary.getGreen() * 0.587f) + (float) (primary.getBlue() * 0.144f)) > 186) {

            primaryFont = "black";

        } else primaryFont = "white";


        ArrayList<String> templateCSS = new ArrayList<String>();

        String line = null;

        try {
            FileReader fileReader =
                    new FileReader(getClass().getResource("/css/templatecss.txt").getFile());

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("{0}{1}{2}{3}")) { //Primary, Secondary, Primary font colour, secondary font colour.

                    templateCSS.add(MessageFormat.format(line, "", "", "", secondaryFont));
                } else if (line.contains("{0}{1}{2}")) {

                    templateCSS.add(MessageFormat.format(line, "", "", primaryFont));
                } else if (line.contains("{0}{1}")) {
                    templateCSS.add(MessageFormat.format(line, "", ldRGB));
                } else if (line.contains("{0}")) {
                    templateCSS.add(MessageFormat.format(line, baseRGB));

                } else {
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

    public String getPrimaryColour() {
        return primaryColour;
    }

    public String getSecondaryColour() {
        return secondaryColour;
    }

    /**
     * Updates the theme handler to use the default colours of orange and white.
     */
    public void setDefaultTheme() {
        setTheme("#1E88E5", "white");
    }


}
