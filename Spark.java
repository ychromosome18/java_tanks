/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Spark {
    private static Texture expsheet=null;
    private static TextureRegion[] exps;
    private float x;
    private float y;
    private boolean active;
    private int lifetime, frametime;

    public Spark() {
        active=false;
        if(expsheet==null) {
            expsheet = new Texture("portal_spark.png");
            exps=new TextureRegion[8];
            exps[7] = new TextureRegion(expsheet, 0, 0, 182, 206);
            exps[6] = new TextureRegion(expsheet, 182, 0, 182, 206);
            exps[5] = new TextureRegion(expsheet, 364, 0, 182, 206);
            exps[4] = new TextureRegion(expsheet, 546, 0, 182, 206);
            exps[3] = new TextureRegion(expsheet, 0, 206, 182, 206);
            exps[2] = new TextureRegion(expsheet, 182, 206, 182, 206);
            exps[1] = new TextureRegion(expsheet, 364, 206, 182, 206);
            exps[0] = new TextureRegion(expsheet, 546, 206, 182, 206);
        }
    }

    public void enable(float px, float py, Mysounds ms) {
        x=px;
        y=py;
        active=true;
        lifetime=Constants.INIT_SPARK_TIME;
        frametime=lifetime/exps.length;
        if(ms!=null) ms.play("portal");
    }

    public void update() {
        if(lifetime>0) {
            lifetime--;
            if (lifetime == 0) active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if(active) {
            int stage=lifetime/frametime;
            if(stage>=exps.length) stage=exps.length-1;
            batch.draw(exps[stage], x-65,y-65 /*x-exps[stage].getRegionWidth()/2, y-exps[stage].getRegionHeight()/2*/, 130,130);
        }
    }

    static public void Dispose() {
        if(expsheet!=null) expsheet.dispose();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
