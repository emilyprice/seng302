package seng302;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import seng302.data.Note;
import seng302.utility.musicNotation.RhythmHandler;

/**
 * The Music Player class handles all sound that is produced by the program.
 */
public class MusicPlayer {
    Sequencer seq;
    RhythmHandler rh;
    Instrument instrument = null;
    Patch instrumentPatch;
    int instrumentInt;

    /**
     * Default tempo is 120 BPM.
     */
    private int tempo = 120;
    Synthesizer synthesizer;
    Instrument[] availableInstruments;

    /**
     * Music Player constructor opens the sequencers and synthesizer. It also sets the receiver.
     */
    public MusicPlayer() {
        rh = new RhythmHandler();

        try {
            this.seq = MidiSystem.getSequencer();
            seq.open();
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            seq.getTransmitter().setReceiver(synthesizer.getReceiver());
            availableInstruments = synthesizer.getAvailableInstruments();
            setStarterInstrument();

        } catch (MidiUnavailableException e) {
            System.err.println("Can't play Midi sound at the moment.");
        }
    }

    /**
     * When the music player is initialised, this function sets the default instrument. The default
     * instrument is the first available instrument on the synthesizer - usually a piano.
     */
    private void setStarterInstrument() {
        instrument = availableInstruments[0];
        instrumentInt = 0;
        instrumentPatch = instrument.getPatch();
    }

    public void setSeq(Sequencer seq) {
        this.seq = seq;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public Instrument[] getAvailableInstruments() {
        return availableInstruments;
    }


    /**
     * Sets the synthesizer to synthesize playback with a given instrument.
     *
     * @param instrument The instrument which will be used for playback
     */
    public void setInstrument(Instrument instrument) {
        instrumentInt = Arrays.asList(availableInstruments).indexOf(instrument);
        this.instrument = instrument;
        this.instrumentPatch = this.instrument.getPatch();
        synthesizer.getChannels()[0].programChange(
                instrumentPatch.getBank(), instrumentPatch.getProgram());
    }

    /**
     * Plays an array of notes directly after each other.
     *
     * @param notes The notes in the order to be played.
     * @param pause The number of ticks to pause between notes. 16 ticks = 1 crotchet beat.
     */
    public void playNotes(Collection<Note> notes, int pause) {
        //int ticks = 16; //16 (1 crotchet beat)
        int ticks = rh.getBeatResolution();
        try {
            // 16 ticks per crotchet note.
            Sequence sequence = new Sequence(Sequence.PPQ, ticks);
            Track track = sequence.createTrack();

            ShortMessage sm = new ShortMessage();
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrumentInt, 0);
            track.add(new MidiEvent(sm, 0));

            int currenttick = 0;
            rh.resetIndex(); //Reset rhythm to first crotchet.
            for (Note note : notes) {
                int timing = rh.getNextTickTiming();

                addNote(track, currenttick, timing, note.getMidi(), 64); //velocity 64
                currenttick += (timing + pause);
            }
            playSequence(sequence);

        } catch (InvalidMidiDataException e) {
            System.err.println("The notes you are trying to play were invalid");
        }
    }

    /**
     * Plays an array of notes as a blues scale directly after each other.
     *
     * @param notes The notes in the order to be played.
     * @param pause The number of ticks to pause between notes. 16 ticks = 1 crotchet beat.
     */

