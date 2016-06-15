/**
 * Copyright (c) 2013 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 */

package com.nokia.example.ammstuner;

import java.io.IOException;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * A custom item for displaying the current frequency.
 */
public class FrequencyBarItem extends CustomItem {
    // Constants
    private static final int ITEM_WIDTH = 240;
    private static final int ITEM_HEIGHT = 50;
    private static final int GREY_COLOR = 0xf4f4f4; // 244, 244, 244

    // Members
    private FMRadio midlet;
    private Image greyBar;
    private Image blueBar;
    private Image frequencyBar;
    private Font font;
    private String frequencyString;
    private int imageHeight;
    private int frequencyStringLength;

    /**
     * Constructor.
     * @param ammsTuner The MIDlet instance.
     */
    public FrequencyBarItem(FMRadio midlet) {
        super(null);
        this.midlet = midlet;
        
        try {
            greyBar = Image.createImage("/greybar.png");
            blueBar = Image.createImage("/bluebar.png");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        imageHeight = blueBar.getHeight();
        font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        updateFrequency(midlet.minFrequency);
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#paint(Graphics, int, int)
     */
    public void paint(Graphics graphics, int width, int height) {
        graphics.setColor(0x000000);
        graphics.drawImage(greyBar, 0, height / 2 - imageHeight / 2,
                           Graphics.TOP | Graphics.LEFT);
        
        if (frequencyBar != null) {
            graphics.drawImage(frequencyBar, 0, height / 2 - imageHeight / 2,
                               Graphics.TOP | Graphics.LEFT);
        }
        
        graphics.setFont(font);
        graphics.setColor(0xffffff);
        graphics.drawString(frequencyString,
                            width / 2 - frequencyStringLength / 2 + 1,
                            (height - graphics.getFont().getHeight()) / 2 - 2,
                            Graphics.TOP | Graphics.LEFT);
        
        graphics.setColor(0x000000);
        graphics.drawString(frequencyString,
                            width / 2 - frequencyStringLength / 2,
                            (height - graphics.getFont().getHeight()) / 2 - 3,
                            Graphics.TOP | Graphics.LEFT);
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getMinContentWidth()
     */
    protected int getMinContentWidth() {
        return ITEM_WIDTH;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getMinContentHeight()
     */
    protected int getMinContentHeight() {
        return ITEM_HEIGHT;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getPrefContentWidth(int)
     */
    protected int getPrefContentWidth(int arg0) {
        return ITEM_WIDTH;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getPrefContentHeight(int)
     */
    protected int getPrefContentHeight(int arg0) {
        return ITEM_HEIGHT;
    }

    /**
     * Updates the current frequency.
     * @param frequency The current frequency.
     */
    public final void updateFrequency(final double frequency) {
        createFrequencyBar(frequency);
        frequencyString = (frequency / 10000) + " MHz";
        frequencyStringLength = font.stringWidth(frequencyString);
        repaint();
    }

    /**
     * Creates the image for displaying the relative frequency in bar form.
     * @param frequency The current frequency.
     * @return The newly created Image instance.
     */
    private Image createFrequencyBar(final double frequency) {
        final int width = 205 - (1080000 - (int)frequency) / 1000 + 12;
        frequencyBar = Image.createImage(width, 44);
        int rgbData[] = new int[width * 44];
        
        try {
            blueBar.getRGB(rgbData, 0, width, 0, 0, width, 44);
        }
        catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
        
        frequencyBar = Image.createRGBImage(rgbData, width, 44, true);
        return frequencyBar;
    }
}
