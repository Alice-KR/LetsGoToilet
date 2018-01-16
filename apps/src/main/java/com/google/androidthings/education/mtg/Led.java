package com.google.androidthings.education.mtg;

import android.graphics.Color;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;

public class Led {
    private static final String TAG = "LED";

    public static final int BLACK = 0xff000000;
    public static final int WHITE = 0xffffffff;
    public static final int RED = 0xffff0000;
    public static final int ORANGE = 0xffffa000;
    public static final int YELLOW = 0xffffff00;
    public static final int GREEN = 0xff00ff00;
    public static final int CYAN = 0xff00ffff;
    public static final int BLUE = 0xff0000ff;
    public static final int VIOLET = 0xff3000ff;
    public static final int MAGENTA = 0xffff00ff;
    public static final int PINK = 0xffff00c0;
    public static final int ALL = 100;

    private static final int DEF_BRIGHTNESS = 3;


    private Apa102 mLedStrip;
    private final int[] mColors = new int[RainbowHat.LEDSTRIP_LENGTH];
    private final int[] mRealColors = new int[RainbowHat.LEDSTRIP_LENGTH];

    public boolean open() {
        // LEDs are white but off.
        for (int i = 0; i < RainbowHat.LEDSTRIP_LENGTH; i++) {
            mColors[i] = WHITE;
            mRealColors[i] = BLACK;
        }
        boolean result = false;
        try {
            mLedStrip = RainbowHat.openLedStrip();
            Log.d(TAG, "Opened LED strip");
            setBrightness(DEF_BRIGHTNESS);
            result = true;
        } catch (IOException e) {
            Log.e(TAG, "Error opening LED strip", e);
        }
        return result;
    }

    public void close() {
        if (mLedStrip == null) {
            return;
        }
        off(ALL);
        try {
            mLedStrip.close();
            Log.d(TAG, "Closed LED strip");
        } catch (IOException e) {
            Log.e(TAG, "Error closing LED strip", e);
        }
        mLedStrip = null;
    }

    public void on(int position, int color) {
        if (position == ALL) {
            for (int i = 0; i < RainbowHat.LEDSTRIP_LENGTH; i++) {
                mColors[i] = color;
                mRealColors[i] = color;
            }
        } else if (!isValidPosition(position)) {
            Log.e(TAG, "Invalid LED position " + position);
            return;
        } else {
            mColors[position] = color;
            mRealColors[position] = color;
        }
        writeColors();
    }

    public void on(int position) {
        if (position == ALL) {
            for (int i = 0; i < RainbowHat.LEDSTRIP_LENGTH; i++) {
                mRealColors[i] = WHITE;
            }
        } else if (!isValidPosition(position)) {
            Log.e(TAG, "Invalid LED position " + position);
            return;
        } else {
            mRealColors[position] = WHITE;
        }
        writeColors();
    }

    public void off(int position) {
        if (position == ALL) {
            for (int i = 0; i < RainbowHat.LEDSTRIP_LENGTH; i++) {
                mRealColors[i] = BLACK;
            }
        } else if (!isValidPosition(position)) {
            Log.e(TAG, "Invalid LED position " + position);
            return;
        } else {
            mRealColors[position] = BLACK;
        }
        writeColors();
    }

    public void toggle(int position) {
        if (!isValidPosition(position)) {
            Log.e(TAG, "Invalid LED position " + position);
            return;
        }
        if (isBlack(mRealColors[position])) {
            mRealColors[position] = mColors[position];
        } else {
            mRealColors[position] = BLACK;
        }
        writeColors();
    }

    public void toggle() {
        for (int i = 0; i < RainbowHat.LEDSTRIP_LENGTH; i++) {
            if (isBlack(mRealColors[i])) {
                mRealColors[i] = mColors[i];
            } else {
                mRealColors[i] = BLACK;
            }
        }
        writeColors();
    }

    public void setBrightness(int level) {
        if (mLedStrip == null) {
            Log.e(TAG, "Failed to set brightness. LED strip is not ready");
            return;
        }
        mLedStrip.setBrightness(convertBrightness(level));
        writeColors();
    }

    public static int getColor(int degree) {
        final float[] hsv = {1f, 1f, 1f};
        degree = Math.max(0, Math.min(degree, 359));
        hsv[0] = (float)degree;
        return Color.HSVToColor(hsv);
    }

    private void writeColors() {
        if (mLedStrip == null) {
            Log.e(TAG, "Failed to write on. LED strip is not ready");
            return;
        }
        try {
            mLedStrip.write(mRealColors);
        } catch (IOException e) {
            Log.e(TAG, "Failed to write on", e);
        }
        // LED values are not changed instantly. Seems like the commands are buffered.
        // As a workaround, write the command twice.
        try {
            mLedStrip.write(mRealColors);
        } catch (IOException e) {
            Log.e(TAG, "Failed to write on", e);
        }
    }

    private static boolean isBlack(int color) {
        return (color & 0xffffff) == (BLACK & 0xffffff);
    }

    private static int convertBrightness(int value) {
        value = Math.max(1, Math.min(value, 10));
        return 31 * value / 10;
    }

    private static boolean isValidPosition(int position) {
        return position >= 0 && position < RainbowHat.LEDSTRIP_LENGTH;
    }
}
