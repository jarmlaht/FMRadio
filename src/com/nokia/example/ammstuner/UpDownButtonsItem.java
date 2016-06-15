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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Up and down buttons for selecting the frequency.
 */
public class UpDownButtonsItem extends CustomItem {
    // Constants
    public static final int NONE = 0;
    public static final int UP = 1;
    public static final int DOWN = 2;
    private static final int BUTTONS_ITEM_WIDTH = 240;
    private static final int BUTTONS_ITEM_HEIGHT = 50;
    private static final int MARGIN = 20;

    // Members
    //private FMRadio midlet;
    private Image upButton = null;
    private Image upButtonPressed = null;
    private Image downButton = null;
    private Image downButtonPressed = null;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private int buttonPressed = NONE;
    private int width = 0;

    /**
     * Constructor.
     */
    public UpDownButtonsItem(FMRadio midlet) {
        super(null);
        //this.midlet = midlet;
        
        try {
            downButton = Image.createImage("/downbutton.png");
            downButtonPressed = Image.createImage("/downbutton_pressed.png");
            upButton = Image.createImage("/upbutton.png");
            upButtonPressed = Image.createImage("/upbutton_pressed.png");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        imageWidth = downButton.getWidth();
        imageHeight = downButton.getHeight();
    }

    /** 
     * @return The button currently pressed (UP or DOWN) or NONE if no button
     * is pressed.
     */
    public final int getButtonPressed() {
        return buttonPressed;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#paint(Graphics, int, int)
     */
    public void paint(Graphics graphics, int width, int height) {
        if (this.width != width) {
            this.width = width;
        }
        
        graphics.drawImage((buttonPressed == DOWN) ? downButtonPressed : downButton,
                           MARGIN, 0, Graphics.TOP | Graphics.LEFT);
        graphics.drawImage((buttonPressed == UP) ? upButtonPressed : upButton,
                           width - imageWidth - MARGIN, 0, Graphics.TOP | Graphics.LEFT);
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getMinContentWidth()
     */
    protected int getMinContentWidth() {
        return BUTTONS_ITEM_WIDTH;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getMinContentHeight()
     */
    protected int getMinContentHeight() {
        return BUTTONS_ITEM_HEIGHT;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getPrefContentWidth(int)
     */
    protected int getPrefContentWidth(int arg0) {
        return BUTTONS_ITEM_WIDTH;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#getPrefContentHeight(int)
     */
    protected int getPrefContentHeight(int arg0) {
        return BUTTONS_ITEM_HEIGHT;
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#pointerPressed(int, int)
     */
    protected void pointerPressed(int x, int y) {
        System.out.println("UpDownButtonsItem::pointerPressed(): [" + x + ", " + y + "]");
        
        int pressed = NONE;
        
        if (x > MARGIN && x < (MARGIN + imageWidth) && y > 0 && y < imageHeight) {
            pressed = DOWN;
        }
        else if (x < width && x > (width - MARGIN - imageWidth)
            && y > 0 && y < imageHeight)
        {
            pressed = UP;
        }
        
        if (pressed != buttonPressed) {
            buttonPressed = pressed;
            notifyStateChanged();
            repaint();
        }
    }

    /**
     * @see javax.microedition.lcdui.CustomItem#pointerReleased(int, int)
     */
    protected void pointerReleased(int x, int y) {
        System.out.println("UpDownButtonsItem.pointerReleased(): [" + x + ", " + y + "]");
        buttonPressed = NONE;
        notifyStateChanged();
        repaint();
    }
}
