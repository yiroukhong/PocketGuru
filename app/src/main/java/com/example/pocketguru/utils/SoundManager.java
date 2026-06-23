package com.example.pocketguru.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.pocketguru.R;

public class SoundManager {

    private static SoundManager instance;
    private SoundPool soundPool;

    private int soundSketch;
    private int soundSwoosh;
    private int soundStart;
    private int soundCorrect;
    private int soundPop;
    private int soundLevelComplete;

    private int sketchStreamId = -1; // track looping stream to stop it

    private SoundManager(Context context) {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(attributes)
                .build();

        soundSketch = soundPool.load(context, R.raw.sketch, 1);
        soundSwoosh = soundPool.load(context, R.raw.swoosh, 1);
        soundStart = soundPool.load(context, R.raw.start, 1);
        soundCorrect = soundPool.load(context, R.raw.correct, 1);
        soundPop = soundPool.load(context, R.raw.pop, 1);
        soundLevelComplete = soundPool.load(context, R.raw.level_complete, 1);
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context.getApplicationContext());
        }
        return instance;
    }

    public void playSketchLoop() {
        if (sketchStreamId != -1) return; // already playing
        sketchStreamId = soundPool.play(soundSketch, 1f, 1f, 1, -1, 1f); // -1 = loop forever
    }

    public void stopSketch() {
        if (sketchStreamId != -1) {
            soundPool.stop(sketchStreamId);
            sketchStreamId = -1;
        }
    }

    public void playSwoosh() {
        soundPool.play(soundSwoosh, 1f, 1f, 1, 0, 1f);
    }

    public void playStart() {
        soundPool.play(soundStart, 1f, 1f, 1, 0, 1f);
    }

    public void playCorrect() {
        soundPool.play(soundCorrect, 1f, 1f, 1, 0, 1f);
    }

    public void playPop() {
        soundPool.play(soundPop, 1f, 1f, 1, 0, 1f);
    }

    public void playLevelComplete() {
        soundPool.play(soundLevelComplete, 1f, 1f, 1, 0, 1f);
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            instance = null;
        }
    }
}
