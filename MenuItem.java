/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class MenuItem {
    private TextureRegion tr,tr_entered;
    private boolean visible, enter;
    private Rectangle rect;

    MenuItem(TextureRegion tr, TextureRegion tr_entered) {
        this.tr=tr;
        this.tr_entered=tr_entered;
        visible=enter=false;
        rect=new Rectangle(0,0,tr.getRegionWidth(), tr.getRegionHeight());
    }

    public void Enable(int x, int y) {
        rect.setPosition(x,y);
        visible=true;
        enter=false;
    }

    public void Hide() {
        visible=false;
    }

    public void Show(SpriteBatch batch) {
        if(visible) {
            if (enter) batch.draw(tr_entered, rect.x, rect.y);
            else batch.draw(tr, rect.x, rect.y);
        }
    }

    public void setEnter(boolean enter) {
        this.enter = enter;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean CheckMouseMove(int x, int y) {
        if(visible) {
            if(rect.contains(x,y)) enter=true;
                else enter=false;
            return enter;
        }
        return false;
    }
}
