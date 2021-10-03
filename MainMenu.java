/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.TreeMap;

public class MainMenu {
    private Texture allmenu;
    private TextureRegion resume, resume_en;
    private TextureRegion start, start_en;
    private TextureRegion exit, exit_en;
    private TextureRegion replay, replay_en;
    private TextureRegion mmenu, mmenu_en;
    private TextureRegion easy, easy_en;
    private TextureRegion medium, medium_en;
    private TextureRegion hard, hard_en;
    private TextureRegion opt, opt_en;
    private TreeMap<String, MenuItem> items;

    public MainMenu() {
        allmenu = new Texture("30_buttons_set.png");
        resume = new TextureRegion(allmenu, 197, 54, 197, 54);
        resume_en = new TextureRegion(allmenu, 394, 54, 197, 54);
        start = new TextureRegion(allmenu, 0, 0, 197, 54);
        start_en = new TextureRegion(allmenu, 197, 0, 197, 54);
        exit = new TextureRegion(allmenu, 197, 270, 197, 54);
        exit_en = new TextureRegion(allmenu, 394, 270, 197, 54);
        replay = new TextureRegion(allmenu, 591, 54, 197, 54);
        replay_en = new TextureRegion(allmenu, 788, 54, 197, 54);
        mmenu = new TextureRegion(allmenu, 591, 162, 197, 54);
        mmenu_en = new TextureRegion(allmenu, 788, 162, 197, 54);
        easy = new TextureRegion(allmenu, 591, 270, 197, 54);
        easy_en = new TextureRegion(allmenu, 788, 270, 197, 54);
        medium = new TextureRegion(allmenu, 197, 162, 197, 54);
        medium_en = new TextureRegion(allmenu, 394, 162, 197, 54);
        hard = new TextureRegion(allmenu, 788, 216, 197, 54);
        hard_en = new TextureRegion(allmenu, 0, 270, 197, 54);
        opt = new TextureRegion(allmenu, 0, 108, 197, 54);
        opt_en = new TextureRegion(allmenu, 197, 108, 197, 54);

        items = new TreeMap<String, MenuItem>();
        FillMenu();
        ConfigMenu("mmenu;start;opt;exit");

        Gdx.input.setInputProcessor(new InputAdapter() {
                                        @Override
                                        public boolean mouseMoved(int x, int y) {
                                            for (String str : items.keySet()) {
                                                if(items.get(str).CheckMouseMove(x, Gdx.graphics.getHeight() - y)) break;
                                                //items.get(str).
                                            }
                                            return false;
                                        }

                                    }
        );
    }

    public void Dispose() {
        allmenu.dispose();
    }

    public void Render(SpriteBatch batch, MyGdxGame mg) {
        for (String str : items.keySet()) {
            items.get(str).Show(batch);
        }

    }

    public void Update(MyGdxGame mg) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            mg.paus(0,true);
        }
        if (Gdx.input.justTouched()/*Gdx.input.isButtonPressed(Input.Buttons.LEFT)*/) {
            for (String str : items.keySet()) {
                if(items.get(str).CheckMouseMove(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                    mg.getMs().play("menu");

                    if(str.equals("start")) {
                        //Gdx.graphics.setWindowedMode(800, 500);
                        ConfigMenu("mmenu;easy;medium;hard");
                        //mg.setBegin(false);
                        break;
                    }
                    if(str.equals("mmenu")) {
                        ConfigMenu("mmenu;start;opt;exit");
                        mg.setBegin(true);
                        if(mg.isPause()) mg.paus(1,true);
                        //mg.InitEnemies();
                        break;
                    }
                    if(str.equals("exit")) {

                        System.exit(0);
                        break;
                    }
                    if(str.equals("resume")) {
                        //ConfigMenu("easy;medium;hard");
                        mg.paus(0,true);
                        break;
                    }
                    if(str.equals("opt")) {
                        //ConfigMenu("easy;medium;hard");
                        //mg.paus();
                        mg.getOpf().setVisible(true);
                        break;
                    }
                    if(str.equals("easy")) {
                        ConfigMenu("mmenu;resume;exit");
                        setLevel(str);
//                        Constants.USER_DURABILITY=3;
//                        Constants.ENEMY_MAX_VISIBLE=6;
//                        Constants.BOSS_DURABILITY=200;
                        //mg.getTank().setDurability(3, mg);
                        mg.InitEnemies(1);
                        mg.setBegin(false);
                        break;
                    }
                    if(str.equals("medium")) {
                        ConfigMenu("mmenu;resume;exit");
                        setLevel(str);
//                        Constants.USER_DURABILITY=2;
//                        Constants.ENEMY_MAX_VISIBLE=8;
//                        Constants.BOSS_DURABILITY=400;
                        //mg.getTank().setDurability(2, mg);
                        mg.InitEnemies(1);
                        mg.setBegin(false);
                        break;
                    }
                    if(str.equals("hard")) {
                        ConfigMenu("mmenu;resume;exit");
                        setLevel(str);
//                        Constants.USER_DURABILITY=1;
//                        Constants.ENEMY_MAX_VISIBLE=10;
//                        Constants.BOSS_DURABILITY=600;
                        //mg.getTank().setDurability(1, mg);
                        mg.InitEnemies(1);
                        mg.setBegin(false);
                        break;
                    }
               }
            }
        }
    }

    public void FillMenu() {
        items.put("resume", new MenuItem(resume, resume_en));
        items.put("start", new MenuItem(start, start_en));
        items.put("exit", new MenuItem(exit, exit_en));
        items.put("replay", new MenuItem(replay, replay_en));
        items.put("mmenu", new MenuItem(mmenu, mmenu_en));
        items.put("easy", new MenuItem(easy, easy_en));
        items.put("medium", new MenuItem(medium, medium_en));
        items.put("hard", new MenuItem(hard, hard_en));
        items.put("opt", new MenuItem(opt, opt_en));
    }

    public void ConfigMenu(String m) {
        String its[] = m.split(";");

        for (String str : items.keySet()) {
            items.get(str).Hide();
        }

        for (int i = 0; i < its.length; i++) {
            int x = (Gdx.graphics.getWidth() - resume.getRegionWidth()) / 2;
            int y = Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() - (resume.getRegionHeight() + 20) * its.length) / 2 - (resume.getRegionHeight() + 20) * i;
            items.get(its[i]).Enable(x, y);
        }

    }

    public void setLevel(String level) {
        if(level.equals("easy")) {
            Constants.USER_DURABILITY=Constants.USER_DURABILITY_EASY;
            Constants.ENEMY_MAX_VISIBLE=Constants.ENEMY_MAX_VISIBLE_EASY;
            Constants.BOSS_DURABILITY=Constants.BOSS_DURABILITY_EASY;
        } else if(level.equals("medium")) {
            Constants.USER_DURABILITY=Constants.USER_DURABILITY_MEDIUM;
            Constants.ENEMY_MAX_VISIBLE=Constants.ENEMY_MAX_VISIBLE_MEDIUM;
            Constants.BOSS_DURABILITY=Constants.BOSS_DURABILITY_MEDIUM;
        } else if(level.equals("hard")) {
            Constants.USER_DURABILITY=Constants.USER_DURABILITY_HARD;
            Constants.ENEMY_MAX_VISIBLE=Constants.ENEMY_MAX_VISIBLE_HARD;
            Constants.BOSS_DURABILITY=Constants.BOSS_DURABILITY_HARD;
        }
    }
}
