package seng302;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import seng302.data.Note;
import seng302.gui.MicrophoneInputPopoverController;

import javax.sound.sampled.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
    private int currentIndex = 0;

    private MicrophoneInputPopoverController microphoneInputPopoverController = null;


    private ArrayList<String> lastRecorded = new ArrayList<>();

    public MicrophoneInput() {
        this.mixer = AudioSystem.getMixer(getMixerInfo(false, true).get(0));
        resetNoteFrequencies();
    }


    private void resetNoteFrequencies() {
        midiFrequencies.clear();
        int a = 440; // a is 440 hz...
        for (int x = 0; x < 127; ++x) {
            midiFrequencies.add(x, (a / (double) 32) * (Math.pow((double) 2, ((x - (double) 9) / (double) 12))));
        }
    }

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

    public void startRecording() throws LineUnavailableException, UnsupportedAudioFileException {
        lastRecorded.clear();
        float sampleRate = 44100;
        int bufferSize = 1024;
        int overlap = 0;

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

        new Thread(dispatcher, "Audio dispatching").start();
    }

    public ArrayList<String> stopRecording() {
        dispatcher.stop();
        return lastRecorded;
    }

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
                        }
                    } else {
                        outlierCount += 1;
                        if (outlierCount > 1) {
                            if (noteFound) {
                                noteFound = false;
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

    public ArrayList<String> getLastRecorded() {
        return lastRecorded;
    }


    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if (pitchDetectionResult.getPitch() != -1) {
            double timeStamp = audioEvent.getTimeStamp();
            float pitch = pitchDetectionResult.getPitch();
            float probability = pitchDetectionResult.getProbability();
            double rms = audioEvent.getRMS() * 100;
//            String estimatedNote = findNote((double) pitch);
//            String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f, Approximated Note: %s )\n", timeStamp, pitch, probability, rms, estimatedNote);
//            textArea.setText(textArea.getText() + message);
//            textArea.positionCaret(textArea.getLength());
            if (silenceDetector.currentSPL() > threshold) {
                if (probability > 0.8) {
                    detectedFrequencies.add(findNote((double) pitch));
                    try {
                        hasNoteBeenFound();
                    } catch (Exception e) {
                        System.err.println("Notes played too quickly, can't keep up..");
                    }
                }
            }
        }
    }

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

    public Mixer getMixer() {
        return mixer;
    }

    public void setMixer(Mixer mixer) {
        this.mixer = mixer;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void addPopover(MicrophoneInputPopoverController m) {
        this.microphoneInputPopoverController = m;
    }

}