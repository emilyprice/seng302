package seng302;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.Label;
import seng302.data.Note;
import seng302.gui.MicrophoneInputPopoverController;

import javax.sound.sampled.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Vector;

/**
 * Created by djj36 on 23/09/16.
 */
public class MicrophoneInput implements PitchDetectionHandler {

    private PitchProcessor.PitchEstimationAlgorithm algo = PitchProcessor.PitchEstimationAlgorithm.YIN;
    private Mixer mixer;
    private AudioDispatcher dispatcher;
    private TargetDataLine line;
    private ArrayList<Double> midiFrequencies = new ArrayList<>();
    private ArrayList<String> detectedFrequencies = new ArrayList<>();
    private SilenceDetector silenceDetector;
    private double threshold = -65.0;

    private Integer noteCount;
    private Integer outlierCount;
    private String latestNote;
    private Boolean noteFound;
    private Boolean singleNote;


    private Thread recordingThread = null;

    private MicrophoneInputPopoverController microphoneInputPopoverController = null;

    private ArrayList<String> lastRecorded = new ArrayList<>();

    private Label tutorAnswer = null;

    /**
     * Constructor. Sets the default mixer (input device), and creates the list of midi frequencies
     * for comparison.
     */
    public MicrophoneInput() {
        try {
            this.mixer = AudioSystem.getMixer(getMixerInfo(false, true).get(0));
            resetNoteFrequencies();

        } catch (ArrayIndexOutOfBoundsException a) {
            // For GitLab testings inability to use a microphone
        }
    }


    /**
     * Resets the list of midi frequencies - used for determining the closest note in the find note
     * function.
     */
    private void resetNoteFrequencies() {
        midiFrequencies.clear();
        int a = 440; // a is 440 hz...
        for (int x = 0; x < 127; ++x) {
            midiFrequencies.add(x, (a / (double) 32) * (Math.pow((double) 2, ((x - (double) 9) / (double) 12))));
        }
    }

    /**
     * Given a frequency, finds the musical note that fits closest to the frequency.
     *
     * @param freq Detected frequency
     * @return Musical note closest to the detected frequency.
     */
    private String findNote(Double freq) {
        String note;
        midiFrequencies.add(freq);
        Collections.sort(midiFrequencies);
        int foundIndex = midiFrequencies.indexOf(freq);
        if (foundIndex <= midiFrequencies.size() - 2) {
            int closestIndex;
            Double higher = Math.abs(midiFrequencies.get(foundIndex + 1) - freq);
            Double lower = Math.abs(midiFrequencies.get(foundIndex - 1) - freq);
            if (higher < lower) {
                closestIndex = foundIndex;
            } else {
                closestIndex = foundIndex - 1;
            }
            note = Note.lookup(String.valueOf(closestIndex)).getNote();
        } else {
            note = Note.lookup(String.valueOf(126)).getNote();
        }

        resetNoteFrequencies();

        return note;
    }

    /**
     * Starts the recoding thread with proconfigured options.
     * Uses the currently selected input device and sound threshold level.
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     */
    public void startRecording(Boolean singleNote) throws LineUnavailableException, UnsupportedAudioFileException {
        lastRecorded.clear();
        float sampleRate = 44100;
        int bufferSize = 1024;
        int overlap = 0;

        this.singleNote = singleNote;

        noteCount = 0;
        outlierCount = 0;
        latestNote = null;
        noteFound = false;
        detectedFrequencies.clear();

        final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
                true);
        final DataLine.Info dataLineInfo = new DataLine.Info(
                TargetDataLine.class, format);
        line = (TargetDataLine) mixer.getLine(dataLineInfo);
        final int numberOfSamples = bufferSize;
        line.open(format, numberOfSamples);
        line.start();
        final AudioInputStream stream = new AudioInputStream(line);


        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
        // create a new dispatcher
        dispatcher = new AudioDispatcher(audioStream, bufferSize,
                overlap);

