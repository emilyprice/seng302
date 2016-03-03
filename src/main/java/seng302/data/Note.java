package seng302.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jat157 on 2/03/16.
 */
public class Note {
    int midi;
    String note;

    public static HashMap<String, Note> notes;
    private static List<String> noteNames = new ArrayList<String>(Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"));

    static {
        int current_octave = -1;
        notes = new HashMap<String, Note>();
        for (int i =0; i<128; i++){
            Note temp = new Note(i,noteNames.get(i%12).concat(Integer.toString(current_octave)));
            notes.put((noteNames.get(i%12).concat(Integer.toString(current_octave))),temp);
            notes.put(Integer.toString(i),temp);
            if((i+1)%12 == 0){
                current_octave +=1;
            }
        }
        // What is this doing?
//        for (int i=0; i < noteNames.size(); i++)
//        {
//            Note temp = new Note(128+i, noteNames.get(i));
//            notes.put(noteNames.get(i), temp);
//        }
    }

    static public Note lookup(String s){
        return notes.get(s);
    }
    static public Note lookup(int i){
        return notes.get(i);
    }

    /**
     * Returns the note name of the note a semitone higher than the input
     * No error handling yet implemented
     */
    public Note semitone_up()
    {

        // Let's maybe reimplement this using the pattern note_object.semitone_up()
        // To get the note_object we can use Note.lookup(note_name) as above.
        // This is the basic idea:
        return Note.lookup(this.getMidi()+1);

//        if (checkOctaveSpecifier(initial_note_name))
//        {
//            if (initial_note_name.equals("B")){
//                return "C";
//            }
//            else{
//                int init_index = noteNames.indexOf(initial_note_name);
//                return noteNames.get(init_index+1);
//            }
//        }
//        else {
//            Integer initial_midi = notes.get(initial_note_name).getMidi();
//            if (initial_midi < 127) {
//                return notes.get(Integer.toString(initial_midi + 1)).getNote();
//            } else {
//
//                System.out.println("There is no higher semitone");
//                return "N/A";
//            }
//        }
}
//    /**
//     * Returns the note name of the note a semitone lower than the input
//     * No error handling yet implemented
//     */
    public Note semitone_down()
    {
        return Note.lookup(this.getMidi()-1);
//        Integer initial_midi = notes.get(initial_note_name).getMidi();
//        if(initial_midi > 0)
//        {
//            System.out.println(notes.get(Integer.toString(initial_midi-1)).getNote());
//            return notes.get(Integer.toString(initial_midi-1)).getNote();
//        }
//        else
//        {
//            System.out.println("There is no lower semitone");
//            return "N/A";
//        }
    }

    private boolean checkOctaveSpecifier(String note)
    {
        Pattern p = Pattern.compile("[A-G|a-g][#|b]?");
        Matcher m = p.matcher(note);
        boolean b = m.matches();
        return b;
    }



    private Note(int midi, String note){
        this.midi = midi;
        this.note = note;

    }

    public String getNote()
    {
        return this.note;
    }

    public Integer getMidi()
    {
        return this.midi;
    }


}
