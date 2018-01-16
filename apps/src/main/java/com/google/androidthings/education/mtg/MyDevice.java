package com.google.androidthings.education.mtg;

import android.util.Log;
import android.view.ViewDebug;

import java.io.IOException;

import static com.google.androidthings.education.mtg.Led.ALL;
import static com.google.androidthings.education.mtg.Led.BLUE;
import static com.google.androidthings.education.mtg.Led.CYAN;
import static com.google.androidthings.education.mtg.Led.GREEN;
import static com.google.androidthings.education.mtg.Led.ORANGE;
import static com.google.androidthings.education.mtg.Led.RED;
import static com.google.androidthings.education.mtg.Led.VIOLET;
import static com.google.androidthings.education.mtg.Led.WHITE;
import static com.google.androidthings.education.mtg.Led.YELLOW;
import static com.google.androidthings.education.mtg.MusicPlayer.Note.C;
import static com.google.androidthings.education.mtg.MusicPlayer.Note.D;
import static com.google.androidthings.education.mtg.MusicPlayer.Note.E;
import static com.google.androidthings.education.mtg.MusicPlayer.Note.F;
import static com.google.androidthings.education.mtg.MusicPlayer.Note.G;

/**
 * My Device!
 */

public class MyDevice {
    private static final String TAG = MyDevice.class.getSimpleName();

    private Led light;
    private Display display;
    private MusicPlayer music;

    public MyDevice(Display display, MusicPlayer music, Led light) {
        this.display = display;
        this.music = music;
        this.light = light;
    }

    public static void pause(double pauseTimeSec) {
        try {
            Thread.sleep((long)(pauseTimeSec * 1000.0));
        } catch (InterruptedException e) {
            Log.e(TAG, "Failed to sleep", e);
        }
    }

    public void toilet() {
        display.show("HELP");
        playmusic();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        display.close();
    }

    public void showCount(int count) {
        display.show(String.valueOf(count));
        display.close();
    }


    void playmusic() {
        light.open();
        music.open();
        light.on(0, RED);
        light.on(1, ORANGE);
        light.on(2, YELLOW);
        light.on(3, GREEN);
        light.on(4, CYAN);
        light.on(5, BLUE);
        light.on(6, VIOLET);
        pause(1);

        light.on(0, WHITE);
        light.on(1, RED);
        light.on(2, ORANGE);
        light.on(3, YELLOW);
        light.on(4, GREEN);
        light.on(5, CYAN);
        light.on(6, BLUE);
        pause(1);

        light.on(0, WHITE);
        light.on(1, WHITE);
        light.on(2, RED);
        light.on(3, ORANGE);
        light.on(4, YELLOW);
        light.on(5, GREEN);
        light.on(6, BLUE);
        pause(1);

        light.on(0, WHITE);
        light.on(1, WHITE);
        light.on(2, WHITE);
        light.on(3, RED);
        light.on(4, ORANGE);
        light.on(5, YELLOW);
        light.on(6, GREEN);
        pause(1);

        light.on(0, WHITE);
        light.on(1, WHITE);
        light.on(2, WHITE);
        light.on(3, WHITE);
        light.on(4, RED);
        light.on(5, ORANGE);
        light.on(6, YELLOW);
        pause(1);

        light.on(0, WHITE);
        light.on(1, WHITE);
        light.on(2, WHITE);
        light.on(3, WHITE);
        light.on(4, WHITE);
        light.on(5, RED);
        light.on(6, ORANGE);
        pause(1);

        light.on(0, WHITE);
        light.on(1, WHITE);
        light.on(2, WHITE);
        light.on(3, WHITE);
        light.on(4, WHITE);
        light.on(5, WHITE);
        light.on(6, RED);
        pause(1);

        light.on(ALL, WHITE);
        pause(1);


        music.playAll(0.5,
                G, E, E, E, F, D, D, D, C, D, E, F, G, G, G, G,
                G, E, E, E, F, D, D, D, C, E, G, G, E, E, E, E);
        light.close();
        music.close();
    }
}
