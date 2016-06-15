/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 */

package com.nokia.example.ammstuner;

import javax.microedition.lcdui.*;

/**
 * FrequencyList lists all the saved frequencies.
 * TODO: Adding and editing the frequency name
 */
public class FrequencyList extends List implements CommandListener {
    private FMRadio midlet;
    private Command editCommand;
    private Command backCommand;
    private Command exitCommand;
    private StationForm stationForm;
    
    /**
     * FrequencyList constructor
     * @param title FrequencyList title
     * @param listType Type of the list, in this MIDlet only List.IMPLICIT is used
     * @param midlet The owner MIDlet
     */
    public FrequencyList(String title, int listType, FMRadio midlet) {
        super (title, listType);
        this.midlet = midlet;
        editCommand = new Command("Edit", Command.SCREEN, 1);
        exitCommand = new Command("Exit", Command.EXIT, 1);
        backCommand = new Command("Back", Command.BACK, 1);
        //this.addCommand(exitCommand);
        this.addCommand(editCommand);
        this.addCommand(backCommand);
        this.setCommandListener(this);
    }

    /**
     * FrequencyList constructor, to be used, if there are already saved frequencies
     * @param title FrequencyList title
     * @param listType Type of the list, in this MIDlet only List.IMPLICIT is used
     * @param strings Frequency Strings
     * @param images Images to be used as icons
     * @param midlet The owner MIDlet
     */
    public FrequencyList(String title, int listType, String[] strings, Image[] images, FMRadio midlet) {
        super (title, listType, strings, images);
        this.midlet = midlet;
        editCommand = new Command("Edit", Command.SCREEN, 1);        
        exitCommand = new Command("Exit", Command.EXIT, 1);
        backCommand = new Command("Back", Command.BACK, 1);
        //this.addCommand(exitCommand);
        this.addCommand(editCommand);
        this.addCommand(backCommand);
        this.setCommandListener(this);
    }    
    
    /**
     * Handles the opening of the selected frequency.
     */
    private void openSelected() {
        int selected = this.getSelectedIndex();
        if (selected == -1) return; // nothing selected
        System.out.println("selected: " + this.getString(selected));
        String name = (String)midlet.tuner.nameVector.elementAt(selected);
        stationForm = new StationForm(name, midlet, selected);
        Display.getDisplay(midlet).setCurrent(stationForm);
    }
    
    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(Command, Displayable)
     */    
    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND) { // IMPLICIT
            openSelected();
        }
        else if (c == backCommand) {
            midlet.showTunerForm();
        }
        else if (c == editCommand) {
            // Not needed.
        }
        else if (c == exitCommand) {
            midlet.notifyDestroyed();
        }
    }
    
    /**
     * Shows an info Alert
     * @param title Alert title
     * @param text Alert text
     */
    protected void showAlert(String title, String text) {
        Alert alert = new Alert(title, text, null, AlertType.INFO);
        alert.setTimeout(Alert.FOREVER);
        Displayable current = Display.getDisplay(midlet).getCurrent();
        if (current instanceof Alert) {}
        else Display.getDisplay(midlet).setCurrent(alert);
    }    
}