        // add a processor
        dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));
        // add a silence detector, to detect the sound level (dB).
        silenceDetector = new SilenceDetector(threshold, false);
        dispatcher.addAudioProcessor(silenceDetector);

        recordingThread = new Thread(dispatcher, "Audio dispatching");
        recordingThread.start();
    }

    /**
     * Stops the recording thread. Returns the list of most recently recorded notes.
     * @return List of recorded notes.
     */
    public ArrayList<String> stopRecording() {
        dispatcher.stop();
        return lastRecorded;
    }

    /**
     * Algorithm to determine whether a note has been detected.
     * If a note has been detected, the note is added to the list of detected notes.
     * Has an allowance for outliers.
     */
    private void hasNoteBeenFound() {
        int arraySize = detectedFrequencies.size();
        switch (arraySize) {
            case 1:
                latestNote = detectedFrequencies.get(0);
                noteCount += 1;
                break;
            case 2:
                if (detectedFrequencies.get(0).equals(detectedFrequencies.get(1))) {
                    noteCount += 1;
                    latestNote = detectedFrequencies.get(0);
                } else {
                    noteCount = 0;
                    detectedFrequencies.remove(0);
                    latestNote = detectedFrequencies.get(0);
                }
                break;
            default:
                noteCount = 0;
                for (int x = 0; x < detectedFrequencies.size(); x++) {
                    if (detectedFrequencies.get(x).equals(latestNote)) {
                        noteCount += 1;
                        outlierCount = 0;
                        if (noteCount >= 5 && !noteFound) {
                            noteFound = true;
                            lastRecorded.add(latestNote);
                            if (singleNote) {
                                stopRecording();
                            }
                            microphoneInputPopoverController.update(lastRecorded, latestNote);
                        }
                    } else {
                        outlierCount += 1;
                        if (outlierCount > 1) {
                            if (noteFound) {
                                noteFound = false;
                                microphoneInputPopoverController.update(lastRecorded, null);
                            }
                            for (int i = 0; i < x; i++) {
                                detectedFrequencies.remove(0);
                            }
                            noteCount = 0;
                            break;
                        }
                    }
                }
        }
    }

    /**
     * Returns the list of the most recently recorded notes.
     * @return Most recently recorded notes.
     */
    public ArrayList<String> getLastRecorded() {
        return lastRecorded;
    }

    /**
     * Called when a audio event has been detected. Used to handle this audio event.
     * Calls the hasNoteBeenFound function. Sleeps the detection thread until the
     * note has been verified either way.
     * @param pitchDetectionResult Result of the pitch analysis of the current audio event.
     * @param audioEvent Audio event
     */
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if (pitchDetectionResult.getPitch() != -1) {
            float pitch = pitchDetectionResult.getPitch();
            final float probability = pitchDetectionResult.getProbability();
            Thread t = new Thread() {
                @Override
                public void run() {

                    if (silenceDetector.currentSPL() > threshold)

                    {
                        if (probability > 0.8) {
                            detectedFrequencies.add(findNote((double) pitch));
                            try {
                                hasNoteBeenFound();
                            } catch (Exception e) {
//                                System.err.println("Notes played too quickly, can't keep up..");
                            }
                        }
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (singleNote && noteFound) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Gets the information of all currently available audio devices.
     * @param supportsPlayback - Whether or not the device supports playback (output).
     * @param supportsRecording - Whether or not the device supports recording (input).
     * @return A vector containing a list of the information for mixers.
     */
    public static Vector<Mixer.Info> getMixerInfo(
            final boolean supportsPlayback, final boolean supportsRecording) {
        final Vector<Mixer.Info> infos = new Vector<Mixer.Info>();
        final Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (final Mixer.Info mixerinfo : mixers) {
            if (supportsRecording
                    && AudioSystem.getMixer(mixerinfo).getTargetLineInfo().length != 0) {
                // Mixer capable of recording audio if target LineWavelet length != 0
                infos.add(mixerinfo);
            } else if (supportsPlayback
                    && AudioSystem.getMixer(mixerinfo).getSourceLineInfo().length != 0) {
                // Mixer capable of audio play back if source LineWavelet length != 0
                infos.add(mixerinfo);
            }
        }
        return infos;
    }

    /**
     * Finds the locally recognised names of a specified (Mixer)
     * @param info Mixer's information
     * @return Returns the name of the mixer formatted according to operating system.
     */
    public static String toLocalString(Mixer.Info info) {
        if (!System.getProperty("os.name").startsWith("Windows")) {
            return info.getDescription();
        }
        String defaultEncoding = Charset.defaultCharset().toString();
        try {
            return new String(info.getDescription().getBytes("windows-1252"), defaultEncoding);
        } catch (UnsupportedEncodingException e) {
            return info.getDescription();
        }
    }

    /**
     * Returns the current input device
     * @return Mixer - currently selected input device.
     */
    public Mixer getMixer() {
        return mixer;
    }

    /**
     * Sets the current mixer.
     * @param mixer Mixer - Audio device.
     */
    public void setMixer(Mixer mixer) {
        this.mixer = mixer;
    }

    /**
     * Sets the minimum sound threshold that an audio even must reach to be used.
     * @param threshold
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Adds the microphone input popover - used for notifying and updating the popover.
     * @param m MicrophoneInputPopoverController
     */
    public void addPopover(MicrophoneInputPopoverController m) {
        this.microphoneInputPopoverController = m;
    }

    public void recordSingleNoteTutorInput() {
        try {
            startRecording(true);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }


}