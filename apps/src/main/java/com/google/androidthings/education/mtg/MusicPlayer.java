package com.google.androidthings.education.mtg;

import android.util.Log;

import com.google.android.things.contrib.driver.pwmspeaker.Speaker;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;
import java.util.EnumMap;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class MusicPlayer {

    public enum Note {
        C(1), D(2), E(3), F(4), G(5), A(6), B(7);
        public final int value;

        private Note(int value) {
            this.value = value;
        }
    }

    private Speaker mSpeaker;
    private EnumMap<Note, Double> mNoteMap;

    public boolean open() {
        try {
            mSpeaker = RainbowHat.openPiezo();
            mSpeaker.stop(); // in case the PWM pin was enabled already
        } catch (IOException e) {
            Log.e(TAG, "Error initializing speaker:" + e.getMessage());
            return false; // don't initialize the handler
        }
        mNoteMap = new EnumMap<Note, Double>(Note.class);
        mNoteMap.put(Note.C, 261.626);
        mNoteMap.put(Note.D, 293.665);
        mNoteMap.put(Note.E, 329.628);
        mNoteMap.put(Note.F, 349.228);
        mNoteMap.put(Note.G, 391.995);
        mNoteMap.put(Note.A, 440.000);
        mNoteMap.put(Note.B, 493.883);
        return true;
    }

    public void close() {
        try {
            mSpeaker.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void playAll(double sec, Note... notes)  {
        try {
            for ( Note note : notes ) {
                mSpeaker.play(mNoteMap.get(note));
                sleepAndStop(sec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void playAll(Note... notes)  {
        try {
            for ( Note note : notes ) {
                mSpeaker.play(mNoteMap.get(note));
                sleepAndStop(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void play(Note note) {
        try {
            mSpeaker.play(mNoteMap.get(note));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void stop() {
        try {
            mSpeaker.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sleepAndStop(double sec) throws InterruptedException, IOException {
        sleep((long) (sec * 1000));
        mSpeaker.stop();
    }

}
