/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RoundEnd {
    private ArrayList<Texture> pics = null;
    private Texture win1, fail1;
    private Random rand;
//    private ArrayList<Sound> snd_win = null;
//    private ArrayList<Sound> snd_fail = null;
    boolean start_sound;
    //  win=1   выйграл     win=2   не совсем
    private int i = 0, win = 0;
    private MyGdxGame mg;

    RoundEnd(MyGdxGame mg) {
        pics = new ArrayList<Texture>();
//        File f=new File(System.getProperty("user.dir"));
//        for (String file : f.list(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.toLowerCase().endsWith(".jpg") && name.toLowerCase().startsWith("ft");
//            }
//        })) {
//            pics.add(new Texture(Gdx.files.internal(file)));
//        }
        pics.add(new Texture(Gdx.files.internal("ft1.jpg")));
        pics.add(new Texture(Gdx.files.internal("ft2.jpg")));
        pics.add(new Texture(Gdx.files.internal("ft3.jpg")));
        pics.add(new Texture(Gdx.files.internal("ft4.jpg")));
        pics.add(new Texture(Gdx.files.internal("ft6.jpg")));
        pics.add(new Texture(Gdx.files.internal("ft7.jpg")));
        pics.add(new Texture(Gdx.files.internal("ft9.jpg")));
        pics.add(new Texture(Gdx.files.internal("ft10.jpg")));

        win1 = new Texture("win1.png");
        fail1 = new Texture("fail1.png");
        rand = new Random();

//        snd_win = new ArrayList<Sound>();
//        snd_win.add(Gdx.audio.newSound(Gdx.files.internal("win1.mp3")));
//        snd_win.add(Gdx.audio.newSound(Gdx.files.internal("win2.mp3")));
//        snd_win.add(Gdx.audio.newSound(Gdx.files.internal("win3.mp3")));
//        snd_win.add(Gdx.audio.newSound(Gdx.files.internal("win4.mp3")));
//        snd_fail = new ArrayList<Sound>();
//        snd_fail.add(Gdx.audio.newSound(Gdx.files.internal("fail1.mp3")));
//        snd_fail.add(Gdx.audio.newSound(Gdx.files.internal("fail2.mp3")));
//        snd_fail.add(Gdx.audio.newSound(Gdx.files.internal("fail3.mp3")));

        this.mg=mg;
        renew();
    }

    public void render(SpriteBatch batch) {
        batch.draw(pics.get(i), (Constants.WINDOW_WIDTH - Constants.ROUNDEND_PIC_WIDTH) / 2, (Constants.WINDOW_HEIGHT - Constants.ROUNDEND_PIC_HEIGHT) / 2, Constants.ROUNDEND_PIC_WIDTH, Constants.ROUNDEND_PIC_HEIGHT);
        if (win == 1) {
            batch.draw(win1, Constants.WINDOW_WIDTH / 2 + Constants.ROUNDEND_PIC_WIDTH / 2 - Constants.ROUNDEND_PIC_WIDTH / Constants.ROUNDEND_WORD_RATIO,
                    (Constants.WINDOW_HEIGHT - Constants.ROUNDEND_PIC_HEIGHT) / 2, Constants.ROUNDEND_PIC_WIDTH / Constants.ROUNDEND_WORD_RATIO, Constants.ROUNDEND_PIC_HEIGHT / Constants.ROUNDEND_WORD_RATIO);
            if (!start_sound) play_snd(win);
        } else {
            batch.draw(fail1, Constants.WINDOW_WIDTH / 2 + Constants.ROUNDEND_PIC_WIDTH / 2 - Constants.ROUNDEND_PIC_WIDTH / Constants.ROUNDEND_WORD_RATIO,
                    (Constants.WINDOW_HEIGHT - Constants.ROUNDEND_PIC_HEIGHT) / 2, Constants.ROUNDEND_PIC_WIDTH / Constants.ROUNDEND_WORD_RATIO, Constants.ROUNDEND_PIC_HEIGHT / Constants.ROUNDEND_WORD_RATIO);
            if (!start_sound) play_snd(win);
        }

    }

    public void dispose() {
        for (int j = 0; j < pics.size(); j++) {
            pics.get(j).dispose();
        }
        pics.clear();
        win1.dispose();
        fail1.dispose();
//        snd_win.clear();
//        snd_fail.clear();
    }

    public void renew() {
        i = rand.nextInt(pics.size());
        start_sound = false;
    }

    public void update(MyGdxGame mg) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
            if (mg.getGame_type() != Constants.GAME_TYPE_PvP) {
                if (win == 1 /*&& mg.getBb().getPosition().y==1000*/) mg.setEnemy_count(mg.getEnemy_count() + mg.getUsers().size());
                mg.InitEnemies(mg.getEnemy_count());
            } else mg.InitEnemies(0);
        }
    }

    public void setWin(int win) {
        this.win = win;
    }

    private void play_snd(int w) {
        start_sound = true;
        if (w == 1) {
            mg.getMs().play_end(true);
        } else {
            mg.getMs().play_end(false);
        }
        mg.getBb().stopSound();
    }

}
