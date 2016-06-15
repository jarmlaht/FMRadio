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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

/**
 * StationForm for selecting a station, modifying its name and for deleting it.
 */
public class StationForm extends Form implements CommandListener, ItemCommandListener {
    private Command backCommand;
    private Command saveCommand;
    private FMRadio midlet;
    private TextField nameTextField;
    private StringItem selectButton;
    private StringItem deleteButton;
    private int stationIndex = 0;
    private String newName = "";
    private String oldName = "";
    
    public StationForm(String title, FMRadio midlet, int index) {
        super(title);
        this.midlet = midlet;
        this.stationIndex = index;
        saveCommand = new Command("Save", Command.SCREEN, 1);
        backCommand = new Command("Back", Command.BACK, 1);
        this.addCommand(saveCommand);
        this.addCommand(backCommand);
        this.setCommandListener(this);
        oldName = (String)midlet.tuner.nameVector.elementAt(index);
        nameTextField = new TextField("Station name:", oldName, 50, TextField.ANY);
        selectButton = new StringItem("", "Select station", StringItem.BUTTON);
        selectButton.setDefaultCommand(new Command("Set", Command.ITEM, 1));
        selectButton.setItemCommandListener(this);
        deleteButton = new StringItem("", "Delete station", StringItem.BUTTON);
        deleteButton.setDefaultCommand(new Command("Set", Command.ITEM, 1));
        deleteButton.setItemCommandListener(this);
        this.append(nameTextField);
        this.append(selectButton);
        this.append(deleteButton);
    }
        
    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(Command, Displayable)
     */	    
    public void commandAction(Command c, Displayable d) {
        if (c == saveCommand) {
            newName = nameTextField.getString();
            if (!newName.equals("") || newName.equals("<no name>")) {
                midlet.tuner.nameVector.setElementAt(newName, stationIndex);
                this.setTitle(newName);
            }
        }
        if (c == backCommand) {
            newName = nameTextField.getString();
            if (newName.equals(oldName)) { 
                // The station name not changed.
                midlet.showFrequencyList();
            }
            else {
                // The station name changed.
                midlet.showFrequencyList(stationIndex, newName);
            }
        }
    }

    /**
     * @see javax.microedition.lcdui.ItemCommandListener#commandAction(Command, Item)
     */	    
    public void commandAction(Command c, Item item) {
        if (item.equals(selectButton)) {
            Integer frequency = (Integer)midlet.tuner.frequencyVector.elementAt(stationIndex);
            midlet.tuner.setFrequency(frequency.intValue());
            midlet.updateUI(frequency.intValue());
            midlet.showTunerForm();
        }
        else if (item.equals(deleteButton)) {}
    }
}