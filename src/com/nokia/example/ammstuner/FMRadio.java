/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 */

package com.nokia.example.ammstuner;

import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

/**
 * The main class of the MIDlet.
 */
public class FMRadio extends MIDlet implements CommandListener, ItemStateListener, Tuner.Listener {
    // Constants
    private final static String RECORDSTORENAME = "STATIONS";
    
    // Members
    private Form form;
    private Command aboutCommand;
    private Command presetCommand;
    private Command listCommand;
    private Command startCommand;
    private Command stopCommand;
    private Command exitCommand;
    protected Tuner tuner;
    private FrequencyBarItem frequencyItem;
    private UpDownButtonsItem upDownButtonsItem;
    private StringItem infoItem;
    private boolean radioIsStarting;
    private AboutForm aboutForm;
    private PresetForm presetForm;
    private FrequencyList frequencyList;
    private String[] frequencyStrings;
    private String[] nameStrings;
    protected Vector allStrings;
    private RMSManager rmsManager;
    protected int[] frequencyLimits;
    protected int minFrequency = 0;
    protected int maxFrequency = 0;
    protected StringItem strengthItem;

    /**
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    protected void startApp() {
        frequencyLimits = Tuner.getMinAndMaxFrequencies(); // min = 875000, max = 1080000
        minFrequency = frequencyLimits[0];
        maxFrequency = frequencyLimits[1];
        initUI();
        initRadio();
        
        radioIsStarting = false;
    }

    /**
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    protected void pauseApp() {
    }

    /**
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    protected void destroyApp(boolean unconditional) {
        tuner.killRadio();
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(Command, Displayable)
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command == startCommand) {
            radioIsStarting = true;
            tuner.startRadio();
        }
        else if (command == stopCommand) {
            tuner.stopRadio();
            
            form.addCommand(startCommand);
            form.removeCommand(stopCommand);
        }
        else if (command == presetCommand) {
            presetForm = new PresetForm("Preset stations", this);
            Display.getDisplay(this).setCurrent(presetForm);
        }
        else if (command == listCommand) {
            frequencyStrings = listFrequencies();
            nameStrings = listStationNames();
            if (frequencyStrings != null && nameStrings != null) {
                final int length = frequencyStrings.length;
                frequencyList = new FrequencyList("Frequencies ", List.IMPLICIT, this);
                for (int i = 0; i < length; i++) {
                    frequencyList.append(nameStrings[i] + " (" + frequencyStrings[i] + ")", null);
                }
            }
            else {
                frequencyList = new FrequencyList("Frequencies", List.IMPLICIT, this);            
            }
            Display.getDisplay(this).setCurrent(frequencyList);
        }
        else if (command == aboutCommand) {
            aboutForm = new AboutForm("About FM Radio", this);
            Display.getDisplay(this).setCurrent(aboutForm);
        }
        else if (command == exitCommand) {
            if (!tuner.frequencyVector.isEmpty()) {
                this.saveStationsToRMS(tuner.frequencyVector);
                // TODO: Sort the radio stations based on the frequency.
            }
            notifyDestroyed();
        }
    }

    /**
     * @see javax.microedition.lcdui.ItemStateListener#itemStateChanged(Item)
     */
    public void itemStateChanged(Item item) {
        if (item.equals(upDownButtonsItem)) {
            if (!tuner.getIsStarted()) {
                return;
            }
            
            if (upDownButtonsItem.getButtonPressed() == UpDownButtonsItem.DOWN) {
                System.out.println("AMMSTuner.itemStateChanged(): Down");
                
                if (!tuner.seekNextFrequency(tuner.getCurrentFrequency(), false)) {
                    log("Seek down: RadioPlayer = null!");
                }
            }
            else if (upDownButtonsItem.getButtonPressed() == UpDownButtonsItem.UP) {
                System.out.println("AMMSTuner.itemStateChanged(): Up");
                
                if (!tuner.seekNextFrequency(tuner.getCurrentFrequency(), true)) {
                    log("Seek up: RadioPlayer = null!");
                }
            }
        }
    }

