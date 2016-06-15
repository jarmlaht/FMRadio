/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 */

package com.nokia.example.ammstuner;

import java.util.Vector;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;

/**
 * PresetForm for showing the preset frequencies
 */
public class PresetForm extends Form implements CommandListener {
    private Command backCommand;
    private FMRadio midlet;
    
    public PresetForm(String title, FMRadio midlet) {
        super(title);
        this.midlet = midlet;
        this.backCommand = new Command("Back", Command.BACK, 1);
        this.addCommand(backCommand);
        this.setCommandListener(this);
        this.append(new StringItem("FM Tuner", "Version: " + midlet.getAppProperty("MIDlet-Version")));
        listPresetFrequencies();
    }
    
    /**
     * Reads the saved frequencies from midlet.tuner.frequencyVector and lists them on the AboutForm.
     */
    private void listPresetFrequencies() {
        int presets = Tuner.numberOfPresets;
        String[] names = Tuner.presetNames;
        int[] frequencies = Tuner.presetFrequencies;
        this.append("Preset frequencies: " + presets);
        if (presets > 0) {
            for (int i = 1; i < presets; i++) {
                this.append(names[i] + " (" + frequencies[i] + ")");
            }
        }
        else {
            this.append("No preset stations.");
        }
    }
    
    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(Command, Displayable)
     */	    
    public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
            midlet.showTunerForm();
        }
    }
}