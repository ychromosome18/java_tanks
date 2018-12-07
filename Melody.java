/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.audio.Music;

public class Melody {
    private float volume;
    private Music mel;

    Melody(Music mel, float volume) {
        this.mel=mel;
        this.volume=volume;
    }

    public void play(float v) {
        float av=volume+v>1 ? 1 : volume+v<0 ? 0 : volume+v;
        mel.setVolume(av);
        mel.setLooping(true);
        mel.play();
    }

    public void setVol(float v) {
        float av=volume+v>1 ? 1 : volume+v<0 ? 0 : volume+v;
        mel.setVolume(av);
    }

    public void stop() {
        if(mel.isPlaying()) mel.stop();
    }

    public void dispose() {
        mel.dispose();
    }

    public void pause() {
        if(mel.isPlaying()) mel.pause();
    }
}
