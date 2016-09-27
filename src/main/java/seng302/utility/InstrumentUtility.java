package seng302.utility;

import javax.sound.midi.Instrument;

import seng302.Environment;

/**
 * This class provides a few basic courtesy functions that are used multiple times.
 * Includes functions for finding an instrument by name or number, as well fetching the 'default'
 * instrument.
 */
public class InstrumentUtility {

    /**
     * Finds the instrument object relating to the given name.
     *
     * @param instrumentName String representation of an instrument's name
     * @param env            The environment in which we are looking for instruments
     * @return The instrument object with the given name, or null if there isn't one.
     */
    public static Instrument getInstrumentByName(String instrumentName, Environment env) {
        for (Instrument instrument : env.getPlayer().getAvailableInstruments()) {
            if (instrument.getName().equals(instrumentName)) {
                return instrument;
            }
        }
        return null;
    }

    /**
     * Finds the instrument with the given ID, based on the music player
     * @param id The ID of the instrument to be found
     * @param env The environment in which we are looking for instruments
     * @return The instrument object with the given id.
     */
    public static Instrument getInstrumentById(int id, Environment env) {
        return env.getPlayer().getAvailableInstruments()[id];
    }

    public static Instrument[] getInstruments(Environment env){
        return env.getPlayer().getAvailableInstruments();
    }

    /**
     * Fetches the first instrument that the music player lists as available.
     * @param env The environment that the instrument is being selected from
     * @return The first available instrument of the environment's music player
     */
    public static Instrument getDefaultInstrument(Environment env) {
        System.out.println(env.getPlayer().getAvailableInstruments());
        return env.getPlayer().getAvailableInstruments()[0];
    }
}