    /**
     * Appends the form with the given message.
     * @param message The message to show on the form.
     */
    protected void log(String message) {
        form.append(message);
    }

    /**
     * Creates an instance of Tuner class and initializes the radio.
     */
    private void initRadio() {
        if (tuner == null) { 
            tuner = new Tuner(this);
        }
        tuner.initializeRadio();
        readStationsFromRMS();        
    }
    
    /**
     * Initializes the MIDlet main UI: the Form, CustomItems and Commands, and shows the Form. 
     */
    private void initUI() {
        form = new Form("FM Radio");
        frequencyItem = new FrequencyBarItem(this);
        infoItem = new StringItem("Status", "Ready.");
        upDownButtonsItem = new UpDownButtonsItem(this);
        //strengthItem = new StringItem("Signal strength: 0/100", "");
        form.append(frequencyItem);
        form.append(infoItem);
        form.append(upDownButtonsItem);
        //form.append(strengthItem); 
        
        startCommand = new Command("Start radio", Command.SCREEN, 1);
        stopCommand = new Command("Stop radio", Command.SCREEN, 2);
        presetCommand = new Command("Preset stations", Command.SCREEN, 3);
        listCommand = new Command("List frequencies", Command.SCREEN, 4);
        aboutCommand = new Command("About FM Radio", Command.SCREEN, 5);
        exitCommand = new Command("Exit", Command.EXIT, 1);
        
        form.addCommand(startCommand);
        form.addCommand(presetCommand);
        form.addCommand(listCommand);
        form.addCommand(aboutCommand);
        form.addCommand(exitCommand);
        form.setCommandListener(this);
        form.setItemStateListener(this);
        Display.getDisplay(this).setCurrent(form);        
    }

    /**
     * Method for adding Commands to the Form for controlling the radio.
     */
    private void addCommandsAfterRadioIsStarted() {
        form.removeCommand(startCommand);
        form.addCommand(stopCommand);
    }


    // Call backs from Tuner.Listener

    /**
     * @see Tuner.Listener#onInitialized()
     */
    public void onInitialized() {
        infoItem.setText("Radio initialized.");
    }

    /**
     * @see Tuner.Listener#onStarted()
     */
    public void onStarted() {
        radioIsStarting = false;
        infoItem.setText("Radio started.");
        addCommandsAfterRadioIsStarted();
        strengthItem.setLabel("Signal strength: " + tuner.getStrength() + "/100");
    }

    /**
     * @see Tuner.Listener#onStopped()
     */
    public void onStopped() {
        frequencyItem.updateFrequency(tuner.minFrequency);
        infoItem.setText("Radio stopped.");
    }

    /**
     * @see Tuner.Listener#onFrequencyChanged(int)
     */
    public void onFrequencyChanged(int frequency) {
        frequencyItem.updateFrequency(frequency);
        infoItem.setText("Frequency set to: " + ((double)frequency / 10000) + " MHz");
        strengthItem.setLabel("Signal strength: " + tuner.getStrength() + "/100");       
    }

    /**
     * @see Tuner.Listener#onSeekFailed()
     */
    public void onSeekFailed() {
        infoItem.setText("No FM broadcast found.");
    }

    /**
     * @see Tuner.Listener#onError(String)
     */
    public void onError(String errorMessage) {
        if (radioIsStarting) {
            // Something went wrong whilst starting the radio. This could easily
            // been caused by the lack of headphones i.e. no antenna.
            radioIsStarting = false;
            
            Alert alert = new Alert("Failed to start",
                "Make sure you have headphones connected to your phone.",
                null, AlertType.ERROR);
            alert.setTimeout(Alert.FOREVER);
            alert.addCommand(new Command("Ok", Command.OK, 1));
            Display.getDisplay(this).setCurrent(alert);
        }
        else {
            log(errorMessage);
        }
    }
    
