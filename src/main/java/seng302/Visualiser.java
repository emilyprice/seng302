package seng302;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;

/**
 * This is a custom implementation of a Meta Event Listener.
 * This class receives meta messages from the music player.
 * These messages are of the type "midinumber on/off"
 * The messages are used to turn the highlights for given notes on and off on the keyboard.
 */
public class Visualiser implements MetaEventListener {

    private Environment env;

    public Visualiser(Environment env) {
        this.env = env;
    }


    @Override
    public void meta(MetaMessage meta) {
        String message = new String(meta.getData());
        String[] messageParts = message.split(" ");

        int midiValue = Integer.parseInt(messageParts[0]);

        boolean isOn = false;
        if (messageParts[1].equals("on")) {
            isOn = true;
        }

        // Either highlights or de-highlights a key
        if (isOn) {
            env.getRootController().getKeyboardPaneController().highlightKey(midiValue);
        } else {
            env.getRootController().getKeyboardPaneController().removeHighlight(midiValue);
        }
    }
}
