/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 */

package com.nokia.example.ammstuner;

import java.io.IOException;

import javax.microedition.amms.control.tuner.TunerControl;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import java.util.Vector;

/**
 * The class for managing the tuner.
 */
public class Tuner {
    // Members
    private Listener listener;
    private Thread thread;
    private static TunerControl tunerControl;
    private Player radioPlayer;
    private int frequency;
    private boolean isStarted;
    protected Vector frequencyVector;
    protected Vector nameVector;
    protected static int numberOfPresets;
    protected static int[] presetFrequencies;
    protected static String[] presetNames;
    //protected int signalStrength = 0;
    protected int[] frequencyLimits;    
    public int minFrequency = 0;
    public int maxFrequency = 0;
    
    /**
     * Constructor.
     * @param ammsTuner
     */
    public Tuner(Listener listener) {
        this.listener = listener;
        frequencyVector = new Vector();
        nameVector = new Vector();
        isStarted = false;
        frequencyLimits = getMinAndMaxFrequencies();
        minFrequency = frequencyLimits[0];
        maxFrequency = frequencyLimits[1];        
    }

    /** 
     * @return The current frequency.
     */
    public int getCurrentFrequency() {
        return frequency;
    }

    /**
     * Sets the tuner frequency
     * @param frequency The frequency to be set
     */
    public int setFrequency(int frequency) {
        return tunerControl.setFrequency(frequency, TunerControl.MODULATION_FM);
    }
    
    /** 
     * @return true if the radio has been started. False otherwise.
     */
    public boolean getIsStarted() {
        return isStarted;
    }
    
    /**
     * Gets tuner signal strength
     * @return signal strength
     */
    public int getStrength() {
        int strength = 0;
        try {
            strength = tunerControl.getSignalStrength();
        } catch (MediaException me) {
            if (listener != null) {
                listener.onError("Tuner.getStrength(): " + me.toString());
            }
        }
        return strength;
    }
    
    /**
     * Initializes the radio.
     */    
    public void initializeRadio () {
        thread = new Thread() {
            public void run() {
                try {
                    radioPlayer = Manager.createPlayer("capture://radio");
                    radioPlayer.realize();
                    tunerControl = (TunerControl)radioPlayer.getControl(
                        "javax.microedition.amms.control.tuner.TunerControl");
                    tunerControl.setStereoMode(TunerControl.STEREO);
                    listPresetFrequenciesAndNames();
                    
                }
                catch (MediaException me) {
                    if (listener != null) {
                        listener.onError("Tuner.initializeRadio(): " + me.toString());
                    }
                }
                catch (IOException ioe) {
                    if (listener != null) {
                        listener.onError("Tuner.initializeRadio(): " + ioe.toString());
                    }
                }
                
                if (listener != null) {
                    listener.onInitialized();
                }
            }
        };
        thread.start();
    }

    /**
     * Gets the TunerControl's minimum and maximum frequencies and saves them to Integer array.
     * @return Integer array with min and max frequencies
     */
    public static int[] getMinAndMaxFrequencies() {        
        Player player;
        int[] freqs = new int[2];
        try {
            player = Manager.createPlayer("capture://radio");
            player.realize();
            tunerControl = (TunerControl)player.getControl("javax.microedition.amms.control.tuner.TunerControl");
            tunerControl.setStereoMode(TunerControl.STEREO);
            freqs[0] = tunerControl.getMinFreq(TunerControl.MODULATION_FM);
            freqs[1] = tunerControl.getMaxFreq(TunerControl.MODULATION_FM);            
        }
        catch (MediaException me) {           
        }        
        catch (IOException ioe) {
        }
        finally {
            player = null;
        }
        return freqs;
    }
    
