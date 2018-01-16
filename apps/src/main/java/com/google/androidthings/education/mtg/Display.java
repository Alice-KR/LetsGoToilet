package com.google.androidthings.education.mtg;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;

public class Display {

    private AlphanumericDisplay mSegmentDisplay;

    public boolean open() {
        try {
            mSegmentDisplay = RainbowHat.openDisplay();
            mSegmentDisplay.setBrightness(1.0f);
            mSegmentDisplay.setEnabled(true);
            mSegmentDisplay.clear();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void show(String str) {
        try {
            mSegmentDisplay.display(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            mSegmentDisplay.display("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (mSegmentDisplay != null) {
            try {
                mSegmentDisplay.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mSegmentDisplay = null;
            }
        }
    }
}
