package seng302.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import seng302.data.Note;

public class MicInputTestController implements PitchDetectionHandler {

    @FXML
    private ToggleButton recordButton;

    @FXML
    private ToggleGroup MIC;

    @FXML
    private ToggleButton stopButton;

    @FXML
    private TextArea textArea;

    private PitchProcessor.PitchEstimationAlgorithm algo;
    private Mixer mixer;
    private AudioDispatcher dispatcher;
    private TargetDataLine line;
    private ArrayList<Double> noteFrequencies = new ArrayList<>();

    public void create() {
        recordButton.setOnAction(event -> {
            try {
                startRecording();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        });
        stopButton.setOnAction(event -> stopRecording());
        algo = PitchProcessor.PitchEstimationAlgorithm.YIN;
        mixer = AudioSystem.getMixer(getMixerInfo(false, true).get(0));
        resetNoteFrequencies();
    }

    private void resetNoteFrequencies() {
        noteFrequencies.clear();
        int a = 440; // a is 440 hz...
        for (int x = 0; x < 127; ++x) {
            noteFrequencies.add(x, (a / (double) 32) * (Math.pow((double) 2, ((x - (double) 9) / (double) 12))));
        }
    }

    private String findNote(Double freq) {
        String note;
        noteFrequencies.add(freq);
        Collections.sort(noteFrequencies);
        int foundIndex = noteFrequencies.indexOf(freq);
        System.out.println(foundIndex);
        if (foundIndex <= noteFrequencies.size() - 2) {
            int closestIndex;
            Double higher = Math.abs(noteFrequencies.get(foundIndex + 1) - freq);
            Double lower = Math.abs(noteFrequencies.get(foundIndex - 1) - freq);
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

    private void startRecording() throws LineUnavailableException, UnsupportedAudioFileException {
//        currentMixer = mixer;

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

    private void stopRecording() {
        dispatcher.stop();
    }


    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if (pitchDetectionResult.getPitch() != -1) {
            double timeStamp = audioEvent.getTimeStamp();
            float pitch = pitchDetectionResult.getPitch();
            float probability = pitchDetectionResult.getProbability();
            double rms = audioEvent.getRMS() * 100;
            String estimatedNote = findNote((double) pitch);
            String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f, Approximated Note: %s )\n", timeStamp, pitch, probability, rms, estimatedNote);
            textArea.setText(textArea.getText() + message);
            textArea.positionCaret(textArea.getLength());
        }
    }

    private static Vector<Mixer.Info> getMixerInfo(
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
}
