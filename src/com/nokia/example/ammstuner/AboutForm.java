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
 * AboutForm for showing information about the application
 */
public class AboutForm extends Form implements CommandListener {
    private Command backCommand;
    private FMRadio midlet;
    
    public AboutForm(String title, FMRadio midlet) {
        super(title);
        this.midlet = midlet;
        this.backCommand = new Command("Back", Command.BACK, 1);
        this.addCommand(backCommand);
        this.setCommandListener(this);
        this.append(new StringItem(midlet.getAppProperty("MIDlet-Name"), "Version: " + midlet.getAppProperty("MIDlet-Version")));
        this.append("This app demonstates the features of AMMS (JSR-234) tuner capability.");
        this.append("You can start and stop the radio and search up and down for the frequencies.");
        this.append("The frequencies found are saved to the list, where they can be selected again, "
            + "and it is also possible to give descriptive names for the stations.");
        listFrequencies();
    }
    
    /**
     * Reads the saved frequencies from midlet.tuner.frequencyVector and lists them on the AboutForm.
     */
    private void listFrequencies() {
        this.append("Frequencies:");
        Vector frequencies = midlet.tuner.frequencyVector;
        Vector names = midlet.tuner.nameVector;
        if (frequencies != null || names != null) {
            int freq;
            String name;
            final int size = frequencies.size();
            for (int i = 0; i < size; i++) {
                freq = ((Integer)frequencies.elementAt(i)).intValue();
                name = names.elementAt(i).toString();
                this.append(name + ": " + freq);
            }
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