    /**
     * Shows the main screen.
     */
    protected void showTunerForm() {
        Display.getDisplay(this).setCurrent(form);
    }
    
    /**
     * Updates the info text and the FrequencyItem, when the frequency has been changed.
     * @param frequency the updated frequency 
     */
    protected void updateUI(int frequency) {
        infoItem.setText("Selected frequency: " + frequency);
        frequencyItem.updateFrequency(frequency);
    }
    
    /**
     * Shows the frequency list, which have been saved to Vector (and RecordStore). This method is called, when 
     * a station name has been modified.
     * @param index Index of the modified frequency.
     * @param newName The new name, which has been set on StationForm.
     */
    protected void showFrequencyList(int index, String newName) {
        int frequency = ((Integer)this.tuner.frequencyVector.elementAt(index)).intValue();
        frequencyList.set(index, newName + " (" + ((double)frequency / 10000) + " MHz)", null);
        Display.getDisplay(this).setCurrent(frequencyList);
    }

    /**
     * Shows the frequency list, which have been saved to Vector (and RecordStore). This method is called, when 
     * a station name has not been modified.
     */
    protected void showFrequencyList() {
        Display.getDisplay(this).setCurrent(frequencyList);
    }    
    
    /**
     * Creates a String array of the frequencies saved to Vector.
     * @return the String array of the saved frequencies
     */
    private String[] listFrequencies() {
        Vector frequencies = tuner.frequencyVector;
        if (frequencies == null) return null;
        final int size = frequencies.size();
        if (size == 0) {
            System.out.println("frequencies size: " + size);
            return null;
        }
        String[] freqStrings = new String[size];
        //int freq = 0;
        for (int i = 0; i < size; i++) {
            final int freq = ((Integer)frequencies.elementAt(i)).intValue();
            freqStrings[i] = ((double)freq / 10000) + " MHz";
        }
        return freqStrings;
    }
    
    /**
     * Creates a String array of the station names saved to Vector.
     * @return the String array of the saved names
     */
    private String[] listStationNames() {
        Vector names = tuner.nameVector;
        if (names == null) return null;
        final int size = names.size();
        if (size == 0) {
            System.out.println("names size: " + size);
            return null;
        }
        String[] stationStrings = new String[size];
        for (int i = 0; i < size; i++) {
            final String name = (String)names.elementAt(i);
            stationStrings[i] = name;
        }
        return stationStrings;    
    }
    
    /**
     * Saves the station frequencies and names to RecordStore.
     * @param stations Vector, where the frequencies have been stored.
     */
    private void saveStationsToRMS(Vector stations) {
        rmsManager = new RMSManager(this, RECORDSTORENAME);
        rmsManager.openRecStore();
        final int length = stations.size();
        for (int i = 0; i < length; i++) {
            final Integer frequency = (Integer)tuner.frequencyVector.elementAt(i);
            rmsManager.addRec(frequency.toString());
            String name = (String)tuner.nameVector.elementAt(i);
            rmsManager.addRec(name);
        }
        rmsManager.closeRecStore();
    }
    
    /**
     * Read the station names and frequencies from RecordStore.
     */
    private void readStationsFromRMS() {
        allStrings = new Vector();
        rmsManager = new RMSManager(this, RECORDSTORENAME);
        rmsManager.openRecStore();
        rmsManager.readData();
        rmsManager.closeRecStore();
        final int length = allStrings.size();
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) {
                final String frequency = (String)allStrings.elementAt(i);
                tuner.frequencyVector.addElement(Integer.valueOf(frequency));
            }
            else {
                tuner.nameVector.addElement((String)allStrings.elementAt(i));
            }
        }
    }
    
    /**
     * Shows an Alert.
     * @param title The Alert title
     * @param text The Alert text content
     */
    protected void showError(String title, String text) {
        Alert alert = new Alert(title, text, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        Displayable current = Display.getDisplay(this).getCurrent();
        if (current instanceof Alert) {}
        else Display.getDisplay(this).setCurrent(alert);
    }      
}
