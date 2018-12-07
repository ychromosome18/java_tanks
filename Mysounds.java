/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Mysounds {
    private HashMap<String, VolSound> sound;
    private ArrayList<Melody> melody = null;
    private int i = 0;
    private Random rand;
    private float volume;
    MyGdxGame mg;

    Mysounds(MyGdxGame mg) {
        sound = new HashMap<String, VolSound>();
        sound.put("menu", new VolSound(Gdx.audio.newSound(Gdx.files.internal("menu.ogg")),0.3f));
        sound.put("shot", new VolSound(Gdx.audio.newSound(Gdx.files.internal("shot.ogg")),0.3f));
        sound.put("explosion", new VolSound(Gdx.audio.newSound(Gdx.files.internal("explosion.ogg")),0.4f));
        sound.put("shot-brick", new VolSound(Gdx.audio.newSound(Gdx.files.internal("shot-brick.ogg")),0.3f));
        sound.put("shot-metal", new VolSound(Gdx.audio.newSound(Gdx.files.internal("shot-metal.wav")),0.2f));
        sound.put("portal", new VolSound(Gdx.audio.newSound(Gdx.files.internal("portal.ogg")),0.3f));
        sound.put("shot-tank", new VolSound(Gdx.audio.newSound(Gdx.files.internal("shot-tank.wav")),0.3f));
        sound.put("take_bonus", new VolSound(Gdx.audio.newSound(Gdx.files.internal("take_bonus.wav")),0.3f));
        sound.put("bonus_drop", new VolSound(Gdx.audio.newSound(Gdx.files.internal("bonus_drop.ogg")),0.3f));
        sound.put("engine", new VolSound(Gdx.audio.newSound(Gdx.files.internal("tankengine.ogg")),0.3f));
        sound.put("enemyengine", new VolSound(Gdx.audio.newSound(Gdx.files.internal("enemyengine.ogg")),0.15f));
        sound.put("explosion_boss", new VolSound(Gdx.audio.newSound(Gdx.files.internal("explosion_boss.mp3")),0.5f));
        sound.put("evil_laugh1", new VolSound(Gdx.audio.newSound(Gdx.files.internal("evil_laugh1.mp3")),0.25f));
        sound.put("evil_laugh2", new VolSound(Gdx.audio.newSound(Gdx.files.internal("evil_laugh2.mp3")),0.25f));
        sound.put("evil_laugh3", new VolSound(Gdx.audio.newSound(Gdx.files.internal("evil_laugh3.mp3")),0.25f));
        sound.put("evil_laugh4", new VolSound(Gdx.audio.newSound(Gdx.files.internal("evil_laugh4.mp3")),0.25f));
        sound.put("turret", new VolSound(Gdx.audio.newSound(Gdx.files.internal("turret.mp3")),0.7f));
        sound.put("boss_engine", new VolSound(Gdx.audio.newSound(Gdx.files.internal("boss_engine2.mp3")),0.3f));

        melody = new ArrayList<Melody>();

        //sound.
//        mg.log("Music path"+Gdx.files.internal(".").path());
//        FileHandle[] FLS=Gdx.files.internal(".").list(new FilenameFilter() {
//                        @Override
//            public boolean accept(File dir, String name) {
//                return name.toLowerCase().endsWith(".mp3");
//            }
//        });
        //File f=new File(System.getProperty("user.dir"));
        //File f=new File(Gdx.files.getLocalStoragePath());
        //mg.log(f.getAbsolutePath());
//        for (String file : f.list(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.toLowerCase().endsWith(".mp3");
//            }
//        }))
//        for(FileHandle file: FLS)
//        {
//            melody.add(Gdx.audio.newMusic(file));
//            mg.log("Add music "+file);
//        }

        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("Battle_City-Theme.mp3")),0.25f));
        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("elixir nu pogodi.mp3")),0.25f));
        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("avenged sevenfold to end the rapture heavy metal version.mp3")),0.25f));
        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("Heavy Metal Guitar Heroes - Ace of Spades.mp3")),0.25f));
        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("helloween heavy metal hamsters.mp3")),0.25f));
        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("joe rinoie heavy metal anthem.mp3")),0.25f));
        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("Mattias IA Eklundh - Lisa's Passion for Heavy Metal.mp3")),0.25f));
        melody.add(new Melody(Gdx.audio.newMusic(Gdx.files.internal("ramin djawadi game of thrones.mp3")),0.25f));
        rand = new Random();
        mg.log("Melodies size " + melody.size());
        i = rand.nextInt(melody.size());
        volume=0;
        this.mg=mg;
    }

    public void play(String name) {
        try {
            if (name == "evil_laugh") {
                int n = rand.nextInt(4) + 1;
                sound.get(name + String.valueOf(n)).play(volume);
            } else if (sound.containsKey(name))
                sound.get(name).play(volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long loop(String name) {
        try {
            if (sound.containsKey(name)) {
                mg.log("loop: "+name);
                    return sound.get(name).loop(volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void pause(String name, long id) {
        try {
            if (sound.containsKey(name)) {
                //mg.log("pause: "+name);
                sound.get(name).pause(id);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume(String name, long id) {
        try {
            if (sound.containsKey(name)) {
                sound.get(name).resume(id);
                //mg.log("resume: "+name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(String name, long id) {
        try {
            if (sound.containsKey(name)) {
                sound.get(name).stop(id);
                //mg.log("stop: "+name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        for (String key : sound.keySet()) {
            sound.get(key).dispose();
        }
        for (int j = 0; j < melody.size(); j++) {
            melody.get(j).dispose();
        }
        melody.clear();
    }

    public void play_melody() {
        try {
            if (melody != null) {
                melody.get(i).play(volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop_melody() {
        try {
                melody.get(i).stop();
                i = rand.nextInt(melody.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause_melody() {
        try {
            melody.get(i).pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume, MyGdxGame mg) {

        if(volume>1) volume=1;
        if(volume<0) volume=0;
        this.volume = volume;
        if (melody != null) {
            melody.get(i).setVol(volume);
        }

        for(user tank: mg.getUsers()) {
            if(tank.getEngine_id()!=-1) sound.get("engine").setVolume(tank.getEngine_id() ,volume);
        }

        for(Enemy tank: mg.getEnemies()) {
            if(tank.getEngine_id()!=-1) sound.get("enemyengine").setVolume(tank.getEngine_id() ,volume);
        }

        mg.log("Set volume: "+volume);
    }
}