    /**
     * Starts the radio.
     */     
    public void startRadio() {
        thread = new Thread() {
            public void run() {
                try {
                    radioPlayer.start();
                    
                    if (listener != null) {
                        isStarted = true;
                        listener.onStarted();
                    }
                    frequency = tunerControl.seek(minFrequency, TunerControl.MODULATION_FM, true);
                    
                    if (frequency == 0) {
                        if (listener != null) {
                            listener.onSeekFailed();
                        }
                    }
                    else {
                        if (listener != null) {
                            listener.onFrequencyChanged(frequency);
                            Integer freqInteger = new Integer(frequency);
                            if (!checkFrequency(freqInteger)) {
                                frequencyVector.addElement(freqInteger);
                                nameVector.addElement("<no name>");
                            }
                        }
                    }
                }
                catch (MediaException me) {
                    if (listener != null) {
                        listener.onError("Tuner.startRadio(): " + me.toString());
                    }
                }
                catch (IllegalArgumentException iae) {
                    if (listener != null) {
                    	listener.onError("Tuner.startRadio(): " + iae.toString());
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * Seeks for a new frequency, either up or down from the current frequency.
     * @param currentFrequency The current frequency
     * @param up If true, method seeks upwards, if false, seeks downwards.
     * @return True if successful, false otherwise.
     */
    public boolean seekNextFrequency(final int currentFrequency, boolean up) {
        if (radioPlayer == null) {
            return false;
        }
        
        final boolean upwards = up;
        
        thread = new Thread() {
            public void run() {
                try {
                    frequency = tunerControl.seek(currentFrequency, TunerControl.MODULATION_FM, upwards);
                    
                    if (frequency == 0) {
                        if (listener != null) {
                            listener.onSeekFailed();
                        }
                    }
                    else {
                        if (listener != null) {
                            listener.onFrequencyChanged(frequency);
                            Integer freqInteger = new Integer(frequency);
                            if (!checkFrequency(freqInteger)) {
                                frequencyVector.addElement(freqInteger);
                                nameVector.addElement("<no name>");
                            }
                        }
                    }
                }
                catch (MediaException me) {
                    if (listener != null) {
                    	listener.onError("Tuner.seekNextFrequency(): " + me.toString());
                    }
                }
            }
        };
        
        thread.start();
        return true;
    }

    /**
     * Stops the radio player.
     */
    public void stopRadio() {
        if (radioPlayer == null) {
            return;
        }
        
        try {
            radioPlayer.stop();
        }
        catch (MediaException me) {
            if (listener != null) {
            	listener.onError("Tuner.stopRadio(): " + me.toString());
            }
        }
        
        if (listener != null) {
            isStarted = false;
            listener.onStopped();
        }
    }

    /**
     * Stops and closes the radio player.
     */
    public void killRadio() {
        if (radioPlayer == null) {
            return;
        }
        
        try {
            radioPlayer.stop();
            radioPlayer.close();
            radioPlayer = null;
            
            if (thread != null) {
                thread = null;
            }
        }
        catch (MediaException me) {
            if (listener != null) {
            	listener.onError("Tuner.killRadio(): " + me.toString());
            }
        }
        
        if (listener != null) {
        	isStarted = false;
            listener.onStopped();
        }
    }

    /**
     * Checks if the frequency f is already added to the Vector
     * @param f frequency to be tested
     * @return true, if f already exists in the Vector, otherwise false
     */
    private boolean checkFrequency(Integer f) {
        boolean exists = false;
        final int length = frequencyVector.size();
        for (int i = 0; i < length; i ++) {
            if (f.equals(frequencyVector.elementAt(i))) {
                exists = true;
            }
        }
        return exists;
    }
    
    /**
     * Lists the preset frequencies and station names stored by the native radio player.
     */
    private static void listPresetFrequenciesAndNames() {
        numberOfPresets = tunerControl.getNumberOfPresets();
        if (numberOfPresets > 0) {
            presetFrequencies = new int[numberOfPresets];
            presetNames = new String[numberOfPresets];
            for (int i = 1; i < numberOfPresets; i++) {
                presetFrequencies[i] = tunerControl.getPresetFrequency(i);
                presetNames[i] = tunerControl.getPresetName(i);
            }        
        }
    }
    
    /**
     * A listener interface for the UI. The listener gets notified about the
     * changes in the state of the tuner. 
     */
    public interface Listener {
        void onInitialized();
        void onStarted();
        void onStopped();
        void onFrequencyChanged(int frequency);
        void onSeekFailed();
        void onError(String errorMessage);
    }
}
