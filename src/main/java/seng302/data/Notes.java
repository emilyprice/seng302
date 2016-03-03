package seng302.data;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by team5 on 2/03/16.
 *
 * An object which contains data for Midi/Note values.
 *
 */
public class Notes {

    //create an object of Notes
    private static Notes instance = new Notes();

    private HashMap<String,Note> notes;

    private List<String> noteNames = new ArrayList<String>(Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"));

    //make the constructor private so that this class cannot be instantiated
    private Notes(){
        int current_octave = -1;
        notes = new HashMap<String, Note>();
        for (int i =0; i<128; i++){
            Note temp = new Note(i,noteNames.get(i%12).concat(Integer.toString(current_octave)));
            notes.put((noteNames.get(i%12).concat(Integer.toString(current_octave))),(temp));
            notes.put(Integer.toString(i),(temp));
            if((i+1)%12 == 0){
                current_octave +=1;
            }
        }
        for (int i=0; i < noteNames.size(); i++)
        {
            Note temp = new Note(128+i, noteNames.get(i));
            notes.put(noteNames.get(i), temp);
        }
    }


    public static Notes getInstance()
    {
        return instance;
    }

    /**
     * Returns the note name of the note a semitone higher than the input
     * No error handling yet implemented
     * @param initial_note_name note string e.g G#0
     * @return One semitone up from the provided note string.
     */
    public String semitone_up(String initial_note_name)
    {
        if (checkOctaveSpecifier(initial_note_name))
        {
            if (initial_note_name.equals("B")){
                return "C";
            }
            else{
                int init_index = noteNames.indexOf(initial_note_name);
                return noteNames.get(init_index+1);
            }
        }
        else {
            Integer initial_midi = notes.get(initial_note_name).getMidi();
            if (initial_midi < 127) {
                return notes.get(Integer.toString(initial_midi + 1)).getNote();
            } else {

                System.out.println("There is no higher semitone");
                return "N/A";
            }
        }
    }


    /**
     * Returns the note name of the note a semitone lower than the input
     * No error handling yet implemented
     * @param initial_note_name note string e.g G#0
     * @return One semitone down from the provided note string.
     */
    public String semitone_down(String initial_note_name)
    {
        Integer initial_midi = notes.get(initial_note_name).getMidi();
        if(initial_midi > 0)
        {
            System.out.println(notes.get(Integer.toString(initial_midi-1)).getNote());
            return notes.get(Integer.toString(initial_midi-1)).getNote();
        }
        else
        {
            System.out.println("There is no lower semitone");
            return "N/A";
        }
    }

    public String getNote(int midi){
        return notes.get(Integer.toString(midi)).getNote();
    }

    public int getMidi(String note){
        return notes.get(note).getMidi();
    }

    private boolean checkOctaveSpecifier(String note)
    {
        Pattern p = Pattern.compile("[A-G|a-g][#|b]?");
        Matcher m = p.matcher(note);
        boolean b = m.matches();
        return b;
    }



}