/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.androidthings.education.mtg;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import static com.google.androidthings.education.mtg.MusicPlayer.Note;
import static com.google.androidthings.education.mtg.Led.ALL;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Led light;
    private Display display;
    private MusicPlayer music;
    private MyDevice myDevice;
    private Gpio mButtonGpio1;
    private Gpio mButtonGpio2;
    int maxcount = 4;
    int count = 0;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ImageView fullScreenImageView = (ImageView) findViewById(R.id.imageView);

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        display = new Display();
        music = new MusicPlayer();
        light = new Led();
        // 인증서
        SSLConnect ssl = new SSLConnect();
        ssl.postHttps("https://hooks.slack.com", 1000, 1000);

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            myDevice = new MyDevice(display, music, light);

            set2zero();

            String pinName1 = BoardDefaults.getGPIOForButton(0);
            mButtonGpio1 = service.openGpio(pinName1);
            mButtonGpio1.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio1.setEdgeTriggerType(Gpio.EDGE_FALLING);

            String pinName2 = BoardDefaults.getGPIOForButton(1);
            mButtonGpio2 = service.openGpio(pinName2);
            mButtonGpio2.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio2.setEdgeTriggerType(Gpio.EDGE_FALLING);
            mButtonGpio1.registerGpioCallback(new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    Log.i(TAG, "GPIO changed, button pressed");
                    count++;
                    if(count <= maxcount && display.open()){
                       myDevice.showCount(count);
                       display.close();
                    }
                    if (count > maxcount)
                    {
                        Log.i(TAG, "Toilet");
                        if (display.open()) {
                            msgThread msg = new msgThread();
                            msg.start();
                            myDevice.toilet();
                        }
                        set2zero();
                    }
                    // Return true to continue listening to events
                    return true;
                }
            });
            mButtonGpio2.registerGpioCallback(new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    Log.i(TAG, "GPIO changed, button pressed B");
                    count+=maxcount+1;
                    if (count > maxcount)
                    {
                        Log.i(TAG, "Toilet");
                        if (display.open()) {
                            msgThread msg = new msgThread();
                            msg.start();
                            myDevice.toilet();
                        }
                        set2zero();
                    }
                    // Return true to continue listening to events
                    return true;
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

    }

    private void set2zero(){
        count = 0;
        if (display.open()) {
            myDevice.showCount(count);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mButtonGpio1 != null || mButtonGpio2 != null) {
            // Close the Gpio pin
            Log.i(TAG, "Closing Button GPIO pin");
            try {
                mButtonGpio1.close();
                mButtonGpio2.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            } finally {
                mButtonGpio1 = null;
                mButtonGpio2 = null;
            }
        }
        light.close();
        display.close();
        music.close();
    }
}


class PostMsg {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    String bowlingJson() {
        return "{\"text\": \"화장실에 가고싶은 학생이 있습니다!\"}";
    }
}
class msgThread extends Thread {
    public msgThread() { }

    @Override
    public void run() {
        sendToiletMsg();
    }
    public void sendToiletMsg() {
        PostMsg toilet = new PostMsg();
        String json = toilet.bowlingJson();
        String response = null;
        try {
            response = toilet.post("https://hooks.slack.com/services/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", json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}