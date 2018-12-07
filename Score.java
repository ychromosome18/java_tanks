/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Score {
    private Texture[] tex;
    private Texture back;
    private Vector2 position;
    private int gap, text_height;
    private Text tx;

    Score(Texture tex_average, Texture tex_fast, Texture tex_big, Texture tex_user_average, Texture tex_user_fast, Texture tex_user_big) {
        tex = new Texture[6];
        back = new Texture("score_back.png");
        tex[0] = tex_fast;
        tex[1] = tex_average;
        tex[2] = tex_big;
        tex[3] = tex_user_fast;
        tex[4] = tex_user_average;
        tex[5] = tex_user_big;
        tx = new Text();
        position = new Vector2(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        gap = Constants.SCORE_GAP;
        text_height = 15;
    }

    public void render(SpriteBatch batch, ArrayList<user> users, int game_type) {
        float block_size=1;
        batch.draw(back, Constants.WINDOW_WIDTH, 0, 120, Constants.WINDOW_HEIGHT);
        float size_x = tex[0].getHeight() * Constants.SCORE_TANK_SIZE;
        float size_y = tex[0].getWidth() * Constants.SCORE_TANK_SIZE;
        if(game_type==Constants.GAME_TYPE_PvB) block_size=(size_y+gap+text_height)*3+text_height;
        else block_size=size_y+gap+text_height+text_height;

        for (int i = 0; i < users.size(); i++) {

            int[] sc=users.get(i).getKills();
            if(users.get(i).getName()!=null) tx.render(batch, users.get(i).getName(), (int) (gap + position.x), (int) (position.y-block_size*i), 1f, Color.WHITE);
            if(game_type==Constants.GAME_TYPE_PvB) {
                for (int k = 0; k < sc.length; k++) {
                    batch.draw(tex[k], gap + position.x, position.y - (k + 1) * (size_y + gap) - text_height - block_size * i, size_x, size_y);
                    //batch.draw(tex[k + 3], gap + position.x, position.y - (k + 1) * (size_y + gap) - text_height - block_size * i, size_x, size_y);
                    tx.render(batch, String.valueOf(sc[k]), (int) (gap * 2 + position.x + size_x), (int) (position.y - gap - (k) * (size_y + gap) - text_height - block_size * i), 2.5f, Color.RED);
                }
            } else {
                Color cl= batch.getColor();
                if(users.get(i).getTeamid()==1) batch.setColor(Constants.TEAM_2_COLOR);
                batch.draw(tex[4], gap + position.x, position.y -  (size_y + gap) - text_height - block_size * i, size_x, size_y);
                if(users.get(i).getTeamid()==1) batch.setColor(cl);
                tx.render(batch, String.valueOf(sc[1]), (int) (gap * 2 + position.x + size_x), (int) (position.y - gap - text_height - block_size * i), 2.5f, Color.RED);
            }
        }

    }

    public void dispose() {
        back.dispose();
    }
}
