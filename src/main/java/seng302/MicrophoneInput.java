package seng302;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import seng302.data.Note;

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
    private ArrayList<Double> detectedFrequencies = new ArrayList<>();
    private ArrayList<String> latestDetectedNotes = new ArrayList<>();
    private double threshold;

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
        System.out.println(foundIndex);
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

        float sampleRate = 44100;
        int bufferSize = 1024;
        int overlap = 0;

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

        new Thread(dispatcher, "Audio dispatching").start();
    }

    public void stopRecording() {
        dispatcher.stop();
        try {
            Collections.sort(detectedFrequencies);
            double range = detectedFrequencies.get(detectedFrequencies.size() - 1) - detectedFrequencies.get(0);
            while (range > 10) {
                detectedFrequencies.remove(0);
                detectedFrequencies.remove(detectedFrequencies.size() - 1);
                range = detectedFrequencies.get(detectedFrequencies.size() - 1) - detectedFrequencies.get(0);
            }
            double total = 0; // For mean calculations
            double mean;
            for (double freq : detectedFrequencies) {
                total += freq;
            }
            mean = total / detectedFrequencies.size();
            String note = findNote(mean);
            latestDetectedNotes.add(note);
            detectedFrequencies.clear();
        } catch (Exception e) {
            System.err.println("No sounds recorded.");
        }
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
            if (probability > 0.8) {
                detectedFrequencies.add((double) pitch);
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


}