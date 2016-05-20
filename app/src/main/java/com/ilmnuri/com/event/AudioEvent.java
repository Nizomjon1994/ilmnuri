package com.ilmnuri.com.event;

import com.ilmnuri.com.model.Audio;

/**
 * Created by User on 19.05.2016.
 */
public class AudioEvent {
    private Type type;
    private int current_size;
    private int total_size;
    private int id;
    private Audio mAudio;

    public static AudioEvent download(Audio id) {
        AudioEvent e = new AudioEvent();
        e.type = Type.DOWNLOAD;
        e.mAudio = id;
        return e;
    }



    public static AudioEvent stop(Audio id) {
        AudioEvent e = new AudioEvent();
        e.type = Type.STOP;
        e.mAudio = id;
        return e;
    }

    public enum Type {
        DOWNLOAD,
        STOP;
    }

    public Audio getAudio() {
        return mAudio;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public int getTotal_size() {
        return total_size;
    }

    public int getCurrent_size() {
        return current_size;
    }
}
