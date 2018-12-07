/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Text {
    private BitmapFont font;

    Text() {
        font = new BitmapFont();
        font.setColor(Color.RED);
        //font.getData().setScale(1.5f,1.5f);
    }

    public void dispose() {
        font.dispose();
    }

    public void render(SpriteBatch batch, String msg, int x, int y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, msg, x, y);
    }
}
