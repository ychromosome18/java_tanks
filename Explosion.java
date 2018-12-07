/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
    private static Texture expsheet = null;
    private static TextureRegion[] exps, exps_boss;
    //TextureRegion currentFrame;
    private float x;
    private float y;
    private boolean active;
    private int stage;
    private int lifetime;
    private int who;
    private int s_time;
    private boolean isuser;
    //Animation<TextureRegion> exp_boss_anim;
    float stateTime;

    public Explosion() {
        active = false;
        if (expsheet == null) {
//            expsheet = new Texture("explosions.png");
//            exps = new TextureRegion[8];
//            exps[7] = new TextureRegion(expsheet, 0, 0, 128, 128);
//            exps[6] = new TextureRegion(expsheet, 128, 0, 128, 128);
//            exps[5] = new TextureRegion(expsheet, 256, 0, 128, 128);
//            exps[4] = new TextureRegion(expsheet, 384, 0, 128, 128);
//            exps[3] = new TextureRegion(expsheet, 0, 128, 128, 128);
//            exps[2] = new TextureRegion(expsheet, 128, 128, 128, 128);
//            exps[1] = new TextureRegion(expsheet, 256, 128, 128, 128);
//            exps[0] = new TextureRegion(expsheet, 384, 128, 128, 128);


            expsheet = new Texture("exp_tank.png");
            TextureRegion[][] tmp = new TextureRegion(expsheet).split(151,151);
            exps = new TextureRegion[8*3];
            int index=0;
            for (int i=2; i>=0; i--) {
                for (int j=7; j>=0; j--) {
                    exps[index++]=tmp[i][j];
                }
            }

            Texture expsheet_boss = new Texture("exp_boss.png");
            TextureRegion[][] tmp2 = new TextureRegion(expsheet_boss).split(expsheet_boss.getWidth()/3,expsheet_boss.getHeight()/4);
            exps_boss = new TextureRegion[3*4];
            index=0;
            for (int i=3; i>-1; i--) {
                for (int j=2; j>-1; j--) {
                    exps_boss[index++]=tmp2[i][j];
                }
            }
            //exp_boss_anim = new Animation(0.2f, exps_boss);
        }
    }

    public void enable(float px, float py, int i, boolean isuser) {
        x = px;
        y = py;
        active = true;
        //stage = 1;
        who = i;
        this.isuser = isuser;
        stateTime=0;

        if (!isuser && who == -1) {
            lifetime = Constants.BOSS_EXPLOSION_TIME / exps_boss.length* exps_boss.length;
            s_time = lifetime / exps_boss.length;
            stage=exps_boss.length-1;
        } else {
            lifetime = Constants.EXPLOSION_TIME / exps.length* exps.length;
            s_time = lifetime / exps.length;
            stage=exps.length-1;
        }
    }

    public void update(MyGdxGame mg) {
        lifetime--;
        stage = lifetime / s_time;
        if (stage <= 1) {
            if (isuser) {
                //if (who == -1) mg.getTank().Hide();
                //else
                mg.getUsers().get(who).Hide();
            } else if (who != -1 && mg.getEnemies().size()>0) mg.getEnemies().get(who).Hide();     //  enemy kaput
            else if (who == -1) mg.getBb().Hide();     //  big boss kaput
        }
        if (lifetime == 0) active = false;
    }

    public void render(SpriteBatch batch) {
        if (stage < exps.length && active)
            if (!isuser && who == -1) {
                //batch.draw(exps[stage], x - exps[stage].getRegionWidth(), y - exps[stage].getRegionHeight(), exps[stage].getRegionWidth() * 2, exps[stage].getRegionHeight() * 2);
                //stateTime += Gdx.graphics.getRawDeltaTime();
                //currentFrame = exp_boss_anim.getKeyFrame(stateTime, false);
                batch.draw(exps_boss[stage], x-exps_boss[stage].getRegionWidth(), y-exps_boss[stage].getRegionHeight(), exps_boss[stage].getRegionWidth() * 2, exps_boss[stage].getRegionHeight() * 2);
            }
            else batch.draw(exps[stage], x - exps[stage].getRegionWidth() / 2, y - exps[stage].getRegionHeight() / 2);
    }

    static public void Dispose() {
        if (expsheet != null) expsheet.dispose();
    }

    public boolean isActive() {
        return active;
    }
}