    public void playBluesNotes(ArrayList<Note> notes, int pause) {
        int ticks = rh.getBeatResolution();
        try {
            int instrument = 1;
            // 16 ticks per crotchet note.
            Sequence sequence = new Sequence(Sequence.PPQ, ticks);
            Track track = sequence.createTrack();

            // Set the instrument on channel 0
            ShortMessage sm = new ShortMessage();
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);
            track.add(new MidiEvent(sm, 0));

            int currenttick = 0;
            int noteNum = 2;
            rh.resetIndex(); //Reset rhythm to first crotchet.
            for (Note note : notes) {
                noteNum++;
                int timing = rh.getNextTickTiming();
                if (noteNum == 3) {
                    noteNum = 0;
                    addNote(track, currenttick, timing, note.getMidi(), 120);
                } else {
                    addNote(track, currenttick, timing, note.getMidi(), 64); //velocity 64
                }
                currenttick += (timing + pause);
            }
            playSequence(sequence);

        } catch (InvalidMidiDataException e) {
            System.err.println("The notes you are trying to play were invalid");
        }
    }


    /**
     * Convenience method to play notes with no pause.
     *
     * @param notes The notes to play.
     */
    public void playNotes(Collection<Note> notes) {
        playNotes(notes, 0);
    }

    /**
     * Method to play blues notes with no pause.
     *
     * @param notes The notes to play.
     */
    public void playBluesNotes(ArrayList<Note> notes) {
        playBluesNotes(notes, 0);
    }


    /**
     * The action touch event for when a key is pressed and a note is "turned on"
     *
     * @param note the note to be played
     */
    public void noteOn(Note note) {
        MidiChannel channel = synthesizer.getChannels()[0];
        channel.noteOn(note.getMidi(), 64);
    }

    /**
     * The action touch event for when a key is pressed and a note is "turned off"
     *
     * @param note the note to be discontinued playing
     */
    public void noteOff(Note note) {
        MidiChannel channel = synthesizer.getChannels()[0];
        channel.noteOff(note.getMidi(), 64);
    }

    /**
     * Plays a collection of notes at the same time.
     *
     * @param notes The notes to be played simultaneously - eg a chord.
     */
    public void playSimultaneousNotes(Collection<Note> notes) {
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, 16);
            Track track = sequence.createTrack();

            ShortMessage sm = new ShortMessage();
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrumentInt, 0);
            track.add(new MidiEvent(sm, 0));

            // Add all notes to the start of the sequence
            for (Note note : notes) {
                addNote(track, 0, 16, note.getMidi(), 64);
            }
            playSequence(sequence);

        } catch (InvalidMidiDataException e) {
            System.err.println("The notes you are trying to play were invalid");
        }

    }

    /**
     * Convenience method to convert a single note into an array list so the method playNotes() can
     * be used.
     *
     * @param note The note to be played.
     */
    public void playNote(Note note) {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(note);
        playNotes(notes);
    }

    /**
     * Temporaily sets the tempo to meet the required duration
     *
     * @param note     The note to play
     * @param duration The duration in milliseconds to play the note for.
     */
    public void playNote(Note note, int duration) {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(note);
        int oldTempo = getTempo();
        setTempo(1000 / duration * 60);
        playNotes(notes);
        setTempo(oldTempo);

    }


    /**
     * A convenience method to add a note to the track on channel 0
     *
     * @param track      The track to add the note to.
     * @param startTick  Tick number to begin the note on.
     * @param tickLength How many ticks to play the note for.
     * @param key        The midi value of the note.
     * @param velocity   The volume the note will be played at.
     */
    private void addNote(Track track, int startTick,
                         int tickLength, int key, int velocity)
            throws InvalidMidiDataException {
        ShortMessage on = new ShortMessage();
        on.setMessage(ShortMessage.NOTE_ON, 0, key, velocity);
        ShortMessage off = new ShortMessage();
        off.setMessage(ShortMessage.NOTE_OFF, 0, key, velocity);
        track.add(new MidiEvent(on, startTick));
        track.add(new MidiEvent(off, startTick + tickLength));
    }

    /**
     * Sets the sequence to be played and the tempo. Then the sequencer starts playing.
     *
     * @param sequence The sequence containing the track to be played.
     */
    private void playSequence(Sequence sequence) {
        try {
            seq.setSequence(sequence);
            seq.setTempoInBPM(tempo);
            seq.start();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            System.err.println("Can't play Midi sound at the moment.");
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.err.println("Can't play Midi sound at the moment.");
        }

    }

    /**
     * Returns the current tempo.
     *
     * @return current tempo.
     */
    public int getTempo() {
        return tempo;
    }

    /**
     * Sets the tempo to the given int.
     *
     * @param tempo The tempo to be changed to.
     */
    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public void stop() {

        seq.stop();
    }

    public RhythmHandler getRhythmHandler() {
        return rh;
    }

}

