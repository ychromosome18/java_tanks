/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;

public class VolSound {
    private float volume;
    private Sound mel;

    VolSound(Sound mel, float volume) {
        this.mel=mel;
        this.volume=volume;
    }

    public void play(float v) {
        float av=volume+v>1 ? 1 : volume+v<0 ? 0 : volume+v;
        mel.play(av);
    }

    public void stop(long id) {
        mel.stop(id);
    }

    public void dispose() {
        mel.dispose();
    }

    public void pause(long id) {
        mel.pause(id);
    }

    public void resume(long id) {
        mel.resume(id);
    }

    public long loop(float v) {
        try {
            float av=volume+v>1 ? 1 : volume+v<0 ? 0 : volume+v;
             return mel.loop(av);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setVolume(long id, float v) {
        float av=volume+v>1 ? 1 : volume+v<0 ? 0 : volume+v;
        mel.setVolume(id, av);
    }
}

