/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 */

package com.nokia.example.ammstuner;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class RMSManager {
    protected RecordStore rs = null;
    private FMRadio midlet;
    private int size = 0;
    private String recordStoreName = "";

    public RMSManager(FMRadio midlet, String name) {
        this.midlet = midlet;
        this.recordStoreName = name;
    }

    /**
     * Opens (and creates if needed) the RecordStore.
     */
    public void openRecStore() {
        try {
            rs = RecordStore.openRecordStore(recordStoreName, true);
            size = rs.getNumRecords();
        }
        catch (RecordStoreNotFoundException rsnfe) {
            midlet.showError("openRecStore(): RecordStoreNotFoundException", rsnfe.getMessage());
        }
        catch (RecordStoreException rse) {
            midlet.showError("openRecStore(): RecordStoreException: ", rse.getMessage());
        }
    }

    /**
     * Closes the RecordStore.
     */
    public void closeRecStore() {
        try {
            rs.closeRecordStore();
        }
        catch (RecordStoreNotOpenException rsnoe) {
            midlet.showError("closeRecStore(): RecordStoreNotOpenException", rsnoe.getMessage());
        }
        catch (RecordStoreException rse) {
            midlet.showError("closeRecStore(): RecordStoreException", rse.getMessage());
        }
    }

    /**
     * Reads the record data from the RecordStore.
     */
    public void readData() {
        byte[] recData = new byte[100];
        int len;
        String record;
        try {
            size = rs.getNumRecords();
            RecordEnumeration re = rs.enumerateRecords(null, null, true);
            while(re.hasNextElement()) {
		int id = re.nextRecordId();
                len = rs.getRecord(id, recData, 0);
                record = new String(recData, 0, len);
                midlet.allStrings.addElement(record);
            }
        }
        catch (InvalidRecordIDException iride) {
            midlet.showError("readData(): InvalidRecordIDException", iride.getMessage());
        }
        catch (RecordStoreNotOpenException rsnoe) {
            midlet.showError("readData(): RecordStoreNotOpenException", rsnoe.getMessage());
        }
        catch (RecordStoreException rse) {
            midlet.showError("readData(): RecordStoreException", rse.getMessage());
        }
    }

    /**
     * Adds a record to the RecordStore.
     * @param data String to be added as record to the RecordStore
     */
    public void addRec(String data) {
        byte[] record = data.getBytes();
        try {
            rs.addRecord(record, 0, record.length);
        }
        catch (RecordStoreNotOpenException rsnoe) {
            midlet.showError("addRec(): RecordStoreNotOpenException", rsnoe.getMessage());
        }
        catch (RecordStoreException rse) {
            midlet.showError("addRec(): RecordStoreException", rse.getMessage());
        }
    }

    /**
     * Sets a record value in the RecordStore.
     * @param data Replaces the existing data in a record with new one
     * @param id The id of the record, which is going to be changed.
     */
    public void setRec(byte[] data, int id) {
        try {
            rs.setRecord(id, data, 0, data.length);
        }
        catch (RecordStoreNotOpenException rsnoe) {
            midlet.showError("setRec(): RecordStoreNotOpenException", rsnoe.getMessage());
        }
        catch (RecordStoreException rse) {
            midlet.showError("setRec(): RecordStoreException", rse.getMessage());
        }
    }

    /**
     * Deletes the last record value in the RecordStore.
     */
    public void deleteLastRec() {
        try {
            size = rs.getNumRecords();
            rs.deleteRecord(size);
        }
        catch (RecordStoreNotOpenException rsnoe) {
            midlet.showError("deleteLastRec(): RecordStoreNotOpenException", rsnoe.getMessage());
        }
        catch (RecordStoreException rse) {
            midlet.showError("deleteLastRec(): RecordStoreException", rse.getMessage());
        }
    }

    /**
     * Deletes the RecordStore.
     */
    public void deleteRecStore() {
        try {
            RecordStore.deleteRecordStore(recordStoreName);
        }
        catch (RecordStoreNotFoundException rsnfe) {
            midlet.showError("deleteRecStore(): RecordStoreNotFoundException", rsnfe.getMessage());
        }
        catch (RecordStoreException rse) {
            midlet.showError("deleteRecStore(): RecordStoreException", rse.getMessage());
        }
    }
}
