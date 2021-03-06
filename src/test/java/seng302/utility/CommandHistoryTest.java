package seng302.utility;

import org.junit.Before;
import org.junit.Test;

import seng302.managers.TranscriptManager;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jonty on 2/03/16.
 */
public class CommandHistoryTest {
    TranscriptManager tm;
    @Before
    public void setUp() throws Exception {



        tm = new TranscriptManager();

        tm.setCommand("note 60");
        tm.setResult("C4");
        tm.setCommand("midi G#0");
        tm.setResult("0");







    }

    @Test
     public void testScrollingUp(){

        String textInput = "comma"; //Emulated beginning to write a command.


        /**
         * note 60
         * midi G#0
         * input: comma
         */

        assertEquals("midi G#0", tm.getCommandHistory().handleScrollUp(textInput) ); //Pressing up before any history is added


        assertEquals("note 60", tm.getCommandHistory().handleScrollUp(textInput) );

        //assertEquals("note 60", tm.inputHistory.handleScrollUp("midi G#0"));


        assertEquals("note 60", tm.getCommandHistory().handleScrollUp("note 60"));








    }

    @Test
    public void testUpAndDown(){
        tm = new TranscriptManager();
        String textInput = "test"; //Emulated beginning to write a command.



        tm.setCommand("note 60");
        tm.setResult("C4");

        assertEquals("note 60", tm.getCommandHistory().handleScrollUp(textInput) ); //up to history command.
        //assertEquals(textInput, tm.historyController.handleScrollDown("note 60"));

        assertEquals(textInput, tm.getCommandHistory().handleScrollDown(textInput) ); //Pressing down should go back to initial input
        assertEquals(textInput, tm.getCommandHistory().handleScrollDown(textInput) ); //Pressing down again should (at user input)
        assertEquals("note 60", tm.getCommandHistory().handleScrollUp(textInput) ); //Should go up 1 in history (note 60)


    }





}